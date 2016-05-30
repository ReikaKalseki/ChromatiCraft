package Reika.ChromatiCraft.World.Dimension.Generators;

import java.util.HashMap;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.ChromaDimensionBiome;
import Reika.ChromatiCraft.Base.ChromaWorldGenerator;
import Reika.ChromatiCraft.Block.Dimension.BlockDimensionDeco.DimDecoTypes;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.DimensionGenerators;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;


public class WorldGenGlassCliffs extends ChromaWorldGenerator {


	public WorldGenGlassCliffs(DimensionGenerators g, Random r, long s) {
		super(g, r, s);
	}

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {
		double ang = rand.nextDouble()*360;
		double ang2 = ang+90;
		double len = 16+rand.nextInt(33);
		double h = 16+rand.nextInt(17);
		double sp = 6+rand.nextDouble()*6;
		double o = 0;
		for (double d = -32; d <= 32; d += sp) {
			double dx = x+d*Math.cos(Math.toRadians(ang2))+o*Math.cos(Math.toRadians(ang));
			double dz = z+d*Math.sin(Math.toRadians(ang2))+o*Math.sin(Math.toRadians(ang));
			Coordinate loc = new Coordinate(dx, y, dz);
			double f = (1-0.8*Math.abs(d)/32D);
			double l2 = len*Math.pow(f, 0.75);
			double h2 = h*Math.pow(f, 0.5);
			Cliff c = new Cliff(loc, ang, l2, h2);
			c.calculate(rand);
			c.generate(world, rand);
			o += (-len/2+rand.nextDouble()*len)/4D;
		}
		return true;
	}

	@Override
	public float getGenerationChance(World world, int cx, int cz, ChromaDimensionBiome biome) {
		return 0.015F;
	}

	private static class Cliff {

		private final double length;
		private final double angle;
		private final double maxHeight;

		private final Coordinate origin;

		private final HashMap<Coordinate, Integer> heightMap = new HashMap();

		private Cliff(Coordinate c, double a, double l, double h) {
			origin = c;
			length = l;
			angle = a;
			maxHeight = h;
		}

		private void calculate(Random r) {
			for (double d = -length; d <= length; d += 0.25) {
				double x = origin.xCoord+0.5+d*Math.cos(Math.toRadians(angle));
				double z = origin.zCoord+0.5+d*Math.sin(Math.toRadians(angle));
				double h = maxHeight*Math.pow(1-Math.abs(d)/length, 0.5);
				this.addHeightBlob(x, z, d, h);
			}
		}

		private void addHeightBlob(double x, double z, double d, double h) {
			double r = 0.0625+0.75*Math.pow(0.75, Math.abs(d)/length);
			for (double i = -r; i <= r; i += Math.min(r/2, 0.5)) {
				for (double k = -r; k <= r; k += Math.min(r/2, 0.5)) {
					if (i*i+k*k <= r*r) {
						Coordinate c = new Coordinate(x+i, 0, z+k);
						Integer get = heightMap.get(c);
						int ih = i == 0 && k == 0 ? (int)Math.round(h) : (int)h;
						heightMap.put(c, Math.max(ih, get != null ? get.intValue() : 0));
					}
				}
			}
		}

		private void generate(World world, Random r) {
			for (Coordinate c : heightMap.keySet()) {
				int h = heightMap.get(c);
				int x = c.xCoord;
				int z = c.zCoord;
				int s = world.getTopSolidOrLiquidBlock(x, z)-origin.yCoord;
				for (int i = s; i <= h; i++) {
					int dy = origin.yCoord+i;
					Block b = ChromaBlocks.DIMGEN.getBlockInstance();
					int m = DimDecoTypes.CLIFFGLASS.ordinal();
					if (i == h) {
						b = Blocks.grass;
						m = 0;
					}
					else if (h-i < 3) {
						b = Blocks.stone;
						m = 0;
					}
					world.setBlock(x, dy, z, b, m, 3);
				}
			}
		}

	}
}
