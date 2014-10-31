package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.ChromatiCraft.TileEntity.TileEntityCastingTable;

public class RitualTableRecipe extends CastingRecipe {

	public RitualTableRecipe(ItemStack out, IRecipe recipe) {
		super(out, recipe);
	}

	@Override
	public void onCrafted(TileEntityCastingTable te, EntityPlayer ep) {
		//achievement
	}

}
