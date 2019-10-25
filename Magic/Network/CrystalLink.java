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

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Magic.Interfaces.CrystalNetworkTile;
import Reika.ChromatiCraft.Magic.Interfaces.LinkWatchingRepeater;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldChunk;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;

public final class CrystalLink {

	public final WorldLocation loc1;
	public final WorldLocation loc2;
	final HashSet<WorldChunk> chunks = new HashSet();
	private final HashSet<Coordinate> locations = new HashSet();

	boolean hasLOS = false;
	boolean isRainable = false;
	public final double length;
	private boolean activeEndpoint1 = false;
	private boolean activeEndpoint2 = false;

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
		activeEndpoint1 = l1.getTileEntity() instanceof LinkWatchingRepeater;
		activeEndpoint2 = l2.getTileEntity() instanceof LinkWatchingRepeater;
		length = l1.getDistanceTo(l2);
	}

	private void recalculateLOS() {
		if (!needsCalculation)
			return;
		needsCalculation = false;
		LOSData los = PylonFinder.lineOfSight(loc1, loc2);
		hasLOS = los.hasLineOfSight;
		locations.clear();
		locations.addAll(los.blocks);
		isRainable = los.canRain;
		if (activeEndpoint1 || activeEndpoint2)
			this.updateEndpoints();
		//ReikaJavaLibrary.pConsole("Recalculating LOS for "+this+" (#"+System.identityHashCode(this)+"): "+hasLOS);
	}

	private void updateEndpoints() {
		if (activeEndpoint1) {
			TileEntity te1 = loc1.getTileEntity();
			if (te1 instanceof LinkWatchingRepeater) {
				((LinkWatchingRepeater)te1).onLinkRecalculated(this);
			}
		}
		if (activeEndpoint2) {
			TileEntity te2 = loc2.getTileEntity();
			if (te2 instanceof LinkWatchingRepeater) {
				((LinkWatchingRepeater)te2).onLinkRecalculated(this);
			}
		}
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

	public boolean isRainable() {
		return isRainable;
	}

	public boolean hasLOS() {
		return hasLOS;
	}

	/** Returns loc2 if the tile is on neither end */
	public CrystalNetworkTile getOtherEnd(CrystalNetworkTile te) {
		return PylonFinder.getNetTileAt(loc1.equals(te.getWorld(), te.getX(), te.getY(), te.getZ()) ? loc2 : loc1, true);
	}

}
