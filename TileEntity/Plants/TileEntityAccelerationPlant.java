/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Plants;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityMagicPlant;
import Reika.ChromatiCraft.Registry.ChromaTiles;


public class TileEntityAccelerationPlant extends TileEntityMagicPlant {

	@Override
	public ForgeDirection getGrowthDirection() {
		return null;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.PLANTACCEL;
	}

	public boolean isActive() {
		return true;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {

	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

}
