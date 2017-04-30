/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.Ability;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;

public class WarpPoint {

	public final String label;
	public final WorldLocation location;

	WarpPoint(String s, EntityPlayer ep) {
		this(s, new WorldLocation(ep));
	}

	private WarpPoint(String s, World world, int x, int y, int z) {
		this(s, new WorldLocation(world, x, y, z));
	}

	WarpPoint(String s, WorldLocation loc) {
		label = s;
		location = loc;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof WarpPoint && ((WarpPoint)o).location.equals(location);
	}

	@Override
	public int hashCode() {
		return ~location.hashCode();
	}

	@Override
	public String toString() {
		return label+" ("+location.toString()+")";
	}

	void teleportPlayerTo(EntityPlayerMP ep) {
		ChromatiCraft.logger.log("Warping player "+ep.getCommandSenderName()+" to warppoint "+this);
		if (ep.worldObj.provider.dimensionId != location.dimensionID) {
			DimensionManager.getWorld(location.dimensionID).getBlock(location.xCoord, location.yCoord, location.zCoord); //force load
			ReikaEntityHelper.transferEntityToDimension(ep, location.dimensionID);
		}
		//ep.setPositionAndUpdate(location.xCoord+0.5, location.yCoord+0.25, location.zCoord+0.5);
		ep.playerNetServerHandler.setPlayerLocation(location.xCoord+0.5, location.yCoord+0.5, location.zCoord+0.5, ep.rotationYaw, ep.rotationPitch);
		ep.playSound("mob.endermen.portal", 1, 1);
		ChromatiCraft.logger.log("Player position: "+new DecimalPosition(ep));
	}

	public boolean canTeleportPlayer(EntityPlayer ep) {
		int dim = ep.worldObj.provider.dimensionId;
		if (location.dimensionID == ExtraChromaIDs.DIMID.getValue() || dim == ExtraChromaIDs.DIMID.getValue())
			return dim == location.dimensionID;
		return true;
	}

}
