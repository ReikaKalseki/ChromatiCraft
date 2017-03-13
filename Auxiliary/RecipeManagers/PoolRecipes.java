/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Base.ItemChromaBasic;
import Reika.ChromatiCraft.Block.BlockActiveChroma;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.ItemElementCalculator;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.DragonAPI.Instantiable.Data.Collections.OneWayCollections.OneWayList;
import Reika.DragonAPI.Instantiable.Data.Maps.ItemHashMap;
import Reika.DragonAPI.Instantiable.IO.CustomRecipeList;
import Reika.DragonAPI.Instantiable.IO.LuaBlock;
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

		PoolRecipe pr = this.addRecipe(ChromaStacks.complexIngot, ChromaStacks.chromaIngot, ChromaStacks.enderIngot, ChromaStacks.waterIngot, ChromaStacks.spaceIngot, ChromaStacks.fieryIngot, ChromaStacks.auraIngot, ChromaStacks.conductiveIngot, ChromaStacks.iridChunk, new ItemStack(Blocks.obsidian, 4, 0), new ItemStack(Items.emerald, 8, 0));
		//pr.allowDoubling = false;

		this.addRecipe(ChromaItems.DATACRYSTAL.getCraftedProduct(2), ChromaItems.DATACRYSTAL.getStackOf(), ReikaItemHelper.getSizedItemStack(ChromaStacks.crystalPowder, 18)).addProgress(ProgressStage.TOWER);
	}

	private PoolRecipe addRecipe(ItemStack out, ItemStack main, ItemStack... ingredients) {
		Collection<PoolRecipe> c = recipes.get(main);
		if (c == null) {
			c = new OneWayList();
			recipes.put(main, c);
		}
		PoolRecipe r = new PoolRecipe(out, main, ingredients);
		c.add(r);
		return r;
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
		int n = pr.allowDoubling() && ReikaRandomHelper.doWithChance(BlockActiveChroma.getDoublingChance(ether)) ? 2 : 1;
		EntityItem newitem = ReikaItemHelper.dropItem(ei, ReikaItemHelper.getSizedItemStack(pr.getOutput(), n*pr.getOutput().stackSize));
		newitem.lifespan = Integer.MAX_VALUE;
		if (flag) {
			ei.worldObj.setBlock(x, y, z, Blocks.air);
		}
		ReikaWorldHelper.causeAdjacentUpdates(ei.worldObj, x, y, z);
		ProgressStage.ALLOY.stepPlayerTo(ReikaItemHelper.getDropper(ei));
	}

	public void loadCustomPoolRecipes() {
		CustomRecipeList crl = new CustomRecipeList(ChromatiCraft.instance, "alloying");
		crl.addFieldLookup("chromaticraft_stack", ChromaStacks.class);
		crl.load();
		for (LuaBlock lb : crl.getEntries()) {
			Exception e = null;
			boolean flag = false;
			try {
				flag = this.addCustomRecipe(lb, crl);
			}
			catch (Exception ex) {
				e = ex;
				flag = false;
			}
			if (flag) {
				ChromatiCraft.logger.log("Loaded custom alloying recipe '"+lb.getString("type")+"'");
			}
			else {
				ChromatiCraft.logger.logError("Could not load custom alloying recipe '"+lb.getString("type")+"'");
				if (e != null)
					e.printStackTrace();
			}
		}
	}

	protected final void verifyOutputItem(ItemStack is) {
		if (is.getItem() instanceof ItemChromaBasic || is.getItem().getClass().getName().startsWith("Reika.ChromatiCraft"))
			throw new IllegalArgumentException("This item is not allowed as an output, as it is a native ChromatiCraft item with its own recipe.");
	}

	private boolean addCustomRecipe(LuaBlock lb, CustomRecipeList crl) throws Exception {
		ItemStack out = crl.parseItemString(lb.getString("output"), lb.getChild("output_nbt"), false);
		this.verifyOutputItem(out);
		ItemStack main = crl.parseItemString(lb.getString("catalyst"), null, false);
		LuaBlock items = lb.getChild("items");
		ArrayList<ItemStack> li = new ArrayList();
		for (String s : items.getDataValues()) {
			ItemStack is = crl.parseItemString(s, null, false);
			li.add(is);
		}
		PoolRecipe r = this.addCustomRecipe(out, main, li.toArray(new ItemStack[li.size()]));
		r.allowDoubling = lb.containsKey("allow_doubling") ? lb.getBoolean("allow_doubling") : true;
		return true;
	}

	public PoolRecipe addCustomRecipe(ItemStack out, ItemStack main, ItemStack... ingredients) {
		PoolRecipe r = this.addRecipe(out, main, ingredients);
		r.isCustom = true;
		return r;
	}

	public static class PoolRecipe {

		private final ItemStack main;
		private final ItemHashMap<Integer> inputs = new ItemHashMap().setOneWay();
		private final ItemStack output;

		private final HashSet<ProgressStage> progress = new HashSet();

		private boolean allowDoubling = true;
		private boolean isCustom = false;

		private PoolRecipe(ItemStack out, ItemStack m, ItemStack... input) {
			output = out.copy();
			main = m;

			for (int i = 0; i < input.length; i++) {
				inputs.put(input[i], input[i].stackSize);
			}
		}

		private PoolRecipe addProgress(ProgressStage p) {
			progress.add(p);
			return this;
		}

		public boolean playerHasProgress(EntityPlayer ep) {
			for (ProgressStage p : progress) {
				if (!p.isPlayerAtStage(ep))
					return false;
			}
			return true;
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

		public ElementTagCompound getInputElements() {
			ElementTagCompound tag = ItemElementCalculator.instance.getValueForItem(main);
			for (ItemStack is : inputs.keySet())
				tag.addButMinimizeWith(ItemElementCalculator.instance.getValueForItem(is));
			return tag;
		}

		public boolean isCustom() {
			return isCustom;
		}

		public boolean allowDoubling() {
			return allowDoubling;
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

	public Collection<PoolRecipe> getAllPoolRecipesForPlayer(EntityPlayer ep) {
		Collection<PoolRecipe> c = new ArrayList();
		for (PoolRecipe p : this.getAllPoolRecipes()) {
			if (p.playerHasProgress(ep))
				c.add(p);
		}
		return c;
	}

	public Collection<ItemStack> getAllOutputItems() {
		Collection<ItemStack> c = new ArrayList();
		for (PoolRecipe pr : this.getAllPoolRecipes()) {
			if (!ReikaItemHelper.collectionContainsItemStack(c, pr.output))
				c.add(pr.output.copy());
		}
		return c;
	}

	public boolean canAlloyItem(EntityItem ei) {
		EntityPlayer ep = ReikaItemHelper.getDropper(ei);
		if (ep != null) {
			if (ProgressStage.ALLOY.playerHasPrerequisites(ep)) {
				return true;
			}
		}
		return false;
	}

}
