/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.crafting.IRecipe;

import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.TempleCastingRecipe;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;


public class CrystalAltarRecipe extends TempleCastingRecipe {

	public CrystalAltarRecipe(CrystalElement e) {
		super(ChromaBlocks.COLORALTAR.getStackOfMetadata(e.ordinal()), getRecipe(e));

		this.addRuneRingRune(e);

		this.addRune(CrystalElement.BLUE, -1, -1, -3);
	}

	private static IRecipe getRecipe(CrystalElement e) {
		return ReikaRecipeHelper.getShapedRecipeFor(ChromaBlocks.COLORALTAR.getStackOfMetadata(e.ordinal()), "qgq", "cIc", "SOS", 'c', Blocks.cobblestone, 'O', Blocks.obsidian, 'I', Items.iron_ingot, 'S', ReikaItemHelper.stoneSlab, 'q', Items.quartz, 'g', ChromaBlocks.LAMP.getStackOfMetadata(e.ordinal()));
	}

	@Override
	public int getTypicalCraftedAmount() {
		return 16;
	}

}
