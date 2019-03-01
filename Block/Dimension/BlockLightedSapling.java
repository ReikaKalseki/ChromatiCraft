/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Dimension;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockSapling;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.World.Dimension.Generators.WorldGenLightedTree;
import Reika.ChromatiCraft.World.Dimension.Generators.WorldGenLightedTree.TreeGen;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockLightedSapling extends BlockSapling {

	public BlockLightedSapling() {
		super();
		stepSound = soundTypeGrass;
		this.setCreativeTab(ChromatiCraft.tabChromaGen);
	}

	@Override
	public int getLightValue(IBlockAccess iba, int x, int y, int z) {
		return 9;
	}

	@Override
	public void func_149878_d(World world, int x, int y, int z, Random r) {
		WorldGenLightedTree.TreeGen.genList[r.nextInt(TreeGen.genList.length)].generate(world, x, y, z, r);
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return blockIcon;
	}

	@Override
	public int damageDropped(int meta) {
		return 0;
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs cr, List li)
	{
		li.add(new ItemStack(this));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ico) {
		blockIcon = ico.registerIcon("chromaticraft:dimgen/sapling");
	}

}
