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

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Auxiliary.CrystalNetworkLogger.FlowFail;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalNetworkTile;
import Reika.ChromatiCraft.Magic.Network.CrystalFlow;
import Reika.ChromatiCraft.Magic.Network.CrystalNetworker;
import Reika.ChromatiCraft.Magic.Network.CrystalPath;
import Reika.ChromatiCraft.Registry.ChromaResearchManager.ResearchLevel;

public abstract class TileEntityCrystalBase extends TileEntityChromaticBase implements CrystalNetworkTile {

	private UUID uniqueID = CrystalNetworker.instance.getNewUniqueID();

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

	public final UUID getUniqueID() {
		return uniqueID;
	}

	public final UUID getPlacerUUID() {
		EntityPlayer ep = this.getPlacer();
		return ep != null ? ep.getUniqueID() : null;
	}

	public double getIncomingBeamRadius() {
		return 0.35;
	}

	public double getOutgoingBeamRadius() {
		return 0.35;
	}

	public void onPathCompleted(CrystalFlow p) {

	}

	public void onPathBroken(CrystalFlow p, FlowFail f) {

	}

	public void onPathConnected(CrystalPath p) {

	}

	public ResearchLevel getResearchTier() {
		return ResearchLevel.PYLONCRAFT;
	}

}
