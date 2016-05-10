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

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Entity.EntityDimensionFlare;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;

public class RenderDimensionFlare extends Render {

	@Override
	public void doRender(Entity e, double par2, double par4, double par6, float par8, float ptick) {
		ReikaTextureHelper.bindFinalTexture(ChromatiCraft.class, "Textures/dimflare.png");
		EntityDimensionFlare eb = (EntityDimensionFlare)e;
		Tessellator v5 = Tessellator.instance;
		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDepthMask(false);
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
		double s1 = 0.75;
		double s2 = 6;
		double d = 0.001;
		v5.setColorOpaque_I(0xffffff);
		v5.addVertexWithUV(-s1, -s1, 0, 0, 0);
		v5.addVertexWithUV(s1, -s1, 0, 1, 0);
		v5.addVertexWithUV(s1, s1, 0, 1, 1);
		v5.addVertexWithUV(-s1, s1, 0, 0, 1);
		v5.draw();

		if (false) {
			ReikaTextureHelper.bindFinalTexture(ChromatiCraft.class, "Textures/dimflare_trail_anim.png");
			int frame = (int)((System.currentTimeMillis()/70)%16);//(e.ticksExisted/4)%16;
			int w = 4;
			double u = (frame%w)/(double)w;
			double v = (frame/w)/(double)w;
			double du = u+1D/w;
			double dv = v+1D/w;
			//ReikaJavaLibrary.pConsole(frame+" > ["+u+", "+v+"] > ["+du+", "+dv+"]");

			Vec3 vel = Vec3.createVectorHelper(e.motionX, e.motionY, e.motionZ);
			EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
			Vec3 pos = Vec3.createVectorHelper(e.posX-ep.posX, e.posY-ep.posY, e.posZ-ep.posZ);
			double ang = 0;

			GL11.glRotated(ang, 0, 0, 1);
			v5.startDrawingQuads();
			v5.setBrightness(240);
			v5.setColorOpaque_I(0xffffff);
			v5.addVertexWithUV(-s2, -s2, 0, u, v);
			v5.addVertexWithUV(s2, -s2, 0, du, v);
			v5.addVertexWithUV(s2, s2, 0, du, dv);
			v5.addVertexWithUV(-s2, s2, 0, u, dv);
			v5.draw();
		}

		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity e) {
		return null;
	}

}
