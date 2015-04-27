/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic.Network;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import Reika.ChromatiCraft.Magic.Interfaces.CrystalNetworkTile;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalRepeater;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalSource;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalTransmitter;
import Reika.ChromatiCraft.Magic.Network.CrystalNetworker.CrystalLink;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;

public class CrystalPath implements Comparable<CrystalPath> {

	protected final ArrayList<WorldLocation> nodes;
	public final CrystalSource transmitter;
	public final WorldLocation origin;
	public final CrystalElement element;
	private int attenuation;
	private final HashSet<CrystalLink> links = new HashSet();
	protected final CrystalNetworker network;

	protected CrystalPath(CrystalNetworker net, CrystalElement e, List<WorldLocation> li) {
		nodes = new ArrayList(li);
		transmitter = (CrystalSource)nodes.get(nodes.size()-1).getTileEntity();
		origin = nodes.get(0);
		element = e;
		network = net;
		this.initialize();
		//remainingAmount = amt;
	}

	protected void initialize() {
		int loss = 0;
		for (int i = 1; i < nodes.size()-1; i++) {
			WorldLocation loc = nodes.get(i);
			WorldLocation prev = nodes.get(i-1);
			CrystalNetworkTile te = (CrystalNetworkTile)loc.getTileEntity();
			if (te instanceof CrystalRepeater)
				loss = Math.max(loss, ((CrystalRepeater)te).getSignalDegradation());
			links.add(network.getLink(prev, loc));
		}
		attenuation = loss;
	}

	public final boolean containsLink(CrystalLink l) {
		return links.contains(l);
	}

	public final int getSignalLoss() {
		return attenuation;
	}

	public boolean canTransmit() {
		return transmitter.getTransmissionStrength() > attenuation;
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
				if (!PylonFinder.lineOfSight(src, tgt)) {
					CrystalTransmitter sr = (CrystalTransmitter)src.getTileEntity();
					if (sr.needsLineOfSight()) {
						return false;
					}
				}
			}
		}
		return true;
	}

	boolean stillValid() {
		if (!transmitter.canConduct() || !transmitter.isConductingElement(element)) {
			return false;
		}
		if (!((CrystalNetworkTile)origin.getTileEntity()).canConduct() || !((CrystalNetworkTile)origin.getTileEntity()).isConductingElement(element)) {
			return false;
		}
		for (int i = 0; i < nodes.size()-2; i++) {
			WorldLocation tgt = nodes.get(i);
			WorldLocation src = nodes.get(i+1);
			CrystalTransmitter sr = (CrystalTransmitter)src.getTileEntity();
			if (sr == null || !sr.canConduct() || !sr.isConductingElement(element)) {
				return false;
			}
			if (!PylonFinder.lineOfSight(tgt, src)) {
				if (sr.needsLineOfSight()) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public int compareTo(CrystalPath o) {
		return o.transmitter.getSourcePriority()-transmitter.getSourcePriority();
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
		return nodes.hashCode()^element.ordinal();
	}

}
