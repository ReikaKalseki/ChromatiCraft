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

import net.minecraft.client.particle.EntityFX;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Rendering.ParticleEngine.CustomTextureMode;
import Reika.DragonAPI.Instantiable.Rendering.ParticleEngine.RenderMode;
import Reika.DragonAPI.Instantiable.Rendering.ParticleEngine.RenderModeFlags;
import Reika.DragonAPI.Instantiable.Rendering.ParticleEngine.TextureMode;
import Reika.DragonAPI.Interfaces.Entity.CustomRenderFX;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityCenterBlurFX extends EntityFX implements CustomRenderFX {

	private static final TextureMode texture = new CustomTextureMode(ChromatiCraft.class, "Textures/Particle/64x.png");
	private static final RenderMode render = new RenderMode().setFlag(RenderModeFlags.ADDITIVE, true).setFlag(RenderModeFlags.ALPHACLIP, false).setFlag(RenderModeFlags.LIGHT, false);

	private float scale;
	private float cyclescale;
	private boolean noSlow;

	public EntityCenterBlurFX(World world, double x, double y, double z) {
		this(CrystalElement.WHITE, world, x, y, z, 0, 0, 0);
	}

	public EntityCenterBlurFX(World world, double x, double y, double z, double vx, double vy, double vz) {
		this(CrystalElement.WHITE, world, x, y, z, vx, vy, vz);
	}

	public EntityCenterBlurFX(CrystalElement e, World world, double x, double y, double z, double vx, double vy, double vz) {
		super(world, x, y, z, vx, vy, vz);
		particleGravity = 0;
		noClip = true;
		particleMaxAge = 63;
		motionX = vx;
		motionY = vy;
		motionZ = vz;
		particleScale = 1F;
		particleRed = e.getRed()/255F;
		particleGreen = e.getGreen()/255F;
		particleBlue = e.getBlue()/255F;
	}

	public EntityCenterBlurFX setScale(float f) {
		particleScale = f;
		return this;
	}

	public final EntityCenterBlurFX setLife(int time) {
		particleMaxAge = time;
		return this;
	}

	public final EntityCenterBlurFX setNoSlowdown() {
		noSlow = true;
		return this;
	}

	public final EntityCenterBlurFX setGravity(float g) {
		particleGravity = g;
		return this;
	}

	public final EntityCenterBlurFX setColor(int rgb) {
		particleRed = ReikaColorAPI.HextoColorMultiplier(rgb, 0);
		particleGreen = ReikaColorAPI.HextoColorMultiplier(rgb, 1);
		particleBlue = ReikaColorAPI.HextoColorMultiplier(rgb, 2);
		return this;
	}

	@Override
	public void onUpdate() {

		if (noSlow) {
			double mx = motionX;
			double my = motionY;
			double mz = motionZ;
			super.onUpdate();
			motionX = mx;
			motionY = my;
			motionZ = mz;
		}
		else {
			super.onUpdate();
		}

		//particleScale = scale*(float)Math.sin(Math.toRadians(180D*particleAge/particleMaxAge));
		//if (particleAge < 10)
		//	particleScale = scale*(particleAge+1)/10F;
		//else if (particleAge > 50)
		//	particleScale = scale*(61-particleAge)/10F;
		//else
		//	particleScale = scale;

		if (particleAge < 16) {
			particleTextureIndexX = particleAge;
			particleTextureIndexY = 4;
		}
		else if (particleAge < 32) {
			particleTextureIndexX = particleAge-16;
			particleTextureIndexY = 5;
		}
		else if (particleAge < 48) {
			particleTextureIndexX = 15-particleAge%16;
			particleTextureIndexY = 5;
		}
		else {
			particleTextureIndexX = 15-particleAge%16;
			particleTextureIndexY = 4;
		}

		if (cyclescale > 0) {
			CrystalElement e = CrystalElement.elements[(int)((particleAge*cyclescale)%16)];
			particleRed = e.getRed()/255F;
			particleGreen = e.getGreen()/255F;
			particleBlue = e.getBlue()/255F;
		}
	}

	/*
	@Override
	public void renderParticle(Tessellator v5, float par2, float par3, float par4, float par5, float par6, float par7)
	{
		v5.draw();
		ReikaTextureHelper.bindFinalTexture(ChromatiCraft.class, "Textures/Particle/64x.png");
		BlendMode.ADDITIVEDARK.apply();
		GL11.glColor4f(1, 1, 1, 1);
		v5.startDrawingQuads();
		v5.setBrightness(this.getBrightnessForRender(0));
		super.renderParticle(v5, par2, par3, par4, par5, par6, par7);
		v5.draw();
		BlendMode.DEFAULT.apply();
		v5.startDrawingQuads();
	}
	 */

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

	public EntityCenterBlurFX setCyclingColor(float scale) {
		cyclescale = scale;
		return this;
	}

	@Override
	public RenderMode getRenderMode() {
		return render;
	}

	@Override
	public TextureMode getTexture() {
		return texture;
	}

	@Override
	public boolean rendersOverLimit() {
		return false;
	}
	/*
	@Override
	public double getRenderRange() {
		return scale*96;
	}
	 */
}
