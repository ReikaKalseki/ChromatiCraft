/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Plants;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Auxiliary.Interfaces.EffectPlant;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Interfaces.TileEntity.BreakAction;

public class TileEntityHeatLily extends TileEntityChromaticBase implements EffectPlant, BreakAction {

	private StepTimer timer = new StepTimer(100);

	private static final Collection<WorldLocation> cache = new ArrayList();

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
		WorldLocation loc = new WorldLocation(this);
		if (!cache.contains(loc))
			cache.add(loc);
	}

	@Override
	public void breakBlock() {
		WorldLocation loc = new WorldLocation(this);
		cache.remove(loc);
	}

	public static boolean stopFreeze(World world, int x, int y, int z) {
		for (WorldLocation te : cache) {
			int dd = Math.abs(x-te.xCoord)+Math.abs(y-te.yCoord)+Math.abs(z-te.zCoord);
			if (dd <= 7) {
				return true;
			}
		}
		return false;
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
