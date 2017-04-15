/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkCoordIntPair;
import Reika.ChromatiCraft.Base.ThreadedGenerator;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.Math.Spline;
import Reika.DragonAPI.Instantiable.Math.Spline.BasicSplinePoint;
import Reika.DragonAPI.Instantiable.Math.Spline.SplineType;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaVectorHelper;

public class SkyRiverGenerator extends ThreadedGenerator {

	protected static final List<Ray> rays = new ArrayList();
	protected static final MultiMap<ChunkCoordIntPair, RiverPoint> clientPoints = new MultiMap().setNullEmpty();
	protected static final MultiMap<ChunkCoordIntPair, RiverPoint> serverPoints = new MultiMap().setNullEmpty();

	public static final double RIVER_TUNNEL_RADIUS = 12.0D; // The tunnel radius of the actual SkyRiver

	private static final double INNER_RADIUS_MIN = 64;
	private static final double INNER_RADIUS_MAX = 256;

	private static final double LAYER2_RADIUS_MIN = 1024;
	private static final double LAYER2_RADIUS_MAX = 3072;

	private static final double OUTER_RADIUS_MIN = StructureCalculator.getMaximumPossibleDistance() + RegionMapper.MAX_BUFFER+512;
	private static final double OUTER_RADIUS_MAX = OUTER_RADIUS_MIN + 2048;

	private static final double FULL_RAY_ANGLE = 45;
	private static final double LAYER2_RAY_ANGLE = 22.5 / 2;
	private static final double NODE_LENGTH = 64 * 2;
	private static final double INNER_ANGLE_VARIATION = 10;
	private static final double ANGLE_VARIATION = 10 / 2;
	private static final double MAX_ANGLE_STEP = ANGLE_VARIATION / 2;
	private static final double VERTICAL_POSITION_MIN = 384;
	private static final double VERTICAL_POSITION_MAX = 512;
	private static final double ANGLE_VARIATION_FADE_RANGE = 384;

	public SkyRiverGenerator(long seed) {
		super(seed);
	}

	@Override
	public void run() throws Throwable {
		rays.clear();
		serverPoints.clear();
		SkyRiverManager.sendRiverClearPacketsToAll(); // Clear all outdated
		// SkyRivers!
		for (double d = 0; d < 360; d += FULL_RAY_ANGLE) {
			double r1 = INNER_RADIUS_MIN + rand.nextDouble() * (INNER_RADIUS_MAX - INNER_RADIUS_MIN);
			double r2 = OUTER_RADIUS_MIN + rand.nextDouble() * (OUTER_RADIUS_MAX - OUTER_RADIUS_MIN);
			this.generateRay(d, r1, r2);
		}

		for (double d = 0; d < 360; d += LAYER2_RAY_ANGLE) {
			if (d % FULL_RAY_ANGLE != 0) {
				double r1 = LAYER2_RADIUS_MIN + rand.nextDouble() * (LAYER2_RADIUS_MAX - LAYER2_RADIUS_MIN);
				double r2 = OUTER_RADIUS_MIN + rand.nextDouble() * (OUTER_RADIUS_MAX - OUTER_RADIUS_MIN);
				this.generateRay(d, r1, r2);
			}
		}

		for (Ray r : rays) {
			RiverPoint prev = null;
			for (int i = 1; i < r.points.size() - 1; i++) {
				DecimalPosition pos = r.points.get(i);
				DecimalPosition nextPos = r.points.get(i + 1);
				DecimalPosition prevPos = r.points.get(i - 1);
				ChunkCoordIntPair ch = new ChunkCoordIntPair(MathHelper.floor_double(pos.xCoord) / 16, MathHelper.floor_double(pos.zCoord) / 16);
				RiverPoint p = new RiverPoint(i, ch, pos, prevPos, nextPos);
				if (prev != null) {
					prev.nextRiverPoint = p;
				}
				prev = p;
				serverPoints.addValue(ch, p);
			}
		}
		if ((DragonAPICore.isReikasComputer() && ReikaObfuscationHelper.isDeObfEnvironment()) || DragonAPICore.debugtest) {
			// ChromatiCraft.logger.log("Generated rivers: "+serverPoints);
			//this.exportAsImage();
		}
		SkyRiverManager.startSendingRiverPacketsToAll(); // Send new SkyRivers
		// to all online
		// players.
	}

	private void exportAsImage() throws IOException {
		File f = new File(DragonAPICore.getMinecraftDirectory(), "DimensionRiver/" + seed + "L/" + System.nanoTime() + ".png");
		if (f.exists())
			f.delete();
		f.getParentFile().mkdirs();
		f.createNewFile();
		int n = 0;
		for (RiverPoint p : serverPoints.allValues(false)) {
			DecimalPosition c = p.position;
			n = Math.max(n, Math.max(1 + (int)Math.abs(c.xCoord), 1 + (int)Math.abs(c.zCoord)));
		}
		n /= 5;
		BufferedImage img = new BufferedImage(n * 2 + 1, n * 2 + 1, BufferedImage.TYPE_INT_ARGB);
		for (RiverPoint p : serverPoints.allValues(false)) {
			int x = (int)p.position.xCoord / 5 + n;
			int z = (int)p.position.zCoord / 5 + n;
			for (int i = -1; i <= 1; i++) {
				for (int k = -1; k <= 1; k++) {
					try {
						img.setRGB(x + i, z + k, 0xff000000);
					}
					catch (Exception e) {

					}
				}
			}
		}
		ImageIO.write(img, "png", f);
	}

