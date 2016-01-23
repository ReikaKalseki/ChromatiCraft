/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Networking;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Auxiliary.Interfaces.ItemOnRightClick;
import Reika.ChromatiCraft.Base.TileEntity.InventoriedCrystalReceiver;
import Reika.ChromatiCraft.Items.ItemStorageCrystal;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Interfaces.TileEntity.InertIInv;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class TileEntityRelaySource extends InventoriedCrystalReceiver implements InertIInv, ItemOnRightClick {

	@Override
	protected int getCooldownLength() {
		return 200;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		if (!world.isRemote && this.getCooldown() == 0 && checkTimer.checkCap()) {
			this.checkAndRequest();
		}

		if (inv[0] != null && ChromaItems.STORAGE.matchWith(inv[0])) {
			for (CrystalElement e : ItemStorageCrystal.getStoredTags(inv[0]).elementSet()) {
				int amt = ItemStorageCrystal.getStoredEnergy(inv[0], e);
				int add = Math.min(amt, Math.min(this.getMaxStorage(e)-energy.getValue(e), this.maxThroughput()*4));
				if (add > 0) {
					ItemStorageCrystal.removeEnergy(inv[0], e, add);
					energy.addValueToColor(e, add);
				}
			}
		}
	}

	private void checkAndRequest() {
		for (int i = 0; i < CrystalElement.elements.length; i++) {
			CrystalElement e = CrystalElement.elements[i];
			int space = this.getRemainingSpace(e);
			if (space > this.getEnergy(e)) { // < 50% full
				this.requestEnergy(e, space);
			}
		}
	}

	@Override
	public int getReceiveRange() {
		return 32;
	}

	@Override
	public boolean isConductingElement(CrystalElement e) {
		return e != null;
	}

	@Override
	public int maxThroughput() {
		return 6000;
	}

	@Override
	public boolean canConduct() {
		return true;
	}

	@Override
	public int getMaxStorage(CrystalElement e) {
		return 720000;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.RELAYSOURCE;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);


	}

	@Override
	public void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);


	}

	@Override
	public boolean canExtractItem(int slot, ItemStack is, int side) {
		return side == 0;
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
		return ChromaItems.STORAGE.matchWith(is) && ItemStorageCrystal.getTotalEnergy(is) > 0;
	}

	@Override
	public ItemStack onRightClickWith(ItemStack item, EntityPlayer ep) {
		this.dropItem(inv[0]);
		inv[0] = null;
		if (this.isItemValidForSlot(0, item)) {
			inv[0] = item.copy();
			item = null;
		}
		return item;
	}

	private void dropItem(ItemStack is) {
		ReikaItemHelper.dropItem(worldObj, xCoord+0.5, yCoord+0.75, zCoord+0.5, is);
	}

}
