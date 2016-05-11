/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render.Entity;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Entity.EntityGluon;

public class RenderGluon extends Render {

	@Override
	public void doRender(Entity e, double par2, double par4, double par6, float par8, float ptick) {
		EntityGluon eg = (EntityGluon)e;
		Tessellator v5 = Tessellator.instance;
		/*
		IIcon icon = ChromaIcons.SPARKLE.getIcon();
		float u = icon.getMinU();
		float v = icon.getMinV();
		float du = icon.getMaxU();
		float dv = icon.getMaxV();
		 */
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		/*
		BlendMode.ADDITIVEDARK.apply();
		 */
		GL11.glTranslated(par2, par4, par6);
		/*
		RenderManager rm = RenderManager.instance;
		double dx = e.posX-RenderManager.renderPosX;
		double dy = e.posY-RenderManager.renderPosY;
		double dz = e.posZ-RenderManager.renderPosZ;
		double[] angs = ReikaPhysicsHelper.cartesianToPolar(dx, dy, dz);
		GL11.glRotated(angs[2], 0, 1, 0);
		GL11.glRotated(90-angs[1], 1, 0, 0);
		//GL11.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);
		 */
		//v5.startDrawingQuads();
		float w = GL11.glGetFloat(GL11.GL_LINE_WIDTH);
		v5.startDrawing(GL11.GL_LINE_STRIP);
		v5.setBrightness(240);
		v5.setColorRGBA_I(0xffffff, 255);
		v5.addVertex(0, 0, 0);
		v5.addVertex(eg.getTargetX()-eg.posX, eg.getTargetY()-eg.posY, eg.getTargetZ()-eg.posZ);
		v5.draw();

		GL11.glLineWidth(4);
		v5.startDrawing(GL11.GL_LINE_STRIP);
		v5.setBrightness(240);
		v5.setColorRGBA_I(0xffffff, 192);
		v5.addVertex(0, 0, 0);
		v5.addVertex(eg.getTargetX()-eg.posX, eg.getTargetY()-eg.posY, eg.getTargetZ()-eg.posZ);
		v5.draw();

		GL11.glLineWidth(8);
		v5.startDrawing(GL11.GL_LINE_STRIP);
		v5.setBrightness(240);
		v5.setColorRGBA_I(0xffffff, 96);
		v5.addVertex(0, 0, 0);
		v5.addVertex(eg.getTargetX()-eg.posX, eg.getTargetY()-eg.posY, eg.getTargetZ()-eg.posZ);
		v5.draw();

		GL11.glLineWidth(16);
		v5.startDrawing(GL11.GL_LINE_STRIP);
		v5.setBrightness(240);
		v5.setColorRGBA_I(0xffffff, 32);
		v5.addVertex(0, 0, 0);
		v5.addVertex(eg.getTargetX()-eg.posX, eg.getTargetY()-eg.posY, eg.getTargetZ()-eg.posZ);
		v5.draw();
		GL11.glLineWidth(w);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		/*
		BlendMode.DEFAULT.apply();
		 */
		GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity e) {
		return null;
	}

}
