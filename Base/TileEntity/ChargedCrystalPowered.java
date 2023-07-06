/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base.TileEntity;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.TileEntity.TileEntityAdjacencyUpgrade.AdjacencyCheckHandlerImpl;
import Reika.ChromatiCraft.Items.ItemStorageCrystal;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.Interfaces.LumenConsumer;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.AOE.Effect.TileEntityEfficiencyUpgrade;

public abstract class ChargedCrystalPowered extends InventoriedChromaticBase implements LumenConsumer {

	private static final AdjacencyCheckHandlerImpl adjacency = TileEntityAdjacencyUpgrade.getOrCreateAdjacencyCheckHandler(CrystalElement.BLACK, null);

	private int efficiencyBoost;

	public void onAdjacentUpdate(World world, int x, int y, int z, Block b) {
		this.calcEfficiency();
		this.syncAllData(false);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		efficiencyBoost = NBT.getInteger("eff");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setInteger("eff", efficiencyBoost);
	}

	public final int getEfficiencyBoost() {
		return efficiencyBoost;
	}

	protected final float getEnergyCostScale() {
		float f = 1;
		int e = this.getEfficiencyBoost();
		if (e > 0)
			f *= TileEntityEfficiencyUpgrade.getCostFactor(e-1);
		f *= this.getCostModifier();
		return f;
	}

	public abstract float getCostModifier();

	private void calcEfficiency() {
		efficiencyBoost = adjacency.getAdjacentUpgrade(this);
	}

	public final int getEnergy(CrystalElement e) {
		if (e == null)
			return 0;
		if (ChromaItems.STORAGE.matchWith(inv[0])) {
			ItemStack is = inv[0];
			return ((ItemStorageCrystal)is.getItem()).getStoredEnergy(is, e);
		}
		return 0;
	}

	protected final int getStoredEnergy(boolean onlyUsedColors) {
		if (ChromaItems.STORAGE.matchWith(inv[0])) {
			ItemStack is = inv[0];
			ElementTagCompound tag = ((ItemStorageCrystal)is.getItem()).getStoredTags(is);
			if (onlyUsedColors) {
				for (int i = 0; i < 16; i++) {
					CrystalElement e = CrystalElement.elements[i];
					if (!this.usesColor(e))
						tag.removeTag(e);
				}
			}
			return tag.getTotalEnergy();
		}
		return 0;
	}

	protected final void useEnergy(CrystalElement e, int amt) {
		if (this.allowsEfficiencyBoost())
			amt = (int)Math.max(1, amt*this.getEnergyCostScale());
		ItemStorageCrystal c = ((ItemStorageCrystal)inv[0].getItem());
		c.removeEnergy(inv[0], e, amt);
	}

	protected final void useEnergy(ElementTagCompound tag) {
		for (CrystalElement e : tag.elementSet()) {
			this.useEnergy(e, tag.getValue(e));
		}
	}

	public final int getMaxStorage(CrystalElement e) {
		if (ChromaItems.STORAGE.matchWith(inv[0])) {
			ItemStack is = inv[0];
			return ((ItemStorageCrystal)is.getItem()).getCapacity(is);
		}
		return 0;
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

	public final boolean canExtractItem(int slot, ItemStack is, int side) {
		return slot == 0 ? this.getStoredEnergy(true) == 0 : this.canExtractOtherItem(slot, is, side);
	}

	public final boolean isItemValidForSlot(int slot, ItemStack is) {
		return slot == 0 ? ChromaItems.STORAGE.matchWith(is) : this.isItemValidForOtherSlot(slot, is);
	}

	protected abstract boolean canExtractOtherItem(int slot, ItemStack is, int side);

	protected abstract boolean isItemValidForOtherSlot(int slot, ItemStack is) ;

	public abstract ElementTagCompound getRequiredEnergy();

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		super.onFirstTick(world, x, y, z);
		this.calcEfficiency();
	}

	@Override
	public boolean allowsEfficiencyBoost() {
		return true;
	}

	@Override
	public void getTagsToWriteToStack(NBTTagCompound NBT) {

	}

	@Override
	public void setDataFromItemStackTag(ItemStack is) {

	}

	@Override
	public void addTooltipInfo(List li, boolean shift) {

	}

}
