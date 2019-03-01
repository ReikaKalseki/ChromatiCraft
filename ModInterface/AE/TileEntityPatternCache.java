/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface.AE;

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
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Instantiable.ModInteract.BasicAEInterface;

import appeng.api.AEApi;
import appeng.api.implementations.ICraftingPatternItem;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridBlock;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IMachineSet;
import appeng.api.networking.crafting.ICraftingMedium;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.networking.crafting.ICraftingProviderHelper;
import appeng.api.networking.events.MENetworkCraftingPatternChange;
import appeng.api.parts.IPart;
import appeng.api.parts.IPartHost;
import appeng.api.util.AECableType;
import appeng.api.util.DimensionalCoord;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

@Strippable(value={"appeng.api.networking.IGridHost", "appeng.api.networking.crafting.ICraftingProvider"})
public class TileEntityPatternCache extends InventoriedChromaticBase implements IGridHost, ICraftingProvider {

	public static final int SIZE = 72;

	private Object aeGridBlock;
	private Object aeGridNode;

	private final StepTimer updateTimer = new StepTimer(50);

	public TileEntityPatternCache() {

		if (ModList.APPENG.isLoaded() && FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
			aeGridBlock = new BasicAEInterface(this, this.getTile().getCraftedProduct());
			aeGridNode = AEApi.instance().createGridNode((IGridBlock)aeGridBlock);
		}
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (ModList.APPENG.isLoaded() && !world.isRemote) {
			updateTimer.update();
			if (updateTimer.checkCap()) {
				aeGridNode = AEApi.instance().createGridNode((IGridBlock)aeGridBlock);
				this.updateAE();
			}
		}
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		if (ModList.APPENG.isLoaded() && aeGridNode != null && !world.isRemote)
			this.updateAE();
	}

	@ModDependent(ModList.APPENG)
	private void updateAE() {
		if (aeGridNode != null)
			((IGridNode)aeGridNode).updateState();

		IGrid grid = ((IGridNode)aeGridNode).getGrid();
		if (grid != null)
			grid.postEvent(new MENetworkCraftingPatternChange(this, (IGridNode)aeGridNode));
	}

	@ModDependent(ModList.APPENG)
	private ArrayList<ICraftingMedium> getValidPushables() {
		ArrayList<ICraftingMedium> li = new ArrayList();
		IMachineSet set = ((IGridNode)aeGridNode).getGrid().getMachines(IGridHost.class);
		for (IGridNode n : set) {
			IGridHost igh = n.getMachine();
			if (igh instanceof ICraftingMedium) {
				if (this.isValidTarget(igh)) {
					li.add((ICraftingMedium)igh);
				}
			}
		}
		return li;
	}

	@ModDependent(ModList.APPENG)
	private boolean isValidTarget(IGridHost igh) {
		if (igh instanceof IPart) {
			IPart ip = (IPart)igh;
			DimensionalCoord dc = ip.getGridNode().getGridBlock().getLocation();
			TileEntity te = dc.getWorld().getTileEntity(dc.x, dc.y, dc.z);
			if (te instanceof IPartHost) {
				IPartHost iph = (IPartHost)te;
				for (int i = 0; i < 6; i++) {
					ForgeDirection dir = dirs[i];
					IPart p = iph.getPart(dir);
					if (p == ip) {
						TileEntity tile = te.worldObj.getTileEntity(te.xCoord+dir.offsetX, te.yCoord+dir.offsetY, te.zCoord+dir.offsetZ);
						if (this.isAssemblerTile(tile)) {
							return true;
						}
					}
				}
			}
		}
		else if (igh instanceof TileEntity) {
			TileEntity te = (TileEntity)igh;
			for (int i = 0; i < 6; i++) {
				ForgeDirection dir = dirs[i];
				TileEntity tile = te.worldObj.getTileEntity(te.xCoord+dir.offsetX, te.yCoord+dir.offsetY, te.zCoord+dir.offsetZ);
				if (this.isAssemblerTile(tile)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isAssemblerTile(TileEntity te) {
		return te.getClass().getSimpleName().equals("TileMolecularAssembler");
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

	@Override
	protected void onSlotSet(int slot, ItemStack is) {
		if (ModList.APPENG.isLoaded() && !worldObj.isRemote)
			this.updateAE();
	}

}
