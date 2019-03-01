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

import net.minecraft.block.Block;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;

public class MazePartEndDoorConnector extends StructurePieceChainable {

	public MazePartEndDoorConnector(DimensionStructureGenerator s) {
		super(s);
	}

	@Override
	public int getCursorStepWidth() {
		return -5;
	}

	@Override
	public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {
		this.genRingOuter(world, x, y, z);
		this.genRingInner(world, x - 1, y, z);
		this.genRingCenter(world, x - 2, y, z);
		this.genRingInner(world, x - 3, y, z);
		this.genRingOuter (world, x - 4, y, z);
	}

	private void genRingCenter(ChunkSplicedGenerationCache world, int x, int y, int z) {
		Block sh = ChromaBlocks.STRUCTSHIELD.getBlockInstance();
		int ms = BlockStructureShield.BlockType.STONE.metadata;
		int ml = BlockStructureShield.BlockType.LIGHT.metadata;

		//Lower line
		world.setBlock(x, y, z,     sh, ml);
		world.setBlock(x, y, z + 1, sh, ms);
		world.setBlock(x, y, z + 2, sh, ms);
		world.setBlock(x, y, z + 3, sh, ml);
		world.setBlock(x, y, z + 4, sh, ms);
		world.setBlock(x, y, z - 1, sh, ms);
		world.setBlock(x, y, z - 2, sh, ms);
		world.setBlock(x, y, z - 3, sh, ml);
		world.setBlock(x, y, z - 4, sh, ms);
		//"Pillars Left right
		world.setBlock(x, y + 1, z + 4, sh, ms);
		world.setBlock(x, y + 1, z - 4, sh, ms);
		world.setBlock(x, y + 2, z + 4, sh, ms);
		world.setBlock(x, y + 2, z - 4, sh, ms);
		world.setBlock(x, y + 3, z + 4, sh, ms);
		world.setBlock(x, y + 3, z - 4, sh, ms);
		world.setBlock(x, y + 4, z + 4, sh, ms);
		world.setBlock(x, y + 4, z - 4, sh, ms);
		//Upper line
		world.setBlock(x, y + 5, z,     sh, ml);
		world.setBlock(x, y + 5, z + 1, sh, ms);
		world.setBlock(x, y + 5, z + 2, sh, ms);
		world.setBlock(x, y + 5, z + 3, sh, ml);
		world.setBlock(x, y + 5, z - 1, sh, ms);
		world.setBlock(x, y + 5, z - 2, sh, ms);
		world.setBlock(x, y + 5, z - 3, sh, ml);

		//Air
		world.setAir(x, y + 1, z    );
		world.setAir(x, y + 1, z + 1);
		world.setAir(x, y + 1, z + 2);
		world.setAir(x, y + 1, z + 3);
		world.setAir(x, y + 1, z - 1);
		world.setAir(x, y + 1, z - 2);
		world.setAir(x, y + 1, z - 3);
		world.setAir(x, y + 2, z    );
		world.setAir(x, y + 2, z + 1);
		world.setAir(x, y + 2, z + 2);
		world.setAir(x, y + 2, z + 3);
		world.setAir(x, y + 2, z - 1);
		world.setAir(x, y + 2, z - 2);
		world.setAir(x, y + 2, z - 3);
		world.setAir(x, y + 3, z    );
		world.setAir(x, y + 3, z + 1);
		world.setAir(x, y + 3, z + 2);
		world.setAir(x, y + 3, z + 3);
		world.setAir(x, y + 3, z - 1);
		world.setAir(x, y + 3, z - 2);
		world.setAir(x, y + 3, z - 3);
		world.setAir(x, y + 4, z    );
		world.setAir(x, y + 4, z + 1);
		world.setAir(x, y + 4, z + 2);
		world.setAir(x, y + 4, z + 3);
		world.setAir(x, y + 4, z - 1);
		world.setAir(x, y + 4, z - 2);
		world.setAir(x, y + 4, z - 3);

		parent.generateLootChest(x, y+1, z + 3, ForgeDirection.NORTH, ChestGenHooks.MINESHAFT_CORRIDOR, 0);
		parent.generateLootChest(x, y+1, z - 3, ForgeDirection.SOUTH, ChestGenHooks.MINESHAFT_CORRIDOR, 0);
	}

