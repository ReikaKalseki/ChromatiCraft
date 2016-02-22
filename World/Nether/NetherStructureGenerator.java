/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Nether;

import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.MapGenCaves;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;
import Reika.DragonAPI.Interfaces.RetroactiveGenerator;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;


public class NetherStructureGenerator implements RetroactiveGenerator {

	public static final NetherStructureGenerator instance = new NetherStructureGenerator();

	private final MapGenFloatingCaves floatingCaves = new MapGenFloatingCaves();

	//sometimes lava, sometimes pyrotheum
	private final LavaRiverGenerator lavaRiverGen = new LavaRiverGenerator();
	private final ChunkSplicedGenerationCache lavaRivers = new ChunkSplicedGenerationCache();
	private final HashSet<ChunkCoordIntPair> gennedChunks = new HashSet();

	private NetherStructureGenerator() {
		//Thread t = new Thread(lavaRiverGen, "Lava River Generation");
		//t.setDaemon(true);
		//t.start();
	}

	@Override
	public void generate(Random rand, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if (world.provider.dimensionId != -1)
			return;
		int n = 8;
		if (rand.nextInt(1) == 0 && Math.abs(chunkX%/*(n-(world.getSeed()%n))*/4) == 0 && Math.abs(chunkZ%/*(n-(world.getSeed()%n))*/4) == /*n/2*/2) {
			int x = chunkX*16+rand.nextInt(16);
			int z = chunkZ*16+rand.nextInt(16);
			this.tryGenerateBypass(world, x, z, rand);
		}

		this.runAirDecorators(world, chunkX, chunkZ, rand, chunkGenerator, chunkProvider);
	}

