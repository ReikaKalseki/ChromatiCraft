/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.Command;

import net.minecraft.command.ICommandSender;

import Reika.ChromatiCraft.World.IWG.DungeonGenerator;
import Reika.DragonAPI.Command.DragonCommandBase;


public class StructureCacheCommand extends DragonCommandBase {

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		DungeonGenerator.instance.printCache(ics);
	}

	@Override
	public String getCommandString() {
		return "structlocs";
	}

	@Override
	protected boolean isAdminOnly() {
		return true;
	}

}
