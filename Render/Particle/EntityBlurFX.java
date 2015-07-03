/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render.Particle;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityBlurFX extends EntityFX {

	private float scale;
	private float cyclescale;
	private boolean noSlow = false;
	private boolean rapidExpand = false;
	private AxisAlignedBB bounds = null;

	public EntityBlurFX(World world, double x, double y, double z) {
		this(CrystalElement.WHITE, world, x, y, z, 0, 0, 0);
	}

	public EntityBlurFX(World world, double x, double y, double z, double vx, double vy, double vz) {
		this(CrystalElement.WHITE, world, x, y, z, vx, vy, vz);
	}

	public EntityBlurFX(CrystalElement e, World world, double x, double y, double z, double vx, double vy, double vz) {
		super(world, x, y, z, vx, vy, vz);
		particleGravity = 0;
		noClip = true;
		particleMaxAge = 60;
		motionX = vx;
		motionY = vy;
		motionZ = vz;
		scale = 1F;
		particleRed = e.getRed()/255F;
		particleGreen = e.getGreen()/255F;
		particleBlue = e.getBlue()/255F;
		particleIcon = ChromaIcons.FADE.getIcon();
	}

	public EntityBlurFX setIcon(ChromaIcons c) {
		particleIcon = c.getIcon();
		return this;
	}

	public EntityBlurFX setScale(float f) {
		scale = f;
		return this;
	}

	public final EntityBlurFX setLife(int time) {
		particleMaxAge = time;
		return this;
	}

	public final EntityBlurFX setNoSlowdown() {
		noSlow = true;
		return this;
	}

	public EntityBlurFX setRapidExpand() {
		rapidExpand = true;
		return this;
	}

	public final EntityBlurFX setGravity(float g) {
		particleGravity = g;
		return this;
	}

	public final EntityBlurFX setColor(int r, int g, int b) {
		particleRed = r/255F;
		particleGreen = g/255F;
		particleBlue = b/255F;
		return this;
	}

	public final EntityBlurFX bound(AxisAlignedBB box) {
		bounds = box;
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

		if (rapidExpand)
			particleScale = scale*(particleMaxAge/particleAge >= 12 ? particleAge*12F/particleMaxAge : 1-particleAge/(float)particleMaxAge);
		else
			particleScale = scale*(float)Math.sin(Math.toRadians(180D*particleAge/particleMaxAge));
		//if (particleAge < 10)
		//	particleScale = scale*(particleAge+1)/10F;
		//else if (particleAge > 50)
		//	particleScale = scale*(61-particleAge)/10F;
		//else
		//	particleScale = scale;

		if (cyclescale > 0) {
			CrystalElement e = CrystalElement.elements[(int)((particleAge*cyclescale)%16)];
			particleRed = e.getRed()/255F;
			particleGreen = e.getGreen()/255F;
			particleBlue = e.getBlue()/255F;
		}

		if (bounds != null) {
			if ((posX <= bounds.minX && motionX < 0) || (posX >= bounds.maxX && motionX > 0)) {
				motionX = -motionX;
			}
			if ((posY <= bounds.minY && motionY < 0) || (posY >= bounds.maxY && motionY > 0)) {
				motionY = -motionY;
			}
			if ((posZ <= bounds.minZ && motionZ < 0) || (posZ >= bounds.maxZ && motionZ > 0)) {
				motionZ = -motionZ;
			}
		}
	}

	@Override
	public void renderParticle(Tessellator v5, float par2, float par3, float par4, float par5, float par6, float par7)
	{
		v5.draw();
		ReikaTextureHelper.bindTerrainTexture();
		BlendMode.ADDITIVEDARK.apply();
		GL11.glColor4f(1, 1, 1, 1);
		v5.startDrawingQuads();
		v5.setBrightness(this.getBrightnessForRender(0));
		super.renderParticle(v5, par2, par3, par4, par5, par6, par7);
		v5.draw();
		BlendMode.DEFAULT.apply();
		v5.startDrawingQuads();
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

	public EntityBlurFX setCyclingColor(float scale) {
		cyclescale = scale;
		return this;
	}

}
