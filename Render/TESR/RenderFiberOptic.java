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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Block.BlockFiberOptic;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityFiberOptic;
import Reika.DragonAPI.Interfaces.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;

public class RenderFiberOptic extends ChromaRenderBase {

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "";
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float f) {
		TileEntityFiberOptic te = (TileEntityFiberOptic)tile;
		if (tile.hasWorldObj()) {

		}
		else {
			ReikaTextureHelper.bindTerrainTexture();
			this.renderBlock(te, par2, par4-0.3, par6, BlockFiberOptic.getOuterIcon(), BlockFiberOptic.getInnerIcon());
			//this.renderBlock(te, par2, par4+0.1, par6, BlockFiberOptic.getInnerIcon(), BlockFiberOptic.getOuterIcon());
			//this.renderBlock(te, par2, par4+0.5, par6, BlockFiberOptic.getOuterIcon(), BlockFiberOptic.getInnerIcon());
		}
	}

	private void renderBlock(TileEntityFiberOptic te, double par2, double par4, double par6, IIcon ico, IIcon ico2) {
		float u = ico.getMinU();
		float v = ico.getMinV();
		float du = ico.getMaxU();
		float dv = ico.getMaxV();

		float u2 = ico2.getMinU();
		float v2 = ico2.getMinV();
		float du2 = ico2.getMaxU();
		float dv2 = ico2.getMaxV();
		GL11.glPushMatrix();
		GL11.glTranslated(par2, par4, par6);
		Tessellator v5 = Tessellator.instance;

		float f = 0.5F;
		double s = 0.4;
		double sy = 1;
		GL11.glColor4f(f, f, f, 1);
		GL11.glScaled(s, sy, s);
		GL11.glTranslated(0.5, 0.25, 0.5);
		v5.startDrawingQuads();
		v5.setNormal(0, 1, 0);
		v5.addVertexWithUV(0, 0, 1, u, dv);
		v5.addVertexWithUV(1, 0, 1, u, v);
		v5.addVertexWithUV(1, 1, 1, du, v);
		v5.addVertexWithUV(0, 1, 1, du, dv);
		v5.draw();

		v5.startDrawingQuads();
		v5.setNormal(0, 1, 0);
		v5.addVertexWithUV(0, 1, 0, u, v);
		v5.addVertexWithUV(1, 1, 0, u, dv);
		v5.addVertexWithUV(1, 0, 0, du, dv);
		v5.addVertexWithUV(0, 0, 0, du, v);
		v5.draw();

		f = 0.33F;
		GL11.glColor4f(f, f, f, 1);
		v5.startDrawingQuads();
		v5.setNormal(0, 1, 0);
		v5.addVertexWithUV(1, 1, 0, u, v);
		v5.addVertexWithUV(1, 1, 1, u, dv);
		v5.addVertexWithUV(1, 0, 1, du, dv);
		v5.addVertexWithUV(1, 0, 0, du, v);
		v5.draw();

		v5.startDrawingQuads();
		v5.setNormal(0, 1, 0);
		v5.addVertexWithUV(0, 0, 0, u, dv);
		v5.addVertexWithUV(0, 0, 1, u, v);
		v5.addVertexWithUV(0, 1, 1, du, v);
		v5.addVertexWithUV(0, 1, 0, du, dv);
		v5.draw();

		f = 1F;
		GL11.glColor4f(f, f, f, 1);
		v5.startDrawingQuads();
		v5.setNormal(0, 1, 0);
		v5.addVertexWithUV(0, 1, 1, u2, dv2);
		v5.addVertexWithUV(1, 1, 1, du2, dv2);
		v5.addVertexWithUV(1, 1, 0, du2, v2);
		v5.addVertexWithUV(0, 1, 0, u2, v2);
		v5.draw();

		v5.startDrawingQuads();
		v5.setNormal(0, 1, 0);
		v5.addVertexWithUV(0, 0, 0, u2, v2);
		v5.addVertexWithUV(1, 0, 0, du2, v2);
		v5.addVertexWithUV(1, 0, 1, du2, dv2);
		v5.addVertexWithUV(0, 0, 1, u2, dv2);
		v5.draw();
		GL11.glPopMatrix();
		v5.setColorOpaque(255, 255, 255);
	}

}
