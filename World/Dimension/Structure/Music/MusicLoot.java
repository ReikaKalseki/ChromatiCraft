/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure.Music;


import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

import Reika.ChromatiCraft.Base.StructurePiece;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.Structure.MusicPuzzleGenerator;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;


public class MusicLoot extends StructurePiece<MusicPuzzleGenerator> {

	public MusicLoot(MusicPuzzleGenerator s) {
		super(s);
	}

	@Override
	public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {

		x += 2; //not sure why

		this.generateBlocks(world, x, y, z);
		this.generateAir(world, x, y, z);

		this.placeCore(x+3, y+2, z+5);

		parent.addBreakable(x, y+1, z+4);
		parent.addBreakable(x, y+2, z+4);
		parent.addBreakable(x, y+1, z+5);
		parent.addBreakable(x, y+2, z+5);

		parent.addBreakable(x+6, y+1, z+4);
		parent.addBreakable(x+6, y+2, z+4);
		parent.addBreakable(x+6, y+1, z+5);
		parent.addBreakable(x+6, y+2, z+5);
	}

	private void generateBlocks(ChunkSplicedGenerationCache world, int x, int y, int z) {
		Block b = ChromaBlocks.STRUCTSHIELD.getBlockInstance();
		int m = BlockType.STONE.metadata;

		world.setBlock(x+3, y+2, z+3, b, BlockType.GLASS.metadata);
		world.setBlock(x+3, y+0, z+1, b, BlockType.LIGHT.metadata);
		world.setBlock(x+3, y+2, z+7, b, BlockType.LIGHT.metadata);

		world.setBlock(x+0, y+0, z+0, b, m);
		world.setBlock(x+0, y+0, z+1, b, m);
		world.setBlock(x+0, y+0, z+2, b, m);
		world.setBlock(x+0, y+0, z+3, b, m);
		world.setBlock(x+0, y+0, z+4, b, m);
		world.setBlock(x+0, y+0, z+5, b, m);
		world.setBlock(x+0, y+0, z+6, b, m);
		world.setBlock(x+0, y+1, z+0, b, m);
		world.setBlock(x+0, y+1, z+1, b, m);
		world.setBlock(x+0, y+1, z+2, b, m);
		world.setBlock(x+0, y+1, z+3, b, m);
		world.setBlock(x+0, y+1, z+4, b, m);
		world.setBlock(x+0, y+1, z+5, b, m);
		world.setBlock(x+0, y+1, z+6, b, m);
		world.setBlock(x+0, y+2, z+0, b, m);
		world.setBlock(x+0, y+2, z+1, b, m);
		world.setBlock(x+0, y+2, z+2, b, m);
		world.setBlock(x+0, y+2, z+3, b, m);
		world.setBlock(x+0, y+2, z+4, b, m);
		world.setBlock(x+0, y+2, z+5, b, m);
		world.setBlock(x+0, y+2, z+6, b, m);
		world.setBlock(x+0, y+3, z+0, b, m);
		world.setBlock(x+0, y+3, z+1, b, m);
		world.setBlock(x+0, y+3, z+2, b, m);
		world.setBlock(x+0, y+3, z+3, b, m);
		world.setBlock(x+0, y+3, z+4, b, m);
		world.setBlock(x+0, y+3, z+5, b, m);
		world.setBlock(x+0, y+3, z+6, b, m);
		world.setBlock(x+0, y+4, z+0, b, m);
		world.setBlock(x+0, y+4, z+1, b, m);
		world.setBlock(x+0, y+4, z+2, b, m);
		world.setBlock(x+0, y+4, z+3, b, m);
		world.setBlock(x+0, y+4, z+4, b, m);
		world.setBlock(x+0, y+4, z+5, b, m);
		world.setBlock(x+0, y+4, z+6, b, m);
		world.setBlock(x+0, y+5, z+0, b, m);
		world.setBlock(x+0, y+5, z+1, b, m);
		world.setBlock(x+0, y+5, z+2, b, m);
		world.setBlock(x+0, y+5, z+3, b, m);
		world.setBlock(x+0, y+5, z+4, b, m);
		world.setBlock(x+0, y+5, z+5, b, m);
		world.setBlock(x+0, y+5, z+6, b, m);
		world.setBlock(x+1, y+0, z+0, b, m);
		world.setBlock(x+1, y+0, z+1, b, m);
		world.setBlock(x+1, y+0, z+2, b, m);
		world.setBlock(x+1, y+0, z+3, b, m);
		world.setBlock(x+1, y+0, z+4, b, m);
		world.setBlock(x+1, y+0, z+5, b, m);
		world.setBlock(x+1, y+0, z+6, b, m);
		world.setBlock(x+1, y+0, z+7, b, m);
		world.setBlock(x+1, y+1, z+0, b, m);
		world.setBlock(x+1, y+1, z+6, b, m);
		world.setBlock(x+1, y+1, z+7, b, m);
		world.setBlock(x+1, y+2, z+0, b, m);
		world.setBlock(x+1, y+2, z+6, b, m);
		world.setBlock(x+1, y+2, z+7, b, m);
		world.setBlock(x+1, y+3, z+0, b, m);
		world.setBlock(x+1, y+3, z+6, b, m);
		world.setBlock(x+1, y+3, z+7, b, m);
		world.setBlock(x+1, y+4, z+0, b, m);
		world.setBlock(x+1, y+4, z+6, b, m);
		world.setBlock(x+1, y+4, z+7, b, m);
		world.setBlock(x+1, y+5, z+0, b, m);
		world.setBlock(x+1, y+5, z+1, b, m);
		world.setBlock(x+1, y+5, z+2, b, m);
		world.setBlock(x+1, y+5, z+3, b, m);
		world.setBlock(x+1, y+5, z+4, b, m);
		world.setBlock(x+1, y+5, z+5, b, m);
		world.setBlock(x+1, y+5, z+6, b, m);
		world.setBlock(x+2, y+0, z+0, b, m);
		world.setBlock(x+2, y+0, z+1, b, m);
		world.setBlock(x+2, y+0, z+2, b, m);
		world.setBlock(x+2, y+0, z+3, b, m);
		world.setBlock(x+2, y+0, z+4, b, m);
		world.setBlock(x+2, y+0, z+5, b, m);
		world.setBlock(x+2, y+0, z+6, b, m);
		world.setBlock(x+2, y+0, z+7, b, m);
		world.setBlock(x+2, y+1, z+3, b, m);
		world.setBlock(x+2, y+1, z+7, b, m);
		world.setBlock(x+2, y+2, z+3, b, m);
		world.setBlock(x+2, y+2, z+7, b, m);
		world.setBlock(x+2, y+3, z+3, b, m);
		world.setBlock(x+2, y+3, z+7, b, m);
		world.setBlock(x+2, y+4, z+0, b, m);
		world.setBlock(x+2, y+4, z+3, b, m);
		world.setBlock(x+2, y+4, z+6, b, m);
		world.setBlock(x+2, y+4, z+7, b, m);
		world.setBlock(x+2, y+5, z+0, b, m);
		world.setBlock(x+2, y+5, z+1, b, m);
		world.setBlock(x+2, y+5, z+2, b, m);
		world.setBlock(x+2, y+5, z+3, b, m);
		world.setBlock(x+2, y+5, z+4, b, m);
		world.setBlock(x+2, y+5, z+5, b, m);
		world.setBlock(x+2, y+5, z+6, b, m);
		world.setBlock(x+3, y+0, z+0, b, m);
		world.setBlock(x+3, y+0, z+2, b, m);
		world.setBlock(x+3, y+0, z+3, b, m);
		world.setBlock(x+3, y+0, z+4, b, m);
		world.setBlock(x+3, y+0, z+5, b, m);
		world.setBlock(x+3, y+0, z+6, b, m);
		world.setBlock(x+3, y+0, z+7, b, m);
		world.setBlock(x+3, y+1, z+3, b, m);
		world.setBlock(x+3, y+1, z+7, b, m);
		world.setBlock(x+3, y+3, z+3, b, m);
		world.setBlock(x+3, y+3, z+7, b, m);
		world.setBlock(x+3, y+4, z+0, b, m);
		world.setBlock(x+3, y+4, z+3, b, m);
		world.setBlock(x+3, y+4, z+6, b, m);
		world.setBlock(x+3, y+4, z+7, b, m);
		world.setBlock(x+3, y+5, z+0, b, m);
		world.setBlock(x+3, y+5, z+1, b, m);
		world.setBlock(x+3, y+5, z+2, b, m);
		world.setBlock(x+3, y+5, z+3, b, m);
		world.setBlock(x+3, y+5, z+4, b, m);
		world.setBlock(x+3, y+5, z+5, b, m);
		world.setBlock(x+3, y+5, z+6, b, m);
		world.setBlock(x+4, y+0, z+0, b, m);
		world.setBlock(x+4, y+0, z+1, b, m);
		world.setBlock(x+4, y+0, z+2, b, m);
		world.setBlock(x+4, y+0, z+3, b, m);
		world.setBlock(x+4, y+0, z+4, b, m);
		world.setBlock(x+4, y+0, z+5, b, m);
		world.setBlock(x+4, y+0, z+6, b, m);
		world.setBlock(x+4, y+0, z+7, b, m);
		world.setBlock(x+4, y+1, z+3, b, m);
		world.setBlock(x+4, y+1, z+7, b, m);
		world.setBlock(x+4, y+2, z+3, b, m);
		world.setBlock(x+4, y+2, z+7, b, m);
		world.setBlock(x+4, y+3, z+3, b, m);
		world.setBlock(x+4, y+3, z+7, b, m);
		world.setBlock(x+4, y+4, z+0, b, m);
		world.setBlock(x+4, y+4, z+3, b, m);
		world.setBlock(x+4, y+4, z+6, b, m);
		world.setBlock(x+4, y+4, z+7, b, m);
		world.setBlock(x+4, y+5, z+0, b, m);
		world.setBlock(x+4, y+5, z+1, b, m);
		world.setBlock(x+4, y+5, z+2, b, m);
		world.setBlock(x+4, y+5, z+3, b, m);
		world.setBlock(x+4, y+5, z+4, b, m);
		world.setBlock(x+4, y+5, z+5, b, m);
		world.setBlock(x+4, y+5, z+6, b, m);
		world.setBlock(x+5, y+0, z+0, b, m);
		world.setBlock(x+5, y+0, z+1, b, m);
		world.setBlock(x+5, y+0, z+2, b, m);
		world.setBlock(x+5, y+0, z+3, b, m);
		world.setBlock(x+5, y+0, z+4, b, m);
		world.setBlock(x+5, y+0, z+5, b, m);
		world.setBlock(x+5, y+0, z+6, b, m);
		world.setBlock(x+5, y+0, z+7, b, m);
		world.setBlock(x+5, y+1, z+0, b, m);
		world.setBlock(x+5, y+1, z+6, b, m);
		world.setBlock(x+5, y+1, z+7, b, m);
		world.setBlock(x+5, y+2, z+0, b, m);
		world.setBlock(x+5, y+2, z+6, b, m);
		world.setBlock(x+5, y+2, z+7, b, m);
		world.setBlock(x+5, y+3, z+0, b, m);
		world.setBlock(x+5, y+3, z+6, b, m);
		world.setBlock(x+5, y+3, z+7, b, m);
		world.setBlock(x+5, y+4, z+0, b, m);
		world.setBlock(x+5, y+4, z+6, b, m);
		world.setBlock(x+5, y+4, z+7, b, m);
		world.setBlock(x+5, y+5, z+0, b, m);
		world.setBlock(x+5, y+5, z+1, b, m);
		world.setBlock(x+5, y+5, z+2, b, m);
		world.setBlock(x+5, y+5, z+3, b, m);
		world.setBlock(x+5, y+5, z+4, b, m);
		world.setBlock(x+5, y+5, z+5, b, m);
		world.setBlock(x+5, y+5, z+6, b, m);
		world.setBlock(x+6, y+0, z+0, b, m);
		world.setBlock(x+6, y+0, z+1, b, m);
		world.setBlock(x+6, y+0, z+2, b, m);
		world.setBlock(x+6, y+0, z+3, b, m);
		world.setBlock(x+6, y+0, z+4, b, m);
		world.setBlock(x+6, y+0, z+5, b, m);
		world.setBlock(x+6, y+0, z+6, b, m);
		world.setBlock(x+6, y+1, z+0, b, m);
		world.setBlock(x+6, y+1, z+1, b, m);
		world.setBlock(x+6, y+1, z+2, b, m);
		world.setBlock(x+6, y+1, z+3, b, m);
		world.setBlock(x+6, y+1, z+4, b, m);
		world.setBlock(x+6, y+1, z+5, b, m);
		world.setBlock(x+6, y+1, z+6, b, m);
		world.setBlock(x+6, y+2, z+0, b, m);
		world.setBlock(x+6, y+2, z+1, b, m);
		world.setBlock(x+6, y+2, z+2, b, m);
		world.setBlock(x+6, y+2, z+3, b, m);
		world.setBlock(x+6, y+2, z+4, b, m);
		world.setBlock(x+6, y+2, z+5, b, m);
		world.setBlock(x+6, y+2, z+6, b, m);
		world.setBlock(x+6, y+3, z+0, b, m);
		world.setBlock(x+6, y+3, z+1, b, m);
		world.setBlock(x+6, y+3, z+2, b, m);
		world.setBlock(x+6, y+3, z+3, b, m);
		world.setBlock(x+6, y+3, z+4, b, m);
		world.setBlock(x+6, y+3, z+5, b, m);
		world.setBlock(x+6, y+3, z+6, b, m);
		world.setBlock(x+6, y+4, z+0, b, m);
		world.setBlock(x+6, y+4, z+1, b, m);
		world.setBlock(x+6, y+4, z+2, b, m);
		world.setBlock(x+6, y+4, z+3, b, m);
		world.setBlock(x+6, y+4, z+4, b, m);
		world.setBlock(x+6, y+4, z+5, b, m);
		world.setBlock(x+6, y+4, z+6, b, m);
		world.setBlock(x+6, y+5, z+0, b, m);
		world.setBlock(x+6, y+5, z+1, b, m);
		world.setBlock(x+6, y+5, z+2, b, m);
		world.setBlock(x+6, y+5, z+3, b, m);
		world.setBlock(x+6, y+5, z+4, b, m);
		world.setBlock(x+6, y+5, z+5, b, m);
		world.setBlock(x+6, y+5, z+6, b, m);
	}

