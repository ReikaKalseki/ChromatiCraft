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

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Block.BlockPylonStructure;
import Reika.ChromatiCraft.Block.BlockPylonStructure.StoneTypes;
import Reika.DragonAPI.Instantiable.Rendering.StructureRenderer;
import Reika.DragonAPI.Interfaces.ISBRH;

public class CrystallineStoneRenderer implements ISBRH {

	public static int renderPass;

	@Override
	public void renderInventoryBlock(Block bk, int metadata, int modelId, RenderBlocks rb) {
		Tessellator tessellator = Tessellator.instance;

		rb.renderMaxX = 1;
		rb.renderMinY = 0;
		rb.renderMaxZ = 1;
		rb.renderMinX = 0;
		rb.renderMinZ = 0;
		rb.renderMaxY = 1;

		BlockPylonStructure b = (BlockPylonStructure)bk;

		GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

		IIcon ico = b.getIcon(0, metadata);
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, -1.0F, 0.0F);
		rb.renderFaceYNeg(b, 0.0D, 0.0D, 0.0D, ico);
		tessellator.draw();

		ico = b.getIcon(1, metadata);
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		rb.renderFaceYPos(b, 0.0D, 0.0D, 0.0D, ico);
		tessellator.draw();

		ico = b.getIcon(2, metadata);
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, -1.0F);
		rb.renderFaceZNeg(b, 0.0D, 0.0D, 0.0D, ico);
		tessellator.draw();

		ico = b.getIcon(3, metadata);
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		rb.renderFaceZPos(b, 0.0D, 0.0D, 0.0D, ico);
		tessellator.draw();

		ico = b.getIcon(4, metadata);
		tessellator.startDrawingQuads();
		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
		rb.renderFaceXNeg(b, 0.0D, 0.0D, 0.0D, ico);
		tessellator.draw();

		ico = b.getIcon(5, metadata);
		tessellator.startDrawingQuads();
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		rb.renderFaceXPos(b, 0.0D, 0.0D, 0.0D, ico);
		tessellator.draw();

		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glDisable(GL11.GL_LIGHTING);
		tessellator.startDrawingQuads();
		tessellator.setBrightness(240);
		tessellator.setColorRGBA_I(0xffffff, 255);
		for (int i = 0; i < 6; i++) {
			ico = b.getBrightOverlay(metadata, i);
			if (ico != null) {
				//ReikaJavaLibrary.pConsole(metadata+" @ "+i+" > "+ico.getIconName());
				switch(i) {
					case 0:
						rb.renderFaceYNeg(b, 0, 0, 0, ico);
						break;
					case 1:
						rb.renderFaceYPos(b, 0, 0, 0, ico);
						break;
					case 2:
						rb.renderFaceZNeg(b, 0, 0, 0, ico);
						break;
					case 3:
						rb.renderFaceZPos(b, 0, 0, 0, ico);
						break;
					case 4:
						rb.renderFaceXNeg(b, 0, 0, 0, ico);
						break;
					case 5:
						rb.renderFaceXPos(b, 0, 0, 0, ico);
						break;
				}
			}
		}
		tessellator.draw();
		GL11.glPopAttrib();

		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block bk, int modelId, RenderBlocks rb) {
		BlockPylonStructure b = (BlockPylonStructure)bk;
		Tessellator v5 = Tessellator.instance;
		int meta = world.getBlockMetadata(x, y, z);
		StoneTypes s = StoneTypes.list[meta];
		/*
		float f1 = 0.5F;
		float f2 = 1;
		float f3 = 0.8F;
		float f4 = 0.8F;
		float f5 = 0.6F;
		float f6 = 0.6F;


		v5.setBrightness(rb.renderMinY > 0.0D ? l : b.getMixedBrightnessForBlock(world, x, y - 1, z));
		v5.setColorOpaque_F(f1, f1, f1);
		rb.renderFaceYNeg(b, x, y, z, ico);

		v5.setBrightness(rb.renderMaxY < 1.0D ? l : b.getMixedBrightnessForBlock(world, x, y + 1, z));
		v5.setColorOpaque_F(f2, f2, f2);
		rb.renderFaceYPos(b, x, y, z, ico);

		v5.setBrightness(rb.renderMinZ > 0.0D ? l : b.getMixedBrightnessForBlock(world, x, y, z - 1));
		v5.setColorOpaque_F(f3, f3, f3);
		rb.renderFaceZNeg(b, x, y, z, ico);

		v5.setBrightness(rb.renderMaxZ < 1.0D ? l : b.getMixedBrightnessForBlock(world, x, y, z + 1));
		v5.setColorOpaque_F(f4, f4, f4);
		rb.renderFaceZPos(b, x, y, z, ico);

		v5.setBrightness(rb.renderMinX > 0.0D ? l : b.getMixedBrightnessForBlock(world, x - 1, y, z));
		v5.setColorOpaque_F(f5, f5, f5);
		rb.renderFaceXNeg(b, x, y, z, ico);

		v5.setBrightness(rb.renderMaxX < 1.0D ? l : b.getMixedBrightnessForBlock(world, x + 1, y, z));
		v5.setColorOpaque_F(f6, f6, f6);
		rb.renderFaceXPos(b, x, y, z, ico);
		 */
		if (renderPass == 0 || StructureRenderer.isRenderingTiles()) {
			if (s.isConnectedTexture()) {
				v5.addTranslation(x, y, z);
				this.renderConnectedTextures(world, x, y, z, s, b, meta, rb, v5);
				v5.addTranslation(-x, -y, -z);
			}
			else {
				rb.renderStandardBlockWithAmbientOcclusion(b, x, y, z, 1, 1, 1);
			}
		}

		if (renderPass == s.getBrightRenderPass() || StructureRenderer.isRenderingTiles()) {
			v5.setBrightness(240);
			v5.setColorOpaque_F(255, 255, 255);
			for (int i = 0; i < 6; i++) {
				ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
				if (b.shouldSideBeRendered(world, x+dir.offsetX, y+dir.offsetY, z+dir.offsetZ, i)) {
					IIcon ico = b.getBrightOverlay(world, x, y, z, i);
					if (ico != null) {
						switch(dir) {
							case DOWN:
								rb.renderFaceYNeg(b, x, y, z, ico);
								break;
							case UP:
								rb.renderFaceYPos(b, x, y, z, ico);
								break;
							case NORTH:
								rb.renderFaceZNeg(b, x, y, z, ico);
								break;
							case SOUTH:
								rb.renderFaceZPos(b, x, y, z, ico);
								break;
							case WEST:
								rb.renderFaceXNeg(b, x, y, z, ico);
								break;
							case EAST:
								rb.renderFaceXPos(b, x, y, z, ico);
								break;
							default:
								break;
						}
					}
				}
			}
		}

		v5.addVertex(0, 0, 0);
		v5.addVertex(0, 0, 0);
		v5.addVertex(0, 0, 0);
		v5.addVertex(0, 0, 0);
		return true;
	}

	private void renderConnectedTextures(IBlockAccess world, int x, int y, int z, StoneTypes s, BlockPylonStructure b, int meta, RenderBlocks rb, Tessellator v5) {
		v5.setColorRGBA_I(0xffffff, 255);
		ArrayList<Integer> li = b.getEdgesForFace(world, x, y, z, ForgeDirection.UP);
		this.setFaceBrightness(v5, ForgeDirection.UP);
		if (b.shouldSideBeRendered(world, x, y+1, z, ForgeDirection.UP.ordinal())) {
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

				v5.addVertexWithUV(1, 1, 0, u, v);
				v5.addVertexWithUV(0, 1, 0, du, v);
				v5.addVertexWithUV(0, 1, 1, du, dv);
				v5.addVertexWithUV(1, 1, 1, u, dv);
			}
		}

		v5.setBrightness(240);
		li = b.getEdgesForFace(world, x, y, z, ForgeDirection.DOWN);
		this.setFaceBrightness(v5, ForgeDirection.DOWN);
		if (b.shouldSideBeRendered(world, x, y-1, z, ForgeDirection.DOWN.ordinal())) {
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

				v5.addVertexWithUV(0, 0, 0, du, v);
				v5.addVertexWithUV(1, 0, 0, u, v);
				v5.addVertexWithUV(1, 0, 1, u, dv);
				v5.addVertexWithUV(0, 0, 1, du, dv);
			}
		}

		v5.setBrightness(240);
		li = b.getEdgesForFace(world, x, y, z, ForgeDirection.EAST);
		this.setFaceBrightness(v5, ForgeDirection.EAST);
		if (b.shouldSideBeRendered(world, x+1, y, z, ForgeDirection.EAST.ordinal())) {
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

				if (edge == 5) {
					v5.addVertexWithUV(1, 0, 0, du, dv);
					v5.addVertexWithUV(1, 1, 0, du, v);
					v5.addVertexWithUV(1, 1, 1, u, v);
					v5.addVertexWithUV(1, 0, 1, u, dv);
				}
				else {
					v5.addVertexWithUV(1, 0, 0, du, v);
					v5.addVertexWithUV(1, 1, 0, u, v);
					v5.addVertexWithUV(1, 1, 1, u, dv);
					v5.addVertexWithUV(1, 0, 1, du, dv);
				}
			}
		}

		v5.setBrightness(240);
		li = b.getEdgesForFace(world, x, y, z, ForgeDirection.WEST);
		this.setFaceBrightness(v5, ForgeDirection.WEST);
		if (b.shouldSideBeRendered(world, x-1, y, z, ForgeDirection.WEST.ordinal())) {
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

				if (edge == 5) {
					v5.addVertexWithUV(0, 1, 0, u, dv);
					v5.addVertexWithUV(0, 0, 0, u, v);
					v5.addVertexWithUV(0, 0, 1, du, v);
					v5.addVertexWithUV(0, 1, 1, du, dv);
				}
				else {
					v5.addVertexWithUV(0, 1, 0, u, v);
					v5.addVertexWithUV(0, 0, 0, du, v);
					v5.addVertexWithUV(0, 0, 1, du, dv);
					v5.addVertexWithUV(0, 1, 1, u, dv);
				}
			}
		}

		v5.setBrightness(240);
		li = b.getEdgesForFace(world, x, y, z, ForgeDirection.SOUTH);
		this.setFaceBrightness(v5, ForgeDirection.SOUTH);
		if (b.shouldSideBeRendered(world, x, y, z+1, ForgeDirection.SOUTH.ordinal())) {
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

				if (edge == 5) {
					v5.addVertexWithUV(0, 1, 1, u, dv);
					v5.addVertexWithUV(0, 0, 1, u, v);
					v5.addVertexWithUV(1, 0, 1, du, v);
					v5.addVertexWithUV(1, 1, 1, du, dv);
				}
				else {
					v5.addVertexWithUV(0, 1, 1, u, v);
					v5.addVertexWithUV(0, 0, 1, du, v);
					v5.addVertexWithUV(1, 0, 1, du, dv);
					v5.addVertexWithUV(1, 1, 1, u, dv);
				}
			}
		}

		v5.setBrightness(240);
		li = b.getEdgesForFace(world, x, y, z, ForgeDirection.NORTH);
		this.setFaceBrightness(v5, ForgeDirection.NORTH);
		if (b.shouldSideBeRendered(world, x, y, z-1, ForgeDirection.NORTH.ordinal())) {
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

				if (edge == 5) {
					v5.addVertexWithUV(0, 0, 0, du, dv);
					v5.addVertexWithUV(0, 1, 0, du, v);
					v5.addVertexWithUV(1, 1, 0, u, v);
					v5.addVertexWithUV(1, 0, 0, u, dv);
				}
				else {
					v5.addVertexWithUV(0, 0, 0, du, v);
					v5.addVertexWithUV(0, 1, 0, u, v);
					v5.addVertexWithUV(1, 1, 0, u, dv);
					v5.addVertexWithUV(1, 0, 0, du, dv);
				}
			}
		}
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
		v5.setColorRGBA_F(f, f, f, 1);
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return ChromatiCraft.proxy.crystalStoneRender;
	}



}
