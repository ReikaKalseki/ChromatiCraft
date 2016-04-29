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

	private final SimplexNoiseGenerator XYNoise;
	private final SimplexNoiseGenerator XZNoise;
	private final SimplexNoiseGenerator YZNoise;

	private final SimplexNoiseGenerator floorNoise;

	private static final int MIN_FLOOR_OCEAN = 6;
	private static final int MAX_FLOOR_OCEAN = 36;
	private static final int MIN_FLOOR_SHALLOWS = 64;
	private static final int MAX_FLOOR_SHALLOWS = 80;

	public WorldGenChromaIslands(long seed) {
		super(seed, Biomes.ISLANDS, SubBiomes.DEEPOCEAN);

		floorNoise = new SimplexNoiseGenerator(seed);

		XYNoise = new SimplexNoiseGenerator(ReikaMathLibrary.cycleBitsLeft(seed, 16));
		XZNoise = new SimplexNoiseGenerator(ReikaMathLibrary.cycleBitsLeft(seed, 32));
		YZNoise = new SimplexNoiseGenerator(ReikaMathLibrary.cycleBitsLeft(seed, 48));
	}

	@Override
	public void generateColumn(World world, int chunkX, int chunkZ, int i, int k, Random rand, double edgeFactor) {
		//3d noise map, have "blobs" of ground on surface and suspended midwater, put stuff in
		double innerScale = 1/16D;
		double mainScale = 1D;
		double floorScale = 0.5;
		double rx = this.calcR(chunkX >> 4, i, innerScale, mainScale);
		double rz = this.calcR(chunkZ >> 4, k, innerScale, mainScale);
		double dxz = XZNoise.getValue(rx, rz);
		int dx = chunkX+i;
		int dz = chunkZ+k;
		ChromaDimensionBiomeType biome = BiomeDistributor.getBiome(dx, dz).getExactType();
		double min = biome == SubBiomes.DEEPOCEAN ? MIN_FLOOR_OCEAN : MIN_FLOOR_SHALLOWS;
		double max = biome == SubBiomes.DEEPOCEAN ? MAX_FLOOR_OCEAN : MAX_FLOOR_SHALLOWS;
		double f = ReikaMathLibrary.normalizeToBounds(floorNoise.getValue(rx*floorScale, rz*floorScale), min, max);
		for (int y = 0; y <= 64+ChunkProviderChroma.VERTICAL_OFFSET+3; y++) {
			double ry = this.calcR(0, y, innerScale, mainScale);
			double dxy = XYNoise.getValue(rx, ry);
			double dyz = YZNoise.getValue(ry, rz);
			double d = this.convolve(dxz, dxy, dyz);
			Block b = Blocks.water;
			if (y >= 64+ChunkProviderChroma.VERTICAL_OFFSET)
				b = Blocks.air;
			if (d > 0 || y <= f) {
				b = Blocks.stone;
			}
			if (y == 0) {
				b = Blocks.bedrock;
			}
			world.setBlock(dx, y, dz, b);
		}
	}

	private double convolve(double d1, double d2, double d3) {
		return (d1+d2+d3)-1;//ReikaMathLibrary.py3d(d1, d2, d3);//*Math.signum(d1)*Math.signum(d2)*Math.signum(d3);
	}

	private double calcR(int chunk, int d, double innerScale, double mainScale) {
		return (chunk+d*innerScale)*mainScale;
	}

	@Override
	public double getBiomeSearchDistance() {
		return 16;
	}

}
