/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Worldgen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;


public class BlockSparkle extends Block {

	public BlockSparkle(Material mat) {
		super(mat);

		this.setCreativeTab(ChromatiCraft.tabChromaGen);
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random r) {
		if (this.canFall(world, x, y, z)) {
			this.doFall(world, x, y, z);
		}
		else {

		}
	}

	@Override
	public float getPlayerRelativeBlockHardness(EntityPlayer ep, World world, int x, int y, int z) {
		return BlockTypes.list[world.getBlockMetadata(x, y, z)].proxy.getPlayerRelativeBlockHardness(ep, world, x, y, z);
	}

	@Override
	public float getExplosionResistance(Entity e, World world, int x, int y, int z, double ex, double ey, double ez) {
		return BlockTypes.list[world.getBlockMetadata(x, y, z)].proxy.getExplosionResistance(e, world, x, y, z, ex, ey, ez);
	}

	private boolean canFall(World world, int x, int y, int z) {
		return BlockTypes.list[world.getBlockMetadata(x, y, z)].canFall();
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block b) {
		world.scheduleBlockUpdate(x, y, z, this, this.tickRate(world));
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		world.scheduleBlockUpdate(x, y, z, this, this.tickRate(world));
	}

	@Override
	public int tickRate(World world) {
		return 2;
	}

	private void doFall(World world, int x, int y, int z) {
		if (!world.isRemote)
			ReikaWorldHelper.triggerFallingBlock(world, x, y, z, this);
	}

	@Override
	public int damageDropped(int meta) {
		return meta;
	}

	@Override
	public int getRenderType() {
		return ChromatiCraft.proxy.sparkleRender;
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return BlockTypes.list[meta].proxy.getIcon(s, 0);
	}

	@Override
	public boolean isReplaceableOreGen(World world, int x, int y, int z, Block b) {
		return b == this || b == BlockTypes.list[world.getBlockMetadata(x, y, z)].proxy;
	}

	public static enum BlockTypes {
		DIRT(Blocks.dirt),
		SAND(Blocks.sand),
		GRAVEL(Blocks.gravel),
		COBBLE(Blocks.cobblestone),
		STONE(Blocks.stone),
		CLAY(Blocks.clay);

		private final Block proxy;

		public static BlockTypes[] list = values();

		private BlockTypes(Block b) {
			proxy = b;
		}

		public boolean canFall() {
			return proxy instanceof BlockFalling;
		}

		public Block getBlockProxy() {
			return proxy;
		}

		public boolean isGround() {
			return proxy.getMaterial() == Material.ground || proxy.getMaterial() == Material.grass || proxy == Blocks.gravel || proxy == Blocks.clay || proxy == Blocks.sand;
		}
	}

}
