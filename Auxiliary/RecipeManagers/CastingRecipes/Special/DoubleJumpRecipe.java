/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Special;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.TempleCastingRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;


public class DoubleJumpRecipe extends TempleCastingRecipe {

	public DoubleJumpRecipe(Item boot) {
		super(getOutput(boot), getRecipe(boot));

		this.addRune(CrystalElement.LIME, -5, -1, -3);
		this.addRune(CrystalElement.LIGHTBLUE, 5, -1, 3);
	}

	@Override
	public NBTTagCompound getOutputTag(NBTTagCompound input) {
		return input;
	}

	private static IRecipe getRecipe(Item boot) {
		ItemStack is = getOutput(boot);
		return ReikaRecipeHelper.getShapedRecipeFor(is, " e ", "fbf", " p ", 'b', boot, 'e', Items.ender_pearl, 'p', Blocks.piston, 'f', Items.feather);
	}

	private static ItemStack getOutput(Item boot) {
		ItemStack is = new ItemStack(boot);
		if (is.stackTagCompound == null)
			is.stackTagCompound = new NBTTagCompound();
		is.stackTagCompound.setBoolean("Chroma_Double_Jump", true);
		return is;
	}

	@Override
	public int getDuration() {
		return 20*super.getDuration();
	}

}
