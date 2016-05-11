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

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Interfaces.DecoType;
import Reika.ChromatiCraft.Block.Dimension.BlockDimensionDeco.DimDecoTypes;
import Reika.ChromatiCraft.Block.Dimension.BlockDimensionDecoTile;
import Reika.ChromatiCraft.Block.Dimension.BlockDimensionDecoTile.DimDecoTileTypes;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class DimensionDecoRenderer implements ISimpleBlockRenderingHandler {

	public static int renderPass;

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks rb) {
		DecoType type = block instanceof BlockDimensionDecoTile ? DimDecoTileTypes.list[metadata] : DimDecoTypes.list[metadata];
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glColor4f(1, 1, 1, 1);
		ReikaRenderHelper.disableEntityLighting();
		GL11.glDisable(GL11.GL_LIGHTING);
		Tessellator.instance.startDrawingQuads();
		Tessellator.instance.setColorOpaque_I(0xffffff);
		Tessellator.instance.setBrightness(240);
		Tessellator.instance.addTranslation(0, -0.08F, 0);
		for (int pass = 0; pass <= 1; pass++) {
			List<IIcon> li = type.getIcons(pass);
			for (IIcon ico : li) {
				float f = 0.5F;
				Tessellator.instance.setColorOpaque_F(f, f, f);
				rb.renderFaceYNeg(block, 0, 0, 0, ico);
				f = 1F;
				Tessellator.instance.setColorOpaque_F(f, f, f);
				rb.renderFaceYPos(block, 0, 0, 0, ico);
				f = 0.66F;
				Tessellator.instance.setColorOpaque_F(f, f, f);
				rb.renderFaceXNeg(block, 0, 0, 0, ico);
				rb.renderFaceXPos(block, 0, 0, 0, ico);
				f = 0.8F;
				Tessellator.instance.setColorOpaque_F(f, f, f);
				rb.renderFaceZNeg(block, 0, 0, 0, ico);
				rb.renderFaceZPos(block, 0, 0, 0, ico);
			}
		}
		Tessellator.instance.addTranslation(0, 0.08F, 0);
		Tessellator.instance.draw();
		GL11.glPopAttrib();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block b, int modelId, RenderBlocks rb) {

		int meta = world.getBlockMetadata(x, y, z);
		DecoType type = b instanceof BlockDimensionDecoTile ? DimDecoTileTypes.list[meta] : DimDecoTypes.list[meta];
		if (renderPass == 0) {
			if (type.hasBlockRender()) {
				rb.renderStandardBlockWithAmbientOcclusion(b, x, y, z, 1, 1, 1);
				List<IIcon> li = type.getIcons(0);
				int idx = 0;
				for (IIcon ico : li) {
					Tessellator.instance.setBrightness(240);
					Tessellator.instance.setColorOpaque_I(idx == 1 ? b.colorMultiplier(world, x, y, z) : 0xffffff);
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
					idx++;
				}
			}
			else {
				Tessellator v5 = Tessellator.instance;
				v5.addTranslation(x, y, z);
				if (type instanceof DimDecoTypes) {
					this.render(v5, world, x, y, z, (DimDecoTypes)type, b, rb);
				}
				else if (type instanceof DimDecoTileTypes) {
					this.render(v5, world, x, y, z, (DimDecoTileTypes)type, b, rb);
				}
				v5.addTranslation(-x, -y, -z);
			}
		}
		else if (renderPass == 1) {
			if (type.hasBlockRender()) {
				List<IIcon> li = type.getIcons(1);
				int idx = 0;
				for (IIcon ico : li) {
					Tessellator.instance.setBrightness(240);
					Tessellator.instance.setColorOpaque_I(idx == 0 ? b.colorMultiplier(world, x, y, z) : 0xffffff);
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
					idx++;
				}
			}
			this.renderAuxEffect(world, x, y, z, type, b, modelId, rb);
		}


		return true;
	}

	private void renderAuxEffect(IBlockAccess world, int x, int y, int z, DecoType type, Block block, int modelId, RenderBlocks rb) {
		Tessellator v5 = Tessellator.instance;
		v5.addTranslation(x, y, z);

		if (type instanceof DimDecoTypes) {
			this.renderEffect(v5, world, x, y, z, (DimDecoTypes)type, block, rb);
		}
		else if (type instanceof DimDecoTileTypes) {
			this.renderEffect(v5, world, x, y, z, (DimDecoTileTypes)type, block, rb);
		}

		v5.addVertexWithUV(0, 0, 0, 0, 0);
		v5.addVertexWithUV(0, 0, 0, 0, 0);
		v5.addVertexWithUV(0, 0, 0, 0, 0);
		v5.addVertexWithUV(0, 0, 0, 0, 0);
		v5.addTranslation(-x, -y, -z);
	}

	private void render(Tessellator v5, IBlockAccess world, int x, int y, int z, DimDecoTypes type, Block b, RenderBlocks rb) {
		switch(type) {
			case MIASMA:
				break;
			case FLOATSTONE:
				break;
			case AQUA:
				break;
			case LIFEWATER:
				break;
			case LATTICE:
				break;
			case GEMSTONE:
				break;
			case CRYSTALLEAF:
				break;
			case OCEANSTONE:
				break;
		}
	}

	private void render(Tessellator v5, IBlockAccess world, int x, int y, int z, DimDecoTileTypes type, Block b, RenderBlocks rb) {
		switch(type) {
			case FIREJET:
				IIcon ico = ChromaBlocks.STRUCTSHIELD.getBlockInstance().getIcon(1, 0);
				float u = ico.getMinU();
				float v = ico.getMinV();
				float du = ico.getMaxU();
				float dv = ico.getMaxV();
				v5.addTranslation(-x, -y, -z);
				v5.setColorOpaque_I(0xffffff);
				rb.renderFaceYNeg(b, x, y, z, ico);
				rb.renderFaceZNeg(b, x, y, z, ico);
				rb.renderFaceZPos(b, x, y, z, ico);
				rb.renderFaceXNeg(b, x, y, z, ico);
				rb.renderFaceXPos(b, x, y, z, ico);
				v5.addTranslation(x, y, z);

				double w = 0.375;
				double h = 0.25;

				//Corners
				v5.addVertexWithUV(0, 1, w, u, v);
				v5.addVertexWithUV(w, 1-h, w, du, v);
				v5.addVertexWithUV(w, 1, 0, du, dv);
				v5.addVertexWithUV(0, 1, 0, u, dv);

				v5.addVertexWithUV(1-w, 1-h, w, u, v);
				v5.addVertexWithUV(1, 1, w, du, v);
				v5.addVertexWithUV(1, 1, 0, du, dv);
				v5.addVertexWithUV(1-w, 1, 0, u, dv);

				v5.addVertexWithUV(0, 1, 1, u, v);
				v5.addVertexWithUV(w, 1, 1, du, v);
				v5.addVertexWithUV(w, 1-h, 1-w, du, dv);
				v5.addVertexWithUV(0, 1, 1-w, u, dv);

				v5.addVertexWithUV(1-w, 1, 1, u, v);
				v5.addVertexWithUV(1, 1, 1, du, v);
				v5.addVertexWithUV(1, 1, 1-w, du, dv);
				v5.addVertexWithUV(1-w, 1-h, 1-w, u, dv);

				//Center
				v5.addVertexWithUV(w, 1-h, 1-w, u, v);
				v5.addVertexWithUV(1-w, 1-h, 1-w, du, v);
				v5.addVertexWithUV(1-w, 1-h, w, du, dv);
				v5.addVertexWithUV(w, 1-h, w, u, dv);

				//Sides
				v5.addVertexWithUV(w, 1-h, w, u, v);
				v5.addVertexWithUV(1-w, 1-h, w, du, v);
				v5.addVertexWithUV(1-w, 1, 0, du, dv);
				v5.addVertexWithUV(w, 1, 0, u, dv);

				v5.addVertexWithUV(w, 1, 1, u, v);
				v5.addVertexWithUV(1-w, 1, 1, du, v);
				v5.addVertexWithUV(1-w, 1-h, 1-w, du, dv);
				v5.addVertexWithUV(w, 1-h, 1-w, u, dv);

				v5.addVertexWithUV(0, 1, w, u, dv);
				v5.addVertexWithUV(0, 1, 1-w, du, dv);
				v5.addVertexWithUV(w, 1-h, 1-w, du, v);
				v5.addVertexWithUV(w, 1-h, w, u, v);

				v5.addVertexWithUV(1-w, 1-h, w, u, v);
				v5.addVertexWithUV(1-w, 1-h, 1-w, du, v);
				v5.addVertexWithUV(1, 1, 1-w, du, dv);
				v5.addVertexWithUV(1, 1, w, u, dv);
				break;
		}
	}

	private void renderEffect(Tessellator v5, IBlockAccess world, int x, int y, int z, DimDecoTileTypes type, Block b, RenderBlocks rb) {
		switch(type) {
			case FIREJET:
				break;
		}
	}

	private void renderEffect(Tessellator v5, IBlockAccess world, int x, int y, int z, DimDecoTypes type, Block b, RenderBlocks rb) {
		switch(type) {
			case MIASMA: {
				v5.setBrightness(240);
				int c = ReikaColorAPI.getModifiedHue(0x0000ff, 220+(int)(80*Math.sin((x*x*2+y*y+z*z*8)/(100000D*20))));
				v5.setColorOpaque_I(c);
				IIcon ico = type.getIcons(1).get(0);
				float u = ico.getMinU();
				float v = ico.getMinV();
				float du = ico.getMaxU();
				float dv = ico.getMaxV();
				double s = 1;
				v5.addVertexWithUV(0.5-s, 0.5, 0.5-s, u, v);
				v5.addVertexWithUV(0.5+s, 0.5, 0.5-s, du, v);
				v5.addVertexWithUV(0.5+s, 0.5, 0.5+s, du, dv);
				v5.addVertexWithUV(0.5-s, 0.5, 0.5+s, u, dv);

				v5.addVertexWithUV(0.5-s, 0.5, 0.5+s, u, dv);
				v5.addVertexWithUV(0.5+s, 0.5, 0.5+s, du, dv);
				v5.addVertexWithUV(0.5+s, 0.5, 0.5-s, du, v);
				v5.addVertexWithUV(0.5-s, 0.5, 0.5-s, u, v);

				v5.addVertexWithUV(0.5-s*0.75, 0.5-s, 0.5-s*0.75, u, v);
				v5.addVertexWithUV(0.5+s*0.75, 0.5-s, 0.5+s*0.75, du, v);
				v5.addVertexWithUV(0.5+s*0.75, 0.5+s, 0.5+s*0.75, du, dv);
				v5.addVertexWithUV(0.5-s*0.75, 0.5+s, 0.5-s*0.75, u, dv);

				v5.addVertexWithUV(0.5-s*0.75, 0.5+s, 0.5-s*0.75, u, dv);
				v5.addVertexWithUV(0.5+s*0.75, 0.5+s, 0.5+s*0.75, du, dv);
				v5.addVertexWithUV(0.5+s*0.75, 0.5-s, 0.5+s*0.75, du, v);
				v5.addVertexWithUV(0.5-s*0.75, 0.5-s, 0.5-s*0.75, u, v);

				v5.addVertexWithUV(0.5+s*0.75, 0.5-s, 0.5-s*0.75, u, v);
				v5.addVertexWithUV(0.5-s*0.75, 0.5-s, 0.5+s*0.75, du, v);
				v5.addVertexWithUV(0.5-s*0.75, 0.5+s, 0.5+s*0.75, du, dv);
				v5.addVertexWithUV(0.5+s*0.75, 0.5+s, 0.5-s*0.75, u, dv);

				v5.addVertexWithUV(0.5+s*0.75, 0.5+s, 0.5-s*0.75, u, dv);
				v5.addVertexWithUV(0.5-s*0.75, 0.5+s, 0.5+s*0.75, du, dv);
				v5.addVertexWithUV(0.5-s*0.75, 0.5-s, 0.5+s*0.75, du, v);
				v5.addVertexWithUV(0.5+s*0.75, 0.5-s, 0.5-s*0.75, u, v);
				break;
			}
			case FLOATSTONE: {
				v5.setBrightness(240);
				v5.setColorOpaque_I(0xffffff);
				IIcon ico = ChromaIcons.PURPLESPIN.getIcon();
				float u = ico.getMinU();
				float v = ico.getMinV();
				float du = ico.getMaxU();
				float dv = ico.getMaxV();
				double s = 1.5;
				v5.addVertexWithUV(0.5-s, 0.5, 0.5-s, u, v);
				v5.addVertexWithUV(0.5+s, 0.5, 0.5-s, du, v);
				v5.addVertexWithUV(0.5+s, 0.5, 0.5+s, du, dv);
				v5.addVertexWithUV(0.5-s, 0.5, 0.5+s, u, dv);

				v5.addVertexWithUV(0.5-s, 0.5, 0.5+s, u, dv);
				v5.addVertexWithUV(0.5+s, 0.5, 0.5+s, du, dv);
				v5.addVertexWithUV(0.5+s, 0.5, 0.5-s, du, v);
				v5.addVertexWithUV(0.5-s, 0.5, 0.5-s, u, v);

				v5.addVertexWithUV(0.5-s*0.75, 0.5-s, 0.5-s*0.75, u, v);
				v5.addVertexWithUV(0.5+s*0.75, 0.5-s, 0.5+s*0.75, du, v);
				v5.addVertexWithUV(0.5+s*0.75, 0.5+s, 0.5+s*0.75, du, dv);
				v5.addVertexWithUV(0.5-s*0.75, 0.5+s, 0.5-s*0.75, u, dv);

				v5.addVertexWithUV(0.5-s*0.75, 0.5+s, 0.5-s*0.75, u, dv);
				v5.addVertexWithUV(0.5+s*0.75, 0.5+s, 0.5+s*0.75, du, dv);
				v5.addVertexWithUV(0.5+s*0.75, 0.5-s, 0.5+s*0.75, du, v);
				v5.addVertexWithUV(0.5-s*0.75, 0.5-s, 0.5-s*0.75, u, v);

				v5.addVertexWithUV(0.5+s*0.75, 0.5-s, 0.5-s*0.75, u, v);
				v5.addVertexWithUV(0.5-s*0.75, 0.5-s, 0.5+s*0.75, du, v);
				v5.addVertexWithUV(0.5-s*0.75, 0.5+s, 0.5+s*0.75, du, dv);
				v5.addVertexWithUV(0.5+s*0.75, 0.5+s, 0.5-s*0.75, u, dv);

				v5.addVertexWithUV(0.5+s*0.75, 0.5+s, 0.5-s*0.75, u, dv);
				v5.addVertexWithUV(0.5-s*0.75, 0.5+s, 0.5+s*0.75, du, dv);
				v5.addVertexWithUV(0.5-s*0.75, 0.5-s, 0.5+s*0.75, du, v);
				v5.addVertexWithUV(0.5+s*0.75, 0.5-s, 0.5-s*0.75, u, v);
				break;
			}
			case AQUA: {
				v5.setBrightness(240);
				v5.setColorOpaque_I(0x00ffcf);
				IIcon ico = ChromaBlocks.DOOR.getBlockInstance().getIcon(0, 1);
				float u = ico.getMinU();
				float v = ico.getMinV();
				float du = ico.getMaxU();
				float dv = ico.getMaxV();

				double[] h = new double[4];

				double[][][] ao = {
						{
							{-1, 1, 1, -1},
							{-1, -1, 1, 1}
						},
						{
							{-1, 1, 1, -1},
							{-1, -1, 1, 1},
						},
						{
							{-1, 1, 1, -1},
							{-1, -1, 1, 1},
						},
				};

				double[][] p = {
						{
							x,
							z,
						},
						{
							y,
							z,
						},
						{
							x,
							y,
						},
				};

				for (int n = 0; n < 2; n++) {
					double[] da = ao[n][0];
					double[] db = ao[n][1];
					double ap = p[n][0];
					double bp = p[n][1];

					for (int i = 0; i < 4; i++) {
						double idx = 48*((ap+bp+(da[i]+db[i])*0.5)%16);
						double idx2 = 128*((ap+bp+(da[i]+db[i])*0.5)%32);

						double fac = 24;//Math.sqrt(x*x+z*z)/512D;
						idx *= fac;
						idx2 *= fac;

						h[i] = 0.5+0.5*Math.sin(Math.toRadians(idx))+0.05*Math.sin(Math.toRadians(idx2));
					}

					switch(n) {
						case 0:
							v5.addVertexWithUV(0, h[0], 0, u, v);
							v5.addVertexWithUV(1, h[1], 0, du, v);
							v5.addVertexWithUV(1, h[2], 1, du, dv);
							v5.addVertexWithUV(0, h[3], 1, u, dv);

							v5.addVertexWithUV(0, h[3], 1, u, dv);
							v5.addVertexWithUV(1, h[2], 1, du, dv);
							v5.addVertexWithUV(1, h[1], 0, du, v);
							v5.addVertexWithUV(0, h[0], 0, u, v);
							break;
						case 1:
							v5.addVertexWithUV(h[0], 0, 0, u, v);
							v5.addVertexWithUV(h[1], 1, 0, du, v);
							v5.addVertexWithUV(h[2], 1, 1, du, dv);
							v5.addVertexWithUV(h[3], 0, 1, u, dv);

							v5.addVertexWithUV(h[3], 0, 1, u, dv);
							v5.addVertexWithUV(h[2], 1, 1, du, dv);
							v5.addVertexWithUV(h[1], 1, 0, du, v);
							v5.addVertexWithUV(h[0], 0, 0, u, v);
							break;
						case 2:
							v5.addVertexWithUV(0, 0, h[0], u, v);
							v5.addVertexWithUV(1, 0, h[1], du, v);
							v5.addVertexWithUV(1, 1, h[2], du, dv);
							v5.addVertexWithUV(0, 1, h[3], u, dv);

							v5.addVertexWithUV(0, 0, h[3], u, dv);
							v5.addVertexWithUV(1, 0, h[2], du, dv);
							v5.addVertexWithUV(1, 1, h[1], du, v);
							v5.addVertexWithUV(0, 1, h[0], u, v);
							break;
					}
				}
				break;
			}
			case LIFEWATER:
				break;
			case LATTICE:
				break;
			case GEMSTONE:
				break;
			case CRYSTALLEAF:
				break;
			case OCEANSTONE:
				break;
		}
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return ChromatiCraft.proxy.dimgenRender;
	}

}
