package Reika.ChromatiCraft.Render.TESR;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.ModInterface.TileEntityLumenAlveary;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;


public class RenderAlveary extends ChromaRenderBase {

	@Override
	public String getImageFileName(RenderFetcher te) {
		return null;
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityLumenAlveary te = (TileEntityLumenAlveary)tile;
		if (te.isInWorld() && te.isAlvearyComplete()) {
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glPushMatrix();

			GL11.glEnable(GL11.GL_BLEND);
			BlendMode.ADDITIVEDARK.apply();
			GL11.glDisable(GL11.GL_LIGHTING);
			ReikaRenderHelper.disableEntityLighting();
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			GL11.glDepthMask(false);
			GL11.glTranslated(par2, par4, par6);
			GL11.glShadeModel(GL11.GL_SMOOTH);

			Coordinate c = te.getRelativeLocation();
			GL11.glTranslated(-c.xCoord, -c.yCoord, -c.zCoord);

			double o = 0.0025;

			ReikaTextureHelper.bindTerrainTexture();

			IIcon[] icons = {
					ChromaIcons.HIVE.getIcon(),
			};

			int ca = 0xffa020;
			int cb = 0x604020;

			ca = ReikaColorAPI.getColorWithBrightnessMultiplier(ca, 0.5F);
			cb = ReikaColorAPI.getColorWithBrightnessMultiplier(cb, 0.25F);

			/*
			float f1 = 0.5F+0.5F*(float)Math.sin((te.getTicksExisted()+par8)/6D);
			float f2 = 0.5F+0.5F*(float)Math.sin(Math.PI*0.5+(te.getTicksExisted()+par8)/6D);
			int c2 = ReikaColorAPI.mixColors(ca, cb, f1);
			int c1 = ReikaColorAPI.mixColors(ca, cb, f2);
			 */

			int[][][] clr = new int[2][2][2];
			for (int i = 0; i < clr.length; i++) {
				for (int j = 0; j < clr[i].length; j++) {
					for (int k = 0; k < clr[i][j].length; k++) {
						float f = 0.5F+0.5F*(float)Math.sin(i*1.5+j*3.5+k+(te.getTicksExisted()+par8)/6D);
						f += 0.25F*(float)Math.cos(i*2.5+j*2+k*1+(te.getTicksExisted()+par8)/4D);
						f = MathHelper.clamp_float(f, 0, 1);
						clr[i][j][k] = ReikaColorAPI.mixColors(ca, cb, f);
					}
				}
			}

			Tessellator v5 = Tessellator.instance;
			v5.startDrawingQuads();

			for (int i = 0; i < icons.length; i++) {
				IIcon ico = icons[i];
				float u = ico.getMinU();
				float v = ico.getMinV();
				float du = ico.getMaxU();
				float dv = ico.getMaxV();

				v5.setColorOpaque_I(clr[0][1][0]);
				v5.addVertexWithUV(0-o, 3.5, 0-o, u, dv);
				v5.setColorOpaque_I(clr[1][1][0]);
				v5.addVertexWithUV(3+o, 3.5, 0-o, du, dv);
				v5.setColorOpaque_I(clr[0][0][0]);
				v5.addVertexWithUV(3+o, 0, 0-o, du, v);
				v5.setColorOpaque_I(clr[0][0][0]);
				v5.addVertexWithUV(0-o, 0, 0-o, u, v);

				v5.setColorOpaque_I(clr[0][0][1]);
				v5.addVertexWithUV(0-o, 0, 3+o, u, v);
				v5.setColorOpaque_I(clr[1][0][1]);
				v5.addVertexWithUV(3+o, 0, 3+o, du, v);
				v5.setColorOpaque_I(clr[1][1][1]);
				v5.addVertexWithUV(3+o, 3.5, 3+o, du, dv);
				v5.setColorOpaque_I(clr[0][1][1]);
				v5.addVertexWithUV(0-o, 3.5, 3+o, u, dv);


				v5.setColorOpaque_I(clr[1][1][0]);
				v5.addVertexWithUV(3+o, 3.5, 0-o, u, dv);
				v5.setColorOpaque_I(clr[1][1][1]);
				v5.addVertexWithUV(3+o, 3.5, 3+o, du, dv);
				v5.setColorOpaque_I(clr[1][0][1]);
				v5.addVertexWithUV(3+o, 0, 3+o, du, v);
				v5.setColorOpaque_I(clr[1][0][0]);
				v5.addVertexWithUV(3+o, 0, 0-o, u, v);


				v5.setColorOpaque_I(clr[0][0][0]);
				v5.addVertexWithUV(0-o, 0, 0-o, u, v);
				v5.setColorOpaque_I(clr[0][0][1]);
				v5.addVertexWithUV(0-o, 0, 3+o, du, v);
				v5.setColorOpaque_I(clr[0][1][1]);
				v5.addVertexWithUV(0-o, 3.5, 3+o, du, dv);
				v5.setColorOpaque_I(clr[0][1][0]);
				v5.addVertexWithUV(0-o, 3.5, 0-o, u, dv);


				v5.setColorOpaque_I(clr[0][1][1]);
				v5.addVertexWithUV(0-o, 3.5+o, 3+o, u, dv);
				v5.setColorOpaque_I(clr[1][1][1]);
				v5.addVertexWithUV(3+o, 3.5+o, 3+o, du, dv);
				v5.setColorOpaque_I(clr[1][1][0]);
				v5.addVertexWithUV(3+o, 3.5+o, 0-o, du, v);
				v5.setColorOpaque_I(clr[0][1][0]);
				v5.addVertexWithUV(0-o, 3.5+o, 0-o, u, v);
			}

			v5.draw();

			GL11.glPopAttrib();
			GL11.glPopMatrix();
		}
	}

}
