/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic.Interfaces;

import org.apache.commons.lang3.tuple.ImmutableTriple;

import Reika.ChromatiCraft.Registry.CrystalElement;

public interface CrystalReceiver extends CrystalNetworkTile {

	public void receiveElement(CrystalElement e, int amt);

	public void onPathBroken(CrystalElement e);

	public int getReceiveRange();

	public ImmutableTriple<Double, Double, Double> getTargetRenderOffset(CrystalElement e);

	//public void markSource(WorldLocation loc);

}
