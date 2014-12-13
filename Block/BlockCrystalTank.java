/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
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
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalTank;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Instantiable.Data.BlockArray;
import Reika.DragonAPI.Interfaces.ConnectedTextureGlass;

@Strippable(value = {"mcp.mobius.waila.api.IWailaDataProvider"})
public class BlockCrystalTank extends Block implements IWailaDataProvider, ConnectedTextureGlass {

	private final ArrayList<Integer> allDirs = new ArrayList();
	private IIcon[] edges = new IIcon[10];

	public BlockCrystalTank(Material mat) {
		super(mat);
		this.setCreativeTab(ChromatiCraft.tabChroma);
		this.setHardness(1);
		this.setResistance(600);

		for (int i = 1; i < 10; i++) {
			allDirs.add(i);
		}
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return new CrystalTankAuxTile();
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return true;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		ItemStack is = ep.getCurrentEquippedItem();
		CrystalTankAuxTile te = (CrystalTankAuxTile)world.getTileEntity(x, y, z);
		TileEntityCrystalTank tk = te.getTankController();
		if (tk == null)
			return false;
		if (is != null) {
			FluidStack fs = FluidContainerRegistry.getFluidForFilledItem(is);
			if (fs != null) {
				int drain = tk.fill(null, fs, false);
				if (drain == fs.amount) {
					tk.fill(null, fs, true);
					if (!ep.capabilities.isCreativeMode) {
						ItemStack is2 = FluidContainerRegistry.drainFluidContainer(is);
						ep.setCurrentItemOrArmor(0, is2);
					}
				}
				return true;
			}
			else if (FluidContainerRegistry.isEmptyContainer(is)) {
				FluidStack rem = tk.drain(null, tk.getLevel(), false);
				if (rem != null) {
					ItemStack fill = FluidContainerRegistry.fillFluidContainer(rem, is);
					if (fill != null) {
						FluidStack removed = FluidContainerRegistry.getFluidForFilledItem(fill);
						tk.drain(null, removed.amount, true);
						if (!ep.capabilities.isCreativeMode) {
							ep.setCurrentItemOrArmor(0, fill);
						}
					}
				}
				return true;
			}
		}
		if (ChromaBlocks.TANK.match(is))
			return false;
		ep.openGui(ChromatiCraft.instance, ChromaGuis.TILE.ordinal(), world, tk.xCoord, tk.yCoord, tk.zCoord);
		return true;
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		CrystalTankAuxTile tile = (CrystalTankAuxTile)world.getTileEntity(x, y, z);
		TileEntityCrystalTank te = tile.getTankController();
		return te != null && te.getFluid() != null ? te.getFluid().getLuminosity() : 0;
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return edges[0];//meta == 0 ? blockIcon : ChromaIcons.TRANSPARENT.getIcon();
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		blockIcon = ico.registerIcon("chromaticraft:basic/tank2");

		for (int i = 0; i < 10; i++) {
			edges[i] = ico.registerIcon("chromaticraft:tank/tank_"+i);
		}
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
	public int getRenderType() {
		return ChromatiCraft.proxy.tankRender;
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		CrystalTankAuxTile te = new CrystalTankAuxTile();
		world.setTileEntity(x, y, z, te);

		TileEntityCrystalTank con = null;
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			ChromaTiles c = ChromaTiles.getTile(world, dx, dy, dz);
			if (c == ChromaTiles.TANK) {
				TileEntityCrystalTank tank = (TileEntityCrystalTank)world.getTileEntity(dx, dy, dz);
				te.setTile(tank);
				world.setBlockMetadataWithNotify(x, y, z, 1, 3);
				te.addToTank();
				con = tank;
			}
			else if (world.getBlock(dx, dy, dz) == this) {
				CrystalTankAuxTile tile = (CrystalTankAuxTile)world.getTileEntity(dx, dy, dz);
				if (tile.hasTile()) {
					te.setTile(tile.getTankController());
					world.setBlockMetadataWithNotify(x, y, z, 1, 3);
					te.addToTank();
					con = tile.getTankController();
				}
			}
		}

		if (con != null) {
			BlockArray blocks = new BlockArray();
			blocks.recursiveAddWithBounds(world, x, y, z, this, x-32, y-32, z-32, x+32, y+32, z+32);
			for (int i = 0; i < blocks.getSize(); i++) {
				int[] xyz = blocks.getNthBlock(i);
				int dx = xyz[0];
				int dy = xyz[1];
				int dz = xyz[2];
				CrystalTankAuxTile tile = (CrystalTankAuxTile)world.getTileEntity(dx, dy, dz);
				tile.setTile(con);
				world.setBlockMetadataWithNotify(dx, dy, dz, 1, 3);
				tile.addToTank();
			}
		}
	}

