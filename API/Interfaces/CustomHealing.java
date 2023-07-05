/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.API.Interfaces;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import Reika.ChromatiCraft.API.AdjacencyUpgradeAPI;

/** Supply an instance of this to the {@link AdjacencyUpgradeAPI} to specify custom healing core behavior on your Block or TileEntity. */
public interface CustomHealing extends CustomAdjacencyHandler {

	/** Whether to apply this effect clientside as well as serverside. Usually false. */
	public boolean runOnClient();

	public static interface CustomTileHealing extends CustomHealing {
		/** This will be called once per tick, with 'te' being your TE and 'tier' being the tier of the core, 0-7 inclusive. */
		public void tick(TileEntity te, int tier);
	}

	public static interface CustomBlockHealing extends CustomHealing {
		/** This will be called once per tick, with your block's position and 'tier' being the tier of the core, 0-7 inclusive. */
		public void tick(World world, int x, int y, int z, int tier);
	}

}
