/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Registry;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.crafting.ShapelessArcaneRecipe;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.RecipesCastingTable;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.ModInterface.ChromaAspectManager;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Instantiable.Data.KeyedItemStack;
import Reika.DragonAPI.Instantiable.Data.Maps.ItemHashMap;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.RotaryCraft.Auxiliary.RecipeManagers.WorktableRecipes;
import Reika.RotaryCraft.Auxiliary.RecipeManagers.WorktableRecipes.WorktableRecipe;
import cpw.mods.fml.common.registry.GameRegistry;

public class ItemElementCalculator {

	public static final ItemElementCalculator instance = new ItemElementCalculator();
	private final ItemHashMap<ElementTagCompound> cache = new ItemHashMap();

	private final ElementTagCompound empty = new ElementTagCompound();

	private List<KeyedItemStack> currentCalculation = new ArrayList();

	private ItemElementCalculator() {
		for (int i = 0; i < CrystalElement.elements.length; i++) {
			CrystalElement e = CrystalElement.elements[i];
			ElementTagCompound tag = new ElementTagCompound();
			ElementTagCompound tag1 = new ElementTagCompound();
			tag1.addValueToColor(e, 1);
			tag.addValueToColor(e, 2);
			cache.put(ChromaItems.ELEMENTAL.getStackOf(e), tag);
			cache.put(ChromaBlocks.RUNE.getStackOfMetadata(i), tag);
			cache.put(ChromaBlocks.DYELEAF.getStackOfMetadata(i), tag1);
			cache.put(ChromaBlocks.GLOW.getStackOfMetadata(i), tag1);
			cache.put(ChromaItems.DYE.getStackOfMetadata(i), tag1);
			cache.put(new ItemStack(Items.dye, 1, i), tag1);

			ElementTagCompound tag2 = tag1.copy();
			tag2.addValueToColor(CrystalElement.GREEN, 1);
			cache.put(new ItemStack(Blocks.wool, 1, i), tag2);
			Block rockwool = GameRegistry.findBlock(ModList.THERMALEXPANSION.modLabel, "Rockwool");
			if (rockwool != null) {
				cache.put(new ItemStack(rockwool, 1, i), tag2);
			}
		}

		ElementTagCompound tag = new ElementTagCompound();
		tag.addTag(CrystalElement.BLACK, 1);
		cache.put(ChromaStacks.auraDust, tag);
		cache.put(ChromaStacks.beaconDust, tag);
		cache.put(ChromaStacks.bindingCrystal, tag);
		cache.put(ChromaStacks.focusDust, tag);
	}

	public ElementTagCompound getValueForItem(ItemStack is) {
		if (is == null)
			return empty.copy();
		if (!currentCalculation.isEmpty() && currentCalculation.contains(new KeyedItemStack(is).setIgnoreNBT(true).setSimpleHash(true))) {
			ChromatiCraft.logger.debug("Recipe contains its own output, possibly recursively.");
			return empty.copy();
		}
		//ChromatiCraft.logger.debug("Fetching element calculation data for "+is);
		ElementTagCompound tag = cache.get(is);
		if (tag == null) {
			tag = this.calculateTag(is);
			cache.put(is, tag);
		}
		tag = tag.copy();
		if (is.isItemEnchanted()) {
			tag.addValueToColor(CrystalElement.BLACK, 5);
			tag.addValueToColor(CrystalElement.PURPLE, 2);
		}
		return tag;
	}

	private ElementTagCompound getFromVanillaCrafting(ItemStack is) {
		ArrayList<IRecipe> li = ReikaRecipeHelper.getAllRecipesByOutput(CraftingManager.getInstance().getRecipeList(), is);
		ElementTagCompound tag = new ElementTagCompound();
		for (IRecipe ir : li) {
			tag.addButMinimizeWith(this.getIRecipeTotal(ir));
		}
		return tag;
	}

	private ElementTagCompound getFromVanillaSmelting(ItemStack is) {
		ElementTagCompound tag = new ElementTagCompound();
		for (Object o : FurnaceRecipes.smelting().getSmeltingList().keySet()) {
			ItemStack in = (ItemStack)o;
			ItemStack out = FurnaceRecipes.smelting().getSmeltingResult(in);
			if (ReikaItemHelper.matchStacks(is, out)) {
				ElementTagCompound tag2 = this.getValueForItem(in);
				tag2.addValueToColor(CrystalElement.ORANGE, 1);
				tag.addButMinimizeWith(tag2);
			}
		}
		return tag;
	}

