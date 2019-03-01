/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure.ShiftMaze;

import java.awt.Point;
import java.util.List;

import com.google.common.collect.Lists;

import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;

public class ShiftMazeState {

	private static int stateCounter = 0;
	private final int stateId;
	private List<Point> coordDoors = Lists.newLinkedList();

	public ShiftMazeState() {
		this.stateId = stateCounter;
		stateCounter++;
	}

	public boolean isDoorOpen(Coordinate c) {
		return isDoorOpen(new Point(c.xCoord, c.zCoord));
	}

	public boolean isDoorOpen(Point p) {
		return coordDoors.contains(p);
	}

	public void appendToggleDoor(Coordinate c) {
		Point p = new Point(c.xCoord, c.zCoord);
		if (!coordDoors.contains(p)) {
			coordDoors.add(p);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		ShiftMazeState that = (ShiftMazeState)o;
		return stateId == that.stateId;
	}

	@Override
	public int hashCode() {
		return stateId;
	}

}
