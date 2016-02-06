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

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import Reika.ChromatiCraft.Auxiliary.Interfaces.EffectPlant;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Instantiable.Data.WeightedRandom;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Interfaces.TileEntity.LocationCached;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

public class TileEntityBiomeReverter extends TileEntityChromaticBase implements EffectPlant, LocationCached {

	private StepTimer timer = new StepTimer(40);

	private static double[][] randomDistrib = {
		{0, 0, 0, 1 , 2 , 1 , 0, 0, 0},
		{0, 0, 1, 2 , 5 , 2 , 1, 0, 0},
		{0, 1, 3, 5 , 7 , 5 , 3, 1, 0},
		{1, 2, 5, 8 , 10, 8 , 5, 2, 1},
		{2, 5, 7, 10, 10, 10, 7, 5, 2},
		{1, 2, 5, 8	, 10, 8 , 5, 2, 1},
		{0, 1, 3, 5	, 7 , 5 , 3, 1, 0},
		{0, 0, 1, 2	, 5 , 2 , 1, 0, 0},
		{0, 0, 0, 1	, 2 , 1 , 0, 0, 0},
	};

	private static final Collection<WorldLocation> cache = new ArrayList();

	private static final WeightedRandom<Coordinate> coordinateRand = new WeightedRandom();

	static {
		for (int i = 0; i < randomDistrib.length; i++) {
			for (int k = 0; k < randomDistrib[i].length; k++) {
				Coordinate c = new Coordinate(i-4, 0, k-4);
				if (randomDistrib[i][k] > 0) {
					coordinateRand.addEntry(c, randomDistrib[i][k]);
				}
			}
		}
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.REVERTER;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (world.isRemote)
			return;
		timer.update();
		if (timer.checkCap()) {
			this.revertBiome(world, x, y, z);
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

	public static boolean stopConversion(World world, int x, int z) {
		for (WorldLocation te : cache) {
			Coordinate c = new Coordinate(x-te.xCoord, 0, z-te.zCoord);
			if (coordinateRand.hasEntry(c)) {
				return true;
			}
		}
		return false;
	}

	private void revertBiome(World world, int x, int y, int z) {
		Coordinate c = coordinateRand.getRandomEntry().offset(x, y, z);
		BiomeGenBase b = c.getBiome(world);
		BiomeGenBase nat = ReikaWorldHelper.getNaturalGennedBiomeAt(world, c.xCoord, c.zCoord);
		if (b != nat && nat != null) {
			ReikaWorldHelper.setBiomeForXZ(world, c.xCoord, c.zCoord, nat);
		}
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

}
