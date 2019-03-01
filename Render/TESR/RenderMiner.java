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

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.MinecraftForgeClient;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityAdjacencyUpgrade;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Acquisition.TileEntityMiner;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

public class RenderMiner extends ChromaRenderBase {

	//private final ModelMiner model = new ModelMiner();

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "miner.png";
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityMiner te = (TileEntityMiner)tile;
		GL11.glPushMatrix();
		GL11.glTranslated(par2, par4, par6);
		//this.renderModel(te, model);
		if (te.isInWorld()) {
			//this.renderMiningHead(te, par2, par4, par6, par8);
			if (MinecraftForgeClient.getRenderPass() == 1) {
				GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

				Tessellator v5 = Tessellator.instance;

				GL11.glDisable(GL11.GL_LIGHTING);
				ReikaRenderHelper.disableEntityLighting();

				GL11.glPushMatrix();
				GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
				this.renderFlares(te, par2, par4, par6, par8, v5);
				GL11.glPopAttrib();
				GL11.glPopMatrix();

				if (te.hasCrystal()) {
					GL11.glPushMatrix();
					GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
					this.renderCrystal(te, par2, par4, par6, par8, v5);
					GL11.glPopAttrib();
					GL11.glPopMatrix();
				}

				GL11.glPopAttrib();
			}
		}
		else {
			this.renderInventory(te, par2, par4, par6, par8);
		}

