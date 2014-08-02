/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Magic.ElementTag;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.RuneShape;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class RecipesCastingTable {

	public static final RecipesCastingTable instance = new RecipesCastingTable();

	private RecipesCastingTable() {

	}

	public static class CastingRecipe {

		private final ItemStack main;
		private final ItemStack out;
		private final ArrayList<ItemStack> inputs = new ArrayList();
		private final ElementTagCompound aura = new ElementTagCompound();
		private final ArrayList<RuneShape> runes = new ArrayList();

		private CastingRecipe(ItemStack main, ItemStack out, ItemStack... in) {
			this.main = main;
			this.out = out;
			for (int i = 0; i < in.length; i++)
				inputs.add(in[i]);
		}

		private CastingRecipe addAuraRequirement(CrystalElement e, int amt) {
			return this.addAuraRequirement(new ElementTag(e, amt));
		}

		private CastingRecipe addAuraRequirement(ElementTag e) {
			aura.maximizeWith(e);
			return this;
		}

		private CastingRecipe addRuneShape(RuneShape rune) {
			runes.add(rune);
			return this;
		}

		public ItemStack getMainInput() {
			return main.copy();
		}

		public ItemStack getOutput() {
			return out.copy();
		}

		public ArrayList<ItemStack> getOtherInputs() {
			ArrayList<ItemStack> li = new ArrayList();
			li.addAll(inputs);
			return li;
		}

		public ElementTagCompound getRequiredAura() {
			return aura.copy();
		}

		public ArrayList<RuneShape> getRequiredRunes() {
			ArrayList<RuneShape> li = new ArrayList();
			li.addAll(runes);
			return li;
		}

	}

}
