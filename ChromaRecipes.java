/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.DoorKeyCopyingRecipe;
import Reika.ChromatiCraft.Auxiliary.LegacyTileAcceleratorRecipe;
import Reika.ChromatiCraft.Auxiliary.RangedLampPanelingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.InscriptionRecipes;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.PoolRecipes;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.RecipesCastingTable;
import Reika.ChromatiCraft.Block.Worldgen.BlockDecoFlower.Flowers;
import Reika.ChromatiCraft.Block.Worldgen.BlockSparkle;
import Reika.ChromatiCraft.Items.ItemInfoFragment;
import Reika.ChromatiCraft.ModInterface.ItemColoredModInteract;
import Reika.ChromatiCraft.ModInterface.ModInteraction;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Instantiable.IO.CustomRecipeList;
import Reika.DragonAPI.Instantiable.Recipe.ShapelessNBTRecipe;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.ModInteract.RecipeHandlers.ThermalRecipeHelper;
import Reika.RotaryCraft.API.BlockColorInterface;
import Reika.RotaryCraft.API.RecipeInterface;

import cpw.mods.fml.common.registry.GameRegistry;
import forestry.api.recipes.RecipeManagers;

public class ChromaRecipes {

	public static void addRecipes() {

		for (int i = 0; i < ReikaDyeHelper.dyes.length; i++) {
			ItemStack berry = ChromaItems.BERRY.getStackOfMetadata(i);
			ItemStack dye = ChromaOptions.isVanillaDyeMoreCommon() ? new ItemStack(Items.dye, 2, i) : ChromaItems.DYE.getCraftedMetadataProduct(2, i);
			GameRegistry.addShapelessRecipe(dye, berry);
		}

		ChromaItems.TOOL.addRecipe("  s", " S ", "S  ", 'S', Items.stick, 's', ChromaItems.SHARD.getAnyMetaStack());
		GameRegistry.addRecipe(ChromaTiles.TABLE.getCraftedProduct(), "SCS", "SsS", "sss", 'S', Blocks.stone, 's', new ItemStack(ChromaItems.SHARD.getItemInstance(), 1, OreDictionary.WILDCARD_VALUE), 'C', Blocks.crafting_table);
		ChromaItems.HELP.addRecipe("abc", "gBg", "def", 'B', Items.book, 'a', getShard(CrystalElement.BLACK), 'b', getShard(CrystalElement.BLUE), 'c', getShard(CrystalElement.GREEN), 'd', getShard(CrystalElement.YELLOW), 'e', getShard(CrystalElement.RED), 'f', getShard(CrystalElement.WHITE), 'g', Items.glowstone_dust);

		for (ChromaResearch r : ChromaResearch.getAllNonParents()) {
			ItemStack output = ItemInfoFragment.getItem(r);
			GameRegistry.addRecipe(new ShapelessNBTRecipe(ReikaItemHelper.getSizedItemStack(output, 2), output, Items.paper/*, ReikaItemHelper.inksac*/));
		}

		//GameRegistry.addRecipe(ChromaTiles.BREWER.getCraftedProduct(), "NNN", "NBN", "SSS", 'N', Items.quartz, 'S', Blocks.stone, 'B', Items.brewing_stand);
		//GameRegistry.addRecipe(ChromaItems.ENDERCRYSTAL.getStackOf(), "ISI", "SCS", "ISI", 'I', Items.iron_ingot, 'S', getShard(ReikaDyeHelper.WHITE), 'C', ChromaItems.CLUSTER.getStackOfMetadata(11));

		if (ModList.THERMALEXPANSION.isLoaded()) {
			FluidStack crystal = FluidRegistry.getFluidStack("potion crystal", 500);
			int energy = 40000;
			for (int i = 0; i < 16; i++) {
				ItemStack shard = ChromaItems.SHARD.getStackOfMetadata(i);
				ThermalRecipeHelper.addCrucibleRecipe(shard, crystal, energy);
				ThermalRecipeHelper.addPulverizerRecipe(shard, ChromaStacks.crystalPowder, 1000);
			}
		}

		GameRegistry.addRecipe(new LegacyTileAcceleratorRecipe());
		GameRegistry.addRecipe(new DoorKeyCopyingRecipe());
		GameRegistry.addRecipe(new RangedLampPanelingRecipe());

		GameRegistry.addShapelessRecipe(ChromaItems.ENDERBUCKET.getStackOf(), ChromaItems.ENDERBUCKET.getAnyMetaStack());

		loadSmelting();
	}

