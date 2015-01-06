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

import java.awt.Color;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Block.BlockChromaPlantTile;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.TileEntity.Plants.TileEntityChromaFlower;
import Reika.DragonAPI.Interfaces.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;

public class ChromaFlowerRenderer extends ChromaRenderBase {

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityChromaFlower te = (TileEntityChromaFlower)tile;
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glTranslated(par2, par4, par6);
		int hue = te.getHue1();
		int hue2 = te.getHue2();
		int hue3 = te.getHue3();
		int color = Color.HSBtoRGB(hue/360F, 1, 0.5F);
		int color2 = Color.HSBtoRGB(hue2/360F, 1, 0.5F);
		int color3 = Color.HSBtoRGB(hue3/360F, 1, 0.5F);
		int[] rgb = ReikaColorAPI.HexToRGB(color);
		int[] rgb2 = ReikaColorAPI.HexToRGB(color2);
		int[] rgb3 = ReikaColorAPI.HexToRGB(color3);
		int[][] colors = new int[][]{rgb, rgb2, rgb3};
		Tessellator v5 = Tessellator.instance;
		ReikaTextureHelper.bindTerrainTexture();
		IIcon ico2 = ((BlockChromaPlantTile)ChromaBlocks.TILEPLANT.getBlockInstance()).getPlantTexture(0, 4);
		float u2 = ico2.getMinU();
		float du2 = ico2.getMaxU();
		float v2 = ico2.getMinV();
		float dv2 = ico2.getMaxV();
		for (int k = 0; k < 3; k++) {
			IIcon ico = ((BlockChromaPlantTile)ChromaBlocks.TILEPLANT.getBlockInstance()).getPlantTexture(0, k+1);
			float u = ico.getMinU();
			float du = ico.getMaxU();
			float v = ico.getMinV();
			float dv = ico.getMaxV();
			for (int i = 0; i < 3; i++) {
				GL11.glTranslated(0.5, 0, 0.5);
				GL11.glRotated(60*i, 0, 1, 0);
				GL11.glTranslated(-0.5, 0, -0.5);
				v5.startDrawingQuads();
				v5.setColorRGBA(255, 255, 255, 255);
				v5.addVertexWithUV(0.5, 0, 1, du2, dv2);
				v5.addVertexWithUV(0.5, 1, 1, du2, v2);
				v5.addVertexWithUV(0.5, 1, 0, u2, v2);
				v5.addVertexWithUV(0.5, 0, 0, u2, dv2);
				v5.setColorRGBA(colors[k][0], colors[k][1], colors[k][2], 255);
				v5.addVertexWithUV(0.5, 0, 1, du, dv);
				v5.addVertexWithUV(0.5, 1, 1, du, v);
				v5.addVertexWithUV(0.5, 1, 0, u, v);
				v5.addVertexWithUV(0.5, 0, 0, u, dv);
				v5.draw();
				GL11.glTranslated(0.5, 0, 0.5);
				GL11.glRotated(-60*i, 0, 1, 0);
				GL11.glTranslated(-0.5, 0, -0.5);
			}
		}
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glPopMatrix();
	}

	@Override
	public String getImageFileName(RenderFetcher te) {
		return null;
	}

}
