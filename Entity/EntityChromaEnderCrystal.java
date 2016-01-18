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
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;

public final class EntityChromaEnderCrystal extends EntityEnderCrystal {

	public EntityChromaEnderCrystal(World world) {
		super(world);
		yOffset = 0;
	}

	public EntityChromaEnderCrystal(World world, EntityEnderCrystal e) {
		this(world);
		this.setPosition(e.posX, e.posY, e.posZ);
		rotationPitch = e.rotationPitch;
		rotationYaw = e.rotationYaw;
		innerRotation = e.innerRotation;
		health = e.health;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (!worldObj.isRemote) {
			int x = MathHelper.floor_double(posX);
			int y = MathHelper.floor_double(posY);
			int z = MathHelper.floor_double(posZ);
			worldObj.setBlock(x, y+1, z, Blocks.fire);
		}
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
	public String getCommandSenderName() {
		return StatCollector.translateToLocal("chroma.endercrystal");
	}

}
