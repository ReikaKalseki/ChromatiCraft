/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.API.Event;

import java.util.UUID;

import net.minecraft.world.World;

import cpw.mods.fml.common.eventhandler.Event;

public class LumenWireToggleEvent extends Event {

	public final World world;
	public final int x;
	public final int y;
	public final int z;

	public final UUID connectionID;
	public final boolean isEnabled;

	public LumenWireToggleEvent(World world, int x, int y, int z, UUID uid, boolean on) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		connectionID = uid;
		isEnabled = on;
	}

}