	private void runAirDecorators(World world, int chunkX, int chunkZ, Random rand, IChunkProvider generator, IChunkProvider loader) {
		this.runCaveGen(world, chunkX, chunkZ, rand, generator, loader);

		this.runGlowstoneGen(world, chunkX, chunkZ, rand);

		lavaRivers.generate(world, chunkX, chunkZ);
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

	private void runCaveGen(World world, int chunkX, int chunkZ, Random rand, IChunkProvider generator, IChunkProvider loader) {
		//Block[] data = new Block[65536];
		//ReikaJavaLibrary.pConsole("Trying cave gen @ "+chunkX+", "+chunkZ);
		//floatingCaves.func_151539_a(generator, world, chunkX, chunkZ, data);
		//lavaRivers.addDataFromColumnData(chunkX, chunkZ, data);

		/*
		Collection<Coordinate> li = floatingCaves.caveBlocks.remove(new ChunkCoordIntPair(chunkX, chunkZ));
		for (Coordinate c : li) {
			if (li.contains(c.offset(1, 0, 0)) && li.contains(c.offset(-1, 0, 0)) && li.contains(c.offset(0, 1, 0)) && li.contains(c.offset(0, -1, 0)) && li.contains(c.offset(0, 0, 1)) && li.contains(c.offset(0, 0, -1))) {
				c.setBlock(world, Blocks.lava);
			}
			else {
				c.setBlock(world, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata, 3);
			}
			/*
			for (int d = 0; d < 6; d++) {
				ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[d];
				Coordinate c2 = c.offset(dir, 1);
				if (dir.offsetY != 0 || ((c2.xCoord >> 4 == c.xCoord >> 4) && (c2.zCoord >> 4) == (c.zCoord >> 4)) || ReikaWorldHelper.isChunkGenerated((WorldServer)world, c2.xCoord, c2.zCoord)) {
					c2.setBlock(world, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata, 3);
				}
				else {
					floatingCaves.overflowCache.addValue(new ChunkCoordIntPair(c2.xCoord >> 4, c2.zCoord >> 4), c2);
				}
			}
		 *//*
		}
		  */

		/*
		for (Coordinate c : li) {
			//if (li.contains(c.offset(1, 0, 0)) && li.contains(c.offset(-1, 0, 0)) && li.contains(c.offset(0, 1, 0)) && li.contains(c.offset(0, -1, 0)) && li.contains(c.offset(0, 0, 1)) && li.contains(c.offset(0, 0, -1))) {
			//	c.setBlock(world, Blocks.air);
			//}
			c.setBlock(world, Blocks.lava);
		}

		li = floatingCaves.overflowCache.remove(new ChunkCoordIntPair(chunkX, chunkZ));
		for (Coordinate c : li) {
			c.setBlock(world, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata, 3);
		}
		 */


		/*
		for (int dx = 0; dx < 16; dx++) {
			for (int dz = 0; dz < 16; dz++) {
				int x = chunkX*16+dx;
				int z = chunkZ*16+dz;
				int d = (dx*16+dz);
				int posIndex = d*data.length/256;

				for (int y = 255; y >= 128; y--) {
					Block b = data[y+posIndex-128];
					if (b != null && (y == 128 || data[y+posIndex-128-1] == null)) {
						//ReikaJavaLibrary.pConsole("Setting block "+b.getLocalizedName()+" at "+x+", "+y+", "+z);
						//if (y < 255)
						//	world.setBlock(x, y+1, z, Blocks.air, 0, 2);
						//world.setBlock(x, y, z, Blocks.glowstone, 0, 2);
						world.setBlock(x, y, z, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata, 2);
						//if (y > 128)
						//	world.setBlock(x, y-1, z, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata, 2);
					}
				}
				/*
				for (int y = 255; y >= 128; y--) {
					Block b = data[y+posIndex];
					if (b != null && (y == 128 || data[y+posIndex-1] == null)) {
						//ReikaJavaLibrary.pConsole("Setting block "+b.getLocalizedName()+" at "+x+", "+y+", "+z);
						//if (y < 255)
						//	world.setBlock(x, y+1, z, Blocks.air, 0, 2);
						//world.setBlock(x, y, z, Blocks.glowstone, 0, 2);
						world.setBlock(x, y, z, Blocks.bedrock, 0, 2);
						//if (y > 128)
						//	world.setBlock(x, y-1, z, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata, 2);
					}
				}
		 *//*
			}
		}
		  */

		//ReikaJavaLibrary.pConsole("Completed cave gen @ "+chunkX+", "+chunkZ);
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
					while (!world.getBlock(dx, dy, dz).isAir(world, dx, dy, dz)) {
						if (ReikaMathLibrary.py3d(i, 0, k) < r-0.5) {
							world.setBlock(dx, dy, dz, Blocks.air);
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

	@Override
	public boolean canGenerateAt(Random rand, World world, int chunkX, int chunkZ) {
		return true;
	}

	@Override
	public String getIDString() {
		return "Chroma_Nether";
	}

	private static class MapGenFloatingCaves extends MapGenCaves {

		//private final MultiMap<ChunkCoordIntPair, Coordinate> caveBlocks = new MultiMap(new HashSetFactory());
		//private final MultiMap<ChunkCoordIntPair, Coordinate> overflowCache = new MultiMap(new HashSetFactory());

		@Override
		protected void digBlock(Block[] data, int index, int x, int y, int z, int chunkX, int chunkZ, boolean foundTop)
		{
			//ReikaJavaLibrary.pConsole("Digging block at "+(x+chunkX*16)+", "+y+", "+(z+chunkZ*16));
			data[index] = Blocks.glowstone;//lava;
			//caveBlocks.addValue(new ChunkCoordIntPair(chunkX, chunkZ), new Coordinate(x+chunkX*16, y, z+chunkZ*16));
		}

		private boolean isTopBlock(Block[] data, int index, int x, int y, int z, int chunkX, int chunkZ)
		{
			return false;
		}

		@Override
		protected boolean isOceanBlock(Block[] data, int index, int x, int y, int z, int chunkX, int chunkZ)
		{
			return false;
		}
	}

	private static class LavaRiverGenerator implements Runnable {

		private final MapGenFloatingCaves caveGenBase = new MapGenFloatingCaves();
		private final ConcurrentLinkedQueue<ChunkCoordIntPair> requestedChunks = new ConcurrentLinkedQueue();

		@Override
		public void run() {
			for (int i = -256; i <= 256; i++) {
				for (int k = -256; k <= 256; k++) {
					requestedChunks.add(new ChunkCoordIntPair(i, k));
				}
			}

			while (true) {
				for (ChunkCoordIntPair p : requestedChunks) {
					Block[] data = new Block[65536];
					caveGenBase.func_151539_a(null, null, p.chunkXPos, p.chunkZPos, data);

					Collection<Coordinate> li = new HashSet();
					for (int dx = 0; dx < 16; dx++) {
						for (int dz = 0; dz < 16; dz++) {
							int x = p.chunkXPos*16+dx;
							int z = p.chunkZPos*16+dz;
							int d = (dx*16+dz);
							int posIndex = d*data.length/256;
							for (int y = 255; y >= 128; y--) {
								Block b = data[y+posIndex-128];
								if (b != null)
									li.add(new Coordinate(x, y, z));
							}
						}
					}

					for (Coordinate c : li) {
						for (int d = 0; d < 6; d++) {
							ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[d];
							Coordinate c2 = c.offset(dir, 1);
							instance.lavaRivers.setBlock(c2.xCoord, c2.yCoord, c2.zCoord, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
						}
					}

					for (Coordinate c : li) {
						instance.lavaRivers.setBlock(c.xCoord, c.yCoord, c.zCoord, Blocks.lava);
					}
				}
			}
		}

	}

}
