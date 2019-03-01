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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import Reika.ChromatiCraft.World.Dimension.Structure.ShiftMaze.MazeGrid;

public class MazeSegmentGraph {

    private Map<Integer, MazeSegmentNode> segmentNodes = new HashMap<>();

    public MazeSegmentNode createOrGetNode(int id) {
        if(segmentNodes.containsKey(id)) return segmentNodes.get(id);
        MazeSegmentNode node = new MazeSegmentNode(id);
        this.segmentNodes.put(id, node);
        return node;
    }

    public void connect(MazeSegmentNode segNode, MazeSegmentNode otherNode) {
        if(!segNode.connectionDoors.containsKey(otherNode)) segNode.connectionDoors.put(otherNode, new LinkedList<MazeGrid.ShiftMazeDoor>());
        if(!otherNode.connectionDoors.containsKey(segNode)) otherNode.connectionDoors.put(segNode, new LinkedList<MazeGrid.ShiftMazeDoor>());
    }

    //Needs to be done in case they COULD connect, but can't/didn't connect for some reason.
    public void checkAndDisconnect(MazeSegmentNode segNode, MazeSegmentNode otherNode) {
        if(segNode.connectionDoors.containsKey(otherNode)) {
            if(segNode.connectionDoors.get(otherNode).isEmpty()) {
                segNode.connectionDoors.remove(otherNode);
                otherNode.connectionDoors.remove(segNode);
            }
        } else if(otherNode.connectionDoors.containsKey(segNode)) {
            if(otherNode.connectionDoors.get(segNode).isEmpty()) {
                otherNode.connectionDoors.remove(segNode);
                segNode.connectionDoors.remove(otherNode);
            }
        }
    }

    public MazeSegmentNode getNode(int id) {
        return segmentNodes.get(id);
    }

    public Collection<Integer> getAllKnownNodeIDs() {
        return Collections.unmodifiableCollection(segmentNodes.keySet());
    }

    public void pushNewDoor(MazeCalculator.BufferedDoorState emptyDoor) {
        MazeGrid.ShiftMazeDoor door = new MazeGrid.ShiftMazeDoor(emptyDoor.from, emptyDoor.to, emptyDoor.dir);
        emptyDoor.fromNode.connectionDoors.get(emptyDoor.toNode).add(door);
        emptyDoor.toNode.connectionDoors.get(emptyDoor.fromNode).add(door);
    }

}
