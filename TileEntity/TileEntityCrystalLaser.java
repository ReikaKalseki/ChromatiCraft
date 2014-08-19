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
import Reika.ChromatiCraft.Base.TileEntity.InventoriedCrystalBase;
import Reika.ChromatiCraft.Magic.CrystalReceiver;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class TileEntityCrystalLaser extends InventoriedCrystalBase implements CrystalReceiver, ItemOnRightClick {

	@Override
	public void receiveElement(CrystalElement e, int amt) {

	}

	@Override
	public void onPathBroken() {

	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.LASER;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public boolean isConductingElement(CrystalElement e) {
		return e != null && e == this.getColor();
	}

	public CrystalElement getColor() {
		return inv[0] != null && ChromaItems.LENS.matchWith(inv[0]) ? CrystalElement.elements[inv[0].getItemDamage()] : null;
	}

	@Override
	public int maxThroughput() {
		return 100;
	}

	@Override
	public boolean canConduct() {
		return inv[0] != null;
	}

	@Override
	public int getReceiveRange() {
		return 40;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack is, int side) {
		return false;
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is) {
		return false;
	}

	@Override
	public void onRightClickWith(ItemStack item) {
		this.dropSlot();
		inv[0] = item != null ? item.copy() : null;
	}

	private void dropSlot() {
		if (inv[0] != null)
			ReikaItemHelper.dropItem(worldObj, xCoord+0.5, yCoord+0.5, zCoord+0.5, inv[0]);
	}

}
