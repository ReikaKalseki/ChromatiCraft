/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render.TESR.Dimension;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Block.Dimension.Structure.AntFarm.BlockAntKey.AntKeyTile;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

public class RenderAntKey extends ChromaRenderBase {

	private static final double[][] ROTATIONS = {
		{1, 0, 0},
		{0, 1, 0},
		{0, 0, 1},
		{1, 1, 0},
		{0, 1, 1},
		{1, 0, 1},
		{1, 1, 1},
		{-2, 0, 0},
		{0, -2, 0},
		{0, 0, -2},
		{-2, 2, 0},
		{0, -2, 2},
		{2, 0, -2},
		{-2, 2, 2},
		{2, -1, 1},
		{1, -2, 2},
	};

	@Override
	public String getImageFileName(RenderFetcher te) {
		return null;
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		AntKeyTile te = (AntKeyTile)tile;

		GL11.glPushMatrix();

		if (te.hasWorldObj()) {
			this.renderItem(te, par2, par4, par6, par8);
		}
		GL11.glTranslated(par2, par4, par6);
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glDisable(GL11.GL_LIGHTING);
		ReikaTextureHelper.bindTerrainTexture();
		Tessellator v5 = Tessellator.instance;
		v5.startDrawingQuads();
		v5.setColorOpaque_I(0xffffff);
		v5.setBrightness(240);
		BlendMode.ADDITIVEDARK.apply();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		IIcon ico = ChromaIcons.RIFTHALO.getIcon();
		float u = ico.getMinU();
		float du = ico.getMaxU();
		float v = ico.getMinV();
		float dv = ico.getMaxV();
		v5.addVertexWithUV(0.125, 0, 0.125, u, v);
		v5.addVertexWithUV(0.125, 4, 0.125, u, dv);
		v5.addVertexWithUV(0.875, 4, 0.125, du, dv);
		v5.addVertexWithUV(0.875, 0, 0.125, du, v);

		v5.addVertexWithUV(0.875, 0, 0.875, du, v);
		v5.addVertexWithUV(0.875, 4, 0.875, du, dv);
		v5.addVertexWithUV(0.125, 4, 0.875, u, dv);
		v5.addVertexWithUV(0.125, 0, 0.875, u, v);

		v5.addVertexWithUV(0.875, 0, 0.125, u, v);
		v5.addVertexWithUV(0.875, 4, 0.125, u, dv);
		v5.addVertexWithUV(0.875, 4, 0.875, du, dv);
		v5.addVertexWithUV(0.875, 0, 0.875, du, v);

		v5.addVertexWithUV(0.125, 0, 0.875, du, v);
		v5.addVertexWithUV(0.125, 4, 0.875, du, dv);
		v5.addVertexWithUV(0.125, 4, 0.125, u, dv);
		v5.addVertexWithUV(0.125, 0, 0.125, u, v);
		v5.draw();
		GL11.glPopAttrib();

		GL11.glPopMatrix();
	}

	private void renderItem(AntKeyTile te, double par2, double par4, double par6, float ptick) {
		GL11.glPushMatrix();
		GL11.glTranslated(par2, par4, par6);
		int size = te.getSizeInventory();
		for (int n = 0; n < size; n++) {
			EntityItem ei = te.getItem(n);
			ItemStack is = te.getStackInSlot(n);
			if (ei != null && is != null) {
				ei.age = 0;
				ei.hoverStart = 0;

				double[] rot = ROTATIONS[n];

				Render r = RenderManager.instance.getEntityClassRenderObject(EntityItem.class);
				double tick = System.currentTimeMillis()/40D+n*36;
				double s = 1.5;
				GL11.glPushMatrix();
				double ang = (tick*3D)%360;
				GL11.glTranslated(0, 0.125+n*0.5, 0);
				GL11.glTranslated(0.5, 0, 0.5);
				GL11.glScaled(s, s, s);
				GL11.glTranslated(0, 0.09375, 0);
				GL11.glRotated(ang*rot[1], 1, 0, 0);
				GL11.glRotated(ang*rot[0], 0, 1, 0);
				GL11.glRotated(ang*rot[2], 0, 0, 1);
				GL11.glTranslated(0, -0.09375, 0);
				r.doRender(ei, 0, 0, 0, 0, 0);
				GL11.glPopMatrix();

			}
		}
		GL11.glPopMatrix();
	}

}
