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
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickHandler;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickType;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class ArtefactSpawner implements TickHandler {

	private static final int SPAWN_DISTANCE = 32;
	private static final int DESPAWN_DISTANCE = 64;

	private static final int SPAWN_LIMIT = 3;
	private static final int FORCED_SPAWN_LIMIT = 20;

	private static final Random rand = new Random();

	private final ArrayList<SpawnedArtefact> artefacts = new ArrayList();

	private static long lastShaderTime = -1;

	public static final ArtefactSpawner instance = new ArtefactSpawner();

	private ArtefactSpawner() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void tick(TickType type, Object... tickData) {
		EntityPlayer pl = (EntityPlayer)tickData[0];
		if (pl.worldObj.isRemote) {
			this.updateShader(pl.worldObj);
			return;
		}
		if (!UnknownArtefactGenerator.instance.canGenerateIn(pl.worldObj)) {
			artefacts.clear();
			return;
		}
		EntityPlayerMP ep = (EntityPlayerMP)pl;
		boolean canSpawn = this.canSpawnArtefactNearPlayer(ep);
		if (canSpawn && rand.nextInt(2) == 0)
			ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.ARTEZONEPARTICLES.ordinal(), ep);
		if (canSpawn && artefacts.size() < SPAWN_LIMIT && rand.nextInt(1000) == 0) {
			double a = Math.toRadians(rand.nextDouble()*360);
			double dx = ep.posX+SPAWN_DISTANCE*Math.cos(a);
			double dz = ep.posZ+SPAWN_DISTANCE*Math.sin(a);
			this.addArtefact(dx, dz, ep, Integer.MAX_VALUE);
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

	public void addArtefact(double dx, double dz, EntityPlayer ep, int life) {
		if (artefacts.size() < FORCED_SPAWN_LIMIT) {
			int x = MathHelper.floor_double(dx);
			int z = MathHelper.floor_double(dz);
			int y = ep.worldObj.getTopSolidOrLiquidBlock(x, z)-1;
			if (UnknownArtefactGenerator.canGenerateArtefactAt(ep.worldObj, x, y, z)) {
				double dy = y-0.5;
				artefacts.add(new SpawnedArtefact(dx, dy, dz, ep, life));
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

	@SideOnly(Side.CLIENT)
	public static void refreshShader(EntityPlayer ep) {
		//double ang = rand.nextDouble()*360;
		lastShaderTime = ep.worldObj.getTotalWorldTime();
	}

	@SideOnly(Side.CLIENT)
	private static void updateShader(World world) {
		if (isShaderActive(world)) {

		}
	}

	public static boolean isShaderActive(World world) {
		return world.getTotalWorldTime()-lastShaderTime <= 10;
	}

	public static class SpawnedArtefact {

		private final DecimalPosition position;
		private final UUID spawnedByPlayer;
		private final int maxLife;

		private int age;

		private SpawnedArtefact(double x, double y, double z, EntityPlayer ep) {
			this(x, y, z, ep, Integer.MAX_VALUE);
		}

		private SpawnedArtefact(double x, double y, double z, EntityPlayer ep, int l) {
			position = new DecimalPosition(x, y, z);
			spawnedByPlayer = ep.getUniqueID();
			maxLife = l;
		}

		private boolean tick(EntityPlayerMP ep) {
			ReikaPacketHelper.sendPositionPacket(ChromatiCraft.packetChannel, ChromaPackets.UAFX.ordinal(), ep.worldObj, position.xCoord, position.yCoord, position.zCoord, new PacketTarget.PlayerTarget(ep));
			age++;
			return age >= maxLife || (ep.getUniqueID().equals(spawnedByPlayer) && ep.getDistanceSq(position.xCoord, position.yCoord, position.zCoord) >= DESPAWN_DISTANCE*DESPAWN_DISTANCE);
		}

	}

}
