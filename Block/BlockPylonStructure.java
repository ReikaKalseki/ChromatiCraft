/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalPylon;
import Reika.DragonAPI.Instantiable.Data.FilledBlockArray;
import Reika.DragonAPI.Instantiable.Data.StructuredBlockArray;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockPylonStructure extends Block {

	private final IIcon[] icons = new IIcon[16];

	public BlockPylonStructure(Material mat) {
		super(mat);
		this.setHardness(4);
		this.setResistance(12);
		this.setCreativeTab(ChromatiCraft.tabChroma);
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		if (s < 2 && meta < 6)
			return icons[0];
		return icons[meta];
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		for (int i = 0; i < 16; i++) {
			icons[i] = ico.registerIcon("chromaticraft:pylon/block_"+i);
		}
	}

	@Override
	public int damageDropped(int meta) {
		switch(meta) {
		case 3:
			return 2;
		case 4:
			return 1;
		case 5:
			return 9;
		default:
			return meta;
		}
	}

	@Override
	public boolean canSilkHarvest() {
		return true;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block oldB, int oldM) {
		StructuredBlockArray blocks = new StructuredBlockArray(world);

		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			blocks.recursiveAddWithBounds(world, dx, dy, dz, this, x-12, y-12, z-12, x+12, y+12, z+12);
		}

		int mx = blocks.getMidX();
		int my = blocks.getMinY(); //intentionally middle
		int mz = blocks.getMidZ();

		TileEntity te = world.getTileEntity(mx, my+9, mz);
		if (te instanceof TileEntityCrystalPylon) {
			((TileEntityCrystalPylon)te).hasMultiblock = false;
		}

		super.breakBlock(world, x, y, z, oldB, oldM);
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		StructuredBlockArray blocks = new StructuredBlockArray(world);

		blocks.recursiveAddWithBounds(world, x, y, z, this, x-12, y-12, z-12, x+12, y+12, z+12);

		int mx = blocks.getMidX();
		int my = blocks.getMinY(); //intentionally middle
		int mz = blocks.getMidZ();

		TileEntity te = world.getTileEntity(mx, my+9, mz);
		if (te instanceof TileEntityCrystalPylon) {
			if (this.isValidStructure(world, mx, my, mz, ((TileEntityCrystalPylon)te).getColor())) {
				((TileEntityCrystalPylon)te).hasMultiblock = true;
			}
		}

		super.onBlockAdded(world, x, y, z);
	}

	private boolean isValidStructure(World world, int x, int y, int z, CrystalElement e) {
		FilledBlockArray array = new FilledBlockArray(world);
		Block b = this;
		for (int n = 0; n <= 9; n++) {
			int dy = y+n;
			Block b2 = n == 0 ? b : Blocks.air;
			for (int i = 2; i < 6; i++) {
				ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
				for (int k = 0; k <= 3; k++) {
					int dx = x+dir.offsetX*k;
					int dz = z+dir.offsetZ*k;
					array.setBlock(dx, dy, dz, b2, 0);
					if (dir.offsetX == 0) {
						array.setBlock(dx+dir.offsetZ, dy, dz, b2, 0);
						array.setBlock(dx-dir.offsetZ, dy, dz, b2, 0);
					}
					else if (dir.offsetZ == 0) {
						array.setBlock(dx, dy, dz+dir.offsetX, b2, 0);
						array.setBlock(dx, dy, dz-dir.offsetX, b2, 0);
					}
				}
			}
		}

		for (int i = 1; i <= 5; i++) {
			int dy = y+i;
			Block b2 = i < 5 ? b : ChromaBlocks.RUNE.getBlockInstance();
			int meta = (i == 2 || i == 3) ? 2 : (i == 4 ? 7 : 8);
			if (i == 5) //rune
				meta = e.ordinal();
			array.setBlock(x-3, dy, z+1, b2, meta);
			array.setBlock(x-3, dy, z-1, b2, meta);

			array.setBlock(x+3, dy, z+1, b2, meta);
			array.setBlock(x+3, dy, z-1, b2, meta);

			array.setBlock(x-1, dy, z+3, b2, meta);
			array.setBlock(x-1, dy, z-3, b2, meta);

			array.setBlock(x+1, dy, z+3, b2, meta);
			array.setBlock(x+1, dy, z-3, b2, meta);
		}

		for (int n = 1; n <= 7; n++) {
			int dy = y+n;
			for (int i = -1; i <= 1; i += 2) {
				int dx = x+i;
				for (int k = -1; k <= 1; k += 2) {
					int dz = z+k;
					int meta = n == 5 ? 3 : (n == 7 ? 5 : 2);
					array.setBlock(dx, dy, dz, b, meta);
				}
			}
		}

		array.setBlock(x-3, y+4, z, b, 4);
		array.setBlock(x+3, y+4, z, b, 4);
		array.setBlock(x, y+4, z-3, b, 4);
		array.setBlock(x, y+4, z+3, b, 4);


		array.setBlock(x-2, y+3, z+1, b, 1);
		array.setBlock(x-2, y+3, z-1, b, 1);

		array.setBlock(x+2, y+3, z+1, b, 1);
		array.setBlock(x+2, y+3, z-1, b, 1);

		array.setBlock(x-1, y+3, z+2, b, 1);
		array.setBlock(x-1, y+3, z-2, b, 1);

		array.setBlock(x+1, y+3, z+2, b, 1);
		array.setBlock(x+1, y+3, z-2, b, 1);

		return array.matchInWorld();
	}

}
