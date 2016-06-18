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
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.DragonAPI.Interfaces.ISBRH;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;

public class LampRenderer implements ISBRH {

	public static int renderPass;

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
		if (renderPass == 0) {
			int color = b.colorMultiplier(world, x, y, z);
			float red = ReikaColorAPI.getRed(color)/255F;
			float green = ReikaColorAPI.getGreen(color)/255F;
			float blue = ReikaColorAPI.getBlue(color)/255F;
			rb.renderStandardBlockWithAmbientOcclusion(b, x, y, z, red, green, blue);
			return true;
		}
		else if (b.getLightValue(world, x, y, z) > 0) {
			Tessellator v5 = Tessellator.instance;
			v5.setBrightness(240);
			v5.addTranslation(x, y, z);
			IIcon ico = ChromaIcons.BLANK.getIcon(); //since cannot turn off GLTexture in ISBRH
			double u = ico.getMinU();
			double v = ico.getMinV();
			int color = b.colorMultiplier(world, x, y, z);
			for (int step = 1; step < 4; step++) {
				double out = step/40D;
				v5.setColorRGBA_I(color, 192-step*40);
				v5.addVertexWithUV(0-out, 1+out, 0-out, u, v);
				v5.addVertexWithUV(1+out, 1+out, 0-out, u, v);
				v5.addVertexWithUV(1+out, 0-out, 0-out, u, v);
				v5.addVertexWithUV(0-out, 0-out, 0-out, u, v);

				v5.addVertexWithUV(0-out, 0-out, 1+out, u, v);
				v5.addVertexWithUV(1+out, 0-out, 1+out, u, v);
				v5.addVertexWithUV(1+out, 1+out, 1+out, u, v);
				v5.addVertexWithUV(0-out, 1+out, 1+out, u, v);

				v5.addVertexWithUV(0-out, 0-out, 0-out, u, v);
				v5.addVertexWithUV(0-out, 0-out, 1+out, u, v);
				v5.addVertexWithUV(0-out, 1+out, 1+out, u, v);
				v5.addVertexWithUV(0-out, 1+out, 0-out, u, v);

				v5.addVertexWithUV(1+out, 1+out, 0-out, u, v);
				v5.addVertexWithUV(1+out, 1+out, 1+out, u, v);
				v5.addVertexWithUV(1+out, 0-out, 1+out, u, v);
				v5.addVertexWithUV(1+out, 0-out, 0-out, u, v);

				v5.addVertexWithUV(0-out, 1+out, 0-out, u, v);
				v5.addVertexWithUV(0-out, 1+out, 1+out, u, v);
				v5.addVertexWithUV(1+out, 1+out, 1+out, u, v);
				v5.addVertexWithUV(1+out, 1+out, 0-out, u, v);

				v5.addVertexWithUV(1+out, 0-out, 0-out, u, v);
				v5.addVertexWithUV(1+out, 0-out, 1+out, u, v);
				v5.addVertexWithUV(0-out, 0-out, 1+out, u, v);
				v5.addVertexWithUV(0-out, 0-out, 0-out, u, v);
			}
			v5.addTranslation(-x, -y, -z);
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
		return ChromatiCraft.proxy.lampRender;
	}

}
