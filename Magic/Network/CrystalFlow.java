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

import java.util.List;

import org.apache.commons.lang3.tuple.ImmutableTriple;

import Reika.ChromatiCraft.Magic.Interfaces.CrystalNetworkTile;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalReceiver;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalSource;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalTransmitter;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;

public class CrystalFlow extends CrystalPath {

	private int remainingAmount;
	private final int maxFlow;
	public final CrystalReceiver receiver;

	protected CrystalFlow(CrystalNetworker net, CrystalReceiver r, CrystalElement e, int amt, List<WorldLocation> li, int maxthru) {
		super(net, e, li);
		remainingAmount = amt+this.getSignalLoss();
		receiver = r;
		maxFlow = Math.min(Math.max(1, this.getMaxFlow()-this.getSignalLoss()), maxthru);
	}

	CrystalFlow(CrystalNetworker net, CrystalPath p, CrystalReceiver r, int amt, int maxthru) {
		this(net, r, p.element, amt, p.nodes, maxthru);
	}

	CrystalPath asPath() {
		return new CrystalPath(network, element, nodes);
	}

	@Override
	protected void initialize() {
		super.initialize();
		//nodes.getFirst().getTileEntity().NOT A TILE
		WorldLocation locs = nodes.get(nodes.size()-2);
		ImmutableTriple<Double, Double, Double> offset = ((CrystalReceiver)locs.getTileEntity()).getTargetRenderOffset(element);
		double sx = offset != null ? offset.left : 0;
		double sy = offset != null ? offset.middle : 0;
		double sz = offset != null ? offset.right : 0;
		((CrystalSource)nodes.get(nodes.size()-1).getTileEntity()).addTarget(locs, element, sx, sy, sz);
		for (int i = 1; i < nodes.size()-1; i++) {
			CrystalNetworkTile te = (CrystalNetworkTile)nodes.get(i).getTileEntity();
			if (te instanceof CrystalTransmitter) {
				WorldLocation tg = nodes.get(i-1);
				offset = ((CrystalReceiver)tg.getTileEntity()).getTargetRenderOffset(element);
				double dx = offset != null ? offset.left : 0;
				double dy = offset != null ? offset.middle : 0;
				double dz = offset != null ? offset.right : 0;
				((CrystalTransmitter)te).addTarget(tg, element, dx, dy, dz);
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
		for (WorldLocation loc : nodes) {
			sb.append(i);
			sb.append("=");
			sb.append(loc.getTileEntity());
			sb.append(";");
			i++;
		}
		sb.append("]");
		return sb.toString();
	}

	public void resetTiles() {
		((CrystalSource)nodes.get(nodes.size()-1).getTileEntity()).removeTarget(nodes.get(nodes.size()-2), element);
		for (int i = 1; i < nodes.size()-1; i++) {
			CrystalNetworkTile te = (CrystalNetworkTile)nodes.get(i).getTileEntity();
			if (te instanceof CrystalTransmitter) {
				WorldLocation tg = nodes.get(i-1);
				((CrystalTransmitter)te).removeTarget(tg, element);
			}/*
			if (te instanceof CrystalReceiver) {
				te.clearSource();
			}*/
		}
	}

	private int getMaxFlow() {
		int max = Math.min(transmitter.maxThroughput(), receiver.maxThroughput());
		for (int i = 1; i < nodes.size()-1; i++) {
			CrystalNetworkTile te = (CrystalNetworkTile)nodes.get(i).getTileEntity();
			max = Math.min(max, te.maxThroughput());
		}
		return max;
	}

	public boolean isComplete() {
		return remainingAmount <= 0;
	}

	int drain() {
		int ret = Math.min(transmitter.getEnergy(element), this.getDrainThisTick());
		if (ret <= 0)
			return 0;
		remainingAmount -= ret;
		return ret;
	}

	private int getDrainThisTick() {
		return Math.min(Math.min(maxFlow, transmitter.maxThroughput()), remainingAmount);
	}

}
