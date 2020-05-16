package Reika.ChromatiCraft.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap.CollectionType;
import Reika.DragonAPI.Instantiable.Math.LobulatedCurve;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaPlantHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaTreeHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

public class EnderOakGenerator extends WorldGenAbstractTree {

	public final int minTrunkBaseHeight;
	public final int maxTrunkBaseHeight;
	public final int minFoliageHeight;
	public final int maxFoliageHeight;
	public final int minFoliageRadius;
	public final int maxFoliageRadius;
	public final float branchChancePerLevel;
	public final int maxBranchLength;
	public final float columnChancePerLeaf;

	public EnderOakGenerator(int h0, int h1, int f0, int f1, int fr0, int fr1, float b, int bl, float c) {
		super(false);
		minTrunkBaseHeight = h0;
		maxTrunkBaseHeight = h1;
		minFoliageHeight = f0;
		maxFoliageHeight = f1;
		minFoliageRadius = fr0;
		maxFoliageRadius = fr1;
		branchChancePerLevel = b;
		maxBranchLength = bl;
		columnChancePerLeaf = c;
	}

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {
		if (!ReikaPlantHelper.SAPLING.canPlantAt(world, x, y, z))
			return false;
		int trunk = ReikaRandomHelper.getRandomBetween(minTrunkBaseHeight, maxTrunkBaseHeight, rand);
		int leaf = ReikaRandomHelper.getRandomBetween(minFoliageHeight, maxFoliageHeight, rand);
		Tree tree = new Tree(rand, trunk, leaf);
		tree.calculate(world, rand, x, y, z);
		if (tree.canPlace(world)) {
			tree.place(world);
			return true;
		}
		return false;
	}

	private class Tree {

		private final HashSet<Coordinate> logs = new HashSet();
		private final MultiMap<Integer, Coordinate> leaves = new MultiMap(CollectionType.HASHSET);

		private final HashMap<Coordinate, Branch> branches = new HashMap();

		private final int trunkHeight;
		private final int leafHeight;
		private final int totalHeight;

		private int lowestLeafY = 999;

		private final LobulatedCurve leafBottom;
		private final LobulatedCurve leafMiddle;
		private final LobulatedCurve leafTop;

		private Tree(Random rand, int ht, int hl) {
			trunkHeight = ht;
			leafHeight = hl;
			totalHeight = ht+hl;

			leafBottom = LobulatedCurve.fromMinMaxRadii(minFoliageRadius, Math.max(minFoliageRadius, maxFoliageRadius*0.8), 3);
			leafMiddle = LobulatedCurve.fromMinMaxRadii(minFoliageRadius, maxFoliageRadius, 3);
			leafTop = LobulatedCurve.fromMinMaxRadii(minFoliageRadius, Math.max(minFoliageRadius, maxFoliageRadius*0.5), 3);
			leafBottom.generate(rand);
			leafMiddle.generate(rand);
			leafTop.generate(rand);
		}

		private void calculate(World world, Random rand, int x, int y, int z) {
			for (int h = 0; h <= totalHeight; h++) {
				Coordinate core = new Coordinate(x, y+h, z);
				if (h >= totalHeight-1)
					leaves.addValue(core.yCoord, core);
				else
					logs.add(core);
				if (h > trunkHeight && h < totalHeight) {
					this.generateLayer(world, rand, x, y, z, h, core);
				}
			}
			if (branchChancePerLevel > 0) {
				for (int yl : leaves.keySet()) {
					int dh = y+totalHeight-yl;
					if (dh > 2 && rand.nextFloat() < branchChancePerLevel) {
						Coordinate c = new Coordinate(x, yl, z);
						float theta = (float)ReikaRandomHelper.getRandomPlusMinus(0F, 30F, rand);
						theta = Math.min(theta, Math.max(0, 10*(dh-3)));
						branches.put(c, new Branch(c, rand.nextFloat()*360, theta));
					}
				}
				if (!branches.isEmpty()) {
					for (Branch b : branches.values()) {
						b.calculate(world, rand);
						logs.addAll(b.logs);
						Collection<Coordinate> li = new ArrayList(b.leaves);
						li.removeAll(logs);
						for (Coordinate c : li) {
							leaves.addValue(c.yCoord, c);
						}
					}
				}
			}
			if (columnChancePerLeaf > 0) {
				Collection<Coordinate> li = leaves.get(lowestLeafY);
				HashSet<Coordinate> leavesToAdd = new HashSet();
				for (Coordinate c : li) {
					if (rand.nextFloat() < columnChancePerLeaf) {
						for (int y2 = lowestLeafY; y2 >= 0; y2--) {
							if (this.canReplace(world, c.xCoord, y2, c.zCoord)) {
								Coordinate c2 = new Coordinate(c.xCoord, y2, c.zCoord);
								leavesToAdd.add(c2);
							}
							else {
								break;
							}
						}
					}
				}
				for (Coordinate c : leavesToAdd) {
					leaves.addValue(c.yCoord, c);
				}
			}

		}