	private static void loadSmelting() {
		int[] metas = {1, 2, 6, 7, 8, 10, 11, 12};
		ItemStack basic = ChromaBlocks.PYLONSTRUCT.getStackOfMetadata(0);
		for (int i = 0; i < metas.length; i++) {
			int meta = metas[i];
			//int n = CrystalStoneRecipe.getRecipeForMeta(meta).getOutput().stackSize;
			ItemStack is = ChromaBlocks.PYLONSTRUCT.getStackOfMetadata(meta);//new ItemStack(ChromaBlocks.PYLONSTRUCT.getBlockInstance(), n, meta);
			ReikaRecipeHelper.addSmelting(is, basic, 0);
		}

		ReikaRecipeHelper.addSmelting(BlockSparkle.BlockTypes.SAND.getItem(), BlockSparkle.BlockTypes.GLASS.getItem(), 0.2F);
	}

	private static ItemStack getShard(CrystalElement color) {
		return ChromaItems.SHARD.getStackOfMetadata(color.ordinal());
	}

	public static void addPostLoadRecipes() {

		RecipesCastingTable.instance.addPostLoadRecipes();

		CustomRecipeList.addFieldLookup("chromaticraft_stack", ChromaStacks.class);
		RecipesCastingTable.instance.loadCustomRecipeFiles();
		PoolRecipes.instance.loadCustomPoolRecipes();
		InscriptionRecipes.instance.loadCustomInscriptionRecipes();

		if (ModList.ROTARYCRAFT.isLoaded()) {
			for (int i = 0; i < CrystalElement.elements.length; i++) {
				CrystalElement color = CrystalElement.elements[i];
				ItemStack shard = ChromaItems.SHARD.getStackOfMetadata(i);
				BlockColorInterface.addGPRBlockColor(ChromaBlocks.CRYSTAL.getBlockInstance(), i, color.getColor());
				BlockColorInterface.addGPRBlockColor(ChromaBlocks.LAMP.getBlockInstance(), i, color.getColor());
				BlockColorInterface.addGPRBlockColor(ChromaBlocks.SUPER.getBlockInstance(), i, color.getColor());
				RecipeInterface.grinder.addAPIRecipe(new ItemStack(ChromaBlocks.CRYSTAL.getBlockInstance(), 1, i), ReikaItemHelper.getSizedItemStack(shard, 18));
				RecipeInterface.grinder.addAPIRecipe(new ItemStack(ChromaBlocks.LAMP.getBlockInstance(), 1, i), ReikaItemHelper.getSizedItemStack(shard, 4));
				RecipeInterface.grinder.addAPIRecipe(shard, ChromaStacks.crystalPowder);
			}

			for (int i = 0; i < Flowers.list.length; i++) {
				ItemStack is = Flowers.list[i].getDrop();
				RecipeInterface.grinder.addAPIRecipe(ChromaBlocks.DECOFLOWER.getStackOfMetadata(i), ReikaItemHelper.getSizedItemStack(is, 3));
			}
		}

		if (ModList.THAUMCRAFT.isLoaded()) {
			ModInteraction.addThaumRecipes();
		}

		if (ModList.FORESTRY.isLoaded()) {
			for (int i = 0; i < CrystalElement.elements.length; i++) {
				CrystalElement color = CrystalElement.elements[i];
				ItemStack shard = ChromaItems.SHARD.getStackOfMetadata(i);
				ItemStack comb = ItemColoredModInteract.ColoredModItems.COMB.getItem(color);
				OreDictionary.registerOre("beeComb", comb);
				if (ModList.ROTARYCRAFT.isLoaded()) {
					RecipeInterface.centrifuge.addAPIRecipe(comb, null, 0, shard, 2.5F);
				}
				Map<ItemStack, Float> map = new HashMap();
				map.put(shard, 0.005F);
				RecipeManagers.centrifugeManager.addRecipe(20, comb, map);
			}
		}
	}

	public static void loadDictionary() {
		for (int i = 0; i < ReikaDyeHelper.dyes.length; i++) {
			ReikaDyeHelper color = ReikaDyeHelper.dyes[i];
			ItemStack crystal = new ItemStack(ChromaBlocks.CRYSTAL.getBlockInstance(), 1, i);
			ItemStack shard = new ItemStack(ChromaItems.SHARD.getItemInstance(), 1, i);
			OreDictionary.registerOre(color.getOreDictName()+"Crystal", crystal);
			OreDictionary.registerOre(color.getOreDictName()+"CrystalShard", shard);
			OreDictionary.registerOre("dyeCrystal", crystal);
			OreDictionary.registerOre("caveCrystal", crystal);
			OreDictionary.registerOre("shardCrystal", shard);
		}
	}

}
