/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items.Tools.Wands;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import Reika.ChromatiCraft.Base.ItemWandBase;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Auxiliary.ProgressiveRecursiveBreaker;
import Reika.DragonAPI.Auxiliary.ProgressiveRecursiveBreaker.BreakerCallback;
import Reika.DragonAPI.Auxiliary.ProgressiveRecursiveBreaker.ProgressiveBreaker;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;

public class ItemTransitionWand extends ItemWandBase implements BreakerCallback {

	private static HashMap<Integer, BlockReplace> breakers = new HashMap();

	public static final int MAX_DEPTH = 18;

	public ItemTransitionWand(int index) {
		super(index);
		this.addEnergyCost(CrystalElement.GRAY, 2);
		this.addEnergyCost(CrystalElement.YELLOW, 1);
	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World world, int x, int y, int z, int s, float a, float b, float c) {
		if (!world.isRemote) {
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
				//br.tickRate = 2;
				br.player = ep;
				br.silkTouch = true;
				breakers.put(br.hashCode(), new BlockReplace(ep, id, meta));
			}
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
				this.drainPlayer(r.player);
				int slot = ReikaInventoryHelper.locateInInventory(r.place, r.placeM, r.player.inventory.mainInventory);
				if (slot != -1) {
					ReikaInventoryHelper.decrStack(slot, r.player.inventory.mainInventory);
				}
				world.setBlock(x, y, z, r.place, r.placeM, 3);
				ReikaSoundHelper.playPlaceSound(world, x, y, z, r.place);
				ArrayList<ItemStack> li = id.getDrops(world, x, y, z, meta, 0);
				for (ItemStack is : li) {
					r.drops.add(is);
				}
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
				if (this.sufficientEnergy(r.player) && ReikaPlayerAPI.playerHasOrIsCreative(r.player, r.place, r.placeM)) {
					return world.isRemote || ReikaPlayerAPI.playerCanBreakAt((WorldServer)world, x, y, z, r.player);
				}
			}
		}
		return false;
	}

	@Override
	public void onFinish(ProgressiveBreaker b) {
		BlockReplace r = breakers.get(b.hashCode());
		for (ItemStack is : r.drops) {
			boolean add = ReikaInventoryHelper.addToIInv(is, r.player.inventory);
			if (!add)
				r.player.dropPlayerItemWithRandomChoice(is, true);
		}
		breakers.remove(b.hashCode());
	}

	private static class BlockReplace {
		private final EntityPlayer player;
		private final Block place;
		private final int placeM;
		private ArrayList<ItemStack> drops = new ArrayList();

		private BlockReplace(EntityPlayer ep, Block b, int meta) {
			place = b;
			placeM = meta;
			player = ep;
		}
	}

}
