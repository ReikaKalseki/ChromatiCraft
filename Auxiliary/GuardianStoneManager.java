/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import Reika.ChromatiCraft.TileEntity.TileEntityGuardianStone;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.ChromatiCraft.ChromatiCraft;

import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
;

public class GuardianStoneManager {

	public static final GuardianStoneManager instance = new GuardianStoneManager();

	private final ArrayList<ProtectionZone> zones = new ArrayList();

	private GuardianStoneManager() {

	}

	public boolean canPlayerOverrideProtections(EntityPlayer ep) {
		if ("Reika_Kalseki".equals(ep.getCommandSenderName()))
			return true;
		return ReikaPlayerAPI.isAdmin(ep);
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

	protected ArrayList<ProtectionZone> getProtectionZonesForPlayer(EntityPlayer ep) {
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
	public void guardArea(PlayerInteractEvent event) {
		EntityPlayer ep = event.entityPlayer;
		World world = ep.worldObj;
		int x = event.x;
		int y = event.y;
		int z = event.z;
		if (!world.isRemote) {
			if (!this.doesPlayerHavePermissions(world, x, y, z, ep))
				event.setCanceled(true);
		}
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
