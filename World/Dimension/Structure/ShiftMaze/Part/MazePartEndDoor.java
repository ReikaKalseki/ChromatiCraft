/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure.ShiftMaze.Part;

import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Block.BlockChromaDoor;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.Structure.ShiftMazeGenerator;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;

public class MazePartEndDoor extends StructurePieceChainable implements ChunkSplicedGenerationCache.TileCallback {

	private static Block door = ChromaBlocks.DOOR.getBlockInstance();
	private static int doorMeta = BlockChromaDoor.getMetadata(false, false, true, true);

	private UUID doorid;

	public MazePartEndDoor(ShiftMazeGenerator s, UUID doorUUID) {
		super(s);
		doorid = doorUUID;
	}

	@Override
	public int getCursorStepWidth() {
		return -1;
	}

	@Override
	public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {
		Block sh = ChromaBlocks.STRUCTSHIELD.getBlockInstance();
		int ms = BlockStructureShield.BlockType.STONE.metadata;

		world.setBlock(x, y,     z,     sh, ms);
		world.setBlock(x, y + 4, z,     sh, ms);

		world.setBlock(x, y,     z + 1, sh, ms);
		world.setBlock(x, y,     z + 2, sh, ms);
		world.setBlock(x, y + 1, z + 2, sh, ms);
		world.setBlock(x, y + 2, z + 2, sh, ms);
		world.setBlock(x, y + 3, z + 2, sh, ms);
		world.setBlock(x, y + 4, z + 2, sh, ms);
		world.setBlock(x, y + 4, z + 1, sh, ms);

		world.setBlock(x, y,     z - 1, sh, ms);
		world.setBlock(x, y,     z - 2, sh, ms);
		world.setBlock(x, y + 1, z - 2, sh, ms);
		world.setBlock(x, y + 2, z - 2, sh, ms);
		world.setBlock(x, y + 3, z - 2, sh, ms);
		world.setBlock(x, y + 4, z - 2, sh, ms);
		world.setBlock(x, y + 4, z - 1, sh, ms);

		this.setDoor(world, x, y + 1, z);
		this.setDoor(world, x, y + 1, z + 1);
		this.setDoor(world, x, y + 1, z - 1);
		this.setDoor(world, x, y + 2, z);
		this.setDoor(world, x, y + 2, z + 1);
		this.setDoor(world, x, y + 2, z - 1);
		this.setDoor(world, x, y + 3, z);
		this.setDoor(world, x, y + 3, z + 1);
		this.setDoor(world, x, y + 3, z - 1);
	}

	private void setDoor(ChunkSplicedGenerationCache world, int x, int y, int z) {
		world.setTileEntity(x, y, z, door, doorMeta, this);
		((ShiftMazeGenerator) parent).addEndDoor(x, y, z);
	}

	@Override
	public void onTilePlaced(World world, int x, int y, int z, TileEntity te) {
		if (te instanceof BlockChromaDoor.TileEntityChromaDoor) {
			((BlockChromaDoor.TileEntityChromaDoor)te).bindUUID(null, doorid, 0);
		}
	}
}
