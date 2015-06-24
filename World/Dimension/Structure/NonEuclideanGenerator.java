package Reika.ChromatiCraft.World.Dimension.Structure;

import java.util.Random;

import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class NonEuclideanGenerator extends DimensionStructureGenerator {

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

}
