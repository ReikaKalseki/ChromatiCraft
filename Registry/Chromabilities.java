/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
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
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.AbilityHelper;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.PlayerElementBuffer;
import Reika.DragonAPI.Instantiable.Data.BlockArray;
import Reika.DragonAPI.Instantiable.Data.BlockBox;
import Reika.DragonAPI.Instantiable.Data.FilledBlockArray;
import Reika.DragonAPI.Instantiable.Data.ScaledDirection;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaVectorHelper;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;

public enum Chromabilities {

	REACH(null, true),
	MAGNET(Phase.END, false),
	SONIC(null, false),
	SHIFT(null, false),
	HEAL(null, false),
	SHIELD(Phase.END, false),
	FIREBALL(null, false),
	COMMUNICATE(Phase.START, false),
	HEALTH(null, true),
	PYLON(null, false);

	public final boolean tickBased;
	public final Phase tickPhase;
	public final boolean actOnClient;

	private static final UUID uid_health = UUID.randomUUID();

	private Chromabilities(Phase tick, boolean client) {
		tickBased = tick != null;
		tickPhase = tick;
		actOnClient = client;
	}

	private static final String NBT_TAG = "chromabilities";
	private static final HashMap<String, Chromabilities> tagMap = new HashMap();

	public static final Chromabilities[] abilities = values();

	public ElementTagCompound getTickCost() {
		if (tickBased) {
			return AbilityHelper.instance.getUsageElementsFor(this);
		}
		switch(this) {
		case HEALTH:
		case PYLON:
			return AbilityHelper.instance.getUsageElementsFor(this);
		case REACH:
			return AbilityHelper.instance.getUsageElementsFor(this).scale(0.5F);
		default:
			return null;
		}
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
		if (ep.worldObj.isRemote) {
			ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.ABILITY.ordinal(), ep.worldObj, 0, 0, 0, this.ordinal(), data);

			if (!actOnClient)
				return;
		}

		ProgressionManager.instance.stepPlayerTo(ep, ProgressStage.ABILITY);
		ElementTagCompound use = AbilityHelper.instance.getUsageElementsFor(this);
		if (this == HEALTH)
			use.scale(5);
		PlayerElementBuffer.instance.removeFromPlayer(ep, use);
		boolean flag = this.enabledOn(ep) || this.isPureEventDriven();
		this.setToPlayer(ep, !flag);

		if (tickBased) {

		}
		else {
			switch(this) {
			case REACH:
				this.setReachDistance(ep, this.enabledOn(ep) ? 128 : -1); //use data?
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
			default:
				break;
			}
		}
	}

	private boolean isPureEventDriven() {
		switch(this) {
		case SONIC:
		case SHIFT:
		case HEAL:
		case FIREBALL:
			return true;
		default:
			return false;
		}
	}

	private void setPlayerMaxHealth(EntityPlayer ep, int value) {
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
	}

	public static ArrayList<Chromabilities> getFrom(EntityPlayer ep) {
		ArrayList<Chromabilities> li = new ArrayList();
		NBTTagCompound nbt = ep.getEntityData();
		NBTTagCompound abilities = nbt.getCompoundTag(NBT_TAG);
		if (abilities != null && !abilities.hasNoTags()) {
			Iterator<String> it = abilities.func_150296_c().iterator();
			while (it.hasNext()) {
				String n = it.next();
				//ReikaJavaLibrary.pConsole(n+":"+abilities.getBoolean(n), Side.SERVER);
				if (abilities.getBoolean(n)) {
					Chromabilities c = tagMap.get(n);
					li.add(c);
				}
			}
		}
		return li;
	}

	public void setToPlayer(EntityPlayer ep, boolean set) {
		NBTTagCompound nbt = ep.getEntityData();
		NBTTagCompound abilities = nbt.getCompoundTag(NBT_TAG);
		if (abilities == null) {
			abilities = new NBTTagCompound();
		}
		abilities.setBoolean(this.getNBTName(), set);
		nbt.setTag(NBT_TAG, abilities);
		if (ep instanceof EntityPlayerMP)
			ReikaPlayerAPI.syncCustomData((EntityPlayerMP)ep);
	}

	private String getNBTName() {
		return this.name().toLowerCase();
	}

	public boolean enabledOn(EntityPlayer ep) {
		NBTTagCompound nbt = ep.getEntityData();
		NBTTagCompound abilities = nbt.getCompoundTag(NBT_TAG);
		return abilities != null && abilities.getBoolean(this.getNBTName());
	}

	public boolean playerHasAbility(EntityPlayer ep) {
		NBTTagCompound nbt = ep.getEntityData();
		NBTTagCompound abilities = nbt.getCompoundTag(NBT_TAG);
		return abilities != null && abilities.hasKey(this.getNBTName());
	}

	public void give(EntityPlayer ep) {
		this.setToPlayer(ep, false);
	}

	public void removeFromPlayer(EntityPlayer ep) {
		this.setToPlayer(ep, false);
		switch(this) {
		case REACH:
			this.setReachDistance(ep, -1);
			break;
		case HEALTH:
			this.setPlayerMaxHealth(ep, 0);
			break;
		default:
			break;
		}
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

	public static void shiftArea(WorldServer world, BlockBox box, ForgeDirection dir, int dist, EntityPlayer ep) {
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
				if (e.getEntityToAttack() == ep) {
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

	static {
		for (int i = 0; i < abilities.length; i++) {
			Chromabilities c = abilities[i];
			tagMap.put(c.getNBTName(), c);
		}
	}

	public boolean canPlayerExecuteAt(EntityPlayer player) {
		ElementTagCompound use = AbilityHelper.instance.getUsageElementsFor(this);
		return PlayerElementBuffer.instance.playerHas(player, use);
	}

	public int maxPower(EntityPlayer ep) {
		int base = this.maxPower();
		int lvl = base;
		ElementTagCompound use = AbilityHelper.instance.getElementsFor(this).scale(0.01F);
		for (CrystalElement e : use.elementSet()) {
			lvl = (int)Math.min(lvl, PlayerElementBuffer.instance.getPlayerContent(ep, e)/(float)use.getValue(e));
		}
		return Math.max(1, lvl);
	}

	public int maxPower() {
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
		default:
			return 0;
		}
	}
}
