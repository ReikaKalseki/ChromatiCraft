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

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Auxiliary.CrystalNetworkLogger.FlowFail;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalNetworkTile;
import Reika.ChromatiCraft.Magic.Network.CrystalFlow;
import Reika.ChromatiCraft.Magic.Network.CrystalNetworker;
import Reika.ChromatiCraft.Magic.Network.CrystalPath;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class TileEntityCrystalBase extends TileEntityChromaticBase implements CrystalNetworkTile {

	public static final double DEFAULT_BEAM_RADIUS = 0.35;

	private UUID uniqueID = CrystalNetworker.instance.getNewUniqueID();

	private int bottleNeckDisplayTick = 0;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (bottleNeckDisplayTick > 0) {
			if (world.isRemote)
				this.doBottleneckDisplay();
			bottleNeckDisplayTick--;
		}
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		this.cachePosition();
	}

	@Override
	public final void cachePosition() {
		CrystalNetworker.instance.addTile(this);
	}

	public final void removeFromCache() {
		CrystalNetworker.instance.removeTile(this);
	}

	public final double getDistanceSqTo(double x, double y, double z) {
		double dx = x-xCoord;
		double dy = y-yCoord;
		double dz = z-zCoord;
		return dx*dx+dy*dy+dz*dz;
	}

	@Override
	public final World getWorld() {
		return worldObj;
	}

	@Override
	public final int getX() {
		return xCoord;
	}

	@Override
	public final int getY() {
		return yCoord;
	}

	@Override
	public final int getZ() {
		return zCoord;
	}

	@Override
	public int getUpdatePacketRadius() {
		return 512;
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		NBT.setString("netuid", uniqueID.toString());
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		if (NBT.hasKey("netuid"))
			uniqueID = UUID.fromString(NBT.getString("netuid"));
		else
			uniqueID = CrystalNetworker.instance.getNewUniqueID();
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setInteger("bottleneck", bottleNeckDisplayTick);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		bottleNeckDisplayTick = NBT.getInteger("bottleneck");
	}

	public final UUID getUniqueID() {
		return uniqueID;
	}

	public final UUID getPlacerUUID() {
		EntityPlayer ep = this.getPlacer();
		return ep != null ? ep.getUniqueID() : null;
	}

	public double getIncomingBeamRadius() {
		return DEFAULT_BEAM_RADIUS;
	}

	public double getOutgoingBeamRadius() {
		return DEFAULT_BEAM_RADIUS;
	}

	public void onPathCompleted(CrystalFlow p) {

	}

	public void onPathBroken(CrystalFlow p, FlowFail f) {

	}

	public void onPathConnected(CrystalPath p) {

	}

	/*
	public ResearchLevel getResearchTier() {
		return ResearchLevel.PYLONCRAFT;
	}
	 */

	public boolean canConductInterdimensionally() {
		return false;
	}

	public final void triggerBottleneckDisplay(int duration) {
		bottleNeckDisplayTick = duration;
		this.syncAllData(false);
	}

	public final boolean isDoingBottleneckDisplay() {
		return bottleNeckDisplayTick > 0;
	}

	@SideOnly(Side.CLIENT)
	protected void doBottleneckDisplay() {

	}

	public final boolean isRemoved() {
		return this.isInvalid();
	}

}
