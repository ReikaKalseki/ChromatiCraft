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
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.DimensionGenerators;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

public class WorldGenMoonPool extends ChromaWorldGenerator {

	public WorldGenMoonPool(DimensionGenerators g, Random rand, long seed) {
		super(g, rand, seed);
	}

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {
		int dy = ReikaWorldHelper.findTopBlockBelowY(world, x, 255, z);
		if (world.getBlock(x, dy, z) == Blocks.water && ReikaWorldHelper.getWaterDepth(world, x, dy, z) >= 5) {
			if (ReikaWorldHelper.getWaterDepth(world, x+6, dy, z) >= 3 && ReikaWorldHelper.getWaterDepth(world, x-6, dy, z) >= 3) {
				if (ReikaWorldHelper.getWaterDepth(world, x, dy, z+6) >= 3 && ReikaWorldHelper.getWaterDepth(world, x, dy, z-6) >= 3) {

					this.generateAt(world, x, dy, z, rand);
					/*
					float e = 0.3F+rand.nextFloat()*0.7F;

					for (int p = -1; p < 6; p++) {
						int h = dy+p;
						int ra = p <= 0 ? 10 : p == 1 ? 8 : p < 3 ? 6 : 6-(p-3);
						ra *= 1+rand.nextFloat();
						int rb = Math.max(2, (int)(ra*e));
						int r2 = (rb-2)*(rb-2);
						for (int i = -ra; i <= ra; i++) {
							for (int k = -rb; k <= rb; k++) {
								int dx = x+i;
								int dz = z+k;
								if (p < 5 && i*i+k*k <= r2) {
									world.setBlock(dx, h, dz, Blocks.air);
								}
								else if (i*i+k*k <= (ra+0.5)*(rb+0.5)) {
									world.setBlock(dx, h, dz, Blocks.grass);
								}
							}
						}
					}
					 */

					return true;
				}
			}
		}
		return false;
	}

	private void generateAt(World world, int x, int y, int z, Random rand) {

		float e = rand.nextFloat()*0.25F;
		int r = 8+rand.nextInt(8);
		int ra = (int)(r*(1-e));

		double ry = ReikaRandomHelper.getRandomPlusMinus(ra*0.675, ra*0.125);

		double in = ReikaRandomHelper.getRandomPlusMinus(0.4375, 0.0625);
		double ir = in*r;
		double ira = in*ra;
		double iry = in*ry;

		for (int i = -r; i <= r; i++) {
			int dx = x+i;
			for (int k = -r; k <= r; k++) {
				int dz = z+k;
				for (int h = 0; h <= r; h++) {
					int dy = y+h;

					if (ReikaMathLibrary.isPointInsideEllipse(i, h, k, r, ry, ra)) {
						BlockKey b = ReikaMathLibrary.isPointInsideEllipse(i, h, k, r-1, ry-1, ra-1) ? new BlockKey(Blocks.air) : new BlockKey(ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
						world.setBlock(dx, dy, dz, b.blockID, b.metadata, 3);
					}
				}

				for (int h = 0; h <= 1; h++) {
					int dy = y+h-1;
					if (ReikaMathLibrary.isPointInsideEllipse(i, 0, k, ir, 0, ira)) {
						if (ReikaMathLibrary.isPointInsideEllipse(i, 0, k, ir-1, 0, ira-1)) {
							if (ReikaMathLibrary.isPointInsideEllipse(i, h, k, ir-1, iry*1.5, ira-1)) {
								if (rand.nextDouble()*rand.nextDouble() >= (i/(ir-1))*(h/iry)*(k/(ira-1)))
									world.setBlock(dx, dy+1, dz, ChromaBlocks.DIMGEN.getBlockInstance(), DimDecoTypes.AQUA.ordinal(), 3);
							}
						}
						else if (h <= 1) {
							world.setBlock(dx, dy, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata, 3);
						}
					}
				}

				if (ReikaMathLibrary.isPointInsideEllipse(i, 0, k, ir, 0, ira)) {
					world.setBlock(dx, y-1, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata, 3);
				}
			}
		}
	}

	@Override
	public float getGenerationChance(World world, int cx, int cz, ChromaDimensionBiome biome) {
		return 0.004F;
	}

}
