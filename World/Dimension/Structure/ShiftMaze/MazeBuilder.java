/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure.ShiftMaze;

import java.util.List;
import java.util.UUID;

import net.minecraft.block.Block;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.Structure.ShiftMazeGenerator;
import Reika.ChromatiCraft.World.Dimension.Structure.ShiftMaze.Part.MazePartEndCore;
import Reika.ChromatiCraft.World.Dimension.Structure.ShiftMaze.Part.MazePartEndDoor;
import Reika.ChromatiCraft.World.Dimension.Structure.ShiftMaze.Part.MazePartEndDoorConnector;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;

public class MazeBuilder {

	private final ChunkSplicedGenerationCache world;
	private final int offsetX, offsetZ;
	private final int posY;

	public MazeBuilder(ChunkSplicedGenerationCache world, int offsetX, int posY, int offsetZ) {
		this.world = world;
		this.offsetX = offsetX;
		this.offsetZ = offsetZ;
		this.posY = posY;
	}

	public void placeControllerTile(ShiftMazeGenerator gen, int x, int y, int z) {
		gen.generateDataTile(x, y, z);
	}

	@SuppressWarnings("incomplete-switch")
	public void build(ShiftMazeGenerator gen, MazeGrid finishedGrid/* ,
	 * Map<Integer
	 * ,
	 * List<Point
	 * >>
	 * segmentInfo */) {
		for (int xx = 0; xx < finishedGrid.getXSize(); xx++) {
			for (int zz = 0; zz < finishedGrid.getZSize(); zz++) {
				int translatedX = offsetX + (xx * 4 - 1);
				int translatedZ = offsetZ + (zz * 4 - 1);
				MazeBaseBuilder.doBasicGen(world, translatedX, posY, translatedZ);
			}
		}

		for (int xx = 0; xx < finishedGrid.getXSize(); xx++) {
			for (int zz = 0; zz < finishedGrid.getZSize(); zz++) {
				int translatedX = offsetX + (xx * 4 - 1);
				int translatedZ = offsetZ + (zz * 4 - 1);
				MazeGrid.MazeSegment segment = finishedGrid.getSegment(xx, zz);
				MazeBaseBuilder.build(gen, world, translatedX, posY, translatedZ, segment);

				/* Point p = new Point(xx, zz); boolean placed = false; for
				 * (Integer id : segmentInfo.keySet()) {
				 * if(segmentInfo.get(id).contains(p)) {
				 * world.setBlock(translatedX + 2, posY + 4, translatedZ + 2,
				 * Blocks.wool, id); placed = true; } } if(!placed) {
				 * world.setBlock(translatedX + 2, posY + 4, translatedZ + 2,
				 * Blocks.bedrock); }
				 * 
				 * if(segment.specialLock) { world.setBlock(translatedX + 2,
				 * posY + 8, translatedZ + 2, Blocks.diamond_block); } */
			}
		}

		for (MazeGrid.ShiftMazeDoor door : finishedGrid.getSplittingDoors()) {
			int trX = offsetX + (door.pointDirFrom.x * 4 - 1);
			int trZ = offsetZ + (door.pointDirFrom.y * 4 - 1);
			List<ShiftMazeState> states = door.doorStates;
			switch (door.dir) {
				case NORTH:
					this.setDoor(gen, world, trX + 1, posY + 1, trZ, states);
					this.setDoor(gen, world, trX + 1, posY + 2, trZ, states);
					this.setDoor(gen, world, trX + 1, posY + 3, trZ, states);
					this.setDoor(gen, world, trX + 2, posY + 1, trZ, states);
					this.setDoor(gen, world, trX + 2, posY + 2, trZ, states);
					this.setDoor(gen, world, trX + 2, posY + 3, trZ, states);
					this.setDoor(gen, world, trX + 3, posY + 1, trZ, states);
					this.setDoor(gen, world, trX + 3, posY + 2, trZ, states);
					this.setDoor(gen, world, trX + 3, posY + 3, trZ, states);
					break;
				case SOUTH:
					this.setDoor(gen, world, trX + 1, posY + 1, trZ + 4, states);
					this.setDoor(gen, world, trX + 1, posY + 2, trZ + 4, states);
					this.setDoor(gen, world, trX + 1, posY + 3, trZ + 4, states);
					this.setDoor(gen, world, trX + 2, posY + 1, trZ + 4, states);
					this.setDoor(gen, world, trX + 2, posY + 2, trZ + 4, states);
					this.setDoor(gen, world, trX + 2, posY + 3, trZ + 4, states);
					this.setDoor(gen, world, trX + 3, posY + 1, trZ + 4, states);
					this.setDoor(gen, world, trX + 3, posY + 2, trZ + 4, states);
					this.setDoor(gen, world, trX + 3, posY + 3, trZ + 4, states);
					break;
				case WEST:
					this.setDoor(gen, world, trX, posY + 1, trZ + 1, states);
					this.setDoor(gen, world, trX, posY + 2, trZ + 1, states);
					this.setDoor(gen, world, trX, posY + 3, trZ + 1, states);
					this.setDoor(gen, world, trX, posY + 1, trZ + 2, states);
					this.setDoor(gen, world, trX, posY + 2, trZ + 2, states);
					this.setDoor(gen, world, trX, posY + 3, trZ + 2, states);
					this.setDoor(gen, world, trX, posY + 1, trZ + 3, states);
					this.setDoor(gen, world, trX, posY + 2, trZ + 3, states);
					this.setDoor(gen, world, trX, posY + 3, trZ + 3, states);
					break;
				case EAST:
					this.setDoor(gen, world, trX + 4, posY + 1, trZ + 1, states);
					this.setDoor(gen, world, trX + 4, posY + 2, trZ + 1, states);
					this.setDoor(gen, world, trX + 4, posY + 3, trZ + 1, states);
					this.setDoor(gen, world, trX + 4, posY + 1, trZ + 2, states);
					this.setDoor(gen, world, trX + 4, posY + 2, trZ + 2, states);
					this.setDoor(gen, world, trX + 4, posY + 3, trZ + 2, states);
					this.setDoor(gen, world, trX + 4, posY + 1, trZ + 3, states);
					this.setDoor(gen, world, trX + 4, posY + 2, trZ + 3, states);
					this.setDoor(gen, world, trX + 4, posY + 3, trZ + 3, states);
					break;
			}
		}
	}

