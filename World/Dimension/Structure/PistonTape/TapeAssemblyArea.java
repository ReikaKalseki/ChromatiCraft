package Reika.ChromatiCraft.World.Dimension.Structure.PistonTape;

import Reika.ChromatiCraft.Base.StructurePiece;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.Structure.PistonTapeGenerator;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;

public class TapeAssemblyArea extends StructurePiece<PistonTapeGenerator> {

	private final TapeStage level;

	public TapeAssemblyArea(PistonTapeGenerator gen, TapeStage t) {
		super(gen);
		level = t;
	}

	@Override
	public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {
		for (int i = 0; i < level.doorCount; i++) {
			for (int k = 0; k < 3; k++) {
				world.setBlock(x+i, y, z+k*3, ChromaBlocks.PISTONBIT.getBlockInstance(), 0);
				world.setBlock(x+i, y, z+k*3+1, ChromaBlocks.PISTONBIT.getBlockInstance(), 2);
				world.setBlock(x+i, y, z+k*3+2, ChromaBlocks.PISTONBIT.getBlockInstance(), 4);
			}
		}

	}
}
