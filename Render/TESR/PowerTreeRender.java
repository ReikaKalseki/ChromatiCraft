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

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.MinecraftForgeClient;

import Reika.ChromatiCraft.Auxiliary.ChromaFX;
import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.TileEntity.Storage.TileEntityPowerTree;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Rendering.StructureRenderer;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

public class PowerTreeRender extends ChromaRenderBase {

	@Override
	public String getImageFileName(RenderFetcher te) {
		return null;
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityPowerTree te = (TileEntityPowerTree)tile;
		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glTranslated(par2, par4, par6);
		if (!te.isInWorld() || MinecraftForgeClient.getRenderPass() == 1 || StructureRenderer.isRenderingTiles()) {
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

			if (te.hasMultiBlock() || StructureRenderer.isRenderingTiles()) {
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

			if (te.canConduct() && te.isEnhanced()) {
				this.renderHalo(te, par2, par4, par6, par8);
			}

		}
		if (tile.hasWorldObj() && MinecraftForgeClient.getRenderPass() == 0) {
			ChromaFX.drawEnergyTransferBeams(new WorldLocation(te), te.getOutgoingBeamRadius(), te.getTargets());
		}
		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

	private void renderHalo(TileEntityPowerTree te, double par2, double par4, double par6, float par8) {
		BlendMode.ADDITIVEDARK.apply();
		Tessellator v5 = Tessellator.instance;

		IIcon ico = ChromaIcons.CAUSTICS_GENTLE.getIcon();
		float u = ico.getMinU();
		float v = ico.getMinV();
		float du = ico.getMaxU();
		float dv = ico.getMaxV();
		double s = 1.5+0.5*Math.sin(Math.toRadians(te.getTicksExisted()+par8));

		ReikaRenderHelper.prepareGeoDraw(true);
		int d = 1;
		double dsq = GuiScreen.isCtrlKeyDown() ? 0 : Minecraft.getMinecraft().thePlayer.getDistanceSq(te.xCoord+0.5, te.yCoord+0.5-7, te.zCoord+0.5);
		double da = dsq >= 4096 ? 60 : dsq >= 1024 ? 45 : dsq >= 256 ? 30 : 20;
		double i = 0;
		int c = 0xff000000 | ReikaColorAPI.getModifiedHue(0x5f0000, te.getTicksExisted()%360);
		double t = -((te.getTicksExisted()+par8)%20)/20D;
		for (int h = -11; h <= 2; h += d) {
			double r = 5-5*Math.pow((i+t)/14D, 6);
			if (i < 6)
				r += Math.pow(6-i-t, 3)*0.03125/3;
			if (r > 0) {
				double y = h+t;
				double y2 = h+d+t;
				double dr = 0.0625;
				double r2 = 5-5*Math.pow((i+1+t)/14D, 6);
				int c1 = c;
				if (h == -11) {
					c1 = ReikaColorAPI.getColorWithBrightnessMultiplier(c, 1+(float)t);
				}
				for (double a = 0; a < 360; a += da) {
					double a2 = a+da;
					double x1 = 1+r*Math.cos(Math.toRadians(a));
					double x2 = 1+r*Math.cos(Math.toRadians(a2));
					double z1 = r*Math.sin(Math.toRadians(a));
					double z2 = r*Math.sin(Math.toRadians(a2));
					ReikaRenderHelper.renderTube(x1, y, z1, x2, y, z2, c1, c1, dr, dr, 4);
					if (r2 > 0) {
						double x1b = 1+r2*Math.cos(Math.toRadians(a));
						double x2b = 1+r2*Math.cos(Math.toRadians(a2));
						double z1b = r2*Math.sin(Math.toRadians(a));
						double z2b = r2*Math.sin(Math.toRadians(a2));
						if (h == 2) {
							ReikaRenderHelper.renderTube(x1b, y2, z1b, x2b, y2, z2b, c1, c1, dr, dr, 4);
						}
						GL11.glEnable(GL11.GL_TEXTURE_2D);
						Tessellator.instance.startDrawingQuads();
						int c2 = ReikaColorAPI.getModifiedHue(0xff7070, te.getTicksExisted()%360);
						if (h == -11) {
							c2 = ReikaColorAPI.getColorWithBrightnessMultiplier(c2, 1+(float)t);
						}
						Tessellator.instance.setColorOpaque_I(c2);

						Tessellator.instance.addVertexWithUV(x1, y, z1, u, v);
						Tessellator.instance.addVertexWithUV(x1b, y+1, z1b, u, dv);
						Tessellator.instance.addVertexWithUV(x2b, y+1, z2b, du, dv);
						Tessellator.instance.addVertexWithUV(x2, y, z2, du, v);

						Tessellator.instance.draw();
						GL11.glDisable(GL11.GL_TEXTURE_2D);
					}
				}
			}
			i++;
		}

	}

}
