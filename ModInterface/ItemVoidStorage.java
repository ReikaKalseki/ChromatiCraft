/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface;

import java.util.List;

import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Instantiable.BasicInventory;
import Reika.DragonAPI.Interfaces.Item.SpriteRenderCallback;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.ModInteract.Instantiable.AECellFormat;
import appeng.api.AEApi;
import appeng.api.config.FuzzyMode;
import appeng.api.implementations.items.IStorageCell;
import appeng.api.storage.ICellInventory;
import appeng.api.storage.ICellInventoryHandler;
import appeng.api.storage.ICellRegistry;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.StorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;

@Strippable(value = "appeng.api.implementations.items.IStorageCell")
public class ItemVoidStorage extends ItemChromaTool implements SpriteRenderCallback, IStorageCell {

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
	public FuzzyMode getFuzzyMode(ItemStack is) {
		//if (is.stackTagCompound == null)
		//	is.stackTagCompound = new NBTTagCompound();
		//return FuzzyMode.values()[is.getTagCompound().getInteger("fuzzyMode")];
		return FuzzyMode.IGNORE_ALL;
	}

	@Override
	public void setFuzzyMode(ItemStack is, FuzzyMode fzMode) {
		//if (is.stackTagCompound == null)
		//	is.stackTagCompound = new NBTTagCompound();
		//is.getTagCompound().setInteger("fuzzyMode", fzMode.ordinal());
	}

	@Override
	public IInventory getUpgradesInventory(ItemStack is) {
		return new AECellFormat(is, "upgrades", 2);
	}

	@Override
	public IInventory getConfigInventory(ItemStack is) {
		return new AECellFormat(is, "config", 1);
	}

	@Override
	public int getBytes(ItemStack cellItem) { //Total storage
		return (Integer.MAX_VALUE/2+1)/4;//67108864;//Integer.MAX_VALUE/2+1;
	}

	@Override
	public int BytePerType(ItemStack cellItem) {
		return 1;
	}

	//@Override
	public int getBytesPerType(ItemStack is) {
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
		return 10;
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
			if (usedBytes > 0L) {
				li.add(String.format("contains %s items.", cellInv.getStoredItemCount()));

				IItemList<IAEItemStack> items = cellInv.getAvailableItems(StorageChannel.ITEMS.createList());
				li.add(String.format("Stores %s.", items.getFirstItem().getItemStack().getDisplayName()));
			}
		}
		else {
			li.add("Kind of useless without an ME system to add it to.");
		}
	}

	@ModDependent(ModList.APPENG)
	public static ItemStack getStoredItem(ItemStack is) {
		if (ModList.APPENG.isLoaded()) {
			ICellRegistry icr = AEApi.instance().registries().cell();
			IMEInventoryHandler inv = icr.getCellInventory(is, null, StorageChannel.ITEMS);
			ICellInventoryHandler icih = (ICellInventoryHandler)inv;
			ICellInventory cellInv = icih.getCellInv();
			IItemList<IAEItemStack> items = cellInv.getAvailableItems(StorageChannel.ITEMS.createList());
			return items.isEmpty() ? null : items.getFirstItem().getItemStack(); //first since one type per cell
		}
		else {
			return null;
		}
	}

	@Override
	public boolean onRender(RenderItem ri, ItemStack is, ItemRenderType type) {
		if (type == ItemRenderType.INVENTORY) {
			if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
				ItemStack store = ItemVoidStorage.getStoredItem(is);
				if (store != null) {
					double s = 0.063;
					GL11.glScaled(s, -s, s);
					ReikaGuiAPI.instance.drawItemStack(ri, ReikaItemHelper.getSizedItemStack(store, 1), 0, -16);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean doPreGLTransforms(ItemStack is, ItemRenderType type) {
		return true;
	}

}
