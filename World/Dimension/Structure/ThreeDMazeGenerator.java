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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;

import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Auxiliary.Interfaces.StructureData;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.World.Dimension.Structure.TDMaze.LootRoom;
import Reika.ChromatiCraft.World.Dimension.Structure.TDMaze.TDMazeEntrance;
import Reika.ChromatiCraft.World.Dimension.Structure.TDMaze.TunnelPiece;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class ThreeDMazeGenerator extends DimensionStructureGenerator {

	private final LinkedList<ForgeDirection> pathCache = new LinkedList();
	private final HashSet<Coordinate> coordCache = new HashSet();
	//private final HashSet<Coordinate> coordCacheInverse = new HashSet();
	private final MultiMap<Coordinate, ForgeDirection> locationCache = new MultiMap();
	//private final HashSet<Coordinate> currentStep = new HashSet();
	//private final HashSet<Coordinate> currentStepTemp = new HashSet();
	//private final MultiMap<Coordinate, ForgeDirection> next = new MultiMap();
	//private final MultiMap<Coordinate, ForgeDirection> nextTemp = new MultiMap();
	private Coordinate step;
	private ForgeDirection nextDir;

	private static final int MAX_SIZE_X = getWidth();
	private static final int MAX_SIZE_Y = getHeight();
	private static final int MAX_SIZE_Z = getWidth();

	private int minX;
	private int maxX;
	private int minY;
	private int maxY;
	private int minZ;
	private int maxZ;

	private int partSize;

	private static int getWidth() {
		switch(ChromaOptions.getStructureDifficulty()) {
			case 1:
			case 2:
				return 16;
			case 3:
				return 32;
			default:
				return 32;
		}
	}

	private static int getHeight() {
		switch(ChromaOptions.getStructureDifficulty()) {
			case 1:
				return 8;
			case 2:
			case 3:
				return 16;
			default:
				return 16;
		}
	}

	@Override
	public void calculate(int x, int z, Random rand) {

		posY = 75;

		partSize = 4;
		minX = x-partSize*MAX_SIZE_X/2;
		maxX = x+partSize*MAX_SIZE_X/2;
		minZ = z-partSize*MAX_SIZE_Z/2;
		maxZ = z+partSize*MAX_SIZE_Z/2;
		maxY = posY;
		minY = posY-partSize*MAX_SIZE_Y;

		this.generatePathFrom(MAX_SIZE_X/2, MAX_SIZE_Y-1, MAX_SIZE_Z/2, rand);
		this.cutExits(rand);
		this.cutExtras(rand);
		this.generateBlocks(x, posY, z, rand);

		int mx = x+partSize*MAX_SIZE_X/2+partSize/2;
		int mz = z+partSize*MAX_SIZE_Z/2+partSize/2;
		int by = posY-partSize*(MAX_SIZE_Y+1)+partSize/2;
		new LootRoom(this, rand).generate(world, mx, by, mz);

		this.addDynamicStructure(new TDMazeEntrance(this, posY+1), mx, mz);
	}

	@Override
	protected int getCenterXOffset() {
		return (partSize*(MAX_SIZE_X+1))/2;
	}

	@Override
	protected int getCenterZOffset() {
		return (partSize*(MAX_SIZE_Z+1))/2;
	}

	private void generateBlocks(int x, int y, int z, Random rand) {
		//s = s+2;
		for (int i = 0; i < MAX_SIZE_X; i++) {
			for (int j = 0; j < MAX_SIZE_Y; j++) {
				for (int k = 0; k < MAX_SIZE_Z; k++) {
					TunnelPiece p = new TunnelPiece(this, partSize);
					for (ForgeDirection dir : locationCache.get(new Coordinate(i, j, k))) {
						p.connect(dir);
					}
					if (i%4 == 0 && j%2 == 0 && k%4 == 0)
						p.setLighted();
					if (i != 0 && j != 0 && k != 0 && i != MAX_SIZE_X-1 && j != MAX_SIZE_Y-1 && k != MAX_SIZE_Z-1 && rand.nextInt(20) == 0) {
						p.addWindow(ForgeDirection.VALID_DIRECTIONS[rand.nextInt(6)]);
						if (rand.nextInt(20) == 0) {
							p.addWindow(ForgeDirection.VALID_DIRECTIONS[rand.nextInt(6)]);
						}
					}
					int dx = x+i*partSize;
					int dy = y+j*partSize-MAX_SIZE_Y*partSize;
					int dz = z+k*partSize;
					p.generate(world, dx, dy, dz);
				}
			}
		}
	}

	private void cutExits(Random rand) {
		locationCache.addValue(new Coordinate(MAX_SIZE_X/2, MAX_SIZE_Y-1, MAX_SIZE_Z/2), ForgeDirection.UP);
		locationCache.addValue(new Coordinate(MAX_SIZE_X/2, 0, MAX_SIZE_Z/2), ForgeDirection.DOWN);
	}

	/** Turns it from a simply connected "perfect" maze to a multiply connected "braid" maze; adds significant difficulty */
	private void cutExtras(Random rand) {
		int rx = 4+rand.nextInt(MAX_SIZE_X*4/5);
		int ry = 2+rand.nextInt(MAX_SIZE_Y/2);
		int rz = 4+rand.nextInt(MAX_SIZE_Z*4/5);
		int n = rx*ry*rz;//MAX_SIZE_Y*4+rand.nextInt(MAX_SIZE_X*MAX_SIZE_Z/4);
		for (int i = 0; i < n; i++) {
			Coordinate c = new Coordinate(rand.nextInt(MAX_SIZE_X), rand.nextInt(MAX_SIZE_Y), rand.nextInt(MAX_SIZE_Z));
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[rand.nextInt(6)];
			if (this.canMove(c.xCoord, c.yCoord, c.zCoord, dir))
				locationCache.addValue(c, dir);
		}
	}

	private void generatePathFrom(int x, int y, int z, Random rand) {
		pathCache.addLast(ForgeDirection.DOWN);
		//this.stepPath(x, y, z, rand, this.getMovementDirection(x, y, z, ForgeDirection.DOWN, rand));
		step = new Coordinate(x, y, z);
		nextDir = this.getMovementDirection(x, y, z, ForgeDirection.DOWN, rand);
		while (!this.isFull()) {
			this.stepPath(step.xCoord, step.yCoord, step.zCoord, rand, nextDir);
			//ReikaJavaLibrary.pConsole("----------"+coordCache.size()+"/"+step+"-------------");
		}

		/*
		currentStep.add(new Coordinate(x, y, z));
		while (!this.isFull()) {
			for (Coordinate c : currentStep)
				this.stepPath2(c.xCoord, c.yCoord, c.zCoord, rand);
			currentStep.clear();
			currentStep.addAll(currentStepTemp);
			currentStepTemp.clear();
			if (!this.isFull() && currentStep.isEmpty()) {
				currentStep.add(coordCacheInverse.iterator().next());
			}
		}
		 */
	}

	/*
	private void stepPath2(int x, int y, int z, Random rand) {
		Coordinate c = new Coordinate(x, y, z);
		coordCache.add(c);
		coordCacheInverse.remove(c);
		if (this.isFull()) {
			ReikaJavaLibrary.pConsole("Have "+coordCache.size()+" points; is full; returning from "+x+", "+y+", "+z);
			return;
		}
		else {
			int n = rand.nextInt(3) == 0 ? 2 : 1;
			ArrayList<ForgeDirection> dirs = new ArrayList();
			for (int i = 0; i < 6; i++) {
				ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
				if (dirs.size() < n) {
					if (this.canMove(x, y, z, dir) && !this.hasCellFrom(x, y, z, dir)) {
						dirs.add(dir);
					}
				}
				else
					break;
			}
			for (ForgeDirection dir : dirs) {
				ReikaJavaLibrary.pConsole("Have "+coordCache.size()+" points; stepping forward "+dir+" from "+x+", "+y+", "+z);
				locationCache.addValue(c, dir);
				//this.stepPath(x+dir.offsetX, y+dir.offsetY, z+dir.offsetZ, rand, dir);
				Coordinate c2 = new Coordinate(x+dir.offsetX, y+dir.offsetY, z+dir.offsetZ);
				currentStepTemp.add(c2);
				locationCache.addValue(c2, dir.getOpposite());
				ReikaJavaLibrary.pConsole("Connecting "+x+", "+y+", "+z+" and "+(x+dir.offsetX)+", "+(y+dir.offsetY)+", "+(z+dir.offsetZ));
			}
		}
	}
	 */
	/*
	private void stepPath(int x, int y, int z, Random rand, ForgeDirection dir) {
		Coordinate c = new Coordinate(x, y, z);
		locationCache.addValue(c, dir.getOpposite());
		coordCache.add(c);
		if (this.isFull()) {
			ReikaJavaLibrary.pConsole("Have "+coordCache.size()+" points; is full; returning from "+dir+" from "+x+", "+y+", "+z);
			return;
		}
		else if (this.hasUnvisitedNeighbors(x, y, z)) {
			Collection<ForgeDirection> dirs = this.getMovementDirections(x, y, z, dir, rand);
			ReikaJavaLibrary.pConsole("Have "+coordCache.size()+" points; stepping forward "+dirs+" from "+x+", "+y+", "+z);
			for (ForgeDirection dir2 : dirs) {
				dir = dir2;
				locationCache.addValue(c, dir);
				pathCache.addLast(dir);
				//this.stepPath(x+dir.offsetX, y+dir.offsetY, z+dir.offsetZ, rand, dir);
				Coordinate c2 = new Coordinate(x+dir.offsetX, y+dir.offsetY, z+dir.offsetZ);
				ReikaJavaLibrary.pConsole("stepping forward from "+x+", "+y+", "+z+" to "+c2);
				nextTemp.addValue(c2, dir);

			}
		}
		else {
			dir = pathCache.removeLast();
			ReikaJavaLibrary.pConsole("Backstep has: "+coordCache.contains(new Coordinate(x-dir.offsetX, y-dir.offsetY, z-dir.offsetZ))+"|"+this.hasUnvisitedNeighbors(x-dir.offsetX, y-dir.offsetY, z-dir.offsetZ));
			ReikaJavaLibrary.pConsole("Current has: "+coordCache.contains(new Coordinate(x, y, z)));
			Coordinate c2 = new Coordinate(x-dir.offsetX, y-dir.offsetY, z-dir.offsetZ);
			ReikaJavaLibrary.pConsole("Have "+coordCache.size()+" points; stepping backward, opposite of "+dir+", from "+x+", "+y+", "+z+" to "+c2);
			//this.stepPath(x-dir.offsetX, y-dir.offsetY, z-dir.offsetZ, rand, dir);
			nextTemp.addValue(c2, dir);
			//return;
		}
	}
	 */

	private void stepPath(int x, int y, int z, Random rand, ForgeDirection dir) {
		Coordinate c = new Coordinate(x, y, z);
		locationCache.addValue(c, dir.getOpposite());
		coordCache.add(c);
		if (this.isFull()) {
			//ReikaJavaLibrary.pConsole("Have "+coordCache.size()+" points; is full; returning from "+dir+" from "+x+", "+y+", "+z);
			return;
		}
		else if (this.hasUnvisitedNeighbors(x, y, z)) {
			dir = this.getMovementDirection(x, y, z, dir, rand);
			//ReikaJavaLibrary.pConsole("Have "+coordCache.size()+" points; stepping forward "+dir+" from "+x+", "+y+", "+z);
			locationCache.addValue(c, dir);
			pathCache.addLast(dir);
			//this.stepPath(x+dir.offsetX, y+dir.offsetY, z+dir.offsetZ, rand, dir);
			step = new Coordinate(x+dir.offsetX, y+dir.offsetY, z+dir.offsetZ);
			nextDir = dir;
		}
		else {
			dir = pathCache.removeLast();
			//ReikaJavaLibrary.pConsole("Backstep has: "+coordCache.contains(new Coordinate(x-dir.offsetX, y-dir.offsetY, z-dir.offsetZ))+"|"+this.hasUnvisitedNeighbors(x-dir.offsetX, y-dir.offsetY, z-dir.offsetZ));
			//ReikaJavaLibrary.pConsole("Current has: "+coordCache.contains(new Coordinate(x, y, z)));
			//ReikaJavaLibrary.pConsole("Have "+coordCache.size()+" points; stepping backward, opposite of "+dir+", from "+x+", "+y+", "+z);
			//this.stepPath(x-dir.offsetX, y-dir.offsetY, z-dir.offsetZ, rand, dir);
			step = new Coordinate(x-dir.offsetX, y-dir.offsetY, z-dir.offsetZ);
			nextDir = dir.getOpposite();
			//return;
		}
	}

	private boolean isFull() {
		return coordCache.size() >= MAX_SIZE_X*MAX_SIZE_Y*MAX_SIZE_Z;
	}

	private Collection<ForgeDirection> getMovementDirections(int x, int y, int z, ForgeDirection last, Random rand) {
		if (rand.nextInt(3) == 0 && this.canMove(x, y, z, last) && !this.hasCellFrom(x, y, z, last)) //bias towards continuing direction
			return ReikaJavaLibrary.makeListFrom(last);
		last = pathCache.getLast().getOpposite();
		int n = rand.nextInt(3) == 0 ? 2 : 1;
		//ReikaJavaLibrary.pConsole("Data at "+x+", "+y+", "+z+"; last is "+last);
		ArrayList<ForgeDirection> li = new ArrayList();
		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			if (li.size() < n) {
				if (dir != last && this.canMove(x, y, z, dir) && !this.hasCellFrom(x, y, z, dir)) {
					li.add(dir);
					//dir = ForgeDirection.VALID_DIRECTIONS[rand.nextInt(6)];
					//ReikaJavaLibrary.pConsole("Random testing "+idx+" of "+li.size());
				}
			}
			else {
				break;
			}
		}
		if (li.isEmpty()) {
			//ReikaJavaLibrary.pConsole("Had no paths yet has an unvisited neighbor?!!");
		}
		return li;
	}

	private ForgeDirection getMovementDirection(int x, int y, int z, ForgeDirection last, Random rand) {
		//if (rand.nextInt(3) == 0 && this.canMove(x, y, z, last) && !this.hasCellFrom(x, y, z, last)) //bias towards continuing direction
		//	return last;
		last = pathCache.getLast().getOpposite();
		//ReikaJavaLibrary.pConsole("Data at "+x+", "+y+", "+z+"; last is "+last);
		ArrayList<ForgeDirection> li = ReikaJavaLibrary.makeListFromArray(ForgeDirection.VALID_DIRECTIONS);
		int idx = rand.nextInt(li.size());
		while (li.get(idx) == last || !this.canMove(x, y, z, li.get(idx)) || this.hasCellFrom(x, y, z, li.get(idx))) {
			li.remove(idx);
			//dir = ForgeDirection.VALID_DIRECTIONS[rand.nextInt(6)];
			if (li.isEmpty()) {
				for (int i = 0; i < 6; i++) {
					ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
					//ReikaJavaLibrary.pConsole(dir+": LAST="+(dir == last)+"| CANMOVE="+this.canMove(x, y, z, dir)+"| HAS="+this.hasCellFrom(x, y, z, dir));
				}
				//ReikaJavaLibrary.pConsole("UNVISIT="+this.hasUnvisitedNeighbors(x, y, z));
				//ReikaJavaLibrary.pConsole("Had no paths yet has an unvisited neighbor?!!");
			}
			idx = rand.nextInt(li.size());
			//ReikaJavaLibrary.pConsole("Random testing "+idx+" of "+li.size());
		}
		if (li.isEmpty()) {
			//ReikaJavaLibrary.pConsole("Had no paths yet has an unvisited neighbor?!!");
		}
		return li.get(idx);
	}

	/*
	private void generateFrom(int entryAX, int entryAY, int entrySide, int pathSize, int x, int y, int z, CrystalElement e, Random rand) {

	}
	 */

	private boolean canMove(int x, int y, int z, ForgeDirection dir) {
		int dx = x+dir.offsetX;
		int dy = y+dir.offsetY;
		int dz = z+dir.offsetZ;
		return dx >= 0 && dx < MAX_SIZE_X && dy >= 0 && dy < MAX_SIZE_Y && dz >= 0 && dz < MAX_SIZE_Z;
	}

	private boolean hasCellFrom(int x, int y, int z, ForgeDirection dir) {
		int dx = x+dir.offsetX;
		int dy = y+dir.offsetY;
		int dz = z+dir.offsetZ;
		return coordCache.contains(new Coordinate(dx, dy, dz));
	}

	private boolean hasUnvisitedNeighbors(int x, int y, int z) {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			if (this.canMove(x, y, z, dir) && !this.hasCellFrom(x, y, z, dir))
				return true;
		}
		return false;
	}

	@Override
	protected void clearCaches() {
		pathCache.clear();
		coordCache.clear();
		locationCache.clear();
		step = null;
		nextDir = null;
		//step.clear();
		//nextTemp.clear();
	}

	@Override
	public StructureData createDataStorage() {
		return null;
	}

}
