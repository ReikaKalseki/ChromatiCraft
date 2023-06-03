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

import net.minecraft.block.Block;
import net.minecraft.block.BlockCactus;
import net.minecraft.block.BlockReed;
import net.minecraft.block.BlockSapling;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Auxiliary.Interfaces.ComplexAOE;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityMagicPlant;
import Reika.ChromatiCraft.Block.Worldgen.BlockCliffStone.Variants;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Instantiable.Data.WeightedRandom;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaCropHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModRegistry.ModCropList;


public class TileEntityCropSpeedPlant extends TileEntityMagicPlant implements ComplexAOE {

	private static double[][] growthDistrib = {
			{0, 0, 4, 0, 0},
			{0, 1, 8, 1, 0},
			{4, 8, 0, 8, 4},
			{0, 1, 8, 1, 0},
			{0, 0, 4, 0, 0},
	};

	private static double[][] hydrateDistrib = {
			{0, 1, 4, 1, 0},
			{1, 2, 6, 2, 1},
			{4, 6, 4, 6, 4},
			{1, 2, 6, 2, 1},
			{0, 1, 4, 1, 0},
	};

	private static final WeightedRandom<Coordinate> growthRand = WeightedRandom.fromArray(growthDistrib);
	private static final WeightedRandom<Coordinate> hydrateRand = WeightedRandom.fromArray(hydrateDistrib);

	@Override
	public ForgeDirection getGrowthDirection() {
		return ForgeDirection.UP;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.CROPSPEED;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (world.isRemote)
			return;
		if (rand.nextInt(4) == 0)
			this.hydrateFarmland(world, x, y, z);
		double n = 0.5+this.getAccelerationPlants()/2D;
		while (n >= 1) {
			this.growCrop(world, x, y, z);
			n -= 1;
		}
		if (ReikaRandomHelper.doWithChance(n))
			this.growCrop(world, x, y, z);
	}

	private void hydrateFarmland(World world, int x, int y, int z) {
		Coordinate c = hydrateRand.getRandomEntry().offset(x, y-1, z);
		Block b = c.getBlock(world);
		if (b == Blocks.farmland) {
			ReikaWorldHelper.hydrateFarmland(world, c.xCoord, c.yCoord, c.zCoord, false);
		}
	}

	private void growCrop(World world, int x, int y, int z) {
		Coordinate c = growthRand.getRandomEntry().offset(x, y, z);
		Block b = c.getBlock(world);
		int meta = c.getBlockMetadata(world);
		boolean flag = b instanceof BlockSapling || b == ChromaBlocks.DECOFLOWER.getBlockInstance() || b instanceof BlockReed || b instanceof BlockCactus || b == Blocks.vine;
		flag |= ReikaCropHelper.getCrop(b) != null;
		flag |= ModCropList.getModCrop(b, meta) != null;
		if (flag) {
			c.updateTick(world);
		}
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public boolean isPlantable(World world, int x, int y, int z) {
		return world.getBlock(x, y-1, z) == Blocks.farmland || ChromaTiles.getTile(world, x, y-1, z) == ChromaTiles.PLANTACCEL || (world.getBlock(x, y-1, z) == ChromaBlocks.CLIFFSTONE.getBlockInstance() && Variants.getVariant(world.getBlockMetadata(x, y-1, z)) == Variants.FARMLAND);
	}

	@Override
	public Collection<Coordinate> getPossibleRelativePositions() {
		return growthRand.getValues();
	}

	@Override
	public double getNormalizedWeight(Coordinate c) {
		return growthRand.getNormalizedWeight(c);
	}

}
