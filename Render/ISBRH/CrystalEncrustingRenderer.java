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

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Block.BlockEncrustedCrystal;
import Reika.ChromatiCraft.Block.BlockEncrustedCrystal.CrystalGrowth;
import Reika.ChromatiCraft.Block.BlockEncrustedCrystal.TileCrystalEncrusted;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Base.ISBRH;
import Reika.DragonAPI.Instantiable.CubePoints;
import Reika.DragonAPI.Instantiable.GridDistortion;
import Reika.DragonAPI.Instantiable.GridDistortion.OffsetGroup;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;

public class CrystalEncrustingRenderer extends ISBRH {

	private static final int MIN_SEGMENTS = 4;
	private static final int MAX_SEGMENTS = 8;

	private final GridDistortion[] distortions = new GridDistortion[MAX_SEGMENTS-MIN_SEGMENTS+1];

	private final CubePoints renderBlock = CubePoints.fullBlock();

	public CrystalEncrustingRenderer(int id) {
		super(id);
	}

	@Override
	public void renderInventoryBlock(Block bk, int metadata, int modelId, RenderBlocks rb) {
		Tessellator v5 = Tessellator.instance;

		rb.renderMaxX = 1;
		rb.renderMinY = 0;
		rb.renderMaxZ = 1;
		rb.renderMinX = 0;
		rb.renderMinZ = 0;
		rb.renderMaxY = 1;

		IIcon ico = bk.getIcon(0, metadata);
		int c = ReikaColorAPI.mixColors(CrystalElement.elements[metadata].getColor(), 0xffffff, 0.85F);

		rand.setSeed(Minecraft.getMinecraft().thePlayer.getUniqueID().hashCode() ^ metadata);
		rand.nextBoolean();
		int pieces = 12;
		int n = 6;

		GL11.glRotatef(90F, 0F, 1F, 0F);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		v5.startDrawingQuads();
		v5.setColorOpaque_I(c);

		boolean[][] rendered = new boolean[n][n];
		for (int i = 0; i < pieces; i++) {
			int a = rand.nextInt(n);
			int b = rand.nextInt(n);
			double w = 1D/n;
			double a1 = w*a;
			double a2 = a1+w;
			double b1 = w*b;
			double b2 = b1+w;
			if (rendered[a][b])
				continue;
			rendered[a][b] = true;
			double h = 0.2+rand.nextDouble()*0.6;

			rb.partialRenderBounds = true;
			rb.renderAllFaces = true;
			rb.setRenderBounds(a1, 0, b1, a2, h, b2);
			v5.setNormal(0F, -1F, 0F);
			rb.renderFaceYNeg(bk, 0D, 0D, 0D, ico);
			v5.setColorOpaque_I(c);
			v5.setNormal(0F, 1F, 0F);
			rb.renderFaceYPos(bk, 0D, 0D, 0D, ico);
			v5.setColorOpaque_I(c);
			v5.setNormal(0F, 0F, -1F);
			rb.renderFaceZNeg(bk, 0D, 0D, 0D, ico);
			v5.setColorOpaque_I(c);
			v5.setNormal(0F, 0F, 1F);
			rb.renderFaceZPos(bk, 0D, 0D, 0D, ico);
			v5.setColorOpaque_I(c);
			v5.setNormal(-1F, 0F, 0F);
			rb.renderFaceXNeg(bk, 0D, 0D, 0D, ico);
			v5.setColorOpaque_I(c);
			v5.setNormal(1F, 0F, 0F);
			rb.renderFaceXPos(bk, 0D, 0D, 0D, ico);
		}

		v5.draw();
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

	private GridDistortion getDistortion(int n) {
		int idx = n-MIN_SEGMENTS;
		if (distortions[idx] == null) {
			GridDistortion grid = new GridDistortion(n);
			grid.maxDeviation *= 0.66;
			distortions[idx] = grid;
		}
		return distortions[idx];
	}

	private void renderCrystalFace(IBlockAccess world, int x, int y, int z, Block bk, TileCrystalEncrusted te, CrystalGrowth g, RenderBlocks rb, int color) {
		int amt = g.getGrowth();
		int h1 = 3+amt*2;
		int h2 = 8+amt*4;
		int n = MIN_SEGMENTS+rand.nextInt(MAX_SEGMENTS-MIN_SEGMENTS+1);
		double w = 1D/n;
		GridDistortion grid = this.getDistortion(n);
		grid.snapToEdges = false;
		grid.randomize(rand);
		int pieces = Math.min(n*n/2, 6+amt*amt/10);
		if (te.isSpecial())
			pieces *= 1.5;
		boolean[][] rendered = new boolean[n][n];
		for (int i = 0; i < pieces; i++) {
			int a = rand.nextInt(n);
			int b = rand.nextInt(n);
			if (rendered[a][b])
				continue;
			rendered[a][b] = true;
			int rh = h1+rand.nextInt(h2-h1+1);
			double h = rh/96D;
			this.renderCrystalPiece(world, x, y, z, bk, te, g, rb, color, a, b, w, h, grid);
		}
	}

	private void renderCrystalPiece(IBlockAccess world, int x, int y, int z, Block bk, TileCrystalEncrusted te, CrystalGrowth g, RenderBlocks rb, int color, int a, int b, double w, double h, GridDistortion grid) {
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
				renderBlock.setSidePosition(ForgeDirection.DOWN, 0);
				renderBlock.setSidePosition(ForgeDirection.UP, h);
				renderBlock.setSidePosition(ForgeDirection.WEST, a*w);
				renderBlock.setSidePosition(ForgeDirection.EAST, a*w+w);
				renderBlock.setSidePosition(ForgeDirection.NORTH, b*w);
				renderBlock.setSidePosition(ForgeDirection.SOUTH, b*w+w);
				break;
			case UP:
				renderBlock.setSidePosition(ForgeDirection.DOWN, 1-h);
				renderBlock.setSidePosition(ForgeDirection.UP, 1);
				renderBlock.setSidePosition(ForgeDirection.WEST, a*w);
				renderBlock.setSidePosition(ForgeDirection.EAST, a*w+w);
				renderBlock.setSidePosition(ForgeDirection.NORTH, b*w);
				renderBlock.setSidePosition(ForgeDirection.SOUTH, b*w+w);
				break;
			case WEST:
				renderBlock.setSidePosition(ForgeDirection.DOWN, b*w);
				renderBlock.setSidePosition(ForgeDirection.UP, b*w+w);
				renderBlock.setSidePosition(ForgeDirection.WEST, 0);
				renderBlock.setSidePosition(ForgeDirection.EAST, h);
				renderBlock.setSidePosition(ForgeDirection.NORTH, a*w);
				renderBlock.setSidePosition(ForgeDirection.SOUTH, a*w+w);
				break;
			case EAST:
				renderBlock.setSidePosition(ForgeDirection.DOWN, b*w);
				renderBlock.setSidePosition(ForgeDirection.UP, b*w+w);
				renderBlock.setSidePosition(ForgeDirection.WEST, 1-h);
				renderBlock.setSidePosition(ForgeDirection.EAST, 1);
				renderBlock.setSidePosition(ForgeDirection.NORTH, a*w);
				renderBlock.setSidePosition(ForgeDirection.SOUTH, a*w+w);
				break;
			case NORTH:
				renderBlock.setSidePosition(ForgeDirection.DOWN, b*w);
				renderBlock.setSidePosition(ForgeDirection.UP, b*w+w);
				renderBlock.setSidePosition(ForgeDirection.WEST, a*w);
				renderBlock.setSidePosition(ForgeDirection.EAST, a*w+w);
				renderBlock.setSidePosition(ForgeDirection.NORTH, 0);
				renderBlock.setSidePosition(ForgeDirection.SOUTH, h);
				break;
			case SOUTH:
				renderBlock.setSidePosition(ForgeDirection.DOWN, b*w);
				renderBlock.setSidePosition(ForgeDirection.UP, b*w+w);
				renderBlock.setSidePosition(ForgeDirection.WEST, a*w);
				renderBlock.setSidePosition(ForgeDirection.EAST, a*w+w);
				renderBlock.setSidePosition(ForgeDirection.NORTH, 1-h);
				renderBlock.setSidePosition(ForgeDirection.SOUTH, 1);
				break;
			default:
				break;
		}
		renderBlock.applyOffset(g.side, off);
		renderBlock.applyOffset(g.side.getOpposite(), off);
		renderBlock.clamp();
		ReikaRenderHelper.renderBlockPieceNonCuboid(world, x, y, z, bk, Tessellator.instance, renderBlock);

		Tessellator.instance.setBrightness(240);
		Tessellator.instance.setColorRGBA_I(ReikaColorAPI.mixColors(color, 0xffffff, 0.9F), te.isSpecial() ? 240 : 192);
		renderBlock.renderIconOnSides(world, x, y, z, ChromaIcons.GLOWFRAME_TRANS.getIcon(), Tessellator.instance);

		if (te.isSpecial()) {
			Tessellator.instance.setBrightness(240);
			Tessellator.instance.setColorRGBA_I(0xffffff, te.isSpecial() ? 48 : 32);
			renderBlock.renderIconOnSides(world, x, y, z, BlockEncrustedCrystal.specialIcon, Tessellator.instance);
		}
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}
}
