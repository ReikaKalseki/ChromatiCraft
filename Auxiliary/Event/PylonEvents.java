/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.Event;

import net.minecraft.entity.player.EntityPlayer;

import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalPylon;

import cpw.mods.fml.common.eventhandler.Event;

public class PylonEvents {

	protected abstract static class PylonEvent extends Event {

		public final TileEntityCrystalPylon pylon;

		protected PylonEvent(TileEntityCrystalPylon te) {
			pylon = te;
		}

	}

	/** Called when a pylon fully recharges */
	public static class PylonFullyChargedEvent extends PylonEvent {

		public PylonFullyChargedEvent(TileEntityCrystalPylon te) {
			super(te);
		}

	}

	/** Called when a pylon comes back online */
	public static class PylonRechargedEvent extends PylonEvent {

		public PylonRechargedEvent(TileEntityCrystalPylon te) {
			super(te);
		}

	}

	public static class PylonDrainedEvent extends PylonEvent {

		public PylonDrainedEvent(TileEntityCrystalPylon te) {
			super(te);
		}

	}

	public static class PlayerChargedFromPylonEvent extends PylonEvent {

		public final EntityPlayer player;

		public PlayerChargedFromPylonEvent(TileEntityCrystalPylon te, EntityPlayer ep) {
			super(te);
			player = ep;
		}
	}

}
