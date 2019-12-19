/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2018
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaShaders;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Formula.MathExpression;
import Reika.DragonAPI.Instantiable.Formula.PeriodicExpression;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Instantiable.ParticleController.CollectingPositionController;
import Reika.DragonAPI.Instantiable.ParticleController.FlashColorController;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import Reika.VoidMonster.API.NonTeleportingDamage;
import Reika.VoidMonster.Entity.EntityVoidMonster;
import Reika.VoidMonster.World.MonsterGenerator;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class VoidMonsterDestructionRitual {

	private final WorldLocation center;
	private final EntityPlayer startingPlayer;
	private final int monsterID;
	private final World world;

	private static final Random rand = new Random();
	private static final Collection<VoidMonsterDestructionRitual> activeRituals = new HashSet();
	private static int ritualCount = 0;

	public VoidMonsterDestructionRitual(TileEntityVoidMonsterTrap loc, EntityLiving e) {
		startingPlayer = loc.getPlacer();
		center = new WorldLocation(loc);
		monsterID = e.getEntityId();
		world = loc.worldObj;
	}

	public EntityLiving getEntity() {
		return (EntityLiving)world.getEntityByID(monsterID);
	}

	public boolean tick() {
		activeRituals.add(this);
		EntityLiving e = this.getEntity();
		for (Effects ef : Effects.list) {
			ef.tickShader(e);
			if (rand.nextInt(ef.effectChance) == 0) {
				ef.doEffectServer(this, e);
			}
		}
		return e.getHealth() <= 0;
	}

	public static void sync() {
		ritualCount = activeRituals.size();
		ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.VOIDMONSTERRITUALSET.ordinal(), PacketTarget.allPlayers.allPlayers, ritualCount);
	}

	@ModDependent(ModList.VOIDMONSTER)
	public void onCompletion() {
		activeRituals.remove(this);
		this.sync();
		MonsterGenerator.instance.addCooldown((EntityVoidMonster)this.getEntity(), 20*60*ReikaRandomHelper.getRandomBetween(20, 45));
	}

	public static void readSync(int amt) {
		ritualCount = amt;
	}

	public static boolean ritualsActive() {
		return ritualCount > 0;
	}

	public static enum Effects {
		COLLAPSING_SPHERE(	40, 20,	false),
		RAYS(				70, 40,	false),
		EXPLOSION(			200, 0, false),
		DISTORTION(			400, 20, true);

		private final int effectChance;
		private final int damageAmount;
		public final boolean hasTerrainShader;
		private final float shaderDecayFactor;
		private final float shaderDecayLinear;

		private final HashMap<String, Object> shaderData = new HashMap();
		private float shaderIntensity = 0;

		private static Effects[] list = values();
		private static final Collection<Effects> terrainShaderEffects = new HashSet();

		private Effects(int c, int dmg, boolean shader) {
			this(c, dmg, shader, 0, 0);
		}

		private Effects(int c, int dmg, boolean shader, float f, float l) {
			effectChance = c;
			damageAmount = dmg;
			hasTerrainShader = shader;
			shaderDecayFactor = f;
			shaderDecayLinear = l;
		}

		static {
			for (Effects e : list) {
				if (e.hasTerrainShader) {
					terrainShaderEffects.add(e);
				}
			}
		}

		public static Collection<Effects> getTerrainShaders() {
			return Collections.unmodifiableCollection(terrainShaderEffects);
		}

		private void fadeShader() {
			shaderIntensity = Math.max(0, shaderIntensity*shaderDecayFactor-shaderDecayLinear);
		}

		public Map<String, Object> getShaderData() {
			return Collections.unmodifiableMap(shaderData);
		}

		public float getShaderIntensity() {
			return shaderIntensity;
		}

		@ModDependent(ModList.VOIDMONSTER)
		public void doEffectServer(VoidMonsterDestructionRitual rit, EntityLiving e) {
			DamageSource src = new VoidMonsterRitualDamage(rit.startingPlayer);
			e.attackEntityFrom(src, damageAmount);
			switch(this) {
				case COLLAPSING_SPHERE:
					break;
				case RAYS:
					break;
				case EXPLOSION:
					e.worldObj.newExplosion(e, e.posX, e.posY, e.posZ, 9, true, true);
					break;
				case DISTORTION:
					break;
			}
			ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.VOIDMONSTERRITUAL.ordinal(), new PacketTarget.RadiusTarget(e, 128), e.getEntityId(), this.ordinal());
		}

		@SideOnly(Side.CLIENT)
		@ModDependent(ModList.VOIDMONSTER)
		public void doEffectClient(EntityLiving e) {
			float f = 1;
			double ex = e.posX;
			double ey = e.posY+1;
			double ez = e.posZ;
			shaderIntensity = 1;
			this.setShaderData(e);
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
				case DISTORTION:
					break;
			}
			ReikaSoundHelper.playClientSound(ChromaSounds.FLAREATTACK, e, 1, f, false);
		}

		private void tickShader(EntityLiving e) {
			switch(this) {
				case COLLAPSING_SPHERE:
					float r = (float)shaderData.get("radius");
					r -= 0.025F;
					shaderData.put("radius", r);
					ChromaShaders.VOIDRITUAL$SPHERE.setIntensity(shaderIntensity);
					ChromaShaders.VOIDRITUAL$SPHERE.getShader().updateEnabled();
					ChromaShaders.VOIDRITUAL$SPHERE.getShader().setFields(shaderData);
					break;
				case RAYS:
					break;
				case EXPLOSION:
					break;
				case DISTORTION:
					break;
			}
		}

		private void setShaderData(EntityLiving e) {
			switch(this) {
				case COLLAPSING_SPHERE:
					float r = 2;
					shaderData.put("radius", r);
					ChromaShaders.VOIDRITUAL$SPHERE.setIntensity(shaderIntensity);
					ChromaShaders.VOIDRITUAL$SPHERE.getShader().updateEnabled();
					ChromaShaders.VOIDRITUAL$SPHERE.getShader().setFields(shaderData);
					break;
				case RAYS:
					break;
				case EXPLOSION:
					break;
				case DISTORTION:
					break;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@ModDependent(ModList.VOIDMONSTER)
	public static void handlePacket(int entity, int effect) {
		Entity e = Minecraft.getMinecraft().theWorld.getEntityByID(entity);
		if (e instanceof EntityVoidMonster) {
			Effects.list[effect].doEffectClient((EntityVoidMonster)e);
		}
	}

	public static class VoidMonsterRitualDamage extends DamageSource implements NonTeleportingDamage {

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
