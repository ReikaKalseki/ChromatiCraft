/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import Reika.ChromatiCraft.Block.Dimension.Structure.BlockStructureDataStorage.TileEntityStructurePassword;
import Reika.DragonAPI.Base.CoreContainer;

public class ContainerStructurePassword extends CoreContainer {

	private final TileEntityStructurePassword tile;

	public ContainerStructurePassword(EntityPlayer player, TileEntityStructurePassword te) {
		super(player, te);
		tile = te;

		for (int i = 0; i < 8; i++)
			this.addSlot(i, 17+i*18, 25);

		this.addPlayerInventoryWithOffset(player, 0, -29);
	}

	@Override
	public ItemStack slotClick(int ID, int par2, int par3, EntityPlayer ep) {
		ItemStack ret = super.slotClick(ID, par2, par3, ep);
		tile.checkPassword(ep);
		return ret;
	}


}
