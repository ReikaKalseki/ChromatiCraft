package Reika.ChromatiCraft.Items.Tools;

import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.ChromatiCraft.Block.BlockCrystalTank.CrystalTankAuxTile;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Interfaces.Block.MachineRegistryBlock;
import Reika.DragonAPI.Interfaces.Registry.TileEnum;
import Reika.DragonAPI.Interfaces.TileEntity.PartialTank;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModRegistry.InterfaceCache;

import buildcraft.transport.TileGenericPipe;

public class ItemEnderBucket extends ItemChromaTool {

	public ItemEnderBucket(int index) {
		super(index);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer ep) {
		MovingObjectPosition mov = this.getMovingObjectPositionFromPlayer(world, ep, true);
		if (mov != null && mov.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
			if (this.getMode(is) == BucketMode.PICKUP) {
				if (BucketMode.PICKUP.process(world, mov.blockX, mov.blockY, mov.blockZ, this.getActiveLink(is, ep))) {
					ReikaSoundHelper.playSoundFromServerAtBlock(world, mov.blockX, mov.blockY, mov.blockZ, "game.neutral.swim", 0.7F, 0.7F+0.3F*world.rand.nextFloat(), true);
				}
			}
			return is;
		}
		else {
			if (ep.isSneaking()) {
				is.setItemDamage((is.getItemDamage()+1)%BucketMode.list.length);
			}
			else {
				ep.openGui(ChromatiCraft.instance, ChromaGuis.ENDERBUCKET.ordinal(), world, 0, 0, 0);
			}
			return is;
		}
	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World world, int x, int y, int z, int s, float a, float b, float c) {
		if (!world.isRemote) {
			if (ep.isSneaking()) {
				TileEntity te = world.getTileEntity(x, y, z);
				if (te instanceof IFluidHandler) {
					this.addFluidHandler(te, ep, is);
					return true;
				}
			}
			else if (this.getMode(is) == BucketMode.PLACE) {
				ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[s];
				x += dir.offsetX;
				y += dir.offsetY;
				z += dir.offsetZ;
				Material material = world.getBlock(x, y, z).getMaterial();
				boolean flag = !material.isSolid();

				if (!world.isAirBlock(x, y, z) && !flag) {

				}
				else {
					if (BucketMode.PLACE.process(world, x, y, z, this.getActiveLink(is, ep))) {
						ReikaSoundHelper.playSoundFromServerAtBlock(world, x, y, z, "game.neutral.swim", 0.7F, 0.7F+0.3F*world.rand.nextFloat(), true);
						if (!world.isRemote && flag && !material.isLiquid()) {
							//world.func_147480_a(x, y, z, true);
						}
					}
				}
				return true;
			}
		}
		return false;
	}

	public BucketMode getMode(ItemStack is) {
		return BucketMode.list[is.getItemDamage()];
	}

	public TankLink getActiveLink(ItemStack is, EntityPlayer ep) {
		if (is.stackTagCompound == null)
			return null;
		ArrayList<TankLink> li = this.getLinks(is, ep);
		int idx = is.stackTagCompound.hasKey("selected") ? is.stackTagCompound.getInteger("selected") : -1;
		return idx >= 0 && idx < li.size() ? li.get(idx) : null;
	}

	public ArrayList<TankLink> getLinks(ItemStack is, EntityPlayer ep) {
		if (is.stackTagCompound == null)
			return new ArrayList();
		ArrayList<TankLink> ret = new ArrayList();
		NBTTagList li = is.stackTagCompound.getTagList("links", NBTTypes.COMPOUND.ID);
		Iterator<Object> it = li.tagList.iterator();
		while (it.hasNext()) {
			NBTTagCompound tag = (NBTTagCompound)it.next();
			TankLink tl = TankLink.createFromTag(tag);
			if (ep.worldObj.isRemote || tl.isValid()) {
				ret.add(tl);
			}
			else {
				it.remove();
			}
		}
		return ret;
	}

	private void addFluidHandler(TileEntity te, EntityPlayer ep, ItemStack is) {
		if (te instanceof CrystalTankAuxTile)
			te = ((CrystalTankAuxTile)te).getTankController();
		if (te instanceof PartialTank) {
			if (!((PartialTank)te).hasTank())
				te = null;
		}
		if (InterfaceCache.BCPIPE.instanceOf(te)) {
			if (!(((TileGenericPipe)te).pipe.transport instanceof IFluidHandler))
				te = null;
		}
		if (te == null)
			return;
		if (is.stackTagCompound == null)
			is.stackTagCompound = new NBTTagCompound();
		ArrayList<TankLink> set = this.getLinks(is, ep);
		for (TankLink tl : set) {
			if (tl.tank.equals(te.worldObj, te.xCoord, te.yCoord, te.zCoord))
				return;
		}
		NBTTagList li = is.stackTagCompound.getTagList("links", NBTTypes.COMPOUND.ID);
		TankLink tl = new TankLink(te);
		li.appendTag(tl.writeTag());
		is.stackTagCompound.setTag("links", li);
	}

