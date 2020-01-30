/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2018
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Entity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

import Reika.DragonAPI.Base.ParticleEntity;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Math.Spline;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;


public class EntitySplineProjectile extends ParticleEntity implements IEntityAdditionalSpawnData {

	private int pathTick = 0;
	private boolean hitTarget;

	private final List<DecimalPosition> path;

	public EntitySplineProjectile(World world, Spline s, int fineness) {
		super(world, 0, 0, 0);
		path = s.get(fineness, false);
		DecimalPosition pos = path.get(0);
		this.spawnAt(pos.getCoordinate());
		this.setPosition(pos);
		//ReikaJavaLibrary.pConsole(path.size());
	}

	public EntitySplineProjectile(World world) {
		super(world);
		path = new ArrayList();
	}

	public Coordinate getFinalPathBlock() {
		return path == null || path.isEmpty() ? null : path.get(path.size()-1).getCoordinate();
	}

	@Override
	protected void entityInit() {
		//dataWatcher.addObject(27, 0F);
		//dataWatcher.addObject(28, 0F);
		//dataWatcher.addObject(29, 0F);
	}

	@Override
	public void writeSpawnData(ByteBuf buf) {
		super.writeSpawnData(buf);

		buf.writeInt(path.size());
		for (DecimalPosition p : path) {
			p.writeToBuf(buf);
		}
	}

	@Override
	public void readSpawnData(ByteBuf buf) {
		super.readSpawnData(buf);

		path.clear();
		int n = buf.readInt();
		for (int i = 0; i < n; i++) {
			DecimalPosition p = DecimalPosition.readFromBuf(buf);
			path.add(p);
		}
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tag) {
		super.readEntityFromNBT(tag);

		path.clear();
		NBTTagList li = tag.getTagList("path", NBTTypes.COMPOUND.ID);
		for (Object o : li.tagList) {
			DecimalPosition p = DecimalPosition.readTag((NBTTagCompound)o);
			path.add(p);
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tag) {
		super.writeEntityToNBT(tag);

		NBTTagList li = new NBTTagList();
		for (DecimalPosition p : path) {
			li.appendTag(p.writeToTag());
		}
		tag.setTag("path", li);
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 1;
	}

	@Override
	protected void onTick() {
		//if (!worldObj.isRemote) {
		if (path != null) {
			if (hitTarget) {
				this.setDead();
			}
			else {
				DecimalPosition p = path.get(pathTick);
				this.setPosition(p);
				pathTick = Math.min(pathTick+1, path.size()-1);
			}
			velocityChanged = true;

			//dataWatcher.updateObject(27, (float)posX);
			//dataWatcher.updateObject(28, (float)posY);
			//dataWatcher.updateObject(29, (float)posZ);
		}
		else {
			ReikaJavaLibrary.pConsole("Clearing pulse, no path");
			this.setDead();
		}/*
		}
		else {
			posX = dataWatcher.getWatchableObjectFloat(27);
			posY = dataWatcher.getWatchableObjectFloat(28);
			posZ = dataWatcher.getWatchableObjectFloat(29);
		}*/
	}

	private void setPosition(DecimalPosition p) {
		double v = 0.0625;
		motionX = (p.xCoord-posX)*v;
		motionY = (p.yCoord-posY)*v;
		motionZ = (p.zCoord-posZ)*v;
		posX = p.xCoord;
		posY = p.yCoord;
		posZ = p.zCoord;

		p = pathTick == 0 ? path.get(0) : path.get(pathTick-1);
		lastTickPosX = p.xCoord;
		lastTickPosY = p.yCoord;
		lastTickPosZ = p.zCoord;
		prevPosX = p.xCoord;
		prevPosY = p.yCoord;
		prevPosZ = p.zCoord;
	}

	public float getRenderSize() {
		return 0.125F;
	}

	@Override
	public double getRenderRangeSquared() {
		return 4096;
	}

	@Override
	public double getHitboxSize() {
		return 0.125;
	}

	@Override
	public boolean despawnOverTime() {
		return false;
	}

	@Override
	public boolean despawnOverDistance() {
		return false;
	}

	@Override
	public boolean canInteractWithSpawnLocation() {
		return false;
	}

	@Override
	public double getSpeed() {
		return 0;
	}

	@Override
	protected boolean dieOnNoVelocity() {
		return false;
	}

	@Override
	protected boolean onEnterBlock(World world, int x, int y, int z) {
		return false;
	}

	@Override
	public void applyEntityCollision(Entity e) {

	}
}
