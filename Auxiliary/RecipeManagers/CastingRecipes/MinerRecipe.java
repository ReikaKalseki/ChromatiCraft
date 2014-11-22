package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes;

import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class MinerRecipe extends PylonRecipe {

	public MinerRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(ChromaStacks.resonanceDust, -2, 0);
		this.addAuxItem(ChromaStacks.resonanceDust, 2, 0);
		this.addAuxItem(ChromaStacks.resonanceDust, 0, -2);
		this.addAuxItem(ChromaStacks.voidCore, 0, 2);

		this.addAuxItem(ChromaStacks.beaconDust, -4, 0);
		this.addAuxItem(ChromaStacks.beaconDust, 4, 0);
		this.addAuxItem(ChromaStacks.beaconDust, 0, -4);

		this.addAuraRequirement(CrystalElement.BROWN, 5000);
		this.addAuraRequirement(CrystalElement.PURPLE, 5000);
		this.addAuraRequirement(CrystalElement.YELLOW, 15000);
	}

}
