/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block;

import java.util.Collection;
import java.util.HashSet;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
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
import Reika.ChromatiCraft.Auxiliary.Interfaces.SidedBlock;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.ChromatiCraft.TileEntity.Transport.TileEntityRouterHub;
import Reika.ChromatiCraft.TileEntity.Transport.TileEntityRouterHub.ItemRule;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.MESystemReader.MatchMode;


public class BlockRouterNode extends Block implements SidedBlock {

	private final IIcon[] icons = new IIcon[2];

	public BlockRouterNode(Material mat) {
		super(mat);
		this.setCreativeTab(ChromatiCraft.tabChroma);
		this.setHardness(0);
		this.setResistance(6000);
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		switch(meta) {
			case 0:
				return new TileEntityRouterExtraction();
			case 1:
				return new TileEntityRouterInsertion();
			default:
				return null;
		}
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
	public IIcon getIcon(int s, int meta) {
		return icons[meta];
	}

	@Override
	public int damageDropped(int meta) {
		return meta;
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		for (int i = 0; i < icons.length; i++) {
			icons[i] = ico.registerIcon("chromaticraft:basic/routernode-"+i);
		}
	}

	public boolean canPlaceOn(World world, int x, int y, int z, int side) {
		return world.getBlock(x, y, z).getMaterial().isSolid();//.isSideSolid(world, x, y, z, ForgeDirection.VALID_DIRECTIONS[side]);
	}

	public void setSide(World world, int x, int y, int z, int side) {
		((TileEntityRouterNode)world.getTileEntity(x, y, z)).place(world, x, y, z, side);
	}

	public void setConnection(World world, int x, int y, int z, Coordinate hub) {
		((TileEntityRouterNode)world.getTileEntity(x, y, z)).setHub(hub);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		ep.openGui(ChromatiCraft.instance, ChromaGuis.TILE.ordinal(), world, x, y, z);
		return true;
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
		float w = 0.1875F;
		TileEntity te = iba.getTileEntity(x, y, z);
		if (te instanceof TileEntityRouterNode) {
			ForgeDirection dir = ((TileEntityRouterNode)te).getSide();
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
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return null;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block b) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityRouterNode) {
			ForgeDirection dir = ((TileEntityRouterNode)te).getSide();
			if (!this.canPlaceOn(world, x-dir.offsetX, y-dir.offsetY, z-dir.offsetZ, world.getBlockMetadata(x, y, z))) {
				ReikaSoundHelper.playBreakSound(world, x, y, z, this);
				this.drop(world, x, y, z);
			}
		}
	}

	private static void drop(World world, int x, int y, int z) {
		ItemStack is = new ItemStack(ChromaBlocks.ROUTERNODE.getBlockInstance(), 1, world.getBlockMetadata(x, y, z));
		ReikaItemHelper.dropItem(world, x+0.5, y+0.5, z+0.5, is);
		world.setBlock(x, y, z, Blocks.air);
	}

	public static interface RouterFilter {

		public void setFilterMode(int slot, MatchMode mode);
		public void setFilterItem(int slot, ItemStack is);
		public ItemRule getFilter(int slot);

	}

	public static abstract class TileEntityRouterNode extends TileEntity implements RouterFilter {

		private ForgeDirection side;
		public boolean isBlacklist;
		private Coordinate hub;

		private ItemRule[] filter = new ItemRule[9];

		@Override
		public final boolean canUpdate() {
			return false;
		}

		public final ForgeDirection getSide() {
			return side != null ? side : ForgeDirection.UP;
		}

		public final void setHub(Coordinate c) {
			if (hub != null && !hub.equals(c)) {
				TileEntity te = hub.getTileEntity(worldObj);
				if (te instanceof TileEntityRouterHub) {
					((TileEntityRouterHub)te).removeConnection(this);
				}
			}
			hub = c;
			if (hub == null)
				return;
			TileEntity te = hub.getTileEntity(worldObj);
			if (te instanceof TileEntityRouterHub) {
				this.addConnection((TileEntityRouterHub)te);
			}
		}

		public Coordinate getHub() {
			return hub;
		}

		private final void place(World world, int x, int y, int z, int side) {
			this.side = ForgeDirection.VALID_DIRECTIONS[side];
		}

		protected abstract void addConnection(TileEntityRouterHub te);

		public void setFilterItem(int slot, ItemStack is) {
			MatchMode mode = filter[slot] != null ? filter[slot].mode : MatchMode.EXACT;
			filter[slot] = is != null ? new ItemRule(is.copy(), mode) : null;
			this.update();
		}

		public void setFilterMode(int slot, MatchMode mode) {
			if (filter[slot] == null)
				return;
			filter[slot] = new ItemRule(filter[slot].getItem(), mode);
			this.update();
		}

		public void update() {
			this.setHub(hub);
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}

		public final Collection<ItemRule> getFilter() {
			HashSet<ItemRule> li = new HashSet();
			for (int i = 0; i < filter.length; i++) {
				if (filter[i] != null) {
					li.add(filter[i]);
				}
			}
			return li;
		}

		public ItemRule getFilter(int slot) {
			return filter[slot];
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);

			filter = new ItemRule[9];
			for (int i = 0; i < filter.length; i++) {
				filter[i] = ItemRule.readFromNBT(NBT.getCompoundTag("slot_"+i));
			}

			if (NBT.hasKey("hub"))
				hub = Coordinate.readFromNBT("hub", NBT);

			side = ForgeDirection.VALID_DIRECTIONS[NBT.getInteger("side")];
			isBlacklist = NBT.getBoolean("black");
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);

			for (int i = 0; i < filter.length; i++) {
				if (filter[i] != null)
					NBT.setTag("slot_"+i, filter[i].writeToNBT());
			}

			if (hub != null)
				hub.writeToNBT("hub", NBT);

			NBT.setInteger("side", this.getSide().ordinal());
			NBT.setBoolean("black", isBlacklist);
		}

		@Override
		public final Packet getDescriptionPacket() {
			NBTTagCompound NBT = new NBTTagCompound();
			this.writeToNBT(NBT);
			S35PacketUpdateTileEntity pack = new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, NBT);
			return pack;
		}

		@Override
		public final void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity p)  {
			this.readFromNBT(p.field_148860_e);
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}

	}

	public static class TileEntityRouterInsertion extends TileEntityRouterNode {

		@Override
		protected void addConnection(TileEntityRouterHub te) {
			te.addInserter(this);
		}

	}

	public static class TileEntityRouterExtraction extends TileEntityRouterNode {

		@Override
		protected void addConnection(TileEntityRouterHub te) {
			te.addExtractor(this);
		}

	}

}
