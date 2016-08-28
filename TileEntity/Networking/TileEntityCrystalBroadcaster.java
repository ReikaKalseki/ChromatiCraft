/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Networking;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Auxiliary.ChromaStructures;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalNetworkTile;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalReceiver;
import Reika.ChromatiCraft.Magic.Interfaces.NotifiedNetworkTile;
import Reika.ChromatiCraft.Magic.Network.CrystalNetworker;
import Reika.ChromatiCraft.Magic.Network.CrystalPath;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;


public class TileEntityCrystalBroadcaster extends TileEntityCrystalRepeater implements NotifiedNetworkTile {

	public static int INTERFERENCE_RANGE = 384;
	public static int MIN_RANGE = 512;
	public static int BROADCAST_RANGE = 4096;

	private boolean interfered;

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.BROADCAST;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		super.onFirstTick(world, x, y, z);
		this.validateStructure();
		this.checkInterfere();
		//this.checkConnectivity();
	}

	@Override
	public int getSendRange() {
		return BROADCAST_RANGE;
	}

	@Override
	public int getReceiveRange() {
		return BROADCAST_RANGE;
	}

	@Override
	public int getSignalDegradation() {
		return 2500;
	}

	@Override
	protected boolean checkForStructure() {
		return ChromaStructures.getBroadcastStructure(worldObj, xCoord, yCoord, zCoord).matchInWorld();
	}

	@Override
	public boolean canConduct() {
		if (!super.canConduct())
			return false;
		return !interfered;
	}

	private void checkInterfere() {
		interfered = CrystalNetworker.instance.getNearestTileOfType(this, this.getClass(), INTERFERENCE_RANGE) != null;
	}

	@Override
	public void redirect(int side) {
		//not redirectable
	}

	@Override
	public boolean isConductingElement(CrystalElement e) {
		return e != null;
	}

	@Override
	public int maxThroughput() {
		return 100000;
	}

	@Override
	public void onPathConnected(CrystalPath p) {

	}

	@Override
	public boolean canTransmitTo(CrystalReceiver r) {
		if (r instanceof TileEntityCrystalBroadcaster) {
			return r.getDistanceSqTo(xCoord, yCoord, zCoord) >= MIN_RANGE*MIN_RANGE;
		}
		else {
			return true;
		}
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		interfered = NBT.getBoolean("interf");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setBoolean("interf", interfered);
	}

	@Override
	public void onTileNetworkTopologyChange(CrystalNetworkTile te, boolean remove) {
		if (te instanceof TileEntityCrystalBroadcaster)
			this.checkInterfere();
	}

	@Override
	public float getFailureWeight(CrystalElement e) {
		return 1.125F;
	}

	@Override
	public int getPathPriority() {
		return -50;
	}

}
