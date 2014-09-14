/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic;

import java.util.LinkedList;

import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.WorldLocation;

public class CrystalFlow extends CrystalPath {

	private int remainingAmount;
	private final int maxFlow;
	public final CrystalReceiver receiver;

	public CrystalFlow(CrystalReceiver r, CrystalElement e, int amt, LinkedList<WorldLocation> li) {
		super(e, li);
		remainingAmount = amt+this.getSignalLoss();
		receiver = r;
		maxFlow = this.getMaxFlow()-this.getSignalLoss();
	}

	@Override
	protected void initialize() {
		super.initialize();
		//nodes.getFirst().getTileEntity().NOT A TILE
		((CrystalSource)nodes.getLast().getTileEntity()).addTarget(nodes.get(nodes.size()-2), element);
		for (int i = 1; i < nodes.size()-1; i++) {
			CrystalNetworkTile te = (CrystalNetworkTile)nodes.get(i).getTileEntity();
			if (te instanceof CrystalTransmitter) {
				WorldLocation tg = nodes.get(i-1);
				((CrystalTransmitter)te).addTarget(tg, element);
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

	public boolean checkLineOfSight() {
		for (int i = 0; i < nodes.size()-1; i++) {
			WorldLocation src = nodes.get(i);
			WorldLocation tgt = nodes.get(i+1);
			if (!PylonFinder.lineOfSight(src.getWorld(), src.xCoord, src.yCoord, src.zCoord, tgt.xCoord, tgt.yCoord, tgt.zCoord)) {
				return false;
			}
		}
		return true;
	}

}
