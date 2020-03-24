/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Transport;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Block.BlockRouterNode.RouterFilter;
import Reika.ChromatiCraft.Block.BlockRouterNode.TileEntityRouterExtraction;
import Reika.ChromatiCraft.Block.BlockRouterNode.TileEntityRouterInsertion;
import Reika.ChromatiCraft.Block.BlockRouterNode.TileEntityRouterNode;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Instantiable.Data.Collections.InventoryCache;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.ModInteract.BasicAEInterface;
import Reika.DragonAPI.Interfaces.TileEntity.AdjacentUpdateWatcher;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.ModInteract.Bees.ReikaBeeHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.MESystemReader;
import Reika.DragonAPI.ModInteract.DeepInteract.MESystemReader.ExtractedItem;
import Reika.DragonAPI.ModInteract.DeepInteract.MESystemReader.ExtractedItemGroup;
import Reika.DragonAPI.ModInteract.DeepInteract.MESystemReader.MatchMode;
import Reika.DragonAPI.ModRegistry.InterfaceCache;

import appeng.api.AEApi;
import appeng.api.networking.IGridBlock;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionHost;
import appeng.api.util.AECableType;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeHousingInventory;
import forestry.api.multiblock.IAlvearyComponent;


@Strippable(value={"appeng.api.networking.security.IActionHost"})
public class TileEntityRouterHub extends TileEntityChromaticBase implements IActionHost, RouterFilter, AdjacentUpdateWatcher {

	private final InventoryCache ingredients = new InventoryCache();

	@ModDependent(ModList.APPENG)
	private MESystemReader network;
	private Object aeGridBlock;
	private Object aeGridNode;

	private final StepTimer cacheTimer = new StepTimer(40);
	private final StepTimer operationTimer = new StepTimer(4);

	private final HashMap<Coordinate, Insertion> insertions = new HashMap();
	private final HashMap<Coordinate, Extraction> extractions = new HashMap();

	private final HashSet<Highlight> highlights = new HashSet();

	private ItemRule[] defaults = new ItemRule[9];

	private ForgeDirection facing = ForgeDirection.DOWN;

	public TileEntityRouterHub() {
		if (ModList.APPENG.isLoaded()) {
			aeGridBlock = new BasicAEInterface(this, this.getTile().getCraftedProduct());
			aeGridNode = FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER ? AEApi.instance().createGridNode((IGridBlock)aeGridBlock) : null;
		}
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.ROUTERHUB;
	}

	public void scanAndLink(World world, int x, int y, int z, int r) {
		for (int i = -r; i <= r; i++) {
			for (int k = -r; k <= r; k++) {
				for (int j = -r/2; j <= r/2; j++) {
					int dx = x+i;
					int dy = y+j;
					int dz = z+k;
					if (world.getBlock(dx, dy, dz) == ChromaBlocks.ROUTERNODE.getBlockInstance()) {
						TileEntityRouterNode te = (TileEntityRouterNode)world.getTileEntity(dx, dy, dz);
						Coordinate c = new Coordinate(this);
						if (te.getHub() == null || te.getHub().equals(c))
							te.setHub(c);
					}
				}
			}
		}
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (!world.isRemote) {
			cacheTimer.update();
			if (cacheTimer.checkCap()) {
				this.buildCache();
			}

			operationTimer.update();
			if (operationTimer.checkCap()) {
				ArrayList<Coordinate> li = new ArrayList(extractions.keySet());
				li.addAll(insertions.keySet());
				int count = this.getRunnableTicks(li);
				Collections.shuffle(li);
				while (count > 0 && !li.isEmpty()) {
					int idx = rand.nextInt(li.size());
					Coordinate c = li.remove(idx);
					Connection e = extractions.get(c);
					if (e == null)
						e = insertions.get(c);
					if (e != null) {
						if (e.performOperation(this)) {
							this.addHighlight(c);
							count--;
						}
					}
				}
			}
		}
		/*
		else {
			if (highlights.size() < 2) {
				if (rand.nextBoolean()) {
					if (!extractions.isEmpty()) {
						Coordinate c = ReikaJavaLibrary.getRandomCollectionEntry(extractions.keySet());
						this.addHighlight(c);
					}
				}
				else {
					if (!insertions.isEmpty()) {
						Coordinate c = ReikaJavaLibrary.getRandomCollectionEntry(insertions.keySet());
						this.addHighlight(c);
					}
				}
			}
		}*/
	}

	private int getRunnableTicks(ArrayList<Coordinate> li) {
		return 1+li.size()/36;
	}

