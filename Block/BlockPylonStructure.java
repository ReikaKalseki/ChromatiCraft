/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaStructures;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalNetworkTile;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalSource;
import Reika.ChromatiCraft.TileEntity.TileEntityPowerTree;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalPylon;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalRepeater;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityAuraInfuser;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingTable;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityRitualTable;
import Reika.DragonAPI.Base.TileEntityBase;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.StructuredBlockArray;
import Reika.DragonAPI.Libraries.Java.ReikaArrayHelper;

public class BlockPylonStructure extends Block {

	private final IIcon[][] icons = new IIcon[16][16];

	private final int[] variants = ReikaArrayHelper.getArrayOf(1, 16);

	public BlockPylonStructure(Material mat) {
		super(mat);
		this.setHardness(4);
		this.setResistance(12);
		this.setCreativeTab(ChromatiCraft.tabChroma);

		variants[0] = 3;
		variants[6] = 4;
		variants[15] = 2;
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		if (s < 2 && meta < 6)
			return icons[0][0];
		return icons[meta][0];
	}

	@Override
	public IIcon getIcon(IBlockAccess iba, int x, int y, int z, int s) {
		int meta = iba.getBlockMetadata(x, y, z);
		if (meta == 15 && s <= 1) {
			if (iba.getBlock(x+1, y, z) == this && iba.getBlockMetadata(x+1, y, z) == meta)
				return icons[meta][0];
			if (iba.getBlock(x-1, y, z) == this && iba.getBlockMetadata(x-1, y, z) == meta)
				return icons[meta][0];

			if (iba.getBlock(x, y, z+1) == this && iba.getBlockMetadata(x, y, z+1) == meta)
				return icons[meta][1];
			if (iba.getBlock(x, y, z-1) == this && iba.getBlockMetadata(x, y, z-1) == meta)
				return icons[meta][1];
		}
		if ((meta == 1 || meta == 4) && s <= 1) {
			if (iba.getBlock(x+1, y, z) == this && iba.getBlockMetadata(x+1, y, z) == meta)
				return icons[0][2];
			if (iba.getBlock(x-1, y, z) == this && iba.getBlockMetadata(x-1, y, z) == meta)
				return icons[0][2];

			if (iba.getBlock(x, y, z+1) == this && iba.getBlockMetadata(x, y, z+1) == meta)
				return icons[0][1];
			if (iba.getBlock(x, y, z-1) == this && iba.getBlockMetadata(x, y, z-1) == meta)
				return icons[0][1];
		}
		return super.getIcon(iba, x, y, z, s);
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		for (int i = 0; i < 16; i++) {
			for (int k = 0; k < variants[i]; k++) {
				String suff = k > 0 ? String.valueOf(i+"-"+(k+1)) : String.valueOf(i);
				icons[i][k] = ico.registerIcon("chromaticraft:pylon/block_"+suff);
			}
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
	public void onBlockDestroyedByExplosion(World world, int x, int y, int z, Explosion e) {
		int r = 12;
		for (int i = -r; i <= r; i++) {
			for (int j = -r; j <= r; j++) {
				for (int k = -r; k <= r; k++) {
					int dx = x+i;
					int dy = y+j;
					int dz = z+k;
					TileEntity te = world.getTileEntity(dx, dy, dz);
					if (te instanceof TileEntityCrystalPylon) {
						((TileEntityCrystalPylon)te).invalidateMultiblock();
					}
					if (te instanceof TileEntityCrystalRepeater) {
						((TileEntityCrystalRepeater)te).validateStructure();
					}
				}
			}
		}
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block oldB, int oldM) {
		this.triggerBreakCheck(world, x, y, z);

		super.breakBlock(world, x, y, z, oldB, oldM);
	}

	@Override
	public float getPlayerRelativeBlockHardness(EntityPlayer ep, World world, int x, int y, int z) {
		int dy = y;
		TileEntity te = world.getTileEntity(x, dy, z);
		while (dy-y < 5 && !(te instanceof CrystalNetworkTile)) {
			dy++;
			te = world.getTileEntity(x, dy, z);
		}
		if (!(te instanceof TileEntityBase))
			return super.getPlayerRelativeBlockHardness(ep, world, x, y, z);
		if (te instanceof CrystalSource)
			return -1;
		return ((TileEntityBase)te).isPlacer(ep) ? super.getPlayerRelativeBlockHardness(ep, world, x, y, z) : -1;
	}

	void triggerBreakCheck(World world, int x, int y, int z) {
		StructuredBlockArray blocks = new StructuredBlockArray(world);

		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			blocks.recursiveAddWithBounds(world, dx, dy, dz, this, x-16, y-16, z-16, x+16, y+16, z+16);
		}

		int mx = blocks.getMidX();
		int my = blocks.getMinY(); //intentionally bottom
		int mz = blocks.getMidZ();

		TileEntity te = world.getTileEntity(mx, my+9, mz);
		if (te instanceof TileEntityCrystalPylon) {
			((TileEntityCrystalPylon)te).invalidateMultiblock();
		}

		te = world.getTileEntity(mx, my+1, mz);
		//ReikaJavaLibrary.pConsole(te+" @ "+mx+", "+(my+1)+", "+mz, Side.SERVER);
		if (te instanceof TileEntityCastingTable) {
			((TileEntityCastingTable)te).validateStructure(blocks, world, mx, my, mz);
		}
		if (te instanceof TileEntityAuraInfuser) {
			((TileEntityAuraInfuser)te).validateMultiblock();
		}

		te = world.getTileEntity(mx, my+2, mz);
		if (te instanceof TileEntityRitualTable) {
			((TileEntityRitualTable)te).validateMultiblock(blocks, world, mx, my, mz);
		}
		if (te instanceof TileEntityAuraInfuser) {
			((TileEntityAuraInfuser)te).validateMultiblock();
		}

		for (int k = 0; k < 6; k++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[k];
			for (int i = 1; i <= 5; i++) {
				te = world.getTileEntity(x+dir.offsetX*i, y+dir.offsetY*i, z+dir.offsetZ*i);
				if (te instanceof TileEntityCrystalRepeater) {
					((TileEntityCrystalRepeater)te).validateStructure();
				}
			}
		}

		te = world.getTileEntity(blocks.getMinX()+1, blocks.getMaxY()+1, blocks.getMaxZ()-1);
		if (te instanceof TileEntityPowerTree) {
			((TileEntityPowerTree)te).validateStructure();
		}
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		this.triggerAddCheck(world, x, y, z);

		super.onBlockAdded(world, x, y, z);
	}

	void triggerAddCheck(World world, int x, int y, int z) {
		StructuredBlockArray blocks = new StructuredBlockArray(world);

		blocks.recursiveAddWithBounds(world, x, y, z, this, x-16, y-16, z-16, x+16, y+16, z+16);

		int mx = blocks.getMidX();
		int my = blocks.getMinY(); //intentionally bottom
		int mz = blocks.getMidZ();

		TileEntity te = world.getTileEntity(mx, my+9, mz);
		if (te instanceof TileEntityCrystalPylon) {
			if (ChromaStructures.getPylonStructure(world, mx, my, mz, ((TileEntityCrystalPylon)te).getColor()).matchInWorld()) {
				((TileEntityCrystalPylon)te).validateMultiblock();
			}
		}

		te = world.getTileEntity(mx, my+1, mz);
		if (te instanceof TileEntityCastingTable) {
			((TileEntityCastingTable)te).validateStructure(blocks, world, mx, my, mz);
		}
		if (te instanceof TileEntityAuraInfuser) {
			((TileEntityAuraInfuser)te).validateMultiblock();
		}

		te = world.getTileEntity(mx, my+2, mz);
		if (te instanceof TileEntityRitualTable) {
			((TileEntityRitualTable)te).validateMultiblock(blocks, world, mx, my, mz);
		}

		for (int k = 0; k < 6; k++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[k];
			for (int i = 1; i <= 5; i++) {
				te = world.getTileEntity(x+dir.offsetX*i, y+dir.offsetY*i, z+dir.offsetZ*i);
				if (te instanceof TileEntityCrystalRepeater) {
					((TileEntityCrystalRepeater)te).validateStructure();
				}
			}
		}

		te = world.getTileEntity(blocks.getMinX()+1, blocks.getMaxY()+1, blocks.getMaxZ()-1);
		if (te instanceof TileEntityPowerTree) {
			((TileEntityPowerTree)te).validateStructure();
		}
	}

}
