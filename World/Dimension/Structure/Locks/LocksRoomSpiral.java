/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure.Locks;

import Reika.ChromatiCraft.Base.LockLevel;
import Reika.ChromatiCraft.Block.Dimension.Structure.BlockLockKey.LockChannel;
import Reika.ChromatiCraft.World.Dimension.Structure.LocksGenerator;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;

public class LocksRoomSpiral extends LockLevel {

	public LocksRoomSpiral(LocksGenerator g) {
		super(g, LockChannel.SPIRAL);
	}

	@Override
	public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {

	}

	@Override
	public int getWidth() {
		return 9;
	}

	@Override
	public int getLength() {
		return 79;
	}

	@Override
	public int getEnterExitDL() {
		return 81;
	}

	@Override
	public int getEnterExitDT() {
		return 0;
	}

	@Override
	public int getDifficultyRating() {
		return 4;
	}

	@Override
	public int getFeatureRating() {
		return 5;
	}

}
