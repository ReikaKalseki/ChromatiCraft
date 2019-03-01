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

import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;

public final class EntityChromaEnderCrystal extends EntityEnderCrystal {

	private double originX;
	private double originY;
	private double originZ;

	public EntityChromaEnderCrystal(World world) {
		super(world);
		yOffset = 0;
	}

	public EntityChromaEnderCrystal(World world, int x, int y, int z) {
		this(world);
		yOffset = 0;
		double dx = x+0.5;
		double dy = y+1;
		double dz = z+0.5;
		this.setPosition(dx, dy, dz);
		originX = dx;
		originY = dy;
		originZ = dz;
	}

	public EntityChromaEnderCrystal(World world, EntityEnderCrystal e) {
		this(world);
		originX = e.posX;
		originY = e.posY;
		originZ = e.posZ;
		this.setPosition(originX, originY, originZ);
		rotationPitch = e.rotationPitch;
		rotationYaw = e.rotationYaw;
		innerRotation = e.innerRotation;
		health = e.health;
	}
	/*
	@Override
	public void setPosition(double x, double y, double z) {
		super.setPosition(x, y, z);
		originX = x;
		originY = y;
		originZ = z;
	}
	 */
	@Override
	public void onUpdate() {
		super.onUpdate();

		int x = MathHelper.floor_double(posX);
		int y = MathHelper.floor_double(posY);
		int z = MathHelper.floor_double(posZ);
		if (worldObj.getBlock(x, y, z) != Blocks.fire)
			worldObj.setBlock(x, y, z, Blocks.fire);
		if (!worldObj.isRemote) {
			if (worldObj.getBlock(x, y-1, z) != Blocks.bedrock)
				this.setDead();
		}

		//this.setPosition(originX, originY, originZ);
	}

	@Override //Identical except cannot die outside of end
	public boolean attackEntityFrom(DamageSource src, float amt) {
		if (worldObj.provider.dimensionId != 1) {
			ReikaParticleHelper.EXPLODE.spawnAt(this);
			return false;
		}
		return super.attackEntityFrom(src, amt);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tag) {
		super.readEntityFromNBT(tag);

		/*
		if (tag.hasKey("ix"))
			originX = tag.getDouble("ix");
		if (tag.hasKey("iy"))
			originY = tag.getDouble("iy");
		if (tag.hasKey("iz"))
			originZ = tag.getDouble("iz");
		 */
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tag) {
		super.writeEntityToNBT(tag);

		/*
		tag.setDouble("ix", originX);
		tag.setDouble("iy", originY);
		tag.setDouble("iz", originZ);
		 */
	}

	@Override
	public String getCommandSenderName() {
		return StatCollector.translateToLocal("chroma.endercrystal");
	}

}
