/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Networking;

import org.apache.commons.lang3.tuple.ImmutableTriple;

import Reika.ChromatiCraft.Auxiliary.CrystalNetworkLogger.FlowFail;
import Reika.ChromatiCraft.Base.TileEntity.CrystalTransmitterBase;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalReceiver;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalRepeater;
import Reika.ChromatiCraft.Magic.Network.CrystalFlow;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class TileEntityWirelessRepeater extends CrystalTransmitterBase implements CrystalRepeater {

	@Override
	public int receiveElement(CrystalElement e, int amt) {
		return 1;
	}

	@Override
	public void onTransfer(CrystalElement e, int amt) {

	}

	@Override
	public int getReceiveRange() {
		return 24;
	}

	@Override
	public ImmutableTriple<Double, Double, Double> getTargetRenderOffset(CrystalElement e) {
		return null;
	}

	@Override
	public boolean isConductingElement(CrystalElement e) {
		return this.canConduct();
	}

	@Override
	public int maxThroughput() {
		return 100;
	}

	@Override
	public boolean canConduct() {
		return false;
	}

	@Override
	public int getSendRange() {
		return 8;
	}

	@Override
	public int getSignalDegradation() {
		return 20;
	}

	@Override
	public ChromaTiles getTile() {
		return null;//ChromaTiles.WIRELESS;
	}

	@Override
	public boolean needsLineOfSight() {
		return false;
	}

	@Override
	public int getSignalDepth(CrystalElement e) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setSignalDepth(CrystalElement e, int d) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPathCompleted(CrystalFlow p) {

	}

	@Override
	public void onPathBroken(CrystalFlow p, FlowFail f) {

	}

	@Override
	public boolean checkConnectivity() {
		return true;
	}

	@Override
	public boolean canTransmitTo(CrystalReceiver r) {
		return !(r instanceof CrystalRepeater);
	}

}
