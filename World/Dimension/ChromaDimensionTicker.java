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

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickHandler;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickType;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;

public class ChromaDimensionTicker implements TickHandler {

	public static final ChromaDimensionTicker instance = new ChromaDimensionTicker();

	public final int dimID = ExtraChromaIDs.DIMID.getValue();
	private final Collection<Ticket> tickets = new ArrayList();

	private ChromaDimensionTicker() {

	}

	@Override
	public void tick(TickType type, Object... tickData) {
		World world = (World)tickData[0];
		if (world.provider.dimensionId == dimID) {
			world.ambientTickCountdown = Integer.MAX_VALUE;

			for (Ticket t : tickets) {
				for (ChunkCoordIntPair p : t.getChunkList()) {
					ForgeChunkManager.unforceChunk(t, p);
				}
			}
			tickets.clear();
		}
	}

	public void scheduleTicketUnload(Ticket t) {
		tickets.add(t);
	}

	@Override
	public TickType getType() {
		return TickType.WORLD;
	}

	@Override
	public boolean canFire(Phase p) {
		return p == Phase.END;
	}

	@Override
	public String getLabel() {
		return "Chroma Dimension Tag";
	}

}
