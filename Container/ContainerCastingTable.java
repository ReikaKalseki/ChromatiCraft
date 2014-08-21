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

import Reika.DragonAPI.Base.CoreContainer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public class ContainerCastingTable extends CoreContainer {

	public ContainerCastingTable(EntityPlayer player, TileEntity te) {
		super(player, te);

		this.addSlot(0, 80, 55);

		this.addPlayerInventoryWithOffset(player, 0, 43);
	}

}
