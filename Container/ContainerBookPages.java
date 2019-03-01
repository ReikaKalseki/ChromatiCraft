/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
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

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Items.ItemInfoFragment;
import Reika.ChromatiCraft.Items.Tools.ItemChromaBook;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.DragonAPI.Instantiable.BasicInventory;

public class ContainerBookPages extends Container {

	private int scroll = 0;

	public static final int width = 9;
	public static final int height = 3;

	public static final int MAX_SCROLL = 1+ChromaResearch.getAllObtainableFragments().size()/width-height;

	private final BookInventory inventory = new BookInventory();

	private final EntityPlayer player;

	public ContainerBookPages(EntityPlayer player, int scroll) {
		int var6;
		int var7;
		this.player = player;

		scroll = Math.min(scroll, MAX_SCROLL);

		this.populate();

		ItemStack tool = player.getCurrentEquippedItem();
		ItemChromaBook iil = (ItemChromaBook)tool.getItem();
		ArrayList<ChromaResearch> li = iil.getItemList(tool);
		for (ChromaResearch r : li) {
			int idx = ChromaResearch.getAllObtainableFragments().indexOf(r);
			inventory.setInventorySlotContents(idx, ItemInfoFragment.getItem(r));
		}

		this.onCraftMatrixChanged(inventory);
	}

	@Override
	public void onCraftMatrixChanged(IInventory ii) {
		super.onCraftMatrixChanged(ii);

		ArrayList<ChromaResearch> li = new ArrayList();
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack in = inventory.getStackInSlot(i);
			if (in != null) {
				li.add(ItemInfoFragment.getResearch(in));
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
		int lim = ChromaResearch.getAllObtainableFragments().size();
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

		for (int i = 0; i < 9; ++i)
			this.addSlotToContainer(new Slot(player.inventory, i, 8 + i * 18, 142));

		for (int i = 0; i < 3; ++i)
			for (int k = 0; k < 9; ++k)
				this.addSlotToContainer(new Slot(player.inventory, k + i * 9 + 9, 8 + k * 18, 84 + i * 18));

	}

	@Override
	public ItemStack slotClick(int slot, int par2, int par3, EntityPlayer ep) {
		int diff = slot-ChromaResearch.getAllObtainableFragments().size();
		if (diff >= 0 && diff < 36) {
			if (ChromaItems.HELP.matchWith(ep.inventory.getStackInSlot(diff))) {
				return ep.inventory.getItemStack();
			}
		}
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

		ArrayList<ChromaResearch> li = new ArrayList();
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack in = inventory.getStackInSlot(i);
			if (in != null) {
				li.add(ItemInfoFragment.getResearch(in));
			}
		}

		ItemStack is = ep.getCurrentEquippedItem();
		if (is == null || !(is.getItem() instanceof ItemChromaBook)) {
			ChromatiCraft.logger.logError("Tried to save lexicon pages without a lexicon!");
			return;
		}
		ItemChromaBook iil = (ItemChromaBook)is.getItem();
		iil.setItems(is, li);
	}

	@Override
	public boolean canInteractWith(EntityPlayer ep) {
		return ChromaItems.HELP.matchWith(ep.getCurrentEquippedItem());
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer ep, int slot) {
		int inv = slot-inventory.inventorySize;
		if (inv >= 0) {
			ItemStack at = ep.inventory.mainInventory[inv];
			if (ChromaItems.FRAGMENT.matchWith(at)) {
				ChromaResearch r = ItemInfoFragment.getResearch(at);
				if (r != null) {
					int idx = ChromaResearch.getAllObtainableFragments().indexOf(r);
					if (idx >= 0) {
						ItemStack in = inventory.getStackInSlot(idx);
						if (in == null) {
							//ReikaJavaLibrary.pConsole(r+" > "+in+" @ "+idx);
							inventory.setInventorySlotContents(idx, at);
							ep.inventory.mainInventory[inv] = null;
						}
					}
				}
			}
		}
		return null;
	}

	public int getPageCount() {
		int c = 0;
		for (int i = 0; i < inventory.inventorySize; i++) {
			if (inventory.getStackInSlot(i) != null)
				c++;
		}
		return c;
	}

	private static class BookInventory extends BasicInventory {

		private BookInventory() {
			super("Chroma Lexicon", ChromaResearch.getAllObtainableFragments().size());
		}

		@Override
		public boolean isUseableByPlayer(EntityPlayer ep) {
			return true;
		}

		@Override
		public boolean isItemValidForSlot(int slot, ItemStack is) {
			if (ChromaItems.FRAGMENT.matchWith(is)) {
				ChromaResearch r = ItemInfoFragment.getResearch(is);
				return r != null && !this.containsFragment(r);
			}
			return false;
		}

		private boolean containsFragment(ChromaResearch r) {
			for (int i = 0; i < inv.length; i++) {
				ItemStack is = inv[i];
				if (ChromaItems.FRAGMENT.matchWith(is) && ItemInfoFragment.getResearch(is) == r)
					return true;
			}
			return false;
		}
	}

	public static final class SlotBook extends Slot {

		public SlotBook(IInventory ii, int id, int x, int y) {
			super(ii, id, x, y);
		}

		@Override
		public boolean isItemValid(ItemStack is)
		{
			return inventory.isItemValidForSlot(this.getSlotIndex(), is);
		}

	}

}
