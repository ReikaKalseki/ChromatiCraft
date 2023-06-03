package Reika.ChromatiCraft.Auxiliary.Interfaces;

import java.util.Collection;

import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;

public interface ComplexAOE {

	public Collection<Coordinate> getPossibleRelativePositions();
	public double getNormalizedWeight(Coordinate c);

}
