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
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Auxiliary.Interfaces.CrystalRenderedBlock;
import Reika.ChromatiCraft.Base.CrystalBlock;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.Base.ISBRH;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Rendering.TessellatorVertexList;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;

public class CrystalRenderer extends ISBRH {

	public static boolean renderAllArmsInInventory = false;
	public static int staticColor = -1;

	public CrystalRenderer(int id) {
		super(id);
	}

	@Override
	public void renderInventoryBlock(Block b, int meta, int modelID, RenderBlocks rb) {
		//GL11.glDisable(GL11.GL_LIGHTING);
		Tessellator v5 = Tessellator.instance;
		//v5.setBrightness(240);
		int color = ReikaColorAPI.getColorWithBrightnessMultiplier(((CrystalRenderedBlock)b).getTintColor(meta), 0.8F);
		if (staticColor >= 0)
			color = staticColor;
		int alpha = 255;
		IIcon ico = b.getIcon(0, meta);
		double u = ico.getMinU();
		double v = ico.getMinV();
		double xu = ico.getMaxU();
		double xv = ico.getMaxV();
		int w = ico.getIconWidth();

		v5.startDrawingQuads();
		v5.setNormal(0, 0.8F, 0);
		v5.setColorRGBA_I(color, alpha);
		this.renderSpike(u, v, xu, xv, w, false, false);
		v5.draw();

		v5.startDrawingQuads();
		//v5.setBrightness(240);
		v5.setNormal(0, 0.5F, 0);
		v5.setColorRGBA_I(color, alpha);
		this.renderXAngledSpike(u, v, xu, xv, 0.1875, w, false, false);
		if (renderAllArmsInInventory)
			this.renderXAngledSpike(u, v, xu, xv, -0.1875, w, false, false);
		v5.draw();

		v5.startDrawingQuads();
		//v5.setBrightness(240);
		v5.setNormal(0, 0.5F, 0);
		v5.setColorRGBA_I(color, alpha);
		this.renderZAngledSpike(u, v, xu, xv, 0.1875, w, false, false);
		if (renderAllArmsInInventory)
			this.renderZAngledSpike(u, v, xu, xv, -0.1875, w, false, false);
		v5.draw();
		//GL11.glEnable(GL11.GL_LIGHTING);

		if (((CrystalRenderedBlock)b).renderBase()) {

			v5.startDrawingQuads();
			//v5.setBrightness(240);
			v5.setColorRGBA_I(color, alpha);
			//this.renderXAngledSpike(v5, u, v, xu, xv, -0.1875, w);
			v5.draw();

			v5.startDrawingQuads();
			//v5.setBrightness(240);
			v5.setColorRGBA_I(color, alpha);
			//this.renderZAngledSpike(v5, u, v, xu, xv, -0.1875, w);
			v5.draw();

			v5.startDrawingQuads();
			//v5.setBrightness(240);
			v5.setColorRGBA_I(color, alpha);
			this.renderBase(v5, Minecraft.getMinecraft().theWorld, 0, 0, 0, (CrystalRenderedBlock)b, false);
			v5.draw();
		}
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block b, int modelId, RenderBlocks rb) {
		int meta = world.getBlockMetadata(x, y, z);
		//boolean discover = ProgressionManager.instance.hasPlayerDiscoveredColor(Minecraft.getMinecraft().thePlayer);
		int color = /*discover ? */((CrystalRenderedBlock)b).getTintColor(meta);// : 0xbbbbbb;
		int alpha = /*discover ? */220;// : 32;

		IIcon ico = b.getIcon(world, x, y, z, 0);
		//ico = Blocks.blockNetherQuartz.getIcon(0, 0);
		double u = ico.getMinU();
		double v = ico.getMinV();
		double xu = ico.getMaxU();
		double xv = ico.getMaxV();

		//xu = u = xv = v = 0;

		Tessellator v5 = Tessellator.instance;

		double maxx = b.getBlockBoundsMaxX();
		double minx = b.getBlockBoundsMinX();
		double miny = b.getBlockBoundsMinY();
		double maxy = b.getBlockBoundsMaxY();
		double maxz = b.getBlockBoundsMaxZ();
		double minz = b.getBlockBoundsMinZ();

		int l = b.getMixedBrightnessForBlock(world, x, y, z);

		boolean above = world.getBlock(x, y+1, z) == ChromaBlocks.CRYSTAL.getBlockInstance();
		boolean below = !((CrystalRenderedBlock)b).renderBase() && world.getBlock(x, y-1, z) instanceof CrystalBlock;

		//v5.setBrightness(rb.renderMaxY < 1.0D ? l : b.getMixedBrightnessForBlock(world, x, y+1, z));
		v5.setBrightness(240);
		v5.addTranslation(x, y, z);
		v5.setColorRGBA_I(color, alpha);

		int w = ico.getIconWidth();

		rand.setSeed(this.calcSeed(x, y, z));
		rand.nextBoolean();
		rand.nextBoolean();

		boolean flip = !world.getBlock(x, y-1, z).isSideSolid(world, x, y-1, z, ForgeDirection.UP) && world.getBlock(x, y+1, z).isSideSolid(world, x, y+1, z, ForgeDirection.DOWN);

		if (renderPass == 1) {
			this.renderSpike(u, v, xu, xv, w, above, flip);
			int val = rand.nextInt(16);//((x*3+y+z*7)%16+16)%16; //16 combos -> binary selector
			if (val > 15 || ((CrystalRenderedBlock)b).renderAllArms())
				val = 15;
			if ((val & 8) == 8)
				this.renderXAngledSpike(u, v, xu, xv, 0.1875, w, below, flip); //8,9,10,11,12,13,14,15
			if ((val & 4) == 4)
				this.renderXAngledSpike(u, v, xu, xv, -0.1875, w, below, flip); //4,5,6,7,12,13,14,15
			if ((val & 2) == 2)
				this.renderZAngledSpike(u, v, xu, xv, 0.1875, w, below, flip); //2,3,6,7,10,11,14,15
			if ((val & 1) == 1)
				this.renderZAngledSpike(u, v, xu, xv, -0.1875, w, below, flip); //1,3,5,7,9,11,13,15

			v5.setColorOpaque_I(0xffffff);
			//this.renderWater(world, x, y, z, b, meta, v5);
		}

		if (renderPass == 0) {
			if (((CrystalRenderedBlock)b).renderBase()) {
				this.renderBase(v5, world, x, y, z, (CrystalRenderedBlock)b, flip);
			}
		}

		v5.addTranslation(-x, -y, -z);

		return true;
	}

