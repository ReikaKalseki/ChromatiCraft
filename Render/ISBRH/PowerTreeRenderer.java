/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render.ISBRH;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Block.BlockPowerTree.TileEntityPowerTreeAux;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class PowerTreeRenderer implements ISimpleBlockRenderingHandler {

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks rb) {

	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks rb) {
		TileEntityPowerTreeAux te = (TileEntityPowerTreeAux)world.getTileEntity(x, y, z);
		if (te == null || te.getDirection() == null) {
			rb.renderBlockAllFaces(te == null ? Blocks.bedrock : te.getDirection() == null ? Blocks.clay : Blocks.brick_block, x, y, z);
			return true;
		}
		int growth = te.getGrowth();
		double size = 0.25+0.75*growth/te.MAX_GROWTH;
		double out = size/2;
		double space = 0.5-out;

		double xmin = 0.5-out;
		double xmax = 0.5+out;
		double ymin = 0.5-out;
		double ymax = 0.5+out;
		double zmin = 0.5-out;
		double zmax = 0.5+out;

		xmin -= te.getDirection().offsetX*space;
		xmax -= te.getDirection().offsetX*space;
		ymin -= te.getDirection().offsetY*space;
		ymax -= te.getDirection().offsetY*space;
		zmin -= te.getDirection().offsetZ*space;
		zmax -= te.getDirection().offsetZ*space;

		IIcon ico = block.getIcon(0, 0);
		int color = ReikaColorAPI.mixColors(0, block.colorMultiplier(world, x, y, z), 1-(float)size);
		float u = ico.getMinU();
		float du = ico.getMaxU();
		float v = ico.getMinV();
		float dv = ico.getMaxV();

		Tessellator v5 = Tessellator.instance;
		v5.addTranslation(x, y, z);
		v5.setBrightness(240);
		v5.setColorOpaque_I(color);

		v5.addVertexWithUV(xmin, ymax, zmin, u, dv);
		v5.addVertexWithUV(xmax, ymax, zmin, du, dv);
		v5.addVertexWithUV(xmax, ymin, zmin, du, v);
		v5.addVertexWithUV(xmin, ymin, zmin, u, v);

		v5.addVertexWithUV(xmin, ymin, zmax, u, v);
		v5.addVertexWithUV(xmax, ymin, zmax, du, v);
		v5.addVertexWithUV(xmax, ymax, zmax, du, dv);
		v5.addVertexWithUV(xmin, ymax, zmax, u, dv);

		v5.addVertexWithUV(xmax, ymax, zmin, u, dv);
		v5.addVertexWithUV(xmax, ymax, zmax, du, dv);
		v5.addVertexWithUV(xmax, ymin, zmax, du, v);
		v5.addVertexWithUV(xmax, ymin, zmin, u, v);

		v5.addVertexWithUV(xmin, ymin, zmin, u, v);
		v5.addVertexWithUV(xmin, ymin, zmax, du, v);
		v5.addVertexWithUV(xmin, ymax, zmax, du, dv);
		v5.addVertexWithUV(xmin, ymax, zmin, u, dv);

		v5.addVertexWithUV(xmin, ymin, zmin, u, v);
		v5.addVertexWithUV(xmax, ymin, zmin, du, v);
		v5.addVertexWithUV(xmax, ymin, zmax, du, dv);
		v5.addVertexWithUV(xmin, ymin, zmax, u, dv);

		v5.addVertexWithUV(xmin, ymax, zmax, u, dv);
		v5.addVertexWithUV(xmax, ymax, zmax, du, dv);
		v5.addVertexWithUV(xmax, ymax, zmin, du, v);
		v5.addVertexWithUV(xmin, ymax, zmin, u, v);

		v5.addTranslation(-x, -y, -z);
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return false;
	}

	@Override
	public int getRenderId() {
		return ChromatiCraft.proxy.treeRender;
	}

}
