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

import io.netty.buffer.ByteBuf;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.World.Dimension.SkyRiverGenerator.RiverPoint;
import Reika.DragonAPI.Base.InertEntity;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;


public class EntitySkyRiver extends InertEntity implements IEntityAdditionalSpawnData {

	private boolean respawn = true;
	private RiverPoint point;

	public EntitySkyRiver(World par1World) {
		super(par1World);

		this.setSize(2, 2);
	}

	public EntitySkyRiver(World world, RiverPoint p) {
		this(world);

		point = p;
	}

	@Override
	protected void entityInit() {

	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (!worldObj.isRemote && respawn) {
			EntitySkyRiver e = new EntitySkyRiver(worldObj, point);
			e.respawn = false;
			worldObj.spawnEntityInWorld(e);
			this.setDead();
		}

		if (point == null)
			this.setDead();
		else
			this.setPosition(point.position.xCoord, point.position.yCoord, point.position.zCoord);

		AxisAlignedBB box = ReikaAABBHelper.getEntityCenteredAABB(this, 16);
		double dx = point.next.xCoord-point.position.xCoord;
		double dy = point.next.yCoord-point.position.yCoord;
		double dz = point.next.zCoord-point.position.zCoord;
		double dd = ReikaMathLibrary.py3d(dx, dy, dz);
		double v = 0.95;
		List<EntityLivingBase> li = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, box);
		for (EntityLivingBase e : li) {
			e.motionX = v*dx/dd;
			e.motionY = v*dy/dd;
			e.motionZ = v*dz/dd;
			e.velocityChanged = true;
		}

		if (worldObj.isRemote) {
			EntityBlurFX fx = new EntityBlurFX(worldObj, posX, posY, posZ, ReikaRandomHelper.getRandomPlusMinus(0, 0.125), ReikaRandomHelper.getRandomPlusMinus(0, 0.125), ReikaRandomHelper.getRandomPlusMinus(0, 0.125));
			fx.setScale(5);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tag) {
		point = RiverPoint.readFromNBT(tag);

		respawn = tag.getBoolean("respawn");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tag) {
		point.writeToNBT(tag);

		tag.setBoolean("respawn", respawn);
	}

	@Override
	public void writeSpawnData(ByteBuf buf) {
		point.writeToBuf(buf);
	}

	@Override
	public void readSpawnData(ByteBuf buf) {
		point = RiverPoint.readFromBuf(buf);
	}

}
