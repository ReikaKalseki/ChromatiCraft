package Reika.ChromatiCraft.Block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.Instantiable.CubePoints;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockDistortingEffect extends BlockContainer {

	public BlockDistortingEffect(Material mat) {
		super(mat);
		this.setBlockUnbreakable();
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return meta == 0 ? new TileEntityGlobalDistorting() : new TileEntityDistorting();
	}

	public static boolean canReplace(World world, int x, int y, int z) {
		Block b = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		return b.isOpaqueCube() && b.getRenderType() == 0 && !b.hasTileEntity(meta) && !b.isAir(world, x, y, z) && !ReikaBlockHelper.isUnbreakable(world, x, y, z, b, meta, null);
	}

	public static void doReplace(World world, int x, int y, int z, boolean global) {
		BlockKey bk = BlockKey.getAt(world, x, y, z);
		world.setBlock(x, y, z, ChromaBlocks.DISTORTING.getBlockInstance(), global ? 0 : 1, 3);
		TileEntityDistorting te = (TileEntityDistorting)world.getTileEntity(x, y, z);
		te.loadBlock(bk);
	}

	public static class TileEntityGlobalDistorting extends TileEntityDistorting {

		@Override
		public void loadBlock(BlockKey bk) {
			super.loadBlock(bk);
			render.isGlobal = true;
		}

	}

	public static class TileEntityDistorting extends TileEntity {

		private BlockKey previous;

		public DistortedBox render;

		public void loadBlock(BlockKey bk) {
			previous = bk;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			if (worldObj.isRemote)
				render = new DistortedBox(bk, new WorldLocation(this));
		}

		@Override
		public final boolean canUpdate() {
			return false;
		}

		public final void reset() {
			worldObj.setBlock(xCoord, yCoord, zCoord, previous.blockID, previous.metadata, 2);
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
		public final void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);

			if (previous != null)
				previous.writeToNBT("block", NBT);
		}

		@Override
		public final void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);

			if (NBT.hasKey("block"))
				this.loadBlock(BlockKey.readFromNBT("block", NBT));
		}

	}

	private static class DistortedBox {

		private final WorldLocation location;
		private final BlockKey block;
		private final CubePoints box;
		private final CubePoints renderBox;
		private final AxisAlignedBB bounds;

		boolean isGlobal = false;

		static Vec3 globalVelocity = Vec3.createVectorHelper(0, 0, 0);

		private DistortedBox(BlockKey bk, WorldLocation loc) {
			location = loc;
			block = bk;
			box = CubePoints.fullBlock();
			renderBox = box.copy();
			if (isGlobal) {
				bounds = ReikaAABBHelper.getBlockAABB(0, 0, 0).expand(4, 4, 4);
			}
			else {
				renderBox.setRandomVelocities(0.3);
				double r = 0.5;
				bounds = ReikaAABBHelper.getBlockAABB(0, 0, 0).expand(r, r, r);
			}
		}

		@SideOnly(Side.CLIENT)
		public void render(Tessellator v5) {
			renderBox.applyVelocities(bounds);
			ReikaRenderHelper.renderBlockPieceNonCuboid(block.blockID, block.metadata, v5, renderBox);
			if (isGlobal) {
				renderBox.setVelocities(globalVelocity);
			}
			else {
				renderBox.multiplyVelocities(0.99);
				if (bounds.maxX > 1) {
					ReikaAABBHelper.compressAABB(bounds, 0.0125, 0.0125, 0.0125);
					ReikaAABBHelper.fillAABB(bounds, 0, 0, 0, 1, 1, 1);
				}
			}
		}

	}

}
