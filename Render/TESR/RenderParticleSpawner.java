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
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.MinecraftForgeClient;

import Reika.ChromatiCraft.Auxiliary.HoldingChecks;
import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.TileEntity.Decoration.TileEntityParticleSpawner;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Rendering.ReikaRenderHelper;

public class RenderParticleSpawner extends ChromaRenderBase {

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "";
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityParticleSpawner te = (TileEntityParticleSpawner)tile;

		if (te.isInWorld()) {
			if (MinecraftForgeClient.getRenderPass() != 1)
				return;
			boolean flag = false;
			if (HoldingChecks.MANIPULATOR.isClientHolding()) {
				flag = true;
			}
			else if (Minecraft.getMinecraft().thePlayer.getDistanceSq(te.xCoord+0.5, te.yCoord+0.5, te.zCoord+0.5) <= 21) {
				MovingObjectPosition mov = ReikaPlayerAPI.getLookedAtBlockClient(4.5, false);
				if (mov != null && mov.blockX == te.xCoord && mov.blockY == te.yCoord && mov.blockZ == te.zCoord) {
					flag = true;
				}
			}
			if (flag) {
				te.renderOpacity = Math.min(1, te.renderOpacity+0.125F);
			}
			else {
				te.renderOpacity = Math.max(0, te.renderOpacity-0.025F);
			}
		}

