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
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.MinecraftForgeClient;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.TileEntity.Transport.TileEntityLaunchPad;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;


public class RenderLaunchPad extends ChromaRenderBase {

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "launchpad.png";
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityLaunchPad te = (TileEntityLaunchPad)tile;
		GL11.glPushMatrix();
		GL11.glTranslated(par2, par4, par6);
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		if (te.isInWorld() && te.hasStructure() && MinecraftForgeClient.getRenderPass() == 1) {
			ReikaRenderHelper.disableLighting();
			GL11.glEnable(GL11.GL_BLEND);
			BlendMode.ADDITIVEDARK.apply();
			GL11.glFrontFace(GL11.GL_CW);
			//GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glDepthMask(false);
			this.renderCharge(te, par8);
		}
		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

	private void renderCharge(TileEntityLaunchPad tile, float par8) {
		float f = tile.getChargeFraction();
		if (f > 0) {

			ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/launchcharge_additive.png");
			//double x0 = 0.5-s;
			//double x1 = 0.5+s;
			//double z0 = 0.5-s;
			//double z1 = 0.5+s;
			Tessellator v5 = Tessellator.instance;
			v5.setBrightness(240);
			v5.startDrawing(GL11.GL_TRIANGLE_FAN);
			v5.setNormal(0, 1, 0);

			if (tile.isEnhanced()) {
				this.renderFan(tile, v5, par8, f, 0x00ff00);
				this.renderFan(tile, v5, par8, f, 0xffffff);
			}
			else {
				this.renderFan(tile, v5, par8, f, 0xffffff);
			}

			v5.draw();
		}
	}

	private void renderFan(TileEntityLaunchPad tile, Tessellator v5, float par8, float f, int color) {
		double s = 1.5;
		double lim = f*360;
		double da = 5;
		double h = 1.001;

		v5.setColorOpaque_I(color);

		//v5.addVertexWithUV(x0, h, z1, u, dv);
		//v5.addVertexWithUV(x1, h, z1, du, dv);
		//v5.addVertexWithUV(x1, h, z0, du, v);
		//v5.addVertexWithUV(x0, h, z0, u, v);

		v5.addVertexWithUV(0.5, h, 0.5, 0.5, 0.5);

		for (double a = 0; a <= lim; a += da) {
			double ang = Math.toRadians(a);
			double r = s*Math.min(Math.abs(1D/Math.cos(ang)), Math.abs(1D/Math.sin(ang)));
			double dx = r*Math.cos(ang);
			double dz = r*Math.sin(ang);
			double u = 0.5+0.5*dx/s;
			double v = 0.5+0.5*dz/s;
			v5.addVertexWithUV(0.5+dx, h, 0.5+dz, u, v);
		}
	}

}
