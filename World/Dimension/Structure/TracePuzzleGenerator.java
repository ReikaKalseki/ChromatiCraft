package Reika.ChromatiCraft.World.Dimension.Structure;

import java.util.Random;

import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.StructureData;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.World.Dimension.Structure.Traces.TracePuzzle;


public class TracePuzzleGenerator extends DimensionStructureGenerator {

	private TracePuzzle[] puzzles;

	@Override
	protected void calculate(int chunkX, int chunkZ, Random rand) {
		puzzles = this.getPuzzles();
		posY = 20+rand.nextInt(50);

		int x = chunkX;
		int y = posY;
		int z = chunkZ;

		for (int i = 0; i < puzzles.length; i++) {
			TracePuzzle p = puzzles[i];
			p.calculate(rand);
			p.generate(world, x, y, z);
		}
	}

	@Override
	public StructureData createDataStorage() {
		return null;
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
		puzzles = null;
	}

	private TracePuzzle[] getPuzzles() {
		switch(ChromaOptions.getStructureDifficulty()) {
			case 1:
				return new TracePuzzle[] {new TracePuzzle(this, 5, 4), new TracePuzzle(this, 5, 5), new TracePuzzle(this, 6, 6), new TracePuzzle(this, 10, 8)};
			case 2:
				return new TracePuzzle[] {new TracePuzzle(this, 5, 4), new TracePuzzle(this, 5, 5), new TracePuzzle(this, 6, 6), new TracePuzzle(this, 8, 8), new TracePuzzle(this, 12, 10)};
			case 3:
			default:
				return new TracePuzzle[] {new TracePuzzle(this, 4, 4), new TracePuzzle(this, 5, 5), new TracePuzzle(this, 6, 6), new TracePuzzle(this, 8, 8), new TracePuzzle(this, 10, 10), new TracePuzzle(this, 12, 12)};
		}
	}

	@Override
	protected boolean hasBeenSolved(World world) {
		for (TracePuzzle p : puzzles) {
			if (!p.hasBeenSolved()) {
				return false;
			}
		}
		return true;
	}

	@Override
	protected void openStructure(World world) {

	}

}
