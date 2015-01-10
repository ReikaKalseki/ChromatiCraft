/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import java.util.HashMap;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import Reika.ChromatiCraft.API.AbilityAPI.Ability;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.DragonAPI.Command.DragonCommandBase;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;

public class ChromabilityCommand extends DragonCommandBase {

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		EntityPlayer ep = this.getCommandSenderAsPlayer(ics);
		String player = args[0];
		String id = args[1];
		String act = args[2];
		EntityPlayer target = ep.worldObj.getPlayerEntityByName(player);
		if (target == null) {
			ReikaChatHelper.sendChatToPlayer(ep, EnumChatFormatting.RED+"No such player '"+player+"'");
			return;
		}
		Ability a = Chromabilities.getAbility(id);
		if (a == null) {
			ReikaChatHelper.sendChatToPlayer(ep, EnumChatFormatting.RED+"No such ability id '"+id+"'");
			return;
		}
		try {
			Action action = Action.valueOf(act.toUpperCase());
			action.perform(target, ep, a);
			ReikaChatHelper.sendChatToPlayer(ep, "Action '"+act+"' with ability '"+id+"' performed on player '"+player+"'");
		}
		catch (IllegalArgumentException e) {
			ReikaChatHelper.sendChatToPlayer(ep, EnumChatFormatting.RED+"No such action '"+act+"'");
		}
	}

	@Override
	public String getCommandString() {
		return "setability";
	}

	@Override
	protected boolean isAdminOnly() {
		return true;
	}

	private static enum Action {
		GIVE(),
		REMOVE(),
		GET();

		private void perform(EntityPlayer ep, EntityPlayer sender, Ability a) {
			switch(this) {
			case GIVE:
				Chromabilities.give(ep, a);
				break;
			case REMOVE:
				Chromabilities.removeFromPlayer(ep, a);
				break;
			case GET:
				HashMap<Ability, Boolean> c = Chromabilities.getAbilitiesOn(ep);
				ReikaChatHelper.sendChatToPlayer(sender, "Abilities for "+ep+":\n"+c.toString());
				break;
			}
		}
	}

}
