/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Rendering;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.client.IRenderHandler;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ChromaSkyRenderer extends IRenderHandler {

	public static final ChromaSkyRenderer instance = new ChromaSkyRenderer();

	private final int[] starColors = {
			0xffffff, 0xC6FFFA, 0xC6E5FF, 0x9BB6FF, 0x7C70FF, 0xFF6868, 0xFFADAD, 0xFFDAAD, 0xFFF0AD
	};

	private final Random rand = new Random();

	public static final int BASE_STARS = 5000;
	public static final int STARS_VARIATION = 2500;

	private final Star[] stars = new Star[BASE_STARS+STARS_VARIATION];
	private final TexturedQuad[] nebulae = new TexturedQuad[16];
	private final TexturedQuad[] planets = new TexturedQuad[32];

	private final ArrayList<Supernova> supernovae = new ArrayList();

	private ChromaSkyRenderer() {
		//rand.setSeed(10842L);
		for (int i = 0; i < stars.length; i++) {

			int texture = rand.nextInt(16);//4+random.nextInt(4);
			int c1 = starColors[rand.nextInt(starColors.length)];

			double tw = ReikaRandomHelper.getRandomBetween(0.125, 4);
			double ta = ReikaRandomHelper.getRandomBetween(0.0625, 0.375);

			stars[i] = Star.createRandomized(c1, texture, tw, ta, rand);
			if (stars[i] != null)
				stars[i].twinkleOffset = i;
		}

		for (int i = 0; i < nebulae.length; i++) {
			nebulae[i] = TexturedQuad.constructRandomized(i, 4, 175, rand);
		}

		for (int i = 0; i < planets.length; i++) {
			planets[i] = TexturedQuad.constructRandomized(i, 2, 30, rand);
		}
	}

	private int getStarCount(double tick) {
		return BASE_STARS+(int)(STARS_VARIATION*Math.sin(tick/24000D));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(float partialTicks, WorldClient world, Minecraft mc) {
		if (supernovae.size() < 30) {
			Supernova s = Supernova.random(rand);
			if (s != null)
				supernovae.add(s);
		}

		GL11.glDisable(GL11.GL_FOG);

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		this.renderBlackscreen();
		GL11.glEnable(GL11.GL_TEXTURE_2D);

		//GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glEnable(GL11.GL_BLEND);
		BlendMode.ADDITIVEDARK.apply();
		GL11.glDepthMask(false);

		double t = System.currentTimeMillis();
		float f = mc.thePlayer.posY <= 18 ? 0 : mc.thePlayer.posY >= 30 ? 1 : (float)((mc.thePlayer.posY-18)/12F);
		if (ReikaEntityHelper.canEntitySeeTheSky(mc.thePlayer))
			f = 1;
		//if (mc.thePlayer.posY > 0 && mc.thePlayer.rotationPitch < -30 && ReikaEntityHelper.canEntitySeeTheSky(mc.thePlayer))
		//	f = Math.min(1, f-mc.thePlayer.rotationPitch/90F);

		GL11.glPushMatrix();
		this.renderStars(t, f);
		GL11.glPopMatrix();

		GL11.glPushMatrix();
		this.renderPlanets(t, f);
		GL11.glPopMatrix();

		BlendMode.DEFAULT.apply();
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDepthMask(true);

		//GL11.glPopAttrib();
	}

	private void renderNebulae(double t, float f) {
		if (f == 0)
			return;
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/stars2.png");

		Tessellator v5 = Tessellator.instance;

		for (int i = 0; i < nebulae.length; i++) {
			TexturedQuad neb = nebulae[i];
			if (neb != null) {
				GL11.glPushMatrix();

				double rx = i*8;
				double ry = (i%4)*32;
				double rz = -i*15+90;

				GL11.glRotated(rx, 1, 0, 0);
				GL11.glRotated(ry, 0, 1, 0);
				GL11.glRotated(rz, 0, 0, 1);

				v5.startDrawingQuads();
				v5.setBrightness(240);
				v5.setColorOpaque_I(ReikaColorAPI.GStoHex((int)(255*f)));

				neb.render(380, 1, t);

				v5.draw();

				GL11.glPopMatrix();
			}

		}
	}

	private void renderPlanets(double t, float f) {
		if (f == 0)
			return;

		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/planets2.png");

		Tessellator v5 = Tessellator.instance;
		for (int k = 0; k < planets.length; k++) {
			TexturedQuad planet = planets[k];
			if (planet != null) {

				int i = k%4;

				GL11.glPushMatrix();

				double rx = k*8+t/4000D%360D+i*90D*Math.signum(k%2-0.5);
				double ry = (i/2)*180+t/16000D%360D-i*30D;
				double rz = (k%4)*60+t/240000D%360D*(1+i%2);

				GL11.glRotated(rx, 1, 0, 0);
				GL11.glRotated(ry, 0, 1, 0);
				GL11.glRotated(rz, 0, 0, 1);

				v5.startDrawingQuads();
				v5.setBrightness(240);
				v5.setColorOpaque_I(ReikaColorAPI.GStoHex((int)(255*f)));

				planet.render(380, 1, t);

				v5.draw();

				GL11.glPopMatrix();
			}
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

	private void renderStars(double t, float f) {
		if (f == 0)
			return;

		double f1 = 0.125;//+0.0625*rand.nextDouble();
		double f2 = 0.125;//+0.0625*rand.nextDouble();
		double f3 = 0.125;//+0.0625*rand.nextDouble();
		GL11.glRotated(RenderManager.renderPosX*f1, 1, 0, 0);
		GL11.glRotated(RenderManager.renderPosY*f2, 0, 1, 0);
		GL11.glRotated(RenderManager.renderPosZ*f3, 0, 0, 1);

		GL11.glRotated(t/12000D%360D, 0, 0, 1);

		this.renderNebulae(t, f);

		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/stars.png");
		int nstar = this.getStarCount(t);

		Tessellator v5 = Tessellator.instance;
		v5.startDrawingQuads();
		v5.setBrightness(240);

		for (int i = 0; i < nstar; i++) {
			Star s = stars[i];
			if (s != null) {
				int dt = nstar-i;

				double dl = i/10D/nstar;
				//200-dl
				s.render(320-dl, Math.min(dt, 24*f), t);
			}
		}
		v5.draw();

		GL11.glColor4f(1, 1, 1, 1);
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/supernova.png");
		v5.startDrawingQuads();
		v5.setBrightness(240);
		Iterator<Supernova> it = supernovae.iterator();
		while (it.hasNext()) {
			Supernova s = it.next();
			s.render(240, 24, t);
			if (s.frame >= s.getMaxFrame())
				it.remove();
		}

		v5.draw();
	}

	private static class Supernova extends Star {

		private int ticksToHoldMiddle = 0; //frame 48
		private int frame;
		private int animationSpeed = 1;

		private Supernova(int c, double s, double d0, double d1, double d2, double d3, double d9, double d10, double d12, double d13, double d16) {
			super(c, 0, s, 0, 0, d0, d1, d2, d3, d9, d10, d12, d13, d16);
		}

		public static Supernova random(Random rand) {
			Star s = Star.createRandomized(0, 0, 0, 0, rand);
			if (s == null)
				return null;
			int c = 0xffffff;
			switch(rand.nextInt(5)) {
				case 0:
					c = 0x7CD5FF;
					break;
				case 1:
					c = 0xAC7CFF;
					break;
				case 2:
					c = 0xFFF47C;
					break;
				case 3:
					c = 0x7CFFF4;
					break;
			}
			Supernova ret = new Supernova(c, ReikaRandomHelper.getRandomBetween(18D, 24D, rand), s.d0, s.d1, s.d2, s.d3, s.d9, s.d10, s.d12, s.d13, s.d16);
			ret.animationSpeed = ReikaRandomHelper.getRandomBetween(1, 4, rand);
			ret.ticksToHoldMiddle = rand.nextInt(20);
			return ret;
		}

		@Override
		protected void render(double d, float brightness, double time) {
			/*
			double d5 = d0 * d;
			double d6 = d1 * d;
			double d7 = d2 * d;
			double u = (frame%12)/12D;
			double v = (frame/12)/10D;
			double du = u+1/12D;
			double dv = v+1/10D;
			Tessellator.instance.setColorOpaque_I(color);
			Tessellator.instance.addVertexWithUV(d5, d6, d7, u, v);
			Tessellator.instance.addVertexWithUV(d5+size, d6, d7, du, v);
			Tessellator.instance.addVertexWithUV(d5+size, d6+size, d7, du, dv);
			Tessellator.instance.addVertexWithUV(d5, d6+size, d7, u, dv);
			 */

			double u = (frame/animationSpeed%12)/12D;
			double v = (frame/animationSpeed/12)/10D;

			texU[0] = u;
			texU[1] = texU[0];
			texU[2] = u+1/12D;
			texU[3] = texU[2];

			texV[0] = v;
			texV[3] = texV[0];
			texV[1] = v+1/10D;
			texV[2] = texV[1];

			super.render(d, brightness, time);

			if (frame == 48*animationSpeed && ticksToHoldMiddle > 0)
				ticksToHoldMiddle--;
			else if (frame < this.getMaxFrame())
				frame++;
		}

		protected int getMaxFrame() {
			return 120*animationSpeed-1;
		}

	}

	private static class Star extends TexturedQuad {

		public final int color;
		protected final double twinkleSpeed;
		protected final double twinkleAmplitude;

		private double twinkleOffset;

		private Star(int c, int tex, double s, double tw, double ta, double d0, double d1, double d2, double d3, double d9, double d10, double d12, double d13, double d16) {
			super(tex, 4, s, d0, d1, d2, d3, d9, d10, d12, d13, d16);
			color = c;
			twinkleSpeed = tw;
			twinkleAmplitude = ta;
		}

		private static Star createRandomized(int c, int tex, double tw, double ta, Random rand) {
			double d0 = rand.nextFloat() * 2.0F - 1.0F;
			double d1 = rand.nextFloat() * 2.0F - 1.0F;
			double d2 = rand.nextFloat() * 2.0F - 1.0F;
			double d3 = 0.15F + rand.nextFloat() * 0.1F;
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
				double d14 = rand.nextDouble() * Math.PI * 2.0D;
				double d16 = Math.cos(d14);
				double size = 1+Math.sin(d14)*(rand.nextDouble()*4);

				return new Star(c, tex, size*12, tw, ta, d0, d1, d2, d3, d9, d10, d12, d13, d16);
			}
			return null;
		}

		@Override
		protected void render(double d, float brightness, double time) {
			if (brightness <= 0)
				return;
			double td = 500D;//+500*Math.sin((time/100000D)%360);
			double tb = 1-twinkleAmplitude;
			int c2 = ReikaColorAPI.getColorWithBrightnessMultiplier(color, (float)(tb+twinkleAmplitude*Math.sin(twinkleOffset+time*twinkleSpeed/td)));
			int c = brightness >= 24 ? c2 : ReikaColorAPI.getColorWithBrightnessMultiplier(c2, brightness/24F);
			Tessellator.instance.setColorOpaque_I(c);

			super.render(d, brightness, time);
		}
	}

	private static class TexturedQuad {

		public final int textureIndex;
		public final double size;

		protected final double[] texU;
		protected final double[] texV;

		protected final double d0;
		protected final double d1;
		protected final double d2;
		protected final double d3;
		protected final double d9;
		protected final double d10;
		protected final double d12;
		protected final double d13;
		protected final double d16;

		private TexturedQuad(int tex, int rowWidth, double s, double d0, double d1, double d2, double d3, double d9, double d10, double d12, double d13, double d16) {
			textureIndex = tex;
			size = s;

			int ttex = rowWidth*rowWidth;
			tex = ((tex%ttex)+ttex)%ttex;

			double du = (textureIndex%rowWidth)/(double)rowWidth;
			double dv = (textureIndex/rowWidth)/(double)rowWidth;

			texU = new double[]{du, du, du+1D/rowWidth, du+1D/rowWidth};
			texV = new double[]{dv, dv+1D/rowWidth, dv+1D/rowWidth, dv};

			this.d0 = d0;
			this.d1 = d1;
			this.d2 = d2;
			this.d3 = d3;
			this.d9 = d9;
			this.d10 = d10;
			this.d12 = d12;
			this.d13 = d13;
			this.d16 = d16;
		}

		private static TexturedQuad constructRandomized(int tex, int row, double s, Random rand) {
			double d0 = rand.nextFloat() * 2.0F - 1.0F;
			double d1 = rand.nextFloat() * 2.0F - 1.0F;
			double d2 = rand.nextFloat() * 2.0F - 1.0F;
			double d3 = 0.15F + rand.nextFloat() * 0.1F;
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
				double d14 = rand.nextDouble() * Math.PI * 2.0D;
				double d16 = Math.cos(d14);

				return new TexturedQuad(tex, row, s, d0, d1, d2, d3, d9, d10, d12, d13, d16);
			}
			return null;
		}

		protected void render(double d, float brightness, double time) {
			//double d0 = random.nextFloat() * 2.0F - 1.0F;
			//double d1 = random.nextFloat() * 2.0F - 1.0F;
			//double d2 = random.nextFloat() * 2.0F - 1.0F;

			double d5 = d0 * d;
			double d6 = d1 * d;
			double d7 = d2 * d;

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
				Tessellator.instance.addVertexWithUV(d5 + d24, d6 + d22, d7 + d25, texU[j], texV[j]);
			}
		}
	}

}
