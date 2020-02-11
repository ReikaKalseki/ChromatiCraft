/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2018
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface.VoidRitual;

import java.util.Collection;
import java.util.HashSet;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.ModInterface.VoidRitual.VoidMonsterRitualClientEffects.EffectVisual;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Auxiliary.Trackers.TickScheduler;
import Reika.DragonAPI.Instantiable.Interpolation;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Event.ScheduledTickEvent;
import Reika.DragonAPI.Instantiable.Event.ScheduledTickEvent.ScheduledEvent;
import Reika.DragonAPI.Instantiable.Formula.MathExpression;
import Reika.DragonAPI.Instantiable.Formula.PeriodicExpression;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Instantiable.ParticleController.CollectingPositionController;
import Reika.DragonAPI.Instantiable.ParticleController.FlashColorController;
import Reika.DragonAPI.Libraries.ReikaNBTHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import Reika.VoidMonster.Entity.EntityVoidMonster;
import Reika.VoidMonster.World.MonsterGenerator;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class VoidMonsterDestructionRitual {

	private final WorldLocation center;
	private final EntityPlayer startingPlayer;
	private final int monsterID;
	private final World world;
	private final int targetDuration;

	private final Interpolation healthCurve = new Interpolation(false);

	private int tick;
	private int effectCooldown = 0;
	private long lastWorldTick = -1;

	private static final Random rand = new Random();
	private static final Collection<VoidMonsterDestructionRitual> activeRituals = new HashSet();
	private static final HashSet<Integer> ritualEntities = new HashSet();

	private static final int MIN_DURATION = 20*60;
	private static final int MAX_DURATION = 20*60*3;

	public VoidMonsterDestructionRitual(TileEntityVoidMonsterTrap loc, EntityLiving e) {
		this(new WorldLocation(loc), loc.getPlacer(), e);
	}

	private VoidMonsterDestructionRitual(WorldLocation loc, EntityPlayer ep, EntityLiving e) {
		startingPlayer = ep;
		center = loc;
		monsterID = e.getEntityId();
		world = ep.worldObj;
		targetDuration = ReikaRandomHelper.getRandomBetween(MIN_DURATION, MAX_DURATION);
		healthCurve.addPoint(0, 1);
		for (double d = 0.1; d < 1; d += 0.1) {
			double f = 1-d;
			healthCurve.addPoint(ReikaRandomHelper.getRandomPlusMinus(d, 0.045)*targetDuration, ReikaRandomHelper.getRandomPlusMinus(f, 0.2));
		}
		healthCurve.addPoint(targetDuration, 0);
	}

	public EntityLiving getEntity() {
		return (EntityLiving)world.getEntityByID(monsterID);
	}

	public boolean tick() {
		if (world.getTotalWorldTime() <= lastWorldTick)
			return false;
		lastWorldTick = world.getTotalWorldTime();

		activeRituals.add(this);

		tick++;
		if (effectCooldown > 0)
			effectCooldown--;

		EntityVoidMonster e = (EntityVoidMonster)this.getEntity();
		e.moveTowards(center.xCoord+0.5, center.yCoord+0.5, center.zCoord+0.5, 2);

		if (effectCooldown == 0) {
			for (Effects ef : Effects.list) {
				if (rand.nextInt(ef.effectChance) == 0) {
					ef.doEffectServer(this, e);
					effectCooldown = ef.cooldown;
					break;
				}
			}
		}

		return e.getHealth() <= 0;
	}

	public static void sync() {
		ritualEntities.clear();
		for (VoidMonsterDestructionRitual v : activeRituals) {
			ritualEntities.add(v.monsterID);
		}
		NBTTagCompound NBT = new NBTTagCompound();
		ReikaNBTHelper.writeCollectionToNBT(ritualEntities, NBT, "data");
		ReikaPacketHelper.sendNBTPacket(ChromatiCraft.packetChannel, ChromaPackets.VOIDMONSTERRITUALSET.ordinal(), NBT, PacketTarget.allPlayers.allPlayers);
	}

	@ModDependent(ModList.VOIDMONSTER)
	void onPrematureTermination() {
		this.onEnd();
		EntityVoidMonster el = (EntityVoidMonster)this.getEntity();
		el.heal(6000);
		el.setAttackTarget(startingPlayer);
		el.increaseDifficulty(2);
	}

	@ModDependent(ModList.VOIDMONSTER)
	void onCompletion() {
		MonsterGenerator.instance.addCooldown(this.getEntity(), 20*60*ReikaRandomHelper.getRandomBetween(20, 45));
		this.onEnd();
	}

	private void onEnd() {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			for (Effects ef : Effects.list) {
				ef.visuals.clearShader();
			}
		}
		else {
			activeRituals.remove(this);
			sync();
		}
	}

	public float getCurrentTargetHealthFraction() {
		return (float)healthCurve.getValue(tick);
	}

	@SideOnly(Side.CLIENT)
	public static void readSync(NBTTagCompound tag) {
		ritualEntities.clear();
		if (tag != null) {
			ReikaNBTHelper.readCollectionFromNBT(ritualEntities, tag, "data");
		}
	}

	public static boolean ritualsActive() {
		return !ritualEntities.isEmpty();
	}

	public static boolean isFocusOfActiveRitual(Entity e) {
		return ritualEntities.contains(e.getEntityId());
	}

	public static enum Effects {
		COLLAPSING_SPHERE(	70, 20, 40),
		RAYS(				40, 40, 20),
		EXPLOSION(			200, 0, 20),
		WAVE(				400, 20, 120),
		CURL(				400, 40, 30, 100),
		STRETCH(			400, 30, 20, 160),
		;

		private final int effectChance;
		private final int damageAmount;
		private final int damageDelay;
		private final int cooldown;

		@SideOnly(Side.CLIENT)
		public EffectVisual visuals;

		static Effects[] list = values();

		private Effects(int c, int dmg, int cl) {
			this(c, dmg, 0, cl);
		}

		private Effects(int c, int dmg, int del, int cl) {
			effectChance = c;
			cooldown = cl;
			damageAmount = dmg;
			damageDelay = del;
		}

		@ModDependent(ModList.VOIDMONSTER)
		public void doEffectServer(VoidMonsterDestructionRitual rit, EntityLiving e) {
			if (e.getHealth()/e.getMaxHealth() >= rit.getCurrentTargetHealthFraction()) {
				DamageSource src = new VoidMonsterRitualDamage(rit.startingPlayer);
				this.doAttack(e, src, damageAmount);
			}
			switch(this) {
				case COLLAPSING_SPHERE:
					break;
				case RAYS:
					break;
				case EXPLOSION:
					e.worldObj.newExplosion(e, e.posX, e.posY, e.posZ, 9, true, false);
					break;
				case WAVE:
					break;
				case CURL:
					break;
				case STRETCH:
					break;
			}
			ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.VOIDMONSTERRITUAL.ordinal(), new PacketTarget.RadiusTarget(e, 128), e.getEntityId(), this.ordinal());
		}

		private void doAttack(EntityLiving e, DamageSource src, int amt) {
			if (damageDelay > 0) {
				TickScheduler.instance.scheduleEvent(new ScheduledTickEvent(new ScheduledDamage(e, src, amt)), damageDelay);
			}
			else {
				runAttack(e, src, amt);
			}
		}

		@SideOnly(Side.CLIENT)
		@ModDependent(ModList.VOIDMONSTER)
		public void doEffectClient(EntityLiving e) {
			float f = 1;
			double ex = e.posX;
			double ey = e.posY+1;
			double ez = e.posZ;
			if (visuals != null) {
				visuals.activate(e);
			}
			switch(this) {
				case COLLAPSING_SPHERE:
					for (int i = 0; i < 128; i++) {
						double a1 = rand.nextDouble()*360;
						double a2 = rand.nextDouble()*360;
						double[] xyz = ReikaPhysicsHelper.polarToCartesian(4+rand.nextDouble()*0.25, a1, a2);
						double px = ex+xyz[0];
						double py = ey+xyz[1];
						double pz = ez+xyz[2];
						EntityBlurFX fx = new EntityBlurFX(e.worldObj, px, py, pz).setAlphaFading();
						int t = ReikaRandomHelper.getRandomBetween(5, 8);
						fx.setPositionController(new CollectingPositionController(px, py, pz, ex, ey, ez, t));
						fx.setLife(t+1).setScale(1+rand.nextFloat()*0.5F);
						Minecraft.getMinecraft().effectRenderer.addEffect(fx);
					}
					break;
				case RAYS:
					for (int k = 0; k < 9; k++) {
						double a1 = rand.nextDouble()*360;
						double a2 = rand.nextDouble()*360;
						for (int i = 0; i < 128; i++) {
							double[] xyz = ReikaPhysicsHelper.polarToCartesian(rand.nextDouble()*96, a1, a2);
							double px = ex+xyz[0];
							double py = ey+xyz[1];
							double pz = ez+xyz[2];
							px = ReikaRandomHelper.getRandomPlusMinus(px, 0.125);
							py = ReikaRandomHelper.getRandomPlusMinus(py, 0.125);
							pz = ReikaRandomHelper.getRandomPlusMinus(pz, 0.125);
							EntityBlurFX fx = new EntityBlurFX(e.worldObj, px, py, pz).setAlphaFading();
							fx.setLife(40).setScale(1.5F+rand.nextFloat()*1.5F);
							double d = rand.nextDouble()*360;
							MathExpression exp = new PeriodicExpression().addWave(1, 1, d).addWave(0.5, 2, d+90).addWave(0.125, 4, d).normalize();
							fx.setColorController(new FlashColorController(exp, 0xffffff, 0x000000));
							Minecraft.getMinecraft().effectRenderer.addEffect(fx);
						}
					}
					f = 0.5F;
					break;
				case EXPLOSION:
					break;
				case WAVE:
					break;
				case CURL:
					break;
				case STRETCH:
					break;
			}
			ReikaSoundHelper.playClientSound(ChromaSounds.FLAREATTACK, e, 1, f, false);
		}
	}

	@SideOnly(Side.CLIENT)
	@ModDependent(ModList.VOIDMONSTER)
	public static void handlePacket(int entity, int effect) {
		Entity e = Minecraft.getMinecraft().theWorld.getEntityByID(entity);
		Effects.list[effect].doEffectClient((EntityLiving)e);
	}

	private static void runAttack(EntityLiving e, DamageSource src, int amt) {
		if (e.getHealth() >= e.getHealth()) { //kill
			e.setHealth(0.1F);
			e.attackEntityFrom(src, 1F);
		}
		else {
			e.setHealth(e.getHealth()-amt+1);
			e.attackEntityFrom(src, 1F);
		}
	}

	private static class ScheduledDamage implements ScheduledEvent {

		private final EntityLiving entity;
		private final DamageSource source;
		private final int amount;

		public ScheduledDamage(EntityLiving e, DamageSource src, int amt) {
			entity = e;
			source = src;
			amount = amt;
		}

		@Override
		public void fire() {
			runAttack(entity, source, amount);
		}

		@Override
		public boolean runOnSide(Side s) {
			return s == Side.SERVER;
		}

	}

	public static class VoidMonsterRitualDamage extends DamageSource {

		private final EntityPlayer player;

		public VoidMonsterRitualDamage(EntityPlayer ep) {
			super("voidmonster.ritual");
			player = ep;
		}

		@Override
		public Entity getEntity() {
			return player;
		}

		@Override
		public boolean isMagicDamage() {
			return true;
		}

	}

}
