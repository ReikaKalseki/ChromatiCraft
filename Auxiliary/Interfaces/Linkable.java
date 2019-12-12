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

import net.minecraft.world.World;

import Reika.DragonAPI.Interfaces.TileEntity.BreakAction;

public interface Linkable extends BreakAction {

	public void reset();

	public void resetOther();

	public boolean connectTo(World world, int x, int y, int z);

}
