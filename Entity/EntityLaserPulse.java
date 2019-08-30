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
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.HoldingChecks;
import Reika.ChromatiCraft.Block.Dimension.Structure.Laser.BlockLaserEffector.ColorData;
import Reika.ChromatiCraft.Block.Dimension.Structure.Laser.BlockLaserEffector.LaserEffectType;
import Reika.ChromatiCraft.Block.Dimension.Structure.PistonTape.BlockPistonTapeBit;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.Base.ParticleEntity;
import Reika.DragonAPI.Interfaces.Registry.SoundEnum;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper.CubeDirections;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;


public class EntityLaserPulse extends ParticleEntity implements IEntityAdditionalSpawnData {

	public ColorData color;
	public CubeDirections direction;
	//private String level = "";

	public boolean silentImpact = false;
	private double moveSpeed = 0.1875;

	public EntityLaserPulse(World world) {
		super(world);
	}

	public EntityLaserPulse(World world, int x, int y, int z, CubeDirections dir, ColorData c, String l) {
		super(world, x, y, z, dir);
		direction = dir;
		color = c.copy();
		//level = l;
	}

	public void setSpeedFactor(double f) {
		moveSpeed *= f;
		moveSpeed = Math.min(moveSpeed, 0.2); //any faster and it might clip
		this.setDirection(direction, true);
	}

	@Override
	protected void entityInit() {
		super.entityInit();

		dataWatcher.addObject(24, 0);
		dataWatcher.addObject(25, 0);
		dataWatcher.addObject(26, 0);
	}

	@Override
	protected double getBlockThreshold() {
		return 0.125;
	}

	@Override
	protected double getDespawnDistance() {
		return 40;
	}

	@Override
	protected void onTick() {
		if (!worldObj.isRemote) {
			dataWatcher.updateObject(24, color.red ? 1 : 0);
			dataWatcher.updateObject(25, color.green ? 1 : 0);
			dataWatcher.updateObject(26, color.blue ? 1 : 0);
		}
		else {
			color.red = dataWatcher.getWatchableObjectInt(24) > 0;
			color.green = dataWatcher.getWatchableObjectInt(25) > 0;
			color.blue = dataWatcher.getWatchableObjectInt(26) > 0;
			this.spawnParticle();
		}
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 1;
	}

	@SideOnly(Side.CLIENT)
	private void spawnParticle() {
		Minecraft mc = Minecraft.getMinecraft();
		int l = 10+rand.nextInt(15);
		if (rand.nextInt(HoldingChecks.MANIPULATOR.isClientHolding() ? 3 : 12) == 0)
			l *= 16;
		double[] r = {0.1875, 0.125, 0.0625};
		for (int i = 0; i < r.length; i++) {
			float s = (1+rand.nextFloat())/(i+1);
			double px = ReikaRandomHelper.getRandomPlusMinus(posX, r[i]);
			double py = ReikaRandomHelper.getRandomPlusMinus(posY, r[i]);
			double pz = ReikaRandomHelper.getRandomPlusMinus(posZ, r[i]);
			EntityFX fx = new EntityBlurFX(worldObj, px, py, pz).setColor(color.getRenderColor()).setIcon(ChromaIcons.FADE_GENTLE).setLife(l).setScale(s);
			mc.effectRenderer.addEffect(fx);
		}
	}

	@Override
	protected void onDeath() {
		if (!worldObj.isRemote) {

		}
	}

	private void playTonalSound(SoundEnum s, float vol, float p) {
		if (color.red) {
			s.playSound(worldObj, posX, posY, posZ, vol, p*0.5F);
		}
		if (color.green) {
			s.playSound(worldObj, posX, posY, posZ, vol, p*0.75F);
		}
		if (color.blue) {
			s.playSound(worldObj, posX, posY, posZ, vol, p*1F);
		}
	}

