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

	private static final int MAX_HEIGHT_OCEAN = 64+ChunkProviderChroma.VERTICAL_OFFSET-1;
	private static final int MAX_HEIGHT_SHALLOWS = 64+ChunkProviderChroma.VERTICAL_OFFSET+64;

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
		double rx = this.calcR(chunkX, i, innerScale, mainScale);
		double rz = this.calcR(chunkZ, k, innerScale, mainScale);
		double dxz = XZNoise.getValue(rx, rz);
		int dx = chunkX+i;
		int dz = chunkZ+k;
		ChromaDimensionBiomeType biome = BiomeDistributor.getBiome(dx, dz).getExactType();
		boolean ocean = biome == SubBiomes.DEEPOCEAN;
		double min = ocean ? MIN_FLOOR_OCEAN : MIN_FLOOR_SHALLOWS;
		double max = ocean ? MAX_FLOOR_OCEAN : MAX_FLOOR_SHALLOWS;
		double f = ReikaMathLibrary.normalizeToBounds(floorNoise.getValue(rx*floorScale, rz*floorScale), min, max);
		double h = ocean ? MAX_HEIGHT_OCEAN : MAX_HEIGHT_SHALLOWS;
		for (int y = 0; y <= h+2; y++) {
			double ry = this.calcR(0, y, innerScale, mainScale);
			double dxy = XYNoise.getValue(rx, ry);
			double dyz = YZNoise.getValue(ry, rz);
			double d = this.convolve(dxz, dxy, dyz, y, f, h, ocean);
			Block b = Blocks.air;//Blocks.water;
			if (d > 0 || y <= f) {
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

	private double convolve(double d1, double d2, double d3, double y, double f, double h, boolean ocean) {
		double val = d1+d2+d3-(ocean ? 1.25 : 0.5);//*Math.signum(d1)*Math.signum(d2)*Math.signum(d3);
		if (y-f < 8)
			val *= (y-f)/8D;
		else if (y > h-8)
			val *= Math.max(0, (h-y)/8D);
		return val;
	}

	@Override
	public double getBiomeSearchDistance() {
		return 16;
	}

}
