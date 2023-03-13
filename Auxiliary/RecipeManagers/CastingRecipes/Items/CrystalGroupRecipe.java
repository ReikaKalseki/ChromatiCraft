/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

import Reika.ChromatiCraft.Auxiliary.Interfaces.ShardGroupingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.TempleCastingRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;
import Reika.DragonAPI.Libraries.Java.ReikaArrayHelper;

public class CrystalGroupRecipe extends TempleCastingRecipe implements ShardGroupingRecipe {

	private static final int CHARGED_YIELD_BOOST = 4;

	private static final List<HashSet<CrystalElement>> groupMixes = new ArrayList();
	private static final int[] elementGroupIndexes = ReikaArrayHelper.getArrayOf(-1, CrystalElement.elements.length);

	private final boolean isChargedShards;

	public CrystalGroupRecipe(ItemStack out, CrystalElement e1, CrystalElement e2, CrystalElement e3, CrystalElement e4, ItemStack ctr, boolean charged) {
		super(out, getRecipe(out, e1, e2, e3, e4, ctr, charged));

		isChargedShards = charged;

		List<ItemStack>[] items = this.getRecipeArray();
		int r = out.getItemDamage() == 0 ? 3 : 4;
		int dl = out.getItemDamage() == 2 ? -2 : (out.getItemDamage() == 3 ? 2 : 0);
		this.addRune(items[1].get(0).getItemDamage()%16, 0+dl, 0, -r);
		this.addRune(items[3].get(0).getItemDamage()%16, -r, 0, 0-dl);
		this.addRune(items[5].get(0).getItemDamage()%16, r, 0, 0+dl);
		this.addRune(items[7].get(0).getItemDamage()%16, 0-dl, 0, r);

		int idx = groupMixes.size();
		groupMixes.add(new HashSet(Arrays.asList(e1, e2, e3, e4)));
		elementGroupIndexes[e1.ordinal()] = idx;
		elementGroupIndexes[e2.ordinal()] = idx;
		elementGroupIndexes[e3.ordinal()] = idx;
		elementGroupIndexes[e4.ordinal()] = idx;
	}

	@Override
	public boolean canBeSimpleAutomated() {
		return true;
	}

	@Override
	public boolean canGiveDoubleOutput() {
		return true;
	}

	private static IRecipe getRecipe(ItemStack out, CrystalElement e1, CrystalElement e2, CrystalElement e3, CrystalElement e4, ItemStack ctr, boolean chg) {
		return ReikaRecipeHelper.getShapedRecipeFor(out, " A ", "BIC", " D ", 'A', getShardType(e1, chg), 'B', getShardType(e2, chg), 'C', getShardType(e3, chg), 'D', getShardType(e4, chg), 'I', ctr);
	}

	private static ItemStack getShardType(CrystalElement e, boolean charged) {
		return charged ? getChargedShard(e) : getShard(e);
	}

	@Override
	public int getExperience() {
		return isChargedShards ? CHARGED_YIELD_BOOST*super.getExperience() : super.getExperience();
	}

	@Override
	public int getDuration() {
		return isChargedShards ? 2*super.getDuration() : super.getDuration();
	}

	@Override
	public int getNumberProduced() {
		return isChargedShards ? CHARGED_YIELD_BOOST : super.getNumberProduced();
	}

	@Override
	public int getTypicalCraftedAmount() {
		return isChargedShards ? this.getTypicalCraftedAmount()/CHARGED_YIELD_BOOST : super.getTypicalCraftedAmount();
	}

	public static Set<CrystalElement> getGroupMix(int idx) {
		return Collections.unmodifiableSet(groupMixes.get(idx));
	}

	public static int getGroupIndex(CrystalElement e) {
		return elementGroupIndexes[e.ordinal()];
	}

}
