/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render.TESR;

import java.awt.Point;
import java.util.Collection;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Models.ModelMultiBuilder;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityMultiBuilder;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockBox;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Rendering.StructureRenderer;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

public class RenderMultiBuilder extends ChromaRenderBase {

	private final ModelMultiBuilder model = new ModelMultiBuilder();

	private static final HashMap<DecimalPosition, Integer> columns = new HashMap();

	static {
		addColumn(1, 12, 1, Blocks.redstone_block);
		addColumn(14, 12, 14, Blocks.redstone_block);
		addColumn(1, 12, 14, Blocks.redstone_block);
		addColumn(14, 12, 1, Blocks.redstone_block);

		addColumn(0, 10, 4, Blocks.gold_block);
		addColumn(0, 10, 11, Blocks.gold_block);

		addColumn(0, 8, 6, Blocks.gold_block);
		addColumn(0, 8, 9, Blocks.gold_block);

		addColumn(15, 10, 4, Blocks.gold_block);
		addColumn(15, 10, 11, Blocks.gold_block);

		addColumn(15, 8, 6, Blocks.gold_block);
		addColumn(15, 8, 9, Blocks.gold_block);

		addColumn(4, 10, 0, Blocks.gold_block);
		addColumn(11, 10, 0, Blocks.gold_block);

		addColumn(6, 8, 0, Blocks.gold_block);
		addColumn(9, 8, 0, Blocks.gold_block);

		addColumn(4, 10, 15, Blocks.gold_block);
		addColumn(11, 10, 15, Blocks.gold_block);

		addColumn(6, 8, 15, Blocks.gold_block);
		addColumn(9, 8, 15, Blocks.gold_block);

		addColumn(4, 7, 4, Blocks.diamond_block);
		addColumn(12, 7, 4, Blocks.diamond_block);
		addColumn(4, 7, 12, Blocks.diamond_block);
		addColumn(12, 7, 12, Blocks.diamond_block);

		addColumn(4, 5, 6, Blocks.quartz_block);
		addColumn(4, 5, 9, Blocks.quartz_block);

		addColumn(11, 5, 6, Blocks.quartz_block);
		addColumn(11, 5, 9, Blocks.quartz_block);

		addColumn(6, 5, 4, Blocks.quartz_block);
		addColumn(9, 5, 4, Blocks.quartz_block);

		addColumn(6, 5, 11, Blocks.quartz_block);
		addColumn(9, 5, 11, Blocks.quartz_block);

		addColumn(8, 4, 8, Blocks.emerald_block);
	}

	private static void addColumn(int x, int y, int z, Block b) {
		columns.put(new DecimalPosition(x/16D+0.03125, y/16D-0.03125, z/16D+0.03125), getColorFromBlock(b));
	}

	private static int getColorFromBlock(Block b) {
		if (b == Blocks.redstone_block)
			return 0xE02308;
		if (b == Blocks.emerald_block)
			return 0x4ADE74;
		if (b == Blocks.diamond_block)
			return 0x89E4E0;
		if (b == Blocks.gold_block)
			return 0xFCF456;
		if (b == Blocks.quartz_block)
			return 0xEAE8E2;
		return 0xffffff;
	}

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "multibuilder.png";
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityMultiBuilder te = (TileEntityMultiBuilder)tile;
		GL11.glPushMatrix();
		GL11.glTranslated(par2, par4, par6);
		if (!te.isInWorld() || MinecraftForgeClient.getRenderPass() == 0 || StructureRenderer.isRenderingTiles())
			this.renderModel(te, model);
		if ((te.isInWorld() && MinecraftForgeClient.getRenderPass() == 1) || StructureRenderer.isRenderingTiles()) {
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			BlendMode.ADDITIVEDARK.apply();
			te.renderBeams(par8, columns);
			this.renderArea(te);
			GL11.glPopAttrib();
		}
		GL11.glPopMatrix();
	}

	private void renderArea(TileEntityMultiBuilder te) {
		BlockBox box = te.getBounds();
		if (box == null)
			return;
		Tessellator v5 = Tessellator.instance;
		GL11.glPushMatrix();
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/blockbox2.png"); //use diff tex, more geometric
		double rx = (box.getSizeX()-1)/2D;
		double rz = (box.getSizeZ()-1)/2D;
		v5.startDrawingQuads();
		v5.setBrightness(240);

		double o = 0.03125/2;

		Collection<Point> c = te.getRegionsForRender();
		for (Point p : c) {
			double dx = p.x*(box.getSizeX()-1);
			double dz = p.y*(box.getSizeZ()-1);

			double u = 0;
			double du = u+1;
			double v = ((System.identityHashCode(te)+System.currentTimeMillis()/64D+(p.x+p.y)*24)%64)/64D;
			double dv = v+1/64D;

			v5.setColorOpaque_I(p.x == 0 && p.y == 0 ? 0xff3030 : 0x3030ff);

			v5.addVertexWithUV(-rx+0.5+dx, 1, rz+0.5+dz-o, v, u);
			v5.addVertexWithUV(-rx+0.5+dx, 0, rz+0.5+dz-o, v, du);
			v5.addVertexWithUV(rx+0.5+dx, 0, rz+0.5+dz-o, dv, du);
			v5.addVertexWithUV(rx+0.5+dx, 1, rz+0.5+dz-o, dv, u);

			v5.addVertexWithUV(-rx+0.5+dx, 1, -rz+0.5+dz+o, v, u);
			v5.addVertexWithUV(-rx+0.5+dx, 0, -rz+0.5+dz+o, v, du);
			v5.addVertexWithUV(rx+0.5+dx, 0, -rz+0.5+dz+o, dv, du);
			v5.addVertexWithUV(rx+0.5+dx, 1, -rz+0.5+dz+o, dv, u);

			v5.addVertexWithUV(rx+0.5+dx-o, 1, -rz+0.5+dz, v, u);
			v5.addVertexWithUV(rx+0.5+dx-o, 0, -rz+0.5+dz, v, du);
			v5.addVertexWithUV(rx+0.5+dx-o, 0, rz+0.5+dz, dv, du);
			v5.addVertexWithUV(rx+0.5+dx-o, 1, rz+0.5+dz, dv, u);

			v5.addVertexWithUV(-rx+0.5+dx+o, 1, -rz+0.5+dz, v, u);
			v5.addVertexWithUV(-rx+0.5+dx+o, 0, -rz+0.5+dz, v, du);
			v5.addVertexWithUV(-rx+0.5+dx+o, 0, rz+0.5+dz, dv, du);
			v5.addVertexWithUV(-rx+0.5+dx+o, 1, rz+0.5+dz, dv, u);
		}

		v5.draw();
		GL11.glPopMatrix();
	}

}
