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
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.CrystalElement;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class CrystalFenceRenderer implements ISimpleBlockRenderingHandler {

	@Override
	public void renderInventoryBlock(Block b, int metadata, int modelId, RenderBlocks rb) {
		Tessellator tessellator = Tessellator.instance;

		rb.renderMaxX = 1;
		rb.renderMinY = 0;
		rb.renderMaxZ = 1;
		rb.renderMinX = 0;
		rb.renderMinZ = 0;
		rb.renderMaxY = 1;

		IIcon ico = b.getIcon(0, metadata);

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


		ico = CrystalElement.elements[metadata].getFaceRune();
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

		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block b, int modelId, RenderBlocks rb) {
		Tessellator v5 = Tessellator.instance;
		int meta = world.getBlockMetadata(x, y, z);
		/*
		float f1 = 0.5F;
		float f2 = 1;
		float f3 = 0.8F;
		float f4 = 0.8F;
		float f5 = 0.6F;
		float f6 = 0.6F;


		v5.setBrightness(rb.renderMinY > 0.0D ? l : b.getMixedBrightnessForBlock(world, x, y - 1, z));
		v5.setColorOpaque_F(f1, f1, f1);
		rb.renderFaceYNeg(b, x, y, z, ico);

		v5.setBrightness(rb.renderMaxY < 1.0D ? l : b.getMixedBrightnessForBlock(world, x, y + 1, z));
		v5.setColorOpaque_F(f2, f2, f2);
		rb.renderFaceYPos(b, x, y, z, ico);

		v5.setBrightness(rb.renderMinZ > 0.0D ? l : b.getMixedBrightnessForBlock(world, x, y, z - 1));
		v5.setColorOpaque_F(f3, f3, f3);
		rb.renderFaceZNeg(b, x, y, z, ico);

		v5.setBrightness(rb.renderMaxZ < 1.0D ? l : b.getMixedBrightnessForBlock(world, x, y, z + 1));
		v5.setColorOpaque_F(f4, f4, f4);
		rb.renderFaceZPos(b, x, y, z, ico);

		v5.setBrightness(rb.renderMinX > 0.0D ? l : b.getMixedBrightnessForBlock(world, x - 1, y, z));
		v5.setColorOpaque_F(f5, f5, f5);
		rb.renderFaceXNeg(b, x, y, z, ico);

		v5.setBrightness(rb.renderMaxX < 1.0D ? l : b.getMixedBrightnessForBlock(world, x + 1, y, z));
		v5.setColorOpaque_F(f6, f6, f6);
		rb.renderFaceXPos(b, x, y, z, ico);
		 */
		rb.renderStandardBlockWithAmbientOcclusion(b, x, y, z, 1, 1, 1);

		IIcon ico = CrystalElement.elements[meta].getFaceRune();
		v5.setBrightness(240);
		v5.setColorOpaque_F(255, 255, 255);
		rb.renderFaceYNeg(b, x, y, z, ico);
		rb.renderFaceYPos(b, x, y, z, ico);
		rb.renderFaceZNeg(b, x, y, z, ico);
		rb.renderFaceZPos(b, x, y, z, ico);
		rb.renderFaceXNeg(b, x, y, z, ico);
		rb.renderFaceXPos(b, x, y, z, ico);
		return true;
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
