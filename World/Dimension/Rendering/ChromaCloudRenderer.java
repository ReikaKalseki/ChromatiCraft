/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Rendering;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.IRenderHandler;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ChromaCloudRenderer extends IRenderHandler {

	public static final ChromaCloudRenderer instance = new ChromaCloudRenderer();

	private ChromaCloudRenderer() {

	}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(float ptick, WorldClient world, Minecraft mc) {
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_FOG);

		Tessellator v5 = Tessellator.instance;

		double w = 512;

		float f2 = 1;//12.0F;

		float playerY = (float)(mc.renderViewEntity.lastTickPosY + (mc.renderViewEntity.posY - mc.renderViewEntity.lastTickPosY) * ptick);
		float y = 70*0+28+8-playerY*0.0625F;//world.provider.getCloudHeight() - playerY + 0.33F;

		double x = (mc.renderViewEntity.prevPosX + (mc.renderViewEntity.posX - mc.renderViewEntity.prevPosX) * ptick) / f2;
		double z = (mc.renderViewEntity.prevPosZ + (mc.renderViewEntity.posZ - mc.renderViewEntity.prevPosZ) * ptick) / f2 + 0.33D;

		GL11.glTranslated(-x%(w*2), y, -z%(w*2));

		GL11.glEnable(GL11.GL_BLEND);
		BlendMode.ADDITIVEDARK.apply();
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		int r = 2;
		for (int a = -r; a <= r; a++) {
			for (int b = -r; b <= r; b++) {
				for (int i = 0; i < 4; i++) {
					ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/clouds/dimsky_"+i+".png");

					double s = 0.75+0.25*i;
					double slide = (((world.getTotalWorldTime()%24000 + ptick)/24000D)*w*s)%w;

					GL11.glPushMatrix();
					GL11.glRotated(i*60, 0, 1, 0);
					GL11.glTranslated(slide, 0, 0);

					double d = -i*6;
					int c1 = 0xa0;
					int c2 = 0x70;

					c1 *= Math.min(1, Math.max(0, playerY/64D));
					c2 *= Math.min(1, Math.max(0, playerY/64D));

					double mult = 0.625+0.375*Math.sin(System.currentTimeMillis()/2000D+i*Math.PI/2);

					c1 *= mult;
					c2 *= mult;

					c1 *= 1D-(a*a+b*b)/8D;
					c2 *= 1D-(a*a+b*b)/8D;

					c1 = ReikaColorAPI.GStoHex(c1);
					c2 = ReikaColorAPI.GStoHex(c2);
					this.drawCloudLayer2(v5, a, b, c1, w*1, 180+d-playerY*0.4375, i);
					this.drawCloudLayer2(v5, a, b, c2, w, 0+d, i);

					GL11.glPopMatrix();
				}
			}
		}


		GL11.glPopMatrix();
		BlendMode.DEFAULT.apply();
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	private double getCloudOffset(int i, int k, int index) {
		long time = System.currentTimeMillis()+index*40000000;
		double d = 2+2*Math.sin((i*i+k*k)/32D*((time%250000+250000)%250000)/250000D);
		return d*(Math.sin(i+time/5000D)+Math.sin(k/2D+time/2000D));
	}

	private void drawCloudLayer2(Tessellator v5, int a, int b, int color, double w, double h, int index) {
		v5.startDrawingQuads();
		//v5.setColorRGBA_I(0xffffff, color);
		v5.setColorOpaque_I(color);

		long time = System.currentTimeMillis()+index*40000000;

		double d1 = 4*Math.sin(time/10000D);
		double d2 = 9*Math.sin(time/20000D);
		double d3 = 3*Math.sin(time/4000D);
		double d4 = 6*Math.cos(time/15000D);

		double h1 = h+d1;
		double h2 = h+d2;
		double h3 = h+d3;
		double h4 = h+d4;

		double dw = a*w*2;
		double dw2 = b*w*2;

		v5.addVertexWithUV(dw-w, h1, dw2-w, 0, 0);
		v5.addVertexWithUV(dw+w, h2, dw2-w, 1, 0);
		v5.addVertexWithUV(dw+w, h3, dw2+w, 1, 1);
		v5.addVertexWithUV(dw-w, h4, dw2+w, 0, 1);

		v5.draw();
	}

	private void drawCloudLayer(Tessellator v5, int color, double w, double h, int index) {

		v5.startDrawingQuads();
		v5.setColorRGBA_I(0xffffff, color);

		int s = 128; //2048x2048

		double[][] off = new double[s+2][s+2];

		for (int i = -1; i <= s; i++) {
			for (int k = -1; k <= s; k++) {
				off[i+1][k+1] = this.getCloudOffset(i, k, index)*(h > 0 ? 4 : 1);
			}
		}

		double f = 1D/s;
		double f2 = f*w;
		for (int i = 0; i < s; i++) {
			double fxt = i*f;
			double fx = i*f2;
			double dx = -w+fx;
			for (int k = 0; k < s; k++) {
				double fzt = k*f;
				double fz = k*f2;
				double dz = -w+fz;

				double hmm = h+(off[i+1][k+1]+off[i-1+1][k-1+1]+off[i-1+1][k+1]+off[i+1][k-1+1])/5D;
				double hpm = h+(off[i+1][k+1]+off[i+1+1][k-1+1]+off[i+1+1][k+1]+off[i+1][k-1+1])/5D;
				double hpp = h+(off[i+1][k+1]+off[i+1+1][k+1+1]+off[i+1+1][k+1]+off[i+1][k+1+1])/5D;
				double hmp = h+(off[i+1][k+1]+off[i-1+1][k+1+1]+off[i-1+1][k+1]+off[i+1][k+1+1])/5D;

				v5.addVertexWithUV(dx, hmm, dz, fxt, fzt);
				v5.addVertexWithUV(dx+f2, hpm, dz, fxt+f, fzt);
				v5.addVertexWithUV(dx+f2, hpp, dz+f2, fxt+f, fzt+f);
				v5.addVertexWithUV(dx, hmp, dz+f2, fxt, fzt+f);

				/*

				v5.addVertexWithUV(dx, h, dz, fxt, fzt);
				v5.addVertexWithUV(dx+f2, h, dz, fxt+f, fzt);
				v5.addVertexWithUV(dx+f2, h, dz+f2, fxt+f, fzt+f);
				v5.addVertexWithUV(dx, h, dz+f2, fxt, fzt+f);

				v5.addVertexWithUV(dx, h2, dz, fxt, fzt);
				v5.addVertexWithUV(dx+f2, h2, dz, fxt+f, fzt);
				v5.addVertexWithUV(dx+f2, h2, dz+f2, fxt+f, fzt+f);
				v5.addVertexWithUV(dx, h2, dz+f2, fxt, fzt+f);

				v5.addVertexWithUV(dx, h, dz, fxt, fzt);
				v5.addVertexWithUV(dx, h2, dz, fxt+f, fzt);
				v5.addVertexWithUV(dx, h2, dz+f2, fxt+f, fzt+f);
				v5.addVertexWithUV(dx, h, dz+f2, fxt, fzt+f);

				v5.addVertexWithUV(dx+f2, h, dz, fxt, fzt);
				v5.addVertexWithUV(dx+f2, h2, dz, fxt+f, fzt);
				v5.addVertexWithUV(dx+f2, h2, dz+f2, fxt+f, fzt+f);
				v5.addVertexWithUV(dx+f2, h, dz+f2, fxt, fzt+f);


				v5.addVertexWithUV(dx, h, dz, fxt, fzt);
				v5.addVertexWithUV(dx, h2, dz, fxt+f, fzt);
				v5.addVertexWithUV(dx+f2, h2, dz, fxt+f, fzt+f);
				v5.addVertexWithUV(dx+f2, h, dz, fxt, fzt+f);

				v5.addVertexWithUV(dx, h, dz+f2, fxt, fzt);
				v5.addVertexWithUV(dx, h2, dz+f2, fxt+f, fzt);
				v5.addVertexWithUV(dx+f2, h2, dz+f2, fxt+f, fzt+f);
				v5.addVertexWithUV(dx+f2, h, dz+f2, fxt, fzt+f);
				 */
			}
		}
		v5.draw();
	}

	private void renderCloudsVanilla(WorldClient world, Minecraft mc, float ptick) {
		float playerY = (float)(mc.renderViewEntity.lastTickPosY + (mc.renderViewEntity.posY - mc.renderViewEntity.lastTickPosY) * ptick);
		Tessellator tessellator = Tessellator.instance;
		float f2 = 12.0F;
		float cloudTickCounter = world.getTotalWorldTime();
		double slide = cloudTickCounter + ptick;
		double posX = (mc.renderViewEntity.prevPosX + (mc.renderViewEntity.posX - mc.renderViewEntity.prevPosX) * ptick + slide * 0.03D) / f2;
		double posZ = (mc.renderViewEntity.prevPosZ + (mc.renderViewEntity.posZ - mc.renderViewEntity.prevPosZ) * ptick) / f2 + 0.33D;
		float f4 = world.provider.getCloudHeight() - playerY + 0.33F;
		int i = MathHelper.floor_double(posX / 2048.0D);
		int j = MathHelper.floor_double(posZ / 2048.0D);
		posX -= i * 2048;
		posZ -= j * 2048;
		//mc.renderEngine.bindTexture(locationCloudsPng);
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/clouds/dimension.png");
		GL11.glEnable(GL11.GL_BLEND);
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		Vec3 vec3 = world.getCloudColour(ptick);
		float red = (float)vec3.xCoord;
		float green = (float)vec3.yCoord;
		float blue = (float)vec3.zCoord;

		if (mc.gameSettings.anaglyph) {
			float f8 = (red * 30.0F + green * 59.0F + blue * 11.0F) / 100.0F;
			float f9 = (red * 30.0F + green * 70.0F) / 100.0F;
			float f10 = (red * 30.0F + blue * 70.0F) / 100.0F;
			red = f8;
			green = f9;
			blue = f10;
		}

		float f8 = (float)(posX * 0.0D);
		float f9 = (float)(posZ * 0.0D);
		float f10 = 1/256F;
		f8 = MathHelper.floor_double(posX) * f10;
		f9 = MathHelper.floor_double(posZ) * f10;
		float floatX = (float)(posX - MathHelper.floor_double(posX));
		float floatZ = (float)(posZ - MathHelper.floor_double(posZ));
		byte b0 = 8;
		byte b1 = 4;
		float f13 = 0;
		GL11.glScalef(f2, 1.0F, f2);

		for (int k = 0; k < 2; ++k)
		{
			if (k == 0)
			{
				GL11.glColorMask(false, false, false, false);
			}
			else if (mc.gameSettings.anaglyph)
			{
				if (EntityRenderer.anaglyphField == 0)
				{
					GL11.glColorMask(false, true, true, true);
				}
				else
				{
					GL11.glColorMask(true, false, false, true);
				}
			}
			else
			{
				GL11.glColorMask(true, true, true, true);
			}

			for (int dx = -b1 + 1; dx <= b1; ++dx)
			{
				for (int dz = -b1 + 1; dz <= b1; ++dz)
				{
					tessellator.startDrawingQuads();
					float f14 = dx * b0;
					float f15 = dz * b0;
					float f16 = f14 - floatX;
					float f17 = f15 - floatZ;
					double sx = f16-mc.renderViewEntity.posX;
					double sz = f17-mc.renderViewEntity.posZ;
					double f3 = 4+2*Math.sin((sx*sx+sz*sz)/10000F);

					if (f4 > -f3 - 1.0F)
					{
						tessellator.setColorRGBA_F(red * 0.7F, green * 0.7F, blue * 0.7F, 0.8F);
						tessellator.setNormal(0.0F, -1.0F, 0.0F);
						tessellator.addVertexWithUV(f16 + 0.0F, f4 + 0.0F, f17 + b0, (f14 + 0.0F) * f10 + f8, (f15 + b0) * f10 + f9);
						tessellator.addVertexWithUV(f16 + b0, f4 + 0.0F, f17 + b0, (f14 + b0) * f10 + f8, (f15 + b0) * f10 + f9);
						tessellator.addVertexWithUV(f16 + b0, f4 + 0.0F, f17 + 0.0F, (f14 + b0) * f10 + f8, (f15 + 0.0F) * f10 + f9);
						tessellator.addVertexWithUV(f16 + 0.0F, f4 + 0.0F, f17 + 0.0F, (f14 + 0.0F) * f10 + f8, (f15 + 0.0F) * f10 + f9);
					}

					if (f4 <= f3 + 1.0F)
					{
						tessellator.setColorRGBA_F(red, green, blue, 0.8F);
						tessellator.setNormal(0.0F, 1.0F, 0.0F);
						tessellator.addVertexWithUV(f16 + 0.0F, f4 + f3 - f13, f17 + b0, (f14 + 0.0F) * f10 + f8, (f15 + b0) * f10 + f9);
						tessellator.addVertexWithUV(f16 + b0, f4 + f3 - f13, f17 + b0, (f14 + b0) * f10 + f8, (f15 + b0) * f10 + f9);
						tessellator.addVertexWithUV(f16 + b0, f4 + f3 - f13, f17 + 0.0F, (f14 + b0) * f10 + f8, (f15 + 0.0F) * f10 + f9);
						tessellator.addVertexWithUV(f16 + 0.0F, f4 + f3 - f13, f17 + 0.0F, (f14 + 0.0F) * f10 + f8, (f15 + 0.0F) * f10 + f9);
					}

					tessellator.setColorRGBA_F(red * 0.9F, green * 0.9F, blue * 0.9F, 0.8F);
					int j1;

					if (dx > -1)
					{
						tessellator.setNormal(-1.0F, 0.0F, 0.0F);

						for (j1 = 0; j1 < b0; ++j1)
						{
							tessellator.addVertexWithUV(f16 + j1 + 0.0F, f4 + 0.0F, f17 + b0, (f14 + j1 + 0.5F) * f10 + f8, (f15 + b0) * f10 + f9);
							tessellator.addVertexWithUV(f16 + j1 + 0.0F, f4 + f3, f17 + b0, (f14 + j1 + 0.5F) * f10 + f8, (f15 + b0) * f10 + f9);
							tessellator.addVertexWithUV(f16 + j1 + 0.0F, f4 + f3, f17 + 0.0F, (f14 + j1 + 0.5F) * f10 + f8, (f15 + 0.0F) * f10 + f9);
							tessellator.addVertexWithUV(f16 + j1 + 0.0F, f4 + 0.0F, f17 + 0.0F, (f14 + j1 + 0.5F) * f10 + f8, (f15 + 0.0F) * f10 + f9);
						}
					}

					if (dx <= 1)
					{
						tessellator.setNormal(1.0F, 0.0F, 0.0F);

						for (j1 = 0; j1 < b0; ++j1)
						{
							tessellator.addVertexWithUV(f16 + j1 + 1.0F - f13, f4 + 0.0F, f17 + b0, (f14 + j1 + 0.5F) * f10 + f8, (f15 + b0) * f10 + f9);
							tessellator.addVertexWithUV(f16 + j1 + 1.0F - f13, f4 + f3, f17 + b0, (f14 + j1 + 0.5F) * f10 + f8, (f15 + b0) * f10 + f9);
							tessellator.addVertexWithUV(f16 + j1 + 1.0F - f13, f4 + f3, f17 + 0.0F, (f14 + j1 + 0.5F) * f10 + f8, (f15 + 0.0F) * f10 + f9);
							tessellator.addVertexWithUV(f16 + j1 + 1.0F - f13, f4 + 0.0F, f17 + 0.0F, (f14 + j1 + 0.5F) * f10 + f8, (f15 + 0.0F) * f10 + f9);
						}
					}

					tessellator.setColorRGBA_F(red * 0.8F, green * 0.8F, blue * 0.8F, 0.8F);

					if (dz > -1)
					{
						tessellator.setNormal(0.0F, 0.0F, -1.0F);

						for (j1 = 0; j1 < b0; ++j1)
						{
							tessellator.addVertexWithUV(f16 + 0.0F, f4 + f3, f17 + j1 + 0.0F, (f14 + 0.0F) * f10 + f8, (f15 + j1 + 0.5F) * f10 + f9);
							tessellator.addVertexWithUV(f16 + b0, f4 + f3, f17 + j1 + 0.0F, (f14 + b0) * f10 + f8, (f15 + j1 + 0.5F) * f10 + f9);
							tessellator.addVertexWithUV(f16 + b0, f4 + 0.0F, f17 + j1 + 0.0F, (f14 + b0) * f10 + f8, (f15 + j1 + 0.5F) * f10 + f9);
							tessellator.addVertexWithUV(f16 + 0.0F, f4 + 0.0F, f17 + j1 + 0.0F, (f14 + 0.0F) * f10 + f8, (f15 + j1 + 0.5F) * f10 + f9);
						}
					}

					if (dz <= 1)
					{
						tessellator.setNormal(0.0F, 0.0F, 1.0F);

						for (j1 = 0; j1 < b0; ++j1)
						{
							tessellator.addVertexWithUV(f16 + 0.0F, f4 + f3, f17 + j1 + 1.0F - f13, (f14 + 0.0F) * f10 + f8, (f15 + j1 + 0.5F) * f10 + f9);
							tessellator.addVertexWithUV(f16 + b0, f4 + f3, f17 + j1 + 1.0F - f13, (f14 + b0) * f10 + f8, (f15 + j1 + 0.5F) * f10 + f9);
							tessellator.addVertexWithUV(f16 + b0, f4 + 0.0F, f17 + j1 + 1.0F - f13, (f14 + b0) * f10 + f8, (f15 + j1 + 0.5F) * f10 + f9);
							tessellator.addVertexWithUV(f16 + 0.0F, f4 + 0.0F, f17 + j1 + 1.0F - f13, (f14 + 0.0F) * f10 + f8, (f15 + j1 + 0.5F) * f10 + f9);
						}
					}

					tessellator.draw();
				}
			}
		}

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_CULL_FACE);

	}

}
