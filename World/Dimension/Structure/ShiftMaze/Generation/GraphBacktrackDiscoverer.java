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

import java.util.ArrayDeque;

//Only used to find a path on a graph that is at least X steps long or longer if exists.
//Returns shorter path if X long or longer doesn't exist.
//Doesn't contain duplicate nodes.
//Result includes StartNode + EndNode.
public class GraphBacktrackDiscoverer {

    public static ArrayDeque<MazeSegmentNode> getNodePath(MazeSegmentNode fromNode, MazeSegmentNode toNode, int steps) {
        ArrayDeque<MazeSegmentNode> nodePath = new ArrayDeque<>();
        nodePath.push(fromNode);
        nodePath = recDiscoverGraph(nodePath, fromNode, toNode, steps);
        return nodePath;
    }

    private static ArrayDeque<MazeSegmentNode> recDiscoverGraph(ArrayDeque<MazeSegmentNode> nodePath,
                                         MazeSegmentNode fromNode, MazeSegmentNode toNode, int steps) {
        /*if(fromNode.equals(toNode)) {
            nodePath.push(fromNode);
            return nodePath;
        }*/

        boolean containsDirectPath = fromNode.connectionDoors.containsKey(toNode);
        if(steps <= 0 && containsDirectPath) {
            nodePath.push(toNode);
            return nodePath;
        }

        for (MazeSegmentNode next : fromNode.connectionDoors.keySet()) {
            if(nodePath.contains(next)) continue; //We don't go back/in circles

            if(next.equals(toNode)) {
                if(steps <= 0) {
                    nodePath.push(next);
                    return nodePath;
                }
                continue; //We don't go the the exit before remainingSteps reaches <= 0!
            }

            ArrayDeque<MazeSegmentNode> nextPath = new ArrayDeque<>(nodePath);
            nextPath.push(next);

            ArrayDeque<MazeSegmentNode> resolvedPath =
                    recDiscoverGraph(nextPath, next, toNode, steps - 1);

            if(resolvedPath != null) {
                return resolvedPath;
            }
        }
        if(containsDirectPath) {
            nodePath.push(toNode);
            return nodePath;
        }
        return null;
    }

}
