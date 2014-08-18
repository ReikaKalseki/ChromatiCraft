/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base.TileEntity;

import Reika.ChromatiCraft.Magic.CrystalNetworkTile;
import Reika.ChromatiCraft.Magic.CrystalNetworker;
import Reika.ChromatiCraft.Magic.CrystalTarget;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.WorldLocation;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public abstract class TileEntityCrystalTile extends TileEntityChromaticBase implements CrystalNetworkTile {

	private CrystalTarget target; //need to reset some way
	public int renderAlpha;

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {
		if (renderAlpha > 0)
			renderAlpha -= 4;
		if (renderAlpha < 0)
			renderAlpha = 0;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (this.getTicksExisted() == 0) {
			this.cachePosition();
		}
		if (target != null && world.isRemote) {
			//this.spawnParticle();
		}
	}
	/*
	private void spawnParticle() {
		double dd = target.getDistanceTo(xCoord, yCoord, zCoord);
		double vx = (target.xCoord-xCoord)/dd;
		double vy = (target.yCoord-yCoord)/dd;
		double vz = (target.zCoord-zCoord)/dd;
		ForgeDirection dir = dirs[rand.nextInt(6)];
		WorldLocation loc = new WorldLocation(this);
		int t = 5;
		int ang = (this.getTicksExisted()*t)%360;
		float r = 0.3F;

		for (int i = 0; i < 360; i += 90) {
			float rx = (float)(r*Math.sin(Math.toRadians(ang+i))*Math.abs(vx));
			float ry = r*(float)Math.cos(Math.toRadians(ang+i));
			float rz = (float)(r*Math.sin(Math.toRadians(ang+i))*Math.abs(vz));
			Minecraft.getMinecraft().effectRenderer.addEffect(new EntityFlareFX(color, worldObj, loc, target, rx, ry, rz));
		}
	}*/

	@Override
	public void markTarget(WorldLocation loc, CrystalElement e) {
		target = new CrystalTarget(loc, e);
		this.onTargetChanged();
	}

	private void onTargetChanged() {
		renderAlpha = 512;
		this.syncAllData(true);
	}

	public void clearTarget() {
		target = null;
		this.onTargetChanged();
	}

	public CrystalTarget getTarget() {
		return target;
	}
	/*
	@Override
	public void markSource(WorldLocation loc) {

	}*/

	@Override
	public final void cachePosition() {
		CrystalNetworker.instance.addTile(this);
	}

	public void removeFromCache() {
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
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		target = CrystalTarget.readFromNBT("target", NBT);

		renderAlpha = NBT.getInteger("alpha");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		if (target != null)
			target.writeToNBT("target", NBT);

		NBT.setInteger("alpha", renderAlpha);
	}

	@Override
	public final AxisAlignedBB getRenderBoundingBox() {
		return target != null ? INFINITE_EXTENT_AABB : super.getRenderBoundingBox();
	}

}
