/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure.ShiftMaze;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.StructurePiece;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.Instantiable.Data.WeightedRandom;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;

public class MazeAnchor extends StructurePiece {

	private final int partSize;
	private final int genMetadata;
	private final BlockKey pyramidBlock;

	private static final WeightedRandom<BlockKey> blockRand = new WeightedRandom();

	static {
		blockRand.addEntry(new BlockKey(Blocks.glowstone), 30);
		blockRand.addEntry(new BlockKey(Blocks.coal_block), 50);
		blockRand.addEntry(new BlockKey(Blocks.quartz_block, 0), 25);
		blockRand.addEntry(new BlockKey(Blocks.quartz_block, 1), 25);
		blockRand.addEntry(new BlockKey(Blocks.redstone_block), 40);
		blockRand.addEntry(new BlockKey(Blocks.lapis_block), 20);
	}

	public MazeAnchor(DimensionStructureGenerator s, int size, int meta, Random r) {
		super(s);
		partSize = size*3;
		genMetadata = meta;

		pyramidBlock = this.getRandomBlockType(s, r);
	}

	private static BlockKey getRandomBlockType(DimensionStructureGenerator s, Random r) {
		return blockRand.getRandomEntry();
	}

	@Override
	public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {
		Block b = ChromaBlocks.STRUCTSHIELD.getBlockInstance();
		int meta = BlockType.STONE.metadata;
		for (int i = 0; i <= partSize; i++) {
			for (int k = 0; k <= partSize; k++) {
				int dx = x+i-partSize/2+2;
				int dz = z+k-partSize/2+2;
				world.setBlock(dx, y, dz, b, meta);
			}
		}

		int h = 3;
		for (int j = 0; j <= h; j++) {
			int in = j+4;
			for (int i = in; i <= partSize-in; i++) {
				for (int k = in; k <= partSize-in; k++) {
					int dx = x+i-partSize/2+2;//+partSize*3/2;
					int dz = z+k-partSize/2+2;//+partSize*3/2;
					world.setBlock(dx, y+j+1, dz, pyramidBlock);
				}
			}
		}

		int dx = x-partSize/2+8;//+partSize*3/2;
		int dz = z-partSize/2+8;//+partSize*3/2;
		world.setBlock(dx, y+1, dz, ChromaBlocks.SHIFTKEY.getBlockInstance(), genMetadata);
	}

}
