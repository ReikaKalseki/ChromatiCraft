package Reika.ChromatiCraft.Auxiliary.Command;

import net.minecraft.command.ICommandSender;
import Reika.ChromatiCraft.World.PylonGenerator;
import Reika.DragonAPI.Command.DragonCommandBase;


public class PylonCacheCommand extends DragonCommandBase {

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		PylonGenerator.instance.printPylonCache(ics);
	}

	@Override
	public String getCommandString() {
		return "pylonlocs";
	}

	@Override
	protected boolean isAdminOnly() {
		return true;
	}

}
