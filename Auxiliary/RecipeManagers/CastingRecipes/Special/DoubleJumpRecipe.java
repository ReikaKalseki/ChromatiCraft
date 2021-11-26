/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Special;

import java.util.HashSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;

import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.TempleCastingRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class DoubleJumpRecipe extends TempleCastingRecipe {

	private static final HashSet<String> alreadyCreated = new HashSet();

	public DoubleJumpRecipe(Item boot) {
		super(getOutput(boot), getRecipe(boot));

		this.addRune(CrystalElement.LIME, -5, -1, -3);
		this.addRune(CrystalElement.LIGHTBLUE, 5, -1, 3);

		if (!alreadyCreated.add(this.getIDString())) {
			throw new RuntimeException("Cannot create second recipe for the same boots item "+boot+" / "+GameRegistry.findUniqueIdentifierFor(boot));
		}
	}

	public static void clearCache() {
		alreadyCreated.clear();
	}

	@Override
	public NBTTagCompound getOutputTag(EntityPlayer ep, NBTTagCompound input) {
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

	@Override
	@SideOnly(Side.CLIENT)
	public String getDisplayName() {
		return "Double Jump Application";
	}

}
