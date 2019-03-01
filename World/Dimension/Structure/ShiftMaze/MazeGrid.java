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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.World.Dimension.Structure.ShiftMaze.Generation.SegmentRestriction;

public class MazeGrid implements SegmentRestriction {

	private final Map<Point, MazeSegment> segmentMap;
	private final int xSize, zSize;
	private List<ShiftMazeDoor> splittingDoors = new LinkedList();

	public MazeGrid(int xSize, int zSize) {
		segmentMap = new HashMap();
		for (int xx = 0; xx < xSize; xx++) {
			for (int zz = 0; zz < zSize; zz++) {
				segmentMap.put(new Point(xx, zz), new MazeSegment());
			}
		}
		this.xSize = xSize;
		this.zSize = zSize;
	}

	public int getXSize() {
		return xSize;
	}

	public int getZSize() {
		return zSize;
	}

	public Point getCentralSegment() {
		return new Point(this.getXSize() / 2, this.getZSize() / 2);
	}

	public Point getStartSegmentPos() {
		return new Point(this.getXSize() - 1, this.getZSize() / 2);
	}

	public Point getEndSegmentPos() {
		return new Point(0, this.getZSize() / 2);
	}

	public boolean isInBounds(int x, int z) {
		return x >= 0 && z >= 0 && x < xSize && z < zSize;
	}

	public MazeSegment getSegment(int x, int z) {
		Point p = new Point(x, z);
		if (!segmentMap.containsKey(p))
			throw new ArrayIndexOutOfBoundsException("Point out of bounds: x=" + x + ", z=" + z + " - Bounds: xSize=" + xSize + ", zSize=" + zSize);
		return segmentMap.get(new Point(x, z));
	}

	public boolean isFree(int x, int z) {
		if (!this.isInBounds(x, z))
			return false;
		MazeSegment seg = this.getSegment(x, z);
		return seg.getType().equals(SegmentType.CORRIDOR) && !seg.wasVisited();
	}

	public MazeGrid subGrid(int offsetX, int offsetZ, int sizeX, int sizeZ) {
		if (!this.isInBounds(offsetX, offsetZ) || !this.isInBounds(offsetX + sizeX, offsetZ + sizeZ))
			throw new IllegalArgumentException("Out Of Bounds subGrid creation");
		return new SubGrid(this, offsetX, offsetZ, sizeX, sizeZ);
	}

	public ShiftMazeDoor appendDoor(Point p1, Point p2, ForgeDirection dir) {
		if (this.getDoor(p1, p2) != null)
			return null;
		ShiftMazeDoor door = new ShiftMazeDoor(p1, p2, dir);
		splittingDoors.add(door);
		return door;
	}

	public ShiftMazeDoor getDoor(Point p1, Point p2) {
		if (!this.isInBounds(p1.x, p2.x) || !this.isInBounds(p2.x, p2.y))
			return null;
		for (ShiftMazeDoor door : splittingDoors) {
			if ((door.pointDirFrom.equals(p1) && door.pointDirTo.equals(p2)) && door.pointDirFrom.equals(p2) && door.pointDirTo.equals(p1)) {
				return door;
			}
		}
		return null;
	}

	public Collection<ShiftMazeDoor> getSplittingDoors() {
		return Collections.unmodifiableCollection(splittingDoors);
	}

	public static class MazeSegment {

		public static final List<ForgeDirection> VALID_CONNECTIONS = new LinkedList();

		private SegmentType type = SegmentType.CORRIDOR;
		private List<ForgeDirection> connections = new LinkedList();
		private boolean visited = false;
		public boolean specialLock = false;

		// Lootchest stuff
		protected UUID keyUUID;

		public SegmentType getType() {
			return type;
		}

		/* public void setTypeToggleDoor(ForgeDirection doorOrientation,
		 * ShiftMazeState... states) { if(!isValid(doorOrientation)) throw new
		 * IllegalArgumentException("Invalid door orientation: " +
		 * doorOrientation); //Input please. this.type = SegmentType.DOOR;
		 * this.isDoorHorizontal = doorOrientation.equals(ForgeDirection.NORTH)
		 * || doorOrientation.equals(ForgeDirection.SOUTH); this.states =
		 * Arrays.asList(states); } */

