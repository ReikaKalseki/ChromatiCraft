/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.Command;

import net.minecraft.command.ICommandSender;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.RecipesCastingTable;
import Reika.DragonAPI.Command.DragonCommandBase;


public class RecipeReloadCommand extends DragonCommandBase {

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		RecipesCastingTable.instance.reload();
		ChromatiCraft.logger.log("Casting Recipes reloaded.");
		this.sendChatToSender(ics, "Recipes reloaded.");
	}

	@Override
	public String getCommandString() {
		return "reloadchromacasting";
	}

	@Override
	protected boolean isAdminOnly() {
		return true;
	}

}
