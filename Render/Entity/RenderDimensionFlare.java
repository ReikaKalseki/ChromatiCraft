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

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Entity.EntityDimensionFlare;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;

public class RenderDimensionFlare extends Render {

	@Override
	public void doRender(Entity e, double par2, double par4, double par6, float par8, float ptick) {
		ReikaTextureHelper.bindFinalTexture(ChromatiCraft.class, "Textures/dimflare2.png");
		EntityDimensionFlare eb = (EntityDimensionFlare)e;
		Tessellator v5 = Tessellator.instance;
		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glDepthMask(false);
		BlendMode.ADDITIVEDARK.apply();
		GL11.glShadeModel(GL11.GL_SMOOTH);
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
		double s1 = 0.75+0.25+0.25*Math.sin(e.ticksExisted/16D);
		double s2 = 6;
		double d = 0.001;
		int tick = e.ticksExisted%128;
		double u = (tick%16)/16D;
		double v = (tick/16)/8D;
		double du = u+1/16D;
		double dv = v+1/8D;
		float f1 = (float)(0.375+0.375*Math.sin(e.ticksExisted/8D));
		float f2 = (float)(0.375+0.375*Math.sin(e.ticksExisted/7D+7));
		float f3 = (float)(0.375+0.375*Math.sin(e.ticksExisted/11D-13));
		float f4 = (float)(0.375+0.375*Math.sin(e.ticksExisted/12D+41));
		int c1 = ReikaColorAPI.mixColors(eb.getIdentity().flareColor, 0xffffff, f1);
		int c2 = ReikaColorAPI.mixColors(eb.getIdentity().flareColor, 0xffffff, f2);
		int c3 = ReikaColorAPI.mixColors(eb.getIdentity().flareColor, 0xffffff, f3);
		int c4 = ReikaColorAPI.mixColors(eb.getIdentity().flareColor, 0xffffff, f4);
		v5.setColorOpaque_I(c1);
		v5.addVertexWithUV(-s1, -s1, 0, u, v);
		v5.setColorOpaque_I(c2);
		v5.addVertexWithUV(s1, -s1, 0, du, v);
		v5.setColorOpaque_I(c3);
		v5.addVertexWithUV(s1, s1, 0, du, dv);
		v5.setColorOpaque_I(c4);
		v5.addVertexWithUV(-s1, s1, 0, u, dv);
		v5.draw();

		if (false) {
			ReikaTextureHelper.bindFinalTexture(ChromatiCraft.class, "Textures/dimflare_trail_anim.png");
			int frame = (int)((System.currentTimeMillis()/70)%16);//(e.ticksExisted/4)%16;
			int w = 4;
			u = (frame%w)/(double)w;
			v = (frame/w)/(double)w;
			du = u+1D/w;
			dv = v+1D/w;
			//ReikaJavaLibrary.pConsole(frame+" > ["+u+", "+v+"] > ["+du+", "+dv+"]");

			Vec3 vel = Vec3.createVectorHelper(e.motionX, e.motionY, e.motionZ);
			EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
			Vec3 pos = Vec3.createVectorHelper(e.posX-ep.posX, e.posY-ep.posY, e.posZ-ep.posZ);
			double dot = vel.dotProduct(pos)+180;
			double ang = Math.toDegrees(Math.acos(dot/(vel.lengthVector()*pos.lengthVector())));

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
