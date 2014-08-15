/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Dye;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.TreeShaper;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaPlantHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSapling;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDyeSapling extends BlockSapling {

	private IIcon icon;

	private static Random r = new Random();

	public BlockDyeSapling() {
		super();
		this.setCreativeTab(ChromatiCraft.tabChroma);
		this.setStepSound(soundTypeGrass);
	}

	@Override
	public void func_149878_d(World world, int x, int y, int z, Random r) {
		if (this.canGrowAt(world, x, y, z, false))
			this.func_149878_d(world, x, y, z, this.getGrowthHeight());
	}

	private int getGrowthHeight() {
		return 5+r.nextInt(3);
	}

	public void func_149878_d(World world, int x, int y, int z, int h)
	{
		if (world.isRemote)
			return;
		int meta = world.getBlockMetadata(x, y, z);
		TreeShaper.getInstance().generateRandomWeightedTree(world, x, y, z, ReikaDyeHelper.dyes[meta], true);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int p6, float a, float b, float c) {
		ItemStack is = ep.getCurrentEquippedItem();
		if (is == null)
			return false;
		if (!ReikaItemHelper.matchStacks(is, ReikaDyeHelper.WHITE.getStackOf()))
			return false;
		int color = world.getBlockMetadata(x, y, z);
		if (this.canGrowAt(world, x, y, z, true))
			this.func_149878_d(world, x, y, z, ep.isSneaking() ? 7 : this.getGrowthHeight());
		else
			world.spawnParticle("happyVillager", x+r.nextDouble(), y+r.nextDouble(), z+r.nextDouble(), 0, 0, 0);
		if (!ep.capabilities.isCreativeMode)
			is.stackSize--;
		return true;
	}

	@Override
	public IIcon getIcon(int par1, int par2)
	{
		return icon;
	}

	@Override
	public void registerBlockIcons(IIconRegister ico)
	{
		icon = ico.registerIcon("ChromatiCraft:dye/sapling");
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

	public static boolean canGrowAt(World world, int x, int y, int z, boolean ignoreLight) {
		Block id = world.getBlock(x, y, z);
		if (!ReikaPlantHelper.SAPLING.canPlantAt(world, x, y, z))
			return false;
		if (id instanceof BlockLiquid)
			return false;
		for (int i = 0; i < 6; i++) {
			if (!ReikaWorldHelper.softBlocks(world, x, y+i, z) && !(i == 0 && id == ChromaBlocks.DYESAPLING.getBlockInstance()))
				return false;
		}
		return ignoreLight || world.getBlockLightValue(x, y, z) >= 9;
	}

	@Override
	public void func_149879_c(World par1World, int par2, int par3, int par4, Random par5Random)
	{
		this.func_149878_d(par1World, par2, par3, par4, par5Random);
	}

	@Override
	public int damageDropped(int par1)
	{
		return par1;
	}

}