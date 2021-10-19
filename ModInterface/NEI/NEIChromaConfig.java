/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface.NEI;

import java.util.Comparator;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.recipe.TemplateRecipeHandler.CachedRecipe;

public class NEIChromaConfig implements IConfigureNEI {

	private static final CrystalBrewerHandler crystal = new CrystalBrewerHandler();
	private static final FabricatorHandler fabrication = new FabricatorHandler();
	private static final CrystalFurnaceHandler furnace = new CrystalFurnaceHandler();
	private static final GlowFireHandler glow = new GlowFireHandler();
	private static final PlantDustHandler plants = new PlantDustHandler();
	private static final EnchantDecompHandler enchant = new EnchantDecompHandler();

	private static final ChromaNEITabOccluder occlusion = new ChromaNEITabOccluder();

	public static final Comparator<CachedRecipe> elementRecipeSorter = new Comparator<CachedRecipe>() {

		@Override
		public int compare(CachedRecipe o1, CachedRecipe o2) {
			if (o1 instanceof ElementTagRecipe && o2 instanceof ElementTagRecipe) {
				ItemStack i1 = ((ElementTagRecipe)o1).getItem();
				ItemStack i2 = ((ElementTagRecipe)o2).getItem();
				ElementTagCompound t1 = ((ElementTagRecipe)o1).getTag();
				ElementTagCompound t2 = ((ElementTagRecipe)o2).getTag();
				int item = ReikaItemHelper.comparator.compare(i1, i2);
				return item;
			}
			else if (o1 instanceof ElementTagRecipe) {
				return -1;
			}
			else if (o2 instanceof ElementTagRecipe) {
				return 1;
			}
			else {
				return 0;
			}
		}

	};

	static interface ElementTagRecipe {

		ItemStack getItem();

		ElementTagCompound getTag();

	}

	@Override
	public void loadConfig() {
		ChromatiCraft.logger.log("Loading NEI Compatibility!");

		API.registerNEIGuiHandler(occlusion);

		API.registerRecipeHandler(crystal);
		API.registerUsageHandler(crystal);

		API.registerRecipeHandler(furnace);
		API.registerUsageHandler(furnace);

		API.registerRecipeHandler(fabrication);

		API.registerRecipeHandler(plants);

		API.registerRecipeHandler(glow);
		API.registerUsageHandler(glow);

		API.registerRecipeHandler(enchant);
		API.registerUsageHandler(enchant);

		ChromatiCraft.logger.log("Hiding technical blocks from NEI!");
		for (int i = 0; i < ChromaBlocks.blockList.length; i++) {
			ChromaBlocks b = ChromaBlocks.blockList[i];
			if (b.isTechnical() || (!DragonAPICore.isReikasComputer() && b.isDimensionStructureBlock()))
				this.hideBlock(b.getBlockInstance());
		}

		if (ChromatiCraft.instance.isLocked()) {
			for (int i = 0; i < ChromaItems.itemList.length; i++) {
				ChromaItems ir = ChromaItems.itemList[i];
				API.hideItem(new ItemStack(ir.getItemInstance(), 1, OreDictionary.WILDCARD_VALUE));
			}
			for (int i = 0; i < ChromaBlocks.blockList.length; i++) {
				ChromaBlocks b = ChromaBlocks.blockList[i];
				this.hideBlock(b.getBlockInstance());
			}
		}
	}

	private void hideBlock(Block b) {
		API.hideItem(new ItemStack(b, 1, OreDictionary.WILDCARD_VALUE));
	}

	@Override
	public String getName() {
		return "ChromatiCraft NEI Handlers";
	}

	@Override
	public String getVersion() {
		return "Gamma";
	}

}
