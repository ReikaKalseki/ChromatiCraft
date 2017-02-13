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

import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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
import Reika.ChromatiCraft.Block.BlockDummyAux.TileEntityDummyAux.Flags;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;


@Strippable(value = {"mcp.mobius.waila.api.IWailaDataProvider"})
public class BlockDummyAux extends BlockContainer implements IWailaDataProvider {

	public BlockDummyAux(Material mat) {
		super(mat);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityDummyAux();
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public IIcon getIcon(IBlockAccess iba, int x, int y, int z, int s) {
		TileEntityDummyAux te = (TileEntityDummyAux)iba.getTileEntity(x, y, z);
		return te.getFlag(Flags.RENDER) ? ChromaBlocks.STRUCTSHIELD.getBlockInstance().getIcon(1, BlockType.STONE.metadata) : ChromaIcons.TRANSPARENT.getIcon();
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		TileEntityDummyAux te = (TileEntityDummyAux)world.getTileEntity(x, y, z);
		return te.relayClick(ep, s, a, b, c);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		TileEntityDummyAux te = (TileEntityDummyAux)world.getTileEntity(x, y, z);
		return te.getFlag(Flags.HITBOX) ? super.getCollisionBoundingBoxFromPool(world, x, y, z) : null;
	}

	@Override
	@ModDependent(ModList.WAILA)
	public ItemStack getWailaStack(IWailaDataAccessor acc, IWailaConfigHandler cfg) {
		TileEntityDummyAux te = (TileEntityDummyAux)acc.getTileEntity();
		if (te == null || te.relay == null)
			return null;
		Block b = te.relay.getBlock(acc.getWorld());
		return b instanceof IWailaDataProvider ? ((IWailaDataProvider)b).getWailaStack(acc, cfg) : null;
	}

	@Override
	@ModDependent(ModList.WAILA)
	public List<String> getWailaHead(ItemStack is, List<String> currenttip, IWailaDataAccessor acc, IWailaConfigHandler cfg) {
		/*
		TileEntityDummyAux te = (TileEntityDummyAux)acc.getTileEntity();
		if (te == null || te.relay == null)
			return currenttip;
		Block b = te.relay.getBlock(acc.getWorld());
		return b instanceof IWailaDataProvider ? ((IWailaDataProvider)b).getWailaHead(is, currenttip, acc, cfg) : currenttip;
		 */
		return currenttip;
	}

	@Override
	@ModDependent(ModList.WAILA)
	public List<String> getWailaBody(ItemStack is, List<String> currenttip, IWailaDataAccessor acc, IWailaConfigHandler cfg) {
		/*
		TileEntityDummyAux te = (TileEntityDummyAux)acc.getTileEntity();
		if (te == null || te.relay == null)
			return currenttip;
		Block b = te.relay.getBlock(acc.getWorld());
		return b instanceof IWailaDataProvider ? ((IWailaDataProvider)b).getWailaBody(is, currenttip, acc, cfg) : currenttip;
		 */
		return currenttip;
	}

	@Override
	@ModDependent(ModList.WAILA)
	public List<String> getWailaTail(ItemStack is, List<String> currenttip, IWailaDataAccessor acc, IWailaConfigHandler cfg) {
		/*
		TileEntityDummyAux te = (TileEntityDummyAux)acc.getTileEntity();
		if (te == null || te.relay == null)
			return currenttip;
		Block b = te.relay.getBlock(acc.getWorld());
		return b instanceof IWailaDataProvider ? ((IWailaDataProvider)b).getWailaTail(is, currenttip, acc, cfg) : currenttip;
		 */
		return currenttip;
	}

	@Override
	@ModDependent(ModList.WAILA)
	public final NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z) {
		return tag;
	}

	public static class TileEntityDummyAux extends TileEntity {

		private int flags;

		private Coordinate relay;

		@Override
		public boolean canUpdate() {
			return false;
		}

		public void link(Coordinate c) {
			relay = c;
		}

		private boolean relayClick(EntityPlayer ep, int s, float a, float b, float c) {
			if (relay != null)
				return relay.getBlock(worldObj).onBlockActivated(worldObj, relay.xCoord, relay.yCoord, relay.zCoord, ep, s, a, b, c);
			return false;
		}

		public boolean relayManipulatorClick(ItemStack is, EntityPlayer ep, int s, float a, float b, float c) {
			if (relay == null)
				return false;
			return ChromaItems.TOOL.getItemInstance().onItemUse(is, ep, worldObj, relay.xCoord, relay.yCoord, relay.zCoord, s, a, b, c);
		}

		public void setFlag(Flags f, boolean set) {
			int base = flags;
			if (this.getFlag(f) != set) {
				flags = ReikaMathLibrary.toggleBit(flags, f.ordinal());
			}
		}

		public boolean getFlag(Flags f) {
			return (f.flag & flags) != 0;
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);

			if (relay != null)
				relay.writeToNBT("loc", NBT);

			NBT.setInteger("flags", flags);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);

			relay = Coordinate.readFromNBT("loc", NBT);

			flags = NBT.getInteger("flags");
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

		public TileEntity getLinkedTile() {
			return relay != null ? relay.getTileEntity(worldObj) : null;
		}

		public static enum Flags {
			HITBOX(),
			MOUSEOVER(),
			RENDER();

			private final int flag;

			private Flags() {
				flag = 1 << this.ordinal();
			}
		}

	}

}
