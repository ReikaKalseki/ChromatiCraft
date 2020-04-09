/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface.AE;

import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;

import Reika.ChromatiCraft.API.Interfaces.CrystalTypesProxy;
import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.ChromatiCraft.Base.ItemCrystalBasic;
import Reika.ChromatiCraft.Items.ItemBlock.ItemBlockCrystal;
import Reika.ChromatiCraft.Items.ItemBlock.ItemBlockCrystalColors;
import Reika.ChromatiCraft.Items.ItemBlock.ItemBlockDyeTypes;
import Reika.ChromatiCraft.ModInterface.ItemColoredModInteract;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Instantiable.BasicInventory;
import Reika.DragonAPI.Instantiable.DummyInventory;
import Reika.DragonAPI.Interfaces.Item.SpriteRenderCallback;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

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
public class ItemCrystalCell extends ItemChromaTool implements SpriteRenderCallback, IStorageCell {

	private static class CellInventory extends BasicInventory {

		public CellInventory() {
			super(ChromaItems.CRYSTALCELL.getBasicName(), 63, 1);
		}

		@Override
		public boolean isItemValidForSlot(int i, ItemStack is) {
			return isTypeStorable(is);
		}
	}

	public ItemCrystalCell(int index) {
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
		return 262144*4; //increased 4x
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
		return 16;
	}

	@Override
	@ModDependent(ModList.APPENG)
	public boolean isBlackListed(ItemStack cellItem, IAEItemStack requestedAddition) {
		return !this.isItemValid(cellItem, requestedAddition.getItemStack());
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
		return 2;
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
				ItemStack in = items.getFirstItem().getItemStack();
				String n = in.getDisplayName();
				ChromaItems c = ChromaItems.getEntry(in);
				if (c != null) {
					n = c.getBasicName();
				}
				ChromaBlocks b = ChromaBlocks.getEntryByItem(in);
				if (b != null) {
					n = b.getBasicName();
				}
				if (in != null && in.getItem() == Items.dye) {
					n = "Dye";
				}
				li.add(String.format("Stores %s.", n));
			}
		}
		else {
			li.add("Kind of useless without an ME system to add it to.");
		}
	}

	@ModDependent(ModList.APPENG)
	public static ItemStack getStoredItemType(ItemStack is) {
		if (ModList.APPENG.isLoaded()) {
			ICellRegistry icr = AEApi.instance().registries().cell();
			IMEInventoryHandler inv = icr.getCellInventory(is, null, StorageChannel.ITEMS);
			ICellInventoryHandler icih = (ICellInventoryHandler)inv;
			ICellInventory cellInv = icih.getCellInv();
			IItemList<IAEItemStack> items = cellInv.getAvailableItems(StorageChannel.ITEMS.createList());
			ItemStack in = items.isEmpty() ? null : items.getFirstItem().getItemStack();
			return in == null ? null : new ItemStack(in.getItem(), 1, CrystalElement.WHITE.ordinal()+16*(in.getItemDamage()/16));
		}
		else {
			return null;
		}
	}

	public static boolean isItemValid(ItemStack cell, ItemStack is) {
		if (ModList.APPENG.isLoaded()) {
			ItemStack in = getStoredItemType(cell);
			if (ChromaItems.PLACER.matchWith(is) && is.getItemDamage() == ChromaTiles.DIMENSIONCORE.ordinal())
				return ReikaItemHelper.matchStacks(is, in);
			return in != null ? (in.getItem() == is.getItem() && in.getItemDamage()/16 == is.getItemDamage()/16) : isTypeStorable(is);
		}
		else {
			return false;
		}
	}

	public static boolean isTypeStorable(ItemStack is) {
		if (is.getItem() == Items.dye)
			return true;
		if (is.getItem() instanceof ItemCrystalBasic || is.getItem() instanceof ItemBlockCrystalColors || is.getItem() instanceof ItemBlockCrystal)
			return true;
		if (is.getItem() instanceof CrystalTypesProxy && ((CrystalTypesProxy)is.getItem()).isCrystalType(is))
			return true;
		if (ChromaItems.PLACER.matchWith(is) && is.getItemDamage() == ChromaTiles.DIMENSIONCORE.ordinal())
			return true;
		return is.getItem() instanceof ItemBlockDyeTypes || is.getItem() instanceof ItemColoredModInteract;
	}

	@Override
	public boolean onRender(RenderItem ri, ItemStack is, ItemRenderType type) {
		if (type == ItemRenderType.INVENTORY) {
			if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
				ItemStack store = ItemCrystalCell.getStoredItemType(is);
				if (store != null) {
					double s = 0.063;
					GL11.glScaled(s, -s, s);
					store = store.copy();
					store.setItemDamage(((store.getItemDamage()/16)*16)+(int)((System.currentTimeMillis()/250)%16));
					//float hue = (float)(((System.currentTimeMillis()/10D)%360)/360F);
					//int rgb = Color.HSBtoRGB(hue, 1, 1);
					//GL11.glColor4f(ReikaColorAPI.getRed(rgb)/255F, ReikaColorAPI.getGreen(rgb)/255F, ReikaColorAPI.getBlue(rgb)/255F, 1);
					ReikaGuiAPI.instance.drawItemStack(ri, ReikaItemHelper.getSizedItemStack(store, 1), 0, -16);
					GL11.glColor4f(1, 1, 1, 1);
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
