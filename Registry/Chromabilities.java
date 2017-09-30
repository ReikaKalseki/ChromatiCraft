/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Registry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockTNT;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.BlockFluidBase;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.INode;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.API.AbilityAPI.Ability;
import Reika.ChromatiCraft.Auxiliary.ChromaDescriptions;
import Reika.ChromatiCraft.Auxiliary.ChromaFX;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Auxiliary.RainbowTreeEffects;
import Reika.ChromatiCraft.Auxiliary.Ability.AbilityHelper;
import Reika.ChromatiCraft.Auxiliary.Ability.LightCast;
import Reika.ChromatiCraft.Auxiliary.Event.DimensionPingEvent;
import Reika.ChromatiCraft.Auxiliary.Render.ChromaFontRenderer.FontType;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator.StructurePair;
import Reika.ChromatiCraft.Entity.EntityAbilityFireball;
import Reika.ChromatiCraft.Entity.EntityNukerBall;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.ItemElementCalculator;
import Reika.ChromatiCraft.Magic.PlayerElementBuffer;
import Reika.ChromatiCraft.ModInterface.TileEntityLifeEmitter;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityCenterBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityFireFX;
import Reika.ChromatiCraft.World.Dimension.ChunkProviderChroma;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Auxiliary.ProgressiveRecursiveBreaker;
import Reika.DragonAPI.Auxiliary.ProgressiveRecursiveBreaker.ProgressiveBreaker;
import Reika.DragonAPI.Auxiliary.Trackers.TickScheduler;
import Reika.DragonAPI.Base.BlockTieredResource;
import Reika.DragonAPI.Instantiable.EntityTumblingBlock;
import Reika.DragonAPI.Instantiable.FlyingBlocksExplosion;
import Reika.DragonAPI.Instantiable.FlyingBlocksExplosion.TumbleCreator;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockArray;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockBox;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Data.Immutable.ScaledDirection;
import Reika.DragonAPI.Instantiable.Data.Maps.ItemHashMap;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap.HashSetFactory;
import Reika.DragonAPI.Instantiable.Event.ScheduledTickEvent;
import Reika.DragonAPI.Instantiable.Event.ScheduledTickEvent.ScheduledSoundEvent;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Interfaces.Block.SemiUnbreakable;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaVectorHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.ThaumItemHelper;
import Reika.DragonAPI.ModRegistry.ModWoodList;
import Reika.ReactorCraft.Entities.EntityRadiation;