		private void generateLayer(World world, Random rand, int x, int y, int z, int h, Coordinate core) {
			for (int i = -maxFoliageRadius-2; i <= maxFoliageRadius+2; i++) {
				for (int k = -maxFoliageRadius-2; k <= maxFoliageRadius+2; k++) {
					double r = this.getRadiusAt(h, i, k);
					double d = ReikaMathLibrary.py3d(i, 0, k);
					if (d <= r+0.5) {
						Coordinate c = new Coordinate(x+i, core.yCoord, z+k);
						leaves.addValue(c.yCoord, c);
						lowestLeafY = Math.min(lowestLeafY, c.yCoord);
					}
				}
			}
		}

		private double getRadiusAt(int h, int i, int k) {
			double midY = leafHeight/2D;
			double dy = h-trunkHeight;
			int dh = totalHeight-h;
			LobulatedCurve l1 = null;
			LobulatedCurve l2 = null;
			double f = 0;
			if (h < midY) {
				l1 = leafBottom;
				l2 = leafMiddle;
				f = h/midY;
			}
			else {
				l1 = leafMiddle;
				l2 = leafTop;
				f = (h-midY)/midY;
			}
			double ang = Math.toDegrees(Math.atan2(i, k));
			double r1 = l1.getRadius(ang);
			double r2 = l2.getRadius(ang);
			double r = r2*f+r1*(1-f);
			r = MathHelper.clamp_double(r, minFoliageRadius, maxFoliageRadius);
			r = Math.min(r, dh);
			return r;
		}

		private boolean canPlace(World world) {
			for (Coordinate c : logs) {
				boolean canPlace = this.canReplace(world, c.xCoord, c.yCoord, c.zCoord);
				if (!canPlace)
					return false;
			}
			int minLeaves = (int)(leaves.totalSize()*0.75);
			int placeable = 0;
			for (Coordinate c : leaves.allValues(false)) {
				boolean canPlace = this.canReplace(world, c.xCoord, c.yCoord, c.zCoord);
				if (canPlace)
					placeable++;
			}
			return placeable >= minLeaves;
		}

		private boolean canReplace(World world, int x, int y, int z) {
			if (ReikaWorldHelper.softBlocks(world, x, y, z))
				return true;
			Block b = world.getBlock(x, y, z);
			return b.canBeReplacedByLeaves(world, x, y, z) || EnderOakGenerator.super.isReplaceable(world, x, y, z);
		}

		private void place(World world) {
			for (Coordinate c : logs) {
				c.setBlock(world, ReikaTreeHelper.OAK.getLogID(), ReikaTreeHelper.OAK.getBaseLogMeta(), 2);
			}
			for (Coordinate c : leaves.allValues(false)) {
				if (logs.contains(c))
					continue;
				c.setBlock(world, ReikaTreeHelper.OAK.getLeafID(), ReikaTreeHelper.OAK.getBaseLeafMeta(), 2);
			}
		}

		private class Branch {

			private final Coordinate root;

			private final HashSet<Coordinate> logs = new HashSet();
			private final HashSet<Coordinate> leaves = new HashSet();

			private final double xStep;
			private final double yStep;
			private final double zStep;

			private Branch(Coordinate c, float phi, float theta) {
				root = c;
				double[] xyz = ReikaPhysicsHelper.polarToCartesian(1, theta, phi);
				xStep = xyz[0];
				yStep = xyz[1];
				zStep = xyz[2];
			}

			private void calculate(World world, Random rand) {
				int l = Math.min((int)(leafHeight*0.67), ReikaRandomHelper.getRandomBetween(2, maxBranchLength));
				for (double d = 0.5; d <= l; d += 0.5) {
					double dx = root.xCoord+0.5+xStep*d;
					double dy = root.yCoord+0.5+yStep*d;
					double dz = root.zCoord+0.5+zStep*d;
					Coordinate core = new Coordinate(dx, dy, dz);
					logs.add(core);
					int n = ReikaRandomHelper.getRandomBetween(8, 20, rand);
					for (int i = 0; i < n; i++) {
						double dx2 = ReikaRandomHelper.getRandomPlusMinus(dx, 1.5);
						double dy2 = ReikaRandomHelper.getRandomPlusMinus(dy, 1.5);
						double dz2 = ReikaRandomHelper.getRandomPlusMinus(dz, 1.5);
						Coordinate c = new Coordinate(dx2, dy2, dz2);
						if (!logs.contains(c))
							leaves.add(c);
					}
				}
			}

		}

	}

}
