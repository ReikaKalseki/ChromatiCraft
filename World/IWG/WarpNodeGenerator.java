/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.IWG;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.BiomeGlowingCliffs;
import Reika.ChromatiCraft.World.GlowingCliffsColumnShaper;
import Reika.DragonAPI.Interfaces.RetroactiveGenerator;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

public class WarpNodeGenerator implements RetroactiveGenerator {

	public static final WarpNodeGenerator instance = new WarpNodeGenerator();

	private WarpNodeGenerator() {

	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if (world.provider.dimensionId == 0 && world.getWorldInfo().getTerrainType() != WorldType.FLAT) {
			chunkX *= 16;
			chunkZ *= 16;
			int posX = chunkX + random.nextInt(16);
			int posZ = chunkZ + random.nextInt(16);
			BiomeGenBase b = world.getBiomeGenForCoords(posX, posZ);
			if (random.nextInt(this.getBiomeChance(b)) == 0) {
				int miny = this.getMinY(world, posX, posZ, b);
				int maxy = this.getMaxY(world, posX, posZ, b);
				if (miny >= maxy) {
					ChromatiCraft.logger.logError("Failed to generate a warp node @ "+posX+", "+posZ+" due to zero height range!");
					return;
				}
				int posY = miny+random.nextInt(maxy-miny+1);
				if (this.canGenerateAt(world, posX, posY, posZ)) {
					world.setBlock(posX, posY, posZ, ChromaBlocks.WARPNODE.getBlockInstance());
				}
			}
		}
	}

	private int getBiomeChance(BiomeGenBase b) {
		int d = this.getAverageDistance(b)/16;
		return d*d;
	}

	private int getAverageDistance(BiomeGenBase b) {
		if (ChromatiCraft.isRainbowForest(b))
			return 850;
		else if (BiomeGlowingCliffs.isGlowingCliffs(b))
			return 1000;
		else if (ChromatiCraft.isEnderForest(b))
			return 600;
		else
			return 1200;
	}

	private int getMinY(World world, int x, int z, BiomeGenBase b) {
		int max = ReikaWorldHelper.getTopNonAirBlock(world, x, z, true)+48;
		if (BiomeGlowingCliffs.isGlowingCliffs(b)) {
			max = Math.max(max, GlowingCliffsColumnShaper.MAX_MIDDLE_TOP_Y+16);
		}
		return max;
	}

	private int getMaxY(World world, int x, int z, BiomeGenBase b) {
		int max = Math.min(255, (int)(192*Math.max(1, b.rootHeight)));
		if (BiomeGlowingCliffs.isGlowingCliffs(b)) {
			max = 255;
		}
		return Math.max(128, max);
	}

	private boolean canGenerateAt(World world, int x, int y, int z) {
		return world.getBlock(x, y, z).isAir(world, x, y, z) && world.canBlockSeeTheSky(x, y, z);
	}

	@Override
	public boolean canGenerateAt(World world, int chunkX, int chunkZ) {
		return true;
	}

	@Override
	public String getIDString() {
		return "ChromatiCraft Warp Nodes";
	}

}
