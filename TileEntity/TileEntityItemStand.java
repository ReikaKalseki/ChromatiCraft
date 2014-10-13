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

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Auxiliary.Interfaces.ItemOnRightClick;
import Reika.ChromatiCraft.Base.TileEntity.InventoriedChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Instantiable.InertItem;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class TileEntityItemStand extends InventoriedChromaticBase implements ItemOnRightClick {

	private InertItem item;
	private int tileX;
	private int tileY;
	private int tileZ;

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

	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		this.updateItem();
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
	public ItemStack onRightClickWith(ItemStack item) {
		this.dropSlot();
		inv[0] = item != null ? ReikaItemHelper.getSizedItemStack(item, 1) : null;
		this.updateItem();
		item.stackSize--;
		return item;
	}

	private void updateItem() {
		item = inv[0] != null ? new InertItem(worldObj, inv[0]) : null;
		if (worldObj != null) {
			TileEntity te = worldObj.getTileEntity(tileX, tileY, tileZ);
			if (te instanceof TileEntityCastingTable) {
				((TileEntityCastingTable)te).markDirty();
			}
		}
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

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		this.updateItem();

		tileX = NBT.getInteger("tx");
		tileY = NBT.getInteger("ty");
		tileZ = NBT.getInteger("tz");
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		NBT.setInteger("tx", tileX);
		NBT.setInteger("ty", tileY);
		NBT.setInteger("tz", tileZ);
	}

	public void setTable(TileEntityCastingTable te) {
		tileX = te.xCoord;
		tileY = te.yCoord;
		tileZ = te.zCoord;
	}

}
