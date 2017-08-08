/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Dye;

import java.awt.Color;
import java.util.List;

import net.minecraft.block.BlockFlower;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.EnumPlantType;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.World.BiomeRainbowForest;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockDyeFlower extends BlockFlower {

	public BlockDyeFlower() {
		super(1);
		this.setCreativeTab(ChromatiCraft.tabChromaGen);
		this.setStepSound(soundTypeGrass);
	}

	@Override
	public EnumPlantType getPlantType(IBlockAccess world, int x, int y, int z)
	{
		return EnumPlantType.Plains;
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		blockIcon = ico.registerIcon("chromaticraft:dye/flower");
	}

	@Override
	public int damageDropped(int par1)
	{
		return par1;
	}

	@Override
	public IIcon getIcon(int s, int meta)
	{
		return blockIcon;
	}

	@Override
	public int getRenderColor(int dmg)
	{
		return ReikaDyeHelper.dyes[dmg].getColor();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item it, CreativeTabs cr, List li) {
		for (int i = 0; i < ReikaDyeHelper.dyes.length; i++) {
			li.add(new ItemStack(this, 1, i));
		}
	}

	@Override
	public int colorMultiplier(IBlockAccess iba, int x, int y, int z)
	{
		int dmg = iba.getBlockMetadata(x, y, z);
		Color c = ReikaDyeHelper.dyes[dmg].getJavaColor();
		return BiomeRainbowForest.isDamaged(iba, x, z) ? ReikaColorAPI.mixColors(0xa0a0a0, c.darker().getRGB(), 0.5F) : c.brighter().getRGB();
	}

}
