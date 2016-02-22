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
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.WeightedRandom;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Interfaces.Block.ConnectedTextureGlass;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;


public class SelectiveGlassRenderer implements ISimpleBlockRenderingHandler {

	private static final Random rand = new Random();

	//public final int renderID;

	private static final ForgeDirection[] dirs = ForgeDirection.values();

	public static int renderPass = 0;

	private final WeightedRandom<CrystalElement> possibleRunes = new WeightedRandom();

	private long lastRerender = System.currentTimeMillis();

	public SelectiveGlassRenderer() {
		//renderID = ID;

		possibleRunes.addEntry(CrystalElement.RED, 20);
		possibleRunes.addEntry(CrystalElement.BLACK, 5);
		possibleRunes.addEntry(CrystalElement.PINK, 10);
		possibleRunes.addEntry(CrystalElement.WHITE, 5);
		possibleRunes.addEntry(CrystalElement.LIME, 2);
		possibleRunes.addEntry(CrystalElement.LIGHTGRAY, 2);
	}

	private IIcon getOverlayIcon(long t, ConnectedTextureGlass b, int meta, ForgeDirection dir, IBlockAccess world, int x, int y, int z, boolean checkTime) {
		if (checkTime && t-lastRerender < 4000) {
			return this.getOverlayIcon(lastRerender, b, meta, dir, world, x, y, z, false);
		}
		lastRerender = t;
		long seed = t+new Coordinate(x, y, z).hashCode()+(dir.ordinal() << 6);
		rand.setSeed(seed);
		possibleRunes.setSeed(seed);
		int r = rand.nextInt(100);
		if (r < 80) {
			return null;
		}
		else if (r < 95) {
			Tessellator.instance.setColorRGBA_I(0xffffff, 255);
			return b.getIconForEdge(world, x, y, z, 5);
		}
		else {
			Tessellator.instance.setColorRGBA_I(0xffffff, 96);
			return possibleRunes.getRandomEntry().getEngravingRune();
		}
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks rb) {
		Tessellator v5 = Tessellator.instance;

		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glDisable(GL11.GL_LIGHTING);
		v5.startDrawingQuads();

		v5.setColorOpaque_I(0xffffff);
		v5.setBrightness(240);

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

		this.setFaceBrightness(v5, ForgeDirection.UP, 1);
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

		this.setFaceBrightness(v5, ForgeDirection.DOWN, 1);
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

		this.setFaceBrightness(v5, ForgeDirection.EAST, 1);
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

		this.setFaceBrightness(v5, ForgeDirection.WEST, 1);
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

		this.setFaceBrightness(v5, ForgeDirection.SOUTH, 1);
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

		this.setFaceBrightness(v5, ForgeDirection.NORTH, 1);
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

		GL11.glPopAttrib();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks rb) {
		Tessellator v5 = Tessellator.instance;
		ConnectedTextureGlass b = (ConnectedTextureGlass)block;
		v5.addTranslation(x, y, z);

		v5.setColorOpaque_I(0xffffff);

		int mix = block.getMixedBrightnessForBlock(world, x, y, z);
		v5.setBrightness(240);
		World w = Minecraft.getMinecraft().theWorld;
		float l = Math.max(w.getSavedLightValue(EnumSkyBlock.Block, x, y, z), w.getSavedLightValue(EnumSkyBlock.Sky, x, y, z)*w.getSunBrightnessFactor(0));
		float a = 1-l/32F;//l < 10 ? 0.875F : 0.875F-l/16F;

		if (renderPass == 1) {
			v5.setColorRGBA_I(0xff0000, 128);
			ArrayList<Integer> li = b.getEdgesForFace(world, x, y, z, ForgeDirection.UP);
			this.setFaceBrightness(v5, ForgeDirection.UP, a);
			if (block.shouldSideBeRendered(world, x, y, z, ForgeDirection.UP.ordinal())) {
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
				IIcon ico = this.getOverlayIcon(System.currentTimeMillis(), b, 0, ForgeDirection.UP, world, x, y, z, true);
				if (ico != null) {
					float u = ico.getMinU();
					float du = ico.getMaxU();
					float v = ico.getMinV();
					float dv = ico.getMaxV();
					v5.setBrightness(mix);
					v5.addVertexWithUV(1, 1, 0, u, v);
					v5.addVertexWithUV(0, 1, 0, du, v);
					v5.addVertexWithUV(0, 1, 1, du, dv);
					v5.addVertexWithUV(1, 1, 1, u, dv);
				}
			}

			v5.setBrightness(240);
			li = b.getEdgesForFace(world, x, y, z, ForgeDirection.DOWN);
			this.setFaceBrightness(v5, ForgeDirection.DOWN, a);
			if (block.shouldSideBeRendered(world, x, y, z, ForgeDirection.DOWN.ordinal())) {
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
				IIcon ico = this.getOverlayIcon(System.currentTimeMillis(), b, 0, ForgeDirection.DOWN, world, x, y, z, true);
				if (ico != null) {
					float u = ico.getMinU();
					float du = ico.getMaxU();
					float v = ico.getMinV();
					float dv = ico.getMaxV();
					v5.setBrightness(mix);
					v5.addVertexWithUV(0, 0, 0, du, v);
					v5.addVertexWithUV(1, 0, 0, u, v);
					v5.addVertexWithUV(1, 0, 1, u, dv);
					v5.addVertexWithUV(0, 0, 1, du, dv);
				}
			}

			v5.setBrightness(240);
			li = b.getEdgesForFace(world, x, y, z, ForgeDirection.EAST);
			this.setFaceBrightness(v5, ForgeDirection.EAST, a);
			if (block.shouldSideBeRendered(world, x, y, z, ForgeDirection.EAST.ordinal())) {
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

					v5.addVertexWithUV(1, 0, 0, du, v);
					v5.addVertexWithUV(1, 1, 0, u, v);
					v5.addVertexWithUV(1, 1, 1, u, dv);
					v5.addVertexWithUV(1, 0, 1, du, dv);
				}
				IIcon ico = this.getOverlayIcon(System.currentTimeMillis(), b, 0, ForgeDirection.EAST, world, x, y, z, true);
				if (ico != null) {
					float u = ico.getMinU();
					float du = ico.getMaxU();
					float v = ico.getMinV();
					float dv = ico.getMaxV();
					v5.setBrightness(mix);
					v5.addVertexWithUV(1, 0, 0, du, dv);
					v5.addVertexWithUV(1, 1, 0, du, v);
					v5.addVertexWithUV(1, 1, 1, u, v);
					v5.addVertexWithUV(1, 0, 1, u, dv);
				}
			}

			v5.setBrightness(240);
			li = b.getEdgesForFace(world, x, y, z, ForgeDirection.WEST);
			this.setFaceBrightness(v5, ForgeDirection.WEST, a);
			if (block.shouldSideBeRendered(world, x, y, z, ForgeDirection.WEST.ordinal())) {
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

					v5.addVertexWithUV(0, 1, 0, u, v);
					v5.addVertexWithUV(0, 0, 0, du, v);
					v5.addVertexWithUV(0, 0, 1, du, dv);
					v5.addVertexWithUV(0, 1, 1, u, dv);
				}
				IIcon ico = this.getOverlayIcon(System.currentTimeMillis(), b, 0, ForgeDirection.WEST, world, x, y, z, true);
				if (ico != null) {
					float u = ico.getMinU();
					float du = ico.getMaxU();
					float v = ico.getMinV();
					float dv = ico.getMaxV();
					v5.setBrightness(mix);
					v5.addVertexWithUV(0, 0, 1, du, dv);
					v5.addVertexWithUV(0, 1, 1, du, v);
					v5.addVertexWithUV(0, 1, 0, u, v);
					v5.addVertexWithUV(0, 0, 0, u, dv);
				}
			}

