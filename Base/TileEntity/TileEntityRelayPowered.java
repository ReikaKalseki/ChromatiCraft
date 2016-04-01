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

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Auxiliary.Interfaces.NBTTile;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.Interfaces.LumenTile;
import Reika.ChromatiCraft.Magic.Network.RelayNetworker;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityRelaySource;
import Reika.DragonAPI.DragonAPICore;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class TileEntityRelayPowered extends TileEntityChromaticBase implements LumenTile, NBTTile {

	protected final ElementTagCompound energy = new ElementTagCompound();

	private int requestTimer = rand.nextInt(200);

	private long lastRequestDecrTime = -1;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (DragonAPICore.debugtest && !world.isRemote) {
			CrystalElement e = CrystalElement.randomElement();
			if (this.isAcceptingColor(e))
				energy.addValueToColor(e, 500);
		}

		if (!world.isRemote) {
			if (requestTimer == 0) {
				ElementTagCompound tag = this.getRequiredEnergy();
				for (CrystalElement e : tag.elementSet()) {
					for (int i = 0; i < 6; i++) {
						if (this.canReceiveFrom(e, dirs[i])) {
							if (this.requestEnergy(e, this.getRemainingSpace(e), dirs[i]))
								break;
						}
					}
				}
				requestTimer = 200;
			}
			else {
				long time = world.getTotalWorldTime();
				if (lastRequestDecrTime != time) {
					requestTimer--;
				}
				lastRequestDecrTime = time;
			}
		}
	}

	protected abstract boolean canReceiveFrom(CrystalElement e, ForgeDirection dir);

	protected abstract ElementTagCompound getRequiredEnergy();

	private final boolean requestEnergy(CrystalElement e, int amt, ForgeDirection dir) {
		TileEntityRelaySource te = RelayNetworker.instance.findRelaySource(worldObj, xCoord, yCoord, zCoord, dir, e, amt, 128);
		if (te != null) {
			int has = te.getEnergy(e);
			int trans = Math.min(Math.min(amt, has), this.getMaxStorage(e)-energy.getValue(e));
			te.drainEnergy(e, trans);
			energy.addValueToColor(e, trans);
			return has >= amt;
		}
		return false;
	}
	/*
	private final boolean requestEnergy(ElementTagCompound tag, ForgeDirection dir) {
		boolean flag = true;
		for (CrystalElement e : tag.elementSet()) {
			flag = this.requestEnergy(e, tag.getValue(e), dir) && flag;
		}
		return flag;
	}
	 */
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

	@Override
	public void getTagsToWriteToStack(NBTTagCompound NBT) {
		energy.writeToNBT("energy", NBT);
	}

	@Override
	public void setDataFromItemStackTag(ItemStack is) {
		if (is.stackTagCompound == null)
			return;
		energy.readFromNBT("energy", is.stackTagCompound);
	}

}
