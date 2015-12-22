/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block;

import net.minecraft.block.material.Material;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;

public class BlockActiveChroma extends BlockLiquidChroma {

	public BlockActiveChroma(Fluid fluid, Material material) {
		super(fluid, material);
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return meta == 0;
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return meta == 0 ? new TileEntityChroma() : null;
	}
	/*
	@Override
	public boolean canDisplace(IBlockAccess world, int x, int y, int z)
	{
		return world.getBlock(x, y, z) == this || super.canDisplace(world, x, y, z);
	}

	@Override
	public boolean displaceIfPossible(World world, int x, int y, int z) {

		if (world.getBlock(x, y, z).isAir(world, x, y, z))
			return true;

		Block block = world.getBlock(x, y, z);

		if (displacements.containsKey(block)) {
			if (displacements.get(block)) {
				block.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
				return true;
			}
			return false;
		}

		Material material = block.getMaterial();
		if (material.blocksMovement() || material == Material.portal)
			return false;

		int density = getDensity(world, x, y, z);
		if (density == Integer.MAX_VALUE) {
			block.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
			return true;
		}

		if (this.density > density || block == this)
			return true;
		else
			return false;
	}
	 */
	/*
	@Override
	public IIcon getIcon(IBlockAccess iba, int x, int y, int z, int s) {
		TileEntity te = iba.getTileEntity(x, y, z);
		IIcon ico = this.getIcon(s, iba.getBlockMetadata(x, y, z));
		return te instanceof TileEntityChroma && ((TileEntityChroma)te).isActive() ? ico : Blocks.grass.getIcon(1, 0);
	}*/

	@Override
	protected String getIcon() {
		return "activechroma";
	}

	@Override
	public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
		if (world.getBlock(x, y+1, z) == this) {
			int c = this.colorMultiplier(world, x, y+1, z);
			return c;
		}
		int meta = world.getBlockMetadata(x, y, z);
		if (meta == 0) {
			TileEntityChroma te = (TileEntityChroma)world.getTileEntity(x, y, z);
			return te != null ? te.getColor() : 0xffffff;
		}
		else {
			for (int i = 2; i < 6; i++) {
				ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
				int dx = x+dir.offsetX;
				int dy = y+dir.offsetY;
				int dz = z+dir.offsetZ;
				if (world.getBlock(dx, dy, dz) == this) {
					if (world.getBlock(dx, dy+1, dz) == this) {
						int c = this.colorMultiplier(world, dx, dy+1, dz);
						return c;
					}
					int meta2 = world.getBlockMetadata(dx, dy, dz);
					if (meta2 == 0) {
						return this.colorMultiplier(world, dx, dy, dz);
					}
					else if (meta2 < meta) {
						int color = this.colorMultiplier(world, dx, dy, dz);
						if (color != 0xffffff)
							return color;
					}
				}
			}
		}
		return 0xffffff;
	}

	public static int getColor(CrystalElement e, int berries) {
		return ReikaColorAPI.mixColors(e.getColor(), 0xffffff, berries/(float)TileEntityChroma.BERRY_SATURATION);
	}

	@Override
	public Fluid getFluid() {
		return FluidRegistry.getFluid("chroma");
	}
	/*
	@Override
	public FluidStack drain(World world, int x, int y, int z, boolean doDrain) {
		super.drain(world, x, y, z, doDrain);
		return new FluidStack(FluidRegistry.getFluid("chroma"), 1000);
	}
	 */
	public static class TileEntityChroma extends TileEntity {

		public static final int BERRY_SATURATION = 24;

		private int berryCount;
		private CrystalElement element;

		public int activate(CrystalElement e, int amt) {
			int add = Math.min(BERRY_SATURATION-berryCount, amt);
			if (add > 0) {
				if (e == element || element == null) {
					berryCount += add;
					element = e;
					this.update();
				}
				else {
					add = 0;
				}
			}
			return add;
		}

		private void update() {
			this.markDirty();

			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);

			worldObj.markBlockForUpdate(xCoord-16, yCoord, zCoord);
			worldObj.markBlockForUpdate(xCoord+16, yCoord, zCoord);
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord-16);
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord+16);

			worldObj.markBlockForUpdate(xCoord-16, yCoord, zCoord-16);
			worldObj.markBlockForUpdate(xCoord+16, yCoord, zCoord-16);
			worldObj.markBlockForUpdate(xCoord-16, yCoord, zCoord+16);
			worldObj.markBlockForUpdate(xCoord+16, yCoord, zCoord+16);
		}

		public int getColor() {
			return element != null ? BlockActiveChroma.getColor(element, berryCount) : 0xffffff;
		}

		public CrystalElement getElement() {
			return element;
		}

		@Override
		public boolean canUpdate() {
			return false;
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);

			//NBT.setBoolean("act", isActive);
			NBT.setInteger("count", berryCount);

			NBT.setInteger("elem", element != null ? element.ordinal() : -1);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);

			//isActive = NBT.getBoolean("act");
			berryCount = NBT.getInteger("count");

			int ord = NBT.getInteger("elem");
			element = ord >= 0 ? CrystalElement.elements[ord] : null;
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

		public boolean isFullyActive() {
			return berryCount == BERRY_SATURATION && element != null;
		}

		public void clear() {
			berryCount = 0;
			element = null;
			this.update();
		}

		/*
		public int getBerryCount() {
			return berryCount;
		}

		public void setBerries(int count) {
			berryCount = count;
			this.update();
		}*/

	}

}
