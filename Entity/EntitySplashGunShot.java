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
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
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
		double v = 1.5;
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
	public void onUpdate()
	{
		double mx = motionX;
		double my = motionY;
		double mz = motionZ;
		super.onUpdate();
		motionX = mx;
		motionY = my;
		motionZ = mz;

		if (!worldObj.isRemote && (firingPlayer == null || ticksExisted > 200)) {
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
		double r = 4+rand.nextDouble()*6;
		AxisAlignedBB box = AxisAlignedBB.getBoundingBox(posX, posY, posZ, posX, posY, posZ).expand(r, r, r);
		List<EntityLivingBase> li = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, box);
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
	}

	private boolean shouldAttack(EntityLivingBase e) {
		if (e instanceof EntityPlayer && !MinecraftServer.getServer().isPVPEnabled())
			return false;
		return e != firingPlayer ? rand.nextBoolean() : true;
	}

	private void destroy() {
		this.setDead();
		ChromaSounds.CAST.playSound(this, 1, 2);
		if (firingPlayer != null)
			ChromaSounds.CAST.playSound(firingPlayer, 0.5F, 2);
		PacketTarget pt = new PacketTarget.RadiusTarget(this, 48);
		ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.SPLASHGUNEND.ordinal(), pt, this.getEntityId());
	}

	@SideOnly(Side.CLIENT)
	public static void doDamagingParticles(int entityID) {
		Entity e = Minecraft.getMinecraft().theWorld.getEntityByID(entityID);
		int n = 92+e.worldObj.rand.nextInt(192);
		for (int i = 0; i < n; i++) {
			double rx = ReikaRandomHelper.getRandomPlusMinus(e.posX, 0.005);
			double ry = ReikaRandomHelper.getRandomPlusMinus(e.posY, 0.005);
			double rz = ReikaRandomHelper.getRandomPlusMinus(e.posZ, 0.005);
			double v = 0.5+e.worldObj.rand.nextDouble();
			double[] vp = ReikaPhysicsHelper.polarToCartesian(v, e.worldObj.rand.nextDouble()*360, e.worldObj.rand.nextDouble()*360);
			float s = 0.5F+e.worldObj.rand.nextFloat()*4;
			int l = 5+e.worldObj.rand.nextInt(20)*(1+e.worldObj.rand.nextInt(2));
			CrystalElement c = e.worldObj.rand.nextBoolean() ? CrystalElement.WHITE : CrystalElement.PINK;
			EntityFX fx = new EntityBlurFX(c, e.worldObj, rx, ry, rz, vp[0], vp[1], vp[2]).setNoSlowdown().setScale(s).setRapidExpand().setLife(l);
			fx.noClip = false;
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

}
