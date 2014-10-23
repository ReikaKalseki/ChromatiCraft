/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Plants;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromaticEventManager;
import Reika.ChromatiCraft.Auxiliary.Interfaces.EffectPlant;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Instantiable.StepTimer;

public class TileEntityHeatLily extends TileEntityChromaticBase implements EffectPlant {

	private StepTimer timer = new StepTimer(100);

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.HEATLILY;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		timer.update();
		if (timer.checkCap()) {
			this.meltIce(world, x, y, z);
		}
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		if (!world.isRemote)
			ChromaticEventManager.instance.addLily(this);
	}

	private void meltIce(World world, int x, int y, int z) {
		int r = 3;
		for (int i = -r; i <= r; i++) {
			for (int k = -r; k <= r; k++) {
				int dx = x+i;
				int dy = y-1;
				int dz = z+k;
				Block b = world.getBlock(dx, dy, dz);
				if (b == Blocks.ice) {
					world.setBlock(dx, dy, dz, Blocks.flowing_water);
				}
				else if (b == Blocks.snow_layer) {
					world.setBlock(dx, dy, dz, Blocks.air);
				}
			}
		}
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

}
