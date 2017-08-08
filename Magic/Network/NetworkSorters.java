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

import java.util.Comparator;

import Reika.ChromatiCraft.Magic.Interfaces.CrystalReceiver;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalSource;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalTransmitter;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;

class NetworkSorters {

	static final SourcePrioritizer prioritizer = new SourcePrioritizer();

	static class TransmitterDistanceSorter implements Comparator<CrystalTransmitter> {

		private final WorldLocation location;

		TransmitterDistanceSorter(CrystalReceiver r) {
			location = new WorldLocation(r.getWorld(), r.getX(), r.getY(), r.getZ());
		}

		@Override
		public int compare(CrystalTransmitter o1, CrystalTransmitter o2) {
			return (int)(o1.getDistanceSqTo(location.xCoord, location.yCoord, location.zCoord)-o2.getDistanceSqTo(location.xCoord, location.yCoord, location.zCoord));
		}

	}

	static class SourcePrioritizer implements Comparator<CrystalTransmitter> {

		private SourcePrioritizer() {

		}

		@Override
		public int compare(CrystalTransmitter o1, CrystalTransmitter o2) {
			if (o1 instanceof CrystalSource && o2 instanceof CrystalSource) {
				return -Integer.compare(((CrystalSource)o1).getSourcePriority(), ((CrystalSource)o2).getSourcePriority());
			}
			else if (o1 instanceof CrystalSource) {
				return Integer.MIN_VALUE;
			}
			else if (o2 instanceof CrystalSource) {
				return Integer.MAX_VALUE;
			}
			else {
				return -Integer.compare(o1.getPathPriority(), o2.getPathPriority());
			}
		}

	}

}
