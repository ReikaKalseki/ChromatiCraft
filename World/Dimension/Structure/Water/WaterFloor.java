/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure.Water;

import java.awt.Point;
import java.util.HashSet;
import java.util.UUID;

import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Base.StructurePiece;
import Reika.ChromatiCraft.Block.Dimension.Structure.Water.BlockEverFluid;
import Reika.ChromatiCraft.Block.Dimension.Structure.Water.BlockEverFluid.TileEntityEverFluid;
import Reika.ChromatiCraft.Block.Dimension.Structure.Water.BlockRotatingLock.TileEntityRotatingLock;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.Structure.WaterPuzzleGenerator;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache.TileCallback;


public class WaterFloor extends StructurePiece {

	public static final int HEIGHT = 8;

	public final int level;
	public final int gridSize;

	private final Lock[][] flowGrid;
	private final WaterPath path;

	private final HashSet<Coordinate> blockedChannels = new HashSet();

	private Coordinate origin;

	public WaterFloor(WaterPuzzleGenerator s, int lvl, int r, WaterPath path) {
		super(s);
		level = lvl;
		gridSize = r*2+1;
		flowGrid = new Lock[gridSize][gridSize];
		for (int i = 0; i < flowGrid.length; i++) {
			for (int k = 0; k < flowGrid[i].length; k++) {
				flowGrid[i][k] = new Lock(this, i-r, k-r);
				for (ForgeDirection dir : path.lockSides[i][k]) {
					flowGrid[i][k].openEnds.add(dir);
				}
			}
		}
		this.path = path;
	}

	public int getWidth() {
		int r = (gridSize-1)/2;
		return r*(2+Lock.SIZE)+(r-1)*2+6+4; //locks + gaps + center space + outer wall space
	}

	@Override
	public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {
		origin = new Coordinate(x, y, z);
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
								if (Math.abs(i) > r-4 && Math.abs(k) > r-4)
									world.setBlock(x+i, y+h, z+k, ChromaBlocks.DOOR.getBlockInstance());
								else
									world.setBlock(x+i, y+h, z+k, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.GLASS.metadata);
							else
								world.setBlock(x+i, y+h, z+k, Blocks.air);
						}
					}
					else if ((h >= 0 || Math.abs(i) < r-4 || Math.abs(k) < r-4) && (Math.abs(i) == r || Math.abs(k) == r || (h == 0 && level == ((WaterPuzzleGenerator)parent).levelCount()-1) || h == HEIGHT))
						world.setBlock(x+i, y+h, z+k, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), (Math.abs(i)+Math.abs(k))%6 == 0 ? BlockType.LIGHT.metadata : BlockType.STONE.metadata);
					else if (h == 1)
						world.setBlock(x+i, y+h, z+k, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.CLOAK.metadata);
					else if (h == 2)
						if (Math.abs(i)%(2*Lock.SIZE+2) == Lock.SIZE+1 || Math.abs(k)%(2*Lock.SIZE+2) == Lock.SIZE+1)
							world.setBlock(x+i, y+h, z+k, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.MOSS.metadata);
						else
							world.setBlock(x+i, y+h, z+k, Blocks.brick_block);
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
				for (ForgeDirection dir : l.openEnds) {
					for (int d = 0; d <= Lock.SIZE; d++) {
						world.setBlock(l.centerLocation.xCoord+d*dir.offsetX, y+2, l.centerLocation.zCoord+d*dir.offsetZ, Blocks.air);
						world.setTileEntity(l.centerLocation.xCoord, l.centerLocation.yCoord, l.centerLocation.zCoord, ChromaBlocks.WATERLOCK.getBlockInstance(), 0, new LockCallback(parent.id, level, i-r0, k-r0, ForgeDirection.EAST));
					}
				}
				for (int d = 2; d < 6; d++) {
					ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[d];
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
		if (level > 0) {
			WaterFloor f = ((WaterPuzzleGenerator)parent).getLevel(level-1);
			Point p = f.path.endLoc;
			for (int i = 0; i <= 4; i++)
				world.setBlock(x+p.x*(Lock.SIZE*2+1+1), y+HEIGHT+i, z+p.y*(Lock.SIZE*2+1+1), Blocks.air);
		}
		else {
			Point p = path.startLoc;
			world.setTileEntity(x+p.x*(Lock.SIZE*2+1+1), y+HEIGHT, z+p.y*(Lock.SIZE*2+1+1), ChromaBlocks.EVERFLUID.getBlockInstance(), 0, new EverFluidCallback(parent.id, level));
			world.setBlock(x+p.x*(Lock.SIZE*2+1+1), y+HEIGHT+1, z+p.y*(Lock.SIZE*2+1+1), ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.CLOAK.metadata);
		}
	}

	public boolean hasBeenSolved() {
		return false;
	}

	public boolean isSlotOccluded(int x, int y, int z) {
		return blockedChannels.contains(new Coordinate(x, y, z));
	}

	public void updateChannels() {
		blockedChannels.clear();
		for (int i = 0; i < flowGrid.length; i++) {
			for (int k = 0; k < flowGrid[i].length; k++) {
				for (int d = 2; d < 6; d++) {
					ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[d];
					if (!flowGrid[i][k].isDirectionOpen(dir)) {
						for (int l = 1; l <= 3; l++) {
							blockedChannels.add(flowGrid[i][k].centerLocation.offset(dir, l));
						}
					}
				}
			}
		}
	}

	public void rotateLock(int i, int k) {
		int r0 = (gridSize-1)/2;
		flowGrid[i+r0][k+r0].rotate();
	}

	private static class EverFluidCallback implements TileCallback {

		private final UUID uid;
		private final int level;

		private EverFluidCallback(UUID id, int lvl) {
			uid = id;
			level = lvl;
		}

		@Override
		public void onTilePlaced(World world, int x, int y, int z, TileEntity te) {
			if (te instanceof TileEntityEverFluid) {
				BlockEverFluid.placeSource(world, x, y, z);
				((TileEntityEverFluid)te).setData(uid, level);
			}
		}

	}

	private static class LockCallback implements TileCallback {

		private final UUID uid;
		private final int level;
		private final ForgeDirection direction;
		private final int lockX;
		private final int lockY;

		private LockCallback(UUID id, int lvl, int lx, int ly, ForgeDirection dir) {
			uid = id;
			level = lvl;
			lockX = lx;
			lockY = ly;
			direction = dir;
		}

		@Override
		public void onTilePlaced(World world, int x, int y, int z, TileEntity te) {
			if (te instanceof TileEntityRotatingLock) {
				((TileEntityRotatingLock)te).setData(direction, level, lockX, lockY);
				((TileEntityRotatingLock)te).uid = uid;
				//while (world.rand.nextInt(4) > 0) cannot do this since gens chunk by chunk
				//	((TileEntityRotatingLock)te).rotate();
			}
		}

	}

}