	public void setLinkIndex(ItemStack is, int idx) {
		if (is.stackTagCompound == null)
			is.stackTagCompound = new NBTTagCompound();
		is.stackTagCompound.setInteger("selected", idx);
	}

	@Override
	public int getItemSpriteIndex(ItemStack item) {
		return super.getItemSpriteIndex(item)+item.getItemDamage();
	}

	public static enum BucketMode {
		PICKUP("Pickup"),
		PLACE("Place");

		public final String displayName;

		public static final BucketMode[] list = values();

		private BucketMode(String s) {
			displayName = s;
		}

		public boolean process(World world, int x, int y, int z, TankLink link) {
			if (link == null)
				return false;
			if (world.isRemote)
				return true;
			switch(this) {
				case PICKUP:
					FluidStack fs = ReikaWorldHelper.getDrainableFluid(world, x, y, z);
					if (fs != null && link.tryAddFluid(fs, false)) {
						link.tryAddFluid(fs, true);
						world.setBlock(x, y, z, Blocks.air);
						return true;
					}
					break;
				case PLACE:
					Fluid f = link.getCurrentFluidToDrain(false);
					if (f != null) {
						link.getCurrentFluidToDrain(true);
						world.setBlock(x, y, z, f.getBlock(), 0, 3);
						world.markBlockForUpdate(x, y, z);
						world.getBlock(x, y, z).onNeighborBlockChange(world, x, y, z, f.getBlock());
						return true;
					}
					break;
			}
			return false;
		}
	}

	public static class TankLink {

		public final WorldLocation tank;
		private final BlockKey cachedBlock;
		private ItemStack cachedIcon;

		private TankLink(TileEntity te) {
			this(new WorldLocation(te), new BlockKey(te.worldObj.getBlock(te.xCoord, te.yCoord, te.zCoord), te.worldObj.getBlockMetadata(te.xCoord, te.yCoord, te.zCoord)));
		}

		private TankLink(WorldLocation loc, BlockKey bk) {
			tank = loc;
			cachedBlock = bk;
			cachedIcon = this.calcItem();
		}

		public boolean isValid() {
			return cachedBlock != null && tank.getBlockKey().equals(cachedBlock);
		}

		public IFluidHandler getTank() {
			TileEntity te = tank != null ? tank.getTileEntity() : null;
			return te instanceof IFluidHandler ? (IFluidHandler)te : null;
		}

		private ItemStack calcItem() {
			Block b = tank.getBlock();
			int meta = tank.getBlockMetadata();
			if (b instanceof MachineRegistryBlock) {
				TileEnum te = ((MachineRegistryBlock)b).getMachine(tank.getWorld(), tank.xCoord, tank.yCoord, tank.zCoord);
				return te.getCraftedProduct(tank.getTileEntity());
			}
			else if (InterfaceCache.BCPIPEBLOCK.instanceOf(b)) {
				return b.getDrops(tank.getWorld(), tank.xCoord, tank.yCoord, tank.zCoord, tank.getBlockMetadata(), 0).get(0);
			}
			return b == Blocks.air ? null : new ItemStack(b, 1, meta);
		}

		public ItemStack getIcon() {
			if (cachedIcon == null)
				cachedIcon = this.calcItem();
			return cachedIcon != null ? cachedIcon.copy() : null;
		}

		public String getDisplayName() {
			ItemStack is = this.getIcon();
			String s = is != null ? is.getDisplayName() : tank.toString();
			return s;
		}

		public Fluid getCurrentFluidToDrain(boolean doDrain) {
			IFluidHandler ifl = this.getTank();
			if (ifl == null)
				return null;
			for (int i = 0; i < 6; i++) {
				ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
				FluidStack fs = ifl.drain(dir, FluidContainerRegistry.BUCKET_VOLUME, doDrain);
				if (fs != null && fs.amount >= FluidContainerRegistry.BUCKET_VOLUME) {
					return fs.getFluid();
				}
			}
			return null;
		}

		public boolean tryAddFluid(FluidStack fs, boolean doAdd) {
			IFluidHandler ifl = this.getTank();
			if (ifl == null)
				return false;
			for (int i = 0; i < 6; i++) {
				ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
				int added = ifl.fill(dir, fs, doAdd);
				if (added >= fs.amount) {
					return true;
				}
			}
			return false;
		}

		public NBTTagCompound writeTag() {
			NBTTagCompound tag = tank.writeToTag();
			cachedBlock.writeToNBT("cache", tag);
			if (cachedIcon != null) {
				NBTTagCompound item = new NBTTagCompound();
				cachedIcon.writeToNBT(item);
				tag.setTag("item", item);
			}
			return tag;
		}

		public static TankLink createFromTag(NBTTagCompound tag) {
			WorldLocation loc = WorldLocation.readTag(tag);
			BlockKey bk = BlockKey.readFromNBT("cache", tag);
			TankLink lk = new TankLink(loc, bk);
			if (tag.hasKey("item")) {
				NBTTagCompound item = tag.getCompoundTag("item");
				ItemStack is = ItemStack.loadItemStackFromNBT(item);
				lk.cachedIcon = is;
			}
			return lk;
		}

	}

}
