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
import Reika.ChromatiCraft.API.CrystalElementProxy;
import Reika.DragonAPI.Instantiable.Event.WorldGenEvent;

/** Fired when a crystal is generated. */
public class CrystalGenEvent extends WorldGenEvent {

	/** Crystal color */
	public final CrystalElementProxy color;

	public CrystalGenEvent(World world, int x, int y, int z, Random random, int meta) {
		super(world, x, y, z, random);
		color = CrystalElementProxy.list[meta%16];
	}

}
