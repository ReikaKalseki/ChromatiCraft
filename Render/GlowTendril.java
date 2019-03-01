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
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;

import Reika.ChromatiCraft.Auxiliary.ChromaFX;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Math.Spline;
import Reika.DragonAPI.Instantiable.Math.Spline.BasicSplinePoint;
import Reika.DragonAPI.Instantiable.Math.Spline.SplineAnchor;
import Reika.DragonAPI.Instantiable.Math.Spline.SplineType;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;

public class GlowTendril {

	private static final Random rand = new Random();

	public final double size;
	private final ArrayList<Tendril> tendrils = new ArrayList();

	public GlowTendril(double size, int count) {
		this.size = size;

		for (int i = 0; i < count; i++) {
			Tendril t = new Tendril();
			double phi = rand.nextDouble()*360;
			double theta = rand.nextDouble()*360;
			t.spline.addPoint(new BasicSplinePoint(0, 0, 0));
			t.spline.addPoint(new TendrilPoint(size/2, phi, theta));
			t.spline.addPoint(new TendrilPoint(size, phi, theta));
			tendrils.add(t);
		}
	}

	public void render(double x, double y, double z, int color, float ptick) {
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glDisable(GL11.GL_LIGHTING);
		BlendMode.ADDITIVEDARK.apply();
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glDepthMask(false);
		Tessellator v5 = Tessellator.instance;
		for (Tendril t : tendrils)
			t.render(x, y, z, color, ptick);
		GL11.glPopAttrib();
	}

	public void update() {
		for (Tendril t : tendrils)
			t.spline.update();
	}

	private static class Tendril {

		private final Spline spline = new Spline(SplineType.CENTRIPETAL);

		private void render(double x, double y, double z, int color, float ptick) {
			//spline.render(Tessellator.instance, 0.5, 0.5, 0.5, color, true, false, 8, 1, BlendMode.ADDITIVEDARK);
			List<DecimalPosition> li = spline.get(8, false);
			for (int i = 0; i < li.size()-1; i++) {
				DecimalPosition pos1 = li.get(i);
				DecimalPosition pos2 = li.get(i+1);
				double t = 0.1875+0.0625*Math.sin(System.identityHashCode(this)+System.currentTimeMillis()/200D+i/128D);
				ChromaFX.renderBeam(pos1.xCoord+0.5, pos1.yCoord+0.5, pos1.zCoord+0.5, pos2.xCoord+0.5, pos2.yCoord+0.5, pos2.zCoord+0.5, ptick, 255, t, color);
			}
		}
	}

	private static class TendrilPoint implements SplineAnchor {

		private final double radius;

		private double theta;
		private double phi;

		private double targetTheta;
		private double targetPhi;

		private TendrilPoint(double r, double t, double p) {
			radius = r;
			theta = t;
			phi = p;

			this.pickNewTarget();
		}

		@Override
		public DecimalPosition asPosition() {
			double[] dat = ReikaPhysicsHelper.polarToCartesian(radius, theta, phi);
			return new DecimalPosition(dat[0], dat[1], dat[2]);
		}

		@Override
		public void update() {
			double dt = targetTheta-theta;
			double dp = targetPhi-phi;

			if (this.atTarget(dt, dp)) {
				this.pickNewTarget();
			}

			this.move(dt, dp);
		}

		private void move(double dt, double dp) {
			if (Math.abs(dt) >= 1)
				theta += 0.375*Math.signum(dt)*radius;
			if (Math.abs(dp) >= 1)
				phi += 0.375*Math.signum(dp)*radius;
		}

		private boolean atTarget(double dt, double dp) {
			return Math.abs(dt) < 1 && Math.abs(dp) < 1;
		}

		private void pickNewTarget() {
			targetTheta = rand.nextDouble()*360;
			targetPhi = rand.nextDouble()*360;
		}
	}

}
