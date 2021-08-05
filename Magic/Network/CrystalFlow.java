/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic.Network;

import java.util.ArrayList;
import java.util.List;

import Reika.ChromatiCraft.Auxiliary.CrystalNetworkLogger;
import Reika.ChromatiCraft.Auxiliary.CrystalNetworkLogger.LoggingLevel;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalNetworkTile;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalReceiver;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalRepeater;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalSource;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalTransmitter;
import Reika.ChromatiCraft.Magic.Interfaces.DynamicRepeater;
import Reika.ChromatiCraft.Magic.Interfaces.ReactiveRepeater;
import Reika.ChromatiCraft.Magic.Interfaces.WrapperTile;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;

public final class CrystalFlow extends CrystalPath {

	private static final int MIN_THROUGHPUT = 10;

	public final int maxFlow;
	private final int requestedAmount;
	public final int totalCost;
	public final CrystalReceiver receiver;
	private final int throughputLimit;

	private int remainingAmount;

	private int throttle = Integer.MAX_VALUE;

	CrystalFlow(CrystalNetworker net, CrystalPath p, CrystalReceiver r, int amt, int maxthru) {
		this(net, r, p.element, amt, p.nodes, maxthru);
	}

	protected CrystalFlow(CrystalNetworker net, CrystalReceiver r, CrystalElement e, int amt, List li, int maxthru) {
		super(net, !(r instanceof WrapperTile), e, li);
		requestedAmount = amt;
		totalCost = requestedAmount+this.getSignalLoss();
		remainingAmount = totalCost;
		receiver = r;
		CrystalNetworkLogger.logPathCalculation("maxthru", maxthru);
		throughputLimit = maxthru;
		maxFlow = this.calcEffectiveThroughput(maxthru);
		if (maxFlow > 0 || totalCost == 0)
			this.buildLeyLines();
		for (PathNode p : nodes) {
			p.flush();
		}
	}

	private int calcEffectiveThroughput(int maxthru) {
		int insured = MIN_THROUGHPUT;
		int bonus = 0;
		int base = Math.min(transmitter.maxThroughput(), receiver.maxThroughput());
		ArrayList<DynamicRepeater> dynamics = new ArrayList();
		for (int i = 1; i < nodes.size()-1; i++) {
			PathNode pn = nodes.get(i);
			CrystalNetworkTile te = nodes.get(i).getTile(true);
			base = Math.min(base, te.maxThroughput());
			if (pn.isRepeater()) {
				insured = Math.max(insured, ((CrystalRepeater)te).getThoughputInsurance());
				bonus += ((CrystalRepeater)te).getThoughputBonus(hasLocus);
				if (DynamicRepeater.class.isAssignableFrom(pn.tileClass)) {
					dynamics.add((DynamicRepeater)te);
				}
			}
		}

		if (base == 0)
			return 0;

		int result = Math.min(Math.max(insured, base-this.getThroughputPenalty(base)+bonus), Math.min(maxthru, base));

		if (CrystalNetworkLogger.getLogLevel().isAtLeast(LoggingLevel.PATHCALC)) {
			CrystalNetworkLogger.logPathCalculation("base", base);
			CrystalNetworkLogger.logPathCalculation("bonus", bonus);
			CrystalNetworkLogger.logPathCalculation("insured", insured);
			CrystalNetworkLogger.logPathCalculation("max/base", Math.min(maxthru, base));
			CrystalNetworkLogger.logPathCalculation("totalthru", result);
		}

		for (DynamicRepeater dr : dynamics) {
			result = dr.getModifiedThoughput(result, transmitter, receiver);
		}
		return result;
	}

	private int getThroughputPenalty(int rawthru) {
		int att = this.getSignalLoss();
		if (att <= 1)
			return 0;
		int amt = (int)Math.pow(att/80, 1.5+0.5*(1-this.getOptimizationFactor())); //raw, directly determined from attenuation & path optimization
		double x = rawthru/att-1;
		if (att > rawthru) { //add to base_rem
			double y = -Math.sqrt(-x)*(rawthru-amt-MIN_THROUGHPUT);
			amt += Math.round(y);
		}
		else if (att < rawthru) { //can combat base_rem
			x = Math.min(x, 1);
			double y = Math.sqrt(x)*amt;
			amt -= Math.round(y);
		}
		return Math.max(0, amt);
	}

