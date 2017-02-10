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

import java.util.Locale;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import Reika.ChromatiCraft.Auxiliary.ChromaStructures.Structures;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Command.DragonCommandBase;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;


public class PlaceStructureCommand extends DragonCommandBase {

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		Structures s = Structures.valueOf(args[0].toUpperCase(Locale.ENGLISH));
		EntityPlayer ep = this.getCommandSenderAsPlayer(ics);
		int x = MathHelper.floor_double(ep.posX);
		int y = MathHelper.floor_double(ep.posY);
		int z = MathHelper.floor_double(ep.posZ);
		FilledBlockArray arr = s.getArray(ep.worldObj, x, y, z, s.requiresColor ? CrystalElement.valueOf(args[1].toUpperCase(Locale.ENGLISH)) : null);
		arr.place();
	}

	@Override
	public String getCommandString() {
		return "chromastruct";
	}

	@Override
	protected boolean isAdminOnly() {
		return true;
	}

}
