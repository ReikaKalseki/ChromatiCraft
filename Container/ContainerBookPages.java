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
import net.minecraft.util.MathHelper;
import Reika.ChromatiCraft.Items.Tools.ItemChromaBook;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;

public class ContainerBookPages extends Container {

	private int scroll = 0;

	public static final int width = 9;
	public static final int height = 3;

	public static final int MAX_SCROLL = 1+ChromaResearch.researchList.length/width-height;

	public final BookInventory inventory = new BookInventory();

	private final EntityPlayer player;

	public ContainerBookPages(EntityPlayer player, int scroll) {
		int var6;
		int var7;
		this.player = player;

		scroll = Math.min(scroll, MAX_SCROLL);

		this.populate();

		ItemStack tool = player.getCurrentEquippedItem();
		ItemChromaBook iil = (ItemChromaBook)tool.getItem();
		ArrayList<ItemStack> li = iil.getItemList(tool);
		for (ItemStack is : li) {
			int idx = ChromaResearch.getAllNonParents().indexOf(ChromaResearch.researchList[is.getItemDamage()]);
			inventory.setInventorySlotContents(idx, is);
		}

		this.onCraftMatrixChanged(inventory);
	}

	@Override
	public void onCraftMatrixChanged(IInventory ii) {
		super.onCraftMatrixChanged(ii);

		ArrayList<ItemStack> li = new ArrayList();
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack in = inventory.getStackInSlot(i);
			if (in != null) {
				li.add(in);
			}
		}

		ItemStack is = player.getCurrentEquippedItem();
		ItemChromaBook iil = (ItemChromaBook)is.getItem();
		iil.setItems(is, li);
	}

	public int getSize() {
		return inventory.getSizeInventory();
	}

	public void populate() {
		inventorySlots.clear();
		inventoryItemStacks.clear();
		int offset = scroll*9;
		int lim = ChromaResearch.getAllNonParents().size();
		int max = Math.min(lim, offset+27);
		//ReikaJavaLibrary.pConsole(0+">"+offset+"|"+offset+">"+max+"|"+max+">"+lim);

		for (int i = 0; i < offset; i++) {
			Slot s = new SlotBook(inventory, i, -200, -200); //offscreen
			this.addSlotToContainer(s);
		}

		for (int i = offset; i < max; i++) {
			int d = i-offset;
			int dx = 8+(d%9)*18;
			int dy = 17+(d/9)*18;
			Slot s = new SlotBook(inventory, i, dx, dy);
			this.addSlotToContainer(s);
		}

		for (int i = max; i < lim; i++) {
			Slot s = new SlotBook(inventory, i, -200, -200); //offscreen
			this.addSlotToContainer(s);
		}
		for (int i = 0; i < 3; ++i)
			for (int k = 0; k < 9; ++k)
				this.addSlotToContainer(new Slot(player.inventory, k + i * 9 + 9, 8 + k * 18, 84 + i * 18));
		for (int i = 0; i < 9; ++i)
			this.addSlotToContainer(new Slot(player.inventory, i, 8 + i * 18, 142));
	}

	@Override
	public ItemStack slotClick(int slot, int par2, int par3, EntityPlayer ep) {
		return super.slotClick(slot, par2, par3, ep);
	}

	public void scroll(boolean up) {
		scroll += up ? 1 : -1;
		scroll = MathHelper.clamp_int(scroll, 0, ContainerBookPages.MAX_SCROLL);
		//mouseX = Mouse.getX();
		//mouseY = Mouse.getY();
		//player.closeScreen();
		//player.openGui(ChromatiCraft.instance, ChromaGuis.BOOKPAGES.ordinal(), null, scroll, 0, 0);
		this.populate();
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

	public int getPageCount() {
		int c = 0;
		for (int i = 0; i < inventory.slots.length; i++) {
			if (inventory.slots[i] != null)
				c++;
		}
		return c;
	}

	private static class BookInventory implements IInventory {

		private final ItemStack[] slots = new ItemStack[ChromaResearch.getAllNonParents().size()];

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

	public static final class SlotBook extends Slot {

		public SlotBook(IInventory ii, int id, int x, int y) {
			super(ii, id, x, y);
		}

		@Override
		public boolean isItemValid(ItemStack is)
		{
			return ChromaItems.FRAGMENT.matchWith(is);
		}

	}

}
