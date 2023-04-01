/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render.Particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Rendering.ParticleEngine;
import Reika.DragonAPI.Instantiable.Rendering.ParticleEngine.RenderMode;
import Reika.DragonAPI.Instantiable.Rendering.ParticleEngine.RenderModeFlags;
import Reika.DragonAPI.Instantiable.Rendering.ParticleEngine.TextureMode;
import Reika.DragonAPI.Interfaces.Entity.CustomRenderFX;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;

public class EntityBallLightningFX extends EntityFX implements CustomRenderFX {

	private static final RenderMode renderMode = new RenderMode().setFlag(RenderModeFlags.FOG, false).setFlag(RenderModeFlags.ADDITIVE, true).setFlag(RenderModeFlags.DEPTH, true).setFlag(RenderModeFlags.LIGHT, false).setFlag(RenderModeFlags.ALPHACLIP, false);

	public double jitterX;
	public double jitterY;
	public double jitterZ;

	private double jitterVX;
	private double jitterVY;
	private double jitterVZ;

	public EntityBallLightningFX(World world, double x, double y, double z, CrystalElement e) {
		super(world, x, y, z);
		this.setPosition(x, y, z);
		noClip = true;
		particleMaxAge = 120;
		particleIcon = ChromaIcons.BIGFLARE.getIcon();
		double[] vals = ReikaPhysicsHelper.polarToCartesian(0.0625, rand.nextInt(360), rand.nextInt(360));
		jitterVX = vals[0];
		jitterVY = vals[1];
		jitterVZ = vals[2];
		particleRed = e.getRed()/255F;
		particleGreen = e.getGreen()/255F;
		particleBlue = e.getBlue()/255F;
	}

	public void setVelocity(double v, int yaw, int pitch) {
		double[] vals = ReikaPhysicsHelper.polarToCartesian(v, pitch, yaw);
		motionX = vals[0];
		motionY = vals[1];
		motionZ = vals[2];
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

		jitterX += jitterVX;
		jitterY += jitterVY;
		jitterZ += jitterVZ;

		double r = 0.5;
		if (Math.abs(jitterX) >= r) {
			jitterVX = -jitterVX;
		}
		if (Math.abs(jitterY) >= r) {
			jitterVY = -jitterVY;
		}
		if (Math.abs(jitterZ) >= r) {
			jitterVZ = -jitterVZ;
		}

		if (rand.nextInt(3) == 0) {
			float s = 3*rand.nextFloat()+0.5F;
			double px = posX+jitterX;
			double py = posY+jitterY;
			double pz = posZ+jitterZ;
			Minecraft.getMinecraft().effectRenderer.addEffect(new EntityCCBlurFX(worldObj, px, py, pz).setIcon(ChromaIcons.CENTER).setScale(s).setColor((int)(particleRed*255), (int)(particleGreen*255), (int)(particleBlue*255)));
		}

		if (particleAge == particleMaxAge) {
			for (int i = 0; i < 18; i++) {
				double[] vals = ReikaPhysicsHelper.polarToCartesian(0.125, rand.nextInt(360), rand.nextInt(360));
				Minecraft.getMinecraft().effectRenderer.addEffect(new EntitySparkleFX(worldObj, posX, posY, posZ, vals[0], vals[1], vals[2]));
			}
		}
	}

	@Override
	public void renderParticle(Tessellator v5, float par2, float par3, float par4, float par5, float par6, float par7) {
		double px = posX;
		double py = posY;
		double pz = posZ;
		double ppx = prevPosX;
		double ppy = prevPosY;
		double ppz = prevPosZ;
		posX += jitterX;
		posY += jitterY;
		posZ += jitterZ;
		prevPosX += jitterX;
		prevPosY += jitterY;
		prevPosZ += jitterZ;
		super.renderParticle(v5, par2, par3, par4, par5, par6, par7);
		prevPosX = ppx;
		prevPosY = ppy;
		prevPosZ = ppz;
		posX = px;
		posY = py;
		posZ = pz;
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
	public final boolean canRenderOnFire()
	{
		return false;
	}

	@Override
	public final boolean isBurning()
	{
		return true;
	}

	@Override
	public boolean shouldRenderInPass(int pass)
	{
		return pass == 1;
	}

	@Override
	public int getFXLayer() {
		return 1;
	}

	@Override
	public RenderMode getRenderMode() {
		return renderMode;
	}

	@Override
	public final TextureMode getTexture() {
		return ParticleEngine.blockTex;
	}

	@Override
	public boolean rendersOverLimit() {
		return false;
	}
	/*
	@Override
	public double getRenderRange() {
		return 60;
	}
	 */
}
