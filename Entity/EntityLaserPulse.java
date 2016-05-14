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
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Block.Dimension.Structure.BlockLaserEffector.ColorData;
import Reika.ChromatiCraft.Block.Dimension.Structure.BlockLaserEffector.LaserEffectType;
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


public class EntityLaserPulse extends ParticleEntity implements IEntityAdditionalSpawnData {

	public ColorData color;
	public CubeDirections direction;
	//private String level = "";

	public EntityLaserPulse(World world) {
		super(world);
	}

	public EntityLaserPulse(World world, int x, int y, int z, CubeDirections dir, ColorData c, String l) {
		super(world, x, y, z, dir);
		direction = dir;
		color = c.copy();
		//level = l;
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
		int l = 10+rand.nextInt(15);
		double[] r = {0.1875, 0.125, 0.0625};
		for (int i = 0; i < r.length; i++) {
			float s = (1+rand.nextFloat())/(i+1);
			double px = ReikaRandomHelper.getRandomPlusMinus(posX, r[i]);
			double py = ReikaRandomHelper.getRandomPlusMinus(posY, r[i]);
			double pz = ReikaRandomHelper.getRandomPlusMinus(posZ, r[i]);
			EntityFX fx = new EntityBlurFX(worldObj, px, py, pz).setColor(color.getRenderColor()).setIcon(ChromaIcons.FADE_GENTLE).setLife(l).setScale(s);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
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
			s.playSound(worldObj, posX, posY, posZ, vol, p);
		}
		if (color.blue) {
			s.playSound(worldObj, posX, posY, posZ, vol, p*2F);
		}
	}

	@SideOnly(Side.CLIENT)
	private void spawnDeathParticle() {
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

		if (worldObj.isRemote) {
			this.spawnDeathParticle();
		}
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
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tag) {
		super.writeEntityToNBT(tag);
		color.writeToNBT(tag);
		tag.setInteger("dir", direction.ordinal());
	}

	@Override
	public void writeSpawnData(ByteBuf data) {
		super.writeSpawnData(data);
		color.writeBuf(data);
		data.writeInt(direction.ordinal());
		//ReikaPacketHelper.writeString(data, level);
	}

	@Override
	public void readSpawnData(ByteBuf data) {
		super.readSpawnData(data);
		color = new ColorData(true);
		color.readBuf(data);
		direction = CubeDirections.list[data.readInt()];
		//level = ReikaPacketHelper.readString(data);
	}

	@Override
	public boolean despawnOverTime() {
		return false;
	}

	@Override
	public double getSpeed() {
		return 0.1875;
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
