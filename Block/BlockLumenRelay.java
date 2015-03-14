/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.ISBRH.RelayRenderer;
import Reika.ChromatiCraft.Render.Particle.EntityCenterBlurFX;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockLumenRelay extends Block {

	private final IIcon[][] icons = new IIcon[6][6];

	public BlockLumenRelay(Material mat) {
		super(mat);
		this.setHardness(0);
		this.setResistance(6000);
		this.setCreativeTab(ChromatiCraft.tabChroma);
		stepSound = new SoundType("stone", 1.0F, 0.5F);
	}

	@Override
	public int getLightValue(IBlockAccess iba, int x, int y, int z) {
		TileEntity te = iba.getTileEntity(x, y, z);
		return te instanceof TileEntityLumenRelay && ((TileEntityLumenRelay)te).isTransmitting() ? 15 : 12;
	}

	public boolean canPlaceOn(World world, int x, int y, int z, int side) {
		return world.getBlock(x, y, z).isSideSolid(world, x, y, z, ForgeDirection.VALID_DIRECTIONS[side]);
	}

	public void setSide(World world, int x, int y, int z, int side) {
		world.setBlockMetadataWithNotify(x, y, z, side, 3);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return null;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block b) {
		int meta = world.getBlockMetadata(x, y, z);
		ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[meta];
		if (!this.canPlaceOn(world, x-dir.offsetX, y-dir.offsetY, z-dir.offsetZ, meta)) {
			ReikaSoundHelper.playBreakSound(world, x, y, z, this);
			ReikaItemHelper.dropItems(world, x+0.5, y+0.5, z+0.5, this.getDrops(world, x, y, z, meta, 0));
			world.setBlock(x, y, z, Blocks.air);
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
		float h = 0.625F;
		float w = 0.125F;
		switch(ForgeDirection.VALID_DIRECTIONS[iba.getBlockMetadata(x, y, z)]) {
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
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int getRenderType() {
		return ChromatiCraft.proxy.relayRender;
	}

	@Override
	public int getRenderBlockPass() {
		return 1;
	}

	@Override
	public boolean canRenderInPass(int pass) {
		RelayRenderer.renderPass = pass;
		return true;
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		blockIcon = ico.registerIcon("chromaticraft:basic/relay");
	}
	/*
	@Override
	public IIcon getIcon(int s, int meta) {
		return icons[meta][s];
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		for (int i = 0; i < 6; i++) { //metas (dirs)
			for (int k = 0; k < 6; k++) { //sides
				if (i == k) { //top face
					icons[i][k] = ico.registerIcon("chromaticraft:crystal/crystal_32");
				}
				else if (i%2 == 0 ? k == i+1 : k == i-1) { //bottom face
					icons[i][k] = ico.registerIcon("chromaticraft:pylon/block_0");
				}
				else {
					icons[i][k] = ico.registerIcon("chromaticraft:basic/relay_side_"+i);
				}
			}
		}
	}
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random r) {
		int meta = world.getBlockMetadata(x, y, z);
		ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[meta];
		TileEntityLumenRelay te = (TileEntityLumenRelay)world.getTileEntity(x, y, z);
		CrystalElement e = te.isMulti ? CrystalElement.randomElement() : te.color;
		double dx = x+0.5+dir.offsetX*0.25;
		double dy = y+0.5+dir.offsetY*0.25;
		double dz = z+0.5+dir.offsetZ*0.25;
		EntityFX fx = new EntityCenterBlurFX(e, world, dx, dy, dz, 0, 0, 0).setScale(2+r.nextFloat()*2);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase e, ItemStack is) {
		TileEntityLumenRelay te = (TileEntityLumenRelay)world.getTileEntity(x, y, z);
		te.isMulti = is.getItemDamage() == 16;
		te.color = te.isMulti ? CrystalElement.WHITE : CrystalElement.elements[is.getItemDamage()];
		te.in = ForgeDirection.VALID_DIRECTIONS[world.getBlockMetadata(x, y, z)].getOpposite();
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		ItemStack is = ep.getCurrentEquippedItem();
		if (ChromaItems.TOOL.matchWith(is)) {
			TileEntityLumenRelay te = (TileEntityLumenRelay)world.getTileEntity(x, y, z);
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[s];
			if (dir.getOpposite().ordinal() != world.getBlockMetadata(x, y, z)) {
				te.in = dir;
				ReikaSoundHelper.playBreakSound(world, x, y, z, this);
				world.markBlockForUpdate(x, y, z);
				return true;
			}
		}
		return false;
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		//RelayNetworker.instance.addBlock(x, y, z, ForgeDirection.VALID_DIRECTIONS[world.getBlockMetadata(x, y, z)]);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block b, int meta) {
		//RelayNetworker.instance.removeBlock(x, y, z, ForgeDirection.VALID_DIRECTIONS[meta]);
		super.breakBlock(world, x, y, z, b, meta);
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return new TileEntityLumenRelay();
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
		TileEntityLumenRelay te = (TileEntityLumenRelay)world.getTileEntity(x, y, z);
		if (te != null) {
			ItemStack is = ChromaBlocks.RELAY.getStackOfMetadata(te.isMulti ? 16 : te.color.ordinal());
			ReikaItemHelper.dropItem(world, x+0.5, y+0.5, z+0.5, is);
		}
	}

	public static class TileEntityLumenRelay extends TileEntity {

		private CrystalElement color = CrystalElement.WHITE;
		private boolean isMulti = false;
		private int energy = 0;
		private ForgeDirection in = ForgeDirection.UNKNOWN;

		@Override
		public boolean canUpdate() {
			return false;
		}

		public boolean isTransmitting() {
			return false;
		}

		public boolean canTransmit(CrystalElement e) {
			return isMulti || e == color;
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);

			NBT.setBoolean("multi", isMulti);
			NBT.setInteger("color", color.ordinal());
			NBT.setInteger("dir", in.ordinal());
			//NBT.setInteger("energy", energy);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);

			isMulti = NBT.getBoolean("multi");
			color = CrystalElement.elements[NBT.getInteger("color")];
			in = ForgeDirection.VALID_DIRECTIONS[NBT.getInteger("dir")];
			//energy = NBT.getInteger("energy");
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

		public ForgeDirection getInput() {
			return in;
		}

	}

}
