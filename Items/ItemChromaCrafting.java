/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items;

import Reika.ChromatiCraft.ChromaNames;
import Reika.ChromatiCraft.Base.ItemChromaMulti;

public class ItemChromaCrafting extends ItemChromaMulti {

	public ItemChromaCrafting(int tex) {
		super(tex);
	}

	@Override
	public int getNumberTypes() {
		return ChromaNames.craftingNames.length;
	}

}
