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
import net.minecraft.world.World;
import Reika.ChromatiCraft.Magic.CrystalNetworker;
import Reika.ChromatiCraft.Magic.CrystalReceiver;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.StepTimer;

public abstract class CrystalReceiverBase extends TileEntityCrystalBase implements CrystalReceiver {

	protected ElementTagCompound energy = new ElementTagCompound();
	private int receiveCooldown = this.getCooldownLength();
	protected StepTimer checkTimer = new StepTimer(this.getCooldownLength());

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		checkTimer.update();
		if (receiveCooldown > 0)
			receiveCooldown--;


		if (DragonAPICore.debugtest) {
			energy.addValueToColor(CrystalElement.randomElement(), 500);
		}
	}

	protected int getCooldownLength() {
		return 40;
	}

	protected final int getCooldown() {
		return receiveCooldown;
	}

	public abstract int getMaxStorage();

	public final int getEnergyScaled(CrystalElement e, int a) {
		return a * this.getEnergy(e) / this.getMaxStorage();
	}

	protected final void requestEnergy(CrystalElement e, int amount) {
		int amt = Math.min(amount, this.getRemainingSpace(e));
		if (amt > 0)
			CrystalNetworker.instance.makeRequest(this, e, amount, this.getReceiveRange());
	}

	protected final void requestEnergy(ElementTagCompound tag) {
		for (CrystalElement e : tag.elementSet()) {
			this.requestEnergy(e, tag.getValue(e));
		}
	}

	protected final void requestEnergyDifference(ElementTagCompound tag) {
		tag.subtract(energy);
		this.requestEnergy(tag);
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
		receiveCooldown = this.getCooldownLength();
	}

	public final int getEnergy(CrystalElement e) {
		return energy.getValue(e);
	}

	protected final void drainEnergy(CrystalElement e, int amt) {
		energy.subtract(e, amt);
	}

	protected final void drainEnergy(ElementTagCompound tag) {
		energy.subtract(tag);
	}

	private void clamp(CrystalElement e) {
		int max = this.getMaxStorage();
		if (this.getEnergy(e) > max)
			energy.setTag(e, max);
	}

	public final void setEnergy(CrystalElement e, int lvl) {
		energy.setTag(e, lvl);
	}

}
