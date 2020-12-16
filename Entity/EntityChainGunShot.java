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

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.potion.Potion;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityCCBlurFX;
import Reika.DragonAPI.Instantiable.Data.WeightedRandom;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityChainGunShot extends EntityFireball {

	private int impacts = Math.max(2, Math.min(12, rand.nextInt(6)+rand.nextInt(6))); //roughly normal distribution

	private EntityPlayer firingPlayer;

	private int immunityTicks = 0;
	private Entity lastHit = null;

	public EntityChainGunShot(World world, EntityPlayer ep, boolean randomVec) {
		super(world);
		firingPlayer = ep;

		Vec3 vec = ep.getLookVec();
		if (randomVec) {
			vec.xCoord = ReikaRandomHelper.getRandomPlusMinus(0, 1D);
			vec.yCoord = ReikaRandomHelper.getRandomPlusMinus(0, 1D);
			vec.zCoord = ReikaRandomHelper.getRandomPlusMinus(0, 1D);
			vec.normalize();
		}
		double v = 1.5;
		motionX = v*vec.xCoord;
		motionY = v*vec.yCoord;
		motionZ = v*vec.zCoord;

		accelerationX = 0;
		accelerationY = 0;
		accelerationZ = 0;

		this.setSize(0.125F, 0.125F);
	}

	public EntityChainGunShot(World world) {
		super(world);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
	}

	@Override
	public void onUpdate()
	{
		double mx = motionX;
		double my = motionY;
		double mz = motionZ;
		super.onUpdate();
		if (immunityTicks != 5) {
			motionX = mx;
			motionY = my;
			motionZ = mz;
		}

		if (!worldObj.isRemote && (firingPlayer == null || ticksExisted > 200)) {
			this.destroy();
		}

		if (immunityTicks > 0)
			immunityTicks--;

		if (worldObj.isRemote && ticksExisted > 10) {
			this.spawnTraceParticle();
		}
	}

	@SideOnly(Side.CLIENT)
	private void spawnTraceParticle() {
		double dd = 0.25;
		if (Minecraft.getMinecraft().gameSettings.particleSetting > 0)
			dd = 0.5;
		if (Minecraft.getMinecraft().gameSettings.particleSetting > 1)
			dd = 1;
		for (double d = 0; d < 1; d += dd) {
			double px = posX+motionX*d;
			double py = posY+motionY*d;
			double pz = posZ+motionZ*d;
			int l = 20+rand.nextInt(80);
			float s = (float)ReikaRandomHelper.getRandomPlusMinus(6D, 1D);
			EntityFX fx = new EntityCCBlurFX(worldObj, px, py, pz, 0, 0, 0).setRapidExpand().setLife(l).setScale(s).setColor(0, 192, 255);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@Override
	protected void onImpact(MovingObjectPosition mov) {
		if (firingPlayer == null && !worldObj.isRemote) {
			this.destroy();
			return;
		}
		if (!worldObj.isRemote) {
			if (mov != null && mov.typeOfHit == MovingObjectType.ENTITY) {
				Entity e = mov.entityHit;
				if (e != firingPlayer && e != lastHit && e instanceof EntityLivingBase && !e.isDead && ((EntityLivingBase)e).getHealth() > 0) {
					if (impacts > 0) {
						impacts--;
						lastHit = e;
						this.findAndRedirect(e);
						ChromaSounds.CAST.playSound(this, 2, 2);
						ChromaSounds.CAST.playSound(firingPlayer, 0.5F, 2);
					}
					else
						this.destroy();
					PacketTarget pt = new PacketTarget.RadiusTarget(this, 48);
					ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.CHAINGUNHURT.ordinal(), pt, e.getEntityId());
					int dmg = firingPlayer.isPotionActive(Potion.damageBoost) || Chromabilities.RANGEDBOOST.enabledOn(firingPlayer) ? 20 : 10;
					e.attackEntityFrom(DamageSource.causePlayerDamage(firingPlayer).setProjectile(), dmg);
					e.hurtResistantTime = 0;
				}
			}
			else if (mov != null && mov.typeOfHit == MovingObjectType.BLOCK && immunityTicks == 0) {
				this.destroy();
			}
		}
	}

	@Override
	public boolean canRenderOnFire() {
		return false;
	}

	private void findAndRedirect(Entity start) {
		List<Entity> li = worldObj.getEntitiesWithinAABBExcludingEntity(start, boundingBox.expand(16, 6, 16));
		if (!li.isEmpty()) {
			WeightedRandom<EntityLivingBase> rand = new WeightedRandom();
			for (Entity e : li) {
				if (e instanceof EntityLivingBase) {
					EntityLivingBase elb = (EntityLivingBase)e;
					int weight = this.getEntityWeight(elb);
					if (weight > 0) {
						rand.addEntry(elb, weight);
					}
				}
			}
			if (rand.isEmpty())
				this.destroy();
			else {
				this.targetEntity(rand.getRandomEntry());
			}
		}
		else {
			this.destroy();
		}
	}

	private int getEntityWeight(EntityLivingBase e) {
		if (e == firingPlayer)
			return 0;
		else if (e.isDead || e.getHealth() <= 0)
			return 0;
		else if (e instanceof EntityPlayer) {
			return MinecraftServer.getServer().isPVPEnabled() ? 100 : 0;
		}
		else if (e instanceof EntityDragon || e instanceof EntityWither) {
			return 80;
		}
		else if (e instanceof EntityWitch || e instanceof EntityIronGolem) {
			return 60;
		}
		else if (e instanceof EntityMob) {
			return 50;
		}
		else if (e instanceof EntityTameable) {
			return 5;
		}
		else if (e instanceof EntityAnimal) {
			return 10;
		}
		else
			return 25;
	}

	private void targetEntity(EntityLivingBase e) {
		double dx = e.posX-posX;
		double dy = e.posY+e.height*(0.2+rand.nextDouble()*0.6)-posY;
		double dz = e.posZ-posZ;
		double dd = ReikaMathLibrary.py3d(dx, dy, dz);

		double v = 1.5;
		motionX = dx/dd*v;
		motionY = dy/dd*v;
		motionZ = dz/dd*v;

		immunityTicks = 5;
	}

	private void destroy() {
		this.setDead();
		ChromaSounds.POWERDOWN.playSound(this);
		if (firingPlayer != null)
			ChromaSounds.POWERDOWN.playSound(firingPlayer, 0.5F, 1);
		PacketTarget pt = new PacketTarget.RadiusTarget(this, 48);
		ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.CHAINGUNEND.ordinal(), pt, this.getEntityId());
	}

	@SideOnly(Side.CLIENT)
	public static void doDamagingParticles(int entityID) {
		Entity e = Minecraft.getMinecraft().theWorld.getEntityByID(entityID);
		for (int i = 0; i < 32; i++) {
			double rx = ReikaRandomHelper.getRandomPlusMinus(e.posX, 0.5);
			double ry = ReikaRandomHelper.getRandomPlusMinus(e.posY, 0.5);
			double rz = ReikaRandomHelper.getRandomPlusMinus(e.posZ, 0.5);
			double vx = ReikaRandomHelper.getRandomPlusMinus(0, 0.5);
			double vy = ReikaRandomHelper.getRandomPlusMinus(0, 0.5);
			double vz = ReikaRandomHelper.getRandomPlusMinus(0, 0.5);
			float s = 0.5F+e.worldObj.rand.nextFloat()*4;
			int l = 5+e.worldObj.rand.nextInt(75)*(1+e.worldObj.rand.nextInt(2));
			CrystalElement c = e.worldObj.rand.nextBoolean() ? CrystalElement.BLACK : CrystalElement.PINK;
			EntityFX fx = new EntityCCBlurFX(c, e.worldObj, rx, ry, rz, vx, vy, vz).setNoSlowdown().setScale(s).setRapidExpand().setLife(l);
			fx.noClip = false;
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@SideOnly(Side.CLIENT)
	public static void doDestructionParticles(int entityID) {
		EntityChainGunShot ec = (EntityChainGunShot)Minecraft.getMinecraft().theWorld.getEntityByID(entityID);
		for (int i = 0; i < 128; i++) {
			//CrystalElement e = CrystalElement.elements[i%16];
			double rx = ReikaRandomHelper.getRandomPlusMinus(ec.posX, 0.5);
			double ry = ReikaRandomHelper.getRandomPlusMinus(ec.posY, 0.5);
			double rz = ReikaRandomHelper.getRandomPlusMinus(ec.posZ, 0.5);
			double vx = ReikaRandomHelper.getRandomPlusMinus(0, 0.5);
			double vy = ReikaRandomHelper.getRandomPlusMinus(0, 0.5);
			double vz = ReikaRandomHelper.getRandomPlusMinus(0, 0.5);
			float s = 0.5F+ec.rand.nextFloat()*4;
			int l = 5+ec.rand.nextInt(75)*(1+ec.rand.nextInt(2));
			int color = ReikaColorAPI.getModifiedHue(0x0000ff, ReikaRandomHelper.getRandomPlusMinus(240, 60));
			EntityFX fx = new EntityCCBlurFX(ec.worldObj, rx, ry, rz, vx, vy, vz).setNoSlowdown().setScale(s).setRapidExpand().setLife(l).setColor(color);
			fx.noClip = false;
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

}
