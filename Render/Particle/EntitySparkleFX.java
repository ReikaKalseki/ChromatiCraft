/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render.Particle;

import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

public class EntitySparkleFX extends EntityFX {

	public EntitySparkleFX(World world, double x, double y, double z, double vx, double vy, double vz) {
		super(world, x, y, z);
		particleMaxAge = 10+rand.nextInt(20);
		noClip = true;
		motionX = vx;
		motionY = vy;
		motionZ = vz;
		particleIcon = ChromaIcons.SPARKLEPARTICLE.getIcon();
		particleScale = 0.5F;
	}

	@Override
	public void renderParticle(Tessellator v5, float par2, float par3, float par4, float par5, float par6, float par7)
	{
		ReikaTextureHelper.bindTerrainTexture();
		super.renderParticle(v5, par2, par3, par4, par5, par6, par7);
	}

	@Override
	public int getBrightnessForRender(float par1)
	{
		return 240;
	}

	@Override
	public int getFXLayer()
	{
		return 2;
	}

}