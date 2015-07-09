/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items;

import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Auxiliary.Interfaces.TieredItem;
import Reika.ChromatiCraft.Base.ItemChromaMulti;

public class ItemDimGen extends ItemChromaMulti implements TieredItem {

	public ItemDimGen(int tex) {
		super(tex);
	}

	@Override
	public ProgressStage getDiscoveryTier(ItemStack is) {
		return ProgressStage.DIMENSION;
	}

	@Override
	public boolean isTiered(ItemStack is) {
		return true;
	}

}
