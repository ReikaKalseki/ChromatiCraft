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

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityHoverPad;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;

public class BlockHoverPad extends Block {

	//private IIcon top;
	//private IIcon side;
	//private IIcon bottom;

	public BlockHoverPad(Material mat) {
		super(mat);
		this.setHardness(2);
		this.setResistance(10);
		this.setLightLevel(8);
		this.setCreativeTab(ChromatiCraft.tabChroma);
	}

	@Override
	public int getLightValue(IBlockAccess iba, int x, int y, int z) {
		return iba.getBlockMetadata(x, y, z) == 0 ? 8 : 15;
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return new HoverPadAuxTile();
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return meta == 0;
	}
	/*
	@Override
	public IIcon getIcon(int s, int meta) {
		return s == 0 ? bottom : (s == 1 ? top : side);
	}*/

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		//bottom = ico.registerIcon("chromaticraft:tile/injector_bottom");
		//top = ico.registerIcon("chromaticraft:tile/injector_top");
		//side = ico.registerIcon("chromaticraft:tile/injector_side");
		blockIcon = ico.registerIcon("chromaticraft:basic/hoverpad");
	}

	@Override
	public int damageDropped(int meta) {
		return 0;
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		if (world.getBlockMetadata(x, y, z) == 0) {
			for (int i = 0; i < 6; i++) {
				ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
				int dx = x+dir.offsetX;
				int dy = y+dir.offsetY;
				int dz = z+dir.offsetZ;
				ChromaTiles c = ChromaTiles.getTile(world, dx, dy, dz);
				if (c == ChromaTiles.HOVERPAD) {
					TileEntityHoverPad te2 = (TileEntityHoverPad)world.getTileEntity(dx, dy, dz);
					te2.clearBox();
				}
				else if (world.getBlock(dx, dy, dz) == this) {
					HoverPadAuxTile tile = (HoverPadAuxTile)world.getTileEntity(dx, dy, dz);
					if (tile.hasTile()) {
						tile.getController().clearBox();
					}
				}
			}
		}
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block old, int oldmeta) {
		HoverPadAuxTile te = (HoverPadAuxTile)world.getTileEntity(x, y, z);
		if (te != null) {
			TileEntityHoverPad te2 = te.getController();
			if (te2 != null) {
				te2.clearBox();
			}
		}
		super.breakBlock(world, x, y, z, old, oldmeta);
	}

	public static class HoverPadAuxTile extends TileEntity {

		private Coordinate controller = null;

		@Override
		public boolean canUpdate() {
			return false;
		}

		public void setTile(TileEntityHoverPad te) {
			controller = new Coordinate(te);
		}

		public boolean hasTile() {
			return controller != null && this.getController() != null;
		}

		public TileEntityHoverPad getController() {
			if (controller == null)
				return null;
			TileEntity te = controller.getTileEntity(worldObj);
			if (te instanceof TileEntityHoverPad) {
				return (TileEntityHoverPad)te;
			}
			else {
				controller = null;
				return null;
			}
		}

		public void reset() {
			controller = null;
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);

			if (controller != null) {
				NBT.setInteger("tx", controller.xCoord);
				NBT.setInteger("ty", controller.yCoord);
				NBT.setInteger("tz", controller.zCoord);
				controller.writeToNBT("controller", NBT);
			}
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);

			if (NBT.hasKey("controller")) {
				controller = Coordinate.readFromNBT("controller", NBT);
			}
			else if (NBT.hasKey("tx")) {
				int x = NBT.getInteger("tx");
				int y = NBT.getInteger("ty");
				int z = NBT.getInteger("tz");
				controller = new Coordinate(x, y, z);
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
		}

	}

}
