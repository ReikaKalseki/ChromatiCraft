/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Recipe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import Reika.ChromatiCraft.Auxiliary.ChromaFX;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.Interfaces.FocusAcceleratable;
import Reika.ChromatiCraft.Auxiliary.Interfaces.OperationInterval;
import Reika.ChromatiCraft.Auxiliary.Interfaces.OwnedTile;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.PoolRecipes;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.PoolRecipes.PoolRecipe;
import Reika.ChromatiCraft.Base.TileEntity.InventoriedRelayPowered;
import Reika.ChromatiCraft.Block.BlockActiveChroma.TileEntityChroma;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityChromaFluidFX;
import Reika.ChromatiCraft.TileEntity.Auxiliary.TileEntityFocusCrystal;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Instantiable.HybridTank;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Maps.ItemHashMap;
import Reika.DragonAPI.Interfaces.TileEntity.BreakAction;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.ReikaNBTHelper;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

import buildcraft.api.transport.IPipeConnection;
import buildcraft.api.transport.IPipeTile.PipeType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


@Strippable(value={"buildcraft.api.transport.IPipeConnection"})
public class TileEntityChromaCrafter extends InventoriedRelayPowered implements IFluidHandler, OwnedTile, IPipeConnection, OperationInterval,
BreakAction, FocusAcceleratable {

	public static final int CRAFTING_DURATION = 300;

	private static final ElementTagCompound required = new ElementTagCompound();

	static {
		required.addTag(CrystalElement.BLACK, 1000);
		required.addTag(CrystalElement.GRAY, 500);
		required.addTag(CrystalElement.PURPLE, 2500);
		required.addTag(CrystalElement.CYAN, 200);
		required.addTag(CrystalElement.BROWN, 200);
	}

	private PoolRecipe recipe = null;
	private int recipeTick = 0;
	private int hasEtherBerries = 0;
	private ArrayList<ItemStack> recipeItems = new ArrayList();

	public static final int CAPACITY = 3000;

	private final HybridTank tank = new HybridTank("chromacraft", CAPACITY);

	public int getChromaLevel() {
		return tank.getLevel();
	}

	public PoolRecipe getActiveRecipe() {
		return recipe;
	}

	public ItemHashMap<Integer> getCurrentItems() {
		ItemHashMap<Integer> ret = new ItemHashMap();
		for (ItemStack is : recipeItems) {
			ret.add(is, is.stackSize);
		}
		return ret;
	}

	public int getBerryCount() {
		return hasEtherBerries;
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setInteger("etherb", hasEtherBerries);
		NBT.setInteger("recipe", recipeTick);
		tank.writeToNBT(NBT);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		hasEtherBerries = NBT.getInteger("etherb");
		recipeTick = NBT.getInteger("recipe");
		tank.readFromNBT(NBT);
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		ReikaNBTHelper.writeCollectionToNBT(recipeItems, NBT, "recipeItems");
		NBT.setString("recipetype", recipe != null ? recipe.ID : "none");
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		ReikaNBTHelper.readCollectionFromNBT(recipeItems, NBT, "recipeItems");
		recipe = PoolRecipes.instance.getByID(NBT.getString("recipetype"));
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		if (inv[0] != null) {
			//ReikaJavaLibrary.pConsole(inv[0]);
			if (ReikaItemHelper.matchStacks(inv[0], ChromaStacks.etherBerries)) {
				hasEtherBerries += inv[0].stackSize;
				inv[0] = null;
				//ReikaJavaLibrary.pConsole(hasEtherBerries+" / "+recipeItems.size()+":"+recipeItems+" > "+recipe);
				this.syncAllData(true);
			}
			else if (recipe == null) {
				recipeItems.add(inv[0]);
				recipeItems = ReikaItemHelper.collateItemList(recipeItems);
				inv[0] = null;
				recipe = this.checkRecipe();
				//ReikaJavaLibrary.pConsole(hasEtherBerries+" / "+recipeItems.size()+":"+recipeItems+" > "+recipe);
				if (recipe != null) {
					recipeTick = this.getCraftingDuration();
					this.syncAllData(true);
				}
			}
			//ReikaJavaLibrary.pConsole(hasEtherBerries+" ["+(inv[0] != null ? inv[0].getDisplayName() : "null")+"] / "+recipeItems.size()+":"+recipeItems+" > "+recipe);
		}

		if (recipeTick > 0) {
			this.onRecipeTick(world, x, y, z);
		}

		if (world.isRemote && this.hasStructure()) {
			ChromaFX.doFocusCrystalParticles(world, x, y, z, this);
		}
	}

	private int getCraftingDuration() {
		int base = CRAFTING_DURATION;
		float f = this.getAccelerationFactor();
		return f >= 1 ? (int)(base/f) : base;
	}

	private PoolRecipe checkRecipe() {
		return PoolRecipes.instance.getPoolRecipe(recipeItems);
	}

	private void onRecipeTick(World world, int x, int y, int z) {
		if (energy.containsAtLeast(required) && tank.getLevel() == 3000 && this.hasStructure())
			recipeTick--;
		if (recipeTick == 0) {
			if (!world.isRemote)
				this.craft();
		}
		else {
			if (world.isRemote)
				this.recipeParticles(world, x, y, z);
		}
	}

	public boolean hasStructure() {
		for (int i = 1; i <= 2; i++) {
			if (worldObj.getBlock(xCoord, yCoord+i, zCoord) != ChromaBlocks.STRUCTSHIELD.getBlockInstance() || worldObj.getBlockMetadata(xCoord, yCoord+i, zCoord)%8 != BlockType.GLASS.ordinal())
				return false;
		}
		return true;
	}

	@SideOnly(Side.CLIENT)
	private void recipeParticles(World world, int x, int y, int z) {
		double[] v = ReikaPhysicsHelper.polarToCartesian(ReikaRandomHelper.getRandomBetween(0.03125, 0.125), rand.nextDouble()*90, rand.nextDouble()*360);
		EntityFX fx = new EntityChromaFluidFX(world, x+rand.nextDouble(), y+rand.nextDouble()*3, z+rand.nextDouble(), v[0], v[1], v[2]).setGravity(0.125F);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}

	private void craft() {
		recipeTick = 0;
		Collection<ItemStack> li = recipe.getInputs();
		li.add(recipe.getMainInput());
		for (ItemStack is : li) {
			int val = is.stackSize;
			Iterator<ItemStack> it = recipeItems.iterator();
			while (it.hasNext()) {
				ItemStack is2 = it.next();
				if (ReikaItemHelper.matchStacks(is, is2)) {
					int rem = Math.min(val, is2.stackSize);
					is2.stackSize -= rem;
					val -= rem;
					if (is2.stackSize == 0) {
						it.remove();
						break;
					}
				}
			}
		}
		ItemStack out = recipe.getOutput();
		if (recipe.allowDoubling() && hasEtherBerries >= TileEntityChroma.ETHER_SATURATION) {
			out.stackSize *= 2;
			hasEtherBerries -= TileEntityChroma.ETHER_SATURATION;
		}
		EntityPlayer ep = this.getPlacer();
		if (ep != null && !ReikaPlayerAPI.isFake(ep) && Chromabilities.DOUBLECRAFT.enabledOn(ep))
			out.stackSize *= 2;
		//recipeItems.clear();
		//hasEtherBerries = false;
		ReikaInventoryHelper.addOrSetStack(out, inv, 1);
		recipe = null;
		tank.removeLiquid(3000);
		energy.subtract(required);
		ChromaSounds.INFUSE.playSoundAtBlock(this);
		this.syncAllData(true);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack is, int side) {
		return slot == 1;
	}

	@Override
	public int getSizeInventory() {
		return 2;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is) {
		return slot == 0 && this.hasStructure() && this.canAcceptMoreOf(is);
	}

	private boolean canAcceptMoreOf(ItemStack is) {
		return ReikaItemHelper.matchStacks(is, ChromaStacks.etherBerries) ? this.getBerryCount()+is.stackSize <= 512 : this.getItemCount(is)+is.stackSize <= 64;
	}

	private int getItemCount(ItemStack is) {
		int ret = 0;
		for (ItemStack in : recipeItems) {
			if (ReikaItemHelper.matchStacks(in, is))
				ret += in.stackSize;
		}
		return ret;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		FluidStack fs = new FluidStack(resource.getFluid(), Math.min(25, resource.amount)); //25mB/t limit
		return this.canFill(from, resource.getFluid()) ? tank.fill(fs, doFill) : 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return from.offsetY == 0 && fluid == FluidRegistry.getFluid("chroma") && this.hasStructure();
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[]{tank.getInfo()};
	}

	@Override
	public int getMaxStorage(CrystalElement e) {
		return 10000;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.CHROMACRAFTER;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	protected boolean canReceiveFrom(CrystalElement e, ForgeDirection dir) {
		return dir != ForgeDirection.UP || tank.getLevel() <= 1000;
	}

	@Override
	public ElementTagCompound getRequiredEnergy() {
		return required.copy();
	}

	@Override
	public boolean isAcceptingColor(CrystalElement e) {
		return required.contains(e);
	}

	@Override
	@ModDependent(ModList.BCTRANSPORT)
	public ConnectOverride overridePipeConnection(PipeType type, ForgeDirection with) {
		if (type == PipeType.FLUID)
			return with.offsetY == 0 ? ConnectOverride.CONNECT : ConnectOverride.DISCONNECT;
		if (type == PipeType.ITEM)
			return with != ForgeDirection.UP ? ConnectOverride.CONNECT : ConnectOverride.DISCONNECT;
		return ConnectOverride.DISCONNECT;
	}

	@Override
	public boolean onlyAllowOwnersToUse() {
		return true;
	}

	@Override
	public float getOperationFraction() {
		if (recipe == null)
			return 0;
		return 1-(float)recipeTick/this.getCraftingDuration();
	}

	@Override
	public OperationState getState() {
		if (recipe == null)
			return OperationState.INVALID;
		return energy.containsAtLeast(required) ? OperationState.RUNNING : OperationState.PENDING;
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return ReikaAABBHelper.getBlockAABB(this).addCoord(0, 2, 0);
	}

	public boolean hasEtherBerries() {
		return hasEtherBerries > 0;
	}

	@Override
	public void breakBlock() {
		ReikaItemHelper.dropItems(worldObj, xCoord+0.5, yCoord+0.5, zCoord+0.5, recipeItems);
		for (int i = 0; i < hasEtherBerries; i++) {
			ReikaItemHelper.dropItem(worldObj, xCoord+0.5, yCoord+0.5, zCoord+0.5, ChromaStacks.etherBerries);
		}
	}

	@Override
	public float getAccelerationFactor() {
		return TileEntityFocusCrystal.getSummedFocusFactorDirect(this, this.getRelativeFocusCrystalLocations())/2; //caps at 6x for turbo, 4x for exq
	}

	@Override
	public float getMaximumAcceleratability() {
		return 4*TileEntityFocusCrystal.CrystalTier.TURBOCHARGED.efficiencyFactor;
	}

	@Override
	public float getProgressToNextStep() {
		return 0;
	}

	@Override
	public void recountFocusCrystals() {
		this.getAccelerationFactor();
	}

	@Override
	public Collection<Coordinate> getRelativeFocusCrystalLocations() {
		Collection<Coordinate> c = new ArrayList();
		int d = 2;
		for (int i = 2; i < 6; i++) {
			c.add(new Coordinate(dirs[i].offsetX*d, 0, dirs[i].offsetZ*d));
		}
		return c;
	}

	@Override
	public boolean hasWork() {
		return this.getState() == OperationState.RUNNING;
	}

}
