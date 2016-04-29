/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.AOE.Effect;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.API.Interfaces.RangeUpgradeable;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityAdjacencyUpgrade;
import Reika.ChromatiCraft.Registry.CrystalElement;


public class TileEntityRangeBoost extends TileEntityAdjacencyUpgrade {

	private static double[] factors = {
		1.03125,
		1.0625,
		1.125,
		1.25,
		1.5,
		2,
		3,
		4
	};

	@Override
	protected boolean tickDirection(World world, int x, int y, int z, ForgeDirection dir, long startTime) {
		TileEntity te = this.getAdjacentTileEntity(dir);
		if (te instanceof RangeUpgradeable) {
			double r = this.getRangeFactor();
			((RangeUpgradeable)te).upgradeRange(r);
		}
		return true;
	}

	public double getRangeFactor() {
		return factors[this.getTier()];
	}

	@Override
	public CrystalElement getColor() {
		return CrystalElement.LIME;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	public static double getFactor(int tier) {
		return factors[tier];
	}

}
