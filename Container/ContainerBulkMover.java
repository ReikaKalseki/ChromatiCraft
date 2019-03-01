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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import Reika.ChromatiCraft.Items.Tools.ItemBulkMover;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class ContainerBulkMover extends Container {

	private InventoryCrafting inventory = new InventoryCrafting(this, 1, 1);

	public ContainerBulkMover(EntityPlayer player)
	{
		int var6;
		int var7;

		this.addSlotToContainer(new Slot(inventory, 0, 8, 10));

		for (var6 = 0; var6 < 3; ++var6)
			for (var7 = 0; var7 < 9; ++var7)
				this.addSlotToContainer(new Slot(player.inventory, var7 + var6 * 9 + 9, 8 + var7 * 18, 34 + var6 * 18));
		for (var6 = 0; var6 < 9; ++var6)
			this.addSlotToContainer(new Slot(player.inventory, var6, 8 + var6 * 18, 92));

		ItemStack tool = player.getCurrentEquippedItem();
		ItemBulkMover iil = (ItemBulkMover)tool.getItem();
		inventory.setInventorySlotContents(0, iil.getStoredItem(tool));

		this.onCraftMatrixChanged(inventory);
	}

	@Override
	public ItemStack slotClick(int slot, int par2, int par3, EntityPlayer ep) {
		boolean inGUI = slot == 0;
		if (inGUI) {
			ItemStack held = ep.inventory.getItemStack();
			ItemStack is = held != null ? ReikaItemHelper.getSizedItemStack(held, 1) : null;
			inventory.setInventorySlotContents(slot, is);
			return held;
		}
		else
			return super.slotClick(slot, par2, par3, ep);
	}

	@Override
	public void onContainerClosed(EntityPlayer ep) {
		super.onContainerClosed(ep);

		ItemStack is = ep.getCurrentEquippedItem();
		if (ChromaItems.BULKMOVER.matchWith(is)) {
			ItemBulkMover iil = (ItemBulkMover)is.getItem();
			iil.setStoredItem(is, inventory.getStackInSlot(0));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
		return true;
	}

	/**
	 * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
	 */
	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
	{
		return null;//this.getSlot(0).getStack();
	}

}
