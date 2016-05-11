/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Entity;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import Reika.DragonAPI.Base.ParticleEntity;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper.CubeDirections;


public class EntityTNTPinball extends ParticleEntity {

	public EntityTNTPinball(World world) {
		super(world);
		// TODO Auto-generated constructor stub
	}

	public EntityTNTPinball(World world, int x, int y, int z, CubeDirections dir) {
		super(world, x, y, z, dir);
		// TODO Auto-generated constructor stub
	}

	@Override
	public double getHitboxSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean despawnOverTime() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean despawnOverDistance() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canInteractWithSpawnLocation() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void onTick() {
		// TODO Auto-generated method stub

	}

	@Override
	public double getSpeed() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected boolean onEnterBlock(World world, int x, int y, int z) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void applyEntityCollision(Entity e) {
		// TODO Auto-generated method stub

	}

}
