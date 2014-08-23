package Reika.ChromatiCraft.Base.TileEntity;

import Reika.ChromatiCraft.Items.ItemStorageCrystal;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.CrystalElement;

import net.minecraft.item.ItemStack;

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

	public abstract boolean canExtractItem(int slot, ItemStack is, int side);
	public abstract boolean isItemValidForSlot(int slot, ItemStack is);

}
