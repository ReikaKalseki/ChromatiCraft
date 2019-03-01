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
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import Reika.ChromatiCraft.Block.BlockRouterNode.RouterFilter;
import Reika.ChromatiCraft.TileEntity.Transport.TileEntityRouterHub.ItemRule;
import Reika.DragonAPI.Auxiliary.Trackers.KeyWatcher;
import Reika.DragonAPI.Auxiliary.Trackers.KeyWatcher.Key;
import Reika.DragonAPI.Base.CoreContainer;
import Reika.DragonAPI.Instantiable.GUI.Slot.GhostSlot;

public class ContainerRouterFilter extends CoreContainer {

	private RouterFilter tile;
	private boolean noUpdate;

	public ContainerRouterFilter(EntityPlayer player, RouterFilter te) {
		super(player, (TileEntity)te);
		int dx = -18;
		int dy = 18;
		tile = te;

		for (int j = 0; j < 9; j++) {
			this.addSlotToContainer(new GhostSlot(j, dx+26+j*18, dy+15));
		}

		this.addPlayerInventoryWithOffset(player, 0, -29);
	}

	@Override
	public boolean allowShiftClicking(EntityPlayer player, int slot, ItemStack stack) {
		return false;
	}

	@Override
	public ItemStack slotClick(int slot, int par2, int action, EntityPlayer ep) {

		if (slot >= 0 && slot < 9) {
			ItemStack held = ep.inventory.getItemStack();
			if (KeyWatcher.instance.isKeyDown(ep, Key.LCTRL)) {
				ItemRule ir = tile.getFilter(slot);
				if (ir != null)
					tile.setFilterMode(slot, ir.mode.next());
			}
			else {
				tile.setFilterItem(slot, held);
				//this.detectAndSendChanges();
			}
			return held;
		}

		ItemStack is = super.slotClick(slot, par2, action, ep);
		InventoryPlayer ip = ep.inventory;
		return is;
	}
}