	private void setDoor(ShiftMazeGenerator gen, ChunkSplicedGenerationCache world, int x, int y, int z, List<ShiftMazeState> states) {
		world.setBlock(x, y, z, ChromaBlocks.SHIFTLOCK.getBlockInstance(), states.contains(gen.getActiveState()) ? 1 : 0);
		gen.addToggleDoor(x, y, z, states);
	}

	public void buildMazeBase(MazeGrid grid) {
		Block sh = ChromaBlocks.STRUCTSHIELD.getBlockInstance();
		int ms = BlockStructureShield.BlockType.STONE.metadata;
		int mg = BlockStructureShield.BlockType.GLASS.metadata;

		int maxX = grid.getXSize() * 4;
		int maxZ = grid.getZSize() * 4;

		Block gl = ChromaBlocks.SPECIALSHIELD.getBlockInstance();

		for (int xx = -1; xx < maxX; xx++) {
			for (int zz = -1; zz < maxZ; zz++) {
				for (int yy = 0; yy < 9; yy++) { // 0=stone, 1,2,3=maze,
					// 4=glass,
					// 5,6,7=runningontopofit,
					// 8=stone
					world.setAir(offsetX + xx, posY + yy, offsetZ + zz);
				}
			}
		}
		for (int xx = -1; xx < maxX; xx++) {
			for (int zz = -1; zz < maxZ; zz++) {
				world.setBlock(offsetX + xx, posY, offsetZ + zz, sh, ms);
				world.setBlock(offsetX + xx, posY + 4, offsetZ + zz, gl, mg);
				world.setBlock(offsetX + xx, posY + 8, offsetZ + zz, sh, ms);
			}
		}

		for (int xx = -1; xx < maxX; xx++) {
			for (int yy = 0; yy < 9; yy++) {
				world.setBlock(offsetX + xx, posY + yy, offsetZ - 1, sh, ms);
				world.setBlock(offsetX + xx, posY + yy, offsetZ + maxZ - 1, sh, ms);
			}
		}

		for (int zz = -1; zz < maxZ; zz++) {
			for (int yy = 0; yy < 9; yy++) {
				world.setBlock(offsetX - 1, posY + yy, offsetZ + zz, sh, ms);
				world.setBlock(offsetX + maxX - 1, posY + yy, offsetZ + zz, sh, ms);
			}
		}
	}

