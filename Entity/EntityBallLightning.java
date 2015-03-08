package Reika.ChromatiCraft.Entity;

import io.netty.buffer.ByteBuf;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.CrystalMusicManager;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Magic.Network.CrystalNetworker;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityCenterBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityLaserFX;
import Reika.DragonAPI.Instantiable.Data.SphericalVector;
import Reika.DragonAPI.Instantiable.IO.PacketTarget.DimensionTarget;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaVectorHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityBallLightning extends EntityLiving implements IEntityAdditionalSpawnData {

	private SphericalVector velocity;
	private double targetTheta = rand.nextInt(360);
	private double targetPhi = rand.nextInt(360);
	private double targetVelocity = ReikaRandomHelper.getRandomPlusMinus(0.1, 0.1);

	private CrystalElement color;

	private CrystalElement targetColor = null;
	private float colorTransitionFraction = 0;

	private boolean isPylonSpawn = false;
	private boolean doDrops = true;

	public EntityBallLightning(World world, CrystalElement color, double x, double y, double z) {
		super(world);
		this.color = color;
		this.setPosition(x, y, z);
		height = 0.25F;
		width = 0.25F;
	}

	public EntityBallLightning(World world) {
		super(world);
	}

	public EntityBallLightning setNoDrops() {
		doDrops = false;
		return this;
	}

	public EntityBallLightning setPylon() {
		isPylonSpawn = true;
		return this;
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
	protected void entityInit() {
		super.entityInit();
		velocity = new SphericalVector(0.15, rand.nextInt(360), rand.nextInt(360));

		if (color == null)
			color = CrystalElement.randomElement();

		dataWatcher.addObject(30, 0);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
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

		velocityChanged = true;
		//ReikaJavaLibrary.pConsole(velocity.inclination+"/"+targetTheta+"; "+velocity.rotation+"/"+targetPhi, Side.SERVER);

		motionX = velocity.getXProjection();
		motionY = velocity.getYProjection();
		motionZ = velocity.getZProjection();

		if (targetColor != null) {
			colorTransitionFraction += 0.1F;
			if (colorTransitionFraction >= 1) {
				color = targetColor;
				targetColor = null;
				colorTransitionFraction = 0;
			}
		}
		dataWatcher.updateObject(30, this.calcRenderColor());

		if (worldObj.isRemote) {
			this.lifeParticles();
		}

		fallDistance = 0;
	}

	@SideOnly(Side.CLIENT)
	private void lifeParticles() {
		double d = 0.25;
		double px = ReikaRandomHelper.getRandomPlusMinus(posX, d);
		double py = ReikaRandomHelper.getRandomPlusMinus(posY, d);
		double pz = ReikaRandomHelper.getRandomPlusMinus(posZ, d);
		float g = (float)ReikaRandomHelper.getRandomPlusMinus(0.05, 0.025);
		if (rand.nextInt(4) == 0)
			g = -g;
		EntityLaserFX fx = new EntityLaserFX(color, worldObj, px, py, pz).setColor(this.getRenderColor()).setGravity(g);
		EntityLaserFX fx2 = new EntityLaserFX(color, worldObj, px, py, pz).setColor(0xffffff).setScale(0.42F).setGravity(g);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx2);
	}


	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);

		int c = nbt.getInteger("color");
		color = c >= 0 ? CrystalElement.elements[c] : CrystalElement.randomElement();

		isPylonSpawn = nbt.getBoolean("pylon");
		doDrops = nbt.getBoolean("dodrops");
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);

		if (color != null)
			nbt.setInteger("color", color.ordinal());

		nbt.setBoolean("dodrops", doDrops);
		nbt.setBoolean("pylon", isPylonSpawn);
	}

	private void die() {
		//particle effect
		if (worldObj.isRemote) {
			this.doDeathParticles(worldObj, posX, posY, posZ, this.getRenderColor());
		}
		this.setDead();
	}

	private void doBolt(EntityBallLightning other) {
		Vec3 vec = ReikaVectorHelper.getVec2Pt(posX, posY, posZ, other.posX, other.posY, other.posZ);
		//EntityGluon g = new EntityGluon(this, other);
		//worldObj.spawnEntityInWorld(g);
		targetColor = other.color;

		ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.GLUON.ordinal(), new DimensionTarget(worldObj), this.getEntityId(), other.getEntityId());

		double len = vec.lengthVector();
		for (double i = 0; i < len; i += 0.0625) {
			double dx = posX-i*vec.xCoord/len;
			double dy = posY-i*vec.yCoord/len;
			double dz = posZ-i*vec.zCoord/len;
			if (worldObj.isRemote)
				this.gluonParticles(other, dx, dy, dz, (float)(i/len));

			if (ChromaOptions.HOSTILEFOREST.getState()) {
				AxisAlignedBB box = AxisAlignedBB.getBoundingBox(dx, dy, dz, dx, dy, dz).expand(0.5, 0.5, 0.5);
				List<EntityLivingBase> elb = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, box);
				for (EntityLivingBase e : elb) {
					e.attackEntityFrom(DamageSource.generic, isPylonSpawn ? 4 : 1);
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public void doBoltClient(EntityBallLightning other) {
		targetColor = other.color;
	}

	@SideOnly(Side.CLIENT)
	private void gluonParticles(EntityBallLightning other, double dx, double dy, double dz, float frac) {
		int c = ReikaColorAPI.mixColors(other.getRenderColor(), this.getRenderColor(), frac);
		EntityBlurFX fx = new EntityBlurFX(worldObj, dx, dy, dz);
		fx.setLife(5).setColor(ReikaColorAPI.getRed(c), ReikaColorAPI.getGreen(c), ReikaColorAPI.getBlue(c));
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}

	private void onReceiveBolt(EntityBallLightning src) {
		targetColor = src.color;
	}

	@Override
	public AxisAlignedBB getCollisionBox(Entity entity) {
		if (entity instanceof EntityLivingBase && !(entity instanceof EntityBallLightning) && !worldObj.isRemote) {
			boolean flag = (entity instanceof EntityPlayer && ((EntityPlayer) entity).capabilities.isCreativeMode);
			if (!flag) {
				if (ChromaOptions.HOSTILEFOREST.getState()) {
					entity.attackEntityFrom(DamageSource.generic, 5);
				}
				if (entity instanceof EntityPlayer)
					ProgressStage.BALLLIGHTNING.stepPlayerTo((EntityPlayer)entity);
				this.die();
			}
		}
		return null;//AxisAlignedBB.getBoundingBox(posX, posY, posZ, posX, posY, posZ).expand(3, 3, 3);
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();

		if (!worldObj.isRemote && colorTransitionFraction == 0 && rand.nextInt(400) == 0) {
			EntityBallLightning e = ReikaEntityHelper.getNearestEntityOfSameType(this, 24);
			if (e != null && e.colorTransitionFraction == 0) {
				this.doBolt(e);
				e.onReceiveBolt(this);
			}
		}

		//if (ticksExisted%36 == 0) {
		//	ChromaSounds.POWER.playSound(this, 0.1F, 2F);
		//}

		if (!worldObj.isRemote && rand.nextInt(20) == 0) {
			if (!CrystalNetworker.instance.getNearbyPylons(worldObj, posX, posY, posZ, color, 24, false).isEmpty()) {
				this.heal(this.getMaxHealth());
			}
		}

		if (!worldObj.isRemote) {

			if (posY >= 128) {
				this.die();
			}
			else if (ticksExisted >= 12000 || rand.nextInt(12000-ticksExisted) == 0) {
				this.die();
			}
			else if (worldObj.isRaining() && rand.nextInt(80) == 0) {
				this.die();
			}
		}
	}

	@Override
	public boolean getCanSpawnHere() {
		return worldObj.canBlockSeeTheSky(MathHelper.floor_double(posX), MathHelper.floor_double(posY)+1, MathHelper.floor_double(posZ));
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 4;
	}

	@Override
	public void playLivingSound() {
		;//ChromaSounds.POWER.playSound(this, 1F, 2F);

		float p = CrystalMusicManager.instance.getRandomScaledDing(color);
		ChromaSounds.BALLLIGHTNING.playSound(this, 1, p);
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
		return "";
	}

	@Override
	protected String getDeathSound() {
		return "";
	}

	@Override
	public void onDeath(DamageSource src) {
		ChromaSounds.DISCHARGE.playSound(this, 1F, 2F);
		if (!worldObj.isRemote) {
			Entity e = src.getEntity();
			if (e instanceof EntityPlayer) {
				EntityPlayer ep = (EntityPlayer)e;
				if (doDrops && !ReikaPlayerAPI.isFake(ep)) {
					int looting = EnchantmentHelper.getLootingModifier((EntityPlayer)src.getEntity());
					ReikaItemHelper.dropItem(this, ReikaItemHelper.getSizedItemStack(ChromaStacks.beaconDust, rand.nextInt(1+looting*2)));
					if (looting > 1) {
						if (color.isPrimary())
							ReikaItemHelper.dropItem(this, ChromaStacks.purityDust);
						else
							ReikaItemHelper.dropItem(this, ChromaStacks.auraDust);
					}
				}
				ProgressStage.BALLLIGHTNING.stepPlayerTo(ep);
			}

			this.sendDeathParticles();
		}
	}

	private void sendDeathParticles() {
		ReikaPacketHelper.sendPositionPacket(ChromatiCraft.packetChannel, ChromaPackets.LIGHTNINGDIE.ordinal(), this, this.calcRenderColor(), new DimensionTarget(worldObj));
	}

	@SideOnly(Side.CLIENT)
	public static void receiveDeathParticles(World world, double dx, double dy, double dz, int color) {
		doDeathParticles(world, dx, dy, dz, color);
	}

	@SideOnly(Side.CLIENT)
	private static void doDeathParticles(World world, double dx, double dy, double dz, int color) {
		int n = 32;
		for (int i = 0; i < n; i++) {
			EntityCenterBlurFX fx = new EntityCenterBlurFX(world, dx, dy, dz).setColor(color).setScale(1.5F);
			fx.motionX = ReikaRandomHelper.getRandomPlusMinus(0, 0.2);
			fx.motionY = ReikaRandomHelper.getRandomPlusMinus(0, 0.2);
			fx.motionZ = ReikaRandomHelper.getRandomPlusMinus(0, 0.2);
			EntityCenterBlurFX fx2 = new EntityCenterBlurFX(world, dx, dy, dz).setColor(0xffffff).setScale(0.5F);
			fx2.motionX = fx.motionX;
			fx2.motionY = fx.motionY;
			fx2.motionZ = fx.motionZ;
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx2);
		}
	}

	@Override
	public void writeSpawnData(ByteBuf buf) {
		buf.writeInt(color != null ? color.ordinal() : -1);
	}

	@Override
	public void readSpawnData(ByteBuf buf) {
		int c = buf.readInt();
		color = c >= 0 && c < 16 ? CrystalElement.elements[c] : CrystalElement.randomElement();
	}

	/*
	public int getRenderColor() {
		return targetColor != null ? ReikaColorAPI.mixColors(targetColor.getColor(), color.getColor(), colorTransitionFraction) : color.getColor();
	}
	 */

	private int calcRenderColor() {
		return targetColor != null ? ReikaColorAPI.mixColors(targetColor.getColor(), color.getColor(), colorTransitionFraction) : color.getColor();
	}

	@SideOnly(Side.CLIENT)
	public int getRenderColor() {
		return dataWatcher.getWatchableObjectInt(30);
	}

	/*
	public int getRenderColor() {
		float frac = dataWatcher.getWatchableObjectFloat(31);
		int tgi = dataWatcher.getWatchableObjectInt(30);
		CrystalElement tg = tgi >= 0 ? CrystalElement.elements[tgi] : null;
		CrystalElement c = CrystalElement.elements[dataWatcher.getWatchableObjectInt(29)];
		return tg != null ? ReikaColorAPI.mixColors(tg.getColor(), c.getColor(), frac) : c.getColor();
	}
	 */

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

	public CrystalElement getElement() {
		return color;
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
