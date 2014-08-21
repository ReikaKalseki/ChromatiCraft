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

import Reika.ChromatiCraft.Base.TileEntity.InventoriedCrystalReceiver;
import Reika.ChromatiCraft.Items.ItemStorageCrystal;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.StepTimer;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class TileEntityCrystalCharger extends InventoriedCrystalReceiver {

	private StepTimer checkTimer = new StepTimer(40);

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		if (this.canConduct()) {
			if (!world.isRemote && this.getCooldown() == 0) {
				this.checkAndRequest();
			}

			for (CrystalElement e : energy.elementSet()) {
				int max = this.getMaxTransfer(e);
				int amt = this.getEnergy(e);
				ItemStorageCrystal cry = this.item();
				int put = Math.min(max, Math.min(amt, cry.getSpace(e, inv[0])));
				if (put > 0) {
					cry.addEnergy(inv[0], e, put);
					this.drainEnergy(e, put);
				}
			}
		}
	}

	private ItemStorageCrystal item() {
		return ((ItemStorageCrystal)inv[0].getItem());
	}

	private int getMaxTransfer(CrystalElement e) {
		return 10+(int)Math.sqrt(this.getEnergy(e));
	}

	private void checkAndRequest() {
		if (this.canConduct()) {
			ElementTagCompound tag = this.item().getStoredTags(inv[0]);
			int capacity = this.item().getCapacity(inv[0]);
			for (CrystalElement e : tag.elementSet()) {
				int space = capacity-tag.getValue(e)-this.getEnergy(e);
				if (space > 0) {
					this.requestEnergy(e, space);
				}
			}
		}
	}

	@Override
	public void onPathBroken() {

	}

	@Override
	public int getReceiveRange() {
		return 20;
	}

	@Override
	public boolean isConductingElement(CrystalElement e) {
		return this.canConduct() && e != null && e.ordinal() == inv[0].getItemDamage();
	}

	@Override
	public int maxThroughput() {
		return 60;
	}

	@Override
	public boolean canConduct() {
		return inv[0] != null && ChromaItems.STORAGE.matchWith(inv[0]);
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
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.CHARGER;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	protected int getMaxStorage() {
		return 1000;
	}

}
