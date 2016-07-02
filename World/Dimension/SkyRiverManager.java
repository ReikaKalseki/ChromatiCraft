package Reika.ChromatiCraft.World.Dimension;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Auxiliary.Trackers.PlayerChunkTracker;
import Reika.DragonAPI.Auxiliary.Trackers.TickScheduler;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Event.ScheduledTickEvent;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaVectorHelper;

public class SkyRiverManager {

	public static final PlayerChunkTracker.TrackingCondition skyRiverCondition = new TrackingConditionSkyRiver();
	private static final List<DelayedSkyRiverPacketEvent> delayedPackets = new LinkedList();
	private static final Map<EntityPlayer, Integer> recentlyLeftSkyRiver = new HashMap(); // Allows
	// the
	// server
	// to
	// keep
	// up
	// after
	// leaving
	// the
	// SkyRiver
	private static final int SKYRIVER_ENTER_DELAY = 60; // ticks - allows the
	// server to catch up
	// after untrack.

	public static void tickSkyRiverServer(World w) {
		if (!w.isRemote) {
			for (Object objPl : w.playerEntities) {
				if (!(objPl instanceof EntityPlayer) || ((EntityPlayer)objPl).isDead)
					continue;
				EntityPlayer pl = (EntityPlayer)objPl;
				if (recentlyLeftSkyRiver.containsKey(pl)) {
					int delay = recentlyLeftSkyRiver.get(pl);
					delay--;
					if (delay <= 0) {
						// Being here means the server has caught up. we can
						// send the client it's ok to enter it again.
						recentlyLeftSkyRiver.remove(pl);
						debugMessage("Player " + pl.getCommandSenderName() + " may enter the SkyRiver again.");
					}
					else {
						recentlyLeftSkyRiver.put(pl, delay);
					}
					continue;
				}

				SkyRiverGenerator.RiverPoint closest = SkyRiverGenerator.getClosestPoint(pl, 16, true);
				if (closest != null) {
					if (SkyRiverGenerator.isWithinSkyRiver(pl, closest)) {
						if (!PlayerChunkTracker.shouldStopChunkloadingFor(pl)) {
							PlayerChunkTracker.startTrackingPlayer(pl, skyRiverCondition);
							sendSkyriverEnterStatePacket(pl, true);
							debugMessage("Player " + pl.getCommandSenderName() + " has entered a SkyRiver.");
						}
						movePlayer(pl, closest);
					}
				}
			}
		}
	}

	// Can/Will be called from Server and Client
	// Set motionX, motionY, motionZ according to the RiverPoints
	// TODO redo.
	protected static void movePlayer(EntityPlayer player, SkyRiverGenerator.RiverPoint rp) {
		DecimalPosition plPos = new DecimalPosition(player);
		double dst;
		Vec3 pullVector, nodeVector;

		// It's important to check for pos->next before checking for prev->pos
		// That way, pulling further is preferred over pulling backwards...

		// checks if the player is between RiverPoint.position and
		// RiverPoint.next
		dst = ReikaVectorHelper.getDistFromPointToLine(rp.next.xCoord, rp.next.yCoord, rp.next.zCoord, rp.position.xCoord, rp.position.yCoord, rp.position.zCoord, player.posX, player.posY, player.posZ);
		if (dst < SkyRiverGenerator.RIVER_TUNNEL_RADIUS) {
			pullVector = Vec3.createVectorHelper(rp.next.xCoord - rp.position.xCoord, rp.next.yCoord - rp.position.yCoord, rp.next.zCoord - rp.position.zCoord);
			nodeVector = Vec3.createVectorHelper(rp.next.xCoord - plPos.xCoord, rp.next.yCoord - plPos.yCoord, rp.next.zCoord - plPos.zCoord);
		}
		else {
			// checks if the player is between RiverPoint.prev and
			// RiverPoint.position
			dst = ReikaVectorHelper.getDistFromPointToLine(rp.position.xCoord, rp.position.yCoord, rp.position.zCoord, rp.prev.xCoord, rp.prev.yCoord, rp.prev.zCoord, player.posX, player.posY, player.posZ);
			if (dst < SkyRiverGenerator.RIVER_TUNNEL_RADIUS) {
				pullVector = Vec3.createVectorHelper(rp.position.xCoord - rp.prev.xCoord, rp.position.yCoord - rp.prev.yCoord, rp.position.zCoord - rp.prev.zCoord);
				nodeVector = Vec3.createVectorHelper(rp.position.xCoord - plPos.xCoord, rp.position.yCoord - plPos.yCoord, rp.position.zCoord - plPos.zCoord);
			}
			else {
				return; // Well. then we're not in a SkyRiver.
			}
		}

		pullVector = pullVector.normalize(); // Normalized vector parallel in
		// the direction of the SkyRiver
		nodeVector = nodeVector.normalize(); // Normalized vector pointing from
		// the player towards the next
		// node.
		pullVector = Vec3.createVectorHelper(pullVector.xCoord * 0.95 + nodeVector.xCoord * 0.05, pullVector.yCoord * 0.95 + nodeVector.yCoord * 0.05, pullVector.zCoord * 0.95 + nodeVector.zCoord * 0.05);
		double multiplier = 3D;
		player.motionX = pullVector.xCoord * multiplier;
		player.motionY = pullVector.yCoord * multiplier;
		player.motionZ = pullVector.zCoord * multiplier;
	}

