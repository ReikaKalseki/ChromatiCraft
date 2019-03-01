/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.StructureData;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.World.Dimension.Structure.TDMaze.LootRoom;
import Reika.ChromatiCraft.World.Dimension.Structure.TDMaze.MazeRoom;
import Reika.ChromatiCraft.World.Dimension.Structure.TDMaze.TDMazeEntrance;
import Reika.ChromatiCraft.World.Dimension.Structure.TDMaze.TunnelPiece;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap.CollectionType;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;

public class ThreeDMazeGenerator extends DimensionStructureGenerator {

	private final LinkedList<ForgeDirection> pathCache = new LinkedList();
	private final LinkedList<Coordinate> pathLocs = new LinkedList();
	private final HashSet<Coordinate> coordCache = new HashSet();
	//private final HashSet<Coordinate> coordCacheInverse = new HashSet();
	private final MultiMap<Coordinate, ForgeDirection> locationCache = new MultiMap(CollectionType.HASHSET);
	//private final HashSet<Coordinate> currentStep = new HashSet();
	//private final HashSet<Coordinate> currentStepTemp = new HashSet();
	//private final MultiMap<Coordinate, ForgeDirection> next = new MultiMap();
	//private final MultiMap<Coordinate, ForgeDirection> nextTemp = new MultiMap();
	private final ArrayList<MazeRoom> rooms = new ArrayList();
	private final HashSet<Coordinate> roomSpaces = new HashSet();
	private Coordinate step;
	private ForgeDirection nextDir;

