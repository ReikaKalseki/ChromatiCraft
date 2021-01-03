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
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import Reika.ChromatiCraft.Block.Decoration.BlockRangedLamp.TileEntityRangedLamp;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.DragonAPI.Base.ISBRH;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;

public class LampRenderer extends ISBRH {

	public LampRenderer(int id) {
		super(id);
	}

	@Override
	public void renderInventoryBlock(Block b, int metadata, int modelId, RenderBlocks rb) {
		Tessellator tessellator = Tessellator.instance;

		rb.renderMaxX = 1;
		rb.renderMinY = 0;
		rb.renderMaxZ = 1;
		rb.renderMinX = 0;
		rb.renderMinZ = 0;
		rb.renderMaxY = 1;

		GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);


		if (metadata >= 16) {
			rb.setRenderBounds(0.0625, 0, 0.0625, 0.9375, 0.1875, 0.9375);
		}
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, -1.0F, 0.0F);
		rb.renderFaceYNeg(b, 0.0D, 0.0D, 0.0D, b.getIcon(0, metadata));
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		rb.renderFaceYPos(b, 0.0D, 0.0D, 0.0D, b.getIcon(1, metadata));
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, -1.0F);
		rb.renderFaceZNeg(b, 0.0D, 0.0D, 0.0D, b.getIcon(2, metadata));
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		rb.renderFaceZPos(b, 0.0D, 0.0D, 0.0D, b.getIcon(3, metadata));
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
		rb.renderFaceXNeg(b, 0.0D, 0.0D, 0.0D, b.getIcon(4, metadata));
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		rb.renderFaceXPos(b, 0.0D, 0.0D, 0.0D, b.getIcon(5, metadata));
		tessellator.draw();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block b, int modelId, RenderBlocks rb) {
		TileEntityRangedLamp te = (TileEntityRangedLamp)world.getTileEntity(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		if (renderPass == 0) {
			int color = b.colorMultiplier(world, x, y, z);
			float red = ReikaColorAPI.getRed(color)/255F;
			float green = ReikaColorAPI.getGreen(color)/255F;
			float blue = ReikaColorAPI.getBlue(color)/255F;
			/*
			if (te.isPanel()) {


				boolean flag = rb.renderAllFaces;
				rb.renderAllFaces = true;
				rb.renderMinX = x1;
				rb.renderMinY = y1;
				rb.renderMinZ = z1;
				rb.renderMaxX = x2;
				rb.renderMaxY = y2;
				rb.renderMaxZ = z2;
				rb.partialRenderBounds = true;
				rb.renderStandardBlockWithAmbientOcclusion(b, x, y, z, red, green, blue);
				rb.setRenderBounds(0, 0, 0, 1, 1, 1);
				rb.renderAllFaces = flag;
			}
			else {*/
			rb.renderStandardBlockWithAmbientOcclusion(b, x, y, z, red, green, blue);
			//}
			return true;
		}
		else if (te.isLit()) {
			Tessellator v5 = Tessellator.instance;
			v5.setBrightness(240);
			IIcon ico = ChromaIcons.BLANK.getIcon(); //since cannot turn off GLTexture in ISBRH
			double u = ico.getMinU();
			double v = ico.getMinV();
			int color = b.colorMultiplier(world, x, y, z);
			for (int step = 1; step < 4; step++) {
				double out = step/40D;
				AxisAlignedBB box = b.getCollisionBoundingBoxFromPool(Minecraft.getMinecraft().theWorld, x, y, z);
				box = box.expand(out, out, out);
				v5.setColorRGBA_I(color, 192-step*40);
				v5.addVertexWithUV(box.minX, box.maxY, box.minZ, u, v);
				v5.addVertexWithUV(box.maxX, box.maxY, box.minZ, u, v);
				v5.addVertexWithUV(box.maxX, box.minY, box.minZ, u, v);
				v5.addVertexWithUV(box.minX, box.minY, box.minZ, u, v);

				v5.addVertexWithUV(box.minX, box.minY, box.maxZ, u, v);
				v5.addVertexWithUV(box.maxX, box.minY, box.maxZ, u, v);
				v5.addVertexWithUV(box.maxX, box.maxY, box.maxZ, u, v);
				v5.addVertexWithUV(box.minX, box.maxY, box.maxZ, u, v);

				v5.addVertexWithUV(box.minX, box.minY, box.minZ, u, v);
				v5.addVertexWithUV(box.minX, box.minY, box.maxZ, u, v);
				v5.addVertexWithUV(box.minX, box.maxY, box.maxZ, u, v);
				v5.addVertexWithUV(box.minX, box.maxY, box.minZ, u, v);

				v5.addVertexWithUV(box.maxX, box.maxY, box.minZ, u, v);
				v5.addVertexWithUV(box.maxX, box.maxY, box.maxZ, u, v);
				v5.addVertexWithUV(box.maxX, box.minY, box.maxZ, u, v);
				v5.addVertexWithUV(box.maxX, box.minY, box.minZ, u, v);

				v5.addVertexWithUV(box.minX, box.maxY, box.minZ, u, v);
				v5.addVertexWithUV(box.minX, box.maxY, box.maxZ, u, v);
				v5.addVertexWithUV(box.maxX, box.maxY, box.maxZ, u, v);
				v5.addVertexWithUV(box.maxX, box.maxY, box.minZ, u, v);

				v5.addVertexWithUV(box.maxX, box.minY, box.minZ, u, v);
				v5.addVertexWithUV(box.maxX, box.minY, box.maxZ, u, v);
				v5.addVertexWithUV(box.minX, box.minY, box.maxZ, u, v);
				v5.addVertexWithUV(box.minX, box.minY, box.minZ, u, v);
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

}
