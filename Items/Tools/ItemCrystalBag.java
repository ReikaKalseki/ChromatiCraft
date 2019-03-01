/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items.Tools;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import Reika.ChromatiCraft.Base.ItemChromaTool;


public class ItemCrystalBag extends ItemChromaTool {

	public ItemCrystalBag(int index) {
		super(index);
	}

	public static class BagInventory implements IInventory {

		private final ItemStack bag;

		private BagInventory(ItemStack is) {
			bag = is;
		}

		@Override
		public int getSizeInventory() {
			return 0;
		}

		@Override
		public ItemStack getStackInSlot(int slot) {
			return null;
		}

		@Override
		public ItemStack decrStackSize(int slot, int decr) {
			return null;
		}

		@Override
		public ItemStack getStackInSlotOnClosing(int slot) {
			return null;
		}

		@Override
		public void setInventorySlotContents(int slot, ItemStack is) {

		}

		@Override
		public String getInventoryName() {
			return "Crystal Bag";
		}

		@Override
		public boolean hasCustomInventoryName() {
			return false;
		}

		@Override
		public int getInventoryStackLimit() {
			return 64;
		}

		@Override
		public void markDirty() {

		}

		@Override
		public boolean isUseableByPlayer(EntityPlayer ep) {
			return false;
		}

		@Override
		public void openInventory() {

		}

		@Override
		public void closeInventory() {

		}

		@Override
		public boolean isItemValidForSlot(int slot, ItemStack is) {
			return false;
		}

	}

}
