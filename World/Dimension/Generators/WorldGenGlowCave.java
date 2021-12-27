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

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeavesBase;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Base.ChromaDimensionBiome;
import Reika.ChromatiCraft.Base.ChromaWorldGenerator;
import Reika.ChromatiCraft.Block.Decoration.BlockEtherealLight.Flags;
import Reika.ChromatiCraft.Block.Dimension.BlockDimensionDeco.DimDecoTypes;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Block.Worldgen.BlockTieredPlant.TieredPlants;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.ChromaDimensionManager.Biomes;
import Reika.ChromatiCraft.World.Dimension.DimensionGenerators;
import Reika.DragonAPI.Instantiable.Interpolation;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Math.Spline;
import Reika.DragonAPI.Instantiable.Math.Spline.BasicSplinePoint;
import Reika.DragonAPI.Instantiable.Math.Spline.SplineType;
import Reika.DragonAPI.Instantiable.Math.Noise.NoiseGeneratorBase;
import Reika.DragonAPI.Instantiable.Math.Noise.SimplexNoiseGenerator;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

public class WorldGenGlowCave extends ChromaWorldGenerator {

	private static final double MIN_RADIUS = 3;
	private static final double MAX_RADIUS = 4.5;
	private static final double MAX_RADIUS_CHANGE_PER_SEGMENT = 0.5;

	private static final double MAX_CRACK_CHANCE = 0.066;//0.08;
	private static final int MAX_CRACK_Y = 20;//16;//14;
	public static final int FULL_BEDROCK_Y = 8;
	public static final int MAX_BEDROCK_Y = 32;//28;//24;

	private final NoiseGeneratorBase wallSelectionNoise;

	public WorldGenGlowCave(DimensionGenerators g, Random rand, long seed) {
		super(g, rand, seed);
		wallSelectionNoise = new SimplexNoiseGenerator(seed).setFrequency(1/3D).addOctave(2, 0.5);
	}

	@Override
	public float getGenerationChance(World world, int cx, int cz, ChromaDimensionBiome biome) {
		return biome == Biomes.CENTER.getBiome() ? 0.0025F : 0.00125F;
	}

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {
		int oy = ReikaWorldHelper.findTopBlockBelowY(world, x, 255, z);
		Block bk = world.getBlock(x, oy, z);
		if (bk == Blocks.grass || bk == Blocks.sand) {
			HashSet<Coordinate> set1 = new HashSet();
			HashSet<Coordinate> set2 = new HashSet();
			double x0 = x+0.5;
			double y0 = oy+0.5+1;
			double z0 = z+0.5;
			this.growFrom(world, rand, x0, y0, z0, MAX_RADIUS, set1, set2, 0, 0);
			for (Coordinate c : set1) {
				if (c.getBlock(world).getMaterial() == Material.water || ReikaWorldHelper.checkForAdjMaterial(world, c.xCoord, c.yCoord, c.zCoord, Material.water) != null || ReikaWorldHelper.checkForAdjBlock(world, c.xCoord, c.yCoord, c.zCoord, ChromaBlocks.STRUCTSHIELD.getBlockInstance()) != null)
					return false;
			}
			if (ReikaObfuscationHelper.isDeObfEnvironment())
				ReikaJavaLibrary.pConsole(set1.size()+" @ "+x+", "+z);
			this.generate(world, rand, set1, oy);

			int r = 16;
			int spokes = ReikaRandomHelper.getRandomBetween(2, 6);
			double da = 360D/spokes;
			double a0 = rand.nextDouble()*360;
			double va = ReikaRandomHelper.getRandomBetween(2.5, 15);
			/*
			for (int i = -r; i <= r; i++) {
				for (int k = -r; k <= r; k++) {
					int dx = x+i;
					int dz = z+k;
					int d = Math.abs(i)+Math.abs(k);
					int dy = ReikaWorldHelper.findTopBlockBelowY(world, dx, oy+20, dz);
					double a = Math.atan2(k, i);
					double ang = Math.toDegrees(a)%da;
					double ta = (a0+va*d/2D)%da;
					boolean spoke = Math.abs(ang-ta) <= 10;
				}
			}*/
			for (int i = -r; i <= r; i++) {
				for (int k = -r; k <= r; k++) {
					int dx = x+i;
					int dz = z+k;
					double d = ReikaMathLibrary.py3d(i, 0, k);
					if (d <= r) {
						double f = Math.sqrt(1-d/r);
						if (rand.nextDouble() < f) {
							int dy = ReikaWorldHelper.findTopBlockBelowY(world, dx, oy+20, dz);
							Block b = world.getBlock(dx, dy, dz);
							while (b instanceof BlockLeavesBase || b == Blocks.air) {
								dy--;
								b = world.getBlock(dx, dy, dz);
							}
							if (b == Blocks.grass || b == Blocks.sand)
								world.setBlock(dx, dy, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), rand.nextBoolean() ? BlockType.MOSS.metadata : BlockType.STONE.metadata, 3);
						}
					}
				}
			}

			return true;
		}

