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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Auxiliary.ChromaFX;
import Reika.ChromatiCraft.Magic.CrystalTarget;
import Reika.ChromatiCraft.Magic.CrystalTarget.TickingCrystalTarget;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalTransmitter;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;

public abstract class CrystalTransmitterBase extends TileEntityCrystalBase implements CrystalTransmitter {

	private ArrayList<CrystalTarget> targets = new ArrayList(); //need to reset some way
	private ArrayList<TickingCrystalTarget> tickingTargets = new ArrayList();

	public int renderAlpha;

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {
		if (renderAlpha > 0)
			renderAlpha -= 4;
		if (renderAlpha < 0)
			renderAlpha = 0;
	}

	@Override
	public final void addTarget(WorldLocation loc, CrystalElement e, double dx, double dy, double dz, double w) {
		CrystalTarget tg = new CrystalTarget(this, loc, e, dx, dy, dz, w);
		if (!worldObj.isRemote) {
			if (!targets.contains(tg))
				targets.add(tg);
			this.onTargetChanged();
		}
	}

	@Override
	public final void addSelfTickingTarget(WorldLocation loc, CrystalElement e, double dx, double dy, double dz, double w, int duration) {
		TickingCrystalTarget tg = new TickingCrystalTarget(this, loc, e, dx, dy, dz, w, duration);
		if (!worldObj.isRemote) {
			if (!targets.contains(tg)) {
				targets.add(tg);
				tickingTargets.add(tg);
			}
			this.onTargetChanged();
		}
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);
		if (!targets.isEmpty() && world.isRemote) {
			//this.spawnBeamParticles(world, x, y, z);
			ChromaFX.drawLeyLineParticles(world, x, y, z, this.getOutgoingBeamRadius(), targets);
		}
		this.tickTargets();
	}

	private void tickTargets() {
		if (!worldObj.isRemote && !tickingTargets.isEmpty()) {
			Iterator<TickingCrystalTarget> it = tickingTargets.iterator();
			while (it.hasNext()) {
				TickingCrystalTarget t = it.next();
				if (t.tick()) {
					it.remove();
					targets.remove(t);
					//ReikaJavaLibrary.pConsole("Removing "+t);
					this.syncAllData(true);
				}
			}
		}
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		super.onFirstTick(world, x, y, z);
		targets.clear();
	}
	/*
	@SideOnly(Side.CLIENT)
	private void spawnBeamParticles(World world, int x, int y, int z) {
		int p = Minecraft.getMinecraft().gameSettings.particleSetting;
		if (rand.nextInt(1+p*2) == 0) {
			for (CrystalTarget tg : targets) {
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
	}
	 */
	private void onTargetChanged() {
		renderAlpha = 512;
		this.syncAllData(true);
	}

	public final void removeTarget(WorldLocation loc, CrystalElement e) {
		if (!worldObj.isRemote) {
			//ReikaJavaLibrary.pConsole(this+":"+targets.size()+":"+targets);
			targets.remove(new CrystalTarget(this, loc, e, 0));
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
			CrystalTarget tg = CrystalTarget.readFromNBT("target"+i, NBT);
			if (tg != null)
				targets.add(tg);
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
	public AxisAlignedBB getRenderBoundingBox() {
		return !targets.isEmpty() ? INFINITE_EXTENT_AABB : super.getRenderBoundingBox();
	}

	@Override
	public double getMaxRenderDistanceSquared() {
		return super.getMaxRenderDistanceSquared()*16;
	}
}
