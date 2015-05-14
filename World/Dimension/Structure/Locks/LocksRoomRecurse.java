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

public class LocksRoomRecurse extends LockLevel {

	public LocksRoomRecurse(LocksGenerator g) {
		super(g, LockChannel.RECURSION);
	}

	@Override
	public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {

	}

	@Override
	public int getWidth() {
		return 0;
	}

	@Override
	public int getLength() {
		return 0;
	}

	@Override
	public int getEnterExitDX() {
		return 0;
	}

	@Override
	public int getEnterExitDZ() {
		return 0;
	}

	@Override
	public int getDifficultyRating() {
		return 3;
	}

	@Override
	public int getFeatureRating() {
		return 2;
	}

}
