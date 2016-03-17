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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.StructureData;
import Reika.ChromatiCraft.Block.BlockChromaDoor;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.World.Dimension.Structure.DataStorage.ShiftMazeData;
import Reika.ChromatiCraft.World.Dimension.Structure.ShiftMaze.FixedMaze;
import Reika.ChromatiCraft.World.Dimension.Structure.ShiftMaze.FixedMazeDoors;
import Reika.ChromatiCraft.World.Dimension.Structure.ShiftMaze.MazeAnchor;
import Reika.ChromatiCraft.World.Dimension.Structure.ShiftMaze.MazePiece;
import Reika.ChromatiCraft.World.Dimension.Structure.ShiftMaze.ShiftMazeEntrance;
import Reika.ChromatiCraft.World.Dimension.Structure.ShiftMaze.ShiftMazeLoot;
import Reika.DragonAPI.Instantiable.Comparators.PointDistanceComparator;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

public class ShiftMazeGenerator extends DimensionStructureGenerator {

	//store maze[], each place there is a hole, have a door

	private final ArrayList<Point> endPoints = new ArrayList();

	private final HashMap<Point, Point> anchorRadii = new HashMap();
	private final HashSet<Point> anchorClearance = new HashSet();

	private Point exit;
	private Point entry;

	private Point destination;

	private static final int ANCHORS = 6;

	private final LinkedList<Point> pathCache = new LinkedList();
	private final HashSet<Point> coordCache = new HashSet();
	private final MultiMap<Point, ForgeDirection> locationCache = new MultiMap();

	private Path mainPath;
	private ArrayList<Point> nodes = new ArrayList();
	private ArrayList<Path> anchorPaths = new ArrayList();
	private MultiMap<Point, Path> nodePaths = new MultiMap(); //point is the end point of the path

	private MazeState[] states = new MazeState[ANCHORS+1];

	private Point step;
	private ForgeDirection nextDir;

	private int partSize = 4;

	public static final int MAX_SIZE_X = getWidth();
	public static final int MAX_SIZE_Z = getWidth();

	private final HashSet<Coordinate> locks = new HashSet();
	private final ArrayList<Coordinate> endDoors = new ArrayList();

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
	public void calculate(int x, int z, Random rand) {

		posY = 20+rand.nextInt(40);

		entryX = x+26;
		entryZ = z+11;

		for (int i = 0; i < states.length; i++) {
			states[i] = new MazeState();
		}

		FixedMaze fixed = new FixedMaze(this, rand);
		FixedMazeDoors doors = new FixedMazeDoors(this, ANCHORS+1, rand);
		ShiftMazeLoot loot = new ShiftMazeLoot(this);

		fixed.generate(world, x, posY, z);
		doors.generate(world, x, posY, z);

		int lx = x-((ANCHORS+1)*2-1)-10;

		loot.generate(world, lx, posY, z+5);

		//this.pickPoints(rand); //start, end, and anchors
		//this.mainPathing(rand); //start -> end pathing
		//this.pickNodes(rand); //pick nodes along start -> end
		//this.anchorPathing(rand); //path from anchors to nodes
		//this.nodePathing(rand); //path between nodes
		//this.addMazing(rand); //add dead ends

		//this.cutExit();

		//this.generateBlocks(x, posY, z, rand);
		//this.generateAnchors(x, posY, z, rand);

		this.addDynamicStructure(new ShiftMazeEntrance(this), x+26, z+11);
	}

	private void addMazing(Random rand) {

	}

	private void nodePathing(Random rand) {
		for (int i = 0; i < nodes.size(); i++) {
			Point from = i == 0 ? entry : nodes.get(i-1);
			Point end = nodes.get(i);
			for (int k = 0; k < ANCHORS+1; k++) {
				Path p = this.pathTo(from, end, rand, 10, nodePaths.get(end));
				nodePaths.addValue(end, p);
				for (int m = 0; m < p.points.size()-1; m++) {
					Point pt = p.points.get(m);
					ForgeDirection dir = p.steps.get(m);
					locationCache.addValue(pt, dir);
					locationCache.addValue(this.getTranslatedPoint(pt, dir), dir.getOpposite());
				}
			}
		}
	}

	private void anchorPathing(Random rand) {
		for (Point a : endPoints) { //anchors
			ArrayList<Point> li = new ArrayList(nodes);
			Collections.sort(li, new PointDistanceComparator(a));
			boolean flag = false;
			for (Point n : li) {
				Path p = this.pathTo(n, a, rand, 50);
				if (p instanceof FailedPath)
					continue;
				flag = true;
				anchorPaths.add(p);
				for (int m = 0; m < p.points.size()-1; m++) {
					Point pt = p.points.get(m);
					ForgeDirection dir = p.steps.get(m);
					locationCache.addValue(pt, dir);
					locationCache.addValue(this.getTranslatedPoint(pt, dir), dir.getOpposite());
				}
				break;
			}
			if (!flag) {
				ReikaJavaLibrary.pConsole("Failed pathing from anchor "+a+" to any node!");
			}
		}
	}

