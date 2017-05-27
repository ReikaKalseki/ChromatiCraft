/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Dimension;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.Instantiable.Math.SimplexNoiseGenerator;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockLiquidLumen extends BlockFluidClassic {

	private static final SimplexNoiseGenerator hueNoise = new SimplexNoiseGenerator(System.currentTimeMillis()).setFrequency(1/32D);
	private static final SimplexNoiseGenerator saturationNoise = new SimplexNoiseGenerator(-System.currentTimeMillis()).setFrequency(1/20D);

	public IIcon[] theIcon = new IIcon[2];

	public BlockLiquidLumen(Fluid f, Material mat) {
		super(f, mat);

		this.setHardness(100F);
		this.setLightLevel(1);
		this.setLightOpacity(0);
		this.setResistance(6000);
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
		theIcon = new IIcon[]{ico.registerIcon("chromaticraft:fluid/lumen"), ico.registerIcon("chromaticraft:fluid/flowinglumen")};
	}

	@Override
	public Fluid getFluid() {
		return FluidRegistry.getFluid("lumen");
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
		e.extinguish();
		if (e instanceof EntityLivingBase) {
			EntityLivingBase elb = (EntityLivingBase)e;
			elb.addPotionEffect(new PotionEffect(ChromatiCraft.lumenRegen.id, 300));
		}
	}

	@Override
	public int colorMultiplier(IBlockAccess iba, int x, int y, int z) {
		int hue = (int)ReikaMathLibrary.normalizeToBounds(hueNoise.getValue(x, z), 195, 310);
		float sat = Math.min(0.75F, (float)ReikaMathLibrary.normalizeToBounds(saturationNoise.getValue(x, z), 0.25, 1.5));
		return ReikaColorAPI.getModifiedSat(ReikaColorAPI.getModifiedHue(0xff0000, hue), sat);
	}

	@Override
	public void velocityToAddToEntity(World world, int x, int y, int z, Entity entity, Vec3 vec) {
		if (densityDir > 0)
			return;
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
