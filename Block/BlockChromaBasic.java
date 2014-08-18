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

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

public class BlockChromaBasic extends Block {

	private final IIcon[] icons = new IIcon[16];

	public BlockChromaBasic(Material mat) {
		super(mat);
		this.setHardness(2);
		this.setResistance(8);
		this.setCreativeTab(ChromatiCraft.tabChroma);
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return icons[meta];
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		for (int i = 0; i < 16; i++) {
			icons[i] = ico.registerIcon("chromaticraft:basic/block_"+i);
		}
	}

	@Override
	public int damageDropped(int meta) {
		return meta;
	}

}
