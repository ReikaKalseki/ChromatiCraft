/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure.ShiftMaze.Generation;

import java.awt.Point;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import com.google.common.collect.Lists;

import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.World.Dimension.Structure.ShiftMazeGenerator;
import Reika.ChromatiCraft.World.Dimension.Structure.ShiftMaze.MazeGrid;
import Reika.ChromatiCraft.World.Dimension.Structure.ShiftMaze.ShiftMazeState;

public class MazeCalculator {

	private final MazeGrid grid;
	private final Random seededRand;
	private final BacktrackDiscoverer backtrackStepper;

	//GenGoal -> playerGoal
	private Map<Point, Point> generationGoals = new HashMap<>();
	public Map<Integer, List<Point>> segmentPoints = new HashMap<>();
	public Map<Integer, Point> segmentIds = new HashMap<>();
	//GenGoal -> State
	private Map<Point, ShiftMazeState> bufferedStates = new HashMap<>();

	public MazeSegmentGraph segmentGraph;

	private Point genGoalEndDoor;

	public MazeCalculator(MazeGrid grid, Random rand) {
		this.grid = grid;
		seededRand = rand;
		backtrackStepper = new BacktrackDiscoverer(rand);
	}

	public Collection<Point> getGenerationGoals() {
		return Collections.unmodifiableCollection(generationGoals.keySet());
	}

	public Collection<Point> getPlayerGoals() {
		return Collections.unmodifiableCollection(generationGoals.values());
	}

	public boolean verifySolvability(ShiftMazeGenerator gen) {
		SegmentDiscoverer disc = new SegmentDiscoverer(grid);
		Point start = grid.getStartSegmentPos();
		List<Point> reachableOriginAnchors = new LinkedList<>();
		/*for (ShiftMazeState state : gen.getAllStates()) {
            disc.reset();
            disc.runCalc(Lists.newArrayList(start), state, false);
            for (Point origin : generationGoals.values()) {
                if(!reachableOriginAnchors.contains(origin) && disc.hasFound(origin)) {
                    reachableOriginAnchors.add(origin);
                }
            }
        }*/
		disc.reset();
		disc.runCalc(Lists.newArrayList(start), null, true);
		for (Point origin : generationGoals.values()) {
			if(!reachableOriginAnchors.contains(origin) && disc.hasFound(origin)) {
				reachableOriginAnchors.add(origin);
			}
		}
		for (Point origin : generationGoals.values()) {
			if(!reachableOriginAnchors.contains(origin)) {
				return false;
			}
		}
		return true;
	}

	/*public void removeRandomWalls() {
        int toRemove = (grid.getXSize() * 2) + seededRand.nextInt(grid.getZSize());

        lblIt: for (int i = 0; i < toRemove; i++) {
            int rX = seededRand.nextInt(grid.getXSize());
            int rZ = seededRand.nextInt(grid.getZSize());
            MazeGrid.MazeSegment sSeg = grid.getSegment(rX, rZ);
            if(sSeg.specialLock) continue;

            int rIndex = seededRand.nextInt(4);
            for (int t = rIndex; t < (rIndex + 4); t++) {
                ForgeDirection dir = MazeGrid.MazeSegment.VALID_CONNECTIONS.get(t % 4);
                int tX = rX + dir.offsetX;
                int tZ = rZ + dir.offsetZ;

                if(grid.isInBounds(tX, tZ)) {
                    MazeGrid.MazeSegment tSeg = grid.getSegment(tX, tZ);
                    if(!tSeg.specialLock) {
                        grid.getSegment(rX, rZ).addConnection(dir);
                        continue lblIt;
                    }
                }
            }
        }
    }*/

	public void pushDoorsToGrid() {
		List<MazeGrid.ShiftMazeDoor> pushedDoors = new LinkedList<>();
		Collection<Integer> ids = segmentGraph.getAllKnownNodeIDs();
		for (int id : ids) {
			MazeSegmentNode node = segmentGraph.getNode(id);
			for (MazeSegmentNode other : node.connectionDoors.keySet()) {
				for (MazeGrid.ShiftMazeDoor door : node.connectionDoors.get(other)) {
					if(!pushedDoors.contains(door)) {
						grid.appendDoor(door.pointDirFrom, door.pointDirTo, door.dir)
						.doorStates.addAll(door.doorStates);
						pushedDoors.add(door);
					}
				}
			}
		}
	}

