/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import java.util.EnumSet;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Auxiliary.Interfaces.ProgressionTrigger;
import Reika.ChromatiCraft.Magic.Progression.ProgressStage;
import Reika.ChromatiCraft.Magic.Progression.ProgressionManager;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalPylon;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickHandler;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickType;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.ModInteract.ItemHandlers.ThaumItemHelper;

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

				if (ChromaTiles.getTile(world, x, y, z) == ChromaTiles.PYLON) {
					TileEntityCrystalPylon te = (TileEntityCrystalPylon)world.getTileEntity(x, y, z);
					if (te.hasStructure())
						ProgressionManager.instance.setPlayerDiscoveredColor(ep, te.getColor(), true, true);
				}

				Block b = world.getBlock(x, y, z);
				if (b instanceof ProgressionTrigger) {
					ProgressStage[] ps = ((ProgressionTrigger)b).getTriggers(ep, world, x, y, z);
					if (ps != null) {
						for (int i = 0; i < ps.length; i++) {
							ProgressStage p = ps[i];
							p.stepPlayerTo(ep);
						}
					}
				}
				else if (b == Blocks.bedrock && y < 6) {
					ProgressStage.BEDROCK.stepPlayerTo(ep);
				}
				else if (b == Blocks.mob_spawner) {
					ProgressStage.FINDSPAWNER.stepPlayerTo(ep);
				}
				else if (ModList.THAUMCRAFT.isLoaded() && b == ThaumItemHelper.BlockEntry.NODE.getBlock()) {
					ProgressStage.NODE.stepPlayerTo(ep);
				}
			}

			if (ep.worldObj.provider.dimensionId == -1 && ep.posY > 128) {
				ProgressStage.NETHERROOF.stepPlayerTo(ep);
			}
		}
	}

	@Override
	public EnumSet<TickType> getType() {
		return EnumSet.of(TickType.PLAYER);
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