	public static Collection<RiverPoint> getPointsForChunk(int x, int z, boolean isServerSide) {
		ChunkCoordIntPair pos = new ChunkCoordIntPair(x, z);
		MultiMap<ChunkCoordIntPair, RiverPoint> points = isServerSide ? serverPoints : clientPoints;
		Collection<RiverPoint> c = points.get(pos);
		return c != null ? Collections.unmodifiableCollection(c) : null;
	}

	public static Collection<RiverPoint> getPointsWithin(EntityPlayer ep, double range, boolean isServerSide) {
		int x = MathHelper.floor_double(ep.posX) / 16;
		int z = MathHelper.floor_double(ep.posZ) / 16;
		int chRange = MathHelper.floor_double(range) / 16; // We're interested
		// in the
		// chunkRadius
		// though... Even if
		// it might not be
		// 100% accurate.
		Collection<RiverPoint> c2 = new LinkedList();
		if (chRange == 0) {
			Collection<RiverPoint> c = getPointsForChunk(x, z, isServerSide);
			if (c != null)
				c2.addAll(c);
		}
		else {
			for (int xDiff = -chRange - 1; xDiff <= chRange; xDiff++) {
				for (int zDiff = -chRange - 1; zDiff <= chRange; zDiff++) {
					Collection<RiverPoint> c = getPointsForChunk(x + xDiff, z + zDiff, isServerSide);
					if (c != null)
						c2.addAll(c);
				}
			}
		}
		return c2;
	}

	public static boolean isWithinSkyRiver(EntityPlayer player, boolean isServerSide) {
		RiverPoint closest = getClosestPoint(player, 32, isServerSide);
		return isWithinSkyRiver(player, closest);
	}

	public static boolean isWithinSkyRiver(EntityPlayer player, RiverPoint closestPoint) {
		DecimalPosition plPos = new DecimalPosition(player);
		if (closestPoint == null)
			return false; // No point in range? not even close to a river.
		return isBetween(closestPoint.prev, closestPoint.position, plPos) || isBetween(closestPoint.position, closestPoint.next, plPos);
	}

	public static boolean isBetween(DecimalPosition pos1, DecimalPosition pos2, DecimalPosition toCheck) {
		double dst = ReikaVectorHelper.getDistFromPointToLine(pos1.xCoord, pos1.yCoord, pos1.zCoord, pos2.xCoord, pos2.yCoord, pos2.zCoord, toCheck.xCoord, toCheck.yCoord, toCheck.zCoord);
		return dst < RIVER_TUNNEL_RADIUS;
	}

	public static RiverPoint getClosestPoint(EntityPlayer ep, double range, boolean isServerSide) {
		Collection<RiverPoint> c = getPointsWithin(ep, range, isServerSide);
		Double d = Double.POSITIVE_INFINITY;
		RiverPoint cl = null;
		for (RiverPoint p : c) {
			double dist = ep.getDistanceSq(p.position.xCoord, p.position.yCoord, p.position.zCoord);
			if (dist < d && dist <= range * range) {
				d = dist;
				cl = p;
			}
		}
		return cl;
	}

	private void generateRay(double ang, double r1, double r2) {
		Ray r = new Ray();
		r.lastAngle = ang;
		for (double d = r1; d <= r2; d += Math.max(NODE_LENGTH, 2 * Math.sqrt(d))) {
			double var = ANGLE_VARIATION;
			if (d < LAYER2_RADIUS_MIN) {
				if (d < LAYER2_RADIUS_MIN - ANGLE_VARIATION_FADE_RANGE) {
					var = INNER_ANGLE_VARIATION;
				}
				else {
					var = ReikaMathLibrary.linterpolate(d, LAYER2_RADIUS_MIN - ANGLE_VARIATION_FADE_RANGE, LAYER2_RADIUS_MIN, INNER_ANGLE_VARIATION, ANGLE_VARIATION);
					// ReikaJavaLibrary.pConsole("Interpolating @ "+d+" for "+ang);
				}
			}
			double a = ang + rand.nextDouble() * var * 2 - var;
			while (Math.abs(a - r.lastAngle) > MAX_ANGLE_STEP)
				a = ang + rand.nextDouble() * var * 2 - var;
			r.lastAngle = a;
			a = Math.toRadians(a);
			double x = d * Math.cos(a);
			double z = d * Math.sin(a);
			double y = VERTICAL_POSITION_MIN + rand.nextDouble() * (VERTICAL_POSITION_MAX - VERTICAL_POSITION_MIN);
			r.addPoint(x, y, z);
			// ReikaJavaLibrary.pConsole("For ray "+ang+" @ "+d+", a="+Math.toDegrees(a)+" out of "+var);
		}
		if (r.points.size() <= 2)
			throw new RuntimeException(r1 + ">" + r2 + "@" + ang);

		r.spline();
		r.rebuildWithMaxDst(Ray.MAX_POINT_DST);
		rays.add(r);
	}

