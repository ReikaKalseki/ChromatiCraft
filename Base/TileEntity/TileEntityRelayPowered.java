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

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Auxiliary.Interfaces.NBTTile;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.Interfaces.LumenTile;
import Reika.ChromatiCraft.Magic.Network.RelayNetworker;
import Reika.ChromatiCraft.Magic.Progression.ProgressStage;
import Reika.ChromatiCraft.Magic.Progression.ProgressionCatchupHandling;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.AOE.Effect.TileEntityEfficiencyUpgrade;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityRelaySource;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Interfaces.TileEntity.AdjacentUpdateWatcher;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class TileEntityRelayPowered extends TileEntityChromaticBase implements LumenTile, NBTTile, AdjacentUpdateWatcher {

	protected final ElementTagCompound energy = new ElementTagCompound();

	private int requestTimer = rand.nextInt(200);

	private long lastRequestDecrTime = -1;

	private int efficiencyBoost;

	public final void onAdjacentUpdate(World world, int x, int y, int z, Block b) {
		this.calcEfficiency();
		this.syncAllData(false);
	}

	public int getEfficiencyBoost() {
		return efficiencyBoost;
	}

	protected final float getEnergyCostScale() {
		float f = 1;
		int e = this.getEfficiencyBoost();
		if (e > 0)
			f *= TileEntityEfficiencyUpgrade.getCostFactor(e-1);
		return f;
	}

	private void calcEfficiency() {
		efficiencyBoost = TileEntityAdjacencyUpgrade.getAdjacentUpgrade(this, CrystalElement.BLACK);
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (DragonAPICore.debugtest && !world.isRemote) {
			CrystalElement e = CrystalElement.randomElement();
			if (this.isAcceptingColor(e))
				energy.addValueToColor(e, 500);
		}

		if (!world.isRemote && this.makeRequests()) {
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

	protected boolean makeRequests() {
		return true;
	}

	protected abstract boolean canReceiveFrom(CrystalElement e, ForgeDirection dir);

	public abstract ElementTagCompound getRequiredEnergy();

	private final boolean requestEnergy(CrystalElement e, int amt, ForgeDirection dir) {
		TileEntityRelaySource te = RelayNetworker.instance.findRelaySource(worldObj, xCoord, yCoord, zCoord, dir, e, amt, 128);
		if (te != null) {
			int has = te.getEnergy(e);
			int trans = Math.min(Math.min(amt, has), this.getMaxStorage(e)-energy.getValue(e));
			te.drainEnergy(e, trans);
			te.onDrain(e, trans);
			energy.addValueToColor(e, trans);
			ProgressStage.RELAYS.stepPlayerTo(this.getPlacer());
			if (worldObj.isRemote) {
				ProgressionCatchupHandling.instance.attemptSync(this, 8, ProgressStage.RELAYS, true);
			}
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

		efficiencyBoost = NBT.getInteger("eff");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		energy.writeToNBT("energy", NBT);

		NBT.setInteger("eff", efficiencyBoost);
	}

	protected final void drainEnergy(CrystalElement e, int amt) {
		if (this.allowsEfficiencyBoost())
			amt = (int)Math.max(1, amt*this.getEnergyCostScale());
		energy.subtract(e, amt);
	}

	protected final void drainEnergy(ElementTagCompound tag) {
		if (this.allowsEfficiencyBoost()) {
			tag = tag.copy();
			tag.scale(this.getEnergyCostScale());
		}
		energy.subtract(tag);
	}

	protected boolean allowsEfficiencyBoost() {
		return true;
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
		this.writeOwnerData(NBT);
		energy.writeToNBT("energy", NBT);
	}

	@Override
	public void setDataFromItemStackTag(ItemStack is) {
		this.readOwnerData(is);
		if (is.stackTagCompound == null)
			return;
		energy.readFromNBT("energy", is.stackTagCompound);
	}

	@Override
	public void addTooltipInfo(List li, boolean shift) {

	}

}
