/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.Instantiable.SimplexNoiseGenerator;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;


public class CliffFogRenderer_Noise {

	public static final CliffFogRenderer_Noise instance = new CliffFogRenderer_Noise();

	private static final int RESOLUTION = 8;

	private static final int HEIGHTVAR = 15;
	private static final int BLOCKSIZE = 1*RESOLUTION;

	private static final int MINHEIGHT = 4;
	private static final int MAXHEIGHT = 12;

	private final SimplexNoiseGenerator cloudNoiseA = new SimplexNoiseGenerator(System.currentTimeMillis()).addOctave(2, 0.5).addOctave(4, 0.125).addOctave(8, 0.125).setFrequency(1/24D*RESOLUTION);
	private final SimplexNoiseGenerator cloudNoiseB = new SimplexNoiseGenerator(System.currentTimeMillis()*2).addOctave(2, 0.5).addOctave(4, 0.125).addOctave(8, 0.125).setFrequency(1/24D*RESOLUTION);

	private final SimplexNoiseGenerator cloudHeight = new SimplexNoiseGenerator(-System.currentTimeMillis()).addOctave(2, 0.25).setFrequency(1/80D*RESOLUTION);
	private final SimplexNoiseGenerator cloudThickness = new SimplexNoiseGenerator(~System.currentTimeMillis()).setFrequency(1/12D*RESOLUTION);

	private CloudVertex[][] clouds;

	private final ArrayList<Integer> layers = new ArrayList();

	private CliffFogRenderer_Noise() {
		layers.add(80);
		layers.add(102);
		layers.add(144);
		layers.add(192);
		layers.add(240);

		cloudNoiseA.clampEdge = true;
		cloudNoiseB.clampEdge = true;
	}

	public void initialize() {
		int size = this.getArraySize();

		float[][] cloudData = new float[size][size];
		float[][] heightData = new float[size][size];
		float[][] thickData = new float[size][size];

		clouds = new CloudVertex[size][size];

		for (int i = 0; i < size; i++) {
			for (int k = 0; k < size; k++) {
				float f = (float)(Math.max(0, cloudNoiseA.getValue(i, k))*Math.max(0, cloudNoiseB.getValue(i, k)));
				cloudData[i][k] = f > 0 ? f : 0;
				heightData[i][k] = (float)(cloudHeight.getValue(i, k)*HEIGHTVAR);
				thickData[i][k] = f > 0 ? (float)(f*ReikaMathLibrary.normalizeToBounds(cloudThickness.getValue(i, k), MINHEIGHT, MAXHEIGHT)) : 0;
			}
		}

		//blend edges for wrap
		for (int i = 0; i < size; i++) {
			float f1 = cloudData[0][i];
			float f2 = cloudData[size-1][i];
			if (f1 > 0 || f2 > 0) {
				cloudData[0][i] = f1*0.67F+f2*0.33F;
				cloudData[size-1][i] = f2*0.67F+f1*0.33F;

				f1 = heightData[0][i];
				f2 = heightData[size-1][i];
				heightData[0][i] = f1*0.5F+f2*0.5F;
				heightData[size-1][i] = f2*0.5F+f1*0.5F;

				f1 = thickData[0][i];
				f2 = thickData[size-1][i];
				thickData[0][i] = f1*0.5F+f2*0.5F;
				thickData[size-1][i] = f2*0.5F+f1*0.5F;
			}

			f1 = cloudData[i][0];
			f2 = cloudData[i][size-1];
			if (f1 > 0 || f2 > 0) {
				cloudData[i][0] = f1*0.67F+f2*0.33F;
				cloudData[i][size-1] = f2*0.67F+f1*0.33F;

				f1 = heightData[i][0];
				f2 = heightData[i][size-1];
				heightData[i][0] = f1*0.5F+f2*0.5F;
				heightData[i][size-1] = f2*0.5F+f1*0.5F;

				f1 = thickData[i][0];
				f2 = thickData[i][size-1];
				thickData[i][0] = f1*0.5F+f2*0.5F;
				thickData[i][size-1] = f2*0.5F+f1*0.5F;
			}
		}

		for (int i = 0; i < size; i++) {
			for (int k = 0; k < size; k++) {
				clouds[i][k] = new CloudVertex(i-size/2, k-size/2, heightData[i][k], cloudData[i][k], thickData[i][k]);
			}
		}
	}

	private int getArraySize() {
		return 256/RESOLUTION;
	}

