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

import java.util.HashSet;
import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import Reika.GeoStrata.GeoStrata;
import Reika.GeoStrata.Registry.RockShapes;
import Reika.GeoStrata.Registry.RockTypes;

public class BlockPath extends Block {

	public static enum PathType {
		BASIC("Basic", Blocks.cobblestone),
		SMOOTH("Smooth", Blocks.stone),
		BRICK("Brick", Blocks.brick_block),
		GLOW("Glowing", Blocks.glowstone),
		FIRE("Fiery", Blocks.obsidian),
		EMERALD("Rich", Blocks.emerald_block),
		RAINBOW("Opalized", Blocks.lapis_block);

		public final String name;
		private final Block addition;

		public static final PathType[] list = values();

		private PathType(String s, Block b) {
			name = s;
			addition = b;
		}

		public ItemStack getBlock() {
			if (this == RAINBOW && ModList.GEOSTRATA.isLoaded())
				return getOpal();
			return new ItemStack(addition);
		}

		@ModDependent(ModList.GEOSTRATA)
		private static ItemStack getOpal() {
			return RockTypes.OPAL.getItem(RockShapes.SMOOTH);
		}
	}

	private IIcon[][] icons = new IIcon[PathType.list.length][3];

	public BlockPath(Material mat) {
		super(mat);
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
		return icons[meta][2];
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		for (int i = 0; i < icons.length; i++) {
			icons[i][0] = ico.registerIcon("chromaticraft:path/"+PathType.list[i].name.toLowerCase(Locale.ENGLISH)+"_back");
			icons[i][1] = ico.registerIcon("chromaticraft:path/"+PathType.list[i].name.toLowerCase(Locale.ENGLISH)+"_front");
			icons[i][2] = ico.registerIcon("chromaticraft:path/path_"+i);
		}
	}

	@Override
	public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
		return world.getBlockMetadata(x, y, z) != PathType.RAINBOW.ordinal() ? 0xffffff : ModList.GEOSTRATA.isLoaded() ? this.getOpalColor(world, x, y, z) : this.getBasicRainbowColor(world, x, y, z);
	}

	private int getBasicRainbowColor(IBlockAccess world, int x, int y, int z) {
		return CrystalElement.getBlendedColor(x, 10);
	}

	@ModDependent(ModList.GEOSTRATA)
	private int getOpalColor(IBlockAccess world, int x, int y, int z) {
		return GeoStrata.getOpalPositionColor(world, x, y, z);
	}

	@Override
	public void onEntityWalking(World world, int x, int y, int z, Entity e) {
		slipperiness = 0.95F;
		if (e instanceof EntityLivingBase) {
			EntityLivingBase el = (EntityLivingBase)e;
			float max = 0.6F;
			int meta = world.getBlockMetadata(x, y, z);
			if (meta == PathType.EMERALD.ordinal())
				max = 0.8F;
			if (meta == PathType.BASIC.ordinal())
				max = 0.3F;

			HashSet<ForgeDirection> set = this.getPathDirectionsAt(world, x, y, z, meta);
			//ReikaJavaLibrary.pConsole(set);

			double curV = ReikaMathLibrary.py3d(el.motionX, 0, el.motionZ);
			double v = Math.min(curV+0.125, max)*2;
			double[] xz = ReikaPhysicsHelper.polarToCartesian(1, 0, el.rotationYawHead+90);
			if (xz[0] < 0 && !set.contains(ForgeDirection.WEST))
				xz[0] = 0;
			if (xz[0] > 0 && !set.contains(ForgeDirection.EAST))
				xz[0] = 0;
			if (xz[1] < 0 && !set.contains(ForgeDirection.NORTH))
				xz[1] = 0;
			if (xz[1] > 0 && !set.contains(ForgeDirection.SOUTH))
				xz[1] = 0;
			el.motionX = xz[0]*v;
			el.motionZ = xz[2]*v;
			el.velocityChanged = true;

			el.addPotionEffect(new PotionEffect(Potion.jump.id, 1, 2));
			if (meta == PathType.FIRE.ordinal())
				el.extinguish();
		}
	}

	private HashSet<ForgeDirection> getPathDirectionsAt(World world, int x, int y, int z, int meta) {
		HashSet<ForgeDirection> set = new HashSet();
		for (int d = 0; d < 12; d++) {
			for (int i = 2; i < 6; i++) {
				ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
				int dx = x+d*dir.offsetX;
				int dz = z+d*dir.offsetZ;
				if (world.getBlock(dx, y, dz) == this && world.getBlockMetadata(dx, y, dz) == meta) {
					set.add(dir);
				}
			}
			if (set.size() == 4) {
				if (world.getBlock(x-d, y, z-d) == this && world.getBlockMetadata(x-d, y, z-d) == meta) {
					if (world.getBlock(x+d, y, z-d) == this && world.getBlockMetadata(x+d, y, z-d) == meta) {
						if (world.getBlock(x-d, y, z+d) == this && world.getBlockMetadata(x-d, y, z+d) == meta) {
							if (world.getBlock(x+d, y, z+d) == this && world.getBlockMetadata(x+d, y, z+d) == meta) {
								set.clear();
							}
						}
					}
				}
			}
			if (!set.isEmpty()) {
				break;
			}
		}
		return set;
	}

}
