package Reika.ChromatiCraft.World.Dimension.Structure.ShiftMaze;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.StructurePiece;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.Instantiable.Data.BlockKey;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;

public class MazeAnchor extends StructurePiece {

	private final int partSize;
	private final int genMetadata;
	private final BlockKey pyramidBlock;

	private static final HashSet<BlockKey> used = new HashSet();
	private static final ArrayList<BlockKey> valid = new ArrayList();

	public MazeAnchor(DimensionStructureGenerator s, int size, int meta, Random r) {
		super(s);
		partSize = size*3;
		genMetadata = meta;

		pyramidBlock = this.getRandomBlockType(s, r);
	}

	private BlockKey getRandomBlockType(DimensionStructureGenerator s, Random r) {
		BlockKey bk = null;
		do {
			bk = valid.get(r.nextInt(valid.size()));
		} while(used.contains(bk));
		used.add(bk);
		return bk;
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

			if (j == h) {
				int dx = x-partSize/2+8;//+partSize*3/2;
				int dz = z-partSize/2+8;//+partSize*3/2;
				world.setBlock(dx, y+j+1, dz, ChromaBlocks.SHIFTKEY.getBlockInstance(), genMetadata);
			}
			else {
				for (int i = in; i <= partSize-in; i++) {
					for (int k = in; k <= partSize-in; k++) {
						int dx = x+i-partSize/2+2;//+partSize*3/2;
						int dz = z+k-partSize/2+2;//+partSize*3/2;
						world.setBlock(dx, y+j+1, dz, pyramidBlock);
					}
				}
			}
		}
	}

	static {
		addBlock(Blocks.glowstone);
		addBlock(Blocks.coal_block);
		addBlock(Blocks.quartz_block, 0);
		addBlock(Blocks.quartz_block, 1);
		addBlock(Blocks.redstone_block);
		addBlock(Blocks.lapis_block);
	}

	public static void clearGenCache() {
		used.clear();
	}

	private static void addBlock(Block b) {
		valid.add(new BlockKey(b));
	}

	private static void addBlock(Block b, int meta) {
		valid.add(new BlockKey(b, meta));
	}

}
