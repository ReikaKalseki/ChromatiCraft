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

public class BlockStructureShield extends Block {

	public static enum BlockType {
		CLOAK("Cloak"),
		STONE("Stone"),
		COBBLE("Cobble"),
		CRACK("Crack"),
		MOSS("Moss");

		public final String name;
		public final int metadata;

		public static final BlockType[] list = values();

		private BlockType(String s) {
			name = s;
			metadata = this.ordinal()+8;
		}
	}

	private static final IIcon[] icons = new IIcon[BlockType.list.length];

	public BlockStructureShield(Material mat) {
		super(mat);
		this.setHardness(2);
		this.setResistance(6000);
		this.setCreativeTab(ChromatiCraft.tabChroma);
		stepSound = soundTypeStone;
	}

	@Override
	public float getBlockHardness(World world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		return meta >= 8 && meta != BlockType.CRACK.metadata ? -1 : super.getBlockHardness(world, x, y, z);
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return icons[meta%8];
	}

	@Override
	public int damageDropped(int meta) {
		return meta;
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
		return meta < 8 || (meta == BlockType.STONE.metadata && side == ForgeDirection.UP);
	}

}
