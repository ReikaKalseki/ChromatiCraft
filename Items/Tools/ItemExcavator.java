/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items.Tools;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.ChromatiCraft.Magic.PlayerElementBuffer;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Auxiliary.ProgressiveRecursiveBreaker;
import Reika.DragonAPI.Auxiliary.ProgressiveRecursiveBreaker.BreakerCallback;
import Reika.DragonAPI.Auxiliary.ProgressiveRecursiveBreaker.ProgressiveBreaker;

public class ItemExcavator extends ItemChromaTool implements BreakerCallback {

	public static final int MAX_DEPTH = 12;

	private static HashMap<Integer, EntityPlayer> breakers = new HashMap();

	public ItemExcavator(int index) {
		super(index);
	}

	@Override
	public boolean onBlockStartBreak(ItemStack itemstack, int x, int y, int z, EntityPlayer ep) {
		World world = ep.worldObj;
		ProgressiveBreaker b = ProgressiveRecursiveBreaker.instance.addCoordinateWithReturn(world, x, y, z, MAX_DEPTH);
		b.call = this;
		breakers.put(b.hashCode(), ep);
		return true;
	}

	@Override
	public void onBreak(ProgressiveBreaker b, World world, int x, int y, int z, Block id, int meta) {
		EntityPlayer ep = breakers.get(b.hashCode());
		if (ep != null) {
			boolean exists = world.getPlayerEntityByName(ep.getCommandSenderName()) != null;
			if (exists) {
				PlayerElementBuffer.instance.removeFromPlayer(ep, CrystalElement.BROWN, 1);
				PlayerElementBuffer.instance.removeFromPlayer(ep, CrystalElement.YELLOW, 2);
			}
			else {
				b.terminate();
			}
		}
	}

	@Override
	public boolean canBreak(ProgressiveBreaker b, World world, int x, int y, int z, Block id, int meta) {
		EntityPlayer ep = breakers.get(b.hashCode());
		if (ep != null) {
			boolean exists = world.getPlayerEntityByName(ep.getCommandSenderName()) != null;
			if (exists) {
				boolean b1 = PlayerElementBuffer.instance.playerHas(ep, CrystalElement.BROWN, 1);
				boolean b2 = PlayerElementBuffer.instance.playerHas(ep, CrystalElement.YELLOW, 2);
				return b1 && b2;
			}
		}
		return false;
	}

	@Override
	public void onFinish(ProgressiveBreaker b) {
		breakers.remove(b.hashCode());
	}

}
