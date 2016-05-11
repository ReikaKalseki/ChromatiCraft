/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Instantiable.Data.Maps.BranchingTree;
import Reika.DragonAPI.Instantiable.Recipe.ItemMatch;


public class TransmutationRecipes {

	public static final TransmutationRecipes instance = new TransmutationRecipes();

	//private final SequenceMap<ItemMatch> recipes = new SequenceMap();
	private final BranchingTree<ItemMatch, ItemStack> recipes = new BranchingTree();

	private TransmutationRecipes() {

		//recipes.addChildless(null);
		/*
		this.addRecipe(ChromaStacks.beaconDust, new ElementTagCompound(), Items.iron_axe, Items.arrow, Blocks.diamond_block, ChromaStacks.icyDust);
		this.addRecipe(ChromaStacks.beaconDust, new ElementTagCompound(), Items.iron_axe, Items.arrow, Blocks.gold_block);
		this.addRecipe(ChromaStacks.beaconDust, new ElementTagCompound(), Items.iron_axe, Items.slime_ball);
		this.addRecipe(ChromaStacks.elementDust, new ElementTagCompound(), Items.gold_ingot, Items.boat, ChromaStacks.brownShard, ChromaStacks.aqua);
		this.addRecipe(ChromaStacks.auraDust, new ElementTagCompound(), Items.stone_axe, Blocks.stained_glass, Blocks.bedrock, ChromaStacks.bindingCrystal);
		this.addRecipe(ChromaStacks.elementDust, new ElementTagCompound(), Items.gold_ingot, Items.baked_potato, ChromaStacks.brownShard, ChromaStacks.aqua);
		ReikaJavaLibrary.pConsole(recipes);
		ReikaJavaLibrary.pConsole(recipes);*/
		/*
		Collection<ItemMatch> c = recipes.getChildren(null);
		ReikaJavaLibrary.pConsole(c);
		for (ItemMatch m : c) {
			Collection<ItemMatch> c2 = recipes.getChildren(null);
			ReikaJavaLibrary.pConsole(c2);
		}
		 */
	}

	private void addRecipe(ItemStack out, ElementTagCompound energy, Object... inputs) {
		/*
		ItemMatch in;
		ItemMatch prev = null;
		for (int i = 0; i < inputs.length; i++) {
			in = this.parseItem(inputs[i]);
			recipes.addParent(in, prev);
			prev = in;
		}
		 */
		ItemMatch[] branches = new ItemMatch[inputs.length];
		for (int i = 0; i < inputs.length; i++) {
			branches[i] = this.parseItem(inputs[i]);
		}
		recipes.addPath(out, branches);
	}

	private ItemMatch parseItem(Object o) {
		if (o instanceof String) {
			return new ItemMatch((ItemStack)o);
		}
		else if (o instanceof Block) {
			return new ItemMatch(new ItemStack((Block)o));
		}
		else if (o instanceof Item) {
			return new ItemMatch(new ItemStack((Item)o));
		}
		else if (o instanceof ItemStack) {
			return new ItemMatch((ItemStack)o);
		}
		else {
			throw new RegistrationException(ChromatiCraft.instance, "Illegal input item "+o+" for transmutation recipe");
		}
	}

	/*
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
	 */
	public ElementTagCompound getEnergyValue(ItemStack is) {
		if (ChromaItems.BERRY.matchWith(is)) {
			return new ElementTagCompound(CrystalElement.elements[is.getItemDamage()], 1);
		}
		else if (ChromaItems.SHARD.matchWith(is)) {
			return new ElementTagCompound(CrystalElement.elements[is.getItemDamage()%16], is.getItemDamage() >= 16 ? 36 : 6);
		}
		else if (ChromaItems.SEED.matchWith(is)) {
			return new ElementTagCompound(CrystalElement.elements[is.getItemDamage()%16], 2);
		}
		else {
			return null;
		}
	}

}
