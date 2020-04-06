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

import java.util.HashSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Block.Decoration.BlockEtherealLight.Flags;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityLightShot extends EntityFireball {

	private EntityPlayer firingPlayer;

	private HashSet<Coordinate> trailCoords = new HashSet();

	public EntityLightShot(World world, EntityPlayer ep, boolean randomVec) {
		super(world);
		firingPlayer = ep;

		Vec3 vec = ep.getLookVec();
		if (randomVec) {
			vec.xCoord = ReikaRandomHelper.getRandomPlusMinus(0, 1D);
			vec.yCoord = ReikaRandomHelper.getRandomPlusMinus(0, 1D);
			vec.zCoord = ReikaRandomHelper.getRandomPlusMinus(0, 1D);
			vec.normalize();
		}
		double v = 3.5;
		motionX = v*vec.xCoord;
		motionY = v*vec.yCoord;
		motionZ = v*vec.zCoord;

		accelerationX = 0;
		accelerationY = 0;
		accelerationZ = 0;

		this.setSize(0.125F, 0.125F);
	}

	public EntityLightShot(World world) {
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

		if (rand.nextInt(30) == 0) {
			Coordinate c = new Coordinate(this);
			if (c.isEmpty(worldObj)) {
				c.setBlock(worldObj, ChromaBlocks.LIGHT.getBlockInstance(), Flags.SLOWDECAY.getFlag());
				c.triggerRenderUpdate(worldObj);
				trailCoords.add(c);
			}
		}

		if (!worldObj.isRemote && (firingPlayer == null || ticksExisted > 50)) {
			this.destroy(false);
		}

		if (worldObj.isRemote && ticksExisted > 10) {
			ReikaParticleHelper.FIREWORK.spawnAt(this);
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
			EntityFX fx = new EntityBlurFX(worldObj, px, py, pz, 0, 0, 0).setRapidExpand().setLife(l).setScale(s).setColor(0, 192, 255);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@Override
	protected void onImpact(MovingObjectPosition mov) {
		if (firingPlayer == null && !worldObj.isRemote) {
			this.destroy(true);
			return;
		}
		if (!worldObj.isRemote) {
			if (mov != null && mov.typeOfHit == MovingObjectType.BLOCK) {
				Coordinate c = new Coordinate(mov);
				if (c.getBlock(worldObj).getCollisionBoundingBoxFromPool(worldObj, c.xCoord, c.yCoord, c.zCoord) != null) {
					c = c.offset(ForgeDirection.VALID_DIRECTIONS[mov.sideHit], 1);
					if (c.isEmpty(worldObj)) {
						c.setBlock(worldObj, ChromaBlocks.LIGHT.getBlockInstance(), Flags.PARTICLES.getFlag() | Flags.MINEABLE.getFlag() | Flags.SLOWDECAY.getFlag());
						c.triggerRenderUpdate(worldObj);
						this.destroy(true);
					}
				}
			}
		}
	}

	@Override
	public boolean canRenderOnFire() {
		return false;
	}

	private void destroy(boolean impact) {
		if (!impact) {

		}
		this.setDead();
		for (Coordinate c : trailCoords) {
			c.scheduleUpdateTick(worldObj, rand.nextInt(40));
		}
		ChromaSounds.BOUNCE.playSound(this, 0.5F, 0.5F);
		if (firingPlayer != null)
			ChromaSounds.BOUNCE.playSound(firingPlayer, 0.25F, 0.5F);
	}
}
