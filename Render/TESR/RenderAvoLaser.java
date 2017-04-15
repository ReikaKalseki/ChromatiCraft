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

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Models.ModelAvoLaser;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.TileEntity.AOE.Defence.TileEntityAvoLaser;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

public class RenderAvoLaser extends ChromaRenderBase {

	private final ModelAvoLaser model = new ModelAvoLaser();

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "avolaser.png";
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityAvoLaser te = (TileEntityAvoLaser)tile;
		if (te.hasWorldObj()) {
			GL11.glPushMatrix();
			//ReikaAABBHelper.renderAABB(te.getScanBox(te.worldObj, te.xCoord, te.yCoord, te.zCoord), par2, par4, par6, te.xCoord, te.yCoord, te.zCoord, 255, 255, 255, 255, true);
			GL11.glTranslated(par2, par4, par6);

			int rot = 0;
			int rotx = 0;

			GL11.glPushMatrix();
			int dx = 0;
			int dz = 0;
			int dy = 0;
			switch(te.getFacing()) {
				case WEST:
					rot = 270;
					dx = 1;
					break;
				case EAST:
					rot = 90;
					dz = 1;
					break;
				case NORTH:
					rot = 180;
					dx = 1;
					dz = 1;
					break;
				case SOUTH:
					rot = 0;
					break;
				case UP:
					rotx = 270;
					dz = 1;
					break;
				case DOWN:
					rotx = 90;
					dy = 1;
					break;
				case UNKNOWN:
					break;
			}
			GL11.glTranslated(dx, dy, dz);
			GL11.glRotated(rot, 0, 1, 0);
			GL11.glRotated(rotx, 1, 0, 0);
			this.renderModel(te, model);
			this.renderCrystal(te);

			GL11.glPopMatrix();

			GL11.glPushMatrix();
			rot = 0;
			rotx = 0;
			switch(te.getFacing()) {
				case WEST:
					rot = 180;
					break;
				case EAST:
					break;
				case NORTH:
					rot = 90;
					break;
				case SOUTH:
					rot = 270;
					break;
				case UP:
					rotx = 90;
					break;
				case DOWN:
					rotx = 270;
					break;
				case UNKNOWN:
					break;
			}
			GL11.glTranslated(0.5, 0, 0.5);
			GL11.glRotated(rot, 0, 1, 0);
			GL11.glRotated(rotx, 0, 0, 1);

			if (te.isActive() && MinecraftForgeClient.getRenderPass() == 1) {
				GL11.glPushMatrix();
				float ry = 0;
				if (te.getFacing().offsetY == 0) {
					float rx = RenderManager.instance.playerViewX;
					boolean lx = RenderManager.instance.viewerPosX < te.xCoord+0.5;
					boolean lz = RenderManager.instance.viewerPosZ < te.zCoord+0.5;
					boolean flip = te.getFacing().offsetX == 0 ? lx : lz;
					if (te.getFacing().offsetZ > 0 || te.getFacing().offsetX < 0)
						flip = !flip;
					ry = flip ? rx : -rx;
				}
				else {
					ry = -RenderManager.instance.playerViewY*te.getFacing().offsetY;
					GL11.glTranslated(0.5*te.getFacing().offsetY, -0.5, 0);
				}

				GL11.glPushMatrix();
				GL11.glTranslated(0, 0.5, 0);
				GL11.glRotated(ry, 1, 0, 0);
				GL11.glTranslated(0, -0.5, 0);
				this.renderBeam(te);
				GL11.glPopMatrix();
				GL11.glPopMatrix();
			}

			GL11.glPopMatrix();

			GL11.glPopMatrix();
		}
		else {
			GL11.glPushMatrix();
			GL11.glTranslated(-0.5, -0.6, -0.5);
			this.renderModel(te, model);
			this.renderCrystal(te);
			GL11.glPopMatrix();
		}
	}

	private void renderCrystal(TileEntityAvoLaser te) {
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glTranslated(0.5, 0.5, 0.625);
		double s = 1.825;
		GL11.glScaled(s, s, s);
		GL11.glRotated(90, 1, 0, 0);
		GL11.glRotated(22.5, 0, 1, 0);
		Tessellator v5 = Tessellator.instance;
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		ReikaRenderHelper.disableLighting();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		BlendMode.DEFAULT.apply();

		/*

		double r3 = 0.125;
		double h3 = 0.5;

		v5.startDrawing(GL11.GL_LINES);
		v5.setColorOpaque_I(0xffa0a0);

		v5.addVertex(-r3, 0, h3);
		v5.addVertex(r3, 0, h3);

		v5.draw();

		v5.startDrawing(GL11.GL_TRIANGLES);
		v5.setColorOpaque_I(0xff4040);
		v5.draw();

		 */

		v5.startDrawing(GL11.GL_LINES);

		double h1 = -0.25;
		double h2 = 0;
		double h3 = 0.125;
		double h4 = 0.1875;

		double r1 = 0.0;
		double r2 = 0.25;
		double r3 = 0.1875;

		int n = 45;
		for (int i = 0; i < 360; i += n) {
			v5.setColorOpaque_I(0xffa0a0);

			double a1 = Math.toRadians(i);
			double a2 = Math.toRadians(i+n);
			double x1 = Math.cos(a1);
			double x2 = Math.cos(a2);
			double z1 = Math.sin(a1);
			double z2 = Math.sin(a2);

			double x11 = r1*x1;
			double x12 = r2*x1;
			double x13 = r3*x1;
			double x21 = r1*x2;
			double x22 = r2*x2;
			double x23 = r3*x2;

			double z11 = r1*z1;
			double z12 = r2*z1;
			double z13 = r3*z1;
			double z21 = r1*z2;
			double z22 = r2*z2;
			double z23 = r3*z2;

			v5.addVertex(x11, h1, z11);
			v5.addVertex(x12, h2, z12);

			v5.addVertex(x12, h2, z12);
			v5.addVertex(x22, h2, z22);

			v5.addVertex(x12, h2, z12);
			v5.addVertex(x12, h3, z12);

			v5.addVertex(x12, h3, z12);
			v5.addVertex(x22, h3, z22);

			v5.addVertex(x12, h3, z12);
			v5.addVertex(x13, h4, z13);

			v5.addVertex(x13, h4, z13);
			v5.addVertex(x23, h4, z23);
		}

		v5.draw();
		v5.startDrawingQuads();

		for (int i = 0; i < 360; i += n) {
			int a = 212;
			float f = 0.875F+0.125F*(float)Math.sin(Math.toRadians(i*2+3*(RenderManager.instance.playerViewX+RenderManager.instance.playerViewY)));
			v5.setColorOpaque_I(ReikaColorAPI.getColorWithBrightnessMultiplier(0xff4040, f));

			double a1 = Math.toRadians(i);
			double a2 = Math.toRadians(i+n);
			double x1 = Math.cos(a1);
			double x2 = Math.cos(a2);
			double z1 = Math.sin(a1);
			double z2 = Math.sin(a2);

			double x11 = r1*x1;
			double x12 = r2*x1;
			double x13 = r3*x1;
			double x21 = r1*x2;
			double x22 = r2*x2;
			double x23 = r3*x2;

			double z11 = r1*z1;
			double z12 = r2*z1;
			double z13 = r3*z1;
			double z21 = r1*z2;
			double z22 = r2*z2;
			double z23 = r3*z2;

			v5.addVertex(x11, h1, z11);
			v5.addVertex(x12, h2, z12);
			v5.addVertex(x22, h2, z22);
			v5.addVertex(x22, h2, z22);


			v5.addVertex(x12, h3, z12);
			v5.addVertex(x22, h3, z22);
			v5.addVertex(x22, h2, z22);
			v5.addVertex(x12, h2, z12);


			v5.addVertex(x12, h3, z12);
			v5.addVertex(x13, h4, z13);
			v5.addVertex(x23, h4, z23);
			v5.addVertex(x22, h3, z22);

			v5.addVertex(x11, h4, z11);
			v5.addVertex(x11, h4, z11);
			v5.addVertex(x23, h4, z23);
			v5.addVertex(x13, h4, z13);
		}

		v5.draw();

		GL11.glPopMatrix();
		GL11.glPopAttrib();
	}

	private void renderBeam(TileEntityAvoLaser te) {
		Tessellator v5 = Tessellator.instance;
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		ReikaRenderHelper.disableLighting();
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_BLEND);
		BlendMode.ADDITIVEDARK.apply();
		ReikaTextureHelper.bindTerrainTexture();
		IIcon ico = ChromaIcons.AVOLASER.getIcon();
		float u = ico.getMinU();
		float v = ico.getMinV();
		float du = ico.getMaxU();
		float dv = ico.getMaxV();

		IIcon ico2 = ChromaIcons.AVOLASER_CORE.getIcon();
		float u2 = ico2.getMinU();
		float v2 = ico2.getMinV();
		float du2 = ico2.getMaxU();
		float dv2 = ico2.getMaxV();

		int r1 = te.getBeamStart();
		int r2 = te.getBeamEnd();

		v5.startDrawingQuads();
		v5.setColorOpaque_I(0xff2020);
		for (int i = r1; i < r2+1; i++) {
			v5.addVertexWithUV(i, 0, 0, u, v);
			v5.addVertexWithUV(i+1, 0, 0, du, v);
			v5.addVertexWithUV(i+1, 1, 0, du, dv);
			v5.addVertexWithUV(i, 1, 0, u, dv);
		}

		v5.setColorOpaque_I(0xffcaca);
		for (int i = r1; i < r2+1; i++) {
			v5.addVertexWithUV(i, 0, 0, u2, v2);
			v5.addVertexWithUV(i+1, 0, 0, du2, v2);
			v5.addVertexWithUV(i+1, 1, 0, du2, dv2);
			v5.addVertexWithUV(i, 1, 0, u2, dv2);
		}

		v5.draw();

		GL11.glPopAttrib();
	}

}
