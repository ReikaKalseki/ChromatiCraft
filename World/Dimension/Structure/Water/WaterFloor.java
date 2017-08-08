/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure.Water;

import java.awt.Point;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.UUID;

import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Base.StructurePiece;
import Reika.ChromatiCraft.Block.BlockChromaDoor.TileEntityChromaDoor;
import Reika.ChromatiCraft.Block.Dimension.Structure.Water.BlockRotatingLock.TileEntityRotatingLock;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.Structure.WaterPuzzleGenerator;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache.TileCallback;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;


public class WaterFloor extends StructurePiece {

	public static final int HEIGHT = 7;

	public final int level;
	public final int gridSize;

	private final Lock[][] flowGrid;
	private final WaterPath path;

	//private final HashSet<Coordinate> blockedChannels = new HashSet();
	//private final HashSet<Coordinate> padCoordinates = new HashSet();
	private final HashSet<Coordinate> doorCoordinates = new HashSet();
	private final HashSet<Lock> checkpoints = new HashSet();

	private Coordinate origin;

	public WaterFloor(WaterPuzzleGenerator s, int lvl, int r, WaterPath path, Random rand) {
		super(s);
		level = lvl;
		gridSize = r*2+1;
		flowGrid = new Lock[gridSize][gridSize];
		for (int i = 0; i < flowGrid.length; i++) {
			for (int k = 0; k < flowGrid[i].length; k++) {
				flowGrid[i][k] = new Lock(this, i-r, k-r);
				for (ForgeDirection dir : path.lockSides[i][k]) {
					flowGrid[i][k].openEnds.add(dir);
					while (rand.nextInt(4) > 0) //randomize
						flowGrid[i][k].rotate(false);
				}
			}
		}
		this.path = path;

		int c = 0;
		for (Point p : path.solution) {
			Lock l = this.getLock(p.x, p.y);
			c++;
			if (c > 2 && rand.nextInt(Math.max(2, 6-c)) == 0 && !p.equals(path.startLoc) && !p.equals(path.endLoc)) {
				c = 0;
				checkpoints.add(l);
			}
		}
	}

	public int getWidth() {
		int r = (gridSize-1)/2;
		return r*(2+Lock.SIZE)+(r-1)*2+6+4; //locks + gaps + center space + outer wall space
	}