import com.google.common.collect.HashBiMap;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public enum Chromabilities implements Ability {

	REACH(null, true),
	MAGNET(Phase.END, false),
	SONIC(null, true),
	SHIFT(null, false),
	HEAL(null, false),
	SHIELD(Phase.START, false),
	FIREBALL(null, false),
	COMMUNICATE(Phase.START, false),
	HEALTH(null, true),
	PYLON(null, false),
	LIGHTNING(null, false),
	LIFEPOINT(null, false, ModList.BLOODMAGIC),
	DEATHPROOF(null, false),
	HOTBAR(null, true),
	SHOCKWAVE(null, true),
	TELEPORT(null, true),
	LEECH(null, false),
	FLOAT(Phase.END, true),
	SPAWNERSEE(null, true),
	BREADCRUMB(null, true),
	RANGEDBOOST(null, false),
	DIMPING(null, false),
	DASH(Phase.END, false),
	LASER(null, true),
	FIRERAIN(Phase.START, true),
	KEEPINV(null, false),
	ORECLIP(Phase.START, true),
	DOUBLECRAFT(null, true),
	GROWAURA(Phase.END, true),
	RECHARGE(null, false),
	MEINV(null, false, ModList.APPENG),
	MOBSEEK(null, true),
	BEEALYZE(null, true),
	NUKER(Phase.START, false),
	LIGHTCAST(null, false),
	JUMP(null, false);


	private final boolean tickBased;
	private final Phase tickPhase;
	private final boolean actOnClient;
	private ModList dependency;

	public static final UUID HEALTH_UUID = UUID.fromString("71d6a916-a54b-11e7-abc4-cec278b6b50a");
	public static final int MAX_REACH = 128;

	private Chromabilities(Phase tick, boolean client) {
		this(tick, client, null);
	}

	private Chromabilities(Phase tick, boolean client, ModList mod) {
		tickBased = tick != null;
		tickPhase = tick;
		actOnClient = client;
		dependency = mod;
	}

	private static final String NBT_TAG = "chromabilities";
	private static final HashMap<String, Ability> tagMap = new HashMap();

	private static final Chromabilities[] abilities = values();

	private static final HashMap<String, Ability> abilityMap = new HashMap();
	private static final HashBiMap<Integer, Ability> intMap = HashBiMap.create();
	private static int maxID = 0;
	private static ArrayList<Ability> sortedList;
	private static final MultiMap<Phase, Ability> tickAbilities = new MultiMap(new HashSetFactory());

	public static Ability getAbility(String id) {
		return abilityMap.get(id);
	}

	public static List<Ability> getAbilities() {
		return Collections.unmodifiableList(sortedList);
	}

	public static Collection<Ability> getAbilitiesAvailableToPlayer(EntityPlayer ep) {
		Collection<Ability> li = new ArrayList();
		for (Ability c : sortedList) {
			if (c.isAvailableToPlayer(ep))
				li.add(c);
		}
		return li;
	}

	public static Collection<Ability> getAbilitiesForTick(Phase p) {
		return Collections.unmodifiableCollection(tickAbilities.get(p));
	}

	public boolean isAvailableToPlayer(EntityPlayer ep) {
		return AbilityHelper.instance.playerCanGetAbility(this, ep);
	}

	public String getDisplayName() {
		String s = StatCollector.translateToLocal("chromability."+this.name().toLowerCase(Locale.ENGLISH));
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT && DragonAPICore.hasGameLoaded()) {
			s = this.deobfuscateIf(s);
		}
		return s;
	}

	@SideOnly(Side.CLIENT)
	private String deobfuscateIf(String s) {
		if (!ChromaResearchManager.instance.playerHasFragment(Minecraft.getMinecraft().thePlayer, ChromaResearch.getPageFor(this))) {
			s = FontType.OBFUSCATED.id+s;
		}
		return s;
	}

	public String getDescription() {
		return ChromaDescriptions.getAbilityDescription(this);
	}

	public boolean isTickBased() {
		return tickBased;
	}

	public Phase getTickPhase() {
		return tickPhase;
	}

	public boolean actOnClient() {
		return actOnClient;
	}

	public ModList getModDependency() {
		return dependency;
	}

	public boolean isDummiedOut() {
		if (this == HOTBAR)
			return true;
		if (DragonAPICore.isReikasComputer() && ReikaObfuscationHelper.isDeObfEnvironment())
			return false;
		return dependency != null && !dependency.isLoaded();
	}

	public ElementTagCompound getTickCost(EntityPlayer ep) {
		return this.getTickCost(this, ep);
	}

	public static ElementTagCompound getTickCost(Ability c, EntityPlayer ep) {
		if (c.isTickBased() || c.costsPerTick()) {
			return AbilityHelper.instance.getUsageElementsFor(c, ep);
		}
		return null;
	}

	public boolean costsPerTick() {
		switch(this) {
			case HEALTH:
			case PYLON:
			case LEECH:
			case DEATHPROOF:
			case BREADCRUMB:
			case SPAWNERSEE:
			case REACH:
			case RANGEDBOOST:
			case FIRERAIN:
			case KEEPINV:
			case DASH:
			case ORECLIP:
			case GROWAURA:
			case RECHARGE:
			case MEINV:
			case MOBSEEK:
			case BEEALYZE:
			case NUKER:
				return true;
			default:
				return false;
		}
	}

	public void apply(EntityPlayer ep) {
		switch(this) {
			case MAGNET:
				this.attractItemsAndXP(ep, 24, AbilityHelper.instance.isMagnetNoClip(ep));
				break;
			case SHIELD:
				this.stopArrows(ep);
				break;
			case COMMUNICATE:
				this.deAggroMobs(ep);
				break;
			case FLOAT:
				this.waterRun(ep);
				break;
			case DASH:
				PotionEffect pot = ep.getActivePotionEffect(Potion.moveSpeed);
				if (pot != null && pot.getAmplifier() >= 60)
					ep.stepHeight = 2.75F;
				else
					ep.stepHeight = 0.5F;
				break;
			case FIRERAIN:
				this.tickFireRain(ep);
				break;
			case GROWAURA:
				this.doGrowthAura(ep);
				break;
			case ORECLIP:
				setNoclipState(ep, true);
				break;
			case BEEALYZE:
				analyzeBees(ep);
				break;
			case NUKER:
				breakSurroundingBlocks(ep);
				break;
			default:
				break;
		}
	}

	public boolean trigger(EntityPlayer ep, int data) {
		switch(this) {
			case REACH:
				this.setReachDistance(ep, this.enabledOn(ep) ? AbilityHelper.REACH_SCALE[data] : -1);
				return true;
			case SONIC:
				this.destroyBlocksAround(ep, data);
				return true;
			case SHIFT:
				if (this.enabledOn(ep)) {
					AbilityHelper.instance.startDrawingBoxes(ep);
					AbilityHelper.instance.shifts.put(ep, new ScaledDirection(ReikaEntityHelper.getDirectionFromEntityLook(ep, true), data));
				}
				else {
					AbilityHelper.instance.stopDrawingBoxes(ep);
					AbilityHelper.instance.shifts.remove(ep);
				}
				return true;
			case HEAL:
				this.healPlayer(ep, data);
				return true;
			case FIREBALL:
				this.launchFireball(ep, data);
				return true;
			case HEALTH:
				this.setPlayerMaxHealth(ep, this.enabledOn(ep) ? data : 0);
				return true;
			case LIGHTNING:
				return this.spawnLightning(ep, data);
			case LIFEPOINT:
				this.convertBufferToLP(ep, data);
				return true;
			case HOTBAR:
				addInvPage(ep);
				return true;
			case SHOCKWAVE:
				causeShockwave(ep);
				return true;
			case TELEPORT:
				teleportPlayerMenu(ep);
				return true;
			case BREADCRUMB:
				AbilityHelper.instance.setPathLength(ep, this.enabledOn(ep) ? ReikaMathLibrary.intpow2(2, data) : 0);
				return true;
			case DIMPING:
				doDimensionPing(ep);
				return true;
			case LASER:
				return doLaserPulse(ep);
			case LIGHTCAST:
				return doLightCast(ep);
			case JUMP:
				return doJump(ep, data);
			default:
				return false;
		}
	}

	public static void triggerAbility(EntityPlayer ep, Ability a, int data, boolean dispatchPacket) {
		if (ep.worldObj.isRemote) {
			ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.ABILITY.ordinal(), ep.worldObj, 0, 0, 0, getAbilityInt(a), data);

			if (!a.actOnClient())
				return;
		}
		else if (a.actOnClient() && dispatchPacket) { //notify other players
			ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.ABILITYSEND.ordinal(), ep.worldObj, 0, 0, 0, getAbilityInt(a), data, ep.getEntityId());
		}

		ProgressStage.ABILITY.stepPlayerTo(ep);
		ElementTagCompound use = AbilityHelper.instance.getUsageElementsFor(a, ep);
		if (a == HEALTH)
			use.scale(10*(1+data));
		if (a == SHIFT)
			use.scale(10);
		if (a == LIGHTNING)
			use.scale(10*(1+data*data));
		if (a == BREADCRUMB)
			use.scale(5*(1+data*4));
		if (a == LIFEPOINT)
			use.scale(5);
		if (a == DIMPING)
			use.scale(125);
		if (a == LASER)
			use.scale(800);
		if (a == LIGHTCAST)
			use.scale(20);
		if (a == JUMP)
			use.scale(1+data);

		boolean flag = enabledOn(ep, a) || a.isPureEventDriven();
		setToPlayer(ep, !flag, a);
		if (flag) {
			a.onRemoveFromPlayer(ep);
		}

		if (a == MAGNET)
			AbilityHelper.instance.setNoClippingMagnet(ep, !flag && data > 0);

		if (a.isTickBased()) {

		}
		else {
			if (a.trigger(ep, data)) {
				PlayerElementBuffer.instance.removeFromPlayer(ep, use);
			}
		}
	}

	public boolean isPureEventDriven() {
		switch(this) {
			case SONIC:
			case HEAL:
			case FIREBALL:
			case LIGHTNING:
			case HOTBAR:
			case SHOCKWAVE:
			case TELEPORT:
			case DIMPING:
			case LASER:
			case LIGHTCAST:
			case JUMP:
				return true;
			default:
				return false;
		}
	}

	public static ArrayList<Ability> getFrom(EntityPlayer ep) {
		ArrayList<Ability> li = new ArrayList();
		NBTTagCompound nbt = ep.getEntityData();
		NBTTagCompound abilities = nbt.getCompoundTag(NBT_TAG);
		if (abilities != null && !abilities.hasNoTags()) {
			Iterator<String> it = abilities.func_150296_c().iterator();
			while (it.hasNext()) {
				String n = it.next();
				//ReikaJavaLibrary.pConsole(n+":"+abilities.getBoolean(n), Side.SERVER);
				if (abilities.getBoolean(n)) {
					Ability c = tagMap.get(n);
					if (c != null)
						li.add(c);
				}
			}
		}
		return li;
	}

	public static ArrayList<Ability> getAvailableFrom(EntityPlayer ep) {
		ArrayList<Ability> li = new ArrayList();
		NBTTagCompound nbt = ep.getEntityData();
		NBTTagCompound abilities = nbt.getCompoundTag(NBT_TAG);
		if (abilities != null && !abilities.hasNoTags()) {
			Iterator<String> it = abilities.func_150296_c().iterator();
			while (it.hasNext()) {
				String n = it.next();
				//ReikaJavaLibrary.pConsole(n+":"+abilities.getBoolean(n), Side.SERVER);
				Ability c = tagMap.get(n);
				if (c != null)
					li.add(c);
			}
		}
		return li;
	}

	public static HashMap<Ability, Boolean> getAbilitiesOn(EntityPlayer ep) {
		HashMap<Ability, Boolean> li = new HashMap();
		NBTTagCompound nbt = ep.getEntityData();
		NBTTagCompound abilities = nbt.getCompoundTag(NBT_TAG);
		if (abilities != null && !abilities.hasNoTags()) {
			Iterator<String> it = abilities.func_150296_c().iterator();
			while (it.hasNext()) {
				String n = it.next();
				//ReikaJavaLibrary.pConsole(n+":"+abilities.getBoolean(n), Side.SERVER);
				Ability c = tagMap.get(n);
				if (c != null)
					li.put(c, abilities.getBoolean(n));
			}
		}
		return li;
	}

	public boolean enabledOn(EntityPlayer ep) {
		return enabledOn(ep, this);
	}

	public boolean playerHasAbility(EntityPlayer ep) {
		return playerHasAbility(ep, this);
	}

	public void setToPlayer(EntityPlayer ep, boolean set) {
		this.setToPlayer(ep, set, this);
	}

	public void give(EntityPlayer ep) {
		give(ep, this);
	}

	public static void give(EntityPlayer ep, Ability a) {
		setToPlayer(ep, false, a, true);
	}

	public void removeFromPlayer(EntityPlayer ep) {
		removeFromPlayer(ep, this);
	}

	public static boolean enabledOn(EntityPlayer ep, Ability a) {
		NBTTagCompound nbt = ep.getEntityData();
		NBTTagCompound abilities = nbt.getCompoundTag(NBT_TAG);
		return abilities != null && abilities.getBoolean(a.getID());
	}

	public static boolean playerHasAbility(EntityPlayer ep, Ability a) {
		NBTTagCompound nbt = ep.getEntityData();
		NBTTagCompound abilities = nbt.getCompoundTag(NBT_TAG);
		return abilities != null && abilities.hasKey(a.getID());
	}

	public static void setToPlayer(EntityPlayer ep, boolean set, Ability a) {
		setToPlayer(ep, set, a, false);
	}

	private static void setToPlayer(EntityPlayer ep, boolean set, Ability a, boolean force) {
		NBTTagCompound nbt = ep.getEntityData();
		NBTTagCompound abilities = nbt.getCompoundTag(NBT_TAG);
		if (abilities == null) {
			abilities = new NBTTagCompound();
		}
		if (force || set || abilities.hasKey(a.getID()))
			abilities.setBoolean(a.getID(), set);
		nbt.setTag(NBT_TAG, abilities);
		if (ep instanceof EntityPlayerMP)
			ReikaPlayerAPI.syncCustomData((EntityPlayerMP)ep);
	}

	public static void removeFromPlayer(EntityPlayer ep, Ability a) {
		NBTTagCompound nbt = ep.getEntityData();
		NBTTagCompound abilities = nbt.getCompoundTag(NBT_TAG);
		if (abilities == null) {
			abilities = new NBTTagCompound();
		}
		abilities.removeTag(a.getID());
		a.onRemoveFromPlayer(ep);
		if (ep instanceof EntityPlayerMP)
			ReikaPlayerAPI.syncCustomData((EntityPlayerMP)ep);
	}

	public void onRemoveFromPlayer(EntityPlayer ep) {
		if (this == REACH)
			this.setReachDistance(ep, -1);
		else if (this == HEALTH)
			this.setPlayerMaxHealth(ep, 0);
		else if (this == MAGNET)
			AbilityHelper.instance.setNoClippingMagnet(ep, false);
		else if (this == ORECLIP) {
			setNoclipState(ep, false);
		}
	}

	private static void tickFireRain(EntityPlayer ep) {
		World world = ep.worldObj;
		if (world.isRemote) {
			doFireRainParticles(ep);
		}
		else {
			int x = MathHelper.floor_double(ep.posX);
			int z = MathHelper.floor_double(ep.posZ);
			int dx = ReikaRandomHelper.getRandomPlusMinus(x, 128);
			int dz = ReikaRandomHelper.getRandomPlusMinus(z, 128);
			int dy = world.getTopSolidOrLiquidBlock(dx, dz);
			ReikaWorldHelper.ignite(world, dx, dy, dz);
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					for (int k = -1; k <= 1; k++) {
						int ddx = dx+i;
						int ddy = dy+j;
						int ddz = dz+k;
						if (ModWoodList.getModWoodFromLeaf(world.getBlock(ddx, ddy, ddz), world.getBlockMetadata(ddx, ddy, ddz)) == ModWoodList.DARKWOOD) {
							world.setBlock(ddx, ddy, ddz, Blocks.fire);
						}
					}
				}
			}
			/*
			if (world.rand.nextInt(20) == 0) {
				ReikaWorldHelper.temperatureEnvironment(world, dx, dy, dz, 910);
			}
			else if (world.rand.nextInt(200) == 0) {
				ReikaWorldHelper.temperatureEnvironment(world, dx, dy, dz, 1510);
			}
			 */
			ChromaSounds.FIRE.playSoundAtBlock(world, dx, dy, dz, 1.5F, 1+world.rand.nextFloat()*0.5F);
			if (ep.ticksExisted%4 == 0) {
				ChromaSounds.FIRE.playSound(ep, 0.3F, 0.2F+world.rand.nextFloat());
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private static void doFireRainParticles(EntityPlayer ep) {
		int n = 1+ep.worldObj.rand.nextInt(8);
		for (int i = 0; i < n; i++) {
			double rx = ReikaRandomHelper.getRandomPlusMinus(ep.posX, 32);
			double rz = ReikaRandomHelper.getRandomPlusMinus(ep.posZ, 32);
			double ry = ReikaRandomHelper.getRandomPlusMinus(ep.posY+32, 32);
			float g = (float)ReikaRandomHelper.getRandomPlusMinus(0.5, 0.25);
			int l = 200;
			EntityFX fx = new EntityFireFX(ep.worldObj, rx, ry, rz).setGravity(g).setScale(8).setLife(l);
			if (ep.worldObj.rand.nextInt(8) == 0)
				((EntityFireFX)fx).setExploding();
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	private static boolean doLightCast(EntityPlayer ep) {
		Coordinate c = new Coordinate(ep).offset(0, 1, 0);
		ProgressiveBreaker b = ProgressiveRecursiveBreaker.instance.addCoordinateWithReturn(ep.worldObj, c.xCoord, c.yCoord, c.zCoord, 200);
		b.call = new LightCast(ep);
		b.player = ep;
		b.hungerFactor = 0;
		b.causeUpdates = false;
		b.breakAir = true;
		ChromaSounds.LIGHTCAST.playSound(ep);
		return true;
	}

	private static boolean doJump(EntityPlayer ep, int power) {
		ep.motionY += power/2D*(1+ep.worldObj.rand.nextDouble());
		ep.velocityChanged = true;
		ep.fallDistance -= 100;
		ChromaSounds.RIFT.playSound(ep, 1, 2);
		return true;
	}

	private static boolean doLaserPulse(EntityPlayer ep) {
		World world = ep.worldObj;
		MovingObjectPosition p = ReikaPlayerAPI.getLookedAtBlock(ep, 128, false);
		if (p == null || p.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK)
			return false;
		if (!world.canBlockSeeTheSky(p.blockX, p.blockY+1, p.blockZ))
			return false;
		if (world.provider.dimensionId == ExtraChromaIDs.DIMID.getValue() && p.blockY < 90)
			return false;
		double px = p.blockX+0.5;
		double py = p.blockY+0.5;
		double pz = p.blockZ+0.5;
		if (world.isRemote) {
			doLaserPunchParticles(ep, px, py, pz);
		}
		else {
			AxisAlignedBB box = AxisAlignedBB.getBoundingBox(px, py, pz, px, py, pz).expand(64, 32, 64);
			List<EntityLiving> li = world.getEntitiesWithinAABB(EntityLiving.class, box);
			for (EntityLiving e : li) {
				e.attackEntityFrom(new ReikaEntityHelper.WrappedDamageSource(ChromatiCraft.pylonDamage[CrystalElement.BLUE.ordinal()], ep), Integer.MAX_VALUE);
			}
			double r = ReikaRandomHelper.getRandomPlusMinus(10D, 2D);
			double h = ReikaRandomHelper.getRandomBetween(r, r*4);
			for (int i = -(int)Math.ceil(r); i <= Math.ceil(r); i++) {
				for (int k = -(int)Math.ceil(r); k <= Math.ceil(r); k++) {
					for (int j = -(int)Math.ceil(h); j <= Math.ceil(h); j++) {
						if (ReikaMathLibrary.isPointInsideEllipse(i, j, k, r, h, r)) {
							double d = ReikaMathLibrary.py3d(i, 0, k);
							double dx = px+i;
							double dy = py+j;
							double dz = pz+k;
							int dpx = MathHelper.floor_double(dx);
							int dpy = MathHelper.floor_double(dy);
							int dpz = MathHelper.floor_double(dz);
							Block b = world.getBlock(dpx, dpy, dpz);
							int meta = world.getBlockMetadata(dpx, dpy, dpz);
							if (b == Blocks.bedrock && dpy <= 4)
								continue;
							if (b instanceof SemiUnbreakable && ((SemiUnbreakable)b).isUnbreakable(world, dpx, dpy, dpz, meta))
								continue;
							if (ReikaPlayerAPI.playerCanBreakAt((WorldServer)world, dpx, dpy, dpz, (EntityPlayerMP)ep)) {
								boolean flag = false;
								if ((0.5+0.5*world.rand.nextDouble())*d < r*(0.5+world.rand.nextDouble()*0.5)) {
									if (ReikaBlockHelper.isOre(b, meta)) {
										ItemStack is = ReikaBlockHelper.getSilkTouch(world, dpx, dpy, dpz, b, meta, ep, false);
										ItemStack out = FurnaceRecipes.smelting().getSmeltingResult(is);
										if (out != null) {
											out = out.copy();
											out.stackSize *= 2;
											EntityItem ei = ReikaItemHelper.dropItem(world, dx, dy, dz, out);
											ReikaEntityHelper.setInvulnerable(ei, true);
										}
									}
									else if (b instanceof BlockTieredResource && ((BlockTieredResource)b).isPlayerSufficientTier(world, dpx, dpy, dpz, ep)) {
										for (ItemStack is : ((BlockTieredResource)b).getHarvestResources(world, dpx, dpy, dpz, 3, ep)) {
											EntityItem ei = ReikaItemHelper.dropItem(world, dx, dy, dz, is);
											ReikaEntityHelper.setInvulnerable(ei, true);
										}
									}
									if (b instanceof BlockTNT) {
										((BlockTNT)b).func_150114_a(world, dpx, dpy, dpz, 1, ep); //NOT meta
									}
									world.setBlock(dpx, dpy, dpz, Blocks.air);
								}
							}
						}
					}
				}
			}
			for (float f = 0.1F; f <= 2; f *= 2) {
				ReikaSoundHelper.playSoundFromServer(world, px, py, pz, "random.explode", 2, f, true);
				ReikaSoundHelper.playSoundFromServer(world, ep.posX, ep.posY, ep.posZ, "random.explode", 1, f, true);
			}
			ChromaSounds.LASER.playSound(ep, 2, 1);
		}
		return true;
	}

	@SideOnly(Side.CLIENT)
	private static void doLaserPunchParticles(EntityPlayer ep, double px, double py, double pz) {
		int n = 2048+ep.worldObj.rand.nextInt(16384);
		double maxr = 32;
		for (int i = 0; i < n; i++) {
			double a = ep.worldObj.rand.nextDouble()*360;
			double r = ReikaRandomHelper.getRandomBetween(0, maxr);
			double rx = px+r*Math.sin(Math.toRadians(a));
			double rz = pz+r*Math.cos(Math.toRadians(a));
			double ry = ReikaRandomHelper.getRandomPlusMinus(py+1.5, 1)+(ep.worldObj.getTopSolidOrLiquidBlock(MathHelper.floor_double(rx), MathHelper.floor_double(rz))-ep.worldObj.getTopSolidOrLiquidBlock(MathHelper.floor_double(px), MathHelper.floor_double(pz)));
			int l = 40+ep.worldObj.rand.nextInt(120);
			double v = ReikaRandomHelper.getRandomPlusMinus(0.25, 0.125)/32D;
			double vx = (rx-px)*v;
			double vz = (rz-pz)*v;
			double vy = ReikaRandomHelper.getRandomPlusMinus(0, 0.125);
			float f = (float)(r/maxr);
			float s = (float)ReikaRandomHelper.getRandomPlusMinus(8D, 4D)+4*(1-f);
			int c = f < 0.5 ? ReikaColorAPI.mixColors(0xffffff, 0x00a0ff, 1-(f*2)) : ReikaColorAPI.mixColors(0x0000ff, 0x00a0ff, (f-0.5F)*2);
			EntityFX fx = new EntityBlurFX(ep.worldObj, rx, ry, rz, vx, vy, vz).setColor(c).setScale(s).setLife(l).setRapidExpand().setColliding();
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}

		for (int i = 0; i < n/16; i++) {
			double a = ep.worldObj.rand.nextDouble()*360;
			double r = ReikaRandomHelper.getRandomPlusMinus(maxr+24, 4);
			double rx = px+r*Math.sin(Math.toRadians(a));
			double rz = pz+r*Math.cos(Math.toRadians(a));
			double ry = ReikaRandomHelper.getRandomPlusMinus(py+1.5, 1)+(ep.worldObj.getTopSolidOrLiquidBlock(MathHelper.floor_double(rx), MathHelper.floor_double(rz))-ep.worldObj.getTopSolidOrLiquidBlock(MathHelper.floor_double(px), MathHelper.floor_double(pz)));
			int l = 40+ep.worldObj.rand.nextInt(120);
			double v = ReikaRandomHelper.getRandomPlusMinus(0.125, 0.0625)/64D;
			double vx = (rx-px)*v;
			double vz = (rz-pz)*v;
			double vy = ReikaRandomHelper.getRandomPlusMinus(0, 0.125);
			float s = (float)ReikaRandomHelper.getRandomPlusMinus(16D, 4D);
			int c = 0xa000ff;
			EntityFX fx = new EntityBlurFX(ep.worldObj, rx, ry, rz, vx, vy, vz).setColor(c).setScale(s).setLife(l).setRapidExpand().setColliding();
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}

		for (double dy = py; dy < 1024; dy += 1) {
			EntityFX fx = new EntityBlurFX(ep.worldObj, px, dy, pz).setColor(0xffffff).setScale(16).setLife(120).setRapidExpand();
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	private static void doGrowthAura(EntityPlayer ep) {
		int x = MathHelper.floor_double(ep.posX);
		int y = MathHelper.floor_double(ep.posY);
		int z = MathHelper.floor_double(ep.posZ);
		if (ep.worldObj.isRemote) {
			doGrowthAuraParticles(ep, x, y, z);
		}
		else {
			RainbowTreeEffects.doRainbowTreeEffects(ep.worldObj, x, y, z, 4, 0.25, ep.worldObj.rand, false);
			for (int i = 0; i < 8; i++) {
				int dx = ReikaRandomHelper.getRandomPlusMinus(x, 8);
				int dz = ReikaRandomHelper.getRandomPlusMinus(z, 8);
				int dy = ReikaRandomHelper.getRandomPlusMinus(y, 2);
				ReikaWorldHelper.fertilizeAndHealBlock(ep.worldObj, dx, dy, dz);
				Block b = ep.worldObj.getBlock(dx, dy, dz);
				if (ModList.THAUMCRAFT.isLoaded() && b == ThaumItemHelper.BlockEntry.NODE.getBlock()) {
					healNodes(ep.worldObj, dx, dy, dz);
				}
				else {
					//if (b.canSustainPlant(ep.worldObj, dx, dy, dz, ForgeDirection.UP, Blocks.red_flower) && ep.worldObj.getBlock(dx, dy+1, dz).isAir(ep.worldObj, dx, dy+1, dz))
					if (ep.worldObj.rand.nextInt(b == Blocks.grass ? 18 : 6) == 0)
						ItemDye.applyBonemeal(ReikaItemHelper.bonemeal.copy(), ep.worldObj, dx, dy, dz, ep);
					else
						b.updateTick(ep.worldObj, dx, dy, dz, ep.worldObj.rand);
				}
			}
			if (ModList.REACTORCRAFT.isLoaded() && ep.worldObj.rand.nextInt(40) == 0) {
				cleanRadiation(ep);
			}
		}
	}

	@ModDependent(ModList.THAUMCRAFT)
	private static void healNodes(World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof INode) {
			INode n = (INode)te;
			AspectList al = n.getAspects();
			Aspect a = ReikaJavaLibrary.getRandomCollectionEntry(world.rand, al.aspects.keySet());
			if (a != null) {
				if (n.getNodeVisBase(a) > al.getAmount(a)) {
					n.addToContainer(a, 1);
				}
			}
			if (world.rand.nextInt(8) == 0) {
				if (world.rand.nextInt(4) == 0) {
					NodeModifier m = n.getNodeModifier();
					if (m != NodeModifier.BRIGHT)
						n.setNodeModifier(m == NodeModifier.FADING ? NodeModifier.PALE : NodeModifier.BRIGHT);
				}
				else {
					NodeType t = n.getNodeType();
					if (t != NodeType.PURE && t != NodeType.NORMAL) {
						n.setNodeType(t == NodeType.HUNGRY || t == NodeType.TAINTED ? NodeType.DARK : t == NodeType.DARK ? NodeType.UNSTABLE : NodeType.NORMAL);
					}
				}
			}
		}
	}

	@ModDependent(ModList.REACTORCRAFT)
	private static void cleanRadiation(EntityPlayer ep) {
		AxisAlignedBB box = ReikaAABBHelper.getEntityCenteredAABB(ep, 8);
		for (EntityRadiation e : ((List<EntityRadiation>)ep.worldObj.getEntitiesWithinAABB(EntityRadiation.class, box))) {
			e.clean();
		}
	}

	@SideOnly(Side.CLIENT)
	private static void doGrowthAuraParticles(EntityPlayer ep, int x, int y, int z) {
		for (int i = 0; i < 4; i++)
			ChromaFX.doGrowthWandParticles(ep.worldObj, ReikaRandomHelper.getRandomPlusMinus(x, 4), y-1, ReikaRandomHelper.getRandomPlusMinus(z, 4));
		for (int i = 0; i < 6; i++) {
			//for (double a = 0; a < 360; a += 12.5) {
			double a = ep.worldObj.rand.nextDouble()*360;
			double r = ReikaRandomHelper.getRandomPlusMinus(2, 0.5);
			float g = -(float)ReikaRandomHelper.getRandomPlusMinus(0.0625, 0.03125);
			float s = 1.5F+ep.worldObj.rand.nextFloat();
			double dx = ep.posX+r*Math.cos(Math.toRadians(a));
			double dy = ep.posY-1.62;
			double dz = ep.posZ+r*Math.sin(Math.toRadians(a));
			int c = CrystalElement.MAGENTA.getColor();
			int l = 20+ep.worldObj.rand.nextInt(20);
			EntityFX fx = new EntityBlurFX(ep.worldObj, dx, dy, dz).setGravity(g).setScale(s).setColor(c).setLife(l).setRapidExpand().setIcon(ChromaIcons.CENTER);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
		//}
	}

	private static void doDimensionPing(EntityPlayer ep) {
		if (ep.worldObj.provider.dimensionId == ExtraChromaIDs.DIMID.getValue()) {
			int x = MathHelper.floor_double(ep.posX);
			int z = MathHelper.floor_double(ep.posZ);

			for (StructurePair s : ChunkProviderChroma.getStructures()) {
				if (s.generator.isComplete()) {
					ChunkCoordIntPair loc = s.generator.getEntryLocation();
					int px = loc.chunkXPos << 4;
					int pz = loc.chunkZPos << 4;
					double dx = px-x;
					double dz = pz-z;
					double dist = ReikaMathLibrary.py3d(dx, 0, dz);
					double ang = ReikaDirectionHelper.getCompassHeading(dx, dz);
					double factor = Math.pow(dist, 1.6);
					factor = factor/20000D;
					int delay = Math.max(1, (int)factor);
					//ReikaJavaLibrary.pConsole(s.color+": DD="+dist+", ang="+ang+", factor="+factor+", delay="+delay);
					ScheduledSoundEvent evt = new DimensionPingEvent(s.color, ep, dist, ang);
					TickScheduler.instance.scheduleEvent(new ScheduledTickEvent(evt), delay);
				}
			}
		}
		else {
			ChromaSounds.ERROR.playSound(ep);
		}
	}

	private static void waterRun(EntityPlayer ep) {
		int x = MathHelper.floor_double(ep.posX);
		int y = MathHelper.floor_double(ep.posY);
		int z = MathHelper.floor_double(ep.posZ);

		Block id = ep.worldObj.getBlock(x, y-1, z);
		Block idbelow = ep.worldObj.getBlock(x, y-2, z);

		if (isValidWaterBlocks(id, idbelow) && ReikaMathLibrary.py3d(ep.motionX, 0, ep.motionZ) >= 0.15) {
			ep.fallDistance = 0;
			if (ep instanceof EntityPlayerMP) {
				((EntityPlayerMP)ep).playerNetServerHandler.floatingTickCount = 0;
			}
			for (int i = 0; i < 8; i++)
				ReikaParticleHelper.RAIN.spawnAt(ep.worldObj, ReikaRandomHelper.getRandomPlusMinus(ep.posX, 0.25), ep.posY-1, ReikaRandomHelper.getRandomPlusMinus(ep.posZ, 0.25));
			if (ep.ticksExisted%2 == 0)
				ep.playSound("random.splash", 0.0625F+ep.worldObj.rand.nextFloat()*0.25F, 0.25F+ep.worldObj.rand.nextFloat());

			ep.motionY = Math.max(0, ep.motionY);
			ep.setPosition(ep.posX, (int)ep.posY+0.7, ep.posZ);
			ep.addVelocity(0.05*ep.motionX, 0, 0.05*ep.motionZ);
		}

	}

	private static boolean isValidWaterBlocks(Block id, Block idbelow) {
		return (idbelow instanceof BlockLiquid || idbelow instanceof BlockFluidBase) && !((id instanceof BlockLiquid || id instanceof BlockFluidBase));
	}

	private static boolean spawnLightning(EntityPlayer ep, int power) {
		if (!ep.worldObj.isRemote) {
			MovingObjectPosition mov = ReikaPlayerAPI.getLookedAtBlock(ep, 128, false);
			if (mov != null) {
				World world = ep.worldObj;
				int x = mov.blockX;
				int y = mov.blockY;
				int z = mov.blockZ;
				if (world.canBlockSeeTheSky(x, y+1, z) && ReikaPlayerAPI.playerCanBreakAt((WorldServer)ep.worldObj, x, y, z, (EntityPlayerMP)ep)) {
					world.addWeatherEffect(new EntityLightningBolt(world, x+0.5, y+0.5, z+0.5));
					int r = 2+power*4;
					if (power == 2) {
						new FlyingBlocksExplosion(world, x+0.5, y-2.5, z+0.5, 6).setTumbling(new LightningTumble(world, x, y, z, r)).doExplosion();
					}
					else if (power == 1) {
						world.newExplosion(null, x+0.5, y-0.5, z+0.5, 4, true, true);
					}
					for (int i = -r; i <= r; i++) {
						for (int j = -r; j <= r; j++) {
							for (int k = -r; k <= r; k++) {
								int dx = x+i;
								int dy = y+j;
								int dz = z+k;
								if (ReikaWorldHelper.flammable(world, dx, dy, dz))
									ReikaWorldHelper.ignite(world, dx, dy, dz);
							}
						}
					}
					return true;
				}
				else {
					ChromaSounds.ERROR.playSound(ep);
					return false;
				}
			}
		}
		return false;
	}

	private static void teleportPlayerMenu(EntityPlayer ep) {
		ep.openGui(ChromatiCraft.instance, ChromaGuis.TELEPORT.ordinal(), ep.worldObj, 0, 0, 0);
	}

	private static void causeShockwave(EntityPlayer ep) {
		if (ep.worldObj.isRemote) {
			spawnShockwaveParticles(ep);
		}
		else {
			ChromaSounds.SHOCKWAVE.playSound(ep);
			AxisAlignedBB box = AxisAlignedBB.getBoundingBox(ep.posX, ep.posY, ep.posZ, ep.posX, ep.posY, ep.posZ).expand(16, 4, 16);
			List<EntityLivingBase> li = ep.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, box);
			for (EntityLivingBase e : li) {
				if (e != ep && ReikaMathLibrary.py3d(e.posX-ep.posX, 0, e.posZ-ep.posZ) <= 16) {
					ReikaEntityHelper.knockbackEntity(ep, e, 4);
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private static void spawnShockwaveParticles(EntityPlayer ep) {
		for (int i = 0; i < 360; i++) {
			double dx = Math.cos(Math.toRadians(i));
			double dz = Math.sin(Math.toRadians(i));
			double vx = dx*0.5;
			double vz = dz*0.5;
			EntityCenterBlurFX fx = new EntityCenterBlurFX(ep.worldObj, ep.posX, ep.posY-1.62+0.1, ep.posZ, vx, 0, vz).setColor(0x0080ff).setScale(2);
			fx.noClip = false;
			if (i%4 == 0) {
				fx.setColor(0xffffff);
			}
			else if (i%2 == 0) {
				fx.setColor(0x0000ff);
			}
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);

			if (i%30 == 0) {
				for (double d = 0.25; d <= 16; d += 0.5) {
					EntityCenterBlurFX fx2 = new EntityCenterBlurFX(ep.worldObj, ep.posX+dx*d, ep.posY-1.62+0.1, ep.posZ+dz*d, 0, 0, 0).setScale(4);
					Minecraft.getMinecraft().effectRenderer.addEffect(fx2);
				}
			}
		}
	}

	private static void addInvPage(EntityPlayer ep) {
		if (ep.worldObj.isRemote)
			return;
		AbilityHelper.instance.addInventoryPage(ep);
		PlayerElementBuffer.instance.removeFromPlayer(ep, AbilityHelper.instance.getElementsFor(Chromabilities.HOTBAR));
	}

	@SideOnly(Side.CLIENT)
	public static void setHealthClient(EntityPlayer ep, int value) {
		setPlayerMaxHealth(ep, value);
	}

	private static void setPlayerMaxHealth(EntityPlayer ep, int value) {
		float factor = value/10F;
		//ReikaJavaLibrary.pConsole(added+":"+add+":"+ep.getMaxHealth());
		ep.getEntityAttribute(SharedMonsterAttributes.maxHealth).removeModifier(new AttributeModifier(HEALTH_UUID, "Chroma", 0, 2));
		if (value > 0) {
			ep.getEntityAttribute(SharedMonsterAttributes.maxHealth).applyModifier(new AttributeModifier(HEALTH_UUID, "Chroma", factor, 2));
			//if (added > 0)
			//	ep.heal(added);
		}
		ep.setHealth(Math.min(ep.getHealth(), ep.getMaxHealth()));
		AbilityHelper.instance.boostHealth(ep, value);
	}

	private static void attractItemsAndXP(EntityPlayer ep, int range, boolean nc) {
		World world = ep.worldObj;
		double x = ep.posX;
		double y = ep.posY+1.5;
		double z = ep.posZ;
		AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x, y, z, x, y, z).expand(range, range, range);
		List<EntityItem> inbox = world.getEntitiesWithinAABB(EntityItem.class, box);
		for (EntityItem ent : inbox) {
			if (ent.isDead)
				continue;
			ReikaEntityHelper.setInvulnerable(ent, true);
			if (ent.delayBeforeCanPickup == 0) {
				double dx = (x+0.5 - ent.posX);
				double dy = (y+0.5 - ent.posY);
				double dz = (z+0.5 - ent.posZ);
				double ddt = ReikaMathLibrary.py3d(dx, dy, dz);
				if (ReikaMathLibrary.py3d(dx, 0, dz) < 1) {
					ent.onCollideWithPlayer(ep);
				}
				else {
					ent.motionX += dx/ddt/ddt/1;
					ent.motionY += dy/ddt/ddt/2;
					ent.motionZ += dz/ddt/ddt/1;
					ent.motionX = MathHelper.clamp_double(ent.motionX, -0.75, 0.75);
					ent.motionY = MathHelper.clamp_double(ent.motionY, -0.75, 0.75);
					ent.motionZ = MathHelper.clamp_double(ent.motionZ, -0.75, 0.75);
					if (ent.posY < y)
						ent.motionY += 0.125;
					if (ent.posY < 0)
						ent.motionY = Math.max(1, ent.motionY);
					if (!world.isRemote)
						ent.velocityChanged = true;
				}
			}
			if (ent.age >= ent.lifespan-5)
				ent.age = 0;
			if (nc)
				ent.noClip = true;
			if (!ent.getEntityData().hasKey("cc_magnetized"))
				ent.getEntityData().setString("cc_magnetized", ep.getUniqueID().toString());
		}
		List<EntityXPOrb> inbox2 = world.getEntitiesWithinAABB(EntityXPOrb.class, box);
		for (EntityXPOrb ent : inbox2) {
			if (ent.isDead)
				continue;
			ReikaEntityHelper.setInvulnerable(ent, true);
			double dx = (x+0.5 - ent.posX);
			double dy = (y+0.5 - ent.posY);
			double dz = (z+0.5 - ent.posZ);
			double ddt = ReikaMathLibrary.py3d(dx, dy, dz);
			if (ReikaMathLibrary.py3d(dx, 0, dz) < 1) {
				ent.onCollideWithPlayer(ep);
			}
			else {
				ent.motionX += dx/ddt/ddt/2;
				ent.motionY += dy/ddt/ddt/2;
				ent.motionZ += dz/ddt/ddt/2;
				ent.motionX = MathHelper.clamp_double(ent.motionX, -0.75, 0.75);
				ent.motionY = MathHelper.clamp_double(ent.motionY, -0.75, 0.75);
				ent.motionZ = MathHelper.clamp_double(ent.motionZ, -0.75, 0.75);
				if (ent.posY < y)
					ent.motionY += 0.1;
				if (ent.posY < 0)
					ent.motionY = Math.max(1, ent.motionY);
				if (!world.isRemote)
					ent.velocityChanged = true;
			}
			if (ent.xpOrbAge >= 6000)
				ent.xpOrbAge = 0;
			if (nc)
				ent.noClip = true;
			if (!ent.getEntityData().hasKey("cc_magnetized"))
				ent.getEntityData().setString("cc_magnetized", ep.getUniqueID().toString());
		}
	}

	private static void setReachDistance(EntityPlayer player, int dist) {
		if (!player.worldObj.isRemote && player instanceof EntityPlayerMP) {
			EntityPlayerMP ep = (EntityPlayerMP)player;
			ep.theItemInWorldManager.setBlockReachDistance(dist > 0 ? dist : 5);
		}
		else {
			AbilityHelper.instance.playerReach = dist;
		}
	}

	private static void destroyBlocksAround(EntityPlayer ep, int power) {
		if (power <= 0)
			return;
		int x = MathHelper.floor_double(ep.posX);
		int y = MathHelper.floor_double(ep.posY)+1;
		int z = MathHelper.floor_double(ep.posZ);
		int r = power;
		if (!ep.worldObj.isRemote) {
			ItemHashMap<Integer> drops = new ItemHashMap();
			for (int i = -r; i <= r; i++) {
				for (int j = -r; j <= r; j++) {
					for (int k = -r; k <= r; k++) {
						int dx = x+i;
						int dy = y+j;
						int dz = z+k;
						if (ReikaMathLibrary.py3d(i, j, k) <= r+0.5) {
							Block b = ep.worldObj.getBlock(dx, dy, dz);
							if (b != Blocks.air && b.isOpaqueCube() && b.blockHardness >= 0) {
								int meta = ep.worldObj.getBlockMetadata(dx, dy, dz);
								if (b instanceof SemiUnbreakable && ((SemiUnbreakable)b).isUnbreakable(ep.worldObj, dx, dy, dz, meta)) {
									continue;
								}
								if (ReikaPlayerAPI.playerCanBreakAt((WorldServer)ep.worldObj, dx, dy, dz, (EntityPlayerMP)ep)) {
									if (power > b.getExplosionResistance(ep, ep.worldObj, dx, dy, dz, ep.posX, ep.posY, ep.posZ)/12F) {
										ArrayList<ItemStack> li = b.getDrops(ep.worldObj, dx, dy, dz, meta, 0);
										if (b instanceof BlockTieredResource) {
											BlockTieredResource bt = (BlockTieredResource)b;
											li.clear();
											if (bt.isPlayerSufficientTier(ep.worldObj, dx, dy, dz, ep))
												li.addAll(bt.getHarvestResources(ep.worldObj, dx, dy, dz, 0, ep));
											else
												li.addAll(bt.getNoHarvestResources(ep.worldObj, dx, dy, dz, 0, ep));
										}
										ForgeEventFactory.fireBlockHarvesting(li, ep.worldObj, b, dx, dy, dz, meta, 0, 1, false, ep);
										for (ItemStack is : li) {
											Integer get = drops.get(is);
											int val = get == null ? 0 : get.intValue();
											drops.put(is, val+is.stackSize);
										}
										b.removedByPlayer(ep.worldObj, ep, dx, dy, dz, true);
										ReikaSoundHelper.playBreakSound(ep.worldObj, dx, dy, dz, b, 0.1F, 1F);
										ep.worldObj.setBlockToAir(dx, dy, dz);
									}
								}
							}
						}
					}
				}
			}
			for (ItemStack is : drops.keySet()) {
				int amt = drops.get(is);
				int max = is.getMaxStackSize();
				while (amt > 0) {
					int drop = Math.min(max, amt);
					amt -= drop;
					DecimalPosition pos = ReikaRandomHelper.getRandomSphericalPosition(x+0.5, y+0.5, z+0.5, r);
					//ReikaJavaLibrary.pConsole(drop+" of "+is+" @ "+pos);
					ReikaItemHelper.dropItem(ep.worldObj, pos.xCoord, pos.yCoord, pos.zCoord, ReikaItemHelper.getSizedItemStack(is, drop));
				}
			}
			AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x, y, z, x+1, y+1, z+1).expand(r, r, r);
			List<EntityXPOrb> li = ep.worldObj.getEntitiesWithinAABB(EntityXPOrb.class, box);
			int amt = 0;
			for (EntityXPOrb e : li) {
				if (e.getDistance(x+0.5, y+0.5, z+0.5) <= r+0.5) {
					amt += e.xpValue;
					e.setDead();
				}
			}
			ReikaWorldHelper.splitAndSpawnXP(ep.worldObj, x+0.5, y+0.5, z+0.5, amt);
		}
		ep.playSound("random.explode", power/6F, 2-power/6F);
	}

	public static boolean shiftArea(WorldServer world, BlockBox box, ForgeDirection dir, int dist, EntityPlayerMP ep) {
		FilledBlockArray moved = new FilledBlockArray(world);
		BlockArray toDel = new BlockArray();
		toDel.setWorld(world);
		int air = 0;
		for (int i = 0; i < box.getSizeX(); i++) {
			for (int j = 0; j < box.getSizeY(); j++) {
				for (int k = 0; k < box.getSizeZ(); k++) {
					int x = i+box.minX;
					int y = j+box.minY;
					int z = k+box.minZ;
					Block b = world.getBlock(x, y, z);
					int meta = world.getBlockMetadata(x, y, z);
					if (!ep.capabilities.isCreativeMode) {
						if (b instanceof SemiUnbreakable && ((SemiUnbreakable)b).isUnbreakable(world, x, y, z, meta))
							continue;
					}
					if (ep.capabilities.isCreativeMode || !ReikaBlockHelper.isUnbreakable(world, x, y, z, b, meta, ep)) {
						if (!b.hasTileEntity(meta) || ChromaOptions.SHIFTTILES.getState()) {
							//if (ReikaWorldHelper.softBlocks(world, dx, dy, dz)) {
							moved.setBlock(x, y, z, b, meta);
							toDel.addBlockCoordinate(x, y, z);
							//}
							if (b.isAir(world, x, y, z))
								air++;
						}
					}
				}
			}
		}
		moved.offset(dir, dist);

		int factor = (int)(Math.pow((box.getVolume()-air), 1.25)*dist/5D);
		ElementTagCompound cost = AbilityHelper.instance.getUsageElementsFor(SHIFT, ep).scale(factor);
		boolean nrg = PlayerElementBuffer.instance.playerHas(ep, cost);
		boolean flag = false;
		if (nrg && ReikaPlayerAPI.playerCanBreakAt(world, toDel, ep) && ReikaPlayerAPI.playerCanBreakAt(world, moved, ep)) {
			BlockArray toDrop = BlockArray.getIntersectedBox(toDel, moved);
			toDrop.setWorld(world);
			for (ItemStack is : toDrop.getAllDroppedItems(world, 0, ep)) {
				//ReikaPlayerAPI.addOrDropItem(is, ep);
			}
			toDel.clearArea();
			moved.place();
			PlayerElementBuffer.instance.removeFromPlayer(ep, cost);
			flag = true;
		}
		else {
			flag = false;
			ChromaSounds.ERROR.playSound(ep);
		}
		Chromabilities.SHIFT.setToPlayer(ep, false);
		return flag;
	}

	private static void healPlayer(EntityPlayer ep, int health) {
		ep.heal(health);
	}

	private static void launchFireball(EntityPlayer ep, int charge) {
		double[] look = ReikaVectorHelper.getPlayerLookCoords(ep, 2);
		EntityAbilityFireball ef = new EntityAbilityFireball(ep.worldObj, ep, look[0], look[1]+1, look[2]);
		Vec3 lookv = ep.getLookVec();
		ef.motionX = lookv.xCoord/5;
		ef.motionY = lookv.yCoord/5;
		ef.motionZ = lookv.zCoord/5;
		ef.accelerationX = ef.motionX;
		ef.accelerationY = ef.motionY;
		ef.accelerationZ = ef.motionZ;
		ef.field_92057_e = charge;
		ef.posY = ep.posY+1;
		if (!ep.worldObj.isRemote) {
			ep.worldObj.playSoundAtEntity(ep, "mob.ghast.fireball", 1, 1);
			ep.worldObj.spawnEntityInWorld(ef);
		}
	}

	private static void stopArrows(EntityPlayer ep) {
		if (!ep.worldObj.isRemote) {
			AxisAlignedBB box = ep.boundingBox.expand(6, 4, 6);
			List<EntityArrow> li = ep.worldObj.getEntitiesWithinAABB(EntityArrow.class, box);
			for (EntityArrow e : li) {
				if (e.shootingEntity != ep && (!(e.shootingEntity instanceof EntityPlayer) || MinecraftServer.getServer().isPVPEnabled())) { //bounceback code
					e.motionX *= -0.10000000149011612D;
					e.motionY *= -0.10000000149011612D;
					e.motionZ *= -0.10000000149011612D;
					e.rotationYaw += 180.0F;
					e.prevRotationYaw += 180.0F;
					e.ticksInAir = 0;
				}
			}
		}
	}

	private static void deAggroMobs(EntityPlayer ep) {
		AxisAlignedBB box = ep.boundingBox.expand(12, 12, 12);
		List<EntityMob> li = ep.worldObj.getEntitiesWithinAABB(EntityMob.class, box);
		for (EntityMob e : li) {
			if (!(e instanceof EntityEnderman || e instanceof EntityPigZombie)) {
				if (e.getEntityToAttack() == ep || e.getEntityToAttack() == null) {
					//e.setAttackTarget(null);
					//e.attackEntityFrom(DamageSource.causeMobDamage(ReikaEntityHelper.getDummyMob(ep.worldObj, e.posX, e.posY, e.posZ)), 0);
				}
				if (e instanceof EntityCreeper) {
					EntityCreeper ec = (EntityCreeper)e;
					if (ec.getEntityToAttack() != ep) {
						ec.setCreeperState(-1);
						ec.getDataWatcher().updateObject(18, (byte)0);
						ec.timeSinceIgnited = 0;
					}
				}
			}
			/*
			List<EntityAITaskEntry> tasks = e.targetTasks.taskEntries;
			for (int k = 0; k < tasks.size(); k++) {
				EntityAIBase a = tasks.get(k).action;
				if (a instanceof EntityAINearestAttackableTarget) {
					EntityAINearestAttackableTarget nat = (EntityAINearestAttackableTarget)a;
					nat.targetEntitySelector = new AbilityHelper.PlayerExemptAITarget(nat.targetEntitySelector);
				}
			}*/
		}
	}

	private static void breakSurroundingBlocks(EntityPlayer ep) {
		if (!ep.worldObj.isRemote) {
			for (int i = 0; i < 6; i++) {
				double ANGLE = 35;//22;
				double phi = ReikaRandomHelper.getRandomPlusMinus(ep.rotationYawHead+90, ANGLE);
				double theta = ReikaRandomHelper.getRandomPlusMinus(-ep.rotationPitch, ANGLE);
				double[] xyz = ReikaPhysicsHelper.polarToCartesian(1, theta, phi);
				Coordinate c = null;
				for (double d = 0; d <= 8; d += 0.125) {
					double dx = ep.posX+xyz[0]*d;
					double dy = ep.posY+1.62+xyz[1]*d;
					double dz = ep.posZ+xyz[2]*d;
					int x = MathHelper.floor_double(dx);
					int y = MathHelper.floor_double(dy);
					int z = MathHelper.floor_double(dz);
					Block b = ep.worldObj.getBlock(x, y, z);
					if (!b.isAir(ep.worldObj, x, y, z) && !ReikaBlockHelper.isLiquid(b) && b != Blocks.mob_spawner && !ReikaBlockHelper.isUnbreakable(ep.worldObj, x, y, z, b, ep.worldObj.getBlockMetadata(x, y, z), ep)) {
						if (ep.worldObj.getEntitiesWithinAABB(EntityNukerBall.class, ReikaAABBHelper.getBlockAABB(x, y, z)).isEmpty()) {
							c = new Coordinate(x, y, z);
							break;
						}
					}
				}
				if (c != null) {
					EntityNukerBall enb = new EntityNukerBall(ep.worldObj, ep, c);
					ep.worldObj.spawnEntityInWorld(enb);
					ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.NUKERLOC.ordinal(), new PacketTarget.RadiusTarget(ep, 64), c.xCoord, c.yCoord, c.zCoord, ep.getEntityId());
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public static void doNukerFX(World world, int x, int y, int z, EntityPlayer ep) {
		double lx = x+0.5-ep.posX;
		double ly = y+0.5-ep.posY;
		double lz = z+0.5-ep.posZ;
		ElementTagCompound tag = ItemElementCalculator.instance.getValueForItem(BlockKey.getAt(world, x, y, z).asItemStack());
		int c = tag == null || tag.isEmpty() ? 0x22aaff : tag.asWeightedRandom().getRandomEntry().getColor();
		for (double d = 0.125; d <= 1; d += 0.03125/2) {
			double dx = ep.posX+d*lx;
			double dy = ep.posY+d*ly;
			double dz = ep.posZ+d*lz;
			EntityBlurFX fx = new EntityBlurFX(world, dx, dy, dz).setLife(5).setAlphaFading().setScale(0.5F).setColor(c);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	//@ModDependent(ModList.FORESTRY)
	private static void analyzeBees(EntityPlayer ep) {
		int slot = (int)(ep.worldObj.getTotalWorldTime()%ep.inventory.mainInventory.length);
		ItemStack is = ep.inventory.mainInventory[slot];
		AbilityHelper.instance.analyzeGenes(is);
	}

	private static void setNoclipState(EntityPlayer ep, boolean set) {
		if (AbilityHelper.instance.isNoClipEnabled != set) {
			AbilityHelper.instance.isNoClipEnabled = set;
			if (set) {
				AbilityHelper.instance.onNoClipEnable(ep);
			}
			else {
				AbilityHelper.instance.onNoClipDisable(ep);
			}
			ChromatiCraft.logger.debug("Noclip state changed to "+set);
		}
		else if (set) {
			if (ep.worldObj.isRemote && ep.ticksExisted%24 == 0)
				ReikaSoundHelper.playClientSound(ChromaSounds.NOCLIPRUN, ep, 1, 1);
		}
		//ep.noClip = set;// && ((ep.capabilities.allowFlying && ep.capabilities.isFlying) || ep.isSneaking() || KeyWatcher.instance.isKeyDown(ep, Key.JUMP));
		/*if (ep.noClip) {
			ep.moveEntity(-ep.motionX, -ep.motionY, -ep.motionZ);
			List<AxisAlignedBB> li = ep.worldObj.getCollidingBoundingBoxes(ep, ep.boundingBox.addCoord(ep.motionX, ep.motionY, ep.motionZ));//AbilityHelper.instance.getNoclipBlockBoxes(ep);
			//ReikaJavaLibrary.pConsole(locs);

			double d6 = ep.motionX;
			double d7 = ep.motionY;
			double d8 = ep.motionZ;

			AxisAlignedBB epbox = ep.boundingBox;//.addCoord(ep.motionX, ep.motionY, ep.motionZ);

			//ReikaJavaLibrary.pConsole("S: "+epbox+"+"+li+"="+(li.isEmpty() ? false : li.get(0).intersectsWith(epbox))+" & "+ep.motionY, Side.SERVER);

			for (AxisAlignedBB box : li) {
				ep.motionY = box.calculateYOffset(box, ep.motionY);
			}

			epbox.offset(0.0D, ep.motionY, 0.0D);

			if (!ep.field_70135_K && d7 != ep.motionY) {
				ep.motionZ = 0.0D;
				ep.motionY = 0.0D;
				ep.motionX = 0.0D;
			}

			boolean flag1 = ep.onGround || d7 != ep.motionY && d7 < 0.0D;
			int j;

			for (AxisAlignedBB box : li) {
				ep.motionX = box.calculateXOffset(box, ep.motionX);
			}

			epbox.offset(ep.motionX, 0.0D, 0.0D);

			if (!ep.field_70135_K && d6 != ep.motionX) {
				ep.motionZ = 0.0D;
				ep.motionY = 0.0D;
				ep.motionX = 0.0D;
			}

			for (AxisAlignedBB box : li) {
				ep.motionZ = box.calculateZOffset(box, ep.motionZ);
			}

			epbox.offset(0.0D, 0.0D, ep.motionZ);

			if (!ep.field_70135_K && d8 != ep.motionZ) {
				ep.motionZ = 0.0D;
				ep.motionY = 0.0D;
				ep.motionX = 0.0D;
			}

			ep.posX = (epbox.minX + epbox.maxX) / 2.0D;
			ep.posY = epbox.minY + ep.yOffset - ep.ySize;
			ep.posZ = (epbox.minZ + epbox.maxZ) / 2.0D;
			ep.isCollidedHorizontally = d6 != ep.motionX || d8 != ep.motionZ;
			ep.isCollidedVertically = d7 != ep.motionY;
			ep.onGround = d7 != ep.motionY && d7 < 0.0D;
			ep.isCollided = ep.isCollidedHorizontally || ep.isCollidedVertically;
			//ep.updateFallState(ep.motionY, ep.onGround);

			if (d6 != ep.motionX) {
				ep.motionX = 0.0D;
			}

			if (d7 != ep.motionY) {
				ep.motionY = 0.0D;
			}

			if (d8 != ep.motionZ) {
				ep.motionZ = 0.0D;
			}


			//ReikaJavaLibrary.pConsole("E: "+epbox+"+"+li+"="+(li.isEmpty() ? false : li.get(0).intersectsWith(epbox))+" & "+ep.motionY, Side.SERVER);
		}*/
	}

	private static void convertBufferToLP(EntityPlayer ep, int data) {
		if (data > 0)
			ep.heal(data); //undo damage dealt
		PlayerElementBuffer.instance.removeFromPlayer(ep, TileEntityLifeEmitter.getLumensPerHundredLP());
	}

	public static boolean canPlayerExecuteAt(EntityPlayer ep, Ability a) {
		ElementTagCompound use = AbilityHelper.instance.getUsageElementsFor(a, ep);
		return PlayerElementBuffer.instance.playerHas(ep, use) && a.canPlayerExecuteAt(ep);
	}

	public boolean canPlayerExecuteAt(EntityPlayer player) {
		return true;
	}

	public static int maxPower(EntityPlayer ep, Ability a) {
		int base = a.getMaxPower();
		if (ep.capabilities.isCreativeMode)
			return base;
		int lvl = base;
		ElementTagCompound use = AbilityHelper.instance.getElementsFor(a).scale(0.01F);
		for (CrystalElement e : use.elementSet()) {
			lvl = (int)Math.min(lvl, PlayerElementBuffer.instance.getPlayerContent(ep, e)/(float)use.getValue(e));
		}
		return Math.max(1, lvl);
	}

	public int getMaxPower() {
		switch(this) {
			case SONIC:
				return 12;
			case SHIFT:
				return 24;
			case HEAL:
				return 4;
			case FIREBALL:
				return 8;
			case HEALTH:
				return 50;
			case LIGHTNING:
				return 2;
			case MAGNET:
				return 1;
			case BREADCRUMB:
				return 12;
			case REACH:
				return AbilityHelper.REACH_SCALE.length-1;
			case JUMP:
				return 8;
			default:
				return 0;
		}
	}

	@Override
	public String getID() {
		return this.name().toLowerCase(Locale.ENGLISH);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getTexturePath(boolean gray) {
		String base = this.getID();
		String name = !gray ? base : base+"_g";
		String path = "Textures/Ability/"+name+".png";
		return path;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Class getTextureReferenceClass() {
		return ChromatiCraft.class;
	}

	public int getInt() {
		return getAbilityInt(this);
	}

	public static int getAbilityInt(Ability a) {
		return intMap.inverse().get(a);
	}

	public static Ability getAbilityByInt(int id) {
		return intMap.get(id);
	}

	static {
		for (int i = 0; i < abilities.length; i++) {
			Chromabilities c = abilities[i];
			if (!c.isDummiedOut())
				addAbility(c);
		}
	}

	public static void addAbility(Ability c) {
		String id = c.getID();
		checkIDValidity(id);
		tagMap.put(id, c);
		abilityMap.put(id, c);
		intMap.put(maxID, c);
		if (c.isTickBased()) {
			tickAbilities.addValue(c.getTickPhase(), c);
		}

		ChromatiCraft.logger.log("Added ability '"+c.getDisplayName()+"', assigned IDs '"+id+"' and #"+maxID);

		sortedList = new ArrayList(abilityMap.values());
		Collections.sort(sortedList, sorter);
		maxID++;
	}

	private static void checkIDValidity(String id) {
		if (id == null || id.isEmpty())
			throw new IllegalArgumentException("ID cannot be null or empty!");
		if (id.equals("null") || id.equals(" "))
			throw new IllegalArgumentException("Invalid ID string "+id+"!");
		if (id.equals("all") || id.equals("none"))
			throw new IllegalArgumentException("Reserved ID string "+id+"!");
		if (abilityMap.containsKey(id))
			throw new IllegalArgumentException("ID string "+id+" already taken!");
	}

	private static final Comparator sorter = new AbilitySorter();

	private static class AbilitySorter implements Comparator<Ability> {

		@Override
		public int compare(Ability o1, Ability o2) {
			if (o1 instanceof Chromabilities && o2 instanceof Chromabilities) {
				return ((Chromabilities)o1).ordinal()-((Chromabilities)o2).ordinal();
			}
			else if (o1 instanceof Chromabilities) {
				return Integer.MIN_VALUE;
			}
			else if (o2 instanceof Chromabilities) {
				return Integer.MAX_VALUE;
			}
			return getAbilityInt(o1)-getAbilityInt(o2);
		}

	}

	public static void copyTo(EntityPlayer from, EntityPlayer to) {
		NBTTagCompound nbt = from.getEntityData();
		NBTTagCompound data = nbt.getCompoundTag(NBT_TAG);
		to.getEntityData().setTag(NBT_TAG, data);
	}

	private static class LightningTumble implements TumbleCreator {

		private final World world;
		private final int posX;
		private final int posY;
		private final int posZ;
		private final int radius;

		private LightningTumble(World world, int x, int y, int z, int r) {
			this.world = world;
			posX = x;
			posY = y;
			posZ = z;
			radius = r;
		}

		@Override
		public EntityTumblingBlock createBlock(World world, int x, int y, int z, Block b, int meta) {
			double dx = x-posX;
			double dz = z-posZ;
			double v = ReikaRandomHelper.getRandomPlusMinus(0D, 15D);
			return new EntityTumblingBlock(world, x, y, z, b, meta).setRotationSpeed(v*dx/radius, 0, v*dz/radius);
		}

	}
}
