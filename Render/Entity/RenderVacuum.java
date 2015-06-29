/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render.Entity;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Entity.EntityVacuum;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;

public class RenderVacuum extends Render {

	@Override
	public void doRender(Entity e, double par2, double par4, double par6, float par8, float ptick) {
		ReikaTextureHelper.bindTerrainTexture();
		EntityVacuum eb = (EntityVacuum)e;
		Tessellator v5 = Tessellator.instance;
		IIcon icon = ChromaIcons.FLARE.getIcon();
		float u = icon.getMinU();
		float v = icon.getMinV();
		float du = icon.getMaxU();
		float dv = icon.getMaxV();
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);
		BlendMode.ADDITIVEDARK.apply();
		GL11.glTranslated(par2, par4, par6);
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
		v5.startDrawingQuads();
		v5.setBrightness(240);
		double s = this.getVortexSize(eb);
		double s1 = 0.25;
		double d = 0.001;
		int c1 = 0xffffff;//eb.getRenderColor();
		v5.setColorOpaque_I(c1);
		if (eb.ticksExisted < 40) {
			v5.addVertexWithUV(-s1, -s1, 0, u, v);
			v5.addVertexWithUV(s1, -s1, 0, du, v);
			v5.addVertexWithUV(s1, s1, 0, du, dv);
			v5.addVertexWithUV(-s1, s1, 0, u, dv);
		}
		if (s > 0) {
			icon = ChromaIcons.FADE.getIcon();
			u = icon.getMinU();
			v = icon.getMinV();
			du = icon.getMaxU();
			dv = icon.getMaxV();
			int w = (int)(16*s);
			for (int dr = 1; dr <= w; dr++) {
				double r = dr/16D;
				int n = 6+1*dr;
				double f = 8D+(dr*dr)/4D;
				for (int i = 0; i < n; i++) {
					int a = 255-dr*12;
					int c = ReikaColorAPI.getColorWithBrightnessMultiplier(0xffffff, a/255F);
					v5.setColorOpaque_I(c);
					double s2 = 0.03125+dr/128D;
					s2 += (s2/4)*Math.sin(i+System.currentTimeMillis()/256D+eb.getEntityId()*8);
					double z = 0.05*dr+0.001*i;
					double ang = i*360/n+System.currentTimeMillis()/f;
					double dx = r*Math.cos(Math.toRadians(ang));
					double dy = r*Math.sin(Math.toRadians(ang));
					v5.addVertexWithUV(-s2+dx, -s2+dy, z, u, v);
					v5.addVertexWithUV(s2+dx, -s2+dy, z, du, v);
					v5.addVertexWithUV(s2+dx, s2+dy, z, du, dv);
					v5.addVertexWithUV(-s2+dx, s2+dy, z, u, dv);
				}
			}
		}
		v5.draw();
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_BLEND);
		BlendMode.DEFAULT.apply();
		GL11.glPopMatrix();
	}

	private double getVortexSize(EntityVacuum eb) {
		if (eb.ticksExisted <= 40) {
			return 0;
		}
		else if (eb.ticksExisted <= 50) {
			return 0.1*(eb.ticksExisted-40);
		}
		else {
			return 1;
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity e) {
		return null;
	}

}
