/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.World.BiomeGlowingCliffs;
import Reika.DragonAPI.Instantiable.Math.SimplexNoiseGenerator;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;


public class CliffFogRenderer_Noise2 {

	public static final CliffFogRenderer_Noise2 instance = new CliffFogRenderer_Noise2();

	private final SimplexNoiseGenerator cloudNoise = new SimplexNoiseGenerator(System.currentTimeMillis()).addOctave(2, 0.5).addOctave(4, 0.125).addOctave(8, 0.125).setFrequency(1/24D);
	private final SimplexNoiseGenerator cloudFreqNoise = new SimplexNoiseGenerator(-System.currentTimeMillis()).setFrequency(1/36D);

	private CliffFogRenderer_Noise2() {

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

		if (!BiomeGlowingCliffs.isGlowingCliffs(b))
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

		Tessellator v5 = Tessellator.instance;

		v5.startDrawingQuads();

		v5.setNormal(0, 1, 0);
		v5.setBrightness(240);



		v5.draw();

		ReikaRenderHelper.enableEntityLighting();
		GL11.glShadeModel(GL11.GL_FLAT);
		BlendMode.DEFAULT.apply();
		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

	private final float getCloudHeight(double x, double z, double t) {
		double v = cloudNoise.getValue(x, z);
		if (v <= 0)
			return 0;
		t *= ReikaMathLibrary.normalizeToBounds(cloudFreqNoise.getValue(x, z), 0, 1);
		double f = Math.sin(x+t)+Math.sin(1.7*z+1.3*t)+0.36*Math.sin(0.98*x+1.23*t+11.4)+Math.sin(0.41*z+2.73*t+5.2);
		return (float)(v*f);
	}

}
