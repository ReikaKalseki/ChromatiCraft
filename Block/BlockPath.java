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

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;

public class BlockPath extends Block {

	public static enum PathType {
		BASIC("Basic", Blocks.cobblestone),
		SMOOTH("Smooth", Blocks.stone),
		BRICK("Brick", Blocks.brick_block),
		GLOW("Glowing", Blocks.glowstone),
		FIRE("Fiery", Blocks.obsidian),
		EMERALD("Rich", Blocks.emerald_block);

		public final String name;
		public final Block addition;

		public static final PathType[] list = values();

		private PathType(String s, Block b) {
			name = s;
			addition = b;
		}
	}

	private IIcon[] icons = new IIcon[PathType.list.length];

	public BlockPath(Material par2Material) {
		super(par2Material);
		this.setHardness(2);
		this.setResistance(10);
		this.setCreativeTab(ChromatiCraft.tabChroma);
		stepSound = soundTypeStone;
		slipperiness = 1.5F;
	}

	@Override
	public int getLightValue(IBlockAccess iba, int x, int y, int z) {
		return iba.getBlockMetadata(x, y, z) == PathType.GLOW.ordinal() ? 12 : 0;
	}

	@Override
	public float getExplosionResistance(Entity e, World world, int x, int y, int z, double eX, double eY, double eZ) {
		return world.getBlockMetadata(x, y, z) == PathType.FIRE.ordinal() ? 6000 : super.getExplosionResistance(e, world, x, y, z, eX, eY, eZ);
	}

	@Override
	public int damageDropped(int meta) {
		return meta;
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return icons[meta];
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		for (int i = 0; i < icons.length; i++) {
			icons[i] = ico.registerIcon("chromaticraft:basic/path_"+i);
		}
	}

	@Override
	public void onEntityWalking(World world, int x, int y, int z, Entity e) {
		if (e instanceof EntityLivingBase) {
			EntityLivingBase el = (EntityLivingBase)e;
			double max = 2.5;
			int meta = world.getBlockMetadata(x, y, z);
			if (meta != PathType.EMERALD.ordinal())
				max = 1.75;
			if (meta == PathType.BASIC.ordinal())
				max = 1.25;
			el.motionX = MathHelper.clamp_double(el.motionX*Math.abs(Math.sin(Math.toRadians(el.rotationYawHead))), -max, max);
			el.motionZ = MathHelper.clamp_double(el.motionZ*Math.abs(Math.cos(Math.toRadians(el.rotationYawHead))), -max, max);
			for (int d = 1; d <= 8; d++) {
				int nx = x+d*(int)Math.signum(e.motionX);
				int nz = z+d*(int)Math.signum(e.motionZ);
				double fac = d > 1 ? 0.8 : 0;
				if (world.getBlock(nx, y, z) != this)
					el.motionX *= fac;
				if (world.getBlock(x, y, nz) != this)
					el.motionZ *= fac;
			}
			el.addPotionEffect(new PotionEffect(Potion.jump.id, 1, 2));
			if (meta == PathType.FIRE.ordinal())
				el.extinguish();
		}
	}

}
