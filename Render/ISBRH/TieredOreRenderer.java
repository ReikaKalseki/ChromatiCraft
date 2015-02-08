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
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Block.BlockTieredOre;
import Reika.ChromatiCraft.Block.BlockTieredOre.TieredOres;
import Reika.DragonAPI.Instantiable.Rendering.TessellatorVertexList;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class TieredOreRenderer implements ISimpleBlockRenderingHandler {

	//private final GeodeModel geode = new GeodeModel();

	private final int numSections = 8;
	private final double[][] stoneOffsets = new double[16][(numSections+1)*(numSections+1)];
	private final double[][] oreOffsets = new double[16][(numSections+1)*(numSections+1)];
	private final int[][][] offsetArray = new int[16][16][16];

	public TieredOreRenderer() {
		for (int a = 0; a < 16; a++) {
			for (int i = 0; i < numSections+1; i++) {
				for (int k = 0; k < numSections+1; k++) {
					double dy = 1;
					double dr = 0.25;
					double di = Math.min(i, numSections-i)/(double)numSections;
					double dk = Math.min(k, numSections-k)/(double)numSections;
					double dp = 0.25;
					dy = 1-dr*Math.pow(di, dp)*Math.pow(dk, dp);
					if (di > 0 && dk > 0)
						dy = ReikaRandomHelper.getRandomPlusMinus(dy, dr*0.125);
					stoneOffsets[a][i*numSections+k] = dy;
				}
			}

			for (int i = 0; i < numSections+1; i++) {
				for (int k = 0; k < numSections+1; k++) {
					double dy = 1;
					double dr = 0.25;
					double di = Math.min(i, numSections-i)/(double)numSections;
					double dk = Math.min(k, numSections-k)/(double)numSections;
					double dp = 0.25;
					dy = 1+dr*Math.pow(di, dp)*Math.pow(dk, dp)-0.25;
					if (di > 0 && dk > 0)
						dy = ReikaRandomHelper.getRandomPlusMinus(dy, dr*0.5);
					oreOffsets[a][i*numSections+k] = dy;
				}
			}
		}

		for (int a = 0; a < 16; a++) {
			for (int b = 0; b < 16; b++) {
				for (int c = 0; c < 16; c++) {
					offsetArray[a][b][c] = ReikaRandomHelper.getSafeRandomInt(16);
				}
			}
		}
	}

	@Override
	public void renderInventoryBlock(Block b, int metadata, int modelId, RenderBlocks rb) {
		BlockTieredOre bt = (BlockTieredOre)b;
		Tessellator v5 = Tessellator.instance;

		rb.renderMaxX = 1;
		rb.renderMinY = 0;
		rb.renderMaxZ = 1;
		rb.renderMinX = 0;
		rb.renderMinZ = 0;
		rb.renderMaxY = 1;

		boolean tier = bt.getProgressStage(metadata).isPlayerAtStage(Minecraft.getMinecraft().thePlayer);
		IIcon ico = tier ? bt.getBacking(metadata) : bt.getDisguise().getIcon(0, 0);

		if (!tier || !TieredOres.list[metadata].renderAsGeode()) {
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
		}

		if (tier) {
			if (TieredOres.list[metadata].renderAsGeode()) {
				GL11.glPushMatrix();
				GL11.glTranslated(-0.5, -0.5, -0.5);
				v5.startDrawingQuads();
				this.renderGeode(null, 0, 0, 0, bt, metadata, rb);
				v5.draw();
				GL11.glPopMatrix();
			}
			else {
				ico = ((BlockTieredOre)b).getOverlay(metadata);
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
			}
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
		TessellatorVertexList v5 = new TessellatorVertexList();
		Tessellator.instance.setColorOpaque_I(0xffffff);
		Tessellator.instance.addTranslation(x, y, z);
		IIcon stone = Blocks.stone.getIcon(0, 0);
		float us = stone.getMinU();
		float vs = stone.getMinV();
		float dus = stone.getMaxU();
		float dvs = stone.getMaxV();

		IIcon obsidian = Blocks.obsidian.getIcon(0, 0);
		float ub = obsidian.getMinU();
		float vb = obsidian.getMinV();
		float dub = obsidian.getMaxU();
		float dvb = obsidian.getMaxV();

		IIcon ico = ((BlockTieredOre)b).getGeodeIcon(meta);
		float uo = ico.getMinU();
		float vo = ico.getMinV();
		float duo = ico.getMaxU();
		float dvo = ico.getMaxV();

		/*
		double in = 0.125;
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];

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
		//geode.render();

		double s = 1D/numSections;
		int da = offsetArray[(x%16+16)%16][(y%16+16)%16][(z%16+16)%16];

		for (int i = 0; i < numSections; i++) {
			double d = i*s;
			for (int k = 0; k < numSections; k++) {
				double d2 = k*s;
				boolean center = false;//ReikaMathLibrary.isValueInsideBoundsIncl(2, numSections-3, i) && ReikaMathLibrary.isValueInsideBoundsIncl(2, numSections-3, k);
				//center |= ReikaMathLibrary.isValueInsideBoundsIncl(1, numSections-2, i) && ReikaMathLibrary.isValueInsideBoundsIncl(2, numSections-3, k);
				//center |= ReikaMathLibrary.isValueInsideBoundsIncl(2, numSections-3, i) && ReikaMathLibrary.isValueInsideBoundsIncl(1, numSections-2, k);
				float us1 = center ? (float)(ub+(dub-ub)*d) : (float)(us+(dus-us)*d);
				float vs1 = center ? (float)(vb+(dvb-vb)*d2) : (float)(vs+(dvs-vs)*d2);
				float us2 = center ? (float)(ub+(dub-ub)*(d+s)) : (float)(us+(dus-us)*(d+s));
				float vs2 = center ? (float)(vb+(dvb-vb)*(d2+s)) : (float)(vs+(dvs-vs)*(d2+s));
				v5.addVertexWithUV(d, stoneOffsets[da][(i)*numSections+(k+1)], d2+s, us1, vs1);
				v5.addVertexWithUV(d+s, stoneOffsets[da][(i+1)*numSections+(k+1)], d2+s, us2, vs1);
				v5.addVertexWithUV(d+s, stoneOffsets[da][(i+1)*numSections+(k)], d2, us2, vs2);
				v5.addVertexWithUV(d, stoneOffsets[da][(i)*numSections+(k)], d2, us1, vs2);
			}
		}

		if (world != null)
			Tessellator.instance.setBrightness(b.getMixedBrightnessForBlock(world, x, y+1, z));
		Tessellator.instance.setNormal(0, 1, 0);
		v5.render();

		if (world != null)
			Tessellator.instance.setBrightness((int)(b.getMixedBrightnessForBlock(world, x, y-1, z)*0.5));
		Tessellator.instance.setNormal(0, -1, 0);
		v5.invertY();
		v5.render();

		if (world != null)
			Tessellator.instance.setBrightness((int)(b.getMixedBrightnessForBlock(world, x-1, y, z)*0.8));
		Tessellator.instance.setNormal(-1, 0, 0);
		v5.rotateYtoX();
		v5.render();

		if (world != null)
			Tessellator.instance.setBrightness((int)(b.getMixedBrightnessForBlock(world, x+1, y, z)*0.8));
		Tessellator.instance.setNormal(1, 0, 0);
		v5.invertX();
		v5.render();

		if (world != null)
			Tessellator.instance.setBrightness((int)(b.getMixedBrightnessForBlock(world, x, y, z+1)*0.7));
		Tessellator.instance.setNormal(0, 0, -1);
		v5.rotateXtoZ();
		v5.render();

		if (world != null)
			Tessellator.instance.setBrightness((int)(b.getMixedBrightnessForBlock(world, x, y, z-1)*0.7));
		Tessellator.instance.setNormal(0, 0, 1);
		v5.invertZ();
		v5.render();

		v5.clear();
		if (world != null)
			Tessellator.instance.setBrightness(240);
		for (int i = 0; i < numSections; i++) {
			double d = i*s;
			double db = d+s;
			for (int k = 0; k < numSections; k++) {
				double d2 = k*s;
				double d2b = d2+s;
				float uo1 = (float)(uo+(duo-uo)*d);
				float vo1 = (float)(vo+(dvo-vo)*d2);
				float uo2 = (float)(uo+(duo-uo)*(d+s));
				float vo2 = (float)(vo+(dvo-vo)*(d2+s));

				if (i == 0) {
					d = 0.2;
				}
				else if (i == numSections-1) {
					db = 0.8;
				}

				if (k == 0) {
					d2 = 0.2;
				}
				else if (k == numSections-1) {
					d2b = 0.8;
				}

				v5.addVertexWithUV(d, oreOffsets[da][(i)*numSections+(k+1)], d2b, uo1, vo1);
				v5.addVertexWithUV(db, oreOffsets[da][(i+1)*numSections+(k+1)], d2b, uo2, vo1);
				v5.addVertexWithUV(db, oreOffsets[da][(i+1)*numSections+(k)], d2, uo2, vo2);
				v5.addVertexWithUV(d, oreOffsets[da][(i)*numSections+(k)], d2, uo1, vo2);
			}
		}
		Tessellator.instance.setNormal(0, 1, 0);
		v5.render();

		Tessellator.instance.setNormal(0, -1, 0);
		v5.invertY();
		v5.render();

		Tessellator.instance.setNormal(-1, 0, 0);
		v5.rotateYtoX();
		v5.render();

		Tessellator.instance.setNormal(1, 0, 0);
		v5.invertX();
		v5.render();

		Tessellator.instance.setNormal(0, 0, -1);
		v5.rotateXtoZ();
		v5.render();

		Tessellator.instance.setNormal(0, 0, 1);
		v5.invertZ();
		v5.render();

		Tessellator.instance.addTranslation(-x, -y, -z);
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
