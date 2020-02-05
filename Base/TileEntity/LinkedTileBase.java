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

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Auxiliary.Interfaces.LinkedTile;
import Reika.ChromatiCraft.Auxiliary.Interfaces.SneakPop;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.DragonAPI.Base.BlockTEBase;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;


public abstract class LinkedTileBase extends TileEntityChromaticBase implements SneakPop, LinkedTile {

	private WorldLocation target;
	private boolean shouldDrop;
	private boolean primary;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (shouldDrop) {
			EntityItem ei = ReikaItemHelper.dropItem(world, x+0.5, y+0.5, z+0.5, this.getTile().getCraftedProduct());
			ei.lifespan = Integer.MAX_VALUE;
			this.delete();
		}
	}

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
			ForgeDirection dir = dirs[i];
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
		this.triggerBlockUpdate();
		ReikaWorldHelper.causeAdjacentUpdates(worldObj, xCoord, yCoord, zCoord);
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
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		if (this.isLinked())
			target.writeToNBT("target", NBT);

		NBT.setBoolean("primary", primary);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

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

}
