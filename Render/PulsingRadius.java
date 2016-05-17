/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render;

import java.util.Random;

import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.Instantiable.Spline;
import Reika.DragonAPI.Instantiable.Spline.SplineAnchor;
import Reika.DragonAPI.Instantiable.Spline.SplineType;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;

public class PulsingRadius {

	private static final Random rand = new Random();

	private final Spline spline;

	public final int density;
	public final double radius;
	public final double radiusVariance;

	public PulsingRadius(double radius, double var) {
		density = 60;
		this.radius = radius;
		radiusVariance = var;
		spline = new Spline(SplineType.CENTRIPETAL);

		for (int i = 0; i < density; i++) {
			spline.addPoint(new RadiusPoint(this.radius, radiusVariance, i*360D/density));
		}
	}

	public void render(double x, double y, double z, int color) {
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		Tessellator v5 = Tessellator.instance;
		spline.render(v5, x, y, z, color, true, true, 6);
		GL11.glPopAttrib();
	}

	private static class RadiusPoint implements SplineAnchor {

		public final double baseRadius;
		public final double radiusVariation;
		public final double angle;

		private double radius;
		private double targetRadius;

		private RadiusPoint(double r, double v, double t) {
			baseRadius = r;
			radiusVariation = v;
			radius = baseRadius;
			angle = t;

			this.pickNewTarget();
		}

		@Override
		public DecimalPosition asPosition() {
			double a = Math.toRadians(angle);
			double dx = radius*Math.cos(a);
			double dz = radius*Math.sin(a);
			return new DecimalPosition(dx, 0, dz);
		}

		@Override
		public void update() {
			double dr = targetRadius-radius;

			if (this.atTarget(dr)) {
				this.pickNewTarget();
			}

			this.move(dr);
		}

		private void move(double dr) {
			if (Math.abs(dr) >= 0.05)
				radius += 0.025*Math.signum(dr);
		}

		private boolean atTarget(double dr) {
			return Math.abs(dr) < 0.05;
		}

		private void pickNewTarget() {
			targetRadius = ReikaRandomHelper.getRandomPlusMinus(baseRadius, radiusVariation);
		}
	}

	public void update() {
		spline.update();
	}

}
