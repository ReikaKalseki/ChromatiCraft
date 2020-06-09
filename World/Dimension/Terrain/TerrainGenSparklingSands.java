/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Terrain;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.ChromaDimensionBiomeTerrainShaper;
import Reika.ChromatiCraft.Block.Worldgen.BlockSparkle.BlockTypes;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.BiomeDistributor;
import Reika.ChromatiCraft.World.Dimension.ChromaDimensionManager.Biomes;
import Reika.ChromatiCraft.World.Dimension.ChromaDimensionManager.ChromaDimensionBiomeType;
import Reika.ChromatiCraft.World.Dimension.ChunkProviderChroma;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Math.Noise.NoiseGeneratorBase;
import Reika.DragonAPI.Instantiable.Math.Noise.SimplexNoiseGenerator;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

public class TerrainGenSparklingSands extends ChromaDimensionBiomeTerrainShaper {

	private final NoiseGeneratorBase beachTypeNoise;
	private final NoiseGeneratorBase heightNoise;
	private final NoiseGeneratorBase duneNoise;
	private final NoiseGeneratorBase duneHeightNoise;
	private final NoiseGeneratorBase beachTypeOffsetNoise;
	private final NoiseGeneratorBase sparkleChoiceNoise;

	private static final int SHORE_Y = 63+ChunkProviderChroma.VERTICAL_OFFSET; // less than this == water
	private static final int LAYER_THICKNESS = 3;

	private static final ArrayList<BlockKey>[] HEIGHT_LEVELS = new ArrayList[]{
			ReikaJavaLibrary.makeListFrom(new BlockKey(ChromaBlocks.SPARKLE.getBlockInstance(), BlockTypes.CLAY.ordinal())),
			ReikaJavaLibrary.makeListFrom(new BlockKey(ChromaBlocks.SPARKLE.getBlockInstance(), BlockTypes.SAND.ordinal()), new BlockKey(ChromaBlocks.SPARKLE.getBlockInstance(), BlockTypes.GRAVEL.ordinal())),
			ReikaJavaLibrary.makeListFrom(new BlockKey(ChromaBlocks.SPARKLE.getBlockInstance(), BlockTypes.DIRT.ordinal()), new BlockKey(ChromaBlocks.SPARKLE.getBlockInstance(), BlockTypes.STONE.ordinal())),
	};

	private static final int UNDERWATER_LAYERS = 1;

	private static final int MIN_Y = SHORE_Y-1-LAYER_THICKNESS*UNDERWATER_LAYERS-1;
	private static final int MAX_Y = MIN_Y+LAYER_THICKNESS*HEIGHT_LEVELS.length;

	private static final int MIN_DUNE_HEIGHT = 3;
	private static final int MAX_DUNE_HEIGHT = 12;

	public TerrainGenSparklingSands(long seed) {
		super(seed, Biomes.SPARKLE);

		heightNoise = new SimplexNoiseGenerator(seed).addOctave(2, 0.5);
		beachTypeNoise = new SimplexNoiseGenerator(-seed).setFrequency(4);
		sparkleChoiceNoise = new SimplexNoiseGenerator(~seed).setFrequency(2);
		beachTypeOffsetNoise = new SimplexNoiseGenerator(2*~seed);

		duneNoise = new SimplexNoiseGenerator(-2*seed).setFrequency(5);
		duneHeightNoise = new SimplexNoiseGenerator(2*seed);

	}

	@Override
	public void generateColumn(World world, int chunkX, int chunkZ, int i, int k, int surface, Random rand, double edgeFactor) {
		double innerScale = 1/16D;
		double mainScale = 	0.25D;
		double floorScale = 0.5D/mainScale;
		int dx = chunkX+i;
		int dz = chunkZ+k;
		ChromaDimensionBiomeType biome = BiomeDistributor.getBiome(dx, dz).getExactType();
		double rx = this.calcR(chunkX, i, innerScale, mainScale);
		double rz = this.calcR(chunkZ, k, innerScale, mainScale);
		int h = (int)(edgeFactor*ReikaMathLibrary.normalizeToBounds(heightNoise.getValue(rx, rz), MIN_Y, MAX_Y)+(1-edgeFactor)*(64+ChunkProviderChroma.VERTICAL_OFFSET));
		double dn = duneNoise.getValue(rx, rz);
		ArrayList<BlockKey> li = HEIGHT_LEVELS[(h-MIN_Y)/LAYER_THICKNESS];
		boolean dune = dn > 0;
		if (dune) {
			h += dn*ReikaMathLibrary.normalizeToBounds(duneHeightNoise.getValue(rx, rz), MIN_DUNE_HEIGHT, MAX_DUNE_HEIGHT);
		}
		this.generateLandColumn(world, dx, dz, h, rx, rz, dune, li);
	}

	private void generateLandColumn(World world, int x, int z, int h, double rx, double rz, boolean dune, ArrayList<BlockKey> topBlocks) {
		for (int y = 5; y < h; y++) {
			world.setBlock(x, y, z, Blocks.stone);
		}
		BlockKey b = null;
		if (dune) {
			double val = beachTypeNoise.getValue(rx, rz);
			b = new BlockKey(ChromaBlocks.SPARKLE.getBlockInstance(), val < 0 ? BlockTypes.SAND.ordinal() : BlockTypes.STONE.ordinal());
		}
		else {
			if (topBlocks.size() == 1) {
				b = topBlocks.get(0);
			}
			else {
				double val = beachTypeNoise.getValue(rx+h*2370, rz+h*1203);
				val = ReikaMathLibrary.normalizeToBounds(val, 0, topBlocks.size()-0.001);
				val = MathHelper.clamp_double(val+beachTypeOffsetNoise.getValue(rx, rz), 0, topBlocks.size()-0.001);
				b = topBlocks.get((int)val);
			}
		}
		if (Math.abs(sparkleChoiceNoise.getValue(rx+h*37, rz+h*28)) < 0.5) {
			b = new BlockKey(BlockTypes.values()[b.metadata].getBlockProxy(), 0);
		}
		if (b.blockID == Blocks.stone)
			b = new BlockKey(Blocks.grass, 0);
		world.setBlock(x, h, z, b.blockID, b.metadata, 2);
		int ay = Math.max(SHORE_Y, h+1);
		for (int y = h+1; y < SHORE_Y; y++) {
			world.setBlock(x, y, z, ChromaBlocks.LUMA.getBlockInstance());
		}
		for (int y = ay; y < 256; y++) {
			world.setBlock(x, y, z, Blocks.air);
		}
	}

	@Override
	public double getBiomeSearchDistance() {
		return 8;
	}

}