		if (te.renderOpacity <= 0)
			return;

		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glTranslated(par2, par4, par6);
		GL11.glEnable(GL11.GL_BLEND);
		BlendMode.ADDITIVEDARK.apply();
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_LIGHTING);
		ReikaRenderHelper.disableEntityLighting();

		Tessellator v5 = Tessellator.instance;

		IIcon[] icons = {
				ChromaIcons.CAUSTICS_GENTLE.getIcon(),
				ChromaIcons.GLOWFRAME.getIcon()
		};
		for (int n = 0; n < icons.length; n++) {
			IIcon ico = icons[n];

			ReikaTextureHelper.bindTerrainTexture();

			float u = ico.getMinU();
			float v = ico.getMinV();
			float du = ico.getMaxU();
			float dv = ico.getMaxV();



			//double su = du-u;
			//double sv = dv-v;

			/*
		double in = 0.25;

		double h0 = 0.25;
		double h1 = 0.375;
		double h2 = 0.5;

		double u1 = u+su*in;
		double u2 = u+su*(1-in);

		double v1 = v+sv*in;
		double v2 = v+sv*(1-in);
			 */

			v5.startDrawingQuads();

			v5.setBrightness(240);

			if (n >= 1)
				v5.setColorRGBA_I(ReikaColorAPI.getColorWithBrightnessMultiplier(0x3f3f3f, te.renderOpacity), 128);
			else
				v5.setColorOpaque_I(ReikaColorAPI.getColorWithBrightnessMultiplier(0xffffff, te.renderOpacity));

			double o = 0.4375;
			double i = 0.3125;

			v5.addVertexWithUV(i, o, i, u, v);
			v5.addVertexWithUV(1-i, o, i, du, v);
			v5.addVertexWithUV(1-i, o, 1-i, du, dv);
			v5.addVertexWithUV(i, o, 1-i, u, dv);

			v5.addVertexWithUV(i, 1-o, i, u, v);
			v5.addVertexWithUV(1-i, 1-o, i, du, v);
			v5.addVertexWithUV(1-i, 1-o, 1-i, du, dv);
			v5.addVertexWithUV(i, 1-o, 1-i, u, dv);

			v5.addVertexWithUV(o, i, i, u, v);
			v5.addVertexWithUV(o, 1-i, i, du, v);
			v5.addVertexWithUV(o, 1-i, 1-i, du, dv);
			v5.addVertexWithUV(o, i, 1-i, u, dv);

			v5.addVertexWithUV(1-o, i, i, u, v);
			v5.addVertexWithUV(1-o, 1-i, i, du, v);
			v5.addVertexWithUV(1-o, 1-i, 1-i, du, dv);
			v5.addVertexWithUV(1-o, i, 1-i, u, dv);

			v5.addVertexWithUV(i, i, o, u, v);
			v5.addVertexWithUV(1-i, i, o, du, v);
			v5.addVertexWithUV(1-i, 1-i, o, du, dv);
			v5.addVertexWithUV(i, 1-i, o, u, dv);

			v5.addVertexWithUV(i, i, 1-o, u, v);
			v5.addVertexWithUV(1-i, i, 1-o, du, v);
			v5.addVertexWithUV(1-i, 1-i, 1-o, du, dv);
			v5.addVertexWithUV(i, 1-i, 1-o, u, dv);

			/*
		double dd = 0.03125;

		for (double d1 = -0.5; d1 <= 0.5; d1 += dd) {
			for (double d2 = -0.5; d2 <= 0.5; d2 += dd) {
				double d11 = ReikaMathLibrary.py3d(d1, 0, d2);
				double d12 = ReikaMathLibrary.py3d(d1, 0, d2+dd);
				double d21 = ReikaMathLibrary.py3d(d1+dd, 0, d2);
				double d22 = ReikaMathLibrary.py3d(d1+dd, 0, d2+dd);
				double h11 = 0.5+0.25*Math.sin(te.xCoord+te.zCoord+4*d11+2*Math.toRadians(te.getTicksExisted()+par8));
				double h12 = 0.5+0.25*Math.sin(te.xCoord+te.zCoord+4*d12+2*Math.toRadians(te.getTicksExisted()+par8));
				double h21 = 0.5+0.25*Math.sin(te.xCoord+te.zCoord+4*d21+2*Math.toRadians(te.getTicksExisted()+par8));
				double h22 = 0.5+0.25*Math.sin(te.xCoord+te.zCoord+4*d22+2*Math.toRadians(te.getTicksExisted()+par8));
				double u1 = u+su*(d1+0.5);
				double u2 = u+su*(d1+dd+0.5);
				double v1 = v+sv*(d2+0.5);
				double v2 = v+sv*(d2+dd+0.5);
				v5.addVertexWithUV(d1, 		h11, d2, 		u1, v1);
				v5.addVertexWithUV(d1+dd, 	h21, d2, 		u2, v1);
				v5.addVertexWithUV(d1+dd, 	h22, d2+dd, 	u2, v2);
				v5.addVertexWithUV(d1, 		h12, d2+dd, 	u1, v2);
			}
		}
			 */

			/*
		v5.addVertexWithUV(in, 		h2, in, 	u1, v1);
		v5.addVertexWithUV(1-in, 	h2, in, 	u2, v1);
		v5.addVertexWithUV(1-in, 	h2, 1-in, 	u2, v2);
		v5.addVertexWithUV(in, 		h2, 1-in, 	u1, v2);

		v5.addVertexWithUV(0, 	h1, in, 	u, v1);
		v5.addVertexWithUV(in, 	h2, in, 	u1, v1);
		v5.addVertexWithUV(in, 	h2, 1-in, 	u1, v2);
		v5.addVertexWithUV(0, 	h1, 1-in, 	u, v2);

		v5.addVertexWithUV(1-in, 	h2, in, 	u2, v1);
		v5.addVertexWithUV(1, 		h1, in, 	du, v1);
		v5.addVertexWithUV(1, 		h1, 1-in, 	du, v2);
		v5.addVertexWithUV(1-in, 	h2, 1-in, 	u2, v2);

		v5.addVertexWithUV(in, 		h1, 0, 		u1, v);
		v5.addVertexWithUV(1-in, 	h1, 0, 		u2, v);
		v5.addVertexWithUV(1-in, 	h2, in, 	u2, v1);
		v5.addVertexWithUV(in, 		h2, in, 	u1, v1);

		v5.addVertexWithUV(in, 		h2, 1-in, 	u1, v2);
		v5.addVertexWithUV(1-in, 	h2, 1-in, 	u2, v2);
		v5.addVertexWithUV(1-in, 	h1, 1, 		u2, dv);
		v5.addVertexWithUV(in, 		h1, 1, 		u1, dv);

		v5.addVertexWithUV(0, 	h0, 0, 		u, v);
		v5.addVertexWithUV(in, 	h1, 0, 		u1, v);
		v5.addVertexWithUV(in, 	h2, in, 	u1, v1);
		v5.addVertexWithUV(0, 	h1, in, 	u, v1);
			 */

			v5.draw();
		}

		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

}
