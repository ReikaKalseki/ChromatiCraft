/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension;

import net.minecraft.world.World;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickHandler;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickType;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;

public class ChromaDimensionTicker implements TickHandler {

	public static final ChromaDimensionTicker instance = new ChromaDimensionTicker();

	public final int dimID = ExtraChromaIDs.DIMID.getValue();

	private ChromaDimensionTicker() {

	}

	@Override
	public void tick(TickType type, Object... tickData) {
		World world = (World)tickData[0];
		if (world.provider.dimensionId == dimID) {
			world.ambientTickCountdown = Integer.MAX_VALUE;
		}
	}

	@Override
	public TickType getType() {
		return TickType.WORLD;
	}

	@Override
	public boolean canFire(Phase p) {
		return p == Phase.START;
	}

	@Override
	public String getLabel() {
		return "Chroma Dimension Tag";
	}

}
