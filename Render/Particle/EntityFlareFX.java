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
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Rendering.ParticleEngine;
import Reika.DragonAPI.Instantiable.Rendering.ParticleEngine.RenderMode;
import Reika.DragonAPI.Instantiable.Rendering.ParticleEngine.RenderModeFlags;
import Reika.DragonAPI.Instantiable.Rendering.ParticleEngine.TextureMode;
import Reika.DragonAPI.Interfaces.Entity.CustomRenderFX;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityFlareFX extends EntityFX implements CustomRenderFX {

	private final CrystalElement color;

	private static final RenderMode rm = new RenderMode().setFlag(RenderModeFlags.ADDITIVE, true).setFlag(RenderModeFlags.LIGHT, false).setFlag(RenderModeFlags.ALPHACLIP, false);

	public EntityFlareFX(CrystalElement e, World world, double x, double y, double z) {
		this(e, world, x, y, z, 0.6F);
	}

	public EntityFlareFX(CrystalElement e, World world, double x, double y, double z, float gv) {
		super(world, x, y, z);
		particleMaxAge = 30+rand.nextInt(30);
		noClip = true;
		particleIcon = ChromaIcons.FLARE.getIcon();
		particleScale = 3F;
		color = e;
		particleGravity = rand.nextInt(3) == 0 ? gv : -gv;
	}

	public EntityFlareFX(CrystalElement e, World world, double x, double y, double z, double vx, double vy, double vz) {
		this(e, world, x, y, z);
		motionX = vx;
		motionY = vy;
		motionZ = vz;
	}

	public EntityFlareFX(CrystalElement e, World world, WorldLocation start, WorldLocation target, float rx, float ry, float rz) {
		this(e, world, start.xCoord+0.5+rx, start.yCoord+0.5+ry, start.zCoord+0.5+rz);
		double dd = target.getDistanceTo(start);
		motionX = (target.xCoord-start.xCoord)/dd/2;
		motionY = (target.yCoord-start.yCoord)/dd/2;
		motionZ = (target.zCoord-start.zCoord)/dd/2;
		particleMaxAge = (int)(dd*2.8);
	}

	public EntityFlareFX setNoGravity() {
		particleGravity = 0;
		return this;
	}

	public EntityFlareFX setLife(int time) {
		particleMaxAge = time;
		return this;
	}

	public EntityFlareFX setScale(float scale) {
		particleScale = scale;
		return this;
	}

	@Override
	public void renderParticle(Tessellator v5, float par2, float par3, float par4, float par5, float par6, float par7)
	{
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
		return 1;
	}

	@Override
	public final RenderMode getRenderMode() {
		return rm;
	}

	@Override
	public TextureMode getTexture() {
		return ParticleEngine.blockTex;
	}

	@Override
	public boolean rendersOverLimit() {
		return false;
	}

	@Override
	public double getRenderRange() {
		return particleScale*96*2;
	}


}
