/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.InfusionRecipe;
import Reika.ChromatiCraft.Auxiliary.ChromaDescriptions;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Items.ItemInfoFragment;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Instantiable.Recipe.ShapelessNBTRecipe;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.ReikaThaumHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.ThaumItemHelper;
import Reika.DragonAPI.ModInteract.RecipeHandlers.ThermalRecipeHelper;
import Reika.RotaryCraft.API.BlockColorInterface;
import Reika.RotaryCraft.API.GrinderAPI;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

public class ChromaRecipes {

	public static void addRecipes() {

		for (int i = 0; i < ReikaDyeHelper.dyes.length; i++) {
			ItemStack berry = ChromaItems.BERRY.getStackOfMetadata(i);
			GameRegistry.addShapelessRecipe(new ItemStack(Items.dye, 2, i), berry);
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
	}

	private static ItemStack getShard(CrystalElement color) {
		return ChromaItems.SHARD.getStackOfMetadata(color.ordinal());
	}

	public static void addPostLoadRecipes() {
		if (ModList.ROTARYCRAFT.isLoaded()) {
			for (int i = 0; i < CrystalElement.elements.length; i++) {
				CrystalElement color = CrystalElement.elements[i];
				BlockColorInterface.addGPRBlockColor(ChromaBlocks.CRYSTAL.getBlockInstance(), i, color.getColor());
				BlockColorInterface.addGPRBlockColor(ChromaBlocks.LAMP.getBlockInstance(), i, color.getColor());
				BlockColorInterface.addGPRBlockColor(ChromaBlocks.SUPER.getBlockInstance(), i, color.getColor());
				ItemStack shard = ChromaItems.SHARD.getStackOfMetadata(i);
				GrinderAPI.addRecipe(new ItemStack(ChromaBlocks.CRYSTAL.getBlockInstance(), 1, i), ReikaItemHelper.getSizedItemStack(shard, 12));
				GrinderAPI.addRecipe(new ItemStack(ChromaBlocks.LAMP.getBlockInstance(), 1, i), ReikaItemHelper.getSizedItemStack(shard, 4));
				GrinderAPI.addRecipe(shard, ChromaStacks.crystalPowder);
			}
		}

		if (ModList.THAUMCRAFT.isLoaded()) {
			ReikaThaumHelper.addBookCategory(new ResourceLocation("chromaticraft", "textures/blocks/tile/table_top.png"), "chromaticraft");
			ItemStack in = ChromaItems.WARP.getStackOfMetadata(0);
			ItemStack out = ChromaItems.WARP.getStackOfMetadata(1);
			String desc = "Pitting one kind of magic against another";
			AspectList al = new AspectList();
			al.add(Aspect.ELDRITCH, 50);
			al.add(Aspect.TAINT, 50);
			al.add(Aspect.MAGIC, 50);
			al.add(Aspect.EXCHANGE, 50);
			al.add(Aspect.CRYSTAL, 20);
			al.add(Aspect.HEAL, 100);
			al.add(Aspect.SENSES, 20);
			ItemStack[] recipe = {
					ThaumItemHelper.ItemEntry.GOO.getItem(),
					ThaumItemHelper.ItemEntry.FABRIC.getItem(),
					ThaumItemHelper.ItemEntry.SALTS.getItem(),
					new ItemStack(Items.string),
					ThaumItemHelper.ItemEntry.PRIMALFOCUS.getItem(),
					ThaumItemHelper.ItemEntry.FABRIC.getItem(),
					ThaumItemHelper.ItemEntry.SALTS.getItem(),
					new ItemStack(Items.string),
					ThaumItemHelper.BlockEntry.CRYSTALCORE.getItem(),
			};
			InfusionRecipe ir = ThaumcraftApi.addInfusionCraftingRecipe("ELDRITCHMINOR", out, 16, al, in, recipe);
			if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
				ReikaThaumHelper.addInfusionRecipeBookEntryViaXML("WARPPROOF", desc, "chromaticraft", ir, ChromatiCraft.class, ChromaDescriptions.getParentPage()+"thaum.xml");
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
