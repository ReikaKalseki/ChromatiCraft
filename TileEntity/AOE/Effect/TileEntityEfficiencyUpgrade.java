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

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityAdjacencyUpgrade;
import Reika.ChromatiCraft.Registry.CrystalElement;


public class TileEntityEfficiencyUpgrade extends TileEntityAdjacencyUpgrade {

	private int[] lastRFValues = new int[6];

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

}
