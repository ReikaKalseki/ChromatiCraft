/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import java.util.Locale;

import net.minecraft.command.ICommandSender;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalNetworkTile;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalReceiver;
import Reika.ChromatiCraft.Magic.Network.CrystalFlow;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Command.DragonCommandBase;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;

public class CrystalNetworkLogger {

	private static LoggingLevel level = LoggingLevel.NONE;

	public static void logRequest(CrystalReceiver r, CrystalElement e, int amount, CrystalFlow p) {
		if (level.isAtLeast(LoggingLevel.CORE)) {
			String st = p == null ? "failed" : "succeeded from "+p.transmitter;
			String s = r+" has requested "+amount+" of "+e+"; request "+st;
			if (p != null)
				s = s+"; max throughput of "+p.maxFlow+", total cost "+p.totalCost+" (loss = "+p.getSignalLoss()+")";
			ChromatiCraft.logger.log(s);
			dumpStack();
		}
	}

	public static void logPathFind(CrystalReceiver target, CrystalElement e, CrystalReceiver r, String transmitters, String steps) {
		if (level.isAtLeast(LoggingLevel.PATHFIND)) {
			String s = "Pathfinding "+e+" to "+target+", at "+r;
			s += "  Potential next hops:"+transmitters;
			s += "  Current path: "+steps;
			ChromatiCraft.logger.log(s);
			dumpStack();
		}
	}

	public static void logPathCalculation(String name, Object val) {
		if (level.isAtLeast(LoggingLevel.PATHCALC)) {
			String s = "Path calculation; key="+name+", value="+val;
			ChromatiCraft.logger.log(s);
			dumpStack();
		}
	}

	public static void logFlowBreak(CrystalFlow p, FlowFail f) {
		if (level.isAtLeast(LoggingLevel.CORE)) {
			String s = p.element+" flow from "+p.transmitter+" to "+p.receiver+" broken due to: "+f.text+". "+p.getRemainingLumens()+" lumens left untransferred.";
			ChromatiCraft.logger.log(s);
			dumpStack();
		}
	}

	public static void logFlowSatisfy(CrystalFlow p) {
		if (level.isAtLeast(LoggingLevel.CORE)) {
			String s = p.element+" flow from "+p.transmitter+" to "+p.receiver+" satisfied and terminated. "+p.totalCost+" lumens transferred.";
			ChromatiCraft.logger.log(s);
			dumpStack();
		}
	}

	public static void logFlowTick(CrystalFlow p, int amt) {
		if (level.isAtLeast(LoggingLevel.ALL)) {
			String s = "Ticking "+p.element+" flow from "+p.transmitter+" to "+p.receiver+"; "+amt+" lumens transferred this tick; "+p.getRemainingLumens()+" remaining.";
			ChromatiCraft.logger.log(s);
			dumpStack();
		}
	}

	public static void logTileAdd(CrystalNetworkTile te) {
		if (level.isAtLeast(LoggingLevel.STATE)) {
			String s = "Added tile "+te+" to network; UUID = "+(te != null ? te.getUniqueID() : "[]");
			ChromatiCraft.logger.log(s);
			dumpStack();
		}
	}

	public static void logTileRemove(CrystalNetworkTile te) {
		if (level.isAtLeast(LoggingLevel.STATE)) {
			String s = "Removed tile "+te+" from network; UUID = "+(te != null ? te.getUniqueID() : "[]");
			ChromatiCraft.logger.log(s);
			dumpStack();
		}
	}

	private static void dumpStack() {
		if (level == LoggingLevel.STACK)
			Thread.dumpStack();
	}

	public static enum FlowFail {
		SIGHT("Line of Sight"),
		ENERGY("Insufficient Energy or Disabled Transmitter"),
		TILE("Missing Network Tile"),
		FULL("Target is full");

		public final String text;

		private FlowFail(String s) {
			text = s;
		}
	}

	public static LoggingLevel getLogLevel() {
		return level;
	}

	public static class NetworkLoggerCommand extends DragonCommandBase {

		@Override
		public void processCommand(ICommandSender ics, String[] args) {
			try {
				level = LoggingLevel.valueOf(args[0].toUpperCase(Locale.ENGLISH));
			}
			catch (Exception e) {
				level = LoggingLevel.NONE;
			}
			ReikaChatHelper.sendChatToAllOnServer("Crystal Network Logger Status: "+level);
		}

		@Override
		public String getCommandString() {
			return "networklog";
		}

		@Override
		protected boolean isAdminOnly() {
			return true;
		}

	}

	public static enum LoggingLevel {
		NONE(),
		CORE(),
		PATHCALC(),
		PATHFIND(),
		STATE(),
		ALL(),
		STACK();

		public boolean isAtLeast(LoggingLevel l) {
			return this.ordinal() >= l.ordinal();
		}
	}

}
