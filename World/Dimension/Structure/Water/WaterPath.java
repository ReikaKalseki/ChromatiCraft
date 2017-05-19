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
import Reika.DragonAPI.Libraries.Java.ReikaArrayHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;


public class WaterPath {

	final Point startLoc;
	final Point endLoc;
	private final int gridSize;

	private final HashSet<Point> visitedCells = new HashSet();
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
			}
		}
		visitedCells.add(startLoc);
		visitedCells.add(endLoc);
	}

	public void genPath(Random rand) {
		ArrayList<Point> li = this.generateShortestPath();
		boolean flag = true;
		while (flag) {
			flag = false;
			ArrayList<Integer> indicesToTry = ReikaJavaLibrary.makeIntListFromArray(ReikaArrayHelper.getLinearArray(li.size()-1)); //-1 since cannot do from last point
			while (!indicesToTry.isEmpty() && !flag) {
				int idx = indicesToTry.remove(rand.nextInt(indicesToTry.size()));
				Point p1 = li.get(idx);
				Point p2 = li.get(idx+1);
				ForgeDirection dir = ReikaDirectionHelper.getDirectionBetween(p1, p2);
				dir = ReikaDirectionHelper.getLeftBy90(dir);
				flag = this.tryExtendPath(p1, p2, idx, li, dir);
				if (!flag) {
					dir = dir.getOpposite();
					flag = this.tryExtendPath(p1, p2, idx, li, dir);
				}
			}
		}
		for (int i = 0; i < li.size()-1; i++) {
			Point p1 = li.get(i);
			Point p2 = li.get(i+1);
			this.stepTo(p1, p2);
		}
		solution.addAll(li);
	}

	private boolean tryExtendPath(Point p1, Point p2, int idx, ArrayList<Point> li, ForgeDirection dir) {
		if (this.canStepTo(p1, dir, 1, false) && this.canStepTo(p2, dir, 1, false)) {
			int dist = 0;
			boolean flag2 = true;
			while (flag2) {
				flag2 = false;
				dist++;
				if (this.canStepTo(p1, dir, dist, false) && this.canStepTo(p2, dir, dist, false)) {
					flag2 = true;
				}
			}
			ArrayList<Point> inject = new ArrayList();
			for (int i = dist-1; i > 0; i--) {
				inject.add(0, new Point(p1.x+dir.offsetX*i, p1.y+dir.offsetZ*i));
				inject.add(new Point(p2.x+dir.offsetX*i, p2.y+dir.offsetZ*i));
			}
			visitedCells.addAll(inject);
			li.addAll(idx+1, inject);
			return true;
		}
		return false;
	}

	private ArrayList<Point> generateShortestPath() {
		int dx = endLoc.x-startLoc.x;
		int dz = endLoc.y-startLoc.y;
		Point p = startLoc;
		ArrayList<Point> li = new ArrayList();
		li.add(p);
		while (dx != 0) {
			p = this.stepTo(p, dx > 0 ? ForgeDirection.EAST : ForgeDirection.WEST);
			li.add(p);
			dx = (int)Math.signum(dx)*(Math.abs(dx)-1);
		}
		while (dz != 0) {
			p = this.stepTo(p, dz > 0 ? ForgeDirection.SOUTH : ForgeDirection.NORTH);
			li.add(p);
			dz = (int)Math.signum(dz)*(Math.abs(dz)-1);
		}
		return li;
	}

	private boolean canStepTo(Point from, ForgeDirection dir, int dist, boolean allowRevisit) {
		Point to = new Point(from.x+dir.offsetX*dist, from.y+dir.offsetZ*dist);
		return Math.abs(to.x) <= gridSize && Math.abs(to.y) <= gridSize && (allowRevisit || !visitedCells.contains(to));
	}

	private Point stepTo(Point from, ForgeDirection dir) {
		Point to = new Point(from.x+dir.offsetX, from.y+dir.offsetZ);
		visitedCells.add(to);
		return to;
	}

	private void stepTo(Point from, Point to) {
		ForgeDirection dir = ReikaDirectionHelper.getDirectionBetween(from, to);
		int idx1a = from.x+gridSize;
		int idx1b = from.y+gridSize;
		int idx2a = to.x+gridSize;
		int idx2b = to.y+gridSize;
		lockSides[idx1a][idx1b].add(dir);
		lockSides[idx2a][idx2b].add(dir.getOpposite());
	}

	public LinkedList<Point> getSolution() {
		return new LinkedList(solution);
	}

	@Override
	public String toString() {
		return solution.size()+": "+solution.toString();
	}

}
