/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft;

import java.util.Collection;

import net.minecraft.entity.player.EntityPlayer;
import Reika.ChromatiCraft.API.AbilityAPI.Ability;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.PlayerElementBuffer;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickHandler;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickType;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;

public class ChromabilityHandler implements TickHandler {

	public static final ChromabilityHandler instance = new ChromabilityHandler();

	private ChromabilityHandler() {

	}

	@Override
	public void tick(TickType type, Object... tickData) {
		EntityPlayer ep = (EntityPlayer) tickData[0];
		Collection<Ability> li = Chromabilities.getAbilitiesForTick((Phase)tickData[1]);
		for (Ability c : li) {
			if (Chromabilities.playerHasAbility(ep, c) && Chromabilities.enabledOn(ep, c)) {
				if (Chromabilities.canPlayerExecuteAt(ep, c))
					c.apply(ep);
				if (ReikaRandomHelper.doWithChance(0.002)) { //was 0.0002
					ElementTagCompound tag = Chromabilities.getTickCost(c);
					if (tag != null) {
						if (PlayerElementBuffer.instance.playerHas(ep, tag))
							PlayerElementBuffer.instance.removeFromPlayer(ep, tag);
						else {
							Chromabilities.removeFromPlayer(ep, c);
						}
					}
				}
			}
		}
		if (DragonAPICore.debugtest)
			for (Ability c : Chromabilities.getAbilities()) {
				if (!Chromabilities.playerHasAbility(ep, c))
					Chromabilities.give(ep, c);
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
