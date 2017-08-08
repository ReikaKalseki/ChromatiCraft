/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import Reika.DragonAPI.Base.InertEntity;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

public class EntityNukerBall extends InertEntity {

	private EntityPlayer firingPlayer;

	public EntityNukerBall(World world, EntityPlayer ep, Coordinate c) {
		super(world);
		firingPlayer = ep;

		this.setLocationAndAngles(c.xCoord+0.5, c.yCoord+0.5, c.zCoord+0.5, 0, 0);

		this.setSize(0.125F, 0.125F);
	}

	public EntityNukerBall(World world) {
		super(world);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (!worldObj.isRemote && ticksExisted >= 5) {
			this.breakBlock();
			this.destroy();
		}
	}

	@Override
	public boolean canRenderOnFire() {
		return false;
	}

	private void breakBlock() {
		int x = MathHelper.floor_double(posX);
		int y = MathHelper.floor_double(posY);
		int z = MathHelper.floor_double(posZ);
		ReikaWorldHelper.dropAndDestroyBlockAt(worldObj, x, y, z, firingPlayer, false, true);
	}

	private void destroy() {
		this.setDead();
	}

	@Override
	protected void entityInit() {

	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tag) {

	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tag) {

	}

}
