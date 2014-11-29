/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.RecipeType;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingTable;
import Reika.DragonAPI.Base.CoreContainer;

public class ContainerCastingTable extends CoreContainer {

	public ContainerCastingTable(EntityPlayer player, TileEntity te) {
		super(player, te);
		TileEntityCastingTable tile = (TileEntityCastingTable)te;

		int dy = tile.isAtLeast(RecipeType.MULTIBLOCK) ? 57 : 37;
		for (int i = 0; i < 3; i++) {
			for (int k = 0; k < 3; k++) {
				int id = i*3+k;
				this.addSlot(id, 62+k*18, dy+i*18);
			}
		}

		this.addSlot(9, 189, 12);

		dy = tile.isAtLeast(RecipeType.MULTIBLOCK) ? 74 : 43;
		this.addPlayerInventoryWithOffset(player, 0, dy);
	}

	@Override
	public ItemStack slotClick(int slot, int par2, int action, EntityPlayer ep)
	{
		if (action == 4 && slot == 9)
			action = 0;
		return super.slotClick(slot, par2, action, ep);
	}

}
