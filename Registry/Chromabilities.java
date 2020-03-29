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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import com.google.common.collect.HashBiMap;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.StatCollector;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.API.AbilityAPI.Ability;
import Reika.ChromatiCraft.Auxiliary.ChromaDescriptions;
import Reika.ChromatiCraft.Auxiliary.Ability.AbilityCalls;
import Reika.ChromatiCraft.Auxiliary.Ability.AbilityHelper;
import Reika.ChromatiCraft.Auxiliary.Ability.AbilitySorter;
import Reika.ChromatiCraft.Auxiliary.Render.ChromaFontRenderer.FontType;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.PlayerElementBuffer;
import Reika.ChromatiCraft.Magic.Progression.ChromaResearchManager;
import Reika.ChromatiCraft.Magic.Progression.ProgressStage;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Instantiable.Data.Immutable.ScaledDirection;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap.CollectionType;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

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
	JUMP(null, false),
	//VOXELPLACE(null, false);
	SUPERBUILD(null, false),
	CHESTCLEAR(Phase.END, false),
	MOBBAIT(null, false),
	//ANGELBLOCK(null, false),
	;


	private final boolean tickBased;
	private final Phase tickPhase;
	private final boolean actOnClient;
	private ModList dependency;

	public static final UUID HEALTH_UUID = UUID.fromString("71d6a916-a54b-11e7-abc4-cec278b6b50a");
	public static final UUID FAKE_UUID = UUID.randomUUID();
	public static final int MAX_REACH = 128;

	private static long lastNullPlayerDump = -1;

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
	private static final MultiMap<Phase, Ability> tickAbilities = new MultiMap(CollectionType.HASHSET);

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
				//case VOXELPLACE:
				return true;
			default:
				return false;
		}
	}

	public void apply(EntityPlayer ep) {
		switch(this) {
			case MAGNET:
				AbilityCalls.attractItemsAndXP(ep, 24, AbilityHelper.instance.isMagnetNoClip(ep));
				break;
			case SHIELD:
				AbilityCalls.stopArrows(ep);
				break;
			case COMMUNICATE:
				AbilityCalls.deAggroMobs(ep);
				break;
			case FLOAT:
				AbilityCalls.waterRun(ep);
				break;
			case DASH:
				PotionEffect pot = ep.getActivePotionEffect(Potion.moveSpeed);
				if (pot != null && pot.getAmplifier() >= 60)
					ep.stepHeight = 2.75F;
				else
					ep.stepHeight = 0.5F;
				break;
			case FIRERAIN:
				AbilityCalls.tickFireRain(ep);
				break;
			case GROWAURA:
				AbilityCalls.doGrowthAura(ep);
				break;
			case ORECLIP:
				AbilityCalls.setNoclipState(ep, true);
				break;
			case BEEALYZE:
				AbilityCalls.analyzeBees(ep);
				break;
			case NUKER:
				AbilityCalls.breakSurroundingBlocks(ep);
				break;
			case CHESTCLEAR:
				AbilityCalls.doChestCollection((EntityPlayerMP)ep);
				break;
			default:
				break;
		}
	}

	public boolean trigger(EntityPlayer ep, int data) {
		switch(this) {
			case REACH:
				AbilityCalls.setReachDistance(ep, this.enabledOn(ep) ? AbilityHelper.REACH_SCALE[data] : -1);
				return true;
			case SONIC:
				AbilityCalls.destroyBlocksAround(ep, data);
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
				AbilityCalls.healPlayer(ep, data);
				return true;
			case FIREBALL:
				AbilityCalls.launchFireball(ep, data);
				return true;
			case HEALTH:
				AbilityCalls.setPlayerMaxHealth(ep, this.enabledOn(ep) ? data : 0);
				return true;
			case LIGHTNING:
				return AbilityCalls.spawnLightning(ep, data);
			case LIFEPOINT:
				AbilityCalls.convertBufferToLP(ep, data);
				return true;
			case HOTBAR:
				AbilityCalls.addInvPage(ep);
				return true;
			case SHOCKWAVE:
				AbilityCalls.causeShockwave(ep);
				return true;
			case TELEPORT:
				AbilityCalls.teleportPlayerMenu(ep);
				return true;
			case BREADCRUMB:
				AbilityHelper.instance.setPathLength(ep, this.enabledOn(ep) ? ReikaMathLibrary.intpow2(2, data) : 0);
				return true;
			case DIMPING:
				AbilityCalls.doDimensionPing(ep);
				return true;
			case LASER:
				return AbilityCalls.doLaserPulse(ep);
			case LIGHTCAST:
				return AbilityCalls.doLightCast(ep);
			case JUMP:
				return AbilityCalls.doJump(ep, data);
			case MOBBAIT:
				return AbilityCalls.doMobBait(ep);
			default:
				return false;
		}
	}

	public static void triggerAbility(EntityPlayer ep, Ability a, int data) {
		if (ep.worldObj.isRemote) {
			ReikaPacketHelper.sendPacketToServer(ChromatiCraft.packetChannel, ChromaPackets.ABILITY.ordinal(), getAbilityInt(a), data);

			if (!a.actOnClient())
				return;
		}
		else if (a.actOnClient() && a.isPureEventDriven()) { //notify other players
			ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.ABILITYSEND.ordinal(), PacketTarget.allPlayers, getAbilityInt(a), data, ep.getEntityId());
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
		if (a == GROWAURA)
			AbilityHelper.instance.setGrowAuraState(ep, flag ? data : 0);

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
			case MOBBAIT:
				return true;
			default:
				return false;
		}
	}

	@Override
	public boolean isFunctioningOn(EntityPlayer ep) {
		switch(this) {
			case COMMUNICATE:
				return AbilityHelper.instance.isPeaceActive(ep);
			default:
				return true;
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
		if (ep == null) {
			if (System.currentTimeMillis()-lastNullPlayerDump > 5000) {
				ChromatiCraft.logger.logError("Tried to get ability status of null player!?");
				Thread.dumpStack();
				lastNullPlayerDump = System.currentTimeMillis();
			}
			return false;
		}
		NBTTagCompound nbt = ep.getEntityData();
		NBTTagCompound abilities = nbt.getCompoundTag(NBT_TAG);
		return abilities != null && abilities.getBoolean(a.getID());
	}

	public static boolean playerHasAbility(EntityPlayer ep, String id) {
		return playerHasAbility(ep, getAbility(id));
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
			AbilityCalls.setReachDistance(ep, -1);
		else if (this == HEALTH)
			AbilityCalls.setPlayerMaxHealth(ep, 0);
		else if (this == MAGNET)
			AbilityHelper.instance.setNoClippingMagnet(ep, false);
		else if (this == ORECLIP)
			AbilityCalls.setNoclipState(ep, false);
		else if (this == GROWAURA)
			AbilityHelper.instance.setGrowAuraState(ep, 0);
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
			case GROWAURA:
				return 3;
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
		Collections.sort(sortedList, AbilitySorter.sorter);
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

	public static void copyTo(EntityPlayer from, EntityPlayer to) {
		NBTTagCompound nbt = from.getEntityData();
		NBTTagCompound data = nbt.getCompoundTag(NBT_TAG);
		to.getEntityData().setTag(NBT_TAG, data);
	}


}
