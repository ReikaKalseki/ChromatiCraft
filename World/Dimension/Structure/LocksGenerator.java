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

import java.util.BitSet;
import java.util.Random;

import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Block.Dimension.BlockColoredLock;
import Reika.ChromatiCraft.Block.Dimension.BlockLockKey;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.World.Dimension.Structure.Locks.LockRoomConnector;

public class LocksGenerator extends DimensionStructureGenerator {

	private BitSet structureSet = new BitSet(12);

	@Override
	public void calculate(int x, int z, CrystalElement e, Random rand) {
		BlockColoredLock.resetCaches(this);

		new LockRoomConnector(this, rand.nextInt(9), rand.nextInt(9), rand.nextInt(9), rand.nextInt(9)).setWindowed().generate(world, x, 20, z);
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
	public final int getNumberGates(int i) {
		return BlockLockKey.LockChannel.lockList[i].numberKeys;
	}
}
