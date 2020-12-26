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

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Block.Crystal.BlockPowerTree.TileEntityPowerTreeAux;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Storage.TileEntityPowerTree;
import Reika.DragonAPI.Base.ISBRH;
import Reika.DragonAPI.Instantiable.Rendering.StructureRenderer;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;

public class PowerTreeRenderer extends ISBRH {

	public PowerTreeRenderer(int id) {
		super(id);
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks rb) {

	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks rb) {
		TileEntityPowerTreeAux te = (TileEntityPowerTreeAux)world.getTileEntity(x, y, z);
		ForgeDirection dir = te != null ? te.getDirection() : null;
		int growth = te != null ? te.getGrowth() : 0;
		if (StructureRenderer.isRenderingTiles() && te == null || dir == null) {
			te = new TileEntityPowerTreeAux();
			te.worldObj = Minecraft.getMinecraft().theWorld;
			te.xCoord = x;
			te.yCoord = y;
			te.zCoord = z;
			dir = TileEntityPowerTree.getDirection(CrystalElement.elements[world.getBlockMetadata(x, y, z)]);
			growth = TileEntityPowerTreeAux.MAX_GROWTH;
		}
		if (te == null) {
			rb.renderBlockAllFaces(Blocks.bedrock, x, y, z);
			return true;
		}
		if (dir == null) {
			rb.renderBlockAllFaces(Blocks.clay, x, y, z);
			return true;
		}
		double size = 0.25+0.75*growth/te.MAX_GROWTH;
		double out = size/2;
		double space = 0.5-out;

		double xmin = 0.5-out;
		double xmax = 0.5+out;
		double ymin = 0.5-out;
		double ymax = 0.5+out;
		double zmin = 0.5-out;
		double zmax = 0.5+out;

		xmin -= dir.offsetX*space;
		xmax -= dir.offsetX*space;
		ymin -= dir.offsetY*space;
		ymax -= dir.offsetY*space;
		zmin -= dir.offsetZ*space;
		zmax -= dir.offsetZ*space;

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

}
