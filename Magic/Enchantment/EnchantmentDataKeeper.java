/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic.Enchantment;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Base.ChromaticEnchantment;
import Reika.ChromatiCraft.Registry.ChromaEnchants;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.IC2RubberLogHandler;


public class EnchantmentDataKeeper extends ChromaticEnchantment {

	private static final HashMap<BlockKey, BlockHandler> handlers = new HashMap();

	public EnchantmentDataKeeper(int id) {
		super(id, EnumEnchantmentType.digger);
	}

	@Override
	public int getMaxLevel() {
		return 1;
	}

	@Override
	public boolean isVisibleToPlayer(EntityPlayer ep) {
		return true;
	}

	public static boolean handleBreak(World world, int x, int y, int z, Block b, int meta, EntityPlayer ep) {
		if (handlers.isEmpty())
			initHandlers();
		if (ep == null || ReikaPlayerAPI.isFake(ep))
			return false;
		ItemStack tool = ep.getCurrentEquippedItem();
		if (ChromaEnchants.DATAKEEP.getLevel(tool) <= 0)
			return false;
		BlockHandler h = handlers.get(new BlockKey(b, meta));
		if (h == null)
			return false;
		ItemStack is = h.getItem(world, x, y, z, b, meta, ep);
		if (is == null && ReikaBlockHelper.isOre(b, meta))
			is = new ItemStack(b, meta);
		if (is == null)
			return false;
		if (ChromaEnchants.AUTOCOLLECT.getLevel(is) > 0)
			ReikaPlayerAPI.addOrDropItem(is, ep);
		else
			ReikaItemHelper.dropItem(world, x+world.rand.nextDouble(), y+world.rand.nextDouble(), z+world.rand.nextDouble(), is);
		return true;
	}

	private static void initHandlers() {
		handlers.put(new BlockKey(Blocks.cake, 0), new DirectDrop(Items.cake));
		if (ModList.IC2.isLoaded()) {
			Block b = IC2RubberLogHandler.getInstance().logBlock;
			Item.getItemFromBlock(b).setHasSubtypes(true); //fixes stacking
			handlers.put(new BlockKey(b), new IC2LogDrop());
		}
	}

	private static final class IC2LogDrop extends BlockHandler {

		private IC2LogDrop() {

		}

		@Override
		protected ItemStack getItem(World world, int x, int y, int z, Block b, int meta, EntityPlayer ep) {
			if (IC2RubberLogHandler.getInstance().isCrop(b, meta)) {
				return new ItemStack(b, 1, IC2RubberLogHandler.getInstance().getMeta(ForgeDirection.EAST));
			}
			return null;
		}

	}

	private static final class DirectDrop extends BlockHandler {

		private final ItemStack item;

		private DirectDrop(Item i) {
			this(new ItemStack(i));
		}

		private DirectDrop(ItemStack is) {
			item = is.copy();
		}

		@Override
		protected ItemStack getItem(World world, int x, int y, int z, Block b, int meta, EntityPlayer ep) {
			return item.copy();
		}

	}

	private abstract static class BlockHandler {

		protected abstract ItemStack getItem(World world, int x, int y, int z, Block b, int meta, EntityPlayer ep);

	}

}
