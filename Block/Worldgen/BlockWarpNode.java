package Reika.ChromatiCraft.Block.Worldgen;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaItems;


public class BlockWarpNode extends BlockContainer {

	public BlockWarpNode(Material mat) {
		super(mat);

		this.setCreativeTab(ChromatiCraft.tabChromaGen);
		this.setResistance(6000000);
		this.setBlockUnbreakable();
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

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		if (ChromaItems.TOOL.matchWith(ep.getCurrentEquippedItem())) {
			TileEntityWarpNode te = (TileEntityWarpNode)world.getTileEntity(x, y, z);
			te.isOpen = true;
			world.markBlockForUpdate(x, y, z);
			return true;
		}
		return false;
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity e) {

	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityWarpNode();
	}

	public static class TileEntityWarpNode extends TileEntity {

		private boolean isOpen;

		@Override
		public boolean canUpdate() {
			return false;
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
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);

			NBT.setBoolean("open", isOpen);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);

			isOpen = NBT.getBoolean("open");

		}

		@Override
		public boolean shouldRenderInPass(int pass) {
			return pass == 1;
		}

	}

}
