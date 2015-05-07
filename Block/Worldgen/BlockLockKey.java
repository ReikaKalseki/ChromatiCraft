/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Worldgen;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Block.Dimension.BlockColoredLock.TileEntityColorLock;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;

public class BlockLockKey extends Block {

	public BlockLockKey(Material mat) {
		super(mat);
		this.setCreativeTab(ChromatiCraft.tabChromaGen);
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		blockIcon = ico.registerIcon("chromaticraft:basic/key");
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return blockIcon;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		for (int i = 2; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			Block b = world.getBlock(dx, dy, dz);
			if (b == ChromaBlocks.RUNE.getBlockInstance()) {
				this.openColor(CrystalElement.elements[world.getBlockMetadata(dx, dy, dz)], world, x, y, z);
				break;
			}
		}
	}

	private static void openColor(CrystalElement e, World world, int x, int y, int z) {
		int r = 18;
		for (int i = -r; i <= r; i++) {
			for (int j = -r; j <= r; j++) {
				for (int k = -r; k <= r; k++) {
					int dx = x+i;
					int dy = y+j;
					int dz = z+k;
					Block b = world.getBlock(dx, dy, dz);
					if (b == ChromaBlocks.COLORLOCK.getBlockInstance() && world.getBlockMetadata(dx, dy, dz) == e.ordinal()) {
						TileEntityColorLock te = (TileEntityColorLock)world.getTileEntity(dx, dy, dz);
						if (te != null) {
							te.open();
							ReikaSoundHelper.playStepSound(world, dx, dy, dz, Blocks.stone);
						}
					}
				}
			}
		}
	}

	private static void closeColor(CrystalElement e, World world, int x, int y, int z) {
		int r = 18;
		for (int i = -r; i <= r; i++) {
			for (int j = -r; j <= r; j++) {
				for (int k = -r; k <= r; k++) {
					int dx = x+i;
					int dy = y+j;
					int dz = z+k;
					Block b = world.getBlock(dx, dy, dz);
					if (b == ChromaBlocks.COLORLOCK.getBlockInstance() && world.getBlockMetadata(dx, dy, dz) == e.ordinal()) {
						TileEntityColorLock te = (TileEntityColorLock)world.getTileEntity(dx, dy, dz);
						if (te != null) {
							te.close();
							ReikaSoundHelper.playStepSound(world, dx, dy, dz, Blocks.stone);
						}
					}
				}
			}
		}
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block b2, int meta2) {
		super.breakBlock(world, x, y, z, b2, meta2);

		for (int i = 2; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			Block b = world.getBlock(dx, dy, dz);
			if (b == ChromaBlocks.RUNE.getBlockInstance()) {
				this.closeColor(CrystalElement.elements[world.getBlockMetadata(dx, dy, dz)], world, x, y, z);
				break;
			}
		}
	}
	/*
	@Override
	public int damageDropped(int meta) {
		return meta;
	}

	@Override
	public int getRenderColor(int meta) {
		return CrystalElement.elements[meta].getColor();
	}

	@Override
	public int colorMultiplier(IBlockAccess iba, int x, int y, int z) {
		return this.getRenderColor(iba.getBlockMetadata(x, y, z));
	}
	 */
}
