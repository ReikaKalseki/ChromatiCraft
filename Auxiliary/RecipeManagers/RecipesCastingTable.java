/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.AcceleratorRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.CastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.CastingRecipe.RecipeType;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.CrystalSeedRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.CrystalStarRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.GuardianStoneRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.RuneRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.StorageCrystalRecipe;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.TileEntityAccelerator;
import Reika.ChromatiCraft.TileEntity.TileEntityCastingTable;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;

public class RecipesCastingTable {

	public static final RecipesCastingTable instance = new RecipesCastingTable();
	private final HashMap<RecipeType, ArrayList<CastingRecipe>> recipes = new HashMap();

	private RecipesCastingTable() {
		this.addRecipe(new CrystalStarRecipe(ChromaStacks.crystalStar, new ItemStack(Items.nether_star)));
		this.addRecipe(new StorageCrystalRecipe(ChromaItems.STORAGE.getStackOf(), ChromaStacks.rawCrystal));
		for (int i = 0; i < 16; i++) {
			this.addRecipe(new RuneRecipe(new ItemStack(ChromaBlocks.RUNE.getBlockInstance(), 1, i), i));
			ItemStack shard = ChromaItems.SHARD.getStackOfMetadata(i);
			ItemStack seed = ChromaItems.SEED.getStackOfMetadata(i);
			ItemStack block = new ItemStack(ChromaBlocks.PYLONSTRUCT.getBlockInstance(), 1, 0);
			ShapedOreRecipe sr = new ShapedOreRecipe(block, " S ", "SCS", " S ", 'S', "stone", 'C', shard);
			this.addRecipe(new CastingRecipe(block, sr));

			this.addRecipe(new CrystalSeedRecipe(seed, CrystalElement.elements[i]));
		}
		Block block = ChromaBlocks.PYLONSTRUCT.getBlockInstance();
		ShapedRecipes sr = ReikaRecipeHelper.getShapedRecipeFor(new ItemStack(block, 2, 1), "S", "S", 'S', new ItemStack(block, 1, 0));
		this.addRecipe(new CastingRecipe(new ItemStack(block, 2, 1), sr));

		sr = ReikaRecipeHelper.getShapedRecipeFor(new ItemStack(block, 2, 2), "SS", 'S', new ItemStack(block, 1, 0));
		this.addRecipe(new CastingRecipe(new ItemStack(block, 2, 2), sr));

		sr = ReikaRecipeHelper.getShapedRecipeFor(new ItemStack(block, 4, 7), " S ", "S S", " S ", 'S', new ItemStack(block, 1, 0));
		this.addRecipe(new CastingRecipe(new ItemStack(block, 4, 7), sr));

		sr = ReikaRecipeHelper.getShapedRecipeFor(new ItemStack(block, 5, 8), " S ", "SSS", " S ", 'S', new ItemStack(block, 1, 0));
		this.addRecipe(new CastingRecipe(new ItemStack(block, 5, 8), sr));

		sr = ReikaRecipeHelper.getShapedRecipeFor(new ItemStack(block, 5, 6), "SSS", "S  ", "S  ", 'S', new ItemStack(block, 1, 0));
		this.addRecipe(new CastingRecipe(new ItemStack(block, 5, 6), sr));

		this.addRecipe(new GuardianStoneRecipe(ChromaTiles.GUARDIAN.getCraftedProduct(), ChromaStacks.crystalStar));

		for (int i = 0; i <= TileEntityAccelerator.MAX_TIER; i++)
			this.addRecipe(new AcceleratorRecipe(i));
	}

	private void addRecipe(CastingRecipe r) {
		ArrayList<CastingRecipe> li = recipes.get(r.type);
		if (li == null) {
			li = new ArrayList();
			recipes.put(r.type, li);
		}
		li.add(r);
	}

	public CastingRecipe getRecipe(TileEntityCastingTable table, ArrayList<RecipeType> type) {
		ItemStack main = table.getStackInSlot(0);
		ArrayList<CastingRecipe> li = new ArrayList();
		for (int i = 0; i < type.size(); i++) {
			ArrayList<CastingRecipe> list = recipes.get(type.get(i));
			if (list != null)
				li.addAll(list);
		}
		for (int i = 0; i < li.size(); i++) {
			CastingRecipe r = li.get(i);
			if (r.match(table))
				return r;
		}
		return null;
	}

}
