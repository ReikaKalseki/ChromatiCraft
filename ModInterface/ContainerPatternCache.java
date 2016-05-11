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

import net.minecraft.entity.player.EntityPlayer;
import Reika.DragonAPI.Base.CoreContainer;


public class ContainerPatternCache extends CoreContainer {

	private final TileEntityPatternCache cache;

	public ContainerPatternCache(EntityPlayer player, TileEntityPatternCache te) {
		super(player, te);

		cache = te;

		for (int i = 0; i < cache.SIZE; i++) {
			int x = i == 0 ? 0 : -500;
			int y = 0;
			this.addSlot(i, x, y);
		}

		this.addPlayerInventory(player);
	}

}
