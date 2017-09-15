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

import java.util.Collection;
import java.util.HashSet;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Interfaces.CustomRenderFX;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.ParticleEngine;
import Reika.ChromatiCraft.Render.ParticleEngine.RenderMode;
import Reika.ChromatiCraft.Render.ParticleEngine.RenderModeFlags;
import Reika.ChromatiCraft.Render.ParticleEngine.TextureMode;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Interfaces.ColorController;
import Reika.DragonAPI.Interfaces.MotionController;
import Reika.DragonAPI.Interfaces.PositionController;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityBlurFX extends EntityFX implements CustomRenderFX {

	private float scale;
	private float cyclescale;

	private boolean noSlow = false;
	private boolean rapidExpand = false;
	private boolean alphaFade = false;

	private AxisAlignedBB bounds = null;
	private double collideAngle;
	private boolean colliding = false;

	private int lifeFreeze;

	private int preColor = -1;
	private int fadeColor = -1;

	private float defaultRed;
	private float defaultGreen;
	private float defaultBlue;

	private double drag;

	private Coordinate destination;

	private EntityFX lock;
	private Collection<EntityFX> locks = new HashSet();

	private boolean additiveBlend = true;
	private boolean depthTest = true;
	private boolean alphaTest = false;

	private MotionController motionController;
	private PositionController positionController;
	private ColorController colorController;

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
		this.setColor(e.getColor());
		particleIcon = ChromaIcons.FADE.getIcon();
	}

	public EntityBlurFX setIcon(ChromaIcons c) {
		particleIcon = c.getIcon();
		if (!c.isTransparent()) {
			alphaTest = false;
			additiveBlend = true;
		}
		return this;
	}

	public EntityBlurFX setIcon(IIcon ii) {
		particleIcon = ii;
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

	public EntityBlurFX setAlphaFading() {
		alphaFade = true;
		return this;
	}

	public final EntityBlurFX setGravity(float g) {
		particleGravity = g;
		return this;
	}

	public final EntityBlurFX setDrag(double d) {
		drag = d;
		return this;
	}

	public final EntityBlurFX setColor(int r, int g, int b) {
		particleRed = r/255F;
		particleGreen = g/255F;
		particleBlue = b/255F;
		defaultRed = particleRed;
		defaultGreen = particleGreen;
		defaultBlue = particleBlue;
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
		if (fx instanceof EntityBlurFX) {
			EntityBlurFX bfx = (EntityBlurFX)fx;
			if (!bfx.getRenderMode().equals(this.getRenderMode()))
				ChromatiCraft.logger.logError("Cannot accurately lock two different particle render types: "+fx+" & "+this);
			bfx.locks.add(this);
		}
		return this;
	}

	public EntityBlurFX setBasicBlend() {
		additiveBlend = false;
		return this;
	}

	public EntityBlurFX setNoDepthTest() {
		depthTest = false;
		return this;
	}

	public EntityBlurFX enableAlphaTest() {
		alphaTest = true;
		return this;
	}

	public EntityBlurFX setAge(int age) {
		particleAge = age;
		return this;
	}

	public EntityBlurFX freezeLife(int ticks) {
		lifeFreeze = ticks;
		return this;
	}

	public EntityBlurFX setMotionController(MotionController m) {
		motionController = m;
		return this;
	}

	public EntityBlurFX setPositionController(PositionController m) {
		positionController = m;
		return this;
	}

	public EntityBlurFX setColorController(ColorController m) {
		colorController = m;
		return this;
	}

	@Override
	public void onUpdate() {
		ticksExisted = particleAge;
		if (particleAge < 0) {
			return;
		}
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
			if (drag != 0) {
				motionX *= drag;
				motionY *= drag;
				motionZ *= drag;
			}
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

		if (alphaFade) {
			particleScale = scale;
			float f = 1;
			if (rapidExpand) {
				f = (particleMaxAge/age >= 12 ? age*12F/particleMaxAge : 1-age/(float)particleMaxAge);
			}
			else {
				f = (float)Math.sin(Math.toRadians(180D*age/particleMaxAge));
			}
			if (additiveBlend) {
				particleRed = defaultRed*f;
				particleGreen = defaultGreen*f;
				particleBlue = defaultBlue*f;
			}
			else {
				particleAlpha = f;
			}
		}
		else {
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
		}

		if (cyclescale > 0) {
			//CrystalElement e = CrystalElement.elements[(int)((age*cyclescale)%16)];
			int c = CrystalElement.getBlendedColor((int)(age*cyclescale), 5);
			particleRed = ReikaColorAPI.getRed(c)/255F;
			particleGreen = ReikaColorAPI.getGreen(c)/255F;
			particleBlue = ReikaColorAPI.getBlue(c)/255F;
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

		for (EntityFX fx : locks) {
			//fx.posX = posX;
			//fx.posY = posY;
			//fx.posZ = posZ;
			fx.motionX = motionX;
			fx.motionY = motionY;
			fx.motionZ = motionZ;
		}

		if (motionController != null) {
			motionX = motionController.getMotionX(this);
			motionY = motionController.getMotionY(this);
			motionZ = motionController.getMotionZ(this);
			motionController.update(this);
		}
		if (positionController != null) {
			posX = positionController.getPositionX(this);
			posY = positionController.getPositionY(this);
			posZ = positionController.getPositionZ(this);
			if (positionController != motionController) //prevent double update
				positionController.update(this);
		}

		if (colorController != null) {
			this.setColor(colorController.getColor(this));
			colorController.update(this);
		}
	}

	protected void onCollision() {

	}
	/*
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

	@Override
	public RenderMode getRenderMode() {
		return new RenderMode().setFlag(RenderModeFlags.ADDITIVE, additiveBlend).setFlag(RenderModeFlags.DEPTH, depthTest).setFlag(RenderModeFlags.LIGHT, false).setFlag(RenderModeFlags.ALPHACLIP, alphaTest && additiveBlend);//additiveBlend ? RenderMode.ADDITIVEDARK : RenderMode.LIT;
	}

	@Override
	public TextureMode getTexture() {
		return ParticleEngine.instance.blockTex;
	}

}
