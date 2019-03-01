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

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;

import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.TempleCastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.HeatLilyRecipe;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class HeatLampRecipe extends TempleCastingRecipe {

	public HeatLampRecipe(Object ingot, int n, HeatLilyRecipe r, boolean isColdLamp) {
		super(calcOutput(n, isColdLamp), getRecipe(ingot, n, isColdLamp));

		this.addRunes(r.getRunes());
		if (isColdLamp) {
			this.addRune(CrystalElement.WHITE, 0, -1, -4);
			this.addRuneRingRune(CrystalElement.LIGHTGRAY);
		}
	}

	private static IRecipe getRecipe(Object ingot, int n, boolean isColdLamp) {
		return new ShapedOreRecipe(calcOutput(n, isColdLamp), "fgf", "gag", "fgf", 'g', isColdLamp ? new ItemStack(Items.snowball) : ChromaStacks.firaxite, 'f', isColdLamp ? ChromaStacks.icyDust : ChromaStacks.firaxite, 'a', ingot);
	}

	private static ItemStack calcOutput(int n, boolean isColdLamp) {
		return ReikaItemHelper.getSizedItemStack(ChromaBlocks.HEATLAMP.getStackOfMetadata(isColdLamp ? 8 : 0), n);
	}

	@Override
	public int getTypicalCraftedAmount() {
		return 8;
	}

}
