package Reika.ChromatiCraft.World.Nether;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.ChestGenHooks;
import Reika.ChromatiCraft.Auxiliary.ChromaAux;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;


public class NetherSpiral {

	private static Block b = ChromaBlocks.STRUCTSHIELD.getBlockInstance();
	private static int ms = BlockType.STONE.metadata;
	private static int mc = BlockType.CRACKS.metadata;

	static void generateAt(World world, int x, int y, int z) {
		for (int i = 0; i <= 18; i++) {
			for (int k = 0; k <= 18; k++) {
				world.setBlock(x+i, y, z+k, b, ms, 3);
			}
		}

		for (int i = 1; i <= 4; i++) {
			generateLayer(world, x, y, z, i);
		}

		for (int i = 1; i <= 17; i++) {
			world.setBlock(x+i, y+4, z+1, b, ms, 3);
			world.setBlock(x+i, y+4, z+17, b, ms, 3);
			world.setBlock(x+1, y+4, z+i, b, ms, 3);
			world.setBlock(x+17, y+4, z+i, b, ms, 3);
		}

		for (int i = 2; i <= 16; i++) {
			world.setBlock(x+i, y+5, z+2, b, ms, 3);
			world.setBlock(x+i, y+5, z+16, b, ms, 3);
			world.setBlock(x+2, y+5, z+i, b, ms, 3);
			world.setBlock(x+16, y+5, z+i, b, ms, 3);
		}

		for (int i = 3; i <= 15; i++) {
			for (int k = 3; k <= 15; k++) {
				if (i != 3 || i != 15 || k != 3 || k != 15) {
					world.setBlock(x+i, y+5, z+k, Blocks.netherrack);
					world.setBlock(x+i, y+6, z+k, Blocks.netherrack);
					world.setBlock(x+i, y+7, z+k, Blocks.lava);
					world.setBlock(x+i, y+8, z+k, b, ms, 3);
				}
			}
		}

		for (int i = 4; i <= 14; i++) {
			for (int h = 0; h <= 1; h++) {
				world.setBlock(x+i, y+6+h, z+2, b, ms, 3);
				world.setBlock(x+i, y+6+h, z+16, b, ms, 3);
				world.setBlock(x+2, y+6+h, z+i, b, ms, 3);
				world.setBlock(x+16, y+6+h, z+i, b, ms, 3);

				world.setBlock(x+3, y+6+h, z+3, b, ms, 3);
				world.setBlock(x+15, y+6+h, z+3, b, ms, 3);
				world.setBlock(x+3, y+6+h, z+15, b, ms, 3);
				world.setBlock(x+15, y+6+h, z+15, b, ms, 3);
			}
		}
	}