	public void calcGraphPaths() {
		int startId = this.getContainingSegmentId(grid.getStartSegmentPos());
		MazeSegmentNode start = segmentGraph.getNode(startId);
		if(start == null)
			throw new IllegalStateException("Could not find start segment!");

		for (Point genGoal : generationGoals.keySet()) {
			int segGoal = this.getContainingSegmentId(genGoal);
			MazeSegmentNode toConnectTo = segmentGraph.getNode(segGoal);
			if(toConnectTo == null)
				throw new IllegalStateException("Could not find segment for a genGoal!");
			ShiftMazeState state = bufferedStates.get(genGoal);

			//System.out.println("Trying to find path from " + start + " to " + toConnectTo);
			ArrayDeque<MazeSegmentNode> nodePath = GraphBacktrackDiscoverer.getNodePath(start, toConnectTo, 2);
			//System.out.println("Connecting " + start + " to " + toConnectTo + " with 2 Steps> " + nodePath.toString());
			while (nodePath.size() > 1) {
				MazeSegmentNode to = nodePath.pop();
				MazeSegmentNode from = nodePath.pop();
				//System.out.println("Connecting " + from + " to " + to);

				from.addStateForConnection(to, seededRand, state);

				nodePath.push(from);
			}
			/*if(!nodePath.isEmpty()) { //Contains 1 element if loop did execute..
                MazeSegmentNode to = nodePath.pop();
                System.out.println("Connecting " + start + " to " + to);
                start.addStateForConnection(to, seededRand, state);
            }*/
		}
	}

	public void calcSegmentGraph() {
		segmentGraph = new MazeSegmentGraph();
		List<BufferedDoorState> prePotentialDoorPoints = new LinkedList<>();
		for (Integer segId : segmentPoints.keySet()) {

			MazeSegmentNode node = segmentGraph.createOrGetNode(segId);
			List<Point> segmentContents = segmentPoints.get(segId);
			for (Point p : segmentContents) {

				if(grid.getSegment(p.x, p.y).specialLock) continue;

				for (ForgeDirection dir : MazeGrid.MazeSegment.VALID_CONNECTIONS) {
					Point other = new Point(p.x + dir.offsetX, p.y + dir.offsetZ);
					if(!grid.isInBounds(other.x, other.y)) continue;
					if(segmentContents.contains(other)) continue;
					if(grid.getSegment(other.x, other.y).specialLock) continue;

					int otherId = this.getContainingSegmentId(other);
					MazeSegmentNode otherNode = segmentGraph.createOrGetNode(otherId);
					//segmentGraph.connect(node, otherNode);

					BufferedDoorState state = new BufferedDoorState(p, other, dir, node, otherNode);
					if(!prePotentialDoorPoints.contains(state)) prePotentialDoorPoints.add(state);
				}
			}
		}

		Collection<Integer> knownIDs = segmentGraph.getAllKnownNodeIDs();
		for (Integer id : knownIDs) {
			MazeSegmentNode node = segmentGraph.getNode(id);
			Map<MazeSegmentNode, List<BufferedDoorState>> sortedDoors = new HashMap<>();
			for (Integer otherId : new ArrayList<>(knownIDs)) {
				if(id.equals(otherId)) continue; //Unnecessary.
				List<BufferedDoorState> potentialDoors = new LinkedList<>();
				MazeSegmentNode otherNode = segmentGraph.getNode(otherId);
				boolean hasDoors = false;
				Iterator<BufferedDoorState> preBuf = prePotentialDoorPoints.iterator();
				while (preBuf.hasNext()) {
					BufferedDoorState door = preBuf.next();
					if((door.toNode.equals(otherNode) && door.fromNode.equals(node)) ||
							(door.fromNode.equals(otherNode) && door.toNode.equals(node))) {
						preBuf.remove();
						potentialDoors.add(door);
						hasDoors = true;
					}
				}
				if(hasDoors) {
					segmentGraph.connect(node, otherNode);
				}
				sortedDoors.put(otherNode, potentialDoors);
			}

			for (MazeSegmentNode conNode : sortedDoors.keySet()) {
				List<BufferedDoorState> doors = sortedDoors.get(conNode);
				if(doors.size() > 0) {
					//System.out.println("Placing door for " + node + " to " + conNode);
					segmentGraph.pushNewDoor(doors.remove(seededRand.nextInt(doors.size())));
				}
				if(doors.size() > 8 && seededRand.nextInt(3) == 0) {
					segmentGraph.pushNewDoor(doors.remove(seededRand.nextInt(doors.size())));
				}
			}
		}
		for (Integer id : knownIDs) {
			MazeSegmentNode node = segmentGraph.getNode(id);
			for (Integer otherId : knownIDs) {
				if(id.equals(otherId)) continue;
				MazeSegmentNode other = segmentGraph.getNode(otherId);
				segmentGraph.checkAndDisconnect(node, other);
			}
		}
	}

