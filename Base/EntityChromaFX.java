/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Registry.CrystalElement;

public abstract class EntityChromaFX extends EntityFX {

	/*
	public EntityChromaFX(World world, double x, double y, double z) {
		this(CrystalElement.WHITE, world, x, y, z, 0, 0, 0);
	}

	public EntityChromaFX(World world, double x, double y, double z, double vx, double vy, double vz) {
		this(CrystalElement.WHITE, world, x, y, z, vx, vy, vz);
	}*/

	public EntityChromaFX(CrystalElement e, World world, double x, double y, double z, double vx, double vy, double vz) {
		super(world, x, y, z, vx, vy, vz);
		motionX = vx;
		motionY = vy;
		motionZ = vz;
		particleRed = e.getRed()/255F;
		particleGreen = e.getGreen()/255F;
		particleBlue = e.getBlue()/255F;
	}

	public EntityChromaFX setScale(float f) {
		particleScale = f;
		return this;
	}

	@Override
	public final int getBrightnessForRender(float par1) {
		return 240;
	}

	@Override
	public final int getFXLayer() {
		return 2;
	}

}
