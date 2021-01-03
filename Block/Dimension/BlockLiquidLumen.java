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

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
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
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Render.Particle.EntityCCBlurFX;
import Reika.DragonAPI.Instantiable.Effects.EntityBlurFX;
import Reika.DragonAPI.Instantiable.Math.Noise.NoiseGeneratorBase;
import Reika.DragonAPI.Instantiable.Math.Noise.SimplexNoiseGenerator;
import Reika.DragonAPI.Interfaces.ColorController;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockLiquidLumen extends BlockFluidClassic {

	private static final NoiseGeneratorBase hueNoise = new SimplexNoiseGenerator(System.currentTimeMillis()).setFrequency(1/32D);
	private static final NoiseGeneratorBase saturationNoise = new SimplexNoiseGenerator(-System.currentTimeMillis()).setFrequency(1/20D);

	private static final ColorController particleColor = new ColorController() {

		@Override
		public void update(Entity e) {

		}

		@Override
		public int getColor(Entity e) {
			return BlockLiquidLumen.getColor(e.posX, e.posZ);
		}

	};

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

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random r) {
		if (r.nextInt(15) == 0) {
			double px = x+r.nextDouble();
			double pz = z+r.nextDouble();
			float g = (float)ReikaRandomHelper.getRandomBetween(0.03125, 0.25);
			float s = (float)ReikaRandomHelper.getRandomBetween(0.75, 2);
			int l = 80;//ReikaRandomHelper.getRandomBetween(10, 80);
			double vx = ReikaRandomHelper.getRandomPlusMinus(0, 0.125);
			double vz = ReikaRandomHelper.getRandomPlusMinus(0, 0.125);
			double vy = ReikaRandomHelper.getRandomBetween(0, 0.25);
			EntityCCBlurFX fx = new EntityCCBlurFX(world, px, y+0.9375-s/16, pz, vx, vy, vz);
			fx.setColor(this.getColor(px, pz)).setGravity(g).setScale(s).setLife(l);
			fx.setIcon(ChromaIcons.FADE_GENTLE.getIcon()).setAlphaFading().setColorController(particleColor);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
		if (r.nextInt(150) == 0) {
			double px = x+r.nextDouble();
			double pz = z+r.nextDouble();
			EntityBlurFX fx = new EntityCCBlurFX(world, px, y+0.9375, pz, 0, 0.75, 0).setIcon(ChromaIcons.SPARKLEPARTICLE).setColor(this.getColor(px, pz)).setScale(5F).setLife(120).setBasicBlend();
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
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
		return this.getColor(x, z);
	}

	@SideOnly(Side.CLIENT)
	public static int getColor(double x, double z) {
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
