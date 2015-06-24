package Reika.ChromatiCraft.World.Dimension.Structure;

import java.util.ArrayList;
import java.util.Random;

import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.World.Dimension.Structure.Music.MusicPuzzle;

public class MusicPuzzleGenerator extends DimensionStructureGenerator {

	private static final int LENGTH = 4+2*ChromaOptions.getStructureDifficulty();

	private final ArrayList<MusicPuzzle> puzzles = new ArrayList();

	@Override
	protected void calculate(int chunkX, int chunkZ, CrystalElement e, Random rand) {

	}

	@Override
	protected int getCenterXOffset() {
		return 0;
	}

	@Override
	protected int getCenterZOffset() {
		return 0;
	}

	@Override
	protected void clearCaches() {
		puzzles.clear();
	}

}
