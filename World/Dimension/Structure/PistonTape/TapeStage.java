package Reika.ChromatiCraft.World.Dimension.Structure.PistonTape;

import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;

public class TapeStage {

	private final DoorKey door;
	private final Coordinate rowCenter;

	public TapeStage(DoorKey d, int x, int y, int z) {
		door = d;
		rowCenter = new Coordinate(x, y, z);
	}

}
