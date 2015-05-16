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

public class LocksRoomWhite extends LockLevel {

	public LocksRoomWhite(LocksGenerator g) {
		super(g, LockChannel.WHITE);
	}

	@Override
	public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {

	}

	@Override
	public int getWidth() {
		return 11;
	}

	@Override
	public int getLength() {
		return 33;
	}

	@Override
	public int getEnterExitDL() {
		return 35;
	}

	@Override
	public int getEnterExitDT() {
		return 2;
	}

	@Override
	public int getDifficultyRating() {
		return 1;
	}

	@Override
	public int getFeatureRating() {
		return 2;
	}

}