			v5.setBrightness(240);
			li = b.getEdgesForFace(world, x, y, z, ForgeDirection.SOUTH);
			this.setFaceBrightness(v5, ForgeDirection.SOUTH, a);
			if (block.shouldSideBeRendered(world, x, y, z, ForgeDirection.SOUTH.ordinal())) {
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

					v5.addVertexWithUV(0, 1, 1, u, v);
					v5.addVertexWithUV(0, 0, 1, du, v);
					v5.addVertexWithUV(1, 0, 1, du, dv);
					v5.addVertexWithUV(1, 1, 1, u, dv);
				}
				IIcon ico = this.getOverlayIcon(System.currentTimeMillis(), b, 0, ForgeDirection.SOUTH, world, x, y, z, true);
				if (ico != null) {
					float u = ico.getMinU();
					float du = ico.getMaxU();
					float v = ico.getMinV();
					float dv = ico.getMaxV();
					v5.setBrightness(mix);
					v5.addVertexWithUV(1, 0, 1, du, dv);
					v5.addVertexWithUV(1, 1, 1, du, v);
					v5.addVertexWithUV(0, 1, 1, u, v);
					v5.addVertexWithUV(0, 0, 1, u, dv);
				}
			}

			v5.setBrightness(240);
			li = b.getEdgesForFace(world, x, y, z, ForgeDirection.NORTH);
			this.setFaceBrightness(v5, ForgeDirection.NORTH, a);
			if (block.shouldSideBeRendered(world, x, y, z, ForgeDirection.NORTH.ordinal())) {
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
					v5.addVertexWithUV(0, 1, 0, u, v);
					v5.addVertexWithUV(1, 1, 0, u, dv);
					v5.addVertexWithUV(1, 0, 0, du, dv);
				}
				IIcon ico = this.getOverlayIcon(System.currentTimeMillis(), b, 0, ForgeDirection.NORTH, world, x, y, z, true);
				if (ico != null) {
					float u = ico.getMinU();
					float du = ico.getMaxU();
					float v = ico.getMinV();
					float dv = ico.getMaxV();
					v5.setBrightness(mix);
					v5.addVertexWithUV(1, 1, 0, u, v);
					v5.addVertexWithUV(1, 0, 0, u, dv);
					v5.addVertexWithUV(0, 0, 0, du, dv);
					v5.addVertexWithUV(0, 1, 0, du, v);
				}
			}
		}

		v5.addVertex(0, 0, 0);
		v5.addVertex(0, 0, 0);
		v5.addVertex(0, 0, 0);
		v5.addVertex(0, 0, 0);
		v5.addTranslation(-x, -y, -z);
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int model) {
		return true;
	}

	@Override
	public int getRenderId() {
		return 0;//renderID;
	}

	private void setFaceBrightness(Tessellator v5, ForgeDirection dir, float a) {
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
		v5.setColorRGBA_F(f, f, f, a);
	}

}
