/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import Reika.ChromatiCraft.ChromaNames;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Base.ItemChromaMulti;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class ItemChromaCrafting extends ItemChromaMulti {

	public ItemChromaCrafting(int tex) {
		super(tex);
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem ei) {
		int x = MathHelper.floor_double(ei.posX);
		int y = MathHelper.floor_double(ei.posY);
		int z = MathHelper.floor_double(ei.posZ);
		ItemStack is = ei.getEntityItem();
		if (ReikaItemHelper.matchStacks(is, ChromaStacks.chromaDust) && is.stackSize == 64) {

		}
		return false;
	}

	@Override
	public int getNumberTypes() {
		return ChromaNames.craftingNames.length;
	}

}
