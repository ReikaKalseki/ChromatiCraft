/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Auxiliary.Trackers.PlayerChunkTracker;
import Reika.DragonAPI.Auxiliary.Trackers.TickScheduler;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Data.Maps.PlayerMap;
import Reika.DragonAPI.Instantiable.Event.ScheduledTickEvent;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaVectorHelper;

import cpw.mods.fml.relauncher.Side;

/* This class was originally written by HellFirePvP, but has since been heavily modified by Reika. The core logic remains his. */
public class SkyRiverManager {

	public static final PlayerChunkTracker.TrackingCondition skyRiverCondition = new TrackingConditionSkyRiver();
	private static final List<DelayedSkyRiverPacketEvent> delayedPackets = new LinkedList();
	private static final PlayerMap<RiverStatus> statusData = new PlayerMap(); // Allows the server to keep up after leaving the SkyRiver
	private static final int SKYRIVER_ENTER_DELAY = 60; // ticks - allows the server to catch up after untrack.
	private static final Random rand = new Random();

	public static void tickSkyRiverServer(World w) {
		if (!w.isRemote) {
			for (Object objPl : w.playerEntities) {
				if (!(objPl instanceof EntityPlayer) || ((EntityPlayer)objPl).isDead)
					continue;
				EntityPlayer pl = (EntityPlayer)objPl;
				RiverStatus rs = getOrCreateEntry(pl);
				rs.tick();
				boolean flag = false;
				if (rs.isMovable()) {
					SkyRiverGenerator.RiverPoint closest = SkyRiverGenerator.getClosestPoint(pl, 16, true);
					if (closest != null) {
						if (SkyRiverGenerator.isWithinSkyRiver(pl, closest)) {
							if (!PlayerChunkTracker.shouldStopChunkloadingFor(pl)) {
								PlayerChunkTracker.startTrackingPlayer(pl, skyRiverCondition);
								sendSkyriverEnterStatePacket(pl, true);
								debugMessage("Player " + pl.getCommandSenderName() + " has entered a SkyRiver.");
							}
							movePlayer(pl, rs, closest, false);
							flag = true;
						}
					}
				}
				if (!flag)
					rs.riverTime = 0;
			}
		}
	}

	private static RiverStatus getOrCreateEntry(EntityPlayer ep) {
		RiverStatus rs = statusData.get(ep);
		if (rs == null) {
			rs = new RiverStatus(ep);
			statusData.put(ep, rs);
		}
		return rs;
	}

	static void movePlayer(EntityPlayer player, SkyRiverGenerator.RiverPoint rp, boolean doesMove) {
		movePlayer(player, getOrCreateEntry(player), rp, doesMove);
	}

	// Can/Will be called from Server and Client
	// Set motionX, motionY, motionZ according to the RiverPoints
	// TODO redo.
	private static void movePlayer(EntityPlayer player, RiverStatus rs, SkyRiverGenerator.RiverPoint rp, boolean doesMove) {
		DecimalPosition plPos = new DecimalPosition(player);
		double dst;
		Vec3 pullVector, nodeVector, moveVector;

		// It's important to check for pos->next before checking for prev->pos
		// That way, pulling further is preferred over pulling backwards...

		// checks if the player is between RiverPoint.position and RiverPoint.next
		dst = ReikaVectorHelper.getDistFromPointToLine(rp.next.xCoord, rp.next.yCoord, rp.next.zCoord, rp.position.xCoord, rp.position.yCoord, rp.position.zCoord, player.posX, player.posY, player.posZ);
		if (dst < SkyRiverGenerator.RIVER_TUNNEL_RADIUS) {
			pullVector = Vec3.createVectorHelper(rp.next.xCoord - rp.position.xCoord, rp.next.yCoord - rp.position.yCoord, rp.next.zCoord - rp.position.zCoord);
			nodeVector = Vec3.createVectorHelper(rp.next.xCoord - plPos.xCoord, rp.next.yCoord - plPos.yCoord, rp.next.zCoord - plPos.zCoord);
		}
		else {
			// checks if the player is between RiverPoint.prev and RiverPoint.position
			dst = ReikaVectorHelper.getDistFromPointToLine(rp.position.xCoord, rp.position.yCoord, rp.position.zCoord, rp.prev.xCoord, rp.prev.yCoord, rp.prev.zCoord, player.posX, player.posY, player.posZ);
			if (dst < SkyRiverGenerator.RIVER_TUNNEL_RADIUS) {
				pullVector = Vec3.createVectorHelper(rp.position.xCoord - rp.prev.xCoord, rp.position.yCoord - rp.prev.yCoord, rp.position.zCoord - rp.prev.zCoord);
				nodeVector = Vec3.createVectorHelper(rp.position.xCoord - plPos.xCoord, rp.position.yCoord - plPos.yCoord, rp.position.zCoord - plPos.zCoord);
			}
			else {
				return; // Well. then we're not in a SkyRiver.
			}
		}

		pullVector = pullVector.normalize(); // Normalized vector parallel in the direction of the SkyRiver
		nodeVector = nodeVector.normalize(); // Normalized vector pointing from the player towards the next node.
		moveVector = Vec3.createVectorHelper(pullVector.xCoord * 0.6 + nodeVector.xCoord * 0.4, pullVector.yCoord * 0.6 + nodeVector.yCoord * 0.4, pullVector.zCoord * 0.6 + nodeVector.zCoord * 0.4);
		if (doesMove) {
			Vec3 playerMove = Vec3.createVectorHelper(player.motionX, player.motionY, player.motionZ).normalize();
			moveVector = Vec3.createVectorHelper(moveVector.xCoord * 0.1 + playerMove.xCoord * 0.9, moveVector.yCoord, moveVector.zCoord * 0.1 + playerMove.zCoord * 0.9);
		}
		double multiplier = 7D;
		float f = DimensionTuningManager.TuningThresholds.SKYRIVER.getTuningFraction(player);
		if (f <= 0) {
			moveVector.xCoord = rp.next.xCoord-rp.position.xCoord;
			moveVector.yCoord = rp.next.yCoord-rp.position.yCoord;
			moveVector.zCoord = rp.next.zCoord-rp.position.zCoord;
			moveVector = moveVector.normalize();
			multiplier = -1;
			ejectPlayer(player, rs);
		}
		else if (f < 1) {
			rand.setSeed(player.getUniqueID().hashCode()^player.worldObj.getTotalWorldTime());
			rand.nextBoolean();
			rand.nextBoolean();
			//int maxl = 50+(int)(f*200)+rand.nextInt(50)+(int)(f*rand.nextInt(100));
			//if (rs.riverTime >= maxl) {
			double d = player.getDistanceSq(0, player.posY, 0);
			double maxd = 150+50*rand.nextDouble()+f*(200+rand.nextInt(100));
			if (d > maxd*maxd) {
				//double rx = rand.nextDouble()*360;
				//double ry = rand.nextDouble()*360;
				//double rz = rand.nextDouble()*360;
				//moveVector = ReikaVectorHelper.rotateVector(moveVector, rx, ry, rz);
				//multiplier = 1;
				ejectPlayer(player, rs);
				return;
			}
		}

		rs.riverTime++;

		player.motionX = moveVector.xCoord * multiplier;
		player.motionY = moveVector.yCoord * multiplier;
		player.motionZ = moveVector.zCoord * multiplier;

		if (player instanceof EntityPlayerMP) {
			((EntityPlayerMP)player).playerNetServerHandler.floatingTickCount = 0;
		}
	}

