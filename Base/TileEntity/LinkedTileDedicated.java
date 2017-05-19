/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base.TileEntity;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Auxiliary.Interfaces.LinkedTile;
import Reika.ChromatiCraft.Auxiliary.Interfaces.SneakPop;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.DragonAPI.Base.BlockTEBase;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;


public abstract class LinkedTileDedicated extends TileEntity implements SneakPop, LinkedTile {

	private WorldLocation target;
	private boolean shouldDrop;
	private boolean primary;

	@Override
	public final boolean canUpdate() {
		return true;
	}

	@Override
	public void updateEntity() {
		if (shouldDrop) {
			ReikaItemHelper.dropItem(worldObj, xCoord+0.5, yCoord+0.5, zCoord+0.5, this.getDrop());
			worldObj.setBlockToAir(xCoord, yCoord, zCoord);
		}
	}

	protected abstract ItemStack getDrop();

	public final void drop() {
		shouldDrop = true;
		LinkedTile te = this.getOther();
		if (te != null)
			te.markForDrop();
		this.reset();
	}

	public final boolean isLinked() {
		return target != null;
	}

	public abstract boolean canLinkTo(World world, int x, int y, int z);

	public final boolean linkTo(World world, int x, int y, int z) {
		if (!world.isRemote && this.canLinkTo(world, x, y, z)) {
			this.resetOther();
			target = new WorldLocation(world, x, y, z);
			this.createRandomLinkID();
			LinkedTile te = this.getOther();
			te.setTarget(new WorldLocation(worldObj, xCoord, yCoord, zCoord));
			te.assignLinkID(this);
			this.onLink(true);
			this.onLinkTo(world, x, y, z, te);
			return true;
		}
		return false;
	}

	protected abstract void createRandomLinkID();

	public abstract void assignLinkID(LinkedTile other);

	protected void onLinkTo(World world, int x, int y, int z, LinkedTile te) {

	}

	public final void onLink(boolean other) {
		ChromaSounds.RIFT.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord);
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			int dx = xCoord+dir.offsetX;
			int dy = yCoord+dir.offsetY;
			int dz = zCoord+dir.offsetZ;
			Block b = worldObj.getBlock(dx, dy, dz);
			if (b instanceof BlockTEBase) {
				((BlockTEBase)b).updateTileCache(worldObj, dx, dy, dz);
			}
		}
		if (other && this.isLinked()) {
			this.getOther().onLink(false);
		}
	}

	public final boolean linkTo(WorldLocation loc) {
		return !loc.equals(worldObj, xCoord, yCoord, zCoord) && this.linkTo(loc.getWorld(), loc.xCoord, loc.yCoord, loc.zCoord);
	}

	public final void reset() {
		this.resetOther();
		target = null;
		this.onReset();
		this.onLink(true);
	}

	public final void resetOther() {
		if (this.isLinked()) {
			LinkedTile te = this.getOther();
			if (te != null) {
				te.setTarget(null);
				this.onResetOther(te);
				this.onLink(true);
			}
		}
	}

	protected void onReset() {

	}

	protected void onResetOther(LinkedTile te) {

	}

	private LinkedTile getOther() {
		return this.isLinked() ? ((LinkedTile)target.getTileEntity()) : null;
	}

	public final WorldLocation getLinkTarget() {
		return target;
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		if (this.isLinked())
			target.writeToNBT("target", NBT);

		NBT.setBoolean("primary", primary);
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		if (NBT.hasKey("target"))
			target = WorldLocation.readFromNBT("target", NBT);

		primary = NBT.getBoolean("primary");
	}

	public final boolean isPrimary() {
		return primary;
	}

	public final void setPrimary() {
		primary = true;
		this.syncAllData(false);
		LinkedTile te = this.getOther();
		if (te != null) {
			te.setPrimary(false);
			te.syncAllData(false);
		}
	}

	public final void setPrimary(boolean flag) {
		if (flag)
			this.setPrimary();
		else
			primary = flag;
	}

	public final void markForDrop() {
		shouldDrop = true;
	}

	@Override
	public final void setTarget(WorldLocation loc) {
		target = loc;
	}

	@Override
	public final Packet getDescriptionPacket() {
		NBTTagCompound NBT = new NBTTagCompound();
		this.writeToNBT(NBT);
		S35PacketUpdateTileEntity pack = new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, NBT);
		return pack;
	}

	@Override
	public final void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity p)  {
		this.readFromNBT(p.field_148860_e);
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

}