	private Point getClosestNode(Point p) {
		double d = Double.POSITIVE_INFINITY;
		Point c = null;
		for (Point n : nodes) {
			double dd = ReikaMathLibrary.py3d(p.x-n.x, 0, p.y-n.y);
			if (dd < d) {
				d = dd;
				c = n;
			}
		}
		return c;
	}

	private void pickNodes(Random rand) {
		ArrayList<Point> li = mainPath.points;
		double d = li.size()/(double)(ANCHORS+1);
		for (double i = d; i < li.size(); i += d) {
			nodes.add(li.get((int)i));
		}
	}

	private void mainPathing(Random rand) {
		mainPath = this.pathTo(entry, exit, rand, 50);
		if (mainPath instanceof FailedPath)
			throw new IllegalStateException("Main pathing failed?!");
		for (int m = 0; m < mainPath.points.size()-1; m++) {
			Point pt = mainPath.points.get(m);
			ForgeDirection dir = mainPath.steps.get(m);
			locationCache.addValue(pt, dir);
			locationCache.addValue(this.getTranslatedPoint(pt, dir), dir.getOpposite());
		}
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

	private void cutExit() {
		locationCache.addValue(exit, this.getExitDirectionTo(exit).getOpposite());
		locationCache.addValue(entry, this.getEntryDirectionFrom(entry));
	}

	private Path pathTo(Point from, Point end, Random rand, int weight) {
		return this.pathTo(from, end, rand, weight, null);
	}

	private Path pathTo(Point from, Point end, Random rand, int weight, Collection<Path> presets) {
		pathCache.clear();
		coordCache.clear();

		destination = end;
		pathCache.add(from);

		step = from;

		Path p = this.stepTowards(end, rand, weight, presets);
		while (p == null) {
			p = this.stepTowards(end, rand, weight, presets);
		}
		//ReikaJavaLibrary.pConsole("Pathing from "+from+" to "+end+", got N="+p.points.size()+":"+p.points);
		return p;
	}

	private Path stepTowards(Point end, Random rand, int weight, Collection<Path> presets) {
		coordCache.add(step);

		int x = step.x;
		int z = step.y;

		if (this.hasUnvisitedNeighbors(x, z)) {
			ForgeDirection dir = this.getMovementDirection(step.x, step.y, weight, rand);
			//ReikaJavaLibrary.pConsole("Stepping "+dir+" from "+step+" to "+this.getTranslatedPoint(step, dir));
			step = this.getTranslatedPoint(step, dir);
			pathCache.addLast(step);
			if (step.equals(end)) {
				return new Path(pathCache);
			}
			else {
				if (presets != null) {
					for (Path p : presets) {
						if (p.contains(step)) {
							LinkedList<Point> li = new LinkedList(pathCache);
							int idx = p.points.indexOf(step);
							for (int i = idx; i < p.points.size(); i++) {
								li.add(p.points.get(i));
							}
							return new Path(li);
						}
					}
				}
				return null;
			}
		}
		else {
			pathCache.removeLast();
			if (pathCache.isEmpty()) {
				//ReikaJavaLibrary.pConsole("Stepping back; path is empty. Pathing failed.");
				return new FailedPath();
			}
			//ReikaJavaLibrary.pConsole("Stepping back from "+step+" to "+pathCache.getLast()+"; "+pathCache.size()+" points remaining.");
			step = pathCache.getLast();
			return null;
		}
	}

	private ForgeDirection getMovementDirection(int x, int z, int weight, Random rand) {
		Point last = pathCache.getLast();
		Point a = anchorRadii.get(step);
		if (a != null && a.equals(destination)) {
			for (ForgeDirection dir : dirs) {
				Point p = this.getTranslatedPoint(step, dir);
				if (p.equals(destination))
					return dir;
			}
			weight = 100;
		}
		if (weight > 0 && rand.nextInt(100) < weight) {
			ForgeDirection close = null;
			double dist = Double.POSITIVE_INFINITY;
			for (ForgeDirection dir : dirs) {
				Point p = this.getTranslatedPoint(step, dir);
				if (p == last || !this.canMove(x, z, dir) || this.hasCellFrom(x, z, dir)) {

				}
				else {
					double dd = ReikaMathLibrary.py3d(p.x-destination.x, 0, p.y-destination.y);
					if (dd < dist) {
						dist = dd;
						close = dir;
					}
				}
			}
			if (close != null)
				return close;
		}
		//ReikaJavaLibrary.pConsole("Data at "+x+", "+y+", "+z+"; last is "+last);
		ArrayList<ForgeDirection> li = new ArrayList(dirs);
		int idx = rand.nextInt(li.size());
		while (this.getTranslatedPoint(step, li.get(idx)) == last || !this.canMove(x, z, li.get(idx)) || this.hasCellFrom(x, z, li.get(idx))) {
			li.remove(idx);
			if (li.isEmpty()) {
				for (int i = 0; i < 4; i++) {
					ForgeDirection dir = dirs.get(i);
					ReikaJavaLibrary.pConsole(dir+": LAST="+(this.getTranslatedPoint(step, dir) == last)+"| CANMOVE="+this.canMove(x, z, dir)+"| HAS="+this.hasCellFrom(x, z, dir));
				}
				ReikaJavaLibrary.pConsole("UNVISIT="+this.hasUnvisitedNeighbors(x, z));
				ReikaJavaLibrary.pConsole("Had no paths yet has an unvisited neighbor?!!");
			}
			idx = rand.nextInt(li.size());
		}
		if (li.isEmpty()) {
			ReikaJavaLibrary.pConsole("Had no paths yet has an unvisited neighbor?!!");
		}
		return li.get(idx);
	}

	private Point getTranslatedPoint(Point p, ForgeDirection dir) {
		return new Point(p.x+dir.offsetX, p.y+dir.offsetZ);
	}

	private void generateBlocks(int x, int y, int z, Random rand) {
		//s = s+2;
		for (int i = 0; i < MAX_SIZE_X; i++) {
			for (int k = 0; k < MAX_SIZE_Z; k++) {
				Point pt = new Point(i, k);
				if (!anchorRadii.containsKey(pt)) {
					MazePiece p = new MazePiece(this, partSize, pt, nodes.contains(pt));
					for (ForgeDirection dir : dirs) {
						if (locationCache.get(pt).contains(dir)) {
							p.connect(dir, true);
						}
					}
					int dx = x+i*partSize;
					int dz = z+k*partSize;

					p.generate(world, dx, y, dz);
				}
			}
		}
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

	private boolean isFull() {
		return coordCache.size() >= MAX_SIZE_X*MAX_SIZE_Z;
	}

	private boolean canMove(int x, int z, ForgeDirection dir) {
		int dx = x+dir.offsetX;
		int dz = z+dir.offsetZ;
		return dx >= 0 && dx < MAX_SIZE_X && dz >= 0 && dz < MAX_SIZE_Z;
	}

	private boolean hasCellFrom(int x, int z, ForgeDirection dir) {
		int dx = x+dir.offsetX;
		int dz = z+dir.offsetZ;
		Point p = new Point(dx, dz);
		if (coordCache.contains(p))
			return true;
		Point a = anchorRadii.get(p);
		if (a != null && !a.equals(destination))
			return true;
		return false;
	}

	private boolean hasUnvisitedNeighbors(int x, int z) {
		for (ForgeDirection dir : dirs) {
			if (this.canMove(x, z, dir) && !this.hasCellFrom(x, z, dir))
				return true;
		}
		return false;
	}

	private void pickPoints(Random rand) {
		entry = this.randomEdgeCenter(rand);
		do {
			exit = this.randomEdgeCenter(rand);
		} while (exit.equals(entry));

		for (int i = 0; i < ANCHORS; i++) {
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
		return 0;//(partSize*(MAX_SIZE_X+1))/2;
	}

	@Override
	protected int getCenterZOffset() {
		return 0;//(partSize*(MAX_SIZE_Z+1))/2;
	}

	@Override
	protected void clearCaches() {
		endPoints.clear();
		anchorRadii.clear();
		anchorClearance.clear();

		entry = null;
		exit = null;

		destination = null;

		pathCache.clear();
		coordCache.clear();
		locationCache.clear();

		mainPath = null;
		nodes.clear();
		anchorPaths.clear();
		nodePaths.clear();

		states = new MazeState[ANCHORS+1];

		step = null;
		nextDir = null;

		locks.clear();
		endDoors.clear();
	}

	public void addDoorState(int x, int z, int state) {
		states[state].openDoors.add(new Point(x, z));
	}

	public void cacheLock(int x, int y, int z) {
		locks.add(new Coordinate(x, y, z));
	}

	public Collection<Coordinate> getLocks() {
		return Collections.unmodifiableCollection(locks);
	}

	public void addEndingDoorLocation(int x, int y, int z) {
		endDoors.add(new Coordinate(x, y, z));
	}

	@Override
	public boolean hasBeenSolved(World world) {
		for (Coordinate c : endDoors) {
			if (!BlockChromaDoor.isOpen(world, c.xCoord, c.yCoord, c.zCoord))
				return false;
		}
		return true;
	}

	@Override
	public StructureData createDataStorage() {
		return new ShiftMazeData(this, ANCHORS+1);
	}

	public static class MazeState {

		private final HashSet<Point> openDoors = new HashSet();

		private MazeState() {

		}

		public boolean isPositionOpen(int x, int z) {
			return openDoors.contains(new Point(x, z));
		}

	}

	public MazeState getState(int state) {
		return states[state];
	}

	private static class Path {

		private final ArrayList<Point> points;
		private final ArrayList<ForgeDirection> steps = new ArrayList();
		private final HashSet<Point> set = new HashSet();

		private Path(LinkedList<Point> li) {
			points = new ArrayList(li);
			for (int i = 0; i < points.size()-1; i++) {
				steps.add(ReikaDirectionHelper.getDirectionBetween(points.get(i), points.get(i+1)));
			}
			set.addAll(points);
		}

		private boolean contains(Point p) {
			return set.contains(p);
		}

	}

	private static class FailedPath extends Path {

		private FailedPath() {
			super(new LinkedList());
		}

	}

}
