package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonRecipe;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;


public class MeteorTowerRecipe extends PylonRecipe {

	public MeteorTowerRecipe(int tier) {
		super(getOutput(tier), getCentral(tier));

		this.addAuxItem(ChromaStacks.thermiticCrystal, 0, -2);
		this.addAuxItem(ChromaStacks.thermiticCrystal, 0, 2);
		this.addAuxItem(ChromaStacks.thermiticCrystal, 0, -2);
		this.addAuxItem(ChromaStacks.thermiticCrystal, 0, -4);
		this.addAuxItem(ChromaStacks.thermiticCrystal, -2, -4);
		this.addAuxItem(ChromaStacks.thermiticCrystal, 2, -4);

		if (tier == 0) {
			this.addAuxItem(ChromaStacks.fieryIngot, -4, -4);
			this.addAuxItem(ChromaStacks.fieryIngot, 4, -4);
			this.addAuxItem(ChromaStacks.fieryIngot, -4, 4);
			this.addAuxItem(ChromaStacks.fieryIngot, 4, 4);
		}

		this.addAuxItem(getGlowSource(tier), -2, 4);
		this.addAuxItem(getGlowSource(tier), 0, 4);
		this.addAuxItem(getGlowSource(tier), 2, 4);

		this.addAuxItem(ChromaStacks.energyPowder, -2, 2);
		this.addAuxItem(ChromaStacks.energyPowder, 0, 2);
		this.addAuxItem(ChromaStacks.energyPowder, 2, 2);

		this.addAuxItem(Blocks.obsidian, -4, 2);
		this.addAuxItem(Blocks.obsidian, 4, 2);

		this.addAuxItem(Items.diamond, -2, 0);
		this.addAuxItem(Items.diamond, 2, 0);

		this.addAuxItem(getResoSource(tier), -4, 0);
		this.addAuxItem(getResoSource(tier), 4, 0);

		this.addAuxItem(getFocus(tier), -2, -2);
		this.addAuxItem(getFocus(tier), 2, -2);

		this.addAuxItem(ChromaStacks.firaxite, -4, -2);
		this.addAuxItem(ChromaStacks.firaxite, 4, -2);

		this.addAuraRequirement(CrystalElement.PINK, 40000*(1+tier));
		this.addAuraRequirement(CrystalElement.ORANGE, 40000*(1+tier));
	}

	private static ItemStack getResoSource(int tier) {
		switch(tier) {
			case 0:
			default:
				return ChromaStacks.resonanceDust;
			case 1:
				return ChromaStacks.resocrystal;
			case 2:
				return ChromaStacks.boostroot;
		}
	}

	private static ItemStack getGlowSource(int tier) {
		switch(tier) {
			case 0:
			default:
				return new ItemStack(Blocks.glowstone);
			case 1:
				return ChromaStacks.glowbeans;
			case 2:
				return ChromaStacks.lumaDust;
		}
	}

	private static ItemStack getFocus(int tier) {
		switch(tier) {
			case 0:
			default:
				return ChromaStacks.focusDust;
			case 1:
				return ChromaStacks.focusDust;
			case 2:
				return ChromaStacks.echoCrystal;
		}
	}

	private static ItemStack getCentral(int tier) {
		return tier == 0 ? ChromaStacks.energyCoreHigh : getOutput(tier-1);
	}

	private static ItemStack getOutput(int tier) {
		return ChromaTiles.METEOR.getCraftedNBTProduct("tier", tier);
	}

}