		public void setTypeKeyChest(UUID keyUUID) {
			this.keyUUID = keyUUID;
			type = SegmentType.CHEST;
		}

		public void addConnection(ForgeDirection dir) {
			if (this.isValid(dir))
				connections.add(dir);
		}

		public void markVisited() {
			visited = true;
		}

		public void markNonVisited() {
			visited = false;
		}

		public boolean wasVisited() {
			return visited;
		}

		private boolean isValid(ForgeDirection dir) {
			return VALID_CONNECTIONS.contains(dir);
		}

		public List<ForgeDirection> getConnections() {
			return connections;
		}

		static {
			VALID_CONNECTIONS.add(ForgeDirection.NORTH);
			VALID_CONNECTIONS.add(ForgeDirection.SOUTH);
			VALID_CONNECTIONS.add(ForgeDirection.WEST);
			VALID_CONNECTIONS.add(ForgeDirection.EAST);
		}

	}

	public static enum SegmentType {

		CORRIDOR(new MazePartGenerator()), CHEST(new MazePartGenerator.ChestGenerator());

		// DOOR(new MazePartGenerator.ToggleDoorGenerator());

		private MazePartGenerator extraGen;

		private SegmentType(MazePartGenerator extraGen) {
			this.extraGen = extraGen;
		}

		public MazePartGenerator getExtraGen() {
			return extraGen;
		}
	}

	public static class SubGrid extends MazeGrid {

		private final MazeGrid parent;
		private final int offsetX, offsetZ;

		private SubGrid(MazeGrid parent, int offsetSubX, int offsetSubZ, int sizeX, int sizeZ) {
			super(sizeX, sizeZ);
			this.parent = parent;
			offsetX = offsetSubX;
			offsetZ = offsetSubZ;
		}

		@Override
		public Point getStartSegmentPos() {
			throw new IllegalStateException("SubGrid doesn't have a start necessarily.");
		}

		@Override
		public Point getEndSegmentPos() {
			throw new IllegalStateException("SubGrid doesn't have a end necessarily.");
		}

		@Override
		public Point getCentralSegment() {
			Point center = parent.getCentralSegment();
			return new Point(offsetX + center.x, offsetZ + center.y);
		}

		@Override
		public boolean isInBounds(int x, int z) {
			return parent.isInBounds(offsetX + x, offsetZ + z);
		}

		@Override
		public ShiftMazeDoor appendDoor(Point p1, Point p2, ForgeDirection dir) {
			return parent.appendDoor(p1, p2, dir);
		}

		@Override
		public ShiftMazeDoor getDoor(Point p1, Point p2) {
			return parent.getDoor(p1, p2);
		}

		@Override
		public boolean isFree(int x, int z) {
			return parent.isFree(offsetX + x, offsetZ + z);
		}

		@Override
		public MazeSegment getSegment(int x, int z) {
			return parent.getSegment(offsetX + x, offsetZ + z);
		}

		@Override
		public MazeGrid subGrid(int offsetX, int offsetZ, int sizeX, int sizeZ) {
			return super.subGrid(this.offsetX + offsetX, this.offsetZ + offsetZ, sizeX, sizeZ);
		}
	}

	public static class ShiftMazeDoor {

		private static int dCounter = 0;
		private final int id;
		public final Point pointDirFrom, pointDirTo;
		public final ForgeDirection dir; // Well THIS is 100% redundant, since u
		// can reproduce it from the points
		// but well...
		public final List<ShiftMazeState> doorStates = new LinkedList();

		public ShiftMazeDoor(Point p1, Point p2, ForgeDirection dir) {
			id = dCounter;
			dCounter++;
			pointDirFrom = p1;
			pointDirTo = p2;
			this.dir = dir;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || this.getClass() != o.getClass())
				return false;
			ShiftMazeDoor that = (ShiftMazeDoor)o;
			return id == that.id;
		}

		@Override
		public int hashCode() {
			return id;
		}
	}
}