	CrystalPath asPath() {
		return new CrystalPath(network, hasRealTarget, element, nodes);
	}

	@Override
	protected void initialize() {
		super.initialize();
	}

	private void buildLeyLines() {
		//nodes.getFirst().getTileEntity().NOT A TILE
		PathNode locs = nodes.get(nodes.size()-2);
		CrystalReceiver r = (CrystalReceiver)locs.getTile(true);
		DecimalPosition offset = r.getTargetRenderOffset(element);
		double sx = offset != null ? offset.xCoord : 0;
		double sy = offset != null ? offset.yCoord : 0;
		double sz = offset != null ? offset.zCoord : 0;
		CrystalSource src = (CrystalSource)nodes.get(nodes.size()-1).getTile(true);
		src.addTarget(locs.location, element, sx, sy, sz, r.getIncomingBeamRadius(), src.getMaximumBeamRadius());
		for (int i = 1; i < nodes.size()-1; i++) {
			CrystalNetworkTile te = nodes.get(i).getTile(true);
			if (te instanceof CrystalTransmitter) {
				PathNode tg = nodes.get(i-1);
				r = (CrystalReceiver)tg.getTile(true);
				offset = r.getTargetRenderOffset(element);
				double dx = offset != null ? offset.xCoord : 0;
				double dy = offset != null ? offset.yCoord : 0;
				double dz = offset != null ? offset.zCoord : 0;
				((CrystalTransmitter)te).addTarget(tg.location, element, dx, dy, dz, r.getIncomingBeamRadius(), src.getMaximumBeamRadius());
			}/*
					if (te instanceof CrystalReceiver) {
						WorldLocation src = nodes.get(i+1);
						te.markSource(src);
					}*/
		}
	}

	private String getTiles() {
		StringBuilder sb = new StringBuilder();
		sb.append("(0 is receiver, size-1 is source) N=");
		sb.append(nodes.size());
		sb.append("[");
		int i = 0;
		for (PathNode loc : nodes) {
			sb.append(i);
			sb.append("=");
			sb.append(loc.toString());
			sb.append(";");
			i++;
		}
		sb.append("]");
		return sb.toString();
	}

	void resetTiles() {
		((CrystalSource)nodes.get(nodes.size()-1).getTile(true)).removeTarget(nodes.get(nodes.size()-2).location, element);
		for (int i = 1; i < nodes.size()-1; i++) {
			CrystalNetworkTile te = nodes.get(i).getTile(true);
			if (te instanceof CrystalTransmitter) {
				WorldLocation tg = nodes.get(i-1).location;
				((CrystalTransmitter)te).removeTarget(tg, element);
			}/*
			if (te instanceof CrystalReceiver) {
				te.clearSource();
			}*/
		}
	}

	public boolean isComplete() {
		return remainingAmount <= 0;
	}

	public int getRemainingLumens() {
		return remainingAmount;
	}

	public int estimateLifetime() {
		return this.getRemainingLumens()/this.getDrainThisTick();
	}

	int drain() {
		int ret = Math.min(transmitter.getEnergy(element), this.getDrainThisTick());
		if (ret <= 0)
			return 0;
		remainingAmount -= ret;
		return ret;
	}

	public int getDrainThisTick() {
		return Math.min(Math.min(Math.min(throttle, maxFlow), transmitter.maxThroughput()), remainingAmount);
	}

	void tickRepeaters(int amt) {
		if (this.hasReactiveRepeaters()) {
			for (int i = 1; i < nodes.size()-1; i++) {
				if (ReactiveRepeater.class.isAssignableFrom(nodes.get(i).tileClass)) {
					CrystalNetworkTile te = nodes.get(i).getTile(true);
					((ReactiveRepeater)te).onTransfer(transmitter, receiver, element, amt);
				}
			}
		}
	}

	@Override
	CrystalPath cleanExtraEndJumps() {
		return new CrystalFlow(network, super.cleanExtraEndJumps(), receiver, requestedAmount, throughputLimit);
	}

}
