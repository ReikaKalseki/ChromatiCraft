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

import Reika.ChromatiCraft.Base.ItemChromaMulti;
import Reika.ChromatiCraft.Registry.ChromaItems;

public class ItemTieredResource extends ItemChromaMulti {

	public ItemTieredResource(int tex) {
		super(tex);
	}

	@Override
	public int getNumberTypes() {
		return ChromaItems.TIERED.getNumberMetadatas();
	}

}
