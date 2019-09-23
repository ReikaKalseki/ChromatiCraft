package Reika.ChromatiCraft.World.Dimension.Structure.PistonTape;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Base.StructurePiece;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.Structure.PistonTapeGenerator;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;

public class PistonTapeAccessHall extends StructurePiece<PistonTapeGenerator> {

	public static final int WIDTH = 9;
	public static final int HEIGHT = 4;
	public static final int DEPTH = 3;

	private final TapeArea tape;

	public PistonTapeAccessHall(PistonTapeGenerator gen, TapeArea t) {
		super(gen);
		tape = t;
	}

	@Override
	public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {
		ForgeDirection dir = tape.hallDirection;
		ForgeDirection left = tape.tape.facing.getOpposite();
		for (int i = 0; i <= DEPTH; i++) {
			for (int l = 0; l <= WIDTH; l++) {
				int dx = x+dir.offsetX*i+left.offsetX*l;
				int dz = z+dir.offsetZ*i+left.offsetZ*l;
				for (int u = 0; u <= HEIGHT; u++) {
					world.setBlock(dx, y+u, dz, Blocks.air);
				}
				int m = l == 0 || l == WIDTH ? BlockType.STONE.metadata : BlockType.COBBLE.metadata;
				world.setBlock(dx, y, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), m);
				world.setBlock(dx, y+HEIGHT, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
			}

			for (int u = 1; u <= HEIGHT; u++) {
				world.setBlock(x+dir.offsetX*i, y+u, z+dir.offsetZ*i, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
				world.setBlock(x+dir.offsetX*i+left.offsetX*WIDTH, y+u, z+dir.offsetZ*i+left.offsetZ*WIDTH, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
			}
		}

		for (int u = 1; u <= HEIGHT; u++) {
			world.setBlock(x+dir.offsetX*DEPTH+left.offsetX*4, y+u, z+dir.offsetZ*DEPTH+left.offsetZ*4, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
			world.setBlock(x+dir.offsetX*DEPTH+left.offsetX*5, y+u, z+dir.offsetZ*DEPTH+left.offsetZ*5, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
		}

		world.setBlock(x+dir.offsetX, y+2, z+dir.offsetZ, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.LIGHT.metadata);
		world.setBlock(x+dir.offsetX+left.offsetX*WIDTH, y+2, z+dir.offsetZ+left.offsetZ*WIDTH, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.LIGHT.metadata);

		int h = HEIGHT;//-1;
		int d = 1+tape.tape.busWidth/2+1;
		for (int i = 1; i <= d; i++) {
			int dx = x+dir.offsetX*(DEPTH+i);
			int dz = z+dir.offsetZ*(DEPTH+i);
			for (int l = 0; l <= WIDTH; l++) {
				for (int dh = 0; dh <= h; dh++) {
					Block b = ChromaBlocks.STRUCTSHIELD.getBlockInstance();
					int m = BlockType.STONE.metadata;
					if (dh != 0 && dh != h && l != 0 && l != WIDTH && l != 4) {
						b = Blocks.air;
						m = 0;
					}
					world.setBlock(x+left.offsetX*l+dir.offsetX*(i+DEPTH), y+dh, z+left.offsetZ*l+dir.offsetZ*(i+DEPTH), b, m);
				}
			}
		}
	}
}
