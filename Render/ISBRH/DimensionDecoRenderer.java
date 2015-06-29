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
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Interfaces.DecoType;
import Reika.ChromatiCraft.Block.Dimension.BlockDimensionDeco.DimDecoTypes;
import Reika.ChromatiCraft.Block.Dimension.BlockDimensionDecoTile;
import Reika.ChromatiCraft.Block.Dimension.BlockDimensionDecoTile.DimDecoTileTypes;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class DimensionDecoRenderer implements ISimpleBlockRenderingHandler {

	public static int renderPass;

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {

	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block b, int modelId, RenderBlocks rb) {

		int meta = world.getBlockMetadata(x, y, z);
		DecoType type = b instanceof BlockDimensionDecoTile ? DimDecoTileTypes.list[meta] : DimDecoTypes.list[meta];
		if (renderPass == 0) {
			if (type.hasBlockRender()) {
				rb.renderStandardBlockWithAmbientOcclusion(b, x, y, z, 1, 1, 1);
				IIcon ico = type.getOverlay();
				Tessellator.instance.setBrightness(240);
				Tessellator.instance.setColorOpaque_I(0xffffff);
				rb.renderFaceYNeg(b, x, y, z, ico);
				rb.renderFaceYPos(b, x, y, z, ico);
				rb.renderFaceZNeg(b, x, y, z, ico);
				rb.renderFaceZPos(b, x, y, z, ico);
				rb.renderFaceXNeg(b, x, y, z, ico);
				rb.renderFaceXPos(b, x, y, z, ico);
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
			int c = ReikaColorAPI.getModifiedHue(0x0000ff, 220+(int)(80*Math.sin((x*x*2+y*y+z*z*8)/100000D)));
			v5.setColorOpaque_I(c);
			IIcon ico = type.getOverlay();
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
