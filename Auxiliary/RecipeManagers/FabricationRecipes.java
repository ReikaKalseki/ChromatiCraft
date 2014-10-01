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
import java.util.Collection;

import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.KeyedItemStack;

import com.google.common.collect.HashBiMap;

public class FabricationRecipes {

	private static final FabricationRecipes instance = new FabricationRecipes();

	private final HashBiMap<KeyedItemStack, ElementTagCompound> data = HashBiMap.create();

	private ArrayList<ItemStack> products = new ArrayList();

	public FabricationRecipes recipes() {
		return instance;
	}

	private FabricationRecipes() {

	}

	public Collection<ItemStack> getItemsFabricableWith(ElementTagCompound tag) {
		Collection<ItemStack> items = new ArrayList();
		for (KeyedItemStack is : data.keySet()) {
			ElementTagCompound val = data.get(is);
			if (tag.containsAtLeast(val))
				items.add(is.getItem());
		}
		return items;
	}

	public boolean isItemFabricable(ItemStack is, ElementTagCompound tag) {
		KeyedItemStack ks = new KeyedItemStack(is);
		return data.containsKey(ks) && tag.containsAtLeast(data.get(ks));
	}

	private void addElement(ItemStack is, CrystalElement e, int amt) {
		KeyedItemStack ks = new KeyedItemStack(is).setSized();
		ElementTagCompound tag = data.get(ks);
		if (tag == null) {
			tag = new ElementTagCompound();
			data.put(ks, tag);
		}
		tag.addTag(e, amt);
	}

}
