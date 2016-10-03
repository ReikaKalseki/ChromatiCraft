package Reika.ChromatiCraft.Block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Interfaces.SidedBlock;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;


public class BlockAvoLamp extends BlockContainer implements SidedBlock {

	public BlockAvoLamp(Material mat) {
		super(mat);

		this.setCreativeTab(ChromatiCraft.tabChromaDeco);
		this.setHardness(5);
		this.setResistance(6000);
		this.setLightLevel(1);

		this.setBlockBounds(0.25F, 0, 0.25F, 0.75F, 0.625F, 0.75F);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityAvoLamp();
	}

	@Override
	public int getRenderType() {
		return -1;
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
		TileEntityAvoLamp te = (TileEntityAvoLamp)world.getTileEntity(x, y, z);
		te.direction = ForgeDirection.VALID_DIRECTIONS[side];
		world.markBlockForUpdate(x, y, z);
	}

	@Override
	public boolean canPlaceOn(World world, int x, int y, int z, int side) {
		return true;
	}

	@Override
	public final void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		TileEntityAvoLamp te = (TileEntityAvoLamp)world.getTileEntity(x, y, z);
		if (te == null)
			return;
		AxisAlignedBB box = te.getBoundingBox();
		this.setBounds(box, x, y, z);
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
		AxisAlignedBB box = this.getCollisionBoundingBoxFromPool(world, x, y, z);
		this.setBounds(box, x, y, z);
		return box;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)  {
		TileEntityAvoLamp te = (TileEntityAvoLamp)world.getTileEntity(x, y, z);
		if (te == null)
			return null;
		AxisAlignedBB box = te.getBoundingBox();
		this.setBounds(box, x, y, z);
		return box;
	}

	private void setBounds(AxisAlignedBB box, int x, int y, int z) {
		this.setBlockBounds((float)box.minX-x, (float)box.minY-y, (float)box.minZ-z, (float)box.maxX-x, (float)box.maxY-y, (float)box.maxZ-z);
	}

	public static class TileEntityAvoLamp extends TileEntity {

		private ForgeDirection direction;

		public ForgeDirection getDirection() {
			return direction != null ? direction : ForgeDirection.UP;
		}

		public AxisAlignedBB getBoundingBox() {
			double mx = direction == ForgeDirection.EAST ? 0 : direction == ForgeDirection.WEST ? 0.5 : 0.25;
			double my = direction == ForgeDirection.UP ? 0 : direction == ForgeDirection.DOWN ? 0.5 : 0.25;
			double mz = direction == ForgeDirection.SOUTH ? 0 : direction == ForgeDirection.NORTH ? 0.5 : 0.25;
			double px = direction == ForgeDirection.WEST ? 1 : direction == ForgeDirection.EAST ? 0.5 : 0.75;
			double py = direction == ForgeDirection.DOWN ? 1 : direction == ForgeDirection.UP ? 0.5 : 0.75;
			double pz = direction == ForgeDirection.NORTH ? 1 : direction == ForgeDirection.SOUTH ? 0.5 : 0.75;
			return AxisAlignedBB.getBoundingBox(xCoord+mx, yCoord+my, zCoord+mz, xCoord+px, yCoord+py, zCoord+pz);
		}

		@Override
		public AxisAlignedBB getRenderBoundingBox()
		{
			return ReikaAABBHelper.getBlockAABB(this);
		}

		@Override
		public boolean canUpdate() {
			return false;
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);
			if (NBT.hasKey("dir")) {
				direction = ForgeDirection.VALID_DIRECTIONS[NBT.getInteger("dir")];
			}
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);
			if (direction != null) {
				NBT.setInteger("dir", direction.ordinal());
			}
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

	}

}
