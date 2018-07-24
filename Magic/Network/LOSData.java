package Reika.ChromatiCraft.Magic.Network;

import java.util.Set;

import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;


public class LOSData {

	public final boolean hasLineOfSight;
	final Set<Coordinate> blocks;

	LOSData(boolean los, Set<Coordinate> ray) {
		hasLineOfSight = los;
		blocks = ray;
	}

}
