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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;

import net.minecraftforge.common.util.ForgeDirection;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;


public class WaterPath {

	final Point startLoc;
	final Point endLoc;
	private final int gridSize;

	private final HashSet<Point> visitedCells = new HashSet();
	private final HashSet<Point> pointsToGo = new HashSet();

	final HashSet<Point> additionalKeys = new HashSet();
	final LinkedList<Point> solution = new LinkedList();

	final HashSet<ForgeDirection>[][] lockSides;

	public WaterPath(int sx, int sz, int ex, int ez, int size) {
		startLoc = new Point(sx, sz);
		endLoc = new Point(ex, ez);
		gridSize = size;
		int s = size*2+1;
		lockSides = new HashSet[s][s];
		for (int i = 0; i < lockSides.length; i++) {
			for (int k = 0; k < lockSides[i].length; k++) {
				lockSides[i][k] = new HashSet();
				pointsToGo.add(new Point(i-size, k-size));
			}
		}
		pointsToGo.remove(startLoc);
		solution.add(startLoc);
	}

	public void genPath(Random rand) {
		Point p = startLoc;
		boolean flag = true;
		while (flag) {
			flag = false;
			ArrayList<ForgeDirection> li = ReikaDirectionHelper.getRandomOrderedDirections(false);
			ForgeDirection dir = li.remove(0);
			while (!this.canStepTo(p, dir, false) && !li.isEmpty()) {
				dir = li.remove(0);
			}
			if (this.canStepTo(p, dir, false)) {
				p = this.stepTo(p, dir);
				solution.add(p);
				if (p.equals(endLoc)) {

				}
				else {
					flag = true;
				}
			}
		}
		while (!pointsToGo.isEmpty()) {
			p = ReikaJavaLibrary.getRandomCollectionEntry(rand, pointsToGo);
			pointsToGo.remove(p);
			additionalKeys.add(p);
			flag = true;
			while (flag) {
				flag = false;
				ArrayList<ForgeDirection> li = ReikaDirectionHelper.getRandomOrderedDirections(false);
				ForgeDirection dir = li.remove(0);
				while (!this.canStepTo(p, dir, true) && !li.isEmpty()) {
					dir = li.remove(0);
				}
				if (this.canStepTo(p, dir, true)) {
					p = this.stepTo(p, dir);
					if (solution.contains(p)) {
						//joined the path
					}
					else {
						flag = true;
					}
				}
			}
		}
	}

	private boolean canStepTo(Point from, ForgeDirection dir, boolean allowRevisit) {
		Point to = new Point(from.x+dir.offsetX, from.y+dir.offsetZ);
		return Math.abs(to.x) <= gridSize && Math.abs(to.y) <= gridSize && (allowRevisit || !visitedCells.contains(to));
	}

	private Point stepTo(Point from, ForgeDirection dir) {
		Point to = new Point(from.x+dir.offsetX, from.y+dir.offsetZ);
		int idx1a = from.x+gridSize;
		int idx1b = from.y+gridSize;
		int idx2a = to.x+gridSize;
		int idx2b = to.y+gridSize;
		lockSides[idx1a][idx1b].add(dir);
		lockSides[idx2a][idx2b].add(dir.getOpposite());
		visitedCells.add(to);
		pointsToGo.remove(to);
		return to;
	}

	public LinkedList<Point> getSolution() {
		return new LinkedList(solution);
	}

	@Override
	public String toString() {
		return solution.size()+": "+solution.toString();
	}

}
