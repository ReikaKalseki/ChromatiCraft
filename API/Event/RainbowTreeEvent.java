/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.API.Event;

import java.util.Random;

import net.minecraft.world.World;
import Reika.DragonAPI.Instantiable.Event.WorldGenEvent;


/** Fired when a rainbow tree is generated. */
public class RainbowTreeEvent extends WorldGenEvent {

	public RainbowTreeEvent(World world, int x, int y, int z, Random r) {
		super(world, x, y, z, r);
	}

}