	private void confirmHasController(World world, int x, int y, int z) {
		BlockArray blocks = new BlockArray();
		List<Block> li = Arrays.asList(this, ChromaTiles.TANK.getBlock());
		blocks.recursiveAddMultipleWithBounds(world, x, y, z, li, x-32, y-32, z-32, x+32, y+32, z+32);
		TileEntityCrystalTank con = null;
		int count = 0;
		for (int i = 0; i < blocks.getSize(); i++) {
			int[] xyz = blocks.getNthBlock(i);
			int dx = xyz[0];
			int dy = xyz[1];
			int dz = xyz[2];
			ChromaTiles c = ChromaTiles.getTile(world, dx, dy, dz);
			if (c == ChromaTiles.TANK) {
				count++;
			}
		}
		if (count != 1) {
			TileEntity te = world.getTileEntity(x, y, z);
			if (te instanceof CrystalTankAuxTile) {
				((CrystalTankAuxTile)te).removeFromTank();
			}
			for (int i = 0; i < blocks.getSize(); i++) {
				int[] xyz = blocks.getNthBlock(i);
				int dx = xyz[0];
				int dy = xyz[1];
				int dz = xyz[2];
				CrystalTankAuxTile te2 = (CrystalTankAuxTile)world.getTileEntity(dx, dy, dz);
				if (te2 != null) {
					te2.removeFromTank();
				}
			}
		}
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block old, int oldmeta) {
		CrystalTankAuxTile te = (CrystalTankAuxTile)world.getTileEntity(x, y, z);
		if (te != null) {
			te.removeFromTank();
		}
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			this.confirmHasController(world, dx, dy, dz);
		}
		super.breakBlock(world, x, y, z, old, oldmeta);
	}

	public static class CrystalTankAuxTile extends TileEntity implements IFluidHandler {

		private int tileX = Integer.MIN_VALUE;
		private int tileY = Integer.MIN_VALUE;
		private int tileZ = Integer.MIN_VALUE;

		@Override
		public boolean canUpdate() {
			return false;
		}

		public void setTile(TileEntityCrystalTank te) {
			tileX = te.xCoord;
			tileY = te.yCoord;
			tileZ = te.zCoord;
		}

		public void addToTank() {
			TileEntityCrystalTank te = this.getTankController();
			if (te != null)
				te.addCoordinate(xCoord, yCoord, zCoord);
		}

		public void removeFromTank() {
			TileEntityCrystalTank te = this.getTankController();
			if (te != null)
				te.removeCoordinate(xCoord, yCoord, zCoord);
			this.reset();
		}

		public void reset() {
			tileX = tileY = tileZ = Integer.MIN_VALUE;
			worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 0, 3);
		}

		public boolean hasTile() {
			return tileY != Integer.MIN_VALUE && this.getTankController() != null;
		}

		public TileEntityCrystalTank getTankController() {
			TileEntity te = worldObj.getTileEntity(tileX, tileY, tileZ);
			return te instanceof TileEntityCrystalTank ? (TileEntityCrystalTank)te : null;
		}

		@Override
		public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
			TileEntityCrystalTank te = this.getTankController();
			return te != null ? te.fill(from, resource, doFill) : 0;
		}

		@Override
		public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
			TileEntityCrystalTank te = this.getTankController();
			return te != null ? te.drain(from, resource, doDrain) : null;
		}

		@Override
		public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
			TileEntityCrystalTank te = this.getTankController();
			return te != null ? te.drain(from, maxDrain, doDrain) : null;
		}

		@Override
		public boolean canFill(ForgeDirection from, Fluid fluid) {
			TileEntityCrystalTank te = this.getTankController();
			return te != null && te.canFill(from, fluid);
		}

		@Override
		public boolean canDrain(ForgeDirection from, Fluid fluid) {
			TileEntityCrystalTank te = this.getTankController();
			return te != null && te.canDrain(from, fluid);
		}

		@Override
		public FluidTankInfo[] getTankInfo(ForgeDirection from) {
			TileEntityCrystalTank te = this.getTankController();
			return te != null ? te.getTankInfo(from) : new FluidTankInfo[0];
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);

			NBT.setInteger("tx", tileX);
			NBT.setInteger("ty", tileY);
			NBT.setInteger("tz", tileZ);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);

			tileX = NBT.getInteger("tx");
			tileY = NBT.getInteger("ty");
			tileZ = NBT.getInteger("tz");
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

	@Override
	@ModDependent(ModList.WAILA)
	public ItemStack getWailaStack(IWailaDataAccessor acc, IWailaConfigHandler cfg) {
		return new ItemStack(this);
	}

	@Override
	@ModDependent(ModList.WAILA)
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor acc, IWailaConfigHandler config) {
		World world = acc.getWorld();
		MovingObjectPosition mov = acc.getPosition();
		if (mov != null) {
			int x = mov.blockX;
			int y = mov.blockY;
			int z = mov.blockZ;
			currenttip.add(EnumChatFormatting.WHITE+this.getPickBlock(mov, world, x, y, z).getDisplayName());
		}
		return currenttip;
	}

	@Override
	@ModDependent(ModList.WAILA)
	public List<String> getWailaBody(ItemStack is, List<String> currenttip, IWailaDataAccessor acc, IWailaConfigHandler cfg) {
		CrystalTankAuxTile te = (CrystalTankAuxTile)acc.getTileEntity();
		TileEntityCrystalTank tank = te.getTankController();
		if (tank != null) {
			tank.syncAllData(false);
			int amt = tank.getLevel();
			int capacity = tank.getCapacity();
			Fluid f = tank.getFluid();
			if (amt > 0 && f != null) {
				currenttip.add(String.format("Tank: %dmB/%dmB of %s", amt, capacity, f.getLocalizedName()));
			}
			else {
				currenttip.add(String.format("Tank: Empty (Capacity %dmB)", capacity));
			}
		}
		else {
			currenttip.add("No Tank");
		}
		return currenttip;
	}

	@Override
	@ModDependent(ModList.WAILA)
	public List<String> getWailaTail(ItemStack is, List<String> currenttip, IWailaDataAccessor acc, IWailaConfigHandler cfg) {
		String s1 = EnumChatFormatting.ITALIC.toString();
		String s2 = EnumChatFormatting.BLUE.toString();
		currenttip.add(s2+s1+"ChromatiCraft");
		return currenttip;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess iba, int x, int y, int z, int side) {
		ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[side];
		int dx = x+dir.offsetX;
		int dy = y+dir.offsetY;
		int dz = z+dir.offsetZ;
		return iba.getBlock(dx, dy, dz) != this && ChromaTiles.getTile(iba, dx, dy, dz) != ChromaTiles.TANK;
	}

	public ArrayList<Integer> getEdgesForFace(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
		ArrayList<Integer> li = new ArrayList();
		li.addAll(allDirs);

		if (world.getBlockMetadata(x, y, z) == 0)
			return li;

		if (face.offsetX != 0) { //test YZ
			//sides; removed if have adjacent on side
			if (world.getBlock(x, y, z+1) == this || ChromaTiles.getTile(world, x, y, z+1) == ChromaTiles.TANK)
				li.remove(new Integer(2));
			if (world.getBlock(x, y, z-1) == this || ChromaTiles.getTile(world, x, y, z-1) == ChromaTiles.TANK)
				li.remove(new Integer(8));
			if (world.getBlock(x, y+1, z) == this || ChromaTiles.getTile(world, x, y+1, z) == ChromaTiles.TANK)
				li.remove(new Integer(4));
			if (world.getBlock(x, y-1, z) == this || ChromaTiles.getTile(world, x, y-1, z) == ChromaTiles.TANK)
				li.remove(new Integer(6));

			//Corners; only removed if have adjacent on side AND corner
			if (world.getBlock(x, y+1, z+1) == this && !li.contains(4) && !li.contains(2))
				li.remove(new Integer(1));
			if (world.getBlock(x, y-1, z-1) == this && !li.contains(6) && !li.contains(8))
				li.remove(new Integer(9));
			if (world.getBlock(x, y+1, z-1) == this && !li.contains(4) && !li.contains(8))
				li.remove(new Integer(7));
			if (world.getBlock(x, y-1, z+1) == this && !li.contains(2) && !li.contains(6))
				li.remove(new Integer(3));
		}
		if (face.offsetY != 0) { //test XZ
			//sides; removed if have adjacent on side
			if (world.getBlock(x, y, z+1) == this || ChromaTiles.getTile(world, x, y, z+1) == ChromaTiles.TANK)
				li.remove(new Integer(2));
			if (world.getBlock(x, y, z-1) == this || ChromaTiles.getTile(world, x, y, z-1) == ChromaTiles.TANK)
				li.remove(new Integer(8));
			if (world.getBlock(x+1, y, z) == this || ChromaTiles.getTile(world, x+1, y, z) == ChromaTiles.TANK)
				li.remove(new Integer(4));
			if (world.getBlock(x-1, y, z) == this || ChromaTiles.getTile(world, x-1, y, z) == ChromaTiles.TANK)
				li.remove(new Integer(6));

			//Corners; only removed if have adjacent on side AND corner
			if (world.getBlock(x+1, y, z+1) == this && !li.contains(4) && !li.contains(2))
				li.remove(new Integer(1));
			if (world.getBlock(x-1, y, z-1) == this && !li.contains(6) && !li.contains(8))
				li.remove(new Integer(9));
			if (world.getBlock(x+1, y, z-1) == this && !li.contains(4) && !li.contains(8))
				li.remove(new Integer(7));
			if (world.getBlock(x-1, y, z+1) == this && !li.contains(2) && !li.contains(6))
				li.remove(new Integer(3));
		}
		if (face.offsetZ != 0) { //test XY
			//sides; removed if have adjacent on side
			if (world.getBlock(x, y+1, z) == this || ChromaTiles.getTile(world, x, y+1, z) == ChromaTiles.TANK)
				li.remove(new Integer(4));
			if (world.getBlock(x, y-1, z) == this || ChromaTiles.getTile(world, x, y-1, z) == ChromaTiles.TANK)
				li.remove(new Integer(6));
			if (world.getBlock(x+1, y, z) == this || ChromaTiles.getTile(world, x+1, y, z) == ChromaTiles.TANK)
				li.remove(new Integer(2));
			if (world.getBlock(x-1, y, z) == this || ChromaTiles.getTile(world, x-1, y, z) == ChromaTiles.TANK)
				li.remove(new Integer(8));

			//Corners; only removed if have adjacent on side AND corner
			if (world.getBlock(x+1, y+1, z) == this && !li.contains(2) && !li.contains(4))
				li.remove(new Integer(1));
			if (world.getBlock(x-1, y-1, z) == this && !li.contains(8) && !li.contains(6))
				li.remove(new Integer(9));
			if (world.getBlock(x+1, y-1, z) == this && !li.contains(2) && !li.contains(6))
				li.remove(new Integer(3));
			if (world.getBlock(x-1, y+1, z) == this && !li.contains(4) && !li.contains(8))
				li.remove(new Integer(7));
		}

		if (!li.contains(2) && !li.contains(4) && !li.contains(6) && !li.contains(8) && (x+z)%2 != 0) {
			li.remove(new Integer(5)); //glass tex
		}

		return li;
	}

	public IIcon getIconForEdge(int edge) {
		return edges[edge];
	}

	@Override
	public boolean renderCentralTextureForItem(int meta) {
		return false;
	}

}
