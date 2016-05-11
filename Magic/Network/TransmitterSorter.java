/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic.Network;

import java.util.Comparator;

import Reika.ChromatiCraft.Magic.Interfaces.CrystalReceiver;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalTransmitter;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;


public class TransmitterSorter implements Comparator<CrystalTransmitter> {

	private final WorldLocation location;

	TransmitterSorter(CrystalReceiver r) {
		location = new WorldLocation(r.getWorld(), r.getX(), r.getY(), r.getZ());
	}

	@Override
	public int compare(CrystalTransmitter o1, CrystalTransmitter o2) {
		return (int)(o1.getDistanceSqTo(location.xCoord, location.yCoord, location.zCoord)-o2.getDistanceSqTo(location.xCoord, location.yCoord, location.zCoord));
	}

}
