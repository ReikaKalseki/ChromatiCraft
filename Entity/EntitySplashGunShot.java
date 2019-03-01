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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.potion.Potion;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Effects.LightningBolt;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntitySplashGunShot extends EntityFireball {

	private EntityPlayer firingPlayer;

	public EntitySplashGunShot(World world, EntityPlayer ep) {
		super(world);
		firingPlayer = ep;

		Vec3 vec = ep.getLookVec();
		double v = 0.25;
		motionX = v*vec.xCoord;
		motionY = v*vec.yCoord;
		motionZ = v*vec.zCoord;

		accelerationX = 0;
		accelerationY = 0;
		accelerationZ = 0;

		this.setSize(0.125F, 0.125F);
	}

	public EntitySplashGunShot(World world) {
		super(world);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
	}

	@Override
	public void onUpdate() {
		double mx = motionX;
		double my = motionY;
		double mz = motionZ;
		super.onUpdate();
		motionX = mx;
		motionY = my;
		motionZ = mz;

		if (!worldObj.isRemote) {
			if (rand.nextInt(12) == 0) {
				AxisAlignedBB box = ReikaAABBHelper.getEntityCenteredAABB(this, 5).expand(3, 0, 3);
				List<EntityLivingBase> li = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, box);
				boolean fired = false;
				while (!fired && !li.isEmpty()) {
					int idx = rand.nextInt(li.size());
					EntityLivingBase e = li.get(idx);
					if (this.shouldAttack(e)) {
						this.attack(e);
						fired = true;
					}
					li.remove(idx);
				}
			}
		}

		if (!worldObj.isRemote && (firingPlayer == null || ticksExisted >= 100)) {
			this.doAttack();
			this.destroy();
		}
	}

	@Override
	protected void onImpact(MovingObjectPosition mov) {
		if (firingPlayer == null && !worldObj.isRemote) {
			this.destroy();
			return;
		}
		if (!worldObj.isRemote) {
			if (mov != null && mov.entityHit == firingPlayer)
				return;
			this.doAttack();
		}
	}

	@Override
	public boolean canRenderOnFire() {
		return false;
	}

	private void doAttack() {
		double r = 4+rand.nextDouble()*2;
		AxisAlignedBB box = ReikaAABBHelper.getEntityCenteredAABB(this, r);
		List<EntityLivingBase> li = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, box);
		while (r < 24 && li.isEmpty()) {
			r++;
			box = ReikaAABBHelper.getEntityCenteredAABB(this, r);
			li = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, box);
		}
		for (EntityLivingBase e : li) {
			if (this.shouldAttack(e)) {
				this.attack(e);
			}
		}
		this.destroy();
	}

	private void attack(EntityLivingBase e) {
		float dmg = firingPlayer.isPotionActive(Potion.damageBoost) || Chromabilities.RANGEDBOOST.enabledOn(firingPlayer) ? 20 : 10;
		if (e instanceof EntityPlayer)
			dmg *= 1.2F;
		if (e == firingPlayer)
			dmg = 0.25F;
		e.attackEntityFrom(DamageSource.magic, dmg);
		e.hurtResistantTime = 0;
		PacketTarget pt = new PacketTarget.RadiusTarget(this, 48);
		ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.SPLASHGUNATTACK.ordinal(), pt, this.getEntityId(), e.getEntityId());
	}

	private boolean shouldAttack(EntityLivingBase e) {
		if (e instanceof EntityPlayer && !MinecraftServer.getServer().isPVPEnabled())
			return false;
		return e != firingPlayer && e.getHealth() > 0 && !e.isDead;
	}

	private void destroy() {
		this.setDead();
		PacketTarget pt = new PacketTarget.RadiusTarget(this, 48);
		ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.SPLASHGUNEND.ordinal(), pt, this.getEntityId());
	}

	@SideOnly(Side.CLIENT)
	public static void doDestroyParticles(int entityID) {
		Entity e = Minecraft.getMinecraft().theWorld.getEntityByID(entityID);
		int n = 32+e.worldObj.rand.nextInt(32);
		for (int i = 0; i < n; i++) {
			double rx = ReikaRandomHelper.getRandomPlusMinus(e.posX, 0.25);
			double ry = ReikaRandomHelper.getRandomPlusMinus(e.posY, 0.25);
			double rz = ReikaRandomHelper.getRandomPlusMinus(e.posZ, 0.25);
			double v = 0.25+e.worldObj.rand.nextDouble()*0.25;
			double[] vp = ReikaPhysicsHelper.polarToCartesian(v, e.worldObj.rand.nextDouble()*360, e.worldObj.rand.nextDouble()*360);
			float s = 5F+e.worldObj.rand.nextFloat()*10;
			int l = 20+e.worldObj.rand.nextInt(20);
			CrystalElement c = e.worldObj.rand.nextBoolean() ? CrystalElement.WHITE : CrystalElement.PINK;
			int clr = ReikaColorAPI.getModifiedSat(ReikaColorAPI.getColorWithBrightnessMultiplier(c.getColor(), 0.4F), 2);
			EntityBlurFX fx = new EntityBlurFX(e.worldObj, rx, ry, rz, vp[0], vp[1], vp[2]);
			fx.setRapidExpand().setAlphaFading().setScale(s).setLife(l).setColor(clr).setColliding().setDrag(0.875);
			fx.noClip = false;
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}

		ReikaSoundHelper.playClientSound(ChromaSounds.SHOCKWAVE, Minecraft.getMinecraft().thePlayer, 0.5F, 1);
		ReikaSoundHelper.playClientSound(ChromaSounds.POWERDOWN, Minecraft.getMinecraft().thePlayer, 2, 0.75F);
	}

	@SideOnly(Side.CLIENT)
	public static void doAttackParticles(int sourceID, int targetID) {
		World world = Minecraft.getMinecraft().theWorld;
		Entity src = world.getEntityByID(sourceID);
		Entity tgt = world.getEntityByID(targetID);
		if (src != null && tgt != null) {
			CrystalElement e = world.rand.nextBoolean() ? CrystalElement.WHITE : CrystalElement.PINK;
			LightningBolt b = new LightningBolt(new DecimalPosition(src), new DecimalPosition(tgt), 6);
			b.variance = 0.125F;
			b.update();
			int l = 5+world.rand.nextInt(5);
			for (int i = 0; i < b.nsteps; i++) {
				DecimalPosition pos1 = b.getPosition(i);
				DecimalPosition pos2 = b.getPosition(i+1);
				for (double r = 0; r <= 1; r += 0.03125) {
					float s = 2F;
					int clr = e.getColor();
					DecimalPosition dd = DecimalPosition.interpolate(pos1, pos2, r);
					EntityFX fx = new EntityBlurFX(world, dd.xCoord, dd.yCoord, dd.zCoord).setScale(s).setColor(clr).setLife(l).setRapidExpand();
					Minecraft.getMinecraft().effectRenderer.addEffect(fx);
					EntityFX fx2 = new EntityBlurFX(world, dd.xCoord, dd.yCoord, dd.zCoord).setScale(s/2.5F).setColor(0xffffff).setLife(l).setRapidExpand();
					Minecraft.getMinecraft().effectRenderer.addEffect(fx2);
				}
			}
		}
		ReikaSoundHelper.playClientSound(ChromaSounds.DISCHARGE, Minecraft.getMinecraft().thePlayer, 0.25F, 1.5F);
	}

}
