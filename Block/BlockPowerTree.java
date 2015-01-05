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
import java.util.List;
import java.util.Random;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.TileEntityPowerTree;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;

@Strippable(value = {"mcp.mobius.waila.api.IWailaDataProvider"})
public class BlockPowerTree extends Block implements IWailaDataProvider {

	private static final Random rand = new Random();

	public BlockPowerTree(Material mat) {
		super(mat);
		this.setHardness(20);
		this.setResistance(6000);
		this.setLightLevel(1);
		stepSound = soundTypeGlass;
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return new TileEntityPowerTreeAux();
	}

	private TileEntityPowerTree getTile(World world, int x, int y, int z) {
		ForgeDirection dir = TileEntityPowerTree.getDirection(this.getColor(world, x, y, z));
		for (int i = 1; i < 32; i++) {
			int dx = x+dir.offsetX*i;
			int dy = y+dir.offsetY*i;
			int dz = z+dir.offsetZ*i;
			ChromaTiles c = ChromaTiles.getTile(world, dx, dy, dz);
			if (c == ChromaTiles.POWERTREE) {
				return (TileEntityPowerTree)world.getTileEntity(dx, dy, dz);
			}
			else if (world.getBlock(dx, dy, dz) != this)
				return null;
		}
		return null;
	}

	public CrystalElement getColor(IBlockAccess world, int x, int y, int z) {
		return CrystalElement.elements[world.getBlockMetadata(x, y, z)];
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		blockIcon = ico.registerIcon("chromaticraft:basic/powertree5");
	}

	@Override
	public int getRenderColor(int meta) {
		return CrystalElement.elements[meta].getColor();
	}

	@Override
	public int colorMultiplier(IBlockAccess iba, int x, int y, int z) {
		return this.getColor(iba, x, y, z).getColor();
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block old, int oldmeta) {
		TileEntityPowerTreeAux te = (TileEntityPowerTreeAux)world.getTileEntity(x, y, z);
		TileEntityPowerTree tree = te.getCenter();
		if (tree != null) {
			tree.onBreakLeaf(world, x, y, z, CrystalElement.elements[oldmeta]);
		}
		super.breakBlock(world, x, y, z, old, oldmeta);
	}

	@Override
	public int damageDropped(int meta) {
		return meta;
	}

	@Override
	public int getRenderType() {
		return ChromatiCraft.proxy.treeRender;
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
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune) {
		ArrayList<ItemStack> li = new ArrayList();
		int n = (1+fortune/2)*rand.nextInt(1+rand.nextInt(4)+2*fortune);
		CrystalElement e = CrystalElement.elements[meta];
		for (int i = 0; i < n; i++) {
			ItemStack is = rand.nextInt(3) == 0 ? ChromaStacks.getChargedShard(e) : ChromaStacks.getShard(e);
			li.add(is);
		}
		return li;
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		return new ItemStack(this, 1, meta);
	}

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor acc, IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack is, List<String> tip, IWailaDataAccessor acc, IWailaConfigHandler config) {
		return tip;
	}

	@Override
	public List<String> getWailaBody(ItemStack is, List<String> tip, IWailaDataAccessor acc, IWailaConfigHandler config) {
		return tip;
	}

	@Override
	public List<String> getWailaTail(ItemStack is, List<String> tip, IWailaDataAccessor acc, IWailaConfigHandler config) {
		return tip;
	}

	@Override
	@ModDependent(ModList.WAILA)
	public final NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z) {
		return tag;
	}

	public static class TileEntityPowerTreeAux extends TileEntity {

		private int growth = 0;
		private ForgeDirection direction;

		private int originX = Integer.MIN_VALUE;
		private int originY = Integer.MIN_VALUE;
		private int originZ = Integer.MIN_VALUE;

		public static final int MAX_GROWTH = 12;

		@Override
		public boolean canUpdate() {
			return false;
		}

		public boolean grow() {
			if (growth < MAX_GROWTH) {
				growth++;
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
				return true;
			}
			return false;
		}

		public boolean ungrow() {
			if (growth > 0) {
				growth--;
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
				return true;
			}
			return false;
		}

		public int getGrowth() {
			return growth;
		}

		public ForgeDirection getDirection() {
			return direction;
		}

		public void setDirection(ForgeDirection dir) {
			direction = dir;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}

		public void setOrigin(TileEntityPowerTree te) {
			originX = te.xCoord;
			originY = te.yCoord;
			originZ = te.zCoord;
		}

		public TileEntityPowerTree getCenter() {
			ChromaTiles c = ChromaTiles.getTile(worldObj, originX, originY, originZ);
			return c == ChromaTiles.POWERTREE ? (TileEntityPowerTree)worldObj.getTileEntity(originX, originY, originZ) : null;
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);

			NBT.setInteger("tx", originX);
			NBT.setInteger("ty", originY);
			NBT.setInteger("tz", originZ);

			NBT.setInteger("grow", growth);

			NBT.setInteger("dir", direction != null ? direction.ordinal() : -1);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);

			originX = NBT.getInteger("tx");
			originY = NBT.getInteger("ty");
			originZ = NBT.getInteger("tz");

			growth = NBT.getInteger("grow");

			int dir = NBT.getInteger("dir");
			direction = dir >= 0 ? ForgeDirection.VALID_DIRECTIONS[dir] : null;
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
