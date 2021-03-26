/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import java.util.HashSet;
import java.util.UUID;

import org.apache.commons.lang3.tuple.ImmutableTriple;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Auxiliary.CrystalNetworkLogger.FlowFail;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalReceiver;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalSource;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalTransmitter;
import Reika.ChromatiCraft.Magic.Interfaces.WrapperTile;
import Reika.ChromatiCraft.Magic.Network.CrystalFlow;
import Reika.ChromatiCraft.Magic.Progression.ResearchLevel;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;


public class TemporaryCrystalReceiver implements CrystalReceiver, WrapperTile {

	public final WorldLocation location;

	public final int throughput;
	public final int range;
	public final double beamRadius;
	public final ResearchLevel level;

	private final UUID uid = UUID.randomUUID();
	private final HashSet<CrystalElement> colorLimit = new HashSet();

	private boolean destroyed = false;

	public TemporaryCrystalReceiver(TileEntity te, int t, int r, double br, ResearchLevel rl) {
		this(new WorldLocation(te), t, r, br, rl);
	}

	public TemporaryCrystalReceiver(WorldLocation loc, int t, int r, double br, ResearchLevel rl) {
		location = loc;

		throughput = t;
		range = r;
		beamRadius = br;
		level = rl;
	}

	public void destroy() {
		destroyed = true;
	}

	@Override
	public boolean isConductingElement(CrystalElement e) {
		return colorLimit.isEmpty() || colorLimit.contains(e);
	}

	@Override
	public void cachePosition() {

	}

	@Override
	public void removeFromCache() {

	}

	@Override
	public double getDistanceSqTo(double x, double y, double z) {
		return location.getSquaredDistance(x, y, z);
	}

	@Override
	public World getWorld() {
		return location.getWorld();
	}

	@Override
	public int getX() {
		return location.xCoord;
	}

	@Override
	public int getY() {
		return location.yCoord;
	}

	@Override
	public int getZ() {
		return location.zCoord;
	}

	@Override
	public int maxThroughput() {
		return throughput;
	}

	@Override
	public boolean canConduct() {
		return true;
	}

	@Override
	public UUID getUniqueID() {
		return uid;
	}

	@Override
	public UUID getPlacerUUID() {
		return null;
	}

	/*
	@Override
	public ResearchLevel getResearchTier() {
		return level;
	}
	 */

	@Override
	public ImmutableTriple<Double, Double, Double> getTargetRenderOffset(CrystalElement e) {
		return null;
	}

	@Override
	public double getIncomingBeamRadius() {
		return beamRadius;
	}

	@Override
	public int receiveElement(CrystalSource src, CrystalElement e, int amt) {
		return 1;
	}

	@Override
	public void onPathBroken(CrystalFlow p, FlowFail f) {

	}

	@Override
	public int getReceiveRange() {
		return range;
	}

	@Override
	public void onPathCompleted(CrystalFlow p) {

	}

	@Override
	public boolean existsInWorld() {
		return false;
	}

	@Override
	public Class getTileClass() {
		return this.getClass();
	}

	public boolean canConductInterdimensionally() {
		return false;
	}

	@Override
	public boolean canReceiveFrom(CrystalTransmitter r) {
		return true;
	}

	@Override
	public boolean needsLineOfSightFromTransmitter(CrystalTransmitter r) {
		return true;
	}

	@Override
	public void triggerBottleneckDisplay(int duration) {

	}

	public void addColorRestriction(CrystalElement e) {
		colorLimit.add(e);
	}

	public boolean canBeSuppliedBy(CrystalSource te, CrystalElement e) {
		return this.isConductingElement(e);
	}

	@Override
	public boolean isRemoved() {
		return destroyed;
	}

}
