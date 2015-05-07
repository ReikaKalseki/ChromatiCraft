/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
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
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityItemCollector;
import Reika.DragonAPI.Base.CoreContainer;
import Reika.DragonAPI.Instantiable.GUI.Slot.GhostSlot;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class ContainerItemCollector extends CoreContainer {

	private TileEntityItemCollector tile;
	private boolean noUpdate;

	public ContainerItemCollector(EntityPlayer player, TileEntityItemCollector te) {
		super(player, te);
		int dx = -18;
		int dy = 18;
		tile = te;

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				this.addSlotToContainer(new Slot(te, i*9+j, dx+26+j*18, dy+i*18));
			}
		}

		dy = 81;
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 9; j++) {
				//this.addSlotToContainer(new GhostSlot(te, 18+i*3+j, dx+26+j*18, dy+i*18));
				this.addSlotToContainer(new GhostSlot(te, 27+i*9+j, dx+26+j*18, dy+i*18+9));
			}
		}

		this.addPlayerInventoryWithOffset(player, 0, 55);
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

		if (slot >= tile.getSizeInventory() && slot < tile.getSizeInventory()+2*9) {
			action = 0;
			ItemStack held = ep.inventory.getItemStack();
			tile.setMapping(slot-tile.getSizeInventory(), ReikaItemHelper.getSizedItemStack(held, 1));
			//this.detectAndSendChanges();
			return held;
		}

		ItemStack is = super.slotClick(slot, par2, action, ep);
		InventoryPlayer ip = ep.inventory;
		return is;
		//ReikaJavaLibrary.pConsole(ip.getItemStack());
	}
}
