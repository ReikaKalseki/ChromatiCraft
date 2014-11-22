/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render.TESR;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.TileEntity.TileEntityPowerTree;
import Reika.DragonAPI.Interfaces.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;

public class PowerTreeRender extends ChromaRenderBase {

	@Override
	public String getImageFileName(RenderFetcher te) {
		return null;
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityPowerTree te = (TileEntityPowerTree)tile;
		GL11.glPushMatrix();
		GL11.glTranslated(par2, par4, par6);
		if (!te.isInWorld() || MinecraftForgeClient.getRenderPass() == 1) {
			double o = 0.005;
			double x = -o;
			double y = -o;
			double z = -o;
			double dx = 1+o;
			double dy = 1+o;
			double dz = 1+o;

			IIcon ico = ChromaIcons.BATTERY.getIcon();
			float u = ico.getMinU();
			float v = ico.getMinV();
			float du = ico.getMaxU();
			float dv = ico.getMaxV();

			if (te.hasMultiBlock()) {
				z = -1-o;
				dx = 2+o;
			}

			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_LIGHTING);

			ReikaTextureHelper.bindTerrainTexture();
			Tessellator v5 = Tessellator.instance;
			v5.startDrawingQuads();
			v5.setColorOpaque_I(0xffffff);
			v5.setBrightness(240);
			v5.addVertexWithUV(x, dy, z, u, dv);
			v5.addVertexWithUV(dx, dy, z, du, dv);
			v5.addVertexWithUV(dx, y, z, du, v);
			v5.addVertexWithUV(x, y, z, u, v);

			v5.addVertexWithUV(x, y, dz, u, v);
			v5.addVertexWithUV(dx, y, dz, du, v);
			v5.addVertexWithUV(dx, dy, dz, du, dv);
			v5.addVertexWithUV(x, dy, dz, u, dv);

			v5.addVertexWithUV(x, y, z, u, v);
			v5.addVertexWithUV(x, y, dz, du, v);
			v5.addVertexWithUV(x, dy, dz, du, dv);
			v5.addVertexWithUV(x, dy, z, u, dv);

			v5.addVertexWithUV(dx, dy, z, u, dv);
			v5.addVertexWithUV(dx, dy, dz, du, dv);
			v5.addVertexWithUV(dx, y, dz, du, v);
			v5.addVertexWithUV(dx, y, z, u, v);

			v5.addVertexWithUV(x, dy, dz, u, v);
			v5.addVertexWithUV(dx, dy, dz, du, v);
			v5.addVertexWithUV(dx, dy, z, du, dv);
			v5.addVertexWithUV(x, dy, z, u, dv);

			v5.addVertexWithUV(x, y, z, u, dv);
			v5.addVertexWithUV(dx, y, z, du, dv);
			v5.addVertexWithUV(dx, y, dz, du, v);
			v5.addVertexWithUV(x, y, dz, u, v);
			v5.draw();

			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_LIGHTING);
		}
		GL11.glPopMatrix();
	}

}
