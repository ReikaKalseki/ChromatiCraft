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

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Auxiliary.Interfaces.ProgressionTrigger;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalPylon;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickHandler;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickType;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;

public class ExplorationMonitor implements TickHandler {

	public static final ExplorationMonitor instance = new ExplorationMonitor();

	private ExplorationMonitor() {

	}

	@Override
	public void tick(TickType type, Object... tickData) {
		EntityPlayer ep = (EntityPlayer)tickData[0];
		World world = ep.worldObj;
		if (!world.isRemote) {
			//ProgressionManager.instance.setPlayerDiscoveredColor(ep, CrystalElement.RED, true);
			MovingObjectPosition mov = ReikaPlayerAPI.getLookedAtBlock(ep, 4, true);
			if (mov != null) {
				int x = mov.blockX;
				int y = mov.blockY;
				int z = mov.blockZ;
				Block b = world.getBlock(x, y, z);
				if (b instanceof ProgressionTrigger) {
					ProgressStage[] ps = ((ProgressionTrigger)b).getTriggers(ep, world, x, y, z);
					if (ps != null) {
						for (int i = 0; i < ps.length; i++) {
							ProgressStage p = ps[i];
							ProgressionManager.instance.stepPlayerTo(ep, p);
						}
					}
				}
				else if (ChromaTiles.getTile(world, x, y, z) == ChromaTiles.PYLON) {
					TileEntityCrystalPylon te = (TileEntityCrystalPylon)world.getTileEntity(x, y, z);
					ProgressionManager.instance.setPlayerDiscoveredColor(ep, te.getColor(), true);
				}
			}
		}
	}

	@Override
	public TickType getType() {
		return TickType.PLAYER;
	}

	@Override
	public boolean canFire(Phase p) {
		return p == Phase.START;
	}

	@Override
	public String getLabel() {
		return "ChromatiCraft Exploration Monitor";
	}

}
