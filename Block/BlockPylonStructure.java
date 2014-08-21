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
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalPylon;
import Reika.ChromatiCraft.World.PylonGenerator;
import Reika.DragonAPI.Instantiable.Data.StructuredBlockArray;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
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
		this.triggerBreakCheck(world, x, y, z);

		super.breakBlock(world, x, y, z, oldB, oldM);
	}

	public void triggerBreakCheck(World world, int x, int y, int z) {
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
			((TileEntityCrystalPylon)te).invalidateMultiblock();
		}
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
			if (PylonGenerator.getPylonStructure(world, mx, my, mz, ((TileEntityCrystalPylon)te).getColor()).matchInWorld()) {
				((TileEntityCrystalPylon)te).validateMultiblock();
			}
		}

		super.onBlockAdded(world, x, y, z);
	}

}
