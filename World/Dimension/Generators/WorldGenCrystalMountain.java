package Reika.ChromatiCraft.World.Dimension.Generators;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.ChromaDimensionBiome;
import Reika.ChromatiCraft.Base.ChromaWorldGenerator;
import Reika.ChromatiCraft.Block.Dimension.BlockDimensionDeco.DimDecoTypes;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.ChunkProviderChroma;
import Reika.ChromatiCraft.World.Dimension.DimensionGenerators;
import Reika.DragonAPI.Instantiable.LobulatedCurve;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;


public class WorldGenCrystalMountain extends ChromaWorldGenerator {

	public WorldGenCrystalMountain(DimensionGenerators g) {
		super(g);
	}

	@Override
	public float getGenerationChance(World world, int cx, int cz, ChromaDimensionBiome biome) {
		return 0.5F;
	}

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {
		if (y > 200)
			return false;
		int yb = 64+ChunkProviderChroma.VERTICAL_OFFSET;
		Mountain m = new Mountain(rand).generate(world, x, y, z);
		for (Coordinate c : m.heightMap.keySet()) {
			int h = yb+m.heightMap.get(c);
			for (int dy = y-1; dy <= h; dy++) {
				Block b = dy == h ? Blocks.grass : Blocks.stone;
				world.setBlock(c.xCoord, dy, c.zCoord, b);
			}
		}
		for (Coordinate c : m.cliffSet) {
			int h = yb+m.heightMap.get(c);
			int hy = (h-y)/2;
			if (hy > 0) {
				int y1 = y+1+rand.nextInt(hy);
				int y2 = h-1-rand.nextInt(hy);
				for (int dy = y1; dy <= y2; dy++) {
					world.setBlock(c.xCoord, dy, c.zCoord, ChromaBlocks.DIMGEN.getBlockInstance(), DimDecoTypes.GEMSTONE.ordinal(), 3);
				}
			}
		}
		return true;
	}

	private static class Mountain {

		private final Random rand;
		private final HashMap<Coordinate, Integer> heightMap = new HashMap();
		private final HashSet<Coordinate> cliffSet = new HashSet();

		private final double baseHeight;
		private final LobulatedCurve curve;
		private final LobulatedCurve heightCurve;
		private final double baseRadius;
		private double maxRadius;
		private final double cliffLocation;

		private Mountain(Random r) {
			rand = r;

			baseHeight = 24+r.nextDouble()*32;
			baseRadius = 20+r.nextDouble()*30;
			curve = new LobulatedCurve(baseRadius, baseRadius/4D, 3, 0.5).generate(rand);
			heightCurve = new LobulatedCurve(baseHeight, baseRadius/5*0.5, 5, 0.5).generate(rand);
			cliffLocation = 0.4+r.nextDouble()*0.4;
		}

		public Mountain generate(World world, int x, int y, int z) {
			for (double a = 0; a < 360; a += 0.5) {
				double ang = Math.toRadians(a);
				double r = curve.getRadius(a);
				maxRadius = Math.max(r, maxRadius);
				for (double dr = 0; dr <= r; dr += 0.5) {
					double dx = x+dr*Math.cos(ang);
					double dz = z+dr*Math.sin(ang);
					Coordinate c = new Coordinate(dx, 0, dz);
					if (heightMap.containsKey(c)) {
						continue;
					}
					double f = dr/r;
					if (ReikaMathLibrary.approxrAbs(f, cliffLocation, 0.1))
						cliffSet.add(c);
					int h = (int)this.calcHeight(a, f);
					if (h > 0)
						heightMap.put(c, h);
				}
			}
			return this;
		}

		private double calcHeight(double ang, double f) {
			double fac = (heightCurve.getRadius(ang)-baseHeight)*f;
			if (f < cliffLocation) {
				return fac >= 0 ? baseHeight+fac : baseHeight+fac*0.25;
			}
			else {
				return fac >= 0 ? baseHeight+0.5*fac : baseHeight+fac;
			}
		}

	}

}