	private int getContainingSegmentId(Point p) {
		for (Integer segId : segmentPoints.keySet()) {
			if(segmentPoints.get(segId).contains(p)) return segId;
		}
		throw new IllegalArgumentException("Searching ID for Point out of bounds!");
	}

	public void calcSegmentInternalMazes() {
		List<Point> deadEndPoints = new LinkedList<>();
		for (int segId : segmentIds.keySet()) {
			Point goal = segmentIds.get(segId);
			List<Point> segmentElements = segmentPoints.get(segId);
			GridSegment segment = new GridSegment(grid, segmentElements, segId);
			backtrackStepper.reset();
			backtrackStepper.setSegmentRestriction(segment);
			backtrackStepper.appendDeadEndsTo(deadEndPoints);
			backtrackStepper.doStep(grid, goal.x, goal.y, true, false, new BacktrackDiscoverer.MatchCoordCondition(-1, -1)); //Fill.

			lblDe:
				for (Point de : deadEndPoints) {
					if(seededRand.nextBoolean()) continue;
					int pX = de.x;
					int pZ = de.y;

					int rIndex = seededRand.nextInt(4);
					for (int i = rIndex; i < (rIndex + 4); i++) {
						ForgeDirection dir = MazeGrid.MazeSegment.VALID_CONNECTIONS.get(i % 4);
						int tX = pX + dir.offsetX;
						int tZ = pZ + dir.offsetZ;

						if(segment.isInBounds(tX, tZ)) {
							MazeGrid.MazeSegment tSeg = grid.getSegment(tX, tZ);
							if(!tSeg.specialLock) {
								grid.getSegment(pX, pZ).addConnection(dir);
								continue lblDe;
							}
						}
					}
				}
			deadEndPoints.clear();
		}
	}