	private ElementTagCompound getFromChromaCasting(ItemStack is) {
		ElementTagCompound tag = new ElementTagCompound();
		ArrayList<CastingRecipe> li = RecipesCastingTable.instance.getAllRecipesMaking(is);
		for (CastingRecipe c : li) {
			ElementTagCompound tag2 = c.getInputElements();
			tag2.addValueToColor(CrystalElement.BLACK, 2);
			tag.addButMinimizeWith(tag2);
		}
		return tag;
	}

	@ModDependent(ModList.ROTARYCRAFT)
	private ElementTagCompound getFromRCWorktable(ItemStack is) {
		ElementTagCompound tag = new ElementTagCompound();
		List<WorktableRecipe> li = WorktableRecipes.getInstance().getRecipeListCopy();
		for (WorktableRecipe wr : li) {
			if (ReikaItemHelper.matchStacks(is, wr.getOutput())) {
				tag.addButMinimizeWith(wr.getElements());
			}
		}
		return tag;
	}

	@ModDependent(ModList.THERMALEXPANSION)
	private ElementTagCompound getFromTE3(ItemStack is) {
		//TODO
		return null;
	}

	@ModDependent(ModList.THAUMCRAFT)
	private ElementTagCompound getFromThaumCraft(ItemStack is) {
		ElementTagCompound tag = new ElementTagCompound();
		List li = ThaumcraftApi.getCraftingRecipes();
		for (Object o : li) {
			if (o instanceof ShapedArcaneRecipe) {
				ShapedArcaneRecipe sr = (ShapedArcaneRecipe)o;
				if (ReikaItemHelper.matchStacks(sr.getRecipeOutput(), is)) {
					Object[] in = sr.getInput();
					for (int i = 0; i < in.length; i++) {
						Object o2 = in[i];
						tag.addButMinimizeWith(this.getTagForItemOrList(o2));
					}
					tag.addValueToColor(CrystalElement.BLACK, 1);
					for (Aspect a : sr.aspects.aspects.keySet()) {
						ElementTagCompound asp = ChromaAspectManager.instance.getElementCost(a, 0);
						asp.setAllValuesTo(1);
						tag.addTag(asp);
					}
				}
			}
			else if (o instanceof ShapelessArcaneRecipe) {
				ShapelessArcaneRecipe sr = (ShapelessArcaneRecipe)o;
				if (ReikaItemHelper.matchStacks(sr.getRecipeOutput(), is)) {
					ArrayList<Object> in = sr.getInput();
					for (Object o2 : in) {
						tag.addButMinimizeWith(this.getTagForItemOrList(o2));
					}
					tag.addValueToColor(CrystalElement.BLACK, 1);
					for (Aspect a : sr.aspects.aspects.keySet()) {
						ElementTagCompound asp = ChromaAspectManager.instance.getElementCost(a, 0);
						asp.setAllValuesTo(1);
						tag.addTag(asp);
					}
				}
			}
			else if (o instanceof CrucibleRecipe) {
				CrucibleRecipe cr = (CrucibleRecipe)o;
				if (ReikaItemHelper.matchStacks(cr.getRecipeOutput(), is)) {
					tag.addButMinimizeWith(this.getTagForItemOrList(cr.catalyst));
					tag.addValueToColor(CrystalElement.BLACK, 1);
					for (Aspect a : cr.aspects.aspects.keySet()) {
						ElementTagCompound asp = ChromaAspectManager.instance.getElementCost(a, 0);
						asp.setAllValuesTo(1);
						tag.addTag(asp);
					}
				}
			}
			else if (o instanceof InfusionRecipe) {
				InfusionRecipe ir = (InfusionRecipe)o;
				if (ir.getRecipeOutput() instanceof ItemStack && ReikaItemHelper.matchStacks((ItemStack)ir.getRecipeOutput(), is)) {
					tag.addButMinimizeWith(this.getValueForItem(ir.getRecipeInput()));
					ItemStack[] parts = ir.getComponents();
					for (int i = 0; i < parts.length; i++) {
						ItemStack in = parts[i];
						tag.addButMinimizeWith(this.getValueForItem(in));
					}
					tag.addValueToColor(CrystalElement.BLACK, 2);
					for (Aspect a : ir.getAspects().aspects.keySet()) {
						ElementTagCompound asp = ChromaAspectManager.instance.getElementCost(a, 0);
						asp.setAllValuesTo(1);
						tag.addTag(asp);
					}
				}
			}
		}
		return tag;
	}

