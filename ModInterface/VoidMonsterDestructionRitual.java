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
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaShaders;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickHandler;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickType;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
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
import Reika.VoidMonster.API.NonTeleportingDamage;
import Reika.VoidMonster.World.MonsterGenerator;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class VoidMonsterDestructionRitual {

	private final WorldLocation center;
	private final EntityPlayer startingPlayer;
	private final int monsterID;
	private final World world;

	private static final Random rand = new Random();
	private static final Collection<VoidMonsterDestructionRitual> activeRituals = new HashSet();
	private static final HashSet<Integer> ritualEntities = new HashSet();

	public VoidMonsterDestructionRitual(TileEntityVoidMonsterTrap loc, EntityLiving e) {
		this(new WorldLocation(loc), loc.getPlacer(), e);
	}

	private VoidMonsterDestructionRitual(WorldLocation loc, EntityPlayer ep, EntityLiving e) {
		startingPlayer = ep;
		center = loc;
		monsterID = e.getEntityId();
		world = ep.worldObj;
	}

	public EntityLiving getEntity() {
		return (EntityLiving)world.getEntityByID(monsterID);
	}

	private boolean tick() {
		activeRituals.add(this);
		EntityLiving e = this.getEntity();
		for (Effects ef : Effects.list) {
			if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
				if (ef.shaderIntensity > 0)
					ef.tickShader();
			}
			else {
				if (rand.nextInt(ef.effectChance) == 0) {
					ef.doEffectServer(this, e);
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
	private void onCompletion() {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			for (Effects ef : Effects.list) {
				ef.shaderData.clear();
				ef.shaderIntensity = 0;
			}
		}
		else {
			activeRituals.remove(this);
			sync();
			MonsterGenerator.instance.addCooldown(this.getEntity(), 20*60*ReikaRandomHelper.getRandomBetween(20, 45));
		}
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

	public static void setShaderFoci(Entity el) {
		for (Effects e : Effects.list) {
			e.setShaderFocus(el);
		}
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

		@SideOnly(Side.CLIENT)
		public void tickShader() {
			switch(this) {
				case COLLAPSING_SPHERE:
					float r = (float)shaderData.get("size");
					r -= 0.025F;
					shaderData.put("size", r);
					if (r <= 0)
						shaderIntensity = 0;
					ChromaShaders.VOIDRITUAL$SPHERE.setIntensity(shaderIntensity);
					ChromaShaders.VOIDRITUAL$SPHERE.getShader().updateEnabled();
					ChromaShaders.VOIDRITUAL$SPHERE.getShader().setFields(shaderData);
					break;
				case RAYS:
					break;
				case EXPLOSION:
					break;
				case DISTORTION:
					int has = (int)shaderData.get("wavePhase");
					has++;
					shaderData.put("wavePhase", has);
					if (has >= 200) {
						shaderIntensity = 0;
					}
					break;
			}
		}

		@SideOnly(Side.CLIENT)
		private void setShaderData(EntityLiving e) {
			switch(this) {
				case COLLAPSING_SPHERE:
					float r = 2;
					shaderData.put("size", r);
					ChromaShaders.VOIDRITUAL$SPHERE.setIntensity(shaderIntensity);
					ChromaShaders.VOIDRITUAL$SPHERE.getShader().updateEnabled();
					ChromaShaders.VOIDRITUAL$SPHERE.getShader().setFields(shaderData);
					break;
				case RAYS:
					break;
				case EXPLOSION:
					break;
				case DISTORTION:
					shaderData.put("wavePhase", -200);
					break;
			}
		}

		@SideOnly(Side.CLIENT)
		private void setShaderFocus(Entity e) {
			switch(this) {
				case COLLAPSING_SPHERE:
					ChromaShaders.VOIDRITUAL$SPHERE.getShader().setFocus(e);
					ChromaShaders.VOIDRITUAL$SPHERE.getShader().setMatricesToCurrent();
					break;
				case RAYS:
					break;
				case EXPLOSION:
					break;
				case DISTORTION:
					shaderData.put("wavePhase", -200);
					break;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@ModDependent(ModList.VOIDMONSTER)
	public static void handlePacket(int entity, int effect) {
		Entity e = Minecraft.getMinecraft().theWorld.getEntityByID(entity);
		Effects.list[effect].doEffectClient((EntityLiving)e);
	}

	public static void registerHandler() {
		TickRegistry.instance.registerTickHandler(RitualTickHandler.instance);
	}

	private static class RitualTickHandler implements TickHandler {

		private static final RitualTickHandler instance = new RitualTickHandler();

		private final HashSet<VoidMonsterDestructionRitual> active = new HashSet();

		private RitualTickHandler() {

		}

		@Override
		public void tick(TickType type, Object... tickData) {
			if (!active.isEmpty()) {
				Iterator<VoidMonsterDestructionRitual> it = active.iterator();
				while (it.hasNext()) {
					VoidMonsterDestructionRitual e = it.next();
					if (e.tick()) {
						e.onCompletion();
						it.remove();
					}
				}
			}
		}

		@Override
		public EnumSet<TickType> getType() {
			return EnumSet.of(TickType.SERVER, TickType.CLIENT);
		}

		@Override
		public boolean canFire(Phase p) {
			return p == Phase.END;
		}

		@Override
		public String getLabel() {
			return "voidritual";
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
