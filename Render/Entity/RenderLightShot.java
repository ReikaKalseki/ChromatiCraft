/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render.Entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import Reika.ChromatiCraft.Auxiliary.ChromaFX;
import Reika.ChromatiCraft.Entity.EntityLightShot;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.DragonAPI.Instantiable.Rendering.ColorBlendList;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;

public class RenderLightShot extends Render {

	@Override
	public void doRender(Entity e, double par2, double par4, double par6, float par8, float ptick) {
		ReikaTextureHelper.bindTerrainTexture();
		EntityLightShot eb = (EntityLightShot)e;
		Tessellator v5 = Tessellator.instance;
		IIcon icon = ChromaIcons.CHROMA.getIcon();
		float u = icon.getMinU();
		float v = icon.getMinV();
		float du = icon.getMaxU();
		float dv = icon.getMaxV();
		IIcon icon2 = ChromaIcons.SPINFLARE.getIcon();
		float u2 = icon2.getMinU();
		float v2 = icon2.getMinV();
		float du2 = icon2.getMaxU();
		float dv2 = icon2.getMaxV();
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glDepthMask(false);
		GL11.glTranslated(par2, par4, par6);
		if (!e.isDead) {
			RenderManager rm = RenderManager.instance;
			double dx = e.posX-RenderManager.renderPosX;
			double dy = e.posY-RenderManager.renderPosY;
			double dz = e.posZ-RenderManager.renderPosZ;
			double[] angs = ReikaPhysicsHelper.cartesianToPolar(dx, dy, dz);
			GL11.glRotated(angs[2], 0, 1, 0);
			GL11.glRotated(90-angs[1], 1, 0, 0);
		}
		//GL11.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);
		BlendMode.DEFAULT.apply();
		v5.startDrawingQuads();
		v5.setBrightness(240);
		double s1 = 0.25;
		double s2 = 0.6;
		double d = 0.001;
		int c1 = 0xffffff;//eb.getRenderColor();
		v5.setColorOpaque_I(c1);
		v5.addVertexWithUV(-s1, -s1, 0, u, v);
		v5.addVertexWithUV(s1, -s1, 0, du, v);
		v5.addVertexWithUV(s1, s1, 0, du, dv);
		v5.addVertexWithUV(-s1, s1, 0, u, dv);
		v5.draw();

		BlendMode.ADDITIVEDARK.apply();
		GL11.glShadeModel(GL11.GL_SMOOTH);
		v5.startDrawingQuads();
		double mod = 15+(e.getEntityId())%15;
		double f = 0.8+0.2*Math.sin((System.currentTimeMillis()/mod)%360);
		int c = new ColorBlendList(5, ChromaFX.getChromaColorTiles()).getColor(e.ticksExisted);
		v5.setColorOpaque_I(ReikaColorAPI.getColorWithBrightnessMultiplier(c, (float)f));
		v5.addVertexWithUV(-s2, -s2, -0.05, u2, v2);
		v5.addVertexWithUV(s2, -s2, -0.05, du2, v2);
		v5.addVertexWithUV(s2, s2, -0.05, du2, dv2);
		v5.addVertexWithUV(-s2, s2, -0.05, u2, dv2);
		v5.draw();
		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity e) {
		return null;
	}

}
