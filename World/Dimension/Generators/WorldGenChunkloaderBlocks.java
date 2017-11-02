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

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.ChromaDimensionBiome;
import Reika.ChromatiCraft.Base.ChromaWorldGenerator;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.DimensionGenerators;

public class WorldGenChunkloaderBlocks extends ChromaWorldGenerator {

	private final double[][] crossSection = {
			{0, 1, 3, 4, 3, 1, 0},
			{1, 2, 5, 6, 5, 2, 1},
			{3, 5, 7, 8, 7, 5, 3},
			{4, 6, 8, 10, 8, 6, 4},
			{3, 5, 7, 8, 7, 5, 3},
			{1, 2, 5, 6, 5, 2, 1},
			{0, 1, 3, 4, 3, 1, 0},
	};

	public WorldGenChunkloaderBlocks(DimensionGenerators g, Random rand, long seed) {
		super(g, rand, seed);
	}

	@Override
	public float getGenerationChance(World world, int cx, int cz, ChromaDimensionBiome biome) {
		return 0.1F;
	}

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {
		for (int i = -4; i <= 4; i += 4) {
			for (int k = -4; k <= 4; k += 4) {
				Block b = world.getBlock(x+i, y-1, z+k);
				if (b != Blocks.grass && b != Blocks.sand)
					return false;
				b = world.getBlock(x+i, y, z+k);
				if (!b.isAir(world, x+i, y, z+k))
					return false;
			}
		}
		double vx = rand.nextDouble()-0.5;
		double vz = rand.nextDouble()-0.5;
		for (int i = 0; i < crossSection.length; i++) {
			for (int k = 0; k < crossSection.length; k++) {
				boolean flag = rand.nextDouble()*10 < crossSection[i][k];
				if (flag) {
					for (int j = 0; j < 6; j++) {
						int dx = MathHelper.floor_double(x+vx*j+(i-crossSection.length/2)/(1+j/1.25D));
						int dz = MathHelper.floor_double(z+vz*j+(k-crossSection.length/2)/(1+j/1.25D));
						int dy = y+j-1;
						world.setBlock(dx, dy, dz, ChromaBlocks.CHUNKLOADER.getBlockInstance(), 0, 3);
						if (j == 0 || j == 1) {
							int ddy = j == 0 ? dy : dy-j;
							for (int a = -1; a <= 1; a++) {
								for (int b = -1; b <= 1; b++) {
									int ddx = dx+a;
									int ddz = dz+b;
									if (world.getBlock(ddx, ddy, ddz) != ChromaBlocks.CHUNKLOADER.getBlockInstance())
										world.setBlock(ddx, ddy, ddz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.CLOAK.ordinal(), 2);
								}
							}
						}
						if (j == 0 && rand.nextInt(3) == 0) {
							int d = rand.nextInt(3) == 0 ? 2 : 1;
							for (int h = 1; h <= d; h++)
								world.setBlock(dx, dy-h, dz, ChromaBlocks.CHUNKLOADER.getBlockInstance(), 0, 3);
						}
					}
				}
			}
		}


		return true;
	}

}
