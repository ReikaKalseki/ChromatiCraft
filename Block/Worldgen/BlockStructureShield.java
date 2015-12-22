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

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.Interfaces.Block.SemiUnbreakable;

public class BlockStructureShield extends Block implements SemiUnbreakable {

	public static enum BlockType {
		CLOAK("Cloak"),
		STONE("Stone"),
		COBBLE("Cobble"),
		CRACK("Crack"),
		MOSS("Moss"),
		GLASS("Glass"),
		LIGHT("Light"),
		CRACKS("Cracks");

		public final String name;
		public final int metadata;

		public static final BlockType[] list = values();

		private BlockType(String s) {
			name = s;
			metadata = this.ordinal()+8;
		}

		public boolean isOpaque(ForgeDirection side) {
			return this == STONE && side == ForgeDirection.UP;
		}

		public boolean isTransparent(ForgeDirection side) {
			return this == CRACK || this == GLASS || this == CRACKS;
		}

		public boolean isTransparentToLight() {
			return this == GLASS;
		}

		public int getLightValue() {
			return this == LIGHT ? 15 : 0;
		}

		public boolean isMineable() {
			return this == CRACK || this == CRACKS;
		}
	}

	protected final IIcon[] icons = new IIcon[BlockType.list.length];

	public BlockStructureShield(Material mat) {
		super(mat);
		this.setHardness(2);
		this.setResistance(6000);
		this.setCreativeTab(ChromatiCraft.tabChromaGen);
		stepSound = soundTypeStone;
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		return BlockType.list[meta%8].getLightValue();
	}

	@Override
	public int getLightOpacity(IBlockAccess world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		return BlockType.list[meta%8].isTransparentToLight() ? 0 : 255;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return true;
	}

	@Override
	public float getBlockHardness(World world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		return meta >= 8 && !BlockType.list[meta%8].isMineable() ? -1 : super.getBlockHardness(world, x, y, z);
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return icons[meta%8];
	}

	@Override
	public int damageDropped(int meta) {
		return meta%8;
	}

	@Override
	public Item getItemDropped(int meta, Random r, int fortune) {
		return super.getItemDropped(meta, r, fortune);
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		for (int i = 0; i < BlockType.list.length; i++) {
			icons[i] = ico.registerIcon("chromaticraft:basic/shield_"+i);
		}
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side)
	{
		int meta = world.getBlockMetadata(x, y, z);
		if (BlockType.list[meta%8].isOpaque(side))
			return true;
		if (BlockType.list[meta%8].isTransparent(side))
			return false;
		return meta < 8;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess world, int dx, int dy, int dz, int s) {
		ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[s];
		Block b = world.getBlock(dx, dy, dz);
		if (b.isOpaqueCube())
			return false;
		if (b == this)
			return false;
		switch(dir) {
			case EAST:
			case WEST:
			case SOUTH:
			case NORTH:
			case UP:
			case DOWN:
			default:
				return true;
		}
	}

	@Override
	public boolean isUnbreakable(World world, int x, int y, int z, int meta) {
		return meta >= 8 && !BlockType.list[meta%8].isMineable();
	}

}
