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

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Block.BlockCrystalPlant;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Plants.TileEntityCrystalPlant;
import Reika.DragonAPI.Interfaces.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

public class CrystalPlantRenderer extends ChromaRenderBase {

	@Override
	public void renderTileEntityAt(TileEntity te, double par2, double par4, double par6, float f) {
		TileEntityCrystalPlant tile = (TileEntityCrystalPlant)te;
		if (tile.renderPod()) {
			GL11.glPushMatrix();
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glTranslatef((float)par2, (float)par4 + 1.0F, (float)par6 + 1.0F);
			GL11.glScalef(1.0F, -1.0F, -1.0F);

			ReikaTextureHelper.bindTerrainTexture();
			//this.drawInner(tile);
			GL11.glTranslated(0, 0.0625, 0);
			double d = 0.625;
			double a1 = 0.2;
			double b1 = 0;
			double c1 = 0.2;
			if (!tile.emitsLight()) {
				GL11.glTranslated(a1, b1, c1);
				GL11.glScaled(d, d, d);
			}
			this.drawBulb(tile);
			if (!tile.emitsLight()) {
				GL11.glScaled(1/d, 1/d, 1/d);
				GL11.glTranslated(-a1, -b1, -c1);
			}
			GL11.glTranslated(0, -0.0625, 0);

			if (tile.emitsLight()) {
				GL11.glColor4f(1, 1, 1, 1);
				CrystalElement dye = tile.getColor();
				GL11.glColor4f(dye.getRed()/255F, dye.getGreen()/255F, dye.getBlue()/255F, 1);
				double s = 0.25;
				GL11.glTranslated(0.5, -0.25, 0.5);
				GL11.glScaled(s, s, s);
				RenderManager rm = RenderManager.instance;
				GL11.glRotatef(rm.playerViewY, 0.0F, 1.0F, 0.0F);
				GL11.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);
				BlendMode.ADDITIVEDARK.apply();
				this.drawGlow(tile);
				this.drawSparkle(tile);
				GL11.glScaled(1/s, 1/s, 1/s);
				BlendMode.DEFAULT.apply();
			}

			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_BLEND);
			if (te.hasWorldObj())
				GL11.glDisable(GL12.GL_RESCALE_NORMAL);
			GL11.glPopMatrix();
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		}
	}

	private void drawSparkle(TileEntityCrystalPlant tile) {
		Tessellator v5 = Tessellator.instance;
		IIcon ico = ChromaIcons.SPARKLE.getIcon();
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

	private void drawGlow(TileEntityCrystalPlant tile) {
		Tessellator v5 = Tessellator.instance;
		IIcon ico = ChromaIcons.RADIATE.getIcon();
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

	private void drawBulb(TileEntityCrystalPlant tile) {
		Tessellator v5 = Tessellator.instance;
		IIcon ico = ((BlockCrystalPlant)ChromaBlocks.PLANT.getBlockInstance()).getBulbIcon(tile.getColor());
		float u = ico.getMinU();
		float v = ico.getMinV();
		float du = ico.getMaxU();
		float dv = ico.getMaxV();
		float mu = u+(du-u)/2;
		float mv = v+(dv-v)/2;

		double s = 0.1;
		double h = 0.3;
		double ph = 0.15;

		GL11.glColor4f(1, 1, 1, 0.25F);

		CrystalElement dye = tile.getColor();
		//GL11.glColor4f(dye.getRed()/255F, dye.getGreen()/255F, dye.getBlue()/255F, 1);
		BlendMode.DEFAULT.apply();
		v5.startDrawingQuads();
		int color = ReikaColorAPI.getModifiedSat(dye.getColor(), 0.75F);
		v5.setColorRGBA_I(color, 255);
		//Bottom
		v5.addVertexWithUV(0.5-s, 0, 0.5+s, u, dv);
		v5.addVertexWithUV(0.5+s, 0, 0.5+s, du, dv);
		v5.addVertexWithUV(0.5+s, 0, 0.5-s, du, v);
		v5.addVertexWithUV(0.5-s, 0, 0.5-s, u, v);

		//Top point
		v5.addVertexWithUV(0.5-s, -h, 0.5-s, u, dv);
		v5.addVertexWithUV(0.5, -h-ph/2, 0.5-s, mu, dv);
		v5.addVertexWithUV(0.5, -h-ph, 0.5, mu, mv);
		v5.addVertexWithUV(0.5-s, -h-ph/2, 0.5, u, mv);

		v5.addVertexWithUV(0.5-s, -h-ph/2, 0.5, u, mv);
		v5.addVertexWithUV(0.5, -h-ph, 0.5, mu, mv);
		v5.addVertexWithUV(0.5, -h-ph/2, 0.5+s, mu, v);
		v5.addVertexWithUV(0.5-s, -h, 0.5+s, u, v);

		v5.addVertexWithUV(0.5+s, -h-ph/2, 0.5, u, mv);
		v5.addVertexWithUV(0.5, -h-ph, 0.5, mu, mv);
		v5.addVertexWithUV(0.5, -h-ph/2, 0.5-s, mu, v);
		v5.addVertexWithUV(0.5+s, -h, 0.5-s, u, v);

		v5.addVertexWithUV(0.5+s, -h, 0.5+s, u, dv);
		v5.addVertexWithUV(0.5, -h-ph/2, 0.5+s, mu, dv);
		v5.addVertexWithUV(0.5, -h-ph, 0.5, mu, mv);
		v5.addVertexWithUV(0.5+s, -h-ph/2, 0.5, u, mv);


		v5.addVertexWithUV(0.5+s, 0, 0.5, u, mv);
		v5.addVertexWithUV(0.5+s, -h-ph/2, 0.5, du, mv);
		v5.addVertexWithUV(0.5+s, -h, 0.5-s, du, v);
		v5.addVertexWithUV(0.5+s, 0, 0.5-s, u, v);

		v5.addVertexWithUV(0.5+s, 0, 0.5+s, u, dv);
		v5.addVertexWithUV(0.5+s, -h, 0.5+s, du, dv);
		v5.addVertexWithUV(0.5+s, -h-ph/2, 0.5, du, mv);
		v5.addVertexWithUV(0.5+s, 0, 0.5, u, mv);

		v5.addVertexWithUV(0.5-s, 0, 0.5, u, mv);
		v5.addVertexWithUV(0.5-s, -h-ph/2, 0.5, du, mv);
		v5.addVertexWithUV(0.5-s, -h, 0.5+s, du, dv);
		v5.addVertexWithUV(0.5-s, 0, 0.5+s, u, dv);

		v5.addVertexWithUV(0.5-s, 0, 0.5-s, u, v);
		v5.addVertexWithUV(0.5-s, -h, 0.5-s, du, v);
		v5.addVertexWithUV(0.5-s, -h-ph/2, 0.5, du, mv);
		v5.addVertexWithUV(0.5-s, 0, 0.5, u, mv);

		v5.addVertexWithUV(0.5, 0, 0.5+s, u, mv);
		v5.addVertexWithUV(0.5, -h-ph/2, 0.5+s, du, mv);
		v5.addVertexWithUV(0.5+s, -h, 0.5+s, du, dv);
		v5.addVertexWithUV(0.5+s, 0, 0.5+s, u, dv);

		v5.addVertexWithUV(0.5-s, 0, 0.5+s, u, v);
		v5.addVertexWithUV(0.5-s, -h, 0.5+s, du, v);
		v5.addVertexWithUV(0.5, -h-ph/2, 0.5+s, du, mv);
		v5.addVertexWithUV(0.5, 0, 0.5+s, u, mv);

		v5.addVertexWithUV(0.5+s, 0, 0.5-s, u, dv);
		v5.addVertexWithUV(0.5+s, -h, 0.5-s, du, dv);
		v5.addVertexWithUV(0.5, -h-ph/2, 0.5-s, du, mv);
		v5.addVertexWithUV(0.5, 0, 0.5-s, u, mv);

		v5.addVertexWithUV(0.5, 0, 0.5-s, u, mv);
		v5.addVertexWithUV(0.5, -h-ph/2, 0.5-s, du, mv);
		v5.addVertexWithUV(0.5-s, -h, 0.5-s, du, v);
		v5.addVertexWithUV(0.5-s, 0, 0.5-s, u, v);
		v5.draw();

		BlendMode.DEFAULT.apply();
	}


	private void drawInner(TileEntityCrystalPlant te) {
		ReikaTextureHelper.bindTerrainTexture();
		IIcon ico = ChromaIcons.GUARDIANINNER.getIcon();
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

		double s = 0.5;
		if (te.hasWorldObj()) {
			GL11.glTranslated(0.5, -0.15, 0.5);
			GL11.glScaled(s, s, s);
			RenderManager rm = RenderManager.instance;
			GL11.glRotatef(rm.playerViewY, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);
		}
		else {
			GL11.glTranslated(0.5, 0.5, 0.5);
			GL11.glScaled(s, s, s);
			GL11.glRotated(-90, 0, 1, 0);
		}
		//double ang = (System.currentTimeMillis()/20D)%360;
		//GL11.glRotated(ang, 0, 0, 1);
		v5.startDrawingQuads();
		v5.addVertexWithUV(-1, -1, 0, u, v);
		v5.addVertexWithUV(1, -1, 0, du, v);
		v5.addVertexWithUV(1, 1, 0, du, dv);
		v5.addVertexWithUV(-1, 1, 0, u, dv);
		v5.draw();
		BlendMode.DEFAULT.apply();

		if (te.hasWorldObj()) {
			RenderManager rm = RenderManager.instance;
			GL11.glRotatef(-rm.playerViewX, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(-rm.playerViewY, 0.0F, 1.0F, 0.0F);
			GL11.glScaled(1/s, 1/s, 1/s);
			GL11.glTranslated(-0.5, 0.15, -0.5);
		}
		else {
			GL11.glRotated(90, 0, 1, 0);
			GL11.glScaled(1/s, 1/s, 1/s);
			GL11.glTranslated(-0.5, -0.5, -0.5);
		}
	}

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "";
	}

}
