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

import org.apache.commons.lang3.tuple.ImmutableTriple;

import Reika.ChromatiCraft.Magic.CrystalNetworker;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalReceiver;
import Reika.ChromatiCraft.Magic.Interfaces.LumenRequestingTile;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.StepTimer;

public abstract class CrystalReceiverBase extends TileEntityCrystalBase implements CrystalReceiver, LumenRequestingTile {

	protected ElementTagCompound energy = new ElementTagCompound();
	private int receiveCooldown = this.getCooldownLength();
	protected StepTimer checkTimer = new StepTimer(this.getCooldownLength());

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		checkTimer.update();
		if (receiveCooldown > 0)
			receiveCooldown--;


		if (DragonAPICore.debugtest && !world.isRemote) {
			CrystalElement e = CrystalElement.randomElement();
			energy.addValueToColor(e, 500);
			this.clamp(e);
		}
	}

	protected int getCooldownLength() {
		return 40;
	}

	protected final int getCooldown() {
		return receiveCooldown;
	}

	public abstract int getMaxStorage(CrystalElement e);

	public final int getEnergyScaled(CrystalElement e, int a) {
		return a * this.getEnergy(e) / this.getMaxStorage(e);
	}

	protected final boolean requestEnergy(CrystalElement e, int amount) {
		int amt = Math.min(amount, this.getRemainingSpace(e));
		boolean flag = false;
		if (amt > 0) {
			flag = CrystalNetworker.instance.makeRequest(this, e, amount, this.getReceiveRange());
		}
		return flag;
	}

	protected final boolean requestEnergy(ElementTagCompound tag) {
		boolean flag = false;
		for (CrystalElement e : tag.elementSet()) {
			flag &= this.requestEnergy(e, tag.getValue(e));
		}
		return flag;
	}

	protected final boolean requestEnergyDifference(ElementTagCompound tag) {
		tag.subtract(energy);
		return this.requestEnergy(tag);
	}

	public final int getRemainingSpace(CrystalElement e) {
		return this.getMaxStorage(e)-this.getEnergy(e);
	}

	public final float getFillFraction(CrystalElement e) {
		return (float)energy.getValue(e)/this.getMaxStorage(e);
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

	protected final void clamp(CrystalElement e) {
		int max = this.getMaxStorage(e);
		if (this.getEnergy(e) > max)
			energy.setTag(e, max);
	}

	public final void setEnergy(CrystalElement e, int lvl) {
		energy.setTag(e, lvl);
	}

	@Override
	public ImmutableTriple<Double, Double, Double> getTargetRenderOffset(CrystalElement e) {
		return null;
	}

	public final ElementTagCompound getEnergy() {
		return energy.copy();
	}

	public ElementTagCompound getRequestedTotal() {
		return this.getCapacity();
	}

	protected final ElementTagCompound getCapacity() {
		ElementTagCompound tag = new ElementTagCompound();
		for (int i = 0; i < CrystalElement.elements.length; i++) {
			CrystalElement e = CrystalElement.elements[i];
			tag.setTag(e, this.getMaxStorage(e));
		}
		return tag;
	}

	protected final ElementTagCompound getDifference() {
		ElementTagCompound tag = new ElementTagCompound();
		for (int i = 0; i < CrystalElement.elements.length; i++) {
			CrystalElement e = CrystalElement.elements[i];
			tag.setTag(e, this.getMaxStorage(e)-this.getEnergy(e));
		}
		return tag;
	}

}
