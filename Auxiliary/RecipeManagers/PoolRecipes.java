/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Block.BlockActiveChroma;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.Instantiable.Data.Collections.OneWayCollections.OneWayList;
import Reika.DragonAPI.Instantiable.Data.Maps.ItemHashMap;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

public class PoolRecipes {

	public static final PoolRecipes instance = new PoolRecipes();

	private final ItemHashMap<Collection<PoolRecipe>> recipes = new ItemHashMap().setOneWay();

	private PoolRecipes() {

		this.addRecipe(ChromaStacks.chromaIngot, new ItemStack(Items.iron_ingot), ReikaItemHelper.getSizedItemStack(ChromaStacks.chromaDust, 16));
		this.addRecipe(ChromaStacks.fieryIngot, new ItemStack(Items.gold_ingot), ReikaItemHelper.getSizedItemStack(ChromaStacks.firaxite, 16), new ItemStack(Items.blaze_powder, 8), new ItemStack(Items.coal, 2, 0));
		this.addRecipe(ChromaStacks.enderIngot, new ItemStack(Items.iron_ingot), ReikaItemHelper.getSizedItemStack(ChromaStacks.enderDust, 16), new ItemStack(Items.ender_pearl, 4, 0));
		this.addRecipe(ChromaStacks.waterIngot, new ItemStack(Items.iron_ingot), ReikaItemHelper.getSizedItemStack(ChromaStacks.waterDust, 16), new ItemStack(Items.gold_ingot, 2, 0));
		this.addRecipe(ChromaStacks.conductiveIngot, new ItemStack(Items.gold_ingot), new ItemStack(Items.redstone, 8, 0), ReikaItemHelper.getSizedItemStack(ChromaStacks.beaconDust, 16));
		this.addRecipe(ChromaStacks.auraIngot, new ItemStack(Items.iron_ingot), ReikaItemHelper.getSizedItemStack(ChromaStacks.auraDust, 8), new ItemStack(Items.glowstone_dust, 8, 0), new ItemStack(Items.redstone, 16, 0), new ItemStack(Items.quartz, 4, 0));
		this.addRecipe(ChromaStacks.spaceIngot, new ItemStack(Items.iron_ingot), ReikaItemHelper.getSizedItemStack(ChromaStacks.spaceDust, 16), new ItemStack(Items.glowstone_dust, 32, 0), new ItemStack(Items.redstone, 64, 0), new ItemStack(Items.quartz, 16, 0), new ItemStack(Items.diamond, 4, 0));
		this.addRecipe(ChromaStacks.complexIngot, ChromaStacks.chromaIngot, ChromaStacks.enderIngot, ChromaStacks.waterIngot, ChromaStacks.spaceIngot, ChromaStacks.fieryIngot, ChromaStacks.auraIngot, ChromaStacks.conductiveIngot, ChromaStacks.iridChunk, new ItemStack(Blocks.obsidian, 4, 0), new ItemStack(Items.emerald, 8, 0));

	}

	private void addRecipe(ItemStack out, ItemStack main, ItemStack... ingredients) {
		Collection<PoolRecipe> c = recipes.get(main);
		if (c == null) {
			c = new OneWayList();
			recipes.put(main, c);
		}
		c.add(new PoolRecipe(out, main, ingredients));
	}

	public PoolRecipe getPoolRecipe(EntityItem ei) {
		return this.getPoolRecipe(ei, null, true);
	}

	public PoolRecipe getPoolRecipe(EntityItem ei, Collection<EntityItem> li, boolean block) {
		Collection<PoolRecipe> prs = recipes.get(ei.getEntityItem());
		if (prs != null) {
			int x = MathHelper.floor_double(ei.posX);
			int y = MathHelper.floor_double(ei.posY);
			int z = MathHelper.floor_double(ei.posZ);
			if (!block || (ei.worldObj.getBlock(x, y, z) == ChromaBlocks.CHROMA.getBlockInstance() && ei.worldObj.getBlockMetadata(x, y, z) == 0)) {
				AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(x, y, z);
				if (li == null)
					li = ei.worldObj.getEntitiesWithinAABB(EntityItem.class, box);
				for (PoolRecipe pr : prs) {
					if (pr.canBeMadeFrom(li)) {
						return pr;
					}
				}
			}
		}
		return null;
	}

