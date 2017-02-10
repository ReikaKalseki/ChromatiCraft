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

import java.awt.Color;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.Instantiable.Data.SphericalVector;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Instantiable.ParticleController.CollectingPositionController;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityGlowCloud extends EntityLiving {

	private SphericalVector velocity;
	private double targetTheta = rand.nextInt(360);
	private double targetPhi = rand.nextInt(360);
	private double targetVelocity = ReikaRandomHelper.getRandomPlusMinus(0.1, 0.1);

	private int color;
	private int targetColor;
	private int colorTransitionTick = 0;

	private boolean isPylonSpawn = false;
	private boolean doDrops = true;

	private static final int COLOR_TRANSITION_LENGTH = 120;
	private static final int SOLID_COLOR_LENGTH = 80;

	private Coordinate light;
	private Coordinate oldLight;

	private static int spawnedEntities;
	private static final int SPAWN_LIMIT = 80;

	private boolean init;

	public EntityGlowCloud(World world, double x, double y, double z) {
		super(world);
		color = this.generateRandomColor();
		targetColor = this.generateRandomColor();
		this.setPosition(x, y, z);
		height = 0.25F;
		width = 0.25F;
	}

	private int generateRandomColor() {
		int hue = ReikaRandomHelper.getRandomBetween(120, 300);
		int c = Color.HSBtoRGB(hue/360F, 1, 1);
		return ReikaColorAPI.mixColors(c, 0xffffff, rand.nextFloat());
	}

	public EntityGlowCloud(World world) {
		super(world);
	}

	@Override
	protected boolean canTriggerWalking()
	{
		return false;
	}

	@Override
	public boolean canRenderOnFire() {
		return false;
	}

	@Override
	public boolean isPotionApplicable(PotionEffect e)
	{
		return false;
	}

	@Override
	protected String func_146067_o(int p_146067_1_)
	{
		return "";
	}

	@Override
	protected void fall(float p_70069_1_)
	{

	}

	@Override
	public boolean handleWaterMovement()
	{
		return false;
	}

	@Override
	public String getCommandSenderName()
	{
		return "Luma Fog";
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		velocity = new SphericalVector(0.15, rand.nextInt(360), rand.nextInt(360));

		color = this.generateRandomColor();
		targetColor = this.generateRandomColor();
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		/*
		if (worldObj != null && !init) {
			spawnedEntities++;
			init = true;
			ReikaJavaLibrary.pConsole("spawned, count="+spawnedEntities);
		}
		 */

		if (!worldObj.isRemote) {
			if (ReikaMathLibrary.approxr(velocity.inclination, targetTheta, 2)) {
				targetTheta = rand.nextInt(360);
			}
			else {
				if (targetTheta > velocity.inclination)
					velocity.inclination++;
				else
					velocity.inclination--;
			}

			if (ReikaMathLibrary.approxr(velocity.rotation, targetPhi, 2)) {
				targetPhi = rand.nextInt(360);
			}
			else {
				if (targetPhi > velocity.rotation)
					velocity.rotation++;
				else
					velocity.rotation--;
			}

			if (ReikaMathLibrary.approxr(velocity.rotation, targetPhi, 0.05)) {
				targetVelocity = ReikaRandomHelper.getRandomPlusMinus(0.1, 0.1);
			}
			else {
				if (targetVelocity > velocity.magnitude)
					velocity.magnitude += 0.01D;
				else
					velocity.magnitude -= 0.01D;
			}

			if (onGround) {
				velocity.inclination = 90;
				velocity.magnitude *= 2;
				posY += 1;//velocity.magnitude;
			}

			velocityChanged = true;
			//ReikaJavaLibrary.pConsole(velocity.inclination+"/"+targetTheta+"; "+velocity.rotation+"/"+targetPhi, Side.SERVER);

			double[] v = velocity.getCartesian();

			motionX = v[0];
			motionY = v[1];
			motionZ = v[2];
		}

		colorTransitionTick++;
		if (colorTransitionTick >= COLOR_TRANSITION_LENGTH) {
			color = targetColor;
			targetColor = this.generateRandomColor();
			colorTransitionTick = -SOLID_COLOR_LENGTH;
		}

		if (worldObj.isRemote) {
			this.lifeParticles();

			if (!isDead && ticksExisted%16 == 0) {
				Coordinate c = new Coordinate(this);
				if (!c.equals(light) && c.getBlock(worldObj) == Blocks.air) {
					this.deleteOldLight();
					oldLight = light;
					light = c;
					light.setBlock(worldObj, ChromaBlocks.LIGHT.getBlockInstance());
				}
			}
		}
		else {
			//worldObj.setBlock(MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ), ChromaBlocks.LIGHT.getBlockInstance(), Flags.DECAY.getFlag(), 3);
		}

		fallDistance = 0;
	}

	private void deleteOldLight() {
		if (worldObj.isRemote) {
			if (light != null && light.getBlock(worldObj) == ChromaBlocks.LIGHT.getBlockInstance()) {
				light.setBlock(worldObj, Blocks.air);
			}
			if (oldLight != null && oldLight.getBlock(worldObj) == ChromaBlocks.LIGHT.getBlockInstance()) {
				oldLight.setBlock(worldObj, Blocks.air);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void lifeParticles() {
		int c = this.getRenderColor();
		double d = 0.125;
		double px = ReikaRandomHelper.getRandomPlusMinus(posX, d);
		double py = ReikaRandomHelper.getRandomPlusMinus(posY, d);
		double pz = ReikaRandomHelper.getRandomPlusMinus(posZ, d);
		int l = ReikaRandomHelper.getRandomBetween(10, 60);
		float s = 2+rand.nextFloat()*2;
		EntityFX fx = new EntityBlurFX(worldObj, px, py, pz).setColor(c).setLife(l).setScale(s).setAlphaFading().setRapidExpand().setColliding();
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);

		c = ReikaColorAPI.getColorWithBrightnessMultiplier(c, 0.4F);
		d = 0.25;
		px = ReikaRandomHelper.getRandomPlusMinus(posX, d);
		py = ReikaRandomHelper.getRandomPlusMinus(posY, d);
		pz = ReikaRandomHelper.getRandomPlusMinus(posZ, d);
		fx = new EntityBlurFX(worldObj, px, py, pz).setColor(c).setLife(l/2).setScale(s*3).setAlphaFading().setRapidExpand().setColliding();
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
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

	private void die() {
		//particle effect
		if (worldObj.isRemote) {
			this.doDeathParticles(/*worldObj, posX, posY, posZ, this.getRenderColor()*/);
		}
		else {
			this.sendDeathParticles();
		}
		this.deleteOldLight();
		this.setDead();
	}

	private void sendDeathParticles() {
		ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.CLOUDDIE.ordinal(), new PacketTarget.RadiusTarget(this, 32), this.getEntityId());
	}

	@Override
	public void setDead()
	{
		super.setDead();

		this.deleteOldLight();

		if (worldObj != null) {
			//spawnedEntities--;
			this.sendDeathParticles();
		}
	}

	@Override
	public AxisAlignedBB getCollisionBox(Entity entity) {
		return null;//AxisAlignedBB.getBoundingBox(posX, posY, posZ, posX, posY, posZ).expand(3, 3, 3);
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();

		if (ticksExisted%80 == 0)
			ChromaSounds.POWERCRYS.playSound(this, 0.5F, 1.5F);
		if (rand.nextInt(40) == 0)
			ChromaSounds.BUFFERWARNING_LOW.playSound(this, 1F, 0.5F);

		if (!worldObj.isRemote) {
			EntityPlayer ep = worldObj.getClosestPlayerToEntity(this, -1);
			if (ep == null || worldObj.playerEntities.isEmpty()) {
				this.die();
			}
			else if (ticksExisted >= 12000 || rand.nextInt(12000-ticksExisted) == 0) {
				this.die();
			}
			else if (worldObj.isRaining() && rand.nextInt(80) == 0) {
				this.die();
			}
			else if (this.getDistanceSqToEntity(ep) >= 16384) {
				this.die();
			}
			else if (this.getDistanceSqToEntity(ep) >= 1024 && rand.nextInt(200) == 0) {
				this.die();
			}
		}
	}

	@Override
	public boolean getCanSpawnHere() {
		return rand.nextInt(5) == 0/* && spawnedEntities < SPAWN_LIMIT*/ && !ReikaEntityHelper.existsAnotherEntityWithin(this, 32);// && worldObj.getClosestPlayer(posX, posY, posZ, 64) != null;
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 8;
	}

	@Override
	public void playLivingSound() {

	}

	@Override
	public final int getTalkInterval()
	{
		return 20;
	}

	@Override
	protected final void func_145780_a(int par1, int par2, int par3, Block par4) //play step sound
	{

	}

	@Override
	protected String getHurtSound() {
		return "mob.bat.takeoff";
	}

	@Override
	protected String getDeathSound() {
		return "mob.bat.loop";
	}

	@Override
	public void onDeath(DamageSource src) {
		if (!worldObj.isRemote) {
			this.sendDeathParticles();
		}
		else {
			this.doDeathParticles();
		}
	}

	@SideOnly(Side.CLIENT)
	public void doDeathParticles() {
		this.deleteOldLight();

		int c = this.getRenderColor();
		for (int i = 0; i < 20; i++) {
			double a1 = rand.nextDouble()*360;
			double a2 = rand.nextDouble()*360;
			double[] xyz = ReikaPhysicsHelper.polarToCartesian(3, a1, a2);
			double px = posX+xyz[0];//ReikaRandomHelper.getRandomPlusMinus(posX, 1);
			double py = posY+xyz[1];//ReikaRandomHelper.getRandomPlusMinus(posY, 1);
			double pz = posZ+xyz[2];//ReikaRandomHelper.getRandomPlusMinus(posZ, 1);
			EntityBlurFX fx = new EntityBlurFX(worldObj, px, py, pz);
			int t = ReikaRandomHelper.getRandomBetween(10, 30);
			int t2 = (int)(t*(0.5+rand.nextDouble()));
			float s = 1+2*rand.nextFloat();
			fx.setPositionController(new CollectingPositionController(px, py, pz, posX, posY, posZ, t)).setColor(color).setAlphaFading().setRapidExpand().setLife(t2).setScale(s);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	public int getRenderColor() {
		float f = this.getColorFraction();
		return f > 0 ? ReikaColorAPI.mixColors(targetColor, color, f) : color;
	}

	private float getColorFraction() {
		return colorTransitionTick > 0 ? (float)colorTransitionTick/COLOR_TRANSITION_LENGTH : 0;
	}

	@Override
	public boolean attackEntityFrom(DamageSource src, float dmg) {
		Entity e = src.getEntity();
		if (e instanceof EntityPlayer) {
			if (!ReikaPlayerAPI.isFake((EntityPlayer)e)) {
				boolean flag = super.attackEntityFrom(src, dmg);
				if (flag && this.getHealth() <= 0) {
					this.die();
				}
				return flag;
			}
		}
		return false;
	}

	@Override
	public boolean shouldRenderInPass(int pass)
	{
		return pass == 1;
	}

	@Override
	protected void dropFewItems(boolean recentHit, int looting) {
		if (recentHit) {

		}
	}

}
