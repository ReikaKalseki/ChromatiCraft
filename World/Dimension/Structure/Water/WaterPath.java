/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure.Water;

import java.awt.Point;
import java.util.HashSet;
import java.util.LinkedList;


public class WaterPath {

	private final Point startLoc;
	private final Point endLoc;

	private final LinkedList<Point> solution = new LinkedList();

	private final HashSet<Point> occupied = new HashSet();

	public WaterPath(int sx, int sz, int ex, int ez) {
		startLoc = new Point(sx, sz);
		endLoc = new Point(ex, ez);
	}

	public void genPath() {

	}

	public LinkedList<Point> getSolution() {
		return new LinkedList(solution);
	}

}
