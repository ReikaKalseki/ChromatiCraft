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
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityAdjacencyUpgrade.AdjacencyCheckHandlerImpl;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.Interfaces.LumenConsumer;
import Reika.ChromatiCraft.Magic.Interfaces.WirelessSource;
import Reika.ChromatiCraft.Magic.Network.CrystalNetworker;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityAuraPoint;
import Reika.ChromatiCraft.TileEntity.AOE.Effect.TileEntityEfficiencyUpgrade;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public abstract class TileEntityWirelessPowered extends TileEntityChromaticBase implements LumenConsumer {

	private static AdjacencyCheckHandlerImpl adjacency;

	public static void loadAdjacencyHandler() {
		TileEntityAdjacencyUpgrade.getOrCreateAdjacencyCheckHandler(CrystalElement.BLACK, null);
	}

	protected final ElementTagCompound energy = new ElementTagCompound();

	private int efficiencyBoost;

	public final void onAdjacentUpdate(World world, int x, int y, int z, Block b) {
		this.calcEfficiency();
		this.syncAllData(false);
	}

	public final int getEfficiencyBoost() {
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
		efficiencyBoost = adjacency.getAdjacentUpgrade(this);
	}

	protected final boolean requestEnergy(CrystalElement e, int amt) {
		if (DragonAPICore.debugtest && !worldObj.isRemote) {
			energy.addValueToColor(e, amt);
		}
		int r = this.getReceiveRange(e);
		if (TileEntityAuraPoint.hasAuraPoints(placerUUID))
			r *= 1.5;
		Collection<WirelessSource> c = CrystalNetworker.instance.getNearTilesOfType(worldObj, xCoord, yCoord, zCoord, WirelessSource.class, r);
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

	public boolean allowsEfficiencyBoost() {
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
