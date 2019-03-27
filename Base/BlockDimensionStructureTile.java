/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;
import Reika.ChromatiCraft.World.Dimension.DimensionTuningManager.TuningThresholds;
import Reika.DragonAPI.DragonAPICore;


public abstract class BlockDimensionStructureTile extends BlockContainer {

	protected BlockDimensionStructureTile(Material mat) {
		super(mat);
		this.setResistance(60000);
		this.setBlockUnbreakable();
		this.setCreativeTab(DragonAPICore.isReikasComputer() ? ChromatiCraft.tabChromaGen : null);
	}

	@Override
	public final boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		if (world.provider.dimensionId != ExtraChromaIDs.DIMID.getValue() || TuningThresholds.STRUCTURES.isSufficientlyTuned(ep)) {
			return this.onRightClicked(world, x, y, z, ep, s, a, b, c);
		}
		else {
			ChromaSounds.ERROR.playSoundAtBlock(world, x, y, z);
			return true;
		}
	}

	protected boolean onRightClicked(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		return false;
	}

}
