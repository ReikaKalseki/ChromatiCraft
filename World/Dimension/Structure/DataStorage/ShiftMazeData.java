/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure.DataStorage;

import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.StructureData;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.Structure.ShiftMazeGenerator;
import Reika.ChromatiCraft.World.Dimension.Structure.ShiftMaze.ShiftMazeState;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;

public class ShiftMazeData extends StructureData {

	private final ShiftMazeGenerator gen;

	public ShiftMazeData(ShiftMazeGenerator gen) {
		super(gen);
		this.gen = gen;
	}

	@Override
	public void load(HashMap<String, Object> map) {}

	@Override
	public void onInteract(World world, int x, int y, int z, EntityPlayer ep, int s, HashMap<String, Object> extraData) {
		this.cycleState(world, x, y, z);
	}

	private void cycleState(World world, int x, int y, int z) {
		ShiftMazeState lastState = gen.getActiveState();
		gen.cycleState();
		ShiftMazeState newState = gen.getActiveState();
		for (Coordinate c : gen.getAllToggleDoors()) {
			if(lastState.isDoorOpen(c) && !newState.isDoorOpen(c)) {
				this.toggleDoor(world, c, false);
			}
			if(!lastState.isDoorOpen(c) && newState.isDoorOpen(c)) {
				this.toggleDoor(world, c, true);
			}
		}
		ReikaSoundHelper.playBreakSound(world, x, y, z, Blocks.stone);
	}

	private void toggleDoor(World world, Coordinate c, boolean open) {
		int meta = open ? 1 : 0;
		c.setBlock(world, ChromaBlocks.SHIFTLOCK.getBlockInstance(), meta);
		c.triggerBlockUpdate(world, false);
		ReikaSoundHelper.playBreakSound(world, c.xCoord, c.yCoord, c.zCoord, Blocks.stone);
	}

}
