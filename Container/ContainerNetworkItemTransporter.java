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
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.TileEntity.Transport.TileEntityNetworkItemTransporter;
import Reika.DragonAPI.Base.CoreContainer;
import Reika.DragonAPI.Instantiable.GUI.Slot.SlotNoClick;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;

public class ContainerNetworkItemTransporter extends CoreContainer {

	private TileEntityNetworkItemTransporter tile;

	private boolean showingFilters = false;

	public ContainerNetworkItemTransporter(EntityPlayer player, TileEntityNetworkItemTransporter te) {
		super(player, te);
		tile = te;

		this.setFilterDisplay(false);
	}

	public boolean isFilterMode() {
		return showingFilters;
	}

	public void setFilterDisplay(boolean show) {
		if (show != showingFilters || inventorySlots.isEmpty()) {
			inventorySlots.clear();
			showingFilters = show;
			int dx = -18;
			int dy = 17;
			int idx = 0;
			for (int j = 0; j < 9; j++) {
				if (j == 4)
					continue;
				for (int i = 0; i < 3; i++) {
					int x = dx+26+j*18;
					int y = dy+i*18;
					Slot s = show ? new SlotNoClick(tile, idx, x, y, false, false) : new Slot(tile, idx, x, y);
					this.addSlotToContainer(s);
					idx++;
				}
			}

			this.addPlayerInventoryWithOffset(ep, 0, 0);
			if (ep.worldObj.isRemote)
				ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.NETWORKITEMMODE.ordinal(), PacketTarget.server, showingFilters ? 1 : 0);
		}
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

		if (slot >= 12 && slot < tile.getSizeInventory() && showingFilters) {
			action = 0;
			ItemStack held = ep.inventory.getItemStack();
			ItemStack cp = held == null ? null : held.copy();
			if (cp != null && par2 != 1)
				cp.stackSize = 1;
			tile.setFilter(slot-12, cp);
			//this.detectAndSendChanges();
			return held;
		}

		ItemStack is = super.slotClick(slot, par2, action, ep);
		InventoryPlayer ip = ep.inventory;
		return is;
		//ReikaJavaLibrary.pConsole(ip.getItemStack());
	}
}
