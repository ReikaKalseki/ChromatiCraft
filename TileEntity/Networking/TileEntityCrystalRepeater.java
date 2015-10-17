/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Networking;

import java.util.HashSet;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.ImmutableTriple;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.CrystalNetworkLogger.FlowFail;
import Reika.ChromatiCraft.Auxiliary.Interfaces.NBTTile;
import Reika.ChromatiCraft.Auxiliary.Interfaces.OwnedTile;
import Reika.ChromatiCraft.Auxiliary.Interfaces.SneakPop;
import Reika.ChromatiCraft.Base.TileEntity.CrystalTransmitterBase;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalNetworkTile;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalReceiver;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalRepeater;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalSource;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalTransmitter;
import Reika.ChromatiCraft.Magic.Network.CrystalFlow;
import Reika.ChromatiCraft.Magic.Network.CrystalNetworker;
import Reika.ChromatiCraft.Magic.Network.PylonFinder;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityCrystalRepeater extends CrystalTransmitterBase implements CrystalRepeater, NBTTile, SneakPop, OwnedTile {

	protected ForgeDirection facing = ForgeDirection.DOWN;
	protected boolean hasMultiblock;
	private int depth = -1;
	private boolean isTurbo = false;

	protected int connectionRenderTick = 0;

	private HashSet<WorldLocation> connectableTiles;

	public static final int RANGE = 32;

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.REPEATER;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		if (connectionRenderTick > 0) {
			connectionRenderTick--;
			if (connectionRenderTick == 0) {
				connectableTiles = null;
			}
		}
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		super.onFirstTick(world, x, y, z);
		this.validateStructure();
		//this.checkConnectivity();
	}

	@Override
	public int getSendRange() {
		return RANGE;
	}

	@Override
	public int getReceiveRange() {
		return RANGE;
	}

	@Override
	public int getSignalDegradation() {
		return this.isTurbocharged() ? 0 : 5;
	}

	@Override
	public boolean canConduct() {
		return hasMultiblock;
	}

	public final void validateStructure() {
		hasMultiblock = this.checkForStructure();
		if (!hasMultiblock) {
			CrystalNetworker.instance.breakPaths(this);
		}
		this.syncAllData(false);
	}

	protected boolean checkForStructure() {
		ForgeDirection dir = facing;
		World world = worldObj;
		int x = xCoord;
		int y = yCoord;
		int z = zCoord;
		if (world.getBlock(x+dir.offsetX, y+dir.offsetY, z+dir.offsetZ) != ChromaBlocks.RUNE.getBlockInstance())
			return false;
		for (int i = 2; i < 4; i++) {
			int dx = x+dir.offsetX*i;
			int dy = y+dir.offsetY*i;
			int dz = z+dir.offsetZ*i;
			Block id = world.getBlock(dx, dy, dz);
			int meta = world.getBlockMetadata(dx, dy, dz);
			if (id != ChromaBlocks.PYLONSTRUCT.getBlockInstance() || meta != 0)
				return false;
		}
		return true;
	}

	public void redirect(int side) {
		facing = dirs[side].getOpposite();
		this.validateStructure();
	}

	public boolean findFirstValidSide() {
		for (int i = 0; i < 6; i++) {
			facing = dirs[i];
			this.validateStructure();
			if (hasMultiblock)
				return true;
		}
		return false;
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		facing = dirs[NBT.getInteger("face")];
		hasMultiblock = NBT.getBoolean("multi");
		depth = NBT.getInteger("depth");
		isTurbo = NBT.getBoolean("turbo");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		if (facing != null)
			NBT.setInteger("face", facing.ordinal());

		NBT.setBoolean("multi", hasMultiblock);
		NBT.setInteger("depth", depth);
		NBT.setBoolean("turbo", isTurbo);
	}

	public final boolean isTurbocharged() {
		return isTurbo;
	}

	@Override
	public int maxThroughput() {
		return this.isTurbocharged() ? 8000 : 1000;
	}

	@Override
	public boolean isConductingElement(CrystalElement e) {
		return e != null && e == this.getActiveColor();
	}

	@Override
	public final int receiveElement(CrystalElement e, int amt) {
		return 1;
	}

	@Override
	public ImmutableTriple<Double, Double, Double> getTargetRenderOffset(CrystalElement e) {
		return null;
	}

	public boolean checkConnectivity() {
		CrystalElement c = this.getActiveColor();
		return c != null && CrystalNetworker.instance.checkConnectivity(c, this);
	}

	public CrystalElement getActiveColor() {
		int dx = xCoord+facing.offsetX;
		int dy = yCoord+facing.offsetY;
		int dz = zCoord+facing.offsetZ;
		return this.canConduct() ? CrystalElement.elements[worldObj.getBlockMetadata(dx, dy, dz)] : null;
	}

	public CrystalSource getEnergySource() {
		CrystalElement e = this.getActiveColor();
		return e != null ? CrystalNetworker.instance.getConnectivity(e, this) : null;
	}

	public void onRelayPlayerCharge(EntityPlayer player, TileEntityCrystalPylon p) {
		if (!worldObj.isRemote) {
			if (!player.capabilities.isCreativeMode && !Chromabilities.PYLON.enabledOn(player) && rand.nextInt(60) == 0)
				p.attackEntityByProxy(player, this);
			CrystalNetworker.instance.makeRequest(this, p.getColor(), 100, this.getReceiveRange(), 1);
		}
	}

	@Override
	public boolean needsLineOfSight() {
		return true;
	}

	@Override
	public int getSignalDepth(CrystalElement e) {
		return depth;
	}

	@Override
	public void setSignalDepth(CrystalElement e, int d) {
		if (e == this.getActiveColor())
			depth = d;
	}

	@Override
	public void getTagsToWriteToStack(NBTTagCompound NBT) {
		NBT.setBoolean("boosted", isTurbo);
	}

	@Override
	public void setDataFromItemStackTag(ItemStack is) {
		isTurbo = ReikaItemHelper.matchStacks(is, this.getTile().getCraftedProduct()) && is.stackTagCompound != null && is.stackTagCompound.getBoolean("boosted");
	}

	@Override
	public final void onPathCompleted(CrystalFlow p) {

	}

	@Override
	public final void onPathBroken(CrystalFlow p, FlowFail f) {

	}

	@Override
	public final void drop() {
		//ReikaItemHelper.dropItem(worldObj, xCoord+0.5, yCoord+0.5, zCoord+0.5, this.getTile().getCraftedProduct());

		ItemStack is = this.getTile().getCraftedProduct();
		is.stackTagCompound = new NBTTagCompound();
		this.getTagsToWriteToStack(is.stackTagCompound);
		ReikaItemHelper.dropItem(worldObj, xCoord+0.5, yCoord+0.5, zCoord+0.5, is);
		this.delete();
	}

	public final boolean canDrop(EntityPlayer ep) {
		return ep.getUniqueID().equals(placerUUID);
	}

	@Override
	public boolean canTransmitTo(CrystalReceiver r) {
		return true;
	}

	public void triggerConnectionRender() {
		if (worldObj.isRemote) {
			connectionRenderTick = 100;
			//connectableTiles = this.getConnectableTilesForRender();
		}
		else {
			ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.REPEATERCONN.ordinal(), this, 128);
		}
	}

	public int getConnectionRenderAlpha() {
		return connectionRenderTick > 0 ? (connectionRenderTick > 10 ? 255 : 25*connectionRenderTick) : 0;
	}

	public HashSet<WorldLocation> getRenderedConnectableTiles() {
		return connectableTiles;
	}

	@SideOnly(Side.CLIENT)
	/** Does not use the crystal network since is clientside. */
	private final HashSet<WorldLocation> getConnectableTilesForRender() {
		HashSet<WorldLocation> c = new HashSet();
		int r = Math.max(this.getReceiveRange(), this.getSendRange());
		for (int i = -r; i <= r; i++) {
			for (int j = -r; j <= r; j++) {
				for (int k = -r; k <= r; k++) {
					boolean flag = false;
					TileEntity te = worldObj.getTileEntity(xCoord+i, yCoord+j, zCoord+k);
					if (te instanceof CrystalReceiver && ((CrystalNetworkTile)te).canConduct() && this.canTransmitTo((CrystalReceiver)te)) {
						flag = true;
					}
					if (te instanceof CrystalTransmitter && ((CrystalNetworkTile)te).canConduct() && ((CrystalTransmitter)te).canTransmitTo(this)) {
						flag = true;
					}
					if (flag) {
						if (this.getDistanceSqTo(te.xCoord, te.yCoord, te.zCoord) <= r*r) {
							if (!this.needsLineOfSight() || (PylonFinder.lineOfSight(worldObj, xCoord, yCoord, zCoord, te.xCoord, te.yCoord, te.zCoord))) {
								c.add(new WorldLocation(te));
							}
						}
					}
				}
			}
		}
		return c;
	}

	@Override
	public final AxisAlignedBB getRenderBoundingBox() {
		AxisAlignedBB def = super.getRenderBoundingBox();
		if (def == INFINITE_EXTENT_AABB)
			return def;
		if (connectionRenderTick > 0) {
			return INFINITE_EXTENT_AABB;
		}
		else if (isTurbo) {
			return ReikaAABBHelper.getBlockAABB(xCoord, yCoord, zCoord).expand(2, 2, 2);
		}
		else {
			return ReikaAABBHelper.getBlockAABB(xCoord, yCoord, zCoord).expand(0.5, 0.5, 0.5);
		}
	}

}
