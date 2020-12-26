/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Registry;

import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

public enum ChromaGuis {

	TILE(),
	ITEMWITHFILTER(),
	ABILITY(),
	BOOKNAV(),
	BOOKPAGES(),
	INFO(),
	STRUCTURE(),
	CRAFTING(),
	ALLOYING(),
	RECIPE(),
	RITUAL(),
	ABILITYDESC(),
	MACHINEDESC(),
	TOOLDESC(),
	BASICDESC(),
	PROGRESS(),
	PROGRESSBYTIER(),
	REFRAGMENT(),
	NOTES(),
	BOOKEMPTIES(),
	AURAPOUCH(),
	TRANSITION(),
	TELEPORT(),
	REMOTETERMINAL(),
	BULKMOVER(),
	HOVER(),
	LOREKEY(),
	BURNERINV(),
	//LORE();
	STRUCTUREPASS(),
	ENDERBUCKET(),
	;

	public static final ChromaGuis[] guiList = values();

	public boolean isLexiconGUI() {
		return ReikaMathLibrary.isValueInsideBoundsIncl(BOOKNAV.ordinal(), BOOKEMPTIES.ordinal(), this.ordinal());
	}

}
