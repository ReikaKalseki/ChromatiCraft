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

import java.util.Collection;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Auxiliary.ChromaStructures;
import Reika.ChromatiCraft.Auxiliary.Interfaces.MultiBlockChromaTile;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalNetworkTile;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalReceiver;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalTransmitter;
import Reika.ChromatiCraft.Magic.Interfaces.NotifiedNetworkTile;
import Reika.ChromatiCraft.Magic.Network.CrystalNetworker;
import Reika.ChromatiCraft.Magic.Network.CrystalPath;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Interfaces.TileEntity.BreakAction;


public class TileEntityCrystalBroadcaster extends TileEntityCrystalRepeater implements NotifiedNetworkTile, MultiBlockChromaTile, BreakAction {

	public static final int INTERFERENCE_RANGE = 384;
	public static final int MIN_RANGE = 512;
	public static final int BROADCAST_RANGE = 4096;

	private static final int AIR_SEARCH = 24;
	private static final int AIR_SEARCH_Y = 4;

	private WorldLocation interference;
	private boolean clearAir;

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
		clearAir = !world.isRemote && this.testAirClear();
		//this.checkConnectivity();
	}

	@Override
	public boolean needsLineOfSightToReceiver(CrystalReceiver r) {
		return !(r instanceof TileEntityCrystalBroadcaster);
	}

	@Override
	public boolean needsLineOfSightFromTransmitter(CrystalTransmitter r) {
		return !(r instanceof TileEntityCrystalBroadcaster);
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
	protected boolean checkForStructure() {
		return ChromaStructures.getBroadcastStructure(worldObj, xCoord, yCoord, zCoord).matchInWorld();
	}

	@Override
	public boolean canConduct() {
		if (!super.canConduct())
			return false;
		return interference == null && clearAir;
	}

	private boolean testAirClear() {
		int r = 8;
		int dd = 1;
		int c = 4;
		for (int i = 2; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			for (int d = 1; d <= r; d += dd) {
				int dx = xCoord+d*dir.offsetX;
				int dz = zCoord+d*dir.offsetZ;
				if (!worldObj.getBlock(dx, yCoord, dz).isAir(worldObj, dx, yCoord, dz)) {
					c--;
					break;
				}
			}
		}
		if (c < 2)
			return false;

		r = AIR_SEARCH;
		int ry = AIR_SEARCH_Y;
		int c1 = 0;
		int c2 = 0;
		for (int i = -r; i <= r; i++) {
			for (int j = -ry; j <= ry; j++) {
				for (int k = -r; k <= r; k++) {
					if (Math.abs(i) > 1 || Math.abs(k) > 1) {
						int dx = xCoord+i;
						int dy = yCoord+j;
						int dz = zCoord+k;
						c2++;
						if (worldObj.getBlock(dx, dy, dz).isAir(worldObj, dx, dy, dz))
							c1++;
					}
				}
			}
		}
		//ReikaJavaLibrary.pConsole(c1+"/"+c2);
		return (float)c1/c2 > 0.8;
	}

	private void checkInterfere() {
		TileEntityCrystalBroadcaster te = CrystalNetworker.instance.getNearestTileOfType(this, this.getClass(), INTERFERENCE_RANGE);
		if (te != null) {
			interference = new WorldLocation(te);
			te.interference = new WorldLocation(this);
		}
		else {
			interference = null;
		}
	}

	public static void updateAirCaches(World world, int x, int y, int z) {
		Collection<TileEntityCrystalBroadcaster> c = CrystalNetworker.instance.getNearTilesOfType(world, x, y, z, TileEntityCrystalBroadcaster.class, AIR_SEARCH);
		for (TileEntityCrystalBroadcaster te : c) {
			if (Math.abs(te.yCoord-y) <= AIR_SEARCH_Y && Math.abs(te.xCoord-x) <= AIR_SEARCH && Math.abs(te.zCoord-z) <= AIR_SEARCH) {
				te.clearAir = te.testAirClear();
				te.syncAllData(true);
			}
		}
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
		return this.isTurbocharged() ? 250000 : 100000;
	}

	@Override
	public int getSignalDegradation() {
		int base = this.isTurbocharged() ? 1500 : 3000;
		if (worldObj.isRaining())
			base *= 4;
		return base;
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

		interference = WorldLocation.readFromNBT("interfere", NBT);
		clearAir = NBT.getBoolean("air");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		if (interference != null)
			interference.writeToNBT("interfere", NBT);
		NBT.setBoolean("air", clearAir);
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

	@Override
	public int getThoughputInsurance() {
		return this.isTurbocharged() ? 1000 : 500;
	}

	@Override
	public void breakBlock() {
		if (interference != null) {
			TileEntity te = interference.getTileEntity();
			if (te instanceof TileEntityCrystalBroadcaster) {
				TileEntityCrystalBroadcaster tb = (TileEntityCrystalBroadcaster)te;
				if (tb.interference != null && tb.interference.equals(worldObj, xCoord, yCoord, zCoord)) {
					tb.interference = null;
				}
			}
		}
	}

}
