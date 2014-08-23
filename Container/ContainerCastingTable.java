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

import Reika.ChromatiCraft.Auxiliary.RecipeManagers.RecipesCastingTable.RecipeType;
import Reika.ChromatiCraft.TileEntity.TileEntityCastingTable;
import Reika.DragonAPI.Base.CoreContainer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public class ContainerCastingTable extends CoreContainer {

	public ContainerCastingTable(EntityPlayer player, TileEntity te) {
		super(player, te);
		TileEntityCastingTable tile = (TileEntityCastingTable)te;

		int dy = tile.getTier().isAtLeast(RecipeType.MULTIBLOCK) ? 57 : 37;
		for (int i = 0; i < 3; i++) {
			for (int k = 0; k < 3; k++) {
				int id = i*3+k;
				this.addSlot(id, 62+k*18, dy+i*18);
			}
		}

		dy = tile.getTier().isAtLeast(RecipeType.MULTIBLOCK) ? 74 : 43;
		this.addPlayerInventoryWithOffset(player, 0, dy);
	}

}
