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

import java.util.List;

import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityFireSmokeFX;
import Reika.ChromatiCraft.TileEntity.AOE.Defence.TileEntityMeteorTower;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModRegistry.InterfaceCache;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class EntityMeteorShot extends Entity implements IEntityAdditionalSpawnData {

	private int level;

	private Entity target;

	private int groundTick;

	private final double gravity;

	public EntityMeteorShot(TileEntityMeteorTower te, Entity e) {
		this(te.worldObj);

		this.setPosition(te.xCoord+0.5, te.yCoord+0.5+4, te.zCoord+0.5);

		target = e;

		double dx = e.posX-posX;
		double dz = e.posZ-posZ;
		double dy = posY-e.posY;
		double dd = ReikaMathLibrary.py3d(dx, 0, dz);
		double ang = 70;
		double phi = ReikaPhysicsHelper.cartesianToPolar(-dx, 0, -dz)[2];
		double v = ReikaPhysicsHelper.getProjectileVelocity(dd, ang, dy, gravity);
		ang = Math.toRadians(ang);
		phi = Math.toRadians(phi);
		motionX = v*Math.cos(ang)*Math.sin(phi);
		motionZ = v*Math.cos(ang)*Math.cos(phi);
		motionY = v*Math.sin(ang);//0.75;
		//ReikaJavaLibrary.pConsole(dx+" : "+dz+" > "+phi+" @ "+motionX+" : "+motionZ);
		velocityChanged = true;

		level = te.getTier();
	}

	public EntityMeteorShot(World world) {
		super(world);

		this.setSize(0.25F, 0.25F);
		gravity = this.getGravity(world);
	}

	private double getGravity(World world) {
		double g = 0.03125;
		if (ModList.GALACTICRAFT.isLoaded() && InterfaceCache.IGALACTICWORLD.instanceOf(world)) {
			g += ((IGalacticraftWorldProvider)world.provider).getGravity();
		}
		return g;
	}

	@Override
	protected void entityInit() {

	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (!worldObj.isRemote) {
			if (target != null) {

			}
			else {
				this.setDead();
			}

			if (onGround) {
				if (groundTick == 0)
					this.impact();
				groundTick++;
				if (groundTick > 0)
					this.setDead();
			}
		}
		else {
			this.spawnParticles();
		}

		if (onGround) {
			motionX = motionY = motionZ = 0;
		}
		else {
			motionY -= gravity;
			this.moveEntity(motionX, motionY, motionZ);
		}

		if (!worldObj.isRemote && !isDead && !this.impacted() && ticksExisted%5 == 0)
			ReikaPacketHelper.sendSoundPacket(ChromatiCraft.packetChannel, ChromaSounds.METEOR, worldObj, posX, posY, posZ, 0.75F, 1, false, 64);
	}

	@SideOnly(Side.CLIENT)
	private void spawnParticles() {
		int n = 1+level+rand.nextInt((1+level)*2);
		for (int i = 0; i < n; i++) {
			double d = 0.5*(1+level);
			double px = ReikaRandomHelper.getRandomPlusMinus(posX, d);
			double py = ReikaRandomHelper.getRandomPlusMinus(posY, d);
			double pz = ReikaRandomHelper.getRandomPlusMinus(posZ, d);
			int c = ReikaColorAPI.RGBtoHex(255, rand.nextInt(256), 0);
			float s = ReikaRandomHelper.getRandomBetween(Math.max(1, level*2), 2+level*3);
			EntityFX fx = new EntityFireSmokeFX(worldObj, px, py, pz, c).setScale(s).setRapidExpand();
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	private void impact() {
		double r = TileEntityMeteorTower.attacks[level].splashRange;
		AxisAlignedBB box = ReikaAABBHelper.getEntityCenteredAABB(this, r);
		List<EntityLiving> li = worldObj.getEntitiesWithinAABB(EntityLiving.class, box);
		for (EntityLiving e : li) {
			if (e == target || ReikaEntityHelper.isHostile(e)) {
				double dmg = e == target ? TileEntityMeteorTower.attacks[level].baseDamage : this.getDamage(e);
				e.setFire(TileEntityMeteorTower.attacks[level].fireDuration);
				if (dmg > 0) {
					e.attackEntityFrom(DamageSource.magic, (float)dmg);
				}
			}
		}
		ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.METEORIMPACT.ordinal(), new PacketTarget.RadiusTarget(this, 96), this.getEntityId());
		motionX = motionY = motionZ = 0;
		ReikaWorldHelper.ignite(worldObj, MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ));
	}

	@SideOnly(Side.CLIENT)
	public static void doClientImpact(World world, int id) {
		EntityMeteorShot e = (EntityMeteorShot)world.getEntityByID(id);
		if (e != null) {
			int n = 32*(1+e.level)+e.rand.nextInt(64*(1+e.level));
			double mr = TileEntityMeteorTower.attacks[e.level].splashRange;
			for (int i = 0; i < n; i++) {
				double r = e.rand.nextBoolean() ? mr*0.75 : e.rand.nextDouble()*mr;
				double a = e.rand.nextDouble()*360;
				double px = e.posX+r*Math.sin(Math.toRadians(a));
				double pz = e.posZ+r*Math.cos(Math.toRadians(a));
				double py = ReikaRandomHelper.getRandomBetween(e.posY-0.5, e.posY+1);
				int c = ReikaColorAPI.RGBtoHex(255, e.rand.nextInt(256), 0);
				float s = (float)ReikaRandomHelper.getRandomBetween(4F, 10F);
				EntityFX fx = new EntityBlurFX(e.worldObj, px, py, pz).setScale(s).setColor(c);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
			double d = e.getDistanceSqToEntity(Minecraft.getMinecraft().thePlayer);
			float v = 0.8F;
			if (d <= 256) {
				v = 4;
			}
			else if (d <= 576) {
				v = 2F;
			}
			else if (d <= 1024) {
				v = 1.5F;
			}
			else if (d <= 2304) { //48
				v = 1;
			}
			while (v > 0) {
				float m = Math.min(2, v);
				ReikaSoundHelper.playClientSound(ChromaSounds.IMPACT, e.posX, e.posY, e.posZ, m, 2, false);
				v -= m;
			}
		}
	}

	private double getDamage(EntityLiving e) {
		return TileEntityMeteorTower.attacks[level].baseDamage*(1-0.5*e.getDistanceToEntity(this)/TileEntityMeteorTower.attacks[level].splashRange);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tag) {
		groundTick = tag.getInteger("gtick");

		level = tag.getInteger("lvl");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tag) {
		tag.setInteger("gtick", groundTick);

		tag.setInteger("lvl", level);
	}

	@Override
	public void writeSpawnData(ByteBuf buf) {
		buf.writeInt(level);
	}

	@Override
	public void readSpawnData(ByteBuf buf) {
		level = buf.readInt();
	}

	public int getTier() {
		return level;
	}

	public boolean impacted() {
		return groundTick > 0;
	}

	@Override
	public boolean canRenderOnFire() {
		return false;
	}

	@Override
	public boolean shouldRenderInPass(int pass)
	{
		return pass == 1;
	}

	@Override
	public boolean isInRangeToRenderDist(double dist)
	{
		return true;
	}

}
