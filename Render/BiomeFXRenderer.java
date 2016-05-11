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

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromaClientEventController;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.IO.ReikaImageLoader;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;


public class BiomeFXRenderer {

	public static final BiomeFXRenderer instance = new BiomeFXRenderer();

	private BufferedImage biomeRain;
	private final int[] rainTextures;
	private final BufferedImage[] rainImages;
	private int rainTickRate = 2;
	private int rainTickCount;
	private int rainTick;
	private int biomeRainColor = 0x000000;

	private BiomeFXRenderer() {
		String file = "biomeFX";
		biomeRain = ReikaImageLoader.readImage(ChromatiCraft.class, "/Reika/ChromatiCraft/Textures/"+file+".png", null);
		rainTextures = new int[biomeRain.getHeight()];
		rainImages = new BufferedImage[rainTextures.length];
	}

	public void initialize() {
		new Thread(new TextureLoader(), "Biome FX Loader").start();
	}

	private static class TextureLoader implements Runnable {

		private final ArrayList<ArrayList<Integer>> rowData = new ArrayList();

		public void run() {
			long start = System.currentTimeMillis();

			this.init();

			for (int i = 0; i < instance.rainTextures.length; i++) {
				BufferedImage img = this.constructBlendedImage(i);
				//instance.rainTextures[i] = ReikaTextureHelper.binder.allocateAndSetupTexture(img);
				instance.rainImages[i] = img;
				ArrayList<Integer> row = rowData.remove(0); //cycle
				rowData.add(row);
			}
			rowData.clear();
			ChromaClientEventController.instance.textureLoadingComplete = true;
			long dur = System.currentTimeMillis()-start;
			ChromatiCraft.logger.log("Constructed biome FX images in "+dur+" ms.");
		}

		private void init() {
			for (int k = 0; k < instance.biomeRain.getHeight(); k++) {
				ArrayList<Integer> row = new ArrayList();
				for (int i = 0; i < instance.biomeRain.getWidth(); i++) {
					row.add(instance.biomeRain.getRGB(i, k));
				}
				rowData.add(row);
			}
		}

		private BufferedImage constructBlendedImage(int step) {
			BufferedImage buf = ReikaImageLoader.copyImage(instance.biomeRain);
			for (int k = 0; k < instance.biomeRain.getHeight(); k++) {
				ArrayList<Integer> row = rowData.get(k);
				for (int i = 0; i < instance.biomeRain.getWidth(); i++) {
					buf.setRGB(i, k, this.blend(buf.getRGB(i, k), row.get(i)));
				}
			}
			return buf;
		}

		private int blend(int c1, int c2) {
			return 0xff000000 | ReikaColorAPI.GStoHex((int)(ReikaColorAPI.getRed(c1)*ReikaColorAPI.getRed(c2)/255F));
		}

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
		if ((b == ChromatiCraft.rainbowforest || b == ChromatiCraft.enderforest) && ep.worldObj.getSavedLightValue(EnumSkyBlock.Sky, px, py, pz) > 5) {
			int c1 = b == ChromatiCraft.rainbowforest ? 0x22aaff : 0xa060ff; //0x22aaff & 0xffaaff
			int c2 = b == ChromatiCraft.rainbowforest ? 0x0060ff : 0xffaaff;
			int c = 0xff000000 | ReikaColorAPI.mixColors(c1, c2, (float)(0.5+0.5*Math.sin(System.currentTimeMillis()/1500D)));
			biomeRainColor = ReikaColorAPI.mixColors(c, biomeRainColor, 0.05F);
		}
		else {
			biomeRainColor = ReikaColorAPI.mixColors(0x00000000, biomeRainColor, 0.025F);
		}

		if (biomeRainColor == 0)
			return;

		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glEnable(GL11.GL_BLEND);
		//GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		BlendMode.ADDITIVEDARK.apply();
		GL11.glDisable(GL11.GL_LIGHTING);
		ReikaRenderHelper.disableEntityLighting();
		GL11.glDepthMask(false);

		if (rainTextures[rainTick] == 0) {
			rainTextures[rainTick] = ReikaTextureHelper.binder.allocateAndSetupTexture(rainImages[rainTick]);
			rainImages[rainTick] = null;
		}

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, rainTextures[rainTick]);

		if (rainTickCount == 0) {
			rainTick--;
			if (rainTick < 0) {
				rainTick = rainTextures.length-1;
			}
			rainTickCount = rainTickRate-1;
		}
		else {
			rainTickCount--;
		}

		GL11.glTranslated(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ);

		double a = 15;
		double n = a/360;
		n = 0.125;
		double p = 3;
		int ty =ep.worldObj.getTopSolidOrLiquidBlock(px, pz);

		double dh = 1+(y-ty)/96D;
		double r = 24*dh;
		double w = 4*dh;

		Tessellator v5 = Tessellator.instance;
		v5.startDrawingQuads();
		v5.setColorOpaque_I(biomeRainColor);
		v5.setBrightness(240);

		double u = 0;
		for (double d = 0; d < 360; d += a) {
			double d2 = d+a;
			double dr1 = r+w*Math.sin(Math.toRadians((System.currentTimeMillis()/80D)%360D+d*p));
			double dr2 = r+w*Math.sin(Math.toRadians((System.currentTimeMillis()/80D)%360D+d2*p));
			double dx1 = x+dr1*Math.cos(Math.toRadians(d));
			double dz1 = z+dr1*Math.sin(Math.toRadians(d));
			double dx2 = x+dr2*Math.cos(Math.toRadians(d2));
			double dz2 = z+dr2*Math.sin(Math.toRadians(d2));
			double dy1 = Math.min(y-32, ty);
			double dy2 = y+64;
			v5.addVertexWithUV(dx1, y, dz1, u, 0);
			v5.addVertexWithUV(dx2, y, dz2, u+n, 0);
			v5.addVertexWithUV(x, dy2, z, u+n, 1);
			v5.addVertexWithUV(x, dy2, z, u, 1);

			v5.addVertexWithUV(x, dy1, z, u, 0);
			v5.addVertexWithUV(x, dy1, z, u+n, 0);
			v5.addVertexWithUV(dx2, y, dz2, u+n, 1);
			v5.addVertexWithUV(dx1, y, dz1, u, 1);

			u += n;
		}

		v5.draw();

		/*
		double d = rand.nextDouble()*360;
		double dx = x+16*Math.cos(Math.toRadians(d));
		double dz = z+16*Math.sin(Math.toRadians(d));
		EntityFX fx = new EntityBlurFX(ep.worldObj, dx, y-2, dz).setGravity(-0.0625F).setLife(80).setScale(4).setRapidExpand();
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		 */

		ReikaRenderHelper.enableEntityLighting();
		BlendMode.DEFAULT.apply();
		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

}
