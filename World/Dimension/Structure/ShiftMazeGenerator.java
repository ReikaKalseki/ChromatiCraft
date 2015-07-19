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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.World.Dimension.Structure.ShiftMaze.MazeAnchor;
import Reika.ChromatiCraft.World.Dimension.Structure.ShiftMaze.MazePiece;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class ShiftMazeGenerator extends DimensionStructureGenerator {

	//store maze[], each place there is a hole, have a door

	private final HashMap<Point, MazePath> endPoints = new HashMap();

	private final HashMap<Point, Point> anchorRadii = new HashMap();
	private final HashSet<Point> anchorClearance = new HashSet();

	private Point exit;
	private Point entry;

	private final LinkedList<ForgeDirection> pathCache = new LinkedList();
	private final HashSet<Point> coordCache = new HashSet();
	private final MultiMap<Point, ForgeDirection> locationCache = new MultiMap();

	private Point step;
	private ForgeDirection nextDir;

	private int partSize = 4;

	private static final int MAX_SIZE_X = getWidth();
	private static final int MAX_SIZE_Z = getWidth();

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


		int n = 6;
		this.pickPoints(rand, n);

		int y = 200;

		this.generatePathFrom(entry.x, entry.y, e, rand);
		this.cutExit();
		this.generateBlocks(x, y, z, rand);
		this.generateAnchors(x, y, z, rand);
	}

	private void generateAnchors(int x, int y, int z, Random rand) {
		int i = 0;
		for (Point p : endPoints.keySet()) {
			MazePath pth = endPoints.get(p);
			MazeAnchor a = new MazeAnchor(this, partSize, i, rand);
			int dx = x+p.x*partSize;
			int dz = z+p.y*partSize;
			a.generate(world, dx, y, dz);
			i++;
		}
	}

	private void cutExit() {
		locationCache.addValue(exit, this.getExitDirectionTo(exit));
	}

	private void generateBlocks(int x, int y, int z, Random rand) {
		//s = s+2;
		for (int i = 0; i < MAX_SIZE_X; i++) {
			for (int k = 0; k < MAX_SIZE_Z; k++) {
				Point pt = new Point(i, k);
				if (!anchorRadii.containsKey(pt)) {
					MazePiece p = new MazePiece(this, partSize);
					for (ForgeDirection dir : locationCache.get(pt)) {
						p.connect(dir);
					}
					int dx = x+i*partSize;
					int dz = z+k*partSize;

					p.generate(world, dx, y, dz);
				}
			}
		}
	}

	private void generatePathFrom(int x, int z, CrystalElement e, Random rand) {
		ForgeDirection dir = this.getEntryDirectionFrom(entry);
		pathCache.addLast(dir);
		step = new Point(x, z);
		nextDir = this.getMovementDirection(x, z, dir, rand);
		while (!this.isFull()) {
			this.stepPath(step.x, step.y, rand, nextDir);
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

	private void stepPath(int x, int z, Random rand, ForgeDirection dir) {
		Point p = new Point(x, z);
		locationCache.addValue(p, dir.getOpposite());
		coordCache.add(p);
		if (this.isFull()) {
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
		else if (anchorRadii.containsKey(p) && endPoints.get(anchorRadii.get(p)) == null) {
			Point anch = anchorRadii.get(p);
			for (int a = -1; a <= 1; a++) {
				for (int b = -1; b <= 1; b++) {
					Point dp = new Point(anch.x+a, anch.y+b);
					coordCache.add(dp);
				}
			}
			endPoints.put(anch, new MazePath(pathCache));
			dir = pathCache.removeLast();
			step.translate(-dir.offsetX, -dir.offsetZ);
			//ReikaJavaLibrary.pConsole("Have "+coordCache.size()+" points; anchor; stepping backward, opposite of "+dir+", from "+x+", "+z+" to "+step);
			nextDir = dir.getOpposite();
		}
		else if (this.hasUnvisitedNeighbors(x, z)) {
			dir = this.getMovementDirection(x, z, dir, rand);
			locationCache.addValue(p, dir);
			pathCache.addLast(dir);
			//this.stepPath(x+dir.offsetX, y+dir.offsetY, z+dir.offsetZ, rand, dir);
			step.translate(dir.offsetX, dir.offsetZ);
			//ReikaJavaLibrary.pConsole("Have "+coordCache.size()+" points; stepping forward "+dir+" from "+x+", "+z+" to "+step);
			nextDir = dir;
		}
		else {
			dir = pathCache.removeLast();
			//ReikaJavaLibrary.pConsole("Backstep has: "+coordCache.contains(new Coordinate(x-dir.offsetX, y-dir.offsetY, z-dir.offsetZ))+"|"+this.hasUnvisitedNeighbors(x-dir.offsetX, y-dir.offsetY, z-dir.offsetZ));
			//ReikaJavaLibrary.pConsole("Current has: "+coordCache.contains(new Coordinate(x, y, z)));
			//this.stepPath(x-dir.offsetX, y-dir.offsetY, z-dir.offsetZ, rand, dir);
			step.translate(-dir.offsetX, -dir.offsetZ);
			//ReikaJavaLibrary.pConsole("Have "+coordCache.size()+" points; stepping backward, opposite of "+dir+", from "+x+", "+z+" to "+step);
			nextDir = dir.getOpposite();
			//return;
		}
	}

	private boolean isFull() {
		return coordCache.size() >= MAX_SIZE_X*MAX_SIZE_Z;
	}

	private ForgeDirection getMovementDirection(int x, int z, ForgeDirection last, Random rand) {
		last = pathCache.getLast().getOpposite();
		ArrayList<ForgeDirection> li = new ArrayList(dirs);
		int idx = rand.nextInt(li.size());
		ForgeDirection side = li.get(idx);
		while (side == last || !this.canMove(x, z, side) || this.hasCellFrom(x, z, side)) {
			li.remove(idx);
			idx = rand.nextInt(li.size());
			side = li.get(idx);
		}
		return li.get(idx);
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
		return coordCache.contains(p)/* || endPoints.get(p) != null*/;
	}

	private boolean hasUnvisitedNeighbors(int x, int z) {
		for (ForgeDirection dir : dirs) {
			if (this.canMove(x, z, dir) && !this.hasCellFrom(x, z, dir))
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
		endPoints.put(p, null);

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

		pathCache.clear();
		coordCache.clear();
		locationCache.clear();

		step = null;
		nextDir = null;

		MazeAnchor.clearGenCache();
	}

	public static class MazePath {

		private MazePath(LinkedList<ForgeDirection> pathCache) {

		}

	}

	public List<MazePath> getPaths() {
		return new ArrayList(endPoints.values());
	}

}
