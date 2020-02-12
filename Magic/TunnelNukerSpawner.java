/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2018
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic;

import java.util.ArrayList;
import java.util.EnumSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Entity.EntityTunnelNuker;
import Reika.ChromatiCraft.Magic.Lore.LoreManager;
import Reika.ChromatiCraft.Magic.Lore.Towers;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickHandler;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickType;
import Reika.DragonAPI.Libraries.Java.ReikaArrayHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;

import cpw.mods.fml.common.gameevent.TickEvent.Phase;


public class TunnelNukerSpawner implements TickHandler {

	public static final TunnelNukerSpawner instance = new TunnelNukerSpawner();

	private TunnelNukerSpawner() {

	}

	@Override
	public void tick(TickType type, Object... tickData) {
		World world = (World)tickData[0];
		if (!world.isRemote && !world.playerEntities.isEmpty() && world.rand.nextInt(120) == 0) {
			EntityPlayer ep = (EntityPlayer)world.playerEntities.get(world.rand.nextInt(world.playerEntities.size()));

			ArrayList<Integer> li = ReikaJavaLibrary.makeIntListFromArray(ReikaArrayHelper.getLinearArray(Towers.towerList.length));
			int idx = li.remove(ep.getRNG().nextInt(li.size()));
			Towers t = Towers.towerList[idx];
			while (LoreManager.instance.hasPlayerScanned(ep, t)) {
				if (li.isEmpty()) {
					return;
				}
				else {
					idx = li.remove(ep.getRNG().nextInt(li.size()));
					t = Towers.towerList[idx];
				}
			}

			double ex = ReikaRandomHelper.getRandomPlusMinus(ep.posX, 32);
			double ez = ReikaRandomHelper.getRandomPlusMinus(ep.posZ, 32);
			double ey = 7.5+world.getTopSolidOrLiquidBlock(MathHelper.floor_double(ex), MathHelper.floor_double(ez));
			ReikaJavaLibrary.pConsole("Spawning tunnel nuker for tower "+t+" @ "+ex+", "+ez);
			ChunkCoordIntPair p2 = t.getRootPosition();
			int tx = p2.chunkXPos+8;
			int tz = p2.chunkZPos+8;

			if (t.getGeneratedLocation() != null) {
				tx = t.getGeneratedLocation().xCoord;
				tz = t.getGeneratedLocation().zCoord;
			}

			double dx = tx-ex;
			double dz = tz-ez;

			double[] angs = ReikaPhysicsHelper.cartesianToPolar(dx, 0, dz);
			EntityTunnelNuker e = new EntityTunnelNuker(world);
			e.setLocationAndAngles(ex, ey, ez, -(float)angs[2]+90, 0);
			world.spawnEntityInWorld(e);
		}
	}

	@Override
	public EnumSet<TickType> getType() {
		return EnumSet.of(TickType.WORLD);
	}

	@Override
	public boolean canFire(Phase p) {
		return p == Phase.START;
	}

	@Override
	public String getLabel() {
		return "Tunnel Nuker Spawner";
	}

}
