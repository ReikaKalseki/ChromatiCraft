package Reika.ChromatiCraft.ModInterface;

import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.Base.InertEntity;
import Reika.DragonAPI.Instantiable.ParticleController.EntityLockMotionController;
import Reika.DragonAPI.Interfaces.MotionController;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import Reika.VoidMonster.Auxiliary.VoidMonsterBait;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

@Strippable(value = "Reika.VoidMonster.Auxiliary.VoidMonsterBait")
public class EntityMonsterBait extends InertEntity implements IEntityAdditionalSpawnData, VoidMonsterBait {

	private static final int MIN_LIFE = 100; //5s
	private static final int MAX_LIFE = 600; //30s

	private UUID placer;
	private int lifespan;

	private int life;

	public EntityMonsterBait(World world) {
		super(world);
	}

	public EntityMonsterBait(World world, EntityPlayer ep) {
		super(world);
		this.setLifeSpan(ReikaRandomHelper.getRandomBetween(MIN_LIFE, MAX_LIFE));
		this.setLocationAndAngles(ep.posX, ep.posY+ep.getEyeHeight()/4F, ep.posZ, 0, 0);
		placer = ep.getPersistentID();
	}

	@Override
	protected void entityInit() {
		dataWatcher.addObject(24, 0);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		AxisAlignedBB box = ReikaAABBHelper.getEntityCenteredAABB(this, 16);
		EntityMob nearest = (EntityMob)worldObj.findNearestEntityWithinAABB(EntityMob.class, box, this);
		if (worldObj.isRemote) {
			life = dataWatcher.getWatchableObjectInt(24);
			if (life > 5)
				this.doParticles(nearest);
		}
		else {
			if (this.isActive() && nearest != null) {
				nearest.setTarget(this);
			}
			life--;
			if (life <= 0)
				this.setDead();
			else
				dataWatcher.updateObject(24, life);
		}
	}

	@SideOnly(Side.CLIENT)
	private void doParticles(EntityMob e) {
		int n = 1+rand.nextInt(4);
		for (int i = 0; i < n; i++) {
			double r = ReikaRandomHelper.getRandomPlusMinus(0.5, 0.0625);
			double[] xyz = ReikaPhysicsHelper.polarToCartesian(r, 0, rand.nextDouble()*360);
			int c = ReikaColorAPI.mixColors(0xa0a0a0, 0xffffff, Math.min(rand.nextFloat(), this.getBrightness()));
			float g = (float)ReikaRandomHelper.getRandomPlusMinus(0, 0.035);
			int l = Math.min(life, ReikaRandomHelper.getRandomBetween(10, 80));
			float s = (float)ReikaRandomHelper.getRandomBetween(0.5, 2);
			EntityBlurFX fx = new EntityBlurFX(worldObj, posX+xyz[0], posY+xyz[1], posZ+xyz[2]);
			fx.setColor(c).setGravity(g).setLife(l).setScale(s);
			fx.setIcon(ChromaIcons.FADE_GENTLE).setAlphaFading().setRapidExpand().forceIgnoreLimits();
			if (e != null) {
				MotionController m = new EntityLockMotionController(e, 0.03125/8, 0.125*4, 0.875);
				fx.setMotionController(m);
			}
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tag) {
		this.setLifeSpan(tag.getInteger("life"));
		if (tag.hasKey("placer"))
			placer = UUID.fromString(tag.getString("placer"));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tag) {
		if (placer != null)
			tag.setString("placer", placer.toString());
		tag.setInteger("life", lifespan);
	}

	@Override
	public void writeSpawnData(ByteBuf buf) {
		buf.writeInt(lifespan);
		if (placer != null) {
			buf.writeBoolean(true);
			ReikaPacketHelper.writeString(buf, placer.toString());
		}
		else {
			buf.writeBoolean(false);
		}
	}

	@Override
	public void readSpawnData(ByteBuf buf) {
		this.setLifeSpan(buf.readInt());
		boolean has = buf.readBoolean();
		if (has)
			placer = UUID.fromString(ReikaPacketHelper.readString(buf));
	}

	private void setLifeSpan(int val) {
		lifespan = life = val;
	}

	@Override
	public boolean isActive() {
		return placer != null && life > 0;
	}

	@Override
	public double maxRangeSquared() {
		return 100;
	}

	@Override
	public void attack(double dmg) {
		life -= dmg;
		if (life <= 0)
			this.setDead();
	}

	public int getLife() {
		return life;
	}

	public float getBrightness() {
		return life <= 0 ? 0 : life >= MIN_LIFE ? 1 : (float)Math.sqrt(life/(float)MIN_LIFE);
	}

}
