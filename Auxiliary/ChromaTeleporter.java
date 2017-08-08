/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;

public class ChromaTeleporter extends Teleporter {

	private final World world;

	private ChromaTeleporter() {
		this(ExtraChromaIDs.DIMID.getValue());
	}

	public ChromaTeleporter(int dim) {
		this(MinecraftServer.getServer().worldServerForDimension(dim));
	}

	private ChromaTeleporter(WorldServer world) {
		super(world);
		this.world = world;
	}

	@Override
	public void placeInPortal(Entity e, double x, double y, double z, float facing) {
		e.setLocationAndAngles(0, 1024, 0, 0, 0);
		if (world.provider.dimensionId == 0) {
			ChunkCoordinates p = world.getSpawnPoint();
			if (e instanceof EntityPlayer)
				p = ((EntityPlayer)e).getBedLocation(0);
			if (p == null)
				p = world.getSpawnPoint();
			e.setLocationAndAngles(p.posX, 1024, p.posZ, 0, 0);
		}
		this.placeInExistingPortal(e, x, y, z, facing);
	}

	@Override
	public boolean placeInExistingPortal(Entity entity, double x, double y, double z, float facing) {
		return true;
	}

	private void makeReturnPortal(World world, int x, int y, int z) {

	}

	@Override
	public boolean makePortal(Entity e) {
		return false;
	}

}
