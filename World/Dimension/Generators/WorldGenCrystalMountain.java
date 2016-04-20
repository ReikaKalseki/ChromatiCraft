package Reika.ChromatiCraft.World.Dimension.Generators;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import Reika.ChromatiCraft.Base.ChromaDimensionBiome;
import Reika.ChromatiCraft.Base.ChromaWorldGenerator;
import Reika.ChromatiCraft.Block.Dimension.BlockDimensionDeco.DimDecoTypes;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.ChunkProviderChroma;
import Reika.ChromatiCraft.World.Dimension.DimensionGenerators;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.SimplexNoiseGenerator;


public class WorldGenCrystalMountain extends ChromaWorldGenerator {

	private final SimplexNoiseGenerator mountainHeight;
	private final SimplexNoiseGenerator shearNoise;
	private final SimplexNoiseGenerator threshNoise;
	private final SimplexNoiseGenerator gemNoise1;
	private final SimplexNoiseGenerator gemNoise2;

	public static final double MAX_AMPLITUDE = 80;

	public static final double MIN_SHEAR = 4;
	public static final double MAX_SHEAR = 45;

	public static final double MIN_THRESH = 4;
	public static final double MAX_THRESH = 24;

	public WorldGenCrystalMountain(DimensionGenerators g, Random rand, long seed) {
		super(g, rand, seed);
		mountainHeight = new SimplexNoiseGenerator(seed);

		shearNoise = new SimplexNoiseGenerator(seed ^ -seed);
		threshNoise = new SimplexNoiseGenerator(~(seed ^ -seed));

		gemNoise1 = new SimplexNoiseGenerator(-seed);
		gemNoise2 = new SimplexNoiseGenerator(~(-seed));
	}

	@Override
	public float getGenerationChance(World world, int cx, int cz, ChromaDimensionBiome biome) {
		return 1;
	}

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {
		int chunkX = x >> 4;
		int chunkZ = z >> 4;
		double innerScale = 1/16D;
		double mainScale = 1/4D;
		x = ReikaMathLibrary.bitRound(x, 4);
		z = ReikaMathLibrary.bitRound(z, 4);
		int dt = 1+rand.nextInt(4);
		for (int i = 0; i < 16; i++) {
			for (int k = 0; k < 16; k++) {
				int dx = x+i;
				int dz = z+k;
				double rx = this.calcR(chunkX, i, innerScale, mainScale);
				double rz = this.calcR(chunkZ, k, innerScale, mainScale);
				double val = this.calcHeight(rx, rz);
				boolean cliff = this.isCliff(chunkX, chunkZ, i, k, rx, rz, innerScale, mainScale);
				double shearThresh = 0;
				if (cliff) {
					shearThresh = ReikaMathLibrary.normalizeToBounds(threshNoise.getValue(rx, rz), MIN_THRESH, MAX_THRESH);
				}
				int h = (int)val;
				for (int j = 0; j <= h; j++) {
					int dy = 64+ChunkProviderChroma.VERTICAL_OFFSET+j;
					if (world.getWorldInfo().getTerrainType() == WorldType.FLAT)
						dy = 3+j;
					int m = 0;
					Block b = Blocks.stone;
					if (j == h)
						b = Blocks.grass;
					else if (h-j <= dt) {
						b = Blocks.dirt;
					}
					else if (cliff) {
						double g1 = ReikaMathLibrary.normalizeToBounds(gemNoise1.getValue(rx, rz), shearThresh+1, h-dt-1);//Math.max(, gemNoiseLow.getValue(rx, rz));
						double g2 = ReikaMathLibrary.normalizeToBounds(gemNoise2.getValue(rx, rz), shearThresh+1, h-dt-1);
						double max = Math.max(g1, g2);
						double min = Math.min(g1, g2);
						if (max-min > 1 && ReikaMathLibrary.isValueInsideBoundsIncl(min, max, j)) {
							b = ChromaBlocks.DIMGEN.getBlockInstance();
							m = DimDecoTypes.GEMSTONE.ordinal();
						}
					}
					world.setBlock(dx, dy, dz, b, m, 2);
				}
			}
		}
		return true;
	}

	private double calcHeight(double rx, double rz) {
		double voff = 0.6;
		double val = Math.max(0, (voff+mountainHeight.getValue(rx, rz))*(MAX_AMPLITUDE-MAX_SHEAR)/(1+voff));
		double shear = ReikaMathLibrary.normalizeToBounds(shearNoise.getValue(rx, rz), MIN_SHEAR, MAX_SHEAR);
		double shearThresh = ReikaMathLibrary.normalizeToBounds(threshNoise.getValue(rx, rz), MIN_THRESH, MAX_THRESH);
		if (val >= shearThresh)
			val += shear;
		return val;
	}

	private boolean isCliff(int cx, int cz, int i, int k, double rx, double rz, double innerScale, double mainScale) {
		double h = this.calcHeight(rx, rz);
		double rxp = rx+innerScale*mainScale;
		double rxn = rx-innerScale*mainScale;
		double rzp = rz+innerScale*mainScale;
		double rzn = rz-innerScale*mainScale;
		double hpx = this.calcHeight(rxp, rz);
		double hnx = this.calcHeight(rxn, rz);
		double hpz = this.calcHeight(rx, rzp);
		double hnz = this.calcHeight(rx, rzn);
		return Math.abs(h-hpx) > 4 || Math.abs(h-hpz) > 4 || Math.abs(h-hnx) > 4 || Math.abs(h-hnz) > 4;
	}

	private double calcR(int chunk, int d, double innerScale, double mainScale) {
		return (chunk+d*innerScale)*mainScale;
	}

	private static class MountainNoiseGenerator {

		private final Random rand;
		private final int chunkX;
		private final int chunkZ;

		private double[][] heightNoise = new double[16][16];

		private MountainNoiseGenerator(int cx, int cz, Random r) {
			rand = r;
			chunkX = cx;
			chunkZ = cz;
		}

	}

}