	private int goalDistance;
	private HashMap<Coordinate, Integer> distances = new HashMap();

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
			/*
			case 1:
			case 2:
				return 16;
			case 3:
				return 32;
			default:
				return 32;
			 */
			case 1:
				return 8;
			case 2:
				return 16;
			case 3:
			default:
				return 24;
		}
	}

	private static int getHeight() {
		switch(ChromaOptions.getStructureDifficulty()) {
			/*
			case 1:
				return 8;
			case 2:
			case 3:
				return 16;
			default:
				return 16;
			 */
			case 1:
				return 6;
			case 2:
				return 8;
			case 3:
			default:
				return 12;
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
		this.addRooms(rand);
		this.generateBlocks(x, posY, z, rand);

		int mx = x+partSize*MAX_SIZE_X/2+partSize/2;
		int mz = z+partSize*MAX_SIZE_Z/2+partSize/2;
		entryX = mx;
		entryZ = mz;

		int by = posY-partSize*(MAX_SIZE_Y+1)+partSize/2;
		new LootRoom(this, rand).generate(world, mx, by, mz);

		this.addDynamicStructure(new TDMazeEntrance(this), mx, mz);
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
					Coordinate c = new Coordinate(i, j, k);
					TunnelPiece p = new TunnelPiece(this, partSize);
					Collection<ForgeDirection> dirs = locationCache.get(new Coordinate(i, j, k));
					for (ForgeDirection dir : dirs) {
						p.connect(dir);
					}
					int ndl = 3;//4;
					if (i%ndl == 0 && j%(ndl/2) == 0 && k%ndl == 0)
						p.setLighted();
					if (i != 0 && j != 0 && k != 0 && i != MAX_SIZE_X-1 && j != MAX_SIZE_Y-1 && k != MAX_SIZE_Z-1 && rand.nextInt(10) == 0) {
						p.addWindow(ForgeDirection.VALID_DIRECTIONS[rand.nextInt(6)]);
						while (rand.nextInt(10) == 0) {
							p.addWindow(ForgeDirection.VALID_DIRECTIONS[rand.nextInt(6)]);
						}
					}
					/*
					//if (roomSpaces.contains(c)) {
					for (int s = 0; s < 6; s++) {
						ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[s];
						if (s >= 2 || dirs.contains(dir)) {
							Coordinate c2 = c.offset(dir, 1);
							if (roomSpaces.contains(c2)) {
								p.addRoomConnection(dir);
							}
						}
					}
					//}
					 */
					int dx = x+i*partSize;
					int dy = y+j*partSize-MAX_SIZE_Y*partSize;
					int dz = z+k*partSize;
					p.generate(world, dx, dy, dz);
				}
			}
		}

		for (MazeRoom r : rooms) {
			int dx = x+r.center.xCoord*partSize;
			int dy = y+r.center.yCoord*partSize-MAX_SIZE_Y*partSize;
			int dz = z+r.center.zCoord*partSize;
			r.generate(world, dx, dy, dz);
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

	private void addRooms(Random rand) {
		int n = (int)(Math.sqrt(MAX_SIZE_X*MAX_SIZE_Y*MAX_SIZE_Z)/2D);
		Coordinate start = new Coordinate(MAX_SIZE_X/2, MAX_SIZE_Y-1, MAX_SIZE_Z/2);
		Coordinate end = new Coordinate(MAX_SIZE_X/2, MAX_SIZE_Y-1, MAX_SIZE_Z/2);
		for (int a = 0; a < n; a++) {
			boolean large = rand.nextInt(6) == 0;
			int s = large ? 2 : 1;
			int dx = ReikaRandomHelper.getRandomBetween(s, MAX_SIZE_X-s-1);
			int dy = ReikaRandomHelper.getRandomBetween(s, MAX_SIZE_Y-s-1);
			int dz = ReikaRandomHelper.getRandomBetween(s, MAX_SIZE_Z-s-1);
			Coordinate c = new Coordinate(dx, dy, dz);
			int d = 4+s;
			int tries = 0;
			while ((c.getTaxicabDistanceTo(end) < d || c.getTaxicabDistanceTo(start) < d) && tries < 50) {
				dx = ReikaRandomHelper.getRandomBetween(s, MAX_SIZE_X-s-1);
				dy = ReikaRandomHelper.getRandomBetween(s, MAX_SIZE_Y-s-1);
				dz = ReikaRandomHelper.getRandomBetween(s, MAX_SIZE_Z-s-1);
				c = new Coordinate(dx, dy, dz);
				tries++;
			}
			boolean flag = false;
			do {
				for (int i = -s-1; i <= s+1; i++) {
					for (int k = -s-1; k <= s+1; k++) {
						Coordinate c2 = c.offset(i, 0, k);
						if (roomSpaces.contains(c2)) {
							flag = true;
							break;
						}
					}
					if (flag)
						break;
				}
				if (flag) {
					dx = ReikaRandomHelper.getRandomBetween(s, MAX_SIZE_X-s);
					dy = ReikaRandomHelper.getRandomBetween(s, MAX_SIZE_Y-s);
					dz = ReikaRandomHelper.getRandomBetween(s, MAX_SIZE_Z-s);
					c = new Coordinate(dx, dy, dz);
				}
				tries++;
			} while (flag && tries < 50);

			if (tries < 50) {
				MazeRoom r = new MazeRoom(this, partSize, s, c, rand);
				for (int i = -s; i <= s; i++) {
					for (int k = -s; k <= s; k++) {
						Coordinate c2 = c.offset(i, 0, k);
						roomSpaces.add(c2);
						r.addCell(c2);
					}
				}
				rooms.add(r);
			}
		}
	}

	private void generatePathFrom(int x, int y, int z, Random rand) {
		pathCache.addLast(ForgeDirection.DOWN);
		//this.stepPath(x, y, z, rand, this.getMovementDirection(x, y, z, ForgeDirection.DOWN, rand));
		step = new Coordinate(x, y, z);
		nextDir = this.getMovementDirection(x, y, z, rand);
		while (!this.isFull()) {
			this.stepPath(step.xCoord, step.yCoord, step.zCoord, rand, nextDir);
			//ReikaJavaLibrary.pConsole("----------"+coordCache.size()+"/"+step+"-------------");
		}
	}

	private void stepPath(int x, int y, int z, Random rand, ForgeDirection dir) {
		Coordinate c = new Coordinate(x, y, z);
		locationCache.addValue(c, dir.getOpposite());
		coordCache.add(c);
		pathLocs.addLast(c);
		if (x == MAX_SIZE_X/2 && y == 0 && z == MAX_SIZE_Z/2) {
			Iterator<Coordinate> it = pathLocs.descendingIterator();
			int d = 0;
			while (it.hasNext()) {
				Coordinate loc = it.next();
				Integer get = distances.get(loc);
				if (get != null) {
					d = Math.min(d, get.intValue());
				}
				distances.put(loc, d);
				d++;
			}
			goalDistance = 0;
		}
		else {
			goalDistance++;
			Integer get = distances.get(c);
			if (get != null) {
				goalDistance = Math.min(goalDistance, get.intValue());
			}
			distances.put(c, goalDistance);
		}

		if (this.isFull()) {
			//ReikaJavaLibrary.pConsole("Have "+coordCache.size()+" points; is full; returning from "+dir+" from "+x+", "+y+", "+z);
			pathLocs.removeLast();
			return;
		}
		else if (this.hasUnvisitedNeighbors(x, y, z)) {
			dir = this.getMovementDirection(x, y, z, rand);
			//ReikaJavaLibrary.pConsole("Have "+coordCache.size()+" points; stepping forward "+dir+" from "+x+", "+y+", "+z);
			locationCache.addValue(c, dir);
			pathCache.addLast(dir);
			//this.stepPath(x+dir.offsetX, y+dir.offsetY, z+dir.offsetZ, rand, dir);
			step = new Coordinate(x+dir.offsetX, y+dir.offsetY, z+dir.offsetZ);
			nextDir = dir;
		}
		else {
			dir = pathCache.removeLast();
			pathLocs.removeLast();
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

	private ForgeDirection getMovementDirection(int x, int y, int z, Random rand) {
		//if (rand.nextInt(3) == 0 && this.canMove(x, y, z, last) && !this.hasCellFrom(x, y, z, last)) //bias towards continuing direction
		//	return last;
		ForgeDirection last = pathCache.getLast().getOpposite();
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
		pathLocs.clear();
		coordCache.clear();
		locationCache.clear();
		distances.clear();
		roomSpaces.clear();
		rooms.clear();
		goalDistance = 0;
		step = null;
		nextDir = null;
		//step.clear();
		//nextTemp.clear();
	}

	@Override
	public StructureData createDataStorage() {
		return null;
	}

	@Override
	public boolean hasBeenSolved(World world) {
		return true; //No idea how to check this
	}

	@Override
	public void openStructure(World world) {
		//Or do this
	}

	/** Takes the coordinate within the maze, not actual block coords! */
	public int getDistanceToGoal(Coordinate loc) {
		return distances.get(loc);
	}

	public boolean isLocationInMaze(double x, double y, double z) {
		int px = MathHelper.floor_double(x);
		int py = MathHelper.floor_double(y);
		int pz = MathHelper.floor_double(z);
		return px >= 0 && px < MAX_SIZE_X && py >= 0 && py < MAX_SIZE_Y && pz >= 0 && pz < MAX_SIZE_Z;
	}

	public Coordinate getCellFromBlockCoords(double x, double y, double z) {
		if (!this.isLocationInMaze(x, y, z))
			return null;
		int dx = MathHelper.floor_double(x)-minX;
		int dy = MathHelper.floor_double(y)-minY;
		int dz = MathHelper.floor_double(z)-minZ;
		int s = partSize;
		return new Coordinate(dx/s, dy/s, dz/s);
	}

}
