/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Instantiable.BasicInventory;
import Reika.DragonAPI.Instantiable.DummyInventory;
import appeng.api.AEApi;
import appeng.api.config.FuzzyMode;
import appeng.api.implementations.items.IStorageCell;
import appeng.api.storage.ICellInventory;
import appeng.api.storage.ICellInventoryHandler;
import appeng.api.storage.ICellRegistry;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.StorageChannel;
import appeng.api.storage.data.IAEItemStack;

@Strippable(value = "appeng.api.implementations.items.IStorageCell")
public class ItemVoidStorage extends ItemChromaTool implements IStorageCell {

	private static class CellInventory extends BasicInventory {

		public CellInventory() {
			super(ChromaItems.VOIDCELL.getBasicName(), 63, 1);
		}

		@Override
		public boolean isItemValidForSlot(int i, ItemStack is) {
			return true;
		}
	}

	public ItemVoidStorage(int index) {
		super(index);
	}

	@Override
	public boolean isEditable(ItemStack is) {
		return true;
	}

	@Override
	public IInventory getUpgradesInventory(ItemStack is) {
		return new DummyInventory();
	}

	@Override
	public IInventory getConfigInventory(ItemStack is) {
		return new CellInventory();
	}

	@Override
	public FuzzyMode getFuzzyMode(ItemStack is) {
		return FuzzyMode.IGNORE_ALL;
	}

	@Override
	@ModDependent(ModList.APPENG)
	public void setFuzzyMode(ItemStack is, FuzzyMode fzMode) {

	}

	@Override
	public int getBytes(ItemStack cellItem) { //Total storage
		return (Integer.MAX_VALUE/2+1)/4;//67108864;//Integer.MAX_VALUE/2+1;
	}

	@Override
	public int BytePerType(ItemStack cellItem) {
		return 1;
	}

	@Override
	public int getTotalTypes(ItemStack cellItem) {
		return 1;
	}

	@Override
	@ModDependent(ModList.APPENG)
	public boolean isBlackListed(ItemStack cellItem, IAEItemStack requestedAddition) {
		return false;
	}

	@Override
	public boolean storableInStorageCell() {
		return false;
	}

	@Override
	public boolean isStorageCell(ItemStack is) {
		return true;
	}

	@Override
	public double getIdleDrain() {
		return 1;
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean vb) {
		if (ModList.APPENG.isLoaded()) {
			ICellRegistry icr = AEApi.instance().registries().cell();
			IMEInventoryHandler inv = icr.getCellInventory(is, null, StorageChannel.ITEMS);
			ICellInventoryHandler icih = (ICellInventoryHandler)inv;
			ICellInventory cellInv = icih.getCellInv();
			long usedBytes = cellInv.getUsedBytes();

			li.add(String.format("%s of %s item bytes used.", usedBytes, cellInv.getTotalBytes()));
			li.add(String.format("%s of %s item types used.", cellInv.getStoredItemTypes(), cellInv.getTotalItemTypes()));
			if (usedBytes > 0L)
				li.add(String.format("contains %s items.", cellInv.getStoredItemCount()));
		}
		else {
			li.add("Kind of useless without an ME system to add it to.");
		}
	}

}
