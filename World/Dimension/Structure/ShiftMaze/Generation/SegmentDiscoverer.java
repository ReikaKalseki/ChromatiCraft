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

import Reika.ChromatiCraft.World.Dimension.Structure.ShiftMaze.MazeGrid;
import Reika.ChromatiCraft.World.Dimension.Structure.ShiftMaze.ShiftMazeState;
import net.minecraftforge.common.util.ForgeDirection;

import java.awt.Point;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SegmentDiscoverer {

    private final MazeGrid grid;
    //SegmentID -> SegmentPoints
    //private Map<Integer, List<Point>> segments = new HashMap<Integer, List<Point>>();
    private List<Point> overallVisisted = new LinkedList<>();
    private int segmentCounter = 0;

    public SegmentDiscoverer(MazeGrid grid) {
        this.grid = grid;
    }

    public void reset() {
        //segments.clear();
        segmentCounter = 0;
        overallVisisted.clear();
    }

    /*public int getSegmentsFound() {
        return segments.size();
    }*/

    public boolean hasFound(Point toFind) {
        for (Point p : overallVisisted) {
            if(p.equals(toFind)) return true;
            /*for (Point p : points) {
                if(p.equals(toFind)) return true;
            }*/
        }
        return false;
    }

    //Note: If doors are not ignored, door segments themselves don't belong to any segment unless they're open.
    public void runCalc(Collection<Point> segmentStartPoints, ShiftMazeState mazeState, boolean ignoreDoorStates) {
        for (Point p : segmentStartPoints) {
            startCalculation(p, mazeState, ignoreDoorStates);
        }
    }

    private void startCalculation(Point start, ShiftMazeState mazeState, boolean ignoreDoorStates) {
        int count = 0;
        //int segmentId = getOrCreateSegment(start);
        overallVisisted.add(start);
        Deque<Point> todo = new LinkedList<>();
        todo.add(start);
        while (!todo.isEmpty()) {
            Point next = todo.removeFirst();
            List<Point> surrounded = getConnectingSegments(next, mazeState, ignoreDoorStates);
            //segments.get(segmentId).addAll(surrounded);
            count += surrounded.size();
            todo.addAll(surrounded);
        }
        System.out.println("Found " + count + " overall mazeParts to connect to.");
    }

    private List<Point> getConnectingSegments(Point center, /*int segmentId, */ShiftMazeState state, boolean ignoreStates) {
        List<Point> collected = new LinkedList<>();
        MazeGrid.MazeSegment at = grid.getSegment(center.x, center.y);
        for (ForgeDirection dir : MazeGrid.MazeSegment.VALID_CONNECTIONS) {
            int tX = center.x + dir.offsetX;
            int tZ = center.y + dir.offsetZ;
            Point target = new Point(tX, tZ);
            System.out.println("Point to scan: " + target);
            if(overallVisisted.contains(target)) {
                System.out.println("Found an already-found point. Not adding.");
                continue;
            }
            //if(segments.get(segmentId).contains(target)) continue; //Already visited.

            if(grid.isInBounds(tX, tZ)) {
                overallVisisted.add(new Point(tX, tZ));

                MazeGrid.ShiftMazeDoor door = grid.getDoor(center, target);
                if(door != null) {
                    System.out.println("Found a door!");
                }
                if(door != null && state != null && !ignoreStates) {
                    System.out.println("Need to do door state checks.");
                    boolean foundOpenState = door.doorStates.isEmpty();
                    for (ShiftMazeState openState : door.doorStates) {
                        if(openState.equals(state)) {
                            foundOpenState = true;
                            break;
                        }
                    }
                    if(!foundOpenState) continue;
                }

                MazeGrid.MazeSegment other = grid.getSegment(tX, tZ);
                /*if(other.getType().equals(MazeGrid.SegmentType.DOOR)) {
                    if (state == null || (!ignoreStates && !state.isDoorOpen(target))) {
                        continue;
                    }
                }*/
                if(other.getConnections().contains(dir.getOpposite()) ||
                        at.getConnections().contains(dir)) { //If either of them connects to the other.
                    System.out.println("Connecting with direction " + dir.name());
                    collected.add(target);
                }
            }
        }
        System.out.println("Found " + collected.size() + " connections.");
        return collected;
    }

    /*private int searchSegmentId(Point p) {
        for (Map.Entry<Integer, List<Point>> segmentEntry : segments.entrySet()) {
            if(segmentEntry.getValue().contains(p)) return segmentEntry.getKey();
        }
        return -1;
    }

    private int getOrCreateSegment(Point p) {
        int segmentId = searchSegmentId(p);
        if(segmentId != -1) return segmentId;
        segmentId = segmentCounter;
        segments.put(segmentId, new LinkedList<Point>());
        segmentCounter++;
        return segmentId;
    }*/

}
