/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity;

import Reika.ChromatiCraft.Auxiliary.RecipeManagers.RecipesCastingTable;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.RecipesCastingTable.CastingRecipe;
import Reika.ChromatiCraft.Base.TileEntity.InventoriedCrystalReceiver;
import Reika.ChromatiCraft.Magic.CrystalNetworker;
import Reika.ChromatiCraft.Magic.CrystalTarget;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class TileEntityCastingTable extends InventoriedCrystalReceiver {

	private CastingRecipe activeRecipe = null;

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.TABLE;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);
		if (!world.isRemote && this.getTicksExisted() == 1) {
			this.evaluateRecipeAndRequest();
		}

		if (activeRecipe != null) {
			if (worldObj.isRemote) {
				this.spawnParticles(world, x, y, z);
			}

			ElementTagCompound req = activeRecipe.getRequiredAura();
			if (energy.containsAtLeast(req)) {
				this.craft();
				energy.subtract(req);
			}
		}
		else {

		}

		//ReikaJavaLibrary.pConsole(energy, Side.SERVER);
	}

	private void craft() {
		inv[0] = activeRecipe.getOutput();

	}

	private void spawnParticles(World world, int x, int y, int z) {

	}

	@Override
	public void markDirty() {
		super.markDirty();

		CastingRecipe r = this.getValidRecipe();
		this.changeRequests(r);
	}

	private void changeRequests(CastingRecipe r) {
		if (r == null) {
			CrystalNetworker.instance.breakPaths(this);
		}
		else if (r != activeRecipe) {
			ElementTagCompound tag = r.getRequiredAura();
			tag.subtract(energy);
			for (CrystalElement e : tag.elementSet()) {
				this.requestEnergy(e, tag.getValue(e));
			}
		}
		activeRecipe = r;
	}

	private CastingRecipe getValidRecipe() {
		ItemStack[] items = new ItemStack[0];
		CastingRecipe r = RecipesCastingTable.instance.getRecipe(inv[0], items);
		return r != null && r.matchRunes(worldObj, xCoord, yCoord, zCoord) ? r : null;
	}

	private void evaluateRecipeAndRequest() {
		CastingRecipe r = this.getValidRecipe();
		if (r != null && r != activeRecipe) {
			ElementTagCompound tag = r.getRequiredAura();
			tag.subtract(energy);
			for (CrystalElement e : tag.elementSet()) {
				this.requestEnergy(e, tag.getValue(e));
			}
		}
	}

	public static ArrayList<TileEntityItemStand> getOtherStands(TileEntityCastingTable tile) {
		World world = tile.worldObj;
		int x = tile.xCoord;
		int y = tile.yCoord;
		int z = tile.zCoord;
		ArrayList<TileEntityItemStand> li = new ArrayList();
		for (int i = -4; i <= 4; i += 2) {
			for (int k = -4; k <= 4; k += 2) {
				int dx = x+i;
				int dz = z+k;
				int dy = y+(Math.abs(i) != 4 && Math.abs(k) != 4 ? 0 : 1);
				ChromaTiles c = ChromaTiles.getTile(world, dx, dy, dz);
				if (c == ChromaTiles.STAND) {
					TileEntityItemStand te = (TileEntityItemStand)world.getTileEntity(dx, dy, dz);
					li.add(te);
				}
			}
		}
		return li;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is) {
		return false;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack is, int side) {
		return false;
	}

	@Override
	public void onPathBroken() {

	}

	@Override
	public boolean isConductingElement(CrystalElement e) {
		return e != null;
	}

	@Override
	public int maxThroughput() {
		return 10;
	}

	@Override
	public boolean canConduct() {
		return true;
	}

	@Override
	public int getReceiveRange() {
		return 32;
	}

	@Override
	protected int getMaxStorage() {
		return 400;
	}

	public boolean isCrafting() {
		return activeRecipe != null;
	}

	public ArrayList<CrystalTarget> getTargets() {
		ArrayList<CrystalTarget> li = new ArrayList();
		return li;
	}

}
