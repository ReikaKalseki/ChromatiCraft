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
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Block.BlockEncrustedCrystal;
import Reika.ChromatiCraft.Block.BlockEncrustedCrystal.CrystalGrowth;
import Reika.ChromatiCraft.Block.BlockEncrustedCrystal.TileCrystalEncrusted;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.CubePoints;
import Reika.DragonAPI.Instantiable.GridDistortion;
import Reika.DragonAPI.Instantiable.GridDistortion.OffsetGroup;
import Reika.DragonAPI.Interfaces.ISBRH;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;

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
		rand.nextBoolean();
		TileCrystalEncrusted te = (TileCrystalEncrusted)world.getTileEntity(x, y, z);
		Collection<CrystalGrowth> c = te.getGrowths();
		if (c.isEmpty())
			return false;
		int meta = world.getBlockMetadata(x, y, z);
		//v5.setColorRGBA_I(CrystalElement.elements[meta].getColor(), 127);
		for (CrystalGrowth g : c) {
			this.renderCrystalFace(world, x, y, z, b, te, g, rb, CrystalElement.elements[meta].getColor());
		}
		return true;
	}

	private void renderCrystalFace(IBlockAccess world, int x, int y, int z, Block bk, TileCrystalEncrusted te, CrystalGrowth g, RenderBlocks rb, int color) {
		int amt = g.getGrowth();
		int h1 = 1+amt/2;
		int h2 = 1+amt*4;
		int n = 4+rand.nextInt(5);
		double w = 1D/n;
		GridDistortion grid = new GridDistortion(n);
		grid.maxDeviation *= 0.66;
		grid.randomize(rand);
		for (int a = 0; a < n; a++) {
			for (int b = 0; b < n; b++) {
				if (amt < 12 && rand.nextInt(2+amt/2) == 0)
					continue;
				int rh = h1+rand.nextInt(h2-h1+1);
				double h = rh/96D;
				this.renderCrystalPiece(world, x, y, z, bk, te, g, rb, color, a, b, w, h, grid);
			}
		}
	}

	private void renderCrystalPiece(IBlockAccess world, int x, int y, int z, Block bk, TileCrystalEncrusted te, CrystalGrowth g, RenderBlocks rb, int color, int a, int b, double w, double h, GridDistortion grid) {
		CubePoints points = CubePoints.fullBlock();
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
		//float red = ReikaColorAPI.getRed(color)/255F;
		//float green = ReikaColorAPI.getGreen(color)/255F;
		//float blue = ReikaColorAPI.getBlue(color)/255F;
		Tessellator.instance.setBrightness(240);
		Tessellator.instance.setColorRGBA_I(color, te.isSpecial() ? 160 : 255);
		OffsetGroup off = grid.getOffset(a, b);
		switch(g.side) {
			case DOWN:
				points.setSidePosition(ForgeDirection.DOWN, 0);
				points.setSidePosition(ForgeDirection.UP, h);
				points.setSidePosition(ForgeDirection.WEST, a*w);
				points.setSidePosition(ForgeDirection.EAST, a*w+w);
				points.setSidePosition(ForgeDirection.NORTH, b*w);
				points.setSidePosition(ForgeDirection.SOUTH, b*w+w);
				break;
			case UP:
				points.setSidePosition(ForgeDirection.DOWN, 1-h);
				points.setSidePosition(ForgeDirection.UP, 1);
				points.setSidePosition(ForgeDirection.WEST, a*w);
				points.setSidePosition(ForgeDirection.EAST, a*w+w);
				points.setSidePosition(ForgeDirection.NORTH, b*w);
				points.setSidePosition(ForgeDirection.SOUTH, b*w+w);
				break;
			case WEST:
				points.setSidePosition(ForgeDirection.DOWN, b*w);
				points.setSidePosition(ForgeDirection.UP, b*w+w);
				points.setSidePosition(ForgeDirection.WEST, 0);
				points.setSidePosition(ForgeDirection.EAST, h);
				points.setSidePosition(ForgeDirection.NORTH, a*w);
				points.setSidePosition(ForgeDirection.SOUTH, a*w+w);
				break;
			case EAST:
				points.setSidePosition(ForgeDirection.DOWN, b*w);
				points.setSidePosition(ForgeDirection.UP, b*w+w);
				points.setSidePosition(ForgeDirection.WEST, 1-h);
				points.setSidePosition(ForgeDirection.EAST, 1);
				points.setSidePosition(ForgeDirection.NORTH, a*w);
				points.setSidePosition(ForgeDirection.SOUTH, a*w+w);
				break;
			case NORTH:
				points.setSidePosition(ForgeDirection.DOWN, b*w);
				points.setSidePosition(ForgeDirection.UP, b*w+w);
				points.setSidePosition(ForgeDirection.WEST, a*w);
				points.setSidePosition(ForgeDirection.EAST, a*w+w);
				points.setSidePosition(ForgeDirection.NORTH, 0);
				points.setSidePosition(ForgeDirection.SOUTH, h);
				break;
			case SOUTH:
				points.setSidePosition(ForgeDirection.DOWN, b*w);
				points.setSidePosition(ForgeDirection.UP, b*w+w);
				points.setSidePosition(ForgeDirection.WEST, a*w);
				points.setSidePosition(ForgeDirection.EAST, a*w+w);
				points.setSidePosition(ForgeDirection.NORTH, 1-h);
				points.setSidePosition(ForgeDirection.SOUTH, 1);
				break;
			default:
				break;
		}
		points.applyOffset(g.side, off);
		points.applyOffset(g.side.getOpposite(), off);
		points.clamp();
		ReikaRenderHelper.renderBlockPieceNonCuboid(world, x, y, z, bk, Tessellator.instance, points);

		Tessellator.instance.setBrightness(240);
		Tessellator.instance.setColorRGBA_I(ReikaColorAPI.mixColors(color, 0xffffff, 0.9F), te.isSpecial() ? 240 : 192);
		points.renderIconOnSides(world, x, y, z, ChromaIcons.GLOWFRAME_TRANS.getIcon(), Tessellator.instance);

		Tessellator.instance.setBrightness(240);
		Tessellator.instance.setColorRGBA_I(0xffffff, te.isSpecial() ? 48 : 32);
		points.renderIconOnSides(world, x, y, z, BlockEncrustedCrystal.specialIcon, Tessellator.instance);
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
