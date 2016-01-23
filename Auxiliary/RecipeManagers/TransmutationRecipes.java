package Reika.ChromatiCraft.Auxiliary.RecipeManagers;

import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.DragonAPI.Instantiable.Data.Maps.ItemHashMap;


public class TransmutationRecipes {

	public static final TransmutationRecipes instance = new TransmutationRecipes();

	private final ItemHashMap<Transmutation> recipes = new ItemHashMap();

	private TransmutationRecipes() {

	}

	public Transmutation getRecipe(ItemStack is) {
		return recipes.get(is);
	}

	public static class Transmutation {

		private final ItemStack catalyst;
		private final ItemStack output;
		private final ElementTagCompound cost;

		private Transmutation(ItemStack in, ItemStack out, ElementTagCompound tag) {
			catalyst = in;
			output = out;
			cost = tag;
		}

		public ElementTagCompound getCost() {
			return cost.copy();
		}

	}

}
