package Reika.ChromatiCraft.World.Dimension.Structure;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.LaserLevel;
import Reika.ChromatiCraft.Base.StructureData;


public class LaserPuzzleGenerator extends DimensionStructureGenerator {

	private final ArrayList<LaserLevel> rooms = new ArrayList();

	@Override
	protected void calculate(int chunkX, int chunkZ, Random rand) {

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
	public boolean hasBeenSolved(World world) {
		for (LaserLevel l : rooms) {
			if (!l.isSolved) {
				return false;
			}
		}
		return true;
	}

	@Override
	public StructureData createDataStorage() {
		return null;
	}

}