	private static void generateLayer(World world, int i, int y, int k, int j) {
		world.setBlock(i + 0, j+y, k + 0, Blocks.air);
		world.setBlock(i + 0, j+y, k + 1, Blocks.air);
		world.setBlock(i + 0, j+y, k + 2, Blocks.air);
		world.setBlock(i + 0, j+y, k + 3, b, ms, 3);
		world.setBlock(i + 0, j+y, k + 4, b, ms, 3);
		world.setBlock(i + 0, j+y, k + 5, b, ms, 3);
		world.setBlock(i + 0, j+y, k + 6, b, ms, 3);
		world.setBlock(i + 0, j+y, k + 7, b, ms, 3);
		world.setBlock(i + 0, j+y, k + 8, b, ms, 3);
		world.setBlock(i + 0, j+y, k + 9, b, ms, 3);
		world.setBlock(i + 0, j+y, k + 10, b, ms, 3);
		world.setBlock(i + 0, j+y, k + 11, b, ms, 3);
		world.setBlock(i + 0, j+y, k + 12, b, ms, 3);
		world.setBlock(i + 0, j+y, k + 13, b, ms, 3);
		world.setBlock(i + 0, j+y, k + 14, b, ms, 3);
		world.setBlock(i + 0, j+y, k + 15, b, ms, 3);
		world.setBlock(i + 0, j+y, k + 16, Blocks.air);
		world.setBlock(i + 0, j+y, k + 17, Blocks.air);
		world.setBlock(i + 0, j+y, k + 18, Blocks.air);
		world.setBlock(i + 1, j+y, k + 0, Blocks.air);
		world.setBlock(i + 1, j+y, k + 1, Blocks.air);
		world.setBlock(i + 1, j+y, k + 2, Blocks.air);
		world.setBlock(i + 1, j+y, k + 3, b, ms, 3);
		world.setBlock(i + 1, j+y, k + 4, b, ms, 3);
		world.setBlock(i + 1, j+y, k + 6, Blocks.air);
		world.setBlock(i + 1, j+y, k + 7, Blocks.air);
		world.setBlock(i + 1, j+y, k + 8, Blocks.air);
		world.setBlock(i + 1, j+y, k + 9, Blocks.air);
		world.setBlock(i + 1, j+y, k + 10, Blocks.air);
		world.setBlock(i + 1, j+y, k + 11, Blocks.air);
		world.setBlock(i + 1, j+y, k + 12, Blocks.air);
		world.setBlock(i + 1, j+y, k + 13, Blocks.air);
		world.setBlock(i + 1, j+y, k + 14, b, ms, 3);
		world.setBlock(i + 1, j+y, k + 15, b, ms, 3);
		world.setBlock(i + 1, j+y, k + 16, Blocks.air);
		world.setBlock(i + 1, j+y, k + 17, Blocks.air);
		world.setBlock(i + 1, j+y, k + 18, Blocks.air);
		world.setBlock(i + 2, j+y, k + 0, Blocks.air);
		world.setBlock(i + 2, j+y, k + 1, Blocks.air);
		world.setBlock(i + 2, j+y, k + 2, Blocks.air);
		world.setBlock(i + 2, j+y, k + 3, Blocks.air);
		world.setBlock(i + 2, j+y, k + 4, b, ms, 3);
		world.setBlock(i + 2, j+y, k + 5, b, ms, 3);
		world.setBlock(i + 2, j+y, k + 6, b, ms, 3);
		world.setBlock(i + 2, j+y, k + 7, b, ms, 3);
		world.setBlock(i + 2, j+y, k + 8, Blocks.air);
		world.setBlock(i + 2, j+y, k + 9, Blocks.air);
		world.setBlock(i + 2, j+y, k + 10, Blocks.air);
		world.setBlock(i + 2, j+y, k + 11, Blocks.air);
		world.setBlock(i + 2, j+y, k + 12, Blocks.air);
		world.setBlock(i + 2, j+y, k + 13, Blocks.air);
		world.setBlock(i + 2, j+y, k + 14, b, ms, 3);
		world.setBlock(i + 2, j+y, k + 15, Blocks.air);
		world.setBlock(i + 2, j+y, k + 16, Blocks.air);
		world.setBlock(i + 2, j+y, k + 17, Blocks.air);
		world.setBlock(i + 2, j+y, k + 18, Blocks.air);
		world.setBlock(i + 3, j+y, k + 0, b, ms, 3);
		world.setBlock(i + 3, j+y, k + 1, b, ms, 3);
		world.setBlock(i + 3, j+y, k + 2, Blocks.air);
		world.setBlock(i + 3, j+y, k + 3, Blocks.air);
		world.setBlock(i + 3, j+y, k + 4, Blocks.air);
		world.setBlock(i + 3, j+y, k + 5, Blocks.air);
		world.setBlock(i + 3, j+y, k + 6, Blocks.air);
		world.setBlock(i + 3, j+y, k + 7, b, ms, 3);
		world.setBlock(i + 3, j+y, k + 8, b, mc, 3);
		world.setBlock(i + 3, j+y, k + 9, b, ms, 3);
		world.setBlock(i + 3, j+y, k + 10, b, ms, 3);
		world.setBlock(i + 3, j+y, k + 11, b, ms, 3);
		world.setBlock(i + 3, j+y, k + 12, b, ms, 3);
		world.setBlock(i + 3, j+y, k + 13, b, ms, 3);
		world.setBlock(i + 3, j+y, k + 14, b, ms, 3);
		world.setBlock(i + 3, j+y, k + 15, Blocks.air);
		world.setBlock(i + 3, j+y, k + 16, Blocks.air);
		world.setBlock(i + 3, j+y, k + 17, b, ms, 3);
		world.setBlock(i + 3, j+y, k + 18, b, ms, 3);
		world.setBlock(i + 4, j+y, k + 0, b, ms, 3);
		world.setBlock(i + 4, j+y, k + 1, b, ms, 3);
		world.setBlock(i + 4, j+y, k + 2, b, ms, 3);
		world.setBlock(i + 4, j+y, k + 3, b, ms, 3);
		world.setBlock(i + 4, j+y, k + 4, b, ms, 3);
		world.setBlock(i + 4, j+y, k + 5, b, ms, 3);
		world.setBlock(i + 4, j+y, k + 6, Blocks.air);
		world.setBlock(i + 4, j+y, k + 7, b, mc, 3);
		world.setBlock(i + 4, j+y, k + 8, Blocks.tnt, 0, 2);
		world.setBlock(i + 4, j+y, k + 9, b, mc, 3);
		world.setBlock(i + 4, j+y, k + 10, Blocks.air);
		world.setBlock(i + 4, j+y, k + 11, Blocks.air);
		world.setBlock(i + 4, j+y, k + 12, Blocks.air);
		world.setBlock(i + 4, j+y, k + 13, Blocks.air);
		world.setBlock(i + 4, j+y, k + 14, b, ms, 3);
		world.setBlock(i + 4, j+y, k + 15, Blocks.air);
		world.setBlock(i + 4, j+y, k + 16, b, ms, 3);
		world.setBlock(i + 4, j+y, k + 17, b, ms, 3);
		world.setBlock(i + 4, j+y, k + 18, b, ms, 3);
		world.setBlock(i + 5, j+y, k + 0, b, ms, 3);
		world.setBlock(i + 5, j+y, k + 1, Blocks.air);
		world.setBlock(i + 5, j+y, k + 2, Blocks.air);
		world.setBlock(i + 5, j+y, k + 3, b, ms, 3);
		world.setBlock(i + 5, j+y, k + 4, Blocks.air);
		world.setBlock(i + 5, j+y, k + 5, Blocks.air);
		world.setBlock(i + 5, j+y, k + 6, Blocks.air);
		world.setBlock(i + 5, j+y, k + 7, b, ms, 3);
		world.setBlock(i + 5, j+y, k + 9, b, ms, 3);
		world.setBlock(i + 5, j+y, k + 10, Blocks.air);
		world.setBlock(i + 5, j+y, k + 11, b, ms, 3);
		world.setBlock(i + 5, j+y, k + 12, b, ms, 3);
		world.setBlock(i + 5, j+y, k + 13, Blocks.air);
		world.setBlock(i + 5, j+y, k + 14, b, ms, 3);
		world.setBlock(i + 5, j+y, k + 15, Blocks.air);
		world.setBlock(i + 5, j+y, k + 16, b, ms, 3);
		world.setBlock(i + 5, j+y, k + 18, b, ms, 3);
		world.setBlock(i + 6, j+y, k + 0, b, ms, 3);
		world.setBlock(i + 6, j+y, k + 1, Blocks.air);
		world.setBlock(i + 6, j+y, k + 2, Blocks.air);
		world.setBlock(i + 6, j+y, k + 3, b, ms, 3);
		world.setBlock(i + 6, j+y, k + 4, Blocks.air);
		world.setBlock(i + 6, j+y, k + 5, b, ms, 3);
		world.setBlock(i + 6, j+y, k + 6, b, ms, 3);
		world.setBlock(i + 6, j+y, k + 7, b, ms, 3);
		world.setBlock(i + 6, j+y, k + 8, b, ms, 3);
		world.setBlock(i + 6, j+y, k + 9, b, ms, 3);
		world.setBlock(i + 6, j+y, k + 10, Blocks.air);
		world.setBlock(i + 6, j+y, k + 11, Blocks.air);
		world.setBlock(i + 6, j+y, k + 12, b, ms, 3);
		world.setBlock(i + 6, j+y, k + 13, Blocks.air);
		world.setBlock(i + 6, j+y, k + 14, Blocks.air);
		world.setBlock(i + 6, j+y, k + 15, Blocks.air);
		world.setBlock(i + 6, j+y, k + 16, b, ms, 3);
		world.setBlock(i + 6, j+y, k + 17, Blocks.air);
		world.setBlock(i + 6, j+y, k + 18, b, ms, 3);
		world.setBlock(i + 7, j+y, k + 0, b, ms, 3);
		world.setBlock(i + 7, j+y, k + 1, Blocks.air);
		world.setBlock(i + 7, j+y, k + 2, Blocks.air);
		world.setBlock(i + 7, j+y, k + 3, b, ms, 3);
		world.setBlock(i + 7, j+y, k + 4, Blocks.air);
		world.setBlock(i + 7, j+y, k + 5, b, ms, 3);
		world.setBlock(i + 7, j+y, k + 6, Blocks.air);
		world.setBlock(i + 7, j+y, k + 7, Blocks.air);
		world.setBlock(i + 7, j+y, k + 8, Blocks.air);
		world.setBlock(i + 7, j+y, k + 9, b, ms, 3);
		world.setBlock(i + 7, j+y, k + 10, b, ms, 3);
		world.setBlock(i + 7, j+y, k + 11, Blocks.air);
		world.setBlock(i + 7, j+y, k + 12, b, ms, 3);
		world.setBlock(i + 7, j+y, k + 13, b, ms, 3);
		world.setBlock(i + 7, j+y, k + 14, b, mc, 3);
		world.setBlock(i + 7, j+y, k + 15, b, ms, 3);
		world.setBlock(i + 7, j+y, k + 16, b, ms, 3);
		world.setBlock(i + 7, j+y, k + 17, Blocks.air);
		world.setBlock(i + 7, j+y, k + 18, b, ms, 3);
		world.setBlock(i + 8, j+y, k + 0, b, ms, 3);
		world.setBlock(i + 8, j+y, k + 1, Blocks.air);
		world.setBlock(i + 8, j+y, k + 2, Blocks.air);
		world.setBlock(i + 8, j+y, k + 3, b, ms, 3);
		world.setBlock(i + 8, j+y, k + 4, Blocks.air);
		world.setBlock(i + 8, j+y, k + 5, Blocks.air);
		world.setBlock(i + 8, j+y, k + 6, Blocks.air);
		world.setBlock(i + 8, j+y, k + 7, b, ms, 3);
		world.setBlock(i + 8, j+y, k + 8, Blocks.air);
		world.setBlock(i + 8, j+y, k + 9, Blocks.air);
		world.setBlock(i + 8, j+y, k + 10, Blocks.air);
		world.setBlock(i + 8, j+y, k + 11, Blocks.air);
		world.setBlock(i + 8, j+y, k + 12, b, ms, 3);
		world.setBlock(i + 8, j+y, k + 14, Blocks.tnt, 0, 2);
		world.setBlock(i + 8, j+y, k + 15, b, mc, 3);
		world.setBlock(i + 8, j+y, k + 16, Blocks.air);
		world.setBlock(i + 8, j+y, k + 17, Blocks.air);
		world.setBlock(i + 8, j+y, k + 18, b, ms, 3);
		world.setBlock(i + 9, j+y, k + 0, b, ms, 3);
		world.setBlock(i + 9, j+y, k + 1, Blocks.air);
		world.setBlock(i + 9, j+y, k + 2, Blocks.air);
		world.setBlock(i + 9, j+y, k + 3, b, ms, 3);
		world.setBlock(i + 9, j+y, k + 4, b, mc, 3);
		world.setBlock(i + 9, j+y, k + 5, b, ms, 3);
		world.setBlock(i + 9, j+y, k + 6, b, ms, 3);
		world.setBlock(i + 9, j+y, k + 7, b, ms, 3);
		world.setBlock(i + 9, j+y, k + 8, Blocks.air);
		world.setBlock(i + 9, j+y, k + 10, Blocks.air);
		world.setBlock(i + 9, j+y, k + 11, b, ms, 3);
		world.setBlock(i + 9, j+y, k + 12, b, ms, 3);
		world.setBlock(i + 9, j+y, k + 13, b, ms, 3);
		world.setBlock(i + 9, j+y, k + 14, b, mc, 3);
		world.setBlock(i + 9, j+y, k + 15, b, ms, 3);
		world.setBlock(i + 9, j+y, k + 16, Blocks.air);
		world.setBlock(i + 9, j+y, k + 17, Blocks.air);
		world.setBlock(i + 9, j+y, k + 18, b, ms, 3);
		world.setBlock(i + 10, j+y, k + 0, b, ms, 3);
		world.setBlock(i + 10, j+y, k + 1, Blocks.air);
		world.setBlock(i + 10, j+y, k + 2, Blocks.air);
		world.setBlock(i + 10, j+y, k + 3, b, mc, 3);
		world.setBlock(i + 10, j+y, k + 4, Blocks.tnt, 0, 2);
		world.setBlock(i + 10, j+y, k + 6, b, ms, 3);
		world.setBlock(i + 10, j+y, k + 7, Blocks.air);
		world.setBlock(i + 10, j+y, k + 8, Blocks.air);
		world.setBlock(i + 10, j+y, k + 9, Blocks.air);
		world.setBlock(i + 10, j+y, k + 10, Blocks.air);
		world.setBlock(i + 10, j+y, k + 11, b, ms, 3);
		world.setBlock(i + 10, j+y, k + 12, Blocks.air);
		world.setBlock(i + 10, j+y, k + 13, Blocks.air);
		world.setBlock(i + 10, j+y, k + 14, Blocks.air);
		world.setBlock(i + 10, j+y, k + 15, b, ms, 3);
		world.setBlock(i + 10, j+y, k + 16, Blocks.air);
		world.setBlock(i + 10, j+y, k + 17, Blocks.air);
		world.setBlock(i + 10, j+y, k + 18, b, ms, 3);
		world.setBlock(i + 11, j+y, k + 0, b, ms, 3);
		world.setBlock(i + 11, j+y, k + 1, Blocks.air);
		world.setBlock(i + 11, j+y, k + 2, b, ms, 3);
		world.setBlock(i + 11, j+y, k + 3, b, ms, 3);
		world.setBlock(i + 11, j+y, k + 4, b, mc, 3);
		world.setBlock(i + 11, j+y, k + 5, b, ms, 3);
		world.setBlock(i + 11, j+y, k + 6, b, ms, 3);
		world.setBlock(i + 11, j+y, k + 7, Blocks.air);
		world.setBlock(i + 11, j+y, k + 8, b, ms, 3);
		world.setBlock(i + 11, j+y, k + 9, b, ms, 3);
		world.setBlock(i + 11, j+y, k + 10, Blocks.air);
		world.setBlock(i + 11, j+y, k + 11, Blocks.air);
		world.setBlock(i + 11, j+y, k + 12, Blocks.air);
		world.setBlock(i + 11, j+y, k + 13, b, ms, 3);
		world.setBlock(i + 11, j+y, k + 14, Blocks.air);
		world.setBlock(i + 11, j+y, k + 15, b, ms, 3);
		world.setBlock(i + 11, j+y, k + 16, Blocks.air);
		world.setBlock(i + 11, j+y, k + 17, Blocks.air);
		world.setBlock(i + 11, j+y, k + 18, b, ms, 3);
		world.setBlock(i + 12, j+y, k + 0, b, ms, 3);
		world.setBlock(i + 12, j+y, k + 1, Blocks.air);
		world.setBlock(i + 12, j+y, k + 2, b, ms, 3);
		world.setBlock(i + 12, j+y, k + 3, Blocks.air);
		world.setBlock(i + 12, j+y, k + 4, Blocks.air);
		world.setBlock(i + 12, j+y, k + 5, Blocks.air);
		world.setBlock(i + 12, j+y, k + 6, b, ms, 3);
		world.setBlock(i + 12, j+y, k + 7, Blocks.air);
		world.setBlock(i + 12, j+y, k + 8, Blocks.air);
		world.setBlock(i + 12, j+y, k + 9, b, ms, 3);
		world.setBlock(i + 12, j+y, k + 10, b, ms, 3);
		world.setBlock(i + 12, j+y, k + 11, b, ms, 3);
		world.setBlock(i + 12, j+y, k + 12, b, ms, 3);
		world.setBlock(i + 12, j+y, k + 13, b, ms, 3);
		world.setBlock(i + 12, j+y, k + 14, Blocks.air);
		world.setBlock(i + 12, j+y, k + 15, b, ms, 3);
		world.setBlock(i + 12, j+y, k + 16, Blocks.air);
		world.setBlock(i + 12, j+y, k + 17, Blocks.air);
		world.setBlock(i + 12, j+y, k + 18, b, ms, 3);
		world.setBlock(i + 13, j+y, k + 0, b, ms, 3);
		world.setBlock(i + 13, j+y, k + 2, b, ms, 3);
		world.setBlock(i + 13, j+y, k + 3, Blocks.air);
		world.setBlock(i + 13, j+y, k + 4, b, ms, 3);
		world.setBlock(i + 13, j+y, k + 5, Blocks.air);
		world.setBlock(i + 13, j+y, k + 6, b, ms, 3);
		world.setBlock(i + 13, j+y, k + 7, b, ms, 3);
		world.setBlock(i + 13, j+y, k + 8, Blocks.air);
		world.setBlock(i + 13, j+y, k + 9, b, ms, 3);
		world.setBlock(i + 13, j+y, k + 11, b, ms, 3);
		world.setBlock(i + 13, j+y, k + 12, Blocks.air);
		world.setBlock(i + 13, j+y, k + 13, Blocks.air);
		world.setBlock(i + 13, j+y, k + 14, Blocks.air);
		world.setBlock(i + 13, j+y, k + 15, b, ms, 3);
		world.setBlock(i + 13, j+y, k + 16, Blocks.air);
		world.setBlock(i + 13, j+y, k + 17, Blocks.air);
		world.setBlock(i + 13, j+y, k + 18, b, ms, 3);
		world.setBlock(i + 14, j+y, k + 0, b, ms, 3);
		world.setBlock(i + 14, j+y, k + 1, b, ms, 3);
		world.setBlock(i + 14, j+y, k + 2, b, ms, 3);
		world.setBlock(i + 14, j+y, k + 3, Blocks.air);
		world.setBlock(i + 14, j+y, k + 4, b, ms, 3);
		world.setBlock(i + 14, j+y, k + 5, Blocks.air);
		world.setBlock(i + 14, j+y, k + 6, Blocks.air);
		world.setBlock(i + 14, j+y, k + 7, Blocks.air);
		world.setBlock(i + 14, j+y, k + 8, Blocks.air);
		world.setBlock(i + 14, j+y, k + 9, b, mc, 3);
		world.setBlock(i + 14, j+y, k + 10, Blocks.tnt, 0, 2);
		world.setBlock(i + 14, j+y, k + 11, b, mc, 3);
		world.setBlock(i + 14, j+y, k + 12, Blocks.air);
		world.setBlock(i + 14, j+y, k + 13, b, ms, 3);
		world.setBlock(i + 14, j+y, k + 14, b, ms, 3);
		world.setBlock(i + 14, j+y, k + 15, b, ms, 3);
		world.setBlock(i + 14, j+y, k + 16, b, ms, 3);
		world.setBlock(i + 14, j+y, k + 17, b, ms, 3);
		world.setBlock(i + 14, j+y, k + 18, b, ms, 3);
		world.setBlock(i + 15, j+y, k + 0, b, ms, 3);
		world.setBlock(i + 15, j+y, k + 1, b, ms, 3);
		world.setBlock(i + 15, j+y, k + 2, Blocks.air);
		world.setBlock(i + 15, j+y, k + 3, Blocks.air);
		world.setBlock(i + 15, j+y, k + 4, b, ms, 3);
		world.setBlock(i + 15, j+y, k + 5, b, ms, 3);
		world.setBlock(i + 15, j+y, k + 6, b, ms, 3);
		world.setBlock(i + 15, j+y, k + 7, b, ms, 3);
		world.setBlock(i + 15, j+y, k + 8, b, ms, 3);
		world.setBlock(i + 15, j+y, k + 9, b, ms, 3);
		world.setBlock(i + 15, j+y, k + 10, b, mc, 3);
		world.setBlock(i + 15, j+y, k + 11, b, ms, 3);
		world.setBlock(i + 15, j+y, k + 12, Blocks.air);
		world.setBlock(i + 15, j+y, k + 13, Blocks.air);
		world.setBlock(i + 15, j+y, k + 14, Blocks.air);
		world.setBlock(i + 15, j+y, k + 15, Blocks.air);
		world.setBlock(i + 15, j+y, k + 16, Blocks.air);
		world.setBlock(i + 15, j+y, k + 17, b, ms, 3);
		world.setBlock(i + 15, j+y, k + 18, b, ms, 3);
		world.setBlock(i + 16, j+y, k + 0, Blocks.air);
		world.setBlock(i + 16, j+y, k + 1, Blocks.air);
		world.setBlock(i + 16, j+y, k + 2, Blocks.air);
		world.setBlock(i + 16, j+y, k + 3, Blocks.air);
		world.setBlock(i + 16, j+y, k + 4, b, ms, 3);
		world.setBlock(i + 16, j+y, k + 5, Blocks.air);
		world.setBlock(i + 16, j+y, k + 6, Blocks.air);
		world.setBlock(i + 16, j+y, k + 7, Blocks.air);
		world.setBlock(i + 16, j+y, k + 8, Blocks.air);
		world.setBlock(i + 16, j+y, k + 9, Blocks.air);
		world.setBlock(i + 16, j+y, k + 10, Blocks.air);
		world.setBlock(i + 16, j+y, k + 11, b, ms, 3);
		world.setBlock(i + 16, j+y, k + 12, b, ms, 3);
		world.setBlock(i + 16, j+y, k + 13, b, ms, 3);
		world.setBlock(i + 16, j+y, k + 14, b, ms, 3);
		world.setBlock(i + 16, j+y, k + 15, Blocks.air);
		world.setBlock(i + 16, j+y, k + 16, Blocks.air);
		world.setBlock(i + 16, j+y, k + 17, Blocks.air);
		world.setBlock(i + 16, j+y, k + 18, Blocks.air);
		world.setBlock(i + 17, j+y, k + 0, Blocks.air);
		world.setBlock(i + 17, j+y, k + 1, Blocks.air);
		world.setBlock(i + 17, j+y, k + 2, Blocks.air);
		world.setBlock(i + 17, j+y, k + 3, b, ms, 3);
		world.setBlock(i + 17, j+y, k + 4, b, ms, 3);
		world.setBlock(i + 17, j+y, k + 5, Blocks.air);
		world.setBlock(i + 17, j+y, k + 6, Blocks.air);
		world.setBlock(i + 17, j+y, k + 7, Blocks.air);
		world.setBlock(i + 17, j+y, k + 8, Blocks.air);
		world.setBlock(i + 17, j+y, k + 9, Blocks.air);
		world.setBlock(i + 17, j+y, k + 10, Blocks.air);
		world.setBlock(i + 17, j+y, k + 11, Blocks.air);
		world.setBlock(i + 17, j+y, k + 12, Blocks.air);
		world.setBlock(i + 17, j+y, k + 14, b, ms, 3);
		world.setBlock(i + 17, j+y, k + 15, b, ms, 3);
		world.setBlock(i + 17, j+y, k + 16, Blocks.air);
		world.setBlock(i + 17, j+y, k + 17, Blocks.air);
		world.setBlock(i + 17, j+y, k + 18, Blocks.air);
		world.setBlock(i + 18, j+y, k + 0, Blocks.air);
		world.setBlock(i + 18, j+y, k + 1, Blocks.air);
		world.setBlock(i + 18, j+y, k + 2, Blocks.air);
		world.setBlock(i + 18, j+y, k + 3, b, ms, 3);
		world.setBlock(i + 18, j+y, k + 4, b, ms, 3);
		world.setBlock(i + 18, j+y, k + 5, b, ms, 3);
		world.setBlock(i + 18, j+y, k + 6, b, ms, 3);
		world.setBlock(i + 18, j+y, k + 7, b, ms, 3);
		world.setBlock(i + 18, j+y, k + 8, b, ms, 3);
		world.setBlock(i + 18, j+y, k + 9, b, ms, 3);
		world.setBlock(i + 18, j+y, k + 10, b, ms, 3);
		world.setBlock(i + 18, j+y, k + 11, b, ms, 3);
		world.setBlock(i + 18, j+y, k + 12, b, ms, 3);
		world.setBlock(i + 18, j+y, k + 13, b, ms, 3);
		world.setBlock(i + 18, j+y, k + 14, b, ms, 3);
		world.setBlock(i + 18, j+y, k + 15, b, ms, 3);
		world.setBlock(i + 18, j+y, k + 16, Blocks.air);
		world.setBlock(i + 18, j+y, k + 17, Blocks.air);
		world.setBlock(i + 18, j+y, k + 18, Blocks.air);

		world.setBlock(i + 13, j+y, k + 10, Blocks.tnt, 0, 2);
		world.setBlock(i + 10, j+y, k + 5, Blocks.tnt, 0, 2);
		world.setBlock(i + 8, j+y, k + 13, Blocks.tnt, 0, 2);
		world.setBlock(i + 5, j+y, k + 8, Blocks.tnt, 0, 2);

		if (j == 1) {
			ChromaAux.generateLootChest(world, i + 9, j+y, k + 9, 3, ChestGenHooks.PYRAMID_JUNGLE_DISPENSER, 0);

			ChromaAux.generateLootChest(world, i + 1, j+y, k + 5, 3, ChestGenHooks.PYRAMID_JUNGLE_CHEST, 2);
			ChromaAux.generateLootChest(world, i + 5, j+y, k + 17, 1, ChestGenHooks.PYRAMID_JUNGLE_CHEST, 2);
			ChromaAux.generateLootChest(world, i + 13, j+y, k + 1, 0, ChestGenHooks.PYRAMID_JUNGLE_CHEST, 2);
			ChromaAux.generateLootChest(world, i + 17, j+y, k + 13, 2, ChestGenHooks.PYRAMID_JUNGLE_CHEST, 2);
		}
	}

}
