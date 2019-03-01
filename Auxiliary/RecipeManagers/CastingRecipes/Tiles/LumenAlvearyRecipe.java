/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles;

import net.minecraft.item.ItemStack;

import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonCastingRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ModInteract.ItemHandlers.ForestryHandler;


public class LumenAlvearyRecipe extends PylonCastingRecipe {

	public LumenAlvearyRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		ItemStack panel = ModList.MAGICBEES.isLoaded() ? ForestryHandler.CraftingMaterials.SCENTEDPANEL.getItem() : ForestryHandler.CraftingMaterials.PULSEMESH.getItem();

		this.addAuxItem(panel, -2, -2);
		this.addAuxItem(panel, 0, -2);
		this.addAuxItem(panel, 2, -2);
		this.addAuxItem(panel, -2, 0);
		this.addAuxItem(panel, 2, 0);
		this.addAuxItem(panel, -2, 2);
		this.addAuxItem(panel, 0, 2);
		this.addAuxItem(panel, 2, 2);

		this.addAuxItem(ChromaStacks.auraDust, -4, -2);
		this.addAuxItem(ChromaStacks.auraDust, -4, 2);
		this.addAuxItem(ChromaStacks.auraDust, 4, -2);
		this.addAuxItem(ChromaStacks.auraDust, 4, 2);
		this.addAuxItem(ChromaStacks.auraDust, -2, -4);
		this.addAuxItem(ChromaStacks.auraDust, 2, -4);
		this.addAuxItem(ChromaStacks.auraDust, -2, 4);
		this.addAuxItem(ChromaStacks.auraDust, 2, 4);

		this.addAuxItem(ChromaStacks.focusDust, 0, 4);
		this.addAuxItem(ChromaStacks.focusDust, 0, -4);
		this.addAuxItem(ChromaStacks.focusDust, 4, 0);
		this.addAuxItem(ChromaStacks.focusDust, -4, 0);

		this.addAuxItem(ChromaStacks.livingEssence, 4, 4);
		this.addAuxItem(ChromaStacks.livingEssence, 4, -4);
		this.addAuxItem(ChromaStacks.livingEssence, -4, 4);
		this.addAuxItem(ChromaStacks.livingEssence, -4, -4);

		this.addAuraRequirement(CrystalElement.GREEN, 20000);
		this.addAuraRequirement(CrystalElement.BLACK, 20000);
	}

}