	private void renderBase(Tessellator v5, IBlockAccess world, int x, int y, int z, CrystalRenderedBlock b, boolean flip) {
		BlockKey bk = b.getBaseBlock(world, x, y, z, ForgeDirection.UP);
		IIcon ico = bk.blockID.getIcon(0, bk.metadata);
		int w = ico.getIconWidth();

		v5.setColorOpaque(255, 255, 255);

		double u = ico.getMinU();
		double v = ico.getMinV();
		double xu = ico.getMaxU();
		double xv = ico.getMaxV();

		double top = 0.125;

		if (flip) {
			v5.addVertexWithUV(0, 1, 1, u, v);
			v5.addVertexWithUV(1, 1, 1, xu, v);
			v5.addVertexWithUV(1, 1, 0, xu, xv);
			v5.addVertexWithUV(0, 1, 0, u, xv);

			v5.setColorOpaque(110, 110, 110);
			v5.addVertexWithUV(0, 1-top, 0, u, xv);
			v5.addVertexWithUV(1, 1-top, 0, xu, xv);
			v5.addVertexWithUV(1, 1-top, 1, xu, v);
			v5.addVertexWithUV(0, 1-top, 1, u, v);
		}
		else {
			v5.addVertexWithUV(0, top, 1, u, xv);
			v5.addVertexWithUV(1, top, 1, xu, xv);
			v5.addVertexWithUV(1, top, 0, xu, v);
			v5.addVertexWithUV(0, top, 0, u, v);

			v5.setColorOpaque(110, 110, 110);
			v5.addVertexWithUV(0, 0, 0, u, v);
			v5.addVertexWithUV(1, 0, 0, xu, v);
			v5.addVertexWithUV(1, 0, 1, xu, xv);
			v5.addVertexWithUV(0, 0, 1, u, xv);
		}

		bk = b.getBaseBlock(world, x, y, z, ForgeDirection.UP);
		ico = bk.blockID.getIcon(0, bk.metadata);
		u = ico.getMinU();
		v = ico.getMinV();
		xu = ico.getMaxU();
		xv = ico.getMaxV();

		double vv = v+(xv-v)/(w)*2;

		if (flip) {
			v5.setColorOpaque(200, 200, 200);
			v5.addVertexWithUV(0, 1, 0, u, vv);
			v5.addVertexWithUV(1, 1, 0, xu, vv);
			v5.addVertexWithUV(1, 1-top, 0, xu, v);
			v5.addVertexWithUV(0, 1-top, 0, u, v);

			v5.setColorOpaque(170, 170, 170);
			v5.addVertexWithUV(0, 1-top, 1, u, v);
			v5.addVertexWithUV(1, 1-top, 1, xu, v);
			v5.addVertexWithUV(1, 1, 1, xu, vv);
			v5.addVertexWithUV(0, 1, 1, u, vv);

			v5.setColorOpaque(200, 200, 200);
			v5.addVertexWithUV(0, 1-top, 0, u, v);
			v5.addVertexWithUV(0, 1-top, 1, xu, v);
			v5.addVertexWithUV(0, 1, 1, xu, vv);
			v5.addVertexWithUV(0, 1, 0, u, vv);

			v5.setColorOpaque(170, 170, 170);
			v5.addVertexWithUV(1, 1, 0, u, vv);
			v5.addVertexWithUV(1, 1, 1, xu, vv);
			v5.addVertexWithUV(1, 1-top, 1, xu, v);
			v5.addVertexWithUV(1, 1-top, 0, u, v);
		}
		else {
			v5.setColorOpaque(200, 200, 200);
			v5.addVertexWithUV(0, top, 0, u, vv);
			v5.addVertexWithUV(1, top, 0, xu, vv);
			v5.addVertexWithUV(1, 0, 0, xu, v);
			v5.addVertexWithUV(0, 0, 0, u, v);

			v5.setColorOpaque(170, 170, 170);
			v5.addVertexWithUV(0, 0, 1, u, v);
			v5.addVertexWithUV(1, 0, 1, xu, v);
			v5.addVertexWithUV(1, top, 1, xu, vv);
			v5.addVertexWithUV(0, top, 1, u, vv);

			v5.setColorOpaque(200, 200, 200);
			v5.addVertexWithUV(0, 0, 0, u, v);
			v5.addVertexWithUV(0, 0, 1, xu, v);
			v5.addVertexWithUV(0, top, 1, xu, vv);
			v5.addVertexWithUV(0, top, 0, u, vv);

			v5.setColorOpaque(170, 170, 170);
			v5.addVertexWithUV(1, top, 0, u, vv);
			v5.addVertexWithUV(1, top, 1, xu, vv);
			v5.addVertexWithUV(1, 0, 1, xu, v);
			v5.addVertexWithUV(1, 0, 0, u, v);
		}
	}

