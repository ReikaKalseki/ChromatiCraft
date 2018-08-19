/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2018
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension;

import java.util.HashSet;
import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.ThreadedGenerator;
import Reika.ChromatiCraft.World.Dimension.Terrain.TerrainGenChromaIslands;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;


public class ArchCalculator extends ThreadedGenerator {

	private static final int AREA = 4096;
	private static final int COUNT = 60;

	private static final ChunkSplicedGenerationCache arches = new ChunkSplicedGenerationCache().setWrapping(AREA, AREA);

	public ArchCalculator(long seed) {
		super(seed);
	}

	@Override
	public void run() throws Throwable {
		arches.clear();

		for (int i = 0; i < COUNT; i++) {
			int x = rand.nextInt(AREA);
			int z = rand.nextInt(AREA);

			Arch a = new Arch(x, z, rand.nextDouble()*360, 30+rand.nextDouble()*60, 0.5+rand.nextDouble(), 4+rand.nextInt(17), rand.nextInt(4), 1+rand.nextDouble()*2);
			a.calculate(rand);
			a.generate(arches, rand);
		}
	}

	public static void generateArchData(World world, int chunkX, int chunkZ) {
		arches.generate(world, chunkX, chunkZ);
	}

	@Override
	public String getStateMessage() {
		return "Arch layouts calculated.";
	}

	public static class Arch {

		private final int posX1;
		private final int posY1;
		private final int posZ1;

		//private final int posX2;
		//private final int posY2;
		//private final int posZ2;

		private final double initAngle;
		private final double compassAngle;
		private final double angleDelta;
		private final double radius;
		private final double radiusVariation;
		private final double stepDistance;

		private double angle;
		private double posX;
		private double posY;
		private double posZ;

		private final HashSet<Coordinate> coords = new HashSet();

		private Arch(int x, int z, double ca, double ia, double da, double r, double rv, double d) {
			this(x, TerrainGenChromaIslands.MIN_FLOOR_OCEAN ,z, ca, ia, da, r, rv, d);
		}

		public Arch(int x, int y, int z, double ca, double ia, double da, double r, double rv, double d) {
			posX1 = x;
			posY1 = y;
			posZ1 = z;

			r = Math.max(r, d*1.25);

			initAngle = ia;
			compassAngle = ca;
			angleDelta = da;
			radius = r;
			radiusVariation = rv;
			stepDistance = d;
		}

		public void calculate(Random rand) {
			angle = initAngle;
			posX = posX1+0.5;
			posY = posY1+0.5;
			posZ = posZ1+0.5;

			do {
				this.placeBlob(rand);
				double[] d = ReikaPhysicsHelper.polarToCartesian(stepDistance, angle, compassAngle);
				posX += d[0];
				posY += d[1];
				posZ += d[2];
				angle -= angleDelta;
			} while (angle > -initAngle);
		}

		private void placeBlob(Random rand) {
			double pow = 1.75+rand.nextDouble()*2.25;
			double r = radius-radiusVariation/2D+rand.nextDouble()*radiusVariation;
			for (double i = -r; i <= r; i += 0.5) {
				for (double j = -r; j <= r; j += 0.5) {
					for (double k = -r; k <= r; k += 0.5) {
						if (Math.pow(Math.abs(i), pow)+Math.pow(Math.abs(j), pow)+Math.pow(Math.abs(k), pow) <= Math.pow(Math.abs(r), pow))
							coords.add(new Coordinate(posX+i, posY+j, posZ+k));
					}
				}
			}
		}

		public void generate(World world, Random rand) {
			for (Coordinate c : coords) {
				if (coords.contains(c.offset(0, 1, 0)) || coords.contains(c.offset(0, -1, 0))) {
					if (coords.contains(c.offset(0, 1, 0))) {
						c.setBlock(world, Blocks.stone);
					}
					else {
						c.setBlock(world, Blocks.grass);
					}
				}
			}
		}

		private void generate(ChunkSplicedGenerationCache world, Random rand) {
			for (Coordinate c : coords) {
				if (coords.contains(c.offset(0, 1, 0)) || coords.contains(c.offset(0, -1, 0))) {
					if (coords.contains(c.offset(0, 1, 0))) {
						c.setBlock(world, Blocks.stone);
					}
					else {
						c.setBlock(world, Blocks.grass);
					}
				}
			}
		}

	}

}
