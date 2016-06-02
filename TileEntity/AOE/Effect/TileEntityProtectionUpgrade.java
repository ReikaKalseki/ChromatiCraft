/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.AOE.Effect;

import net.minecraft.world.Explosion;
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
	protected boolean tickDirection(World world, int x, int y, int z, ForgeDirection dir, long startTime) {
		return false;
	}

	@Override
	public CrystalElement getColor() {
		return CrystalElement.RED;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	public static boolean canExplode(World world, int x, int y, int z, Explosion e) {
		Integer get = getAdjacentUpgrades(world, x, y, z).get(CrystalElement.RED);
		if (get == null || get.intValue() == 0) {
			return true;
		}
		else {
			return e.explosionSize > getMaxProtectedPower(get.intValue()-1);
		}
	}

	public static double getMaxProtectedPower(int tier) {
		return MAX_BLAST[tier];
	}

}