	public PoolRecipe getPoolRecipeByOutput(ItemStack is) {
		for (PoolRecipe p : this.getAllPoolRecipes()) {
			if (ReikaItemHelper.matchStacks(p.output, is))
				return p;
		}
		return null;
	}

	public void makePoolRecipe(EntityItem ei, PoolRecipe pr, int ether, int x, int y, int z) {
		AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(x, y, z);
		Collection<EntityItem> li = ei.worldObj.getEntitiesWithinAABB(EntityItem.class, box);
		boolean flag = ei.worldObj.getBlock(x, y, z) == ChromaBlocks.CHROMA.getBlockInstance();
		pr.makeFrom(li);
		ReikaEntityHelper.decrEntityItemStack(ei, 1);
		int n = ReikaRandomHelper.doWithChance(BlockActiveChroma.getDoublingChance(ether)) ? 2 : 1;
		EntityItem newitem = ReikaItemHelper.dropItem(ei, ReikaItemHelper.getSizedItemStack(pr.getOutput(), n*pr.getOutput().stackSize));
		newitem.lifespan = Integer.MAX_VALUE;
		if (flag) {
			ei.worldObj.setBlock(x, y, z, Blocks.air);
		}
		ReikaWorldHelper.causeAdjacentUpdates(ei.worldObj, x, y, z);
		ProgressStage.ALLOY.stepPlayerTo(ReikaItemHelper.getDropper(ei));
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

		private void makeFrom(Collection<EntityItem> li) {
			ItemHashMap<Integer> map = inputs.clone();
			for (EntityItem ei : li) {
				ItemStack is = ei.getEntityItem();
				Integer get = map.get(is);
				if (get != null && get > 0) {
					int rem = Math.min(is.stackSize, get);
					get -= rem;
					ReikaEntityHelper.decrEntityItemStack(ei, rem);
					if (get <= 0) {
						map.remove(is);
						if (map.isEmpty())
							return;
					}
				}
			}
		}

		private boolean canBeMadeFrom(Collection<EntityItem> li) {
			ItemHashMap<Integer> map = inputs.clone();
			for (EntityItem ei : li) {
				ItemStack is = ei.getEntityItem();
				Integer get = map.get(is);
				if (get != null && get > 0) {
					int rem = Math.min(is.stackSize, get);
					get -= rem;
					if (get <= 0) {
						map.remove(is);
						if (map.isEmpty())
							return true;
					}
				}
			}
			return false;
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

	public Collection<PoolRecipe> getRecipesForItem(ItemStack is) {
		Collection<PoolRecipe> c = recipes.get(is);
		return c != null ? Collections.unmodifiableCollection(c) : new ArrayList();
	}

	public boolean isCompatibleWith(ItemStack is) {
		Collection<PoolRecipe> c = recipes.get(is);
		for (PoolRecipe r : c) {
			if (r.inputs.containsKey(is))
				return true;
		}
		return false;
	}

	public Collection<PoolRecipe> getAllPoolRecipes() {
		return Collections.unmodifiableCollection(ReikaJavaLibrary.getCompoundCollection(recipes.values()));
	}

	public Collection<ItemStack> getAllOutputItems() {
		Collection<ItemStack> c = new ArrayList();
		for (PoolRecipe pr : this.getAllPoolRecipes()) {
			if (!ReikaItemHelper.collectionContainsItemStack(c, pr.output))
				c.add(pr.output.copy());
		}
		return c;
	}

}
