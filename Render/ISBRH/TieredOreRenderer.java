/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render.ISBRH;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Block.BlockTieredOre;
import Reika.ChromatiCraft.Block.BlockTieredOre.TieredOres;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class TieredOreRenderer implements ISimpleBlockRenderingHandler {

	@Override
	public void renderInventoryBlock(Block b, int metadata, int modelId, RenderBlocks rb) {
		BlockTieredOre bt = (BlockTieredOre)b;
		Tessellator tessellator = Tessellator.instance;

		rb.renderMaxX = 1;
		rb.renderMinY = 0;
		rb.renderMaxZ = 1;
		rb.renderMinX = 0;
		rb.renderMinZ = 0;
		rb.renderMaxY = 1;

		boolean tier = bt.getProgressStage(metadata).isPlayerAtStage(Minecraft.getMinecraft().thePlayer);
		IIcon ico = tier ? bt.getBacking(metadata) : bt.getDisguise().getIcon(0, 0);

		GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, -1.0F, 0.0F);
		rb.renderFaceYNeg(b, 0.0D, 0.0D, 0.0D, ico);
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		rb.renderFaceYPos(b, 0.0D, 0.0D, 0.0D, ico);
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, -1.0F);
		rb.renderFaceZNeg(b, 0.0D, 0.0D, 0.0D, ico);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		rb.renderFaceZPos(b, 0.0D, 0.0D, 0.0D, ico);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
		rb.renderFaceXNeg(b, 0.0D, 0.0D, 0.0D, ico);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		rb.renderFaceXPos(b, 0.0D, 0.0D, 0.0D, ico);
		tessellator.draw();

		if (tier) {
			ico = ((BlockTieredOre)b).getOverlay(metadata);
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0F, -1.0F, 0.0F);
			rb.renderFaceYNeg(b, 0.0D, 0.0D, 0.0D, ico);
			tessellator.draw();

			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0F, 1.0F, 0.0F);
			rb.renderFaceYPos(b, 0.0D, 0.0D, 0.0D, ico);
			tessellator.draw();

			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0F, 0.0F, -1.0F);
			rb.renderFaceZNeg(b, 0.0D, 0.0D, 0.0D, ico);
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0F, 0.0F, 1.0F);
			rb.renderFaceZPos(b, 0.0D, 0.0D, 0.0D, ico);
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(-1.0F, 0.0F, 0.0F);
			rb.renderFaceXNeg(b, 0.0D, 0.0D, 0.0D, ico);
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(1.0F, 0.0F, 0.0F);
			rb.renderFaceXPos(b, 0.0D, 0.0D, 0.0D, ico);
			tessellator.draw();
		}

		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block b, int modelId, RenderBlocks rb) {
		Tessellator v5 = Tessellator.instance;
		int meta = world.getBlockMetadata(x, y, z);

		BlockTieredOre t = (BlockTieredOre)b;
		if (t.isPlayerSufficientTier(world, x, y, z, Minecraft.getMinecraft().thePlayer)) {
			if (TieredOres.list[meta].renderAsGeode()) {
				this.renderGeode(world, x, y, z, b, meta, rb);
			}
			else {
				rb.renderStandardBlockWithAmbientOcclusion(b, x, y, z, 1, 1, 1);

				IIcon ico = t.getOverlay(meta);
				v5.setBrightness(240);
				v5.setColorOpaque_F(255, 255, 255);
				if (b.shouldSideBeRendered(world, x, y-1, z, ForgeDirection.DOWN.ordinal()))
					rb.renderFaceYNeg(b, x, y, z, ico);
				if (b.shouldSideBeRendered(world, x, y+1, z, ForgeDirection.UP.ordinal()))
					rb.renderFaceYPos(b, x, y, z, ico);
				if (b.shouldSideBeRendered(world, x, y, z-1, ForgeDirection.NORTH.ordinal()))
					rb.renderFaceZNeg(b, x, y, z, ico);
				if (b.shouldSideBeRendered(world, x, y, z+1, ForgeDirection.SOUTH.ordinal()))
					rb.renderFaceZPos(b, x, y, z, ico);
				if (b.shouldSideBeRendered(world, x-1, y, z, ForgeDirection.WEST.ordinal()))
					rb.renderFaceXNeg(b, x, y, z, ico);
				if (b.shouldSideBeRendered(world, x+1, y, z, ForgeDirection.EAST.ordinal()))
					rb.renderFaceXPos(b, x, y, z, ico);
			}
		}
		else {
			rb.renderBlockAllFaces(t.getDisguise(), x, y, z);
			//rb.renderStandardBlockWithAmbientOcclusion(t.getDisguise(), x, y, z, 1, 1, 1);
		}
		return true;
	}

	private void renderGeode(IBlockAccess world, int x, int y, int z, Block b, int meta, RenderBlocks rb) {
		Tessellator v5 = Tessellator.instance;
		v5.setColorOpaque_I(0xffffff);
		v5.addTranslation(x, y, z);
		double in = 0.125;
		/*
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			IIcon stone = Blocks.stone.getIcon(0, 0);
			float us = stone.getMinU();
			float vs = stone.getMinV();
			float dus = stone.getMaxU();
			float dvs = stone.getMaxV();
			v5.addVertexWithUV(0, 		1, 		1, 		us, 	dvs);
			v5.addVertexWithUV(0+in, 	1, 		1, 		dus, 	dvs);
			v5.addVertexWithUV(0+in, 	1, 		0, 		dus, 	vs);
			v5.addVertexWithUV(0, 		1, 		0, 		us, 	vs);

			v5.addVertexWithUV(1-in, 		1, 		1, 		us, 	dvs);
			v5.addVertexWithUV(1, 			1, 		1, 		dus, 	dvs);
			v5.addVertexWithUV(1, 			1, 		0, 		dus, 	vs);
			v5.addVertexWithUV(1-in, 		1, 		0, 		us, 	vs);
		}
		 */
		//ISBRHModel
		v5.addTranslation(-x, -y, -z);
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return ChromatiCraft.proxy.runeRender;
	}



}
