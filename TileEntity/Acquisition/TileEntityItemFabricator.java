/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Acquisition;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import org.apache.commons.lang3.tuple.ImmutableTriple;

import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.Interfaces.OperationInterval;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.FabricationRecipes;
import Reika.ChromatiCraft.Base.TileEntity.InventoriedCrystalReceiver;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.InertItem;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityItemFabricator extends InventoriedCrystalReceiver implements OperationInterval {

	public int progress = 0;

	private static class Recipe {

		private final ItemStack output;
		private final ElementTagCompound energy;
		private final int duration;

		private Recipe(ElementTagCompound tag, ItemStack is) {
			energy = tag;
			output = is;
			duration = this.duration(energy);
		}

		private static int duration(ElementTagCompound energy) {
			return ReikaMathLibrary.roundUpToX(20, (int)Math.sqrt(energy.getTotalEnergy()));
		}
	}

	private Recipe recipe = null;
	private int craftingTick = 0;
	private EntityItem entity;

	private void setRecipe(ItemStack out) {
		if (out == null) {
			recipe = null;
			this.onRecipeChanged();
		}
		else if (recipe == null || !ReikaItemHelper.matchStacks(recipe.output, out) || craftingTick == 0) {
			ElementTagCompound tag = FabricationRecipes.recipes().getItemCost(out);
			if (tag != null) {
				recipe = new Recipe(tag, out);
				this.onRecipeChanged();
			}
		}
		entity = recipe != null ? new InertItem(worldObj, recipe.output) : null;
	}

	private void onRecipeChanged() {
		craftingTick = recipe != null ? recipe.duration : 0;
	}

	public ElementTagCompound getCurrentRequirements() {
		return recipe != null ? recipe.energy.copy() : null;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);
		//ReikaJavaLibrary.pConsole(FabricationRecipes.recipes().getItemsFabricableWith(ElementTagCompound.getUniformTag(5000)));
		if (!world.isRemote && this.getCooldown() == 0 && checkTimer.checkCap()) {
			this.checkAndRequest();
		}

		//ReikaJavaLibrary.pConsole(recipe.energy+" <"+craftingTick+"> "+energy, Side.SERVER);
		if (recipe != null && energy.containsAtLeast(recipe.energy) && craftingTick > 0) {
			this.onCraftingTick(world, x, y, z);
		}
	}

	@Override
	public void markDirty() {
		super.markDirty();
		this.setRecipe(inv[0]);
	}

	@Override
	public void onFirstTick(World world, int x, int y, int z) {
		super.onFirstTick(world, x, y, z);
		this.markDirty();
		//ReikaJavaLibrary.pConsole(craftingTick+":"+inv[0]+">"+recipe+","+entity);
	}

	private void checkAndRequest() {
		for (int i = 0; i < CrystalElement.elements.length; i++) {
			CrystalElement e = CrystalElement.elements[i];
			int capacity = this.getMaxStorage(e);
			int space = capacity-this.getEnergy(e);
			if (space > 0) {
				this.requestEnergy(e, space);
			}
		}
	}

	private void onCraftingTick(World world, int x, int y, int z) {
		if (world.isRemote)
			this.craftingFX(world, x, y, z);
		craftingTick--;
		progress = recipe.duration-craftingTick;
		if (craftingTick == 0) {
			if (this.canCraft()) {
				this.craft(world, x, y, z);
			}
			else {
				craftingTick = 5;
			}
		}
	}

	private boolean canCraft() {
		if (inv[1] == null)
			return true;
		return ReikaItemHelper.matchStacks(inv[1], recipe.output) && inv[1].stackSize+recipe.output.stackSize <= inv[1].getMaxStackSize();
	}

	private void craft(World world, int x, int y, int z) {
		ReikaInventoryHelper.addOrSetStack(recipe.output.copy(), inv, 1);
		energy.subtract(recipe.energy);
		progress = 0;
		this.markDirty();
	}

	@SideOnly(Side.CLIENT)
	private void craftingFX(World world, int x, int y, int z) {

	}

	public int getProgressScaled(int a) {
		return recipe != null ? a * progress / recipe.duration : 0;
	}

	public ItemStack getRecipe() {
		return recipe != null ? recipe.output.copy() : null;
	}

	@Override
	public int getReceiveRange() {
		return 16;
	}

	@Override
	public boolean isConductingElement(CrystalElement e) {
		return true;
	}

	@Override
	public int maxThroughput() {
		return 500;
	}

	@Override
	public boolean canConduct() {
		return true;
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
		return slot == 0 && ReikaItemHelper.matchStacks(is, ChromaStacks.chromaDust);
	}

	@Override
	public int getMaxStorage(CrystalElement e) {
		return FabricationRecipes.recipes().getMaximumCost()*3/2;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.FABRICATOR;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		craftingTick = NBT.getInteger("craft");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setInteger("craft", craftingTick);
	}

	@Override
	public ImmutableTriple<Double, Double, Double> getTargetRenderOffset(CrystalElement e) {
		double ang = Math.toRadians(e.ordinal()*22.5D);
		double r = 1.5;
		double dx = r*Math.sin(ang);
		double dy = 0.55;
		double dz = r*Math.cos(ang);
		return new ImmutableTriple(dx, dy, dz);
	}

	@Override
	public double getIncomingBeamRadius() {
		return 0.25;
	}

	public EntityItem getEntityItem() {
		return entity;
	}

	@Override
	public ElementTagCompound getRequestedTotal() {
		return craftingTick > 0 && recipe != null ? recipe.energy.copy() : null;
	}

	@Override
	public double getMaxRenderDistanceSquared() {
		return super.getMaxRenderDistanceSquared()*4;
	}

	@Override
	public float getOperationFraction() {
		return recipe == null ? 0 : 1F-craftingTick/(float)recipe.duration;
	}

	@Override
	public OperationState getState() {
		return recipe == null ? OperationState.INVALID : (energy.containsAtLeast(recipe.energy) ? OperationState.RUNNING : OperationState.PENDING);
	}

}
