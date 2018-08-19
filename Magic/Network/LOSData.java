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
	final Set<Coordinate> blocks;

	LOSData(boolean los, Set<Coordinate> ray) {
		hasLineOfSight = los;
		blocks = ray;
	}

}
