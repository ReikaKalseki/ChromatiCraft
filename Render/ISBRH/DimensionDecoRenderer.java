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
import Reika.ChromatiCraft.Block.Dimension.BlockDimensionDeco;
import Reika.ChromatiCraft.Block.Dimension.BlockDimensionDeco.Types;
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
		Types type = BlockDimensionDeco.Types.list[meta];
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
		}
		else if (renderPass == 1) {
			this.renderAuxEffect(world, x, y, z, type, b, modelId, rb);
		}


		return true;
	}

	private void renderAuxEffect(IBlockAccess world, int x, int y, int z, Types type, Block block, int modelId, RenderBlocks rb) {
		Tessellator v5 = Tessellator.instance;
		v5.addTranslation(x, y, z);

		switch(type) {
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
		default:
			break;
		}

		v5.addVertexWithUV(0, 0, 0, 0, 0);
		v5.addVertexWithUV(0, 0, 0, 0, 0);
		v5.addVertexWithUV(0, 0, 0, 0, 0);
		v5.addVertexWithUV(0, 0, 0, 0, 0);
		v5.addTranslation(-x, -y, -z);
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
