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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.DragonAPI.Instantiable.BasicInventory;

import appeng.api.networking.IGrid;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.data.IAEItemStack;

public class ContainerRemoteTerminal extends Container {

	private final ItemStack held;
	private final EntityPlayer player;
	private final IGridHost grid;
	private final List<IAEItemStack> items = new ArrayList();
	private final ForgeDirection direction;
	private AEInventory inventory;

	public ContainerRemoteTerminal(EntityPlayer ep) {
		player = ep;
		held = player.getCurrentEquippedItem();
		ItemRemoteTerminal irt = (ItemRemoteTerminal)held.getItem();
		grid = irt.getLink(held, player.worldObj);
		direction = ForgeDirection.VALID_DIRECTIONS[held.stackTagCompound.getInteger("dir")];

		this.populate();
	}

	public void populate() {
		items.clear();

		IMEMonitor<IAEItemStack> ime = this.getAEInventory();
		if (ime != null) {
			for (IAEItemStack iae : ime.getStorageList()) {
				items.add(iae);
			}
		}

		this.sort(TerminalSort.ID);

		inventory = new AEInventory(items.size());
		for (int i = 0; i < items.size(); i++) {
			IAEItemStack iae = items.get(i);
			inventory.setInventorySlotContents(i, iae.getItemStack());
			int x = 0;
			int y = 0;
			this.addSlotToContainer(new Slot(inventory, i, x, y));
		}
	}

	private IMEMonitor<IAEItemStack> getAEInventory() {
		IGridNode ign = grid.getGridNode(direction);
		if (ign != null) {
			IGrid ig = ign.getGrid();
			if (ig != null) {
				IStorageGrid isg = ig.getCache(IStorageGrid.class);
				if (isg != null) {
					IMEMonitor<IAEItemStack> ime = isg.getItemInventory();
					return ime;
				}
			}
		}
		return null;
	}

	public void sort(TerminalSort sort) {
		Collections.sort(items, sort.comparator);
	}

	@Override
	public ItemStack slotClick(int slot, int button, int c, EntityPlayer ep) {
		return super.slotClick(slot, button, c, ep);
	}

	@Override
	public boolean canInteractWith(EntityPlayer ep) {
		return true;
	}

	public static enum TerminalSort {
		ID(new IDComparator()),
		NAME(new NameComparator()),
		COUNT(new SizeComparator());

		private final Comparator<IAEItemStack> comparator;

		private TerminalSort(Comparator<IAEItemStack> c) {
			comparator = c;
		}
	}

	private static class IDComparator implements Comparator<IAEItemStack> {

		@Override
		public int compare(IAEItemStack o1, IAEItemStack o2) {
			return Item.getIdFromItem(o1.getItem())-Item.getIdFromItem(o2.getItem());
		}

	}

	private static class NameComparator implements Comparator<IAEItemStack> {

		@Override
		public int compare(IAEItemStack o1, IAEItemStack o2) {
			return String.CASE_INSENSITIVE_ORDER.compare(o1.getItemStack().getDisplayName(), o2.getItemStack().getDisplayName());
		}

	}

	private static class SizeComparator implements Comparator<IAEItemStack> {

		@Override
		public int compare(IAEItemStack o1, IAEItemStack o2) {
			return o1.getStackSize() > o2.getStackSize() ? -1 : o1.getStackSize() < o2.getStackSize() ? 1 : 0;
		}

	}

	private static class AEInventory extends BasicInventory {

		public AEInventory(int size) {
			super("Terminal", size);
		}

		@Override
		public boolean isItemValidForSlot(int i, ItemStack is) {
			return true;
		}

	}

}
