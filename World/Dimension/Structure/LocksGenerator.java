/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure;

import java.util.Random;

import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Block.Dimension.BlockColoredLock;
import Reika.ChromatiCraft.Block.Dimension.BlockLockKey;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class LocksGenerator extends DimensionStructureGenerator {

	@Override
	public void calculate(int chunkX, int chunkZ, CrystalElement e, Random rand) {
		BlockColoredLock.resetCaches(this);
	}

	@Override
	protected int getCenterXOffset() {
		return 0;
	}

	@Override
	protected int getCenterZOffset() {
		return 0;
	}

	/** The number of "exit gates" for a given room */
	public int getNumberGates(int i) {
		return BlockLockKey.LockChannel.lockList[i].numberKeys;
	}
}
