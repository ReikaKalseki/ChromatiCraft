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

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalConsole;
import Reika.DragonAPI.Interfaces.Block.ConnectedTextureGlass;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;


public class ConsoleRenderer implements ISimpleBlockRenderingHandler {

	private static final ForgeDirection[] dirs = ForgeDirection.values();

	public static int renderPass = 0;

	public ConsoleRenderer() {

	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks rb) {
		Tessellator v5 = Tessellator.instance;

		GL11.glColor3f(1, 1, 1);
		v5.startDrawingQuads();

		ConnectedTextureGlass b = (ConnectedTextureGlass)block;

		boolean render5 = b.renderCentralTextureForItem(metadata);

		IIcon ico = b.getIconForEdge(metadata, 0);
		IIcon ico2 = b.getIconForEdge(metadata, 5);
		float u = ico.getMinU();
		float du = ico.getMaxU();
		float v = ico.getMinV();
		float dv = ico.getMaxV();

		float u2 = ico2.getMinU();
		float du2 = ico2.getMaxU();
		float v2 = ico2.getMinV();
		float dv2 = ico2.getMaxV();

		float dx = -0.5F;
		float dy = -0.5F;
		float dz = -0.5F;
		v5.addTranslation(dx, dy, dz);

		this.setFaceBrightness(v5, ForgeDirection.UP);
		v5.setNormal(0, 1, 0);
		v5.addVertexWithUV(1, 1, 0, u, v);
		v5.addVertexWithUV(0, 1, 0, du, v);
		v5.addVertexWithUV(0, 1, 1, du, dv);
		v5.addVertexWithUV(1, 1, 1, u, dv);

		if (render5) {
			v5.addVertexWithUV(1, 1, 0, u2, v2);
			v5.addVertexWithUV(0, 1, 0, du2, v2);
			v5.addVertexWithUV(0, 1, 1, du2, dv2);
			v5.addVertexWithUV(1, 1, 1, u2, dv2);
		}

		this.setFaceBrightness(v5, ForgeDirection.DOWN);
		v5.addVertexWithUV(0, 0, 0, du, v);
		v5.addVertexWithUV(1, 0, 0, u, v);
		v5.addVertexWithUV(1, 0, 1, u, dv);
		v5.addVertexWithUV(0, 0, 1, du, dv);

		if (render5) {
			v5.addVertexWithUV(0, 0, 0, du2, v2);
			v5.addVertexWithUV(1, 0, 0, u2, v2);
			v5.addVertexWithUV(1, 0, 1, u2, dv2);
			v5.addVertexWithUV(0, 0, 1, du2, dv2);
		}

		this.setFaceBrightness(v5, ForgeDirection.EAST);
		v5.addVertexWithUV(1, 0, 0, du, v);
		v5.addVertexWithUV(1, 1, 0, u, v);
		v5.addVertexWithUV(1, 1, 1, u, dv);
		v5.addVertexWithUV(1, 0, 1, du, dv);

		if (render5) {
			v5.addVertexWithUV(1, 0, 0, du2, v2);
			v5.addVertexWithUV(1, 1, 0, u2, v2);
			v5.addVertexWithUV(1, 1, 1, u2, dv2);
			v5.addVertexWithUV(1, 0, 1, du2, dv2);
		}

		this.setFaceBrightness(v5, ForgeDirection.WEST);
		v5.addVertexWithUV(0, 1, 0, u, v);
		v5.addVertexWithUV(0, 0, 0, du, v);
		v5.addVertexWithUV(0, 0, 1, du, dv);
		v5.addVertexWithUV(0, 1, 1, u, dv);

		if (render5) {
			v5.addVertexWithUV(0, 1, 0, u2, v2);
			v5.addVertexWithUV(0, 0, 0, du2, v2);
			v5.addVertexWithUV(0, 0, 1, du2, dv2);
			v5.addVertexWithUV(0, 1, 1, u2, dv2);
		}

		this.setFaceBrightness(v5, ForgeDirection.SOUTH);
		v5.addVertexWithUV(0, 1, 1, u, v);
		v5.addVertexWithUV(0, 0, 1, du, v);
		v5.addVertexWithUV(1, 0, 1, du, dv);
		v5.addVertexWithUV(1, 1, 1, u, dv);

		if (render5) {
			v5.addVertexWithUV(0, 1, 1, u2, v2);
			v5.addVertexWithUV(0, 0, 1, du2, v2);
			v5.addVertexWithUV(1, 0, 1, du2, dv2);
			v5.addVertexWithUV(1, 1, 1, u2, dv2);
		}

		this.setFaceBrightness(v5, ForgeDirection.NORTH);
		v5.addVertexWithUV(0, 0, 0, du, v);
		v5.addVertexWithUV(0, 1, 0, u, v);
		v5.addVertexWithUV(1, 1, 0, u, dv);
		v5.addVertexWithUV(1, 0, 0, du, dv);

		if (render5) {
			v5.addVertexWithUV(0, 0, 0, du2, v2);
			v5.addVertexWithUV(0, 1, 0, u2, v2);
			v5.addVertexWithUV(1, 1, 0, u2, dv2);
			v5.addVertexWithUV(1, 0, 0, du2, dv2);
		}

		v5.addTranslation(-dx, -dy, -dz);

		v5.draw();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks rb) {
		Tessellator v5 = Tessellator.instance;

		if (renderPass == 0) {
			rb.renderStandardBlockWithAmbientOcclusion(block, x, y, z, 1, 1, 1);
		}
		else if (renderPass == 1) {

			TileEntityCrystalConsole te = (TileEntityCrystalConsole)world.getTileEntity(x, y, z);
			if (te.isValid()) {

				ConnectedTextureGlass b = (ConnectedTextureGlass)block;
				v5.addTranslation(x, y, z);

				double o = 0.005;

				ArrayList<Integer> li = b.getEdgesForFace(world, x, y, z, ForgeDirection.UP);
				this.setFaceBrightness(v5, ForgeDirection.UP);
				if (block.shouldSideBeRendered(world, x, y+1, z, ForgeDirection.UP.ordinal())) {
					int mix = block.getMixedBrightnessForBlock(world, x, y+1, z);
					v5.setBrightness(mix);
					v5.setNormal(0, 1, 0);
					for (int i = 0; i < li.size(); i++) {
						int edge = li.get(i);
						IIcon ico = b.getIconForEdge(world, x, y, z, edge);
						float u = ico.getMinU();
						float du = ico.getMaxU();
						float v = ico.getMinV();
						float dv = ico.getMaxV();
						float uu = du-u;
						float vv = dv-v;
						float dx = uu/16F;
						float dz = vv/16F;

						v5.addVertexWithUV(1, 1+o, 0, u, v);
						v5.addVertexWithUV(0, 1+o, 0, du, v);
						v5.addVertexWithUV(0, 1+o, 1, du, dv);
						v5.addVertexWithUV(1, 1+o, 1, u, dv);
					}
				}



				li = b.getEdgesForFace(world, x, y, z, ForgeDirection.DOWN);
				this.setFaceBrightness(v5, ForgeDirection.DOWN);
				if (block.shouldSideBeRendered(world, x, y-1, z, ForgeDirection.DOWN.ordinal())) {
					int mix = block.getMixedBrightnessForBlock(world, x, y-1, z);
					v5.setBrightness(mix);
					v5.setNormal(0, -1, 0);
					for (int i = 0; i < li.size(); i++) {
						int edge = li.get(i);
						IIcon ico = b.getIconForEdge(world, x, y, z, edge);
						float u = ico.getMinU();
						float du = ico.getMaxU();
						float v = ico.getMinV();
						float dv = ico.getMaxV();
						float uu = du-u;
						float vv = dv-v;
						float dx = uu/16F;
						float dz = vv/16F;

						v5.addVertexWithUV(0, -o, 0, du, v);
						v5.addVertexWithUV(1, -o, 0, u, v);
						v5.addVertexWithUV(1, -o, 1, u, dv);
						v5.addVertexWithUV(0, -o, 1, du, dv);
					}
				}


				li = b.getEdgesForFace(world, x, y, z, ForgeDirection.EAST);
				this.setFaceBrightness(v5, ForgeDirection.EAST);
				if (block.shouldSideBeRendered(world, x+1, y, z, ForgeDirection.EAST.ordinal())) {
					int mix = block.getMixedBrightnessForBlock(world, x+1, y, z);
					v5.setBrightness(mix);
					v5.setNormal(1, 0, 0);
					for (int i = 0; i < li.size(); i++) {
						int edge = li.get(i);
						IIcon ico = b.getIconForEdge(world, x, y, z, edge);
						float u = ico.getMinU();
						float du = ico.getMaxU();
						float v = ico.getMinV();
						float dv = ico.getMaxV();
						float uu = du-u;
						float vv = dv-v;
						float dx = uu/16F;
						float dz = vv/16F;

						v5.addVertexWithUV(1+o, 0, 0, du, v);
						v5.addVertexWithUV(1+o, 1, 0, u, v);
						v5.addVertexWithUV(1+o, 1, 1, u, dv);
						v5.addVertexWithUV(1+o, 0, 1, du, dv);
					}
				}

				li = b.getEdgesForFace(world, x, y, z, ForgeDirection.WEST);
				this.setFaceBrightness(v5, ForgeDirection.WEST);
				if (block.shouldSideBeRendered(world, x-1, y, z, ForgeDirection.WEST.ordinal())) {
					int mix = block.getMixedBrightnessForBlock(world, x-1, y, z);
					v5.setBrightness(mix);
					v5.setNormal(-1, 0, 0);
					for (int i = 0; i < li.size(); i++) {
						int edge = li.get(i);
						IIcon ico = b.getIconForEdge(world, x, y, z, edge);
						float u = ico.getMinU();
						float du = ico.getMaxU();
						float v = ico.getMinV();
						float dv = ico.getMaxV();
						float uu = du-u;
						float vv = dv-v;
						float dx = uu/16F;
						float dz = vv/16F;

						v5.addVertexWithUV(-o, 1, 0, u, v);
						v5.addVertexWithUV(-o, 0, 0, du, v);
						v5.addVertexWithUV(-o, 0, 1, du, dv);
						v5.addVertexWithUV(-o, 1, 1, u, dv);
					}
				}

				li = b.getEdgesForFace(world, x, y, z, ForgeDirection.SOUTH);
				this.setFaceBrightness(v5, ForgeDirection.SOUTH);
				if (block.shouldSideBeRendered(world, x, y, z+1, ForgeDirection.SOUTH.ordinal())) {
					int mix = block.getMixedBrightnessForBlock(world, x, y, z+1);
					v5.setBrightness(mix);
					v5.setNormal(0, 0, 1);
					for (int i = 0; i < li.size(); i++) {
						int edge = li.get(i);
						IIcon ico = b.getIconForEdge(world, x, y, z, edge);
						float u = ico.getMinU();
						float du = ico.getMaxU();
						float v = ico.getMinV();
						float dv = ico.getMaxV();
						float uu = du-u;
						float vv = dv-v;
						float dx = uu/16F;
						float dz = vv/16F;

						v5.addVertexWithUV(0, 1, 1+o, u, v);
						v5.addVertexWithUV(0, 0, 1+o, du, v);
						v5.addVertexWithUV(1, 0, 1+o, du, dv);
						v5.addVertexWithUV(1, 1, 1+o, u, dv);
					}
				}

				li = b.getEdgesForFace(world, x, y, z, ForgeDirection.NORTH);
				this.setFaceBrightness(v5, ForgeDirection.NORTH);
				if (block.shouldSideBeRendered(world, x, y, z-1, ForgeDirection.NORTH.ordinal())) {
					int mix = block.getMixedBrightnessForBlock(world, x, y, z-1);
					v5.setBrightness(mix);
					v5.setNormal(0, 0, -1);
					for (int i = 0; i < li.size(); i++) {
						int edge = li.get(i);
						IIcon ico = b.getIconForEdge(world, x, y, z, edge);
						float u = ico.getMinU();
						float du = ico.getMaxU();
						float v = ico.getMinV();
						float dv = ico.getMaxV();
						float uu = du-u;
						float vv = dv-v;
						float dx = uu/16F;
						float dz = vv/16F;

						v5.addVertexWithUV(0, 0, -o, du, v);
						v5.addVertexWithUV(0, 1, -o, u, v);
						v5.addVertexWithUV(1, 1, -o, u, dv);
						v5.addVertexWithUV(1, 0, -o, du, dv);
					}
				}
				v5.addTranslation(-x, -y, -z);
			}
		}

		v5.addVertexWithUV(0, 0, 0, 0, 0);
		v5.addVertexWithUV(0, 0, 0, 0, 0);
		v5.addVertexWithUV(0, 0, 0, 0, 0);
		v5.addVertexWithUV(0, 0, 0, 0, 0);

		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int model) {
		return true;
	}

	@Override
	public int getRenderId() {
		return ChromatiCraft.proxy.consoleRender;
	}

	private void setFaceBrightness(Tessellator v5, ForgeDirection dir) {
		float f = 1;
		switch(dir) {
			case DOWN:
				f = 0.4F;
				break;
			case EAST:
				f = 0.5F;
				break;
			case NORTH:
				f = 0.65F;
				break;
			case SOUTH:
				f = 0.65F;
				break;
			case UP:
				f = 1F;
				break;
			case WEST:
				f = 0.5F;
				break;
			default:
				break;
		}
		v5.setColorOpaque_F(f, f, f);
	}

}
