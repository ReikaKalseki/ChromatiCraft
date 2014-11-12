package Reika.ChromatiCraft.Auxiliary;

import net.minecraft.command.ICommandSender;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Magic.CrystalFlow;
import Reika.ChromatiCraft.Magic.CrystalReceiver;
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
