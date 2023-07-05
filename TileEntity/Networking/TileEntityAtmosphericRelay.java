/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Networking;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import Reika.ChromatiCraft.Auxiliary.Interfaces.OwnedTile;
import Reika.ChromatiCraft.Auxiliary.Interfaces.SneakPop;
import Reika.ChromatiCraft.Base.TileEntity.CrystalTransmitterBase;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalReceiver;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalRepeater;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalSource;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalTransmitter;
import Reika.ChromatiCraft.Magic.Interfaces.PylonConnector;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;


public class TileEntityAtmosphericRelay extends CrystalTransmitterBase implements CrystalRepeater, SneakPop, OwnedTile, PylonConnector {

	private int depth;
	private boolean hasStructure = true;

	@Override
	public int receiveElement(CrystalSource src, CrystalElement e, int amt) {
		return 1;
	}

	@Override
	public int getReceiveRange() {
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean isConductingElement(CrystalElement e) {
		return hasStructure;
	}

	@Override
	public int maxThroughput() {
		return 40000;
	}

	@Override
	public int getSignalDegradation(boolean point) {
		return point ? 1000 : 20000;
	}

	@Override
	public boolean canConduct() {
		return hasStructure;
	}

	@Override
	public DecimalPosition getTargetRenderOffset(CrystalElement e) {
		return null;
	}

	@Override
	public int getSendRange() {
		return 16;
	}

	@Override
	public boolean needsLineOfSightToReceiver(CrystalReceiver r) {
		return true;
	}

	@Override
	public boolean canTransmitTo(CrystalReceiver r) {
		return true;
	}

	@Override
	public boolean canReceiveFrom(CrystalTransmitter r) {
		return r instanceof TileEntityCrystalPylon && ((TileEntityCrystalPylon)r).hasBroadcastUpgrade();
	}

	@Override
	public final void drop() {
		//ReikaItemHelper.dropItem(worldObj, xCoord+0.5, yCoord+0.5, zCoord+0.5, this.getTile().getCraftedProduct());
		ItemStack is = this.getTile().getCraftedProduct();
		//is.stackTagCompound = new NBTTagCompound();
		//this.getTagsToWriteToStack(is.stackTagCompound);
		ReikaItemHelper.dropItem(worldObj, xCoord+0.5, yCoord+0.5, zCoord+0.5, is);
		this.delete();
	}

	public final boolean canDrop(EntityPlayer ep) {
		return ep.getUniqueID().equals(placerUUID);
	}

	@Override
	public boolean allowMining(EntityPlayer ep) {
		return this.canDrop(ep);
	}

	@Override
	public int getSignalDepth(CrystalElement e) {
		return depth;
	}

	@Override
	public void setSignalDepth(CrystalElement e, int d) {
		depth = d;
	}

	@Override
	public boolean checkConnectivity() {
		return false;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.IONOSPHERIC;
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		hasStructure = NBT.getBoolean("multi");
		depth = NBT.getInteger("depth");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setBoolean("multi", hasStructure);
		NBT.setInteger("depth", depth);
	}

	@Override
	public int getPathPriority() {
		return Integer.MIN_VALUE;
	}

	@Override
	public boolean needsLineOfSightFromTransmitter(CrystalTransmitter r) {
		return false;
	}

	@Override
	public double getPylonRange() {
		return 8192;
	}

	@Override
	public int getThoughputBonus(boolean point) {
		return 0;
	}

	@Override
	public int getThoughputInsurance() {
		return 1000;
	}

	@Override
	public void getTagsToWriteToStack(NBTTagCompound NBT) {
		this.writeOwnerData(NBT);
	}

	@Override
	public void setDataFromItemStackTag(ItemStack is) {
		this.readOwnerData(is);
	}

	public boolean canBeSuppliedBy(CrystalSource te, CrystalElement e) {
		return true;
	}

	@Override
	public void addTooltipInfo(List li, boolean shift) {

	}

}