	private static void ejectPlayer(EntityPlayer player, RiverStatus rs) {
		if (rs.isMovable() && player.worldObj.isRemote)
			ReikaSoundHelper.playClientSound(ChromaSounds.ERROR, player, 1, 1, false);
		player.capabilities.allowFlying = false;
		player.capabilities.isFlying = false;
		rs.ejectCooldown = SKYRIVER_ENTER_DELAY;
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
		Collection<EntityPlayerMP> objPlayers;
		try {
			objPlayers = ReikaPlayerAPI.getAllPlayers();
		}
		catch (NullPointerException exc) {
			// Well, then we're still in the server startup and sending is not necessary.
			return;
		}
		for (EntityPlayer player : objPlayers) {
			clearClientRiver(player);
		}
	}

	public static void clearClientRiver(EntityPlayer player) {
		synchronized (delayedPackets) { // Remove packets in queue. That way we don't send wrong data.
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

	// Fired, once a player leaves a SkyRiver. If this happens, we can expect LAG from ChunkLoading.
	// Don't allow new SkyRiver entries from anyone that isn't inside one atm.
	// Players already inside don't matter, they don't cause ChunkLoading anyway since they're already tracked.
	// New people do matter since if they'd move around, they accumulate non-tracked movement packets which cause >200 chunks to load.
	// RIP server if we don't handle that. Thus, everyone gets a refresh-state DENY packet and set onto the enter-delay.
	private static void handleUntrack() {
		Collection<EntityPlayerMP> objPlayers;
		try {
			objPlayers = ReikaPlayerAPI.getAllPlayers();
		}
		catch (NullPointerException exc) {
			return;
		}

		for (EntityPlayer pl : objPlayers) {
			if (!PlayerChunkTracker.shouldStopChunkloadingFor(pl)) {
				sendSkyriverEnterStatePacket(pl, false);
				RiverStatus rs = getOrCreateEntry(pl);
				rs.departCooldown = SKYRIVER_ENTER_DELAY;
			}
		}
	}

	// Split up river rays and send them individually with some delay.
	// If there's a river close to the player in the chroma dim, send that one first!
	// If the player is in the chroma dim, start sending them immediately rather than 10 sec after login. He might need the rays.
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
			schedulePacketSending(player, toSend, ticksDelay + (i * 10)); // 10 Ticks as delay between packets to split the load.
		}
	}

	protected static void startSendingRiverPacketsToAll() {
		Collection<EntityPlayerMP> objPlayers;
		try {
			objPlayers = ReikaPlayerAPI.getAllPlayers();
		}
		catch (NullPointerException exc) {
			// Well, then we're still in the server startup and sending is not necessary.
			return;
		}
		for (EntityPlayer player : objPlayers) {
			startSendingRiverPackets(player);
		}
	}

	private static void schedulePacketSending(EntityPlayer player, SkyRiverGenerator.Ray toSend, int delay) {
		DelayedSkyRiverPacketEvent pktEvent = new DelayedSkyRiverPacketEvent(player, toSend);
		delayedPackets.add(pktEvent);
		TickScheduler.instance.scheduleEvent(new ScheduledTickEvent(pktEvent), 1+delay);
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

		@Override
		public boolean runOnSide(Side s) {
			return s == Side.SERVER;
		}

	}

	private static class RiverStatus {

		private final UUID player;
		private final String name;

		private RiverStatus(EntityPlayer ep) {
			player = ep.getPersistentID();
			name = ep.getCommandSenderName();
		}

		private int ejectCooldown;
		private int departCooldown;
		private int riverTime;

		public boolean isMovable() {
			return ejectCooldown == 0 && departCooldown == 0;
		}

		private void tick() {
			boolean move = this.isMovable();
			if (ejectCooldown > 0)
				ejectCooldown--;
			if (departCooldown > 0)
				departCooldown--;
			if (!move && this.isMovable())
				debugMessage("Player " + name + " may enter the SkyRiver again.");
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
