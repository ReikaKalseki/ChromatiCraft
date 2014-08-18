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

import Reika.ChromatiCraft.Magic.ElementTag;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.RuneShape;
import Reika.ChromatiCraft.Registry.CrystalElement;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class RecipesCastingTable {

	public static final RecipesCastingTable instance = new RecipesCastingTable();
	private final ArrayList<CastingRecipe> recipes = new ArrayList();

	private RecipesCastingTable() {



	}

	public static class CastingRecipe {

		private final ItemStack main;
		private final ItemStack out;
		private final ArrayList<ItemStack> inputs = new ArrayList();
		private final ElementTagCompound elements = new ElementTagCompound();
		private final RuneShape runes = new RuneShape();

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
			elements.maximizeWith(e);
			return this;
		}

		private CastingRecipe addRune(CrystalElement color, int rx, int ry, int rz) {
			runes.addRune(color, rx, ry, rz);
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
			return elements.copy();
		}

		public boolean matchRunes(World world, int x, int y, int z) {
			return runes.matchAt(world, x, y, z, 0, 0, 0);
		}

		public void drawInParticlesTo(World world, int x, int y, int z) {

		}

	}

	public static CastingRecipe getRecipe(ItemStack main, ItemStack... aux) {
		return null;
	}

}