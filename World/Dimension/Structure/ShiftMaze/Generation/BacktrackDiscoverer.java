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
import net.minecraftforge.common.util.ForgeDirection;

import java.awt.Point;
import java.util.LinkedList;
import java.util.Random;
import java.util.List;

public class BacktrackDiscoverer {

    private final Random seededRand;

    private Point reachedPoint = null;
    private List<Point> deadEndList = null;
    private List<Point> visited = null;
    private SegmentRestriction restriction;
    private int maxSteps = Integer.MAX_VALUE;
    private boolean sortHorizontal = false;

    public BacktrackDiscoverer(Random rand) {
        this.seededRand = rand;
    }

    public void reset() {
        this.reachedPoint = null;
        this.deadEndList = null;
        this.visited = null;
        this.restriction = null;
        this.sortHorizontal = false;
        this.maxSteps = Integer.MAX_VALUE;
    }

    public Point getReachedPoint() {
        return reachedPoint;
    }

    public void appendDeadEndsTo(List<Point> points) {
        this.deadEndList = points;
    }

    public void appendVisitedPointsTo(List<Point> points) {
        this.visited = points;
    }

    public void capSteps(int maxSteps) {
        this.maxSteps = maxSteps;
    }

    public void setSegmentRestriction(SegmentRestriction restriction) {
        this.restriction = restriction;
    }

    public void sortHorizontally() {
        this.sortHorizontal = true;
    }

    public boolean doStep(MazeGrid grid, int sX, int sZ, boolean markPassedNodes,
                          boolean lockNodes, GridSearchCondition condition) {
        if(condition.isMet(grid, sX, sZ)) {
            reachedPoint = new Point(sX, sZ);
            return true;
        }
        maxSteps--;
        if(maxSteps <= 0) {
            reachedPoint = new Point(sX, sZ);
            return true;
        }

        List<ForgeDirection> dirs = sortDirections(/*new Point(sX, sZ)*/);

        boolean isFlatDeadEnd = true;
        for (ForgeDirection dir : dirs) {
            int tX = sX + dir.offsetX;
            int tZ = sZ + dir.offsetZ;
            if((restriction == null ? grid.isInBounds(tX, tZ) : restriction.isInBounds(tX, tZ)) && grid.isFree(tX, tZ)) {
                isFlatDeadEnd = false;
                MazeGrid.MazeSegment seg = grid.getSegment(tX, tZ);
                seg.addConnection(dir.getOpposite()); //Where we came from.
                if(markPassedNodes) {
                    seg.markVisited();
                }
                if(lockNodes) {
                    seg.specialLock = true;
                }
                if(visited != null) {
                    visited.add(new Point(tX, tZ));
                }
                if(doStep(grid, tX, tZ, markPassedNodes, lockNodes, condition.copyNext())) return true;
            }
        }
        if(isFlatDeadEnd && deadEndList != null) {
            deadEndList.add(new Point(sX, sZ));
        }
        return false;
    }

    private List<ForgeDirection> sortDirections(/*Point current*/) {
        List<ForgeDirection> dirs = new LinkedList<>();
        //if(splitPoint == null) {
        if(sortHorizontal) {
            if(seededRand.nextBoolean()) {
                dirs.add(ForgeDirection.NORTH);
                dirs.add(ForgeDirection.SOUTH);
            } else {
                dirs.add(ForgeDirection.SOUTH);
                dirs.add(ForgeDirection.NORTH);
            }
            dirs.add(ForgeDirection.WEST);
            //dirs.add(ForgeDirection.EAST);
        } else {
            int rIndex = seededRand.nextInt(4);
            for (int i = rIndex; i < (rIndex + 4); i++) {
                dirs.add(MazeGrid.MazeSegment.VALID_CONNECTIONS.get(i % 4));
            }
        }
        return dirs;
        /*} else {
            if(current.x != splitPoint.x) {
                if(current.x < splitPoint.x) {
                    dirs.add(ForgeDirection.EAST);
                } else {
                    dirs.add(ForgeDirection.WEST);
                }
            }
            if(current.y != splitPoint.y) {
                if(current.y < splitPoint.y) {
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
        }*/
    }

    public static interface GridSearchCondition {

        public boolean isMet(MazeGrid grid, int sX, int sZ);

        public GridSearchCondition copyNext();

    }

    public static class MatchCoordCondition implements GridSearchCondition {

        private final int matchX, matchZ;

        public MatchCoordCondition(int matchX, int matchZ) {
            this.matchZ = matchZ;
            this.matchX = matchX;
        }

        @Override
        public boolean isMet(MazeGrid grid, int sX, int sZ) {
            return sX == matchX && sZ == matchZ;
        }

        @Override
        public GridSearchCondition copyNext() {
            return this;
        }

    }

    public static class GenerateDoorAfterStepCondition implements GridSearchCondition {

        private final int steps, dstBorders;

        public GenerateDoorAfterStepCondition(int count, int dstBorders) {
            this.steps = count;
            this.dstBorders = dstBorders;
        }

        @Override
        public boolean isMet(MazeGrid grid, int sX, int sZ) {
            if(steps <= 0) {
                for (ForgeDirection dir : MazeGrid.MazeSegment.VALID_CONNECTIONS) {
                    if(MazeCalculator.canGenerateDoor(grid, sX, sZ, dir, dstBorders)) return true;
                }
            }
            return false;
        }

        @Override
        public GridSearchCondition copyNext() {
            return new GenerateDoorAfterStepCondition(steps - 1, dstBorders);
        }

    }

}
