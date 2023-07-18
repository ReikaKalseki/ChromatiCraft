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

import java.util.Collection;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Auxiliary.Interfaces.ComplexAOE;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityMagicPlant;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.TileEntity.Auxiliary.TileEntityFunctionRelay;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Instantiable.Data.WeightedRandom;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Libraries.Registry.ReikaPlantHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

public class TileEntityBiomeReverter extends TileEntityMagicPlant implements ComplexAOE/* implements LocationCached*/ {

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

	//private static final Collection<WorldLocation> cache = new ArrayList();

	private static final WeightedRandom<Coordinate> coordinateRand = WeightedRandom.fromArray(randomDistrib);

	@Override
	public ForgeDirection getGrowthDirection() {
		return ForgeDirection.UP;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.REVERTER;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (world.isRemote)
			return;
		int n = 1+2*this.getAccelerationPlants();
		timer.update(n);
		if (timer.checkCap()) {
			this.revertBiome(world, x, y, z, n > 5);
		}
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		WorldLocation loc = new WorldLocation(this);
		//if (!cache.contains(loc))
		//	cache.add(loc);
	}
	/*
	@Override
	public void breakBlock() {
		WorldLocation loc = new WorldLocation(this);
		cache.remove(loc);
	}*/

	public static boolean stopConversion(World world, int x, int z) {/*
		for (WorldLocation te : cache) {
			Coordinate c = new Coordinate(x-te.xCoord, 0, z-te.zCoord);
			if (coordinateRand.hasEntry(c)) {
				return true;
			}
		}*/
		return false;
	}

	private void revertBiome(World world, int x, int y, int z, boolean large) {
		Coordinate c = coordinateRand.getRandomEntry();
		if (large && rand.nextInt(4) == 0) {
			c = c.scale(2D);
		}
		c = c.offset(x, y, z);
		if (ChromaTiles.getTileFromIDandMetadata(c.getBlock(world), c.getBlockMetadata(world)) == ChromaTiles.FUNCTIONRELAY)
			c = ((TileEntityFunctionRelay)c.getTileEntity(world)).getRandomCoordinate();
		BiomeGenBase b = c.getBiome(world);
		BiomeGenBase nat = ReikaWorldHelper.getNaturalGennedBiomeAt(world, c.xCoord, c.zCoord);
		if (b != nat && nat != null) {
			ReikaWorldHelper.setBiomeForXZ(world, c.xCoord, c.zCoord, nat, true);
		}
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public boolean isPlantable(World world, int x, int y, int z) {
		return ReikaPlantHelper.FLOWER.canPlantAt(world, x, y, z) || ChromaTiles.getTile(world, x, y-1, z) == ChromaTiles.PLANTACCEL;
	}

	@Override
	public Collection<Coordinate> getPossibleRelativePositions() {
		return coordinateRand.getValues();
	}

	@Override
	public double getNormalizedWeight(Coordinate c) {
		return coordinateRand.getNormalizedWeight(c);
	}

}
