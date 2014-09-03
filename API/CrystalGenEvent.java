/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.API;

import java.util.Random;

import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import cpw.mods.fml.common.eventhandler.Event;


public class CrystalGenEvent extends Event {

	public final World world;
	public final int x;
	public final int y;
	public final int z;
	public final ReikaDyeHelper color;
	public final Random rand;

	public CrystalGenEvent(World world, int x, int y, int z, ReikaDyeHelper dye, Random random) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		color = dye;
		rand = random;
	}

	public CrystalGenEvent(World world, int x, int y, int z, int meta, Random random) {
		this(world, x, y, z, ReikaDyeHelper.dyes[meta], random);
	}

}
