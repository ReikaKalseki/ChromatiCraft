/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base;

import java.nio.FloatBuffer;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.MinecraftForgeClient;

import Reika.ChromatiCraft.ChromaClient;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityLocusPoint;
import Reika.DragonAPI.Instantiable.RayTracer;
import Reika.DragonAPI.Instantiable.RayTracer.RayTracerWithCache;
import Reika.DragonAPI.Instantiable.IO.RemoteSourcedAsset;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;

public abstract class RenderLocusPoint extends ChromaRenderBase {

	private final RemoteSourcedAsset texture = ChromaClient.dynamicAssets.createAsset("Textures/aurapoint2-grid.png");

	protected static final RayTracerWithCache LOS = RayTracer.getVisualLOSForRenderCulling();

	@Override
	public final void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDepthMask(false);
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glTranslated(par2, par4, par6);
		if (MinecraftForgeClient.getRenderPass() == 1 || !tile.hasWorldObj()) {
			//ShaderRegistry.runShader(shader);
			this.renderCore((TileEntityLocusPoint)tile, par2, par4, par6, par8);
			//ShaderRegistry.completeShader();
		}
		this.doOtherRendering((TileEntityLocusPoint)tile, par8);
		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

	protected abstract void doOtherRendering(TileEntityLocusPoint tile, float par8);

	private void renderCore(TileEntityLocusPoint tile, double par2, double par4, double par6, float par8) {
		Tessellator v5 = Tessellator.instance;
		//this.renderStaticTexture(par2, par4, par6, par8);
		//BlendMode.ADDITIVEDARK.apply();
		BlendMode.ADDITIVE2.apply();
		GL11.glPushMatrix();
		GL11.glTranslated(0.5, 0.5, 0.5);
		RenderManager rm = RenderManager.instance;
		if (tile.hasWorldObj()) {
			GL11.glRotatef(-rm.playerViewY, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);
		}
		else {
			GL11.glTranslated(0.55, 0.5, 0.5);
			GL11.glRotatef(45, 0, 1, 0);
			GL11.glRotatef(-30, 1, 0, 0);
		}

		//ReikaJavaLibrary.pConsole(te.getEnergy());

		ReikaTextureHelper.bindTexture(texture);

		for (int i = 0; i < 3; i++) {

			int alpha = 255-i*64;//255;
			int tick = (int)((System.currentTimeMillis()/250)%80);//(tile.getTicksExisted()*(i*3+1))%80;//+par8;
			double u = (tick%8)/8D;//tick/80D;//0;//0.125;
			double v = (tick/8)/10D;//0;//(int)(tick/20)/4D;//0.125;
			double du = u+1/8D;//u+1/80D;//u+1;//0.9375;
			double dv = v+1/10D;//v+1;//0.9375;

			double s = 1-i*0.25;
			//for (double s = 0.5; s >= 0.25; s -= 0.0625) {

			GL11.glPushMatrix();

			double s1 = tile.isInWorld() ? s : s;

			GL11.glScaled(s1, s1, s1);
			/*
			GL11.glMatrixMode(GL11.GL_TEXTURE);
			GL11.glPushMatrix();

			/*
		double d = 0.5;
		GL11.glTranslated(d, d, d);
		GL11.glRotated(rm.playerViewY, 0, 0, 1);
		GL11.glTranslated(-d, -d, -d);
			 */
			if (tile.isInWorld()) {
				double d = 0.5;
				GL11.glTranslated(d, d, d);

				double dx = tile.xCoord+0.5-RenderManager.renderPosX;
				double dy = tile.yCoord+0.5-RenderManager.renderPosY;
				double dz = tile.zCoord+0.5-RenderManager.renderPosZ;
				double[] angs = ReikaPhysicsHelper.cartesianToPolar(dx, dy, dz);
				GL11.glTranslated(-d, -d, -d);
				GL11.glRotated(angs[2], 0, 0, 1);
			}


			//GL11.glMatrixMode(GL11.GL_MODELVIEW);

			v5.startDrawingQuads();
			v5.setBrightness(240);
			int color = ReikaColorAPI.getColorWithBrightnessMultiplier(tile.getRenderColor(), alpha/255F);
			v5.setColorRGBA_I(color, alpha);
			v5.addVertexWithUV(-1, -1, 0, u, v);
			v5.addVertexWithUV(1, -1, 0, du, v);
			v5.addVertexWithUV(1, 1, 0, du, dv);
			v5.addVertexWithUV(-1, 1, 0, u, dv);
			v5.draw();


			//GL11.glMatrixMode(GL11.GL_TEXTURE);
			//GL11.glPopMatrix();
			//GL11.glMatrixMode(GL11.GL_MODELVIEW);

			GL11.glPopMatrix();

			//}
		}

		double s = 0.5;
		GL11.glScaled(s, s, s);

		/*
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/Dimension Bump Maps/gray.png");

		u = 0;
		v = 0;
		du = 1;
		dv = 1;

		GL11.glMatrixMode(GL11.GL_TEXTURE);
		GL11.glPushMatrix();

		GL11.glRotated(angs[2], 0, 1, 0);
		GL11.glRotated(90-angs[1], 1, 0, 0);

		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		v5.startDrawingQuads();
		v5.setColorRGBA_I(this.getColor(), alpha);
		v5.addVertexWithUV(-1, -1, 0, u, v);
		v5.addVertexWithUV(1, -1, 0, du, v);
		v5.addVertexWithUV(1, 1, 0, du, dv);
		v5.addVertexWithUV(-1, 1, 0, u, dv);
		v5.draw();


		GL11.glMatrixMode(GL11.GL_TEXTURE);
		GL11.glPopMatrix();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		 */

		GL11.glPopMatrix();
	}