	@ModDependent(ModList.BCSILICON)
	private ElementTagCompound getFromBCLasers(ItemStack is) {
		//TODO
		return null;
	}

	@ModDependent(ModList.TINKERER)
	private ElementTagCompound getFromTinkerTable(ItemStack is) {
		//TODO
		return null;
	}

	//In case of multiple recipes, need to take cheapest tag of each color possible, as risk exploit otherwise
	//check : Crafting, Smelting, TE3 machines, BC laser table, ChromatiCraft manufacture, TiC, Thaumcraft
	private ElementTagCompound calculateTag(ItemStack is) {
		ElementTagCompound tag = ItemMagicRegistry.instance.getItemValue(is);
		if (tag != null)
			return tag; //Basic registry overrides anything else

		tag = new ElementTagCompound();
		currentCalculation.add(new KeyedItemStack(is).setIgnoreNBT(true).setSimpleHash(true));

		tag.addButMinimizeWith(this.getFromVanillaCrafting(is));
		tag.addButMinimizeWith(this.getFromVanillaSmelting(is));
		tag.addButMinimizeWith(this.getFromChromaCasting(is));
		if (ModList.ROTARYCRAFT.isLoaded())
			tag.addButMinimizeWith(this.getFromRCWorktable(is));
		if (ModList.THERMALEXPANSION.isLoaded())
			tag.addButMinimizeWith(this.getFromTE3(is));
		if (ModList.THAUMCRAFT.isLoaded())
			tag.addButMinimizeWith(this.getFromThaumCraft(is));
		if (ModList.BCSILICON.isLoaded())
			tag.addButMinimizeWith(this.getFromBCLasers(is));
		if (ModList.TINKERER.isLoaded())
			tag.addButMinimizeWith(this.getFromTinkerTable(is));
		//ChromatiCraft.logger.debug("Calculated for "+is+" ("+is.getDisplayName()+"): "+tag);
		currentCalculation.remove(new KeyedItemStack(is).setIgnoreNBT(true).setSimpleHash(true));
		return tag;
	}

	public ElementTagCompound getIRecipeTotal(IRecipe ir) {
		ElementTagCompound tag = new ElementTagCompound();
		if (ir instanceof ShapedRecipes) {
			ShapedRecipes sr = (ShapedRecipes)ir;
			for (int k = 0; k < sr.recipeItems.length; k++) {
				ItemStack in = sr.recipeItems[k];
				ElementTagCompound value = this.getValueForItem(in);
				tag.addButMinimizeWith(value);
			}
		}
		else if (ir instanceof ShapelessRecipes) {
			ShapelessRecipes sr = (ShapelessRecipes)ir;
			for (int k = 0; k < sr.recipeItems.size(); k++) {
				ItemStack in = (ItemStack)sr.recipeItems.get(k);
				ElementTagCompound value = this.getValueForItem(in);
				tag.addButMinimizeWith(value);
			}
		}
		else if (ir instanceof ShapedOreRecipe) {
			ShapedOreRecipe sr = (ShapedOreRecipe)ir;
			for (int k = 0; k < sr.getInput().length; k++) {
				Object in = sr.getInput()[k];
				ElementTagCompound value = this.getTagForItemOrList(in);
				tag.addButMinimizeWith(value);
			}
		}
		else if (ir instanceof ShapelessOreRecipe) {
			ShapelessOreRecipe sr = (ShapelessOreRecipe)ir;
			for (int k = 0; k < sr.getInput().size(); k++) {
				Object in = sr.getInput().get(k);
				ElementTagCompound value = this.getTagForItemOrList(in);
				tag.addButMinimizeWith(value);
			}
		}
		return tag;
	}

	private ElementTagCompound getTagForItemOrList(Object in) {
		if (in instanceof ItemStack)
			return this.getValueForItem((ItemStack)in);
		else if (in instanceof Block)
			return this.getValueForItem(new ItemStack((Block)in));
		else if (in instanceof Item)
			return this.getValueForItem(new ItemStack((Item)in));
		else if (in instanceof ArrayList) {
			ArrayList li = (ArrayList)in;
			ElementTagCompound tag = new ElementTagCompound();
			for (int i = 0; i < li.size(); i++) {
				ItemStack is = (ItemStack)li.get(i);
				ElementTagCompound value = this.getValueForItem(is);
				tag.addButMinimizeWith(value);
			}
			return tag;
		}
		else
			return null;
	}

}
