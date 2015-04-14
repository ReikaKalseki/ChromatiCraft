package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonRecipe;

public class AutomatorRecipe extends PylonRecipe {

	public AutomatorRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(ChromaStacks.complexIngot, -4, -4);
		this.addAuxItem(ChromaStacks.complexIngot, 4, -4);
		this.addAuxItem(ChromaStacks.complexIngot, -4, 4);
		this.addAuxItem(ChromaStacks.complexIngot, 4, 4);

		this.addAuxItem(Items.diamond, -2, -4);
		this.addAuxItem(Items.diamond, 2, -4);

		this.addAuxItem(Items.diamond, -2, 4);
		this.addAuxItem(Items.diamond, 2, 4);

		this.addAuxItem(Items.diamond, -4, -2);
		this.addAuxItem(Items.diamond, -4, 2);

		this.addAuxItem(Items.diamond, 4, -2);
		this.addAuxItem(Items.diamond, 4, 2);

		this.addAuxItem(ChromaStacks.chargedWhiteShard, 0, -4);
		this.addAuxItem(ChromaStacks.chargedWhiteShard, 0, 4);
		this.addAuxItem(ChromaStacks.chargedWhiteShard, 4, 0);
		this.addAuxItem(ChromaStacks.chargedWhiteShard, -4, 0);

		this.addAuxItem(ChromaStacks.spaceDust, -2, -2);
		this.addAuxItem(ChromaStacks.spaceDust, -2, 2);
		this.addAuxItem(ChromaStacks.spaceDust, 2, -2);
		this.addAuxItem(ChromaStacks.spaceDust, 2, 2);

		this.addAuxItem(ChromaStacks.enderDust, 0, -2);
		this.addAuxItem(ChromaStacks.enderDust, 0, 2);
		this.addAuxItem(ChromaStacks.enderDust, -2, 0);
		this.addAuxItem(ChromaStacks.enderDust, 2, 0);
	}

}
