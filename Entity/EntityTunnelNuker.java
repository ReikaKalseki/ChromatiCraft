/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2018
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Entity;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Block.Decoration.BlockEtherealLight.Flags;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;
import Reika.ChromatiCraft.Render.Particle.EntityCCBlurFX;
import Reika.DragonAPI.Instantiable.Effects.EntityBlurFX;
import Reika.DragonAPI.Interfaces.Entity.DestroyOnUnload;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.Satisforestry.API.SFAPI;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class EntityTunnelNuker extends EntityLiving implements DestroyOnUnload {

	public EntityTunnelNuker(World world) {
		super(world);
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();

		int x = MathHelper.floor_double(posX);
		int z = MathHelper.floor_double(posZ);

		doEntityTick(this);

		rotationPitch = 0;
		//rotationYaw = 0;
		rotationYaw += Math.signum(System.identityHashCode(this))/8F;
		prevRotationYaw = rotationYaw;

		onGround = false;
		isAirBorne = true;

		int top = ReikaWorldHelper.getTopNonAirBlock(worldObj, x, z, true);
		if (SFAPI.biomeHandler.isPinkForest(ReikaWorldHelper.getNaturalGennedBiomeAt(worldObj, x, z)))
			top = SFAPI.biomeHandler.getTrueTopAt(worldObj, x, z);

		double vy = motionY;

		if (posY-top < 8) {
			//posY += 0.125;
			//motionY = 0;//Math.max(motionY, 0.0625);
		}
		else {
			//motionY = Math.max(motionY, -0.03125);
			//motionY = 0;
		}

		double[] xyz = ReikaPhysicsHelper.polarToCartesian(0.075, 0, rotationYaw);

		motionY = MathHelper.clamp_double(((top-posY)+8)/32D, -0.08, 0.125);
		motionX = xyz[0];
		motionZ = xyz[2];
		velocityChanged = true;
		/*
		if (Math.abs(motionY-vy) > 0.0625) {
			motionY = Math.max(motionY*0.25+vy*0.75, -0.03125);
		}

		motionY += 0.035;*/
	}

	public static void doEntityTick(EntityLivingBase e) {
		if (e.worldObj.isRemote) {
			if (e.ticksExisted%16 == 0) {
				doTunnelNukerFX(e);
			}
		}
		else {
			if (e.ticksExisted%8 == 0) {
				ChromaSounds.TUNNELNUKERAMBIENT.playSound(e, 0.2F+e.getRNG().nextFloat()*0.2F, 1);
			}
			if (e.getRNG().nextInt(160) == 0) {
				ChromaSounds.TUNNELNUKERCALL.playSound(e, 0.25F, 0.75F+e.getRNG().nextFloat()*0.75F);
			}
			if (e.worldObj.provider.dimensionId != ExtraChromaIDs.DIMID.getValue() && e.ticksExisted%64 == 0) {
				int y = MathHelper.floor_double(e.posY);
				int x = MathHelper.floor_double(e.posX);
				int z = MathHelper.floor_double(e.posZ);
				if (e.worldObj.getBlock(x, y, z).isAir(e.worldObj, x, y, z)) {
					e.worldObj.setBlock(x, y, z, ChromaBlocks.LIGHT.getBlockInstance(), Flags.FASTDECAY.getFlag(), 3);
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void doParticles() {
		doTunnelNukerFX(this);
	}

	@SideOnly(Side.CLIENT)
	public static void doTunnelNukerFX(EntityLivingBase e) {
		float s = (e.getRNG().nextFloat()*0.75F+0.25F)*7;
		int l = ReikaRandomHelper.getRandomBetween(10, 60);
		int c = ReikaColorAPI.getModifiedHue(0xff0000, e.getRNG().nextInt(60));
		EntityBlurFX fx = new EntityCCBlurFX(e.worldObj, e.posX, e.posY+0.9, e.posZ);
		fx.setRapidExpand().setAlphaFading().setScale(s).setLife(l).setColor(c);//.setPositionController();
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}

	@Override
	public boolean getCanSpawnHere() {
		int y = MathHelper.floor_double(posY);
		int x = MathHelper.floor_double(posX);
		int z = MathHelper.floor_double(posZ);
		return worldObj.canBlockSeeTheSky(x, y+1, z) && !ReikaEntityHelper.existsAnotherValidEntityWithin(this, 32, new ReikaEntityHelper.ClassEntitySelector(this.getClass(), true));
	}

	@Override
	public void destroy() {
		this.setDead();
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);

		if (nbt.getBoolean("isdead"))
			this.setDead();
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);

		nbt.setBoolean("isdead", isDead);
	}

	@Override
	public boolean getAlwaysRenderNameTag() {
		return false;
	}

	@Override
	public String getCommandSenderName() {
		return "Lumafly";
	}

}
