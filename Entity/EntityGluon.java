/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityGluon extends Entity implements IEntityAdditionalSpawnData {

	private double targetX;
	private double targetY;
	private double targetZ;

	private double targetVX;
	private double targetVY;
	private double targetVZ;

	public EntityGluon(World world) {
		super(world);
	}

	public EntityGluon(EntityBallLightning src1, EntityBallLightning src2) {
		this(src1.worldObj);
		this.setPosition(src1.posX, src1.posY, src1.posZ);

		/*
		double dx = src2.posX-src1.posX;
		double dy = src2.posY-src1.posY;
		double dz = src2.posZ-src1.posZ;
		double dd = ReikaMathLibrary.py3d(dx, dy, dz);

		motionX = dx/dd;
		motionY = dy/dd;
		motionZ = dz/dd;
		 */

		//targetID = src2;

		targetX = src2.posX;
		targetY = src2.posY;
		targetZ = src2.posZ;

		targetVX = src2.motionX;
		targetVY = src2.motionY;
		targetVZ = src2.motionZ;
		//ReikaJavaLibrary.pConsole("from "+src1+" to "+src2);
	}

	@Override
	protected void entityInit() {

	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		/*
		if (targetID == null || this.isComplete()) {
			this.setDead();
		}
		else if (ticksExisted%4 == 0) {
			this.addBoltElement();
		}*/

		targetX += targetVX;
		targetY += targetVY;
		targetZ += targetVZ;

		if (ticksExisted > 5) {
			this.setDead();
		}

		//this.moveEntity(motionX, motionY, motionZ);
	}

	public double getTargetX() {
		return targetX;
	}

	public double getTargetY() {
		return targetY;
	}

	public double getTargetZ() {
		return targetZ;
	}

	/*
	private void addBoltElement() {
		double tx = targetID.posX;
		double ty = targetID.posY;
		double tz = targetID.posZ;

		double dx = tx-posX;
		double dy = ty-posY;
		double dz = tz-posZ;
		double dd = ReikaMathLibrary.py3d(dx, dy, dz);

		SphericalVector v = SphericalVector.fromCartesian(dx, dy, dz);

		if (v.magnitude > 6) {
			v.inclination = ReikaRandomHelper.getRandomPlusMinus(v.inclination, 10);
			v.rotation = ReikaRandomHelper.getRandomPlusMinus(v.rotation, 10);
		}

		v.magnitude = Math.min(v.magnitude, 2+rand.nextDouble()*4);

		Vec3 last = this.getBoltTerminusPos();
		bolt.add(Vec3.createVectorHelper(last.xCoord+v.getXProjection(), last.yCoord+v.getYProjection(), last.zCoord+v.getZProjection()));
	}

	private boolean isComplete() {
		return !bolt.isEmpty() && this.getBoltTerminusPos().distanceTo(Vec3.createVectorHelper(targetID.posX, targetID.posY, targetID.posZ)) < 0.125;
	}

	private Vec3 getBoltTerminusPos() {
		/* relative
		double dx = 0;
		double dy = 0;
		double dz = 0;
		for (Vec3 v : bolt) {
			dx += v.xCoord;
			dy += v.yCoord;
			dz += v.zCoord;
		}
		return Vec3.createVectorHelper(dx, dy, dz);
	 *//*

	return bolt.isEmpty() ? Vec3.createVectorHelper(0, 0, 0) : bolt.get(bolt.size()-1);
}

public List<Vec3> getBolt() {
	return Collections.unmodifiableList(bolt);
}
	  */
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {

	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {

	}

	@Override
	public void writeSpawnData(ByteBuf buf) {
		//buf.writeInt(targetID != null ? targetID.getEntityId() : -1);
		buf.writeDouble(targetX);
		buf.writeDouble(targetY);
		buf.writeDouble(targetZ);
		buf.writeDouble(targetVX);
		buf.writeDouble(targetVY);
		buf.writeDouble(targetVZ);
	}

	@Override
	public void readSpawnData(ByteBuf buf) {
		//targetID = worldObj.getEntityByID(buf.readInt());
		targetX = buf.readDouble();
		targetY = buf.readDouble();
		targetZ = buf.readDouble();
		targetVX = buf.readDouble();
		targetVY = buf.readDouble();
		targetVZ = buf.readDouble();
	}

}
