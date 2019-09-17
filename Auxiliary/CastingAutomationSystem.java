package Reika.ChromatiCraft.Auxiliary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Interfaces.CastingAutomationBlock;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.ItemElementCalculator;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityCenterBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityLaserFX;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingTable;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityItemStand;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Base.TileEntityBase;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Instantiable.Data.KeyedItemStack;
import Reika.DragonAPI.Instantiable.Data.Collections.ItemCollection;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.ModInteract.BasicAEInterface;
import Reika.DragonAPI.Instantiable.Recipe.ItemMatch;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.MESystemReader;
import Reika.DragonAPI.ModInteract.DeepInteract.MESystemReader.ExtractedItem;

import appeng.api.AEApi;
import appeng.api.config.FuzzyMode;
import appeng.api.networking.IGridBlock;
import appeng.api.networking.IGridNode;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CastingAutomationSystem {

	protected final Random rand = new Random();

	protected final CastingAutomationBlock tile;

	private CastingRecipe recipe;

	private final StepTimer stepDelay;
	private final StepTimer cacheTimer = new StepTimer(40);

	private int recipesToGo = 0;
	private int recipeCycles = 0;

	private final ItemCollection ingredients = new ItemCollection();
	@ModDependent(ModList.APPENG)
	private MESystemReader network;
	private Object aeGridBlock;
	private Object aeGridNode;

	public CastingAutomationSystem(CastingAutomationBlock te) {
		tile = te;
		stepDelay = new StepTimer(te.getInjectionTickRate());

		if (ModList.APPENG.isLoaded()) {
			aeGridBlock = new BasicAEInterface((TileEntity)te, ((TileEntityChromaticBase)te).getTile().getCraftedProduct());
			aeGridNode = FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER ? AEApi.instance().createGridNode((IGridBlock)aeGridBlock) : null;
		}
	}

	public final boolean isIdle() {
		return recipe == null || recipesToGo == 0;
	}

	public final CastingRecipe getCurrentRecipeOutput() {
		return recipe;
	}

	public final void destroy() {
		if (ModList.APPENG.isLoaded() && aeGridNode != null)
			((IGridNode)aeGridNode).destroy();
	}

	@ModDependent(ModList.APPENG)
	public final IGridNode getGridNode(ForgeDirection dir) {
		return (IGridNode)aeGridNode;
	}

	@ModDependent(ModList.APPENG)
	public final IGridNode getActionableNode() {
		return (IGridNode)aeGridNode;
	}

	public void setRecipe(CastingRecipe c, int amt) {
		//ReikaJavaLibrary.pConsole(amt+" x "+c);
		recipe = c;
		if (c != null && !tile.canTriggerCrafting()) {
			amt = Math.min(amt, 64/c.getOutput().stackSize);
		}
		recipesToGo = amt;
	}

	public final void cancelCrafting() {
		this.setRecipe(null, 0);
	}

	protected final boolean matches(Object object, ItemStack is) {
		if (object instanceof ItemStack) {
			return ReikaItemHelper.matchStacks(is, (ItemStack)object) && ItemStack.areItemStackTagsEqual(is, (ItemStack)object);
		}
		else if (object instanceof List) {
			return ReikaItemHelper.listContainsItemStack((Collection<ItemStack>)object, is, true);
		}
		else if (object == null && is == null)
			return true;
		return false;
	}

	protected final boolean hasItem(Object item, int amt) {
		ItemStack is = this.findItem(item, amt, true);
		return is != null && is.stackSize >= amt;
	}

	protected final int countItem(Object item) {
		if (DragonAPICore.debugtest)
			return Integer.MAX_VALUE;

		List<ItemStack> li = new ArrayList();
		if (item instanceof ItemStack)
			li.add((ItemStack)item);
		if (item instanceof List)
			li.addAll((List)item);
		if (item instanceof ItemMatch) {
			for (KeyedItemStack ks : ((ItemMatch)item).getItemList()) {
				li.add(ks.getItemStack());
			}
		}

		if (ModList.APPENG.isLoaded()) {
			ChromatiCraft.logger.debug("Delegate "+this+" counting "+li+" from "+ingredients+" / "+network);
		}
		else {
			ChromatiCraft.logger.debug("Delegate "+this+" counting "+li+" from "+ingredients);
		}

		int ret = 0;
		for (ItemStack is : li) {
			if (ModList.APPENG.isLoaded()) {
				if (is.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
					ret += network.getFuzzyItemCount(is, FuzzyMode.IGNORE_ALL, false, is.stackTagCompound != null);
				}
				else {
					ret += network.getItemCount(is, is.stackTagCompound != null);
				}
				ChromatiCraft.logger.debug(this+" failed to find "+is+" in its ME System.");
			}
			ret += ingredients.getItemCount(is);
		}
		return ret;
	}

	protected final ItemStack findItem(Object item, int amt, boolean simulate) {
		List<ItemStack> li = new ArrayList();
		if (item instanceof ItemStack)
			li.add((ItemStack)item);
		if (item instanceof List)
			li.addAll((List)item);
		if (item instanceof ItemMatch) {
			for (KeyedItemStack ks : ((ItemMatch)item).getItemList()) {
				li.add(ks.getItemStack());
			}
		}

		if (DragonAPICore.debugtest)
			return ReikaItemHelper.getSizedItemStack(li.get(0), amt);

		if (ModList.APPENG.isLoaded()) {
			ChromatiCraft.logger.debug("Delegate "+this+" requesting "+li+" from "+ingredients+" / "+network);
		}
		else {
			ChromatiCraft.logger.debug("Delegate "+this+" requesting "+li+" from "+ingredients);
		}

		for (ItemStack is : li) {
			if (ModList.APPENG.isLoaded()) {
				if (is.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
					ExtractedItem rem = network.removeItemFuzzy(ReikaItemHelper.getSizedItemStack(is, amt), simulate, FuzzyMode.IGNORE_ALL, false, is.stackTagCompound != null);
					if (rem != null) {
						ItemStack ret = ReikaItemHelper.getSizedItemStack(rem.getItem(), (int)rem.amount);
						ret.setItemDamage(0);
						return ret;
					}
					else {
						//network.triggerFuzzyCrafting(worldObj, is, amt, null, null);
					}
				}
				else {
					int rem = (int)network.removeItem(ReikaItemHelper.getSizedItemStack(is, amt), simulate, is.stackTagCompound != null);
					if (rem > 0) {
						return ReikaItemHelper.getSizedItemStack(is, rem);
					}
					else {
						//network.triggerCrafting(worldObj, is, amt, null, null); GOD DAMN IT AE
					}
				}
				ChromatiCraft.logger.debug(this+" failed to find "+is+" in its ME System.");
			}
			int has = ingredients.getItemCount(is);
			if (has > 0) {
				int rem = Math.min(amt, has);
				if (!simulate)
					ingredients.removeXItems(is, rem);
				return ReikaItemHelper.getSizedItemStack(is, rem);
			}
		}
		return null;
	}

	private boolean recoverItem(ItemStack is) {
		if (DragonAPICore.debugtest)
			return true;
		if (ModList.APPENG.isLoaded()) {
			int left = (int)network.addItem(is, false);
			is.stackSize = left;
			if (left == 0)
				return true;
		}
		int left = ingredients.addItemsToUnderlyingInventories(is, false);
		return left <= 0;
	}

	public final int pushItemToME(ItemStack is) {
		if (DragonAPICore.debugtest)
			return is.stackSize;
		if (ModList.APPENG.isLoaded()) {
			int left = (int)network.addItem(is, false);
			int delta = is.stackSize-left;
			is.stackSize = left;
			return delta;
		}
		return 0;
	}

	public void tick(World world) {
		cacheTimer.update();
		if (cacheTimer.checkCap()) {
			this.buildCache();
		}

		if (ModList.APPENG.isLoaded()) {
			if (aeGridBlock != null && !world.isRemote) {
				((BasicAEInterface)aeGridBlock).setPowerCost(recipe != null ? 6 : 1);
			}
		}

		TileEntityCastingTable te = tile.getTable();
		if (te != null && recipe != null && recipesToGo > 0 && tile.isAbleToRun(te)) {
			if (te != null) {
				int x = this.getX();
				int y = this.getY();
				int z = this.getZ();
				if (tile.canCraft(world, x, y, z, te)) {
					if (this.isRecipeReady(world, x, y, z, te)) {
						if (tile.canTriggerCrafting()) {
							if (this.triggerCrafting(world, x, y, z, te)) {
								this.onTriggerCrafting(recipe, recipeCycles);
								te.syncAllData(true);
								tile.consumeEnergy(recipe, te, null);
								recipesToGo -= recipeCycles;
								recipeCycles = 0;
							}
						}
						else {
							recipesToGo -= recipeCycles;
							recipeCycles = 0;
						}
					}
					else {
						int amt = Math.min(recipesToGo, recipe.getOutput().getMaxStackSize()/recipe.getOutput().stackSize);
						if (recipe instanceof MultiBlockCastingRecipe) {
							MultiBlockCastingRecipe mr = (MultiBlockCastingRecipe)recipe;
							amt = Math.min(amt, mr.getMainInput().getMaxStackSize()/mr.getRequiredCentralItemCount());
						}
						//ReikaJavaLibrary.pConsole("Preparing step for recipe "+recipe);
						UpdateStep c = this.prepareRecipeStep(world, x, y, z, te, amt);
						//ReikaJavaLibrary.pConsole("Obtained "+c);
						if (c != null) {
							recipeCycles = recipeCycles > 0 ? Math.min(recipeCycles, c.getItem().stackSize) : c.getItem().stackSize;
							ChromaSounds.CAST.playSoundAtBlock(world, c.loc.xCoord, c.loc.yCoord, c.loc.zCoord);
							int[] dat = new int[]{c.loc.xCoord, c.loc.yCoord, c.loc.zCoord, 0, Item.getIdFromItem(c.getItem().getItem()), c.getItem().getItemDamage(), c.getItem().stackTagCompound != null ? 1 : 0};
							ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.CASTAUTOUPDATE.ordinal(), (TileEntity)tile, 48, dat);
							tile.consumeEnergy(recipe, te, c.getItem());
							te.markDirty();
							TileEntity tile = c.loc.getTileEntity(world);
							if (tile != null) {
								tile.markDirty();
								if (tile instanceof TileEntityBase) {
									((TileEntityBase)tile).syncAllData(true);
								}
							}
						}
					}
				}
			}
		}
		else if (recipe == null || recipesToGo == 0) {
			this.cancelCrafting();
		}
	}

	protected void onTriggerCrafting(CastingRecipe r, int cycles) {

	}

	private void buildCache() {
		ingredients.clear();
		TileEntity te = tile.getItemPool();
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
					network = new MESystemReader((IGridNode)aeGridNode, tile);
				else
					network = new MESystemReader((IGridNode)aeGridNode, network);
			}
		}
	}

	private UpdateStep prepareRecipeStep(World world, int x, int y, int z, TileEntityCastingTable te, int amt) {
		stepDelay.update();
		if (stepDelay.checkCap()) {
			if (recipe instanceof MultiBlockCastingRecipe) {
				MultiBlockCastingRecipe mr = (MultiBlockCastingRecipe)recipe;
				HashMap<List<Integer>, TileEntityItemStand> map = te.getOtherStands();
				Map<List<Integer>, ItemMatch> items = mr.getAuxItems();
				//ReikaJavaLibrary.pConsole("Need items "+items);
				for (List<Integer> key : map.keySet()) {
					ItemMatch item = items.get(key);
					TileEntityItemStand stand = map.get(key);
					if (stand != null) {
						ItemStack in = stand.getStackInSlot(0);
						if ((item == null && in != null) || (item != null && !item.match(in))) {
							if (in != null) {
								if (this.recoverItem(in)) {
									stand.setInventorySlotContents(0, null);
									return new UpdateStep(stand, in);
								}
							}
							else {
								ItemStack ret = this.findItem(item, amt, false);
								//ReikaJavaLibrary.pConsole("Looking for "+item+", got "+ret);
								if (ret != null) {
									stand.setInventorySlotContents(0, ret);
									return new UpdateStep(stand, ret);
								}
							}
						}
						else {
							//matches
						}
					}
				}
				if (tile.canPlaceCentralItemForMultiRecipes()) {
					ItemStack ctr = mr.getMainInput();
					for (int i = 0; i < 9; i++) {
						ItemStack in = te.getStackInSlot(i);
						if (i == 4) {
							if (in != null) {
								if (ReikaItemHelper.matchStacks(in, ctr) && in.stackSize >= mr.getRequiredCentralItemCount() && (ctr.stackTagCompound == null || ItemStack.areItemStackTagsEqual(in, ctr))) {
									//matches
								}
								else {
									if (this.recoverItem(in)) {
										te.setInventorySlotContents(i, null);
										return new UpdateStep(te, in);
									}
								}
							}
							else {
								ItemStack ret = this.findItem(ctr, amt*mr.getRequiredCentralItemCount(), false);
								//ReikaJavaLibrary.pConsole("Looking for center item "+ctr+", got "+ret);
								if (ret != null) {
									te.setInventorySlotContents(i, ret);
									return new UpdateStep(te, ret);
								}
							}
						}
						else {
							if (in != null && this.recoverItem(in)) {
								te.setInventorySlotContents(i, null);
								return new UpdateStep(te, in);
							}
						}
					}
				}
			}
			else {
				Object[] arr = recipe.getInputArray();
				//ReikaJavaLibrary.pConsole("Looking for "+Arrays.toString(arr));
				for (int i = 0; i < 9; i++) {
					Object item = arr[i];
					ItemStack in = te.getStackInSlot(i);
					if (this.matches(item, in)) {
						//match
					}
					else {
						if (in != null) {
							if (this.recoverItem(in)) {
								te.setInventorySlotContents(i, null);
								return new UpdateStep(te, in);
							}
						}
						else {
							ItemStack ret = this.findItem(item, amt, false);
							//ReikaJavaLibrary.pConsole("Looking for "+item+", got "+ret);
							if (ret != null) {
								te.setInventorySlotContents(i, ret);
								return new UpdateStep(te, ret);
							}
						}
					}
				}
			}
		}
		return null;
	}

	private boolean triggerCrafting(World world, int x, int y, int z, TileEntityCastingTable te) {
		return te.trigger();
	}

	private boolean isRecipeReady(World world, int x, int y, int z, TileEntityCastingTable te) {
		return te.getActiveRecipe() == recipe;
	}

	public final int getX() {
		return ((TileEntity)tile).xCoord;
	}

	public final int getY() {
		return ((TileEntity)tile).yCoord;
	}

	public final int getZ() {
		return ((TileEntity)tile).zCoord;
	}

	@SideOnly(Side.CLIENT)
	public final void receiveUpdatePacket(World world, int[] data) {
		double x = data[0]+0.5;
		double y = data[1]+0.5;
		double z = data[2]+0.5;
		ItemStack is = new ItemStack(Item.getItemById(data[4]), 1, data[5]);
		boolean nbt = data[6] > 0;
		ElementTagCompound tag = ItemElementCalculator.instance.getValueForItem(is);
		if (nbt && tag != null) {
			tag.addTag(CrystalElement.PURPLE, 1);
			tag.addTag(CrystalElement.BLACK, 1);
		}
		for (int i = 0; i < 32; i++) {
			CrystalElement e = tag != null ? ReikaJavaLibrary.getRandomCollectionEntry(rand, tag.elementSet()) : null;
			int color = e != null ? e.getColor() : 0x00aaff;
			double px = ReikaRandomHelper.getRandomPlusMinus(x, 1);
			double py = ReikaRandomHelper.getRandomPlusMinus(y, 0.5);
			double pz = ReikaRandomHelper.getRandomPlusMinus(z, 1);
			EntityFX fx = null;
			float g = -(float)ReikaRandomHelper.getRandomPlusMinus(0.0625, 0.03125);
			switch(rand.nextInt(3)) {
				case 0:
					fx = new EntityCenterBlurFX(world, px, py, pz).setColor(color).setGravity(g);
					break;
				case 1:
					fx = new EntityLaserFX(CrystalElement.WHITE, world, px, py, pz).setColor(color).setGravity(g);
					break;
				case 2:
				default:
					fx = new EntityBlurFX(world, px, py, pz).setColor(color).setGravity(g);
					break;
			}
			fx.motionY = 0.03125+rand.nextDouble()*0.0625;
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	public void writeToNBT(NBTTagCompound NBT) {
		NBT.setInteger("recipes", recipesToGo);
		NBT.setInteger("cycles", recipeCycles);
	}

	public void readFromNBT(NBTTagCompound NBT) {
		recipesToGo = NBT.getInteger("recipes");
		recipeCycles = NBT.getInteger("cycles");
	}

	public void onBreak(World world) {

	}

	private static class UpdateStep {

		public final Coordinate loc;
		private final ItemStack item;

		private UpdateStep(TileEntity te, ItemStack is) {
			this(new Coordinate(te), is);
		}

		private UpdateStep(Coordinate c, ItemStack is) {
			loc = c;
			item = is;
		}

		public ItemStack getItem() {
			return item.copy();
		}

	}

}
