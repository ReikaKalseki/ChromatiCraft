package Reika.ChromatiCraft.Auxiliary.Event;

import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalPylon;
import cpw.mods.fml.common.eventhandler.Event;

public class PylonEvents {

	/** Called when a pylon fully recharges */
	public static class PylonFullyChargedEvent extends Event {

		public final TileEntityCrystalPylon pylon;

		public PylonFullyChargedEvent(TileEntityCrystalPylon te) {
			pylon = te;
		}

	}

	/** Called when a pylon comes back online */
	public static class PylonRechargedEvent extends Event {

		public final TileEntityCrystalPylon pylon;

		public PylonRechargedEvent(TileEntityCrystalPylon te) {
			pylon = te;
		}

	}

	public static class PylonDrainedEvent extends Event {

		public final TileEntityCrystalPylon pylon;

		public PylonDrainedEvent(TileEntityCrystalPylon te) {
			pylon = te;
		}

	}

}
