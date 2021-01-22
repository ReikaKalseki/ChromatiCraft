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
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutableTriple;

import Reika.ChromatiCraft.Magic.Interfaces.ConnectivityAction;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalNetworkTile;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalReceiver;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalRepeater;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalSource;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalTransmitter;
import Reika.ChromatiCraft.Magic.Interfaces.ReactiveRepeater;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityAuraPoint;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldChunk;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;

public class CrystalPath implements Comparable<CrystalPath> {

	protected final ArrayList<PathNode> nodes;
	public final CrystalSource transmitter;
	public final WorldLocation origin;
	public final CrystalElement element;
	private final HashSet<CrystalLink> links = new HashSet();
	protected final CrystalNetworker network;
	protected final boolean hasRealTarget;
	public final boolean hasLocus;

	private int attenuation;
	private int theoreticalRange;
	private double totalDistance;
	private boolean hasReactiveRepeaters;

	private boolean wasOptimized = false;

	/** Call flush() on every PathNode at the end of your constructor! */
	protected CrystalPath(CrystalNetworker net, boolean real, CrystalElement e, List li) {
		if (li.get(0) instanceof PathNode) {
			nodes = new ArrayList(li);
		}
		else {
			nodes = this.createNodeList(li);
		}
		for (PathNode p : nodes) {
			p.cacheTile();
		}
		transmitter = (CrystalSource)nodes.get(nodes.size()-1).getTile(true);
		origin = nodes.get(0).location;
		element = e;
		network = net;
		hasRealTarget = real;
		hasLocus = TileEntityAuraPoint.isPointWithin(transmitter.getWorld(), transmitter.getX(), transmitter.getY(), transmitter.getZ(), 1024);
		this.initialize();
		//remainingAmount = amt;
		if (this.getClass() == CrystalPath.class) {
			for (PathNode p : nodes) {
				p.flush();
			}
		}
	}

	private static ArrayList<PathNode> createNodeList(List<WorldLocation> li) {
		ArrayList<PathNode> ret = new ArrayList();
		for (int i = 0; i < li.size(); i++) {
			ret.add(new PathNode(li.get(i), i, i == 0));
		}
		return ret;
	}

	private CrystalPath setOptimized() {
		wasOptimized = true;
		return this;
	}

	protected void initialize() {
		int loss = 0;
		int range = 0;
		double dist = 0;
		for (int i = 1; i < nodes.size(); i++) {
			PathNode loc = nodes.get(i);
			PathNode prev = nodes.get(i-1);
			CrystalLink l = network.getLink(prev.location, loc.location);
			CrystalNetworkTile te = loc.getTile(true); //transmitter
			CrystalNetworkTile teprev = prev.getTile(true); //receiver
			range += Math.min(((CrystalReceiver)teprev).getReceiveRange(), ((CrystalTransmitter)te).getSendRange());
			dist += Math.sqrt(te.getDistanceSqTo(teprev.getX()+0.5, teprev.getY()+0.5, teprev.getZ()+0.5));
			if (i < nodes.size()-1) {
				if (te instanceof CrystalRepeater) {
					int atten = ((CrystalRepeater)te).getSignalDegradation(hasLocus);
					if (l.isRainable() && transmitter.getWorld().isRaining())
						atten *= 1.15;
					loss += atten;
					if (te instanceof ConnectivityAction) {
						PathNode next = nodes.get(i+1);
						CrystalNetworkTile tenext = next.getTile(true);
						ConnectivityAction ca = (ConnectivityAction)te;
						ca.notifyReceivingFrom(this, (CrystalTransmitter)tenext);
						ca.notifySendingTo(this, (CrystalReceiver)teprev);
					}
					if (te instanceof ReactiveRepeater) {
						hasReactiveRepeaters = true;
					}
				}
			}
			links.add(l);
		}
		attenuation = loss;
		totalDistance = dist;
		theoreticalRange = range;
	}

	public final void addBaseAttenuation(int amt) {
		if (amt > 0)
			attenuation += amt;
	}

