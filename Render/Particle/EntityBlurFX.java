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
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
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
	private double collideAngle;
	private boolean colliding = false;

	private int lifeFreeze;

	private int preColor = -1;
	private int fadeColor = -1;

	private Coordinate destination;

	private EntityFX lock;

	private boolean additiveBlend = true;

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

	public final EntityBlurFX setColor(int rgb) {
		return this.setColor(ReikaColorAPI.getRed(rgb), ReikaColorAPI.getGreen(rgb), ReikaColorAPI.getBlue(rgb));
	}

	public final EntityBlurFX fadeColors(int c1, int c2) {
		preColor = c1;
		fadeColor = c2;
		return this.setColor(c1);
	}

	public final EntityBlurFX bound(AxisAlignedBB box) {
		bounds = box;
		return this;
	}

	public final EntityBlurFX setColliding() {
		return this.setColliding(rand.nextDouble()*360);
	}

	public final EntityBlurFX setColliding(double ang) {
		noClip = false;
		colliding = true;
		collideAngle = ang;
		return this;
	}

	public EntityBlurFX setCyclingColor(float scale) {
		cyclescale = scale;
		return this;
	}

	public EntityBlurFX markDestination(int x, int y, int z) {
		destination = new Coordinate(x, y, z);
		return this;
	}

	public EntityBlurFX lockTo(EntityFX fx) {
		lock = fx;
		return this;
	}

	public EntityBlurFX setBasicBlend() {
		additiveBlend = false;
		return this;
	}

	public EntityBlurFX setAge(int age) {
		particleAge = age;
		return this;
	}

	@Override
	public void onUpdate() {

		if (colliding) {
			if (isCollidedVertically) {
				double v = rand.nextDouble()*0.0625;
				motionX = v*Math.sin(Math.toRadians(collideAngle));
				motionZ = v*Math.cos(Math.toRadians(collideAngle));
				colliding = false;
				this.setNoSlowdown();
				lifeFreeze = 20;
				particleGravity *= 4;
				this.onCollision();
			}
			if (isCollidedHorizontally) {

			}
		}

		if (destination != null) {
			Coordinate c = new Coordinate(this);
			if (c.equals(destination)) {
				this.setDead();
			}
		}

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

		if (lifeFreeze > 0) {
			lifeFreeze--;
			particleAge--;
		}

		int age = Math.max(particleAge, 1);

		if (fadeColor != -1) {
			int c = ReikaColorAPI.mixColors(fadeColor, preColor, age/(float)particleMaxAge);
			this.setColor(c);
		}

		if (rapidExpand)
			particleScale = scale*(particleMaxAge/age >= 12 ? age*12F/particleMaxAge : 1-age/(float)particleMaxAge);
		else
			particleScale = scale*(float)Math.sin(Math.toRadians(180D*age/particleMaxAge));
		//if (particleAge < 10)
		//	particleScale = scale*(particleAge+1)/10F;
		//else if (particleAge > 50)
		//	particleScale = scale*(61-particleAge)/10F;
		//else
		//	particleScale = scale;

		if (cyclescale > 0) {
			CrystalElement e = CrystalElement.elements[(int)((age*cyclescale)%16)];
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

		if (lock != null) {
			posX = lock.posX;
			posY = lock.posY;
			posZ = lock.posZ;
			motionX = lock.motionX;
			motionY = lock.motionY;
			motionZ = lock.motionZ;
		}
	}

	protected void onCollision() {

	}

	@Override
	public void renderParticle(Tessellator v5, float par2, float par3, float par4, float par5, float par6, float par7)
	{
		v5.draw();
		ReikaTextureHelper.bindTerrainTexture();
		if (additiveBlend)
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

}
