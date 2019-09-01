package Reika.ChromatiCraft.World.Dimension.Structure.PistonTape;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Base.StructurePiece;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.Structure.PistonTapeGenerator;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;

public class TapeArea extends StructurePiece<PistonTapeGenerator> {

	final PistonTapeLoop tape;

	public TapeArea(PistonTapeGenerator gen, PistonTapeLoop p) {
		super(gen);
		tape = p;
	}

	@Override
	public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {
		ForgeDirection main = PistonTapeGenerator.DIRECTION;

		int w = tape.busWidth+1;

		for (int i = 0; i < w; i++) {
			world.setBlock(x-tape.facing.offsetX*4+main.offsetX*i, y+5, z-tape.facing.offsetZ*4+main.offsetZ*i, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
			for (int d = 0; d <= 2; d++) {
				world.setBlock(x-tape.facing.offsetX*6+main.offsetX*i, y+d, z-tape.facing.offsetZ*6+main.offsetZ*i, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
			}
			for (int h = 1; h <= 3; h++) {
				for (int d = 2; d <= 4; d++) {
					world.setBlock(x+tape.facing.offsetX*d+main.offsetX*i, y+h, z+tape.facing.offsetZ*d+main.offsetZ*i, Blocks.air);
				}
				world.setBlock(x+tape.facing.offsetX+main.offsetX*i, y+h, z+tape.facing.offsetZ+main.offsetZ*i, ChromaBlocks.SPECIALSHIELD.getBlockInstance(), BlockType.GLASS.metadata);
				world.setBlock(x+tape.facing.offsetX*5+main.offsetX*i, y+h, z+tape.facing.offsetZ*5+main.offsetZ*i, ChromaBlocks.SPECIALSHIELD.getBlockInstance(), BlockType.GLASS.metadata);
				world.setBlock(x+tape.facing.offsetX*6+main.offsetX*i, y+h, z+tape.facing.offsetZ*6+main.offsetZ*i, Blocks.air);
				world.setBlock(x+tape.facing.offsetX*7+main.offsetX*i, y+h, z+tape.facing.offsetZ*7+main.offsetZ*i, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.CLOAK.metadata);
			}
			for (int d = 2; d <= 4; d++) {
				world.setBlock(x+tape.facing.offsetX*d+main.offsetX*i, y, z+tape.facing.offsetZ*d+main.offsetZ*i, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.COBBLE.metadata);
				world.setBlock(x+tape.facing.offsetX*d+main.offsetX*i, y+4, z+tape.facing.offsetZ*d+main.offsetZ*i, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
			}

			world.setBlock(x+tape.facing.offsetX+main.offsetX*i, y, z+tape.facing.offsetZ+main.offsetZ*i, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
			world.setBlock(x+tape.facing.offsetX*5+main.offsetX*i, y, z+tape.facing.offsetZ*5+main.offsetZ*i, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
			world.setBlock(x+tape.facing.offsetX*6+main.offsetX*i, y, z+tape.facing.offsetZ*6+main.offsetZ*i, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.CLOAK.metadata);

			world.setBlock(x+tape.facing.offsetX*5+main.offsetX*i, y+4, z+tape.facing.offsetZ*5+main.offsetZ*i, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
			world.setBlock(x+tape.facing.offsetX*6+main.offsetX*i, y+4, z+tape.facing.offsetZ*6+main.offsetZ*i, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.CLOAK.metadata);
			world.setBlock(x+tape.facing.offsetX*7+main.offsetX*i, y+4, z+tape.facing.offsetZ*7+main.offsetZ*i, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.CLOAK.metadata);
		}

		for (int i = -5; i <= 7; i++) {
			int h = i > 1 ? 4 : 5;
			int m = i > 5 ? BlockType.CLOAK.metadata : BlockType.STONE.metadata;
			for (int d = 0; d <= h; d++) {
				world.setBlock(x+tape.facing.offsetX*i, y+d, z+tape.facing.offsetZ*i, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), m);
				world.setBlock(x+tape.facing.offsetX*i+main.offsetX*w, y+d, z+tape.facing.offsetZ*i+main.offsetZ*w, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), m);
			}
		}

		for (int i = 2; i <= 4; i++) {
			for (int h = 1; h <= 3; h++) {
				world.setBlock(x+tape.facing.offsetX*i, y+h, z+tape.facing.offsetZ*i, Blocks.air);
				world.setBlock(x+tape.facing.offsetX*i+main.offsetX*w, y+h, z+tape.facing.offsetZ*i+main.offsetZ*w, Blocks.air);
			}
		}

		for (int i = -3; i <= 0; i++) {
			for (int h = 1; h <= 3; h++) {
				world.setBlock(x+tape.facing.offsetX*i+main.offsetX*w, y+h, z+tape.facing.offsetZ*i+main.offsetZ*w, ChromaBlocks.SPECIALSHIELD.getBlockInstance(), BlockType.GLASS.metadata);
			}
		}

		for (int i = -4; i <= 0; i++) {
			for (int h = 1; h <= 4; h++) {
				Block b = Blocks.air;
				int m = 0;
				if (i == -4 || i == 0 || h == 1 || h == 4) {
					b = ChromaBlocks.STRUCTSHIELD.getBlockInstance();
					m = BlockType.COBBLE.metadata;
				}
				world.setBlock(x+tape.facing.offsetX*i, y+h, z+tape.facing.offsetZ*i, b, m);
			}
		}

		for (int d = 1; d <= 3; d++)
			world.setBlock(x+tape.facing.offsetX*d+main.offsetX*w, y, z+tape.facing.offsetZ*d+main.offsetZ*w, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.COBBLE.metadata);

		tape.generate(world, x+main.offsetX-tape.facing.offsetX, y+1, z+main.offsetZ-tape.facing.offsetZ);
	}
}
