package Reika.ChromatiCraft.Items;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.Instantiable.Data.Collections.OneWayCollections.OneWayList;
import Reika.DragonAPI.Instantiable.Data.Maps.ItemHashMap;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class PoolRecipes {

	public static final PoolRecipes instance = new PoolRecipes();

	private final Collection<PoolRecipe> recipes = new OneWayList();

	private PoolRecipes() {

		recipes.add(new PoolRecipe(ChromaStacks.magicIngot, new ItemStack(Items.iron_ingot), ReikaItemHelper.getSizedItemStack(ChromaStacks.chromaDust, 16)));
		recipes.add(new PoolRecipe(ChromaStacks.magicIngot2, new ItemStack(Items.gold_ingot), ReikaItemHelper.getSizedItemStack(ChromaStacks.firaxite, 16), new ItemStack(Items.blaze_powder, 8)));
		recipes.add(new PoolRecipe(ChromaStacks.magicIngot3, new ItemStack(Items.iron_ingot), ReikaItemHelper.getSizedItemStack(ChromaStacks.enderDust, 16), new ItemStack(Items.ender_pearl, 4, 0)));
		recipes.add(new PoolRecipe(ChromaStacks.magicIngot4, new ItemStack(Items.iron_ingot), ReikaItemHelper.getSizedItemStack(ChromaStacks.waterDust, 16)));
		recipes.add(new PoolRecipe(ChromaStacks.magicIngot5, new ItemStack(Items.gold_ingot), new ItemStack(Items.redstone, 8, 0), ReikaItemHelper.getSizedItemStack(ChromaStacks.beaconDust, 16)));

	}

	public ItemStack getPoolRecipe(EntityItem ei) {
		int x = MathHelper.floor_double(ei.posX);
		int y = MathHelper.floor_double(ei.posY);
		int z = MathHelper.floor_double(ei.posZ);
		if (ei.worldObj.getBlock(x, y, z) == ChromaBlocks.CHROMA.getBlockInstance()) {
			AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(x, y, z);
			Collection<EntityItem> li = ei.worldObj.getEntitiesWithinAABB(EntityItem.class, box);
			Collection<ItemStack> items = new ArrayList();
			for (EntityItem ei2 : li) {
				items.add(ei2.getEntityItem());
			}
			for (PoolRecipe pr : recipes) {
				if (pr.canBeMadeFrom(items)) {
					return pr.getOutput();
				}
			}
		}
		return null;
	}

	public static class PoolRecipe {

		private final ItemStack main;
		private final ItemHashMap<Integer> inputs = new ItemHashMap().setOneWay();
		private final ItemStack output;

		private PoolRecipe(ItemStack out, ItemStack m, ItemStack... input) {
			output = out.copy();
			main = m;

			for (int i = 0; i < input.length; i++) {
				inputs.put(input[i], input[i].stackSize);
			}
		}

		private boolean canBeMadeFrom(Collection<ItemStack> li) {
			if (!ReikaItemHelper.collectionContainsItemStack(li, main))
				return false;
			for (ItemStack is : inputs.keySet()) {
				boolean has = false;
				int req = inputs.get(is);
				for (ItemStack in : li) {
					if (ReikaItemHelper.matchStacks(in, is) && in.stackSize >= req) {
						has = true;
						break;
					}
				}
				if (!has)
					return false;
			}
			return true;
		}

		public ItemStack getMainInput() {
			return main.copy();
		}

		public Collection<ItemStack> getInputs() {
			Collection<ItemStack> c = new ArrayList();
			for (ItemStack is : inputs.keySet()) {
				c.add(ReikaItemHelper.getSizedItemStack(is, inputs.get(is)));
			}
			return c;
		}

		public ItemStack getOutput() {
			return output.copy();
		}

	}

	public Collection<PoolRecipe> getAllPoolRecipes() {
		return Collections.unmodifiableCollection(recipes);
	}

	public Collection<ItemStack> getAllOutputItems() {
		Collection<ItemStack> c = new ArrayList();
		for (PoolRecipe pr : recipes) {
			c.add(pr.output.copy());
		}
		return c;
	}

}
