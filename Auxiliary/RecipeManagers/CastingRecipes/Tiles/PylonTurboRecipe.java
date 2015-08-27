package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;


public class PylonTurboRecipe extends PylonRecipe {

	public PylonTurboRecipe(ItemStack out, ItemStack main, RecipeCrystalRepeater repeater) {
		super(out, main);

		this.addAuxItem(ChromaStacks.chargedWhiteShard, 0, -4);
		this.addAuxItem(Blocks.obsidian, 0, -2);

		this.addAuxItem(ChromaStacks.purityDust, 2, 0);
		this.addAuxItem(ChromaStacks.purityDust, -2, 0);

		this.addAuxItem(Blocks.cobblestone, -2, 2);
		this.addAuxItem(Blocks.cobblestone, 0, 2);
		this.addAuxItem(Blocks.cobblestone, 2, 2);

		this.addAuxItem(Blocks.cobblestone, 0, 4);

		this.addAuxItem(Items.iron_ingot, -4, 4);
		this.addAuxItem(Items.iron_ingot, 4, 4);

		this.addAuxItem(Items.gold_ingot, -2, 4);
		this.addAuxItem(Items.gold_ingot, 2, 4);

		for (int i = 0; i < 16; i++) {
			CrystalElement e = CrystalElement.elements[i];
			this.addAuraRequirement(e, 10000);
			this.addRuneRingRune(e);
		}
		this.addRunes(repeater.getRunes());
	}

	@Override
	public int getNumberProduced() {
		return 9;
	}

	@Override
	public int getTypicalCraftedAmount() {
		return 16;
	}

	@Override
	public int getPenaltyThreshold() {
		return this.getTypicalCraftedAmount();
	}

	@Override
	public float getPenaltyMultiplier() {
		return 0;
	}

}
