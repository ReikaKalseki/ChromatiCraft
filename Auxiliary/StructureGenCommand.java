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
import net.minecraft.entity.player.EntityPlayer;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator.DimensionStructureType;
import Reika.ChromatiCraft.World.Dimension.ChunkProviderChroma;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Command.DragonCommandBase;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;

public class StructureGenCommand extends DragonCommandBase {

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		if (DragonAPICore.isReikasComputer() && ReikaObfuscationHelper.isDeObfEnvironment()) {
			EntityPlayer ep = this.getCommandSenderAsPlayer(ics);
			ReikaChatHelper.sendChatToPlayer(ep, "Generating structures...");
			ChunkProviderChroma.triggerStructureGen();
			ReikaChatHelper.sendChatToPlayer(ep, "Generation complete.");
			if (args.length > 0 && args[0].equals("true")) {
				ReikaChatHelper.sendChatToPlayer(ep, "Placing structures...");
				while (!ChunkProviderChroma.areStructuresReady()) {
					try {
						Thread.sleep(100);
					}
					catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				for (int i = 0; i < DimensionStructureGenerator.DimensionStructureType.types.length; i++) {
					DimensionStructureType type = DimensionStructureType.types[i];
					ReikaChatHelper.sendChatToPlayer(ep, "Generating "+type+"...");
					type.getGenerator().generateAll(ep.worldObj);
					ReikaChatHelper.sendChatToPlayer(ep, "Generating "+type+" complete.");
				}
				ReikaChatHelper.sendChatToPlayer(ep, "Placing complete.");
			}
		}
	}

	@Override
	public String getCommandString() {
		return "gendimstructures";
	}

	@Override
	protected boolean isAdminOnly() {
		return true;
	}



}
