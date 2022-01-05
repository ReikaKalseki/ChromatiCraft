/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Processing;

import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.oredict.OreDictionary;

import Reika.ChromatiCraft.API.Interfaces.CrystalFurnaceMultiplier;
import Reika.ChromatiCraft.Auxiliary.Interfaces.OperationInterval;
import Reika.ChromatiCraft.Auxiliary.Interfaces.VariableTexture;
import Reika.ChromatiCraft.Base.TileEntity.InventoriedRelayPowered;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Acquisition.TileEntityCollector;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Instantiable.HybridTank;
import Reika.DragonAPI.Interfaces.Block.CraftableStone;
import Reika.DragonAPI.Interfaces.Registry.OreType;
import Reika.DragonAPI.Interfaces.Registry.OreType.OreRarity;
import Reika.DragonAPI.Libraries.ReikaFluidHelper;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaOreHelper;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;
import Reika.DragonAPI.ModInteract.ItemStackRepository;
import Reika.DragonAPI.ModInteract.ItemHandlers.AppEngHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.HardOresHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.ThaumItemHelper;
import Reika.DragonAPI.ModRegistry.ModOreList;
import Reika.DragonAPI.ModRegistry.ModWoodList;
import Reika.GeoStrata.Registry.RockTypes;
import Reika.RotaryCraft.Auxiliary.RecipeManagers.ExtractorModOres;

import buildcraft.api.transport.IPipeConnection;
import buildcraft.api.transport.IPipeTile.PipeType;

