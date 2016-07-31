/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Base.ChromaDimensionBiome;
import Reika.ChromatiCraft.Base.ChromaDimensionBiomeTerrainShaper;
import Reika.ChromatiCraft.World.Dimension.ChromaDimensionManager.ChromaDimensionBiomeType;
import Reika.ChromatiCraft.World.Dimension.Terrain.TerrainGenChromaIslands;
import Reika.ChromatiCraft.World.Dimension.Terrain.TerrainGenCrystalMountain;
import Reika.ChromatiCraft.World.Dimension.Terrain.TerrainGenSkylandCanyons;


public class BiomeTerrainProvider {

	private final ArrayList<ChromaDimensionBiomeTerrainShaper> terrain = new ArrayList();

	protected BiomeTerrainProvider(long seed) {
		this.registerTerrain(new TerrainGenSkylandCanyons(seed));
		this.registerTerrain(new TerrainGenCrystalMountain(seed));
		this.registerTerrain(new TerrainGenChromaIslands(seed));
	}

	public void registerTerrain(ChromaDimensionBiomeTerrainShaper b) {
		terrain.add(b);
	}

	public void generateChunk(World world, int chunkX, int chunkZ, Random rand) {
		//ReikaJavaLibrary.pConsole("Providing chunk "+chunkX+", "+chunkZ+" ["+(chunkX*16)+"-"+(chunkX*16+15)+","+(chunkZ*16)+"-"+(chunkZ*16+15)+"]");
		chunkX = chunkX << 4;
		chunkZ = chunkZ << 4;
		for (int i = 0; i < 16; i++) {
			for (int k = 0; k < 16; k++) {
				int dx = chunkX+i;
				int dz = chunkZ+k;
				ChromaDimensionBiome b = this.getBiome(dx, dz);
				//ReikaJavaLibrary.pConsole("Generating column "+dx+", "+dz+" with "+b);
				int top = world.getTopSolidOrLiquidBlock(dx, dz)-1;
				for (ChromaDimensionBiomeTerrainShaper s : terrain) {
					if (s.canGenerateIn(b)) {
						//this.clearColumn(world, dx, dz);
						s.generateColumn(world, chunkX, chunkZ, i, k, top, rand, this.getEdgeFactor(world, dx, dz, s));
					}
				}
			}
		}
	}

	private void clearColumn(World world, int dx, int dz) {
		for (int y = 0; y <= 255; y++) {
			world.setBlock(dx, y, dz, Blocks.stone, 0, 4);
		}
	}

	protected final double getEdgeFactor(World world, int x, int z, ChromaDimensionBiomeTerrainShaper t) {
		if (t.isFlatWorld(world))
			return 1;
		int minDist = Integer.MAX_VALUE;
		double search = t.getBiomeSearchDistance();
		for (int i = 2; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			for (int d = 0; d < search; d++) {
				int dx = x+d*dir.offsetX;
				int dz = z+d*dir.offsetZ;
				ChromaDimensionBiome b = BiomeDistributor.getBiome(dx, dz);
				if (!t.canGenerateIn(b)) {
					minDist = Math.min(minDist, d);
					break;
				}
			}
		}
		return minDist == Integer.MAX_VALUE ? 1 : minDist/search;
	}

	protected ChromaDimensionBiome getBiome(int x, int z) {
		return BiomeDistributor.getBiome(x, z);
	}

	public static class ForcedBiomeTerrainProvider extends BiomeTerrainProvider {

		private final ChromaDimensionBiomeType biome;

		public ForcedBiomeTerrainProvider(ChromaDimensionBiomeType type, long seed) {
			super(seed);
			biome = type;
		}

		@Override
		protected ChromaDimensionBiome getBiome(int x, int z) {
			return biome.getBiome();
		}

	}

}
