/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Rendering;

import java.util.Locale;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.client.IRenderHandler;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ChromaDimensionBiome;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class ChromaWeatherRenderer extends IRenderHandler {

	public static final ChromaWeatherRenderer instance = new ChromaWeatherRenderer();

	private final float[] rainXCoords;
	private final float[] rainYCoords;

	private int rendererUpdateCount;

	private final Random rand = new Random();

	private ChromaWeatherRenderer() {
		rainXCoords = new float[1024];
		rainYCoords = new float[1024];

		for (int i = 0; i < 32; ++i)
		{
			for (int j = 0; j < 32; ++j)
			{
				float f2 = j-16;
				float f3 = i-16;
				float f4 = MathHelper.sqrt_float(f2*f2+f3*f3);
				rainXCoords[i << 5 | j] = -f3/f4;
				rainYCoords[i << 5 | j] = f2/f4;
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(float ptick, WorldClient world, Minecraft mc) {
		//this.enableLightmap((double)partialTicks);

		EntityLivingBase e = mc.renderViewEntity;
		int ex = MathHelper.floor_double(e.posX);
		int ey = MathHelper.floor_double(e.posY);
		int ez = MathHelper.floor_double(e.posZ);
		Tessellator v5 = Tessellator.instance;
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_LIGHTING);
		ReikaRenderHelper.disableEntityLighting();
		GL11.glNormal3f(0F, 1F, 0F);
		GL11.glEnable(GL11.GL_BLEND);
		BlendMode.ADDITIVEDARK.apply();
		double rdx = e.lastTickPosX+(e.posX-e.lastTickPosX)*ptick;
		double rdy = e.lastTickPosY+(e.posY-e.lastTickPosY)*ptick;
		double rdz = e.lastTickPosZ+(e.posZ-e.lastTickPosZ)*ptick;
		int rdh = MathHelper.floor_double(rdy);
		int radius = 10;

		int b1 = -1;
		float tick = world.getTotalWorldTime()+ptick;//rendererUpdateCount+ptick;
		//rendererUpdateCount++;

		GL11.glColor4f(1F, 1F, 1F, 1F);

		int d = 8;
		for (int z = ez-radius; z <= ez+radius; z += d) {
			for (int x = ex-radius; x <= ex+radius; x += d) {
				int od = x*(radius*2+1)+z;
				int idx = (z-ez+16)*32+x-ex+16;
				float staggerX = rainXCoords[idx]*4;//0.5F;
				float staggerY = rainYCoords[idx]*4;//0.5F;
				BiomeGenBase biome = world.getBiomeGenForCoords(x, z);

				if (!(biome instanceof ChromaDimensionBiome)) {
					//ChromatiCraft.logger.logError("Biome at "+x+", "+z+" in dim "+world.provider.dimensionId+" is not a dimension biome, but is "+biome.biomeName+" ("+biome.biomeID+")!");
					continue;
				}

				ChromaDimensionBiome b = (ChromaDimensionBiome)biome;

				if (biome.canSpawnLightningBolt() || biome.getEnableSnow() || true) {
					int rainY = world.getPrecipitationHeight(x, z);
					int minY = ey-radius;
					int maxY = ey+radius;

					if (minY < rainY) {
						minY = rainY;
					}

					if (maxY < rainY) {
						maxY = rainY;
					}

					int y = rainY;

					if (rainY < rdh) {
						y = rdh;
					}

					if (minY != maxY) {
						rand.setSeed(x*x*3121+x*45238971 ^ z*z*418711+z*13761);
						float frame;

						if (b1 != 0) {
							if (b1 >= 0) {
								v5.draw();
							}

							b1 = 0;
							ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/DimWeather/"+b.getExactType().name().toLowerCase(Locale.ENGLISH)+".png");
							ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/DimWeather/deepocean.png");
							v5.startDrawingQuads();
						}

						float f = (float)(96+32*Math.sin(System.currentTimeMillis()/2000D));
						frame = (tick+(x%16)*6+(z%16)*8)/f;//((rendererUpdateCount+x*x*3121+x*45238971+z*z*418711+z*13761 & 31)+ptick)/32.0F*(3.0F+rand.nextFloat());
						double dx = x+0.5F-e.posX;
						double dz = z+0.5F-e.posZ;
						float dd = MathHelper.sqrt_double(dx*dx+dz*dz)/radius;
						float colorFactor = 1F;
						v5.setBrightness(240/*world.getLightBrightnessForSkyBlocks(x, y, z, 0)*/);
						float alpha = 1;
						float frameSizeX = 1F;
						float frameSizeY = 1F;
						double u = od/16D;
						v5.setColorRGBA_F(colorFactor, colorFactor, colorFactor, ((1F-dd*dd)*0.5F+0.5F)*alpha);
						v5.setTranslation(-rdx*1D, -rdy*1D, -rdz*1D);
						v5.addVertexWithUV(x-staggerX+0.5, minY, z-staggerY+0.5, u+0F*frameSizeX, minY*frameSizeY/4F+frame*frameSizeY);
						v5.addVertexWithUV(x+staggerX+0.5, minY, z+staggerY+0.5, u+1F*frameSizeX, minY*frameSizeY/4F+frame*frameSizeY);
						v5.addVertexWithUV(x+staggerX+0.5, maxY, z+staggerY+0.5, u+1F*frameSizeX, maxY*frameSizeY/4F+frame*frameSizeY);
						v5.addVertexWithUV(x-staggerX+0.5, maxY, z-staggerY+0.5, u+0F*frameSizeX, maxY*frameSizeY/4F+frame*frameSizeY);
						v5.setTranslation(0, 0, 0);
					}
				}
			}
		}

		if (b1 >= 0)
		{
			v5.draw();
		}

		GL11.glPopAttrib();
		GL11.glPopMatrix();
		//this.disableLightmap((double)partialTicks);
	}

}
