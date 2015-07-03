/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Base.TileEntity.InventoriedChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import appeng.api.config.Actionable;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingLink;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.networking.crafting.ICraftingProviderHelper;
import appeng.api.networking.crafting.ICraftingRequester;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.util.AECableType;

import com.google.common.collect.ImmutableSet;

@Strippable(value={"appeng.api.networking.IGridHost", "appeng.api.networking.crafting.ICraftingProvider", "appeng.api.networking.crafting.ICraftingRequester"})
public class TileEntityPatternCache extends InventoriedChromaticBase implements IGridHost, ICraftingProvider, ICraftingRequester {

	public static final int SIZE = 72;

	//private Object tracker;
	//private Object duality;

	private TileEntityPatternCache() {
		//tracker = new MultiCraftingTracker(this, SIZE);
		//duality = new DualityInterface(this);
	}

	@Override
	public boolean pushPattern(ICraftingPatternDetails patternDetails, InventoryCrafting table) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isBusy() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IGridNode getActionableNode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canExtractItem(int p_102008_1_, ItemStack p_102008_2_,
			int p_102008_3_) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getSizeInventory() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getInventoryStackLimit() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ImmutableSet<ICraftingLink> getRequestedJobs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IAEItemStack injectCraftedItems(ICraftingLink link,
			IAEItemStack items, Actionable mode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void jobStateChange(ICraftingLink link) {
		// TODO Auto-generated method stub

	}

	@Override
	public void provideCrafting(ICraftingProviderHelper craftingTracker) {
		// TODO Auto-generated method stub

	}

	@Override
	public IGridNode getGridNode(ForgeDirection dir) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AECableType getCableConnectionType(ForgeDirection dir) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void securityBreak() {
		// TODO Auto-generated method stub

	}

	@Override
	public ChromaTiles getTile() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {
		// TODO Auto-generated method stub

	}

}
