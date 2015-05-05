/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Crystal;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.CrystalTypeBlock;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Render.ISBRH.CrystalGlowRenderer;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityCenterBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityLaserFX;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockCrystalGlow extends CrystalTypeBlock {

	public BlockCrystalGlow(Material mat) {
		super(mat);
		this.setHardness(0);
		this.setResistance(0);
		this.setCreativeTab(ChromatiCraft.tabChromaDeco);
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		blockIcon = ico.registerIcon("chromaticraft:basic/glow");
	}

	@Override
	public int getBrightness(IBlockAccess iba, int x, int y, int z) {
		return 15;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		double rx = x+rand.nextDouble();
		double ry = y+rand.nextDouble();
		double rz = z+rand.nextDouble();
		float g = (float)ReikaRandomHelper.getRandomPlusMinus(0.025, 0.0125);
		TileEntityCrystalGlow te = (TileEntityCrystalGlow)world.getTileEntity(x, y, z);
		if (te.getDirection() != ForgeDirection.DOWN)
			g = -g;
		boolean in = rand.nextBoolean();
		double v = 0.0125;
		double vx = in ? v*(0.5-(rx-x)) : (rx-x)*v;//ReikaRandomHelper.getRandomPlusMinus(0, 0.03125);
		double vz = in ? v*(0.5-(rz-z)) : (rz-z)*v;//ReikaRandomHelper.getRandomPlusMinus(0, 0.03125);
		EntityFX fx = null;
		int type = rand.nextInt(3);
		switch(type) {
		case 0:
			fx = new EntityBlurFX(this.getCrystalElement(world, x, y, z), world, rx, ry, rz, vx, 0, vz).setScale(1+rand.nextFloat()).setGravity(g);
			break;
		case 1:
			fx = new EntityCenterBlurFX(this.getCrystalElement(world, x, y, z), world, rx, ry, rz, vx, 0, vz).setScale(1+rand.nextFloat()).setGravity(g);
			break;
		case 2:
			fx = new EntityLaserFX(this.getCrystalElement(world, x, y, z), world, rx, ry, rz, vx, 0, vz).setScale(1+rand.nextFloat()).setGravity(g);
			break;
		}
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}

	@Override
	public final int getRenderType() {
		return ChromatiCraft.proxy.glowRender;
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
	public final int getRenderBlockPass() {
		return 1;
	}

	@Override
	public boolean canRenderInPass(int pass)
	{
		CrystalGlowRenderer.renderPass = pass;
		return pass <= 1;
	}

	public boolean canPlaceOn(World world, int x, int y, int z, int side) {
		return this.canPlaceOn(world, x, y, z, ForgeDirection.VALID_DIRECTIONS[side]);
	}

	public boolean canPlaceOn(World world, int x, int y, int z, ForgeDirection side) {
		return world.getBlock(x, y, z).isSideSolid(world, x, y, z, side);
	}

	public void setSide(World world, int x, int y, int z, ForgeDirection dir) {
		TileEntityCrystalGlow te = (TileEntityCrystalGlow)world.getTileEntity(x, y, z);
		te.direction = dir;
		te.longAxis = ReikaDirectionHelper.getPerpendicularDirections(dir).get(0);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return null;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block b) {
		TileEntityCrystalGlow te = (TileEntityCrystalGlow)world.getTileEntity(x, y, z);
		ForgeDirection dir = te.direction;
		if (!this.canPlaceOn(world, x-dir.offsetX, y-dir.offsetY, z-dir.offsetZ, dir)) {
			ItemStack is = ChromaBlocks.GLOW.getStackOfMetadata(world.getBlockMetadata(x, y, z)+16*te.base.ordinal());
			world.setBlock(x, y, z, Blocks.air);
			ReikaSoundHelper.playBreakSound(world, x, y, z, this);
			ReikaItemHelper.dropItem(world, x+0.5, y+0.5, z+0.5, is);
		}
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess iba, int x, int y, int z) {
		float xmin = 0;
		float ymin = 0;
		float zmin = 0;
		float xmax = 1;
		float ymax = 1;
		float zmax = 1;
		float h = 0.125F;
		float w = 0.25F;
		TileEntity te = iba.getTileEntity(x, y, z);
		ForgeDirection dir = te instanceof TileEntityCrystalGlow ? ((TileEntityCrystalGlow)te).direction : ForgeDirection.UNKNOWN;
		ForgeDirection ax = te instanceof TileEntityCrystalGlow ? ((TileEntityCrystalGlow)te).longAxis : ForgeDirection.UNKNOWN;
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
	public TileEntity createTileEntity(World world, int meta) {
		return new TileEntityCrystalGlow();
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return true;
	}

	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean harvest)
	{
		if (this.canHarvest(world, player, x, y, z))
			this.harvestBlock(world, player, x, y, z, 0);
		return world.setBlockToAir(x, y, z);
	}

	private boolean canHarvest(World world, EntityPlayer ep, int x, int y, int z) {
		if (ep.capabilities.isCreativeMode)
			return false;
		return true;
	}

	@Override
	public void harvestBlock(World world, EntityPlayer ep, int x, int y, int z, int meta)
	{
		if (!this.canHarvest(world, ep, x, y, z))
			return;
		TileEntityCrystalGlow te = (TileEntityCrystalGlow)world.getTileEntity(x, y, z);
		if (te != null) {
			ItemStack is = ChromaBlocks.GLOW.getStackOfMetadata(world.getBlockMetadata(x, y, z)+16*te.base.ordinal());
			ReikaItemHelper.dropItem(world, x+0.5, y+0.5, z+0.5, is);
		}
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		TileEntityCrystalGlow te = (TileEntityCrystalGlow)world.getTileEntity(x, y, z);
		return new ItemStack(this, 1, meta+16*te.base.ordinal());
	}

	public static enum Bases {
		STONE("Stone", Blocks.stone),
		CRYSTALSTONE("Crystal Stone", ChromaBlocks.PYLONSTRUCT.getBlockInstance()),
		IRON("Iron", Items.iron_ingot, Blocks.iron_block),
		GOLD("Gold", Items.gold_ingot, Blocks.gold_block),
		OBSIDIAN("Obsidian", Blocks.obsidian),
		QUARTZ("Quartz", Blocks.quartz_block);

		public final Object ingredient;
		public final Block texture;
		public final String displayName;

		public static final Bases[] baseList = values();

		private Bases(String s, Block b) {
			this(s, b, b);
		}

		private Bases(String s, Object o, Block b) {
			displayName = s;
			ingredient = o;
			texture = b;
		}
	}

	public static class TileEntityCrystalGlow extends TileEntity {

		private ForgeDirection direction = ForgeDirection.UNKNOWN;
		private ForgeDirection longAxis = ForgeDirection.UNKNOWN;
		public Bases base = Bases.STONE;
		public boolean isIridescent;
		public boolean isRainbow;

		@Override
		public boolean canUpdate() {
			return false;
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);

			NBT.setInteger("dir", direction.ordinal());
			NBT.setInteger("long", longAxis.ordinal());

			NBT.setInteger("base", base.ordinal());

			NBT.setBoolean("irid", isIridescent);
			NBT.setBoolean("rainbow", isRainbow);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);

			direction = ForgeDirection.values()[NBT.getInteger("dir")];
			longAxis = ForgeDirection.values()[NBT.getInteger("long")];

			isIridescent = NBT.getBoolean("irid");
			isRainbow = NBT.getBoolean("rainbow");

			base = Bases.baseList[NBT.getInteger("base")];
		}

		@Override
		public Packet getDescriptionPacket() {
			NBTTagCompound NBT = new NBTTagCompound();
			this.writeToNBT(NBT);
			S35PacketUpdateTileEntity pack = new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, NBT);
			return pack;
		}

		@Override
		public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity p)  {
			this.readFromNBT(p.field_148860_e);
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}

		@Override
		public AxisAlignedBB getRenderBoundingBox() {
			return ReikaAABBHelper.getBlockAABB(xCoord, yCoord, zCoord);
		}

		public ForgeDirection getDirection() {
			return direction;
		}

		public ForgeDirection getLongAxis() {
			return longAxis;
		}

		public void toggle() {
			CrystalTypeBlock.ding(worldObj, xCoord, yCoord, zCoord);

			if (isIridescent) {
				isIridescent = false;
				isRainbow = true;
			}
			else if (isRainbow) {
				isRainbow = false;
			}
			else {
				isIridescent = true;
			}

			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}

		public void rotate() {
			CrystalTypeBlock.ding(worldObj, xCoord, yCoord, zCoord);
			longAxis = longAxis.getRotation(direction);
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}

}
