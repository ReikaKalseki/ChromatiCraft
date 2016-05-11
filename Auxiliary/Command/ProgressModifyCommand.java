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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import Reika.ChromatiCraft.API.AbilityAPI.Ability;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Registry.ChromaResearchManager;
import Reika.ChromatiCraft.Registry.ChromaResearchManager.ResearchLevel;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Auxiliary.Trackers.ReflectiveFailureTracker;
import Reika.DragonAPI.Command.DragonCommandBase;
import Reika.DragonAPI.Instantiable.BasicModEntry;
import Reika.DragonAPI.Interfaces.Registry.ModEntry;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaCommandHelper;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;


public class ProgressModifyCommand extends DragonCommandBase {

	private final HashMap<String, AutoFiller> fillers = new HashMap();
	private CommandBase relay;

	public ProgressModifyCommand() {
		ModEntry mod = new BasicModEntry("chromaticommands");
		if (mod.isLoaded()) {
			try {
				Class manager = Class.forName("chromaticommands.commands.Chrom_Commands");
				Class obj = Class.forName("chromaticommands.commands.ChromComBase");
				Field f = manager.getDeclaredField("coms");
				f.setAccessible(true);
				ICommand comm = ReikaCommandHelper.getCommandByName("/chromaprog2");
				relay = (CommandBase)comm;
				String n = comm.getCommandName();
				Method name = obj.getMethod("getCommand");
				Method args = obj.getMethod("validArgs", String[].class);
				Method auto = obj.getMethod("autoComplete", ICommandSender.class, String[].class);
				ArrayList li = (ArrayList)f.get(comm);
				for (Object o : li) {
					AutoFiller fill = new AutoFiller(o, name, args, auto);
					fillers.put((String)name.invoke(o), fill);
				}
			}
			catch (ClassNotFoundException e) {
				ReflectiveFailureTracker.instance.logModReflectiveFailure(mod, e);
				e.printStackTrace();
			}
			catch (NoSuchFieldException e) {
				ReflectiveFailureTracker.instance.logModReflectiveFailure(mod, e);
				e.printStackTrace();
			}
			catch (NoSuchMethodException e) {
				ReflectiveFailureTracker.instance.logModReflectiveFailure(mod, e);
				e.printStackTrace();
			}
			catch (IllegalAccessException e) {
				ReflectiveFailureTracker.instance.logModReflectiveFailure(mod, e);
				e.printStackTrace();
			}
			catch (IllegalArgumentException e) {
				ReflectiveFailureTracker.instance.logModReflectiveFailure(mod, e);
				e.printStackTrace();
			}
			catch (InvocationTargetException e) {
				ReflectiveFailureTracker.instance.logModReflectiveFailure(mod, e);
				e.printStackTrace();
			}
		}
	}

	private static class AutoFiller {

		private final Method autoComplete;
		private final Method validArgs;
		private final Method getCommand;
		private final Object reference;

		private AutoFiller(Object o, Method n, Method v, Method c) {
			getCommand = n;
			autoComplete = c;
			validArgs = v;
			reference = o;
		}

	}

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		EntityPlayer caller = this.getCommandSenderAsPlayer(ics);

