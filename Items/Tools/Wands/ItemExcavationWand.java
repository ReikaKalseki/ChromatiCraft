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
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import Reika.ChromatiCraft.Base.ItemWandBase;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Auxiliary.ProgressiveRecursiveBreaker;
import Reika.DragonAPI.Auxiliary.ProgressiveRecursiveBreaker.BreakerCallback;
import Reika.DragonAPI.Auxiliary.ProgressiveRecursiveBreaker.ProgressiveBreaker;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Libraries.ReikaEnchantmentHelper;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;

public class ItemExcavationWand extends ItemWandBase implements BreakerCallback {

	private static final int MAX_DEPTH = 12;
	private static final int MAX_DEPTH_BOOST = 18;

	private static HashMap<Integer, EntityPlayer> breakers = new HashMap();

	public ItemExcavationWand(int index) {
		super(index);
		this.addEnergyCost(CrystalElement.BROWN, 1);
		this.addEnergyCost(CrystalElement.YELLOW, 2);
	}

	@Override
	public float getDigSpeed(ItemStack is, Block b, int meta) {
		return 5;
	}

	@Override
	public boolean canHarvestBlock(Block b, ItemStack is) {
		return b != Blocks.stone && b != Blocks.netherrack && b != Blocks.end_stone;
	}

	@Override
	public boolean onBlockStartBreak(ItemStack itemstack, int x, int y, int z, EntityPlayer ep) {
		World world = ep.worldObj;
		if (!world.isRemote) {
			ProgressiveBreaker b = ProgressiveRecursiveBreaker.instance.addCoordinateWithReturn(world, x, y, z, this.getDepth(ep));
			//b.looseMatches.put(Blocks.redstone_ore, new BlockKey(Blocks.lit_redstone_ore));
			b.call = this;
			b.silkTouch = ReikaEnchantmentHelper.getEnchantmentLevel(Enchantment.silkTouch, itemstack) > 0;
			b.drops = !ep.capabilities.isCreativeMode;
			b.fortune = ReikaEnchantmentHelper.getEnchantmentLevel(Enchantment.fortune, itemstack);
			Block bk = ep.worldObj.getBlock(x, y, z);
			if (bk == Blocks.lit_redstone_ore)
				b.addBlock(new BlockKey(Blocks.redstone_ore));
			else if (bk == Blocks.redstone_ore)
				b.addBlock(new BlockKey(Blocks.lit_redstone_ore));
			breakers.put(b.hashCode(), ep);
		}
		return true;
	}

	public static int getDepth(EntityPlayer ep) {
		return canUseBoostedEffect(ep) ? MAX_DEPTH_BOOST : MAX_DEPTH;
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
				return this.sufficientEnergy(ep) && (world.isRemote || ReikaPlayerAPI.playerCanBreakAt((WorldServer)world, x, y, z, (EntityPlayerMP)ep));
			}
		}
		return false;
	}

	@Override
	public void onFinish(ProgressiveBreaker b) {
		breakers.remove(b.hashCode());
	}

}
