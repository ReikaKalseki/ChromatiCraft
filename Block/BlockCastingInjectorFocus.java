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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.GUI.Book.GuiMachineDescription;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingInjector;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;

public class BlockCastingInjectorFocus extends Block {

	private IIcon bottom;
	private final IIcon[] sideTextures = new IIcon[2];
	private final IIcon[] topTextures = new IIcon[2];

	public BlockCastingInjectorFocus(Material mat) {
		super(mat);
		this.setHardness(3);
		this.setResistance(12);
		this.setCreativeTab(ChromatiCraft.tabChroma);
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return new CastingInjectorAuxTile();
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return true;
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return s == 0 ? bottom : (s == 1 ? topTextures[GuiMachineDescription.runningRender ? 1 : 0] : sideTextures[GuiMachineDescription.runningRender ? 1 : 0]);
	}

	@Override
	public IIcon getIcon(IBlockAccess iba, int x, int y, int z, int s) {
		if (s == 0)
			return bottom;
		boolean active = GuiMachineDescription.runningRender || this.isValidPosition(iba, x, y, z);
		int idx = active ? 1 : 0;
		return s == 1 ? topTextures[idx] : sideTextures[idx];
	}

	private boolean isValidPosition(IBlockAccess iba, int x, int y, int z) {
		if (ChromaTiles.getTile(iba, x, y+1, z) != ChromaTiles.STAND)
			return false;
		int r = 4;
		for (int i = -r; i <= r; i += r) {
			for (int k = -r; k <= r; k += r) {
				if (ChromaTiles.getTile(iba, x+i, y, z+k) == ChromaTiles.TABLE)
					return true;
			}
		}
		return false;
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		bottom = ico.registerIcon("chromaticraft:tile/injector_bottom");
		topTextures[0] = ico.registerIcon("chromaticraft:tile/injector_top");
		sideTextures[0] = ico.registerIcon("chromaticraft:tile/injector_side");
		topTextures[1] = ico.registerIcon("chromaticraft:tile/injector_top_variant");
		sideTextures[1] = ico.registerIcon("chromaticraft:tile/injector_side_variant");
	}

	@Override
	public int damageDropped(int meta) {
		return 0;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block oldB, int oldM) {
		super.breakBlock(world, x, y, z, oldB, oldM);
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		super.onBlockAdded(world, x, y, z);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		ItemStack is = ep.getCurrentEquippedItem();
		if (ChromaTiles.STAND.match(is))
			return false;
		CastingInjectorAuxTile te = (CastingInjectorAuxTile)world.getTileEntity(x, y, z);
		TileEntityCastingInjector tk = te.getController();
		if (tk == null)
			return false;
		ep.openGui(ChromatiCraft.instance, ChromaGuis.TILE.ordinal(), world, tk.xCoord, tk.yCoord, tk.zCoord);
		return true;
	}

	public static class CastingInjectorAuxTile extends TileEntity {

		private Coordinate controller = null;

		@Override
		public boolean canUpdate() {
			return false;
		}

		public void setTile(TileEntityCastingInjector te) {
			controller = new Coordinate(te);
		}

		public boolean hasTile() {
			return controller != null && this.getController() != null;
		}

		public TileEntityCastingInjector getController() {
			if (controller == null)
				return null;
			TileEntity te = controller.getTileEntity(worldObj);
			if (te instanceof TileEntityCastingInjector) {
				return (TileEntityCastingInjector)te;
			}
			else {
				controller = null;
				return null;
			}
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
