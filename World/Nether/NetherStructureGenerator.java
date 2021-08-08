/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Nether;

import java.util.HashSet;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Block.Worldgen.BlockNetherBypassGate.GateLevels;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Interfaces.RetroactiveGenerator;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;


public class NetherStructureGenerator implements RetroactiveGenerator {

	public static final NetherStructureGenerator instance = new NetherStructureGenerator();

	private LavaRiverGenerator lavaRiverGen;

	private NetherStructureGenerator() {

	}

	@Override
	public void generate(Random rand, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if (lavaRiverGen == null || lavaRiverGen.seed != world.getSeed())
			lavaRiverGen = new LavaRiverGenerator(world.getSeed());

		if (world.provider.dimensionId != -1)
			return;
		int n = 8;
		if (rand.nextInt(1) == 0 && Math.abs(chunkX%/*(n-(world.getSeed()%n))*/4) == 0 && Math.abs(chunkZ%/*(n-(world.getSeed()%n))*/4) == /*n/2*/2) {
			int x = chunkX*16+rand.nextInt(16);
			int z = chunkZ*16+rand.nextInt(16);
			this.tryGenerateBypass(world, x, z, rand);
		}

		this.runGroundDecorators(world, chunkX, chunkZ, rand);
		this.runAirDecorators(world, chunkX, chunkZ, rand, chunkGenerator, chunkProvider);
	}

	private void runGroundDecorators(World world, int chunkX, int chunkZ, Random rand) {
		if (rand.nextDouble() < NetherStructures.BASE_GEN_FACTOR) {
			NetherStructures s = NetherStructures.getRandomStructure();
			int x = chunkX*16+rand.nextInt(16);
			int z = chunkZ*16+rand.nextInt(16);
			s.generate(world, x, z);
		}
	}

	private void runAirDecorators(World world, int chunkX, int chunkZ, Random rand, IChunkProvider generator, IChunkProvider loader) {
		this.runGlowstoneGen(world, chunkX, chunkZ, rand);

		lavaRiverGen.generate(world, chunkX, chunkZ);
	}

	private void runGlowstoneGen(World world, int chunkX, int chunkZ, Random rand) {
		if (rand.nextInt(2) == 0) {
			int x = chunkX*16+rand.nextInt(16);
			int z = chunkZ*16+rand.nextInt(16);
			int y = 145+rand.nextInt(100);
			int n = 12+rand.nextInt(36);
			new WorldGenMinable(Blocks.glowstone, n, Blocks.air).generate(world, rand, x, y, z);
		}
	}

	private boolean tryGenerateBypass(World world, int x, int z, Random rand) {
		int c = 3;
		for (int i = -c; i <= c; i++) {
			for (int k = -c; k <= c; k++) {
				for (int y = 100; y >= 50; y--) {
					if (!world.getBlock(x+i, y, z+k).isAir(world, x+i, y, z+k))
						return false;
				}
			}
		}
		return this.generateBedrockBypass(world, x, z, rand);
	}

	private boolean generateBedrockBypass(World world, int x, int z, Random rand) {
		int r = 3+rand.nextInt(4);
		HashSet<Coordinate> edge = new HashSet();
		for (int i = -r; i <= r; i++) {
			for (int k = -r; k <= r; k++) {
				int dx = x+i;
				int dz = z+k;
				if (ReikaMathLibrary.py3d(i, 0, k) <= r+0.5) {
					int dy = 127;
					while (!this.isCountedAsAir(world, dx, dy, dz)) {
						int dd = 127-dy;
						if (ReikaMathLibrary.py3d(i, 0, k) < r-0.5) {
							Block air = Blocks.air;
							int m = 0;
							if (dd <= 16) {
								air = ChromaBlocks.NETHERGATE.getBlockInstance();
								if (dd == 0) {
									m = GateLevels.KILL.ordinal();
								}
								else if (dd <= 2) {
									m = GateLevels.HURT2.ordinal();
								}
								else if (dd <= 5) {
									m = GateLevels.HURT1.ordinal();
								}
								else if (dd <= 8) {
									m = GateLevels.PUSH3.ordinal();
								}
								else if (dd <= 12) {
									m = GateLevels.PUSH2.ordinal();
								}
								else if (dd <= 16) {
									m = GateLevels.PUSH1.ordinal();
								}
							}
							world.setBlock(dx, dy, dz, air, m, 3);
						}
						else {
							world.setBlock(dx, dy, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata, 3);
						}
						dy--;
					}

					if (ReikaMathLibrary.py3d(i, 0, k) >= r-0.5) {
						edge.add(new Coordinate(dx, dy, dz));
					}
				}

				if (ReikaMathLibrary.py3d(i, 0, k) >= r-0.5 && ReikaMathLibrary.py3d(i, 0, k) <= r+1.5) {
					for (int d = 0; d < 6; d++) {
						ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[d];
						if (ReikaMathLibrary.py3d(i, 0, k) <= r+0.5 || dir.offsetY == 0) {
							int ddx = dx+dir.offsetX;
							int ddy = 128+dir.offsetY;
							int ddz = dz+dir.offsetZ;
							world.setBlock(ddx, ddy, ddz, Blocks.bedrock);
						}
					}
				}
			}
		}

		for (Coordinate c : edge) {
			for (int d = 0; d < 6; d++) {
				ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[d];
				c.offset(dir, 1).setBlock(world, Blocks.nether_brick);
			}
		}

		return true;
	}

	private boolean isCountedAsAir(World world, int x, int y, int z) {
		Block b = world.getBlock(x, y, z);
		return b.isAir(world, x, y, z) || b.getMaterial() == Material.plants;
	}

	@Override
	public boolean canGenerateAt(World world, int chunkX, int chunkZ) {
		return true;
	}

	@Override
	public String getIDString() {
		return "Chroma_Nether";
	}

}