	private void renderOutline(boolean flip) {
		double core = 0.15;
		double vl = 0.8;
		double dd = 0.01;
		double tip = 1;
		double zf = 0.4;

		TessellatorVertexList v5 = new TessellatorVertexList();

		v5.addVertexWithUV(0.5+core-dd, 0, 0.5-core, 0, 0);
		v5.addVertexWithUV(0.5+core+dd, 0, 0.5-core, 0, 0);
		v5.addVertexWithUV(0.5+core+dd, vl, 0.5-core, 0, 0);
		v5.addVertexWithUV(0.5+core-dd, vl, 0.5-core, 0, 0);

		v5.addVertexWithUV(0.5+core, 0, 0.5-core-dd, 0, 0);
		v5.addVertexWithUV(0.5+core, 0, 0.5-core+dd, 0, 0);
		v5.addVertexWithUV(0.5+core, vl, 0.5-core+dd, 0, 0);
		v5.addVertexWithUV(0.5+core, vl, 0.5-core-dd, 0, 0);

		v5.addVertexWithUV(0.5-core-dd, 0, 0.5-core, 0, 0);
		v5.addVertexWithUV(0.5-core+dd, 0, 0.5-core, 0, 0);
		v5.addVertexWithUV(0.5-core+dd, vl, 0.5-core, 0, 0);
		v5.addVertexWithUV(0.5-core-dd, vl, 0.5-core, 0, 0);

		v5.addVertexWithUV(0.5-core, 0, 0.5-core-dd, 0, 0);
		v5.addVertexWithUV(0.5-core, 0, 0.5-core+dd, 0, 0);
		v5.addVertexWithUV(0.5-core, vl, 0.5-core+dd, 0, 0);
		v5.addVertexWithUV(0.5-core, vl, 0.5-core-dd, 0, 0);

		v5.addVertexWithUV(0.5+core-dd, 0, 0.5+core, 0, 0);
		v5.addVertexWithUV(0.5+core+dd, 0, 0.5+core, 0, 0);
		v5.addVertexWithUV(0.5+core+dd, vl, 0.5+core, 0, 0);
		v5.addVertexWithUV(0.5+core-dd, vl, 0.5+core, 0, 0);

		v5.addVertexWithUV(0.5+core, 0, 0.5-core-dd, 0, 0);
		v5.addVertexWithUV(0.5+core, 0, 0.5-core+dd, 0, 0);
		v5.addVertexWithUV(0.5+core, vl, 0.5-core+dd, 0, 0);
		v5.addVertexWithUV(0.5+core, vl, 0.5-core-dd, 0, 0);

		v5.addVertexWithUV(0.5-core-dd, 0, 0.5+core, 0, 0);
		v5.addVertexWithUV(0.5-core+dd, 0, 0.5+core, 0, 0);
		v5.addVertexWithUV(0.5-core+dd, vl, 0.5+core, 0, 0);
		v5.addVertexWithUV(0.5-core-dd, vl, 0.5+core, 0, 0);

		v5.addVertexWithUV(0.5+core, 0, 0.5-core-dd, 0, 0);
		v5.addVertexWithUV(0.5+core, 0, 0.5-core+dd, 0, 0);
		v5.addVertexWithUV(0.5+core, vl, 0.5-core+dd, 0, 0);
		v5.addVertexWithUV(0.5+core, vl, 0.5-core-dd, 0, 0);

		v5.addVertexWithUV(0.5+core, vl, 0.5+core, 0, 0);
		v5.addVertexWithUV(0.5+core, vl+dd, 0.5+core, 0, 0);
		v5.addVertexWithUV(0.5, tip+dd, 0.5, 0, 0);
		v5.addVertexWithUV(0.5, tip, 0.5, 0, 0);

		v5.addVertexWithUV(0.5+core-dd*zf, vl, 0.5+core+dd*zf, 0, 0);
		v5.addVertexWithUV(0.5+core+dd*zf, vl, 0.5+core-dd*zf, 0, 0);
		v5.addVertexWithUV(0.5+dd*zf, tip, 0.5-dd*zf, 0, 0);
		v5.addVertexWithUV(0.5-dd*zf, tip, 0.5+dd*zf, 0, 0);

		v5.addVertexWithUV(0.5-core, vl, 0.5-core, 0, 0);
		v5.addVertexWithUV(0.5-core, vl+dd, 0.5-core, 0, 0);
		v5.addVertexWithUV(0.5, tip+dd, 0.5, 0, 0);
		v5.addVertexWithUV(0.5, tip, 0.5, 0, 0);

		v5.addVertexWithUV(0.5-core-dd*zf, vl, 0.5-core+dd*zf, 0, 0);
		v5.addVertexWithUV(0.5-core+dd*zf, vl, 0.5-core-dd*zf, 0, 0);
		v5.addVertexWithUV(0.5+dd*zf, tip, 0.5-dd*zf, 0, 0);
		v5.addVertexWithUV(0.5-dd*zf, tip, 0.5+dd*zf, 0, 0);

		v5.addVertexWithUV(0.5-core, vl, 0.5+core, 0, 0);
		v5.addVertexWithUV(0.5-core, vl+dd, 0.5+core, 0, 0);
		v5.addVertexWithUV(0.5, tip+dd, 0.5, 0, 0);
		v5.addVertexWithUV(0.5, tip, 0.5, 0, 0);

		v5.addVertexWithUV(0.5+core+dd, vl+dd, 0.5-core, 0, 0);
		v5.addVertexWithUV(0.5+core-dd, vl+dd, 0.5-core, 0, 0);
		v5.addVertexWithUV(0.5-dd, tip+dd, 0.5, 0, 0);
		v5.addVertexWithUV(0.5+dd, tip+dd, 0.5, 0, 0);

		v5.addVertexWithUV(0.5-core, vl, 0.5+core, 0, 0);
		v5.addVertexWithUV(0.5-core, vl+dd, 0.5+core, 0, 0);
		v5.addVertexWithUV(0.5, tip+dd, 0.5, 0, 0);
		v5.addVertexWithUV(0.5, tip, 0.5, 0, 0);

		v5.addVertexWithUV(0.5-core+dd, vl+dd, 0.5+core, 0, 0);
		v5.addVertexWithUV(0.5-core-dd, vl+dd, 0.5+core, 0, 0);
		v5.addVertexWithUV(0.5-dd, tip+dd, 0.5, 0, 0);
		v5.addVertexWithUV(0.5+dd, tip+dd, 0.5, 0, 0);

		if (flip) {
			v5.invertY();
		}
		v5.render();
	}

