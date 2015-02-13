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

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Registry.ChromaResearchManager;
import Reika.ChromatiCraft.Registry.ChromaResearchManager.ResearchLevel;
import Reika.DragonAPI.Command.DragonCommandBase;

public class ChromaResearchCommand extends DragonCommandBase {

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		if (args.length != 1 && args.length != 2) {
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
			if (stage == -1) {
				ChromaResearchManager.instance.maxPlayerResearch(ep);
			}
			else if (stage == -2) {
				ChromaResearchManager.instance.resetPlayerResearch(ep);
			}
			else if (stage == -3) {
				for (int i = 0; i < ResearchLevel.levelList.length; i++) {
					ResearchLevel rl = ResearchLevel.levelList[i];
					Collection<ChromaResearch> c = ChromaResearchManager.instance.getResearchForLevel(rl);
				}
				ResearchLevel pl = ChromaResearchManager.instance.getPlayerResearchLevel(ep);
				Collection<ChromaResearch> cp = ChromaResearchManager.instance.getFragments(ep);
				sendChatToSender(ics, "Player research: ");
				sendChatToSender(ics, "Level "+pl);
				sendChatToSender(ics, "Fragments: "+cp);
				Collection<ChromaResearch> missing = new ArrayList(ChromaResearchManager.instance.getResearchForLevel(pl));
				//ReikaJavaLibrary.pConsole(missing+" - "+cp+" = ");
				missing.removeAll(cp);
				//ReikaJavaLibrary.pConsole(missing);
				sendChatToSender(ics, "Can step to "+pl.post()+": "+pl.post().canProgressTo(ep)+" && "+missing.isEmpty());
				sendChatToSender(ics, "Missing research for "+pl+": "+missing);
			}
			else {
				if (ChromaResearchManager.instance.setPlayerResearchLevel(ep, ResearchLevel.levelList[stage])) {
					sendChatToSender(ics, EnumChatFormatting.GREEN.toString()+"Player research stage set successfully.");
				}
				else {
					sendChatToSender(ics, EnumChatFormatting.RED.toString()+"Invalid stage number: "+args[args.length-1]);
				}
			}
		}
		catch (NumberFormatException e) {
			sendChatToSender(ics, EnumChatFormatting.RED.toString()+"\""+args[args.length-1]+"\" is not an integer.");
		}
	}

	@Override
	public String getCommandString() {
		return "chromaresearch";
	}

	@Override
	protected boolean isAdminOnly() {
		return true;
	}

}
