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

import net.minecraft.block.Block;
import net.minecraft.world.World;


public abstract class StructureElement {

	protected static final Block STRUCTURE_AIR = DimensionStructureGenerator.STRUCTURE_AIR;

	protected final DimensionStructureGenerator parent;

	protected StructureElement(DimensionStructureGenerator s) {
		parent = s;
	}

	protected final void placeCore(int x, int y, int z) {
		parent.placeCore(x, y, z);
	}

	public static abstract class BasicStructurePiece extends StructureElement {

		protected BasicStructurePiece(DimensionStructureGenerator s) {
			super(s);
		}

		public abstract void generate(World world, int x, int y, int z);

	}

}
