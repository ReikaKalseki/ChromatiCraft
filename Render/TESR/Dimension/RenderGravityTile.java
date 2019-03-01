/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render.TESR.Dimension;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;

import Reika.ChromatiCraft.Auxiliary.ChromaFX;
import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Block.Dimension.Structure.Gravity.BlockGravityTile;
import Reika.ChromatiCraft.Block.Dimension.Structure.Gravity.BlockGravityTile.GravityTarget;
import Reika.ChromatiCraft.Block.Dimension.Structure.Gravity.BlockGravityTile.GravityTile;
import Reika.ChromatiCraft.Block.Dimension.Structure.Gravity.BlockGravityTile.GravityTiles;
import Reika.ChromatiCraft.Block.Dimension.Structure.Gravity.BlockGravityTile.GravityWarp;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;


public class RenderGravityTile extends ChromaRenderBase {

	//private final RandomVariance randomX = new RandomVariance(0.0625, 0.0625, 0.03125/32D);
	//private final RandomVariance randomY = new RandomVariance(0.03125, 0.0625, 0.03125/32D);
	//private final RandomVariance randomZ = new RandomVariance(0.0625, 0.0625, 0.03125/32D);

	@Override
	public String getImageFileName(RenderFetcher te) {
		return null;
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glPushMatrix();

		GravityTile te = (GravityTile)tile;
		GL11.glTranslated(par2, par4, par6);
		GL11.glDisable(GL11.GL_LIGHTING);
		ReikaRenderHelper.disableEntityLighting();

		Tessellator v5 = Tessellator.instance;
		ReikaTextureHelper.bindTerrainTexture();
		IIcon icon = BlockGravityTile.getOverlay();
		float u = icon.getMinU();
		float v = icon.getMinV();
		float du = icon.getMaxU();
		float dv = icon.getMaxV();
		double h = te.getBlockType().getBlockBoundsMaxY()-0.005;
		v5.startDrawingQuads();
		v5.setBrightness(240);
		v5.setColorOpaque_I(0xffffff);
		v5.addVertexWithUV(0, h, 1, u, dv);
		v5.addVertexWithUV(1, h, 1, du, dv);
		v5.addVertexWithUV(1, h, 0, du, v);
		v5.addVertexWithUV(0, h, 0, u, v);
		v5.draw();

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glDepthMask(false);
		BlendMode.ADDITIVEDARK.apply();

		double t = System.currentTimeMillis();

		double s = 0.75;
		boolean flag = false;
		if (te instanceof GravityTarget) {
			GravityTarget gt = (GravityTarget)te;
			GL11.glPushMatrix();
			GL11.glTranslated(0.5, te.getBlockType().getBlockBoundsMaxY()+0.0125, 0.5);
			GL11.glRotated(90, 1, 0, 0);
			if (gt.timer != null) {
				gt.timer.render(0.5, par8);
				flag = true;
			}
			float f = gt.getFillFraction();
			s *= 0.25+f/2;
			if (f >= 1) {
				ReikaTextureHelper.bindTerrainTexture();
				if (f > 1.0625) {
					double shake = 0.25*Math.pow(f-1.0625, 2);
					if (shake > 0) {
						double shakex = shake*Math.sin(System.currentTimeMillis()/10D);
						double shakez = shake*Math.cos(System.currentTimeMillis()/20D);
						GL11.glTranslated(shakex, shakez, 0);
					}
				}
				ChromaIcons[] ico = new ChromaIcons[]{ChromaIcons.CONCENTRIC2REV, ChromaIcons.HEXFLARE};
				for (int i = 0; i < ico.length; i++) {
					GL11.glTranslated(0, 0.005, 0);
					icon = ico[i].getIcon();
					u = icon.getMinU();
					v = icon.getMinV();
					du = icon.getMaxU();
					dv = icon.getMaxV();

					if (i == 1) {
						GL11.glRotated((t/10D)%360D, 0, 0, 1);
					}

					v5.startDrawingQuads();
					int c = i == 0 ? 0xffffff : te.getColor().getColor();
					v5.setColorOpaque_I(c);
					v5.addVertexWithUV(-1, 1, 0, u, dv);
					v5.addVertexWithUV(1, 1, 0, du, dv);
					v5.addVertexWithUV(1, -1, 0, du, v);
					v5.addVertexWithUV(-1, -1, 0, u, v);
					v5.draw();
				}
			}
			else {
				float cm = (float)(0.8125+0.1875*Math.sin(System.currentTimeMillis()/500D));
				int c = ReikaColorAPI.mixColors(te.getColor().getColor(), 0xffffff, cm);
				ChromaFX.drawRadialFillbar(f, c, false);
			}
			GL11.glPopMatrix();
		}

		//GL11.glTranslated(0.5, 0.75, 0.5);
		if (flag) {
			s = 0.375;
			/*
			double f = Math.min(1, (double)te.timer.getTotalTick()/te.timer.getTotalDuration());
			if (f >= 0.75)
				s = 0.375+0.375*4*(f-0.75);
			else if (f < 0.0625/2)
				s = 0.375+(0.0625/2-f)*16*2*0.375;
			 */
		}
		else {
			/*
			double shake = 8*(te.getFillFraction()-1);
			if (shake > 0) {
				randomX.update();
				randomY.update();
				randomZ.update();
				GL11.glTranslated(shake*randomX.getValue(), shake*randomY.getValue(), shake*randomZ.getValue());
			}
			 */
		}

		ChromaIcons ico = te.getTileType().icon;
		if (ico != null) {
			GL11.glPushMatrix();
			//RenderManager rm = RenderManager.instance;
			//GL11.glRotatef(180-rm.playerViewY, 0.0F, 1.0F, 0.0F);
			//GL11.glRotatef(-rm.playerViewX, 1.0F, 0.0F, 0.0F);

			GL11.glTranslated(0.5, te.getBlockType().getBlockBoundsMaxY()+0.0125, 0.5);
			GL11.glRotated(90, 1, 0, 0);

			GL11.glRotated(-te.getFacing().angle-90, 0, 0, 1);
			if (te.getTileType().isOmniDirectional()) {
				double ang = (System.currentTimeMillis()/8D)%360D;
				GL11.glRotated(ang, 0, 0, 1);
			}

			GL11.glScaled(s, s, s);

			icon = ico.getIcon();
			ReikaTextureHelper.bindTerrainTexture();
			u = icon.getMinU();
			v = icon.getMinV();
			du = icon.getMaxU();
			dv = icon.getMaxV();

			v5.startDrawingQuads();
			v5.setColorOpaque_I(ReikaColorAPI.getColorWithBrightnessMultiplier(te.getColor().getColor(), te.renderAlpha/255F));
			v5.addVertexWithUV(-1, 1, 0, u, dv);
			v5.addVertexWithUV(1, 1, 0, du, dv);
			v5.addVertexWithUV(1, -1, 0, du, v);
			v5.addVertexWithUV(-1, -1, 0, u, v);
			v5.draw();

			GL11.glPopMatrix();
		}

		if (te.getTileType() == GravityTiles.TINTER) {
			icon = ChromaIcons.BATTERY.getIcon();
			ReikaTextureHelper.bindTerrainTexture();
			u = icon.getMinU();
			v = icon.getMinV();
			du = icon.getMaxU();
			dv = icon.getMaxV();

			GL11.glTranslated(0.5, te.getBlockType().getBlockBoundsMaxY()-0.0025, 0.5);
			GL11.glRotated(90, 1, 0, 0);
			GL11.glScaled(0.5, 0.5, 0.5);
			v5.startDrawingQuads();
			float f = (float)(0.75+0.125*Math.sin(t/500D));
			v5.setColorOpaque_I(ReikaColorAPI.getColorWithBrightnessMultiplier(te.getColor().getColor(), te.renderAlpha/255F*f));
			v5.addVertexWithUV(-1, 1, 0, u, dv);
			v5.addVertexWithUV(1, 1, 0, du, dv);
			v5.addVertexWithUV(1, -1, 0, du, v);
			v5.addVertexWithUV(-1, -1, 0, u, v);
			v5.draw();
		}

		double gr = te.getTileType().gravityRange;
		if (gr > 0) {
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glPushMatrix();
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glTranslated(0.5, te.getBlockType().getBlockBoundsMaxY()/2, 0.5);

			if (te.gravityDisplay != null) {
				te.gravityDisplay.render(0, 0, 0, 0xffffffff);
				te.gravityDisplay.update();
			}

			/*
			v5.startDrawing(GL11.GL_LINES);
			for (double a = 0; a < 360; a += 15) {
				double ang = Math.toRadians(a);
				double dx = gr*Math.cos(ang);
				double dz = gr*Math.sin(ang);
				v5.addVertex(dx, 0, dz);
				v5.addVertex(dx, 0.5, dz);
			}
			v5.draw();
			 */

			GL11.glPopAttrib();
			GL11.glPopMatrix();
		}

		if (te instanceof GravityWarp) {
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glPushMatrix();
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GravityWarp gw = (GravityWarp)te;
			Coordinate c = gw.getLink();
			GL11.glTranslated(0.5, te.getBlockType().getBlockBoundsMaxY()/2, 0.5);
			if (c != null && gw.linkRender != null) {
				gw.linkRender.update();
				ChromaFX.renderBolt(gw.linkRender, par8, 192, 0.1875, 6);
			}
			GL11.glPopAttrib();
			GL11.glPopMatrix();
		}

		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

}
