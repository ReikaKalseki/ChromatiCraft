/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base.TileEntity;

import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Items.ItemStorageCrystal;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.Interfaces.LumenTile;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.CrystalElement;

public abstract class ChargedCrystalPowered extends InventoriedChromaticBase implements LumenTile {

	protected final float getEnergyCostScale() {
		float f = 1;
		if (this.hasEfficiency())
			f *= 0.25F;
		f *= this.getCostModifier();
		return f;
	}

	public abstract boolean hasEfficiency();

	public abstract float getCostModifier();

	public final int getEnergy(CrystalElement e) {
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

	protected final void useEnergy(CrystalElement e, int amt) {
		ItemStorageCrystal c = ((ItemStorageCrystal)inv[0].getItem());
		c.removeEnergy(inv[0], e, amt);
	}

	public final int getMaxStorage(CrystalElement e) {
		if (ChromaItems.STORAGE.matchWith(inv[0])) {
			ItemStack is = inv[0];
			return ((ItemStorageCrystal)is.getItem()).getCapacity(is);
		}
		return 0;
	}

	protected final void useEnergy(ElementTagCompound tag) {
		for (CrystalElement e : tag.elementSet()) {
			this.useEnergy(e, tag.getValue(e));
		}
	}

	protected final boolean hasEnergy(ElementTagCompound tag) {
		for (CrystalElement e : tag.elementSet()) {
			if (!this.hasEnergy(e, tag.getValue(e)))
				return false;
		}
		return true;
	}

	protected final boolean hasEnergy(CrystalElement e, int amt) {
		return this.getEnergy(e) >= amt;
	}

	public final ElementTagCompound getEnergy() {
		ElementTagCompound tag = new ElementTagCompound();
		for (int i = 0; i < CrystalElement.elements.length; i++) {
			CrystalElement e = CrystalElement.elements[i];
			if (this.usesColor(e))
				tag.setTag(e, this.getEnergy(e));
		}
		return tag;
	}

	public abstract boolean usesColor(CrystalElement e);

	public abstract boolean canExtractItem(int slot, ItemStack is, int side);
	public abstract boolean isItemValidForSlot(int slot, ItemStack is);

}
