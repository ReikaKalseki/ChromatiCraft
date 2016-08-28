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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import Reika.DragonAPI.Base.CoreContainer;
import Reika.DragonAPI.Instantiable.GUI.Slot.GhostSlot;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class ContainerMEDistributor extends CoreContainer {

	private TileEntityMEDistributor tile;

	public ContainerMEDistributor(EntityPlayer player, TileEntityMEDistributor te) {
		super(player, te);
		tile = te;

		int w = 67;
		int w2 = 115;
		for (int i = 0; i < tile.NSLOTS; i++) {
			int dx = 8;
			if (i >= 5)
				dx += w2;
			int dy = 7+(i%5)*20+9;
			this.addSlotToContainer(new GhostSlot(i, dx, dy));
			this.addSlotToContainer(new GhostSlot(i+tile.NSLOTS, dx+w, dy));
		}

		this.addPlayerInventoryWithOffset(player, 19, 49);
	}

	@Override
	public boolean allowShiftClicking(EntityPlayer player, int slot, ItemStack stack) {
		return false;
	}

	@Override
	public ItemStack slotClick(int slot, int mouse, int action, EntityPlayer ep) {
		/*
		if (slot >= 18 && slot < tile.getSizeInventory()) {
			ItemStack held = ep.inventory.getItemStack();
			tile.setMapping(slot, ReikaItemHelper.getSizedItemStack(held, 1));
			return held;
		}
		 */
		if (slot >= 0 && slot < tile.NSLOTS*2) {
			ItemStack held = ep.inventory.getItemStack();
			int idx = ((Slot)inventorySlots.get(slot)).getSlotIndex();
			if (mouse == 0) {
				tile.setMapping(idx, ReikaItemHelper.getSizedItemStack(held, 1));
			}
			//else if (mouse == 1) {
			//	tile.toggleFuzzy(idx%tile.NSLOTS);
			//}
			//this.detectAndSendChanges();
			return held;
		}

		ItemStack is = super.slotClick(slot, mouse, action, ep);
		InventoryPlayer ip = ep.inventory;
		return is;
		//ReikaJavaLibrary.pConsole(ip.getItemStack());
	}
}
