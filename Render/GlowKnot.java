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

import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.IIcon;

import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Math.Spline;
import Reika.DragonAPI.Instantiable.Math.Spline.SplineAnchor;
import Reika.DragonAPI.Instantiable.Math.Spline.SplineType;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;

public class GlowKnot {

	private static final Random rand = new Random();

	private final Spline spline;

	public final int density;
	public final double size;

	public GlowKnot(double size) {
		density = 48;
		this.size = size;
		spline = new Spline(SplineType.CENTRIPETAL);

		for (int i = 0; i < density; i++) {
			double phi = rand.nextDouble()*360;
			double theta = rand.nextDouble()*360;
			spline.addPoint(new KnotPoint(size, phi, theta, size));
		}
	}

	public void render(double x, double y, double z, int color, boolean inworld) {
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		Tessellator v5 = Tessellator.instance;
		spline.render(v5, 0.5, 0.5, 0.5, color, inworld, true, 32, 1, BlendMode.DEFAULT);

		IIcon ico = ChromaIcons.FADE.getIcon();
		float u = ico.getMinU();
		float v = ico.getMinV();
		float du = ico.getMaxU();
		float dv = ico.getMaxV();
		BlendMode.ADDITIVEDARK.apply();
		ReikaTextureHelper.bindTerrainTexture();
		GL11.glPushMatrix();
		GL11.glTranslated(0.5, 0.5, 0.5);

		if (inworld) {
			RenderManager rm = RenderManager.instance;
			double dx = x-RenderManager.renderPosX;
			double dy = y-RenderManager.renderPosY;
			double dz = z-RenderManager.renderPosZ;
			double[] angs = ReikaPhysicsHelper.cartesianToPolar(dx, dy, dz);
			GL11.glRotated(angs[2], 0, 1, 0);
			GL11.glRotated(90-angs[1], 1, 0, 0);
		}

		double d = 1.25;

		double pz = 0.05;

		v5.startDrawingQuads();
		int a = 160;
		v5.setColorRGBA_I(ReikaColorAPI.getColorWithBrightnessMultiplier(color, a/255F), a);
		v5.addVertexWithUV(-d, -d, pz, u, v);
		v5.addVertexWithUV(d, -d, pz, du, v);
		v5.addVertexWithUV(d, d, pz, du, dv);
		v5.addVertexWithUV(-d, d, pz, u, dv);
		v5.draw();

		BlendMode.DEFAULT.apply();
		GL11.glPopMatrix();

		GL11.glEnable(GL11.GL_LIGHTING);
	}

	private static class KnotPoint implements SplineAnchor {

		private double radius;
		private double theta;
		private double phi;

		public final double maxSize;

		private double targetRadius;
		private double targetTheta;
		private double targetPhi;

		private KnotPoint(double r, double t, double p, double size) {
			radius = r;
			theta = t;
			phi = p;
			maxSize = size;

			this.pickNewTarget();
		}

		@Override
		public DecimalPosition asPosition() {
			double[] dat = ReikaPhysicsHelper.polarToCartesian(radius, theta, phi);
			return new DecimalPosition(dat[0], dat[1], dat[2]);
		}

		@Override
		public void update() {
			double dr = targetRadius-radius;
			double dt = targetTheta-theta;
			double dp = targetPhi-phi;

			if (this.atTarget(dr, dt, dp)) {
				this.pickNewTarget();
			}

			this.move(dr, dt, dp);
		}

		private void move(double dr, double dt, double dp) {
			//ReikaJavaLibrary.pConsole(this+":"+dr+":"+this.atTarget(dr, dt, dp));
			if (Math.abs(dr) >= 0.05)
				radius += 0.025*Math.signum(dr);//Math.max(0.0125, Math.abs(dr)*0.03125*0.03125*0.03125)*Math.signum(dr);
			if (Math.abs(dt) >= 1)
				theta += 0.25*Math.signum(dt);//Math.max(0.125, Math.abs(dt)*0.03125*0.03125*0.03125)*Math.signum(dt);
			if (Math.abs(dp) >= 1)
				phi += 0.25*Math.signum(dp);//Math.max(0.125, Math.abs(dp)*0.03125*0.03125*0.03125)*Math.signum(dp);
		}

		private boolean atTarget(double dr, double dt, double dp) {
			return Math.abs(dr) < 0.05 && Math.abs(dt) < 1 && Math.abs(dp) < 1;
		}

		private void pickNewTarget() {
			targetRadius = ReikaRandomHelper.getRandomPlusMinus(maxSize, maxSize/16D);//rand.nextDouble()*maxSize;
			targetTheta = rand.nextDouble()*360;
			targetPhi = rand.nextDouble()*360;
		}
	}

	public void update() {
		spline.update();
	}

}
