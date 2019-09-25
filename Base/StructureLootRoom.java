/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base;


import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;

public abstract class StructureLootRoom<V extends DimensionStructureGenerator> extends StructurePiece<V> {

	protected StructureLootRoom(V s) {
		super(s);
	}

	@Override
	public abstract void generate(ChunkSplicedGenerationCache world, int x, int y, int z);

	protected final void placeCore(int x, int y, int z) {
		parent.placeCore(x, y, z);
	}

}
