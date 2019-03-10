/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure.GOL;

import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.StructurePiece;
import Reika.ChromatiCraft.Block.BlockChromaDoor.TileEntityChromaDoor;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.Structure.GOLGenerator;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache.TileCallback;


public class GOLDoors extends StructurePiece {

	public GOLDoors(DimensionStructureGenerator s) {
		super(s);
	}

	@Override
	public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {
		Block b = ChromaBlocks.STRUCTSHIELD.getBlockInstance();
		int mc = BlockType.CLOAK.metadata;
		int ml = BlockType.LIGHT.metadata;
		this.generateEntrance(world, x-GOLGenerator.SIZE-1, y, z, b, mc, ml);

		z += 4;

		this.generateExit(world, x+GOLGenerator.SIZE+1, y, z, b, mc, ml);
	}

	private void generateEntrance(ChunkSplicedGenerationCache world, int x, int y, int z, Block b, int mc, int ml) {
		world.setBlock(x+0, y+0, z+0, b, mc);
		world.setBlock(x+0, y+0, z+1, b, mc);
		world.setBlock(x+0, y+0, z+15, b, mc);
		world.setBlock(x+0, y+0, z+16, b, mc);
		world.setBlock(x+0, y+1, z+0, b, mc);
		world.setBlock(x+0, y+1, z+1, b, mc);
		world.setBlock(x+0, y+1, z+15, b, mc);
		world.setBlock(x+0, y+1, z+16, b, mc);
		world.setBlock(x+0, y+2, z+0, b, mc);
		world.setBlock(x+0, y+2, z+1, b, mc);
		world.setBlock(x+0, y+2, z+15, b, mc);
		world.setBlock(x+0, y+2, z+16, b, mc);
		world.setBlock(x+0, y+3, z+0, b, mc);
		world.setBlock(x+0, y+3, z+1, b, mc);
		world.setBlock(x+0, y+3, z+2, b, mc);
		world.setBlock(x+0, y+3, z+14, b, mc);
		world.setBlock(x+0, y+3, z+15, b, mc);
		world.setBlock(x+0, y+3, z+16, b, mc);
		world.setBlock(x+0, y+4, z+0, b, mc);
		world.setBlock(x+0, y+4, z+1, b, mc);
		world.setBlock(x+0, y+4, z+2, b, mc);
		world.setBlock(x+0, y+4, z+3, b, mc);
		world.setBlock(x+0, y+4, z+13, b, mc);
		world.setBlock(x+0, y+4, z+14, b, mc);
		world.setBlock(x+0, y+4, z+15, b, mc);
		world.setBlock(x+0, y+4, z+16, b, mc);
		world.setBlock(x+0, y+5, z+0, b, mc);
		world.setBlock(x+0, y+5, z+1, b, mc);
		world.setBlock(x+0, y+5, z+2, b, ml);
		world.setBlock(x+0, y+5, z+3, b, mc);
		world.setBlock(x+0, y+5, z+4, b, mc);
		world.setBlock(x+0, y+5, z+5, b, mc);
		world.setBlock(x+0, y+5, z+11, b, mc);
		world.setBlock(x+0, y+5, z+12, b, mc);
		world.setBlock(x+0, y+5, z+13, b, mc);
		world.setBlock(x+0, y+5, z+14, b, ml);
		world.setBlock(x+0, y+5, z+15, b, mc);
		world.setBlock(x+0, y+5, z+16, b, mc);
		world.setBlock(x+0, y+6, z+0, b, mc);
		world.setBlock(x+0, y+6, z+1, b, ml);
		world.setBlock(x+0, y+6, z+2, b, mc);
		world.setBlock(x+0, y+6, z+3, b, mc);
		world.setBlock(x+0, y+6, z+4, b, mc);
		world.setBlock(x+0, y+6, z+5, b, mc);
		world.setBlock(x+0, y+6, z+6, b, mc);
		world.setBlock(x+0, y+6, z+7, b, mc);
		world.setBlock(x+0, y+6, z+8, b, mc);
		world.setBlock(x+0, y+6, z+9, b, mc);
		world.setBlock(x+0, y+6, z+10, b, mc);
		world.setBlock(x+0, y+6, z+11, b, mc);
		world.setBlock(x+0, y+6, z+12, b, mc);
		world.setBlock(x+0, y+6, z+13, b, mc);
		world.setBlock(x+0, y+6, z+14, b, mc);
		world.setBlock(x+0, y+6, z+15, b, ml);
		world.setBlock(x+0, y+6, z+16, b, mc);
		world.setBlock(x+0, y+7, z+0, b, ml);
		world.setBlock(x+0, y+7, z+1, b, mc);
		world.setBlock(x+0, y+7, z+2, b, mc);
		world.setBlock(x+0, y+7, z+3, b, mc);
		world.setBlock(x+0, y+7, z+4, b, mc);
		world.setBlock(x+0, y+7, z+5, b, mc);
		world.setBlock(x+0, y+7, z+6, b, mc);
		world.setBlock(x+0, y+7, z+7, b, mc);
		world.setBlock(x+0, y+7, z+8, b, ml);
		world.setBlock(x+0, y+7, z+9, b, mc);
		world.setBlock(x+0, y+7, z+10, b, mc);
		world.setBlock(x+0, y+7, z+11, b, mc);
		world.setBlock(x+0, y+7, z+12, b, mc);
		world.setBlock(x+0, y+7, z+13, b, mc);
		world.setBlock(x+0, y+7, z+14, b, mc);
		world.setBlock(x+0, y+7, z+15, b, mc);
		world.setBlock(x+0, y+7, z+16, b, ml);

		world.setBlock(x+0, y+0, z+2, STRUCTURE_AIR);
		world.setBlock(x+0, y+0, z+3, STRUCTURE_AIR);
		world.setBlock(x+0, y+0, z+4, STRUCTURE_AIR);
		world.setBlock(x+0, y+0, z+5, STRUCTURE_AIR);
		world.setBlock(x+0, y+0, z+6, STRUCTURE_AIR);
		world.setBlock(x+0, y+0, z+7, STRUCTURE_AIR);
		world.setBlock(x+0, y+0, z+8, STRUCTURE_AIR);
		world.setBlock(x+0, y+0, z+9, STRUCTURE_AIR);
		world.setBlock(x+0, y+0, z+10, STRUCTURE_AIR);
		world.setBlock(x+0, y+0, z+11, STRUCTURE_AIR);
		world.setBlock(x+0, y+0, z+12, STRUCTURE_AIR);
		world.setBlock(x+0, y+0, z+13, STRUCTURE_AIR);
		world.setBlock(x+0, y+0, z+14, STRUCTURE_AIR);
		world.setBlock(x+0, y+1, z+2, STRUCTURE_AIR);
		world.setBlock(x+0, y+1, z+3, STRUCTURE_AIR);
		world.setBlock(x+0, y+1, z+4, STRUCTURE_AIR);
		world.setBlock(x+0, y+1, z+5, STRUCTURE_AIR);
		world.setBlock(x+0, y+1, z+6, STRUCTURE_AIR);
		world.setBlock(x+0, y+1, z+7, STRUCTURE_AIR);
		world.setBlock(x+0, y+1, z+8, STRUCTURE_AIR);
		world.setBlock(x+0, y+1, z+9, STRUCTURE_AIR);
		world.setBlock(x+0, y+1, z+10, STRUCTURE_AIR);
		world.setBlock(x+0, y+1, z+11, STRUCTURE_AIR);
		world.setBlock(x+0, y+1, z+12, STRUCTURE_AIR);
		world.setBlock(x+0, y+1, z+13, STRUCTURE_AIR);
		world.setBlock(x+0, y+1, z+14, STRUCTURE_AIR);
		world.setBlock(x+0, y+2, z+2, STRUCTURE_AIR);
		world.setBlock(x+0, y+2, z+3, STRUCTURE_AIR);
		world.setBlock(x+0, y+2, z+4, STRUCTURE_AIR);
		world.setBlock(x+0, y+2, z+5, STRUCTURE_AIR);
		world.setBlock(x+0, y+2, z+6, STRUCTURE_AIR);
		world.setBlock(x+0, y+2, z+7, STRUCTURE_AIR);
		world.setBlock(x+0, y+2, z+8, STRUCTURE_AIR);
		world.setBlock(x+0, y+2, z+9, STRUCTURE_AIR);
		world.setBlock(x+0, y+2, z+10, STRUCTURE_AIR);
		world.setBlock(x+0, y+2, z+11, STRUCTURE_AIR);
		world.setBlock(x+0, y+2, z+12, STRUCTURE_AIR);
		world.setBlock(x+0, y+2, z+13, STRUCTURE_AIR);
		world.setBlock(x+0, y+2, z+14, STRUCTURE_AIR);
		world.setBlock(x+0, y+3, z+3, STRUCTURE_AIR);
		world.setBlock(x+0, y+3, z+4, STRUCTURE_AIR);
		world.setBlock(x+0, y+3, z+5, STRUCTURE_AIR);
		world.setBlock(x+0, y+3, z+6, STRUCTURE_AIR);
		world.setBlock(x+0, y+3, z+7, STRUCTURE_AIR);
		world.setBlock(x+0, y+3, z+8, STRUCTURE_AIR);
		world.setBlock(x+0, y+3, z+9, STRUCTURE_AIR);
		world.setBlock(x+0, y+3, z+10, STRUCTURE_AIR);
		world.setBlock(x+0, y+3, z+11, STRUCTURE_AIR);
		world.setBlock(x+0, y+3, z+12, STRUCTURE_AIR);
		world.setBlock(x+0, y+3, z+13, STRUCTURE_AIR);
		world.setBlock(x+0, y+4, z+4, STRUCTURE_AIR);
		world.setBlock(x+0, y+4, z+5, STRUCTURE_AIR);
		world.setBlock(x+0, y+4, z+6, STRUCTURE_AIR);
		world.setBlock(x+0, y+4, z+7, STRUCTURE_AIR);
		world.setBlock(x+0, y+4, z+8, STRUCTURE_AIR);
		world.setBlock(x+0, y+4, z+9, STRUCTURE_AIR);
		world.setBlock(x+0, y+4, z+10, STRUCTURE_AIR);
		world.setBlock(x+0, y+4, z+11, STRUCTURE_AIR);
		world.setBlock(x+0, y+4, z+12, STRUCTURE_AIR);
		world.setBlock(x+0, y+5, z+6, STRUCTURE_AIR);
		world.setBlock(x+0, y+5, z+7, STRUCTURE_AIR);
		world.setBlock(x+0, y+5, z+8, STRUCTURE_AIR);
		world.setBlock(x+0, y+5, z+9, STRUCTURE_AIR);
		world.setBlock(x+0, y+5, z+10, STRUCTURE_AIR);
	}

