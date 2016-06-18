/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure.ShiftMaze.Generation;

import Reika.ChromatiCraft.World.Dimension.Structure.ShiftMaze.MazeGrid;

import java.awt.*;
import java.util.List;

public class GridSegment implements SegmentRestriction {

    private final MazeGrid grid;
    public final List<Point> segmentPoints;
    public final int id;

    public GridSegment(MazeGrid grid, List<Point> segmentPoints, int id) {
        this.grid = grid;
        this.segmentPoints = segmentPoints;
        this.id = id;
    }

    @Override
    public boolean isInBounds(int x, int z) {
        return grid.isInBounds(x, z) && segmentPoints.contains(new Point(x, z));
    }

}
