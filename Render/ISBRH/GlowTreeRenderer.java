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
import net.minecraft.block.BlockLeavesBase;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Interfaces.LightedTreeBlock;
import Reika.DragonAPI.Interfaces.ISBRH;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;

public class GlowTreeRenderer implements ISBRH {

	public static int renderPass;

	@Override
	public void renderInventoryBlock(Block b, int metadata, int modelId, RenderBlocks rb) {
		Tessellator v5 = Tessellator.instance;

		rb.renderMaxX = 1;
		rb.renderMinY = 0;
		rb.renderMaxZ = 1;
		rb.renderMinX = 0;
		rb.renderMinZ = 0;
		rb.renderMaxY = 1;

		GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		v5.startDrawingQuads();
		v5.setNormal(0.0F, -1.0F, 0.0F);
		rb.renderFaceYNeg(b, 0.0D, 0.0D, 0.0D, b.getIcon(0, metadata));
		v5.draw();

		v5.startDrawingQuads();
		v5.setNormal(0.0F, 1.0F, 0.0F);
		rb.renderFaceYPos(b, 0.0D, 0.0D, 0.0D, b.getIcon(1, metadata));
		v5.draw();

		v5.startDrawingQuads();
		v5.setNormal(0.0F, 0.0F, -1.0F);
		rb.renderFaceZNeg(b, 0.0D, 0.0D, 0.0D, b.getIcon(2, metadata));
		v5.draw();
		v5.startDrawingQuads();
		v5.setNormal(0.0F, 0.0F, 1.0F);
		rb.renderFaceZPos(b, 0.0D, 0.0D, 0.0D, b.getIcon(3, metadata));
		v5.draw();
		v5.startDrawingQuads();
		v5.setNormal(-1.0F, 0.0F, 0.0F);
		rb.renderFaceXNeg(b, 0.0D, 0.0D, 0.0D, b.getIcon(4, metadata));
		v5.draw();
		v5.startDrawingQuads();
		v5.setNormal(1.0F, 0.0F, 0.0F);
		rb.renderFaceXPos(b, 0.0D, 0.0D, 0.0D, b.getIcon(5, metadata));
		v5.draw();


		LightedTreeBlock ltb = (LightedTreeBlock)b;
		IIcon ico = ltb.getOverlay(metadata);
		if (ltb.renderOverlayOnSide(0, metadata)) {
			v5.startDrawingQuads();
			v5.setColorOpaque_I(0xffffff);
			v5.setBrightness(240);
			v5.setNormal(0.0F, -1.0F, 0.0F);
			rb.renderFaceYNeg(b, 0.0D, 0.0D, 0.0D, ico);
			v5.draw();
		}

		if (ltb.renderOverlayOnSide(1, metadata)) {
			v5.startDrawingQuads();
			v5.setColorOpaque_I(0xffffff);
			v5.setBrightness(240);
			v5.setNormal(0.0F, 1.0F, 0.0F);
			rb.renderFaceYPos(b, 0.0D, 0.0D, 0.0D, ico);
			v5.draw();
		}

		if (ltb.renderOverlayOnSide(2, metadata)) {
			v5.startDrawingQuads();
			v5.setColorOpaque_I(0xffffff);
			v5.setBrightness(240);
			v5.setNormal(0.0F, 0.0F, -1.0F);
			rb.renderFaceZNeg(b, 0.0D, 0.0D, 0.0D, ico);
			v5.draw();
		}

		if (ltb.renderOverlayOnSide(3, metadata)) {
			v5.startDrawingQuads();
			v5.setColorOpaque_I(0xffffff);
			v5.setBrightness(240);
			v5.setNormal(0.0F, 0.0F, 1.0F);
			rb.renderFaceZPos(b, 0.0D, 0.0D, 0.0D, ico);
			v5.draw();
		}

		if (ltb.renderOverlayOnSide(4, metadata)) {
			v5.startDrawingQuads();
			v5.setColorOpaque_I(0xffffff);
			v5.setBrightness(240);
			v5.setNormal(-1.0F, 0.0F, 0.0F);
			rb.renderFaceXNeg(b, 0.0D, 0.0D, 0.0D, ico);
			v5.draw();
		}

		if (ltb.renderOverlayOnSide(5, metadata)) {
			v5.startDrawingQuads();
			v5.setColorOpaque_I(0xffffff);
			v5.setBrightness(240);
			v5.setNormal(1.0F, 0.0F, 0.0F);
			rb.renderFaceXPos(b, 0.0D, 0.0D, 0.0D, ico);
			v5.draw();
		}

		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block b, int modelId, RenderBlocks rb) {
		Tessellator v5 = Tessellator.instance;
		int meta = world.getBlockMetadata(x, y, z);
		if (renderPass == 0) {
			int color = b.colorMultiplier(world, x, y, z);
			float red = ReikaColorAPI.getRed(color)/255F;
			float grn = ReikaColorAPI.getGreen(color)/255F;
			float blu = ReikaColorAPI.getBlue(color)/255F;
			rb.renderStandardBlockWithAmbientOcclusion(b, x, y, z, red, grn, blu);
			return true;
		}
		else if (renderPass == 1) {
			LightedTreeBlock ltb = (LightedTreeBlock)b;
			IIcon ico = ltb.getOverlay(meta);
			v5.setBrightness(240);
			v5.setColorRGBA_I(0xffffff, b instanceof BlockLeavesBase ? 255 : 220);
			if (ltb.renderOverlayOnSide(0, meta))
				rb.renderFaceYNeg(b, x, y, z, ico);
			if (ltb.renderOverlayOnSide(1, meta))
				rb.renderFaceYPos(b, x, y, z, ico);
			if (ltb.renderOverlayOnSide(2, meta))
				rb.renderFaceZNeg(b, x, y, z, ico);
			if (ltb.renderOverlayOnSide(3, meta))
				rb.renderFaceZPos(b, x, y, z, ico);
			if (ltb.renderOverlayOnSide(4, meta))
				rb.renderFaceXNeg(b, x, y, z, ico);
			if (ltb.renderOverlayOnSide(5, meta))
				rb.renderFaceXPos(b, x, y, z, ico);
			return true;
		}
		return false;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return ChromatiCraft.proxy.glowTreeRender;
	}



}
