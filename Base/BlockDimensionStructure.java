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

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;
import Reika.ChromatiCraft.World.Dimension.DimensionTuningManager.TuningThresholds;
import Reika.DragonAPI.DragonAPICore;


public abstract class BlockDimensionStructure extends Block {

	protected BlockDimensionStructure(Material mat) {
		super(mat);
		this.setResistance(60000);
		this.setBlockUnbreakable();
		this.setCreativeTab(DragonAPICore.isReikasComputer() ? ChromatiCraft.tabChromaGen : null);
	}

	@Override
	public final ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		return new ArrayList();
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

	@Override
	public final void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity e) {
		if (world.provider.dimensionId == ExtraChromaIDs.DIMID.getValue() && e instanceof EntityPlayer && !TuningThresholds.STRUCTURES.isSufficientlyTuned((EntityPlayer)e)) {

		}
		else {
			this.onEntityCollision(world, x, y, z, e);
		}
	}

	protected void onEntityCollision(World world, int x, int y, int z, Entity e) {

	}

}
