/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Interfaces.SidedBlock;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public abstract class BlockAttachableMini extends Block implements SidedBlock {

	public BlockAttachableMini(Material mat) {
		super(mat);
		this.setHardness(0);
		this.setResistance(0);
		this.setCreativeTab(ChromatiCraft.tabChroma);
		stepSound = new SoundType("stone", 1.0F, 0.5F);
		this.setBounds(ForgeDirection.EAST);
	}

	@Override
	public final int getLightValue(IBlockAccess iba, int x, int y, int z) {
		return ModList.COLORLIGHT.isLoaded() ? ReikaColorAPI.getPackedIntForColoredLight(this.getColor(iba, x, y, z), 7) : 7;
	}

	@Override
	public final int damageDropped(int meta) {
		return meta & (~7);
	}

	@Override
	public final Item getItemDropped(int dmg, Random r, int fortune) {
		return super.getItemDropped(dmg, r, fortune);
	}

	public final boolean canPlaceOn(World world, int x, int y, int z, int side) {
		Block b = world.getBlock(x, y, z);
		ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[side];
		if (b.isSideSolid(world, x, y, z, dir))
			return true;
		if (b.getMaterial().isSolid()) {
			b.setBlockBoundsBasedOnState(world, x, y, z);
			switch(dir) {
				case DOWN:
					if (b.getBlockBoundsMinY() == 0)
						return true;
					break;
				case UP:
					if (b.getBlockBoundsMaxY() == 1)
						return true;
					break;
				case EAST:
					if (b.getBlockBoundsMinX() == 0)
						return true;
					break;
				case WEST:
					if (b.getBlockBoundsMaxX() == 1)
						return true;
					break;
				case NORTH:
					if (b.getBlockBoundsMinZ() == 0)
						return true;
					break;
				case SOUTH:
					if (b.getBlockBoundsMaxZ() == 1)
						return true;
					break;
				default:
					break;
			}
		}
		return false;
	}

	public final void setSide(World world, int x, int y, int z, int side) {
		world.setBlockMetadataWithNotify(x, y, z, (world.getBlockMetadata(x, y, z) & (~7)) | side, 3);
	}

	public final ForgeDirection getSide(IBlockAccess world, int x, int y, int z) {
		return ForgeDirection.VALID_DIRECTIONS[world.getBlockMetadata(x, y, z) & 7];
	}

	@Override
	public final AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return null;
	}

	@Override
	public final boolean canHarvestBlock(EntityPlayer player, int meta) {
		return true;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block b) {
		int meta = world.getBlockMetadata(x, y, z) & 7;
		ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[meta];
		if (!this.canPlaceOn(world, x-dir.offsetX, y-dir.offsetY, z-dir.offsetZ, meta)) {
			ReikaSoundHelper.playBreakSound(world, x, y, z, this);
			this.drop(world, x, y, z);
		}
	}

	public final void drop(World world, int x, int y, int z) {
		ItemStack is = new ItemStack(this, 1, this.damageDropped(world.getBlockMetadata(x, y, z)));
		ReikaItemHelper.dropItem(world, x+0.5, y+0.5, z+0.5, is);
		world.setBlock(x, y, z, Blocks.air);
	}

	@Override
	public final void setBlockBoundsForItemRender() {
		this.setBounds(ForgeDirection.EAST);
	}

	@Override
	public final void setBlockBoundsBasedOnState(IBlockAccess iba, int x, int y, int z) {
		this.setBounds(this.getSide(iba, x, y, z));
	}

	private void setBounds(ForgeDirection dir) {
		float xmin = 0;
		float ymin = 0;
		float zmin = 0;
		float xmax = 1;
		float ymax = 1;
		float zmax = 1;

		float h = 0.25F;
		float w = 0.25F;

		switch(dir) {
			case WEST:
				zmin = 0.5F-w;
				zmax = 0.5F+w;
				ymin = 0.5F-w;
				ymax = 0.5F+w;
				xmin = 1-h;
				break;
			case EAST:
				zmin = 0.5F-w;
				zmax = 0.5F+w;
				ymin = 0.5F-w;
				ymax = 0.5F+w;
				xmax = h;
				break;
			case NORTH:
				xmin = 0.5F-w;
				xmax = 0.5F+w;
				ymin = 0.5F-w;
				ymax = 0.5F+w;
				zmin = 1-h;
				break;
			case SOUTH:
				xmin = 0.5F-w;
				xmax = 0.5F+w;
				ymin = 0.5F-w;
				ymax = 0.5F+w;
				zmax = h;
				break;
			case UP:
				xmin = 0.5F-w;
				xmax = 0.5F+w;
				zmin = 0.5F-w;
				zmax = 0.5F+w;
				ymax = h;
				break;
			case DOWN:
				xmin = 0.5F-w;
				xmax = 0.5F+w;
				zmin = 0.5F-w;
				zmax = 0.5F+w;
				ymin = 1-h;
				break;
			default:
				break;
		}
		this.setBlockBounds(xmin, ymin, zmin, xmax, ymax, zmax);
	}

	@Override
	public final boolean isOpaqueCube() {
		return false;
	}

	@Override
	public final boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public final int getRenderType() {
		return 0;//ChromatiCraft.proxy.relayRender;
	}

	@Override
	public final int getRenderBlockPass() {
		return 0;//1;
	}

	@Override
	public final boolean canRenderInPass(int pass) {
		return super.canRenderInPass(pass);//true;
	}

	@Override
	public final void onBlockAdded(World world, int x, int y, int z) {

	}

	@Override
	public final void breakBlock(World world, int x, int y, int z, Block b, int meta) {
		this.onBlockBreak(world, x, y, z);
		super.breakBlock(world, x, y, z, b, meta);
		this.updateNeighbors(world, x, y, z, meta);
	}

	protected void onBlockBreak(World world, int x, int y, int z) {

	}

	protected final void updateNeighbors(World world, int x, int y, int z, int meta) {
		world.markBlockForUpdate(x, y, z);
		ReikaWorldHelper.causeAdjacentUpdates(world, x, y, z);
		ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[meta & 7].getOpposite();
		ReikaWorldHelper.causeAdjacentUpdates(world, x+dir.offsetX, y+dir.offsetY, z+dir.offsetZ);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final void randomDisplayTick(World world, int x, int y, int z, Random r) {
		ForgeDirection dir = this.getSide(world, x, y, z);
		double dx = x+0.5-dir.offsetX*0.3125;
		double dy = y+0.5-dir.offsetY*0.3125;
		double dz = z+0.5-dir.offsetZ*0.3125;

		double w = 0.1875;
		double h = 0.125;

		if (Math.abs(dir.offsetX) == 1) {
			dx = ReikaRandomHelper.getRandomPlusMinus(dx, h);
			dy = ReikaRandomHelper.getRandomPlusMinus(dy, w);
			dz = ReikaRandomHelper.getRandomPlusMinus(dz, w);
		}
		else if (Math.abs(dir.offsetY) == 1) {
			dx = ReikaRandomHelper.getRandomPlusMinus(dx, w);
			dy = ReikaRandomHelper.getRandomPlusMinus(dy, h);
			dz = ReikaRandomHelper.getRandomPlusMinus(dz, w);
		}
		else if (Math.abs(dir.offsetZ) == 1) {
			dx = ReikaRandomHelper.getRandomPlusMinus(dx, w);
			dy = ReikaRandomHelper.getRandomPlusMinus(dy, w);
			dz = ReikaRandomHelper.getRandomPlusMinus(dz, h);
		}

		this.createFX(world, x, y, z, dx, dy, dz, r);
	}

	@SideOnly(Side.CLIENT)
	protected abstract void createFX(World world, int x, int y, int z, double dx, double dy, double dz, Random r);

	public abstract int getColor(IBlockAccess iba, int x, int y, int z);

}
