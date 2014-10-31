package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;

public class TransitionRecipe extends MultiBlockCastingRecipe {

	public TransitionRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		//for now
		this.addAuxItem(new ItemStack(Items.stick), -2, -2);
		this.addAuxItem(new ItemStack(Items.stick), 2, 2);
		this.addAuxItem(new ItemStack(Items.stick), -2, 2);
		this.addAuxItem(new ItemStack(Items.stick), 2, -2);

		this.addAuxItem(new ItemStack(Items.iron_ingot), -2, 0);
		this.addAuxItem(new ItemStack(Items.iron_ingot), 2, 0);
		this.addAuxItem(new ItemStack(Items.iron_ingot), 0, 2);
		this.addAuxItem(new ItemStack(Items.iron_ingot), 0, -2);

		this.addAuxItem(ChromaStacks.auraDust, -4, 0);
		this.addAuxItem(ChromaStacks.auraDust, 4, 0);
		this.addAuxItem(ChromaStacks.auraDust, 0, 4);
		this.addAuxItem(ChromaStacks.auraDust, 0, -4);
	}

}
