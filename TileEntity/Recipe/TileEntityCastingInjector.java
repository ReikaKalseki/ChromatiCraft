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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Auxiliary.CastingAutomationSystem;
import Reika.ChromatiCraft.Auxiliary.Interfaces.CastingAutomationBlock;
import Reika.ChromatiCraft.Auxiliary.Interfaces.VariableTexture;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.ChromatiCraft.Base.TileEntity.InventoriedChromaticBase;
import Reika.ChromatiCraft.Block.BlockCastingInjectorFocus.CastingInjectorAuxTile;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;

import appeng.api.networking.IGridNode;
import appeng.api.util.AECableType;

public class TileEntityCastingInjector extends InventoriedChromaticBase implements CastingAutomationBlock, VariableTexture {

	private static final HashSet<Coordinate> foci = new HashSet();

	static {
		for (int i = -4; i <= 4; i += 4) {
			for (int k = -4; k <= 4; k += 4) {
				if (i != 0 || k != 0) {
					foci.add(new Coordinate(i, 0, k));
				}
			}
		}
	}

	private CastingAutomationSystem handler;
	private Coordinate delegate = null;

	private final HashSet<Coordinate> localFoci = new HashSet();

	public TileEntityCastingInjector() {
		handler = new CastingAutomationSystem(this);
	}

	public void setInactive(TileEntityCastingAuto te) {
		delegate = new Coordinate(te);
	}

	public TileEntityCastingTable getTable() {
		TileEntity te = this.getAdjacentTileEntity(ForgeDirection.UP);
		return te instanceof TileEntityCastingTable ? (TileEntityCastingTable)te : null;
	}

	public Collection<CastingRecipe> getAvailableRecipes() {
		TileEntityCastingTable te = this.getTable();
		HashSet<CastingRecipe> rec = te != null ? te.getCompletedRecipes() : new HashSet();
		Iterator<CastingRecipe> it = rec.iterator();
		while (it.hasNext()) {
			CastingRecipe c = it.next();
			if (!c.canBeSimpleAutomated())
				it.remove();
		}
		return rec;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.INJECTOR;
	}

	@Override
	protected void onInvalidateOrUnload(World world, int x, int y, int z, boolean invalid) {
		super.onInvalidateOrUnload(world, x, y, z, invalid);
		handler.destroy();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (!world.isRemote) {
			if (delegate != null) {
				ChromaTiles c = ChromaTiles.getTile(world, delegate.xCoord, delegate.yCoord, delegate.zCoord);
				if (c != ChromaTiles.AUTOMATOR)
					delegate = null;
			}
			else {
				handler.tick(world);
			}

			if (this.getTicksExisted()%5 == 0)
				this.checkStructure(this.getTable());

			if (inv[0] != null) {
				int added = handler.pushItemToME(inv[0]);
				ReikaInventoryHelper.decrStack(0, this, added);
			}
		}
	}

	@Override
	public int getInjectionTickRate() {
		return 30;
	}

	@Override
	public boolean canRecursivelyRequest() {
		return false;
	}

	@Override
	public boolean isAbleToRun(TileEntityCastingTable te) {
		return localFoci.size() == foci.size();
	}

	private void checkStructure(TileEntityCastingTable te) {
		localFoci.clear();
		for (Coordinate c : foci) {
			Coordinate c2 = c.offset(te.xCoord, te.yCoord, te.zCoord);
			if (c2.getBlock(te.worldObj) == ChromaBlocks.INJECTORAUX.getBlockInstance()) {
				CastingInjectorAuxTile te2 = (CastingInjectorAuxTile)c2.getTileEntity(te.worldObj);
				te2.setTile(this);
				localFoci.add(c);
			}
		}
	}

	@Override
	public boolean canTriggerCrafting() {
		return false;
	}

	@Override
	public CastingAutomationSystem getAutomationHandler() {
		return handler;
	}

	@Override
	public void consumeEnergy(CastingRecipe c, TileEntityCastingTable te, ItemStack is) {

	}

	public boolean canCraft(World world, int x, int y, int z, TileEntityCastingTable te) {
		return te.isReadyToCraft() && te.getPlacerUUID() != null && te.getPlacerUUID().equals(this.getPlacerUUID());
	}

	public final UUID getPlacerUUID() {
		EntityPlayer ep = this.getPlacer();
		return ep != null ? ep.getUniqueID() : null;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	@ModDependent(ModList.APPENG)
	public IGridNode getGridNode(ForgeDirection dir) {
		return handler.getGridNode(dir);
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
	public boolean onlyAllowOwnersToUse() {
		return true;
	}

	@Override
	@ModDependent(ModList.APPENG)
	public IGridNode getActionableNode() {
		return handler.getActionableNode();
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack is, int side) {
		return true;
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is) {
		return true;
	}

	@Override
	public int getIconState(int side) {
		return side != 0 && this.getTable() != null ? 1 : 0;
	}

	public static Set<Coordinate> getFoci() {
		return Collections.unmodifiableSet(foci);
	}

	@Override
	public boolean canPlaceCentralItemForMultiRecipes() {
		return false;
	}

}
