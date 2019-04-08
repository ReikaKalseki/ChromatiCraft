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


public abstract class StructureElement<V extends DimensionStructureGenerator> {

	protected final V parent;

	protected StructureElement(V s) {
		parent = s;
	}

	protected final void placeCore(int x, int y, int z) {
		parent.placeCore(x, y, z);
	}

	public static abstract class BasicStructurePiece<V extends DimensionStructureGenerator> extends StructureElement<V> {

		protected BasicStructurePiece(V s) {
			super(s);
		}

		public abstract void generate(World world, int x, int y, int z);

	}

}
