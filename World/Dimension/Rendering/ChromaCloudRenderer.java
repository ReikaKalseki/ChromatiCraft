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

import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.client.MinecraftForgeClient;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaShaders;
import Reika.ChromatiCraft.World.Dimension.ChromaDimensionManager;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.IO.RemoteSourcedAsset;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ChromaCloudRenderer extends IRenderHandler {

	public static final ChromaCloudRenderer instance = new ChromaCloudRenderer();

	private static final int FOG_STAR_SECTIONS = 8;

	private final FogStarBrightness[][] fogStarBrightness = new FogStarBrightness[1+FOG_STAR_SECTIONS][1+FOG_STAR_SECTIONS];

	private final RemoteSourcedAsset[] skyTex = new RemoteSourcedAsset[4];

	private ChromaCloudRenderer() {
		for (int i = 0; i < fogStarBrightness.length; i++) {
			for (int k = 0; k < fogStarBrightness.length; k++) {
				fogStarBrightness[i][k] = new FogStarBrightness(DragonAPICore.rand);
			}
		}

		for (int i = 0; i < 4; i++) {
			skyTex[i] = new RemoteSourcedAsset(ChromatiCraft.class, "Textures/clouds/dimsky_"+i+".png", "https://github.com/ReikaKalseki/ChromatiCraft/tree/master/Textures/clouds", "Reika/ChromatiCraft/TextureDL");
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(float ptick, WorldClient world, Minecraft mc) {
		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_FOG);
		GL11.glDepthMask(false);

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
		//GL11.glDisable(GL11.GL_DEPTH_TEST);

		int r = 2;
		for (int a = -r; a <= r; a++) {
			for (int b = -r; b <= r; b++) {
				for (int i = 0; i < 4; i++) {
					ReikaTextureHelper.bindTexture(skyTex[i]);

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

		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

	public static void drawVoidFog(EntityPlayer ep) {
		if (MinecraftForgeClient.getRenderPass() != 0)
			return;

		if (ep.posY > 80)
			return;

		if (ep.posY < 20 && ChromaDimensionManager.getStructurePlayerIsIn(ep) == null) {
			ChromaShaders.DIMFLOOR.refresh();
			ChromaShaders.DIMFLOOR.rampUpIntensity(0.05F, 1.1F);
			float f = ep.posY <= 10 ? 1 : 1-(float)((ep.posY-10)/10);
			ChromaShaders.DIMFLOOR.setIntensity(Math.min(ChromaShaders.DIMFLOOR.getIntensity(), f));
			ChromaShaders.DIMFLOOR.lingerTime = 20;
			ChromaShaders.DIMFLOOR.rampDownAmount = 0.001F;
			ChromaShaders.DIMFLOOR.rampDownFactor = 0.999F;
			float f2 = 0;
			if (ep.posY <= 1)
				f2 = 1;
			else if (ep.posY <= 3.5)
				f2 = 1-(float)((ep.posY-1)/2.5);
			ChromaShaders.DIMFLOOR.getShader().setField("starFactor", f2);
		}

		Tessellator v5 = Tessellator.instance;

		float f = ReikaRenderHelper.getPartialTickTime();

		double ox = ep.posX+(ep.posX-ep.lastTickPosX)*f;
		double oy = ep.posY+(ep.posY-ep.lastTickPosY)*f;
		double oz = ep.posZ+(ep.posZ-ep.lastTickPosZ)*f;

		double h0 = 0;
		if (oy < 0) {
			h0 += oy;
		}

		double w = 512;

		int color = ReikaColorAPI.getModifiedHue(0xff0000, 190+(int)(10*Math.sin((ep.ticksExisted+f)/83F)));
		color = ReikaColorAPI.getModifiedSat(color, 0.25F+(float)(0.125*Math.cos((ep.ticksExisted+f)/57F)));

		double r = w*2;
		double tx = ox%r/r;
		double tz = oz%r/r;

		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		//GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		//
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_CULL_FACE);
		ReikaRenderHelper.disableEntityLighting();
		GL11.glDepthMask(false);

		GL11.glTranslated(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ);

		double t = ep.ticksExisted+f;

		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/clouds/dimfog.png");

		v5.startDrawingQuads();

		double h1 = 0.125+0.0625*Math.cos(t/71D);
		double h = 0.05;
		float a = 1F;
		double dx = 0;
		double dz = 0;
		double maxh = h0;
		while (a > 0.125) {
			h += h1+h/3D;
			//v5.setColorOpaque_I(ReikaColorAPI.getColorWithBrightnessMultiplier(color, a));
			v5.setColorRGBA_I(color, (int)(a*255));
			v5.addVertexWithUV(ox+dx-w, h0+h, oz+dz-w, tx, tz);
			v5.addVertexWithUV(ox+dx+w, h0+h, oz+dz-w, tx+6, tz);
			v5.addVertexWithUV(ox+dx+w, h0+h, oz+dz+w, tx+6, tz+6);
			v5.addVertexWithUV(ox+dx-w, h0+h, oz+dz+w, tx, tz+6);
			dx += Math.sin(ep.hashCode()/117D+h*11+t/47D);
			dz += Math.sin(ep.hashCode()/153D+h*13+t/53D);
			a *= 0.75;
			maxh = h0+h;
		}

		if (oy < 2) {
			a = Math.min(1, 1-(float)(oy/2F));
			dx = 0;
			dz = 0;
			//v5.setColorOpaque_I(ReikaColorAPI.getColorWithBrightnessMultiplier(color, a));
			v5.setColorRGBA_I(color, (int)(a*255));
			v5.addVertexWithUV(ox+dx-w, oy-4, oz+dz-w, tx, tz);
			v5.addVertexWithUV(ox+dx+w, oy-4, oz+dz-w, tx+3, tz);
			v5.addVertexWithUV(ox+dx+w, oy-4, oz+dz+w, tx+3, tz+3);
			v5.addVertexWithUV(ox+dx-w, oy-4, oz+dz+w, tx, tz+3);
		}

		v5.draw();

		BlendMode.ADDITIVEDARK.apply();

		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/clouds/dimfog-stars.png");
		v5.startDrawingQuads();
		//v5.setColorOpaque_I(ReikaColorAPI.getColorWithBrightnessMultiplier(color, a));
		v5.setColorOpaque_I(ReikaColorAPI.mixColors(color, 0xffffff, 0.5F));
		double x0 = ox-w;
		double z0 = oz-w;
		double sz = w*2/FOG_STAR_SECTIONS;
		double dt = 12;
		double st = dt/FOG_STAR_SECTIONS;
		double d = 36;
		double y = Math.min(h0+0.05, oy-d);//h0+0.05;//maxh;//oy-d;
		for (int i = 0; i < FOG_STAR_SECTIONS; i++) {
			for (int k = 0; k < FOG_STAR_SECTIONS; k++) {
				double x1 = x0+i*sz;
				double z1 = z0+k*sz;
				double u = tx+0.35+st*i;
				double v = tz+0.5+st*k;
				/*
				double b1 = 0.5+0.5*Math.sin((i+k*FOG_STAR_SECTIONS)*5.3D+t/17D);
				double b2 = 0.5+0.5*Math.sin((i+1+k*FOG_STAR_SECTIONS)*5.3D+t/17D);
				double b3 = 0.5+0.5*Math.sin((i+1+(k+1)*FOG_STAR_SECTIONS)*5.3D+t/17D);
				double b4 = 0.5+0.5*Math.sin((i+(k+1)*FOG_STAR_SECTIONS)*5.3D+t/17D);
				 */
				double b1 = instance.fogStarBrightness[i][k].getBrightness(t);
				double b2 = instance.fogStarBrightness[i+1][k].getBrightness(t);
				double b3 = instance.fogStarBrightness[i+1][k+1].getBrightness(t);
				double b4 = instance.fogStarBrightness[i][k+1].getBrightness(t);
				v5.setColorOpaque_I(ReikaColorAPI.GStoHex((int)(255*b1)));
				v5.addVertexWithUV(x1, 		y, z1, 		u, v);
				v5.setColorOpaque_I(ReikaColorAPI.GStoHex((int)(255*b2)));
				v5.addVertexWithUV(x1+sz, 	y, z1, 		u+st, v);
				v5.setColorOpaque_I(ReikaColorAPI.GStoHex((int)(255*b3)));
				v5.addVertexWithUV(x1+sz, 	y, z1+sz, 	u+st, v+st);
				v5.setColorOpaque_I(ReikaColorAPI.GStoHex((int)(255*b4)));
				v5.addVertexWithUV(x1, 		y, z1+sz, 	u, v+st);
			}
		}
		v5.draw();

		ReikaRenderHelper.enableEntityLighting();
		BlendMode.DEFAULT.apply();
		GL11.glPopAttrib();
		GL11.glPopMatrix();
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

	private static class FogStarBrightness {

		private final double period;
		private final double offset;

		private FogStarBrightness(Random rand) {
			this(8+rand.nextDouble()*18, rand.nextDouble()*Math.PI*2);
		}

		private FogStarBrightness(double p, double o) {
			period = 1D/p;
			offset = o;
		}

		public double getBrightness(double t) {
			return 0.5+0.5*Math.sin(offset+t*period);
		}

	}

}
