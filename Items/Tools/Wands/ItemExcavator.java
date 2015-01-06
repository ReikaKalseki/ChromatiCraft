/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items.Tools.Wands;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import Reika.ChromatiCraft.Base.ItemWandBase;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Auxiliary.ProgressiveRecursiveBreaker;
import Reika.DragonAPI.Auxiliary.ProgressiveRecursiveBreaker.BreakerCallback;
import Reika.DragonAPI.Auxiliary.ProgressiveRecursiveBreaker.ProgressiveBreaker;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;

public class ItemExcavator extends ItemWandBase implements BreakerCallback {

	public static final int MAX_DEPTH = 12;

	private static HashMap<Integer, EntityPlayer> breakers = new HashMap();

	public ItemExcavator(int index) {
		super(index);
		this.addEnergyCost(CrystalElement.BROWN, 1);
		this.addEnergyCost(CrystalElement.YELLOW, 2);
	}

	@Override
	public boolean onBlockStartBreak(ItemStack itemstack, int x, int y, int z, EntityPlayer ep) {
		World world = ep.worldObj;
		if (!world.isRemote) {
			ProgressiveBreaker b = ProgressiveRecursiveBreaker.instance.addCoordinateWithReturn(world, x, y, z, MAX_DEPTH);
			b.call = this;
			breakers.put(b.hashCode(), ep);
		}
		return true;
	}

	@Override
	public void onBreak(ProgressiveBreaker b, World world, int x, int y, int z, Block id, int meta) {
		EntityPlayer ep = breakers.get(b.hashCode());
		if (ep != null) {
			boolean exists = world.getPlayerEntityByName(ep.getCommandSenderName()) != null;
			if (exists) {
				this.drainPlayer(ep);
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
				return this.sufficientEnergy(ep) && (world.isRemote || ReikaPlayerAPI.playerCanBreakAt((WorldServer)world, x, y, z, ep));
			}
		}
		return false;
	}

	@Override
	public void onFinish(ProgressiveBreaker b) {
		breakers.remove(b.hashCode());
	}

}
