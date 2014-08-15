/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft;

import net.minecraft.util.StatCollector;

public class ChromaNames {

	private static final String[] blockNames = {

	};

	private static final String[] fluidNames = {
		"fluid.chroma", "fluid.ender"
	};

	public static final String[] clusterNames = {
		"crystal.redgroup", "crystal.greengroup", "crystal.orangegroup", "crystal.whitegroup",
		"crystal.bunch0", "crystal.bunch1", "crystal.bunch2", "crystal.bunch3",
		"crystal.cluster0", "crystal.cluster1", "crystal.core", "crystal.star", "crystal.multi"
	};

	public static void addNames() {

		for (int i = 0; i < blockNames.length; i++) {
			//ItemStack blockStack = new ItemStack(RotaryCraft.decoblock, 1, i);
			//LanguageRegistry.addName(blockStack, getName(blockNames[i], false));
		}
	}

	public static String getFluidName(int i) {
		return fluidNames[i];
	}

	private static String getName(String[] names, int i) {
		return StatCollector.translateToLocal(names[i]);
	}
}