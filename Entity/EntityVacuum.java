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

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityVacuum extends Entity {

	public EntityVacuum(World world) {
		super(world);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (ticksExisted > 20) {
			motionX = 0;
			motionY = 0;
			motionZ = 0;
			noClip = true;
			this.suckInBlocks();
			this.suckInMobs();
		}
		else {

		}

		if (ticksExisted > 100)
			this.setDead();
		ticksExisted++;
	}

	private void suckInMobs() {

	}

	private void suckInBlocks() {

	}

	@Override
	protected void entityInit() {

	}

	@Override
	protected boolean canTriggerWalking()
	{
		return false;
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound NBT) {
		ticksExisted = NBT.getInteger("tick");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound NBT) {
		NBT.setInteger("tick", ticksExisted);
	}

	@Override
	public float getBrightness(float p_70013_1_)
	{
		return 1;
	}

	@Override
	public int getBrightnessForRender(float p_70070_1_)
	{
		return 15728880;
	}

	@Override
	public final boolean canAttackWithItem()
	{
		return false;
	}

	@Override
	public final boolean isEntityInvulnerable()
	{
		return true;
	}

	@Override
	public final boolean canRenderOnFire()
	{
		return false;
	}

}