		return false;
	}

	private void growFrom(World world, Random rand, double x0, double y0, double z0, double maxr, HashSet<Coordinate> parent, HashSet<Coordinate> path, int forkDepth, double minY) {
		minY = Math.max(0, Math.min(minY, y0-1));
		List<CavePoint> li0 = new ArrayList();
		double r0 = ReikaRandomHelper.getRandomBetween(MIN_RADIUS, maxr);
		li0.add(new CavePoint(x0, y0, z0, r0));
		boolean first = true;
		while (y0 > minY) {
			double x2 = ReikaRandomHelper.getRandomPlusMinus(x0, first ? 6 : 32);
			double z2 = ReikaRandomHelper.getRandomPlusMinus(z0, first ? 6 : 32);
			double y2 = first ? y0-8-rand.nextInt(5) : y0-rand.nextInt(16);
			y2 = Math.max(y2, minY);
			x0 = x2;
			y0 = y2;
			z0 = z2;
			li0.add(new CavePoint(x0, y0, z0, r0));
			first = false;
		}
		Spline s = new Spline(SplineType.CHORDAL);
		for (int i = 0; i < li0.size(); i++) {
			CavePoint pos = li0.get(i);
			s.addPoint(pos);
			if (i != 0 && i != li0.size()-1)
				s.addPoint(pos.offset(0, -1-rand.nextInt(6), 0));
		}
		List<DecimalPosition> li = s.get(12, false);
		Interpolation rVar = new Interpolation(false);
		int d = 0;
		while (d < li.size()) {
			rVar.addPoint(d, ReikaRandomHelper.getRandomBetween(MIN_RADIUS, maxr));
			int l = li.size()-d;
			d += 1+rand.nextInt(Math.max(4, l/6));
		}
		rVar.addPoint(li.size()-1, ReikaRandomHelper.getRandomBetween(MIN_RADIUS, maxr));
		for (int i = 0; i < li.size()-1; i++) {
			DecimalPosition pos1 = li.get(i);
			DecimalPosition pos2 = li.get(i+1);
			if (this.getLine(world, rand, pos1, pos2, rVar.getValue(i), rVar.getValue(i+1), i, parent, path) && forkDepth > 0 && i > 10) //prevent overtangling
				break;
			if (forkDepth <= 3 && rand.nextInt(Math.max(24, (int)pos1.yCoord)) == 0) {
				this.fork(world, rand, pos1, pos2, rVar, i, path, forkDepth);
			}
		}
		parent.addAll(path);
	}

	private void fork(World world, Random rand, DecimalPosition p1, DecimalPosition p2, Interpolation rVar, int idx, HashSet<Coordinate> set, int depth) {
		double f = rand.nextDouble();
		HashSet<Coordinate> ret = new HashSet();
		this.growFrom(world, rand, p1.xCoord+(p2.xCoord-p1.xCoord)*f, p1.yCoord+(p2.yCoord-p1.yCoord)*f, p1.zCoord+(p2.zCoord-p1.zCoord)*f, rVar.getValue(idx+f), set, ret, depth+1, rand.nextInt(3) == 0 ? 0 : ReikaRandomHelper.getRandomBetween(6, 20));
		set.addAll(ret);
	}

	private void generate(World world, Random rand, HashSet<Coordinate> set, int upper) {
		HashSet<Coordinate> falls = new HashSet();
		HashSet<Coordinate> air = new HashSet();
		for (Coordinate c : set) {
			if (c.yCoord < 0)
				continue;
			boolean top = c.yCoord == upper;
			boolean edge = c.yCoord <= upper && !set.containsAll(c.getAdjacentCoordinates());
			Block b = Blocks.air;
			int meta = 0;
			if (!top) {
				if (edge) {
					if (this.isBedrockWall(world, rand, c)) {
						b = Blocks.bedrock;
					}
					else {
						if (rand.nextInt(20) == 0) {
							b = ChromaBlocks.DIMGEN.getBlockInstance();
							meta = DimDecoTypes.GLOWCAVE.ordinal();
							for (Coordinate c2 : c.getAdjacentCoordinates()) {
								if (!set.contains(c2)) {
									c2.setBlock(world, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
								}
							}
						}
						else {
							b = ChromaBlocks.STRUCTSHIELD.getBlockInstance();
							meta = ReikaRandomHelper.doWithChance(20) ? BlockType.MOSS.metadata : BlockType.STONE.metadata;
						}
					}
				}/*
				if (/*!edge && *//*c.yCoord <= 10 && ReikaRandomHelper.doWithChance((11-c.yCoord)/10D)) {
					b = Blocks.air;
				}*/
				if (c.yCoord == 0) {
					b = Blocks.air;
				}
			}
			if (b == Blocks.air && rand.nextInt(c.yCoord >= 16 ? 160 : 10+10*c.yCoord) == 0) {
				b = ChromaBlocks.LIGHT.getBlockInstance();
				meta = c.yCoord > 12 ? Flags.PARTICLES.getFlag() : 0;
			}
			if (b == Blocks.air && rand.nextInt(60) == 0 && c.yCoord < upper-10 && !set.contains(c.offset(0, 2, 0))) {
				b = ChromaBlocks.TIEREDPLANT.getBlockInstance();
				meta = TieredPlants.CAVE.ordinal();
			}
			/*
			if (b == Blocks.stone) {
				if (rand.nextInt(3) == 0) {
					OreType ore = GlowingCliffsAuxGenerator.instance.oreRand.getRandomEntry();
					ItemStack is = ore.getFirstOreBlock();
					b = Block.getBlockFromItem(is.getItem());
					meta = is.getItemDamage();
				}
				else if (rand.nextInt(4) == 0) {
					b = ChromaBlocks.TIEREDORE.getBlockInstance();
					meta = rand.nextInt(TieredOres.list.length);
					while (TieredOres.list[meta].genBlock != Blocks.stone)
						meta = rand.nextInt(TieredOres.list.length);
				}
				else {

				}
			}*/
			if (b == Blocks.bedrock) {
				if (c.yCoord < MAX_CRACK_Y) {
					double f = this.getBedrockCrackChance(c);
					if (rand.nextDouble() < f) {
						b = ChromaBlocks.BEDROCKCRACK.getBlockInstance();
						meta = this.getRandomCrackMeta(rand, c);
						for (Coordinate c2 : c.getAdjacentCoordinates()) {
							if (!set.contains(c2)) {
								c2.setBlock(world, Blocks.bedrock);
							}
						}
					}
				}
			}
			if (b == Blocks.bedrock && c.yCoord == 2) {
				falls.add(c);
			}
			c.setBlock(world, b, meta);
			if (b == Blocks.air) {
				air.add(c);
			}
		}
		for (Coordinate c : falls) {
			if (air.contains(c.offset(0, 1, 0)))
				continue;
			int meta = 0;
			for (int i = 2; i < 6; i++) {
				ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
				Coordinate c2 = c.offset(dir, 1);
				if (air.contains(c2) && air.contains(c2.offset(0, -1, 0))) {
					meta |= 1 << (i-2);
				}
			}
			if (meta > 0)
				c.setBlock(world, ChromaBlocks.VOIDCAVE.getBlockInstance(), meta);
		}
	}

	private int getRandomCrackMeta(Random rand, Coordinate c) {
		int max = 9;
		if (c.yCoord > 8) {
			max *= 1D-((c.yCoord-8)/(MAX_CRACK_Y-8D));
		}
		return rand.nextInt(1+max);
	}

	private double getBedrockCrackChance(Coordinate c) {
		if (c.yCoord <= 8)
			return MAX_CRACK_CHANCE;
		double f = (c.yCoord-8)/(MAX_CRACK_Y-8D);
		return (1-f)*MAX_CRACK_CHANCE;
	}

	private boolean isBedrockWall(World world, Random rand, Coordinate c) {
		//was c.yCoord <= 27 && ReikaRandomHelper.doWithChance((28-c.yCoord)/28D)
		if (c.yCoord >= MAX_BEDROCK_Y)
			return false;
		if (c.yCoord <= FULL_BEDROCK_Y)
			return true;
		return c.yCoord <= ReikaMathLibrary.normalizeToBounds(wallSelectionNoise.getValue(c.xCoord, c.zCoord), FULL_BEDROCK_Y, MAX_BEDROCK_Y);
	}

	private boolean getLine(World world, Random rand, DecimalPosition pos1, DecimalPosition pos2, double r1, double r2, int i, HashSet<Coordinate> main, HashSet<Coordinate> line) {
		double dx = pos2.xCoord-pos1.xCoord;
		double dy = pos2.yCoord-pos1.yCoord;
		double dz = pos2.zCoord-pos1.zCoord;
		double dd = ReikaMathLibrary.py3d(dx, dy, dz);
		double dr = r2-r1;
		boolean flag = false;
		for (double d = 0.25; d < dd; d += 1) {
			double f = d/dd;
			double x = pos1.xCoord+f*dx;
			double y = pos1.yCoord+f*dy;
			double z = pos1.zCoord+f*dz;
			double r = r1+dr*f;
			flag |= this.getSphere(world, x, y, z, r, i, main, line);
		}
		return flag;
	}

	private boolean getSphere(World world, double x, double y, double z, double r, int i, HashSet<Coordinate> main, HashSet<Coordinate> line) {
		boolean flag = false;
		for (double dx = -r; dx <= r; dx += 0.75) {
			for (double dy = -r; dy <= r; dy += 0.75) {
				for (double dz = -r; dz <= r; dz += 0.75) {
					if (dx*dx+dy*dy+dz*dz <= r*r) {
						int x0 = MathHelper.floor_double(x+dx);
						int y0 = MathHelper.floor_double(y+dy);
						int z0 = MathHelper.floor_double(z+dz);
						Coordinate c = new Coordinate(x0, y0, z0);
						line.add(c);
						flag |= main.contains(c);
						//CaveBlock cv = new CaveBlock(x0, y0, z0, i);
						//if (!set.contains(cv))
						//set.add(cv);
					}
				}
			}
		}
		return flag;
	}

	private static class CaveBlock {

		private final Coordinate location;
		private final int depthIndex;

		private CaveBlock(int x, int y, int z, int i) {
			this(new Coordinate(x, y, z), i);
		}

		private CaveBlock(Coordinate c, int i) {
			location = c;
			depthIndex = i;
		}

		@Override
		public int hashCode() {
			return location.hashCode();
		}

		@Override
		public boolean equals(Object o) {
			return o instanceof CaveBlock && ((CaveBlock)o).location.equals(location);
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

	}

}
