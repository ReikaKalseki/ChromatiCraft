/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render.TESR;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.MinecraftForgeClient;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.ModInterface.TileEntitySmelteryDistributor;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

public class RenderSmelteryDistributor extends ChromaRenderBase {

	//private final ModelFluidDistributor model = new ModelFluidDistributor();

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "smelterydistrib.png";
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntitySmelteryDistributor te = (TileEntitySmelteryDistributor)tile;
		if (MinecraftForgeClient.getRenderPass() == 1 || !te.isInWorld()) {
			GL11.glPushMatrix();
			Tessellator v5 = Tessellator.instance;
			GL11.glTranslated(par2, par4, par6);

			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			ReikaTextureHelper.bindTerrainTexture();
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glDepthMask(false);
			BlendMode.ADDITIVEDARK.apply();
			GL11.glDisable(GL11.GL_LIGHTING);
			ReikaRenderHelper.disableEntityLighting();

			this.renderFlare(te, v5);

			GL11.glPopAttrib();
			GL11.glPopMatrix();
		}
	}

	private void renderFlare(TileEntitySmelteryDistributor te, Tessellator v5) {
		GL11.glPushMatrix();
		GL11.glTranslated(0.5, 0.5, 0.5);
		double s = 0.8;
		GL11.glScaled(s, s, s);
		RenderManager rm = RenderManager.instance;
		if (te.isInWorld()) {
			GL11.glRotatef(-rm.playerViewY, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);
		}
		else {
			GL11.glRotatef(45, 0, 1, 0);
			GL11.glRotatef(-45, 1, 0, 0);
		}

		double t = te.isInWorld() ? te.getTicksExisted() : -System.currentTimeMillis()/80D;

		GL11.glRotated((te.getRotation())%360, 0, 0, 1);

		IIcon ico = ChromaIcons.PINWHEEL.getIcon();
		ReikaTextureHelper.bindTerrainTexture();
		float u = ico.getMinU();
		float v = ico.getMinV();
		float du = ico.getMaxU();
		float dv = ico.getMaxV();

		int c1 = this.getColor(te);
		int c2 = 0xffffff;

		double da = 10+5*Math.sin(t/10D);

		double d0 = 0;
		for (double d = 1; d >= 0.25; d *= 0.75) {
			GL11.glRotated(da, 0, 0, 1);
			v5.startDrawingQuads();
			int c = ReikaColorAPI.mixColors(c1, c2, (float)d);
			v5.setColorRGBA_I(c, 255);
			v5.addVertexWithUV(-d, -d, d0, u, v);
			v5.addVertexWithUV(d, -d, d0, du, v);
			v5.addVertexWithUV(d, d, d0, du, dv);
			v5.addVertexWithUV(-d, d, d0, u, dv);
			v5.draw();
			d0 += 0.03125;
		}

		GL11.glPopMatrix();
	}

	public int getColor(TileEntitySmelteryDistributor te) {
		int c0 = 0xFFA700;
		float f = te.getColorIntensity();
		return f >= 1 ? c0 : ReikaColorAPI.mixColors(c0, 0xd0d0d0, f);
	}
}
