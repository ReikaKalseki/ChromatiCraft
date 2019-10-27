package Reika.ChromatiCraft.World.Dimension.Structure.RayBlend;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Base.DynamicStructurePiece;
import Reika.ChromatiCraft.Block.Worldgen.BlockLootChest;
import Reika.ChromatiCraft.Block.Worldgen.BlockLootChest.TileEntityLootChest;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.Structure.RayBlendGenerator;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;
import Reika.DragonAPI.Libraries.Java.ReikaArrayHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;


public class RayBlendEntrance extends DynamicStructurePiece<RayBlendGenerator> {

	public RayBlendEntrance(RayBlendGenerator s) {
		super(s);
	}

	@Override
	public void generate(World world, int x, int z) {
		int y1 = parent.getPosY()+2;

		x -= EntranceLevel.GRID_RADIUS*EntranceLevel.CELL_SPACING+2;

		Random rand = new Random();

		int y2 = world.getTopSolidOrLiquidBlock(x, z);
		int y = y1;
		EntranceLevel last = null;
		int dx = 0;
		int dz = 0;
		int floor = 0;
		while (y+EntranceLevel.HEIGHT+EntranceLevel.SEP < y2) {
			int sX = last != null ? last.shaftUp.x : Integer.MIN_VALUE;
			int sZ = last != null ? last.shaftUp.y : Integer.MIN_VALUE;
			EntranceLevel el = new EntranceLevel(rand, floor, x, z, sX, sZ, EntranceLevel.rand(), EntranceLevel.rand());
			floor++;
			el.generate(world, x, y, z);
			y += EntranceLevel.HEIGHT+EntranceLevel.SEP;
			last = el;
			dx = x+last.getUpperShaftXCenterOffset();
			dz = z+last.getUpperShaftZCenterOffset();
		}

		while (y <= y2) {
			EntranceLevel.generateShaft(world, dx, y, dz);
			y++;
		}

		for (int i = -Cell.TOTAL_RADIUS-1; i <= Cell.TOTAL_RADIUS+1; i++) {
			for (int k = -Cell.TOTAL_RADIUS-1; k <= Cell.TOTAL_RADIUS+1; k++) {
				if (Math.abs(i) == Cell.TOTAL_RADIUS+1 || Math.abs(k) == Cell.TOTAL_RADIUS+1) {
					world.setBlock(dx+i, y2-1, dz+k, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.COBBLE.metadata, 3);
				}
			}
		}

		for (int i = 0; i <= 4; i++) {
			int m = i == 4 ? BlockType.LIGHT.metadata : BlockType.STONE.metadata;
			world.setBlock(dx-Cell.TOTAL_RADIUS/2-1, y2+i, dz+Cell.TOTAL_RADIUS/2+1, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), m, 3);
			world.setBlock(dx-Cell.TOTAL_RADIUS/2-1, y2+i, dz-Cell.TOTAL_RADIUS/2-1, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), m, 3);
			world.setBlock(dx+Cell.TOTAL_RADIUS/2+1, y2+i, dz+Cell.TOTAL_RADIUS/2+1, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), m, 3);
			world.setBlock(dx+Cell.TOTAL_RADIUS/2+1, y2+i, dz-Cell.TOTAL_RADIUS/2-1, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), m, 3);
		}

		for (int i = -1; i <= 2; i++) {
			int m = i == 2 ? BlockType.LIGHT.metadata : BlockType.COBBLE.metadata;
			world.setBlock(dx-Cell.TOTAL_RADIUS*2-1, y2+i, dz+Cell.TOTAL_RADIUS*2+1, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), m, 3);
			world.setBlock(dx-Cell.TOTAL_RADIUS*2-1, y2+i, dz-Cell.TOTAL_RADIUS*2-1, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), m, 3);
			world.setBlock(dx+Cell.TOTAL_RADIUS*2+1, y2+i, dz+Cell.TOTAL_RADIUS*2+1, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), m, 3);
			world.setBlock(dx+Cell.TOTAL_RADIUS*2+1, y2+i, dz-Cell.TOTAL_RADIUS*2-1, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), m, 3);
			if (i == -1) {
				for (int a = -1; a <= 1; a++) {
					for (int b = -1; b <= 1; b++) {
						if (a == 0 || b == 0) {
							world.setBlock(dx-Cell.TOTAL_RADIUS*2-1+a, y2+i, dz+Cell.TOTAL_RADIUS*2+1+b, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.CLOAK.metadata, 3);
							world.setBlock(dx-Cell.TOTAL_RADIUS*2-1+a, y2+i, dz-Cell.TOTAL_RADIUS*2-1+b, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.CLOAK.metadata, 3);
							world.setBlock(dx+Cell.TOTAL_RADIUS*2+1+a, y2+i, dz+Cell.TOTAL_RADIUS*2+1+b, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.CLOAK.metadata, 3);
							world.setBlock(dx+Cell.TOTAL_RADIUS*2+1+a, y2+i, dz-Cell.TOTAL_RADIUS*2-1+b, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.CLOAK.metadata, 3);
						}
					}
				}
			}
		}
	}

	private static class EntranceLevel {

		private static final int HEIGHT = 5;
		private static final int SEP = 3;
		private static final int GRID_RADIUS = 1;
		private static final int CELL_SPACING = Cell.HOLE_RADIUS*2+2;

		private final HashMap<Point, Cell> cells = new HashMap();

		private final Point shaftDown;
		private final Point shaftUp;

		private final int floorIndex;

		private final ArrayList<Point> path = new ArrayList();
		//private final MultiMap<Point, ForgeDirection> openSides = new MultiMap(CollectionType.HASHSET);

		private EntranceLevel(Random rand, int fn, int rx, int rz, int xd, int zd, int xu, int zu) {
			floorIndex = fn;
			shaftDown = xd != Integer.MIN_VALUE ? new Point(xd, zd) : null;
			shaftUp = new Point(xu, zu);

			for (int i = -GRID_RADIUS; i <= GRID_RADIUS; i++) {
				for (int k = -GRID_RADIUS; k <= GRID_RADIUS; k++) {
					int x = rx+i*CELL_SPACING;
					int z = rz+k*CELL_SPACING;
					cells.put(new Point(i, k), new Cell(i, k, x, z));
				}
			}

			this.calculatePath(rand);

			if (shaftDown != null)
				cells.get(shaftDown).openFloor = true;
			cells.get(shaftUp).openCeiling = true;

			int n1 = GRID_RADIUS*GRID_RADIUS/2;
			int n2 = Math.max(1, GRID_RADIUS*GRID_RADIUS*2/3);
			int n = ReikaRandomHelper.getRandomBetween(n1, n2);
			int amt = 0;
			for (Cell c : cells.values()) {
				if (c.canHaveLoot()) {
					c.hasLoot = true;
					amt++;
				}
				if (amt >= n)
					break;
			}
		}

		private void calculatePath(Random rand) {
			Point p = shaftUp;
			Point end = shaftDown != null ? shaftDown : new Point(GRID_RADIUS, 0);
			int dx = p.x-end.x;
			int dz = p.y-end.y;
			while (dx != 0 || dz != 0) {
				path.add(p);
				boolean moveX = dx != 0 && dz != 0 ? rand.nextBoolean() : dx != 0;
				int sx = moveX ? -(int)Math.signum(dx) : 0;
				int sz = moveX ? 0 : -(int)Math.signum(dz);
				ReikaJavaLibrary.pConsole("At "+p+", moving "+sx+", "+sz+", towards "+end+" to ");
				p = new Point(p.x+sx, p.y+sz);
				ReikaJavaLibrary.pConsole(p.toString());
				dx = p.x-end.x;
				dz = p.y-end.y;
				ReikaJavaLibrary.pConsole("dx,dz = "+dx+", "+dz);
			}
			if (!p.equals(end)) {
				throw new RuntimeException("Path "+path+" ended early at "+p+", before "+end);
			}
			path.add(end);

			for (int i = 1; i < path.size()-1; i++) {
				Point p0 = path.get(i-1);
				Point p1 = path.get(i);
				Point p2 = path.get(i+1);
				this.connect(p0, p1);
				this.connect(p1, p2);
			}

			HashSet<Point> unconnected = new HashSet(cells.keySet());
			unconnected.removeAll(path);

			while (!unconnected.isEmpty()) {
				Point pr = ReikaJavaLibrary.getRandomCollectionEntry(rand, unconnected);
				boolean pathed = false;
				while (!pathed) {
					Point next = this.getValidRandomNeighbor(pr, rand);
					this.connect(pr, next);
					if (!unconnected.contains(next)) {
						unconnected.remove(pr);
						pathed = true;
					}
					else {
						pr = next;
					}
				}
			}
		}

		private Point getValidRandomNeighbor(Point pr, Random rand) {
			ArrayList<ForgeDirection> li = ReikaDirectionHelper.getRandomOrderedDirections(false);
			while (!li.isEmpty()) {
				int idx = rand.nextInt(li.size());
				ForgeDirection dir = li.remove(idx);
				Point p2 = new Point(pr.x+dir.offsetX, pr.y+dir.offsetZ);
				if (this.isInGrid(p2)) {
					return p2;
				}
			}
			return null;
		}

		private boolean isInGrid(Point p) {
			return Math.abs(p.x) <= GRID_RADIUS && Math.abs(p.y) <= GRID_RADIUS;
		}

		private void connect(Point p1, Point p2) {
			ForgeDirection side = ReikaDirectionHelper.getDirectionBetween(p1, p2);
			if (side == null)
				throw new IllegalArgumentException("You cannot link two non-adjacent points "+p1+", "+p2+" from "+path);
			Cell c1 = cells.get(p1);
			Cell c2 = cells.get(p2);
			c1.sides[side.ordinal()-2] = true;
			c2.sides[side.getOpposite().ordinal()-2] = true;
		}

		private void generate(World world, int x, int y, int z) {
			int r = CELL_SPACING*GRID_RADIUS+3;
			/*
			int r = Cell.TOTAL_RADIUS/2+Cell.TOTAL_RADIUS+3;
			int fx1 = x+this.getLowerShaftXCenterOffset()-1;
			int fx2 = x+this.getLowerShaftXCenterOffset()+1;
			int fz1 = z+this.getLowerShaftZCenterOffset()-1;
			int fz2 = z+this.getLowerShaftZCenterOffset()+1;
			int cx1 = x+this.getUpperShaftXCenterOffset()-1;
			int cx2 = x+this.getUpperShaftXCenterOffset()+1;
			int cz1 = z+this.getUpperShaftZCenterOffset()-1;
			int cz2 = z+this.getUpperShaftZCenterOffset()+1;
			 */

			for (int i = -r; i <= r; i++) {
				for (int k = -r; k <= r; k++) {
					int dx = x+i;
					int dz = z+k;
					world.setBlock(dx, y, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.COBBLE.metadata, 3);
					world.setBlock(dx, y+HEIGHT, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.COBBLE.metadata, 3);

					for (int h = 1; h < HEIGHT; h++)
						world.setBlock(dx, y+h, dz, Blocks.air);
				}
			}

			/*
			for (int i = -r; i <= r; i++) {
				for (int k = -r; k <= r; k++) {
					int dx = x+i;
					int dz = z+k;
					int m = BlockType.STONE.metadata;
					boolean airFloor = dx >= fx1 && dx <= fx2 && dz >= fz1 && dz <= fz2;
					boolean airCeil = dx >= cx1 && dx <= cx2 && dz >= cz1 && dz <= cz2;
					if (airFloor)
						world.setBlock(dx, y, dz, Blocks.air);
					else
						world.setBlock(dx, y, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), m, 3);
					if (airCeil)
						world.setBlock(dx, y+HEIGHT, dz, Blocks.air);
					else
						world.setBlock(dx, y+HEIGHT, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), m, 3);
				}
			}*/
			/*
			for (int i = -GRID; i <= GRID; i++) {
				for (int k = -GRID; k <= GRID; k++) {
					int cellX = x+i*(CELL+1);
					int cellZ = z+k*(CELL+1);
					int r1 = CELL;
					int r2 = CELL+1;
					for (int i2 = -r1; i2 <= r1; i2++) {
						for (int k2 = -r1; k2 <= r1; k2++) {
							int dx = x+i2+r2*i;
							int dz = z+k2+r2*k;
							if (Math.abs(i2) < r2 && Math.abs(k2) < r2) {
								if (cellX == shaftDownX && cellZ == shaftDownZ) {
									world.setBlock(dx, y, dz, Blocks.air);
								}
								else {
									world.setBlock(dx, y, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata, 3);
								}
								if (cellX == shaftUpX && cellZ == shaftUpZ) {
									world.setBlock(dx, y+HEIGHT, dz, Blocks.air);
								}
								else {
									world.setBlock(dx, y+HEIGHT, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata, 3);
								}
							}
							else {
								world.setBlock(dx, y, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.COBBLE.metadata, 3);
								world.setBlock(dx, y+HEIGHT, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.COBBLE.metadata, 3);
							}
						}
					}
				}
			}
			 */
			for (Cell c : cells.values())
				c.generate(world, y);
			for (int h = 1; h < HEIGHT; h++) {
				for (int d = -r; d <= r; d++) {
					world.setBlock(x+d, y+h, z-r, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), world.rand.nextInt(12) == 0 ? BlockType.LIGHT.metadata : BlockType.STONE.metadata, 3);
					world.setBlock(x+d, y+h, z+r, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), world.rand.nextInt(12) == 0 ? BlockType.LIGHT.metadata : BlockType.STONE.metadata, 3);
					world.setBlock(x-r, y+h, z+d, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), world.rand.nextInt(12) == 0 ? BlockType.LIGHT.metadata : BlockType.STONE.metadata, 3);
					world.setBlock(x+r, y+h, z+d, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), world.rand.nextInt(12) == 0 ? BlockType.LIGHT.metadata : BlockType.STONE.metadata, 3);
				}

				if (floorIndex == 0) {
					for (int i = -1; i <= 1; i++) {
						for (int d = 1; d <= 3; d++) {
							world.setBlock(x+r, y+d, z+i, Blocks.air);
						}
						world.setBlock(x+r-1, y+4, z+i, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.COBBLE.metadata, 3);
					}
				}

				for (int i = -GRID_RADIUS-1; i <= GRID_RADIUS; i++) {
					for (int k = -GRID_RADIUS-1; k <= GRID_RADIUS; k++) {
						world.setBlock(x+i*CELL_SPACING+Cell.TOTAL_RADIUS, y+h, z+k*CELL_SPACING+Cell.TOTAL_RADIUS, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.COBBLE.metadata, 3);
					}
				}
			}

			world.setBlock(x+Cell.TOTAL_RADIUS/2+1, y+HEIGHT/2, z+Cell.TOTAL_RADIUS/2+1, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.LIGHT.metadata, 3);
			world.setBlock(x+Cell.TOTAL_RADIUS/2+1, y+HEIGHT/2, z-Cell.TOTAL_RADIUS/2-1, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.LIGHT.metadata, 3);
			world.setBlock(x-Cell.TOTAL_RADIUS/2-1, y+HEIGHT/2, z+Cell.TOTAL_RADIUS/2+1, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.LIGHT.metadata, 3);
			world.setBlock(x-Cell.TOTAL_RADIUS/2-1, y+HEIGHT/2, z-Cell.TOTAL_RADIUS/2-1, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.LIGHT.metadata, 3);

			for (int i = HEIGHT+1; i <= HEIGHT+SEP; i++) {
				this.generateShaft(world, x+this.getUpperShaftXCenterOffset(), y+i, z+this.getUpperShaftZCenterOffset());
			}
		}

		private int getLowerShaftXCenterOffset() {
			return shaftDown.x*CELL_SPACING;
		}

		private int getLowerShaftZCenterOffset() {
			return shaftDown.y*CELL_SPACING;
		}

		private int getUpperShaftXCenterOffset() {
			return shaftUp.x*CELL_SPACING;
		}

		private int getUpperShaftZCenterOffset() {
			return shaftUp.y*CELL_SPACING;
		}

		private static int rand() {
			return ReikaRandomHelper.getRandomPlusMinus(0, GRID_RADIUS);
		}

		private static void generateShaft(World world, int x, int y, int z) {
			int r = Cell.TOTAL_RADIUS/2+1;
			for (int i = -r; i <= r; i++) {
				for (int k = -r; k <= r; k++) {
					if (Math.abs(i) == r || Math.abs(k) == r) {
						world.setBlock(x+i, y, z+k, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata, 3);
					}
					else {
						world.setBlock(x+i, y, z+k, Blocks.air);
					}
				}
			}
		}

	}

	private static class Cell {

		private static final int HOLE_RADIUS = 1;
		private static final int TOTAL_RADIUS = 2;

		private final int xPos;
		private final int zPos;

		private final int rootX;
		private final int rootZ;

		private boolean openCeiling;
		private boolean openFloor;

		private boolean hasLoot;

		private boolean[] sides = new boolean[4];

		private Cell(int x, int z, int rx, int rz) {
			xPos = x;
			zPos = z;

			rootX = rx;
			rootZ = rz;

			//ReikaJavaLibrary.pConsole(rx+", "+rz);
		}

		public boolean canHaveLoot() {
			return !openCeiling && !openFloor && this.countWalls() == 3;
		}

		private int countWalls() {
			return 4-ReikaArrayHelper.countTrue(sides);
		}

		private Cell getNeighbor(EntranceLevel el, ForgeDirection dir) {
			int dx = xPos+dir.offsetX;
			int dz = zPos+dir.offsetZ;
			return el.cells.get(new Point(dx, dz));
		}

		private void generate(World world, int y) {
			int r = HOLE_RADIUS;//TOTAL_RADIUS;
			for (int i = -r; i <= r; i++) {
				for (int k = -r; k <= r; k++) {
					int dx = this.getCenterX(true)+i;
					int dz = this.getCenterZ(true)+k;
					if (Math.abs(i) <= HOLE_RADIUS && Math.abs(k) <= HOLE_RADIUS) {
						if (openFloor) {
							world.setBlock(dx, y, dz, Blocks.air);
						}
						else {
							world.setBlock(dx, y, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata, 3);
						}

						if (openCeiling) {
							world.setBlock(dx, y+EntranceLevel.HEIGHT, dz, Blocks.air);
						}
						else {
							world.setBlock(dx, y+EntranceLevel.HEIGHT, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata, 3);
						}
					}
					else {
						world.setBlock(dx, y, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.COBBLE.metadata, 3);
					}
				}
			}

			for (int d = 0; d < 4; d++) {
				ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[2+d];
				int dx = this.getCenterX(true)+dir.offsetX*Cell.TOTAL_RADIUS;
				int dz = this.getCenterZ(true)+dir.offsetZ*Cell.TOTAL_RADIUS;
				for (int h = 1; h < EntranceLevel.HEIGHT; h++) {
					for (int a = -HOLE_RADIUS; a <= HOLE_RADIUS; a++) {
						Block b = sides[d] ? Blocks.air : ChromaBlocks.STRUCTSHIELD.getBlockInstance();
						int m = sides[d] ? 0 : BlockType.GLASS.metadata;
						if (dir.offsetX == 0) {
							world.setBlock(dx+a, y+h, dz, b, m, 3);
						}
						else {
							world.setBlock(dx, y+h, dz+a, b, m, 3);
						}
					}
				}
			}

			if (hasLoot) {
				int x = this.getCenterX(true);
				int z = this.getCenterZ(true);
				for (int i = -1; i <= 1; i++) {
					for (int k = -1; k <= 1; k++) {
						int m = i == 0 && k == 0 ? BlockType.LIGHT.metadata : BlockType.CLOAK.metadata;
						world.setBlock(x+i, y, z+k, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), m, 3);
					}
				}
				ForgeDirection dir = ForgeDirection.UNKNOWN;
				for (int i = 0; i < sides.length; i++) {
					if (sides[i]) {
						dir = ForgeDirection.VALID_DIRECTIONS[2+i];
						break;
					}
				}
				this.generateLootChest(world, x, y+1, z, dir);
			}
		}

		private void generateLootChest(World world, int x, int y, int z, ForgeDirection dir) {
			world.setBlock(x, y, z, ChromaBlocks.LOOTCHEST.getBlockInstance(), BlockLootChest.getMeta(dir), 3);
			TileEntity te = world.getTileEntity(x, y, z);
			if (te instanceof TileEntityLootChest) {
				TileEntityLootChest tc = (TileEntityLootChest)te;
				tc.populateChest(ChestGenHooks.STRONGHOLD_CROSSING, null, 0, world.rand);
			}
		}

		private int getCenterX(boolean real) {
			return real ? rootX : xPos*(EntranceLevel.CELL_SPACING+1);
		}

		private int getCenterZ(boolean real) {
			return real ? rootZ : zPos*(EntranceLevel.CELL_SPACING+1);
		}

	}

}
