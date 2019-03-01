/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render.TESR.Dimension;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.MinecraftForgeClient;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

public class RenderGlowingCracks extends ChromaRenderBase {

	private final Random rand = new Random();

	@Override
	public String getImageFileName(RenderFetcher te) {
		return null;
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		if (MinecraftForgeClient.getRenderPass() != 1)
			return;
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glPushMatrix();
		GL11.glTranslated(par2, par4, par6);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		ReikaRenderHelper.disableEntityLighting();
		//GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDepthMask(false);
		BlendMode.ADDITIVEDARK.apply();
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		this.renderCracks(tile);
		GL11.glPopMatrix();
		GL11.glPopAttrib();
	}

	private void renderCracks(TileEntity te) {
		int r = 4;
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/glowcracks.png");
		Tessellator v5 = Tessellator.instance;

		rand.setSeed(System.identityHashCode(te));
		double sp = 600+400*rand.nextDouble();

		double t1 = (System.currentTimeMillis()/sp)%360D+rand.nextDouble()*3;
		double s1 = 0.5+rand.nextDouble();
		double s2 = 0.5+rand.nextDouble();
		double s3 = 0.5+rand.nextDouble();
		double s4 = 0.5+rand.nextDouble();
		double a1 = -2+rand.nextDouble()*4;
		double a2 = -2+rand.nextDouble()*4;
		double a3 = -2+rand.nextDouble()*4;
		double a4 = -2+rand.nextDouble()*4;
		float f1a = (float)(0.5+0.5*Math.sin(t1*s1+a1));
		float f1b = (float)(0.5+0.5*Math.sin(t1*s2+a2));
		float f1c = (float)(0.5+0.5*Math.sin(t1*s3+a3));
		float f1d = (float)(0.5+0.5*Math.sin(t1*s4+a4));

		s1 = 0.5+rand.nextDouble();
		s2 = 0.5+rand.nextDouble();
		s3 = 0.5+rand.nextDouble();
		s4 = 0.5+rand.nextDouble();
		a1 = -2+rand.nextDouble()*4;
		a2 = -2+rand.nextDouble()*4;
		a3 = -2+rand.nextDouble()*4;
		a4 = -2+rand.nextDouble()*4;

		double t2 = (System.currentTimeMillis()/512D)%360D;
		float f2a = (float)(0.75+0.25*Math.sin(t2*s1+a1));
		float f2b = (float)(0.75+0.25*Math.sin(t2*s2+a2));
		float f2c = (float)(0.75+0.25*Math.sin(t2*s3+a3));
		float f2d = (float)(0.75+0.25*Math.sin(t2*s4+a4));

		int c1 = ReikaColorAPI.getModifiedHue(0xff0000, rand.nextInt(360));
		int c2 = ReikaColorAPI.getModifiedHue(0xff0000, rand.nextInt(360));
		int ca = ReikaColorAPI.mixColors(c1, c2, f1a);
		int cb = ReikaColorAPI.mixColors(c1, c2, f1b);
		int cc = ReikaColorAPI.mixColors(c1, c2, f1c);
		int cd = ReikaColorAPI.mixColors(c1, c2, f1d);

		GL11.glRotated(rand.nextInt(4)*90, 0, 1, 0);

		v5.startDrawingQuads();

		v5.setColorOpaque_I(ReikaColorAPI.getColorWithBrightnessMultiplier(ca, 1));
		v5.addVertexWithUV(-r, 0.005, r+1, 0, 1);
		v5.setColorOpaque_I(ReikaColorAPI.getColorWithBrightnessMultiplier(cb, 1));
		v5.addVertexWithUV(r+1, 0.005, r+1, 1, 1);
		v5.setColorOpaque_I(ReikaColorAPI.getColorWithBrightnessMultiplier(cc, 1));
		v5.addVertexWithUV(r+1, 0.005, -r, 1, 0);
		v5.setColorOpaque_I(ReikaColorAPI.getColorWithBrightnessMultiplier(cd, 1));
		v5.addVertexWithUV(-r, 0.005, -r, 0, 0);

		v5.setColorOpaque_I(ReikaColorAPI.getColorWithBrightnessMultiplier(0xffffff, f2a));
		v5.addVertexWithUV(-r, 0.005, r+1, 0, 1);
		v5.setColorOpaque_I(ReikaColorAPI.getColorWithBrightnessMultiplier(0xffffff, f2b));
		v5.addVertexWithUV(r+1, 0.005, r+1, 1, 1);
		v5.setColorOpaque_I(ReikaColorAPI.getColorWithBrightnessMultiplier(0xffffff, f2c));
		v5.addVertexWithUV(r+1, 0.005, -r, 1, 0);
		v5.setColorOpaque_I(ReikaColorAPI.getColorWithBrightnessMultiplier(0xffffff, f2d));
		v5.addVertexWithUV(-r, 0.005, -r, 0, 0);

		v5.draw();
	}
}
