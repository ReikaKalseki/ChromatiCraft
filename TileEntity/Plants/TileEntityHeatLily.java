/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
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
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Base.TileEntity.TileEntityMagicPlant;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Interfaces.TileEntity.LocationCached;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaPlantHelper;

public class TileEntityHeatLily extends TileEntityMagicPlant implements LocationCached {

	private StepTimer timer = new StepTimer(40);

	private static final Collection<WorldLocation> cache = new ArrayList();

	@Override
	public ForgeDirection getGrowthDirection() {
		return ForgeDirection.UP;
	}

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
		int dx = ReikaRandomHelper.getRandomPlusMinus(x, r);
		int dy = y-1;
		int dz = ReikaRandomHelper.getRandomPlusMinus(z, r);
		Block b = world.getBlock(dx, dy, dz);
		if (b == Blocks.ice) {
			world.setBlock(dx, dy, dz, Blocks.flowing_water);
		}
		else if (b == Blocks.snow_layer) {
			world.setBlock(dx, dy, dz, Blocks.air);
		}
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public boolean isPlantable(World world, int x, int y, int z) {
		return ReikaPlantHelper.LILYPAD.canPlantAt(world, x, y, z);
	}

}
