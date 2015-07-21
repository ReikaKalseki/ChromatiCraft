/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;

import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Auxiliary.Interfaces.StructureData;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.World.Dimension.Structure.DataStorage.ShiftMazeData;
import Reika.ChromatiCraft.World.Dimension.Structure.ShiftMaze.MazeAnchor;
import Reika.ChromatiCraft.World.Dimension.Structure.ShiftMaze.MazePiece;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.PointDirection;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class ShiftMazeGenerator extends DimensionStructureGenerator {

	//store maze[], each place there is a hole, have a door

	private final ArrayList<Point> endPoints = new ArrayList();

	private final HashMap<Point, Point> anchorRadii = new HashMap();
	private final HashSet<Point> anchorClearance = new HashSet();

	private Point exit;
	private Point entry;

	private Point destination;

	private int copyLength;

	private static final int ANCHORS = 6;

	private PathPre[] path = new PathPre[ANCHORS+1];
	private LinkedList<ForgeDirection>[] pathCache = new LinkedList[ANCHORS+1];
	private HashSet<Point> coordCache[] = new HashSet[ANCHORS+1];
	private MultiMap<Point, ForgeDirection>[] locationCache = new MultiMap[ANCHORS+1];
	private MazePath[] solutions = new MazePath[ANCHORS+1];

	private Point step;
	private ForgeDirection nextDir;

	private int partSize = 4;

	public static final int MAX_SIZE_X = getWidth();
	public static final int MAX_SIZE_Z = getWidth();

	private final MultiMap<PointDirection, Coordinate> locks = new MultiMap();

	private static final ArrayList<ForgeDirection> dirs = ReikaJavaLibrary.makeListFrom(ForgeDirection.EAST, ForgeDirection.WEST, ForgeDirection.NORTH, ForgeDirection.SOUTH);

	private static int getWidth() {
		switch(ChromaOptions.getStructureDifficulty()) {
		case 1:
			return 16;
		case 2:
			return 24;
		case 3:
			return 32;
		default:
			return 32;
		}
	}

	@Override
	public void calculate(int x, int z, CrystalElement e, Random rand) {


		this.pickPoints(rand, ANCHORS);

		int y = 200;

		for (int i = 0; i < ANCHORS+1; i++) {
			pathCache[i] = new LinkedList();
			coordCache[i] = new HashSet();
			locationCache[i] = new MultiMap(new MultiMap.HashSetFactory());
			destination = i == ANCHORS ? exit : endPoints.get(i);
			this.generatePathFrom(i, entry.x, entry.y, e, rand);
			this.cutExit(i);
		}

		this.generateBlocks(x, y, z, rand);
		this.generateAnchors(x, y, z, rand);
	}

	private void generateAnchors(int x, int y, int z, Random rand) {
		int i = 0;
		for (Point p : endPoints) {
			MazeAnchor a = new MazeAnchor(this, partSize, i, rand);
			int dx = x+p.x*partSize;
			int dz = z+p.y*partSize;
			a.generate(world, dx, y, dz);
			i++;
		}
	}

	private void cutExit(int layer) {
		boolean end = layer == ANCHORS;
		if (end) {
			locationCache[layer].addValue(exit, this.getExitDirectionTo(exit));
		}
		else {

		}
	}

	private void generateBlocks(int x, int y, int z, Random rand) {
		//s = s+2;
		for (int i = 0; i < MAX_SIZE_X; i++) {
			for (int k = 0; k < MAX_SIZE_Z; k++) {
				Point pt = new Point(i, k);
				if (!anchorRadii.containsKey(pt)) {
					MazePiece p = new MazePiece(this, partSize, pt);
					for (ForgeDirection dir : dirs) {
						int c = this.getConnection(pt, dir);
						if (c > 0) {
							p.connect(dir, c == ANCHORS+1);
						}
					}
					int dx = x+i*partSize;
					int dz = z+k*partSize;

					p.generate(world, dx, y, dz);
				}
			}
		}
	}

	private int getConnection(Point pt, ForgeDirection dir) {
		int c = 0;
		for (int i = 0; i < ANCHORS+1; i++) {
			if (locationCache[i].get(pt).contains(dir)) {
				c++;
			}
		}
		return c;
	}

	private void generatePathFrom(int layer, int x, int z, CrystalElement e, Random rand) {
		ForgeDirection dir = this.getEntryDirectionFrom(entry);
		pathCache[layer].addLast(dir);
		step = new Point(x, z);
		nextDir = this.getMovementDirection(layer, x, z, rand);
		while (!this.isFull(layer)) {
			this.stepPath(layer, step.x, step.y, rand, nextDir);
		}
		solutions[layer] = new MazePath(path[layer], locationCache[layer]);
	}

	private ForgeDirection getEntryDirectionFrom(Point p) {
		if (p.x == 0)
			return ForgeDirection.EAST;
		else if (p.x == MAX_SIZE_X-1)
			return ForgeDirection.WEST;
		else if (p.y == 0)
			return ForgeDirection.SOUTH;
		else if (p.y == MAX_SIZE_Z-1)
			return ForgeDirection.NORTH;
		else
			throw new IllegalStateException("No direction from "+p+"!?");
	}

	private ForgeDirection getExitDirectionTo(Point p) {
		if (p.x == 0)
			return ForgeDirection.WEST;
		else if (p.x == MAX_SIZE_X-1)
			return ForgeDirection.EAST;
		else if (p.y == 0)
			return ForgeDirection.NORTH;
		else if (p.y == MAX_SIZE_Z-1)
			return ForgeDirection.SOUTH;
		else
			throw new IllegalStateException("No direction to "+p+"!?");
	}

	private void stepPath(int layer, int x, int z, Random rand, ForgeDirection dir) {
		Point p = new Point(x, z);
		locationCache[layer].addValue(p, dir.getOpposite());
		coordCache[layer].add(p);
		if (this.isFull(layer)) {
			//ReikaJavaLibrary.pConsole("Have "+coordCache.size()+" points; is full; returning from "+dir+" from "+x+", "+z);
			return;
		}/*
		else if (endPoints.containsKey(p) && endPoints.get(p) == null) {
			endPoints.put(p, new Path(pathCache));
			dir = pathCache.removeLast();
			step.translate(-dir.offsetX, -dir.offsetZ);
			//ReikaJavaLibrary.pConsole("Have "+coordCache.size()+" points; anchor; stepping backward, opposite of "+dir+", from "+x+", "+z+" to "+step);
			nextDir = dir.getOpposite();
		}*/
		else if (p.equals(destination)) {
			path[layer] = this.getCurrentPath(layer);
			dir = pathCache[layer].removeLast();
			step.translate(-dir.offsetX, -dir.offsetZ);
			//ReikaJavaLibrary.pConsole("Have "+coordCache.size()+" points; destination; stepping backward, opposite of "+dir+", from "+x+", "+z+" to "+step);
			nextDir = dir.getOpposite();
		}
		else if (anchorRadii.containsKey(p) && anchorRadii.get(p).equals(destination)) {
			Point anch = anchorRadii.get(p);
			for (int a = -1; a <= 1; a++) {
				for (int b = -1; b <= 1; b++) {
					Point dp = new Point(anch.x+a, anch.y+b);
					coordCache[layer].add(dp);
				}
			}
			path[layer] = this.getCurrentPath(layer);
			dir = pathCache[layer].removeLast();
			step.translate(-dir.offsetX, -dir.offsetZ);
			//ReikaJavaLibrary.pConsole("Have "+coordCache.size()+" points; anchor; stepping backward, opposite of "+dir+", from "+x+", "+z+" to "+step);
			nextDir = dir.getOpposite();
		}
		else if (this.hasUnvisitedNeighbors(layer, x, z)) {
			dir = this.getMovementDirection(layer, x, z, rand);
			locationCache[layer].addValue(p, dir);
			pathCache[layer].addLast(dir);
			//this.stepPath(x+dir.offsetX, y+dir.offsetY, z+dir.offsetZ, rand, dir);
			step.translate(dir.offsetX, dir.offsetZ);
			//ReikaJavaLibrary.pConsole("Have "+coordCache.size()+" points; stepping forward "+dir+" from "+x+", "+z+" to "+step);
			nextDir = dir;
		}
		else {
			dir = pathCache[layer].removeLast();
			//ReikaJavaLibrary.pConsole("Backstep has: "+coordCache.contains(new Coordinate(x-dir.offsetX, y-dir.offsetY, z-dir.offsetZ))+"|"+this.hasUnvisitedNeighbors(x-dir.offsetX, y-dir.offsetY, z-dir.offsetZ));
			//ReikaJavaLibrary.pConsole("Current has: "+coordCache.contains(new Coordinate(x, y, z)));
			//this.stepPath(x-dir.offsetX, y-dir.offsetY, z-dir.offsetZ, rand, dir);
			step.translate(-dir.offsetX, -dir.offsetZ);
			//ReikaJavaLibrary.pConsole("Have "+coordCache.size()+" points; stepping backward, opposite of "+dir+", from "+x+", "+z+" to "+step);
			nextDir = dir.getOpposite();
			//return;
		}
	}

	private PathPre getCurrentPath(int layer) {
		Point pt = entry;
		LinkedList<Point> li = new LinkedList();
		li.add(pt);
		for (ForgeDirection dir : pathCache[layer]) {
			pt = new Point(pt.x+dir.offsetX, pt.y+dir.offsetZ);
			li.add(pt);
		}
		ArrayList<ForgeDirection> dir = new ArrayList(pathCache[layer]);
		dir.add(ReikaDirectionHelper.getDirectionBetween(li.getLast(), destination));
		return new PathPre(li, dir);
	}

	private boolean isFull(int layer) {
		return coordCache[layer].size() >= MAX_SIZE_X*MAX_SIZE_Z;
	}

	private ForgeDirection getMovementDirection(int layer, int x, int z, Random rand) {
		if (copyLength > 0 || rand.nextInt(4) > 0) {
			ForgeDirection dir = this.getSuccessfulPathDirection(x, z, layer);
			if (dir != null) {
				if (copyLength > 0) {
					copyLength--;
				}
				else {
					copyLength = 4+rand.nextInt(9);
					copyLength += 8;
					copyLength *= 5;
				}
				return dir;
			}
		}
		ForgeDirection last = pathCache[layer].getLast().getOpposite();
		ArrayList<ForgeDirection> li = new ArrayList(dirs);
		int idx = rand.nextInt(li.size());
		ForgeDirection side = li.get(idx);
		while (side == last || !this.canMove(x, z, side) || this.hasCellFrom(layer, x, z, side)) {
			li.remove(idx);
			idx = rand.nextInt(li.size());
			side = li.get(idx);
		}
		return li.get(idx);
	}

	private ForgeDirection getSuccessfulPathDirection(int x, int z, int layer) {
		Point pt = new Point(x, z);
		for (int i = 0; i < layer; i++) {
			if (solutions[i].hasPoint(pt)) {
				return solutions[i].getDirection(pt);
			}
			else {
				int r = 4;
				for (int d = 1; d <= r; d++) {
					for (ForgeDirection dir : dirs) {
						int dx = x+dir.offsetX*d;
						int dz = z+dir.offsetZ*d;
						Point dp = new Point(dx, dz);
						if (solutions[i].hasPoint(dp) || dp.equals(destination) || (anchorRadii.containsKey(dp) && anchorRadii.get(dp).equals(destination))) {
							return dir;
						}
					}
				}
			}
		}
		return null;
	}

	private boolean canMove(int x, int z, ForgeDirection dir) {
		int dx = x+dir.offsetX;
		int dz = z+dir.offsetZ;
		return dx >= 0 && dx < MAX_SIZE_X && dz >= 0 && dz < MAX_SIZE_Z;
	}

	private boolean hasCellFrom(int layer, int x, int z, ForgeDirection dir) {
		int dx = x+dir.offsetX;
		int dz = z+dir.offsetZ;
		Point p = new Point(dx, dz);
		return coordCache[layer].contains(p)/* || endPoints.get(p) != null*/;
	}

	private boolean hasUnvisitedNeighbors(int layer, int x, int z) {
		for (ForgeDirection dir : dirs) {
			if (this.canMove(x, z, dir) && !this.hasCellFrom(layer, x, z, dir))
				return true;
		}
		return false;
	}

	private void pickPoints(Random rand, int n) {
		entry = this.randomEdgeCenter(rand);
		do {
			exit = this.randomEdgeCenter(rand);
		} while (exit.equals(entry));

		for (int i = 0; i < n; i++) {
			Point p = new Point(1+rand.nextInt(MAX_SIZE_X-2), 1+rand.nextInt(MAX_SIZE_Z-2));
			while (this.anchorOverlaps(p)) {
				p = new Point(1+rand.nextInt(MAX_SIZE_X-2), 1+rand.nextInt(MAX_SIZE_Z-2));
			}

			this.addAnchor(p);
		}
	}

	private boolean anchorOverlaps(Point p) {
		for (int a = -2; a <= 2; a++) { //intentionally 2, not 1
			for (int b = -2; b <= 2; b++) {
				Point dp = new Point(p.x+a, p.y+b);
				if (anchorClearance.contains(p) || dp.equals(exit) || dp.equals(entry))
					return true;
			}
		}
		return false;
	}

	private void addAnchor(Point p) {
		endPoints.add(p);

		//locationCache.addValue(p, ForgeDirection.EAST);
		//locationCache.addValue(p, ForgeDirection.WEST);
		//locationCache.addValue(p, ForgeDirection.SOUTH);
		//locationCache.addValue(p, ForgeDirection.NORTH);

		for (int a = -2; a <= 2; a++) {
			for (int b = -2; b <= 2; b++) {
				Point dp = new Point(p.x+a, p.y+b);
				anchorClearance.add(dp);

				if (Math.abs(a) <= 1 && Math.abs(b) <= 1)
					anchorRadii.put(dp, p);
			}
		}
	}

	private Point randomEdgeCenter(Random rand) {
		Point p = new Point(MAX_SIZE_X/2, MAX_SIZE_Z/2);
		switch(rand.nextInt(4)) {
		case 0:
			p.x = MAX_SIZE_X-1;
			break;
		case 1:
			p.x = 0;
			break;
		case 2:
			p.y = MAX_SIZE_Z-1;
			break;
		case 3:
			p.y = 0;
			break;
		}
		return p;
	}

	@Override
	protected int getCenterXOffset() {
		return (partSize*(MAX_SIZE_X+1))/2;
	}

	@Override
	protected int getCenterZOffset() {
		return (partSize*(MAX_SIZE_Z+1))/2;
	}

	@Override
	protected void clearCaches() {
		endPoints.clear();
		anchorRadii.clear();
		anchorClearance.clear();

		entry = null;
		exit = null;
		destination = null;

		copyLength = 0;

		path = new PathPre[ANCHORS+1];
		pathCache = new LinkedList[ANCHORS+1];
		coordCache = new HashSet[ANCHORS+1];
		locationCache = new MultiMap[ANCHORS+1];
		solutions = new MazePath[ANCHORS+1];

		step = null;
		nextDir = null;

		MazeAnchor.clearGenCache();

		locks.clear();
	}

	public ArrayList<MazePath> getPaths() {
		return ReikaJavaLibrary.makeListFromArray(solutions);
	}

	public void cacheLock(Point pt, ForgeDirection dir, int x, int y, int z) {
		locks.addValue(new PointDirection(pt, dir), new Coordinate(x, y, z));
	}

	public Collection<Coordinate> getLocks(int x, int z, ForgeDirection dir) {
		return locks.get(new PointDirection(x, z, dir));
	}

	@Override
	public StructureData createDataStorage() {
		return new ShiftMazeData(this);
	}

	private static class PathPre {

		private final LinkedList<Point> points;
		private final ArrayList<ForgeDirection> dirs;

		private PathPre(LinkedList<Point> path, ArrayList<ForgeDirection> dir) {
			points = path;
			dirs = dir;
		}

	}

	public static class MazePath {

		private final LinkedList<Point> solution;
		private final HashMap<Point, ForgeDirection> directions;
		private final MultiMap<Point, ForgeDirection> connections;

		private MazePath(PathPre path, MultiMap<Point, ForgeDirection> con) {
			solution = path.points;
			connections = con;
			directions = new HashMap();

			int i = 0;
			for (Point p : solution) {
				ForgeDirection dir = path.dirs.get(i);
				directions.put(p, dir);
				i++;
			}
		}

		public boolean hasPoint(Point pt) {
			return directions.containsKey(pt);
		}

		public ForgeDirection getDirection(Point pt) {
			return directions.get(pt);
		}

		public boolean isPositionOpen(int x, int z, ForgeDirection dir) {
			return connections.get(new Point(x, z)).contains(dir);
		}

	}

}
