/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
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
import java.util.UUID;

import net.minecraft.block.Block;
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
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.API.AbilityAPI.Ability;
import Reika.ChromatiCraft.Auxiliary.AbilityHelper;
import Reika.ChromatiCraft.Auxiliary.ChromaDescriptions;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.PlayerElementBuffer;
import Reika.ChromatiCraft.ModInterface.TileEntityLifeEmitter;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Instantiable.FlyingBlocksExplosion;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockArray;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockBox;
import Reika.DragonAPI.Instantiable.Data.Immutable.ScaledDirection;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaVectorHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

import com.google.common.collect.HashBiMap;

import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public enum Chromabilities implements Ability {

	REACH(null, true),
	MAGNET(Phase.END, false),
	SONIC(null, false),
	SHIFT(null, false),
	HEAL(null, false),
	SHIELD(Phase.END, false),
	FIREBALL(null, false),
	COMMUNICATE(Phase.START, false),
	HEALTH(null, true),
	PYLON(null, false),
	LIGHTNING(null, false),
	LIFEPOINT(null, false, ModList.BLOODMAGIC),
	DEATHPROOF(null, false),
	HOTBAR(null, true);

	private final boolean tickBased;
	private final Phase tickPhase;
	private final boolean actOnClient;
	private ModList dependency;

	private static final UUID uid_health = UUID.randomUUID();
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
	private static final MultiMap<Phase, Ability> tickAbilities = new MultiMap();

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
		return StatCollector.translateToLocal("chromability."+this.name().toLowerCase());
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
		return dependency != null && !dependency.isLoaded();
	}

	public ElementTagCompound getTickCost() {
		return getTickCost(this);
	}

	public static ElementTagCompound getTickCost(Ability c) {
		if (c.isTickBased()) {
			return AbilityHelper.instance.getUsageElementsFor(c);
		}

		if (c == HEALTH || c == PYLON)
			return AbilityHelper.instance.getUsageElementsFor(c);
		else if (c == REACH)
			return AbilityHelper.instance.getUsageElementsFor(c).scale(0.5F);

		return null;
	}

	public void apply(EntityPlayer ep) {
		switch(this) {
		case MAGNET:
			this.attractItemsAndXP(ep, 24);
			break;
		case SHIELD:
			this.stopArrows(ep);
			break;
		case COMMUNICATE:
			this.deAggroMobs(ep);
			break;
		default:
			break;
		}
	}

	public void trigger(EntityPlayer ep, int data) {
		switch(this) {
		case REACH:
			this.setReachDistance(ep, this.enabledOn(ep) ? MAX_REACH : -1); //use data?
			break;
		case SONIC:
			this.destroyBlocksAround(ep, data);
			break;
		case SHIFT:
			if (this.enabledOn(ep)) {
				AbilityHelper.instance.startDrawingBoxes(ep);
				AbilityHelper.instance.shifts.put(ep, new ScaledDirection(ReikaPlayerAPI.getDirectionFromPlayerLook(ep, true), data));
			}
			else {
				AbilityHelper.instance.stopDrawingBoxes(ep);
				AbilityHelper.instance.shifts.remove(ep);
			}
			break;
		case HEAL:
			this.healPlayer(ep, data);
			break;
		case FIREBALL:
			this.launchFireball(ep, data);
			break;
		case HEALTH:
			this.setPlayerMaxHealth(ep, this.enabledOn(ep) ? data : 0);
			break;
		case LIGHTNING:
			this.spawnLightning(ep, data);
			break;
		case LIFEPOINT:
			this.convertBufferToLP(ep, data);
			break;
		case HOTBAR:
			addInvPage(ep);
			break;
		default:
			break;
		}
	}

	public static void triggerAbility(EntityPlayer ep, Ability a, int data) {
		if (ep.worldObj.isRemote) {
			ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.ABILITY.ordinal(), ep.worldObj, 0, 0, 0, getAbilityInt(a), data);

			if (!a.actOnClient())
				return;
		}

		ProgressStage.ABILITY.stepPlayerTo(ep);
		ElementTagCompound use = AbilityHelper.instance.getUsageElementsFor(a);
		if (a == HEALTH)
			use.scale(10);
		if (a == SHIFT)
			use.scale(25);
		if (a == LIGHTNING)
			use.scale(10);
		if (a == LIFEPOINT)
			use.scale(5);
		PlayerElementBuffer.instance.removeFromPlayer(ep, use);
		boolean flag = enabledOn(ep, a) || a.isPureEventDriven();
		setToPlayer(ep, !flag, a);

		if (a.isTickBased()) {

		}
		else {
			a.trigger(ep, data);
		}
	}

	public boolean isPureEventDriven() {
		switch(this) {
		case SONIC:
		case HEAL:
		case FIREBALL:
		case LIGHTNING:
		case HOTBAR:
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
		setToPlayer(ep, false, a);
		a.onRemoveFromPlayer(ep);
	}

	public void onRemoveFromPlayer(EntityPlayer ep) {
		if (this == REACH)
			this.setReachDistance(ep, -1);
		else if (this == HEALTH)
			this.setPlayerMaxHealth(ep, 0);
	}

	private static void spawnLightning(EntityPlayer ep, int power) {
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
						new FlyingBlocksExplosion(world, x+0.5, y-2.5, z+0.5, 6).doExplosion();
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
				}
				else {
					ChromaSounds.ERROR.playSound(ep);
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
		float added = value+20-ep.getMaxHealth();
		//ReikaJavaLibrary.pConsole(added+":"+add+":"+ep.getMaxHealth());
		if (ep.worldObj.isRemote)
			ep.getEntityAttribute(SharedMonsterAttributes.maxHealth).removeAllModifiers();
		ep.getEntityAttribute(SharedMonsterAttributes.maxHealth).removeModifier(new AttributeModifier(uid_health, "Chroma", value/20D, 2));
		if (value > 0) {
			ep.getEntityAttribute(SharedMonsterAttributes.maxHealth).applyModifier(new AttributeModifier(uid_health, "Chroma", value/20D, 2));
			if (added > 0)
				ep.heal(added);
		}
		ep.setHealth(Math.min(ep.getHealth(), ep.getMaxHealth()));
		AbilityHelper.instance.boostHealth(ep, value);
	}

	private static void attractItemsAndXP(EntityPlayer ep, int range) {
		World world = ep.worldObj;
		double x = ep.posX;
		double y = ep.posY+1.5;
		double z = ep.posZ;
		AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x, y, z, x, y, z).expand(range, range, range);
		List<EntityItem> inbox = world.getEntitiesWithinAABB(EntityItem.class, box);
		for (EntityItem ent : inbox) {
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
					if (!world.isRemote)
						ent.velocityChanged = true;
				}
			}
		}
		List<EntityXPOrb> inbox2 = world.getEntitiesWithinAABB(EntityXPOrb.class, box);
		for (EntityXPOrb ent : inbox2) {
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
				if (!world.isRemote)
					ent.velocityChanged = true;
			}
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
		for (int i = -r; i <= r; i++) {
			for (int j = -r; j <= r; j++) {
				for (int k = -r; k <= r; k++) {
					int dx = x+i;
					int dy = y+j;
					int dz = z+k;
					if (ReikaMathLibrary.py3d(i, j, k) <= r+0.5) {
						Block b = ep.worldObj.getBlock(dx, dy, dz);
						if (b != Blocks.air && b.isOpaqueCube()) {
							if (power > b.blockResistance/12F) {
								b.dropBlockAsItem(ep.worldObj, dx, dy, dz, ep.worldObj.getBlockMetadata(dx, dy, dz), 0);
								ReikaSoundHelper.playBreakSound(ep.worldObj, dx, dy, dz, b, 0.1F, 1F);
								ep.worldObj.setBlockToAir(dx, dy, dz);
							}
						}
					}
				}
			}
		}

	}

	public static void shiftArea(WorldServer world, BlockBox box, ForgeDirection dir, int dist, EntityPlayerMP ep) {
		FilledBlockArray moved = new FilledBlockArray(world);
		BlockArray toDel = new BlockArray();
		toDel.setWorld(world);
		for (int i = 0; i < box.getSizeX(); i++) {
			for (int j = 0; j < box.getSizeY(); j++) {
				for (int k = 0; k < box.getSizeZ(); k++) {
					int x = i+box.minX;
					int y = j+box.minY;
					int z = k+box.minZ;
					Block b = world.getBlock(x, y, z);
					int meta = world.getBlockMetadata(x, y, z);
					if (!b.hasTileEntity(meta)) {
						//if (ReikaWorldHelper.softBlocks(world, dx, dy, dz)) {
						moved.setBlock(x, y, z, b, meta);
						toDel.addBlockCoordinate(x, y, z);
						//}
					}
				}
			}
		}
		moved.offset(dir, dist);
		if (ReikaPlayerAPI.playerCanBreakAt(world, toDel, ep) && ReikaPlayerAPI.playerCanBreakAt(world, moved, ep)) {
			toDel.clearArea();
			moved.place();
		}
		Chromabilities.SHIFT.setToPlayer(ep, false);
		int factor = box.getVolume()*box.getVolume()*dist/4;
		PlayerElementBuffer.instance.removeFromPlayer(ep, AbilityHelper.instance.getUsageElementsFor(SHIFT).scale(factor));
	}

	private static void healPlayer(EntityPlayer ep, int health) {
		ep.heal(health);
	}

	private static void launchFireball(EntityPlayer ep, int charge) {
		double[] look = ReikaVectorHelper.getPlayerLookCoords(ep, 2);
		EntityLargeFireball ef = new EntityLargeFireball(ep.worldObj, ep, look[0], look[1]+1, look[2]);
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
		AxisAlignedBB box = ep.boundingBox.expand(4, 4, 4);
		List<EntityArrow> li = ep.worldObj.getEntitiesWithinAABB(EntityArrow.class, box);
		for (EntityArrow e : li) {
			if (e.shootingEntity != ep && !e.worldObj.isRemote) { //bounceback code
				e.motionX *= -0.10000000149011612D;
				e.motionY *= -0.10000000149011612D;
				e.motionZ *= -0.10000000149011612D;
				e.rotationYaw += 180.0F;
				e.prevRotationYaw += 180.0F;
				e.ticksInAir = 0;
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
					e.attackEntityFrom(DamageSource.causeMobDamage(ReikaEntityHelper.getDummyMob(ep.worldObj)), 0);
				}
				if (e instanceof EntityCreeper) {
					EntityCreeper ec = (EntityCreeper)e;
					ec.setCreeperState(-1);
					ec.getDataWatcher().updateObject(18, (byte)0);
					ec.timeSinceIgnited = 0;
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

	private static void convertBufferToLP(EntityPlayer ep, int data) {
		ep.heal(data); //undo damage dealt
		PlayerElementBuffer.instance.removeFromPlayer(ep, TileEntityLifeEmitter.getLumensPerHundredLP());
	}

	public static boolean canPlayerExecuteAt(EntityPlayer ep, Ability a) {
		ElementTagCompound use = AbilityHelper.instance.getUsageElementsFor(a);
		return PlayerElementBuffer.instance.playerHas(ep, use) && a.canPlayerExecuteAt(ep);
	}

	public boolean canPlayerExecuteAt(EntityPlayer player) {
		return true;
	}

	public static int maxPower(EntityPlayer ep, Ability a) {
		int base = a.getMaxPower();
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
			return 40;
		case LIGHTNING:
			return 2;
		default:
			return 0;
		}
	}

	@Override
	public String getID() {
		return this.name().toLowerCase();
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
}
