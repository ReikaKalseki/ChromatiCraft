/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.TileEntity.CrystalTransmitterBase;
import Reika.ChromatiCraft.Magic.CrystalSource;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityFlareFX;
import Reika.ChromatiCraft.Render.Particle.EntityRuneFX;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import codechicken.lib.math.MathHelper;
//Make player able to manufacture in the very late game, otherwise rare worldgen
public class TileEntityCrystalPylon extends CrystalTransmitterBase implements CrystalSource {

	private boolean hasMultiblock = false;
	private CrystalElement color = CrystalElement.WHITE;
	public int randomOffset = rand.nextInt(360);

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.PYLON;
	}

	@Override
	public boolean isConductingElement(CrystalElement e) {
		return e == color;
	}

	public CrystalElement getColor() {
		return color;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		/*
		FilledBlockArray b = PylonGenerator.getPylonStructure(world, x, y-9, z, color);
		b.place();*/

		if (hasMultiblock) {
			if (world.isRemote) {
				this.spawnParticle(world, x, y, z);
			}

			if (!world.isRemote && rand.nextInt(40) == 0) {
				int r = 8+rand.nextInt(8);
				AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(x, y, z).expand(r, r, r);
				List<EntityLivingBase> li = world.getEntitiesWithinAABB(EntityLivingBase.class, box);
				for (int i = 0; i < li.size(); i++) {
					EntityLivingBase e = li.get(i);
					boolean attack = !e.isDead && e.getHealth() > 0;
					if (e instanceof EntityPlayer) {
						attack = attack && !((EntityPlayer)e).capabilities.isCreativeMode;
					}
					if (attack)
						this.attackEntity(e);
				}
			}

			if (this.getTicksExisted()%72 == 0) {
				ChromaSounds.POWER.playSoundAtBlock(this);
			}
		}
	}

	public void particleAttack(int x, int y, int z) {
		int n = 8+rand.nextInt(24);
		for (int i = 0; i < n; i++) {
			float rx = xCoord+rand.nextFloat();
			float ry = yCoord+rand.nextFloat();
			float rz = zCoord+rand.nextFloat();
			double dx = x-xCoord;
			double dy = y-yCoord;
			double dz = z-zCoord;
			double dd = ReikaMathLibrary.py3d(dx, dy, dz);
			double vx = 2*dx/dd;
			double vy = 2*dy/dd;
			double vz = 2*dz/dd;
			EntityFlareFX f = new EntityFlareFX(color, worldObj, rx, ry, rz, vx, vy, vz);
			Minecraft.getMinecraft().effectRenderer.addEffect(f);
		}
	}

	private void attackEntity(EntityLivingBase e) {
		ChromaSounds.DISCHARGE.playSoundAtBlock(this);
		ChromaSounds.DISCHARGE.playSound(worldObj, e.posX, e.posY, e.posZ, 1, 1);

		e.attackEntityFrom(DamageSource.magic, 5);

		int x = MathHelper.floor_double(e.posX);
		int y = MathHelper.floor_double(e.posY);
		int z = MathHelper.floor_double(e.posZ);
		ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.PYLONATTACK.ordinal(), this, x, y, z);
	}

	public void invalidateMultiblock() {
		if (hasMultiblock) {
			ChromaSounds.POWERDOWN.playSoundAtBlock(this);
			ChromaSounds.POWERDOWN.playSound(worldObj, xCoord, yCoord, zCoord, 1F, 2F);
			ChromaSounds.POWERDOWN.playSound(worldObj, xCoord, yCoord, zCoord, 1F, 0.5F);

			double d = 1.25;
			int n = 64+rand.nextInt(64);
			for (int i = 0; i < n; i++) {
				double rx = ReikaRandomHelper.getRandomPlusMinus(xCoord+0.5, d);
				double ry = ReikaRandomHelper.getRandomPlusMinus(yCoord+0.5, d);
				double rz = ReikaRandomHelper.getRandomPlusMinus(zCoord+0.5, d);
				double vx = rand.nextDouble()-0.5;
				double vy = rand.nextDouble()-0.5;
				double vz = rand.nextDouble()-0.5;
				EntityRuneFX fx = new EntityRuneFX(worldObj, rx, ry, rz, vx, vy, vz, color);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
		}
		hasMultiblock = false;
		this.clearTargets();
		//play sounds, particle effects
	}

	public void validateMultiblock() {
		hasMultiblock = true;
	}

	private void spawnParticle(World world, int x, int y, int z) {
		double d = 1.25;
		double rx = ReikaRandomHelper.getRandomPlusMinus(x+0.5, d);
		double ry = ReikaRandomHelper.getRandomPlusMinus(y+0.5, d);
		double rz = ReikaRandomHelper.getRandomPlusMinus(z+0.5, d);
		EntityFlareFX fx = new EntityFlareFX(color, world, rx, ry, rz);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		color = CrystalElement.elements[NBT.getInteger("color")];
		hasMultiblock = NBT.getBoolean("multi");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setInteger("color", color.ordinal());
		NBT.setBoolean("multi", hasMultiblock);
	}

	@Override
	public int getSendRange() {
		return 32;
	}

	@Override
	public boolean canConduct() {
		return hasMultiblock;
	}

	@Override
	public int maxThroughput() {
		return 1000;
	}

	@Override
	public int getTransmissionStrength() {
		return 100;
	}

	public void setColor(CrystalElement e) {
		color = e;
	}

}