	protected static void debugMessage(String message) {
		if ((DragonAPICore.isReikasComputer() && ReikaObfuscationHelper.isDeObfEnvironment()) || DragonAPICore.debugtest) {
			ChromatiCraft.logger.log("SkyRiver> " + message);
		}
	}

	private static void sendRiverPoints(EntityPlayer player, SkyRiverGenerator.Ray r) {
		NBTTagCompound cmp = new NBTTagCompound();
		r.writeToPktNBT(cmp);

		ReikaPacketHelper.sendNBTPacket(ChromatiCraft.packetChannel, ChromaPackets.SKYRIVER_SYNC.ordinal(), cmp, new PacketTarget.PlayerTarget((net.minecraft.entity.player.EntityPlayerMP)player));
	}

	protected static void sendRiverClearPacketsToAll() {
		List objPlayers;
		try {
			objPlayers = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
		}
		catch (NullPointerException exc) {
			// Well, then we're still in the server startup and sending is not
			// necessary.
			return;
		}
		for (Object player : objPlayers) {
			clearClientRiver((EntityPlayer)player);
		}
	}

	public static void clearClientRiver(EntityPlayer player) {
		synchronized (delayedPackets) { // Remove packets in queue. That way we
			// don't send wrong data.
			for (DelayedSkyRiverPacketEvent event : delayedPackets) {
				if (event.recipient.equals(player))
					event.aborted = true;
			}
		}
		debugMessage("Sending SkyRiver clear to " + player.getCommandSenderName());
		ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.SKYRIVER_STATE.ordinal(), (net.minecraft.entity.player.EntityPlayerMP)player, 0);
	}

	private static void sendSkyriverEnterStatePacket(EntityPlayer player, boolean allowEntering) {
		debugMessage("Sending SkyRiver State-Change to " + player.getCommandSenderName() + " - new State: " + (allowEntering ? "Allow" : "Deny"));
		ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.SKYRIVER_STATE.ordinal(), (net.minecraft.entity.player.EntityPlayerMP)player, allowEntering ? 1 : 2);
	}

	// Fired, once a player leaves a SkyRiver. If this happens, we can expect
	// LAG from ChunkLoading.
	// Don't allow new SkyRiver entries from anyone that isn't inside one atm.
	// Players already inside don't matter, they don't cause ChunkLoading anyway
	// since they're already tracked.
	// New people do matter since if they'd move around, they accumulate
	// non-tracked movement packets which cause >200 chunks to load.
	// RIP server if we don't handle that.
	// Thus, everyone gets a refresh-state DENY packet and set onto the
	// enter-delay.
	private static void handleUntrack() {
		List objPlayers;
		try {
			objPlayers = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
		}
		catch (NullPointerException exc) {
			return;
		}

		for (Object pl : objPlayers) {
			if (!PlayerChunkTracker.shouldStopChunkloadingFor((EntityPlayer)pl)) {
				sendSkyriverEnterStatePacket((EntityPlayer)pl, false);
				recentlyLeftSkyRiver.put((EntityPlayer)pl, SKYRIVER_ENTER_DELAY);
			}
		}
	}

	// Split up river rays and send them individually with some delay.
	// If there's a river close to the player in the chroma dim, send that one
	// first!
	// If the player is in the chroma dim, start sending them immediately rather
	// than 10 sec after login. He might need the rays.
	protected static void startSendingRiverPackets(EntityPlayer player) {
		debugMessage("Scheduling SkyRiver Packets for " + player.getCommandSenderName());
		int ticksDelay = 200; // 10 seconds after.

		int startIndex = 0;
		if (player.worldObj.provider.dimensionId == ExtraChromaIDs.DIMID.getValue()) {
			ticksDelay = 0; // Nope. no delay.
			SkyRiverGenerator.RiverPoint rp = SkyRiverGenerator.getClosestPoint(player, 128, true);
			if (rp != null) {
				DecimalPosition pos = rp.position;
				for (int i = 0; i < SkyRiverGenerator.rays.size(); i++) { // Make
					// this
					// a
					// bit
					// prettier
					// one
					// day...
					SkyRiverGenerator.Ray r = SkyRiverGenerator.rays.get(i);
					if (r.getPoints().contains(pos)) {
						startIndex = i;
						break;
					}
				}
			}
		}
		for (int i = 0; i < SkyRiverGenerator.rays.size(); i++) {
			int index = (startIndex + i) % SkyRiverGenerator.rays.size();
			SkyRiverGenerator.Ray toSend = SkyRiverGenerator.rays.get(index);
			schedulePacketSending(player, toSend, ticksDelay + (i * 10)); // 10
			// Ticks
			// as
			// delay
			// between
			// packets
			// to
			// split
			// the
			// load.
		}
	}

	protected static void startSendingRiverPacketsToAll() {
		List objPlayers;
		try {
			objPlayers = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
		}
		catch (NullPointerException exc) {
			// Well, then we're still in the server startup and sending is not
			// necessary.
			return;
		}
		for (Object player : objPlayers) {
			startSendingRiverPackets((EntityPlayer)player);
		}
	}

	private static void schedulePacketSending(EntityPlayer player, SkyRiverGenerator.Ray toSend, int delay) {
		DelayedSkyRiverPacketEvent pktEvent = new DelayedSkyRiverPacketEvent(player, toSend);
		delayedPackets.add(pktEvent);
		TickScheduler.instance.scheduleEvent(new ScheduledTickEvent(pktEvent), delay);
	}

	private static class DelayedSkyRiverPacketEvent implements ScheduledTickEvent.ScheduledEvent {

		private final EntityPlayer recipient;
		private final SkyRiverGenerator.Ray toSend;
		private boolean aborted = false;

		public DelayedSkyRiverPacketEvent(EntityPlayer player, SkyRiverGenerator.Ray toSend) {
			recipient = player;
			this.toSend = toSend;
		}

		@Override
		public void fire() {
			synchronized (delayedPackets) {
				delayedPackets.remove(this);
			}
			if (aborted)
				return;
			sendRiverPoints(recipient, toSend);
		}

	}

	public static class TrackingConditionSkyRiver implements PlayerChunkTracker.TrackingCondition {

		@Override
		public boolean shouldBeTracked(EntityPlayer player) {
			return SkyRiverGenerator.isWithinSkyRiver(player, true);
		}

		@Override
		public void onUntrack(EntityPlayer player) {
			debugMessage("Player " + player.getCommandSenderName() + " has left a SkyRiver");
			handleUntrack();
		}

	}

}
