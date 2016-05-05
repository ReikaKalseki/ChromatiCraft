package Reika.ChromatiCraft.Base;

import Reika.ChromatiCraft.World.Dimension.Structure.LaserPuzzleGenerator;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;


public abstract class LaserLevel extends StructurePiece {

	public boolean isSolved = false;

	protected LaserLevel(LaserPuzzleGenerator s) {
		super(s);
	}

	@Override
	public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {

	}

}
