/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2018
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic.Network;

import java.util.Set;

import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;


public class LOSData {

	public final boolean hasLineOfSight;
	public final boolean canRain;
	final Set<Coordinate> blocks;

	LOSData(boolean los, boolean rain, Set<Coordinate> ray) {
		hasLineOfSight = los;
		canRain = rain;
		blocks = ray;
	}

}
