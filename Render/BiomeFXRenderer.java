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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaFX;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;
import Reika.DragonAPI.Instantiable.Rendering.ColorBlendList;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;


public class BiomeFXRenderer {

	public static final BiomeFXRenderer instance = new BiomeFXRenderer();

	private static final int RAIN_RADIUS = 48;
	private static final int POINT_COUNT = 28;

	private static final HashMap<Integer, ColorReference> colorMapA = new HashMap();
	private static final HashMap<Integer, ColorReference> colorMapB = new HashMap();

	static {
		colorMapA.put(ExtraChromaIDs.RAINBOWFOREST.getValue(), new SolidColorReference(0x22aaff));
		colorMapB.put(ExtraChromaIDs.RAINBOWFOREST.getValue(), new SolidColorReference(0x0060ff));

		colorMapA.put(ExtraChromaIDs.ENDERFOREST.getValue(), new SolidColorReference(0xa060ff));
		colorMapB.put(ExtraChromaIDs.ENDERFOREST.getValue(), new SolidColorReference(0xffaaff));

		colorMapA.put(ExtraChromaIDs.LUMINOUSCLIFFS.getValue(), new ColorBlendReference(new ColorBlendList(5, ChromaFX.getChromaColorTiles())));
		colorMapB.put(ExtraChromaIDs.LUMINOUSCLIFFS.getValue(), colorMapA.get(ExtraChromaIDs.LUMINOUSCLIFFS.getValue()));
	}

	private int biomeRainColor = 0x000000;
	private ArrayList<RainPoint> points = new ArrayList();

	private BiomeFXRenderer() {

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
		int b = ep.worldObj.getBiomeGenForCoords(px, pz).biomeID;
		ColorReference c1 = colorMapA.get(b);
		if (c1 != null && ep.worldObj.getSavedLightValue(EnumSkyBlock.Sky, px, py, pz) > 5) {
			ColorReference c2 = colorMapB.get(b);
			long tick = ep.worldObj.getTotalWorldTime();
			int c = 0xff000000 | (c1 == c2 ? c1.getColor(tick) : ReikaColorAPI.mixColors(c1.getColor(tick), c2.getColor(tick), (float)(0.5+0.5*Math.sin(System.currentTimeMillis()/1500D))));
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

		GL11.glTranslated(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ);

		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/biomeFX2.png");

		Tessellator v5 = Tessellator.instance;

		Iterator<RainPoint> it = points.iterator();
		while (it.hasNext()) {
			RainPoint p = it.next();
			if (p.tick(ep))
				it.remove();
		}
		while (points.size() < POINT_COUNT) {
			this.generateNewPoint(ep);
		}

		v5.startDrawingQuads();

		for (RainPoint p : points) {

			int c = biomeRainColor;
			float al = p.getAlpha();
			if (al < 1)
				c = ReikaColorAPI.getColorWithBrightnessMultiplier(c, al);
			v5.setColorOpaque_I(c);

			double u = (System.currentTimeMillis()/(int)(50*p.animationSpeed))%64/64D;
			double du = u+1/64D;

			double rx = p.posX;
			double rz = p.posZ;

			int n = 6;
			double da = 360D/n;
			double r = p.radius;
			double h = p.height;
			int ty = ep.worldObj.getTopSolidOrLiquidBlock(px, pz);
			double my = Math.max(ty-8, ep.posY-64);
			my = Math.max(my, ep.worldObj.getPrecipitationHeight(MathHelper.floor_double(rx), MathHelper.floor_double(rz)));

			for (double ry = my; ry <= ep.posY+64; ry += h*2) {
				for (double a = 0; a <= 360; a += da) {
					double ang1 = Math.toRadians(a);
					double dx1 = rx+r*Math.cos(ang1);
					double dz1 = rz+r*Math.sin(ang1);
					double ang2 = Math.toRadians(a+da);
					double dx2 = rx+r*Math.cos(ang2);
					double dz2 = rz+r*Math.sin(ang2);
					v5.addVertexWithUV(dx1, ry+h, dz1, u, 0);
					v5.addVertexWithUV(dx2, ry+h, dz2, du, 0);
					v5.addVertexWithUV(dx2, ry-h, dz2, du, 1);
					v5.addVertexWithUV(dx1, ry-h, dz1, u, 1);
				}
			}

		}

		v5.draw();

		ReikaRenderHelper.enableEntityLighting();
		BlendMode.DEFAULT.apply();
		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

	private void generateNewPoint(EntityPlayer ep) {
		double dx = ReikaRandomHelper.getRandomPlusMinus(ep.posX, RAIN_RADIUS);
		double dz = ReikaRandomHelper.getRandomPlusMinus(ep.posZ, RAIN_RADIUS);
		Random r = ep.worldObj.rand;
		RainPoint p = new RainPoint(dx, dz, 40+r.nextInt(440), 0.0625+0.0625*r.nextDouble(), ReikaRandomHelper.getRandomPlusMinus(9, 3), 0.5F+r.nextFloat()*1.5F);
		points.add(p);
	}

	private static abstract class ColorReference {

		protected abstract int getColor(long tick);

	}

	private static class ColorBlendReference extends ColorReference {

		private final ColorBlendList list;

		private ColorBlendReference(ColorBlendList c) {
			list = c;
		}

		@Override
		protected int getColor(long tick) {
			return list.getColor(tick);
		}

	}

	private static class SolidColorReference extends ColorReference {

		private final int color;

		private SolidColorReference(int c) {
			color = c;
		}

		@Override
		protected int getColor(long tick) {
			return color;
		}

	}

	private static class RainPoint {

		private static final int FADE_IN = 10;
		private static final int FADE_OUT = 20;

		private final int lifespan;
		private int age;

		private final float animationSpeed;
		private final double radius;
		private final double height;

		private final double posX;
		private final double posZ;

		private RainPoint(double x, double z, int l, double r, double h, float sp) {
			posX = x;
			posZ = z;
			lifespan = l;
			radius = r;
			height = h;
			animationSpeed = sp;
		}

		private boolean tick(EntityPlayer ep) {
			double dx = ep.posX-posX;
			double dz = ep.posZ-posZ;
			double dd = dx*dx+dz*dz;
			if (dd >= RAIN_RADIUS*RAIN_RADIUS) {
				age += lifespan/8;
			}
			else {
				age++;
			}
			return age >= lifespan;
		}

		private float getAlpha() {
			if (age < FADE_IN) {
				return (float)age/FADE_IN;
			}
			else if (age > lifespan-FADE_OUT) {
				int rem = lifespan-age;
				return (float)rem/FADE_OUT;
			}
			else {
				return 1;
			}
		}

	}

}