	public void buildEntrance(int centerX, int lowestY, int centerZ) {
		this.cleanCorridor(centerX, lowestY - 1, centerZ);

		Block sh = ChromaBlocks.STRUCTSHIELD.getBlockInstance();
		int ms = BlockStructureShield.BlockType.STONE.metadata;
		world.setBlock(centerX, lowestY + 2, centerZ, sh, ms);
		world.setBlock(centerX, lowestY + 2, centerZ + 1, sh, ms);
		world.setBlock(centerX, lowestY + 2, centerZ - 1, sh, ms);

		world.setAir(centerX, lowestY + 3, centerZ + 3);
		world.setAir(centerX, lowestY + 3, centerZ + 4);
		world.setAir(centerX, lowestY + 4, centerZ + 3);
		world.setAir(centerX, lowestY + 4, centerZ + 4);
		world.setAir(centerX, lowestY + 5, centerZ + 3);
		world.setAir(centerX, lowestY + 5, centerZ + 4);

		world.setAir(centerX, lowestY + 3, centerZ - 3);
		world.setAir(centerX, lowestY + 3, centerZ - 4);
		world.setAir(centerX, lowestY + 4, centerZ - 3);
		world.setAir(centerX, lowestY + 4, centerZ - 4);
		world.setAir(centerX, lowestY + 5, centerZ - 3);
		world.setAir(centerX, lowestY + 5, centerZ - 4);
	}

	public void buildExit(ShiftMazeGenerator gen, int endX, int posY, int endZ, List<UUID> createdKeys) {
		int xCursor = endX;
		this.cleanCorridor(xCursor, posY + 1, endZ);

		xCursor--;
		this.buildTransitionRing(xCursor, posY, endZ);

		MazePartEndDoorConnector partCon = new MazePartEndDoorConnector(gen);
		MazePartEndCore partEnd = new MazePartEndCore(gen);
		xCursor--;
		for (UUID key : createdKeys) {
			xCursor = partCon.generateAndMoveCursor(world, xCursor, posY, endZ, xCursor);
			MazePartEndDoor door = new MazePartEndDoor(gen, key);
			xCursor = door.generateAndMoveCursor(world, xCursor, posY, endZ, xCursor);
		}
		partEnd.generate(world, xCursor, posY, endZ);
	}

	private void cleanCorridor(int posX, int posY, int posZ) {
		world.setAir(posX, posY, posZ);
		world.setAir(posX, posY, posZ + 1);
		world.setAir(posX, posY, posZ - 1);
		world.setAir(posX, posY + 1, posZ);
		world.setAir(posX, posY + 1, posZ + 1);
		world.setAir(posX, posY + 1, posZ - 1);
		world.setAir(posX, posY + 2, posZ);
		world.setAir(posX, posY + 2, posZ + 1);
		world.setAir(posX, posY + 2, posZ - 1);
	}

	private void buildTransitionRing(int posX, int posY, int posZ) {
		Block sh = ChromaBlocks.STRUCTSHIELD.getBlockInstance();
		int ms = BlockStructureShield.BlockType.STONE.metadata;
		world.setBlock(posX, posY, posZ, sh, ms);
		world.setBlock(posX, posY, posZ + 1, sh, ms);
		world.setBlock(posX, posY, posZ + 2, sh, ms);
		world.setBlock(posX, posY, posZ - 1, sh, ms);
		world.setBlock(posX, posY, posZ - 2, sh, ms);
		world.setBlock(posX, posY + 1, posZ + 2, sh, ms);
		world.setBlock(posX, posY + 1, posZ - 2, sh, ms);
		world.setBlock(posX, posY + 2, posZ + 2, sh, ms);
		world.setBlock(posX, posY + 2, posZ - 2, sh, ms);
		world.setBlock(posX, posY + 3, posZ + 2, sh, ms);
		world.setBlock(posX, posY + 3, posZ - 2, sh, ms);
		world.setBlock(posX, posY + 4, posZ, sh, ms);
		world.setBlock(posX, posY + 4, posZ + 1, sh, ms);
		world.setBlock(posX, posY + 4, posZ + 2, sh, ms);
		world.setBlock(posX, posY + 4, posZ - 1, sh, ms);
		world.setBlock(posX, posY + 4, posZ - 2, sh, ms);
		this.cleanCorridor(posX, posY + 1, posZ);
	}

}
