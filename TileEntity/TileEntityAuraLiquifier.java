/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Base.TileEntity.FluidEmitterChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaTiles;

@Deprecated
public class TileEntityAuraLiquifier extends FluidEmitterChromaticBase {

	@Override
	public int getCapacity() {
		return 12000;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {

	}

	@Override
	public boolean canOutputTo(ForgeDirection to) {
		return true;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.LIQUIFIER;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

}
