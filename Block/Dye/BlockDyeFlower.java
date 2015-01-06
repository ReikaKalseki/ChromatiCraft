/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Dye;

import net.minecraft.block.BlockFlower;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.EnumPlantType;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;

public class BlockDyeFlower extends BlockFlower {

	public BlockDyeFlower() {
		super(1);
		this.setCreativeTab(ChromatiCraft.tabChroma);
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
	public int colorMultiplier(IBlockAccess iba, int x, int y, int z)
	{
		int dmg = iba.getBlockMetadata(x, y, z);
		return ReikaDyeHelper.dyes[dmg].getJavaColor().brighter().getRGB();
	}

}
