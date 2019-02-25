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

import Reika.ChromatiCraft.ModInterface.ThaumCraft.NodeRecharger;
import Reika.DragonAPI.Command.DragonCommandBase;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.ModRegistry.InterfaceCache;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;


public class NodeWrapperInspectionCommand extends DragonCommandBase {

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		EntityPlayer ep = this.getCommandSenderAsPlayer(ics);
		MovingObjectPosition mov = ReikaPlayerAPI.getLookedAtBlock(ep, 5, false);
		if (mov != null) {
			TileEntity te = ep.worldObj.getTileEntity(mov.blockX, mov.blockY, mov.blockZ);
			if (InterfaceCache.NODE.instanceOf(te)) {
				WorldLocation loc = new WorldLocation(te);
				if (NodeRecharger.instance.hasLocation(loc)) {
					for (String s : NodeRecharger.instance.debug(loc)) {
						this.sendChatToSender(ics, s);
					}
				}
			}
		}
	}

	@Override
	public String getCommandString() {
		return "nodewrapper";
	}

	@Override
	protected boolean isAdminOnly() {
		return true;
	}

}
