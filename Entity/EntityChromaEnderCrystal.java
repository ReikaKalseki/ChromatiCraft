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

import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public final class EntityChromaEnderCrystal extends EntityEnderCrystal {

	public EntityChromaEnderCrystal(World world) {
		super(world);
	}

	public EntityChromaEnderCrystal(World world, EntityEnderCrystal e) {
		this(world);
		this.setPosition(e.posX, e.posY, e.posZ);
		rotationPitch = e.rotationPitch;
		rotationYaw = e.rotationYaw;
		innerRotation = e.innerRotation;
		health = e.health;
	}

	@Override //Identical except cannot die outside of end
	public boolean attackEntityFrom(DamageSource src, float amt) {
		if (worldObj.provider.dimensionId != 1)
			return false;
		return super.attackEntityFrom(src, amt);
	}

	@Override
	public String getCommandSenderName() {
		return StatCollector.translateToLocal("chroma.endercrystal");
	}

}
