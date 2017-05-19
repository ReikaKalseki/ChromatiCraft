/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.Interfaces;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;


public interface LinkedTile {

	void onLink(boolean other);

	void syncAllData(boolean fullNBT);

	void setTarget(WorldLocation loc);

	void assignLinkID(LinkedTile other);

	void markForDrop();

	boolean isPrimary();

	void setPrimary(boolean primary);

	void setPlacer(EntityPlayer ep);

	boolean linkTo(WorldLocation loc);

	boolean linkTo(World world, int x, int y, int z);

	void reset();

}
