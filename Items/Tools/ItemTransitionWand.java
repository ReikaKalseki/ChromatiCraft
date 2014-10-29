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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.ChromatiCraft.Magic.PlayerElementBuffer;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Auxiliary.ProgressiveRecursiveBreaker;
import Reika.DragonAPI.Auxiliary.ProgressiveRecursiveBreaker.BreakerCallback;
import Reika.DragonAPI.Auxiliary.ProgressiveRecursiveBreaker.ProgressiveBreaker;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;

public class ItemTransitionWand extends ItemChromaTool implements BreakerCallback {

	private static HashMap<Integer, BlockReplace> breakers = new HashMap();

	public static final int MAX_DEPTH = 18;

	public ItemTransitionWand(int index) {
		super(index);
	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World world, int x, int y, int z, int s, float a, float b, float c) {
		if (ep.isSneaking()) {
			this.setStoredItem(is, ReikaBlockHelper.getWorldBlockAsItemStack(world, x, y, z));
		}
		else {
			ItemStack store = this.getStoredItem(is);
			if (store == null)
				return false;
			Block id = Block.getBlockFromItem(store.getItem());
			int meta = store.getItemDamage();
			if (id == null)
				return false;
			ProgressiveBreaker br = ProgressiveRecursiveBreaker.instance.addCoordinateWithReturn(world, x, y, z, MAX_DEPTH);
			br.call = this;
			br.drops = false;
			//br.extraSpread = true;
			br.player = ep;
			br.silkTouch = true;
			breakers.put(br.hashCode(), new BlockReplace(ep, id, meta));
		}
		return true;
	}

	public static ItemStack getStoredItem(ItemStack is) {
		if (is.stackTagCompound != null) {
			NBTTagCompound tag = is.stackTagCompound.getCompoundTag("stored");
			ItemStack ret = ItemStack.loadItemStackFromNBT(tag);
			return ret;
		}
		return null;
	}

	public static void setStoredItem(ItemStack tool, ItemStack is) {
		//if (is.stackTagCompound == null)
		tool.stackTagCompound = new NBTTagCompound();
		NBTTagCompound tag = new NBTTagCompound();
		is.writeToNBT(tag);
		tool.stackTagCompound.setTag("stored", tag);
	}

	@Override
	public void onBreak(ProgressiveBreaker b, World world, int x, int y, int z, Block id, int meta) {
		BlockReplace r = breakers.get(b.hashCode());
		if (r != null) {
			boolean exists = world.getPlayerEntityByName(r.player.getCommandSenderName()) != null;
			if (exists) {
				PlayerElementBuffer.instance.removeFromPlayer(r.player, CrystalElement.GRAY, 10);
				PlayerElementBuffer.instance.removeFromPlayer(r.player, CrystalElement.YELLOW, 5);
				int slot = ReikaInventoryHelper.locateInInventory(r.place, r.placeM, r.player.inventory.mainInventory);
				if (slot != -1) {
					ReikaInventoryHelper.decrStack(slot, r.player.inventory.mainInventory);
				}
				world.setBlock(x, y, z, r.place, r.placeM, 3);
				ItemStack is = new ItemStack(r.place, 1, r.placeM);
				boolean add = ReikaInventoryHelper.addToIInv(is, r.player.inventory);
				if (!add)
					r.player.dropPlayerItemWithRandomChoice(is, true);
			}
			else {
				b.terminate();
			}
		}
	}

	@Override
	public boolean canBreak(ProgressiveBreaker b, World world, int x, int y, int z, Block id, int meta) {
		BlockReplace r = breakers.get(b.hashCode());
		if (r != null) {
			boolean exists = world.getPlayerEntityByName(r.player.getCommandSenderName()) != null;
			if (exists) {
				boolean b1 = PlayerElementBuffer.instance.playerHas(r.player, CrystalElement.GRAY, 10);
				boolean b2 = PlayerElementBuffer.instance.playerHas(r.player, CrystalElement.YELLOW, 5);
				return b1 && b2 && ReikaPlayerAPI.playerHasOrIsCreative(r.player, r.place, r.placeM);
			}
		}
		return false;
	}

	@Override
	public void onFinish(ProgressiveBreaker b) {
		breakers.remove(b.hashCode());
	}

	private static class BlockReplace {
		private final EntityPlayer player;
		private final Block place;
		private final int placeM;

		private BlockReplace(EntityPlayer ep, Block b, int meta) {
			place = b;
			placeM = meta;
			player = ep;
		}
	}

}
