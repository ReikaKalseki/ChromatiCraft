package Reika.ChromatiCraft.World.Dimension.Structure.Traces;

import java.util.Random;

import Reika.ChromatiCraft.Base.StructurePiece;
import Reika.ChromatiCraft.World.Dimension.Structure.TracePuzzleGenerator;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;


public class TracePuzzle extends StructurePiece<TracePuzzleGenerator> {

	public final int gridSize;
	public final int pathCount;
	private final Path[] paths;

	public TracePuzzle(TracePuzzleGenerator s, int sz, int n) {
		super(s);

		gridSize = sz;
		pathCount = n;
		paths = new Path[n];
	}

	public void calculate(Random rand) {

	}

	@Override
	public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {

	}

	public boolean hasBeenSolved() {
		return false;
	}

}
