/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity;

import Reika.ChromatiCraft.Auxiliary.Interfaces.ItemOnRightClick;
import Reika.ChromatiCraft.Base.TileEntity.InventoriedChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Instantiable.InertItem;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class TileEntityItemStand extends InventoriedChromaticBase implements ItemOnRightClick {

	private InertItem item;

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (inv[0] != null && this.getTicksExisted() == 0) {
			item = new InertItem(world, inv[0]);
		}
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is) {
		return false;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack is, int side) {
		return false;
	}

	@Override
	public void onRightClickWith(ItemStack item) {
		this.dropSlot();
		inv[0] = item != null ? item.copy() : null;
		this.item = item != null ? new InertItem(worldObj, item) : null;
	}

	public EntityItem getItem() {
		return item;
	}

	private void dropSlot() {
		if (inv[0] != null)
			ReikaItemHelper.dropItem(worldObj, xCoord+0.5, yCoord+1, zCoord+0.5, inv[0]);
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.STAND;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

}
