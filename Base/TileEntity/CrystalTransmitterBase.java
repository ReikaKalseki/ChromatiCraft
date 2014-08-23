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
import Reika.ChromatiCraft.Magic.CrystalTarget;
import Reika.ChromatiCraft.Magic.CrystalTransmitter;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.WorldLocation;

import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public abstract class CrystalTransmitterBase extends TileEntityCrystalBase implements CrystalTransmitter {

	private ArrayList<CrystalTarget> targets = new ArrayList(); //need to reset some way
	public int renderAlpha;

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {
		if (renderAlpha > 0)
			renderAlpha -= 4;
		if (renderAlpha < 0)
			renderAlpha = 0;
	}

	@Override
	public final void addTarget(WorldLocation loc, CrystalElement e) {
		CrystalTarget tg = new CrystalTarget(loc, e);
		if (!targets.contains(tg))
			targets.add(tg);
		this.onTargetChanged();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);
		if (this.getTicksExisted() == 0) {
			Iterator<CrystalTarget> it = targets.iterator();
			while (it.hasNext()) {
				CrystalTarget c = it.next();
				if (!(c.location.getTileEntity() instanceof CrystalNetworkTile))
					it.remove();
			}
		}
		if (!targets.isEmpty() && world.isRemote) {
			//this.spawnParticle();
		}
	}

	private void onTargetChanged() {
		renderAlpha = 512;
		this.syncAllData(true);
	}

	@Override
	public final int getUpdatePacketRadius() {
		return 128;
	}

	public final void removeTarget(WorldLocation loc, CrystalElement e) {
		//ReikaJavaLibrary.pConsole(this+":"+targets.size()+":"+targets);
		targets.remove(new CrystalTarget(loc, e));
		this.onTargetChanged();
		//ReikaJavaLibrary.pConsole(this+":"+targets.size()+":"+targets);
	}


	public final void clearTargets() {
		targets.clear();
		this.onTargetChanged();
	}

	public final ArrayList<CrystalTarget> getTargets() {
		ArrayList<CrystalTarget> li = new ArrayList();
		li.addAll(targets);
		return li;
	}
	/*
	private void spawnParticle(World world, int x, int y, int z) {
		double dd = target.getDistanceTo(x, y, z);
		double vx = (target.xCoord-x)/dd;
		double vy = (target.yCoord-y)/dd;
		double vz = (target.zCoord-z)/dd;
		ForgeDirection dir = dirs[rand.nextInt(6)];
		WorldLocation loc = new WorldLocation(this);
		int t = 5;
		int ang = (this.getTicksExisted()*t)%360;
		float r = 0.3F;

		for (int i = 0; i < 360; i += 90) {
			float rx = (float)(r*Math.sin(Math.toRadians(ang+i))*Math.abs(vx));
			float ry = r*(float)Math.cos(Math.toRadians(ang+i));
			float rz = (float)(r*Math.sin(Math.toRadians(ang+i))*Math.abs(vz));
			Minecraft.getMinecraft().effectRenderer.addEffect(new EntityFlareFX(color, world, loc, target, rx, ry, rz));
		}
	}*/

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		targets = new ArrayList();
		int num = NBT.getInteger("targetcount");
		for (int i = 0; i < num; i++) {
			targets.add(CrystalTarget.readFromNBT("target"+i, NBT));
		}

		renderAlpha = NBT.getInteger("alpha");
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		NBT.setInteger("targetcount", targets.size());
		for (int i = 0; i < targets.size(); i++)
			targets.get(i).writeToNBT("target"+i, NBT);

		NBT.setInteger("alpha", renderAlpha);
	}

	@Override
	public final AxisAlignedBB getRenderBoundingBox() {
		return !targets.isEmpty() ? INFINITE_EXTENT_AABB : super.getRenderBoundingBox();
	}

	public double getMaxRenderDistance() {
		return super.getMaxRenderDistanceSquared()*8;
	}
}
