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

import ic2.api.energy.tile.IEnergySink;

import java.awt.Color;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.CrystalMusicManager;
import Reika.ChromatiCraft.Auxiliary.PylonDamage;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Instantiable.Data.SphericalVector;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Instantiable.ParticleController.CollectingPositionController;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import cofh.api.energy.IEnergyHandler;
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

	private static final IEntitySelector naturalSpawnedSelector = new IEntitySelector() {

		@Override
		public boolean isEntityApplicable(Entity e) {
			return e instanceof EntityGlowCloud && ((EntityGlowCloud)e).isNaturalSpawn;
		}

	};

	private boolean init;

	private boolean isAngry;
	private int attackCooldown = 20;

	private boolean isNaturalSpawn = true;

	private Coordinate cachedTile;

	public EntityGlowCloud(World world, double x, double y, double z) {
		super(world);
		color = this.generateRandomColor();
		targetColor = this.generateRandomColor();
		this.setPosition(x, y, z);
		height = 0.25F;
		width = 0.25F;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(50);
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

			int tx = 0;
			int ty = 0;
			int tz = 0;
			if (cachedTile != null && cachedTile.getDistanceTo(this) < 8) {
				tx = cachedTile.xCoord;
				ty = cachedTile.yCoord;
				tz = cachedTile.zCoord;
			}
			else {
				cachedTile = null;
				tx = MathHelper.floor_double(ReikaRandomHelper.getRandomPlusMinus(posX, 3));
				ty = MathHelper.floor_double(ReikaRandomHelper.getRandomPlusMinus(posY, 3));
				tz = MathHelper.floor_double(ReikaRandomHelper.getRandomPlusMinus(posZ, 3));
			}
			TileEntity te = worldObj.getTileEntity(tx, ty, tz);
			int amtToSpawn = isAngry ? 120 : 40;
			if (te instanceof IEnergyHandler) {
				if (((IEnergyHandler)te).receiveEnergy(ForgeDirection.VALID_DIRECTIONS[rand.nextInt(6)], amtToSpawn, false) > 0) {
					if (cachedTile == null)
						cachedTile = new Coordinate(te);
				}
				else {
					cachedTile = null;
				}
			}
			else if (ModList.IC2.isLoaded() && te instanceof IEnergySink) {
				if (((IEnergySink)te).injectEnergy(ForgeDirection.VALID_DIRECTIONS[rand.nextInt(6)], amtToSpawn, 32) < 50) {
					if (cachedTile == null)
						cachedTile = new Coordinate(te);
				}
				else {
					cachedTile = null;
				}
			}
			else {
				cachedTile = null;
			}
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

		isAngry = nbt.getBoolean("angry");
		isNaturalSpawn = nbt.getBoolean("natural");
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);

		nbt.setBoolean("isdead", isDead);
		nbt.setBoolean("angry", isAngry);
		nbt.setBoolean("natural", isNaturalSpawn);
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

	private void doDrops(EntityPlayer ep) {
		this.drop(ep, new ItemStack(Items.glowstone_dust, 1+rand.nextInt(16), 0));
		if (rand.nextInt(10) == 0)
			this.drop(ep, new ItemStack(Items.ghast_tear));
		if (rand.nextInt(3) == 0)
			this.drop(ep, ReikaItemHelper.getSizedItemStack(ChromaStacks.energyPowder, 1+rand.nextInt(4)));
	}

	private void drop(EntityPlayer ep, ItemStack is) {
		if (Chromabilities.RANGEDBOOST.enabledOn(ep)) {
			EntityItem ei = new EntityItem(ep.worldObj, posX, posY, posZ, is);
			if (!MinecraftForge.EVENT_BUS.post(new EntityItemPickupEvent(ep, ei))) {
				if (ReikaInventoryHelper.addToIInv(is, ep.inventory)) {

				}
				else {
					ReikaItemHelper.dropItem(this, is);
				}
			}
		}
		else {
			ReikaItemHelper.dropItem(this, is);
		}
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
			if (!worldObj.isRemote)
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
			ChromaSounds.GLOWCLOUD.playSound(this, 0.5F, 1.5F);
		if (rand.nextInt(40) == 0)
			ChromaSounds.BUFFERWARNING_LOW.playSound(this, 1F, 0.5F);

		if (!worldObj.isRemote) {
			EntityPlayer ep = worldObj.getClosestPlayerToEntity(this, -1);
			if (ep == null || worldObj.playerEntities.isEmpty()) {
				this.die();
			}
			else if (ticksExisted >= 80000 || rand.nextInt(80000-ticksExisted) == 0) {
				this.die();
			}
			else if (this.isInWater()) {
				this.die();
			}
			else if (this.getDistanceSqToEntity(ep) >= 65536) {
				this.die();
			}
			else if (this.getDistanceSqToEntity(ep) >= 16384 && rand.nextInt(200) == 0) {
				this.die();
			}

			if (isAngry) {
				if (attackCooldown > 0)
					attackCooldown--;
				else if (this.getDistanceSqToEntity(ep) <= 64) {
					if (rand.nextInt(40) == 0) {
						this.attack();
					}
				}
				if (velocity != null) {
					velocity.aimFrom(posX, posY, posZ, ep.posX, ep.posY+1.62, ep.posZ);
					velocity.magnitude = 0.375;
					velocityChanged = true;
				}
			}
		}
	}

	private void attack() {
		if (worldObj.isRemote) {
			this.doAttackFX();
		}
		else {
			this.doAttack();
			ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.CLOUDATTACK.ordinal(), new PacketTarget.RadiusTarget(this, 32), this.getEntityId());
		}
	}

	private void doAttack() {
		attackCooldown = 15;
		AxisAlignedBB box = ReikaAABBHelper.getEntityCenteredAABB(this, 8);
		List<EntityLivingBase> li = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, box);
		for (EntityLivingBase e : li) {
			if (!(e instanceof EntityGlowCloud)) {
				e.attackEntityFrom(DamageSource.magic, 4);
				if (e instanceof EntityPlayer && e.getHealth() <= 0)
					isAngry = false;
			}
		}
		this.attackEntityFrom(ChromatiCraft.pylonDamage[0], 2);
	}

	@SideOnly(Side.CLIENT)
	public void doAttackFX() {
		ReikaSoundHelper.playClientSound(ChromaSounds.FLAREATTACK, this, 2, 2*CrystalMusicManager.instance.getRandomScaledDing(CrystalElement.BLACK));

		int c = this.getRenderColor();
		for (int i = 0; i < 180; i++) {
			double a1 = rand.nextDouble()*360;
			double a2 = rand.nextDouble()*360;
			double[] xyz = ReikaPhysicsHelper.polarToCartesian(0.5, a1, a2);
			double px = posX+xyz[0];//ReikaRandomHelper.getRandomPlusMinus(posX, 1);
			double py = posY+xyz[1];//ReikaRandomHelper.getRandomPlusMinus(posY, 1);
			double pz = posZ+xyz[2];//ReikaRandomHelper.getRandomPlusMinus(posZ, 1);
			double v = 0.375;
			EntityBlurFX fx = new EntityBlurFX(worldObj, px, py, pz, xyz[0]*v, xyz[1]*v, xyz[2]*v);
			int t = ReikaRandomHelper.getRandomBetween(20, 60);
			int t2 = (int)(t*(0.5+rand.nextDouble()));
			float s = 1+2*rand.nextFloat();
			fx.setColor(color).setAlphaFading().setRapidExpand().setLife(t2).setScale(s);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@Override
	public boolean getCanSpawnHere() {
		return rand.nextInt(5) == 0/* && spawnedEntities < SPAWN_LIMIT*/ && !ReikaEntityHelper.existsAnotherValidEntityWithin(this, 32, naturalSpawnedSelector);// && worldObj.getClosestPlayer(posX, posY, posZ, 64) != null;
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
		if (src.getClass().getName().equals("tconstruct.smeltery.SmelteryDamageSource")) {
			return false;
		}
		else if (e instanceof EntityPlayer) {
			if (!ReikaPlayerAPI.isFake((EntityPlayer)e)) {
				boolean flag = super.attackEntityFrom(src, dmg);
				if (flag && this.getHealth() <= 0) {
					this.die();
					this.doDrops((EntityPlayer)e);
				}
				isAngry = true;
				return flag;
			}
		}
		else if (src instanceof PylonDamage) {
			boolean flag = super.attackEntityFrom(src, dmg);
			if (flag && this.getHealth() <= 0) {
				this.die();
			}
			return flag;
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

	@Override //spawner
	public IEntityLivingData onSpawnWithEgg(IEntityLivingData dat) {
		isNaturalSpawn = false;
		return dat;
	}

}