	@Override
	public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {
		origin = new Coordinate(x, y, z);
		boolean bottom = level == ((WaterPuzzleGenerator)parent).levelCount()-1;
		int r0 = (gridSize-1)/2;
		for (int i = 0; i < flowGrid.length; i++) {
			for (int k = 0; k < flowGrid[i].length; k++) {
				flowGrid[i][k].centerLocation = new Coordinate(x, y, z).offset((i-r0)*(Lock.SIZE*2+1+1), 1, (k-r0)*(Lock.SIZE*2+1+1));
			}
		}
		int r = this.getWidth();
		for (int i = -r; i <= r; i++) {
			for (int k = -r; k <= r; k++) {
				for (int h = -3; h <= HEIGHT; h++) {
					if ((Math.abs(i) >= r-4 || Math.abs(k) >= r-4) && Math.abs(k) < r && Math.abs(i) < r && (h < HEIGHT || (Math.abs(i) > r-4 && Math.abs(k) > r-4))) {
						if (Math.abs(i) == r-4 || Math.abs(k) == r-4)
							if (h <= 2)
								world.setBlock(x+i, y+h, z+k, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.COBBLE.metadata);
							else
								world.setBlock(x+i, y+h, z+k, Blocks.air);
						else {
							if (h == 2)
								if (Math.abs(i) > r-4 && Math.abs(k) > r-4) {
									world.setBlock(x+i, y+h, z+k, ChromaBlocks.DOOR.getBlockInstance());
									doorCoordinates.add(new Coordinate(x+i, y+h, z+k));
								}
								else
									world.setBlock(x+i, y+h, z+k, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), bottom ? BlockType.STONE.metadata : BlockType.GLASS.metadata);
							else
								world.setBlock(x+i, y+h, z+k, Blocks.air);
						}
					}
					else if ((h >= 0 || Math.abs(i) < r-4 || Math.abs(k) < r-4) && (Math.abs(i) == r || Math.abs(k) == r || (h == 0 && bottom) || h == HEIGHT))
						world.setBlock(x+i, y+h, z+k, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), (Math.abs(i)+Math.abs(k))%6 == 0 ? BlockType.LIGHT.metadata : BlockType.STONE.metadata);
					else if (h == 1)
						world.setBlock(x+i, y+h, z+k, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.CLOAK.metadata);
					else if (h == 2)
						if (Math.abs(i)%(2*Lock.SIZE+2) == Lock.SIZE+1 || Math.abs(k)%(2*Lock.SIZE+2) == Lock.SIZE+1)
							world.setBlock(x+i, y+h, z+k, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), ReikaRandomHelper.doWithChance(40) ? BlockType.MOSS.metadata : BlockType.STONE.metadata);
						else {
							world.setBlock(x+i, y+h, z+k, ChromaBlocks.WATERLOCK.getBlockInstance(), 1); //this is the "pads"
							//padCoordinates.add(new Coordinate(x+i, y+h, z+k));
						}
					else if (h >= 0 || (Math.abs(i) < r-4 && Math.abs(k) < r-4))
						if (h > 0)
							world.setBlock(x+i, y+h, z+k, Blocks.air);
						else
							world.setBlock(x+i, y+h, z+k, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.COBBLE.metadata);
				}
			}
		}
		if (level > 0) {
			WaterFloor f = ((WaterPuzzleGenerator)parent).getLevel(level-1);
			int r2 = f.getWidth();
			for (int h = HEIGHT+1; h <= HEIGHT+4; h++) {
				for (int i = r2-3; i <= r; i++) {
					for (int k = r2-3; k <= r; k++) {
						world.setBlock(x+i, y+h, z+k, Blocks.air);
						world.setBlock(x-i, y+h, z+k, Blocks.air);
						world.setBlock(x+i, y+h, z-k, Blocks.air);
						world.setBlock(x-i, y+h, z-k, Blocks.air);
					}
				}
				for (int i = -2; i <= 2; i++) {
					for (int k = -2; k <= 2; k++) {
						if (Math.abs(i) == 2 || Math.abs(k) == 2 || h == HEIGHT+4) {
							world.setBlock(x-r+2+i, y+h, z-r+2+k, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
							world.setBlock(x-r+2+i, y+h, z+r-2+k, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
							world.setBlock(x+r-2+i, y+h, z-r+2+k, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
							world.setBlock(x+r-2+i, y+h, z+r-2+k, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
						}
						else {
							world.setBlock(x-r+2+i, y+h, z-r+2+k, Blocks.air);
							world.setBlock(x-r+2+i, y+h, z+r-2+k, Blocks.air);
							world.setBlock(x+r-2+i, y+h, z-r+2+k, Blocks.air);
							world.setBlock(x+r-2+i, y+h, z+r-2+k, Blocks.air);
						}
					}
				}
				for (int i = r2-4; i <= r; i++) {
					for (int k = r2-4; k <= r; k++) {
						if (Math.abs(i) > r2 || Math.abs(k) > r2) {
							world.setBlock(x+i, y+HEIGHT+4, z+k, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
							world.setBlock(x-i, y+HEIGHT+4, z+k, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
							world.setBlock(x+i, y+HEIGHT+4, z-k, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
							world.setBlock(x-i, y+HEIGHT+4, z-k, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
						}
					}
				}
				for (int i = r2; i <= r; i++) {
					world.setBlock(x+i, y+h, z+r2-4, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
					world.setBlock(x-i, y+h, z+r2-4, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
					world.setBlock(x+i, y+h, z-r2+4, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
					world.setBlock(x-i, y+h, z-r2+4, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
					world.setBlock(x+r2-4, y+h, z+i, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
					world.setBlock(x+r2-4, y+h, z-i, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
					world.setBlock(x-r2+4, y+h, z+i, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
					world.setBlock(x-r2+4, y+h, z-i, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
				}
				for (int i = r2-4; i <= r; i++) {
					world.setBlock(x+i, y+h, z+r, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
					world.setBlock(x+i, y+h, z-r, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
					world.setBlock(x-i, y+h, z+r, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
					world.setBlock(x-i, y+h, z-r, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
					world.setBlock(x+r, y+h, z+i, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
					world.setBlock(x-r, y+h, z+i, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
					world.setBlock(x+r, y+h, z-i, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
					world.setBlock(x-r, y+h, z-i, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
				}
				world.setBlock(x+r2, y+h, z+r2, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
				world.setBlock(x-r2, y+h, z+r2, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
				world.setBlock(x+r2, y+h, z-r2, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
				world.setBlock(x-r2, y+h, z-r2, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
				world.setBlock(x+r2+1, y+h, z+r2+1, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
				world.setBlock(x-r2-1, y+h, z+r2+1, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
				world.setBlock(x+r2+1, y+h, z-r2-1, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
				world.setBlock(x-r2-1, y+h, z-r2-1, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
			}
			for (int h = HEIGHT+1; h < HEIGHT+4; h++) {
				for (int i = 1; i <= 3; i++) {
					world.setBlock(x+r-i, y+h, z+r-4, Blocks.air);
					world.setBlock(x-r+i, y+h, z+r-4, Blocks.air);
					world.setBlock(x+r-i, y+h, z-r+4, Blocks.air);
					world.setBlock(x-r+i, y+h, z-r+4, Blocks.air);
					world.setBlock(x-r+4, y+h, z+r-i, Blocks.air);
					world.setBlock(x-r+4, y+h, z-r+i, Blocks.air);
					world.setBlock(x+r-4, y+h, z+r-i, Blocks.air);
					world.setBlock(x+r-4, y+h, z-r+i, Blocks.air);
				}
			}
			parent.generateLootChest(x+r2+1, y+HEIGHT+1, z+r2, ForgeDirection.EAST, ChestGenHooks.DUNGEON_CHEST, 0);
			parent.generateLootChest(x+r2+2, y+HEIGHT+1, z+r2+1, ForgeDirection.NORTH, ChestGenHooks.DUNGEON_CHEST, 0);
			parent.generateLootChest(x+r2, y+HEIGHT+1, z+r2+1, ForgeDirection.SOUTH, ChestGenHooks.DUNGEON_CHEST, 0);
			parent.generateLootChest(x+r2+1, y+HEIGHT+1, z+r2+2, ForgeDirection.WEST, ChestGenHooks.DUNGEON_CHEST, 0);

			parent.generateLootChest(x-r2-1, y+HEIGHT+1, z+r2, ForgeDirection.WEST, ChestGenHooks.DUNGEON_CHEST, 0);
			parent.generateLootChest(x-r2-2, y+HEIGHT+1, z+r2+1, ForgeDirection.NORTH, ChestGenHooks.DUNGEON_CHEST, 0);
			parent.generateLootChest(x-r2, y+HEIGHT+1, z+r2+1, ForgeDirection.SOUTH, ChestGenHooks.DUNGEON_CHEST, 0);
			parent.generateLootChest(x-r2-1, y+HEIGHT+1, z+r2+2, ForgeDirection.EAST, ChestGenHooks.DUNGEON_CHEST, 0);

			parent.generateLootChest(x+r2+1, y+HEIGHT+1, z-r2, ForgeDirection.EAST, ChestGenHooks.DUNGEON_CHEST, 0);
			parent.generateLootChest(x+r2+2, y+HEIGHT+1, z-r2-1, ForgeDirection.SOUTH, ChestGenHooks.DUNGEON_CHEST, 0);
			parent.generateLootChest(x+r2, y+HEIGHT+1, z-r2-1, ForgeDirection.NORTH, ChestGenHooks.DUNGEON_CHEST, 0);
			parent.generateLootChest(x+r2+1, y+HEIGHT+1, z-r2-2, ForgeDirection.WEST, ChestGenHooks.DUNGEON_CHEST, 0);

			parent.generateLootChest(x-r2-1, y+HEIGHT+1, z-r2, ForgeDirection.WEST, ChestGenHooks.DUNGEON_CHEST, 0);
			parent.generateLootChest(x-r2-2, y+HEIGHT+1, z-r2-1, ForgeDirection.SOUTH, ChestGenHooks.DUNGEON_CHEST, 0);
			parent.generateLootChest(x-r2, y+HEIGHT+1, z-r2-1, ForgeDirection.NORTH, ChestGenHooks.DUNGEON_CHEST, 0);
			parent.generateLootChest(x-r2-1, y+HEIGHT+1, z-r2-2, ForgeDirection.EAST, ChestGenHooks.DUNGEON_CHEST, 0);
		}
		for (int i = 0; i < flowGrid.length; i++) {
			for (int k = 0; k < flowGrid[i].length; k++) {
				Lock l = flowGrid[i][k];
				world.setTileEntity(l.centerLocation.xCoord, l.centerLocation.yCoord, l.centerLocation.zCoord, ChromaBlocks.WATERLOCK.getBlockInstance(), 0, new LockCallback(parent.id, level, i-r0, k-r0, l.facing, checkpoints.contains(l), l.openEnds));
				for (int n = 2; n < 6; n++) {
					ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[n];
					for (int d = 0; d <= Lock.SIZE; d++) {
						int dx = l.centerLocation.xCoord+d*dir.offsetX;
						int dz = l.centerLocation.zCoord+d*dir.offsetZ;
						if (l.isDirectionOpen(dir))
							world.setBlock(dx, y+2, dz, Blocks.air);
						//padCoordinates.remove(new Coordinate(dx, y+2, dz));
					}
					world.setBlock(l.centerLocation.xCoord+(Lock.SIZE+1)*dir.offsetX, y+2, l.centerLocation.zCoord+(Lock.SIZE+1)*dir.offsetZ, Blocks.air);
				}
			}
		}
		/*
		for (Point p : path.getSolution()) {
			world.setBlock(x+p.x*(Lock.SIZE*2+1+1), y+4, z+p.y*(Lock.SIZE*2+1+1), Blocks.emerald_block);
		}
		world.setBlock(x+path.startLoc.x*(Lock.SIZE*2+1+1), y+4, z+path.startLoc.y*(Lock.SIZE*2+1+1), Blocks.redstone_block);
		world.setBlock(x+path.endLoc.x*(Lock.SIZE*2+1+1), y+4, z+path.endLoc.y*(Lock.SIZE*2+1+1), Blocks.gold_block);
		 */

		int lx = x+path.startLoc.x*(Lock.SIZE*2+1+1);
		int lz = z+path.startLoc.y*(Lock.SIZE*2+1+1);
		for (int i = 0; i <= 4; i++)
			world.setBlock(lx, y+HEIGHT+i, lz, Blocks.air);

		//ReikaJavaLibrary.pConsole("Level "+level+": Path from "+path.startLoc+" to "+path.endLoc+", end at "+lx+","+lz);


		//this.updateChannels();

		/*
		for (Coordinate c : blockedChannels) {
			world.setBlock(c.xCoord, c.yCoord, c.zCoord, Blocks.gold_block);
		}*/
	}

	public boolean hasBeenSolved() {
		for (Lock l : checkpoints) {
			if (!l.hasFluid)
				return false;
		}
		return true;
	}
	/*
	public boolean isSlotOccluded(int x, int y, int z) {
		Coordinate c = new Coordinate(x, y, z);
		return padCoordinates.contains(c) || blockedChannels.contains(c);
	}

	public void updateChannels() {
		blockedChannels.clear();
		for (int i = 0; i < flowGrid.length; i++) {
			for (int k = 0; k < flowGrid[i].length; k++) {
				for (int d = 2; d < 6; d++) {
					ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[d];
					if (!flowGrid[i][k].isDirectionOpen(dir)) {
						for (int l = 1; l <= Lock.SIZE; l++) {
							blockedChannels.add(flowGrid[i][k].centerLocation.offset(dir, l).offset(0, 1, 0));
						}
					}
				}
			}
		}
	}
	 */

	public void rotateLock(int i, int k) {
		this.getLock(i, k).rotate(true);
	}

	public Lock getLock(int i, int k) {
		int r0 = (gridSize-1)/2;
		return flowGrid[i+r0][k+r0];
	}

	public void updateFluid(World world, int i, int k, boolean fluid) {
		boolean flag = this.hasBeenSolved();
		this.getLock(i, k).hasFluid = fluid;
		boolean flag2 = this.hasBeenSolved();
		if (flag != flag2) {
			for (Coordinate c : doorCoordinates) {
				if (flag2)
					((TileEntityChromaDoor)c.getTileEntity(world)).open(-1);
				else
					((TileEntityChromaDoor)c.getTileEntity(world)).close();
			}
		}
	}

	public Point getStartLocation() {
		return path.startLoc;
	}

	private static class LockCallback implements TileCallback {

		private final UUID uid;
		private final int level;
		private final ForgeDirection direction;
		private final int lockX;
		private final int lockY;
		private final Collection<ForgeDirection> ends;
		private final boolean isCheckpoint;

		private LockCallback(UUID id, int lvl, int lx, int ly, ForgeDirection dir, boolean check, Collection<ForgeDirection> c) {
			uid = id;
			level = lvl;
			lockX = lx;
			lockY = ly;
			direction = dir;
			ends = c;
			isCheckpoint = check;
		}

		@Override
		public void onTilePlaced(World world, int x, int y, int z, TileEntity te) {
			if (te instanceof TileEntityRotatingLock) {
				((TileEntityRotatingLock)te).setData(direction, level, lockX, lockY, isCheckpoint, ends);
				((TileEntityRotatingLock)te).uid = uid;
				//while (world.rand.nextInt(4) > 0) cannot do this since gens chunk by chunk
				//	((TileEntityRotatingLock)te).rotate();
			}
		}

	}

}
