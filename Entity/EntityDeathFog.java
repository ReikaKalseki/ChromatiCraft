/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Entity;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import Reika.DragonAPI.Base.InertEntity;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;

public class EntityDeathFog extends InertEntity implements IEntityAdditionalSpawnData {

	public static final int MIN_LIFE = 40; //2s
	public static final int MAX_LIFE = 300; //15s

	private int lifespan;
	private int remainingLife;

	private boolean enhanced = false;

	public EntityDeathFog(World world, double x, double y, double z, double vx, double vy, double vz, boolean flag) {
		super(world);

		this.setLocationAndAngles(x, y, z, 0, 0);

		motionX = vx;
		motionY = vy;
		motionZ = vz;

		lifespan = ReikaRandomHelper.getRandomBetween(MIN_LIFE, MAX_LIFE);
		remainingLife = lifespan;
		noClip = false;

		enhanced = flag;

		this.setSize(0.125F, 0.125F);
	}

	public EntityDeathFog(World world) {
		super(world);
	}

	@Override
	protected void entityInit() {
		dataWatcher.addObject(24, 0);
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 1;
	}

	@Override
	public void onUpdate() {
		double mx = motionX;
		double my = motionY;
		double mz = motionZ;
		super.onUpdate();
		motionX = mx;
		motionY = my;
		motionZ = mz;

		if (!worldObj.isRemote) {
			remainingLife = lifespan-ticksExisted;
			dataWatcher.updateObject(24, remainingLife);

			List<AxisAlignedBB> li2 = worldObj.getCollidingBoundingBoxes(this, ReikaAABBHelper.getEntityCenteredAABB(this, 0.3));
			while (!li2.isEmpty()) {
				this.setPosition(posX, posY+0.1, posZ);
				li2 = worldObj.getCollidingBoundingBoxes(this, ReikaAABBHelper.getEntityCenteredAABB(this, 0.3));
				motionY = Math.max(motionY, 0);
				velocityChanged = true;
			}

			AxisAlignedBB box = ReikaAABBHelper.getEntityCenteredAABB(this, enhanced ? 4 : 2.5);
			List<EntityLivingBase> li = worldObj.getEntitiesWithinAABBExcludingEntity(this, box, ReikaEntityHelper.hostileSelector);
			for (EntityLivingBase e : li) {
				if (this.shouldAttack(e)) {
					this.attack(e);
				}
			}
		}
		else {
			remainingLife = dataWatcher.getWatchableObjectInt(24);
		}

		if (!worldObj.isRemote && ticksExisted >= lifespan) {
			this.destroy();
		}
	}

	@Override
	public boolean canRenderOnFire() {
		return false;
	}

	/*
	private void doAttack() {
		double r = 4+rand.nextDouble()*2;
		AxisAlignedBB box = ReikaAABBHelper.getEntityCenteredAABB(this, r);
		List<EntityLivingBase> li = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, box);
		while (r < 24 && li.isEmpty()) {
			r++;
			box = ReikaAABBHelper.getEntityCenteredAABB(this, r);
			li = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, box);
		}
		for (EntityLivingBase e : li) {
			if (this.shouldAttack(e)) {
				this.attack(e);
			}
		}
		this.destroy();
	}
	 */
	private void attack(EntityLivingBase e) {
		if (enhanced) {
			e.attackEntityFrom(DamageSource.magic, 0.5F);
			e.hurtResistantTime = e.maxHurtResistantTime/2+1;
		}
		else {
			e.attackEntityFrom(DamageSource.magic, 1);
		}
	}

	private boolean shouldAttack(EntityLivingBase e) {
		return ReikaEntityHelper.isHostile(e) && e.getHealth() > 0 && !e.isDead;
	}

	private void destroy() {
		this.setDead();
	}

	public boolean isEnhanced() {
		return enhanced;
	}

	public float getLifeRatio() {
		return remainingLife/(float)lifespan;
	}

	public int getRemainingLife() {
		return remainingLife;
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tag) {
		lifespan = tag.getInteger("life");
		remainingLife = tag.getInteger("time");
		enhanced = tag.getBoolean("enhance");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tag) {
		tag.setInteger("time", remainingLife);
		tag.setInteger("life", lifespan);
		tag.setBoolean("enhance", enhanced);
	}

	@Override
	public void writeSpawnData(ByteBuf buf) {
		buf.writeInt(lifespan);
		buf.writeInt(remainingLife);
		buf.writeBoolean(enhanced);
	}

	@Override
	public void readSpawnData(ByteBuf buf) {
		lifespan = buf.readInt();
		remainingLife = buf.readInt();
		enhanced = buf.readBoolean();
	}

}
