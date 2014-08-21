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

import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Magic.ElementTag;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.RuneShape;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.WorldLocation;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class RecipesCastingTable {

	public static final RecipesCastingTable instance = new RecipesCastingTable();
	private final ArrayList<CastingRecipe> recipes = new ArrayList();

	private RecipesCastingTable() {
		CastingRecipe crystal = new CastingRecipe(ChromaStacks.rawCrystal, ChromaItems.STORAGE.getStackOf(), ChromaStacks.shards);
		crystal.addAuraRequirement(ElementTagCompound.getUniformTag(250));
		recipes.add(crystal);
	}

	public static class CastingRecipe {

		private final ItemStack main;
		private final ItemStack out;
		private final HashMap<List<Integer>, ItemStack> inputs = new HashMap();
		private final ElementTagCompound elements = new ElementTagCompound();
		private final RuneShape runes = new RuneShape();

		private CastingRecipe(ItemStack main, ItemStack out) {
			this.main = main;
			this.out = out;
		}

		private void addAuxItem(ItemStack is, int dx, int dz) {
			inputs.put(Arrays.asList(dx, dz), is);
		}

		private CastingRecipe addAuraRequirement(CrystalElement e, int amt) {
			return this.addAuraRequirement(new ElementTag(e, amt));
		}

		private CastingRecipe addAuraRequirement(ElementTag e) {
			elements.maximizeWith(e);
			return this;
		}

		private CastingRecipe addAuraRequirement(ElementTagCompound e) {
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

		public HashMap<WorldLocation, ItemStack> getOtherInputs(World world, int x, int y, int z) {
			HashMap<WorldLocation, ItemStack> map = new HashMap();
			for (List<Integer> li : inputs.keySet()) {
				ItemStack is = inputs.get(li);
				int dx = li.get(0);
				int dz = li.get(1);
				int dy = y+(Math.abs(dx) != 4 && Math.abs(dz) != 4 ? 0 : 1);
				WorldLocation loc = new WorldLocation(world, x+dx, dy, z+dz);
				map.put(loc, is);
			}
			return map;
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

	public CastingRecipe getRecipe(ItemStack main) {
		for (int i = 0; i < recipes.size(); i++) {
			CastingRecipe r = recipes.get(i);
			ItemStack ctr = r.main;
			if (ReikaItemHelper.matchStacks(main, ctr) && ItemStack.areItemStackTagsEqual(main, ctr)) {
				ArrayList<ItemStack> other = r.inputs;
				if (other.size() == aux.length) {
					for (int k = 0; k < other.size(); k++) {
						ItemStack is = other.get(k);
						if (!ReikaInventoryHelper.checkForItemStack(is, aux, false)) {
							break;
						}
					}
					return r;
				}
			}
		}
		return null;
	}

}
