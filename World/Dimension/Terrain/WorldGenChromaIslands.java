package Reika.ChromatiCraft.World.Dimension.Terrain;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.ChromaDimensionBiomeTerrainShaper;
import Reika.ChromatiCraft.World.Dimension.BiomeDistributor;
import Reika.ChromatiCraft.World.Dimension.ChromaDimensionManager.Biomes;
import Reika.ChromatiCraft.World.Dimension.ChromaDimensionManager.ChromaDimensionBiomeType;
import Reika.ChromatiCraft.World.Dimension.ChromaDimensionManager.SubBiomes;
import Reika.ChromatiCraft.World.Dimension.ChunkProviderChroma;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.SimplexNoiseGenerator;


public class WorldGenChromaIslands extends ChromaDimensionBiomeTerrainShaper {

	private final SimplexNoiseGenerator archHeightNoise;
	private final SimplexNoiseGenerator archLocationNoise; //only when abs(val) < thresh
	private final SimplexNoiseGenerator archThreshNoise;

	private final SimplexNoiseGenerator floorNoise;

	private static final int MIN_FLOOR_OCEAN = 6;
	private static final int MAX_FLOOR_OCEAN = 36;
	private static final int MIN_FLOOR_SHALLOWS = 64;
	private static final int MAX_FLOOR_SHALLOWS = 80;

	private static final int MIN_ARCH_HEIGHT = MIN_FLOOR_SHALLOWS-24;
	private static final int MAX_ARCH_HEIGHT = 64+ChunkProviderChroma.VERTICAL_OFFSET+64;

	private static final double ARCH_VAL_LIMIT = 0.6;

	public WorldGenChromaIslands(long seed) {
		super(seed, Biomes.ISLANDS, SubBiomes.DEEPOCEAN);

		floorNoise = new SimplexNoiseGenerator(seed);

		archHeightNoise = new SimplexNoiseGenerator(ReikaMathLibrary.cycleBitsLeft(seed, 16));
		archLocationNoise = new SimplexNoiseGenerator(ReikaMathLibrary.cycleBitsLeft(seed, 32));
		archThreshNoise = new SimplexNoiseGenerator(ReikaMathLibrary.cycleBitsLeft(seed, 48));
	}

	@Override
	public void generateColumn(World world, int chunkX, int chunkZ, int i, int k, int surface, Random rand, double edgeFactor) {
		//3d noise map, have "blobs" of ground on surface and suspended midwater, put stuff in
		double innerScale = 1/16D;
		double mainScale = 0.25D;
		double floorScale = 0.5D/mainScale;
		int dx = chunkX+i;
		int dz = chunkZ+k;
		ChromaDimensionBiomeType biome = BiomeDistributor.getBiome(dx, dz).getExactType();
		boolean ocean = biome == SubBiomes.DEEPOCEAN;
		double rx = this.calcR(chunkX, i, innerScale, mainScale);
		double rz = this.calcR(chunkZ, k, innerScale, mainScale);
		double ay1 = 0;
		double ay2 = 0;
		if (!ocean) {
			double thresh = ReikaMathLibrary.normalizeToBounds(archThreshNoise.getValue(rx, rz), -ARCH_VAL_LIMIT, ARCH_VAL_LIMIT);
			double aval = Math.abs(archLocationNoise.getValue(rx, rz));
			//ReikaJavaLibrary.pConsole(aval+" & "+thresh+":  "+ReikaMathLibrary.approxp(aval, thresh, 0.05));
			if (ReikaMathLibrary.approxr(aval, thresh, 0.05)) {
				double ay = ReikaMathLibrary.normalizeToBounds(archHeightNoise.getValue(rx, rz), MIN_ARCH_HEIGHT, MAX_ARCH_HEIGHT);
				ay1 = ay-2;
				ay2 = ay+2;
			}
		}
		double min = ocean ? MIN_FLOOR_OCEAN : MIN_FLOOR_SHALLOWS;
		double max = ocean ? MAX_FLOOR_OCEAN : MAX_FLOOR_SHALLOWS;
		double f = ReikaMathLibrary.normalizeToBounds(floorNoise.getValue(rx*floorScale, rz*floorScale), min, max);
		for (int y = 0; y <= 255; y++) {
			double ry = this.calcR(0, y, innerScale, mainScale);
			Block b = Blocks.air;//Blocks.water;
			if ((y >= ay1 && y <= ay2) || y <= f) {
				b = Blocks.stone;
			}
			if (y == 0) {
				b = Blocks.bedrock;
			}
			if (y >= 64+ChunkProviderChroma.VERTICAL_OFFSET-1 && b == Blocks.water)
				b = Blocks.air;
			world.setBlock(dx, y, dz, b, 0, 2);
			//world.setBlock(dx, y+1, dz, Blocks.grass, 0, 2);
		}
	}

	@Override
	public double getBiomeSearchDistance() {
		return 16;
	}

}
