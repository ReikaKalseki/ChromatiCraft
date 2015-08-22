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
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Interfaces.CrystalRenderedBlock;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Rendering.TessellatorVertexList;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class CrystalRenderer implements ISimpleBlockRenderingHandler {

	public static int renderPass;
	public static boolean renderAllArmsInInventory = false;
	public static int staticColor = -1;

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
		this.renderSpike(v5, u, v, xu, xv, w);
		v5.draw();

		v5.startDrawingQuads();
		//v5.setBrightness(240);
		v5.setNormal(0, 0.5F, 0);
		v5.setColorRGBA_I(color, alpha);
		this.renderXAngledSpike(u, v, xu, xv, 0.1875, w);
		if (renderAllArmsInInventory)
			this.renderXAngledSpike(u, v, xu, xv, -0.1875, w);
		v5.draw();

		v5.startDrawingQuads();
		//v5.setBrightness(240);
		v5.setNormal(0, 0.5F, 0);
		v5.setColorRGBA_I(color, alpha);
		this.renderZAngledSpike(u, v, xu, xv, 0.1875, w);
		if (renderAllArmsInInventory)
			this.renderZAngledSpike(u, v, xu, xv, -0.1875, w);
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
			this.renderBase(v5, Minecraft.getMinecraft().theWorld, 0, 0, 0, (CrystalRenderedBlock)b);
			v5.draw();
		}
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block b, int modelId, RenderBlocks rb) {
		int meta = world.getBlockMetadata(x, y, z);
		//boolean discover = ProgressionManager.instance.hasPlayerDiscoveredColor(Minecraft.getMinecraft().thePlayer);
		int color = /*discover ? */((CrystalRenderedBlock)b).getTintColor(meta);// : 0xbbbbbb;
		int alpha = /*discover ? */220;// : 32;

		IIcon ico = b.getIcon(0, meta);
		//ico = Blocks.blockNetherQuartz.getIcon(0, 0);
		double u = ico.getMinU();
		double v = ico.getMinV();
		double xu = ico.getMaxU();
		double xv = ico.getMaxV();

		//xu = u = xv = v = 0;

		Tessellator v5 = Tessellator.instance;
		//GL11.glEnable(GL11.GL_DEPTH_TEST);
		//v5.draw();
		//GL11.glDisable(GL11.GL_CULL_FACE);
		//GL11.glEnable(GL11.GL_BLEND);
		//v5.startDrawingQuads();

		double maxx = b.getBlockBoundsMaxX();
		double minx = b.getBlockBoundsMinX();
		double miny = b.getBlockBoundsMinY();
		double maxy = b.getBlockBoundsMaxY();
		double maxz = b.getBlockBoundsMaxZ();
		double minz = b.getBlockBoundsMinZ();

		int l = b.getMixedBrightnessForBlock(world, x, y, z);

		//v5.setBrightness(rb.renderMaxY < 1.0D ? l : b.getMixedBrightnessForBlock(world, x, y+1, z));
		v5.setBrightness(240);
		v5.addTranslation(x, y, z);
		v5.setColorRGBA_I(color, alpha);

		int w = ico.getIconWidth();

		if (renderPass == 1) {
			this.renderSpike(v5, u, v, xu, xv, w);
			int val = Math.abs(x)%9+Math.abs(z)%9; //16 combos -> binary selector
			if (val > 15 || ((CrystalRenderedBlock)b).renderAllArms())
				val = 15;
			if ((val & 8) == 8)
				this.renderXAngledSpike(u, v, xu, xv, 0.1875, w); //8,9,10,11,12,13,14,15
			if ((val & 4) == 4)
				this.renderXAngledSpike(u, v, xu, xv, -0.1875, w); //4,5,6,7,12,13,14,15
			if ((val & 2) == 2)
				this.renderZAngledSpike(u, v, xu, xv, 0.1875, w); //2,3,6,7,10,11,14,15
			if ((val & 1) == 1)
				this.renderZAngledSpike(u, v, xu, xv, -0.1875, w); //1,3,5,7,9,11,13,15
		}

		//v5.setColorOpaque(0, 0, 0);
		//this.renderOutline(v5);
		if (renderPass == 0) {
			if (((CrystalRenderedBlock)b).renderBase()) {
				this.renderBase(v5, world, x, y, z, (CrystalRenderedBlock)b);
			}
		}

		v5.addTranslation(-x, -y, -z);

		//v5.draw();
		//GL11.glEnable(GL11.GL_CULL_FACE);
		//GL11.glDisable(GL11.GL_BLEND);
		//v5.startDrawingQuads();
		//v5.addVertex(0, 0, 0);
		//v5.addVertex(0, 0, 0);
		//v5.addVertex(0, 0, 0);
		//v5.addVertex(0, 0, 0);

		return true;
	}

	private void renderBase(Tessellator v5, IBlockAccess world, int x, int y, int z, CrystalRenderedBlock b) {
		BlockKey bk = b.getBaseBlock(world, x, y, z, ForgeDirection.UP);
		IIcon ico = bk.blockID.getIcon(0, bk.metadata);
		int w = ico.getIconWidth();

		v5.setColorOpaque(255, 255, 255);

		double u = ico.getMinU();
		double v = ico.getMinV();
		double xu = ico.getMaxU();
		double xv = ico.getMaxV();

		double top = 0.125;

		v5.addVertexWithUV(0, top, 1, u, xv);
		v5.addVertexWithUV(1, top, 1, xu, xv);
		v5.addVertexWithUV(1, top, 0, xu, v);
		v5.addVertexWithUV(0, top, 0, u, v);

		v5.setColorOpaque(110, 110, 110);
		v5.addVertexWithUV(0, 0, 0, u, v);
		v5.addVertexWithUV(1, 0, 0, xu, v);
		v5.addVertexWithUV(1, 0, 1, xu, xv);
		v5.addVertexWithUV(0, 0, 1, u, xv);

		bk = b.getBaseBlock(world, x, y, z, ForgeDirection.UP);
		ico = bk.blockID.getIcon(0, bk.metadata);
		u = ico.getMinU();
		v = ico.getMinV();
		xu = ico.getMaxU();
		xv = ico.getMaxV();

		double vv = v+(xv-v)/(w)*2;

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

	private void renderOutline(Tessellator v5) {
		double core = 0.15;
		double vl = 0.8;
		double dd = 0.01;
		double tip = 1;
		double zf = 0.4;

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
	}

	private void renderSpike(Tessellator v5, double u, double v, double xu, double xv, int w) {
		double core = 0.15;
		double vl = 0.8;

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
	}

	private void renderXAngledSpike(double u, double v, double xu, double xv, double out, int w) {
		double core = 0.12;
		double vl = 0.55;
		double dvl = vl/6D;
		double dy = -0.05;
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

		//panels
		v5.addVertexWithUV(0.5+core*dir, dy+dvl, 0.5-core, u, v);
		v5.addVertexWithUV(0.5+core*dir*3, dy+0, 0.5-core, xu, v);
		v5.addVertexWithUV(0.5+core*dir*3+out, dy+vl, 0.5-core, xu, xv);
		v5.addVertexWithUV(0.5+core*dir+out, dy+vl+dvl, 0.5-core, u, xv);

		v5.addVertexWithUV(0.5+core*dir+out, dy+vl+dvl, 0.5+core, u, xv);
		v5.addVertexWithUV(0.5+core*dir*3+out, dy+vl, 0.5+core, xu, xv);
		v5.addVertexWithUV(0.5+core*dir*3, dy+0, 0.5+core, xu, v);
		v5.addVertexWithUV(0.5+core*dir, dy+dvl, 0.5+core, u, v);

		v5.addVertexWithUV(0.5+core*dir+out, dy+vl+dvl, 0.5-core, u, xv);
		v5.addVertexWithUV(0.5+core*dir+out, dy+vl+dvl, 0.5+core, xu, xv);
		v5.addVertexWithUV(0.5+core*dir, dy+dvl, 0.5+core, xu, v);
		v5.addVertexWithUV(0.5+core*dir, dy+dvl, 0.5-core, u, v);

		v5.addVertexWithUV(0.5+core*dir*3, dy, 0.5-core, u, v);
		v5.addVertexWithUV(0.5+core*dir*3, dy, 0.5+core, xu, v);
		v5.addVertexWithUV(0.5+core*dir*3+out, dy+vl, 0.5+core, xu, xv);
		v5.addVertexWithUV(0.5+core*dir*3+out, dy+vl, 0.5-core, u, xv);

		//base
		v5.addVertexWithUV(0.5-core*dir*3+out*2.56, dy+dvl, 0.5-core, u, xv);
		v5.addVertexWithUV(0.5-core*dir*3+out*2.56, dy+dvl, 0.5+core, xu, xv);
		v5.addVertexWithUV(0.5+core*dir*3, dy, 0.5+core, xu, v);
		v5.addVertexWithUV(0.5+core*dir*3, dy, 0.5-core, u, v);

		if (out > 0)
			v5.reverse();
		v5.render();
	}

	private void renderZAngledSpike(double u, double v, double xu, double xv, double out, int w) {
		double core = 0.12;
		double vl = 0.55;
		double dvl = vl/6D;
		double dy = -0.1;
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

		//panels
		v5.addVertexWithUV(0.5-core, dy+dvl, 0.5+core*dir, u, v);
		v5.addVertexWithUV(0.5-core, dy+0, 0.5+core*dir*3, xu, v);
		v5.addVertexWithUV(0.5-core, dy+vl, 0.5+core*dir*3+out, xu, xv);
		v5.addVertexWithUV(0.5-core, dy+vl+dvl, 0.5+core*dir+out, u, xv);

		v5.addVertexWithUV(0.5+core, dy+vl+dvl, 0.5+core*dir+out, u, xv);
		v5.addVertexWithUV(0.5+core, dy+vl, 0.5+core*dir*3+out, xu, xv);
		v5.addVertexWithUV(0.5+core, dy+0, 0.5+core*dir*3, xu, v);
		v5.addVertexWithUV(0.5+core, dy+dvl, 0.5+core*dir, u, v);

		v5.addVertexWithUV(0.5-core, dy+vl+dvl, 0.5+core*dir+out, u, xv);
		v5.addVertexWithUV(0.5+core, dy+vl+dvl, 0.5+core*dir+out, xu, xv);
		v5.addVertexWithUV(0.5+core, dy+dvl, 0.5+core*dir, xu, v);
		v5.addVertexWithUV(0.5-core, dy+dvl, 0.5+core*dir, u, v);

		v5.addVertexWithUV(0.5-core, dy, 0.5+core*dir*3, u, v);
		v5.addVertexWithUV(0.5+core, dy, 0.5+core*dir*3, xu, v);
		v5.addVertexWithUV(0.5+core, dy+vl, 0.5+core*dir*3+out, xu, xv);
		v5.addVertexWithUV(0.5-core, dy+vl, 0.5+core*dir*3+out, u, xv);

		//base
		v5.addVertexWithUV(0.5-core, dy+dvl+0.0025, 0.5-core*dir*3+out*2.56, u, xv);
		v5.addVertexWithUV(0.5+core, dy+dvl+0.0025, 0.5-core*dir*3+out*2.56, xu, xv);
		v5.addVertexWithUV(0.5+core, dy, 0.5+core*dir*3, xu, v);
		v5.addVertexWithUV(0.5-core, dy, 0.5+core*dir*3, u, v);

		if (out < 0)
			v5.reverse();
		v5.render();
	}

	@Override
	public boolean shouldRender3DInInventory(int model) {
		return true;
	}

	@Override
	public int getRenderId() {
		return ChromatiCraft.proxy.crystalRender;
	}

}
