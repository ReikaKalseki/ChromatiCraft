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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import Reika.DragonAPI.Base.InertEntity;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;


public class EntityParticleCluster extends InertEntity implements IEntityAdditionalSpawnData {

	public static final int MIN_MOVEMENT_DELAY = 20;
	public static final int MAX_MOVEMENT_DELAY = 120;
	public static final int MIN_MOVEMENT_TIME = 0;
	public static final int MAX_MOVEMENT_TIME = 20;

	private static final double MOVEMENT_DISTANCE = 2;

	private static final int MIN_PARTICLES = 7;
	private static final int MAX_PARTICLES = 20;

	private int ticksUntilMove;
	private int movingParticles;

	private final Collection<Particle> particles;

	public EntityParticleCluster(World world, double x, double y, double z) {
		super(world);
		particles = new ArrayList();
		int n = ReikaRandomHelper.getRandomPlusMinus(MIN_PARTICLES, MAX_PARTICLES);
		for (int i = 0; i < n; i++) {
			double dx = ReikaRandomHelper.getRandomPlusMinus(x, 1);
			double dy = ReikaRandomHelper.getRandomPlusMinus(y, 1);
			double dz = ReikaRandomHelper.getRandomPlusMinus(z, 1);
			particles.add(new Particle(dx, dy, dz));
		}
		this.setPosition(x, y, z);
		ReikaJavaLibrary.pConsole(particles.size()+":"+this);
	}

	@Override
	public void setPosition(double x, double y, double z) {
		/*
		if (particles != null) {
			for (Particle p : particles) {
				p.posX += (x-posX);
				p.posY += (y-posY);
				p.posZ += (z-posZ);
				p.lastPosX += (x-posX);
				p.lastPosY += (y-posY);
				p.lastPosZ += (z-posZ);
			}
		}
		 */
		super.setPosition(x, y, z);
	}

	public EntityParticleCluster(World world) {
		super(world);
		particles = new ArrayList();
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

	@Override
	public void onUpdate() {
		super.onUpdate();

		for (Particle p : particles) {
			p.update(this);
		}

		if (ticksUntilMove > 0) {
			if (movingParticles == 0) {
				ticksUntilMove--;
			}
		}
		if (ticksUntilMove == 0) {
			this.move();
		}
	}

	private void move() {
		int deg = 30+rand.nextInt(6)*60;
		double dx = MOVEMENT_DISTANCE*Math.cos(Math.toRadians(deg));
		double dz = MOVEMENT_DISTANCE*Math.sin(Math.toRadians(deg));
		for (Particle p : particles) {
			p.startMoving(this, dx, dz);
		}
		movingParticles = particles.size();
		ticksUntilMove = ReikaRandomHelper.getRandomBetween(MIN_MOVEMENT_DELAY, MAX_MOVEMENT_DELAY);
	}

	public Collection<Particle> getParticles() {
		return Collections.unmodifiableCollection(particles);
	}

	@Override
	public void writeSpawnData(ByteBuf buf) {
		buf.writeInt(particles.size());
		for (Particle p : particles) {
			p.writeToBuf(buf);
		}
	}

	@Override
	public void readSpawnData(ByteBuf buf) {
		int n = buf.readInt();
		for (int i = 0; i < n; i++) {
			Particle p = Particle.createFromBuf(buf);
			particles.add(p);
		}
	}

	public static class Particle {

		private double posX;
		private double posY;
		private double posZ;

		private double lastPosX;
		private double lastPosY;
		private double lastPosZ;

		private int ticksUntilMove;
		private int moveProgress;

		private double moveDistanceX;
		private double moveDistanceZ;

		private static final double[] MOTION = {0, 0.03125, 0.125, 0.25, 0.5, 0.75, 0.875, 0.9375, 1};

		private Particle(double x, double y, double z) {
			posX = lastPosX = x;
			posY = lastPosY = y;
			posZ = lastPosZ = z;
			moveProgress = -1;
			ticksUntilMove = -1;
		}

		public void writeToBuf(ByteBuf buf) {
			buf.writeDouble(posX);
			buf.writeDouble(posY);
			buf.writeDouble(posZ);
		}

		public static Particle createFromBuf(ByteBuf buf) {
			double x = buf.readDouble();
			double y = buf.readDouble();
			double z = buf.readDouble();
			return new Particle(x, y, z);
		}

		private void update(EntityParticleCluster parent) {
			if (ticksUntilMove > 0) {
				ticksUntilMove--;
			}
			else {
				if (moveProgress >= 0) {
					posX = lastPosX+moveDistanceX*MOTION[moveProgress];
					posZ = lastPosZ+moveDistanceZ*MOTION[moveProgress];
					moveProgress++;
					if (moveProgress == MOTION.length-1) {
						this.finishMoving(parent);
					}
				}
			}
		}

		private void startMoving(EntityParticleCluster parent, double dx, double dz) {
			ticksUntilMove = ReikaRandomHelper.getRandomBetween(MIN_MOVEMENT_TIME, MAX_MOVEMENT_TIME);
			moveDistanceX = dx;
			moveDistanceZ = dz;
			moveProgress = 0;
		}

		private void finishMoving(EntityParticleCluster parent) {
			lastPosX += moveDistanceX;
			lastPosZ += moveDistanceZ;
			moveProgress = -1;
			parent.movingParticles--;
			if (parent.movingParticles == 0) {
				parent.posX += moveDistanceX;
				parent.posZ += moveDistanceZ;
				parent.lastTickPosX = parent.posX;
				parent.lastTickPosY = parent.posY;
				parent.lastTickPosZ = parent.posZ;
			}
		}

		public double getRenderPosX(float ptick) {
			if (moveProgress == -1)
				return posX;
			double avg = ReikaMathLibrary.getUnequalAverage(MOTION[moveProgress], MOTION[moveProgress+1], ptick);
			return lastPosX+moveDistanceX*avg;
		}

		public double getRenderPosZ(float ptick) {
			if (moveProgress == -1)
				return posZ;
			double avg = ReikaMathLibrary.getUnequalAverage(MOTION[moveProgress], MOTION[moveProgress+1], ptick);
			return lastPosZ+moveDistanceZ*avg;
		}

	}

}
