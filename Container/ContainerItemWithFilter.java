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
import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.ItemWithItemFilter;
import Reika.ChromatiCraft.Base.ItemWithItemFilter.Filter;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class ContainerItemWithFilter extends Container {

	private static final int width = 9;
	private static final int height = 3;

	private final InventoryCrafting inventory = new InventoryCrafting(this, width, height);
	private final Filter[] filters = new Filter[width*height];
	private World worldObj;

	public ContainerItemWithFilter(EntityPlayer player, World par2World)
	{
		worldObj = par2World;
		int var6;
		int var7;

		for (int i = 0; i < height; i++) {
			for (int k = 0; k < width; k++) {
				this.addSlotToContainer(new Slot(inventory, i*width+k, 8+k*18, 17+i*18));
			}
		}

		for (var6 = 0; var6 < 3; ++var6)
			for (var7 = 0; var7 < 9; ++var7)
				this.addSlotToContainer(new Slot(player.inventory, var7 + var6 * 9 + 9, 8 + var7 * 18, 84 + var6 * 18));
		for (var6 = 0; var6 < 9; ++var6)
			this.addSlotToContainer(new Slot(player.inventory, var6, 8 + var6 * 18, 142));

		ItemStack tool = player.getCurrentEquippedItem();
		int i = 0;
		for (Filter f : ((ItemWithItemFilter)tool.getItem()).getItemList(tool)) {
			filters[i] = f;
			i++;
		}
		for (i = 0; i < filters.length; i++) {
			inventory.setInventorySlotContents(i, filters[i] == null ? null : filters[i].getDisplay());
		}

		this.onCraftMatrixChanged(inventory);
	}

	@Override
	public ItemStack slotClick(int slot, int button, int par3, EntityPlayer ep) {
		boolean inGUI = slot < width*height && slot >= 0;
		if (inGUI) {
			ItemStack held = ep.inventory.getItemStack();
			if (button == 1 && filters[slot] != null) { //right click
				filters[slot].toggleNBT();
			}
			else {
				ItemStack is = held != null ? ReikaItemHelper.getSizedItemStack(held, 1) : null;
				inventory.setInventorySlotContents(slot, is);
				filters[slot] = is == null ? null : new Filter(is);
			}
			this.detectAndSendChanges();
			this.save(ep);
			return held;
		}
		else if (slot >= width*height+27) {
			ItemStack in = ep.inventory.getStackInSlot(slot-width*height-27);
			if (in != null && in.getItem() instanceof ItemWithItemFilter) {
				return ep.inventory.getItemStack();
			}
		}
		return super.slotClick(slot, button, par3, ep);
	}

	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();

		int flags = 0;

		for (int i = 0; i < filters.length; i++) {
			if (filters[i] != null && filters[i].hasNBT()) {
				flags |= (1 << i);
			}
		}

		for (int j = 0; j < crafters.size(); ++j)
		{

		}
	}

	@Override
	public void onContainerClosed(EntityPlayer ep) {
		super.onContainerClosed(ep);

		this.save(ep);
	}

	private void save(EntityPlayer ep) {
		ItemStack is = ep.getCurrentEquippedItem();
		if (is != null && is.getItem() instanceof ItemWithItemFilter) {
			((ItemWithItemFilter)ChromaItems.LINK.getItemInstance()).setItems(is, filters);
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
		return true;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
		return null;
	}

}