	@SideOnly(Side.CLIENT)
	private void spawnDeathParticle() {
		if (silentImpact)
			return;
		int l = 10+rand.nextInt(15);
		int n = 8+rand.nextInt(24);
		for (int i = 0; i < n; i++) {
			float s = 1+rand.nextFloat();
			double px = ReikaRandomHelper.getRandomPlusMinus(posX, 0.75);
			double py = ReikaRandomHelper.getRandomPlusMinus(posY, 0.75);
			double pz = ReikaRandomHelper.getRandomPlusMinus(posZ, 0.75);
			EntityFX fx = new EntityBlurFX(worldObj, px, py, pz).setColor(color.getRenderColor()).setIcon(ChromaIcons.FADE_RAY).setLife(l).setScale(s);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@Override
	protected boolean onEnterBlock(World world, int x, int y, int z) {
		Block b = world.getBlock(x, y, z);
		if (b.isAir(world, x, y, z))
			return false;
		if (b == ChromaBlocks.LASEREFFECT.getBlockInstance()) {
			int meta = world.getBlockMetadata(x, y, z);
			LaserEffectType e = LaserEffectType.list[meta];
			this.playTonalSound(ChromaSounds.USE, 0.5F, 2);
			return e.affectPulse(world, x, y, z, this);
		}
		if (b == ChromaBlocks.PISTONBIT.getBlockInstance()) {
			return BlockPistonTapeBit.affectPulse(this, world, x, y, z);
		}
		if (b == ChromaBlocks.SPECIALSHIELD.getBlockInstance() && world.getBlockMetadata(x, y, z) == BlockType.GLASS.metadata)
			return false;

		if (worldObj.isRemote) {
			this.spawnDeathParticle();
		}
		if (!silentImpact)
			this.playTonalSound(ChromaSounds.POWERDOWN, 0.5F, 2);

		return true;
	}

	public void reflect(CubeDirections d) {
		int n = d.isCardinal() || d == direction.getOpposite() ? 2 : 1;
		int dx = direction.directionX+n*d.directionX;
		int dz = direction.directionZ+n*d.directionZ;
		CubeDirections dir = CubeDirections.getFromVectors(dx, dz);
		if (dir == null) {
			ChromatiCraft.logger.logError("Tried to reflect from "+direction+" off of "+d+", vec="+dx+","+dz);
			this.setDead();
			return;
		}
		this.setDirection(dir, true);
	}

	public void refract(boolean clockwise) {
		CubeDirections dir = direction.getRotation(clockwise);
		this.setDirection(dir, true);
	}

	@Override
	public void setDirection(CubeDirections dir, boolean setPos) {
		super.setDirection(dir, setPos);
		direction = dir;
	}

	@Override
	public void applyEntityCollision(Entity e) {

	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tag) {
		super.readEntityFromNBT(tag);
		color = new ColorData(true);
		color.readFromNBT(tag);
		direction = CubeDirections.list[tag.getInteger("dir")];
		silentImpact = tag.getBoolean("silent");
		moveSpeed = tag.getDouble("speed");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tag) {
		super.writeEntityToNBT(tag);
		color.writeToNBT(tag);
		tag.setInteger("dir", direction.ordinal());
		tag.setBoolean("silent", silentImpact);
		tag.setDouble("speed", moveSpeed);
	}

	@Override
	public void writeSpawnData(ByteBuf data) {
		super.writeSpawnData(data);
		color.writeBuf(data);
		data.writeInt(direction.ordinal());
		data.writeBoolean(silentImpact);
		data.writeDouble(moveSpeed);
		//ReikaPacketHelper.writeString(data, level);
	}

	@Override
	public void readSpawnData(ByteBuf data) {
		super.readSpawnData(data);
		color = new ColorData(true);
		color.readBuf(data);
		direction = CubeDirections.list[data.readInt()];
		silentImpact = data.readBoolean();
		moveSpeed = data.readDouble();
		//level = ReikaPacketHelper.readString(data);
	}

	@Override
	public boolean despawnOverTime() {
		return false;
	}

	@Override
	public double getSpeed() {
		return moveSpeed;
	}

	@Override
	public double getHitboxSize() {
		return 0.05;
	}

	@Override
	public boolean canInteractWithSpawnLocation() {
		return false;
	}

	@Override
	public boolean despawnOverDistance() {
		return true;
	}

	public String getLevel() {
		return "";//level;
	}

	@Override
	public double getRenderRangeSquared() {
		return Double.POSITIVE_INFINITY;
	}

}
