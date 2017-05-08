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
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Base.StructurePiece;
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
		return r*Lock.SIZE+(r-1)*2+6+3; //locks + gaps + center space + outer wall space
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
				for (int h = 0; h <= HEIGHT; h++) {
					if (i == -r || k == -r || i == r || k == r || (h == 0 && level == ((WaterPuzzleGenerator)parent).levelCount()-1) || h == HEIGHT)
						world.setBlock(x+i, y+h, z+k, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), (Math.abs(i)+Math.abs(k))%6 == 0 ? BlockType.LIGHT.metadata : BlockType.STONE.metadata);
					else if (h == 1)
						world.setBlock(x+i, y+h, z+k, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.CLOAK.metadata);
					else if (h == 2)
						world.setBlock(x+i, y+h, z+k, Math.abs(i)%(2*Lock.SIZE+2) == Lock.SIZE+1 || Math.abs(k)%(2*Lock.SIZE+2) == Lock.SIZE+1 ? Blocks.sandstone : Blocks.brick_block);
					else
						world.setBlock(x+i, y+h, z+k, Blocks.air);
				}
			}
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
		for (Point p : path.getSolution()) {
			world.setBlock(x+p.x*(Lock.SIZE*2+1+1), y+4, z+p.y*(Lock.SIZE*2+1+1), Blocks.emerald_block);
		}
		world.setBlock(x+path.startLoc.x*(Lock.SIZE*2+1+1), y+4, z+path.startLoc.y*(Lock.SIZE*2+1+1), Blocks.redstone_block);
		world.setBlock(x+path.endLoc.x*(Lock.SIZE*2+1+1), y+4, z+path.endLoc.y*(Lock.SIZE*2+1+1), Blocks.gold_block);
		for (Point p : path.additionalKeys) {
			world.setBlock(x+p.x*(Lock.SIZE*2+1+1), y+5, z+p.y*(Lock.SIZE*2+1+1), Blocks.diamond_block);
		}
		if (level > 0) {
			WaterFloor f = ((WaterPuzzleGenerator)parent).getLevel(level-1);
			Point p = f.path.endLoc;
			world.setBlock(x+p.x*(Lock.SIZE*2+1+1), y+HEIGHT, z+p.y*(Lock.SIZE*2+1+1), Blocks.air);
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
			}
		}

	}

}
