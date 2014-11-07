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
		"fluid.chroma", "fluid.endere", "fluid.activechroma"
	};

	public static final String[] clusterNames = {
		"crystal.redgroup", "crystal.greengroup", "crystal.orangegroup", "crystal.whitegroup",
		"crystal.bunch0", "crystal.bunch1", "crystal.bunch2", "crystal.bunch3",
		"crystal.cluster0", "crystal.cluster1", "crystal.core", "crystal.star", "crystal.multi"
	};

	public static final String[] craftingNames = {
		"chromacraft.void", "chromacraft.lens", "chromacraft.focus", "chromacraft.mirror", "chromacraft.rawcrystal",
		"chromacraft.energycore", "chromacraft.crystaldust", "chromacraft.transformcore", "chromacraft.elementunit"
	};

	public static final String[] tieredNames = {
		"chromacraft.chromadust", "chromacraft.auradust", "chromacraft.puredust", "chromacraft.focusdust", "chromacraft.elementdust",
		"chromacraft.beacondust", "chromacraft.bindingcrystal", "chromacraft.resodust"
	};

	public static final String[] storageNames = {
		"", "", "", "", "", "", ""
	};

	public static String getFluidName(int i) {
		return fluidNames[i];
	}

	private static String getName(String[] names, int i) {
		return StatCollector.translateToLocal(names[i]);
	}
}
