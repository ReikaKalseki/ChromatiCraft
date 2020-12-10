/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.Command;

import java.util.Arrays;
import java.util.HashSet;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import Reika.ChromatiCraft.Base.DimensionStructureGenerator.DimensionStructureType;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator.StructurePair;
import Reika.ChromatiCraft.World.Dimension.ChunkProviderChroma;
import Reika.ChromatiCraft.World.Dimension.StructureCalculator;
import Reika.ChromatiCraft.World.Dimension.ThreadedGenerators;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Command.DragonCommandBase;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.Java.ReikaArrayHelper;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;

public class StructureGenCommand extends DragonCommandBase {

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		if (DragonAPICore.isReikasComputer() && ReikaObfuscationHelper.isDeObfEnvironment()) {
			EntityPlayer ep = this.getCommandSenderAsPlayer(ics);
			ReikaChatHelper.sendChatToPlayer(ep, "Generating structures...");
			if (args.length > 0) {
				if (args[0].equals("finished")) {
					StructureCalculator.allowUnfinishedStructures = false;
					if (args.length > 1)
						args = Arrays.copyOfRange(args, 1, args.length);
					else
						args = new String[0];
				}
			}
			ChunkProviderChroma.triggerGenerator(ThreadedGenerators.STRUCTURE);
			ReikaChatHelper.sendChatToPlayer(ep, "Generation complete.");
			if (args.length > 0) {
				ReikaChatHelper.sendChatToPlayer(ep, "Placing "+args.length+" structures ("+Arrays.toString(args)+")...");
				while (!ChunkProviderChroma.areGeneratorsReady()) {
					try {
						Thread.sleep(100);
					}
					catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				HashSet<DimensionStructureType> set = new HashSet();
				for (StructurePair p : ChunkProviderChroma.getStructures()) {
					p.generatedDimension = ep.worldObj.provider.dimensionId;
					DimensionStructureType type = p.generator.getType();
					if (set.contains(type)) //only gen one of each
						continue;
					set.add(type);
					if (ReikaArrayHelper.arrayContains(args, type.name(), true)) {
						ReikaChatHelper.sendChatToPlayer(ep, "Generating "+type+" @ "+p.generator.getEntryPosX()+", "+p.generator.getEntryPosZ()+"...");
						p.generator.generateAll(ep.worldObj);
						ReikaChatHelper.sendChatToPlayer(ep, "Generating "+type+" complete.");
					}
					else {
						ReikaChatHelper.sendChatToPlayer(ep, "Not generating "+type+". "+Arrays.toString(args)+" does not contain '"+type.name()+"'.");
					}
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
