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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mantle.utils.ItemMetaWrapper;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.Smeltery;
import tconstruct.library.crafting.ToolBuilder;
import tconstruct.library.crafting.ToolRecipe;
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
import appeng.recipes.handlers.Inscribe;
import appeng.recipes.handlers.Inscribe.InscriberRecipe;
import buildcraft.api.recipes.BuildcraftRecipeRegistry;
import buildcraft.api.recipes.CraftingResult;
import buildcraft.api.recipes.IFlexibleRecipe;
import buildcraft.api.recipes.IIntegrationRecipe;
import buildcraft.core.recipes.FlexibleRecipe;
import buildcraft.silicon.TileAssemblyTable;
import buildcraft.silicon.TileIntegrationTable;
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

		cache.put(ChromaStacks.auraDust, new ElementTagCompound(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1));
		cache.put(ChromaStacks.beaconDust, new ElementTagCompound(1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0));
		cache.put(ChromaStacks.bindingCrystal, new ElementTagCompound(1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
		cache.put(ChromaStacks.focusDust, new ElementTagCompound(1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1));
		cache.put(ChromaStacks.enderDust, new ElementTagCompound(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0));
		cache.put(ChromaStacks.waterDust, new ElementTagCompound(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
		cache.put(ChromaStacks.resocrystal, new ElementTagCompound(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
		cache.put(ChromaStacks.resonanceDust, new ElementTagCompound(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1));
		cache.put(ChromaStacks.purityDust, new ElementTagCompound(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1));
		cache.put(ChromaStacks.firaxite, new ElementTagCompound(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0));
		cache.put(ChromaStacks.spaceDust, new ElementTagCompound(2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1));
		cache.put(ChromaStacks.elementDust, new ElementTagCompound(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1));
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
		ElementTagCompound tag = new ElementTagCompound();
		try {
			Object[] pulv = (Object[])getPulverizer.invoke(null);
			Object[] sawm = (Object[])getSawmill.invoke(null);
			Object[] smelt = (Object[])getSmelter.invoke(null);
			Object[] transp1 = (Object[])getTransposerFilling.invoke(null);
			Object[] transp2 = (Object[])getTransposerDraining.invoke(null);

			for (int i = 0; i < pulv.length; i++) {
				Object r = pulv[i];
				if (ReikaItemHelper.matchStacks(is, (ItemStack)pulverizerOut1.get(r)) || ReikaItemHelper.matchStacks(is, (ItemStack)pulverizerOut2.get(r))) {
					tag.addButMinimizeWith(this.getValueForItem((ItemStack)pulverizerIn.get(r)));
				}
			}

			for (int i = 0; i < sawm.length; i++) {
				Object r = sawm[i];
				if (ReikaItemHelper.matchStacks(is, (ItemStack)sawmillOut1.get(r)) || ReikaItemHelper.matchStacks(is, (ItemStack)sawmillOut2.get(r))) {
					tag.addButMinimizeWith(this.getValueForItem((ItemStack)sawmillIn.get(r)));
				}
			}

			for (int i = 0; i < smelt.length; i++) {
				Object r = smelt[i];
				if (ReikaItemHelper.matchStacks(is, (ItemStack)smelterOut1.get(r)) || ReikaItemHelper.matchStacks(is, (ItemStack)smelterOut2.get(r))) {
					tag.addButMinimizeWith(this.getValueForItem((ItemStack)smelterIn1.get(r)));
					tag.addButMinimizeWith(this.getValueForItem((ItemStack)smelterIn2.get(r)));
				}
			}

			for (int i = 0; i < transp1.length; i++) {
				Object r = transp1[i];
				if (ReikaItemHelper.matchStacks(is, (ItemStack)transposerOut.get(r))) {
					tag.addButMinimizeWith(this.getValueForItem((ItemStack)transposerIn.get(r)));
				}
			}

			for (int i = 0; i < transp2.length; i++) {
				Object r = transp2[i];
				if (ReikaItemHelper.matchStacks(is, (ItemStack)transposerOut.get(r))) {
					tag.addButMinimizeWith(this.getValueForItem((ItemStack)transposerIn.get(r)));
				}
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		return tag;
	}

	@ModDependent(ModList.THAUMCRAFT)
	private ElementTagCompound getFromThaumCraft(ItemStack is) {
		ElementTagCompound tag = new ElementTagCompound();
		try {
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
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		return tag;
	}

	@ModDependent(ModList.BCSILICON)
	private ElementTagCompound getFromBCLasers(ItemStack is) {
		ElementTagCompound tag = new ElementTagCompound();
		try {
			TileAssemblyTable fakeTable = new TileAssemblyTable();
			TileIntegrationTable fakeTable2 = new TileIntegrationTable();
			for (IIntegrationRecipe recipe : BuildcraftRecipeRegistry.integrationTable.getRecipes()) {
				CraftingResult<ItemStack> out = recipe.craft(fakeTable2, true);
				if (ReikaItemHelper.matchStacks(out.crafted, is)) {
					for (ItemStack in : (ArrayList<ItemStack>)((FlexibleRecipe)recipe).inputItems) {
						tag.addButMinimizeWith(this.getValueForItem(in));
					}
				}
			}

			for (IFlexibleRecipe<ItemStack> recipe : BuildcraftRecipeRegistry.assemblyTable.getRecipes()) {
				CraftingResult<ItemStack> out = recipe.craft(fakeTable, true);
				if (ReikaItemHelper.matchStacks(out.crafted, is)) {
					for (ItemStack in : (ArrayList<ItemStack>)((FlexibleRecipe)recipe).inputItems) {
						tag.addButMinimizeWith(this.getValueForItem(in));
					}
				}
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		return tag;
	}

	@ModDependent(ModList.TINKERER)
	private ElementTagCompound getFromTinkerTable(ItemStack is) {
		ElementTagCompound tag = new ElementTagCompound();
		try {
			for (tconstruct.library.crafting.CastingRecipe cr : TConstructRegistry.instance.getTableCasting().getCastingRecipes()) {
				if (ReikaItemHelper.matchStacks(is, cr.output)) {
					tag.addButMinimizeWith(this.getFromTinkerSmeltery(cr.castingMetal));
				}
			}

			for (ToolRecipe tr : ToolBuilder.instance.combos) {
				if (tr.getType() == is.getItem()) {
					List<Item> li = new ArrayList();
					li.addAll((List<Item>)toolRecipeHeads.get(tr));
					li.addAll((List<Item>)toolRecipeHandles.get(tr));
					li.addAll((List<Item>)toolRecipeAccessories.get(tr));
					li.addAll((List<Item>)toolRecipeExtras.get(tr));
					for (Item i : li) {
						tag.addButMinimizeWith(this.getValueForItem(new ItemStack(i)));
					}
				}
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		return tag;
	}

	@ModDependent(ModList.TINKERER)
	private ElementTagCompound getFromTinkerSmeltery(FluidStack fs) {
		ElementTagCompound tag = new ElementTagCompound();
		try {
			Map<ItemMetaWrapper, FluidStack> map = Smeltery.getSmeltingList();
			for (ItemMetaWrapper imw : map.keySet()) {
				if (fs.fluidID == map.get(imw).fluidID) {
					tag.addButMinimizeWith(this.getValueForItem(new ItemStack(imw.item, 1, imw.meta)));
				}
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		tag.addValueToColor(CrystalElement.BROWN, 1);
		tag.addValueToColor(CrystalElement.ORANGE, 2);
		return tag;
	}

	@ModDependent(ModList.APPENG)
	private ElementTagCompound getFromAECrafting(ItemStack is) {
		ElementTagCompound tag = new ElementTagCompound();
		try {
			for (InscriberRecipe ir : Inscribe.RECIPES) {
				if (ReikaItemHelper.matchStacks(is, ir.output)) {
					if (ir.usePlates) {
						tag.addButMinimizeWith(this.getValueForItem(ir.plateA));
						tag.addButMinimizeWith(this.getValueForItem(ir.plateB));
					}
					else {
						for (int i = 0; i < ir.imprintable.length; i++) {
							tag.addButMinimizeWith(this.getValueForItem(ir.imprintable[i]));
						}
					}
				}
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		return tag;
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
		if (ModList.APPENG.isLoaded())
			tag.addButMinimizeWith(this.getFromAECrafting(is));
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

	private static Field toolRecipeHeads;
	private static Field toolRecipeHandles;
	private static Field toolRecipeAccessories;
	private static Field toolRecipeExtras;

	private static Method getTransposerFilling;
	private static Method getTransposerDraining;
	private static Method getSmelter;
	private static Method getSawmill;
	private static Method getPulverizer;
	//private static Method getPrecipitator;

	private static Field transposerIn;
	private static Field transposerOut;
	private static Field smelterIn1;
	private static Field smelterIn2;
	private static Field smelterOut1;
	private static Field smelterOut2;
	private static Field sawmillIn;
	private static Field sawmillOut1;
	private static Field sawmillOut2;
	private static Field pulverizerIn;
	private static Field pulverizerOut1;
	private static Field pulverizerOut2;

	static {
		toolRecipeHeads = loadField(ToolRecipe.class, "headList");
		toolRecipeHandles = loadField(ToolRecipe.class, "handleList");
		toolRecipeAccessories = loadField(ToolRecipe.class, "accessoryList");
		toolRecipeExtras = loadField(ToolRecipe.class, "extraList");

		getTransposerFilling = loadMethod("cofh.thermalexpansion.util.crafting.TransposerManager", "getFillRecipeList");
		getTransposerDraining = loadMethod("cofh.thermalexpansion.util.crafting.TransposerManager", "getExtractionRecipeList");
		getSmelter = loadMethod("cofh.thermalexpansion.util.crafting.SmelterManager", "getRecipeList");
		getSawmill = loadMethod("cofh.thermalexpansion.util.crafting.SawmillManager", "getRecipeList");
		getPulverizer = loadMethod("cofh.thermalexpansion.util.crafting.PulverizerManager", "getRecipeList");
		//getPrecipitator = loadMethod("cofh.thermalexpansion.util.crafting.PrecipitatorManager", "getRecipeList");

		transposerIn = loadField("cofh.thermalexpansion.util.crafting.TransposerManager$RecipeTransposer", "input");
		transposerOut = loadField("cofh.thermalexpansion.util.crafting.TransposerManager$RecipeTransposer", "output");

		smelterIn1 = loadField("cofh.thermalexpansion.util.crafting.SmelterManager$RecipeSmelter", "primaryInput");
		smelterIn2 = loadField("cofh.thermalexpansion.util.crafting.SmelterManager$RecipeSmelter", "secondaryInput");
		smelterOut1 = loadField("cofh.thermalexpansion.util.crafting.SmelterManager$RecipeSmelter", "primaryOutput");
		smelterOut2 = loadField("cofh.thermalexpansion.util.crafting.SmelterManager$RecipeSmelter", "secondaryOutput");

		sawmillIn = loadField("cofh.thermalexpansion.util.crafting.SawmillManager$RecipeSawmill", "input");
		sawmillOut1 = loadField("cofh.thermalexpansion.util.crafting.SawmillManager$RecipeSawmill", "primaryOutput");
		sawmillOut2 = loadField("cofh.thermalexpansion.util.crafting.SawmillManager$RecipeSawmill", "secondaryOutput");

		pulverizerIn = loadField("cofh.thermalexpansion.util.crafting.PulverizerManager$RecipePulverizer", "input");
		pulverizerOut1 = loadField("cofh.thermalexpansion.util.crafting.PulverizerManager$RecipePulverizer", "primaryOutput");
		pulverizerOut2 = loadField("cofh.thermalexpansion.util.crafting.PulverizerManager$RecipePulverizer", "secondaryOutput");
	}

	private static Field loadField(String c, String s) {
		try {
			return loadField(Class.forName(c), s);
		}
		catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}

	private static Field loadField(Class c, String s) {
		try {
			Field f = c.getDeclaredField(s);
			f.setAccessible(true);
			return f;
		}
		catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}

	private static Method loadMethod(String c, String s, Class... args) {
		try {
			return loadMethod(Class.forName(c), s, args);
		}
		catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}

	private static Method loadMethod(Class c, String s, Class... args) {
		try {
			Method f = c.getDeclaredMethod(s, args);
			f.setAccessible(true);
			return f;
		}
		catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}

}
