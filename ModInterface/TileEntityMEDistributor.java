package Reika.ChromatiCraft.ModInterface;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Instantiable.Data.Collections.ItemCollection;
import Reika.DragonAPI.Instantiable.ModInteract.DirectionalAEInterface;
import Reika.DragonAPI.Interfaces.SidePlacedTile;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.MESystemReader;
import Reika.DragonAPI.ModInteract.DeepInteract.MESystemReader.SourceType;
import appeng.api.AEApi;
import appeng.api.config.FuzzyMode;
import appeng.api.networking.IGridBlock;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.util.AECableType;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Strippable(value={"appeng.api.networking.IGridHost"})
public class TileEntityMEDistributor extends TileEntityChromaticBase implements IGridHost, SidePlacedTile {

	@ModDependent(ModList.APPENG)
	private MESystemReader network;
	private Object aeGridBlock;
	private Object aeGridNode;

	private final ItemCollection output = new ItemCollection();

	private StepTimer checkTimer = new StepTimer(10);
	private StepTimer cacheTimer = new StepTimer(40);

	public static final int NSLOTS = 10;

	private ItemStack[] filter = new ItemStack[NSLOTS*2];
	private int[] threshold = new int[NSLOTS];
	private boolean[] fuzzy = new boolean[NSLOTS];
	private boolean[] oreDict = new boolean[NSLOTS];

	public TileEntityMEDistributor() {
		if (ModList.APPENG.isLoaded()) {
			aeGridBlock = new DirectionalAEInterface(this, this.getTile().getCraftedProduct());
			aeGridNode = FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER ? AEApi.instance().createGridNode((IGridBlock)aeGridBlock) : null;
		}
	}

