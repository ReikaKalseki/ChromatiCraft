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

import java.util.HashSet;
import java.util.LinkedList;

import Reika.ChromatiCraft.Magic.Interfaces.CrystalNetworkTile;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalRepeater;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalSource;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalTransmitter;
import Reika.ChromatiCraft.Magic.Network.CrystalNetworker.CrystalLink;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;

public class CrystalPath implements Comparable<CrystalPath> {

	protected final LinkedList<WorldLocation> nodes;
	public final CrystalSource transmitter;
	public final WorldLocation origin;
	public final CrystalElement element;
	private int attenuation;
	private final HashSet<CrystalLink> links = new HashSet();
	protected final CrystalNetworker network;

	protected CrystalPath(CrystalNetworker net, CrystalElement e, LinkedList<WorldLocation> li) {
		nodes = li;
		transmitter = (CrystalSource)nodes.getLast().getTileEntity();
		origin = nodes.getFirst();
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
	public String toString() {
		return element+": to "+origin+" from "+transmitter+": "+nodes.toString();
	}

	public boolean contains(CrystalNetworkTile te) {
		return nodes.contains(new WorldLocation(te.getWorld(), te.getX(), te.getY(), te.getZ()));
	}

	public final boolean checkLineOfSight() {
		for (int i = 0; i < nodes.size()-1; i++) {
			WorldLocation tgt = nodes.get(i);
			WorldLocation src = nodes.get(i+1);
			if (!PylonFinder.lineOfSight(src.getWorld(), src.xCoord, src.yCoord, src.zCoord, tgt.xCoord, tgt.yCoord, tgt.zCoord)) {
				CrystalTransmitter sr = (CrystalTransmitter)src.getTileEntity();
				if (sr.needsLineOfSight()) {
					return false;
				}
			}
		}
		return true;
	}

	boolean stillValid() {
		for (int i = 0; i < nodes.size()-1; i++) {
			WorldLocation src = nodes.get(i);
			WorldLocation tgt = nodes.get(i+1);
			if (src.getTileEntity() instanceof CrystalTransmitter) {
				CrystalTransmitter tr = (CrystalTransmitter)src.getTileEntity();
				CrystalTransmitter tg = (CrystalTransmitter)tgt.getTileEntity();
				if (!PylonFinder.lineOfSight(src.getWorld(), src.xCoord, src.yCoord, src.zCoord, tgt.xCoord, tgt.yCoord, tgt.zCoord)) {
					if (tr.needsLineOfSight() || tg.needsLineOfSight()) {
						return false;
					}
				}
				if (!tr.canConduct() || !tr.isConductingElement(element)) {
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

}
