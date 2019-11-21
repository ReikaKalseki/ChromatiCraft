/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render.Entity;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import Reika.ChromatiCraft.Entity.EntityDeathFog;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.DragonAPI.Instantiable.RayTracer;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;

public class RenderDeathFog extends Render {

	private static final RayTracer trace = RayTracer.getVisualLOS();

	@Override
	public void doRender(Entity e, double par2, double par4, double par6, float par8, float ptick) {
		EntityDeathFog eb = (EntityDeathFog)e;
		EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
		trace.setOrigins(eb.posX, eb.posY, eb.posZ, ep.posX, ep.posY, ep.posZ);
		if (!trace.isClearLineOfSight(eb.worldObj))
			return;
		ReikaTextureHelper.bindTerrainTexture();
		Tessellator v5 = Tessellator.instance;
		IIcon icon = ChromaIcons.FADE_BASICBLEND.getIcon();
		float u = icon.getMinU();
		float v = icon.getMinV();
		float du = icon.getMaxU();
		float dv = icon.getMaxV();
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		BlendMode.DEFAULT.apply();
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glDepthMask(false);
		GL11.glTranslated(par2, par4, par6);
		Random rand = new Random(System.identityHashCode(e));
		rand.nextBoolean();
		rand.nextBoolean();
		if (!e.isDead) {
			RenderManager rm = RenderManager.instance;
			double dx = e.posX-RenderManager.renderPosX;
			double dy = e.posY-RenderManager.renderPosY;
			double dz = e.posZ-RenderManager.renderPosZ;
			double[] angs = ReikaPhysicsHelper.cartesianToPolar(dx, dy, dz);
			GL11.glRotated(angs[2], 0, 1, 0);
			GL11.glRotated(90-angs[1], 1, 0, 0);
		}
		//GL11.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);
		double sb = eb.isEnhanced() ? 2.25 : 1.75;//0.75;
		int n = eb.isEnhanced() ? 1+rand.nextInt(4) : 1;//2+rand.nextInt(7);
		v5.startDrawingQuads();
		v5.setBrightness(240);
		int c1 = 0x000000;
		for (int i = 0; i < n; i++) {
			double dx = (rand.nextDouble()*1-0.5D)*sb;
			double dy = (rand.nextDouble()*1-0.5D)*sb;
			float f2 = rand.nextFloat()+0.5F;
			int d = rand.nextInt(Math.max(1, Math.min(eb.getRemainingLife(), 10)));
			float l = eb.getRemainingLife()-d-ptick;
			float f = l >= 20 ? 1 : l/20F;
			double f3 = 0.5+rand.nextDouble()*0.5;
			double s1 = sb*f*f3;
			if (f > 0) {
				v5.setColorRGBA_I(c1, (int)(f*255*f2));
				v5.addVertexWithUV(dx-s1, dy-s1, 0, u, v);
				v5.addVertexWithUV(dx+s1, dy-s1, 0, du, v);
				v5.addVertexWithUV(dx+s1, dy+s1, 0, du, dv);
				v5.addVertexWithUV(dx-s1, dy+s1, 0, u, dv);
			}
		}
		v5.draw();
		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity e) {
		return null;
	}

}
