/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.Command;

import java.util.ArrayList;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumChatFormatting;
import Reika.ChromatiCraft.Auxiliary.GuardianStoneManager;
import Reika.ChromatiCraft.Auxiliary.ProtectionZone;
import Reika.ChromatiCraft.TileEntity.AOE.Defence.TileEntityGuardianStone;
import Reika.DragonAPI.Command.DragonCommandBase;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;

public class GuardianCommand extends DragonCommandBase {

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		if (args == null || args.length != 2 || args[0] == null || args[1] == null || args[0].isEmpty() || args[1].isEmpty()) {
			StringBuilder sb = new StringBuilder();
			sb.append("You must specify a valid name and whether to add or remove it.");
			sb.append("\n");
			sb.append("Command format: /"+this.getCommandString()+" [add/remove] <playername>");
			ReikaChatHelper.sendChatToPlayer(ep, EnumChatFormatting.RED+sb.toString());
			return;
		}
		String command = args[0];
		String name = args[1];
		if (command.equals("add")) {
			this.addPlayerToLists(ep, name);
		}
		else if (command.equals("remove")) {
			this.removePlayerFromLists(ep, name);
		}
	}

	private void addPlayerToLists(EntityPlayerMP ep, String name) {
		ArrayList<ProtectionZone> zones = GuardianStoneManager.instance.getProtectionZonesForPlayer(ep);
		for (int i = 0; i < zones.size(); i++) {
			ProtectionZone zone = zones.get(i);
			TileEntityGuardianStone te = zone.getControllingGuardianStone();
			if (te != null) {
				te.addPlayer(name);
			}
		}

		String sg = "Player "+name+" added to your Guardian Stone permissions.";
		ReikaChatHelper.sendChatToPlayer(ep, sg);
	}

	private void removePlayerFromLists(EntityPlayerMP ep, String name) {
		ArrayList<ProtectionZone> zones = GuardianStoneManager.instance.getProtectionZonesForPlayer(ep);
		for (int i = 0; i < zones.size(); i++) {
			ProtectionZone zone = zones.get(i);
			TileEntityGuardianStone te = zone.getControllingGuardianStone();
			if (te != null) {
				te.removePlayer(name);
			}
		}

		String sg = "Player "+name+" removed from your Guardian Stone permissions.";
		ReikaChatHelper.sendChatToPlayer(ep, sg);
	}

	@Override
	protected boolean isAdminOnly() {
		return false;
	}

	@Override
	public String getCommandString() {
		return "guardstone";
	}
}
