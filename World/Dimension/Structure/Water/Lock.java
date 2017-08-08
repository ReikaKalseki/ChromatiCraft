/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure.Water;

import java.awt.Point;
import java.util.HashSet;

import net.minecraftforge.common.util.ForgeDirection;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;


public class Lock {

	public static final int SIZE = 2;
	private static final int ROTATION_STEP = 90;

	/** At zero rotation */
	final HashSet<ForgeDirection> openEnds = new HashSet();
	final Point location;

	private int rotation;

	private final WaterFloor level;

	Coordinate centerLocation;

	ForgeDirection facing = ForgeDirection.EAST;
	boolean hasFluid;

	Lock(WaterFloor f, int i, int k) {
		level = f;

		location = new Point(i, k);
	}

	//see if can make animated, slow, heavy-sounding
	void rotate(boolean updateParent) { //CLOCKWISE
		rotation += ROTATION_STEP;
		facing = ReikaDirectionHelper.getRightBy90(facing);
		if (updateParent)
			;//level.updateChannels();
	}

	public int getRotation() {
		return rotation;
	}

	public boolean isDirectionOpen(ForgeDirection dir) {
		for (int i = 0; i < this.getRotation()/ROTATION_STEP; i++)
			dir = ReikaDirectionHelper.getLeftBy90(dir);
		return openEnds.contains(dir);
	}

}
