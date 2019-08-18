package Reika.ChromatiCraft.World.Dimension.Structure.RayBlend;

import java.awt.Point;
import java.util.HashMap;

import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Base.DynamicStructurePiece;
import Reika.ChromatiCraft.Block.Worldgen.BlockLootChest.TileEntityLootChest;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.Structure.RayBlendGenerator;
import Reika.DragonAPI.DragonAPICore;
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

		int y2 = world.getTopSolidOrLiquidBlock(x, z);
		int y = y1;
		EntranceLevel last = null;
		int dx = 0;
		int dz = 0;
		int floor = 0;
		while (y+EntranceLevel.HEIGHT+EntranceLevel.SEP < y2) {
			int sX = last != null ? last.shaftUpX : Integer.MIN_VALUE;
			int sZ = last != null ? last.shaftUpZ : Integer.MIN_VALUE;
			EntranceLevel el = new EntranceLevel(floor, x, z, sX, sZ, EntranceLevel.rand(), EntranceLevel.rand());
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

		private final HashMap<Point, Cell> floorCells = new HashMap();
		private final HashMap<Point, Cell> ceilCells = new HashMap();

		private final int shaftDownX;
		private final int shaftDownZ;
		private final int shaftUpX;
		private final int shaftUpZ;

		private final int floorIndex;

		private EntranceLevel(int fn, int rx, int rz, int xd, int zd, int xu, int zu) {
			floorIndex = fn;
			shaftDownX = xd;
			shaftDownZ = zd;
			shaftUpX = xu;
			shaftUpZ = zu;

			for (int i = -GRID_RADIUS; i <= GRID_RADIUS; i++) {
				for (int k = -GRID_RADIUS; k <= GRID_RADIUS; k++) {
					int x = rx+i*CELL_SPACING;
					int z = rz+k*CELL_SPACING;
					floorCells.put(new Point(i, k), new Cell(i, k, x, z));
					ceilCells.put(new Point(i, k), new Cell(i, k, x, z));
				}
			}

			Cell p = floorCells.get(new Point(shaftDownX, shaftDownZ));
			if (p != null)
				p.isOpen = true;
			ceilCells.get(new Point(shaftUpX, shaftUpZ)).isOpen = true;

			int n1 = GRID_RADIUS*GRID_RADIUS/2;
			int n2 = Math.max(1, GRID_RADIUS*GRID_RADIUS*2/3);
			int n = ReikaRandomHelper.getRandomBetween(n1, n2);
			for (int i = 0; i < n; i++) {
				Cell c = ReikaJavaLibrary.getRandomCollectionEntry(DragonAPICore.rand, floorCells.values());
				if (!c.isOpen && !ceilCells.get(new Point(c.xPos, c.zPos)).isOpen)
					c.hasLoot = true;
			}
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
			for (Cell c : floorCells.values())
				c.generate(world, y);
			for (Cell c : ceilCells.values())
				c.generate(world, y+HEIGHT);

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

			int ng = ReikaRandomHelper.getRandomBetween(4, 7);
			for (int n = 0; n < ng; n++) {
				Point p = new Point(rand(), rand());
				Cell c = floorCells.get(p);
				if (c != null) {
					ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[ReikaRandomHelper.getRandomBetween(2, 5)];
					Cell c2 = c.getNeighbor(this, dir);
					if (c2 != null && c2.countGlassSides() > 0)
						continue;
					c.glass[dir.ordinal()-2] = true;
					if (c2 != null)
						c2.glass[dir.getOpposite().ordinal()-2] = true;
					int dx = c.getCenterX(true)+dir.offsetX*Cell.TOTAL_RADIUS;
					int dz = c.getCenterZ(true)+dir.offsetZ*Cell.TOTAL_RADIUS;
					for (int i = 1; i < HEIGHT; i++) {
						for (int d = -Cell.HOLE_RADIUS; d <= Cell.HOLE_RADIUS; d++) {
							if (dir.offsetX == 0) {
								world.setBlock(dx+d, y+i, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.GLASS.metadata, 3);
							}
							else {
								world.setBlock(dx, y+i, dz+d, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.GLASS.metadata, 3);
							}
						}
					}
				}
			}

			for (int i = HEIGHT+1; i <= HEIGHT+SEP; i++) {
				this.generateShaft(world, x+this.getUpperShaftXCenterOffset(), y+i, z+this.getUpperShaftZCenterOffset());
			}
		}

		private int getLowerShaftXCenterOffset() {
			return shaftDownX*CELL_SPACING;
		}

		private int getLowerShaftZCenterOffset() {
			return shaftDownZ*CELL_SPACING;
		}

		private int getUpperShaftXCenterOffset() {
			return shaftUpX*CELL_SPACING;
		}

		private int getUpperShaftZCenterOffset() {
			return shaftUpZ*CELL_SPACING;
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

		private boolean isOpen;
		private boolean hasLoot;

		private boolean[] glass = new boolean[4];

		private Cell(int x, int z, int rx, int rz) {
			xPos = x;
			zPos = z;

			rootX = rx;
			rootZ = rz;

			//ReikaJavaLibrary.pConsole(rx+", "+rz);
		}

		private int countGlassSides() {
			return ReikaArrayHelper.countTrue(glass);
		}

		private Cell getNeighbor(EntranceLevel el, ForgeDirection dir) {
			int dx = xPos+dir.offsetX;
			int dz = zPos+dir.offsetZ;
			return el.floorCells.get(new Point(dx, dz));
		}

		private void generate(World world, int y) {
			int r = HOLE_RADIUS;//TOTAL_RADIUS;
			for (int i = -r; i <= r; i++) {
				for (int k = -r; k <= r; k++) {
					int dx = this.getCenterX(true)+i;
					int dz = this.getCenterZ(true)+k;
					if (Math.abs(i) <= HOLE_RADIUS && Math.abs(k) <= HOLE_RADIUS) {
						if (isOpen) {
							world.setBlock(dx, y, dz, Blocks.air);
						}
						else {
							world.setBlock(dx, y, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata, 3);
						}
					}
					else {
						world.setBlock(dx, y, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.COBBLE.metadata, 3);
					}
				}
			}

			if (!isOpen && hasLoot) {
				int x = this.getCenterX(true);
				int z = this.getCenterZ(true);
				for (int i = -1; i <= 1; i++) {
					for (int k = -1; k <= 1; k++) {
						int m = i == 0 && k == 0 ? BlockType.LIGHT.metadata : BlockType.CLOAK.metadata;
						world.setBlock(x+i, y, z+k, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), m, 3);
					}
				}
				this.generateLootChest(world, x, y+1, z, ReikaDirectionHelper.getRandomDirection(false, world.rand));
			}
		}

		private void generateLootChest(World world, int x, int y, int z, ForgeDirection dir) {
			world.setBlock(x, y, z, ChromaBlocks.LOOTCHEST.getBlockInstance(), dir.ordinal()-2, 3);
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
