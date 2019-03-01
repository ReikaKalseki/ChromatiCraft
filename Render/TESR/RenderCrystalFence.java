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
import Reika.ChromatiCraft.Models.ModelFence;
import Reika.ChromatiCraft.TileEntity.AOE.Defence.TileEntityCrystalFence;
import Reika.DragonAPI.Instantiable.Data.Perimeter;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Rendering.ColorBlendList;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

public class RenderCrystalFence extends ChromaRenderBase {

	private int[] alpha = new int[4];

	private final ModelFence model = new ModelFence();

	public static final int MIN_ALPHA = 48;

	private static final ColorBlendList colors = new ColorBlendList(120);

	static {
		colors.addColor(0xffffff);
		colors.addColor(0xff8080);
		colors.addColor(0xffffff);
		colors.addColor(0x80ff80);
		colors.addColor(0xffffff);
		colors.addColor(0x8080ff);
		colors.addColor(0xffffff);
		colors.addColor(0xff80ff);
		colors.addColor(0xffffff);
		colors.addColor(0xffff80);
		colors.addColor(0xffffff);
		colors.addColor(0x80ffff);
	}

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "fence.png";
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityCrystalFence te = (TileEntityCrystalFence)tile;
		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glTranslated(par2, par4, par6);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		if (!te.isInWorld() || MinecraftForgeClient.getRenderPass() == 1)
			this.renderModel(te, model);
		if (te.isInWorld() && te.isValid() && MinecraftForgeClient.getRenderPass() == 1) {
			Perimeter p = te.getFence();
			Tessellator v5 = Tessellator.instance;

			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glShadeModel(GL11.GL_SMOOTH);
			GL11.glDisable(GL11.GL_CULL_FACE);
			BlendMode.ADDITIVEDARK.apply();

			int my = te.getFenceDepth();
			int py = te.getFenceHeight();

			//v5.setColorOpaque(255, 255, 255);
			for (int i = 0; i < p.segmentCount(); i++) {
				int ap = i == p.segmentCount()-1 ? te.getSegmentAlpha(0) : te.getSegmentAlpha(i+1);
				int a = te.getSegmentAlpha(i);
				int an = i == 0 ? te.getSegmentAlpha(p.segmentCount()-1) : te.getSegmentAlpha(i-1);
				Coordinate c1 = p.getSegmentPreCoord(i);
				Coordinate c2 = p.getSegmentPostCoord(i);
				int dx = c2.xCoord-c1.xCoord;
				int dz = c2.zCoord-c1.zCoord;
				int mx = (int)Math.signum(dx);
				int mz = (int)Math.signum(dz);

				int len = Math.abs(dx+dz);
				//v5.startDrawing(GL11.GL_LINE_LOOP);
				//v5.setColorRGBA(255, 255, 255, a);
				//v5.addVertex(c1.xCoord+0.5-te.xCoord, 000-te.yCoord, c1.zCoord+0.5-te.zCoord);
				//v5.addVertex(c2.xCoord+0.5-te.xCoord, 255-te.yCoord, c2.zCoord+0.5-te.zCoord);
				//v5.addVertex(c2.xCoord+0.5-te.xCoord, 255-te.yCoord, c2.zCoord+0.5-te.zCoord);
				//v5.addVertex(c2.xCoord+0.5-te.xCoord, 000-te.yCoord, c2.zCoord+0.5-te.zCoord);
				//v5.draw();

				if (a > MIN_ALPHA || ap > MIN_ALPHA || an > MIN_ALPHA) {
					if (a <= MIN_ALPHA)
						a = MIN_ALPHA;
					ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/hex-5.png");
				}
				else {
					a = MIN_ALPHA;
					ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/hex-7.png");
				}

				a = Math.min(a, 255);

				v5.startDrawingQuads();
				v5.setBrightness(240);
				//for (int l = 0; l < len; l++) {
				for (int l = 0; l < 2; l++) {
					for (double h = my; h < py; h += 0.5) {
						double v = ReikaMathLibrary.getDecimalPart(h);
						this.getAlphas(a, an, ap, l, len, h, my, py);

						double x1 = c1.xCoord+len/2D*l*mx+0.5-te.xCoord;
						double x2 = c1.xCoord+len/2D*(l+1)*mx+0.5-te.xCoord;

						double z1 = c1.zCoord+len/2D*l*mz+0.5-te.zCoord;
						double z2 = c1.zCoord+len/2D*(l+1)*mz+0.5-te.zCoord;

						int color1 = colors.getColor(te.getTicksExisted()+par8+Math.abs(((x1-te.xCoord)+z1-(te.zCoord)*2)*12));
						int color2 = colors.getColor(te.getTicksExisted()+par8+Math.abs(((x2-te.xCoord)+z2-(te.zCoord)*2)*12));

						if (a > MIN_ALPHA && te.colorFade(i)) {
							color1 = ReikaColorAPI.mixColors(0xffffff, color1, (a-MIN_ALPHA)/223F);
							color2 = ReikaColorAPI.mixColors(0xffffff, color2, (a-MIN_ALPHA)/223F);
						}

						int r1 = ReikaColorAPI.getRed(color1);
						int g1 = ReikaColorAPI.getGreen(color1);
						int b1 = ReikaColorAPI.getBlue(color1);
						int r2 = ReikaColorAPI.getRed(color2);
						int g2 = ReikaColorAPI.getGreen(color2);
						int b2 = ReikaColorAPI.getBlue(color2);

						double u = len/2D*l;
						double du = len/2D*(l+1);

						v5.setColorRGBA(alpha[0]*r2/255, alpha[0]*g2/255, alpha[0]*b2/255, alpha[0]);
						v5.addVertexWithUV(x2, h, z2, u, v);

						v5.setColorRGBA(alpha[1]*r1/255, alpha[1]*g1/255, alpha[1]*b1/255, alpha[1]);
						v5.addVertexWithUV(x1, h, z1, du, v);

						v5.setColorRGBA(alpha[2]*r1/255, alpha[2]*g1/255, alpha[2]*b1/255, alpha[2]);
						v5.addVertexWithUV(x1, h+0.5, z1, du, v+0.5);

						v5.setColorRGBA(alpha[3]*r2/255, alpha[3]*g2/255, alpha[3]*b2/255, alpha[3]);
						v5.addVertexWithUV(x2, h+0.5, z2, u, v+0.5);
					}
				}
				v5.draw();
				//v5.addVertex(loc.xCoord-te.xCoord+0.5, loc.yCoord-te.yCoord+0.5, loc.zCoord-te.zCoord+0.5);
				//ReikaAABBHelper.renderAABB(box, par2, par4, par6, te.xCoord, te.yCoord, te.zCoord, a, 64, 192, 255, true);
			}
			//AxisAlignedBB box = p.getAABBs().get(p.getAABBs().size()-1);
			//
		}

		GL11.glPopMatrix();
		GL11.glPopAttrib();
	}

	//0 - +D, -H; 1 - -D, -H; 2 - -D, +H; 3 - +D, +H
	private void getAlphas(int a, int an, int ap, int l, int len, double h, int my, int py) {
		double mid = len/2D;
		double pedge = l+1;
		//double vedge = 1D-Math.min(h-my, py-h)/(double)(py-my+1);

		//Reset
		alpha[0] = a;
		alpha[1] = a;
		alpha[2] = a;
		alpha[3] = a;

		//Horizontal
		if (l == 1) {
			alpha[0] = Math.max(ap, a);
			alpha[3] = Math.max(ap, a);
		}
		else {
			alpha[1] = Math.max(an, a);
			alpha[2] = Math.max(an, a);
		}

		//Vertical
		if (h <= my) {
			alpha[0] = 0;
			alpha[1] = 0;
		}
		else if (h >= py-0.5) {
			alpha[2] = 0;
			alpha[3] = 0;
		}
	}

}
