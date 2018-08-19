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

import java.util.HashSet;

import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldChunk;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;

final class CrystalLink {

	public final WorldLocation loc1;
	public final WorldLocation loc2;
	final HashSet<WorldChunk> chunks = new HashSet();
	private final HashSet<Coordinate> locations = new HashSet();

	boolean hasLOS = false;
	boolean needsCalculation = true;

	CrystalLink(WorldLocation l1, WorldLocation l2) {
		loc1 = l1;
		loc2 = l2;
		double dd = l1.getDistanceTo(l2);
		World world = l1.getWorld();
		for (int i = 0; i < dd; i++) {
			int x = MathHelper.floor_double(l1.xCoord+i*(l2.xCoord-l1.xCoord)/dd);
			int z = MathHelper.floor_double(l1.zCoord+i*(l2.zCoord-l1.zCoord)/dd);
			WorldChunk ch = new WorldChunk(world, new ChunkCoordIntPair(x >> 4, z >> 4));
			if (!chunks.contains(ch))
				chunks.add(ch);
		}
	}

	private void recalculateLOS() {
		if (!needsCalculation)
			return;
		needsCalculation = false;
		LOSData los = PylonFinder.lineOfSight(loc1, loc2);
		hasLOS = los.hasLineOfSight;
		locations.clear();
		locations.addAll(los.blocks);
		//ReikaJavaLibrary.pConsole("Recalculating LOS for "+this+" (#"+System.identityHashCode(this)+"): "+hasLOS);
	}

	public boolean isChunkInPath(WorldChunk wc) {
		return chunks.contains(wc);
	}

	public boolean containsBlock(Coordinate c) {
		return locations.contains(c);
	}

	@Override
	public final int hashCode() {
		return loc1.hashCode()^loc2.hashCode();
	}

	@Override
	public final boolean equals(Object o) {
		if (o instanceof CrystalLink) {
			CrystalLink l = (CrystalLink)o;
			return (l.loc1.equals(loc1) && l.loc2.equals(loc2)) || (l.loc1.equals(loc2) && l.loc2.equals(loc1)); //order irrelevant
		}
		return false;
	}

	@Override
	public final String toString() {
		return "["+loc1+" > "+loc2+"]";
	}

	final boolean hasLineOfSight() {
		if (needsCalculation)
			this.recalculateLOS();
		//ReikaJavaLibrary.pConsole("Returning LOS for "+this+" (#"+System.identityHashCode(this)+"): "+hasLOS);
		return hasLOS;
	}

}
