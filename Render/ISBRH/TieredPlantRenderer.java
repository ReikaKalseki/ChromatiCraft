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

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import Reika.ChromatiCraft.Block.Worldgen.BlockTieredPlant;
import Reika.ChromatiCraft.Block.Worldgen.BlockTieredPlant.TieredPlants;
import Reika.DragonAPI.Interfaces.ISBRH;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;

public class TieredPlantRenderer implements ISBRH {

	public int renderPass;

	@Override
	public void renderInventoryBlock(Block b, int metadata, int modelId, RenderBlocks renderer) {
		Tessellator v5 = Tessellator.instance;
		BlockTieredPlant t = (BlockTieredPlant)b;
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
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glTranslated(x, y, z);
		v5.startDrawingQuads();
		v5.setColorOpaque_I(0xffffff);
		v5.setBrightness(240);
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
		v5.setBrightness(240);
		v5.setColorOpaque_I(0xffffff);
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
		BlockTieredPlant t = (BlockTieredPlant)b;
		if (t.isPlayerSufficientTier(world, x, y, z, Minecraft.getMinecraft().thePlayer)) {
			Tessellator v5 = Tessellator.instance;
			int meta = world.getBlockMetadata(x, y, z);
			v5.setColorOpaque(255, 255, 255);
			if (renderPass == 0) {
				v5.setBrightness(b.getMixedBrightnessForBlock(world, x, y, z));
				if (TieredPlants.list[meta] == TieredPlants.POD) {
					rb.setRenderBoundsFromBlock(b);
					rb.renderStandardBlockWithAmbientOcclusion(b, x, y, z, 1, 1, 1);
				}
				else if (TieredPlants.list[meta] == TieredPlants.ROOT) {
					ReikaRenderHelper.renderCropTypeTex(world, x, y, z, t.getBacking(meta), v5, rb, 0.0625, 1);
				}
				else {
					rb.drawCrossedSquares(t.getBacking(meta), x, y, z, 1);
				}
				return true;
			}
			else if (renderPass == 1) {
				v5.setBrightness(240);
				IIcon ico = t.getOverlay(meta);
				if (TieredPlants.list[meta] == TieredPlants.POD) {
					rb.setRenderBoundsFromBlock(b);
					rb.unlockBlockBounds();
					double d = ReikaRandomHelper.getRandomPlusMinus(0.03125, 0.03125);
					rb.renderMaxX += d;
					rb.renderMaxY += d;
					rb.renderMaxZ += d;
					rb.renderMinX -= d;
					rb.renderMinY -= d;
					rb.renderMinZ -= d;
					rb.renderFaceXNeg(b, x, y, z, ico);
					rb.renderFaceYNeg(b, x, y, z, ico);
					rb.renderFaceZNeg(b, x, y, z, ico);
					rb.renderFaceXPos(b, x, y, z, ico);
					rb.renderFaceYPos(b, x, y, z, ico);
					rb.renderFaceZPos(b, x, y, z, ico);
				}
				else if (TieredPlants.list[meta] == TieredPlants.ROOT) {
					ReikaRenderHelper.renderCropTypeTex(world, x, y, z, ico, v5, rb, 0.0625, 1);
				}
				else {
					rb.drawCrossedSquares(ico, x, y, z, 1);
				}
				return true;
			}
		}
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
