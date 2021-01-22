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

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import Reika.ChromatiCraft.TileEntity.Transport.TileEntityNetworkItemTransporter;
import Reika.DragonAPI.Base.CoreContainer;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class ContainerNetworkItemTransporter extends CoreContainer {

	private TileEntityNetworkItemTransporter tile;

	public ContainerNetworkItemTransporter(EntityPlayer player, TileEntityNetworkItemTransporter te) {
		super(player, te);
		int dx = -18;
		int dy = 17;
		tile = te;

		int idx = 0;
		for (int j = 0; j < 9; j++) {
			if (j == 4)
				continue;
			for (int i = 0; i < 3; i++) {
				int x = dx+26+j*18;
				int y = dy+i*18;
				Slot s = new Slot(te, idx, x, y);
				this.addSlotToContainer(s);
				idx++;
			}
		}

		this.addPlayerInventoryWithOffset(player, 0, 0);
	}

	@Override
	public boolean allowShiftClicking(EntityPlayer player, int slot, ItemStack stack) {
		return false;
	}

	@Override
	public ItemStack slotClick(int slot, int par2, int action, EntityPlayer ep) {
		/*
		if (slot >= 18 && slot < tile.getSizeInventory()) {
			ItemStack held = ep.inventory.getItemStack();
			tile.setMapping(slot, ReikaItemHelper.getSizedItemStack(held, 1));
			return held;
		}
		 */

		if (slot >= 12 && slot < tile.getSizeInventory() && GuiScreen.isCtrlKeyDown()) {
			action = 0;
			ItemStack held = ep.inventory.getItemStack();
			tile.setFilter(slot-12, ReikaItemHelper.getSizedItemStack(held, 1));
			//this.detectAndSendChanges();
			return held;
		}

		ItemStack is = super.slotClick(slot, par2, action, ep);
		InventoryPlayer ip = ep.inventory;
		return is;
		//ReikaJavaLibrary.pConsole(ip.getItemStack());
	}
}
