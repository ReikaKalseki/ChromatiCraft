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

import java.util.ArrayList;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Base.TileEntity.InventoriedChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Instantiable.ModInteract.BasicAEInterface;
import appeng.api.AEApi;
import appeng.api.implementations.ICraftingPatternItem;
import appeng.api.networking.IGridBlock;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IMachineSet;
import appeng.api.networking.crafting.ICraftingMedium;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.networking.crafting.ICraftingProviderHelper;
import appeng.api.networking.events.MENetworkCraftingPatternChange;
import appeng.api.util.AECableType;

@Strippable(value={"appeng.api.networking.IGridHost", "appeng.api.networking.crafting.ICraftingProvider"})
public class TileEntityPatternCache extends InventoriedChromaticBase implements IGridHost, ICraftingProvider {

	public static final int SIZE = 72;

	private Object aeGridBlock;
	private Object aeGridNode;

	private TileEntityPatternCache() {

		if (ModList.APPENG.isLoaded()) {
			aeGridBlock = new BasicAEInterface(this, this.getTile().getCraftedProduct());
			aeGridNode = AEApi.instance().createGridNode((IGridBlock)aeGridBlock);
		}
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {

	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		if (ModList.APPENG.isLoaded())
			this.updateAE();
	}

	@ModDependent(ModList.APPENG)
	private void updateAE() {
		((IGridNode)aeGridNode).getGrid().postEvent(new MENetworkCraftingPatternChange(this, (IGridNode)aeGridNode));
	}

	@ModDependent(ModList.APPENG)
	private ArrayList<ICraftingMedium> getValidPushables() {
		ArrayList<ICraftingMedium> li = new ArrayList();
		IMachineSet set = ((IGridNode)aeGridNode).getGrid().getMachines(IGridHost.class);
		for (IGridNode n : set) {
			IGridHost igh = n.getMachine();
			if (igh instanceof ICraftingMedium && igh instanceof TileEntity) {
				if (this.isValidTarget((TileEntity)igh)) {
					li.add((ICraftingMedium)igh);
				}
			}
		}
		return li;
	}

	private boolean isValidTarget(TileEntity te) {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			TileEntity tile = te.worldObj.getTileEntity(te.xCoord+dir.offsetX, te.yCoord+dir.offsetY, te.zCoord+dir.offsetZ);
			if (tile.getClass().getSimpleName().equals("TileMolecularAssembler")) {
				return true;
			}
		}
		return false;
	}

	@Override
	@ModDependent(ModList.APPENG)
	public boolean pushPattern(ICraftingPatternDetails details, InventoryCrafting table) {
		ArrayList<ICraftingMedium> li = this.getValidPushables();
		for (ICraftingMedium icm : li) {
			if (!icm.isBusy()) {
				icm.pushPattern(details, table);
			}
		}
		return false;
	}

	@Override
	@ModDependent(ModList.APPENG)
	public void provideCrafting(ICraftingProviderHelper tracker) {
		for (int i = 0; i < SIZE; i++) {
			ItemStack is = inv[i];
			if (this.isPattern(is)) {
				tracker.addCraftingOption(this, ((ICraftingPatternItem)is.getItem()).getPatternForItem(is, worldObj));
			}
		}
	}

	private boolean isPattern(ItemStack is) {
		return is != null && ModList.APPENG.isLoaded() && is.getItem() instanceof ICraftingPatternItem;
	}

	@Override
	@ModDependent(ModList.APPENG)
	public boolean isBusy() {
		ArrayList<ICraftingMedium> li = this.getValidPushables();
		for (ICraftingMedium icm : li) {
			if (!icm.isBusy())
				return false;
		}
		return true;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack is, int side) {
		return side == 0;
	}

	@Override
	public int getSizeInventory() {
		return SIZE;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is) {
		return this.isPattern(is);
	}

	@Override
	@ModDependent(ModList.APPENG)
	public IGridNode getGridNode(ForgeDirection dir) {
		return (IGridNode)aeGridNode;
	}

	@Override
	@ModDependent(ModList.APPENG)
	public AECableType getCableConnectionType(ForgeDirection dir) {
		return AECableType.GLASS;
	}

	@Override
	public void securityBreak() {

	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.PATTERNS;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	protected void onInvalidateOrUnload(World world, int x, int y, int z, boolean invalid) {
		super.onInvalidateOrUnload(world, x, y, z, invalid);
		if (ModList.APPENG.isLoaded() && aeGridNode != null)
			((IGridNode)aeGridNode).destroy();
	}

}
