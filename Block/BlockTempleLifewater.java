/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
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
import net.minecraft.entity.Entity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

import Reika.DragonAPI.Libraries.Java.ReikaArrayHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class BlockTempleLifewater extends BlockFluidClassic {

	public IIcon[] theIcon = new IIcon[2];

	public BlockTempleLifewater(Fluid fluid, Material material) {
		super(fluid, material);
		this.setQuantaPerBlock(11);
	}

	@Override
	protected boolean canFlowInto(IBlockAccess world, int x, int y, int z) {
		Block b = world.getBlock(x, y, z);
		return b == this || b.isAir(world, x, y, z);
	}

	@Override
	public boolean canDisplace(IBlockAccess world, int x, int y, int z) {
		return this.canFlowInto(world, x, y, z);
	}

	@Override
	public boolean displaceIfPossible(World world, int x, int y, int z) {
		return this.canDisplace(world, x, y, z) ? super.displaceIfPossible(world, x, y, z) : false;
	}

	@Override
	protected boolean[] getOptimalFlowDirections(World world, int x, int y, int z) {
		return ReikaArrayHelper.getTrueArray(4);
	}

	@Override
	protected void flowIntoBlock(World world, int x, int y, int z, int meta) {
		//int m2 = meta;
		//if (ReikaBlockHelper.isLiquid(world.getBlock(x, y-1, z)))
		//	m2++;
		//Block b = world.getBlock(x, y-1, z);
		//this.setQuantaPerBlock(ReikaBlockHelper.isLiquid(b) ? 3 : 16);
		//ReikaJavaLibrary.pConsole(b+ " > "+quantaPerBlock);
		super.flowIntoBlock(world, x, y, z, meta);
	}

	@Override
	public int getQuantaValue(IBlockAccess world, int x, int y, int z) {
		return super.getQuantaValue(world, x, y, z);
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		theIcon = new IIcon[]{ico.registerIcon("chromaticraft:fluid/lifewater"), ico.registerIcon("chromaticraft:fluid/lifewater_flow")};
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random r) {

	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity e) {
		super.onEntityCollidedWithBlock(world, x, y, z, e);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getMixedBrightnessForBlock(IBlockAccess iba, int x, int y, int z) {
		return 240;
	}

	@Override
	public boolean isReplaceable(IBlockAccess world, int x, int y, int z) {
		return true;
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return s <= 1 ? theIcon[0] : theIcon[1];
	}

}
