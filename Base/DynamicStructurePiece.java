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


import net.minecraft.world.World;

public abstract class DynamicStructurePiece<V extends DimensionStructureGenerator> extends StructureElement<V> {

	protected DynamicStructurePiece(V s) {
		super(s);
	}

	public abstract void generate(World world, int x, int z);

}
