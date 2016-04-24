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
import Reika.ChromatiCraft.Auxiliary.Interfaces.TieredItem;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Base.ItemCrystalBasic;

public class ItemElementalStone extends ItemCrystalBasic implements TieredItem {

	public ItemElementalStone(int tex) {
		super(tex);
	}

	@Override
	public ProgressStage getDiscoveryTier(ItemStack is) {
		return ProgressStage.RUNEUSE;
	}

	@Override
	public boolean isTiered(ItemStack is) {
		return true;
	}

}