	private static final Random staticRand = new Random(31100L);
	FloatBuffer fBuffer = GLAllocation.createDirectFloatBuffer(16);

	private void renderStaticTexture(double par2, double par4, double par6, float ptick) {
		float f1 = (float)field_147501_a.field_147560_j;
		float f2 = (float)field_147501_a.field_147561_k;
		float f3 = (float)field_147501_a.field_147558_l;
		staticRand.setSeed(31100L);

		float f4 = 1.001F;//0.75F;//+i*0.125F;
		GL11.glPushMatrix();
		float f5 = 16;
		float f6 = 0.0625F;
		float f7 = 1.0F/(f5+1.0F);

		float f8 = (float)(-(par4+f4));
		float f9 = f8+ActiveRenderInfo.objectY;
		float f10 = f8+f5+ActiveRenderInfo.objectY;
		float f11 = f9/f10;
		f11 += (float)(par4+f4);
		//GL11.glTranslatef(f1, f11, f3);
		GL11.glTexGeni(GL11.GL_S, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_OBJECT_LINEAR);
		GL11.glTexGeni(GL11.GL_T, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_OBJECT_LINEAR);
		GL11.glTexGeni(GL11.GL_R, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_OBJECT_LINEAR);
		GL11.glTexGeni(GL11.GL_Q, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_EYE_LINEAR);
		GL11.glTexGen(GL11.GL_S, GL11.GL_OBJECT_PLANE, this.genBuffer(1.0F, 0.0F, 0.0F, 0.0F));
		GL11.glTexGen(GL11.GL_T, GL11.GL_OBJECT_PLANE, this.genBuffer(0.0F, 0.0F, 1.0F, 0.0F));
		GL11.glTexGen(GL11.GL_R, GL11.GL_OBJECT_PLANE, this.genBuffer(0.0F, 0.0F, 0.0F, 1.0F));
		GL11.glTexGen(GL11.GL_Q, GL11.GL_EYE_PLANE, this.genBuffer(0.0F, 1.0F, 0.0F, 0.0F));
		GL11.glEnable(GL11.GL_TEXTURE_GEN_S);
		GL11.glEnable(GL11.GL_TEXTURE_GEN_T);
		GL11.glEnable(GL11.GL_TEXTURE_GEN_R);
		GL11.glEnable(GL11.GL_TEXTURE_GEN_Q);
		GL11.glPopMatrix();
		GL11.glMatrixMode(GL11.GL_TEXTURE);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glTranslatef(0.0F, Minecraft.getSystemTime() % 700000L/700000.0F, 0.0F);
		GL11.glScalef(f6, f6, f6);
		GL11.glTranslatef(0.5F, 0.5F, 0.0F);
		GL11.glTranslatef(-0.5F, -0.5F, 0.0F);
		//GL11.glTranslatef(-f1, -f3, -f2);
		f9 = f8+ActiveRenderInfo.objectY;
		GL11.glTranslatef(ActiveRenderInfo.objectX*f5/f9, ActiveRenderInfo.objectZ*f5/f9, -f2);
		Tessellator v5 = Tessellator.instance;
		v5.startDrawingQuads();
		f11 = staticRand.nextFloat()*0.5F+0.1F;
		float f12 = staticRand.nextFloat()*0.5F+0.4F;
		float f13 = staticRand.nextFloat()*0.5F+0.5F;

		f7 = 1;
		f13 = 1.0F;
		f12 = 1.0F;
		f11 = 1.0F;

		//v5.setColorRGBA_F(e.getRed()*f7/255F, e.getGreen()*f7/255F, e.getBlue()*f7/255F, 1);
		v5.setColorRGBA_F(f11*f7, f12*f7, f13*f7, 1.0F);
		v5.addVertex(par2, par4+f4, par6);
		v5.addVertex(par2, par4+f4, par6+1.0D);
		v5.addVertex(par2+1.0D, par4+f4, par6+1.0D);
		v5.addVertex(par2+1.0D, par4+f4, par6);
		v5.draw();
		GL11.glPopMatrix();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		GL11.glDisable(GL11.GL_TEXTURE_GEN_S);
		GL11.glDisable(GL11.GL_TEXTURE_GEN_T);
		GL11.glDisable(GL11.GL_TEXTURE_GEN_R);
		GL11.glDisable(GL11.GL_TEXTURE_GEN_Q);
	}

	private FloatBuffer genBuffer(float v1, float v2, float v3, float v4) {
		fBuffer.clear();
		fBuffer.put(v1).put(v2).put(v3).put(v4);
		fBuffer.flip();
		return fBuffer;
	}

}
