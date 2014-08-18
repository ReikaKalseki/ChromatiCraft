/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic;

import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.WorldLocation;

import net.minecraft.world.World;

public interface CrystalNetworkTile {

	public boolean isConductingElement(CrystalElement e);

	public int getSendRange();

	public void cachePosition();

	public void removeFromCache();

	public double getDistanceSqTo(double x, double y, double z);

	public World getWorld();

	public int getX();

	public int getY();

	public int getZ();

	public int maxThroughput();

	public boolean canConduct();

	public void addTarget(WorldLocation loc, CrystalElement e);

	public void removeTarget(WorldLocation loc, CrystalElement e);

	//public void markSource(WorldLocation loc);

}
