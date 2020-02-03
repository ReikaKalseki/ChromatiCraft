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
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldChunk;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;

public class CrystalPath implements Comparable<CrystalPath> {

	protected final ArrayList<WorldLocation> nodes;
	public final CrystalSource transmitter;
	public final WorldLocation origin;
	public final CrystalElement element;
	private final HashSet<CrystalLink> links = new HashSet();
	protected final CrystalNetworker network;
	protected final boolean hasRealTarget;

	private int attenuation;
	private int theoreticalRange;
	private double totalDistance;

	private boolean wasOptimized = false;

	protected CrystalPath(CrystalNetworker net, boolean real, CrystalElement e, List<WorldLocation> li) {
		nodes = new ArrayList(li);
		transmitter = PylonFinder.getSourceAt(nodes.get(nodes.size()-1), true);
		origin = nodes.get(0);
		element = e;
		network = net;
		hasRealTarget = real;
		this.initialize();
		//remainingAmount = amt;
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
			WorldLocation loc = nodes.get(i);
			WorldLocation prev = nodes.get(i-1);
			CrystalLink l = network.getLink(prev, loc);
			CrystalNetworkTile te = PylonFinder.getNetTileAt(loc, true); //transmitter
			CrystalNetworkTile teprev = PylonFinder.getNetTileAt(prev, true); //receiver
			range += Math.min(((CrystalReceiver)teprev).getReceiveRange(), ((CrystalTransmitter)te).getSendRange());
			dist += Math.sqrt(te.getDistanceSqTo(teprev.getX()+0.5, teprev.getY()+0.5, teprev.getZ()+0.5));
			if (i < nodes.size()-1) {
				if (te instanceof CrystalRepeater) {
					int atten = ((CrystalRepeater)te).getSignalDegradation();
					if (l.isRainable() && transmitter.getWorld().isRaining())
						atten *= 1.15;
					loss += atten;
					if (te instanceof ConnectivityAction) {
						WorldLocation next = nodes.get(i+1);
						CrystalNetworkTile tenext = PylonFinder.getNetTileAt(next, true);
						ConnectivityAction ca = (ConnectivityAction)te;
						ca.notifyReceivingFrom(this, (CrystalTransmitter)tenext);
						ca.notifySendingTo(this, (CrystalReceiver)teprev);
					}
				}
			}
			links.add(l);
		}
		attenuation = loss;
		totalDistance = dist;
		theoreticalRange = range;
	}

	public void addBaseAttenuation(int amt) {
		if (amt > 0)
			attenuation += amt;
	}

	public final boolean containsLink(CrystalLink l) {
		return links.contains(l);
	}

	public HashSet<WorldChunk> getRelevantChunks() {
		HashSet<WorldChunk> ret = new HashSet();
		for (CrystalLink l : links) {
			ret.addAll(l.chunks);
		}
		return ret;
	}

	public final int getSignalLoss() {
		return attenuation;
	}

	public boolean canTransmit() {
		return true;//transmitter.getTransmissionStrength() > attenuation;
	}

	@Override
	public final String toString() {
		return element+": to "+origin+" from "+transmitter+": "+nodes.size()+"x "+nodes.toString();
	}

	public boolean contains(CrystalNetworkTile te) {
		return nodes.contains(new WorldLocation(te.getWorld(), te.getX(), te.getY(), te.getZ()));
	}

	public final boolean checkLineOfSight() {
		return this.checkLineOfSight(null);
	}

	public final boolean checkLineOfSight(CrystalLink l) {
		for (int i = 0; i < nodes.size()-2; i++) {
			WorldLocation tgt = nodes.get(i);
			if (l == null || tgt.equals(l.loc1) || tgt.equals(l.loc2)) {
				WorldLocation src = nodes.get(i+1);
				if (!PylonFinder.lineOfSight(src, tgt).hasLineOfSight) {
					CrystalReceiver rec = PylonFinder.getReceiverAt(tgt, true);
					CrystalTransmitter sr = PylonFinder.getTransmitterAt(src, true);
					if (sr.needsLineOfSightToReceiver(rec) || rec.needsLineOfSightFromTransmitter(sr)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public boolean stillValid() {
		if (!transmitter.canConduct() || !transmitter.isConductingElement(element)) {
			return false;
		}
		if (hasRealTarget) {
			CrystalNetworkTile tile = PylonFinder.getNetTileAt(origin, false);
			if (tile == null || !tile.canConduct() || !tile.isConductingElement(element)) {
				return false;
			}
		}
		for (int i = hasRealTarget ? 0 : 1; i < nodes.size()-2; i++) {
			WorldLocation tgt = nodes.get(i);
			WorldLocation src = nodes.get(i+1);
			CrystalTransmitter sr = PylonFinder.getTransmitterAt(src, false);
			CrystalReceiver rec = PylonFinder.getReceiverAt(tgt, false);
			if (sr == null || !sr.canConduct() || !sr.isConductingElement(element)) {
				return false;
			}
			if (sr.needsLineOfSightToReceiver(rec) || rec.needsLineOfSightFromTransmitter(sr)) {
				CrystalLink l = network.getLink(tgt, src);
				if (!l.hasLineOfSight()) {
					return false;
				}
			}
		}
		return true;
	}

	public void blink(int ticks, CrystalReceiver r) {
		CrystalSource src = PylonFinder.getSourceAt(nodes.get(nodes.size()-1), true);
		WorldLocation locs = nodes.get(nodes.size()-2);
		if (r == null)
			r = PylonFinder.getReceiverAt(locs, true);
		ImmutableTriple<Double, Double, Double> offset = r.getTargetRenderOffset(element);
		double sx = offset != null ? offset.left : 0;
		double sy = offset != null ? offset.middle : 0;
		double sz = offset != null ? offset.right : 0;
		src.addSelfTickingTarget(locs, element, sx, sy, sz, r.getIncomingBeamRadius(), ticks);
		for (int i = 1; i < nodes.size()-1; i++) {
			CrystalNetworkTile te = PylonFinder.getNetTileAt(nodes.get(i), true);
			if (te instanceof CrystalTransmitter) {
				WorldLocation tg = nodes.get(i-1);
				r = PylonFinder.getReceiverAt(tg, true);
				offset = r.getTargetRenderOffset(element);
				double dx = offset != null ? offset.left : 0;
				double dy = offset != null ? offset.middle : 0;
				double dz = offset != null ? offset.right : 0;
				((CrystalTransmitter)te).addSelfTickingTarget(tg, element, dx, dy, dz, r.getIncomingBeamRadius(), ticks);
			}/*
			if (te instanceof CrystalReceiver) {
				WorldLocation src = nodes.get(i+1);
				te.markSource(src);
			}*/
		}
	}

	public void endBlink() {
		CrystalSource src = PylonFinder.getSourceAt(nodes.get(nodes.size()-1), true);
		WorldLocation locs = nodes.get(nodes.size()-2);
		src.removeTarget(locs, element);
		for (int i = 1; i < nodes.size()-1; i++) {
			CrystalNetworkTile te = PylonFinder.getNetTileAt(nodes.get(i), false);
			if (te instanceof CrystalTransmitter) {
				WorldLocation tg = nodes.get(i-1);
				((CrystalTransmitter)te).removeTarget(tg, element);
			}
		}
	}

	/** where 0 = worst possible and 1 == perfect */
	public float getOptimizationFactor() {
		return (float)(theoreticalRange/totalDistance);
	}

	@Override
	public int compareTo(CrystalPath o) {
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
	public ArrayList<CrystalNetworkTile> getTileList() {
		ArrayList<CrystalNetworkTile> li = new ArrayList();
		for (WorldLocation loc : nodes) {
			li.add(PylonFinder.getNetTileAt(loc, true));
		}
		return li;
	}

	public boolean hasSameEndpoints(CrystalPath p) {
		return PylonFinder.getLocation(transmitter).equals(PylonFinder.getLocation(p.transmitter)) && origin.equals(p.origin);
	}

	CrystalPath cleanExtraEndJumps() {
		return new CrystalPath(network, hasRealTarget, element, this.getCleanedNodePath());
	}

	protected final ArrayList<WorldLocation> getCleanedNodePath() {
		return cleanRoute(network, element, nodes);
	}

	public void optimize() {
		this.optimize(Integer.MAX_VALUE);
	}

	public CrystalPath optimize(int nsteps) {
		ArrayList<WorldLocation> li = new ArrayList(nodes);
		boolean complete = PylonFinder.optimizeRoute(network, li, nsteps, JumpOptimizationCheck.always);
		CrystalPath p = new CrystalPath(network, hasRealTarget, element, li);
		if (complete) {
			p.setOptimized();
		}
		return p;
	}

	public boolean isOptimized() {
		return wasOptimized;
	}

	/** Reduces hop count; obeys LOS */
	static final ArrayList<WorldLocation> cleanRoute(CrystalNetworker net, CrystalElement e, ArrayList<WorldLocation> nodes) {
		ArrayList<WorldLocation> li = new ArrayList();
		li.add(0, nodes.get(nodes.size()-1));
		for (int i = nodes.size()-2; i > 0; i--) {
			li.add(0, nodes.get(i));
			CrystalTransmitter src = PylonFinder.getTransmitterAt(nodes.get(i), true);
			CrystalReceiver tgt = PylonFinder.getReceiverAt(nodes.get(/*i-1*/0), true);
			if (net.canMakeConnection(src, tgt, e)) {
				break;
			}
		}
		li.add(0, nodes.get(0));
		return li;
	}

}
