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

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Base.TileEntity.TileEntityAdjacencyUpgrade;
import Reika.ChromatiCraft.Registry.CrystalElement;


public class TileEntityTankCapacityUpgrade extends TileEntityAdjacencyUpgrade {

	private static double[] CAPACITY_FACTOR = {
			1.03125,
			1.0625,
			1.125,
			1.25,
			1.5,
			2,
			4,
			8
	};

	@Override
	protected EffectResult tickDirection(World world, int x, int y, int z, ForgeDirection dir, long startTime) {
		return EffectResult.STOP;
	}

	@Override
	public CrystalElement getColor() {
		return CrystalElement.CYAN;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	public static double getCapacityFactor(int tier) {
		return CAPACITY_FACTOR[tier];
	}

}