	private void renderSpike(double u, double v, double xu, double xv, int w, boolean above, boolean flip) {
		double core = 0.15;
		double vl = above ? 1 : 0.8;

		TessellatorVertexList v5 = new TessellatorVertexList();

		if (!above) {
			// Top point
			v5.addVertexWithUV(0.5-core, vl, 0.5-core, u, v);
			v5.addVertexWithUV(0.5-core, vl, 0.5+core, xu, v);
			v5.addVertexWithUV(0.5, 1, 0.5, xu, xv);
			v5.addVertexWithUV(0.5, 1, 0.5, xu, xv);

			v5.addVertexWithUV(0.5-core, vl, 0.5+core, u, v);
			v5.addVertexWithUV(0.5+core, vl, 0.5+core, xu, v);
			v5.addVertexWithUV(0.5, 1, 0.5, xu, xv);
			v5.addVertexWithUV(0.5, 1, 0.5, xu, xv);

			v5.addVertexWithUV(0.5, 1, 0.5, u, xv);
			v5.addVertexWithUV(0.5, 1, 0.5, xu, xv);
			v5.addVertexWithUV(0.5+core, vl, 0.5+core, xu, v);
			v5.addVertexWithUV(0.5+core, vl, 0.5-core, u, v);

			v5.addVertexWithUV(0.5, 1, 0.5, u, xv);
			v5.addVertexWithUV(0.5, 1, 0.5, xu, xv);
			v5.addVertexWithUV(0.5+core, vl, 0.5-core, xu, v);
			v5.addVertexWithUV(0.5-core, vl, 0.5-core, u, v);
		}

		xv -= (xv-v)/w;
		v += (xv-v)/(w*1.2);
		u += (xu-u)/(w*2);
		xu -= (xu-u)/(w*2);

		//columns
		v5.addVertexWithUV(0.5-core, vl, 0.5-core, u, xv);
		v5.addVertexWithUV(0.5+core, vl, 0.5-core, xu, xv);
		v5.addVertexWithUV(0.5+core, 0, 0.5-core, xu, v);
		v5.addVertexWithUV(0.5-core, 0, 0.5-core, u, v);

		v5.addVertexWithUV(0.5-core, 0, 0.5+core, u, v);
		v5.addVertexWithUV(0.5+core, 0, 0.5+core, xu, v);
		v5.addVertexWithUV(0.5+core, vl, 0.5+core, xu, xv);
		v5.addVertexWithUV(0.5-core, vl, 0.5+core, u, xv);

		v5.addVertexWithUV(0.5+core, vl, 0.5-core, u, xv);
		v5.addVertexWithUV(0.5+core, vl, 0.5+core, xu, xv);
		v5.addVertexWithUV(0.5+core, 0, 0.5+core, xu, v);
		v5.addVertexWithUV(0.5+core, 0, 0.5-core, u, v);

		v5.addVertexWithUV(0.5-core, 0, 0.5-core, u, v);
		v5.addVertexWithUV(0.5-core, 0, 0.5+core, xu, v);
		v5.addVertexWithUV(0.5-core, vl, 0.5+core, xu, xv);
		v5.addVertexWithUV(0.5-core, vl, 0.5-core, u, xv);

		//bottom
		v5.addVertexWithUV(0.5+core, 0, 0.5-core, u, xv);
		v5.addVertexWithUV(0.5+core, 0, 0.5+core, xu, xv);
		v5.addVertexWithUV(0.5-core, 0, 0.5+core, xu, v);
		v5.addVertexWithUV(0.5-core, 0, 0.5-core, u, v);

		if (flip) {
			v5.invertY();
		}
		v5.render();
	}

