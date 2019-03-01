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

import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Block.Decoration.BlockMetaAlloyLamp;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Rendering.TessellatorVertexList;
import Reika.DragonAPI.Interfaces.ISBRH;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;


public class MetaAlloyRenderer implements ISBRH {

	private final Random rand = new Random();

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		Tessellator.instance.startDrawingQuads();
		GL11.glColor4f(1, 1, 1, 1);
		double s = 1;
		GL11.glScaled(s, s, s);
		GL11.glTranslated(-0.5, -0.5, -0.5);
		ReikaRenderHelper.disableEntityLighting();
		GL11.glDisable(GL11.GL_LIGHTING);
		Tessellator.instance.setBrightness(240);
		Tessellator.instance.setColorOpaque_I(0xffffff);
		this.renderGlowingPod(Minecraft.getMinecraft().theWorld, 0, 0, 0, block, 0, 0, 0);
		Tessellator.instance.draw();
		GL11.glPopAttrib();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks rb) {
		rand.setSeed(this.calcSeed(x, y, z));
		Tessellator.instance.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
		this.renderLowPetals(world, x, y, z, Tessellator.instance);

		double ang1 = -5+rand.nextDouble()*10;
		double ang2 = 0;//rand.nextDouble()*360;
		double ang3 = -5+rand.nextDouble()*10;

		Tessellator.instance.setColorOpaque_I(0xffffff);
		this.renderRaisedPetals(world, x, y, z, ang1, ang2, ang3);
		if (((BlockMetaAlloyLamp)block).hasPod(world, x, y, z))
			this.renderGlowingPod(world, x, y, z, block, ang1, ang2, ang3);
		return true;
	}

	private void renderLowPetals(IBlockAccess world, int x, int y, int z, Tessellator v5) {
		//float f = 0.5F+rand.nextFloat()*0.5F;
		//int c = ReikaColorAPI.mixColors(Blocks.grass.colorMultiplier(world, x, y, z), 0x22aaff, f);
		//ReikaJavaLibrary.pConsole(this.calcSeed(x, y, z)+" @ "+x+","+z+" > "+f+" > "+Integer.toHexString(c));
		//Tessellator.instance.setColorOpaque_I(c);
		IIcon ico = BlockMetaAlloyLamp.leaf1;
		//double u = ico.getMinU();
		//double v = ico.getMinV();
		//double du = ico.getMaxU();
		//double dv = ico.getMaxV();
		//double h = 0.0625;

		//tv5.addVertexWithUV(x, y+h, z+1, u, dv);
		//tv5.addVertexWithUV(x+1, y+h, z+1, du, dv);
		//tv5.addVertexWithUV(x+1, y+h, z, du, v);
		//tv5.addVertexWithUV(x, y+h, z, u, v);

		//do not draw textures; extrude the icon sideways
		TessellatorVertexList tv5 = new TessellatorVertexList(0, 0, 0);
		ReikaRenderHelper.renderIconIn3D(tv5, ico, x, y, z);
		tv5.rotateYtoX();
		tv5.rotateYtoZ();
		tv5.scale(0.5, 0.5, 0.5);
		tv5.center();
		tv5.offset(x+0.75, y+0.03125-0.01, z+0.5);
		tv5.render();

		tv5 = new TessellatorVertexList(0, 0, 0);
		ReikaRenderHelper.renderIconIn3D(tv5, ico, x, y, z);
		tv5.rotateYtoX();
		tv5.rotateYtoZ();
		tv5.scale(0.5, 0.5, 0.5);
		tv5.center();
		tv5.rotateXtoZ();
		tv5.offset(x+0.5, y+0.03125, z+0.75);
		tv5.render();

		tv5 = new TessellatorVertexList(0, 0, 0);
		ReikaRenderHelper.renderIconIn3D(tv5, ico, x, y, z);
		tv5.rotateYtoZ();
		tv5.scale(0.5, 0.5, 0.5);
		tv5.center();
		tv5.rotateNonOrthogonal(0, 90, 0);
		tv5.offset(x+0.25, y+0.03125-0.01, z+0.5);
		tv5.render();

		tv5 = new TessellatorVertexList(0, 0, 0);
		ReikaRenderHelper.renderIconIn3D(tv5, ico, x, y, z);
		tv5.rotateYtoZ();
		tv5.scale(0.5, 0.5, 0.5);
		tv5.center();
		tv5.rotateNonOrthogonal(0, 180, 0);
		tv5.offset(x+0.5, y+0.03125, z+0.25);
		tv5.render();
	}

	private void renderRaisedPetals(IBlockAccess world, int x, int y, int z, double ang1, double ang2, double ang3) {
		IIcon ico = BlockMetaAlloyLamp.leaf2;
		for (int i = 0; i < 360; i += 90) {
			TessellatorVertexList tv5 = new TessellatorVertexList(0, 0, 0);
			ReikaRenderHelper.renderIconIn3D(tv5, ico, x, y, z);
			tv5.scale(0.75, 0.75, 0.75);
			tv5.offset(-0.375, -0.0625, -0.325);
			tv5.rotateNonOrthogonal(0, 90, 0);
			tv5.rotateNonOrthogonal(0, 0, 30);
			tv5.rotateNonOrthogonal(0, i, 0);
			tv5.rotateNonOrthogonal(ang1, ang2, ang3);
			tv5.offset(x+0.5, y+0.25, z+0.5);
			tv5.render();
		}
	}

	private void renderGlowingPod(IBlockAccess world, int x, int y, int z, Block b, double ang1, double ang2, double ang3) {
		Tessellator.instance.setColorOpaque_I(0xffffff);

		IIcon ico = BlockMetaAlloyLamp.podSide;
		double u = ico.getMinU();
		double v = ico.getMinV();
		double du = ico.getMaxU();
		double dv = ico.getMaxV();

		TessellatorVertexList tv5 = new TessellatorVertexList(0, 0, 0);
		double[] r = {0.125, 	0.5, 	0.625, 	0.5, 	0.25, 	0.125, 	0.0625};
		double[] h = {0.0625,	0.125, 	0.375, 	0.625,	0.825,	0.9375,	1};
		double[] c = {0.5, 		0.625, 	0.75, 	0.875,	0.875,	0.9735,	1};

		for (int i = 0; i < r.length-1; i++) {
			double r1 = r[i]/2;
			double r2 = r[i+1]/2;
			double h1 = h[i];
			double h2 = h[i+1];
			int c10 = ReikaColorAPI.GStoHex((int)(255*c[i]));
			int c20 = ReikaColorAPI.GStoHex((int)(255*c[i+1]));

			int c1 = c10;
			int c2 = c20;

			tv5.addVertexWithUVColor(0.5+r1, h1, 0.5-r1, u, dv, c1);
			tv5.addVertexWithUVColor(0.5+r2, h2, 0.5-r2, du, dv, c2);
			tv5.addVertexWithUVColor(0.5+r2, h2, 0.5+r2, du, v, c2);
			tv5.addVertexWithUVColor(0.5+r1, h1, 0.5+r1, u, v, c1);

			tv5.addVertexWithUVColor(0.5-r1, h1, 0.5+r1, u, v, c1);
			tv5.addVertexWithUVColor(0.5-r2, h2, 0.5+r2, du, v, c2);
			tv5.addVertexWithUVColor(0.5-r2, h2, 0.5-r2, du, dv, c2);
			tv5.addVertexWithUVColor(0.5-r1, h1, 0.5-r1, u, dv, c1);

			c1 = ReikaColorAPI.getColorWithBrightnessMultiplier(c10, 0.875F);
			c2 = ReikaColorAPI.getColorWithBrightnessMultiplier(c20, 0.875F);

			tv5.addVertexWithUVColor(0.5+r1, h1, 0.5+r1, u, v, c1);
			tv5.addVertexWithUVColor(0.5+r2, h2, 0.5+r2, du, v, c2);
			tv5.addVertexWithUVColor(0.5-r2, h2, 0.5+r2, du, dv, c2);
			tv5.addVertexWithUVColor(0.5-r1, h1, 0.5+r1, u, dv, c1);

			tv5.addVertexWithUVColor(0.5-r1, h1, 0.5-r1, u, dv, c1);
			tv5.addVertexWithUVColor(0.5-r2, h2, 0.5-r2, du, dv, c2);
			tv5.addVertexWithUVColor(0.5+r2, h2, 0.5-r2, du, v, c2);
			tv5.addVertexWithUVColor(0.5+r1, h1, 0.5-r1, u, v, c1);
		}

		ico = BlockMetaAlloyLamp.podEnd;
		u = ico.getMinU();
		v = ico.getMinV();
		du = ico.getMaxU();
		dv = ico.getMaxV();

		tv5.addVertexWithUVColor(0.5-r[r.length-1]/2, h[h.length-1], 0.5-r[r.length-1]/2, u, dv, 0xffffff);
		tv5.addVertexWithUVColor(0.5-r[r.length-1]/2, h[h.length-1], 0.5+r[r.length-1]/2, du, dv, 0xffffff);
		tv5.addVertexWithUVColor(0.5+r[r.length-1]/2, h[h.length-1], 0.5+r[r.length-1]/2, du, v, 0xffffff);
		tv5.addVertexWithUVColor(0.5+r[r.length-1]/2, h[h.length-1], 0.5-r[r.length-1]/2, u, v, 0xffffff);

		tv5.addVertexWithUVColor(0.5+r[0]/2, h[0], 0.5-r[0]/2, u, v, 0x7a7a7a);
		tv5.addVertexWithUVColor(0.5+r[0]/2, h[0], 0.5+r[0]/2, du, v, 0x7a7a7a);
		tv5.addVertexWithUVColor(0.5-r[0]/2, h[0], 0.5+r[0]/2, du, dv, 0x7a7a7a);
		tv5.addVertexWithUVColor(0.5-r[0]/2, h[0], 0.5-r[0]/2, u, dv, 0x7a7a7a);

		tv5.rotateNonOrthogonal(ang1, ang2, ang3);
		//tv5.center();
		tv5.offset(x, y, z);
		tv5.render();
	}

	private long calcSeed(int x, int y, int z) {
		return Minecraft.getMinecraft().theWorld.getSeed() ^ 60*new Coordinate(x, y, z).hashCode();
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return ChromatiCraft.proxy.metaAlloyRender;
	}

}
