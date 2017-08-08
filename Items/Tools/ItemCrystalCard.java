/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items.Tools;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalConsole.TileControl;


public class ItemCrystalCard extends ItemChromaTool {

	public ItemCrystalCard(int index) {
		super(index);
	}

	public static ArrayList<TileControl> getControllers(ItemStack is) {
		return null;
	}

}
