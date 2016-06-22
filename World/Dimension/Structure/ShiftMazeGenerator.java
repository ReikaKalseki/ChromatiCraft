/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.StructureData;
import Reika.ChromatiCraft.Block.BlockChromaDoor;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.World.Dimension.Structure.DataStorage.ShiftMazeData;
import Reika.ChromatiCraft.World.Dimension.Structure.ShiftMaze.MazeBuilder;
import Reika.ChromatiCraft.World.Dimension.Structure.ShiftMaze.MazeGrid;
import Reika.ChromatiCraft.World.Dimension.Structure.ShiftMaze.ShiftMazeEntrance;
import Reika.ChromatiCraft.World.Dimension.Structure.ShiftMaze.ShiftMazeState;
import Reika.ChromatiCraft.World.Dimension.Structure.ShiftMaze.Generation.MazeCalculator;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;

//Credit to HellFirePVP for this dynamic shifting maze implementation
//Carving generator
public class ShiftMazeGenerator extends DimensionStructureGenerator {

	private MazeGrid grid = null;
	//Those are coords for the center of 1st entrance-segment and last end-segment.

	private List<Coordinate> endDoorCoords = new LinkedList();
	private List<Coordinate> toggleDoors = new LinkedList();
	private List<UUID> requestedKeys = new LinkedList();

	private List<ShiftMazeState> states = new ArrayList();
	private int indexActiveState = 0;

	@Override
	protected void calculate(int x, int z, Random rand) {
		posY = 20+rand.nextInt(40);

		int rdX = this.calcSizeX(rand);
		int rdZ = this.calcSizeZ(rand);
		grid = new MazeGrid(rdX, rdZ);
		entryX = x + rdX * 4;
		entryZ = z + ((rdZ / 2) * 4 + 1);

		int startSegX = x + rdX * 4 - 3; //?
		int startSegZ = z + ((rdZ / 2) * 4 + 1);
		int endSegX = x + 1;
		int endSegZ = z + ((rdZ / 2) * 4 + 1);

		int chestCount = this.getChestAmount();

		MazeCalculator calc = new MazeCalculator(grid, rand);
		calc.genFixedAnchors(this, chestCount);
		calc.calcMazeSegments();
		calc.calcSegmentInternalMazes();
		calc.calcSegmentGraph();
		calc.calcGraphPaths();
		calc.pushDoorsToGrid(); //Applies all calculated data to the grid.
		indexActiveState = rand.nextInt(states.size());

		//NOTE: End is generated in -x direction.
		//Generate maze into the MazeGrid before building stuff with the MazeBuilder...
		MazeBuilder builder = new MazeBuilder(world, x, posY, z);
		builder.buildMazeBase(grid);
		builder.build(this, grid);
		builder.placeControllerTile(this, startSegX + 2, posY + 6, startSegZ);
		builder.buildEntrance(startSegX + 2, posY + 2, startSegZ);
		builder.buildExit(this, endSegX - 2, posY, endSegZ, requestedKeys);

		this.addDynamicStructure(new ShiftMazeEntrance(this), entryX, entryZ);
	}

	private int getChestAmount() {
		switch (ChromaOptions.getStructureDifficulty()) {
			case 1:
				return 4;
			case 2:
				return 5;
			case 3:
				return 6;
			default:
				return 4;
		}
	}

	private int calcSizeX(Random rand) {
		switch (ChromaOptions.getStructureDifficulty()) {
			case 1:
				return rand.nextInt(4) * 2 + 21;
			case 2:
				return rand.nextInt(5) * 2 + 25;
			case 3:
			default:
				return rand.nextInt(7) * 2 + 29; //Have fun lol....
		}
	}

	private int calcSizeZ(Random rand) {
		switch (ChromaOptions.getStructureDifficulty()) {
			case 1:
				return rand.nextInt(4) * 2 + 19;
			case 2:
				return rand.nextInt(5) * 2 + 23;
			case 3:
			default:
				return rand.nextInt(6) * 2 + 29; //ENJOY!
		}
	}

	@Override
	public StructureData createDataStorage() {
		return new ShiftMazeData(this);
	}

	public UUID createNewDoorKeyId() {
		UUID newKey = UUID.randomUUID();
		requestedKeys.add(newKey);
		return newKey;
	}

	public ShiftMazeState createNewMazeState() {
		ShiftMazeState state = new ShiftMazeState();
		states.add(state);
		return state;
	}

	public List<ShiftMazeState> getAllStates() {
		return Collections.unmodifiableList(states);
	}

	public void addEndDoor(int x, int y, int z) {
		endDoorCoords.add(new Coordinate(x, y, z));
	}

	public void addToggleDoor(int x, int y, int z, List<ShiftMazeState> states) {
		Coordinate c = new Coordinate(x, y, z);
		toggleDoors.add(c);
		for (ShiftMazeState state : states)
			state.appendToggleDoor(c);
	}

	public Collection<Coordinate> getAllToggleDoors() {
		return Collections.unmodifiableCollection(toggleDoors);
	}

	public ShiftMazeState getActiveState() {
		return states.get(indexActiveState);
	}

	public void cycleState() {
		indexActiveState = (indexActiveState + 1) % states.size();
	}

	@Override
	protected int getCenterXOffset() {
		return 0;
	}

	@Override
	protected int getCenterZOffset() {
		return 0;
	}

	@Override
	protected void clearCaches() {
		grid = null;

		requestedKeys.clear();
		endDoorCoords.clear();
		toggleDoors.clear();
		states.clear();

		indexActiveState = 0;
	}

	@Override
	public boolean hasBeenSolved(World world) {
		for (Coordinate c : endDoorCoords) {
			if (!BlockChromaDoor.isOpen(world, c.xCoord, c.yCoord, c.zCoord))
				return false;
		}
		return true;
	}

}
