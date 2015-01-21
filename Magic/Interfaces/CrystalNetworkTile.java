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

import net.minecraft.world.World;
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

	public int maxThroughput();

	public boolean canConduct();

	public String getUniqueID();

}
