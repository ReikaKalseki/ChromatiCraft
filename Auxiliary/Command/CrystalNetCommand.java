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

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import Reika.ChromatiCraft.Magic.Network.CrystalNetworker;
import Reika.DragonAPI.Command.DragonCommandBase;


public class CrystalNetCommand extends DragonCommandBase {

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		EntityPlayer ep = this.getCommandSenderAsPlayer(ics);
		World world = args.length >= 1 ? args[0].equals("*") ? null : DimensionManager.getWorld(Integer.parseInt(args[0])) : ep.worldObj;
		int cx = args.length >= 3 ? Integer.parseInt(args[1])/16 : args.length > 0 ? -1 : MathHelper.floor_double(ep.posX)/16;
		int cz = args.length >= 3 ? Integer.parseInt(args[2])/16 : args.length > 0 ? -1 : MathHelper.floor_double(ep.posZ)/16;
		CrystalNetworker.instance.printCrystalNetwork(world, cx, cz);
	}

	@Override
	public String getCommandString() {
		return "printcrystalnet";
	}

	@Override
	protected boolean isAdminOnly() {
		return true;
	}

}
