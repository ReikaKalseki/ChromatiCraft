/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic.Artefact;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Random;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.World.IWG.UnknownArtefactGenerator;
import Reika.DragonAPI.APIPacketHandler.PacketIDs;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickHandler;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickType;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;


public class ArtefactSpawner implements TickHandler {

	private static final int SPAWN_DISTANCE = 32;
	private static final int DESPAWN_DISTANCE = 64;

	private static final int SPAWN_LIMIT = 3;

	private static final Random rand = new Random();

	private final ArrayList<SpawnedArtefact> artefacts = new ArrayList();

	public static final ArtefactSpawner instance = new ArtefactSpawner();

	private ArtefactSpawner() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void tick(TickType type, Object... tickData) {
		EntityPlayer pl = (EntityPlayer)tickData[0];
		if (pl.worldObj.isRemote)
			return;
		EntityPlayerMP ep = (EntityPlayerMP)pl;
		if (rand.nextInt(2) == 0 && this.canSpawnArtefactNearPlayer(ep))
			ReikaPacketHelper.sendDataPacket(DragonAPIInit.packetChannel, PacketIDs.PARTICLE.ordinal(), ep.worldObj, MathHelper.floor_double(ep.posX), (int)ep.posY+1, MathHelper.floor_double(ep.posZ), new PacketTarget.PlayerTarget(ep), ReikaJavaLibrary.makeListFrom(ReikaParticleHelper.PORTAL.ordinal(), 1));
		if (artefacts.size() < SPAWN_LIMIT && rand.nextInt(1000) == 0 && this.canSpawnArtefactNearPlayer(ep)) {
			double a = Math.toRadians(rand.nextDouble()*360);
			double dx = ep.posX+SPAWN_DISTANCE*Math.cos(a);
			double dz = ep.posZ+SPAWN_DISTANCE*Math.sin(a);
			int x = MathHelper.floor_double(dx);
			int z = MathHelper.floor_double(dz);
			int y = ep.worldObj.getTopSolidOrLiquidBlock(x, z)-1;
			if (UnknownArtefactGenerator.canGenerateArtefactAt(ep.worldObj, x, y, z)) {
				double dy = y-0.5;
				artefacts.add(new SpawnedArtefact(dx, dy, dz, ep));
			}
		}
		if (!artefacts.isEmpty()) {
			Iterator<SpawnedArtefact> it = artefacts.iterator();
			while (it.hasNext()) {
				SpawnedArtefact a = it.next();
				if (a.tick(ep))
					it.remove();
			}
		}
	}

	private boolean canSpawnArtefactNearPlayer(EntityPlayer ep) {
		if (ReikaPlayerAPI.isFake(ep))
			return false;
		int cx = ReikaMathLibrary.roundDownToX(16, MathHelper.floor_double(ep.posX));
		int cz = ReikaMathLibrary.roundDownToX(16, MathHelper.floor_double(ep.posZ));
		return UnknownArtefactGenerator.instance.isUAChunk(ep.worldObj, cx, cz);
	}

	public boolean isOnTopOfArtefact(World world, int x, int y, int z) {
		if (!artefacts.isEmpty()) {
			Iterator<SpawnedArtefact> it = artefacts.iterator();
			while (it.hasNext()) {
				SpawnedArtefact a = it.next();
				Coordinate c = a.position.getCoordinate();
				if (c.equals(x, y-1, z)) {
					it.remove(); //remove if found
					return true;
				}
			}
		}
		return false;
	}

	//only fired serverside, must dispatch to client -- not anymore
	@SubscribeEvent
	public void checkPlayerBreak(BreakEvent evt) {
		EntityPlayer ep = evt.getPlayer();
		if (ep instanceof EntityPlayerMP && !ReikaPlayerAPI.isFake(ep)) {
			int cx = ReikaMathLibrary.roundDownToX(16, evt.x);
			int cz = ReikaMathLibrary.roundDownToX(16, evt.z);
			//if (UnknownArtefactGenerator.instance.isUAChunk(evt.world, cx, cz)) {
			//ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.DIGARTEFACT.ordinal(), (EntityPlayerMP)ep, evt.x, evt.y, evt.z);
			if (this.isOnTopOfArtefact(evt.world, evt.x, evt.y, evt.z)) {
				this.confirmUA(evt.world, evt.x, evt.y, evt.z);
			}
			//}
		}
	}

	//client
	/*
	public void checkPlayerBreakClient(World world, int x, int y, int z) {
		if (this.isOnTopOfArtefact(world, x, y, z)) {
			ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.ARTEFACTCONFIRM.ordinal(), PacketTarget.server, x, y, z);
		}
	}
	 */

	//server
	private void confirmUA(World world, int x, int y, int z) {
		world.setBlock(x, y-1, z, ChromaBlocks.ARTEFACT.getBlockInstance());
	}

	@Override
	public EnumSet<TickType> getType() {
		return EnumSet.of(TickType.PLAYER);
	}

	@Override
	public boolean canFire(Phase p) {
		return p == Phase.START;
	}

	@Override
	public String getLabel() {
		return "UA Spawner";
	}

	public static class SpawnedArtefact {

		private final DecimalPosition position;
		private final UUID spawnedByPlayer;

		private SpawnedArtefact(double x, double y, double z, EntityPlayer ep) {
			position = new DecimalPosition(x, y, z);
			spawnedByPlayer = ep.getUniqueID();
		}

		private boolean tick(EntityPlayerMP ep) {
			ReikaPacketHelper.sendPositionPacket(ChromatiCraft.packetChannel, ChromaPackets.UAFX.ordinal(), ep.worldObj, position.xCoord, position.yCoord, position.zCoord, new PacketTarget.PlayerTarget(ep));
			return ep.getUniqueID().equals(spawnedByPlayer) && ep.getDistanceSq(position.xCoord, position.yCoord, position.zCoord) >= DESPAWN_DISTANCE*DESPAWN_DISTANCE;
		}

	}

}