		if (args.length < 2) {
			this.sendChatToSender(ics, EnumChatFormatting.RED+"Invalid arguments. Use /"+this.getCommandString()+" <player> [action] [entry] [set].");
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
		if (!ep.capabilities.isCreativeMode && !ReikaPlayerAPI.isReika(caller) && ep != caller) {
			this.sendChatToSender(ics, EnumChatFormatting.RED+"This command can only be called from players in creative mode.");
			return;
		}
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
			case "abilities":
			case "ability": {
				try {
					Ability a = Chromabilities.getAbility(args[1].toLowerCase(Locale.ENGLISH));
					if (set)
						Chromabilities.give(ep, a);
					else
						Chromabilities.removeFromPlayer(ep, a);
					this.sendChatToSender(ics, EnumChatFormatting.GREEN+"Fragment "+a.getDisplayName()+" set to "+set+" for "+ep.getCommandSenderName());
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
				if (args[1].equals("all") || args[1].equals("progress") || args[1].equals("progression")) {
					ProgressionManager.instance.resetPlayerProgression(ep, false);
					this.sendChatToSender(ics, EnumChatFormatting.GREEN+"Progression reset for "+ep.getCommandSenderName());
				}
				if (args[1].equals("all") || args[1].equals("fragment") || args[1].equals("research")) {
					ChromaResearchManager.instance.resetPlayerResearch(ep, false);
					this.sendChatToSender(ics, EnumChatFormatting.GREEN+"Fragments reset for "+ep.getCommandSenderName());
				}
				if (args[1].equals("all") || args[1].equals("ability") || args[1].equals("abilities")) {
					for (Ability c : Chromabilities.getAbilities()) {
						if (!Chromabilities.playerHasAbility(ep, c))
							Chromabilities.removeFromPlayer(ep, c);
					}
					this.sendChatToSender(ics, EnumChatFormatting.GREEN+"Abilities reset for "+ep.getCommandSenderName());
				}
				if (args[1].equals("all") || args[1].equals("dimstruct")) {
					for (int i = 0; i < 16; i++) {
						ProgressionManager.instance.markPlayerCompletedStructureColor(ep, CrystalElement.elements[i], false, false);
					}
					this.sendChatToSender(ics, EnumChatFormatting.GREEN+"Dimstruct reset for "+ep.getCommandSenderName());
				}
				break;
			}
			case "maximize": {
				if (args[1].equals("all") || args[1].equals("progress") || args[1].equals("progression")) {
					ProgressionManager.instance.maxPlayerProgression(ep, false);
					this.sendChatToSender(ics, EnumChatFormatting.GREEN+"Progression maximized for "+ep.getCommandSenderName());
				}
				if (args[1].equals("all") || args[1].equals("fragment") || args[1].equals("research")) {
					ChromaResearchManager.instance.maxPlayerResearch(ep, false);
					this.sendChatToSender(ics, EnumChatFormatting.GREEN+"Fragments maximized for "+ep.getCommandSenderName());
				}
				if (args[1].equals("all") || args[1].equals("ability") || args[1].equals("abilities")) {
					for (Ability c : Chromabilities.getAbilities()) {
						if (!Chromabilities.playerHasAbility(ep, c))
							Chromabilities.give(ep, c);
					}
					this.sendChatToSender(ics, EnumChatFormatting.GREEN+"Abilities maximized for "+ep.getCommandSenderName());
				}
				if (args[1].equals("all") || args[1].equals("dimstruct")) {
					for (int i = 0; i < 16; i++) {
						ProgressionManager.instance.markPlayerCompletedStructureColor(ep, CrystalElement.elements[i], true, false);
					}
					this.sendChatToSender(ics, EnumChatFormatting.GREEN+"Dimstruct maximized for "+ep.getCommandSenderName());
				}
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
					sendChatToSender(ics, "Progress for "+ep.getCommandSenderName()+":\n"+c.toString());
					sendChatToSender(ics, "Elements for "+ep.getCommandSenderName()+":\n"+c2.toString());
				}
				if (args[1].equals("all") || args[1].equals("dimstruct")) {
					Collection<CrystalElement> c3 = ProgressionManager.instance.getStructuresFor(ep);
					sendChatToSender(ics, "Structure Flags for "+ep.getCommandSenderName()+":\n"+c3.toString());
				}
				break;
			}
			default:
				this.sendChatToSender(ics, EnumChatFormatting.RED+" Unrecognized progress action/type '"+args[0]+"'.");
				break;
		}
		ProgressionManager.instance.updateChunks(ep);
	}

	private CrystalElement getColor(String s) {
		try {
			return CrystalElement.valueOf(s.toUpperCase());
		}
		catch (IllegalArgumentException e) {

		}
		return CrystalElement.getByName(ReikaStringParser.capFirstChar(s.toLowerCase(Locale.ENGLISH)));
	}

	@Override
	public String getCommandString() {
		return "chromaprog";
	}

	@Override
	protected boolean isAdminOnly() {
		return true;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender ics, String[] args) {
		return relay != null ? relay.addTabCompletionOptions(ics, args) : super.addTabCompletionOptions(ics, args);
	}

}
