/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.DragonAPI.Auxiliary.TickRegistry.TickHandler;
import Reika.DragonAPI.Auxiliary.TickRegistry.TickType;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;

public class ChromabilityHandler implements TickHandler {

	public static final ChromabilityHandler instance = new ChromabilityHandler();

	private ChromabilityHandler() {

	}

	@Override
	public void tick(TickType type, Object... tickData) {
		EntityPlayer ep = (EntityPlayer) tickData[0];
		ArrayList<Chromabilities> li = Chromabilities.getFrom(ep);
		for (int i = 0; i < li.size(); i++) {
			Chromabilities c = li.get(i);
			if (c.tickBased && c.tickPhase == tickData[1] && c.playerHasAbility(ep)) {
				c.apply(ep);
			}
		}
	}

	@Override
	public TickType getType() {
		return TickType.PLAYER;
	}

	@Override
	public boolean canFire(Phase p) {
		return true;
	}

	@Override
	public String getLabel() {
		return "Chromabilities";
	}

}
