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

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Block.Dimension.Structure.Pinball.BlockPinballTile.PinballRerouteType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.Base.ParticleEntity;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;

import io.netty.buffer.ByteBuf;


public class EntityTNTPinball extends ParticleEntity {

	private ForgeDirection direction;
	private double speed = 0.125;

	public EntityTNTPinball(World world) {
		super(world);
	}

	public EntityTNTPinball(World world, int x, int y, int z, ForgeDirection dir, double speed) {
		super(world, x, y, z, dir);
		direction = dir;
		this.speed = speed;
		this.setDirection(dir, true);
	}

	@Override
	protected void entityInit() {
		super.entityInit();

		dataWatcher.addObject(24, 0F);
	}

	@Override
	public double getHitboxSize() {
		return 0.4;
	}

	@Override
	protected double getBlockThreshold() {
		return 0.125;
	}

	@Override
	public boolean despawnOverTime() {
		return false;
	}

	@Override
	public boolean despawnOverDistance() {
		return true;
	}

	@Override
	protected double getDespawnDistance() {
		return 40;
	}

	@Override
	public boolean canInteractWithSpawnLocation() {
		return false;
	}

	@Override
	protected void onTick() {
		if (!worldObj.isRemote) {
			dataWatcher.updateObject(24, (float)speed);
		}
		else {
			speed = dataWatcher.getWatchableObjectFloat(24);
		}
	}

	@Override
	public double getSpeed() {
		return speed;
	}

	@Override
	protected boolean onEnterBlock(World world, int x, int y, int z) {
		Block b = world.getBlock(x, y, z);
		if (b.isAir(world, x, y, z))
			return false;
		if (b == ChromaBlocks.PINBALL.getBlockInstance()) {
			int meta = world.getBlockMetadata(x, y, z);
			PinballRerouteType e = PinballRerouteType.list[meta];
			return e.affectPulse(world, x, y, z, this);
		}
		return true;
	}

	@Override
	public void applyEntityCollision(Entity e) {

	}

	@Override
	public void setDirection(ForgeDirection dir, boolean setPos) {
		super.setDirection(dir, setPos);
		direction = dir;
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tag) {
		super.readEntityFromNBT(tag);
		direction = ForgeDirection.VALID_DIRECTIONS[tag.getInteger("dir")];
		speed = tag.getDouble("speed");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tag) {
		super.writeEntityToNBT(tag);
		tag.setInteger("dir", direction.ordinal());
		tag.setDouble("speed", speed);
	}

	@Override
	public void writeSpawnData(ByteBuf data) {
		super.writeSpawnData(data);
		data.writeInt(direction.ordinal());
		data.writeDouble(speed);
		//ReikaPacketHelper.writeString(data, level);
	}

	@Override
	public void readSpawnData(ByteBuf data) {
		super.readSpawnData(data);
		direction = ForgeDirection.VALID_DIRECTIONS[data.readInt()];
		speed = data.readDouble();
		//level = ReikaPacketHelper.readString(data);
	}

	@Override
	protected boolean needsSpeedUpdates() {
		return true;
	}

	@Override
	protected void updateSpeed() {
		if (speed > 0.125) {
			speed = Math.max(0.125, speed*0.975);
		}
		this.setDirection(direction, false);
		velocityChanged = true;
	}

	public ForgeDirection getDirection() {
		return direction;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public void bounce(boolean state) {
		ForgeDirection dir = state ? ReikaDirectionHelper.getRightBy90(direction) : ReikaDirectionHelper.getLeftBy90(direction);
		this.setDirection(dir, true);
	}

	@Override
	public double getRenderRangeSquared() {
		return 16384D;
	}

}
