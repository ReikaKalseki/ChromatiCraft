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

import java.util.Locale;

import net.minecraft.world.gen.structure.MapGenStructure;

public abstract class ChromaStructureBase extends MapGenStructure {

	public ChromaStructureBase() {
		super();
	}

	@Override
	public final String func_143025_a() {
		return this.getClass().getSimpleName().toLowerCase(Locale.ENGLISH);
	}

}
