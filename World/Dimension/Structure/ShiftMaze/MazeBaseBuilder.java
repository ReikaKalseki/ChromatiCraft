/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure.ShiftMaze;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.Structure.ShiftMazeGenerator;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;

public class MazeBaseBuilder {

	public static void build(ShiftMazeGenerator gen, ChunkSplicedGenerationCache world, int segmentOffsetX, int posY, int segmentOffsetZ, MazeGrid.MazeSegment segment) {
		carveWalls(world, segmentOffsetX, posY, segmentOffsetZ, segment);
		MazePartGenerator extraGen = segment.getType().getExtraGen();
		if (extraGen != null) {
			extraGen.generateExtras(gen, world, segmentOffsetX, posY, segmentOffsetZ, segment);
		}
	}

	@SuppressWarnings("incomplete-switch")
	private static void carveWalls(ChunkSplicedGenerationCache world, int posX, int posY, int posZ, MazeGrid.MazeSegment segment) {
		List<ForgeDirection> connections = segment.getConnections();
		for (ForgeDirection dir : MazeGrid.MazeSegment.VALID_CONNECTIONS) {
			if (!connections.contains(dir))
				continue;
			switch (dir) {
				case NORTH:
					world.setAir(posX + 1, posY + 1, posZ);
					world.setAir(posX + 1, posY + 2, posZ);
					world.setAir(posX + 1, posY + 3, posZ);
					world.setAir(posX + 2, posY + 1, posZ);
					world.setAir(posX + 2, posY + 2, posZ);
					world.setAir(posX + 2, posY + 3, posZ);
					world.setAir(posX + 3, posY + 1, posZ);
					world.setAir(posX + 3, posY + 2, posZ);
					world.setAir(posX + 3, posY + 3, posZ);
					break;
				case SOUTH:
					world.setAir(posX + 1, posY + 1, posZ + 4);
					world.setAir(posX + 1, posY + 2, posZ + 4);
					world.setAir(posX + 1, posY + 3, posZ + 4);
					world.setAir(posX + 2, posY + 1, posZ + 4);
					world.setAir(posX + 2, posY + 2, posZ + 4);
					world.setAir(posX + 2, posY + 3, posZ + 4);
					world.setAir(posX + 3, posY + 1, posZ + 4);
					world.setAir(posX + 3, posY + 2, posZ + 4);
					world.setAir(posX + 3, posY + 3, posZ + 4);
					break;
				case WEST:
					world.setAir(posX, posY + 1, posZ + 1);
					world.setAir(posX, posY + 2, posZ + 1);
					world.setAir(posX, posY + 3, posZ + 1);
					world.setAir(posX, posY + 1, posZ + 2);
					world.setAir(posX, posY + 2, posZ + 2);
					world.setAir(posX, posY + 3, posZ + 2);
					world.setAir(posX, posY + 1, posZ + 3);
					world.setAir(posX, posY + 2, posZ + 3);
					world.setAir(posX, posY + 3, posZ + 3);
					break;
				case EAST:
					world.setAir(posX + 4, posY + 1, posZ + 1);
					world.setAir(posX + 4, posY + 2, posZ + 1);
					world.setAir(posX + 4, posY + 3, posZ + 1);
					world.setAir(posX + 4, posY + 1, posZ + 2);
					world.setAir(posX + 4, posY + 2, posZ + 2);
					world.setAir(posX + 4, posY + 3, posZ + 2);
					world.setAir(posX + 4, posY + 1, posZ + 3);
					world.setAir(posX + 4, posY + 2, posZ + 3);
					world.setAir(posX + 4, posY + 3, posZ + 3);
					break;
			}
		}
	}

	public static void doBasicGen(ChunkSplicedGenerationCache world, int posX, int posY, int posZ) {
		Block sh = ChromaBlocks.STRUCTSHIELD.getBlockInstance();
		int ms = BlockStructureShield.BlockType.STONE.metadata;
		int ml = BlockStructureShield.BlockType.LIGHT.metadata;

		for (int xx = 0; xx <= 4; xx++) {
			for (int zz = 0; zz <= 4; zz++) {
				for (int i = 0; i <= 3; i++) {
					world.setAir(posX + xx, posY + i, posZ + zz);
				}
			}
		}

		// Basic field below.
		for (int xx = 0; xx <= 4; xx++) { // Basic 5x5 ground-field. Outermost
			// parts are overlapping anyway.
			for (int zz = 0; zz <= 4; zz++) {
				world.setBlock(posX + xx, posY, posZ + zz, sh, ms);
			}
		}

		for (int xx = 0; xx <= 4; xx++) {
			world.setBlock(posX + xx, posY + 1, posZ, sh, ms);
			world.setBlock(posX + xx, posY + 2, posZ, sh, ms);
			world.setBlock(posX + xx, posY + 3, posZ, sh, ms);
			world.setBlock(posX + xx, posY + 1, posZ + 4, sh, ms);
			world.setBlock(posX + xx, posY + 2, posZ + 4, sh, ms);
			world.setBlock(posX + xx, posY + 3, posZ + 4, sh, ms);
		}

		for (int zz = 0; zz <= 4; zz++) {
			world.setBlock(posX, posY + 1, posZ + zz, sh, ms);
			world.setBlock(posX, posY + 2, posZ + zz, sh, ms);
			world.setBlock(posX, posY + 3, posZ + zz, sh, ms);
			world.setBlock(posX + 4, posY + 1, posZ + zz, sh, ms);
			world.setBlock(posX + 4, posY + 2, posZ + zz, sh, ms);
			world.setBlock(posX + 4, posY + 3, posZ + zz, sh, ms);
		}

		// "Pillars" that always exist.
		world.setBlock(posX, posY + 1, posZ, sh, ms);
		world.setBlock(posX, posY + 2, posZ, sh, ml);
		world.setBlock(posX, posY + 3, posZ, sh, ms);
		world.setBlock(posX + 4, posY + 1, posZ, sh, ms);
		world.setBlock(posX + 4, posY + 2, posZ, sh, ml);
		world.setBlock(posX + 4, posY + 3, posZ, sh, ms);
		world.setBlock(posX, posY + 1, posZ + 4, sh, ms);
		world.setBlock(posX, posY + 2, posZ + 4, sh, ml);
		world.setBlock(posX, posY + 3, posZ + 4, sh, ms);
		world.setBlock(posX + 4, posY + 1, posZ + 4, sh, ms);
		world.setBlock(posX + 4, posY + 2, posZ + 4, sh, ml);
		world.setBlock(posX + 4, posY + 3, posZ + 4, sh, ms);
	}

}
