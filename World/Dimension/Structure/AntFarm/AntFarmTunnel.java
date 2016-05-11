/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure.AntFarm;


import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import net.minecraft.util.MathHelper;
import Reika.ChromatiCraft.Base.StructurePiece;
import Reika.ChromatiCraft.World.Dimension.Structure.AntFarmGenerator;
import Reika.DragonAPI.Instantiable.Spline;
import Reika.DragonAPI.Instantiable.Spline.BasicSplinePoint;
import Reika.DragonAPI.Instantiable.Spline.SplineType;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Effects.LightningBolt;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;


public class AntFarmTunnel extends StructurePiece {

	public final Coordinate point1;
	public final Coordinate point2;

	public final int tunnelRadius;
	public final int curvature;

	private final HashMap<Coordinate, Boolean> blocks = new HashMap();

	public AntFarmTunnel(AntFarmGenerator a, Coordinate c1, Coordinate c2, int curve, int r) {
		super(a);

		point1 = c1;
		point2 = c2;
		curvature = curve;

		tunnelRadius = r;

		this.initialize();
	}

	private void initialize() {
		DecimalPosition d1 = new DecimalPosition(point1);
		DecimalPosition d2 = new DecimalPosition(point2);
		LightningBolt b = new LightningBolt(d1, d2, curvature); //was 6, then 2, then 3
		double len = d1.getDistanceTo(d2);
		b.variance = Math.min(6, len/8D); //was 12, L/4
		b.velocity = b.variance*2;
		b.update();
		Spline s = new Spline(SplineType.CHORDAL);
		for (int i = 0; i <= b.nsteps; i++) {
			s.addPoint(new BasicSplinePoint(b.getPosition(i)));
		}
		List<DecimalPosition> li = s.get((int)(4*len), false);
		for (DecimalPosition p : li) {
			this.generateTunnelSection(MathHelper.floor_double(p.xCoord), MathHelper.floor_double(p.yCoord), MathHelper.floor_double(p.zCoord), tunnelRadius);
		}
	}

	private void generateTunnelSection(int dx, int dy, int dz, int r) {
		for (int i = -r; i <= r; i++) {
			for (int j = -r; j <= r; j++) {
				for (int k = -r; k <= r; k++) {
					double dd = ReikaMathLibrary.py3d(i, j, k);
					if (dd <= r+0.5) {
						int ddx = dx+i;
						int ddy = dy+j;
						int ddz = dz+k;
						Coordinate c = new Coordinate(ddx, ddy, ddz);
						Boolean pre = blocks.get(c);
						blocks.put(c, (pre != null && pre.booleanValue()) || dd <= r-0.5);
					}
				}
			}
		}
	}

	public void register(AntFarmGenerator g, HashSet<Coordinate> tunnelSpaces) {
		for (Coordinate c : blocks.keySet()) {
			g.cutBlock(c, blocks.get(c));
		}
		tunnelSpaces.addAll(blocks.keySet());
	}

	public boolean intersectsWith(HashSet<Coordinate> tunnelSpaces, HashSet<Coordinate> levelSpaces) {
		for (Coordinate c : blocks.keySet()) {
			if (tunnelSpaces.contains(c) && !levelSpaces.contains(c))
				return true;
		}
		return false;
	}

	@Override
	public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {

	}

}
