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

import net.minecraft.nbt.NBTTagCompound;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.CrystalElement;

public abstract class TileEntityFiberPowered extends TileEntityChromaticBase {

	protected ElementTagCompound energy = new ElementTagCompound();

	public final int addEnergy(CrystalElement e, int amt) {
		if (e == null || !this.isAcceptingColor(e))
			return 0;
		int diff = Math.min(amt, this.getRemainingSpace(e));
		energy.addValueToColor(e, diff);
		return diff;
	}

	public abstract boolean isAcceptingColor(CrystalElement e);

	public final int getEnergyScaled(CrystalElement e, int a) {
		return a * this.getEnergy(e) / this.getMaxStorage();
	}

	public final int getEnergy(CrystalElement e) {
		return energy.getValue(e);
	}

	public final int getRemainingSpace(CrystalElement e) {
		return this.getMaxStorage()-this.getEnergy(e);
	}

	public abstract int getMaxStorage();

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

	protected final void drainEnergy(CrystalElement e, int amt) {
		energy.subtract(e, amt);
	}

	protected final void drainEnergy(ElementTagCompound tag) {
		energy.subtract(tag);
	}

	public final void setEnergy(CrystalElement e, int lvl) {
		energy.setTag(e, lvl);
	}

}
