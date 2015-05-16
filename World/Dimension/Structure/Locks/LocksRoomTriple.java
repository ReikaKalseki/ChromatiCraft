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

public class LocksRoomTriple extends LockLevel {

	public LocksRoomTriple(LocksGenerator g) {
		super(g, LockChannel.TRIPLE);
	}

	@Override
	public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {

	}

	@Override
	public int getWidth() {
		return 19;
	}

	@Override
	public int getLength() {
		return 26;
	}

	@Override
	public int getEnterExitDL() {
		return 28;
	}

	@Override
	public int getEnterExitDT() {
		return 0;
	}

	@Override
	public int getDifficultyRating() {
		return 3;
	}

	@Override
	public int getFeatureRating() {
		return 1;
	}

}
