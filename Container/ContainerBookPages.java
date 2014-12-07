/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Container;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Items.Tools.ItemChromaBook;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import cpw.mods.fml.relauncher.Side;

public class ContainerBookPages extends Container {

	private int rowOffset = 0;

	public static final int width = 9;
	public static final int height = 3;

	public static final int MAX_SCROLL = 1+ChromaResearch.researchList.length/width-height;

	public BookInventory inventory = new BookInventory();

	public ContainerBookPages(EntityPlayer player, int scroll) {
		int var6;
		int var7;

		rowOffset = Math.min(scroll, MAX_SCROLL);

		this.populate();

		for (var6 = 0; var6 < 3; ++var6)
			for (var7 = 0; var7 < 9; ++var7)
				this.addSlotToContainer(new Slot(player.inventory, var7 + var6 * 9 + 9, 8 + var7 * 18, 84 + var6 * 18));
		for (var6 = 0; var6 < 9; ++var6)
			this.addSlotToContainer(new Slot(player.inventory, var6, 8 + var6 * 18, 142));

		ItemStack tool = player.getCurrentEquippedItem();
		ItemChromaBook iil = (ItemChromaBook)tool.getItem();
		ArrayList<ItemStack> li = iil.getItemList(tool);
		for (int i = 0; i < li.size(); i++) {
			inventory.setInventorySlotContents(i, li.get(i));
		}

		this.onCraftMatrixChanged(inventory);
	}

	private void populate() {
		int offset = rowOffset*width;
		for (int i = 0; i < height; i++) {
			for (int k = 0; k < width; k++) {
				int id = i*width+k+offset;
				ReikaJavaLibrary.pConsole("Added ID "+id, Side.SERVER);
				this.addSlotToContainer(new Slot(inventory, id, 8+k*18, 17+i*18));
			}
		}
	}

	@Override
	public ItemStack slotClick(int slot, int par2, int par3, EntityPlayer ep) {
		return super.slotClick(slot, par2, par3, ep);
	}

	public void scroll(boolean up) {
		if (up && rowOffset >= 9) {
			return;
		}
		if (!up && rowOffset <= 0) {
			return;
		}
	}

	@Override
	public void onContainerClosed(EntityPlayer ep) {
		super.onContainerClosed(ep);

		ArrayList<ItemStack> li = new ArrayList();
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack in = inventory.getStackInSlot(i);
			if (in != null) {
				li.add(in);
			}
		}

		ItemStack is = ep.getCurrentEquippedItem();
		ItemChromaBook iil = (ItemChromaBook)is.getItem();
		iil.setItems(is, li);
	}

	@Override
	public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
		return true;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
	{
		return null;
	}

	private static class BookInventory implements IInventory {

		private final ItemStack[] slots = new ItemStack[ChromaResearch.researchList.length];

		@Override
		public int getSizeInventory() {
			return slots.length;
		}

		@Override
		public ItemStack getStackInSlot(int slot) {
			return slots[slot];
		}

		@Override
		public ItemStack decrStackSize(int slot, int amt) {
			return ReikaInventoryHelper.decrStackSize(this, slot, amt);
		}

		@Override
		public ItemStack getStackInSlotOnClosing(int slot) {
			return ReikaInventoryHelper.getStackInSlotOnClosing(this, slot);
		}

		@Override
		public void setInventorySlotContents(int slot, ItemStack is) {
			slots[slot] = is;
		}

		@Override
		public String getInventoryName() {
			return "Book";
		}

		@Override
		public boolean hasCustomInventoryName() {
			return false;
		}

		@Override
		public int getInventoryStackLimit() {
			return 1;
		}

		@Override
		public void markDirty() {

		}

		@Override
		public boolean isUseableByPlayer(EntityPlayer ep) {
			return true;
		}

		@Override
		public void openInventory() {

		}

		@Override
		public void closeInventory() {

		}

		@Override
		public boolean isItemValidForSlot(int slot, ItemStack is) {
			return ChromaItems.FRAGMENT.matchWith(is);
		}
	}

}