	public void calcMazeSegments() {
		Point start = grid.getStartSegmentPos();
		Point center = grid.getCentralSegment();
		segmentPoints = new HashMap<>();
		segmentIds = new HashMap<>();

		LinkedList<Point> goals = new LinkedList<>();
		goals.addAll(generationGoals.keySet());
		goals.remove(genGoalEndDoor);

		grid.getSegment(genGoalEndDoor.x, genGoalEndDoor.y).markNonVisited();
		grid.getSegment(start.x, start.y).markNonVisited();
		for (Point p : goals) {
			grid.getSegment(p.x, p.y).markNonVisited();
		}

		Collections.sort(goals, new ComparatorGridDistance(genGoalEndDoor));
		goals.addFirst(genGoalEndDoor);
		goals.addLast(start);

		//Segments building from end-containing segment
		int forcedZFlip = grid.getZSize() / 5;
		int highestId = -1;
		boolean fromZeroZ = seededRand.nextBoolean();
		for (int i = 0; i < goals.size() - 1; i++) {
			if(i > highestId) highestId = i;
			Point to = goals.get(i); //matches with genGoal
			Point plGoal = generationGoals.get(to);
			Point from = goals.get(i + 1);

			List<Point> containing = new LinkedList<>();

			/*Point divPoint = new Point(
                    to.x - ((from.x - to.x) / 2),
                    to.y - ((from.y - to.y) / 2));*/
			Point divPoint = new Point(to.x + ((from.x - to.x) / 2), center.y);

			int offset, dirStep, zToPass;
			if(to.y < forcedZFlip) {
				offset = 0;
				dirStep = 1;
				int zBuf = to.y < plGoal.y ? plGoal.y : to.y;

				zToPass = center.y < zBuf ? zBuf : center.y;
				fromZeroZ = false;
			} else if(to.y > grid.getZSize() - forcedZFlip - 1) {
				offset = grid.getZSize() - 1;
				dirStep = -1;
				int zBuf = to.y < plGoal.y ? to.y : plGoal.y;

				zToPass = center.y < zBuf ? center.y : zBuf;
				fromZeroZ = true;
			} else {
				if(fromZeroZ) {
					offset = 0;
					dirStep = 1;
					int zBuf = to.y < plGoal.y ? plGoal.y : to.y;

					zToPass = center.y < zBuf ? zBuf : center.y;
					fromZeroZ = !fromZeroZ;
				} else {
					offset = grid.getZSize() - 1;
					dirStep = -1;
					int zBuf = to.y < plGoal.y ? to.y : plGoal.y;

					zToPass = center.y < zBuf ? center.y : zBuf;
					fromZeroZ = !fromZeroZ;
				}
			}

			int step = offset - dirStep;
			do {
				step += dirStep;
				for (int xx = 0; xx < divPoint.x; xx++) {
					Point at = new Point(xx, step);
					boolean contains = false;
					for (List<Point> pList : segmentPoints.values()) {
						if(pList.contains(at)) contains = true; //Some segment already contains this pos.
					}
					if(!contains) {
						if(grid.getSegment(xx, step).specialLock) continue;
						containing.add(at);
					}
				}
			} while (step != zToPass);

			step += dirStep; //Chest selection secures us from stepping over the grid size
			for (int xx = 0; xx < divPoint.x; xx++) {
				Point at = new Point(xx, step);
				boolean contains = false;
				for (List<Point> pList : segmentPoints.values()) {
					if(pList.contains(at)) contains = true; //Some segment already contains this pos.
				}
				if(!contains) {
					if(grid.getSegment(xx, step).specialLock) continue;
					containing.add(at);
				}
			}

			containing.add(to);
			segmentPoints.put(i, containing);
			segmentIds.put(i, to);
		}

		//The 'last' segment around the start.
		highestId++;
		List<Point> startSegment = new LinkedList<>();
		for (int xx = 0; xx < grid.getXSize(); xx++) {
			for (int zz = 0; zz < grid.getZSize(); zz++) {
				Point at = new Point(xx, zz);
				if(grid.getSegment(xx, zz).specialLock) continue;
				boolean contains = false;
				for (List<Point> pList : segmentPoints.values()) {
					if(pList.contains(at)) contains = true; //Some segment already contains this pos.
				}
				if(!contains) {
					startSegment.add(at);
				}
			}
		}
		startSegment.add(start);
		segmentIds.put(highestId, start);
		segmentPoints.put(highestId, startSegment);
	}

	public void genFixedAnchors(ShiftMazeGenerator gen, int amtChests) {
		Point end = grid.getEndSegmentPos();
		Point start = grid.getStartSegmentPos();
		Point center = grid.getCentralSegment();

		MazeGrid.MazeSegment endSeg = grid.getSegment(end.x, end.y);
		endSeg.markVisited();
		endSeg.specialLock = true;
		ShiftMazeState stateEndDoor = gen.createNewMazeState();
		grid.getSegment(start.x, start.y).markVisited();

		backtrackStepper.reset();
		backtrackStepper.sortHorizontally();
		backtrackStepper.doStep(grid, end.x, end.y, true, true,
				new BacktrackDiscoverer.GenerateDoorAfterStepCondition(0, 0));

		Point reached = backtrackStepper.getReachedPoint();
		if(reached != null) {
			boolean gennedDoor = false;
			int rIndex = seededRand.nextInt(4);
			for (int t = rIndex; t < (rIndex + 4); t++) {
				ForgeDirection dir = MazeGrid.MazeSegment.VALID_CONNECTIONS.get(t % 4);
				if(canGenerateDoor(grid, reached.x, reached.y, dir, 0)) {
					genGoalEndDoor = this.genDoorUnsafe(reached.x, reached.y, dir, stateEndDoor);
					gennedDoor = true;
					generationGoals.put(genGoalEndDoor, end);
					bufferedStates.put(genGoalEndDoor, stateEndDoor);
					break;
				}
			}
			if(!gennedDoor)
				throw new IllegalStateException("Could not generate end door.");
		} else
			throw new IllegalStateException("Maze too small.");

		int startXLayer = start.x - 2;
		int endXLayer = 6;
		int xDst = (startXLayer - endXLayer) / amtChests;

		for (int i = 0; i < amtChests; i++) {
			Point chest = this.getRandomPos(endXLayer + (xDst * i));
			MazeGrid.MazeSegment seg = grid.getSegment(chest.x, chest.y);
			UUID key = gen.createNewDoorKeyId();
			seg.setTypeKeyChest(key);
			seg.specialLock = true;
			seg.markVisited();

			backtrackStepper.reset();
			backtrackStepper.sortHorizontally();
			backtrackStepper.doStep(grid, chest.x, chest.y, true, true, new BacktrackDiscoverer.GenerateDoorAfterStepCondition(1 + seededRand.nextInt(2), 2));

			reached = backtrackStepper.getReachedPoint();
			ShiftMazeState chestState = gen.createNewMazeState();
			if(reached != null) {
				boolean gennedDoor = false;
				List<ForgeDirection> dirs = this.sortDirections(reached, new Point(reached.x, center.y));
				for (ForgeDirection dir : dirs) {
					if(canGenerateDoor(grid, reached.x, reached.y, dir, 2)) {
						Point genGoal = this.genDoorUnsafe(reached.x, reached.y, dir, chestState);
						generationGoals.put(genGoal, chest);
						bufferedStates.put(genGoal, chestState);
						gennedDoor = true;
						break;
					}
				}
				if(!gennedDoor)
					throw new IllegalStateException("Could not generate a chest door.");
			} else
				throw new IllegalStateException("Maze too small."); //Not rly a thing
		}
	}