	private void genRingInner(ChunkSplicedGenerationCache world, int x, int y, int z) {
		Block sh = ChromaBlocks.STRUCTSHIELD.getBlockInstance();
		int ms = BlockStructureShield.BlockType.STONE.metadata;

		//Lower line
		world.setBlock(x, y, z,     sh, ms);
		world.setBlock(x, y, z + 1, sh, ms);
		world.setBlock(x, y, z + 2, sh, ms);
		world.setBlock(x, y, z + 3, sh, ms);
		world.setBlock(x, y, z + 4, sh, ms);
		world.setBlock(x, y, z - 1, sh, ms);
		world.setBlock(x, y, z - 2, sh, ms);
		world.setBlock(x, y, z - 3, sh, ms);
		world.setBlock(x, y, z - 4, sh, ms);
		//"Pillars Left right
		world.setBlock(x, y + 1, z + 4, sh, ms);
		world.setBlock(x, y + 1, z - 4, sh, ms);
		world.setBlock(x, y + 2, z + 4, sh, ms);
		world.setBlock(x, y + 2, z - 4, sh, ms);
		world.setBlock(x, y + 3, z + 4, sh, ms);
		world.setBlock(x, y + 3, z - 4, sh, ms);
		world.setBlock(x, y + 4, z + 4, sh, ms);
		world.setBlock(x, y + 4, z - 4, sh, ms);
		//Edges
		world.setBlock(x, y + 4, z + 3, sh, ms);
		world.setBlock(x, y + 4, z - 3, sh, ms);
		world.setBlock(x, y + 1, z + 3, sh, ms);
		world.setBlock(x, y + 1, z - 3, sh, ms);
		//Upper line
		world.setBlock(x, y + 5, z,     sh, ms);
		world.setBlock(x, y + 5, z + 1, sh, ms);
		world.setBlock(x, y + 5, z + 2, sh, ms);
		world.setBlock(x, y + 5, z + 3, sh, ms);
		world.setBlock(x, y + 5, z - 1, sh, ms);
		world.setBlock(x, y + 5, z - 2, sh, ms);
		world.setBlock(x, y + 5, z - 3, sh, ms);

		//Air
		world.setAir(x, y + 1, z    );
		world.setAir(x, y + 1, z + 1);
		world.setAir(x, y + 1, z + 2);
		world.setAir(x, y + 1, z - 1);
		world.setAir(x, y + 1, z - 2);
		world.setAir(x, y + 2, z    );
		world.setAir(x, y + 2, z + 1);
		world.setAir(x, y + 2, z + 2);
		world.setAir(x, y + 2, z + 3);
		world.setAir(x, y + 2, z - 1);
		world.setAir(x, y + 2, z - 2);
		world.setAir(x, y + 2, z - 3);
		world.setAir(x, y + 3, z    );
		world.setAir(x, y + 3, z + 1);
		world.setAir(x, y + 3, z + 2);
		world.setAir(x, y + 3, z + 3);
		world.setAir(x, y + 3, z - 1);
		world.setAir(x, y + 3, z - 2);
		world.setAir(x, y + 3, z - 3);
		world.setAir(x, y + 4, z    );
		world.setAir(x, y + 4, z + 1);
		world.setAir(x, y + 4, z + 2);
		world.setAir(x, y + 4, z - 1);
		world.setAir(x, y + 4, z - 2);
	}

	private void genRingOuter(ChunkSplicedGenerationCache world, int x, int y, int z) {
		Block sh = ChromaBlocks.STRUCTSHIELD.getBlockInstance();
		int ms = BlockStructureShield.BlockType.STONE.metadata;

		//Lower line
		world.setBlock(x, y, z,     sh, ms);
		world.setBlock(x, y, z + 1, sh, ms);
		world.setBlock(x, y, z + 2, sh, ms);
		world.setBlock(x, y, z + 3, sh, ms);
		world.setBlock(x, y, z - 1, sh, ms);
		world.setBlock(x, y, z - 2, sh, ms);
		world.setBlock(x, y, z - 3, sh, ms);
		//Upper line
		world.setBlock(x, y + 5, z,     sh, ms);
		world.setBlock(x, y + 5, z + 1, sh, ms);
		world.setBlock(x, y + 5, z + 2, sh, ms);
		world.setBlock(x, y + 5, z + 3, sh, ms);
		world.setBlock(x, y + 5, z - 1, sh, ms);
		world.setBlock(x, y + 5, z - 2, sh, ms);
		world.setBlock(x, y + 5, z - 3, sh, ms);
		//"Pillars Left right
		world.setBlock(x, y + 1, z + 3, sh, ms);
		world.setBlock(x, y + 1, z - 3, sh, ms);
		world.setBlock(x, y + 2, z + 3, sh, ms);
		world.setBlock(x, y + 2, z - 3, sh, ms);
		world.setBlock(x, y + 3, z + 3, sh, ms);
		world.setBlock(x, y + 3, z - 3, sh, ms);
		world.setBlock(x, y + 4, z + 3, sh, ms);
		world.setBlock(x, y + 4, z - 3, sh, ms);
		//Edges
		world.setBlock(x, y + 4, z + 2, sh, ms);
		world.setBlock(x, y + 4, z - 2, sh, ms);

		//Air
		world.setAir(x, y + 1, z    );
		world.setAir(x, y + 1, z + 1);
		world.setAir(x, y + 1, z + 2);
		world.setAir(x, y + 1, z - 1);
		world.setAir(x, y + 1, z - 2);
		world.setAir(x, y + 2, z    );
		world.setAir(x, y + 2, z + 1);
		world.setAir(x, y + 2, z + 2);
		world.setAir(x, y + 2, z - 1);
		world.setAir(x, y + 2, z - 2);
		world.setAir(x, y + 3, z    );
		world.setAir(x, y + 3, z + 1);
		world.setAir(x, y + 3, z + 2);
		world.setAir(x, y + 3, z - 1);
		world.setAir(x, y + 3, z - 2);
		world.setAir(x, y + 4, z    );
		world.setAir(x, y + 4, z + 1);
		world.setAir(x, y + 4, z - 1);
	}

}
