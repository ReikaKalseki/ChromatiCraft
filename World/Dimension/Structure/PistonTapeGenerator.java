package Reika.ChromatiCraft.World.Dimension.Structure;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.StructureData;
import Reika.ChromatiCraft.World.Dimension.Structure.PistonTape.DoorKey;
import Reika.ChromatiCraft.World.Dimension.Structure.PistonTape.PistonTapeData;
import Reika.ChromatiCraft.World.Dimension.Structure.PistonTape.TapeStage;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;


public class PistonTapeGenerator extends DimensionStructureGenerator {

	private static final int MAX_ID = 511;
	private static final int MIN_ID = 1;

	private final HashSet<Integer> generatedIDs = new HashSet();
	private ArrayList<TapeStage> doors;

	@Override
	protected void calculate(int chunkX, int chunkZ, Random rand) {
		doors = new ArrayList();
		int x = chunkX;
		int z = chunkZ;
		int y = 10+rand.nextInt(70);
		posY = y;
		this.generateDataTile(x, y+1, z);
		x += 2;
		for (int i = 0; i < this.getDoorCount(); i++) {
			int id = MIN_ID+rand.nextInt(MAX_ID-MIN_ID+1);
			while (generatedIDs.contains(id)) {
				id = MIN_ID+rand.nextInt(MAX_ID-MIN_ID+1);
			}
			generatedIDs.add(id);
			HashSet<Coordinate> set = new HashSet();
			TapeStage ts = new TapeStage(this, new DoorKey(id, set), x, y, z-12, x, y, z-2);
			ts.generate(world, x, y, z);
			doors.add(ts);
			x++;
		}
	}

	private int getDoorCount() {
		return 12;
	}

	@Override
	public StructureData createDataStorage() {
		return new PistonTapeData(this, doors);
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
		return false;
	}

	@Override
	protected void openStructure(World world) {

	}

	@Override
	protected void clearCaches() {
		generatedIDs.clear();
		doors = null;
	}

}
