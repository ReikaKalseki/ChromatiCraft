/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.PlayerElementBuffer;
import Reika.ChromatiCraft.Magic.Progression.ProgressStage;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.TileEntity.Processing.TileEntityGlowFire;
import Reika.DragonAPI.Instantiable.BasicInventory;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ContainerItemBurner extends Container {

	private static final int BASE_BURN_DURATION = 40;
	private static final int MIN_BURN_DURATION = 4;

	private final EntityPlayer player;
	private final BurnerInventory inventory = new BurnerInventory();
	private int burnTick;
	private int burnDuration = BASE_BURN_DURATION;

	public ContainerItemBurner(EntityPlayer ep) {
		super();
		player = ep;

		this.addSlotToContainer(new Slot(inventory, 0, 80, 18));

		for (int i = 0; i < 3; ++i)
			for (int k = 0; k < 9; ++k)
				this.addSlotToContainer(new Slot(player.inventory, k+i*9+9, 8+k*18, 42+i*18));
		for (int i = 0; i < 9; ++i)
			this.addSlotToContainer(new Slot(player.inventory, i, 8+i*18, 100));

		this.onCraftMatrixChanged(inventory);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		ItemStack is = inventory.getStackInSlot(0);
		if (is != null) {
			ElementTagCompound tag = TileEntityGlowFire.getDecompositionValue(is);
			if (tag != null && !tag.isEmpty()) {
				burnTick++;
				if (burnTick >= burnDuration) {
					ReikaInventoryHelper.decrStack(0, inventory, 1);
					PlayerElementBuffer.instance.addToPlayer(player, tag.copy(), false);
					burnTick = 0;
					int d = 4;
					if (ProgressStage.CTM.isPlayerAtStage(player))
						d *= 2;
					if (ProgressStage.DIMENSION.isPlayerAtStage(player))
						d *= 2;
					if (ProgressStage.LINK.isPlayerAtStage(player))
						d *= 2;
					burnDuration = Math.max(MIN_BURN_DURATION, burnDuration-d);
					for (int i = 0; i < crafters.size(); ++i) {
						ICrafting icrafting = (ICrafting)crafters.get(i);
						icrafting.sendProgressBarUpdate(this, 2, 0);
					}
				}
			}
			else {
				burnTick = 0;
				burnDuration = BASE_BURN_DURATION;
			}
		}
		else {
			burnTick = 0;
			burnDuration = BASE_BURN_DURATION;
		}

		for (int i = 0; i < crafters.size(); ++i) {
			ICrafting icrafting = (ICrafting)crafters.get(i);
			icrafting.sendProgressBarUpdate(this, 0, burnTick);
			icrafting.sendProgressBarUpdate(this, 1, burnDuration);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int val) {
		if (id == 0) {
			burnTick = val;
		}
		else if (id == 1) {
			burnDuration = val;
		}
		else if (id == 2) {
			ReikaSoundHelper.playClientSound(ChromaSounds.GUISEL, player, 0.5F, 0.5F);
		}
	}

	public int getScaledBurn(int a) {
		return burnTick * a / burnDuration;
	}

	@Override
	public void onCraftMatrixChanged(IInventory ii) {
		super.onCraftMatrixChanged(ii);
	}

	public int getSize() {
		return inventory.getSizeInventory();
	}

	@Override
	public boolean canInteractWith(EntityPlayer ep) {
		return true;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
	{
		return null;
	}

	@Override
	public ItemStack slotClick(int slot, int button, int par3, EntityPlayer ep) {
		/*
		boolean inGUI = slot == 0;
		if (inGUI) {
			return super.slotClick(slot, button, par3, ep);
		}
		else if (slot >= 1+27) {
			ItemStack in = ep.inventory.getStackInSlot(slot-1-27);
			return ep.inventory.getItemStack();
		}
		 */
		return super.slotClick(slot, button, par3, ep);
	}

	@Override
	public void onContainerClosed(EntityPlayer ep) {
		super.onContainerClosed(ep);
		ItemStack is = inventory.getStackInSlot(0);
		if (is != null) {
			ReikaItemHelper.dropItem(ep, is);
		}
	}

	private static class BurnerInventory extends BasicInventory {

		private BurnerInventory() {
			super("Burner", 1, 64);
		}

		@Override
		public boolean isUseableByPlayer(EntityPlayer ep) {
			return true;
		}

		@Override
		public boolean isItemValidForSlot(int slot, ItemStack is) {
			return true;
		}
	}

}