	private void generateAir(ChunkSplicedGenerationCache world, int x, int y, int z) {
		world.setBlock(x+1, y+1, z+1, Blocks.air);
		world.setBlock(x+1, y+1, z+2, Blocks.air);
		world.setBlock(x+1, y+1, z+3, Blocks.air);
		world.setBlock(x+1, y+1, z+4, Blocks.air);
		world.setBlock(x+1, y+1, z+5, Blocks.air);
		world.setBlock(x+1, y+2, z+1, Blocks.air);
		world.setBlock(x+1, y+2, z+2, Blocks.air);
		world.setBlock(x+1, y+2, z+3, Blocks.air);
		world.setBlock(x+1, y+2, z+4, Blocks.air);
		world.setBlock(x+1, y+2, z+5, Blocks.air);
		world.setBlock(x+1, y+3, z+1, Blocks.air);
		world.setBlock(x+1, y+3, z+2, Blocks.air);
		world.setBlock(x+1, y+3, z+3, Blocks.air);
		world.setBlock(x+1, y+3, z+4, Blocks.air);
		world.setBlock(x+1, y+3, z+5, Blocks.air);
		world.setBlock(x+1, y+4, z+1, Blocks.air);
		world.setBlock(x+1, y+4, z+2, Blocks.air);
		world.setBlock(x+1, y+4, z+3, Blocks.air);
		world.setBlock(x+1, y+4, z+4, Blocks.air);
		world.setBlock(x+1, y+4, z+5, Blocks.air);
		world.setBlock(x+2, y+1, z+0, Blocks.air);
		world.setBlock(x+2, y+1, z+1, Blocks.air);
		world.setBlock(x+2, y+1, z+2, Blocks.air);
		world.setBlock(x+2, y+1, z+4, Blocks.air);
		world.setBlock(x+2, y+1, z+5, Blocks.air);
		world.setBlock(x+2, y+1, z+6, Blocks.air);
		world.setBlock(x+2, y+2, z+0, Blocks.air);
		world.setBlock(x+2, y+2, z+1, Blocks.air);
		world.setBlock(x+2, y+2, z+2, Blocks.air);
		world.setBlock(x+2, y+2, z+4, Blocks.air);
		world.setBlock(x+2, y+2, z+5, Blocks.air);
		world.setBlock(x+2, y+2, z+6, Blocks.air);
		world.setBlock(x+2, y+3, z+0, Blocks.air);
		world.setBlock(x+2, y+3, z+1, Blocks.air);
		world.setBlock(x+2, y+3, z+2, Blocks.air);
		world.setBlock(x+2, y+3, z+4, Blocks.air);
		world.setBlock(x+2, y+3, z+5, Blocks.air);
		world.setBlock(x+2, y+3, z+6, Blocks.air);
		world.setBlock(x+2, y+4, z+1, Blocks.air);
		world.setBlock(x+2, y+4, z+2, Blocks.air);
		world.setBlock(x+2, y+4, z+4, Blocks.air);
		world.setBlock(x+2, y+4, z+5, Blocks.air);
		world.setBlock(x+3, y+1, z+0, Blocks.air);
		world.setBlock(x+3, y+1, z+1, Blocks.air);
		world.setBlock(x+3, y+1, z+2, Blocks.air);
		world.setBlock(x+3, y+1, z+4, Blocks.air);
		world.setBlock(x+3, y+1, z+5, Blocks.air);
		world.setBlock(x+3, y+1, z+6, Blocks.air);
		world.setBlock(x+3, y+2, z+0, Blocks.air);
		world.setBlock(x+3, y+2, z+1, Blocks.air);
		world.setBlock(x+3, y+2, z+2, Blocks.air);
		world.setBlock(x+3, y+2, z+4, Blocks.air);
		world.setBlock(x+3, y+2, z+6, Blocks.air);
		world.setBlock(x+3, y+3, z+0, Blocks.air);
		world.setBlock(x+3, y+3, z+1, Blocks.air);
		world.setBlock(x+3, y+3, z+2, Blocks.air);
		world.setBlock(x+3, y+3, z+4, Blocks.air);
		world.setBlock(x+3, y+3, z+5, Blocks.air);
		world.setBlock(x+3, y+3, z+6, Blocks.air);
		world.setBlock(x+3, y+4, z+1, Blocks.air);
		world.setBlock(x+3, y+4, z+2, Blocks.air);
		world.setBlock(x+3, y+4, z+4, Blocks.air);
		world.setBlock(x+3, y+4, z+5, Blocks.air);
		world.setBlock(x+4, y+1, z+0, Blocks.air);
		world.setBlock(x+4, y+1, z+1, Blocks.air);
		world.setBlock(x+4, y+1, z+2, Blocks.air);
		world.setBlock(x+4, y+1, z+4, Blocks.air);
		world.setBlock(x+4, y+1, z+5, Blocks.air);
		world.setBlock(x+4, y+1, z+6, Blocks.air);
		world.setBlock(x+4, y+2, z+0, Blocks.air);
		world.setBlock(x+4, y+2, z+1, Blocks.air);
		world.setBlock(x+4, y+2, z+2, Blocks.air);
		world.setBlock(x+4, y+2, z+4, Blocks.air);
		world.setBlock(x+4, y+2, z+5, Blocks.air);
		world.setBlock(x+4, y+2, z+6, Blocks.air);
		world.setBlock(x+4, y+3, z+0, Blocks.air);
		world.setBlock(x+4, y+3, z+1, Blocks.air);
		world.setBlock(x+4, y+3, z+2, Blocks.air);
		world.setBlock(x+4, y+3, z+4, Blocks.air);
		world.setBlock(x+4, y+3, z+5, Blocks.air);
		world.setBlock(x+4, y+3, z+6, Blocks.air);
		world.setBlock(x+4, y+4, z+1, Blocks.air);
		world.setBlock(x+4, y+4, z+2, Blocks.air);
		world.setBlock(x+4, y+4, z+4, Blocks.air);
		world.setBlock(x+4, y+4, z+5, Blocks.air);
		world.setBlock(x+5, y+1, z+1, Blocks.air);
		world.setBlock(x+5, y+1, z+2, Blocks.air);
		world.setBlock(x+5, y+1, z+3, Blocks.air);
		world.setBlock(x+5, y+1, z+4, Blocks.air);
		world.setBlock(x+5, y+1, z+5, Blocks.air);
		world.setBlock(x+5, y+2, z+1, Blocks.air);
		world.setBlock(x+5, y+2, z+2, Blocks.air);
		world.setBlock(x+5, y+2, z+3, Blocks.air);
		world.setBlock(x+5, y+2, z+4, Blocks.air);
		world.setBlock(x+5, y+2, z+5, Blocks.air);
		world.setBlock(x+5, y+3, z+1, Blocks.air);
		world.setBlock(x+5, y+3, z+2, Blocks.air);
		world.setBlock(x+5, y+3, z+3, Blocks.air);
		world.setBlock(x+5, y+3, z+4, Blocks.air);
		world.setBlock(x+5, y+3, z+5, Blocks.air);
		world.setBlock(x+5, y+4, z+1, Blocks.air);
		world.setBlock(x+5, y+4, z+2, Blocks.air);
		world.setBlock(x+5, y+4, z+3, Blocks.air);
		world.setBlock(x+5, y+4, z+4, Blocks.air);
		world.setBlock(x+5, y+4, z+5, Blocks.air);
	}

}
