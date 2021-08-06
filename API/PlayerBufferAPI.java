/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.API;

import net.minecraft.entity.player.EntityPlayer;

import Reika.ChromatiCraft.API.CrystalElementAccessor.CrystalElementProxy;

public interface PlayerBufferAPI {

	/** Subtracts the specified amount of (or, if the player has less, all of their) energy of a given color from a player's buffer. */
	public void removeFromPlayer(EntityPlayer ep, CrystalElementProxy color, int amt);

}
