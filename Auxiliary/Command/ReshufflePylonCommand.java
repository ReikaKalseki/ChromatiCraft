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

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Random;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Auxiliary.ChromaAux;
import Reika.ChromatiCraft.Magic.Network.CrystalNetworker;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalPylon;
import Reika.DragonAPI.Command.DragonCommandBase;


public class ReshufflePylonCommand extends DragonCommandBase {

	private static final Random rand = new Random();

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		EntityPlayer ep = this.getCommandSenderAsPlayer(ics);
		World world = ep.worldObj;
		int x = MathHelper.floor_double(ep.posX);
		int y = MathHelper.floor_double(ep.posY);
		int z = MathHelper.floor_double(ep.posZ);
		int r = Math.max(1024, Integer.parseInt(args[0]));
		Collection<TileEntityCrystalPylon> c = CrystalNetworker.instance.getAllNearbyPylons(world, x, y, z, r, false);
		EnumMap<CrystalElement, Integer> map = new EnumMap(CrystalElement.class);
		ArrayList<CrystalElement> unused = new ArrayList(map.keySet());
		int num = 0;
		for (TileEntityCrystalPylon te : c) {
			int idx = unused.isEmpty() ? -1 : rand.nextInt(unused.size());
			CrystalElement e = idx == -1 ? CrystalElement.randomElement() : unused.get(idx);
			if (idx != -1)
				unused.remove(idx);
			ChromaAux.changePylonColor(world, te, e);
			Integer get = map.get(e);
			int val = get != null ? get.intValue() : 0;
			map.put(e, val+1);
			num++;
		}
		this.sendChatToSender(ics, num+" pylons reshuffled.");
		for (int i = 0; i < 16; i++) {
			CrystalElement e = CrystalElement.elements[i];
			this.sendChatToSender(ics, e.displayName+": "+map.get(e)+" Pylons");
		}
	}

	@Override
	public String getCommandString() {
		return "shufflepylons";
	}

	@Override
	protected boolean isAdminOnly() {
		return true;
	}

}
