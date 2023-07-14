/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenLiquids;
import net.minecraft.world.gen.feature.WorldGenerator;

import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.IWG.GlowingCliffsAuxGenerator.Island;
import Reika.DragonAPI.Instantiable.Worldgen.ControllableOreVein;


public class GlowingCliffsDecorator extends BiomeDecorator {

	private int flowsPerChunk = 2;
	private static final Random islandRandom = new Random();

	private World islandWorld;// = new GenerationInterceptWorld();
	private World decoWorld;// = new GenerationInterceptWorld();

	GlowingCliffsDecorator() {
		super();

		sandPerChunk = 2;
		sandPerChunk2 = 6;

		clayPerChunk = 1;
		grassPerChunk = 3;
		flowersPerChunk = 8;
		treesPerChunk = 4;
		waterlilyPerChunk = 2;
		reedsPerChunk = 10;
		mushroomsPerChunk = 0;

		gravelGen = new NoFloatingGravelVein(/*((WorldGenMinable)gravelGen).numberOfBlocks*/32);
	}

	private void setWorld(World world) {
		currentWorld = world;
		decoWorld = currentWorld;//.link(currentWorld);
	}

	@Override
	public void decorateChunk(World world, Random rand, BiomeGenBase biome, int chunkX, int chunkZ) {
		if (currentWorld != null) {
			//throw new RuntimeException("Already decorating!!"); NO

			World lastWorld = currentWorld;
			Random lastRand = randomGenerator;
			int lastX = chunk_X;
			int lastZ = chunk_Z;

			this.setWorld(world);
			randomGenerator = rand;
			chunk_X = chunkX;
			chunk_Z = chunkZ;

			this.genDecorations(biome);

			this.setWorld(lastWorld);
			randomGenerator = lastRand;
			chunk_X = lastX;
			chunk_Z = lastZ;
		}
		else {
			this.setWorld(world);
			randomGenerator = rand;
			chunk_X = chunkX;
			chunk_Z = chunkZ;
			this.genDecorations(biome);
			this.setWorld(null);
			randomGenerator = null;
		}
	}

