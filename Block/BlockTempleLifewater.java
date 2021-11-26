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
import net.minecraftforge.fluids.BlockFluidFinite;
import net.minecraftforge.fluids.Fluid;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Deprecated
public class BlockTempleLifewater extends BlockFluidFinite {

	private final IIcon[][] theIcon = new IIcon[2][2];

	public BlockTempleLifewater(Fluid fluid, Material material) {
		super(fluid, material);
		//this.setQuantaPerBlock(11);
		this.setQuantaPerBlock(16);
	}

	//@Override
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
	/*
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
	 */
	@Override
	public int getQuantaValue(IBlockAccess world, int x, int y, int z) {
		return super.getQuantaValue(world, x, y, z);
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		theIcon[0][0] = ico.registerIcon("chromaticraft:fluid/lifewater_still");
		theIcon[0][1] = ico.registerIcon("chromaticraft:fluid/lifewater_flow");
		theIcon[1][0] = ico.registerIcon("chromaticraft:fluid/lifewater_still_trans");
		theIcon[1][1] = ico.registerIcon("chromaticraft:fluid/lifewater_flow_trans");
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
	public IIcon getIcon(IBlockAccess iba, int x, int y, int z, int s) {
		boolean flow = s > 1;
		Material mat = iba.getBlock(x, y-1, z).getMaterial();
		boolean trans = true;//mat == Material.ground || mat == Material.grass || mat == Material.clay || mat == Material.sand;
		return theIcon[trans ? 1 : 0][flow ? 1 : 0];
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return s <= 1 ? theIcon[0][0] : theIcon[0][1];
	}

}
