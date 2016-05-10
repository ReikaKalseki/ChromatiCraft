/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ThreadedGenerator;
import Reika.DragonAPI.Instantiable.LobulatedCurve;


public class RegionMapper extends ThreadedGenerator {

	private static final double MIN_BUFFER = 200;
	private static final double MAX_BUFFER = 1500;

	//private final HashSet<Coordinate> centralRegion = new HashSet();
	//private static final HashMap<Double, Double> angleMap = new HashMap();

	private static LobulatedCurve region;

	public RegionMapper(long seed) {
		super(seed);
	}

	@Override
	public void run() throws Throwable {
		//angleMap.clear();
		StructureCalculator calc = (StructureCalculator)ThreadedGenerators.STRUCTURE.getCurrentlyActiveGenerator();
		boolean printed = false;
		while (!calc.arePositionsDetermined()) {
			Thread.sleep(50);
			if (!printed)
				ChromatiCraft.logger.log("Waiting for structure generator to finish to calculate locations...");
			printed = true;
		}
		double maxr = calc.getMaximumDistanceFromOrigin();
		double r1 = maxr+MIN_BUFFER;
		double r2 = maxr+MAX_BUFFER;
		/*LobulatedCurve c*/region = LobulatedCurve.fromMinMaxRadii(r1, r2, 20).generate(rand);
		/*
		for (double a = 0; a < 360; a += 0.25) {
			double r = c.getRadius(a);
			double ar = Math.toRadians(a);
			/*
			for (double d = 0; d <= r; d += 0.25) {
				double dx = d*Math.cos(ar);
				double dz = d*Math.sin(ar);
				centralRegion.add(new Coordinate(dx, 0, dz));
			}
		 *//*
			angleMap.put(a, r);
		}
		  */
	}

	public static boolean isPointInCentralRegion(double x, double z) {
		return region.isPointInsideCurve(x, z);
	}

	@Override
	public String getStateMessage() {
		return "Central region created; "+region.minRadius+" +/- "+(region.amplitudeVariation*20);
	}

}