	public void addHighlight(Coordinate c) {
		if (worldObj.isRemote) {
			highlights.add(new Highlight(c, 5+rand.nextInt(6)));
		}
		else {
			ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.ROUTERLINK.ordinal(), this, 48, c.xCoord, c.yCoord, c.zCoord);
		}
	}

	public Collection<Highlight> getHighlightLocations() {
		return highlights;
	}

	public void removeConnection(TileEntityRouterNode te) {
		Coordinate c = new Coordinate(te);
		insertions.remove(c);
		extractions.remove(c);
		this.syncAllData(true);
	}

	public void addInserter(TileEntityRouterInsertion te) {
		Coordinate c = new Coordinate(te);
		insertions.put(c, new Insertion(c, te.getSide(), te.isBlacklist, te.getFilter()));
		this.syncAllData(true);
	}

	public void addExtractor(TileEntityRouterExtraction te) {
		Coordinate c = new Coordinate(te);
		extractions.put(c, new Extraction(c, te.getSide(), te.isBlacklist, te.getFilter()));
		this.syncAllData(true);
	}

	private void buildCache() {
		ingredients.clear();
		TileEntity te = this.getAdjacentTileEntity(this.getFacing());
		if (te instanceof IInventory) {
			ingredients.addInventory((IInventory)te);
		}

		if (ModList.APPENG.isLoaded()) {
			Object oldNode = aeGridNode;
			if (aeGridNode == null) {
				aeGridNode = FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER ? AEApi.instance().createGridNode((IGridBlock)aeGridBlock) : null;
			}
			if (aeGridNode != null)
				((IGridNode)aeGridNode).updateState();

			if (oldNode != aeGridNode || network == null) {
				if (aeGridNode == null)
					network = null;
				else if (network == null)
					network = new MESystemReader((IGridNode)aeGridNode, this);
				else
					network = new MESystemReader((IGridNode)aeGridNode, network);
			}
		}
	}

	public ForgeDirection getFacing() {
		return facing != null ? facing : ForgeDirection.DOWN;
	}

	public void setFacing(ForgeDirection dir) {
		facing = dir;
		this.buildCache();
	}

	private ItemStack findItem(ItemStack is, MatchMode mode, boolean simulate) {
		if (DragonAPICore.debugtest)
			return is.copy();

		if (ModList.APPENG.isLoaded()) {
			ChromatiCraft.logger.debug("Router "+this+" requesting "+is+" from "+ingredients+" / "+network);
		}
		else {
			ChromatiCraft.logger.debug("Router "+this+" requesting "+is+" from "+ingredients);
		}

		if (ModList.APPENG.isLoaded()) {
			ExtractedItemGroup rem = mode.removeItems(network, is, simulate, false);
			if (rem != null) {
				ExtractedItem ei = rem.getBiggest();
				return ReikaItemHelper.getSizedItemStack(ei.getItem(), (int)ei.amount);
			}
			ChromatiCraft.logger.debug(this+" failed to find "+is+" in its ME System.");
		}
		int has = ingredients.getItemCount(is);
		if (has > 0) {
			int rem = Math.min(is.stackSize, has);
			if (!simulate)
				ingredients.removeXItems(is, rem);
			return ReikaItemHelper.getSizedItemStack(is, rem);
		}
		return null;
	}

	/** Returns amount NOT added */
	private int injectItem(ItemStack is, boolean simulate) {
		if (DragonAPICore.debugtest)
			return 0;
		is = is.copy();
		if (ModList.APPENG.isLoaded() && network != null) {
			int left = (int)network.addItem(is, simulate);
			if (left == 0)
				return 0;
			else
				is.stackSize = left;
		}
		int left = ingredients.addItemsToUnderlyingInventories(is, simulate);
		return left;
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		super.onFirstTick(world, x, y, z);
		this.buildCache();
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	@ModDependent(ModList.APPENG)
	public IGridNode getActionableNode() {
		return (IGridNode)aeGridNode;
	}

	@Override
	@ModDependent(ModList.APPENG)
	public IGridNode getGridNode(ForgeDirection dir) {
		return dir == this.getFacing() ? (IGridNode)aeGridNode : null;
	}

	@Override
	@ModDependent(ModList.APPENG)
	public AECableType getCableConnectionType(ForgeDirection dir) {
		return AECableType.COVERED;
	}

	@Override
	@ModDependent(ModList.APPENG)
	public void securityBreak() {

	}

	@Override
	protected void onInvalidateOrUnload(World world, int x, int y, int z, boolean invalid) {
		super.onInvalidateOrUnload(world, x, y, z, invalid);
		if (ModList.APPENG.isLoaded() && aeGridNode != null)
			((IGridNode)aeGridNode).destroy();
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setInteger("facing", this.getFacing().ordinal());
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		facing = dirs[NBT.getInteger("facing")];
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		defaults = new ItemRule[9];
		for (int i = 0; i < defaults.length; i++) {
			defaults[i] = ItemRule.readFromNBT(NBT.getCompoundTag("slot_"+i));
		}

		insertions.clear();
		extractions.clear();
		NBTTagList in = NBT.getTagList("insert", NBTTypes.COMPOUND.ID);
		for (Object o : in.tagList) {
			NBTTagCompound tag = (NBTTagCompound)o;
			Insertion i = (Insertion)Connection.readFromNBT(tag);
			insertions.put(i.location, i);
		}

		NBTTagList out = NBT.getTagList("extract", NBTTypes.COMPOUND.ID);
		for (Object o : out.tagList) {
			NBTTagCompound tag = (NBTTagCompound)o;
			Extraction i = (Extraction)Connection.readFromNBT(tag);
			extractions.put(i.location, i);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		for (int i = 0; i < defaults.length; i++) {
			if (defaults[i] != null)
				NBT.setTag("slot_"+i, defaults[i].writeToNBT());
		}

		NBTTagList li = new NBTTagList();
		for (Insertion i : insertions.values()) {
			li.appendTag(i.writeToNBT());
		}
		NBT.setTag("insert", li);

		li = new NBTTagList();
		for (Extraction i : extractions.values()) {
			li.appendTag(i.writeToNBT());
		}
		NBT.setTag("extract", li);
	}

	public void setFilterItem(int slot, ItemStack is) {
		MatchMode mode = defaults[slot] != null ? defaults[slot].mode : MatchMode.EXACT;
		defaults[slot] = is != null ? new ItemRule(is.copy(), mode) : null;
		this.syncAllData(true);
	}

	public void setFilterMode(int slot, MatchMode mode) {
		if (defaults[slot] == null)
			return;
		defaults[slot] = new ItemRule(defaults[slot].getItem(), mode);
		this.syncAllData(true);
	}

	public ItemRule getFilter(int slot) {
		return defaults[slot];
	}

	public Collection<Coordinate> getInsertionLocations() {
		return Collections.unmodifiableCollection(insertions.keySet());
	}

	public Collection<Coordinate> getExtractionLocations() {
		return Collections.unmodifiableCollection(extractions.keySet());
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}

	private boolean canExtractItemFrom(int slot, ItemStack in, IInventory ii, ForgeDirection side) {
		if (ModList.FORESTRY.isLoaded() && InterfaceCache.BEEHOUSE.instanceOf(ii)) {
			if (ii instanceof IAlvearyComponent && ii.getClass().getName().endsWith("Sieve")) {
				return slot < 4;
			}
			else {
				IBeeHousing ibh = (IBeeHousing)ii;
				IBeeHousingInventory bhi = ibh.getBeeInventory();
				EnumBeeType type = ReikaBeeHelper.getBeeRoot().getType(in);
				if (bhi.getQueen() == null && (type == EnumBeeType.PRINCESS || type == EnumBeeType.QUEEN)) {
					return false;
				}
				if (bhi.getDrone() == null && type == EnumBeeType.DRONE) {
					return false;
				}
			}
		}
		return !(ii instanceof ISidedInventory) || ((ISidedInventory)ii).canExtractItem(slot, in, side.ordinal());
	}

	public static final class ItemRule {

		private final ItemStack item;
		public final MatchMode mode;

		public ItemRule(ItemStack is, MatchMode m) {
			item = ReikaItemHelper.getSizedItemStack(is, is.getMaxStackSize());
			mode = m;
		}

		public boolean matches(ItemStack is) {
			return mode.compare(is, item);
		}

		@Override
		public int hashCode() {
			return item.getItem().hashCode() ^ mode.ordinal();
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof ItemRule) {
				ItemRule ir = (ItemRule)o;
				return ItemStack.areItemStacksEqual(item, ir.item) && mode == ir.mode;
			}
			return false;
		}

		@Override
		public String toString() {
			return item+" * "+mode;
		}

		public ItemStack getItem() {
			return item.copy();
		}

		public static ItemRule readFromNBT(NBTTagCompound tag) {
			ItemStack is = ItemStack.loadItemStackFromNBT(tag);
			int mode = tag.getInteger("mode");
			return is != null ? new ItemRule(is, MatchMode.list[mode]) : null;
		}

		public NBTTagCompound writeToNBT() {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger("mode", mode.ordinal());
			item.writeToNBT(tag);
			return tag;
		}

	}

	private static abstract class Connection {

		protected final Coordinate location;
		protected final ForgeDirection direction;
		protected final HashSet<ItemRule> items = new HashSet();
		protected final boolean blacklist;

		private Connection(Coordinate c, ForgeDirection dir, boolean blacklist, Collection<ItemRule> filter) {
			location = c;
			direction = dir;
			this.blacklist = blacklist;
			items.addAll(filter);
		}

		public final boolean allowsItem(ItemStack is) {
			if (items.isEmpty())
				return true;
			for (ItemRule item : items) {
				if (item.matches(is))
					return true;
			}
			return false;
		}

		protected abstract boolean performOperation(TileEntityRouterHub te);

		protected final IInventory getTarget(World world) {
			Coordinate c = location.offset(direction.getOpposite(), 1);
			TileEntity te = c.getTileEntity(world);
			return te instanceof IInventory ? (IInventory)te : null;
		}

		public final NBTTagCompound writeToNBT() {
			NBTTagCompound NBT = new NBTTagCompound();
			location.writeToNBT("loc", NBT);
			NBT.setInteger("dir", direction.ordinal());
			NBT.setBoolean("black", blacklist);
			NBTTagList li = new NBTTagList();
			for (ItemRule ir : items) {
				li.appendTag(ir.writeToNBT());
			}
			NBT.setTag("items", li);
			NBT.setString("type", this.getClass().getName());
			return NBT;
		}

		public static Connection readFromNBT(NBTTagCompound NBT) {
			String type = NBT.getString("type");
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[NBT.getInteger("dir")];
			Coordinate c = Coordinate.readFromNBT("loc", NBT);
			boolean bl = NBT.getBoolean("black");
			NBTTagList li = NBT.getTagList("items", NBTTypes.COMPOUND.ID);
			Collection<ItemRule> set = new HashSet();
			for (Object o : li.tagList) {
				NBTTagCompound tag = (NBTTagCompound)o;
				ItemRule ir = ItemRule.readFromNBT(tag);
				set.add(ir);
			}
			try {
				Class cl = Class.forName(type);
				Constructor<Connection> con = cl.getDeclaredConstructor(Coordinate.class, ForgeDirection.class, boolean.class, Collection.class);
				con.setAccessible(true);
				return con.newInstance(c, dir, bl, set);
			}
			catch (Exception e) {
				return null;
			}
		}

	}

	private static class Insertion extends Connection {

		private Insertion(Coordinate c, ForgeDirection dir, boolean blacklist, Collection<ItemRule> filter) {
			super(c, dir, blacklist, filter);
		}

		@Override
		protected boolean performOperation(TileEntityRouterHub te) {
			IInventory ii = this.getTarget(te.worldObj);
			if (ii == null)
				return false;
			boolean flag = false;
			Collection<ItemRule> c = new HashSet(items);
			for (int i = 0; i < te.defaults.length; i++) {
				if (te.defaults[i] != null)
					c.add(te.defaults[i]);
			}
			//ReikaJavaLibrary.pConsole("Running "+c.size()+" insertions from "+te+": "+c);
			for (ItemRule i : c) {
				ItemStack found = te.findItem(i.item, i.mode, true);//i.mode.removeItems(te.network, i.item, true);
				//ReikaJavaLibrary.pConsole("Found: "+found);
				if (found != null) {
					ItemStack move = ReikaItemHelper.getSizedItemStack(i.item, found.stackSize);
					//ReikaJavaLibrary.pConsole("Attempting to move "+move);
					if (ReikaInventoryHelper.addToIInv(move, ii)) {
						te.findItem(move, i.mode, false);//i.mode.removeItems(te.network, i.item, false);
						//ReikaJavaLibrary.pConsole("Moved "+move);
						flag = true;
					}
				}
			}
			return flag;
		}
	}

	private static class Extraction extends Connection {

		private Extraction(Coordinate c, ForgeDirection dir, boolean blacklist, Collection<ItemRule> filter) {
			super(c, dir, blacklist, filter);
		}

		@Override
		protected boolean performOperation(TileEntityRouterHub te) {
			IInventory ii = this.getTarget(te.worldObj);
			if (ii == null)
				return false;
			boolean flag = false;
			for (int i = 0; i < ii.getSizeInventory(); i++) {
				ItemStack in = ii.getStackInSlot(i);
				if (in != null) {
					if (te.canExtractItemFrom(i, in, ii, direction) && this.allowsItem(in) != blacklist) {
						int left = te.injectItem(in, true);
						int amt = in.stackSize-left;
						if (amt > 0) {
							ItemStack move = ReikaItemHelper.getSizedItemStack(in, amt);
							ReikaInventoryHelper.decrStack(i, ii, amt);
							te.injectItem(move, false);
							flag = true;
						}
					}
				}
			}
			return flag;
		}

	}

	public static class Highlight {

		public final Coordinate location;
		public final int lifespan;
		public int age;

		private Highlight(Coordinate c, int l) {
			location = c;
			lifespan = l;
		}

	}

	@Override
	public void onAdjacentUpdate(World world, int x, int y, int z, Block b) {
		this.buildCache();
	}

}
