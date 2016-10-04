/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure.Water;

import java.util.HashSet;

import net.minecraftforge.common.util.ForgeDirection;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;


public class Lock {

	public static final int SIZE = 3;
	private static final int ROTATION_STEP = 90;

	/** At zero rotation */
	private final HashSet<ForgeDirection> openEnds = new HashSet();

	private int rotation;

	public void rotate() { //CLOCKWISE
		rotation += ROTATION_STEP;
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
