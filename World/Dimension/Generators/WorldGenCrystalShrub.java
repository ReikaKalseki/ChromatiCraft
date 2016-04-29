/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Generators;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Base.ChromaDimensionBiome;
import Reika.ChromatiCraft.Base.ChromaWorldGenerator;
import Reika.ChromatiCraft.Block.Dimension.BlockDimensionDeco.DimDecoTypes;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.DimensionGenerators;


public class WorldGenCrystalShrub extends ChromaWorldGenerator {

	public WorldGenCrystalShrub(DimensionGenerators g, Random rand, long seed) {
		super(g, rand, seed);
	}

	@Override
	public float getGenerationChance(World world, int cx, int cz, ChromaDimensionBiome biome) {
		return 1;
	}

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {
		if (world.getBlock(x, y-1, z) != Blocks.grass)
			return false;
		int size = 0;
		if (rand.nextInt(40) == 0)
			size = 2;
		else if (rand.nextInt(15) == 0)
			size = 1;
		if (size == 2) {
			for (int j = 0; j < 2; j++) {
				int dy = y+j;
				for (int i = -2; i <= 2; i++) {
					for (int k = -2; k <= 2; k++) {
						if (Math.abs(i) < 2 || Math.abs(k) < 2) {
							int dx = x+i;
							int dz = z+k;
							if (world.getBlock(dx, dy, dz).isAir(world, dx, dy, dz)) {
								world.setBlock(dx, dy, dz, ChromaBlocks.DIMGEN.getBlockInstance(), DimDecoTypes.CRYSTALLEAF.ordinal(), 3);
							}
						}
					}
				}
			}
			int[] r = {2, 2, 1};
			for (int j = 0; j < r.length; j++) {
				int dy = y+j+2;
				for (int i = -2; i <= 2; i++) {
					for (int k = -2; k <= 2; k++) {
						if (Math.abs(i)+Math.abs(k) <= r[j]+1) {
							int dx = x+i;
							int dz = z+k;
							if (world.getBlock(dx, dy, dz).isAir(world, dx, dy, dz)) {
								world.setBlock(dx, dy, dz, ChromaBlocks.DIMGEN.getBlockInstance(), DimDecoTypes.CRYSTALLEAF.ordinal(), 3);
							}
						}
					}
				}
			}
			world.setBlock(x, y, z, WorldGenCrystalTree.CRYSTAL_TRUNK.blockID, WorldGenCrystalTree.CRYSTAL_TRUNK.metadata, 3);
		}
		else if (size == 1) {
			int[] r = {2, 2, 1};
			for (int j = 0; j < r.length; j++) {
				int dy = y+j;
				for (int i = -2; i <= 2; i++) {
					for (int k = -2; k <= 2; k++) {
						if (Math.abs(i)+Math.abs(k) <= r[j]+1) {
							int dx = x+i;
							int dz = z+k;
							if (world.getBlock(dx, dy, dz).isAir(world, dx, dy, dz)) {
								world.setBlock(dx, dy, dz, ChromaBlocks.DIMGEN.getBlockInstance(), DimDecoTypes.CRYSTALLEAF.ordinal(), 3);
							}
						}
					}
				}
			}
			world.setBlock(x, y, z, WorldGenCrystalTree.CRYSTAL_TRUNK.blockID, WorldGenCrystalTree.CRYSTAL_TRUNK.metadata, 3);
		}
		else {
			world.setBlock(x, y, z, WorldGenCrystalTree.CRYSTAL_TRUNK.blockID, WorldGenCrystalTree.CRYSTAL_TRUNK.metadata, 3);
			for (int i = 1; i < 6; i++) {
				ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
				int dx = x+dir.offsetX;
				int dy = y+dir.offsetY;
				int dz = z+dir.offsetZ;
				if (world.getBlock(dx, dy, dz).isAir(world, dx, dy, dz)) {
					world.setBlock(dx, dy, dz, ChromaBlocks.DIMGEN.getBlockInstance(), DimDecoTypes.CRYSTALLEAF.ordinal(), 3);
				}
			}
		}
		return true;
	}

}
