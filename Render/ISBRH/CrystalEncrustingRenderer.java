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

import java.util.Collection;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.IBlockAccess;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Block.BlockEncrustedCrystal.CrystalGrowth;
import Reika.ChromatiCraft.Block.BlockEncrustedCrystal.TileCrystalEncrusted;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Interfaces.ISBRH;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;

public class CrystalEncrustingRenderer implements ISBRH {

	public static int renderPass;

	private final Random rand = new Random();

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
		if (renderPass != 1)
			return false;
		rand.setSeed(this.calcSeed(x, y, z));
		rand.nextBoolean();
		Tessellator v5 = Tessellator.instance;
		TileCrystalEncrusted te = (TileCrystalEncrusted)world.getTileEntity(x, y, z);
		Collection<CrystalGrowth> c = te.getGrowths();
		if (c.isEmpty())
			return false;
		int meta = world.getBlockMetadata(x, y, z);
		//v5.setColorRGBA_I(CrystalElement.elements[meta].getColor(), 127);
		v5.setBrightness(240);
		for (CrystalGrowth g : c) {
			this.renderCrystalFace(world, x, y, z, b, g, rb, CrystalElement.elements[meta].getColor());
		}
		return true;
	}

	private void renderCrystalFace(IBlockAccess world, int x, int y, int z, Block bk, CrystalGrowth g, RenderBlocks rb, int color) {
		int h1 = 1+g.getGrowth()/2;
		int h2 = 1+g.getGrowth()*4;
		int n = 8;
		double w = 1D/n;
		for (int a = 0; a < n; a++) {
			for (int b = 0; b < n; b++) {
				int rh = h1+rand.nextInt(h2-h1+1);
				double h = rh/96D;
				this.renderCrystalPiece(world, x, y, z, bk, g, rb, color, a, b, w, h);
			}
		}
	}

	private void renderCrystalPiece(IBlockAccess world, int x, int y, int z, Block bk, CrystalGrowth g, RenderBlocks rb, int color, int a, int b, double w, double h) {
		double x1 = 0;
		double y1 = 0;
		double z1 = 0;
		double x2 = 0;
		double y2 = 0;
		double z2 = 0;
		//red = MathHelper.clamp_float((float)ReikaRandomHelper.getRandomPlusMinus(red, 0.2F), 0, 1);
		//green = MathHelper.clamp_float((float)ReikaRandomHelper.getRandomPlusMinus(green, 0.2F), 0, 1);
		//blue = MathHelper.clamp_float((float)ReikaRandomHelper.getRandomPlusMinus(blue, 0.2F), 0, 1);
		float f1 = 0.75F+rand.nextFloat()*0.25F;
		float f2 = 0.75F+rand.nextFloat()*0.25F;
		color = ReikaColorAPI.mixColors(color, 0xffffff, f1);
		color = ReikaColorAPI.mixColors(color, 0x000000, f2);
		int hue = ReikaColorAPI.getHue(color);
		hue = hue-5+rand.nextInt(11);
		color = ReikaColorAPI.getModifiedHue(color, hue);
		float red = ReikaColorAPI.getRed(color)/255F;
		float green = ReikaColorAPI.getGreen(color)/255F;
		float blue = ReikaColorAPI.getBlue(color)/255F;
		switch(g.side) {
			case DOWN:
				x1 = a*w;
				x2 = x1+w;
				z1 = b*w;
				z2 = z1+w;
				y1 = 0;
				y2 = h;
				break;
			case UP:
				x1 = a*w;
				x2 = x1+w;
				z1 = b*w;
				z2 = z1+w;
				y1 = 1-h;
				y2 = 1;
				break;
			case WEST:
				x1 = 0;
				x2 = h;
				y1 = b*w;
				y2 = y1+w;
				z1 = a*w;
				z2 = z1+w;
				break;
			case EAST:
				x1 = 1-h;
				x2 = 1;
				y1 = b*w;
				y2 = y1+w;
				z1 = a*w;
				z2 = z1+w;
				break;
			case NORTH:
				z1 = 0;
				z2 = h;
				y1 = b*w;
				y2 = y1+w;
				x1 = a*w;
				x2 = x1+w;
				break;
			case SOUTH:
				z1 = 1-h;
				z2 = 1;
				y1 = b*w;
				y2 = y1+w;
				x1 = a*w;
				x2 = x1+w;
				break;
			default:
				break;
		}
		boolean flag = rb.renderAllFaces;
		rb.renderAllFaces = true;
		rb.renderMinX = x1;
		rb.renderMinY = y1;
		rb.renderMinZ = z1;
		rb.renderMaxX = x2;
		rb.renderMaxY = y2;
		rb.renderMaxZ = z2;
		rb.partialRenderBounds = true;
		rb.renderStandardBlockWithAmbientOcclusion(bk, x, y, z, red, green, blue);
		rb.setRenderBounds(0, 0, 0, 1, 1, 1);
		rb.renderAllFaces = flag;
	}

	private long calcSeed(int x, int y, int z) {
		return ChunkCoordIntPair.chunkXZ2Int(x, z) ^ y;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return ChromatiCraft.proxy.encrustedRender;
	}



}
