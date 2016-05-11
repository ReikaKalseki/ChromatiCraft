/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockLiquidEnder extends BlockFluidClassic {

	public IIcon[] theIcon = new IIcon[2];

	public BlockLiquidEnder(Fluid f, Material mat) {
		//super(par1, EnderForest.ender, par2Material);
		super(f, mat);

		this.setHardness(100F);
		this.setLightOpacity(0);
		this.setResistance(500);
		renderPass = 0;
		this.setCreativeTab(null);
	}

	@Override
	public int getRenderType() {
		return 4;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ico) {
		theIcon = new IIcon[]{ico.registerIcon("chromaticraft:fluid/ender"), ico.registerIcon("chromaticraft:fluid/flowingender")};
	}

	@Override
	public Fluid getFluid() {
		return FluidRegistry.getFluid("ender");
	}
	/*
	@Override
	public FluidStack drain(World world, int x, int y, int z, boolean doDrain) {
		world.setBlockToAir(x, y, z);
		return new FluidStack(FluidRegistry.getFluid("ender"), 1000);
	}

	@Override
	public boolean canDrain(World world, int x, int y, int z) {
		return true;
	}
	 */
	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity e) {
		if (!ChromaOptions.ENDEREFFECT.getState())
			return;
		double v = 1;
		e.motionX = ReikaRandomHelper.getRandomPlusMinus(0, v);
		e.motionZ = ReikaRandomHelper.getRandomPlusMinus(0, v);
		e.motionY += 0.2;
		e.fallDistance = 0;
		//e.motionZ = -e.motionZ;
		if (e instanceof EntityLivingBase)
			e.playSound("mob.endermen.portal", 0.5F, 1.0F);
	}

	@Override
	public void velocityToAddToEntity(World world, int x, int y, int z, Entity entity, Vec3 vec)
	{
		if (densityDir > 0) return;
		Vec3 vec_flow = this.getFlowVector(world, x, y, z);
		vec.xCoord += vec_flow.xCoord * (quantaPerBlock * 4);
		vec.yCoord += vec_flow.yCoord * (quantaPerBlock * 4);
		vec.zCoord += vec_flow.zCoord * (quantaPerBlock * 4);
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
