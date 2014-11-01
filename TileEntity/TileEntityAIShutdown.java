/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;

public class TileEntityAIShutdown extends TileEntityChromaticBase {

	private static final Collection<EntityLiving> stopped = new ArrayList();
	private List<EntityLiving> local = new ArrayList();

	private final StepTimer resyncTimer = new StepTimer(100);

	//private ForgeDirection facing;

	public ForgeDirection getFacing() {
		switch(this.getBlockMetadata()) {
		case 0:
			return ForgeDirection.WEST;
		case 1:
			return ForgeDirection.EAST;
		case 2:
			return ForgeDirection.NORTH;
		case 3:
			return ForgeDirection.SOUTH;
		case 4:
			return ForgeDirection.UP;
		case 5:
			return ForgeDirection.DOWN;
		default:
			return ForgeDirection.UNKNOWN;
		}
	}
	/*
	public void incrementFacing() {
		int o = facing.ordinal()+1;
		if (o >= 6)
			o = 0;
		facing = dirs[o];
	}

	public void setFacing(ForgeDirection dir) {
		facing = dir;
	}
	 */
	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.AISHUTDOWN;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		resyncTimer.update();
		if (resyncTimer.checkCap()) {
			stopped.removeAll(local);
			AxisAlignedBB box = this.getBox(world, x, y, z);
			local = world.getEntitiesWithinAABB(EntityLiving.class, box);
			//stopped.addAll(local);
		}
	}

	private AxisAlignedBB getBox(World world, int x, int y, int z) {
		int r = 8;
		int dx = (r+1)*this.getFacing().offsetX;
		int dy = (r+1)*this.getFacing().offsetY;
		int dz = (r+1)*this.getFacing().offsetZ;
		return ReikaAABBHelper.getBlockAABB(x, y, z).expand(r, r, r).offset(dx, dy, dz);
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	public void freeAll() {
		stopped.removeAll(local);
	}

	public static boolean stopUpdate(Entity e) {
		return e instanceof EntityLiving && stopped.contains(e);
	}

}
