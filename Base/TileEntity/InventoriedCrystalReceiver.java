/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base.TileEntity;

import Reika.ChromatiCraft.Magic.CrystalNetworker;
import Reika.ChromatiCraft.Magic.CrystalReceiver;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.CrystalElement;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public abstract class InventoriedCrystalReceiver extends InventoriedCrystalBase implements CrystalReceiver {

	protected ElementTagCompound energy = new ElementTagCompound();
	private int receiveCooldown = 40;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		if (receiveCooldown > 0)
			receiveCooldown--;
	}

	protected final int getCooldown() {
		return receiveCooldown;
	}

	protected abstract int getMaxStorage();

	protected final void requestEnergy(CrystalElement e, int amount) {
		int amt = Math.min(amount, this.getRemainingSpace(e));
		if (amt > 0)
			CrystalNetworker.instance.makeRequest(this, e, amount, this.getReceiveRange());
	}

	public final int getRemainingSpace(CrystalElement e) {
		return this.getMaxStorage()-this.getEnergy(e);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		energy.readFromNBT("energy", NBT);
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		energy.writeToNBT("energy", NBT);
	}

	@Override
	public final void receiveElement(CrystalElement e, int amt) {
		energy.addValueToColor(e, amt);
		this.clamp(e);
		receiveCooldown = 40;
	}

	public final int getEnergy(CrystalElement e) {
		return energy.getValue(e);
	}

	protected final void drainEnergy(CrystalElement e, int amt) {
		energy.subtract(e, amt);
	}

	private void clamp(CrystalElement e) {
		int max = this.getMaxStorage();
		if (this.getEnergy(e) > max)
			energy.setTag(e, max);
	}

}
