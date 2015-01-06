/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic;

import java.util.LinkedList;

import org.apache.commons.lang3.tuple.ImmutableTriple;

import Reika.ChromatiCraft.Magic.Interfaces.CrystalNetworkTile;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalReceiver;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalSource;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalTransmitter;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.WorldLocation;

public class CrystalFlow extends CrystalPath {

	private int remainingAmount;
	private final int maxFlow;
	public final CrystalReceiver receiver;

	protected CrystalFlow(CrystalReceiver r, CrystalElement e, int amt, LinkedList<WorldLocation> li, int maxthru) {
		super(e, li);
		remainingAmount = amt+this.getSignalLoss();
		receiver = r;
		maxFlow = Math.min(Math.max(1, this.getMaxFlow()-this.getSignalLoss()), maxthru);
	}

	CrystalFlow(CrystalPath p, CrystalReceiver r, int amt, int maxthru) {
		this(r, p.element, amt, p.nodes, maxthru);
	}

	CrystalPath asPath() {
		return new CrystalPath(element, nodes);
	}

	@Override
	protected void initialize() {
		super.initialize();
		//nodes.getFirst().getTileEntity().NOT A TILE
		((CrystalSource)nodes.getLast().getTileEntity()).addTarget(nodes.get(nodes.size()-2), element, 0, 0, 0);
		for (int i = 1; i < nodes.size()-1; i++) {
			CrystalNetworkTile te = (CrystalNetworkTile)nodes.get(i).getTileEntity();
			if (te instanceof CrystalTransmitter) {
				WorldLocation tg = nodes.get(i-1);
				ImmutableTriple<Double, Double, Double> offset = i == 1 ? ((CrystalReceiver)tg.getTileEntity()).getTargetRenderOffset(element) : null;
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

	public void resetTiles() {
		((CrystalSource)nodes.getLast().getTileEntity()).removeTarget(nodes.get(nodes.size()-2), element);
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
		int ret = Math.min(transmitter.getEnergy(element), Math.min(maxFlow, remainingAmount));
		if (ret <= 0)
			return 0;
		remainingAmount -= ret;
		return ret;
	}

}