	private void generateExit(ChunkSplicedGenerationCache world, int x, int y, int z, Block b, int mc, int ml) {
		world.setBlock(x+0, y+0, z+0, b, mc);
		world.setBlock(x+0, y+0, z+1, b, mc);
		world.setBlock(x+0, y+0, z+7, b, mc);
		world.setBlock(x+0, y+0, z+8, b, mc);
		world.setBlock(x+0, y+1, z+0, b, mc);
		world.setBlock(x+0, y+1, z+1, b, mc);
		world.setBlock(x+0, y+1, z+7, b, mc);
		world.setBlock(x+0, y+1, z+8, b, mc);
		world.setBlock(x+0, y+2, z+0, b, mc);
		world.setBlock(x+0, y+2, z+1, b, mc);
		world.setBlock(x+0, y+2, z+7, b, mc);
		world.setBlock(x+0, y+2, z+8, b, mc);
		world.setBlock(x+0, y+3, z+0, b, ml);
		world.setBlock(x+0, y+3, z+1, b, mc);
		world.setBlock(x+0, y+3, z+2, b, mc);
		world.setBlock(x+0, y+3, z+6, b, mc);
		world.setBlock(x+0, y+3, z+7, b, mc);
		world.setBlock(x+0, y+3, z+8, b, ml);
		world.setBlock(x+0, y+4, z+0, b, mc);
		world.setBlock(x+0, y+4, z+1, b, mc);
		world.setBlock(x+0, y+4, z+2, b, mc);
		world.setBlock(x+0, y+4, z+3, b, mc);
		world.setBlock(x+0, y+4, z+4, b, mc);
		world.setBlock(x+0, y+4, z+5, b, mc);
		world.setBlock(x+0, y+4, z+6, b, mc);
		world.setBlock(x+0, y+4, z+7, b, mc);
		world.setBlock(x+0, y+4, z+8, b, mc);

		this.placeGate(world, x+0, y+0, z+2);
		this.placeGate(world, x+0, y+0, z+3);
		this.placeGate(world, x+0, y+0, z+4);
		this.placeGate(world, x+0, y+0, z+5);
		this.placeGate(world, x+0, y+0, z+6);
		this.placeGate(world, x+0, y+1, z+2);
		this.placeGate(world, x+0, y+1, z+3);
		this.placeGate(world, x+0, y+1, z+4);
		this.placeGate(world, x+0, y+1, z+5);
		this.placeGate(world, x+0, y+1, z+6);
		this.placeGate(world, x+0, y+2, z+2);
		this.placeGate(world, x+0, y+2, z+3);
		this.placeGate(world, x+0, y+2, z+4);
		this.placeGate(world, x+0, y+2, z+5);
		this.placeGate(world, x+0, y+2, z+6);
		this.placeGate(world, x+0, y+3, z+3);
		this.placeGate(world, x+0, y+3, z+4);
		this.placeGate(world, x+0, y+3, z+5);
	}

	private void placeGate(ChunkSplicedGenerationCache world, int x, int y, int z) {
		world.setTileEntity(x, y, z, ChromaBlocks.DOOR.getBlockInstance(), 0, new DoorCallback(parent.id));
	}

	private static class DoorCallback implements TileCallback {

		private final UUID id;

		private DoorCallback(UUID uid) {
			id = uid;
		}

		@Override
		public void onTilePlaced(World world, int x, int y, int z, TileEntity te) {
			if (te instanceof TileEntityChromaDoor) {
				((TileEntityChromaDoor)te).bindUUID(null, id, 0);
			}
		}

	}

}