	@Override
	protected void genDecorations(BiomeGenBase biome) {
		this.generateOres();
		int i;
		int j;
		int k;

		for (i = 0; i < sandPerChunk2; i++) {
			j = chunk_X + randomGenerator.nextInt(16) + 8;
			k = chunk_Z + randomGenerator.nextInt(16) + 8;
			sandGen.generate(decoWorld, randomGenerator, j, decoWorld.getTopSolidOrLiquidBlock(j, k), k);
		}

		for (i = 0; i < clayPerChunk; i++) {
			j = chunk_X + randomGenerator.nextInt(16) + 8;
			k = chunk_Z + randomGenerator.nextInt(16) + 8;
			clayGen.generate(decoWorld, randomGenerator, j, decoWorld.getTopSolidOrLiquidBlock(j, k), k);
		}

		for (i = 0; i < sandPerChunk; i++) {
			j = chunk_X + randomGenerator.nextInt(16) + 8;
			k = chunk_Z + randomGenerator.nextInt(16) + 8;
			gravelGen.generate(decoWorld, randomGenerator, j, decoWorld.getTopSolidOrLiquidBlock(j, k), k);
		}

		i = treesPerChunk;

		if (randomGenerator.nextInt(10) == 0) {
			i++;
		}

		int l;
		int i1;

		for (j = 0; j < i; j++) {
			k = chunk_X + randomGenerator.nextInt(16) + 8;
			l = chunk_Z + randomGenerator.nextInt(16) + 8;
			i1 = decoWorld.getHeightValue(k, l);
			WorldGenAbstractTree tree = biome.func_150567_a(randomGenerator);
			tree.setScale(1.0D, 1.0D, 1.0D);

			if (tree.generate(decoWorld, randomGenerator, k, i1, l)) {
				tree.func_150524_b(decoWorld, randomGenerator, k, i1, l);
			}
		}

		for (j = 0; j < flowersPerChunk; j++) {
			k = chunk_X + randomGenerator.nextInt(16) + 8;
			l = chunk_Z + randomGenerator.nextInt(16) + 8;
			i1 = this.nextInt(decoWorld.getHeightValue(k, l) + 32);
			String s = biome.func_150572_a(randomGenerator, k, i1, l);
			BlockFlower blockflower = BlockFlower.func_149857_e(s);

			if (blockflower.getMaterial() != Material.air) {
				yellowFlowerGen.func_150550_a(blockflower, BlockFlower.func_149856_f(s));
				yellowFlowerGen.generate(decoWorld, randomGenerator, k, i1, l);
			}
		}

		for (j = 0; j < grassPerChunk; j++) {
			k = chunk_X + randomGenerator.nextInt(16) + 8;
			l = chunk_Z + randomGenerator.nextInt(16) + 8;
			i1 = this.nextInt(decoWorld.getHeightValue(k, l) * 2);
			WorldGenerator worldgenerator = biome.getRandomWorldGenForGrass(randomGenerator);
			worldgenerator.generate(decoWorld, randomGenerator, k, i1, l);
		}

		for (j = 0; j < waterlilyPerChunk; j++) {
			k = chunk_X + randomGenerator.nextInt(16) + 8;
			l = chunk_Z + randomGenerator.nextInt(16) + 8;

			for (i1 = this.nextInt(decoWorld.getHeightValue(k, l) * 2); i1 > 0 && decoWorld.isAirBlock(k, i1 - 1, l); --i1) {
				;
			}

			waterlilyGen.generate(decoWorld, randomGenerator, k, i1, l);
		}

		for (j = 0; j < mushroomsPerChunk; j++) {
			if (randomGenerator.nextInt(4) == 0) {
				k = chunk_X + randomGenerator.nextInt(16) + 8;
				l = chunk_Z + randomGenerator.nextInt(16) + 8;
				i1 = decoWorld.getHeightValue(k, l);
				mushroomBrownGen.generate(decoWorld, randomGenerator, k, i1, l);
			}

			if (randomGenerator.nextInt(8) == 0) {
				k = chunk_X + randomGenerator.nextInt(16) + 8;
				l = chunk_Z + randomGenerator.nextInt(16) + 8;
				i1 = this.nextInt(decoWorld.getHeightValue(k, l) * 2);
				mushroomRedGen.generate(decoWorld, randomGenerator, k, i1, l);
			}
		}

		if (randomGenerator.nextInt(4) == 0) {
			j = chunk_X + randomGenerator.nextInt(16) + 8;
			k = chunk_Z + randomGenerator.nextInt(16) + 8;
			l = this.nextInt(decoWorld.getHeightValue(j, k) * 2);
			mushroomBrownGen.generate(decoWorld, randomGenerator, j, l, k);
		}

		if (randomGenerator.nextInt(8) == 0) {
			j = chunk_X + randomGenerator.nextInt(16) + 8;
			k = chunk_Z + randomGenerator.nextInt(16) + 8;
			l = this.nextInt(decoWorld.getHeightValue(j, k) * 2);
			mushroomRedGen.generate(decoWorld, randomGenerator, j, l, k);
		}

		for (j = 0; j < reedsPerChunk; j++) {
			k = chunk_X + randomGenerator.nextInt(16) + 8;
			l = chunk_Z + randomGenerator.nextInt(16) + 8;
			i1 = this.nextInt(decoWorld.getHeightValue(k, l) * 2);
			reedGen.generate(decoWorld, randomGenerator, k, i1, l);
		}

		for (j = 0; j < 10; j++) {
			k = chunk_X + randomGenerator.nextInt(16) + 8;
			l = chunk_Z + randomGenerator.nextInt(16) + 8;
			i1 = this.nextInt(decoWorld.getHeightValue(k, l) * 2);
			reedGen.generate(decoWorld, randomGenerator, k, i1, l);
		}
		/*
		doGen = TerrainGen.decorate(decoWorld, randomGenerator, chunk_X, chunk_Z, PUMPKIN);
		if (randomGenerator.nextInt(32) == 0) {
			j = chunk_X + randomGenerator.nextInt(16) + 8;
			k = chunk_Z + randomGenerator.nextInt(16) + 8;
			l = this.nextInt(decoWorld.getHeightValue(j, k) * 2);
			(new WorldGenPumpkin()).generate(decoWorld, randomGenerator, j, l, k);
		}
		 */

		for (int a = 0; a < flowsPerChunk; a++) {
			for (j = 0; j < 50; j++) {
				k = chunk_X + randomGenerator.nextInt(16) + 8;
				l = randomGenerator.nextInt(randomGenerator.nextInt(248) + 8);
				i1 = chunk_Z + randomGenerator.nextInt(16) + 8;
				(new WorldGenLiquids(Blocks.flowing_water)).generate(decoWorld, randomGenerator, k, l, i1);
			}

			for (j = 0; j < 20; j++) {
				k = chunk_X + randomGenerator.nextInt(16) + 8;
				l = randomGenerator.nextInt(randomGenerator.nextInt(randomGenerator.nextInt(240) + 8) + 8);
				i1 = chunk_Z + randomGenerator.nextInt(16) + 8;
				(new WorldGenLiquids(Blocks.flowing_lava)).generate(decoWorld, randomGenerator, k, l, i1);
			}
		}
	}

