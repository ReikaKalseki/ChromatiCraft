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

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityAdjacencyUpgrade;
import Reika.ChromatiCraft.Registry.CrystalElement;


public class TileEntityEfficiencyUpgrade extends TileEntityAdjacencyUpgrade {

	private static double[] factor = {
		0.975,
		0.875,
		0.75,
		0.625,
		0.5,
		0.25,
		0.125,
		0.0625,
	};

	@Override
	protected boolean tickDirection(World world, int x, int y, int z, ForgeDirection dir, long startTime) {
		return false;
	}

	@Override
	public CrystalElement getColor() {
		return CrystalElement.BLACK;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	public static double getCostFactor(int tier) {
		return factor[tier];
	}

}
