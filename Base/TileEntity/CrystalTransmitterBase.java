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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Magic.CrystalTarget;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalTransmitter;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityLaserFX;
import Reika.DragonAPI.Instantiable.Data.WorldLocation;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
	public final void addTarget(WorldLocation loc, CrystalElement e, double dx, double dy, double dz) {
		CrystalTarget tg = new CrystalTarget(loc, e, dx, dy, dz);
		if (!worldObj.isRemote) {
			if (!targets.contains(tg))
				targets.add(tg);
			this.onTargetChanged();
		}
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (!targets.isEmpty() && world.isRemote) {
			this.spawnBeamParticles(world, x, y, z);
		}
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		super.onFirstTick(world, x, y, z);
		targets.clear();
	}

	@SideOnly(Side.CLIENT)
	private void spawnBeamParticles(World world, int x, int y, int z) {
		for (int i = 0; i < targets.size(); i++) {
			CrystalTarget tg = targets.get(i);
			double dx = tg.location.xCoord+tg.offsetX-x;
			double dy = tg.location.yCoord+tg.offsetY-y;
			double dz = tg.location.zCoord+tg.offsetZ-z;
			double dd = ReikaMathLibrary.py3d(dx, dy, dz);
			double dr = rand.nextDouble();
			double px = dx*dr+x+0.5;
			double py = dy*dr+y+0.5;
			double pz = dz*dr+z+0.5;
			EntityLaserFX fx = new EntityLaserFX(tg.color, world, px, py, pz).setScale(15);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	private void onTargetChanged() {
		renderAlpha = 512;
		this.syncAllData(true);
	}

	public final void removeTarget(WorldLocation loc, CrystalElement e) {
		if (!worldObj.isRemote) {
			//ReikaJavaLibrary.pConsole(this+":"+targets.size()+":"+targets);
			targets.remove(new CrystalTarget(loc, e));
			this.onTargetChanged();
			//ReikaJavaLibrary.pConsole(this+":"+targets.size()+":"+targets);
		}
	}

	public final void clearTargets(boolean unload) {
		if (!worldObj.isRemote) {
			targets.clear();
			if (!unload)
				this.onTargetChanged();
		}
	}

	public final Collection<CrystalTarget> getTargets() {
		return Collections.unmodifiableCollection(targets);
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

	@Override
	public double getMaxRenderDistanceSquared() {
		return super.getMaxRenderDistanceSquared()*16;
	}
}
