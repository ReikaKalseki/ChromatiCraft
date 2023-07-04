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
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Items.Tools.ItemAuraPouch;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.DragonAPI.Instantiable.BasicInventory;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;

public class ContainerAuraPouch extends Container {

	private final EntityPlayer player;
	private final PouchInventory inventory = new PouchInventory();

	public ContainerAuraPouch(EntityPlayer ep) {
		super();
		player = ep;

		int w = 9;
		int h = ItemAuraPouch.SIZE/w;

		for (int i = 0; i < h; ++i)
			for (int k = 0; k < w; ++k)
				this.addSlotToContainer(new Slot(inventory, k+i*w, 8+k*18, 17+i*18));

		for (int i = 0; i < 3; ++i)
			for (int k = 0; k < 9; ++k)
				this.addSlotToContainer(new Slot(player.inventory, k+i*9+9, 8+k*18, 84+i*18));
		for (int i = 0; i < 9; ++i)
			this.addSlotToContainer(new Slot(player.inventory, i, 8+i*18, 142));

		ItemStack tool = player.getCurrentEquippedItem();
		if (!ChromaItems.AURAPOUCH.matchWith(tool)) {
			ChromatiCraft.logger.logError("Opened an Aura Pouch GUI without holding an aura pouch!?");
			return;
		}
		ItemAuraPouch iil = (ItemAuraPouch)tool.getItem();
		ItemStack[] li = iil.getInventory(tool);
		for (int i = 0; i < li.length; i++) {
			inventory.setInventorySlotContents(i, li[i]);
		}

		this.onCraftMatrixChanged(inventory);
	}

	@Override
	public void onCraftMatrixChanged(IInventory ii) {
		super.onCraftMatrixChanged(ii);

		ItemStack is = player.getCurrentEquippedItem();
		ItemAuraPouch iil = (ItemAuraPouch)is.getItem();
		iil.setItems(is, inventory.getItems());
	}

	public int getSize() {
		return inventory.getSizeInventory();
	}

	@Override
	public boolean canInteractWith(EntityPlayer ep) {
		return ChromaItems.AURAPOUCH.matchWith(ep.getHeldItem());
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
	{
		return null;
	}

	@Override
	public ItemStack slotClick(int slot, int button, int par3, EntityPlayer ep) {
		boolean inGUI = slot < ItemAuraPouch.SIZE && slot >= 0;
		if (inGUI) {
			if (ChromaItems.AURAPOUCH.matchWith(ep.inventory.getStackInSlot(slot))) {
				return ep.inventory.getItemStack();
			}
			if (button == 1) {
				ItemStack is = player.getCurrentEquippedItem();
				ItemAuraPouch iil = (ItemAuraPouch)is.getItem();
				boolean[] act = iil.getActiveSlots(is);
				if (player instanceof EntityPlayerMP)
					ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.AURAPOUCH.ordinal(), (EntityPlayerMP)player, slot, !act[slot] ? 1 : 0);
				iil.setSlotActive(is, slot, !act[slot]);
				return ep.inventory.getItemStack();
			}
			return super.slotClick(slot, button, par3, ep);
		}
		else if (slot >= ItemAuraPouch.SIZE+27) {
			ItemStack in = ep.inventory.getStackInSlot(slot-ItemAuraPouch.SIZE-27);
			if (ChromaItems.AURAPOUCH.matchWith(in)/* && !ChromaOptions.RECURSIVEPOUCH.getState()*/) { //introduces ability to move item and close gui
				return ep.inventory.getItemStack();
			}
		}
		return super.slotClick(slot, button, par3, ep);
	}

	@Override
	public void onContainerClosed(EntityPlayer ep) {
		super.onContainerClosed(ep);

		ItemStack is = ep.getCurrentEquippedItem();
		if (is == null || !(is.getItem() instanceof ItemAuraPouch)) {
			ChromatiCraft.logger.logError("Tried to access aura pouch inventory without a pouch!");
			return;
		}
		ItemAuraPouch iil = (ItemAuraPouch)is.getItem();
		iil.setItems(is, inventory.getItems());
	}

	private static class PouchInventory extends BasicInventory {

		private PouchInventory() {
			super("Aura Pouch", ItemAuraPouch.SIZE, 1);
		}

		@Override
		public boolean isUseableByPlayer(EntityPlayer ep) {
			return ChromaItems.AURAPOUCH.matchWith(ep.getCurrentEquippedItem());
		}

		@Override
		public boolean isItemValidForSlot(int slot, ItemStack is) {
			return true;
		}
	}

}
