/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
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
import Reika.ChromatiCraft.Block.Dimension.Structure.BlockSpecialShield;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.Instantiable.Rendering.EdgeDetectionRenderer;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;


public class SpecialShieldRenderer implements ISimpleBlockRenderingHandler {

	private final EdgeDetectionRenderer edge = new EdgeDetectionRenderer(ChromaBlocks.SPECIALSHIELD.getBlockInstance()).setIcons(BlockSpecialShield.edgeIcons);

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

		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block b, int modelId, RenderBlocks rb) {

		Tessellator v5 = Tessellator.instance;
		int meta = world.getBlockMetadata(x, y, z)%8;
		BlockSpecialShield bs = (BlockSpecialShield)b;

		boolean flag = bs.useNoLighting(world, x, y, z);
		if (flag)
			v5.setBrightness(240);

		if (!flag)
			v5.setBrightness(b.getMixedBrightnessForBlock(world, x, y-1, z));
		v5.setColorOpaque_F(0.5F, 0.5F, 0.5F);
		IIcon ico = b.getIcon(world, x, y, z, 0);
		if (b.shouldSideBeRendered(world, x, y-1, z, 0))
			rb.renderFaceYNeg(b, x, y, z, ico);

		if (!flag)
			v5.setBrightness(b.getMixedBrightnessForBlock(world, x, y+1, z));
		v5.setColorOpaque_F(1, 1, 1);
		ico = b.getIcon(world, x, y, z, 1);
		if (b.shouldSideBeRendered(world, x, y+1, z, 1))
			rb.renderFaceYPos(b, x, y, z, ico);

		if (!flag)
			v5.setBrightness(b.getMixedBrightnessForBlock(world, x, y, z-1));
		v5.setColorOpaque_F(0.8F, 0.8F, 0.8F);
		ico = b.getIcon(world, x, y, z, 2);
		if (b.shouldSideBeRendered(world, x, y, z-1, 2))
			rb.renderFaceZNeg(b, x, y, z, ico);

		if (!flag)
			v5.setBrightness(b.getMixedBrightnessForBlock(world, x, y, z+1));
		ico = b.getIcon(world, x, y, z, 3);
		if (b.shouldSideBeRendered(world, x, y, z+1, 3))
			rb.renderFaceZPos(b, x, y, z, ico);

		if (!flag)
			v5.setBrightness(b.getMixedBrightnessForBlock(world, x-1, y, z));
		v5.setColorOpaque_F(0.65F, 0.65F, 0.65F);
		ico = b.getIcon(world, x, y, z, 4);
		if (b.shouldSideBeRendered(world, x-1, y, z, 4))
			rb.renderFaceXNeg(b, x, y, z, ico);

		if (!flag)
			v5.setBrightness(b.getMixedBrightnessForBlock(world, x+1, y, z));
		ico = b.getIcon(world, x, y, z, 5);
		if (b.shouldSideBeRendered(world, x+1, y, z, 5))
			rb.renderFaceXPos(b, x, y, z, ico);

		if (meta <= 1) {
			v5.addTranslation(x, y, z);
			v5.setColorOpaque_I(0xffffff);
			v5.setBrightness(240);
			edge.renderBlock(world, x, y, z, rb);
			v5.addTranslation(-x, -y, -z);
		}

		int n = BlockSpecialShield.getOverlayIndex(world, x, y, z, meta);
		if (n >= 0) {
			ico = BlockSpecialShield.overlayIcons[meta][n];
			v5.setBrightness(240);
			v5.setColorOpaque_I(0xffffff);

			if (b.shouldSideBeRendered(world, x, y-1, z, 0))
				rb.renderFaceYNeg(b, x, y, z, ico);

			if (b.shouldSideBeRendered(world, x, y+1, z, 1))
				rb.renderFaceYPos(b, x, y, z, ico);

			if (b.shouldSideBeRendered(world, x, y, z-1, 2))
				rb.renderFaceZNeg(b, x, y, z, ico);

			if (b.shouldSideBeRendered(world, x, y, z+1, 3))
				rb.renderFaceZPos(b, x, y, z, ico);

			if (b.shouldSideBeRendered(world, x-1, y, z, 4))
				rb.renderFaceXNeg(b, x, y, z, ico);

			if (b.shouldSideBeRendered(world, x+1, y, z, 5))
				rb.renderFaceXPos(b, x, y, z, ico);
		}

		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return ChromatiCraft.proxy.specialShieldRender;
	}

}
