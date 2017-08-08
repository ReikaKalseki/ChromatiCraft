/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render.ISBRH;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Block.BlockDecoPlant;
import Reika.DragonAPI.Interfaces.ISBRH;

public class DecoPlantRenderer implements ISBRH {

	public int renderPass;

	@Override
	public void renderInventoryBlock(Block b, int metadata, int modelId, RenderBlocks renderer) {
		Tessellator v5 = Tessellator.instance;
		GL11.glDisable(GL11.GL_LIGHTING);
		BlockDecoPlant t = (BlockDecoPlant)b;
		IIcon ico = t.getBacking(metadata);
		float u = ico.getMinU();
		float v = ico.getMinV();
		float du = ico.getMaxU();
		float dv = ico.getMaxV();
		GL11.glPushMatrix();
		GL11.glRotated(45, 0, 1, 0);
		GL11.glRotated(-30, 1, 0, 0);
		double s = 1.6;
		GL11.glScaled(s, s, s);
		double x = -0.5;
		double y = -0.5;
		double z = 0;
		GL11.glTranslated(x, y, z);
		v5.startDrawingQuads();
		v5.setColorOpaque_I(0xffffff);
		v5.addVertexWithUV(0, 0, 0, u, dv);
		v5.addVertexWithUV(1, 0, 0, du, dv);
		v5.addVertexWithUV(1, 1, 0, du, v);
		v5.addVertexWithUV(0, 1, 0, u, v);
		v5.draw();

		ico = t.getOverlay(metadata);
		u = ico.getMinU();
		v = ico.getMinV();
		du = ico.getMaxU();
		dv = ico.getMaxV();
		v5.startDrawingQuads();
		v5.addVertexWithUV(0, 0, 0, u, dv);
		v5.addVertexWithUV(1, 0, 0, du, dv);
		v5.addVertexWithUV(1, 1, 0, du, v);
		v5.addVertexWithUV(0, 1, 0, u, v);
		v5.draw();
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block b, int model, RenderBlocks rb) {
		BlockDecoPlant t = (BlockDecoPlant)b;
		Tessellator v5 = Tessellator.instance;
		int meta = world.getBlockMetadata(x, y, z);
		if (renderPass == 0) {
			v5.setColorOpaque(255, 255, 255);
			v5.setBrightness(b.getMixedBrightnessForBlock(world, x, y, z));
			rb.drawCrossedSquares(t.getBacking(meta), x, y, z, 1);
			v5.setBrightness(240);
			rb.drawCrossedSquares(t.getOverlay(meta), x, y, z, 1);
			return true;
		}/*
			else if (renderPass == 1) {
				IIcon ico = ChromaIcons.CENTER.getIcon();
				BlendMode.ADDITIVEDARK.apply();
				rb.drawCrossedSquares(ico, x, y, z, 1);
				BlendMode.DEFAULT.apply();
				return true;
			}*/
		return false;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return 0;
	}

}
