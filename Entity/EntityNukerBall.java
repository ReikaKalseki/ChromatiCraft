/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

public class EntityNukerBall extends EntityFireball {

	private EntityPlayer firingPlayer;

	public EntityNukerBall(World world, EntityPlayer ep, double v, double phi, double theta) {
		super(world);
		firingPlayer = ep;

		this.setLocationAndAngles(ep.posX, ep.posY+1.62, ep.posZ, 0, 0);

		double[] xyz = ReikaPhysicsHelper.polarToCartesian(v, theta, phi);
		motionX = xyz[0];
		motionY = xyz[1];
		motionZ = xyz[2];

		accelerationX = 0;
		accelerationY = 0;
		accelerationZ = 0;

		this.setSize(0.125F, 0.125F);
	}

	public EntityNukerBall(World world) {
		super(world);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
	}

	@Override
	public void onUpdate()
	{
		double mx = motionX;
		double my = motionY;
		double mz = motionZ;

		if (!worldObj.isRemote && (shootingEntity != null && shootingEntity.isDead || !worldObj.blockExists((int)posX, (int)posY, (int)posZ)))
		{
			this.setDead();
		}
		else
		{
			this.onEntityUpdate();

			Vec3 vec3 = Vec3.createVectorHelper(posX, posY, posZ);
			Vec3 vec31 = Vec3.createVectorHelper(posX + motionX, posY + motionY, posZ + motionZ);
			MovingObjectPosition movingobjectposition = worldObj.rayTraceBlocks(vec3, vec31);
			vec3 = Vec3.createVectorHelper(posX, posY, posZ);
			vec31 = Vec3.createVectorHelper(posX + motionX, posY + motionY, posZ + motionZ);

			if (movingobjectposition != null)
			{
				vec31 = Vec3.createVectorHelper(movingobjectposition.hitVec.xCoord, movingobjectposition.hitVec.yCoord, movingobjectposition.hitVec.zCoord);
			}

			if (movingobjectposition != null)
			{
				this.onImpact(movingobjectposition);
			}

			posX += motionX;
			posY += motionY;
			posZ += motionZ;

			float f2 = this.getMotionFactor();

			if (this.isInWater())
			{
				for (int j = 0; j < 4; ++j)
				{
					float f3 = 0.25F;
					worldObj.spawnParticle("bubble", posX - motionX * f3, posY - motionY * f3, posZ - motionZ * f3, motionX, motionY, motionZ);
				}

				f2 = 0.8F;
			}

			motionX += accelerationX;
			motionY += accelerationY;
			motionZ += accelerationZ;
			motionX *= f2;
			motionY *= f2;
			motionZ *= f2;
			this.setPosition(posX, posY, posZ);
		}

		motionX = mx;
		motionY = my;
		motionZ = mz;

		if (!worldObj.isRemote && (firingPlayer == null || ticksExisted > 10)) {
			this.destroy();
		}
	}

	@Override
	protected void onImpact(MovingObjectPosition mov) {
		if (firingPlayer == null && !worldObj.isRemote) {
			this.destroy();
			return;
		}
		if (!worldObj.isRemote) {
			if (mov == null || mov.typeOfHit != MovingObjectType.BLOCK)
				return;
			this.breakBlock(mov);
			this.destroy();
		}
	}

	@Override
	public boolean canRenderOnFire() {
		return false;
	}

	private void breakBlock(MovingObjectPosition mov) {
		ReikaWorldHelper.dropAndDestroyBlockAt(worldObj, mov.blockX, mov.blockY, mov.blockZ, firingPlayer, false, true);
	}

	private void destroy() {
		this.setDead();

	}

}
