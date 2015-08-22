/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render.TESR;

import java.util.Random;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalMusic;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

public class RenderCrystalMusic extends ChromaRenderBase {

	private static final Random rand = new Random();

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityCrystalMusic te = (TileEntityCrystalMusic)tile;

		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glTranslatef((float)par2, (float)par4 + 1.0F, (float)par6 + 1.0F);
		GL11.glScalef(1.0F, -1.0F, -1.0F);

		if (tile.hasWorldObj())
			this.drawMiddle(te);
		this.drawInner(te);
		if (!tile.hasWorldObj()) {
			GL11.glTranslated(0, 0, 1);
			GL11.glRotated(90, 0, 1, 0);
			//this.drawInner(te);
			GL11.glRotated(-90, 0, 1, 0);
			GL11.glTranslated(0, 0, -1);
			this.drawOuter(te, 1, par8, 0);
		}
		else {
			//RenderManager rm = RenderManager.instance;

			double ctr = 0.75;
			double spr = 0.0625;
			double min = ctr-spr;
			double max = ctr+spr;
			double ds = 0.0625;

			int da = 0;

			for (double s = min; s <= max; s += ds) {
				double d = 0.5;

				double ax = (te.getTicksExisted()+par8)*(1+da)/2D;
				double ay = (te.getTicksExisted()+par8)*(1+da)/2D;

				GL11.glPushMatrix();
				GL11.glTranslated(d, d, d);
				GL11.glRotated(/*rm.playerViewY+*/ay, 1, 0, 0);
				GL11.glRotated(/*rm.playerViewX+*/ax, 0, 1, 0);
				GL11.glTranslated(-d, -d, -d);
				this.drawOuter(te, s, par8, da);
				GL11.glPopMatrix();

				GL11.glPushMatrix();
				GL11.glTranslated(d, d, d);
				GL11.glRotated(/*rm.playerViewY+*/ay, 0, 0, 1);
				GL11.glRotated(/*rm.playerViewX+*/ax, 1, 0, 0);
				GL11.glTranslated(-d, -d, -d);
				this.drawOuter(te, s, par8, da+1);
				GL11.glPopMatrix();

				GL11.glPushMatrix();
				GL11.glTranslated(d, d, d);
				GL11.glRotated(/*rm.playerViewY+*/ay, 0, 1, 0);
				GL11.glRotated(/*rm.playerViewX+*/ax, 0, 0, 1);
				GL11.glTranslated(-d, -d, -d);
				this.drawOuter(te, s, par8, da+2);
				GL11.glPopMatrix();

				da += 3;
			}
		}

		if (te.hasWorldObj()) {
			GL11.glEnable(GL11.GL_LIGHTING);
		}
		else
			RenderHelper.enableGUIStandardItemLighting();

		GL11.glDisable(GL11.GL_BLEND);
		if (te.hasWorldObj())
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
		GL11.glPopAttrib();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	private void drawInner(TileEntityCrystalMusic te) {
		ReikaTextureHelper.bindTerrainTexture();
		IIcon ico = ChromaIcons.NODE.getIcon();
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
		BlendMode.ADDITIVEDARK.apply();

		GL11.glPushMatrix();

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
		//double ang = (System.currentTimeMillis()/20D)%360;
		//GL11.glRotated(ang, 0, 0, 1);
		v5.startDrawingQuads();
		v5.addVertexWithUV(-1, -1, 0, u, v);
		v5.addVertexWithUV(1, -1, 0, du, v);
		v5.addVertexWithUV(1, 1, 0, du, dv);
		v5.addVertexWithUV(-1, 1, 0, u, dv);
		v5.draw();
		GL11.glEnable(GL11.GL_CULL_FACE);
		BlendMode.DEFAULT.apply();

		GL11.glPopMatrix();
	}

	private void drawMiddle(TileEntityCrystalMusic te) {
		GL11.glPushMatrix();
		GL11.glPopMatrix();
	}

	private void drawOuter(TileEntityCrystalMusic te, double size, float par8, int da) {
		ReikaTextureHelper.bindTerrainTexture();
		BlendMode.ADDITIVEDARK.apply();
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		IIcon ico = ChromaIcons.RIFTHALO.getIcon();
		float u = ico.getMinU();
		float v = ico.getMinV();
		float du = ico.getMaxU();
		float dv = ico.getMaxV();
		double a = 0.625+0.375*Math.sin(24*Math.toRadians(da*8+te.getTicksExisted()+par8));
		this.drawBox((float)a, size, 0, u, v, du, dv);
	}

	private void drawBox(float a, double size, double s, float u, float v, float du, float dv) {
		Tessellator v5 = Tessellator.instance;

		double min = 0.5-size/2D+s;
		double max = 0.5+size/2D-s;

		int alp = (int)(a*255);

		v5.startDrawingQuads();
		v5.setColorRGBA_I(ReikaColorAPI.GStoHex(alp), alp);
		v5.addVertexWithUV(min, min, min, u, v);
		v5.addVertexWithUV(max, min, min, du, v);
		v5.addVertexWithUV(max, min, max, du, dv);
		v5.addVertexWithUV(min, min, max, u, dv);

		v5.addVertexWithUV(min, max, max, u, dv);
		v5.addVertexWithUV(max, max, max, du, dv);
		v5.addVertexWithUV(max, max, min, du, v);
		v5.addVertexWithUV(min, max, min, u, v);

		v5.addVertexWithUV(min, min, min, u, v);
		v5.addVertexWithUV(min, max, min, u, dv);
		v5.addVertexWithUV(max, max, min, du, dv);
		v5.addVertexWithUV(max, min, min, du, v);

		v5.addVertexWithUV(max, min, max, du, v);
		v5.addVertexWithUV(max, max, max, du, dv);
		v5.addVertexWithUV(min, max, max, u, dv);
		v5.addVertexWithUV(min, min, max, u, v);

		v5.addVertexWithUV(min, min, max, du, v);
		v5.addVertexWithUV(min, max, max, du, dv);
		v5.addVertexWithUV(min, max, min, u, dv);
		v5.addVertexWithUV(min, min, min, u, v);

		v5.addVertexWithUV(max, min, min, u, v);
		v5.addVertexWithUV(max, max, min, u, dv);
		v5.addVertexWithUV(max, max, max, du, dv);
		v5.addVertexWithUV(max, min, max, du, v);
		v5.draw();
	}

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "";
	}

}