	private void renderXAngledSpike(double u, double v, double xu, double xv, double out, int w, boolean below, boolean flip) {
		double core = 0.12;
		double vl = 0.55;
		double dvl = vl/6D;
		double dy = below ? 0.15 : -0.05;
		double tout = out;
		double htip = 0.1;
		int dir = out > 0 ? 1 : -1;

		TessellatorVertexList v5 = new TessellatorVertexList();

		v5.addVertexWithUV(0.5+core*dir+out, dy+vl+dvl, 0.5+core, u, v);
		v5.addVertexWithUV(0.5+core*dir+out, dy+vl+dvl, 0.5-core, xu, v);
		v5.addVertexWithUV(0.5+core*dir+out+tout, dy+vl+dvl+htip, 0.5, xu, xv); //tip
		v5.addVertexWithUV(0.5+core*dir+out+tout, dy+vl+dvl+htip, 0.5, xu, xv);

		v5.addVertexWithUV(0.5+core*dir+out+tout, dy+vl+dvl+htip, 0.5, u, xv);
		v5.addVertexWithUV(0.5+core*dir+out+tout, dy+vl+dvl+htip, 0.5, xu, xv); //tip
		v5.addVertexWithUV(0.5+core*dir*3+out, dy+vl, 0.5-core, xu, v);
		v5.addVertexWithUV(0.5+core*dir*3+out, dy+vl, 0.5+core, u, v);

		v5.addVertexWithUV(0.5+core*dir+out+tout, dy+vl+dvl+htip, 0.5, u, xv);
		v5.addVertexWithUV(0.5+core*dir+out+tout, dy+vl+dvl+htip, 0.5, xu, xv); //tip
		v5.addVertexWithUV(0.5+core*dir*3+out, dy+vl, 0.5+core, xu, v);
		v5.addVertexWithUV(0.5+core*dir+out, dy+vl+dvl, 0.5+core, u, v);

		v5.addVertexWithUV(0.5+core*dir+out, dy+vl+dvl, 0.5-core, u, v);
		v5.addVertexWithUV(0.5+core*dir*3+out, dy+vl, 0.5-core, xu, v);
		v5.addVertexWithUV(0.5+core*dir+out+tout, dy+vl+dvl+htip, 0.5, xu, xv); //tip
		v5.addVertexWithUV(0.5+core*dir+out+tout, dy+vl+dvl+htip, 0.5, xu, xv);

		xv -= (xv-v)/w;
		v += (xv-v)/(w*1.2);
		u += (xu-u)/(w*2);
		xu -= (xu-u)/(w*2);

		double ds = 3;
		double ds2 = ds;
		double ddy = 0;
		double dvl2 = dvl;
		double dvl3 = dvl;
		if (below) {
			ds2 = 1;
			ddy = Math.abs(core*dir);
			dvl2 *= 4;
			//dvl3 *= 1.5;
		}

		//panels
		v5.addVertexWithUV(0.5+core*dir, dy+dvl2, 0.5-core, u, v);
		v5.addVertexWithUV(0.5+core*dir*ds2, dy-ddy, 0.5-core, xu, v);
		v5.addVertexWithUV(0.5+core*dir*ds+out, dy+vl, 0.5-core, xu, xv);
		v5.addVertexWithUV(0.5+core*dir+out, dy+vl+dvl3, 0.5-core, u, xv);

		v5.addVertexWithUV(0.5+core*dir+out, dy+vl+dvl3, 0.5+core, u, xv);
		v5.addVertexWithUV(0.5+core*dir*ds+out, dy+vl, 0.5+core, xu, xv);
		v5.addVertexWithUV(0.5+core*dir*ds2, dy-ddy, 0.5+core, xu, v);
		v5.addVertexWithUV(0.5+core*dir, dy+dvl2, 0.5+core, u, v);

		v5.addVertexWithUV(0.5+core*dir+out, dy+vl+dvl3, 0.5-core, u, xv);
		v5.addVertexWithUV(0.5+core*dir+out, dy+vl+dvl3, 0.5+core, xu, xv);
		v5.addVertexWithUV(0.5+core*dir, dy+dvl2, 0.5+core, xu, v);
		v5.addVertexWithUV(0.5+core*dir, dy+dvl2, 0.5-core, u, v);

		v5.addVertexWithUV(0.5+core*dir*ds2, dy-ddy, 0.5-core, u, v);
		v5.addVertexWithUV(0.5+core*dir*ds2, dy-ddy, 0.5+core, xu, v);
		v5.addVertexWithUV(0.5+core*dir*ds+out, dy+vl, 0.5+core, xu, xv);
		v5.addVertexWithUV(0.5+core*dir*ds+out, dy+vl, 0.5-core, u, xv);

		if (!below) {
			//base
			v5.addVertexWithUV(0.5-core*dir*ds+out*2.56, dy+dvl, 0.5-core, u, xv);
			v5.addVertexWithUV(0.5-core*dir*ds+out*2.56, dy+dvl, 0.5+core, xu, xv);
			v5.addVertexWithUV(0.5+core*dir*ds, dy, 0.5+core, xu, v);
			v5.addVertexWithUV(0.5+core*dir*ds, dy, 0.5-core, u, v);
		}

		if (flip) {
			v5.invertY();
		}
		if (out > 0)
			v5.reverse();
		v5.render();
	}

