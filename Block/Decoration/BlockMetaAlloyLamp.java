/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Decoration;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Interfaces.SidedBlock;
import Reika.ChromatiCraft.ModInterface.Bees.CrystalBees;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.ModInteract.Bees.BeeSpecies;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import forestry.api.apiculture.EnumBeeType;


public class BlockMetaAlloyLamp extends Block implements SidedBlock {

	public static final int COLOR1 = 0xA1E56A;
	public static final int COLOR2 = 0x75DAFF;

	public static IIcon leaf1;
	public static IIcon leaf2;
	public static IIcon podEnd;
	public static IIcon podSide;

	public BlockMetaAlloyLamp(Material mat) {
		super(mat);

		this.setCreativeTab(ChromatiCraft.tabChromaDeco);
		this.setHardness(0.25F);
		this.setResistance(0);

		stepSound = soundTypeGrass;
		this.setTickRandomly(true);
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		return this.hasPod(world, x, y, z) ? ModList.COLORLIGHT.isLoaded() ? ReikaColorAPI.getPackedIntForColoredLight(0x000000, 15) : 15 : 0;
	}

	public boolean hasPod(IBlockAccess world, int x, int y, int z) {
		return world.getBlockMetadata(x, y, z) < 8;
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		if (!this.hasPod(world, x, y, z) && rand.nextInt(4) == 0 && !world.isRemote) {
			this.setPod(world, x, y, z, true);
		}
	}

	private void setPod(World world, int x, int y, int z, boolean pod) {
		world.setBlockMetadataWithNotify(x, y, z, (world.getBlockMetadata(x, y, z) & 7) | (pod ? 0 : 8), 3);
		world.updateLightByType(EnumSkyBlock.Block, x, y, z);
		world.markBlockForUpdate(x, y, z);
		ReikaSoundHelper.playBreakSound(world, x, y, z, this);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block b) {
		ForgeDirection dir = this.getSide(world, x, y, z);
		if (!this.canPlaceOn(world, x-dir.offsetX, y-dir.offsetY, z-dir.offsetZ, dir.ordinal())) {
			ReikaSoundHelper.playBreakSound(world, x, y, z, this);
			ReikaItemHelper.dropItem(world, x+0.5, y+0.5, z+0.5, new ItemStack(this));
			world.setBlockToAir(x, y, z);
		}
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		this.setPod(world, x, y, z, false);
	}

	@Override
	public void onBlockClicked(World world, int x, int y, int z, EntityPlayer ep) {
		if (this.hasPod(world, x, y, z)) {
			this.setPod(world, x, y, z, false);
			ReikaItemHelper.dropItem(world, x+0.5, y+0.5, z+0.5, new ItemStack(this));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		if (this.hasPod(world, x, y, z) && rand.nextBoolean()) {
			double px = ReikaRandomHelper.getRandomPlusMinus(x+0.5, 0.5);
			double py = ReikaRandomHelper.getRandomPlusMinus(y+0.5, 0.5);
			double pz = ReikaRandomHelper.getRandomPlusMinus(z+0.5, 0.5);
			int l = ReikaRandomHelper.getRandomBetween(10, 40);
			double s = 5+rand.nextDouble()*5;
			int c = ReikaColorAPI.getColorWithBrightnessMultiplier(ReikaColorAPI.mixColors(0x006020, 0x000060, rand.nextFloat()), 0.5F+rand.nextFloat()*0.5F);
			EntityFX fx = new EntityBlurFX(world, px, py, pz).setColor(c).setLife(l).setScale((float)s).setAlphaFading().setIcon(ChromaIcons.FADE_CLOUD);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		leaf1 = ico.registerIcon("chromaticraft:metaleaf");
		leaf2 = ico.registerIcon("chromaticraft:metaleaf_2");
		podSide = ico.registerIcon("chromaticraft:metaalloy_side");
		podEnd = ico.registerIcon("chromaticraft:metaalloy");
	}

	@Override
	public int getRenderType() {
		return ChromatiCraft.proxy.metaAlloyRender;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	public void setSide(World world, int x, int y, int z, int side) {
		world.setBlockMetadataWithNotify(x, y, z, (world.getBlockMetadata(x, y, z) & 8) | side, 3);
		world.markBlockForUpdate(x, y, z);
	}

	public ForgeDirection getSide(IBlockAccess world, int x, int y, int z) {
		return ForgeDirection.VALID_DIRECTIONS[world.getBlockMetadata(x, y, z) & 7];
	}

	@Override
	public boolean canPlaceOn(World world, int x, int y, int z, int side) {
		Block b = world.getBlock(x, y, z);
		return b.isSideSolid(world, x, y, z, ForgeDirection.VALID_DIRECTIONS[side]) && (b.getMaterial() == Material.rock || b.getMaterial() == Material.ground || b.getMaterial() == Material.plants || b.getMaterial() == Material.grass);
	}

	@Override
	public final void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		float maxY = this.hasPod(world, x, y, z) ? 1 : 0.5F;
		this.setBlockBounds(0, 0, 0, 1, maxY, 1);
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
		AxisAlignedBB box = this.getCollisionBoundingBoxFromPool(world, x, y, z);
		return box;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)  {
		float maxY = this.hasPod(world, x, y, z) ? 1 : 0.5F;
		AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x, y, z, x+1, y+maxY, z+1);
		return box;
	}

	@ModDependent(ModList.FORESTRY)
	public static void doBeeDrops(World world, int x, int y, int z) {
		BeeSpecies s = CrystalBees.getTowerBee();
		if (world.rand.nextInt(5) > 0) {
			ItemStack is = s.getBeeItem(world, EnumBeeType.DRONE);
			ReikaItemHelper.dropItem(world, x+world.rand.nextDouble(), y+world.rand.nextDouble(), z+world.rand.nextDouble(), is);
		}
		if (world.rand.nextInt(4) > 0) {
			ItemStack is = s.getBeeItem(world, EnumBeeType.LARVAE);
			ReikaItemHelper.dropItem(world, x+world.rand.nextDouble(), y+world.rand.nextDouble(), z+world.rand.nextDouble(), is);
		}
		if (world.rand.nextInt(2) == 0) {
			ItemStack is = s.getBeeItem(world, EnumBeeType.PRINCESS);
			ReikaItemHelper.dropItem(world, x+world.rand.nextDouble(), y+world.rand.nextDouble(), z+world.rand.nextDouble(), is);
		}
		if (world.rand.nextInt(5) == 0) {
			ItemStack is = s.getBeeItem(world, EnumBeeType.QUEEN);
			ReikaItemHelper.dropItem(world, x+world.rand.nextDouble(), y+world.rand.nextDouble(), z+world.rand.nextDouble(), is);
		}
	}

}
