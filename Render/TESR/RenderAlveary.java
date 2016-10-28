/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render.TESR;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Auxiliary.Render.ChromaFontRenderer;
import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.ModInterface.TileEntityLumenAlveary;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaVectorHelper;
import Reika.DragonAPI.ModInteract.Bees.ReikaBeeHelper;


public class RenderAlveary extends ChromaRenderBase {

	private IIcon[] overlay = {
			ChromaIcons.HIVE.getIcon(),
	};

	public static boolean renderBeeGlint = true;

	@Override
	public String getImageFileName(RenderFetcher te) {
		return null;
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityLumenAlveary te = (TileEntityLumenAlveary)tile;
		if (te.isInWorld() && MinecraftForgeClient.getRenderPass() == 1) {
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glPushMatrix();

			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_LIGHTING);
			ReikaRenderHelper.disableEntityLighting();
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			GL11.glDepthMask(false);
			GL11.glTranslated(par2, par4, par6);
			Coordinate c = te.getRelativeLocation();
			if (c != null)
				GL11.glTranslated(-c.xCoord, -c.yCoord, -c.zCoord);

			if (te.isAlvearyComplete()) {

				double t = te.getTicksExisted()+par8+te.xCoord+te.zCoord*0.5D+te.yCoord*1.5D;

				if (te.hasQueen()) {
					this.renderBee(te, t, par8);
				}
				BlendMode.ADDITIVEDARK.apply();
				GL11.glShadeModel(GL11.GL_SMOOTH);
				ReikaTextureHelper.bindTerrainTexture();
				this.renderOverlay(te, t, par8);
			}
			else if (te.hasMultipleBoosters()) {
				BlendMode.DEFAULT.apply();
				GL11.glShadeModel(GL11.GL_SMOOTH);
				ReikaTextureHelper.bindTerrainTexture();

				double o = 0.0025;

				Tessellator v5 = Tessellator.instance;
				v5.startDrawingQuads();

				IIcon ico = ChromaIcons.NOENTER.getIcon();
				float u = ico.getMinU();
				float v = ico.getMinV();
				float du = ico.getMaxU();
				float dv = ico.getMaxV();

				v5.setColorOpaque_I(0xffffff);
				v5.addVertexWithUV(0-o, 3.25, 0-o, u, dv);
				v5.addVertexWithUV(3+o, 3.25, 0-o, du, dv);
				v5.addVertexWithUV(3+o, 0.25, 0-o, du, v);
				v5.addVertexWithUV(0-o, 0.25, 0-o, u, v);

				v5.addVertexWithUV(0-o, 0.25, 3+o, u, v);
				v5.addVertexWithUV(3+o, 0.25, 3+o, du, v);
				v5.addVertexWithUV(3+o, 3.25, 3+o, du, dv);
				v5.addVertexWithUV(0-o, 3.25, 3+o, u, dv);


				v5.addVertexWithUV(3+o, 3.25, 0-o, u, dv);
				v5.addVertexWithUV(3+o, 3.25, 3+o, du, dv);
				v5.addVertexWithUV(3+o, 0.25, 3+o, du, v);
				v5.addVertexWithUV(3+o, 0.25, 0-o, u, v);


				v5.addVertexWithUV(0-o, 0.25, 0-o, u, v);
				v5.addVertexWithUV(0-o, 0.25, 3+o, du, v);
				v5.addVertexWithUV(0-o, 3.25, 3+o, du, dv);
				v5.addVertexWithUV(0-o, 3.25, 0-o, u, dv);


				v5.addVertexWithUV(0-o, 3.5+o, 3+o, u, dv);
				v5.addVertexWithUV(3+o, 3.5+o, 3+o, du, dv);
				v5.addVertexWithUV(3+o, 3.5+o, 0-o, du, v);
				v5.addVertexWithUV(0-o, 3.5+o, 0-o, u, v);

				v5.draw();
			}

			GL11.glPopAttrib();
			GL11.glPopMatrix();
		}
	}

	private void renderBee(TileEntityLumenAlveary te, double t, float par8) {

		EntityItem ei = te.getRenderItem();
		if (ei != null) {
			EntityPlayer ep = Minecraft.getMinecraft().thePlayer;

			Vec3 look = ep.getLookVec();
			Coordinate c = te.getRelativeLocation();
			Vec3 rel = Vec3.createVectorHelper(te.xCoord-c.xCoord+1.5-ep.posX, te.yCoord-c.yCoord+2.5-ep.posY, te.zCoord-c.zCoord+1.5-ep.posZ);
			double ang = ReikaVectorHelper.getAngleBetween(look, rel);

			boolean text = Math.abs(ang) <= 18;

			GL11.glPushMatrix();
			GL11.glTranslated(1.5, text ? 4.375 : 4.25, 1.5);
			GL11.glTranslated(0, 0.25*Math.sin(t/8D), 0);
			GL11.glPushMatrix();
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			Tessellator v5 = Tessellator.instance;

			ItemStack is = ei.getEntityItem();
			ei.age = 0;
			ei.hoverStart = 0;//(float)Math.sin(t/8 + 0.2);
			ei.rotationYaw = 0;

			GL11.glDisable(GL11.GL_CULL_FACE);
			BlendMode.ADDITIVE2.apply();
			GL11.glDepthMask(true);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
			GL11.glAlphaFunc(GL11.GL_GREATER, 1/255F);
			RenderItem.renderInFrame = true;
			double s = 4;//text ? 4 : 2;
			GL11.glRotated(t%360D, 0, 1, 0);
			GL11.glScaled(s, s, s);
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glPushMatrix();
			renderBeeGlint = false;
			RenderManager.instance.renderEntityWithPosYaw(ei, 0, 0, 0, 0, 0/*tick*/);
			GL11.glPopAttrib();
			GL11.glPopMatrix();
			RenderItem.renderInFrame = false;
			renderBeeGlint = true;

			if (!te.canQueenWork()) {
				GL11.glRotated(90, 0, 1, 0);
				GL11.glTranslated(0, -0.25/s, -1/s);
				ReikaTextureHelper.bindTerrainTexture();
				IIcon ico = ChromaIcons.NOENTER.getIcon();
				float u = ico.getMinU();
				float v = ico.getMinV();
				float du = ico.getMaxU();
				float dv = ico.getMaxV();
				v5.startDrawingQuads();
				v5.addVertexWithUV(0, 0, 0, u, v);
				v5.addVertexWithUV(0, 2/s, 0, du, v);
				v5.addVertexWithUV(0, 2/s, 2/s, du, dv);
				v5.addVertexWithUV(0, 0, 2/s, u, dv);
				v5.draw();

				BlendMode.DEFAULT.apply();

				v5.startDrawingQuads();
				v5.setColorRGBA_I(0xffffff, 64);
				v5.addVertexWithUV(0, 0, 0, u, v);
				v5.addVertexWithUV(0, 2/s, 0, du, v);
				v5.addVertexWithUV(0, 2/s, 2/s, du, dv);
				v5.addVertexWithUV(0, 0, 2/s, u, dv);
				v5.draw();
			}
			GL11.glDepthMask(false);
			GL11.glDisable(GL11.GL_ALPHA_TEST);

			GL11.glPopMatrix();
			GL11.glPushMatrix();

			if (text) {
				double s2 = 0.0625;
				GL11.glScaled(s2, -s2, s2);
				String sg = ReikaBeeHelper.getBee(is).getDisplayName();
				FontRenderer f = ChromaFontRenderer.FontType.GUI.renderer;

				double a = 180+ReikaPhysicsHelper.cartesianToPolar(RenderManager.renderPosX-te.xCoord, RenderManager.renderPosY-te.yCoord, RenderManager.renderPosZ-te.zCoord)[2];
				GL11.glRotated(a, 0, 1, 0);
				GL11.glTranslated(-f.getStringWidth(sg)/32D/s2, 0.1875D/s2-ei.hoverStart*0.375/s2-0.125/s2, 0);
				f.drawString(sg, 0, 0, 0xffffff);
			}
			GL11.glPopAttrib();
			GL11.glPopMatrix();
			GL11.glPopMatrix();
		}
	}

	private void renderOverlay(TileEntityLumenAlveary te, double t, float par8) {

		int ca = 0xffa020;
		int cb = 0x604020;

		ca = ReikaColorAPI.getColorWithBrightnessMultiplier(ca, 0.5F);
		cb = ReikaColorAPI.getColorWithBrightnessMultiplier(cb, 0.25F);

		/*
		float f1 = 0.5F+0.5F*(float)Math.sin((te.getTicksExisted()+par8)/6D);
		float f2 = 0.5F+0.5F*(float)Math.sin(Math.PI*0.5+(te.getTicksExisted()+par8)/6D);
		int c2 = ReikaColorAPI.mixColors(ca, cb, f1);
		int c1 = ReikaColorAPI.mixColors(ca, cb, f2);
		 */

		double o = 0.0025;

		int[][][] clr = new int[2][2][2];
		for (int i = 0; i < clr.length; i++) {
			for (int j = 0; j < clr[i].length; j++) {
				for (int k = 0; k < clr[i][j].length; k++) {
					float f = 0.5F+0.5F*(float)Math.sin(i*1.5+j*3.5+k+t/6D);
					f += 0.25F*(float)Math.cos(i*2.5+j*2+k*1+t/4D);
					f = MathHelper.clamp_float(f, 0, 1);
					clr[i][j][k] = ReikaColorAPI.mixColors(ca, cb, f);
				}
			}
		}

		Tessellator v5 = Tessellator.instance;
		v5.startDrawingQuads();

		overlay[0] = ChromaIcons.HIVE.getIcon();

		for (int i = 0; i < overlay.length; i++) {
			IIcon ico = overlay[i];
			float u = ico.getMinU();
			float v = ico.getMinV();
			float du = ico.getMaxU();
			float dv = ico.getMaxV();

			v5.setColorOpaque_I(clr[0][1][0]);
			v5.addVertexWithUV(0-o, 3.5, 0-o, u, dv);
			v5.setColorOpaque_I(clr[1][1][0]);
			v5.addVertexWithUV(3+o, 3.5, 0-o, du, dv);
			v5.setColorOpaque_I(clr[0][0][0]);
			v5.addVertexWithUV(3+o, 0, 0-o, du, v);
			v5.setColorOpaque_I(clr[0][0][0]);
			v5.addVertexWithUV(0-o, 0, 0-o, u, v);

			v5.setColorOpaque_I(clr[0][0][1]);
			v5.addVertexWithUV(0-o, 0, 3+o, u, v);
			v5.setColorOpaque_I(clr[1][0][1]);
			v5.addVertexWithUV(3+o, 0, 3+o, du, v);
			v5.setColorOpaque_I(clr[1][1][1]);
			v5.addVertexWithUV(3+o, 3.5, 3+o, du, dv);
			v5.setColorOpaque_I(clr[0][1][1]);
			v5.addVertexWithUV(0-o, 3.5, 3+o, u, dv);


			v5.setColorOpaque_I(clr[1][1][0]);
			v5.addVertexWithUV(3+o, 3.5, 0-o, u, dv);
			v5.setColorOpaque_I(clr[1][1][1]);
			v5.addVertexWithUV(3+o, 3.5, 3+o, du, dv);
			v5.setColorOpaque_I(clr[1][0][1]);
			v5.addVertexWithUV(3+o, 0, 3+o, du, v);
			v5.setColorOpaque_I(clr[1][0][0]);
			v5.addVertexWithUV(3+o, 0, 0-o, u, v);


			v5.setColorOpaque_I(clr[0][0][0]);
			v5.addVertexWithUV(0-o, 0, 0-o, u, v);
			v5.setColorOpaque_I(clr[0][0][1]);
			v5.addVertexWithUV(0-o, 0, 3+o, du, v);
			v5.setColorOpaque_I(clr[0][1][1]);
			v5.addVertexWithUV(0-o, 3.5, 3+o, du, dv);
			v5.setColorOpaque_I(clr[0][1][0]);
			v5.addVertexWithUV(0-o, 3.5, 0-o, u, dv);


			v5.setColorOpaque_I(clr[0][1][1]);
			v5.addVertexWithUV(0-o, 3.5+o, 3+o, u, dv);
			v5.setColorOpaque_I(clr[1][1][1]);
			v5.addVertexWithUV(3+o, 3.5+o, 3+o, du, dv);
			v5.setColorOpaque_I(clr[1][1][0]);
			v5.addVertexWithUV(3+o, 3.5+o, 0-o, du, v);
			v5.setColorOpaque_I(clr[0][1][0]);
			v5.addVertexWithUV(0-o, 3.5+o, 0-o, u, v);
		}

		v5.draw();
	}

}
