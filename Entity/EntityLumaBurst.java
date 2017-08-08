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

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Block.Dimension.Structure.Gravity.BlockGravityTile.GravityTiles;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.Base.ParticleEntity;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper.CubeDirections;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class EntityLumaBurst extends ParticleEntity {

	private CrystalElement color;
	private boolean outOfSpawnZone;
	private boolean outOfSpawnZoneLast;

	public EntityLumaBurst(World world) {
		super(world);
	}

	public EntityLumaBurst(World world, int x, int y, int z, CubeDirections dir, CrystalElement e) {
		super(world, x, y, z, dir);
		this.setColor(e);
	}

	public EntityLumaBurst(World world, int x, int y, int z, double ang, CrystalElement e) {
		super(world, x, y, z);
		this.setColor(e);
		this.setAngle(ang);
	}

	@Override
	protected void entityInit() {
		dataWatcher.addObject(24, 0);
	}

	public void copyFrom(EntityLumaBurst e) {
		this.setColor(e.color);
		motionX = e.motionX;
		motionY = e.motionY;
		motionZ = e.motionZ;
	}

	public void setColor(CrystalElement e) {
		color = e;
		dataWatcher.updateObject(24, e.ordinal());
	}

	@Override
	protected void setDirection(CubeDirections dir, boolean setPos) {
		if (setPos)
			super.setDirection(dir, setPos);

		double d = 10;//0.03125/4;
		double a = ReikaRandomHelper.getRandomPlusMinus(dir.angle, d);
		this.setAngle(a);
	}

	private void setAngle(double a) {
		double[] vel = ReikaPhysicsHelper.polarToCartesian(this.getSpeed(), 0, -a);
		motionX = vel[0];//ReikaRandomHelper.getRandomPlusMinus(motionX, d);
		motionZ = vel[2];//ReikaRandomHelper.getRandomPlusMinus(motionZ, d);
		motionY = vel[1];//ReikaRandomHelper.getRandomPlusMinus(motionY, d/4D);
	}

	public void resetSpawnTimer() {
		if (!outOfSpawnZoneLast) {
			outOfSpawnZone = false;
		}
	}

	public boolean isOutOfSpawnZone() {
		return outOfSpawnZone;
	}

	@Override
	public double getHitboxSize() {
		return 0.0625;
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
		return 25;
	}

	@Override
	public boolean canInteractWithSpawnLocation() {
		return false;
	}

	@Override
	protected void onTick() {
		if (this.getSpawnLocation() != null) {
			double dy = posY-this.getSpawnLocation().yCoord+0.5;
			if (Math.abs(dy) >= 0.4 && Math.signum(motionY) == Math.signum(dy)) {
				motionY = -motionY;
			}
		}
		if (worldObj.isRemote) {
			this.doParticles();
		}
		if (outOfSpawnZone)
			outOfSpawnZoneLast = true;
		outOfSpawnZone = true;
		color = CrystalElement.elements[dataWatcher.getWatchableObjectInt(24)];
	}

	@SideOnly(Side.CLIENT)
	private void doParticles() {
		for (double d = 0; d <= 1; d += 0.25) {
			double px = posX-(posX-lastTickPosX)*d;
			double py = posY-(posY-lastTickPosY)*d;
			double pz = posZ-(posZ-lastTickPosZ)*d;
			int c = ReikaColorAPI.mixColors(color.getColor(), 0x000000, 0.5F);
			EntityBlurFX fx = new EntityBlurFX(worldObj, px, py, pz).setColor(c).setAlphaFading().setScale(0.5F).setLife(4).setAge((int)Math.round(d));
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@Override
	public double getSpeed() {
		return ReikaRandomHelper.getRandomPlusMinus(0.15, 0.025);
	}

	@Override
	protected boolean onEnterBlock(World world, int x, int y, int z) {
		Block b = world.getBlock(x, y, z);
		if (b.isAir(world, x, y, z))
			return false;
		if (b == ChromaBlocks.GRAVITY.getBlockInstance()) {
			int meta = world.getBlockMetadata(x, y, z);
			GravityTiles g = GravityTiles.list[meta];
			return g.onPulse(world, x, y, z, this);
		}
		return true;
	}

	@Override
	public void applyEntityCollision(Entity e) {

	}

	@Override
	public void writeSpawnData(ByteBuf data) {
		super.writeSpawnData(data);

		data.writeInt(color.ordinal());
	}

	@Override
	public void readSpawnData(ByteBuf data) {
		super.readSpawnData(data);

		color = CrystalElement.elements[data.readInt()];
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tag) {
		super.readEntityFromNBT(tag);

		color = CrystalElement.elements[tag.getInteger("color")];
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tag) {
		super.writeEntityToNBT(tag);

		tag.setInteger("color", color.ordinal());
	}

	public CrystalElement getColor() {
		return color;
	}

	@Override
	public double getRenderRangeSquared() {
		return 4096D;
	}

	public void setRandomDirection(boolean fullFreedom) {
		if (fullFreedom) {
			double ang = rand.nextDouble()*360;
			double slope = ReikaRandomHelper.getRandomPlusMinus(0, 5);
			double[] vel = ReikaPhysicsHelper.polarToCartesian(this.getSpeed(), slope, ang);
			motionX = vel[0];
			motionY = vel[1];
			motionZ = vel[2];
			velocityChanged = true;
		}
		else {
			this.setDirection(CubeDirections.list[rand.nextInt(CubeDirections.list.length)], false);
		}
	}

}
