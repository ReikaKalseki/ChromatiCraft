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

import Reika.ChromatiCraft.Base.TileEntity.InventoriedChromaticBase;
import Reika.ChromatiCraft.Magic.CrystalNetworker;
import Reika.ChromatiCraft.Magic.CrystalReceiver;
import Reika.ChromatiCraft.Magic.RuneShape.RuneLocation;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;

import java.util.ArrayList;
import java.util.EnumMap;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class TileEntityCastingTable extends InventoriedChromaticBase implements CrystalReceiver {

	private ArrayList<RuneLocation> runes = new ArrayList();
	private EnumMap<CrystalElement, Integer> energy = new EnumMap(CrystalElement.class);

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.TABLE;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (!world.isRemote && this.getTicksExisted() == 0) {
			this.requestEnergy(CrystalElement.RED, 50000);
			this.requestEnergy(CrystalElement.BLUE, 50000);
		}
		//ReikaJavaLibrary.pConsole(energy, Side.SERVER);
	}

	private void requestEnergy(CrystalElement e, int amount) {
		CrystalNetworker.instance.makeRequest(this, e, amount, worldObj, xCoord, yCoord, zCoord, 24);
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public boolean canExtractItem(int slot, ItemStack is, int side) {
		return false;
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
	public void receiveElement(CrystalElement e, int amt) {
		this.addKey(e, amt);
	}

	private void addKey(CrystalElement e, int amt) {
		if (energy.containsKey(e)) {
			int sum = energy.get(e)+amt;
			energy.put(e, sum);
		}
		else {
			energy.put(e, amt);
		}
	}

	@Override
	public void onPathBroken() {

	}

}
