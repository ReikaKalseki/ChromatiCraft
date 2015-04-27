/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.client.IRenderHandler;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ChromaSkyRenderer extends IRenderHandler {

	public static final ChromaSkyRenderer instance = new ChromaSkyRenderer();

	private final int[] starColors = {
			0xffffff, 0xC6FFFA, 0xC6E5FF, 0x9BB6FF, 0x7C70FF, 0xFF6868, 0xFFADAD, 0xFFDAAD, 0xFFF0AD
	};

	private ChromaSkyRenderer() {

	}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(float partialTicks, WorldClient world, Minecraft mc) {
		GL11.glDisable(GL11.GL_FOG);

		this.renderBlackscreen();

		GL11.glEnable(GL11.GL_BLEND);
		BlendMode.ADDITIVEDARK.apply();

		GL11.glPushMatrix();
		this.renderStars();
		GL11.glPopMatrix();

		GL11.glPushMatrix();
		this.renderPlanets();
		GL11.glPopMatrix();

		BlendMode.DEFAULT.apply();
		GL11.glDisable(GL11.GL_BLEND);
	}

	private void renderBrightStars() {
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/stars2.png");
		Random random = new Random(10842L);
		for (int i = 0; i < 16; i++) {

			GL11.glPushMatrix();

			double rx = i*8;
			double ry = (i%4)*32;
			double rz = -i*15+90;

			GL11.glRotated(rx, 1, 0, 0);
			GL11.glRotated(ry, 0, 1, 0);
			GL11.glRotated(rz, 0, 0, 1);

			double d0 = random.nextFloat() * 2.0F - 1.0F;
			double d1 = random.nextFloat() * 2.0F - 1.0F;
			double d2 = random.nextFloat() * 2.0F - 1.0F;
			double d3 = 0.15F + random.nextFloat() * 0.1F;
			double d4 = d0 * d0 + d1 * d1 + d2 * d2;

			double size = 175;

			if (d4 < 1.0D && d4 > 0.01D) {
				d4 = 1.0D / Math.sqrt(d4);
				d0 *= d4;
				d1 *= d4;
				d2 *= d4;
				double d8 = Math.atan2(d0, d2);
				double d9 = Math.sin(d8);
				double d10 = Math.cos(d8);
				double d11 = Math.atan2(Math.sqrt(d0 * d0 + d2 * d2), d1);
				double d12 = Math.sin(d11);
				double d13 = Math.cos(d11);
				double d14 = random.nextDouble() * Math.PI * 2.0D;
				double d16 = Math.cos(d14);

				Tessellator v5 = Tessellator.instance;

				v5.startDrawing(GL11.GL_QUADS);
				v5.setBrightness(240);
				v5.setColorOpaque_I(0xffffff);

				double d = 380;

				double d5 = d0 * d;
				double d6 = d1 * d;
				double d7 = d2 * d;

				double du = (i%4)/4D;
				double dv = (i/4)/4D;

				double[] u = {du, du, du+0.25, du+0.25};
				double[] v = {dv, dv+0.25, dv+0.25, dv};

				for (int j = 0; j < 4; j++) {
					double d17 = 0.0D;
					double d18 = ((j & 2) - 1) * d3;
					double d19 = ((j + 1 & 2) - 1) * d3;


					double d20 = d18 * d16 - d19 * size;
					double d21 = d19 * d16 + d18 * size;
					double d22 = d20 * d12 + d17 * d13;
					double d23 = d17 * d12 - d20 * d13;
					double d24 = d23 * d9 - d21 * d10;
					double d25 = d21 * d9 + d23 * d10;
					v5.addVertexWithUV(d5 + d24, d6 + d22, d7 + d25, u[j], v[j]);
				}

				v5.draw();

			}

			GL11.glPopMatrix();

		}
	}

	private void renderPlanets() {
		Random random = new Random(245434L);

		double f1 = 1+0.125*random.nextDouble();
		double f2 = 1+0.125*random.nextDouble();
		double f3 = 1+0.125*random.nextDouble();

		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/planets2.png");

		for (int k = 0; k < 32; k++) {

			int i = k%4;

			GL11.glPushMatrix();

			double rx = k*8+System.currentTimeMillis()/4000D%360D+i*90D*Math.signum(k%2-0.5);
			double ry = (i/2)*180+System.currentTimeMillis()/16000D%360D-i*30D;
			double rz = (k%4)*60+System.currentTimeMillis()/240000D%360D*(1+i%2);

			GL11.glRotated(rx*f1, 1, 0, 0);
			GL11.glRotated(ry*f2, 0, 1, 0);
			GL11.glRotated(rz*f3, 0, 0, 1);

			double d0 = random.nextFloat() * 2.0F - 1.0F;
			double d1 = random.nextFloat() * 2.0F - 1.0F;
			double d2 = random.nextFloat() * 2.0F - 1.0F;
			double d3 = 0.15F + random.nextFloat() * 0.1F;
			double d4 = d0 * d0 + d1 * d1 + d2 * d2;

			double size = 30;

			if (d4 < 1.0D && d4 > 0.01D) {
				d4 = 1.0D / Math.sqrt(d4);
				d0 *= d4;
				d1 *= d4;
				d2 *= d4;
				double d8 = Math.atan2(d0, d2);
				double d9 = Math.sin(d8);
				double d10 = Math.cos(d8);
				double d11 = Math.atan2(Math.sqrt(d0 * d0 + d2 * d2), d1);
				double d12 = Math.sin(d11);
				double d13 = Math.cos(d11);
				double d14 = random.nextDouble() * Math.PI * 2.0D;
				double d16 = Math.cos(d14);

				Tessellator v5 = Tessellator.instance;

				v5.startDrawing(GL11.GL_QUADS);
				v5.setBrightness(240);
				v5.setColorOpaque_I(0xffffff);

				double d = 380;

				double d5 = d0 * d;
				double d6 = d1 * d;
				double d7 = d2 * d;

				double du = (i%2)/2D;
				double dv = (i/2)/2D;

				double[] u = {du, du, du+0.5, du+0.5};
				double[] v = {dv, dv+0.5, dv+0.5, dv};

				for (int j = 0; j < 4; j++) {
					double d17 = 0.0D;
					double d18 = ((j & 2) - 1) * d3;
					double d19 = ((j + 1 & 2) - 1) * d3;


					double d20 = d18 * d16 - d19 * size;
					double d21 = d19 * d16 + d18 * size;
					double d22 = d20 * d12 + d17 * d13;
					double d23 = d17 * d12 - d20 * d13;
					double d24 = d23 * d9 - d21 * d10;
					double d25 = d21 * d9 + d23 * d10;
					v5.addVertexWithUV(d5 + d24, d6 + d22, d7 + d25, u[j], v[j]);
				}

				v5.draw();

			}

			GL11.glPopMatrix();
		}
	}

	private void renderBlackscreen() {
		Tessellator v5 = Tessellator.instance;
		for (int i = 0; i < 6; i++) {

			double d = 450;

			double u = 0;
			double v = 0;

			double du = 1-u;
			double dv = 1-v;

			GL11.glPushMatrix();

			switch(i) {
			case 1:
				GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
				break;
			case 2:
				GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
				break;
			case 3:
				GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
				break;
			case 4:
				GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
				break;
			case 5:
				GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
				break;
			}

			GL11.glColor4f(0, 0, 0, 1);
			v5.startDrawingQuads();
			v5.setColorOpaque_I(0);
			v5.addVertexWithUV(-d, -d, -d, u, v);
			v5.addVertexWithUV(-d, -d, d, u, dv);
			v5.addVertexWithUV(d, -d, d, du, dv);
			v5.addVertexWithUV(d, -d, -d, du, v);
			v5.draw();

			GL11.glPopMatrix();
		}
	}

	private void renderStars() {
		Random random = new Random(10842L);

		double f1 = 0.125+0.0625*random.nextDouble();
		double f2 = 0.125+0.0625*random.nextDouble();
		double f3 = 0.125+0.0625*random.nextDouble();
		GL11.glRotated(RenderManager.renderPosX*f1, 1, 0, 0);
		GL11.glRotated(RenderManager.renderPosY*f2, 0, 1, 0);
		GL11.glRotated(RenderManager.renderPosZ*f3, 0, 0, 1);

		GL11.glRotated(System.currentTimeMillis()/12000D%360D, 0, 0, 1);

		this.renderBrightStars();

		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/stars.png");

		int nstar = 5000+(int)(2500*Math.sin(System.currentTimeMillis()/24000D));

		for (int i = 0; i < nstar; i++) {
			double d0 = random.nextFloat() * 2.0F - 1.0F;
			double d1 = random.nextFloat() * 2.0F - 1.0F;
			double d2 = random.nextFloat() * 2.0F - 1.0F;
			double d3 = 0.15F + random.nextFloat() * 0.1F;
			double d4 = d0 * d0 + d1 * d1 + d2 * d2;

			if (d4 < 1.0D && d4 > 0.01D) {
				d4 = 1.0D / Math.sqrt(d4);
				d0 *= d4;
				d1 *= d4;
				d2 *= d4;
				double d8 = Math.atan2(d0, d2);
				double d9 = Math.sin(d8);
				double d10 = Math.cos(d8);
				double d11 = Math.atan2(Math.sqrt(d0 * d0 + d2 * d2), d1);
				double d12 = Math.sin(d11);
				double d13 = Math.cos(d11);
				double d14 = random.nextDouble() * Math.PI * 2.0D;
				double size = 1+Math.sin(d14)*(random.nextDouble()*4);
				double d16 = Math.cos(d14);

				int n = random.nextInt(16);//4+random.nextInt(4);

				int dt = nstar-i;
				int c1 = starColors[random.nextInt(starColors.length)];
				int c = dt >= 4 ? c1 : ReikaColorAPI.getColorWithBrightnessMultiplier(c1, dt/4F);

				//this.renderStar(random, 0x080808, 220, size*4, n, d0, d1, d2, d3, d9, d10, d12, d13, d16);
				//this.renderStar(random, 0x555555, 210, size*2, n, d0, d1, d2, d3, d9, d10, d12, d13, d16);
				//this.renderStar(random, 0xffffff, 200, size, n, d0, d1, d2, d3, d9, d10, d12, d13, d16);

				double dl = i/10D/nstar;
				//200-dl
				this.renderStar(random, c, 320-dl, size*4*3, n, d0, d1, d2, d3, d9, d10, d12, d13, d16);

			}
		}
	}

	private void renderStar(Random random, int c, double d, double size, int n, double d0, double d1, double d2, double d3, double d9, double d10, double d12, double d13, double d16) {

		Tessellator v5 = Tessellator.instance;

		v5.startDrawing(GL11.GL_QUADS);
		v5.setBrightness(240);
		v5.setColorOpaque_I(c);

		//double d0 = random.nextFloat() * 2.0F - 1.0F;
		//double d1 = random.nextFloat() * 2.0F - 1.0F;
		//double d2 = random.nextFloat() * 2.0F - 1.0F;

		double d5 = d0 * d;
		double d6 = d1 * d;
		double d7 = d2 * d;

		double du = (n%4)/4D;
		double dv = (n/4)/4D;

		double[] u = {du, du, du+0.25, du+0.25};
		double[] v = {dv, dv+0.25, dv+0.25, dv};

		for (int j = 0; j < 4; j++) {
			double d17 = 0.0D;
			double d18 = ((j & 2) - 1) * d3;
			double d19 = ((j + 1 & 2) - 1) * d3;


			double d20 = d18 * d16 - d19 * size;
			double d21 = d19 * d16 + d18 * size;
			double d22 = d20 * d12 + d17 * d13;
			double d23 = d17 * d12 - d20 * d13;
			double d24 = d23 * d9 - d21 * d10;
			double d25 = d21 * d9 + d23 * d10;
			v5.addVertexWithUV(d5 + d24, d6 + d22, d7 + d25, u[j], v[j]);
		}

		v5.draw();
	}

}
