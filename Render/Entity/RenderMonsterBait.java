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
import net.minecraft.entity.Entity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import Reika.ChromatiCraft.Entity.EntityMonsterBait;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

public class RenderMonsterBait extends Render {

	@Override
	public void doRender(Entity e, double par2, double par4, double par6, float par8, float ptick) {
		ReikaTextureHelper.bindTerrainTexture();
		EntityMonsterBait eb = (EntityMonsterBait)e;
		Tessellator v5 = Tessellator.instance;
		IIcon icon = ChromaIcons.ROSEFLARE.getIcon();
		float u = icon.getMinU();
		float v = icon.getMinV();
		float du = icon.getMaxU();
		float dv = icon.getMaxV();
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_CULL_FACE);
		BlendMode.ADDITIVEDARK.apply();
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glDepthMask(false);
		GL11.glTranslated(par2, par4, par6);
		/*
		if (!e.isDead) {
			RenderManager rm = RenderManager.instance;
			double dx = e.posX-RenderManager.renderPosX;
			double dy = e.posY-RenderManager.renderPosY;
			double dz = e.posZ-RenderManager.renderPosZ;
			double[] angs = ReikaPhysicsHelper.cartesianToPolar(dx, dy, dz);
			GL11.glRotated(angs[2], 0, 1, 0);
			GL11.glRotated(90-angs[1], 1, 0, 0);
		}
		 */
		//GL11.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);
		v5.startDrawingQuads();
		v5.setBrightness(240);
		double s1 = 0.5;
		double r = 0.0625;//0.0625+0.03125*Math.sin(eb.ticksExisted/8D);
		for (double y = -r; y <= r; y += r/4D) {
			double t = e.ticksExisted/10D+y*40;
			int c = eb.getRenderColor(y);
			double dx = 0.0625*Math.sin(t*0.46);
			double dz = 0.0625*Math.sin(t*0.78);
			v5.setColorOpaque_I(c);
			v5.addVertexWithUV(-s1+dx, y, -s1+dz, u, v);
			v5.addVertexWithUV(s1+dx, y, -s1+dz, du, v);
			v5.addVertexWithUV(s1+dx, y, s1+dz, du, dv);
			v5.addVertexWithUV(-s1+dx, y, s1+dz, u, dv);
		}
		v5.draw();
		/*
		TessellatorVertexList tv5 = new TessellatorVertexList(0, 0, 0);
		ReikaRenderHelper.renderIconIn3D(tv5, icon, 0, 0, 0);
		tv5.render();
		 */
		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity e) {
		return null;
	}

}
