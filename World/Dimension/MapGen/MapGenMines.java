/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.MapGen;

import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.StructureStart;

@Deprecated
public class MapGenMines extends MapGenStructure {

	@Override
	protected boolean canSpawnStructureAtCoords(int x, int z) {
		return false;
	}

	@Override
	protected StructureStart getStructureStart(int x, int z) {
		return null;
	}

	@Override
	public String func_143025_a() {
		return null;
	}

}
