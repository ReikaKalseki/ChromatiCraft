package Reika.ChromatiCraft.Block;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
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
import Reika.ChromatiCraft.Base.CrystalTypeBlock;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.ISBRH.CrystalEncrustingRenderer;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class BlockEncrustedCrystal extends CrystalTypeBlock {

	public static IIcon basicIcon;
	public static IIcon specialIcon;

	public BlockEncrustedCrystal(Material mat) {
		super(mat);
		if (DragonAPICore.isReikasComputer())
			this.setCreativeTab(ChromatiCraft.tabChromaGen);
		else
			this.setCreativeTab(null);
		this.setHardness(0.6F);
		this.setResistance(5F);
		//this.setTickRandomly(true);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase e, ItemStack is) {
		super.onBlockPlacedBy(world, x, y, z, e, is);

		TileCrystalEncrusted te = (TileCrystalEncrusted)world.getTileEntity(x, y, z);
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			if (CrystalGrowth.canExist(world, x, y, z, dir))
				te.addGrowth(dir, 1+world.rand.nextInt(8));
		}
		te.markReady();
		te.updateSides(world, x, y, z);
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		TileCrystalEncrusted te = (TileCrystalEncrusted)world.getTileEntity(x, y, z);
		te.markReady();
		te.grow(world, x, y, z);
		te.updateSides(world, x, y, z);
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
	public boolean hasTileEntity(int meta) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return new TileCrystalEncrusted();
	}

	@Override
	public int getBrightness(IBlockAccess iba, int x, int y, int z) {
		return 0;
	}

	@Override
	public int getRenderBlockPass() {
		return 1;
	}

	@Override
	public boolean canRenderInPass(int pass) {
		CrystalEncrustingRenderer.renderPass = pass;
		return pass <= 1;
	}

	@Override
	public final int getRenderType() {
		return ChromatiCraft.proxy.encrustedRender;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return null;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block b) {
		TileCrystalEncrusted te = (TileCrystalEncrusted)world.getTileEntity(x, y, z);
		te.updateSides(world, x, y, z);
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		basicIcon = ico.registerIcon("chromaticraft:crystal/encrusted");
		specialIcon = ico.registerIcon("chromaticraft:crystal/encrusted_special");
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return basicIcon;
	}

	@Override
	public IIcon getIcon(IBlockAccess iba, int x, int y, int z, int side) {
		//if (true)
		//	return specialIcon;
		TileEntity te = iba.getTileEntity(x, y, z);
		if (te instanceof TileCrystalEncrusted) {
			TileCrystalEncrusted tile = (TileCrystalEncrusted)te;
			return tile.isSpecial() ? specialIcon : basicIcon;
		}
		return basicIcon;
	}

	@Override
	public final boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean harvest) {
		if (!player.capabilities.isCreativeMode)
			this.harvestBlock(world, player, x, y, z, world.getBlockMetadata(x, y, z));
		return world.setBlockToAir(x, y, z);
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune) {
		ArrayList<ItemStack> ret = new ArrayList();
		TileCrystalEncrusted te = (TileCrystalEncrusted)world.getTileEntity(x, y, z);
		if (te != null) {
			for (CrystalGrowth g : te.getGrowths()) {
				ret.add(g.getDrop(world.rand));
			}
		}
		return ret;
	}

	@Override
	public final boolean canSilkHarvest(World world, EntityPlayer player, int x, int y, int z, int metadata) {
		return false;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess iba, int x, int y, int z) {
		super.setBlockBoundsBasedOnState(iba, x, y, z);

		TileEntity te = iba.getTileEntity(x, y, z);
		if (te instanceof TileCrystalEncrusted) {
			TileCrystalEncrusted tile = (TileCrystalEncrusted)te;
			HashSet<ForgeDirection> set = tile.getSides();
			float x1 = 0.5F;
			float x2 = 0.5F;
			float y1 = 0.5F;
			float y2 = 0.5F;
			float z1 = 0.5F;
			float z2 = 0.5F;
			if (set.contains(ForgeDirection.UP) || set.contains(ForgeDirection.DOWN)) {
				x1 = 0;
				x2 = 1;
				z1 = 0;
				z2 = 1;
			}
			if (set.contains(ForgeDirection.EAST) || set.contains(ForgeDirection.WEST)) {
				y1 = 0;
				y2 = 1;
				z1 = 0;
				z2 = 1;
			}
			if (set.contains(ForgeDirection.NORTH) || set.contains(ForgeDirection.SOUTH)) {
				x1 = 0;
				x2 = 1;
				y1 = 0;
				y2 = 1;
			}
			if (set.contains(ForgeDirection.UP)) {
				y2 = 1;
			}
			if (set.contains(ForgeDirection.DOWN)) {
				y1 = 0;
			}
			if (set.contains(ForgeDirection.EAST)) {
				x2 = 1;
			}
			if (set.contains(ForgeDirection.WEST)) {
				x1 = 0;
			}
			if (set.contains(ForgeDirection.SOUTH)) {
				z2 = 1;
			}
			if (set.contains(ForgeDirection.NORTH)) {
				z1 = 0;
			}
			this.setBlockBounds(x1, y1, z1, x2, y2, z2);
		}
		else {
			this.setBlockBounds(0, 0, 0, 1, 1, 1);
		}
	}

	public static void setColor(World world, int x, int y, int z, CrystalElement e) {
		world.setBlockMetadataWithNotify(x, y, z, e.ordinal(), 3);
		TileCrystalEncrusted te = (TileCrystalEncrusted)world.getTileEntity(x, y, z);
		te.setColor(e);
	}

	public static class TileCrystalEncrusted extends TileEntity {

		private CrystalGrowth[] sides = new CrystalGrowth[6];
		private boolean isSpecial;
		private boolean isReadyToUpdate;

		@Override
		public boolean canUpdate() {
			return false;
		}

		public void setColor(CrystalElement e) {
			for (int i = 0; i < sides.length; i++) {
				if (sides[i] != null)
					sides[i] = sides[i].getWithColor(e);
			}
		}

		public void markReady() {
			isReadyToUpdate = true;
		}

		public void makeSpecial() {
			isSpecial = true;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}

		public boolean isSpecial() {
			return isSpecial;
		}

		public boolean grow(World world, int x, int y, int z) {
			ding(world, x, y, z);
			if (world.rand.nextInt(4) > 0) {
				for (int i = 0; i < 6; i++) {
					CrystalGrowth g = sides[i];
					if (g != null) {
						if (g.grow(world, x, y, z))
							return true;
					}
				}
			}
			for (int i = 0; i < 6; i++) {
				CrystalGrowth g = sides[i];
				if (g == null) {
					ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
					if (CrystalGrowth.canExist(world, x, y, z, dir)) {
						this.addGrowth(dir);
						return true;
					}
				}
			}
			return false;
		}

		public void updateSides(World world, int x, int y, int z) {
			if (!isReadyToUpdate)
				return;
			for (int i = 0; i < 6; i++) {
				CrystalGrowth g = sides[i];
				if (g != null) {
					if (!g.canExist(world, x, y, z, ForgeDirection.VALID_DIRECTIONS[i])) {
						ItemStack drop = g.getDrop(world.rand);
						ReikaItemHelper.dropItem(world, x+world.rand.nextDouble(), y+world.rand.nextDouble(), z+world.rand.nextDouble(), drop);
						sides[i] = null;
					}
				}
			}
			if (this.getGrowths().isEmpty())
				world.setBlockToAir(x, y, z);
			world.markBlockForUpdate(x, y, z);
		}

		public void addGrowth(ForgeDirection dir) {
			this.addGrowth(dir, 0);
		}

		public void addGrowth(ForgeDirection dir, int amt) {
			sides[dir.ordinal()] = new CrystalGrowth(this.getColor(), dir, isSpecial);
			sides[dir.ordinal()].setGrowth(worldObj, xCoord, yCoord, zCoord, amt);
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}

		public CrystalElement getColor() {
			return CrystalElement.elements[this.getBlockMetadata()];
		}

		public CrystalElement getInternalColor() {
			return CrystalElement.elements[this.getBlockMetadata()];
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);

			for (int i = 0; i < 6; i++) {
				CrystalGrowth g = sides[i];
				if (g != null) {
					NBTTagCompound tag = new NBTTagCompound();
					g.writeToNBT(tag);
					NBT.setTag("side_"+i, tag);
				}
			}

			NBT.setBoolean("special", isSpecial);
			NBT.setBoolean("ready", isReadyToUpdate);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);

			for (int i = 0; i < 6; i++) {
				sides[i] = null;
				if (NBT.hasKey("side_"+i)) {
					NBTTagCompound tag = NBT.getCompoundTag("side_"+i);
					sides[i] = CrystalGrowth.readFromNBT(tag);
				}
			}

			isSpecial = NBT.getBoolean("special");
			isReadyToUpdate = NBT.getBoolean("ready");
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

		public HashSet<ForgeDirection> getSides() {
			HashSet<ForgeDirection> set = new HashSet();
			for (int i = 0; i < 6; i++) {
				if (sides[i] != null)
					set.add(ForgeDirection.VALID_DIRECTIONS[i]);
			}
			return set;
		}

		public Collection<CrystalGrowth> getGrowths() {
			ArrayList<CrystalGrowth> li = new ArrayList();
			for (int i = 0; i < 6; i++) {
				if (sides[i] != null)
					li.add(sides[i]);
			}
			return li;
		}

	}

	public static class CrystalGrowth {

		public final ForgeDirection side;
		public final CrystalElement color;
		public final boolean special;
		private int growthStage;

		private CrystalGrowth(CrystalElement e, ForgeDirection dir, boolean sp) {
			side = dir;
			color = e;
			special = sp;
		}

		public CrystalGrowth getWithColor(CrystalElement e) {
			CrystalGrowth gr = new CrystalGrowth(e, side, special);
			gr.growthStage = growthStage;
			return gr;
		}

		private void setGrowth(World world, int x, int y, int z, int amt) {
			growthStage = Math.min(amt, this.getMaxGrowth(world, x, y, z));
		}

		public boolean grow(World world, int x, int y, int z) {
			if (growthStage < this.getMaxGrowth(world, x, y, z)) {
				growthStage++;
				world.markBlockForUpdate(x, y, z);
				return true;
			}
			return false;
		}

		private int getMaxGrowth(World world, int x, int y, int z) {
			return 6;
		}

		public ItemStack getDrop(Random rand) {
			int amt = Math.max(1+growthStage, rand.nextInt(growthStage*2+1));
			return ReikaItemHelper.getSizedItemStack(ChromaItems.SHARD.getStackOf(color), amt);
		}

		public static boolean canExist(World world, int x, int y, int z, ForgeDirection side) {
			int dx = x+side.offsetX;
			int dy = y+side.offsetY;
			int dz = z+side.offsetZ;
			Block b = world.getBlock(dx, dy, dz);
			return b == ChromaBlocks.STRUCTSHIELD.getBlockInstance() || b.isSideSolid(world, dx, dy, dz, side.getOpposite());
		}

		private void writeToNBT(NBTTagCompound NBT) {
			NBT.setInteger("grow", growthStage);
			NBT.setBoolean("special", special);
			NBT.setInteger("dir", side != null ? side.ordinal() : -1);
			NBT.setInteger("color", color.ordinal());
		}

		private static CrystalGrowth readFromNBT(NBTTagCompound NBT) {
			int dir = NBT.getInteger("dir");
			int elem = NBT.getInteger("color");
			ForgeDirection face = dir >= 0 ? ForgeDirection.VALID_DIRECTIONS[dir] : null;
			CrystalGrowth g = new CrystalGrowth(CrystalElement.elements[elem], face, NBT.getBoolean("special"));
			g.growthStage = NBT.getInteger("grow");
			return g;
		}

		public int getGrowth() {
			return growthStage;
		}

	}

}
