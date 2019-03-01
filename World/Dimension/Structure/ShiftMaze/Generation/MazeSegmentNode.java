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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import Reika.ChromatiCraft.World.Dimension.Structure.ShiftMaze.MazeGrid;
import Reika.ChromatiCraft.World.Dimension.Structure.ShiftMaze.ShiftMazeState;

/**
* This class is part of the Gadomancy Mod
* Gadomancy is Open Source and distributed under the
* GNU LESSER GENERAL PUBLIC LICENSE
* for more read the LICENSE file
* <p/>
* Created by HellFirePvP @ 18.06.2016 / 14:54
*/
public class MazeSegmentNode {

    public final int nodeId;
    public final Map<MazeSegmentNode, List<MazeGrid.ShiftMazeDoor>> connectionDoors = new HashMap<>();

    public MazeSegmentNode(int nodeId) {
        this.nodeId = nodeId;
    }

    public void addStateForConnection(MazeSegmentNode other, Random rand, ShiftMazeState state) {
        if(state == null)
            throw new IllegalArgumentException("Trying to add null state!");
        List<MazeGrid.ShiftMazeDoor> doors = connectionDoors.get(other);
        if(doors == null || doors.isEmpty()) {
            throw new IllegalArgumentException("Trying to add door state to connection without doors. Nodes not connected in graph.");
        }
        MazeGrid.ShiftMazeDoor randomDoor = doors.get(rand.nextInt(doors.size()));
        randomDoor.doorStates.add(state);
    }

    @Override
    public String toString() {
        return String.valueOf(nodeId);
    }
}
