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

import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Auxiliary.Interfaces.TieredItem;
import Reika.ChromatiCraft.Base.ItemChromaMulti;
import Reika.ChromatiCraft.Registry.ChromaItems;

public class ItemTieredResource extends ItemChromaMulti implements TieredItem {

	public ItemTieredResource(int tex) {
		super(tex);
	}

	@Override
	public int getNumberTypes() {
		return ChromaItems.TIERED.getNumberMetadatas();
	}

	@Override
	public ProgressStage getDiscoveryTier(ItemStack is) {
		switch(is.getItemDamage()) {
		case 0:
			return ProgressStage.CRYSTALS;
		case 1:
			return ProgressStage.CRYSTALS;
		case 2:
			return ProgressStage.RUNEUSE;
		case 3:
			return ProgressStage.MULTIBLOCK;
		case 4:
			return ProgressStage.PYLON;
		case 5:
			return ProgressStage.PYLON;
		case 6:
			return ProgressStage.CHARGE;
		case 7:
			return ProgressStage.MULTIBLOCK;
		default:
			return null;
		}
	}

	@Override
	public boolean isTiered(ItemStack is) {
		if (is.getItemDamage() <= 7)
			return true;
		switch(is.getItemDamage()) {

		default:
			return false;
		}
	}

}
