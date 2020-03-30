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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.client.MinecraftForgeClient;

import Reika.ChromatiCraft.ChromaClient;
import Reika.ChromatiCraft.World.BiomeGlowingCliffs;
import Reika.DragonAPI.Instantiable.IO.RemoteSourcedAsset;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;


public class CliffFogRenderer {

	public static final CliffFogRenderer instance = new CliffFogRenderer();

	private static final int CLOUD_COUNT = 48;

	private final RemoteSourcedAsset texture = ChromaClient.dynamicAssets.createAsset("Textures/biomefog_alpha.png");

	private final ArrayList<Cloud> clouds = new ArrayList();
	private final Random rand = new Random();

	private CliffFogRenderer() {

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

		if (BiomeGlowingCliffs.isGlowingCliffs(b) && clouds.size() < CLOUD_COUNT && rand.nextInt(4) == 0) {
			Cloud c = this.createCloud(ep.worldObj, x, y, z);
			if (c != null)
				clouds.add(c);
		}

		if (clouds.isEmpty())
			return;

		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		//BlendMode.DEFAULT.apply();
		BlendMode.DEFAULT.apply();
		//GL11.glDisable(GL11.GL_LIGHTING);
		//ReikaRenderHelper.disableEntityLighting();
		GL11.glDepthMask(false);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glColor4f(1, 1, 1, 1);

		ReikaTextureHelper.bindTexture(texture);

		GL11.glTranslated(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ);

		double v = 0.125;
		double a = ((4*ep.worldObj.getTotalWorldTime())%24000)/24000D*(Math.PI*2);
		double vx = v*Math.cos(a);
		double vz = v*Math.sin(a);
		double vy = 1+Math.sin(-a);

		Iterator<Cloud> it = clouds.iterator();
		while (it.hasNext()) {
			Cloud c = it.next();
			c.render(Tessellator.instance, x, y, z);
			if (c.update(vx, 0, vz))
				it.remove();
		}

		//ReikaRenderHelper.enableEntityLighting();
		GL11.glShadeModel(GL11.GL_FLAT);
		//BlendMode.DEFAULT.apply();
		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

	private Cloud createCloud(World world, double x, double y, double z) {
		double dx = ReikaRandomHelper.getRandomPlusMinus(x, 96);
		double dz = ReikaRandomHelper.getRandomPlusMinus(z, 96);

		if (!BiomeGlowingCliffs.isGlowingCliffs(world.getBiomeGenForCoords(MathHelper.floor_double(dx), MathHelper.floor_double(dz))))
			return null;

		double dy = ReikaRandomHelper.getRandomBetween(60D, 256D);

		int l = 40+rand.nextInt(200);
		double s = ReikaRandomHelper.getRandomBetween(24D, 96D);
		float sx = (float)ReikaRandomHelper.getRandomBetween(0.0625, 0.375);
		float sy = (float)ReikaRandomHelper.getRandomBetween(0.0625, 0.375);
		float tx = (float)ReikaRandomHelper.getRandomPlusMinus(0, 0.03125D/128);
		float ty = (float)ReikaRandomHelper.getRandomPlusMinus(0, 0.03125D/128);
		float fx = (float)ReikaRandomHelper.getRandomPlusMinus(0, 0.03125D/32);
		float fy = (float)ReikaRandomHelper.getRandomPlusMinus(0, 0.03125D/32);
		Cloud c = new Cloud(rand.nextFloat(), rand.nextFloat(), sx, sy, s, l, dx, dy, dz).setTextureSpeed(tx, ty).setSizeSpeed(fx, fy);
		c.scaleSpeed = ReikaRandomHelper.getRandomPlusMinus(0, 0.03125D/16);
		return c;
	}

	private static class Cloud {

		private float textureX;
		private float textureY;
		private float sizeX;
		private float sizeY;
		private double scale;

		private float textureXSpeed = 0;
		private float textureYSpeed = 0;
		private float sizeXSpeed = 0;
		private float sizeYSpeed = 0;
		private double scaleSpeed = 0;

		private final int lifespan;
		private int age;

		private double posX;
		private double posY;
		private double posZ;

		private Cloud(float tx, float ty, float sx, float sy, double s, int l, double x, double y, double z) {
			textureX = tx;
			textureY = ty;
			sizeX = sx;
			sizeY = sy;
			scale = s;
			lifespan = l;
			posX = x;
			posY = y;
			posZ = z;
		}

		private Cloud setTextureSpeed(float x, float y) {
			textureXSpeed = x;
			textureYSpeed = y;
			return this;
		}

		private Cloud setSizeSpeed(float x, float y) {
			sizeXSpeed = x;
			sizeYSpeed = y;
			return this;
		}

		private boolean update(double vx, double vy, double vz) {
			posX += vx;
			posY += vy;
			posZ += vz;

			textureX += textureXSpeed;
			textureY += textureYSpeed;

			sizeX += sizeXSpeed;
			sizeY += sizeYSpeed;

			scale += scaleSpeed;

			age++;
			return age >= lifespan;
		}

		private float getAlpha() {
			return (float)Math.sin(Math.toRadians(180D*age/lifespan));//age > 0.5F*lifespan ? 2F*(1-age/lifespan) : 2F*age/lifespan;
		}

		private void render(Tessellator v5, double x, double y, double z) {
			GL11.glPushMatrix();
			GL11.glTranslated(posX, posY, posZ);

			RenderManager rm = RenderManager.instance;
			GL11.glRotatef(-rm.playerViewY, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);

			v5.startDrawingQuads();

			float f = this.getAlpha();

			v5.setNormal(0, 1, 0);
			v5.setBrightness(240);
			v5.setColorRGBA_I(0xffffff, (int)(255*f));

			/*
			v5.addVertexWithUV(-scale*sizeX,	-scale*sizeY,	0, textureX,		textureY);
			v5.addVertexWithUV(scale*sizeX,		-scale*sizeY,	0, textureX+sizeX,	textureY);
			v5.addVertexWithUV(scale*sizeX,		scale*sizeY,	0, textureX+sizeX,	textureY+sizeY);
			v5.addVertexWithUV(-scale*sizeX,	scale*sizeY,	0, textureX,		textureY+sizeY);
			 */

			double x1 = -scale*sizeX;
			double x2 = scale*sizeX;
			double y1 = -scale*sizeY;
			double y2 = scale*sizeY;


			double ds = 0.125;
			for (double d1 = 0; d1 < 1; d1 += ds) {
				for (double d2 = 0; d2 < 1; d2 += ds) {
					double dx = x1+(x2-x1)*d1;
					double dy = y1+(y2-y1)*d2;
					double dx2 = dx+ds*sizeX*scale*2;
					double dy2 = dy+ds*sizeY*scale*2;

					double u1 = textureX+d1*sizeX;
					double u2 = u1+ds*sizeX;
					double v1 = textureY+d2*sizeY;
					double v2 = v1+ds*sizeY;

					double rx1 = 1-Math.abs((d1)*2-1);
					double rz1 = 1-Math.abs((d2)*2-1);
					double r1 = rx1*rz1;
					float f2 = (float)(f*r1);
					v5.setColorRGBA_I(0xffffff, (int)(255*f2));
					v5.addVertexWithUV(dx,	dy,		0, u1,	v1);

					rx1 = 1-Math.abs((d1+ds)*2-1);
					rz1 = 1-Math.abs((d2)*2-1);
					r1 = rx1*rz1;
					f2 = (float)(f*r1);
					v5.setColorRGBA_I(0xffffff, (int)(255*f2));
					v5.addVertexWithUV(dx2,	dy,		0, u2,	v1);

					rx1 = 1-Math.abs((d1+ds)*2-1);
					rz1 = 1-Math.abs((d2+ds)*2-1);
					r1 = rx1*rz1;
					f2 = (float)(f*r1);
					v5.setColorRGBA_I(0xffffff, (int)(255*f2));
					v5.addVertexWithUV(dx2,	dy2,	0, u2,	v2);

					rx1 = 1-Math.abs((d1)*2-1);
					rz1 = 1-Math.abs((d2+ds)*2-1);
					r1 = rx1*rz1;
					f2 = (float)(f*r1);
					v5.setColorRGBA_I(0xffffff, (int)(255*f2));
					v5.addVertexWithUV(dx,	dy2,	0, u1,	v2);
				}
			}

			v5.draw();

			GL11.glPopMatrix();
		}

	}

}
