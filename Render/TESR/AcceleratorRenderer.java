/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
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
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityAccelerator;
import Reika.DragonAPI.Interfaces.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

public class AcceleratorRenderer extends ChromaRenderBase {

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityAccelerator te = (TileEntityAccelerator)tile;

		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glDisable(GL11.GL_LIGHTING);
		//ReikaRenderHelper.disableLighting();

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glTranslated(par2, par4, par6);
		GL11.glScaled(1, -1, -1);

		if (!tile.hasWorldObj()) {
			GL11.glEnable(GL11.GL_BLEND);
			BlendMode.ADDITIVEDARK.apply();
			GL11.glDisable(GL11.GL_CULL_FACE);
			double a = 0.5;
			double b = -0.4;
			double c = -0.5;
			GL11.glTranslated(a, b, c);
			this.drawInner(te);
			GL11.glRotated(90, 0, 1, 0);
			this.drawInner(te);
			GL11.glRotated(-90, 0, 1, 0);
			GL11.glTranslated(-a, -b, -c);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_CULL_FACE);
		}
		else {
			//GL11.glDisable(GL11.GL_LIGHTING);
			//ReikaRenderHelper.disableEntityLighting();
		}

		this.drawMiddle(te);

		GL11.glEnable(GL11.GL_BLEND);
		BlendMode.ADDITIVEDARK.apply();
		GL11.glDisable(GL11.GL_CULL_FACE);


		if (te.hasWorldObj()) {
			this.drawInner(te);
		}
		else {
			//GL11.glEnable(GL11.GL_LIGHTING);
			//RenderHelper.enableGUIStandardItemLighting();
		}
		//ReikaRenderHelper.enableEntityLighting();
		GL11.glEnable(GL11.GL_LIGHTING);

		//this.drawSparkle(te);

		GL11.glEnable(GL11.GL_CULL_FACE);
		BlendMode.DEFAULT.apply();
		GL11.glDisable(GL11.GL_BLEND);
		if (te.hasWorldObj())
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	private void drawMiddle(TileEntityAccelerator te) {
		GL11.glLineWidth(4);
		GL11.glTranslated(0.5, -0.5, -0.5);
		GL11.glDisable(GL11.GL_TEXTURE_2D);

		double s = 1.5;
		if (!te.isInWorld()) {
			GL11.glScaled(s, s, s);
		}

		Tessellator v5 = Tessellator.instance;
		double time = System.currentTimeMillis();
		double rsc = 0.2+te.getTier()/4D;
		double rx = (time*rsc/4)%360;
		double ry = (time*rsc/3)%360;
		double rz = (time*rsc/5)%360;

		double d = 7200-te.getTier()*975;
		double d2 = d/2;
		double d3 = d/3;

		double t1 = time%d;
		double b1 = t1 >= d2 ? (d-t1)/d2 : t1/d2;
		double t2 = (time+d3)%d;
		double b2 = t2 >= d2 ? (d-t2)/d2 : t2/d2;
		double t3 = (time+2*d3)%d;
		double b3 = t3 >= d2 ? (d-t3)/d2 : t3/d2;

		int g1 = (int)(255*b1);
		int g2 = (int)(255*b2);
		int g3 = (int)(255*b3);

		int c1 = ReikaColorAPI.RGBtoHex(0, g1, 255);
		int c2 = ReikaColorAPI.RGBtoHex(0, g2, 255);
		int c3 = ReikaColorAPI.RGBtoHex(0, g3, 255);

		GL11.glRotated(rx, 1, 0, 0);
		ReikaRenderHelper.renderVCircle(0.25, 0, 0, 0, c1, 90, 15);
		GL11.glRotated(ry, 0, 1, 0);
		ReikaRenderHelper.renderVCircle(0.28125, 0, 0, 0, c2, 0, 15);
		GL11.glRotated(rz, 0, 0, 1);
		ReikaRenderHelper.renderCircle(0.3125, 0, 0, 0, c3, 15);
		GL11.glRotated(-rz, 0, 0, 1);
		GL11.glRotated(-ry, 0, 1, 0);
		GL11.glRotated(-rx, 1, 0, 0);

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glTranslated(-0.5, 0.5, 0.5);
		GL11.glLineWidth(2);

		if (!te.isInWorld()) {
			GL11.glScaled(1/s, 1/s, 1/s);
		}
	}

	private void drawSparkle(TileEntityAccelerator tile) {
		Tessellator v5 = Tessellator.instance;
		IIcon ico = ChromaIcons.GLOWSECTION.getIcon();
		float u = ico.getMinU();
		float v = ico.getMinV();
		float du = ico.getMaxU();
		float dv = ico.getMaxV();
		v5.startDrawingQuads();
		v5.addVertexWithUV(-1, -1, 0, u, v);
		v5.addVertexWithUV(1, -1, 0, du, v);
		v5.addVertexWithUV(1, 1, 0, du, dv);
		v5.addVertexWithUV(-1, 1, 0, u, dv);
		v5.draw();
	}

	private void drawInner(TileEntityAccelerator te) {
		ReikaTextureHelper.bindTerrainTexture();
		IIcon ico = ChromaIcons.GLOWSECTION.getIcon();
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

		double s = 0.1875;

		if (te.hasWorldObj()) {
			GL11.glTranslated(0.5, -0.5, -0.5);
			GL11.glScaled(s, s, s);
			RenderManager rm = RenderManager.instance;
			GL11.glRotatef(rm.playerViewY, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);
		}
		else {
			GL11.glTranslated(0, -0.125, 0);
			GL11.glRotated(-45, 0, 1, 0);
			GL11.glRotated(-30, 1, 0, 0);
			GL11.glScaled(s, s, s);
		}
		v5.startDrawingQuads();
		v5.addVertexWithUV(-1, -1, 0, u, v);
		v5.addVertexWithUV(1, -1, 0, du, v);
		v5.addVertexWithUV(1, 1, 0, du, dv);
		v5.addVertexWithUV(-1, 1, 0, u, dv);
		v5.draw();

		if (!te.hasWorldObj()) {
			GL11.glScaled(1/s, 1/s, 1/s);
			GL11.glRotated(30, 1, 0, 0);
			GL11.glRotated(45, 0, 1, 0);
			GL11.glTranslated(0, 0.125, 0);
		}
	}

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "";
	}

}
