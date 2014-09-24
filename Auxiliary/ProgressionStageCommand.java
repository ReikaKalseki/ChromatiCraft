/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import Reika.DragonAPI.Command.DragonCommandBase;

public class ProgressionStageCommand extends DragonCommandBase {

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		if (args.length != 2 && args.length != 1) {
			sendChatToSender(ics, EnumChatFormatting.RED.toString()+"Invalid arguments. Valid Formats:");
			sendChatToSender(ics, EnumChatFormatting.RED.toString()+"  Set a player: /"+this.getCommandString()+" <playername> <level>");
			sendChatToSender(ics, EnumChatFormatting.RED.toString()+"  Set yourself: /"+this.getCommandString()+" <level>");
			return;
		}
		EntityPlayer ep = args.length == 2 ? ics.getEntityWorld().getPlayerEntityByName(args[0]) : this.getCommandSenderAsPlayer(ics);
		if (ep == null) {
			sendChatToSender(ics, EnumChatFormatting.RED.toString()+"Player \""+args[0]+"\" not found.");
			return;
		}
		try {
			int stage = Integer.parseInt(args[args.length-1]);
			if (ProgressionManager.instance.setPlayerStage(ep, stage)) {
				sendChatToSender(ics, EnumChatFormatting.GREEN.toString()+"Player progression stage set successfully.");
			}
			else {
				sendChatToSender(ics, EnumChatFormatting.RED.toString()+"Invalid stage number: "+args[args.length-1]);
			}
		}
		catch (NumberFormatException e) {
			sendChatToSender(ics, EnumChatFormatting.RED.toString()+"\""+args[args.length-1]+"\" is not an integer.");
		}
	}

	@Override
	public String getCommandString() {
		return "chromaprog";
	}

	@Override
	public int getRequiredPermissionLevel()
	{
		return 4;
	}

}
