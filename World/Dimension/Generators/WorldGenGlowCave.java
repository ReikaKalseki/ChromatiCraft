/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Generators;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import Reika.ChromatiCraft.Base.ChromaDimensionBiome;
import Reika.ChromatiCraft.Base.ChromaWorldGenerator;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.ChromaDimensionManager.Biomes;
import Reika.ChromatiCraft.World.Dimension.DimensionGenerators;
import Reika.DragonAPI.Instantiable.Interpolation;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Math.Spline;
import Reika.DragonAPI.Instantiable.Math.Spline.BasicSplinePoint;
import Reika.DragonAPI.Instantiable.Math.Spline.SplineType;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class WorldGenGlowCave extends ChromaWorldGenerator {

	private static final double MIN_RADIUS = 3.5;
	private static final double MAX_RADIUS = 5;
	private static final double MAX_RADIUS_CHANGE_PER_SEGMENT = 0.5;

	public WorldGenGlowCave(DimensionGenerators g, Random rand, long seed) {
		super(g, rand, seed);
	}

	@Override
	public float getGenerationChance(World world, int cx, int cz, ChromaDimensionBiome biome) {
		return biome == Biomes.CENTER.getBiome() ? 0.005F : 0.0025F;
	}

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {

		int dy = ReikaWorldHelper.findTopBlockBelowY(world, x, 255, z);
		Block bk = world.getBlock(x, dy, z);
		if (bk == Blocks.grass || bk == Blocks.sand) {
			HashSet<Coordinate> set = new HashSet();
			int n = 6+rand.nextInt(4);
			double x0 = x+0.5;
			double y0 = y+0.5;
			double z0 = z+0.5;
			double r0 = ReikaRandomHelper.getRandomBetween(MIN_RADIUS, MAX_RADIUS);
			double rdir = rand.nextBoolean() ? -1 : 1;
			List<CavePoint> li0 = new ArrayList();
			li0.add(new CavePoint(x0, y0, z0, r0));
			boolean first = true;
			while (y0 >= 16) {
				double x2 = ReikaRandomHelper.getRandomPlusMinus(x0, first ? 6 : 32);
				double z2 = ReikaRandomHelper.getRandomPlusMinus(z0, first ? 6 : 32);
				double y2 = first ? y0-8-rand.nextInt(5) : y0-rand.nextInt(16);
				double r2 = r0+rdir*rand.nextDouble()*MAX_RADIUS_CHANGE_PER_SEGMENT;
				if (rand.nextInt(6) == 0)
					rdir = -rdir;
				x0 = x2;
				y0 = y2;
				z0 = z2;
				r0 = r2;
				li0.add(new CavePoint(x0, y0, z0, r0));
				first = false;
			}
			Spline s = new Spline(SplineType.CHORDAL);
			for (int i = 0; i < li0.size(); i++) {
				CavePoint pos = li0.get(i);
				s.addPoint(pos);
				if (i != 0 && i != li0.size()-1)
					s.addPoint(pos.offset(0, -1, 0));
			}
			List<DecimalPosition> li = s.get(12, false);
			Interpolation rVar = new Interpolation(false);
			int d = 0;
			while (d < li.size()) {
				rVar.addPoint(d, ReikaRandomHelper.getRandomBetween(MIN_RADIUS, MAX_RADIUS));
				int l = li.size()-d;
				d += 1+rand.nextInt(l);
			}
			rVar.addPoint(li.size()-1, ReikaRandomHelper.getRandomBetween(MIN_RADIUS, MAX_RADIUS));
			for (int i = 0; i < li.size()-1; i++) {
				DecimalPosition pos1 = li.get(i);
				DecimalPosition pos2 = li.get(i+1);
				this.getLine(world, pos1, pos2, rVar.getValue(i), rVar.getValue(i+1), set);
			}
			ReikaJavaLibrary.pConsole(set.size()+" @ "+x+", "+z);
			for (Coordinate c : set) {
				if (c.getBlock(world).getMaterial() == Material.water || ReikaWorldHelper.checkForAdjMaterial(world, c.xCoord, c.yCoord, c.zCoord, Material.water) != null || ReikaWorldHelper.checkForAdjBlock(world, c.xCoord, c.yCoord, c.zCoord, ChromaBlocks.STRUCTSHIELD.getBlockInstance()) != null)
					return false;
			}
			this.generate(world, set, dy);
			return true;
		}

		return false;
	}

	private void generate(World world, HashSet<Coordinate> set, int upper) {
		for (Coordinate c : set) {
			if (c.yCoord > upper)
				continue;
			boolean top = c.yCoord == upper;
			boolean edge = !set.containsAll(c.getAdjacentCoordinates());
			Block b = Blocks.air;
			int meta = 0;
			if (!top) {
				if (edge) {
					if (c.yCoord <= 24) {
						b = Blocks.bedrock;
					}
					else if (c.yCoord <= 30 && ReikaRandomHelper.doWithChance((30-c.yCoord)/6D)) {
						b = Blocks.bedrock;
					}
					else {
						b = ChromaBlocks.STRUCTSHIELD.getBlockInstance();
						meta = ReikaRandomHelper.doWithChance(20) ? BlockType.MOSS.metadata : BlockType.STONE.metadata;
					}
				}
			}
			c.setBlock(world, b, meta);
		}
	}

	private void getLine(World world, DecimalPosition pos1, DecimalPosition pos2, double r1, double r2, HashSet<Coordinate> set) {
		double dx = pos2.xCoord-pos1.xCoord;
		double dy = pos2.yCoord-pos1.yCoord;
		double dz = pos2.zCoord-pos1.zCoord;
		double dd = ReikaMathLibrary.py3d(dx, dy, dz);
		double dr = r2-r1;
		for (double d = 0.25; d < dd; d += 1) {
			double f = d/dd;
			double x = pos1.xCoord+f*dx;
			double y = pos1.yCoord+f*dy;
			double z = pos1.zCoord+f*dz;
			double r = r1+dr*f;
			this.getSphere(world, x, y, z, r, set);
		}
	}

	private void getSphere(World world, double x, double y, double z, double r, HashSet<Coordinate> set) {
		for (double dx = -r; dx <= r; dx += 0.75) {
			for (double dy = -r; dy <= r; dy += 0.75) {
				for (double dz = -r; dz <= r; dz += 0.75) {
					if (dx*dx+dy*dy+dz*dz <= r*r) {
						int x0 = MathHelper.floor_double(x+dx);
						int y0 = MathHelper.floor_double(y+dy);
						int z0 = MathHelper.floor_double(z+dz);
						set.add(new Coordinate(x0, y0, z0));
					}
				}
			}
		}
	}

	private static class CavePoint extends BasicSplinePoint {

		public final double radius;

		public CavePoint(double x, double y, double z, double r) {
			super(x, y, z);
			radius = r;
		}

		public CavePoint offset(int x, int y, int z) {
			return new CavePoint(posX+x, posY+y, posZ+z, radius);
		}

		@Override
		public DecimalPosition asPosition() {
			return super.asPosition();
		}

	}

}
