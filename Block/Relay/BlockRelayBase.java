/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Relay;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.ISBRH.RelayRenderer;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;


public abstract class BlockRelayBase extends Block {

	protected BlockRelayBase(Material mat) {
		super(mat);
		this.setHardness(0);
		this.setResistance(6000);
		this.setCreativeTab(ChromatiCraft.tabChroma);
		stepSound = new SoundType("stone", 1.0F, 0.5F);
	}

	@Override
	public final int getLightValue(IBlockAccess iba, int x, int y, int z) {
		TileEntity te = iba.getTileEntity(x, y, z);
		return te instanceof TileRelayBase && ((TileRelayBase)te).isTransmitting() ? 15 : 12;
	}

	@Override
	public final AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return null;
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
	public abstract TileEntity createTileEntity(World world, int meta);

	@Override
	public final boolean hasTileEntity(int meta) {
		return true;
	}

	@Override
	public final boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		ItemStack is = ep.getCurrentEquippedItem();
		if (ChromaItems.TOOL.matchWith(is)) {
			TileRelayBase te = (TileRelayBase)world.getTileEntity(x, y, z);
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[s];
			//if (dir.getOpposite().ordinal() != world.getBlockMetadata(x, y, z)) {
			te.setInput(dir);
			return true;
			//}
		}
		return false;
	}

	@Override
	public final void onBlockAdded(World world, int x, int y, int z) {
		//RelayNetworker.instance.addBlock(x, y, z, ForgeDirection.VALID_DIRECTIONS[world.getBlockMetadata(x, y, z)]);
	}

	@Override
	public final void breakBlock(World world, int x, int y, int z, Block b, int meta) {
		//RelayNetworker.instance.removeBlock(x, y, z, ForgeDirection.VALID_DIRECTIONS[meta]);
		super.breakBlock(world, x, y, z, b, meta);
	}

	@Override
	public final int getRenderType() {
		return ChromatiCraft.proxy.relayRender;
	}

	@Override
	public final int getRenderBlockPass() {
		return 1;
	}

	@Override
	public final boolean canRenderInPass(int pass) {
		RelayRenderer.renderPass = pass;
		return true;
	}

	public abstract static class TileRelayBase extends TileEntity {

		private ForgeDirection in = ForgeDirection.UNKNOWN;

		public abstract boolean canTransmit(CrystalElement e);

		public final boolean isTransmitting() {
			return false;
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

		@Override
		public final AxisAlignedBB getRenderBoundingBox() {
			return ReikaAABBHelper.getBlockAABB(xCoord, yCoord, zCoord);
		}

		public final ForgeDirection getInput() {
			return in;
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);

			NBT.setInteger("dir", in.ordinal());
			//NBT.setInteger("energy", energy);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);

			int dir = NBT.getInteger("dir");
			if (dir < 6)
				in = ForgeDirection.VALID_DIRECTIONS[dir];
			//energy = NBT.getInteger("energy");
		}

		public final void setInput(ForgeDirection dir) {
			in = dir;
			ReikaSoundHelper.playBreakSound(worldObj, xCoord, yCoord, zCoord, this.getBlockType());
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}

		@Override
		public boolean canUpdate() {
			return false;
		}

	}

}
