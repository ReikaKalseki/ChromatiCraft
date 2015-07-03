package Reika.ChromatiCraft.Block.Dimension.Structure;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Block.Dimension.Structure.BlockGOLTile.GOLTile;
import Reika.ChromatiCraft.Registry.ChromaBlocks;

public class BlockGOLController extends BlockContainer {

	private final IIcon[] icons = new IIcon[2];

	public BlockGOLController(Material mat) {
		super(mat);
		this.setBlockUnbreakable();
		this.setResistance(60000);
		this.setCreativeTab(ChromatiCraft.tabChromaGen);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new GOLController();
	}

	@Override
	public void onBlockClicked(World world, int x, int y, int z, EntityPlayer ep) {
		if (!world.isRemote)
			this.activate(world, x, y, z);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		if (!world.isRemote)
			this.activate(world, x, y, z);
		return true;
	}

	private void activate(World world, int x, int y, int z) {
		GOLController te = (GOLController)world.getTileEntity(x, y, z);
		if (te.isActive)
			te.reset();
		else
			te.activate();
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		blockIcon = ico.registerIcon("chromaticraft:dimstruct/gol_control");
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return blockIcon;
	}

	public static class GOLController extends TileEntity {

		private int minX;
		private int maxX;
		private int minZ;
		private int maxZ;
		private int floorY;

		private boolean isActive;

		@Override
		public boolean canUpdate() {
			return false;
		}

		public void initialize(int x1, int x2, int z1, int z2, int y) {
			minX = x1;
			maxX = x2;
			minZ = z1;
			maxZ = z2;
			floorY = y;
		}

		private void activate() {
			isActive = true;
			for (int x = minX; x <= maxX; x++) {
				for (int z = minZ; z <= maxZ; z++) {
					Block b = worldObj.getBlock(x, floorY, z);
					if (b == ChromaBlocks.GOL.getBlockInstance()) {
						GOLTile te = (GOLTile)worldObj.getTileEntity(x, floorY, z);
						te.activate();
					}
				}
			}
		}

		private void reset() {
			isActive = false;
			for (int x = minX; x <= maxX; x++) {
				for (int z = minZ; z <= maxZ; z++) {
					Block b = worldObj.getBlock(x, floorY, z);
					if (b == ChromaBlocks.GOL.getBlockInstance()) {
						GOLTile te = (GOLTile)worldObj.getTileEntity(x, floorY, z);
						te.reset();
					}
				}
			}
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);

			NBT.setInteger("minx", minX);
			NBT.setInteger("maxx", maxX);
			NBT.setInteger("minz", minZ);
			NBT.setInteger("maxz", maxZ);
			NBT.setInteger("posy", floorY);

			NBT.setBoolean("active", isActive);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);

			minX = NBT.getInteger("minx");
			maxX = NBT.getInteger("maxx");
			minZ = NBT.getInteger("minz");
			maxZ = NBT.getInteger("maxz");
			floorY = NBT.getInteger("posy");

			isActive = NBT.getBoolean("active");
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
