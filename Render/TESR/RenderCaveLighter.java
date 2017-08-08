/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render.TESR;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityCaveLighter;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;


public class RenderCaveLighter extends ChromaRenderBase {

	@Override
	public String getImageFileName(RenderFetcher te) {
		return null;
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityCaveLighter te = (TileEntityCaveLighter)tile;

		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);
		BlendMode.ADDITIVEDARK.apply();
		ReikaRenderHelper.disableEntityLighting();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glTranslatef((float)par2, (float)par4 + 1.0F, (float)par6 + 1.0F);
		GL11.glScalef(1.0F, -1.0F, -1.0F);

		this.drawInner(te);

		IIcon ico = ChromaIcons.GLOWFRAME.getIcon();
		float u = ico.getMinU();
		float v = ico.getMinV();
		float du = ico.getMaxU();
		float dv = ico.getMaxV();
		Tessellator v5 = Tessellator.instance;

		if (te.isInWorld()) {
			v5.startDrawingQuads();
			v5.addVertexWithUV(0, 0, 0, u, v);
			v5.addVertexWithUV(1, 0, 0, du, v);
			v5.addVertexWithUV(1, 0, 1, du, dv);
			v5.addVertexWithUV(0, 0, 1, u, dv);

			v5.addVertexWithUV(0, 1, 1, u, dv);
			v5.addVertexWithUV(1, 1, 1, du, dv);
			v5.addVertexWithUV(1, 1, 0, du, v);
			v5.addVertexWithUV(0, 1, 0, u, v);

			v5.addVertexWithUV(0, 1, 0, u, dv);
			v5.addVertexWithUV(1, 1, 0, du, dv);
			v5.addVertexWithUV(1, 0, 0, du, v);
			v5.addVertexWithUV(0, 0, 0, u, v);

			v5.addVertexWithUV(0, 0, 1, u, v);
			v5.addVertexWithUV(1, 0, 1, du, v);
			v5.addVertexWithUV(1, 1, 1, du, dv);
			v5.addVertexWithUV(0, 1, 1, u, dv);

			v5.addVertexWithUV(0, 0, 0, u, v);
			v5.addVertexWithUV(0, 0, 1, du, v);
			v5.addVertexWithUV(0, 1, 1, du, dv);
			v5.addVertexWithUV(0, 1, 0, u, dv);

			v5.addVertexWithUV(1, 1, 0, u, dv);
			v5.addVertexWithUV(1, 1, 1, du, dv);
			v5.addVertexWithUV(1, 0, 1, du, v);
			v5.addVertexWithUV(1, 0, 0, u, v);
			v5.draw();
		}/*
		else {
			GL11.glPushMatrix();
			GL11.glRotated(-45, 0, 1, 0);
			GL11.glRotated(-30, 1, 0, 0);
			GL11.glTranslated(0.65, 0.45, 0);
			//double s = 1;
			//GL11.glScaled(s, s, s);
			v5.startDrawingQuads();
			v5.addVertexWithUV(-1, 1, 0, u, dv);
			v5.addVertexWithUV(1, 1, 0, du, dv);
			v5.addVertexWithUV(1, -1, 0, du, v);
			v5.addVertexWithUV(-1, -1, 0, u, v);
			v5.draw();
			GL11.glPopMatrix();
		}*/

		if (te.hasWorldObj())
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
		GL11.glPopAttrib();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	private void drawInner(TileEntityCaveLighter te) {
		ReikaTextureHelper.bindTerrainTexture();
		IIcon ico = ChromaIcons.OVALS.getIcon();
		float u = ico.getMinU();
		float v = ico.getMinV();
		float du = ico.getMaxU();
		float dv = ico.getMaxV();
		float uu = du-u;
		float vv = dv-v;

		float r = 16;
		u += uu/r;
		du -= uu/r;
		v += vv/r;
		dv -= vv/r;

		Tessellator v5 = Tessellator.instance;
		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glDepthMask(false);
		GL11.glDisable(GL11.GL_CULL_FACE);

		double s = te.isInWorld() ? 0.4375 : 0.33;
		if (te.hasWorldObj()) {
			GL11.glTranslated(0.5, 0.5, 0.5);
			GL11.glScaled(s, s, s);
			RenderManager rm = RenderManager.instance;
			GL11.glRotatef(rm.playerViewY, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);
		}
		else {
			s = 0.5;
			GL11.glTranslated(0.5, 0.5, 0.5);
			GL11.glRotated(-45, 0, 1, 0);
			GL11.glRotated(-30, 1, 0, 0);
			GL11.glScaled(s, s, s);
		}

		int[] c = {0xff0000, 0x00ff00, 0x0000ff, 0xffffff};
		for (int i = 0; i < c.length; i++) {
			//double ang = (System.currentTimeMillis()/20D)%360;
			//GL11.glRotated(ang, 0, 0, 1);
			GL11.glPushMatrix();
			if (te.isInWorld())
				GL11.glTranslated(0, 0/*-i*/, 0.005*i);
			GL11.glRotated(i*30, 0, 0, 1);
			double s2 = 1-i/6D;
			GL11.glScaled(s2, s2, s2);
			v5.startDrawingQuads();
			v5.setColorOpaque_I(c[i]);
			v5.addVertexWithUV(-1, -1, 0, u, v);
			v5.addVertexWithUV(1, -1, 0, du, v);
			v5.addVertexWithUV(1, 1, 0, du, dv);
			v5.addVertexWithUV(-1, 1, 0, u, dv);
			v5.draw();
			GL11.glPopMatrix();
		}

		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

}
