/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.GameRegistry;
import forestry.api.storage.BackpackManager;
import forestry.api.storage.EnumBackpackType;
import forestry.api.storage.IBackpackDefinition;


public class CrystalBackpack implements IBackpackDefinition {

	public static final CrystalBackpack instance = new CrystalBackpack();

	private Item item1;
	private Item item2;

	private CrystalBackpack() {

	}

	public void register() {
		if (BackpackManager.backpackInterface == null) //backpacks disabled
			return;
		item1 = BackpackManager.backpackInterface.addBackpack(this, EnumBackpackType.T1);
		item2 = BackpackManager.backpackInterface.addBackpack(this, EnumBackpackType.T2);

		GameRegistry.registerItem(item1, "CrystalBackPackT1");
		GameRegistry.registerItem(item2, "CrystalBackPackT2");
	}

	public Item getItem1() {
		return item1;
	}

	public Item getItem2() {
		return item2;
	}

	@Override
	public String getKey() {
		return "crystal";
	}

	@Override
	public String getName(ItemStack backpack) {
		return "Crystal Backpack";
	}

	@Override
	public int getPrimaryColour() {
		return 0x303030;
	}

	@Override
	public int getSecondaryColour() {
		return 0x22aaff;
	}

	@Override
	public void addValidItem(ItemStack is) {
		//NOOP, since acceptable types are fixed
	}

	@Override
	public void addValidItems(List<ItemStack> li) {
		for (ItemStack is : li) {
			this.addValidItem(is);
		}
	}

	@Override
	public boolean isValidItem(ItemStack is) {
		return ItemCrystalCell.isTypeStorable(is);
	}

}
