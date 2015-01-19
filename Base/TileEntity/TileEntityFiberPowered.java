/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base.TileEntity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.Interfaces.LumenTile;
import Reika.ChromatiCraft.Magic.Network.RelayNetworker;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityFiberReceiver;
import Reika.DragonAPI.DragonAPICore;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class TileEntityFiberPowered extends TileEntityChromaticBase implements LumenTile {

	protected final ElementTagCompound energy = new ElementTagCompound();

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (DragonAPICore.debugtest && !world.isRemote) {
			CrystalElement e = CrystalElement.randomElement();
			energy.addValueToColor(e, 500);
		}
	}

	protected final boolean requestEnergy(CrystalElement e, int amt, ForgeDirection dir) {
		TileEntityFiberReceiver te = RelayNetworker.instance.findRelaySource(worldObj, xCoord, yCoord, zCoord, dir, e, amt, 128);
		if (te != null) {
			int has = te.getEnergy(e);
			te.drainEnergy(e, Math.min(amt, has));
			return has >= amt;
		}
		return false;
	}

	protected final boolean requestEnergy(CrystalElement e, int amt) {
		for (int i = 0; i < 6; i++) {
			if (this.requestEnergy(e, amt, dirs[i]))
				return true;
		}
		return false;
	}

	protected final boolean requestEnergy(ElementTagCompound tag) {
		for (int i = 0; i < 6; i++) {
			if (this.requestEnergy(tag, dirs[i]))
				return true;
		}
		return false;
	}

	protected final boolean requestEnergy(ElementTagCompound tag, ForgeDirection dir) {
		boolean flag = true;
		for (CrystalElement e : tag.elementSet()) {
			flag = this.requestEnergy(e, tag.getValue(e), dir) && flag;
		}
		return flag;
	}

	private final int addEnergy(CrystalElement e, int amt) {
		if (e == null || !this.isAcceptingColor(e))
			return 0;
		int diff = Math.min(amt, this.getRemainingSpace(e));
		energy.addValueToColor(e, diff);
		return diff;
	}

	public abstract boolean isAcceptingColor(CrystalElement e);

	public final int getEnergyScaled(CrystalElement e, int a) {
		return a * this.getEnergy(e) / this.getMaxStorage(e);
	}

	public final int getEnergy(CrystalElement e) {
		return energy.getValue(e);
	}

	public final int getRemainingSpace(CrystalElement e) {
		return this.getMaxStorage(e)-this.getEnergy(e);
	}

	public abstract int getMaxStorage(CrystalElement e);

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

	@SideOnly(Side.CLIENT)
	public final void setEnergyClient(CrystalElement e, int lvl) {
		energy.setTag(e, lvl);
	}

	public final ElementTagCompound getEnergy() {
		return energy.copy();
	}

}
