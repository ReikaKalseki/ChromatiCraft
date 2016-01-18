package Reika.ChromatiCraft.Auxiliary.Command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Registry.ChromaResearchManager;
import Reika.ChromatiCraft.Registry.ChromaResearchManager.ResearchLevel;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Command.DragonCommandBase;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;


public class ProgressModifyCommand extends DragonCommandBase {

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		EntityPlayer caller = this.getCommandSenderAsPlayer(ics);
		if (!caller.capabilities.isCreativeMode) {
			this.sendChatToSender(ics, EnumChatFormatting.RED+"This command can only be called from players in creative mode.");
			return;
		}

		if (args.length == 2 && (args[0].equals("debug") || args[0].equals("reset") || args[0].equals("maximize"))) {
			args = Arrays.copyOf(args, args.length+1);
		}
		else if (args.length == 3 && (args[1].equals("debug") || args[1].equals("reset") || args[1].equals("maximize"))) {
			args = Arrays.copyOf(args, args.length+1);
		}

		if (args.length < 3 || args.length > 4) {
			this.sendChatToSender(ics, EnumChatFormatting.RED+"Invalid arguments. Use /"+this.getCommandString()+" <player> [action] [entry] [set].");
			return;
		}
		EntityPlayer ep = args.length == 4 ? ReikaPlayerAPI.getPlayerByNameAnyWorld(args[0]) : this.getCommandSenderAsPlayer(ics);
		if (args.length == 4)
			args = Arrays.copyOfRange(args, 1, args.length);
		boolean set = Boolean.valueOf(args[2]);
		switch(args[0]) {
			case "color": {
				CrystalElement e = this.getColor(args[1]);
				if (e == null) {
					this.sendChatToSender(ics, EnumChatFormatting.RED+" Invalid color '"+args[1]+"'.");
					return;
				}
				ProgressionManager.instance.setPlayerDiscoveredColor(ep, e, set, false);
				this.sendChatToSender(ics, EnumChatFormatting.GREEN+"Color Discovery "+e.displayName+" set to "+set+" for "+ep.getCommandSenderName());
				break;
			}
			case "progress": {
				try {
					ProgressStage p = ProgressStage.valueOf(args[1].toUpperCase());
					ProgressionManager.instance.setPlayerStage(ep, p, set, false);
					this.sendChatToSender(ics, EnumChatFormatting.GREEN+"Progress Stage "+p.name()+" set to "+set+" for "+ep.getCommandSenderName());
				}
				catch (IllegalArgumentException e) {
					this.sendChatToSender(ics, EnumChatFormatting.RED+" Invalid progression stage '"+args[1]+"'.");
					return;
				}
				break;
			}
			case "fragment": {
				try {
					ChromaResearch r = ChromaResearch.valueOf(args[1].toUpperCase());
					if (set)
						ChromaResearchManager.instance.givePlayerFragment(ep, r, false);
					else
						ChromaResearchManager.instance.removePlayerFragment(ep, r, false);
					this.sendChatToSender(ics, EnumChatFormatting.GREEN+"Fragment "+r.name()+" set to "+set+" for "+ep.getCommandSenderName());
				}
				catch (IllegalArgumentException e) {
					this.sendChatToSender(ics, EnumChatFormatting.RED+" Invalid fragment '"+args[1]+"'.");
					return;
				}
				break;
			}
			case "level": {
				try {
					ResearchLevel r = ResearchLevel.valueOf(args[1].toUpperCase());
					ChromaResearchManager.instance.setPlayerResearchLevel(ep, r, false);
					this.sendChatToSender(ics, EnumChatFormatting.GREEN+"Research level set to "+r.name()+" for "+ep.getCommandSenderName());
				}
				catch (IllegalArgumentException e) {
					this.sendChatToSender(ics, EnumChatFormatting.RED+" Invalid research level '"+args[1]+"'.");
					return;
				}
				break;
			}
			case "dimstruct": {
				CrystalElement e = this.getColor(args[1]);
				if (e == null) {
					this.sendChatToSender(ics, EnumChatFormatting.RED+" Invalid color '"+args[1]+"'.");
					return;
				}
				ProgressionManager.instance.markPlayerCompletedStructureColor(ep, e, set, false);
				this.sendChatToSender(ics, EnumChatFormatting.GREEN+"Structure "+e.displayName+" set to "+set+" for "+ep.getCommandSenderName());
				break;
			}
			case "reset": {
				if (args[1].equals("all") || args[1].equals("progress") || args[1].equals("progression"))
					ProgressionManager.instance.resetPlayerProgression(ep, false);
				if (args[1].equals("all") || args[1].equals("fragment") || args[1].equals("research"))
					ChromaResearchManager.instance.resetPlayerResearch(ep, false);
				this.sendChatToSender(ics, EnumChatFormatting.GREEN+"Progression reset for "+ep.getCommandSenderName());
				break;
			}
			case "maximize": {
				if (args[1].equals("all") || args[1].equals("progress") || args[1].equals("progression"))
					ProgressionManager.instance.maxPlayerProgression(ep, false);
				if (args[1].equals("all") || args[1].equals("fragment") || args[1].equals("research"))
					ChromaResearchManager.instance.maxPlayerResearch(ep, false);
				this.sendChatToSender(ics, EnumChatFormatting.GREEN+"Progression maximized for "+ep.getCommandSenderName());
				break;
			}
			case "debug": {
				if (args[1].equals("all") || args[1].equals("fragment") || args[1].equals("research")) {
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
				if (args[1].equals("all") || args[1].equals("progress") || args[1].equals("progression")) {
					Collection<ProgressStage> c = ProgressionManager.instance.getStagesFor(ep);
					Collection<CrystalElement> c2 = ProgressionManager.instance.getColorsFor(ep);
					Collection<CrystalElement> c3 = ProgressionManager.instance.getStructuresFor(ep);
					sendChatToSender(ics, "Progress for "+ep.getCommandSenderName()+":\n"+c.toString());
					sendChatToSender(ics, "Elements for "+ep.getCommandSenderName()+":\n"+c2.toString());
					sendChatToSender(ics, "Structure Flags for "+ep.getCommandSenderName()+":\n"+c3.toString());
				}
				break;
			}
			default:
				this.sendChatToSender(ics, EnumChatFormatting.RED+" Unrecognized progress action/type '"+args[0]+"'.");
				break;
		}
	}

	private CrystalElement getColor(String s) {
		try {
			return CrystalElement.valueOf(s.toUpperCase());
		}
		catch (IllegalArgumentException e) {

		}
		return CrystalElement.getByName(ReikaStringParser.capFirstChar(s.toLowerCase()));
	}

	@Override
	public String getCommandString() {
		return "chromaprog";
	}

	@Override
	protected boolean isAdminOnly() {
		return true;
	}

}
