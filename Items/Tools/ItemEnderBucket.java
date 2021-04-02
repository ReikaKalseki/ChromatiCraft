package Reika.ChromatiCraft.Items.Tools;

import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.block.Block;
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
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.ChromatiCraft.Block.BlockCrystalTank.CrystalTankAuxTile;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Interfaces.Block.MachineRegistryBlock;
import Reika.DragonAPI.Interfaces.Registry.TileEnum;
import Reika.DragonAPI.Interfaces.TileEntity.PartialTank;
import Reika.DragonAPI.Libraries.ReikaFluidHelper;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;
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
			if (!world.isRemote)
				this.actAt(world, mov.blockX, mov.blockY, mov.blockZ, mov.sideHit, ep, is);
			return is;
		}
		else {
			if (ep.isSneaking()) {
				if (!world.isRemote) {
					this.stepMode(is);
					ReikaChatHelper.sendChatToPlayer(ep, "Bucket now in "+this.getMode(is).displayName+" mode.");
				}
			}
			else {
				ep.openGui(ChromatiCraft.instance, ChromaGuis.ENDERBUCKET.ordinal(), world, 0, 0, 0);
			}
			return is;
		}
	}

	@Override
	public boolean onItemUseFirst(ItemStack is, EntityPlayer ep, World world, int x, int y, int z, int s, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			return this.actAt(world, x, y, z, s, ep, is);
		}
		return false;
	}

	public void stepMode(ItemStack is) {
		is.setItemDamage((is.getItemDamage()+1)%BucketMode.list.length);
	}

	private boolean actAt(World world, int x, int y, int z, int s, EntityPlayer ep, ItemStack is) {
		ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[s];
		BucketMode mode = this.getMode(is);
		Operation o = this.getOperation(world, x, y, z, ep, mode);
		if (o != null) {
			if (o.apply(world, x, y, z, dir, ep, is, this.getActiveLink(is, ep))) {
				if (o == Operation.ADDTANK)
					ChromaSounds.USE.playSoundAtBlock(world, x, y, z);
				else
					ReikaSoundHelper.playSoundFromServerAtBlock(world, x, y, z, "game.neutral.swim", 0.7F, 0.7F+0.3F*world.rand.nextFloat(), true);
				return true;
			}
		}
		return false;
	}

	private Operation getOperation(World world, int x, int y, int z, EntityPlayer ep, BucketMode mode) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof IFluidHandler) {
			if (ep.isSneaking())
				return Operation.ADDTANK;
			return mode == BucketMode.PLACE ? Operation.TANKFILL : Operation.TANKDRAIN;
		}
		return mode == BucketMode.PLACE ? Operation.PLACEWORLD : Operation.PICKUPWORLD;
	}

	public BucketMode getMode(ItemStack is) {
		return BucketMode.list[is.getItemDamage()];
	}

	public int getActiveLinkIndex(ItemStack is, EntityPlayer ep) {
		if (is.stackTagCompound == null)
			return -1;
		int idx = is.stackTagCompound.hasKey("selected") ? is.stackTagCompound.getInteger("selected") : -1;
		return idx;
	}

	private TankLink getActiveLink(ItemStack is, EntityPlayer ep) {
		int idx = this.getActiveLinkIndex(is, ep);
		if (idx < 0)
			return null;
		ArrayList<TankLink> li = this.getLinks(is, ep);
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

	public void removeLinkIndex(ItemStack is, int idx) {
		if (idx < 0 || is.stackTagCompound == null)
			return;
		NBTTagList li = is.stackTagCompound.getTagList("links", NBTTypes.COMPOUND.ID);
		if (idx < li.tagList.size())
			li.tagList.remove(idx);
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

	private static enum Operation {
		PICKUPWORLD,
		TANKDRAIN,
		PLACEWORLD,
		TANKFILL,
		ADDTANK;

		private boolean apply(World world, int x, int y, int z, ForgeDirection dir, EntityPlayer ep, ItemStack is, TankLink link) {
			switch(this) {
				case ADDTANK:
					((ItemEnderBucket)is.getItem()).addFluidHandler(world.getTileEntity(x, y, z), ep, is);
					return true;
				case PICKUPWORLD: {
					if (link == null)
						return false;
					FluidStack fs = ReikaWorldHelper.getDrainableFluid(world, x, y, z);
					if (fs != null && link.tryAddFluid(fs, false)) {
						link.tryAddFluid(fs, true);
						world.setBlock(x, y, z, Blocks.air);
						return true;
					}
					return false;
				}
				case PLACEWORLD: {
					if (link == null)
						return false;
					if (ReikaBlockHelper.isLiquid(world.getBlock(x, y, z)))
						return false;
					x += dir.offsetX;
					y += dir.offsetY;
					z += dir.offsetZ;
					if (ReikaWorldHelper.softBlocks(world, x, y, z)) {
						Fluid f = link.getCurrentFluidToDrain(false, false);
						if (f != null && f.getBlock() != null) {
							if (ReikaFluidHelper.lookupFluidForBlock(world.getBlock(x, y, z)) == f && ReikaWorldHelper.isLiquidSourceBlock(world, x, y, z))
								return false;
							link.getCurrentFluidToDrain(true, false);
							world.setBlock(x, y, z, f.getBlock(), 0, 3);
							world.markBlockForUpdate(x, y, z);
							world.getBlock(x, y, z).onNeighborBlockChange(world, x, y, z, f.getBlock());
							return true;
						}
					}
					return false;
				}
				case TANKDRAIN: {
					if (link == null)
						return false;
					if (link.tank.equals(world, x, y, z))
						return false;
					IFluidHandler ifl = (IFluidHandler)world.getTileEntity(x, y, z);
					if (ifl == null)
						return false;
					Fluid f = link.getCurrentFluidToDrain(false, false);
					FluidStack fs = tryDrainFluid(ifl, f, false);
					if (fs != null && link.tryAddFluid(fs, false)) {
						tryDrainFluid(ifl, f, true);
						link.tryAddFluid(fs, true);
						return true;
					}
					return false;
				}
				case TANKFILL: {
					if (link == null)
						return false;
					if (link.tank.equals(world, x, y, z))
						return false;
					IFluidHandler ifl = (IFluidHandler)world.getTileEntity(x, y, z);
					if (ifl == null)
						return false;
					Fluid f = link.getCurrentFluidToDrain(false, false);
					if (f != null) {
						if (tryAddFluid(ifl, f, false)) {
							link.getCurrentFluidToDrain(true, false);
							tryAddFluid(ifl, f, true);
							return true;
						}
					}
					return false;
				}
			}
			return false;
		}
	}

	private static boolean tryAddFluid(IFluidHandler ifl, Fluid f, boolean doAdd) {
		FluidStack fs = new FluidStack(f, FluidContainerRegistry.BUCKET_VOLUME);
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			int amt = ifl.fill(dir, fs, doAdd);
			if (amt >= FluidContainerRegistry.BUCKET_VOLUME) {
				return true;
			}
		}
		return false;
	}

	private static FluidStack tryDrainFluid(IFluidHandler ifl, Fluid seek, boolean doDrain) {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			FluidStack fs = ifl.drain(dir, FluidContainerRegistry.BUCKET_VOLUME, doDrain);
			if (fs != null && (seek == null || seek == fs.getFluid()) && fs.amount >= FluidContainerRegistry.BUCKET_VOLUME) {
				return fs;
			}
		}
		return null;
	}

	public static enum BucketMode {
		PICKUP("Pickup"),
		PLACE("Place");

		public final String displayName;

		public static final BucketMode[] list = values();

		private BucketMode(String s) {
			displayName = s;
		}
	}

	public static class TankLink {

		public final WorldLocation tank;
		private final BlockKey cachedBlock;
		private ItemStack cachedIcon;
		private Fluid cachedFluid;

		private TankLink(TileEntity te) {
			this(new WorldLocation(te), new BlockKey(te.worldObj.getBlock(te.xCoord, te.yCoord, te.zCoord), te.worldObj.getBlockMetadata(te.xCoord, te.yCoord, te.zCoord)));
			cachedFluid = this.getCurrentFluidToDrain(false, false);
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
			String s = is != null ? is.getDisplayName() : "";
			//s = s+" "+new Coordinate(tank).toString();
			return s;
		}

		public Fluid getCurrentFluidToDrain(boolean doDrain, boolean render) {
			IFluidHandler ifl = this.getTank();
			if (ifl == null)
				return render ? cachedFluid : null;
			for (int i = 0; i < 6; i++) {
				ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
				FluidStack fs = ifl.drain(dir, FluidContainerRegistry.BUCKET_VOLUME, doDrain);
				if (fs != null && fs.amount >= FluidContainerRegistry.BUCKET_VOLUME) {
					cachedFluid = fs.getFluid();
					return cachedFluid;
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
			if (cachedFluid != null) {
				tag.setString("fluid", cachedFluid.getName());
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
			if (tag.hasKey("fluid")) {
				lk.cachedFluid = FluidRegistry.getFluid(tag.getString("fluid"));
			}
			return lk;
		}

	}

}
