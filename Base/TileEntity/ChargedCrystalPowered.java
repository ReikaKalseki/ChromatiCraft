/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base.TileEntity;

import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Items.ItemStorageCrystal;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.CrystalElement;

public abstract class ChargedCrystalPowered extends InventoriedChromaticBase {

	protected final int getStoredEnergy(CrystalElement e) {
		if (e == null)
			return 0;
		if (ChromaItems.STORAGE.matchWith(inv[0])) {
			ItemStack is = inv[0];
			return ((ItemStorageCrystal)is.getItem()).getStoredEnergy(is, e);
		}
		return 0;
	}

	protected final int getStoredEnergy() {
		if (ChromaItems.STORAGE.matchWith(inv[0])) {
			ItemStack is = inv[0];
			return ((ItemStorageCrystal)is.getItem()).getTotalEnergy(is);
		}
		return 0;
	}

	protected final void useEnergy(ElementTagCompound tag) {
		ItemStorageCrystal c = ((ItemStorageCrystal)inv[0].getItem());
		for (CrystalElement e : tag.elementSet()) {
			c.removeEnergy(inv[0], e, tag.getValue(e));
		}
	}

	public abstract boolean canExtractItem(int slot, ItemStack is, int side);
	public abstract boolean isItemValidForSlot(int slot, ItemStack is);

}
