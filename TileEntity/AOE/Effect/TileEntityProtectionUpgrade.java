/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.AOE.Effect;

import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Base.TileEntity.TileEntityAdjacencyUpgrade;
import Reika.ChromatiCraft.Registry.CrystalElement;


public class TileEntityProtectionUpgrade extends TileEntityAdjacencyUpgrade {

	private static double[] MAX_BLAST = {
			0.5,
			1,
			2,
			4,
			6,
			12,
			48,
			Double.POSITIVE_INFINITY
	};

	@Override
	protected EffectResult tickDirection(World world, int x, int y, int z, ForgeDirection dir, long startTime) {
		return EffectResult.STOP;
	}

	@Override
	public CrystalElement getColor() {
		return CrystalElement.RED;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	public static boolean canExplode(World world, double dx, double dy, double dz, double power, boolean ic2Nuke) {
		int x = MathHelper.floor_double(dx);
		int y = MathHelper.floor_double(dy);
		int z = MathHelper.floor_double(dz);
		Integer get = getAdjacentUpgrades(world, x, y, z).get(CrystalElement.RED);
		if (ic2Nuke) {
			if (get == null || get.intValue() == 0) {
				get = checkForStep2(world, x, y, z);
			}
		}
		if (get == null || get.intValue() == 0) {
			return true;
		}
		else {
			return power > getMaxProtectedPower(get.intValue()-1);
		}
	}

	private static Integer checkForStep2(World world, int x, int y, int z) {
		Integer ret = null;
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			Integer at = getAdjacentUpgrades(world, dx, dy, dz).get(CrystalElement.RED);
			if (at != null) {
				if (ret == null) {
					ret = at;
				}
				else {
					ret = Math.max(ret, at);
				}
			}
		}
		return ret;
	}

	public static double getMaxProtectedPower(int tier) {
		return MAX_BLAST[tier];
	}

}
