/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Container;

import net.minecraft.entity.player.EntityPlayer;
import Reika.ChromatiCraft.TileEntity.Processing.TileEntityInventoryTicker;
import Reika.DragonAPI.Base.CoreContainer;

public class ContainerInventoryTicker extends CoreContainer {

	public ContainerInventoryTicker(EntityPlayer player, TileEntityInventoryTicker te) {
		super(player, te);

		for (int i = 0; i < 3; i++) {
			for (int k = 0; k < 9; k++) {
				this.addSlot(i*9+k, 8+k*18, 17+i*18);
			}
		}

		this.addPlayerInventoryWithOffset(player, 4, 0);
	}

}
