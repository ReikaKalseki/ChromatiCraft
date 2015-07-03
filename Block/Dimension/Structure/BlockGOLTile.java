package Reika.ChromatiCraft.Block.Dimension.Structure;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;

public class BlockGOLTile extends BlockContainer {

	private final IIcon[] icons = new IIcon[2];

	public BlockGOLTile(Material mat) {
		super(mat);
		this.setBlockUnbreakable();
		this.setResistance(60000);
		this.setCreativeTab(ChromatiCraft.tabChromaGen);
	}

	@Override
	public int getLightValue(IBlockAccess iba, int x, int y, int z) {
		TileEntity te = iba.getTileEntity(x, y, z);
		return te instanceof GOLTile && ((GOLTile)te).isActive() ? 15 : 0;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new GOLTile();
	}

	@Override
	public void onBlockClicked(World world, int x, int y, int z, EntityPlayer ep) {
		if (!world.isRemote)
			this.toggle(world, x, y, z);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		if (!world.isRemote)
			this.toggle(world, x, y, z);
		return true;
	}

	@Override
	public void onFallenUpon(World world, int x, int y, int z, Entity e, float d) {
		if (!world.isRemote && e instanceof EntityPlayer && d > 1)
			this.activate(world, x, y, z);
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random r) {
		((GOLTile)world.getTileEntity(x, y, z)).recalculate();
	}

	private void activate(World world, int x, int y, int z) {
		GOLTile te = (GOLTile)world.getTileEntity(x, y, z);
		te.setActive(true);
		//te.scheduleRecalculation(5);
	}

	private void toggle(World world, int x, int y, int z) {
		GOLTile te = (GOLTile)world.getTileEntity(x, y, z);
		te.setActive(!te.isActive());
		//te.scheduleRecalculation(5);
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		icons[0] = ico.registerIcon("chromaticraft:dimstruct/gol_off");
		icons[1] = ico.registerIcon("chromaticraft:dimstruct/gol_on");
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return icons[0];
	}

	@Override
	public IIcon getIcon(IBlockAccess iba, int x, int y, int z, int s) {
		TileEntity te = iba.getTileEntity(x, y, z);
		if (te instanceof GOLTile) {
			return ((GOLTile)te).isActive() ? icons[1] : icons[0];
		}
		else {
			return ChromaBlocks.STRUCTSHIELD.getBlockInstance().getIcon(BlockType.STONE.ordinal(), 0);
		}
	}

	public static class GOLTile extends TileEntity {

		private boolean defaultActive;
		private boolean isActive;
		private int numberNeighbors;
		private boolean isTicking;

		private static final int OFFSET = 1;
		private static final int RATE = 5;

		@Override
		public boolean canUpdate() {
			return true;//false;
		}

		/*
		private void scheduleRecalculation(int ticks) {
			worldObj.scheduleBlockUpdate(xCoord, yCoord, zCoord, this.getBlockType(), ticks);
		}
		 */

		public void activate() {
			isTicking = true;
		}

		public void reset() {
			this.setActive(defaultActive);
		}

		@Override
		public void updateEntity() {
			if (isTicking) {
				int mod = (int)(worldObj.getTotalWorldTime()%RATE);
				if (mod == 0) {
					numberNeighbors = this.countNeighbors();
				}
				else if (mod == OFFSET) {
					this.recalculate();
				}
			}
		}

		private void recalculate() {
			if (isActive) {
				if (numberNeighbors < 2 || numberNeighbors > 3) {
					this.setActive(false);
				}
			}
			else if (numberNeighbors == 3) {
				this.setActive(true);
			}

		}

		public void initialize(boolean active) {
			defaultActive = active;
			isActive = active;
		}

		public boolean isActive() {
			return isActive;
		}

		private void setActive(boolean ac) {
			isActive = ac;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			//this.updateNeighbors();
		}

		private int countNeighbors() {
			int c = 0;
			boolean flag = xCoord == -1 && zCoord == 0;
			for (int a1 = -1; a1 <= 1; a1++) {
				for (int a2 = -1; a2 <= 1; a2++) {
					if (a1 != 0 || a2 != 0) {
						int dx = xCoord+a1;
						int dz = zCoord+a2;
						Block b = worldObj.getBlock(dx, yCoord, dz);
						if (b == ChromaBlocks.GOL.getBlockInstance()) {
							GOLTile te = (GOLTile)worldObj.getTileEntity(dx, yCoord, dz);
							if (te.isActive())
								c++;
						}
					}
				}
			}
			return c;
		}

		private void updateNeighbors() {
			for (int i = 2; i < 6; i++) {
				ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
				int dx = xCoord+dir.offsetX;
				int dz = zCoord+dir.offsetY;
				Block b = worldObj.getBlock(dx, yCoord, dz);
				if (b == ChromaBlocks.GOL.getBlockInstance()) {
					GOLTile te = (GOLTile)worldObj.getTileEntity(dx, yCoord, dz);
					te.recalculate();
				}
			}
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);

			NBT.setBoolean("active", isActive);
			NBT.setBoolean("def", defaultActive);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);

			isActive = NBT.getBoolean("active");
			defaultActive = NBT.getBoolean("def");
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