	@Override
	public String getStateMessage() {
		return "Generated sky rivers, with " + rays.size() + " rays and " + serverPoints.totalSize() + " points.";
	}

	protected static class Ray {

		private static final double MAX_POINT_DST = 18.0D;
		private List<DecimalPosition> points = new ArrayList();
		private double lastAngle;

		private void addPoint(double x, double y, double z) {
			points.add(new DecimalPosition(x, y, z));
		}

		private void spline() {
			Spline s = new Spline(SplineType.CHORDAL);
			for (DecimalPosition p : points) {
				s.addPoint(new BasicSplinePoint(p));
			}
			points = s.get(/* 128 */8, false);
		}

		public void rebuildWithMaxDst(double maxDst) {
			List<DecimalPosition> newPoints = new ArrayList<>();
			for (int i = 0; i < points.size() - 1; i++) {
				DecimalPosition pos = points.get(i);
				DecimalPosition next = points.get(i + 1);
				double dst = pos.getDistanceTo(next);
				if (dst < maxDst) {
					newPoints.add(pos);
				}
				else {
					double pointsToAdd = Math.floor(dst / maxDst);
					Vec3 vec = Vec3.createVectorHelper((next.xCoord - pos.xCoord) / pointsToAdd, (next.yCoord - pos.yCoord) / pointsToAdd, (next.zCoord - pos.zCoord) / pointsToAdd);
					for (int j = 0; j < Math.round(pointsToAdd); j++) {
						newPoints.add(new DecimalPosition(pos.xCoord + (vec.xCoord * j), pos.yCoord + (vec.yCoord * j), pos.zCoord + (vec.zCoord * j)));
					}
				}
			}
			newPoints.add(points.get(points.size() - 1)); // Last one.
			points = newPoints;
		}

		@Override
		public String toString() {
			return points.toString();
		}

		protected List<DecimalPosition> getPoints() {
			return Collections.unmodifiableList(points);
		}

		protected void writeToPktNBT(NBTTagCompound cmp) {
			NBTTagList list = new NBTTagList();
			for (DecimalPosition point : points) {
				list.appendTag(point.writeToTag());
			}
			cmp.setTag("list", list);
		}

		protected static Ray readFromPktNBT(NBTTagCompound cmp) {
			Ray r = new Ray();
			NBTTagList list = cmp.getTagList("list", 10);
			for (int i = 0; i < list.tagCount(); i++) {
				r.points.add(DecimalPosition.readTag(list.getCompoundTagAt(i)));
			}
			return r;
		}
	}

	public static class RiverPoint {

		private final ChunkCoordIntPair chunk;
		public final DecimalPosition position;
		public final DecimalPosition next;
		public final DecimalPosition prev;

		public final int positionID;

		public RiverPoint nextRiverPoint; // Util nextNode.

		public RiverPoint(int id, ChunkCoordIntPair ch, DecimalPosition pos, DecimalPosition prev, DecimalPosition next) {
			positionID = id;
			chunk = ch;
			position = pos;
			this.next = next;
			this.prev = prev;
		}

		@Override
		public String toString() {
			return prev + " > " + position + " > " + next;
		}

		/* public static RiverPoint readFromNBT(NBTTagCompound tag) {
		 * ChunkCoordIntPair ch = new ChunkCoordIntPair(tag.getInteger("cx"),
		 * tag.getInteger("cz")); return new RiverPoint(ch,
		 * DecimalPosition.readFromNBT("pos", tag),
		 * DecimalPosition.readFromNBT("pre", tag),
		 * DecimalPosition.readFromNBT("next", tag)); }
		 * 
		 * public void writeToNBT(NBTTagCompound tag) { tag.setInteger("cx",
		 * chunk.chunkXPos); tag.setInteger("cz", chunk.chunkZPos);
		 * position.writeToNBT("pos", tag); next.writeToNBT("next", tag);
		 * prev.writeToNBT("prev", tag); }
		 * 
		 * public void writeToBuf(ByteBuf buf) { buf.writeInt(chunk.chunkXPos);
		 * buf.writeInt(chunk.chunkZPos); position.writeToBuf(buf);
		 * next.writeToBuf(buf); prev.writeToBuf(buf); }
		 * 
		 * public static RiverPoint readFromBuf(ByteBuf buf) { int cx =
		 * buf.readInt(); int cz = buf.readInt(); DecimalPosition pos =
		 * DecimalPosition.readFromBuf(buf); DecimalPosition nex =
		 * DecimalPosition.readFromBuf(buf); DecimalPosition pre =
		 * DecimalPosition.readFromBuf(buf); return new RiverPoint(new
		 * ChunkCoordIntPair(cx, cz), pos, pre, nex); } */

	}

}