	public void genIslandDecorations(World world, BiomeGenBase biome, Island is) {

		islandWorld = world;//.link(world);
		islandRandom.setSeed(world.getSeed());

		int i;
		int j;
		int k;

		for (i = 0; i < sandPerChunk2; i++) {
			j = is.getRandomX(islandRandom);
			k = is.getRandomZ(islandRandom);
			sandGen.generate(islandWorld, islandRandom, j, is.getTopY(islandWorld, j, k), k);
		}

		for (i = 0; i < clayPerChunk; i++) {
			j = is.getRandomX(islandRandom);
			k = is.getRandomZ(islandRandom);
			clayGen.generate(islandWorld, islandRandom, j, is.getTopY(islandWorld, j, k), k);
		}

		for (i = 0; i < sandPerChunk; i++) {
			j = is.getRandomX(islandRandom);
			k = is.getRandomZ(islandRandom);
			gravelAsSandGen.generate(islandWorld, islandRandom, j, is.getTopY(islandWorld, j, k), k);
		}

		i = treesPerChunk;

		if (islandRandom.nextInt(10) == 0) {
			i++;
		}

		int l;
		int i1;

		for (j = 0; j < i; j++) {
			k = is.getRandomX(islandRandom);
			l = is.getRandomZ(islandRandom);
			i1 = is.getTopY(islandWorld, j, k);
			WorldGenAbstractTree tree = biome.func_150567_a(islandRandom);
			tree.setScale(1.0D, 1.0D, 1.0D);

			if (tree.generate(islandWorld, islandRandom, k, i1, l)) {
				tree.func_150524_b(islandWorld, islandRandom, k, i1, l);
			}
		}

		for (j = 0; j < flowersPerChunk; j++) {
			k = is.getRandomX(islandRandom);
			l = is.getRandomZ(islandRandom);
			i1 = is.getTopY(islandWorld, j, k);
			String s = biome.func_150572_a(islandRandom, k, i1, l);
			BlockFlower blockflower = BlockFlower.func_149857_e(s);

			if (blockflower.getMaterial() != Material.air) {
				yellowFlowerGen.func_150550_a(blockflower, BlockFlower.func_149856_f(s));
				yellowFlowerGen.generate(islandWorld, islandRandom, k, i1, l);
			}
		}

		for (j = 0; j < grassPerChunk; j++) {
			k = is.getRandomX(islandRandom);
			l = is.getRandomZ(islandRandom);
			i1 = is.getTopY(islandWorld, j, k);
			WorldGenerator worldgenerator = biome.getRandomWorldGenForGrass(islandRandom);
			worldgenerator.generate(islandWorld, islandRandom, k, i1, l);
		}

		for (j = 0; j < reedsPerChunk; j++) {
			k = is.getRandomX(islandRandom);
			l = is.getRandomZ(islandRandom);
			i1 = is.getTopY(islandWorld, j, k);
			reedGen.generate(islandWorld, islandRandom, k, i1, l);
		}

		for (j = 0; j < 10; j++) {
			k = is.getRandomX(islandRandom);
			l = is.getRandomZ(islandRandom);
			i1 = is.getTopY(islandWorld, j, k);
			reedGen.generate(islandWorld, islandRandom, k, i1, l);
		}
		/*
		doGen = TerrainGen.decorate(islandWorld, islandRandom, chunk_X, chunk_Z, PUMPKIN);
		if (islandRandom.nextInt(32) == 0) {
			j = getRandomX(is);
			k = getRandomZ(is);
			l = this.nextInt(islandWorld.getHeightValue(j, k) * 2);
			(new WorldGenPumpkin()).generate(islandWorld, islandRandom, j, l, k);
		}
		 */

		for (int a = 0; a < flowsPerChunk; a++) {
			for (j = 0; j < 50; j++) {
				k = is.getRandomX(islandRandom);
				l = is.getTopY(islandWorld, j, k);
				i1 = is.getRandomZ(islandRandom);
				(new WorldGenLiquids(Blocks.flowing_water)).generate(islandWorld, islandRandom, k, l, i1);
			}

			for (j = 0; j < 20; j++) {
				k = is.getRandomX(islandRandom);
				l = is.getTopY(islandWorld, j, k);
				i1 = is.getRandomZ(islandRandom);
				(new WorldGenLiquids(Blocks.flowing_lava)).generate(islandWorld, islandRandom, k, l, i1);
			}
		}

		//islandWorld.link(null);
	}

	private int nextInt(int i) {
		if (i <= 1)
			return 0;
		return randomGenerator.nextInt(i);
	}

	private static class NoFloatingGravelVein extends ControllableOreVein {

		public NoFloatingGravelVein(int size) {
			super(Blocks.gravel, size);
		}

		@Override
		public boolean canPlaceBlockHere(World world, int x, int y, int z) {
			Block b = world.getBlock(x, y, z);
			if (b == ChromaBlocks.CLIFFSTONE.getBlockInstance())
				return false;
			return world.getBlock(x, y-1, z).getMaterial().isSolid();
		}

	}

}
