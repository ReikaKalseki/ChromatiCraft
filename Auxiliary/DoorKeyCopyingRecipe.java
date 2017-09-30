/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import java.util.ArrayList;
import java.util.UUID;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Items.Tools.ItemDoorKey;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;


public class DoorKeyCopyingRecipe implements IRecipe {

	@Override
	public boolean matches(InventoryCrafting ic, World world) {
		return this.getCraftingResult(ic) != null;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting ic) {
		ArrayList<ItemStack> c = ReikaInventoryHelper.convertCraftToItemList(ic);
		if (c.size() != 2)
			return null;
		ItemStack is1 = c.get(0);
		ItemStack is2 = c.get(1);
		if (!ChromaItems.KEY.matchWith(is1) || !ChromaItems.KEY.matchWith(is2))
			return null;
		UUID uid1 = ((ItemDoorKey)is1.getItem()).getUID(is1);
		UUID uid2 = ((ItemDoorKey)is2.getItem()).getUID(is2);
		if (uid1 == null || uid2 != null)
			return null;
		return ReikaItemHelper.getSizedItemStack(is1, 2);
	}

	@Override
	public int getRecipeSize() {
		return 2;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return ChromaItems.KEY.getCraftedProduct(2);
	}

}
