/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base;


import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;

public abstract class StructurePiece extends StructureElement {

	protected StructurePiece(DimensionStructureGenerator s) {
		super(s);
	}

	public abstract void generate(ChunkSplicedGenerationCache world, int x, int y, int z);

}
