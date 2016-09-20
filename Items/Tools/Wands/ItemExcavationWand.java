/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items.Tools.Wands;

import java.util.HashMap;
import java.util.HashSet;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import Reika.ChromatiCraft.Base.ItemWandBase;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalNetworkTile;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.ProgressiveRecursiveBreaker;
import Reika.DragonAPI.Auxiliary.ProgressiveRecursiveBreaker.BreakerCallback;
import Reika.DragonAPI.Auxiliary.ProgressiveRecursiveBreaker.ProgressiveBreaker;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Libraries.ReikaEnchantmentHelper;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.ModInteract.ItemHandlers.TwilightForestHandler;
import Reika.GeoStrata.Registry.GeoBlocks;

public class ItemExcavationWand extends ItemWandBase implements BreakerCallback {

	private static final int MAX_DEPTH = 12;
	private static final int MAX_DEPTH_BOOST = 18;

	private static final HashMap<Integer, EntityPlayer> breakers = new HashMap();

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
		return true;
	}

	@Override
	public int getHarvestLevel(ItemStack stack, String toolClass) {
		return Integer.MAX_VALUE;
	}

	public static boolean spreadOn(World world, int x, int y, int z, Block b, int meta) {
		if (world.provider.isSurfaceWorld() && b == Blocks.stone)
			return false;
		if (world.provider.isHellWorld && b == Blocks.netherrack)
			return false;
		if (world.provider.dimensionId == 1 && b == Blocks.end_stone)
			return false;
		return !(world.getTileEntity(x, y, z) instanceof CrystalNetworkTile);
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
			if (ep.isSneaking())
				b.extraSpread = true;
			if (ReikaEnchantmentHelper.getEnchantmentLevel(Enchantment.field_151369_A, itemstack) > 0) { //lure
				b.dropInventory = ep.inventory;
			}
			b.taxiCabDistance = true;
			b.player = ep;
			b.hungerFactor = 0.125F;
			HashSet<BlockKey> set = getSpreadBlocks(world, x, y, z);
			for (BlockKey bk : set) {
				b.addBlock(bk);
			}
			breakers.put(b.hashCode(), ep);
		}
		return true;
	}

	public static HashSet<BlockKey> getSpreadBlocks(World world, int x, int y, int z) {
		HashSet<BlockKey> set = new HashSet();
		Block bk = world.getBlock(x, y, z);
		if (bk == Blocks.lit_redstone_ore)
			set.add(new BlockKey(Blocks.redstone_ore));
		else if (bk == Blocks.redstone_ore)
			set.add(new BlockKey(Blocks.lit_redstone_ore));
		else if (bk == ChromaBlocks.GLOWLEAF.getBlockInstance()) {
			for (int i = 0; i < 16; i++) {
				set.add(new BlockKey(bk, i));
			}
		}
		else if (ModList.GEOSTRATA.isLoaded() && bk == GeoBlocks.LAVAROCK.getBlockInstance()) {
			for (int i = 0; i < 16; i++) {
				set.add(new BlockKey(bk, i));
			}
		}
		else if (bk == TwilightForestHandler.BlockEntry.NAGASTONE.getBlock()) {
			for (int i = 0; i < 16; i++) {
				set.add(new BlockKey(bk, i));
			}
		}
		else if (bk == TwilightForestHandler.BlockEntry.AURORA.getBlock()) {
			for (int i = 0; i < 16; i++) {
				set.add(new BlockKey(bk, i));
			}
		}
		return set;
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
				return this.sufficientEnergy(ep) && this.canBreakBlock(world, x, y, z, id, meta, ep);
			}
		}
		return false;
	}

	private boolean canBreakBlock(World world, int x, int y, int z, Block id, int meta, EntityPlayer ep) {
		return (world.isRemote || ReikaPlayerAPI.playerCanBreakAt((WorldServer)world, x, y, z, (EntityPlayerMP)ep)) && this.spreadOn(world, x, y, z, id, meta);
	}

	@Override
	public void onFinish(ProgressiveBreaker b) {
		breakers.remove(b.hashCode());
	}

}