	public static boolean canGenerateDoor(MazeGrid grid, int sX, int sZ, ForgeDirection dir, int dstToBorders) {
		Point doorE = new Point(sX + dir.offsetX, sZ + dir.offsetZ);
		return grid.isFree(doorE.x, doorE.y) && !violatesAnchors(doorE, grid, null, null, dstToBorders);
	}

	private Point genDoorUnsafe(int sX, int sZ, ForgeDirection dir, ShiftMazeState... doorStates) {
		MazeGrid.MazeSegment seg = grid.getSegment(sX, sZ);
		seg.specialLock = true;
		seg.addConnection(dir);
		seg.markVisited();
		Point to = new Point(sX + dir.offsetX, sZ + dir.offsetZ);
		seg = grid.getSegment(to.x, to.y);
		seg.specialLock = true;
		seg.markVisited();
		MazeGrid.ShiftMazeDoor door = grid.appendDoor(new Point(sX, sZ), to, dir);
		Collections.addAll(door.doorStates, doorStates);
		return to;
	}

	private List<ForgeDirection> sortDirections(Point current, Point aim) {
		List<ForgeDirection> dirs = new LinkedList<>();
		if(seededRand.nextBoolean()) {
			dirs.add(ForgeDirection.NORTH);
			dirs.add(ForgeDirection.SOUTH);
		} else {
			dirs.add(ForgeDirection.SOUTH);
			dirs.add(ForgeDirection.NORTH);
		}
		dirs.add(ForgeDirection.WEST);
		return dirs;
		/*if(splHorizontal) {
            if(seededRand.nextBoolean()) {
                dirs.add(ForgeDirection.NORTH);
                dirs.add(ForgeDirection.SOUTH);
            } else {
                dirs.add(ForgeDirection.SOUTH);
                dirs.add(ForgeDirection.NORTH);
            }
            dirs.add(ForgeDirection.WEST);
            return dirs;
        }
        if(aim == null) {
            int rIndex = seededRand.nextInt(4);
            if(seededRand.nextBoolean()) dirs.add(ForgeDirection.WEST);
            for (int i = rIndex; i < (rIndex + 4); i++) {
                dirs.add(MazeGrid.MazeSegment.VALID_CONNECTIONS.get(i % 4));
            }
            return dirs;
        } else {
            if(current.x != aim.x) {
                if(current.x < aim.x) {
                    dirs.add(ForgeDirection.EAST);
                } else {
                    dirs.add(ForgeDirection.WEST);
                }
            }
            if(current.y != aim.y) {
                if(current.y < aim.y) {
                    dirs.add(ForgeDirection.SOUTH);
                } else {
                    dirs.add(ForgeDirection.NORTH);
                }
            }
            int rIndex = seededRand.nextInt(4);
            for (int i = rIndex; i < (rIndex + 4); i++) {
                ForgeDirection dir = MazeGrid.MazeSegment.VALID_CONNECTIONS.get(i % 4);
                if(!dirs.contains(dir)) {
                    dirs.add(dir);
                }
            }
        }
        return dirs;*/
	}