	public final boolean containsLink(CrystalLink l) {
		return links.contains(l);
	}

	public final HashSet<WorldChunk> getRelevantChunks() {
		HashSet<WorldChunk> ret = new HashSet();
		for (CrystalLink l : links) {
			ret.addAll(l.chunks);
		}
		return ret;
	}

	public final int getSignalLoss() {
		return attenuation;
	}

	public final boolean canTransmit() {
		return true;//transmitter.getTransmissionStrength() > attenuation;
	}

	@Override
	public final String toString() {
		return element+": to "+origin+" from "+transmitter+": "+nodes.size()+"x "+nodes.toString();
	}

	public final boolean contains(CrystalNetworkTile te) {
		return nodes.contains(new PathNode(te));
	}

	public final boolean checkLineOfSight() {
		return this.checkLineOfSight(null);
	}

	public final boolean checkLineOfSight(CrystalLink l) {
		for (int i = 0; i < nodes.size()-2; i++) {
			PathNode tgt = nodes.get(i);
			if (l == null || tgt.location.equals(l.loc1) || tgt.location.equals(l.loc2)) {
				PathNode src = nodes.get(i+1);
				if (!PylonFinder.lineOfSight(src.location, tgt.location).hasLineOfSight) {
					CrystalReceiver rec = (CrystalReceiver)tgt.getTile(true);
					CrystalTransmitter sr = (CrystalTransmitter)src.getTile(true);
					if (sr.needsLineOfSightToReceiver(rec) || rec.needsLineOfSightFromTransmitter(sr)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public final boolean stillValid() {
		if (!transmitter.canConduct() || !transmitter.isConductingElement(element)) {
			return false;
		}
		if (hasRealTarget) {
			CrystalReceiver tile = PylonFinder.getReceiverAt(origin, false);
			if (tile == null || !tile.canConduct() || !tile.isConductingElement(element))
				return false;
			if (!tile.canBeSuppliedBy(transmitter, element) || !transmitter.canSupply(tile, element))
				return false;
		}
		for (int i = hasRealTarget ? 0 : 1; i < nodes.size()-2; i++) {
			PathNode tgt = nodes.get(i);
			PathNode src = nodes.get(i+1);
			CrystalTransmitter sr = (CrystalTransmitter)src.getTile(false);
			CrystalReceiver rec = (CrystalReceiver)tgt.getTile(false);
			if (sr == null || !sr.canConduct() || !sr.isConductingElement(element)) {
				return false;
			}
			if (sr.needsLineOfSightToReceiver(rec) || rec.needsLineOfSightFromTransmitter(sr)) {
				CrystalLink l = network.getLink(tgt.location, src.location);
				if (!l.hasLineOfSight()) {
					return false;
				}
			}
		}
		return true;
	}

	public final void blink(int ticks, CrystalReceiver r) {
		CrystalSource src = (CrystalSource)nodes.get(nodes.size()-1).getTile(true);
		PathNode locs = nodes.get(nodes.size()-2);
		if (r == null)
			r = (CrystalReceiver)locs.getTile(true);
		ImmutableTriple<Double, Double, Double> offset = r.getTargetRenderOffset(element);
		double sx = offset != null ? offset.left : 0;
		double sy = offset != null ? offset.middle : 0;
		double sz = offset != null ? offset.right : 0;
		src.addSelfTickingTarget(locs.location, element, sx, sy, sz, r.getIncomingBeamRadius(), ticks);
		for (int i = 1; i < nodes.size()-1; i++) {
			CrystalNetworkTile te = nodes.get(i).getTile(true);
			if (te instanceof CrystalTransmitter) {
				PathNode tg = nodes.get(i-1);
				r = (CrystalReceiver)tg.getTile(true);
				offset = r.getTargetRenderOffset(element);
				double dx = offset != null ? offset.left : 0;
				double dy = offset != null ? offset.middle : 0;
				double dz = offset != null ? offset.right : 0;
				((CrystalTransmitter)te).addSelfTickingTarget(tg.location, element, dx, dy, dz, r.getIncomingBeamRadius(), ticks);
			}/*
			if (te instanceof CrystalReceiver) {
				WorldLocation src = nodes.get(i+1);
				te.markSource(src);
			}*/
		}
	}

	public final void endBlink() {
		CrystalSource src = (CrystalSource)nodes.get(nodes.size()-1).getTile(true);
		WorldLocation locs = nodes.get(nodes.size()-2).location;
		src.removeTarget(locs, element);
		for (int i = 1; i < nodes.size()-1; i++) {
			CrystalNetworkTile te = nodes.get(i).getTile(false);
			if (te instanceof CrystalTransmitter) {
				WorldLocation tg = nodes.get(i-1).location;
				((CrystalTransmitter)te).removeTarget(tg, element);
			}
		}
	}

	/** where 0 = worst possible and 1 == perfect */
	public final float getOptimizationFactor() {
		return (float)(theoreticalRange/totalDistance);
	}

	@Override
	public final int compareTo(CrystalPath o) {
		return PylonFinder.getSourcePriority(o.transmitter, element)-PylonFinder.getSourcePriority(transmitter, element);
	}

	@Override
	public final boolean equals(Object o) {
		if (o instanceof CrystalPath) {
			CrystalPath p = (CrystalPath)o;
			return p.element == element && p.nodes.equals(nodes);
		}
		return false;
	}

	@Override
	public final int hashCode() {
		return nodes.hashCode() ^ element.ordinal();
	}

	/** Sorted from receiver to source */
	public final ArrayList<CrystalNetworkTile> getTileList() {
		ArrayList<CrystalNetworkTile> li = new ArrayList();
		for (PathNode loc : nodes) {
			li.add(loc.getTile(true));
		}
		return li;
	}

	public final boolean hasSameEndpoints(CrystalPath p) {
		return PylonFinder.getLocation(transmitter).equals(PylonFinder.getLocation(p.transmitter)) && origin.equals(p.origin);
	}

	CrystalPath cleanExtraEndJumps() {
		return new CrystalPath(network, hasRealTarget, element, this.getCleanedNodePath());
	}

	protected final ArrayList<WorldLocation> getCleanedNodePath() {
		return cleanRoute(network, element, nodes);
	}

	public final void optimize() {
		this.optimize(Integer.MAX_VALUE);
	}

	public final CrystalPath optimize(int nsteps) {
		ArrayList<WorldLocation> li = new ArrayList(nodes);
		boolean complete = PylonFinder.optimizeRoute(network, li, nsteps, JumpOptimizationCheck.always);
		CrystalPath p = new CrystalPath(network, hasRealTarget, element, li);
		if (complete) {
			p.setOptimized();
		}
		return p;
	}

	public final boolean isOptimized() {
		return wasOptimized;
	}

	protected final boolean hasReactiveRepeaters() {
		return hasReactiveRepeaters;
	}

	static final ArrayList<WorldLocation> cleanLocRoute(CrystalNetworker net, CrystalElement e, ArrayList<WorldLocation> nodes) {
		return cleanRoute(net, e, createNodeList(nodes));
	}

	/** Reduces hop count; obeys LOS */
	static final ArrayList<WorldLocation> cleanRoute(CrystalNetworker net, CrystalElement e, ArrayList<PathNode> nodes) {
		ArrayList<WorldLocation> li = new ArrayList();
		li.add(0, nodes.get(nodes.size()-1).location);
		for (int i = nodes.size()-2; i > 0; i--) {
			li.add(0, nodes.get(i).location);
			CrystalTransmitter src = (CrystalTransmitter)nodes.get(i).getTile(true);
			CrystalReceiver tgt = (CrystalReceiver)nodes.get(/*i-1*/0).getTile(true);
			if (net.canMakeConnection(src, tgt, e)) {
				break;
			}
		}
		li.add(0, nodes.get(0).location);
		return li;
	}

}
