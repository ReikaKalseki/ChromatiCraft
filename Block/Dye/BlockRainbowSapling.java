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
import net.minecraftforge.fluids.BlockFluidBase;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.RainbowTreeGenerator;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaPlantHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

public class BlockRainbowSapling extends BlockSapling {

	private IIcon icon;

	private static Random r = new Random();

	public BlockRainbowSapling() {
		super();
		this.setCreativeTab(ChromatiCraft.tabChromaGen);
		this.setStepSound(soundTypeGrass);
	}

	@Override
	public void func_149878_d(World world, int x, int y, int z, Random r) {
		if (this.canGrowAt(world, x, y, z))
			this.growTree(world, x, y, z, r);
	}

	public void growTree(World world, int x, int y, int z, Random r)
	{
		if (world.isRemote)
			return;

		Block id = world.getBlock(x+1, y, z);
		if (id == this)
			x++;
		id = world.getBlock(x, y, z+1);
		if (id == this)
			z++;

		RainbowTreeGenerator.getInstance().generateRainbowTree(world, x, y, z, r);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int p6, float a, float b, float c) {
		ItemStack is = ep.getCurrentEquippedItem();
		if (is == null)
			return false;
		if (!ReikaItemHelper.matchStacks(is, ReikaDyeHelper.WHITE.getStackOf()))
			return false;
		int color = world.getBlockMetadata(x, y, z);
		if (this.canGrowAt(world, x, y, z))
			this.growTree(world, x, y, z, r);
		else
			world.spawnParticle("happyVillager", x+r.nextDouble(), y+r.nextDouble(), z+r.nextDouble(), 0, 0, 0);
		if (!ep.capabilities.isCreativeMode)
			is.stackSize--;
		return true;
	}

	@Override
	public IIcon getIcon(int par1, int x)
	{
		return icon;
	}

	@Override
	public void registerBlockIcons(IIconRegister ico)
	{
		icon = ico.registerIcon("ChromatiCraft:dye/rainbowsapling");
	}

	@Override
	public final int colorMultiplier(IBlockAccess iba, int x, int y, int z)
	{
		return 0xffffff;
	}

	public boolean canGrowAt(World world, int x, int y, int z) {
		if (y < 4)
			return false;

		Block id = world.getBlock(x, y, z);
		if (id != ChromaBlocks.RAINBOWSAPLING.getBlockInstance())
			return false;

		boolean flag = false;
		for (int dx = 0; dx >= -1; dx--) {
			for (int dz = 0; dz >= -1; dz--)  {
				if (id == world.getBlock(x + dx, y, z + dz) && id == world.getBlock(x + dx + 1, y, z + dz) && id == world.getBlock(x + dx, y, z + dz + 1) && id == world.getBlock(x + dx + 1, y, z + dz + 1)) {
					flag = true;
					break;
				}
			}

			if (flag)
				break;
		}
		if (!flag)
			return false;

		if (ReikaWorldHelper.countAdjacentBlocks(world, x, y, z, this, true) != 3)
			return false;

		if (!ReikaPlantHelper.SAPLING.canPlantAt(world, x, y, z))
			return false;
		if (id instanceof BlockLiquid || id instanceof BlockFluidBase)
			return false;

		id = world.getBlock(x+1, y, z);
		if (id == ChromaBlocks.RAINBOWSAPLING.getBlockInstance())
			x++;
		id = world.getBlock(x, y, z+1);
		if (id == ChromaBlocks.RAINBOWSAPLING.getBlockInstance())
			z++;
		return world.getBlockLightValue(x, y, z) >= 9 && RainbowTreeGenerator.getInstance().checkRainbowTreeSpace(world, x, y, z);
	}

	@Override
	public void func_149879_c(World world, int x, int y, int z, Random par5Random)
	{
		if (r.nextInt(20) > 0)
			return;
		this.func_149878_d(world, x, y, z, par5Random);
	}

	@Override
	public int damageDropped(int par1)
	{
		return par1;
	}

}
