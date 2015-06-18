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

import net.minecraft.command.ICommandSender;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalReceiver;
import Reika.ChromatiCraft.Magic.Network.CrystalFlow;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Command.DragonCommandBase;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;

public class CrystalNetworkLogger {

	private static boolean active = false;

	public static void logRequest(CrystalReceiver r, CrystalElement e, int amount, CrystalFlow p) {
		if (active) {
			String st = p == null ? "failed" : "succeeded from "+p.transmitter;
			String s = r+" has requested "+amount+" of "+e+"; request "+st;
			ChromatiCraft.logger.log(s);
		}
	}

	public static void logFlowBreak(CrystalFlow p, FlowFail f) {
		if (active) {
			String s = p.element+" flow from "+p.transmitter+" to "+p.receiver+" broken due to: "+f.text+".";
			ChromatiCraft.logger.log(s);
		}
	}

	public static void logFlowSatisfy(CrystalFlow p) {
		if (active) {
			String s = p.element+" flow from "+p.transmitter+" to "+p.receiver+" satisfied and terminated.";
			ChromatiCraft.logger.log(s);
		}
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

	public static class NetworkLoggerCommand extends DragonCommandBase {

		@Override
		public void processCommand(ICommandSender ics, String[] args) {
			active = Boolean.parseBoolean(args[0]);
			ReikaChatHelper.sendChatToAllOnServer("Crystal Network Logger Status: "+active);
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

}
