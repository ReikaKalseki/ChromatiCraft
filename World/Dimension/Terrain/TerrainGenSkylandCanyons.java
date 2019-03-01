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

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.ChromaDimensionBiomeTerrainShaper;
import Reika.ChromatiCraft.World.Dimension.BiomeDistributor;
import Reika.ChromatiCraft.World.Dimension.ChromaDimensionManager.Biomes;
import Reika.ChromatiCraft.World.Dimension.ChromaDimensionManager.SubBiomes;
import Reika.ChromatiCraft.World.Dimension.ChunkProviderChroma;
import Reika.DragonAPI.Instantiable.Math.SimplexNoiseGenerator;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;


public class TerrainGenSkylandCanyons extends ChromaDimensionBiomeTerrainShaper {

	private final SimplexNoiseGenerator canyonDepth;
	private final SimplexNoiseGenerator canyonShape;

	private final SimplexNoiseGenerator bottomEdge;
	private final SimplexNoiseGenerator bottomLowerEdge;

	private final SimplexNoiseGenerator islandIntensity;

	private static final double MIN_DEPTH = 12;
	private static final double MAX_DEPTH = 64+ChunkProviderChroma.VERTICAL_OFFSET-10;

	public static final int LOW_FLOOR = 10;
	public static final int HIGH_FLOOR = 30;

	public static final int LOW_CEIL = 35;
	public static final int HIGH_CEIL = 50;

	public static final int BIOME_SEARCH = 24;

	public static final int VERTICAL_BUFFER = 18;

	public TerrainGenSkylandCanyons(long seed) {
		super(seed, Biomes.SKYLANDS, SubBiomes.VOIDLANDS);

		canyonDepth = new SimplexNoiseGenerator(seed);
		canyonShape = new SimplexNoiseGenerator(~seed);

		bottomEdge = new SimplexNoiseGenerator(-seed);
		bottomLowerEdge = new SimplexNoiseGenerator(~(-seed));

		islandIntensity = new SimplexNoiseGenerator(seed ^ -seed);
	}

	@Override
	public void generateColumn(World world, int chunkX, int chunkZ, int i, int k, int surface, Random rand, double edgeFactor) {
		double innerScale = 1/16D;
		double mainScale = 2/3D;
		int dx = chunkX+i;
		int dz = chunkZ+k;
		boolean isVoid = BiomeDistributor.getBiome(dx, dz).getExactType() == SubBiomes.VOIDLANDS;
		int dt = 1+rand.nextInt(4);
		double rx = this.calcR(chunkX, i, innerScale, mainScale);
		double rz = this.calcR(chunkZ, k, innerScale, mainScale);
		double raw = Math.pow(1-Math.abs(canyonShape.getValue(rx, rz)), 5D);
		double df = ReikaMathLibrary.normalizeToBounds(canyonDepth.getValue(rx, rz), MIN_DEPTH, MAX_DEPTH);
		double depth = MathHelper.clamp_double(raw*df, 0, MAX_DEPTH);
		depth *= edgeFactor;
		int h = (int)depth;
		for (int j = 0; j <= h+VERTICAL_BUFFER; j++) {
			int dy = 64+ChunkProviderChroma.VERTICAL_OFFSET+VERTICAL_BUFFER-j;
			this.cutBlock(world, dx, dy, dz);
		}
		world.setBlock(dx, 64+ChunkProviderChroma.VERTICAL_OFFSET-h-1, dz, Blocks.grass, 0, 2);
		for (int di = 0; di < dt; di++) {
			world.setBlock(dx, 64+ChunkProviderChroma.VERTICAL_OFFSET-h-2-di, dz, Blocks.dirt, 0, 2);
		}
		double floor = ReikaMathLibrary.normalizeToBounds(bottomLowerEdge.getValue(rx, rz), LOW_FLOOR, HIGH_FLOOR);
		double h2 = ReikaMathLibrary.normalizeToBounds(bottomEdge.getValue(rx, rz), LOW_CEIL, HIGH_CEIL);
		if (edgeFactor < 1) {
			double avg = (floor+h2)/2D;
			h2 = avg+(h2-avg)*edgeFactor;
			floor = avg-(avg-floor)*edgeFactor;
		}
		int h1 = isVoid ? 0 : (int)floor;
		for (int dy = h1; dy <= h2; dy++) {
			this.cutBlock(world, dx, dy, dz);
		}
		if (h1 > 0) {
			world.setBlock(dx, h1-1, dz, Blocks.grass, 0, 2);
			for (int di = 0; di < dt; di++) {
				world.setBlock(dx, h1-2-di, dz, Blocks.dirt, 0, 2);
			}
		}

		if (isVoid) {
			double densityControl = ReikaMathLibrary.normalizeToBounds(islandIntensity.getValue(rx, rz), -20, -3); //more negative for less
			double islandHeight = Math.max(0, ReikaMathLibrary.normalizeToBounds(bottomLowerEdge.getValue(rx, rz), densityControl, HIGH_FLOOR/2D));
			if (islandHeight > 0) {
				int i1 = (int)(HIGH_FLOOR/2D-islandHeight);
				int i2 = (int)(HIGH_FLOOR/2D+islandHeight);
				for (int dy = i1; dy <= i2; dy++) {
					Block b = Blocks.stone;
					if (dy == i2) {
						b = Blocks.grass;
					}
					else if (i2-dy < dt) {
						b = Blocks.dirt;
					}
					world.setBlock(dx, dy, dz, b, 0, 2);
				}
			}
		}
	}

	@Override
	public double getBiomeSearchDistance() {
		return BIOME_SEARCH;
	}


}
