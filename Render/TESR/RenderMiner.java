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
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.TileEntityMiner;
import Reika.DragonAPI.Interfaces.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;

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
		}
		else {
			this.renderInventory(te, par2, par4, par6, par8);
		}

		GL11.glPopMatrix();
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
