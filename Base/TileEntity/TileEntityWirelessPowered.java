/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base.TileEntity;

import java.util.Collection;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Interfaces.NBTTile;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.Interfaces.LumenTile;
import Reika.ChromatiCraft.Magic.Interfaces.WirelessSource;
import Reika.ChromatiCraft.Magic.Network.CrystalNetworker;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public abstract class TileEntityWirelessPowered extends TileEntityChromaticBase implements LumenTile, NBTTile {

	protected final ElementTagCompound energy = new ElementTagCompound();

	protected final boolean requestEnergy(CrystalElement e, int amt) {
		if (DragonAPICore.debugtest && !worldObj.isRemote) {
			energy.addValueToColor(e, amt);
		}
		Collection<WirelessSource> c = CrystalNetworker.instance.getNearTilesOfType(worldObj, xCoord, yCoord, zCoord, WirelessSource.class, this.getReceiveRange(e));
		for (WirelessSource s : c) {
			if (s.canConduct() && s.canTransmitTo(this)) {
				int ret = s.request(e, amt, xCoord, yCoord, zCoord);
				if (ret > 0) {
					energy.addValueToColor(e, ret);
					ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.WIRELESS.ordinal(), this, 64, s.getX(), s.getY(), s.getZ(), e.ordinal(), ret);
					return true;
				}
			}
		}
		return false;
	}

	@SideOnly(Side.CLIENT)
	public final void doEnergyRequestClient(World world, int x, int y, int z, int dx, int dy, int dz, CrystalElement e, int amt) {
		//particle bezier
	}

	public final int getEnergy(CrystalElement e) {
		return energy.getValue(e);
	}

	protected abstract int getReceiveRange(CrystalElement e);

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

	@Override
	public void getTagsToWriteToStack(NBTTagCompound NBT) {
		super.getTagsToWriteToStack(NBT);
		energy.writeToNBT("energy", NBT);
	}

	@Override
	public void setDataFromItemStackTag(ItemStack is) {
		super.setDataFromItemStackTag(is);
		if (is.stackTagCompound == null)
			return;
		energy.readFromNBT("energy", is.stackTagCompound);
	}

}
