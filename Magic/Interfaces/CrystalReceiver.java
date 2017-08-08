/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic.Interfaces;

import Reika.ChromatiCraft.Auxiliary.CrystalNetworkLogger.FlowFail;
import Reika.ChromatiCraft.Magic.Network.CrystalFlow;
import Reika.ChromatiCraft.Registry.CrystalElement;

public interface CrystalReceiver extends CrystalNetworkTile, EnergyBeamReceiver {

	/** Returns the amount successfully added. */
	public int receiveElement(CrystalElement e, int amt);

	public void onPathBroken(CrystalFlow p, FlowFail f);

	public int getReceiveRange();

	public void onPathCompleted(CrystalFlow p);

	public boolean canReceiveFrom(CrystalTransmitter r);

	public boolean needsLineOfSightFromTransmitter(CrystalTransmitter r);

	//public void markSource(WorldLocation loc);

}