@Strippable(value = {"buildcraft.api.transport.IPipeConnection"})
public class TileEntityCrystalFurnace extends InventoriedRelayPowered implements IFluidHandler, IPipeConnection, OperationInterval,
VariableTexture {

	private static final ElementTagCompound smelt = new ElementTagCompound();

	public static final int MULTIPLY = 2;

	public int smeltTimer;

	private float xp;

	private final HybridTank tank = new HybridTank("crystalfurn", 4000);

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);
		if (this.canSmelt()) {
			smeltTimer += this.getSmeltSpeed();
			if (smeltTimer >= this.getSmeltTime()) {
				this.smelt();
				smeltTimer = 0;
			}
		}
		else {
			smeltTimer = 0;
		}
	}

	public int getSmeltSpeed() {
		return 1+energy.getTotalEnergy()/12000;
	}

	public int getSmeltTime() {
		return Math.max(5, 200-this.getEnergy(CrystalElement.LIGHTBLUE)/100);
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
		return slot == 0 && FurnaceRecipes.smelting().getSmeltingResult(is) != null;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.FURNACE;
	}

	private boolean canSmelt() {
		if (inv[0] == null)
			return false;
		if (!energy.containsAtLeast(smelt))
			return false;
		ItemStack out = FurnaceRecipes.smelting().getSmeltingResult(inv[0]);
		if (out == null)
			return false;
		out = out.copy();
		out.stackSize *= this.getMultiplyRate(inv[0], out);
		if (inv[1] == null)
			return true;
		if (!ReikaItemHelper.areStacksCombinable(out, inv[1], this.getInventoryStackLimit()))
			return false;
		return true;
	}

	private void smelt() {
		ItemStack is = FurnaceRecipes.smelting().getSmeltingResult(inv[0]).copy();
		ElementTagCompound tag = getSmeltingCost(inv[0], is);
		is.stackSize *= this.getMultiplyRate(inv[0], is);
		ReikaInventoryHelper.addOrSetStack(is, inv, 1);
		xp += FurnaceRecipes.smelting().func_151398_b(inv[1])*6*getXPModifier(inv[0]);
		if (xp >= TileEntityCollector.XP_PER_CHROMA) {
			int amt = (int)(xp/TileEntityCollector.XP_PER_CHROMA);
			tank.addLiquid(amt, FluidRegistry.getFluid("chroma"));
			xp = 0;
		}
		ReikaInventoryHelper.decrStack(0, inv);
		tag.addValueToColor(CrystalElement.LIGHTBLUE, 250);
		this.drainEnergy(tag);
	}

	public static float getXPModifier(ItemStack in) {
		if (ReikaBlockHelper.isOre(in))
			return 2;
		else if (ModList.ROTARYCRAFT.isLoaded() && ExtractorModOres.isOreFlake(in)) {
			return 2;
		}
		else if (in.getItem() instanceof ItemFood) {
			return 0.125F;
		}
		else if (ReikaItemHelper.matchStackWithBlock(in, Blocks.log) || ReikaItemHelper.matchStackWithBlock(in, Blocks.log2)) {
			return 1.125F;
		}
		else if (ModWoodList.isModWood(in)) {
			return 1.125F;
		}
		else if (ModList.THAUMCRAFT.isLoaded() && in.getItem() == ThaumItemHelper.ItemEntry.NUGGETCLUSTER.getItem().getItem()) {
			return 4;
		}
		else if (in.getDisplayName() != null && in.getDisplayName().toLowerCase(Locale.ENGLISH).contains("cobblestone"))
			return 0;
		return 1;
	}

	public static ElementTagCompound getSmeltingCost(ItemStack in, ItemStack out) {
		ElementTagCompound tag = smelt.copy();
		if (ReikaBlockHelper.isOre(in))
			tag.scale(1.5F);
		else if (HardOresHandler.instance.isLoaded() && HardOresHandler.instance.getRootOre(in) != null)
			tag.scale(4);
		else if (ModList.ROTARYCRAFT.isLoaded() && ExtractorModOres.isOreFlake(in)) {
			tag.scale(2F);
			OreType ore = ExtractorModOres.getOreFromExtract(in);
			if (ore.getRarity() == OreRarity.RARE) {
				tag.scale(1.5F);
			}
		}
		else if (in.getItem() instanceof ItemFood) {
			tag.scale(0.5F);
		}
		else if (ReikaItemHelper.matchStackWithBlock(in, Blocks.log) || ReikaItemHelper.matchStackWithBlock(in, Blocks.log2)) {
			tag.scale(0.75F);
		}
		else if (ModWoodList.isModWood(in)) {
			tag.scale(0.75F);
		}
		return tag.scale((float)Math.pow(getMultiplyRate(in, out)/2F, 2));
	}

	public static int getMultiplyRate(ItemStack in, ItemStack out) {
		if (in.getItem() instanceof CrystalFurnaceMultiplier) {
			int ret = ((CrystalFurnaceMultiplier)in.getItem()).getMultiplyRateAsInput(in, out);
			if (ret > 0)
				return ret;
		}
		if (out.getItem() instanceof CrystalFurnaceMultiplier) {
			int ret = ((CrystalFurnaceMultiplier)out.getItem()).getMultiplyRateAsOutput(out, in);
			if (ret > 0)
				return ret;
		}
		return calcMultiplyRate(in, out);//Integer get = multiply
	}

	private static int calcMultiplyRate(ItemStack in, ItemStack out) {
		if (ChromaBlocks.PYLONSTRUCT.match(out)) {
			return 1;
		}
		else if (in.getDisplayName() != null && in.getDisplayName().toLowerCase(Locale.ENGLISH).contains("cobblestone")) {
			return 1;
		}
		else if (ModList.GENDUSTRY.isLoaded() && in.getDisplayName() != null && in.getDisplayName().toLowerCase(Locale.ENGLISH).contains("gene")) {
			return 1;
		}
		else if (Block.getBlockFromItem(in.getItem()) instanceof CraftableStone || Block.getBlockFromItem(out.getItem()) instanceof CraftableStone) {
			return 1;
		}
		else if (ModList.GREGTECH.isLoaded() && (ReikaItemHelper.getRegistrantMod(in).toLowerCase(Locale.ENGLISH).contains("gregapi") || ReikaItemHelper.getRegistrantMod(in).toLowerCase(Locale.ENGLISH).contains("gregtech"))) {
			if (in.getItem() instanceof ItemFood)
				return MULTIPLY;
			int[] ids = OreDictionary.getOreIDs(in);
			for (int i = 0; i < ids.length; i++) {
				String name = OreDictionary.getOreName(ids[i]);
				if (name.startsWith("crushed") || name.startsWith("ore"))
					return MULTIPLY;
			}
			return 1;
		}
		else if (ModList.GEOSTRATA.isLoaded() && RockTypes.getTypeFromID(Block.getBlockFromItem(in.getItem())) != null) {
			//ReikaItemHelper.matchStacks(out, RockTypes.getTypeFromID(Block.getBlockFromItem(in.getItem())).getItem(RockShapes.SMOOTH)))
			return 1;
		}
		else if (ModList.ROTARYCRAFT.isLoaded() && ReikaItemHelper.matchStacks(in, ItemStackRepository.instance.getItem(ModList.ROTARYCRAFT, "ironscrap")))
			return 1;
		else if (ModList.THAUMCRAFT.isLoaded() && in.getItem() == ThaumItemHelper.ItemEntry.NUGGETCLUSTER.getItem().getItem()) {
			return 2*MULTIPLY;
		}
		else if (ModList.THAUMCRAFT.isLoaded() && in.getItem() == ThaumItemHelper.ItemEntry.COIN.getItem().getItem()) {
			return 1;
		}
		else if (ModList.APPENG.isLoaded() && ReikaItemHelper.matchStackWithBlock(in, AppEngHandler.getInstance().chargedCertusOre)) {
			return 4*MULTIPLY;
		}
		else if (ReikaItemHelper.getRegistrantMod(in).equals("GardenStuff") && Item.itemRegistry.getNameForObject(in.getItem()).equals("wrought_iron_ingot")) {
			return 1;
		}
		else if (ReikaBlockHelper.isOre(in)) {
			int ret = 2*MULTIPLY;
			OreType ore = ReikaOreHelper.getEntryByOreDict(in);
			if (ore == null)
				ore = ModOreList.getModOreFromOre(in);
			if (ore != null) {
				if (ore.getRarity() == OreRarity.RARE) {
					ret *= 2;
				}
				ret *= ore.getDropCount();
			}
			return ret;
		}
		else if (HardOresHandler.instance.isLoaded() && HardOresHandler.instance.getRootOre(in) != null) {
			in = HardOresHandler.instance.getRootOre(in);
			out = FurnaceRecipes.smelting().getSmeltingResult(in);
			if (out != null && !ReikaItemHelper.matchStacks(in, out))
				return HardOresHandler.instance.getYield(in)*getMultiplyRate(in, out);
		}
		int[] ids = OreDictionary.getOreIDs(in);
		for (int i = 0; i < ids.length; i++) {
			String name = OreDictionary.getOreName(ids[i]);
			if (name.startsWith("dust")) //exploits
				return 1;
			else if (name.equalsIgnoreCase("cobblestone")) //exploits
				return 1;
		}
		return MULTIPLY;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		smeltTimer = NBT.getInteger("time");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setInteger("time", smeltTimer);

		tank.writeToNBT(NBT);
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		//xp = NBT.getFloat("xp");

		tank.readFromNBT(NBT);
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		//NBT.setFloat("xp", xp);
	}

	static {
		smelt.addTag(CrystalElement.ORANGE, 200);
		smelt.addTag(CrystalElement.YELLOW, 40);
		smelt.addTag(CrystalElement.PURPLE, 100);
	}

	@Override
	public ElementTagCompound getRequiredEnergy() {
		ElementTagCompound tag = smelt.copy();
		tag.addTag(CrystalElement.LIGHTBLUE, 500);
		return tag;
	}

	@Override
	public int getMaxStorage(CrystalElement e) {
		return e == CrystalElement.LIGHTBLUE ? 60000 : 400000;
	}

	public static ElementTagCompound smeltTags() {
		return smelt.copy();
	}

	public int getCookProgressScaled(int a) {
		return smeltTimer * a / this.getSmeltTime();
	}

	@Override
	public boolean isAcceptingColor(CrystalElement e) {
		return smelt.contains(e) || e == CrystalElement.LIGHTBLUE;
	}

	@Override
	protected boolean canReceiveFrom(CrystalElement e, ForgeDirection dir) {
		return true;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		return this.canDrain(from, resource.getFluid()) ? tank.drain(resource.amount, doDrain) : null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return this.canDrain(from, null) ? tank.drain(maxDrain, doDrain) : null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return ReikaFluidHelper.isFluidDrainableFromTank(fluid, tank);
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[]{tank.getInfo()};
	}

	@Override
	@ModDependent(ModList.BCTRANSPORT)
	public ConnectOverride overridePipeConnection(PipeType type, ForgeDirection with) {
		return ConnectOverride.DEFAULT;
	}

	@Override
	public int getIconState(int side) {
		return side > 1 && this.canSmelt() ? 1 : 0;
	}

	@Override
	public float getOperationFraction() {
		return !this.canSmelt() ? 0 : smeltTimer/(float)(Math.max(1, this.getSmeltTime()));
	}

	@Override
	public OperationState getState() {
		return inv[0] != null && FurnaceRecipes.smelting().getSmeltingResult(inv[0]) != null ? (energy.containsAtLeast(smelt) ? OperationState.RUNNING : OperationState.PENDING) : OperationState.INVALID;
	}

	@Override
	public boolean hasWork() {
		return this.getState() == OperationState.RUNNING;
	}

}
