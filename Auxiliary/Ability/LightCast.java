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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.Auxiliary.ProgressiveRecursiveBreaker.BreakerCallback;
import Reika.DragonAPI.Auxiliary.ProgressiveRecursiveBreaker.ProgressiveBreaker;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public class LightCast implements BreakerCallback {

	private final EntityPlayer player;

	private static final HashSet<Block> blockPasses = new HashSet();

	static {
		blockPasses.add(Blocks.wooden_door);
		blockPasses.add(Blocks.iron_door);
		blockPasses.add(Blocks.trapdoor);
		blockPasses.add(Blocks.ladder);
		blockPasses.add(Blocks.iron_bars);
		blockPasses.add(Blocks.rail);
		blockPasses.add(Blocks.web);
		blockPasses.add(Blocks.fence);
		blockPasses.add(Blocks.reeds);
		blockPasses.add(Blocks.cactus);
		blockPasses.add(ChromaBlocks.CRYSTAL.getBlockInstance());
	}

	public static Collection<Block> getPassthroughBlocks() {
		return Collections.unmodifiableCollection(blockPasses);
	}

	public LightCast(EntityPlayer ep) {
		player = ep;
	}

	private boolean isValidBlock(World world, int x, int y, int z, Block id, int meta) {
		if (id.isAir(world, x, y, z))
			return true;
		if (blockPasses.contains(id))
			return true;//((BlockDoor)id).func_150015_f(world, x, y, z);
		return false;
	}

	@Override
	public boolean canBreak(ProgressiveBreaker b, World world, int x, int y, int z, Block id, int meta) {
		return this.isValidBlock(world, x, y, z, id, meta) && !world.canBlockSeeTheSky(x, y+1, z) && world.getSavedLightValue(EnumSkyBlock.Sky, x, y, z) == 0;
	}

	@Override
	public void onPreBreak(ProgressiveBreaker b, World world, int x, int y, int z, Block id, int meta) {

	}

	@Override
	public void onPostBreak(ProgressiveBreaker b, World world, int x, int y, int z, Block id, int meta) {
		if (world.getBlockLightValue(x, y, z) < 2) {
			world.setBlock(x, y, z, ChromaBlocks.LIGHT.getBlockInstance(), 0, 3);
			world.updateLightByType(EnumSkyBlock.Block, x, y, z);
		}
	}

	@Override
	public void onFinish(ProgressiveBreaker b) {

	}

}