	private void renderZAngledSpike(double u, double v, double xu, double xv, double out, int w, boolean below, boolean flip) {
		double core = 0.12;
		double vl = 0.55;
		double dvl = vl/6D;
		double dy = below ? 0.1 : -0.1;
		double tout = out;//0.1875;
		double htip = 0.1;
		int dir = out > 0 ? 1 : -1;

		TessellatorVertexList v5 = new TessellatorVertexList();

		v5.addVertexWithUV(0.5+core, dy+vl+dvl, 0.5+core*dir+out, u, v);
		v5.addVertexWithUV(0.5-core, dy+vl+dvl, 0.5+core*dir+out, xu, v);
		v5.addVertexWithUV(0.5, dy+vl+dvl+htip, 0.5+core*dir+out+tout, xu, xv); //tip
		v5.addVertexWithUV(0.5, dy+vl+dvl+htip, 0.5+core*dir+out+tout, u, xv);

		v5.addVertexWithUV(0.5, dy+vl+dvl+htip, 0.5+core*dir+out+tout, xu, xv);
		v5.addVertexWithUV(0.5, dy+vl+dvl+htip, 0.5+core*dir+out+tout, xu, xv); //tip
		v5.addVertexWithUV(0.5-core, dy+vl, 0.5+core*dir*3+out, xu, v);
		v5.addVertexWithUV(0.5+core, dy+vl, 0.5+core*dir*3+out, u, v);

		v5.addVertexWithUV(0.5, dy+vl+dvl+htip, 0.5+core*dir+out+tout, xu, xv);
		v5.addVertexWithUV(0.5, dy+vl+dvl+htip, 0.5+core*dir+out+tout, xu, xv); //tip
		v5.addVertexWithUV(0.5+core, dy+vl, 0.5+core*dir*3+out, xu, v);
		v5.addVertexWithUV(0.5+core, dy+vl+dvl, 0.5+core*dir+out, u, v);

		v5.addVertexWithUV(0.5-core, dy+vl+dvl, 0.5+core*dir+out, u, v);
		v5.addVertexWithUV(0.5-core, dy+vl, 0.5+core*dir*3+out, xu, v);
		v5.addVertexWithUV(0.5, dy+vl+dvl+htip, 0.5+core*dir+out+tout, xu, xv); //tip
		v5.addVertexWithUV(0.5, dy+vl+dvl+htip, 0.5+core*dir+out+tout, u, xv);

		xv -= (xv-v)/w;
		v += (xv-v)/(w*1.2);
		u += (xu-u)/(w*2);
		xu -= (xu-u)/(w*2);

		double ds = 3;
		double ds2 = ds;
		double ddy = 0;
		double dvl2 = dvl;
		double dvl3 = dvl;
		if (below) {
			ds2 = 1;
			ddy = Math.abs(core*dir);
			dvl2 *= 4;
			//dvl3 *= 1.5;
		}

		//panels
		v5.addVertexWithUV(0.5-core, dy+dvl2, 0.5+core*dir, u, v);
		v5.addVertexWithUV(0.5-core, dy-ddy, 0.5+core*dir*ds2, xu, v);
		v5.addVertexWithUV(0.5-core, dy+vl, 0.5+core*dir*ds+out, xu, xv);
		v5.addVertexWithUV(0.5-core, dy+vl+dvl3, 0.5+core*dir+out, u, xv);

		v5.addVertexWithUV(0.5+core, dy+vl+dvl3, 0.5+core*dir+out, u, xv);
		v5.addVertexWithUV(0.5+core, dy+vl, 0.5+core*dir*ds+out, xu, xv);
		v5.addVertexWithUV(0.5+core, dy-ddy, 0.5+core*dir*ds2, xu, v);
		v5.addVertexWithUV(0.5+core, dy+dvl2, 0.5+core*dir, u, v);

		v5.addVertexWithUV(0.5-core, dy+vl+dvl3, 0.5+core*dir+out, u, xv);
		v5.addVertexWithUV(0.5+core, dy+vl+dvl3, 0.5+core*dir+out, xu, xv);
		v5.addVertexWithUV(0.5+core, dy+dvl2, 0.5+core*dir, xu, v);
		v5.addVertexWithUV(0.5-core, dy+dvl2, 0.5+core*dir, u, v);

		v5.addVertexWithUV(0.5-core, dy-ddy, 0.5+core*dir*ds2, u, v);
		v5.addVertexWithUV(0.5+core, dy-ddy, 0.5+core*dir*ds2, xu, v);
		v5.addVertexWithUV(0.5+core, dy+vl, 0.5+core*dir*ds+out, xu, xv);
		v5.addVertexWithUV(0.5-core, dy+vl, 0.5+core*dir*ds+out, u, xv);

		if (!below) {
			//base
			v5.addVertexWithUV(0.5-core, dy+dvl+0.0025, 0.5-core*dir*ds+out*2.56, u, xv);
			v5.addVertexWithUV(0.5+core, dy+dvl+0.0025, 0.5-core*dir*ds+out*2.56, xu, xv);
			v5.addVertexWithUV(0.5+core, dy, 0.5+core*dir*ds, xu, v);
			v5.addVertexWithUV(0.5-core, dy, 0.5+core*dir*ds, u, v);
		}

		if (flip) {
			v5.invertY();
		}
		if (out < 0)
			v5.reverse();
		v5.render();
	}

	@Override
	public boolean shouldRender3DInInventory(int model) {
		return true;
	}

}