	private Point getRandomPos(int xLayer) {
		int rZOffset = seededRand.nextInt(grid.getZSize());
		for (int zz = 0; zz < grid.getZSize(); zz++) {
			int pZ = (zz + rZOffset) % grid.getZSize();
			Point p = new Point(xLayer, pZ);
			if(grid.isFree(xLayer, pZ) && !violatesAnchors(p, grid, this.getGenerationGoals(), this.getPlayerGoals(), 2)) return p;
		}
		throw new IllegalStateException("Cannot get random position that's far enough away from all current anchors. Maze too small?");
		/*Point p;
        do {
            int rX = seededRand.nextInt(grid.getXSize());
            int rZ = seededRand.nextInt(grid.getZSize());
            p = new Point(rX, rZ);
            tries--;
            if(tries < 0)
                throw new IllegalStateException("Cannot get random position that's far enough away from all current anchors. Maze too small?");
        } while (!grid.isFree(p.x, p.y) || violatesAnchors(p, grid, getGenerationGoals(), getPlayerGoals()));
        return p;*/
	}

	private static boolean violatesAnchors(Point p, MazeGrid grid, Collection<Point> genGoals, Collection<Point> plGoals, int dstToBorders) {
		if(p.x < dstToBorders || p.y < dstToBorders ||
				p.x > (grid.getXSize() - dstToBorders - 1) ||
				p.y > (grid.getZSize() - dstToBorders - 1)) {
			return true; //Not at edges.
		}
		if(Math.abs(grid.getXSize() - p.x) < 2 &&
				Math.abs((grid.getZSize() / 2) - p.y) < 2) {
			//Ensure distanceToStart.
			return true;
		}
		if(Math.abs(p.x) < 2 &&
				Math.abs((grid.getZSize() / 2) - p.y) < 2) {
			//Ensure distanceToEnd.
			return true;
		}

		if(genGoals != null) {
			for (Point other : genGoals) {
				int xDiff = Math.abs(other.x - p.x);
				int zDiff = Math.abs(other.y - p.y);
				if(xDiff == 0 || zDiff == 0) return true;
				if((xDiff + zDiff) < 3) return true;
			}
		}
		if(plGoals != null) {
			for (Point other : plGoals) {
				int xDiff = Math.abs(other.x - p.x);
				int zDiff = Math.abs(other.y - p.y);
				if(xDiff == 0 || zDiff == 0) return true;
				if((xDiff + zDiff) < 3) return true;
			}
		}
		return false;
	}

	public static class BufferedDoorState {

		public final Point from, to;
		public final ForgeDirection dir;
		public final MazeSegmentNode fromNode, toNode;

		private BufferedDoorState(Point from, Point to, ForgeDirection dir,
				MazeSegmentNode fromNode,
				MazeSegmentNode toNode) {
			this.from = from;
			this.to = to;
			this.dir = dir;
			this.fromNode = fromNode;
			this.toNode = toNode;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || this.getClass() != o.getClass()) return false;
			BufferedDoorState that = (BufferedDoorState) o;

			if((from.equals(that.from) && to.equals(that.to) &&
					fromNode.equals(that.fromNode) && toNode.equals(that.toNode)) ||
					from.equals(that.to) && to.equals(that.from) &&
					fromNode.equals(that.toNode) && toNode.equals(that.fromNode)) return true;

			return false;
		}

	}

	public static class ComparatorGridDistance implements Comparator<Point> {

		private final Point toCompareTo;

		public ComparatorGridDistance(Point toCompareTo) {
			this.toCompareTo = toCompareTo;
		}

		/**
		 * Note: this comparator imposes orderings that are inconsistent with equals.
		 */

		@Override
		public int compare(Point o1, Point o2) {
			int x1Diff = Math.abs(toCompareTo.x - o1.x);
			int x2Diff = Math.abs(toCompareTo.x - o2.x);
			return x1Diff - x2Diff;
		}

	}

}
