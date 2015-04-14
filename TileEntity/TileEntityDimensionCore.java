/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity;

import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityLocusPoint;
import Reika.ChromatiCraft.Registry.ChromaTiles;

//Structure core, does FX and things
public class TileEntityDimensionCore extends TileEntityLocusPoint {

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		super.onFirstTick(world, x, y, z);
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.DIMENSIONCORE;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

}