	public void render() {
		if (MinecraftForgeClient.getRenderPass() != 1)
			return;

		float f = ReikaRenderHelper.getPartialTickTime();

		EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
		double x = ep.posX+(ep.posX-ep.lastTickPosX)*f;
		double y = ep.posY+(ep.posY-ep.lastTickPosY)*f;
		double z = ep.posZ+(ep.posZ-ep.lastTickPosZ)*f;

		int px = MathHelper.floor_double(x);
		int py = MathHelper.floor_double(y);
		int pz = MathHelper.floor_double(z);
		BiomeGenBase b = ep.worldObj.getBiomeGenForCoords(px, pz);

		if (b != ChromatiCraft.glowingcliffs)
			return;

		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glEnable(GL11.GL_BLEND);
		//GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		//BlendMode.DEFAULT.apply();
		BlendMode.ADDITIVEDARK.apply();
		GL11.glDisable(GL11.GL_LIGHTING);
		ReikaRenderHelper.disableEntityLighting();
		GL11.glDepthMask(false);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glColor4f(1, 1, 1, 1);

		GL11.glTranslated(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ);

		int ox = ReikaMathLibrary.roundDownToX(256, px);
		int oz = ReikaMathLibrary.roundDownToX(256, pz);

		int x2 = px-ox > 256/2 ? ox+256 : ox-256;
		int z2 = pz-oz > 256/2 ? oz+256 : oz-256;

		this.renderRegion(Tessellator.instance, ox, oz);
		this.renderRegion(Tessellator.instance, x2, oz);
		this.renderRegion(Tessellator.instance, ox, z2);
		this.renderRegion(Tessellator.instance, x2, z2);

		//ReikaJavaLibrary.pConsole(ox+", "+oz+" @ "+x+", "+z+" > "+x2+", "+z2);

		ReikaRenderHelper.enableEntityLighting();
		GL11.glShadeModel(GL11.GL_FLAT);
		BlendMode.DEFAULT.apply();
		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

	private void renderRegion(Tessellator v5, int x, int z) {
		GL11.glPushMatrix();
		GL11.glTranslated(x, 0, z);

		v5.startDrawingQuads();

		v5.setNormal(0, 1, 0);
		v5.setBrightness(240);

		for (int layer : layers) {
			this.renderLayer(layer, v5);
		}

		v5.draw();
		GL11.glPopMatrix();
	}

	private void renderLayer(int layer, Tessellator v5) {

		double a = System.currentTimeMillis()*(1+layer/192D)/90000D%(2*Math.PI)+layer/80D;
		double ax = clouds.length*0.5*Math.cos(a);
		double az = clouds.length*0.5*Math.sin(a);
		//GL11.glTranslated(ax, 0, az);
		v5.addTranslation((float)ax, 0, (float)az);

		for (int i = 0; i < clouds.length; i++) {
			for (int k = 0; k < clouds.length; k++) {
				this.renderVertex(v5, 0, layer, 0, i, k);
			}
		}

		v5.addTranslation(-(float)ax, 0, -(float)az);
	}

	private void renderVertex(Tessellator v5, double x, double y, double z, int i, int k) {
		int pi = i == clouds.length-1 ? 0 : i+1;
		int pk = k == clouds.length-1 ? 0 : k+1;

		CloudVertex c7 = clouds[i][k];
		CloudVertex c9 = clouds[pi][k];
		CloudVertex c1 = clouds[i][pk];
		CloudVertex c3 = clouds[pi][pk];

		if (c7.value == 0 && c9.value == 0 && c1.value == 0 && c3.value == 0)
			return;

		v5.setColorOpaque_I(c7.value > 0 ? ReikaColorAPI.getColorWithBrightnessMultiplier(0xffffff, c7.value) : 0);
		v5.addVertex(x+this.offset(i),		y+c7.heightOffset-c7.value*c7.thickness, 	z+this.offset(k));

		v5.setColorOpaque_I(c9.value > 0 ? ReikaColorAPI.getColorWithBrightnessMultiplier(0xffffff, c9.value) : 0);
		v5.addVertex(x+this.offset(pi == 0 ? clouds.length : pi),		y+c9.heightOffset-c9.value*c9.thickness, 	z+this.offset(k));

		v5.setColorOpaque_I(c3.value > 0 ? ReikaColorAPI.getColorWithBrightnessMultiplier(0xffffff, c3.value) : 0);
		v5.addVertex(x+this.offset(pi == 0 ? clouds.length : pi),		y+c3.heightOffset-c3.value*c3.thickness, 	z+this.offset(pk == 0 ? clouds.length : pk));

		v5.setColorOpaque_I(c1.value > 0 ? ReikaColorAPI.getColorWithBrightnessMultiplier(0xffffff, c1.value) : 0);
		v5.addVertex(x+this.offset(i),		y+c1.heightOffset-c1.value*c1.thickness, 	z+this.offset(pk == 0 ? clouds.length : pk));


		v5.setColorOpaque_I(c1.value > 0 ? ReikaColorAPI.getColorWithBrightnessMultiplier(0xffffff, c1.value) : 0);
		v5.addVertex(x+this.offset(i),		y+c1.heightOffset+c1.value*c1.thickness, 	z+this.offset(pk == 0 ? clouds.length : pk));

		v5.setColorOpaque_I(c3.value > 0 ? ReikaColorAPI.getColorWithBrightnessMultiplier(0xffffff, c3.value) : 0);
		v5.addVertex(x+this.offset(pi == 0 ? clouds.length : pi),		y+c3.heightOffset+c3.value*c3.thickness, 	z+this.offset(pk == 0 ? clouds.length : pk));

		v5.setColorOpaque_I(c9.value > 0 ? ReikaColorAPI.getColorWithBrightnessMultiplier(0xffffff, c9.value) : 0);
		v5.addVertex(x+this.offset(pi == 0 ? clouds.length : pi),		y+c9.heightOffset+c9.value*c9.thickness, 	z+this.offset(k));

		v5.setColorOpaque_I(c7.value > 0 ? ReikaColorAPI.getColorWithBrightnessMultiplier(0xffffff, c7.value) : 0);
		v5.addVertex(x+this.offset(i),		y+c7.heightOffset+c7.value*c7.thickness, 	z+this.offset(k));
	}

	private double offset(int a) {
		return a*BLOCKSIZE;
	}

	private static class CloudVertex {

		private final int relPosX;
		private final int relPosZ;
		private final float value;
		private final float heightOffset;
		private final float thickness;

		private CloudVertex(int x, int z, float dy, float v, float t) {
			relPosX = x;
			relPosZ = z;

			value = v;
			heightOffset = dy;
			thickness = t;
		}

	}

}
