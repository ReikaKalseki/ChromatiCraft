/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Rendering;

import java.util.List;

import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.Instantiable.Spline;
import Reika.DragonAPI.Instantiable.Spline.BasicSplinePoint;
import Reika.DragonAPI.Instantiable.Spline.SplineType;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Aurora {

	private static final int HEIGHT = 8;

	private final DecimalPosition point1;
	private final DecimalPosition point2;

	private final double length;
	private final double segmentSize;
	//private final LightningBolt shape;
	private AuroraSpline shape;

	public final int color1;
	public final int color2;

	public Aurora(int c1, int c2, double speed, /*double variance, double segSize, */double x1, double y1, double z1, double x2, double y2, double z2) {
		point1 = new DecimalPosition(x1, y1, z1);
		point2 = new DecimalPosition(x2, y2, z2);
		length = point2.getDistanceTo(point1);
		segmentSize = 16;//segSize;
		int n = (int)Math.round(length/segmentSize);
		shape = new AuroraSpline(point1, point2, n, speed, segmentSize/3D/*variance*/);
		color1 = c1;
		color2 = c2;
	}

	public void update() {
		shape.update();
	}

	public void render() {
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_LIGHTING);
		BlendMode.ADDITIVEDARK.apply();
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		ReikaRenderHelper.disableEntityLighting();

		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/aurora2.png");

		Tessellator v5 = Tessellator.instance;
		v5.startDrawingQuads();
		v5.setBrightness(240);

		double t = System.currentTimeMillis()/200D;
		int f = (int)(t%32);
		double u = (f%4)*0.125;
		double v = (f/4)*0.25;
		double du = u+0.125;
		double dv = v+0.25;

		int steps = 32;
		List<DecimalPosition> li = shape.spline.get(steps, false);
		for (int i = 0; i < li.size()-1; i++) {
			DecimalPosition pos1 = li.get(i);
			DecimalPosition pos2 = li.get(i+1);

			double y1 = point1.yCoord+(point2.yCoord-point1.yCoord)*(i/(double)li.size());
			double y2 = point1.yCoord+(point2.yCoord-point1.yCoord)*((i+1)/(double)li.size());

			int c = (i-1)%steps;
			double u1 = u+c*(du-u)/steps;
			double u2 = u1+(du-u)/steps;

			double v1 = dv;
			double v2 = v;

			v5.setColorOpaque_I(color1);
			v5.addVertexWithUV(pos1.xCoord, y1, pos1.zCoord, u1, v1);

			v5.setColorOpaque_I(color2);
			v5.addVertexWithUV(pos1.xCoord, y1+HEIGHT, pos1.zCoord, u1, v2);
			v5.addVertexWithUV(pos2.xCoord, y2+HEIGHT, pos2.zCoord, u2, v2);

			v5.setColorOpaque_I(color1);
			v5.addVertexWithUV(pos2.xCoord, y2, pos2.zCoord, u2, v1);
		}

		v5.draw();
		GL11.glPopMatrix();
		GL11.glPopAttrib();
	}

	private static class AuroraSpline {

		private final Spline spline;

		private AuroraSpline(DecimalPosition p1, DecimalPosition p2, int n, double vel, double var) {
			spline = new Spline(SplineType.CHORDAL);

			double dx = p2.xCoord-p1.xCoord;
			double dy = p2.yCoord-p1.yCoord;
			double dz = p2.zCoord-p1.zCoord;
			double ang = Math.toRadians(-ReikaPhysicsHelper.cartesianToPolar(dx, dy, dz)[2]);
			double fx = Math.cos(ang);
			double fz = Math.sin(ang);
			for (int i = 0; i <= n; i++) {
				boolean end = i == 0 || i == n;
				double f = i/(double)n;
				DecimalPosition mid = DecimalPosition.interpolate(p1, p2, f);
				spline.addPoint(new AuroraPoint(mid, end ? 0 : var, end ? 0 : vel, p1, dx, dy, dz, f, fx, fz));
			}
		}

		private void update() {
			spline.update();
		}

	}

	private static class AuroraPoint extends BasicSplinePoint {

		private final double velocity;
		private final double variance;
		private double tolerance = 0.25;

		private double targetOffset;
		private double offset;

		private final double distanceX;
		private final double distanceY;
		private final double distanceZ;

		private final DecimalPosition origin;
		private final double distanceFraction;
		private final double xFactor;
		private final double zFactor;

		private AuroraPoint(DecimalPosition pos, double var, double vel, DecimalPosition o, double dxTot, double dyTot, double dzTot, double f, double fx, double fz) {
			super(pos.xCoord, pos.yCoord, pos.zCoord);
			variance = var;
			velocity = vel;

			distanceX = dxTot;
			distanceY = dyTot;
			distanceZ = dzTot;
			distanceFraction = f;
			origin = o;
			xFactor = fx;
			zFactor = fz;

			offset = ReikaRandomHelper.getRandomPlusMinus(0, variance);
			this.pickNewTarget();
		}

		@Override
		public void update() {
			double d = targetOffset-offset;

			tolerance = 1;

			if (this.atTarget(d)) {
				this.pickNewTarget();
			}

			posY = origin.yCoord+distanceFraction*distanceY;
			posX = origin.xCoord+distanceFraction*distanceX+offset*xFactor;
			posZ = origin.zCoord+distanceFraction*distanceZ+offset*zFactor;

			this.move(d);
		}

		private void move(double d) {
			//ReikaJavaLibrary.pConsole(dx+":"+dy+":"+dz+" from "+targetX+":"+targetY+":"+targetZ+" @ "+posX+":"+posY+":"+posZ);
			if (Math.abs(d) >= tolerance)
				offset += velocity/48D*Math.signum(d);
		}

		private boolean atTarget(double d) {
			return Math.abs(d) < tolerance;
		}

		private void pickNewTarget() {
			targetOffset = ReikaRandomHelper.getRandomPlusMinus(0, variance);
		}

	}

}