		GL11.glPopMatrix();
	}

	private void renderFlares(TileEntityMiner te, double par2, double par4, double par6, float par8, Tessellator v5) {

		ArrayList<Integer> li = new ArrayList();

		if (TileEntityAdjacencyUpgrade.getAdjacentUpgrades(te).containsKey(CrystalElement.LIGHTBLUE))
			li.add(0xff0000);
		if (te.getEfficiencyBoost() > 0)
			li.add(0x00ff00);
		if (te.hasSilkTouch())
			li.add(0x0000ff);

		if (li.isEmpty())
			return;

		GL11.glEnable(GL11.GL_BLEND);
		BlendMode.ADDITIVEDARK.apply();

		GL11.glTranslated(0, 1, 1);
		GL11.glScalef(1.0F, -1.0F, -1.0F);

		IIcon ico = ChromaIcons.SPINFLARE.getIcon();
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

		GL11.glDisable(GL11.GL_CULL_FACE);

		double s = 0.75;//0.4375;
		GL11.glTranslated(0.5, 0.5, 0.5);
		GL11.glScaled(s, s, s);
		RenderManager rm = RenderManager.instance;
		GL11.glRotatef(rm.playerViewY, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);

		for (int i = 0; i < li.size(); i++) {
			int c = li.get(i);
			//double ang = (System.currentTimeMillis()/20D)%360;
			//GL11.glRotated(ang, 0, 0, 1);
			GL11.glPushMatrix();
			if (te.isInWorld())
				GL11.glTranslated(0, 0/*-i*/, 0.005*i);
			GL11.glRotated(i*30, 0, 0, 1);
			double s2 = 1;//1-i/16D;
			GL11.glScaled(s2, s2, s2);
			v5.startDrawingQuads();
			v5.setColorOpaque_I(c);
			v5.addVertexWithUV(-1, -1, 0, u, v);
			v5.addVertexWithUV(1, -1, 0, du, v);
			v5.addVertexWithUV(1, 1, 0, du, dv);
			v5.addVertexWithUV(-1, 1, 0, u, dv);
			v5.draw();
			GL11.glPopMatrix();
		}
	}

	private void renderCrystal(TileEntityMiner te, double par2, double par4, double par6, float par8, Tessellator v5) {

		double t = (te.getTicksExisted()+par8);
		double y = 0.1*Math.sin(t/32D);
		double dr = 0.0625;
		double dx = dr*(1+Math.sin(t/32D));
		double dz = dr*(1+Math.sin(t/64D));
		double ang = (t)%360;
		double angh = 30*Math.cos(t/256D);
		double d = 0.5;
		GL11.glTranslated(dx, y, dz);
		GL11.glTranslated(d, d, d);
		GL11.glRotated(ang, 0, 1, 0);
		GL11.glRotated(angh, 1, 0, 0);
		GL11.glTranslated(-d, -d, -d);

		double w = 0.175;
		double h = 0.2875;
		double h2 = 0.1875;

		ReikaTextureHelper.bindTerrainTexture();
		IIcon ico = ChromaBlocks.RAINBOWCRYSTAL.getBlockInstance().getIcon(0, 0);
		float u = ico.getMinU();
		float du = ico.getMaxU();
		float v = ico.getMinV();
		float dv = ico.getMaxV();

		v5.startDrawingQuads();
		v5.addVertexWithUV(0.5-w, 0.5-h, 0.5-w, u, v);
		v5.addVertexWithUV(0.5-w, 0.5+h, 0.5-w, du, v);
		v5.addVertexWithUV(0.5+w, 0.5+h, 0.5-w, du, dv);
		v5.addVertexWithUV(0.5+w, 0.5-h, 0.5-w, u, dv);

		v5.addVertexWithUV(0.5+w, 0.5-h, 0.5+w, u, dv);
		v5.addVertexWithUV(0.5+w, 0.5+h, 0.5+w, du, dv);
		v5.addVertexWithUV(0.5-w, 0.5+h, 0.5+w, du, v);
		v5.addVertexWithUV(0.5-w, 0.5-h, 0.5+w, u, v);

		v5.addVertexWithUV(0.5+w, 0.5-h, 0.5-w, u, v);
		v5.addVertexWithUV(0.5+w, 0.5+h, 0.5-w, du, v);
		v5.addVertexWithUV(0.5+w, 0.5+h, 0.5+w, du, dv);
		v5.addVertexWithUV(0.5+w, 0.5-h, 0.5+w, u, dv);

		v5.addVertexWithUV(0.5-w, 0.5-h, 0.5+w, u, dv);
		v5.addVertexWithUV(0.5-w, 0.5+h, 0.5+w, du, dv);
		v5.addVertexWithUV(0.5-w, 0.5+h, 0.5-w, du, v);
		v5.addVertexWithUV(0.5-w, 0.5-h, 0.5-w, u, v);
		v5.draw();

		v5.startDrawing(GL11.GL_TRIANGLE_FAN);
		v5.addVertexWithUV(0.5, 0.5+h+h2, 0.5, u, dv);
		v5.addVertexWithUV(0.5+w, 0.5+h, 0.5+w, du, dv);
		v5.addVertexWithUV(0.5+w, 0.5+h, 0.5-w, du, dv);
		v5.addVertexWithUV(0.5-w, 0.5+h, 0.5-w, du, dv);
		v5.addVertexWithUV(0.5-w, 0.5+h, 0.5+w, du, dv);
		v5.addVertexWithUV(0.5+w, 0.5+h, 0.5+w, du, dv);
		v5.draw();

		v5.startDrawing(GL11.GL_TRIANGLE_FAN);
		v5.addVertexWithUV(0.5, 0.5-h-h2, 0.5, u, dv);
		v5.addVertexWithUV(0.5-w, 0.5-h, 0.5+w, du, dv);
		v5.addVertexWithUV(0.5-w, 0.5-h, 0.5-w, du, dv);
		v5.addVertexWithUV(0.5+w, 0.5-h, 0.5-w, du, dv);
		v5.addVertexWithUV(0.5+w, 0.5-h, 0.5+w, du, dv);
		v5.addVertexWithUV(0.5-w, 0.5-h, 0.5+w, du, dv);
		v5.draw();
	}

	private void renderMiningHead(TileEntityMiner te, double par2, double par4, double par6, float par8) {
		int dx = te.getReadX()-te.xCoord;
		int dy = te.getReadY()-te.yCoord;
		int dz = te.getReadZ()-te.zCoord;
		Tessellator v5 = Tessellator.instance;
		ReikaRenderHelper.prepareGeoDraw(false);
		GL11.glDisable(GL11.GL_DEPTH_TEST);


		v5.startDrawing(GL11.GL_LINE_LOOP);
		v5.setBrightness(240);
		v5.setColorOpaque_I(0xffffff);
		v5.addVertex(dx, dy, dz);
		v5.addVertex(dx+1, dy, dz);
		v5.addVertex(dx+1, dy+1, dz);
		v5.addVertex(dx, dy+1, dz);
		v5.draw();

		v5.startDrawing(GL11.GL_LINE_LOOP);
		v5.setBrightness(240);
		v5.setColorOpaque_I(0xffffff);
		v5.addVertex(dx, dy, dz+1);
		v5.addVertex(dx+1, dy, dz+1);
		v5.addVertex(dx+1, dy+1, dz+1);
		v5.addVertex(dx, dy+1, dz+1);
		v5.draw();

		v5.startDrawing(GL11.GL_LINE_LOOP);
		v5.setBrightness(240);
		v5.setColorOpaque_I(0xffffff);
		v5.addVertex(dx, dy, dz);
		v5.addVertex(dx, dy, dz+1);
		v5.addVertex(dx, dy+1, dz+1);
		v5.addVertex(dx, dy+1, dz);
		v5.draw();

		v5.startDrawing(GL11.GL_LINE_LOOP);
		v5.setBrightness(240);
		v5.setColorOpaque_I(0xffffff);
		v5.addVertex(dx+1, dy, dz);
		v5.addVertex(dx+1, dy, dz+1);
		v5.addVertex(dx+1, dy+1, dz+1);
		v5.addVertex(dx+1, dy+1, dz);
		v5.draw();

		v5.startDrawing(GL11.GL_LINE_LOOP);
		v5.setBrightness(240);
		v5.setColorOpaque_I(0xffffff);
		v5.addVertex(dx, dy, dz);
		v5.addVertex(dx, dy, dz+1);
		v5.addVertex(dx+1, dy, dz+1);
		v5.addVertex(dx+1, dy, dz);
		v5.draw();

		v5.startDrawing(GL11.GL_LINE_LOOP);
		v5.setBrightness(240);
		v5.setColorOpaque_I(0xffffff);
		v5.addVertex(dx, dy+1, dz);
		v5.addVertex(dx, dy+1, dz+1);
		v5.addVertex(dx+1, dy+1, dz+1);
		v5.addVertex(dx+1, dy+1, dz);
		v5.draw();

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		ReikaRenderHelper.exitGeoDraw();
	}

	private void renderInventory(TileEntityMiner te, double par2, double par4, double par6, float par8) {
		int dx = te.getReadX()-te.xCoord;
		int dy = te.getReadY()-te.yCoord;
		int dz = te.getReadZ()-te.zCoord;
		Tessellator v5 = Tessellator.instance;
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_LIGHTING);

		int tick = Math.abs((int)System.currentTimeMillis());
		float w = GL11.glGetFloat(GL11.GL_LINE_WIDTH);
		float t = par8 < 0 ? 6 : 2;
		GL11.glLineWidth(t);

		v5.startDrawing(GL11.GL_LINE_LOOP);
		v5.setBrightness(240);
		v5.setColorOpaque_I(CrystalElement.getBlendedColor(tick, 200));
		v5.addVertex(dx, dy, dz);
		v5.addVertex(dx+1, dy, dz);
		v5.addVertex(dx+1, dy+1, dz);
		v5.addVertex(dx, dy+1, dz);
		v5.draw();

		v5.startDrawing(GL11.GL_LINE_LOOP);
		v5.setBrightness(240);
		v5.setColorOpaque_I(CrystalElement.getBlendedColor(tick, 300));
		v5.addVertex(dx, dy, dz+1);
		v5.addVertex(dx+1, dy, dz+1);
		v5.addVertex(dx+1, dy+1, dz+1);
		v5.addVertex(dx, dy+1, dz+1);
		v5.draw();

		v5.startDrawing(GL11.GL_LINE_LOOP);
		v5.setBrightness(240);
		v5.setColorOpaque_I(CrystalElement.getBlendedColor(tick, 400));
		v5.addVertex(dx, dy, dz);
		v5.addVertex(dx, dy, dz+1);
		v5.addVertex(dx, dy+1, dz+1);
		v5.addVertex(dx, dy+1, dz);
		v5.draw();

		v5.startDrawing(GL11.GL_LINE_LOOP);
		v5.setBrightness(240);
		v5.setColorOpaque_I(CrystalElement.getBlendedColor(tick, 500));
		v5.addVertex(dx+1, dy, dz);
		v5.addVertex(dx+1, dy, dz+1);
		v5.addVertex(dx+1, dy+1, dz+1);
		v5.addVertex(dx+1, dy+1, dz);
		v5.draw();

		v5.startDrawing(GL11.GL_LINE_LOOP);
		v5.setBrightness(240);
		v5.setColorOpaque_I(CrystalElement.getBlendedColor(tick, 600));
		v5.addVertex(dx, dy, dz);
		v5.addVertex(dx, dy, dz+1);
		v5.addVertex(dx+1, dy, dz+1);
		v5.addVertex(dx+1, dy, dz);
		v5.draw();

		v5.startDrawing(GL11.GL_LINE_LOOP);
		v5.setBrightness(240);
		v5.setColorOpaque_I(CrystalElement.getBlendedColor(tick, 700));
		v5.addVertex(dx, dy+1, dz);
		v5.addVertex(dx, dy+1, dz+1);
		v5.addVertex(dx+1, dy+1, dz+1);
		v5.addVertex(dx+1, dy+1, dz);
		v5.draw();

		GL11.glLineWidth(w);

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_LIGHTING);
	}

}
