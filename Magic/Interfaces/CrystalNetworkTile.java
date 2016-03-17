/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic.Interfaces;

import java.util.UUID;

import net.minecraft.world.World;
import Reika.ChromatiCraft.Registry.ChromaResearchManager.ResearchLevel;
import Reika.ChromatiCraft.Registry.CrystalElement;

public interface CrystalNetworkTile {

	public boolean isConductingElement(CrystalElement e);

	public void cachePosition();

	public void removeFromCache();

	public double getDistanceSqTo(double x, double y, double z);

	public World getWorld();

	public int getX();

	public int getY();

	public int getZ();

	/** Max per-tick flow. Called on connection for repeaters, called every tick for sources, to let them throttle as they drain. */
	public int maxThroughput();

	public boolean canConduct();

	public UUID getUniqueID();

	public UUID getPlacerUUID();

	public ResearchLevel getResearchTier();

}
