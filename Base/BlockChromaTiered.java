/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.IBlockAccess;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.DragonAPI.Base.BlockTieredResource;

public abstract class BlockChromaTiered extends BlockTieredResource {

	public BlockChromaTiered(Material mat) {
		super(mat);
		this.setCreativeTab(ChromatiCraft.tabChroma);
	}

	public final ProgressStage getProgressStage(IBlockAccess world, int x, int y, int z) {
		return this.getProgressStage(world.getBlockMetadata(x, y, z));
	}

	public abstract ProgressStage getProgressStage(int meta);

	@Override
	public final boolean isPlayerSufficientTier(IBlockAccess world, int x, int y, int z, EntityPlayer ep) {
		return ProgressionManager.instance.isPlayerAtStage(ep, this.getProgressStage(world, x, y, z));
	}

}
