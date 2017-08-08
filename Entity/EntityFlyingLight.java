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

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class EntityFlyingLight extends EntityFireball {

	private static MultiMap<Integer, EntityFlyingLight> activeLights = new MultiMap();

	private boolean init = false;

	private ArrayList<Coordinate> lastTickLight = new ArrayList();
	private ArrayList<Coordinate> thisTickLight = new ArrayList();

	public EntityFlyingLight(World world, EntityPlayer ep) {
		super(world);

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

	public EntityFlyingLight(World world) {
		super(world);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
	}

	@Override
	public void onUpdate() {
		if (!init) {
			//register(this);
			init = true;
		}

		double mx = motionX;
		double my = motionY;
		double mz = motionZ;
		super.onUpdate();
		motionX = mx;
		motionY = my;
		motionZ = mz;

		if (!worldObj.isRemote && ticksExisted > 200) {
			this.destroy();
		}

		if (worldObj.isRemote) {
			this.spawnLight();
			if (ticksExisted > 10) {
				//this.spawnTraceParticle();
			}
		}
		else {
			//this.spawnLight();
		}
	}
	/*
	private static void register(EntityFlyingLight e) {
		int dim = e.worldObj.provider.dimensionId;
		activeLights.addValue(dim, e);
	}

	private static void unregister(EntityFlyingLight e) {
		int dim = e.worldObj.provider.dimensionId;
		Collection<EntityFlyingLight> c = activeLights.get(dim);
		c.remove(e);
	}

	public static double getClosestLight(World world, int x, int y, int z) {
		double d = Double.POSITIVE_INFINITY;
		Collection<EntityFlyingLight> c = activeLights.get(world.provider.dimensionId);
		for (EntityFlyingLight e : c) {
			double dd = e.getDistanceSq(x+0.5, y+0.5, z+0.5);
			if (dd < d)
				d = dd;
		}
		return Math.sqrt(d);
	}

	public static boolean lightsInWorld(World world) {
		int dim = world.provider.dimensionId;
		return !activeLights.get(dim).isEmpty();
	}
	 */
	private void spawnLight() {
		int x = MathHelper.floor_double(posX);
		int y = MathHelper.floor_double(posY);
		int z = MathHelper.floor_double(posZ);
		worldObj.markBlockForUpdate(x, y, z);

		int r = 0;
		lastTickLight.clear();
		for (Coordinate c : thisTickLight) {
			c.setBlock(worldObj, Blocks.air);
			c.triggerBlockUpdate(worldObj, false);
			lastTickLight.add(c);
		}
		thisTickLight.clear();
		for (int i = -r; i <= r; i++) {
			for (int j = -r; j <= r; j++) {
				for (int k = -r; k <= r; k++) {
					int dx = x+i;
					int dy = y+j;
					int dz = z+k;
					Block b = worldObj.getBlock(dx, dy, dz);
					if (b.isAir(worldObj, dx, dy, dz)) {
						worldObj.setBlock(dx, dy, dz, ChromaBlocks.LIGHT.getBlockInstance(), 0, 3);
						worldObj.markBlockRangeForRenderUpdate(x-16, y-16, z-16, x+16, y+16, z+16);
						/*
						worldObj.getChunkFromBlockCoords(dx, dz).enqueueRelightChecks();
						worldObj.func_147451_t(dx, dy, dz);
						worldObj.updateLightByType(EnumSkyBlock.Block, dx, dy, dz);
						worldObj.markBlocksDirtyVertical(dx, dz, dy-32, dy+32);
						 */
						thisTickLight.add(new Coordinate(dx, dy, dz));
					}
				}
			}
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

	private void destroy() {
		//unregister(this);
		this.clearLight();
		this.setDead();
		int x = MathHelper.floor_double(posX);
		int y = MathHelper.floor_double(posY);
		int z = MathHelper.floor_double(posZ);
		worldObj.markBlockRangeForRenderUpdate(x-64, y-64, z-64, x+64, y+64, z+64);
		//ChromaSounds.POWERDOWN.playSound(this);
		//PacketTarget pt = new PacketTarget.RadiusTarget(this, 48);
		//ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.CHAINGUNEND.ordinal(), pt, this.getEntityId());
	}

	@Override
	protected void onImpact(MovingObjectPosition mov) {
		if (!worldObj.isRemote) {
			if (mov != null && mov.typeOfHit == MovingObjectType.BLOCK) {
				this.destroy();
			}
		}
	}

	private void clearLight() {
		for (Coordinate c : lastTickLight) {
			c.setBlock(worldObj, Blocks.air);
		}
		for (Coordinate c : thisTickLight) {
			c.setBlock(worldObj, Blocks.air);
		}
	}

	@Override
	public boolean canRenderOnFire() {
		return false;
	}
}
