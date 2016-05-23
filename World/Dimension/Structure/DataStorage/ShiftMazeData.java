/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
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
import Reika.ChromatiCraft.World.Dimension.Structure.ShiftMazeGenerator.MazeState;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;

public class ShiftMazeData extends StructureData {

	private int state = 0;
	private final int length;
	private final ShiftMazeGenerator shift;

	public ShiftMazeData(ShiftMazeGenerator gen, int len) {
		super(gen);
		length = len;
		shift = (ShiftMazeGenerator)generator;
	}

	@Override
	public void load(HashMap<String, Object> map) {

	}

	@Override
	public void onInteract(World world, int x, int y, int z, EntityPlayer ep, int s, HashMap<String, Object> extraData) {
		this.cycle(world, x, y, z);
	}

	private void cycle(World world, int x, int y, int z) {
		MazeState last = shift.getState(state);
		state = (state+1)%length;
		MazeState active = shift.getState(state);
		for (Coordinate c : shift.getLocks()) {
			int dx = c.xCoord;
			int dz = c.zCoord;
			if (last.isPositionOpen(dx, dz) && !active.isPositionOpen(dx, dz)) {
				this.toggleLock(world, c, false);
			}
			else if (!last.isPositionOpen(dx, dz) && active.isPositionOpen(dx, dz)) {
				this.toggleLock(world, c, true);
			}
		}
		ReikaSoundHelper.playBreakSound(world, x, y, z, Blocks.stone);
	}

	private void toggleLock(World world, Coordinate c, boolean open) {
		int meta = open ? 1 : 0;
		c.setBlock(world, ChromaBlocks.SHIFTLOCK.getBlockInstance(), meta);
		c.triggerBlockUpdate(world, false);
		ReikaSoundHelper.playBreakSound(world, c.xCoord, c.yCoord, c.zCoord, Blocks.stone);
	}

}
