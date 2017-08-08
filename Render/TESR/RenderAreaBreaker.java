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

import java.util.Collection;
import java.util.Map;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.MinecraftForgeClient;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import Reika.ChromatiCraft.Auxiliary.ChromaFX;
import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityAreaBreaker;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Effects.LightningBolt;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

public class RenderAreaBreaker extends ChromaRenderBase {

	@Override
	public String getImageFileName(RenderFetcher te) {
		return null;
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityAreaBreaker te = (TileEntityAreaBreaker)tile;

		if (MinecraftForgeClient.getRenderPass() == 1 || !te.isInWorld()) {

			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glPushMatrix();
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_LIGHTING);
			BlendMode.ADDITIVEDARK.apply();
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glTranslatef((float)par2, (float)par4, (float)par6);

			GL11.glPushMatrix();
			double dy = 0.125*Math.sin((te.getTicksExisted()+par8)*0.125);
			GL11.glTranslated(0, dy, 0);
			GL11.glTranslated(0.5, 0.5, 0.5);
			GL11.glRotated((te.getTicksExisted()+par8)/2, 1, 0, 0);
			GL11.glRotated(te.getTicksExisted()+par8, 0, 1, 0);
			GL11.glRotated((te.getTicksExisted()+par8)/4, 0, 0, 1);
			GL11.glTranslated(-0.5, -0.5, -0.5);
			GL11.glDisable(GL11.GL_CULL_FACE);
			Tessellator v5 = Tessellator.instance;
			ReikaTextureHelper.bindTerrainTexture();
			IIcon ico = ChromaIcons.HOLE.getIcon();
			float u = ico.getMinU();
			float du = ico.getMaxU();
			float v = ico.getMinV();
			float dv = ico.getMaxV();
			v5.startDrawingQuads();
			v5.setBrightness(240);
			v5.setColorOpaque_I(0xffffff);
			v5.addVertexWithUV(0, 0, 0.5, u, v);
			v5.addVertexWithUV(1, 0, 0.5, du, v);
			v5.addVertexWithUV(1, 1, 0.5, du, dv);
			v5.addVertexWithUV(0, 1, 0.5, u, dv);

			v5.addVertexWithUV(0.5, 0, 0, u, v);
			v5.addVertexWithUV(0.5, 0, 1, du, v);
			v5.addVertexWithUV(0.5, 1, 1, du, dv);
			v5.addVertexWithUV(0.5, 1, 0, u, dv);

			v5.addVertexWithUV(0, 0.5, 0, u, v);
			v5.addVertexWithUV(0, 0.5, 1, du, v);
			v5.addVertexWithUV(1, 0.5, 1, du, dv);
			v5.addVertexWithUV(1, 0.5, 0, u, dv);
			v5.draw();
			GL11.glPopMatrix();

			ico = ChromaIcons.RIFTHALO.getIcon();
			u = ico.getMinU();
			du = ico.getMaxU();
			v = ico.getMinV();
			dv = ico.getMaxV();

			GL11.glPushMatrix();
			if (te.isInWorld()) {
				double r = 0.03125;
				double t = (te.getTicksExisted()+par8)/32D;
				double x = r*Math.sin(t);
				double y = r*Math.sin(t*2-1);
				double z = r*Math.sin(t/1.5+0.5);

				double da = 5;
				double a1 = da*Math.cos(t);
				double a2 = da*Math.cos(t*2.4+0.6);
				double a3 = da*Math.cos(t*0.9-1.1);
				GL11.glTranslated(0.5, 0.5, 0.5);
				GL11.glRotated(a1, 1, 0, 0);
				GL11.glRotated(a2, 0, 1, 0);
				GL11.glRotated(a3, 0, 0, 1);
				GL11.glTranslated(-0.5, -0.5, -0.5);
				GL11.glTranslated(x, y, z);
			}
			v5.startDrawingQuads();
			v5.setBrightness(240);
			v5.setColorOpaque_I(0xffffff);
			v5.addVertexWithUV(0, 0, 0, u, v);
			v5.addVertexWithUV(1, 0, 0, du, v);
			v5.addVertexWithUV(1, 1, 0, du, dv);
			v5.addVertexWithUV(0, 1, 0, u, dv);

			v5.addVertexWithUV(0, 0, 1, u, v);
			v5.addVertexWithUV(1, 0, 1, du, v);
			v5.addVertexWithUV(1, 1, 1, du, dv);
			v5.addVertexWithUV(0, 1, 1, u, dv);

			v5.addVertexWithUV(0, 0, 0, u, v);
			v5.addVertexWithUV(0, 0, 1, du, v);
			v5.addVertexWithUV(0, 1, 1, du, dv);
			v5.addVertexWithUV(0, 1, 0, u, dv);

			v5.addVertexWithUV(1, 0, 0, u, v);
			v5.addVertexWithUV(1, 0, 1, du, v);
			v5.addVertexWithUV(1, 1, 1, du, dv);
			v5.addVertexWithUV(1, 1, 0, u, dv);

			v5.addVertexWithUV(0, 0, 0, u, v);
			v5.addVertexWithUV(0, 0, 1, du, v);
			v5.addVertexWithUV(1, 0, 1, du, dv);
			v5.addVertexWithUV(1, 0, 0, u, dv);

			v5.addVertexWithUV(0, 1, 0, u, v);
			v5.addVertexWithUV(0, 1, 1, du, v);
			v5.addVertexWithUV(1, 1, 1, du, dv);
			v5.addVertexWithUV(1, 1, 0, u, dv);
			v5.draw();
			GL11.glPopMatrix();

			if (te.isInWorld()) {

				GL11.glPushMatrix();
				GL11.glTranslated(-te.xCoord, -te.yCoord, -te.zCoord);

				GL11.glDisable(GL11.GL_TEXTURE_2D);
				GL11.glPushMatrix();
				GL11.glTranslated(0, te.getRange()*0.75-0.99, 0);
				GL11.glScaled(1, 0.5, 1);
				v5.startDrawing(GL11.GL_LINES);
				v5.setBrightness(240);
				v5.addTranslation(te.xCoord+0.5F, te.yCoord+0.5F, te.zCoord+0.5F);
				v5.setColorOpaque_I(0xffffff);
				te.getShape().renderPreview(v5, te.getRange());
				v5.addTranslation(-te.xCoord-0.5F, -te.yCoord-0.5F, -te.zCoord-0.5F);
				v5.addVertex(0, 0, 0);
				v5.draw();
				GL11.glPopMatrix();

				Map<Coordinate, ImmutablePair<Integer, Integer>> map = te.getBreakLocs();
				for (Coordinate c : map.keySet()) {
					BlendMode.ADDITIVEDARK.apply();
					ImmutablePair<Integer, Integer> get = map.get(c);
					Collection<LightningBolt> li = te.getBolts(c);
					for (LightningBolt b : li) {
						ChromaFX.renderBolt(b, par8, 255, 0.125, 4);
					}

					BlendMode.DEFAULT.apply();

					double f = 1-((double)get.left/get.right);
					int a = (int)(255*f);
					v5.startDrawingQuads();
					v5.setBrightness(240);
					v5.setColorRGBA_I(0xff8888, a);
					double o = 0.005;
					v5.addVertex(c.xCoord-o, c.yCoord-o, c.zCoord-o);
					v5.addVertex(c.xCoord+1+o, c.yCoord-o, c.zCoord-o);
					v5.addVertex(c.xCoord+1+o, c.yCoord-o, c.zCoord+1+o);
					v5.addVertex(c.xCoord-o, c.yCoord-o, c.zCoord+1+o);

					v5.addVertex(c.xCoord-o, c.yCoord+1+o, c.zCoord+1+o);
					v5.addVertex(c.xCoord+1+o, c.yCoord+1+o, c.zCoord+1+o);
					v5.addVertex(c.xCoord+1+o, c.yCoord+1+o, c.zCoord-o);
					v5.addVertex(c.xCoord-o, c.yCoord+1+o, c.zCoord-o);

					v5.addVertex(c.xCoord-o, c.yCoord-o, c.zCoord+o+1);
					v5.addVertex(c.xCoord-o, c.yCoord+o+1, c.zCoord+o+1);
					v5.addVertex(c.xCoord-o, c.yCoord+o+1, c.zCoord-o);
					v5.addVertex(c.xCoord-o, c.yCoord-o, c.zCoord-o);

					v5.addVertex(c.xCoord+o+1, c.yCoord-o, c.zCoord-o);
					v5.addVertex(c.xCoord+o+1, c.yCoord+o+1, c.zCoord-o);
					v5.addVertex(c.xCoord+o+1, c.yCoord+o+1, c.zCoord+o+1);
					v5.addVertex(c.xCoord+o+1, c.yCoord-o, c.zCoord+o+1);

					v5.addVertex(c.xCoord-o, c.yCoord-o, c.zCoord-o);
					v5.addVertex(c.xCoord-o, c.yCoord+o+1, c.zCoord-o);
					v5.addVertex(c.xCoord+o+1, c.yCoord+o+1, c.zCoord-o);
					v5.addVertex(c.xCoord+o+1, c.yCoord-o, c.zCoord-o);

					v5.addVertex(c.xCoord+o+1, c.yCoord-o, c.zCoord+o+1);
					v5.addVertex(c.xCoord+o+1, c.yCoord+o+1, c.zCoord+o+1);
					v5.addVertex(c.xCoord-o, c.yCoord+o+1, c.zCoord+o+1);
					v5.addVertex(c.xCoord-o, c.yCoord-o, c.zCoord+o+1);
					v5.draw();

					v5.startDrawing(GL11.GL_LINE_LOOP);
					v5.setBrightness(240);
					v5.setColorOpaque_I(0xff8888);
					v5.addVertex(c.xCoord-o, c.yCoord+1+o, c.zCoord-o);
					v5.addVertex(c.xCoord+1+o, c.yCoord+1+o, c.zCoord-o);
					v5.addVertex(c.xCoord+1+o, c.yCoord+1+o, c.zCoord+1+o);
					v5.addVertex(c.xCoord-o, c.yCoord+1+o, c.zCoord+1+o);
					v5.draw();

					v5.startDrawing(GL11.GL_LINE_LOOP);
					v5.setBrightness(240);
					v5.setColorOpaque_I(0xff8888);
					v5.addVertex(c.xCoord-o, c.yCoord-o, c.zCoord-o);
					v5.addVertex(c.xCoord+1+o, c.yCoord-o, c.zCoord-o);
					v5.addVertex(c.xCoord+1+o, c.yCoord-o, c.zCoord+1+o);
					v5.addVertex(c.xCoord-o, c.yCoord-o, c.zCoord+1+o);
					v5.draw();

					v5.startDrawing(GL11.GL_LINES);
					v5.setBrightness(240);
					v5.setColorOpaque_I(0xff8888);
					v5.addVertex(c.xCoord-o, c.yCoord-o, c.zCoord-o);
					v5.addVertex(c.xCoord-o, c.yCoord+o+1, c.zCoord-o);
					v5.addVertex(c.xCoord+1+o, c.yCoord-o, c.zCoord-o);
					v5.addVertex(c.xCoord+1+o, c.yCoord+o+1, c.zCoord-o);
					v5.addVertex(c.xCoord+1+o, c.yCoord-o, c.zCoord+1+o);
					v5.addVertex(c.xCoord+1+o, c.yCoord+o+1, c.zCoord+1+o);
					v5.addVertex(c.xCoord-o, c.yCoord-o, c.zCoord+1+o);
					v5.addVertex(c.xCoord-o, c.yCoord+o+1, c.zCoord+1+o);
					v5.draw();
				}

				BlendMode.DEFAULT.apply();

				v5.startDrawing(GL11.GL_LINES);
				v5.setBrightness(240);
				v5.addTranslation(te.xCoord+0.5F, te.yCoord+0.5F, te.zCoord+0.5F);
				v5.setColorOpaque_I(0xffffff);
				te.getShape().renderPreview(v5, 0.35);
				v5.addTranslation(-te.xCoord-0.5F, -te.yCoord-0.5F, -te.zCoord-0.5F);
				v5.addVertex(0, 0, 0);
				v5.draw();

				GL11.glEnable(GL11.GL_TEXTURE_2D);
				BlendMode.ADDITIVEDARK.apply();

				GL11.glPopMatrix();
			}

			GL11.glPopMatrix();
			GL11.glPopAttrib();

		}
	}

}
