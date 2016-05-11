/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render.Particle;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EngravedRuneFX extends EntityFX {

	private final ForgeDirection direction;

	public EngravedRuneFX(World world, double x, double y, double z, CrystalElement e, ForgeDirection dir) {
		super(world, x, y, z);
		particleMaxAge = 30;
		noClip = true;
		particleIcon = e.getEngravingRune();
		particleScale = 1F;
		particleGravity = 0;
		direction = dir;
	}

	public EngravedRuneFX setScale(float sc) {
		particleScale = sc;
		return this;
	}

	public EngravedRuneFX setLife(int life) {
		particleMaxAge = life;
		return this;
	}

	@Override
	public void renderParticle(Tessellator v5, float par2, float par3, float par4, float par5, float par6, float par7)
	{
		v5.draw();
		ReikaTextureHelper.bindTerrainTexture();
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glEnable(GL11.GL_BLEND);
		v5.startDrawingQuads();
		v5.setBrightness(this.getBrightnessForRender(0));
		//super.renderParticle(v5, par2, par3, par4, par5, par6, par7);
		double dx = posX+direction.offsetX;
		double dy = posY+direction.offsetY+4;
		double dz = posZ+direction.offsetZ;
		v5.setColorRGBA_F(particleRed, particleGreen, particleBlue, particleAlpha);
		v5.addVertexWithUV(dx, dy, dz, particleIcon.getMinU(), particleIcon.getMinV());
		v5.addVertexWithUV(dx+1, dy, dz, particleIcon.getMaxU(), particleIcon.getMinV());
		v5.addVertexWithUV(dx+1, dy, dz+1, particleIcon.getMaxU(), particleIcon.getMaxV());
		v5.addVertexWithUV(dx, dy, dz+1, particleIcon.getMinU(), particleIcon.getMaxV());
		v5.draw();
		v5.startDrawingQuads();
	}

	@Override
	public void onUpdate() {
		if (particleAge++ >= particleMaxAge)
			this.setDead();
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
