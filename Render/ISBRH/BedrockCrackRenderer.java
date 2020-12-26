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
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Block.Dimension.BlockBedrockCrack;
import Reika.DragonAPI.Base.ISBRH;

public class BedrockCrackRenderer extends ISBRH {

	public BedrockCrackRenderer(int id) {
		super(id);
	}

	@Override
	public void renderInventoryBlock(Block b, int metadata, int modelId, RenderBlocks rb) {
		Tessellator v5 = Tessellator.instance;

		rb.renderMaxX = 1;
		rb.renderMinY = 0;
		rb.renderMaxZ = 1;
		rb.renderMinX = 0;
		rb.renderMinZ = 0;
		rb.renderMaxY = 1;

		IIcon ico = Blocks.bedrock.blockIcon;

		GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		v5.startDrawingQuads();
		v5.setNormal(0.0F, -1.0F, 0.0F);
		rb.renderFaceYNeg(b, 0.0D, 0.0D, 0.0D, ico);
		v5.draw();

		v5.startDrawingQuads();
		v5.setNormal(0.0F, 1.0F, 0.0F);
		rb.renderFaceYPos(b, 0.0D, 0.0D, 0.0D, ico);
		v5.draw();

		v5.startDrawingQuads();
		v5.setNormal(0.0F, 0.0F, -1.0F);
		rb.renderFaceZNeg(b, 0.0D, 0.0D, 0.0D, ico);
		v5.draw();
		v5.startDrawingQuads();
		v5.setNormal(0.0F, 0.0F, 1.0F);
		rb.renderFaceZPos(b, 0.0D, 0.0D, 0.0D, ico);
		v5.draw();
		v5.startDrawingQuads();
		v5.setNormal(-1.0F, 0.0F, 0.0F);
		rb.renderFaceXNeg(b, 0.0D, 0.0D, 0.0D, ico);
		v5.draw();
		v5.startDrawingQuads();
		v5.setNormal(1.0F, 0.0F, 0.0F);
		rb.renderFaceXPos(b, 0.0D, 0.0D, 0.0D, ico);
		v5.draw();

		ico = BlockBedrockCrack.getCrackOverlay(metadata);
		v5.startDrawingQuads();
		v5.setNormal(0.0F, -1.0F, 0.0F);
		rb.renderFaceYNeg(b, 0.0D, 0.0D, 0.0D, ico);
		v5.draw();

		v5.startDrawingQuads();
		v5.setNormal(0.0F, 1.0F, 0.0F);
		rb.renderFaceYPos(b, 0.0D, 0.0D, 0.0D, ico);
		v5.draw();

		v5.startDrawingQuads();
		v5.setNormal(0.0F, 0.0F, -1.0F);
		rb.renderFaceZNeg(b, 0.0D, 0.0D, 0.0D, ico);
		v5.draw();
		v5.startDrawingQuads();
		v5.setNormal(0.0F, 0.0F, 1.0F);
		rb.renderFaceZPos(b, 0.0D, 0.0D, 0.0D, ico);
		v5.draw();
		v5.startDrawingQuads();
		v5.setNormal(-1.0F, 0.0F, 0.0F);
		rb.renderFaceXNeg(b, 0.0D, 0.0D, 0.0D, ico);
		v5.draw();
		v5.startDrawingQuads();
		v5.setNormal(1.0F, 0.0F, 0.0F);
		rb.renderFaceXPos(b, 0.0D, 0.0D, 0.0D, ico);
		v5.draw();

		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block b, int modelId, RenderBlocks rb) {
		Tessellator v5 = Tessellator.instance;

		if (renderPass == 0) {
			rb.renderStandardBlockWithAmbientOcclusion(Blocks.bedrock, x, y, z, 1, 1, 1);
		}

		if (renderPass == 1) {
			v5.setBrightness(240);
			v5.setColorOpaque(255, 255, 255);

			IIcon ico = BlockBedrockCrack.getCrackOverlay(world, x, y, z);
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

		v5.addVertex(0, 0, 0);
		v5.addVertex(0, 0, 0);
		v5.addVertex(0, 0, 0);
		v5.addVertex(0, 0, 0);
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

}
