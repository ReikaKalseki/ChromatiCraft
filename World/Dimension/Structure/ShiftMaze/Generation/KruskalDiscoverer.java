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
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

//Based on 'Randomly' removing/carving walls
public class KruskalDiscoverer {

    private final MazeGrid grid;
    private final Random seededRand;

    public KruskalDiscoverer(Random rand, MazeGrid grid) {
        this.grid = grid;
        this.seededRand = rand;
    }

    public void calculateMazeSegment(List<Point> pointsInSegment) {

        Map<Point, List<Point>> associated = new HashMap<>();
        List<GridConnection> cons = new LinkedList<>();
        for (int xx = 0; xx < grid.getXSize(); xx++) {
            for (int zz = 0; zz < grid.getZSize(); zz++) {
                Point at =      new Point(xx,     zz);

                if(!pointsInSegment.contains(at)) continue;
                if(grid.getSegment(xx, zz).specialLock) continue;

                Point southAt = new Point(xx,     zz + 1);
                Point eastAt =  new Point(xx + 1, zz);

                if(!associated.containsKey(at)) associated.put(at, new LinkedList<Point>());

                if(pointsInSegment.contains(southAt) && grid.isInBounds(southAt.x, southAt.y)
                        && grid.isFree(southAt.x, southAt.y) && !grid.getSegment(southAt.x, southAt.y).specialLock) {

                    cons.add(new GridConnection(at, southAt, ForgeDirection.SOUTH));
                    if(!associated.containsKey(southAt)) associated.put(southAt, new LinkedList<Point>());
                }
                if(pointsInSegment.contains(eastAt) && grid.isInBounds(southAt.x, southAt.y)
                        && grid.isFree(eastAt.x, eastAt.y) && !grid.getSegment(eastAt.x, eastAt.y).specialLock) {

                    cons.add(new GridConnection(at, eastAt, ForgeDirection.EAST));
                    if(!associated.containsKey(eastAt)) associated.put(eastAt, new LinkedList<Point>());
                }
            }
        }

        Collections.shuffle(cons, seededRand);

        for (GridConnection connection : cons) {
            Point from = connection.from;
            Point to = connection.to;

            if(associated.get(from).contains(to)) continue;

            //Do checks here in case we ever want to use Kruskal.

            associated.get(from).addAll(associated.remove(to));
            associated.put(to, associated.get(from));

            grid.getSegment(from.x, from.y).addConnection(connection.fromToDirection);
            grid.getSegment(to.x, to.y).addConnection(connection.fromToDirection.getOpposite());
        }

    }

    private static class GridConnection {

        private final Point from, to;
        private final ForgeDirection fromToDirection;

        private GridConnection(Point from, Point to, ForgeDirection fromToDirection) {
            this.from = from;
            this.to = to;
            this.fromToDirection = fromToDirection;
        }
    }

}