	@Override
	protected void onInvalidateOrUnload(World world, int x, int y, int z, boolean invalid) {
		super.onInvalidateOrUnload(world, x, y, z, invalid);
		if (ModList.APPENG.isLoaded() && aeGridNode != null)
			((IGridNode)aeGridNode).destroy();
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.MEDISTRIBUTOR;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (world.isRemote) {
			this.spawnParticles(world, x, y, z, meta);
		}
		else {
			cacheTimer.update();
			if (cacheTimer.checkCap()) {
				this.buildCache();
			}

			if (network != null) {
				checkTimer.update();
				if (checkTimer.checkCap()) {
					output.clear();
					TileEntity te = this.getAdjacentTileEntity(this.getFacing().getOpposite());
					if (te instanceof IInventory) {
						output.addInventory((IInventory)te);
					}
					for (int i = 0; i < NSLOTS; i++) {
						ItemStack f1 = filter[i];
						ItemStack f2 = filter[i+NSLOTS];
						if (f1 != null && f2 != null) {
							int fit = f2.getMaxStackSize()-output.addItemsToUnderlyingInventories(ReikaItemHelper.getSizedItemStack(f2, f2.getMaxStackSize()), true);
							if (fit > 0) {
								long has = this.isFuzzy(i) ? network.getFuzzyItemCount(f1, FuzzyMode.IGNORE_ALL, this.useOreDict(i)) : network.getItemCount(f1);
								int missing = this.getThreshold(i)-(has > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)has);
								if (missing > 0) {
									this.transferItem(ReikaItemHelper.getSizedItemStack(f2, Math.min(Math.min(fit, missing), f2.getMaxStackSize())), (IInventory)te);
								}
							}
						}
					}
				}
			}
		}
	}

	public int getThreshold(int i) {
		return threshold[i];
	}

	public boolean useOreDict(int i) {
		return oreDict[i];
	}

	public boolean isFuzzy(int i) {
		return fuzzy[i];
	}

	private void buildCache() {
		if (ModList.APPENG.isLoaded()) {
			((DirectionalAEInterface)aeGridBlock).disconnectAll().connect(this.getFacing());
			if (aeGridNode == null) {
				aeGridNode = FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER ? AEApi.instance().createGridNode((IGridBlock)aeGridBlock) : null;
			}
			((IGridNode)aeGridNode).updateState();
			network = aeGridNode != null ? new MESystemReader((IGridNode)aeGridNode, SourceType.MACHINE) : null;
		}
	}

	private void transferItem(ItemStack is, IInventory ii) {
		ReikaInventoryHelper.addToIInv(is, ii);
		network.removeItem(is, false);
		ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.METRANSFER.ordinal(), this, 32, Item.getIdFromItem(is.getItem()), is.getItemDamage());
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		NBTTagCompound fil = new NBTTagCompound();

		for (int i = 0; i < filter.length; i++) {
			ItemStack is = filter[i];
			if (is != null) {
				NBTTagCompound tag = new NBTTagCompound();
				is.writeToNBT(tag);
				if (i < 9)
					tag.setInteger("thresh", threshold[i]);
				fil.setTag("filter_"+i, tag);
			}
		}

		for (int i = 0; i < threshold.length; i++) {
			fil.setInteger("thresh_"+i, threshold[i]);
			fil.setBoolean("fuzzy_"+i, fuzzy[i]);
			fil.setBoolean("ore_"+i, oreDict[i]);
		}

		NBT.setTag("filter", fil);
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		filter = new ItemStack[filter.length];
		NBTTagCompound fil = NBT.getCompoundTag("filter");
		for (int i = 0; i < filter.length; i++) {
			String name = "filter_"+i;
			if (fil.hasKey(name)) {
				NBTTagCompound tag = fil.getCompoundTag(name);
				ItemStack is = ItemStack.loadItemStackFromNBT(tag);
				filter[i] = is;
			}
		}

		threshold = new int[threshold.length];
		fuzzy = new boolean[fuzzy.length];
		oreDict = new boolean[oreDict.length];
		for (int i = 0; i < threshold.length; i++) {
			String name = "filter_"+i;
			threshold[i] = fil.getInteger("thresh_"+i);
			fuzzy[i] = fil.getBoolean("fuzzy_"+i);
			oreDict[i] = fil.getBoolean("ore_"+i);
		}
	}

	public void setMapping(int slot, ItemStack is) {
		filter[slot] = is;
		this.syncAllData(true);
	}

	public ItemStack getMapping(int slot) {
		return filter[slot] != null ? filter[slot].copy() : null;
	}

	public void setThreshold(int slot, int thresh) {
		threshold[slot] = thresh;
		this.syncAllData(true);
	}

	public void toggleFuzzy(int slot) {
		if (fuzzy[slot]) {
			if (oreDict[slot]) {
				fuzzy[slot] = false;
				oreDict[slot] = false;
			}
			else {
				oreDict[slot] = true;
			}
		}
		else {
			fuzzy[slot] = true;
		}
		this.syncAllData(true);
	}

	@SideOnly(Side.CLIENT)
	private void spawnParticles(World world, int x, int y, int z, int meta) {

	}

	@SideOnly(Side.CLIENT)
	public void spawnTransferParticles(World world, int x, int y, int z, int id, int meta) {
		ItemStack is = new ItemStack(Item.getItemById(id), 1, meta);
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	@ModDependent(ModList.APPENG)
	public IGridNode getGridNode(ForgeDirection dir) {
		return dir == this.getFacing() ? (IGridNode)aeGridNode : null;
	}

	@Override
	@ModDependent(ModList.APPENG)
	public AECableType getCableConnectionType(ForgeDirection dir) {
		return dir == this.getFacing() ? AECableType.GLASS : null;
	}

	private ForgeDirection getFacing() {
		return dirs[this.getBlockMetadata()];
	}

	@Override
	@ModDependent(ModList.APPENG)
	public void securityBreak() {

	}

	@Override
	public void placeOnSide(int s) {
		this.setBlockMetadata(s);
	}

}
