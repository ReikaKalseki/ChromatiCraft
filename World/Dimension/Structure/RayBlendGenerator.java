package Reika.ChromatiCraft.World.Dimension.Structure;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.StructureData;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.World.Dimension.Structure.RayBlend.RayBlendPuzzle;


public class RayBlendGenerator extends DimensionStructureGenerator {

	private final ArrayList<RayBlendPuzzle> puzzles = new ArrayList();

	@Override
	protected void calculate(int chunkX, int chunkZ, Random rand) {
		int x = chunkX;
		int z = chunkZ;
		int y = 10+rand.nextInt(70);
		posY = y;
		RayBlendPuzzle rb = new RayBlendPuzzle(this, 4, this.getInitialFillFraction(), rand);
		puzzles.add(rb);
		rb.generate(world, x, y, z);
	}

	private float getInitialFillFraction() {
		switch(ChromaOptions.getStructureDifficulty()) {
			case 0:
				return 0.33F;
			case 1:
				return 0.2F;
			case 2:
			default:
				return 0.1F;
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
	protected boolean hasBeenSolved(World world) {
		for (RayBlendPuzzle rb : puzzles) {
			if (!rb.isComplete())
				return false;
		}
		return true;
	}

	@Override
	protected void openStructure(World world) {

	}

	@Override
	protected void clearCaches() {
		puzzles.clear();
	}

}
