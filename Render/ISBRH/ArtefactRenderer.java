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
import java.util.Random;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.Instantiable.Rendering.TessellatorVertexList;
import Reika.DragonAPI.Instantiable.Rendering.TessellatorVertexList.TessellatorVertex;
import Reika.DragonAPI.Interfaces.ISBRH;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.IBlockAccess;


public class ArtefactRenderer implements ISBRH {

	private final Random rand = new Random();

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {

	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks rb) {
		rand.setSeed(this.calcSeed(x, y, z));
		rand.nextBoolean();
		Tessellator.instance.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
		if (world.getBlockMetadata(x, y, z) == 0) {
			this.renderDirtChunks(world, x, y, z, rb);
		}
		else {
			this.renderPedestal(world, x, y, z, rb);
		}
		this.renderArtefactSimple(world, x, y, z, block);
		return true;
	}

	private void renderPedestal(IBlockAccess world, int x, int y, int z, RenderBlocks rb) {
		ReikaRenderHelper.renderBlockSubCube(x, y, z, 1, 1, 1, 14, 2, 14, Tessellator.instance, rb, Blocks.stonebrick, 0);
		ReikaRenderHelper.renderBlockSubCube(x, y, z, 2, 12, 2, 12, 2, 12, Tessellator.instance, rb, Blocks.stonebrick, 0);
		ReikaRenderHelper.renderBlockSubCube(x, y, z, 4, 2, 4, 8, 11, 8, Tessellator.instance, rb, Blocks.stonebrick, 0);
		ReikaRenderHelper.renderBlockSubCube(x, y, z, 3, 2, 3, 10, 5, 10, Tessellator.instance, rb, Blocks.stonebrick, 0);
	}

	private void renderArtefactSimple(IBlockAccess world, int x, int y, int z, Block b) {
		Tessellator.instance.setColorOpaque_I(0xffffff);

		boolean natural = world.getBlockMetadata(x, y, z) == 0;

		double ang1 = natural ? 90-60+rand.nextDouble()*120 : 85+rand.nextDouble()*10;
		double ang2 = natural ? -45+rand.nextDouble()*90 : -5+rand.nextDouble()*10;
		double ang3 = natural ? rand.nextDouble()*360 : rand.nextDouble()*360;

		TessellatorVertexList tv5 = new TessellatorVertexList(0, 0, 0);
		ReikaRenderHelper.renderIconIn3D(tv5, b.getIcon(0, 0), x, y, z);
		tv5.rotateNonOrthogonal(ang1, ang2, ang3);
		tv5.center();
		tv5.offset(x+0.5, y+0.5, z+0.5);
		if (!natural)
			tv5.offset(0, 0.4, 0);
		tv5.render();
	}

	private void renderArtefact(IBlockAccess world, int x, int y, int z, Block b) {
		Tessellator.instance.setColorOpaque_I(0xffffff);
		boolean natural = world.getBlockMetadata(x, y, z) == 0;

		double ang1 = natural ? rand.nextDouble()*360 : 0;
		double ang2 = natural ? rand.nextDouble()*360 : 0;
		double ang3 = natural ? rand.nextDouble()*360 : 0;

		IIcon ico = b.getIcon(0, 0);
		double u = ico.getMinU();
		double v = ico.getMinV();
		double du = ico.getMaxU();
		double dv = ico.getMaxV();
		double uu = du-u;
		double vv = dv-v;

		ArrayList<TessellatorVertex> li = new ArrayList();

		for (int i = 0; i < 4; i++) {

			double x1 = 6/16D;
			double x2 = 9/16D;
			double x3 = 9/16D;
			double x4 = 6/16D;
			double y1 = 0/16D;
			double y2 = 1/16D;
			double y3 = 5/16D;
			double y4 = 8/16D;

			li.add(new TessellatorVertex(x4, 0, y4));
			li.add(new TessellatorVertex(x3, 0, y3));
			li.add(new TessellatorVertex(x2, 0, y2));
			li.add(new TessellatorVertex(x1, 0, y1));

			x1 = 12/16D;
			x2 = 14/16D;
			x3 = 11/16D;
			x4 = 7/16D;
			y1 = 1/16D;
			y2 = 4/16D;
			y3 = 7/16D;
			y4 = 6/16D;

			li.add(new TessellatorVertex(x4, 0, y4));
			li.add(new TessellatorVertex(x3, 0, y3));
			li.add(new TessellatorVertex(x2, 0, y2));
			li.add(new TessellatorVertex(x1, 0, y1));

			for (TessellatorVertex vtx : li) {
				vtx.rotate(0, 90, 0, 0.5, 0.5, 0.5);
			}

		}

		double t = 0.0625;

		TessellatorVertexList tv5 = new TessellatorVertexList(x+0.5, y+0.5, z+0.5);

		for (TessellatorVertex vtx : li) {
			double dx = vtx.x();
			double dz = vtx.z();
			double fu = u+uu*dx;
			double fv = v+vv*dz;
			tv5.addVertexWithUVColor(x+dx, y+0.5+t, z+dz, fu, fv, 0xffffffff);
		}

		for (int i = li.size()-1; i >= 0; i--) {
			TessellatorVertex vtx = li.get(i);
			double dx = vtx.x();
			double dz = vtx.z();
			double fu = u+uu*dx;
			double fv = v+vv*dz;
			tv5.addVertexWithUVColor(x+dx, y+0.5-t, z+dz, fu, fv, 0xffa0a0a0);
		}

		for (int i = 0; i < li.size(); i++) {
			TessellatorVertex vtx1 = li.get(i);
			TessellatorVertex vtx2 = i%4 != 3 ? li.get(i+1) : li.get(i-i%4);
			double dx1 = vtx1.x();
			double dz1 = vtx1.z();
			double fu1 = u+uu*dx1;
			double fv1 = v+vv*dz1;
			double dx2 = vtx2.x();
			double dz2 = vtx2.z();
			double fu2 = u+uu*dx2;
			double fv2 = v+vv*dz2;
			tv5.addVertexWithUVColor(x+dx2, y+0.5-t, z+dz2, fu2, fv2, 0xffcacaca);
			tv5.addVertexWithUVColor(x+dx2, y+0.5+t, z+dz2, fu2, fv2, 0xffcacaca);
			tv5.addVertexWithUVColor(x+dx1, y+0.5+t, z+dz1, fu1, fv1, 0xffcacaca);
			tv5.addVertexWithUVColor(x+dx1, y+0.5-t, z+dz1, fu1, fv1, 0xffcacaca);
		}

		tv5.rotateNonOrthogonal(ang1, ang2, ang3);
		tv5.render();
	}

	private long calcSeed(int x, int y, int z) {
		return ChunkCoordIntPair.chunkXZ2Int(x, z) ^ y;
	}

	private void renderDirtChunks(IBlockAccess world, int x, int y, int z, RenderBlocks rb) {
		int n = 6+rand.nextInt(7);
		for (int i = 0; i < n; i++) {
			int s = 4+rand.nextInt(5);
			double dx = rand.nextInt(16-s+1);
			double dy = rand.nextInt(/*16*/Math.max(8-s, 0)+1);
			double dz = rand.nextInt(16-s+1);
			dx += i*0.001;
			dy += i*0.001;
			dz += i*0.001;
			ReikaRenderHelper.renderBlockSubCube(x, y, z, dx, dy, dz, s, Tessellator.instance, rb, Blocks.dirt, 0);
		}
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return false;
	}

	@Override
	public int getRenderId() {
		return ChromatiCraft.proxy.artefactRender;
	}

}
