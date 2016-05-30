/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.TileEntity.AOE.Defence.TileEntityGuardianStone;
import Reika.DragonAPI.Instantiable.Data.KeyedItemStack;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap.HashSetFactory;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;


public class GuardianStoneManager {

	public static final GuardianStoneManager instance = new GuardianStoneManager();

	private final ArrayList<ProtectionZone> zones = new ArrayList();

	private final MultiMap<BlockKey, Action> blockExceptions = new MultiMap(new HashSetFactory());
	private final MultiMap<KeyedItemStack, Action> itemExceptions = new MultiMap(new HashSetFactory());

	private GuardianStoneManager() {
		for (String s : ChromatiCraft.config.getGuardianExceptions()) {
			if (this.parseString(s)) {
				ChromatiCraft.logger.log("Registered Guardian Stone Exception: "+s);
			}
			else {
				ChromatiCraft.logger.logError("Could not parse Guardian Stone exception, due to a malformed line (or missing item): "+s);
			}
		}
	}

	private boolean parseString(String s) {
		String[] parts = s.split("#");
		if (parts.length != 2)
			return false;
		ItemStack is = ReikaItemHelper.lookupItem(parts[0]);
		if (is == null)
			return false;
		Action a = null;
		try {
			a = Action.valueOf(parts[1].toUpperCase(Locale.ENGLISH));
		}
		catch (IllegalArgumentException e) {
			return false;
		}
		if (a == null)
			return false;
		Block b = Block.getBlockFromItem(is.getItem());
		if (b != null) {
			this.addBlockException(new BlockKey(b, is.getItemDamage()), a);
		}
		else {
			this.addItemException(is, a);
		}
		return true;
	}

	public void addBlockException(BlockKey bk, Action a) {
		blockExceptions.addValue(bk, a);
	}

	public void addItemException(ItemStack is, Action a) {
		itemExceptions.addValue(new KeyedItemStack(is).setSimpleHash(true), a);
	}

	public boolean canPlayerOverrideProtections(EntityPlayer ep) {
		if (ReikaPlayerAPI.isFake(ep))
			return false;
		return ReikaPlayerAPI.isReika(ep) || (ep instanceof EntityPlayerMP && ReikaPlayerAPI.isAdmin((EntityPlayerMP)ep));
	}

	public ProtectionZone addZone(World world, int x, int y, int z, EntityPlayer ep, int r) {
		if (ep == null) {
			int dim = world.provider.dimensionId;
			ChromatiCraft.logger.logError("Tried to generate a protection zone for a null player at "+x+", "+y+", "+z+" in "+dim);
			return null;
		}
		ProtectionZone zone = new ProtectionZone(world, ep, x, y, z, r);
		zones.add(zone);
		return zone;
	}

	private ArrayList<ProtectionZone> getProtectionZonesForArea(World world, int x, int y, int z) {
		ArrayList<ProtectionZone> in = new ArrayList();
		for (int i = 0; i < zones.size(); i++) {
			ProtectionZone zone = zones.get(i);
			if (world.provider.dimensionId == zone.dimensionID) {
				if (zone.isBlockInZone(x, y, z))
					in.add(zone);
			}
		}
		return in;
	}

	public ArrayList<ProtectionZone> getProtectionZonesForPlayer(EntityPlayer ep) {
		ArrayList<ProtectionZone> in = new ArrayList();
		for (int i = 0; i < zones.size(); i++) {
			ProtectionZone zone = zones.get(i);
			if (zone.creator.equals(ep.getCommandSenderName())) {
				in.add(zone);
			}
		}
		return in;
	}

	public void removeAreasForStone(TileEntityGuardianStone te) {
		int id = te.worldObj.provider.dimensionId;
		int x = te.xCoord;
		int y = te.yCoord;
		int z = te.zCoord;
		Iterator<ProtectionZone> it = zones.iterator();
		while (it.hasNext()) {
			ProtectionZone zone = it.next();
			if (zone.originX == x && zone.originY == y && zone.originZ == z && zone.dimensionID == id)
				it.remove();
		}
	}

	public boolean doesPlayerHavePermissions(World world, int x, int y, int z, EntityPlayer ep) {
		if (this.canPlayerOverrideProtections(ep))
			return true;
		for (int i = 0; i < zones.size(); i++) {
			ProtectionZone zone = zones.get(i);
			if (world.provider.dimensionId == zone.dimensionID) {
				if (zone.isBlockInZone(x, y, z)) {
					if (!zone.canPlayerEditIn(ep))
						return false;
				}
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return zones.toString();
	}

	@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
	public void guardArea(BreakEvent event) {
		EntityPlayer ep = event.getPlayer();
		if (ep == null) {
			ChromatiCraft.logger.logError("Something tried a null-player break event!");
			ReikaJavaLibrary.dumpStack();
			return;
		}
		World world = event.world;
		int x = event.x;
		int y = event.y;
		int z = event.z;
		if (!world.isRemote) {
			if (!this.doesPlayerHavePermissions(world, x, y, z, ep))
				event.setCanceled(true);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
	public void guardArea(PlayerInteractEvent event) {
		EntityPlayer ep = event.entityPlayer;
		if (ep == null) {
			ChromatiCraft.logger.logError("Something tried a null-player interact event!");
			ReikaJavaLibrary.dumpStack();
			return;
		}
		World world = ep.worldObj;
		int x = event.x;
		int y = event.y;
		int z = event.z;
		if (!world.isRemote) {
			if (!this.doesPlayerHavePermissions(world, x, y, z, ep) && !this.isWhitelistedAction(event.action, world, x, y, z, ep.getCurrentEquippedItem()))
				event.setCanceled(true);
		}
	}

	private boolean isWhitelistedAction(Action a, World world, int x, int y, int z, ItemStack is) {
		return blockExceptions.get(BlockKey.getAt(world, x, y, z)).contains(a) || itemExceptions.get(new KeyedItemStack(is).setSimpleHash(true)).contains(a);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
	public void preventEndermen(EnderTeleportEvent event) {
		if (event.entityLiving instanceof EntityEnderman) {
			int x = MathHelper.floor_double(event.targetX);
			int y = MathHelper.floor_double(event.targetY);
			int z = MathHelper.floor_double(event.targetZ);
			if (!this.getProtectionZonesForArea(event.entityLiving.worldObj, x, y, z).isEmpty()) {
				event.setCanceled(true);
			}
		}
	}
}
