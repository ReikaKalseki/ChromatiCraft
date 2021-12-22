/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Worldgen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Magic.Potions.PotionVoidGaze;
import Reika.ChromatiCraft.Magic.Potions.PotionVoidGaze.VoidGazeLevels;
import Reika.ChromatiCraft.Magic.Progression.ProgressStage;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;
import Reika.ChromatiCraft.Render.Particle.EntityCCBlurFX;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class BlockEtherealLuma extends BlockFluidClassic {

	private final IIcon[] icons = new IIcon[16];
	private final IIcon[] dimensionIcons = new IIcon[16];

	public BlockEtherealLuma(Fluid fluid, Material material) {
		super(fluid, material);
		this.setQuantaPerBlock(16);
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
		Block b = world.getBlock(x, y-1, z);
		int val = super.getQuantaValue(world, x, y, z);
		return ReikaBlockHelper.isLiquid(b) ? Math.min(val, 2) : val;
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		for (int i = 0; i < 16; i++) {
			icons[i] = ico.registerIcon("chromaticraft:fluid/aether/aether_still_"+i);
			dimensionIcons[i] = ico.registerIcon("chromaticraft:fluid/aether/aether_still_dim_"+i);
		}
		blockIcon = ico.registerIcon("chromaticraft:fluid/aether/aether_full");
	}

	@Override
	public IIcon getIcon(IBlockAccess iba, int x, int y, int z, int s) {
		if (iba.getBlockMetadata(x, y, z) != 0 || this.getFlowVector(iba, x, y, z).lengthVector() > 0)
			return ChromatiCraft.luma.getFlowingIcon();//blockIcon;
		IIcon[] arr = icons;
		int dx = (x%4+4)%4;
		int dz = (z%4+4)%4;
		if (Minecraft.getMinecraft().theWorld != null && Minecraft.getMinecraft().theWorld.provider.dimensionId == ExtraChromaIDs.DIMID.getValue()) {
			arr = dimensionIcons;
			int sc = dx;
			dx = dz;
			dz = sc;
		}
		int idx = dz*4+dx;
		return arr[idx];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random r) {
		if (r.nextInt(2*(1+Minecraft.getMinecraft().gameSettings.particleSetting)) == 0) {
			double px = x+r.nextDouble();
			double py = y+r.nextDouble();
			double pz = z+r.nextDouble();
			int l = 5+r.nextInt(40);
			float s = 0.75F;
			EntityFX fx = new EntityCCBlurFX(world, px, py, pz).setIcon(ChromaIcons.SPARKLEPARTICLE).setBasicBlend().setLife(l).setAlphaFading().setScale(s);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);

			if (r.nextInt(4) == 0) {
				float g = -(float)ReikaRandomHelper.getRandomBetween(0.03125, 0.125);
				s = (float)ReikaRandomHelper.getRandomBetween(1D, 2D);
				l = 20+r.nextInt(30);
				fx = new EntityCCBlurFX(world, px, py, pz).setIcon(ChromaIcons.FADE_GENTLE).setLife(l).setScale(s).setGravity(g).setRapidExpand();
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
		}
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity e) {
		if (e instanceof EntityLivingBase) {
			EntityLivingBase elb = (EntityLivingBase)e;
			if (elb.getAir() < 300)
				elb.setAir(elb.getAir()+1);
			if (elb instanceof EntityPlayer && world.getBlockMetadata(x, y, z) == 0) {
				EntityPlayer ep = (EntityPlayer)elb;
				ProgressStage.LUMA.stepPlayerTo(ep);
				addPotions(ep);
			}
		}
		super.onEntityCollidedWithBlock(world, x, y, z, e);
	}

	public static void addPotions(EntityPlayer ep) {
		PotionEffect cur = ep.getActivePotionEffect(ChromatiCraft.voidGaze);
		if (cur == null || cur.getDuration() < 5) {
			int lvl = PotionVoidGaze.VoidGazeLevels.getAppliedLevel(ep);
			if (lvl >= 0) {
				ep.addPotionEffect(new PotionEffect(ChromatiCraft.voidGaze.id, 100, lvl));
				if (cur == null)
					VoidGazeLevels.list[lvl].onStart(ep);
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getMixedBrightnessForBlock(IBlockAccess iba, int x, int y, int z) {
		return 240;
	}

}
