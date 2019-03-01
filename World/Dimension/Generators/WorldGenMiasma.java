/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Generators;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.ChromaDimensionBiome;
import Reika.ChromatiCraft.Base.ChromaWorldGenerator;
import Reika.ChromatiCraft.Block.Dimension.BlockDimensionDeco.DimDecoTypes;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.BiomeDistributor;
import Reika.ChromatiCraft.World.Dimension.ChromaDimensionManager.SubBiomes;
import Reika.ChromatiCraft.World.Dimension.DimensionGenerators;
import Reika.ChromatiCraft.World.Dimension.Terrain.TerrainGenSkylandCanyons;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

public class WorldGenMiasma extends ChromaWorldGenerator {

	public WorldGenMiasma(DimensionGenerators g, Random rand, long seed) {
		super(g, rand, seed);
	}

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {

		int r = 12+rand.nextInt(24);
		double ecc = 1-rand.nextDouble()*0.5;
		int r2 = (int)(r*ecc);

		boolean isVoid = BiomeDistributor.getBiome(x, z).getExactType() == SubBiomes.VOIDLANDS;

		int ymin = 0;
		int ymax = 0;
		if (!isVoid) {
			for (int i = TerrainGenSkylandCanyons.LOW_FLOOR-2; i <= TerrainGenSkylandCanyons.HIGH_FLOOR+2; i++) {
				if (world.getBlock(x, i, z).isAir(world, x, i, z)) {
					ymin = i;
					break;
				}
			}
		}
		for (int i = TerrainGenSkylandCanyons.LOW_CEIL-2; i <= TerrainGenSkylandCanyons.HIGH_CEIL+2; i++) {
			if (!world.getBlock(x, i, z).isAir(world, x, i, z)) {
				ymax = i-1;
				break;
			}
		}
		y = ReikaRandomHelper.getRandomPlusMinus(ymin, ymax);

		for (int i = -r; i <= r; i++) {
			for (int k = -r; k <= r; k++) {
				for (int j = -r2; j <= r2; j++) {
					if (ReikaMathLibrary.isPointInsideEllipse(i, j, k, r, r2, r)) {
						double c = (1-Math.abs((double)i)/r)*(1-Math.abs((double)k)/r)*(1-Math.abs((double)j)/r2);
						c *= 0.25;
						if (c >= 1 || ReikaRandomHelper.doWithChance(c)) {
							int dx = x+i;
							int dz = z+k;
							int dy = y+j;
							if (world.getBlock(dx, dy, dz) == Blocks.air)
								world.setBlock(dx, dy, dz, ChromaBlocks.DIMGEN.getBlockInstance(), DimDecoTypes.MIASMA.ordinal(), 3);
						}
					}
				}
			}
		}

		return true;
	}

	@Override
	public float getGenerationChance(World world, int cx, int cz, ChromaDimensionBiome biome) {
		return 0.0025F*40;//0.02F;
	}

}
