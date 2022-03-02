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

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Items.ItemInfoFragment;
import Reika.ChromatiCraft.Magic.Progression.FragmentCategorizationSystem.FragmentCategory;
import Reika.ChromatiCraft.Magic.Progression.ProgressionChoiceSystem;
import Reika.ChromatiCraft.Magic.Progression.ProgressionChoiceSystem.Selection;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.DragonAPI.Instantiable.BasicInventory;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ContainerFragmentSelect extends Container {

	private final EntityPlayer player;
	private final FragmentInventory inventory = new FragmentInventory();

	private final ProgressionChoiceSystem choice;

	private final Selection[] options = new Selection[3];

	private boolean hasFragment;

	public ContainerFragmentSelect(EntityPlayer ep) {
		super();
		player = ep;
		choice = new ProgressionChoiceSystem(ep);

		this.addSlotToContainer(new SlotFragmentSelect(inventory, 0, 82, 8));

		for (int i = 0; i < 3; ++i)
			for (int k = 0; k < 9; ++k)
				this.addSlotToContainer(new Slot(player.inventory, k+i*9+9, 9+k*18, 77+i*18));
		for (int i = 0; i < 9; ++i)
			this.addSlotToContainer(new Slot(player.inventory, i, 9+i*18, 135));

		this.onCraftMatrixChanged(inventory);
	}

	public boolean hasFragment() {
		return hasFragment;
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		if (!player.worldObj.isRemote) {
			ItemStack is = inventory.getStackInSlot(0);
			if (ChromaItems.FRAGMENT.matchWith(is) && ItemInfoFragment.isBlank(is)) {
				hasFragment = true;
			}
			else {
				hasFragment = false;
			}
			for (int i = 0; i < crafters.size(); ++i) {
				ICrafting icrafting = (ICrafting)crafters.get(i);
				icrafting.sendProgressBarUpdate(this, 0, hasFragment ? 1 : 0);
				for (int k = 0; k < 3; k++) {
					//icrafting.sendProgressBarUpdate(this, k*2+1, options[k] != null ? options[k].category.ordinal() : -1);
					//icrafting.sendProgressBarUpdate(this, k*2+2, options[k] != null ? options[k].fragment.ordinal() : -1);
					int bits = options[k] != null ? options[k].fragment.ordinal() | (options[k].category.ordinal() << 11) : -1;
					//if (options[k] != null)
					//	ReikaJavaLibrary.pConsole(options[k].fragment+"@"+options[k].fragment.ordinal()+" > "+bits+"="+Integer.toBinaryString(bits));
					icrafting.sendProgressBarUpdate(this, k+1, bits);
				}
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int val) {
		if (id == 0) {
			hasFragment = val > 0;
		}/*
		else if (id <= 6) {
			int slot = (id-1)/2;
			if (val < 0)
				options[slot] = null;
			else if (id%2 == 1)
				options[slot].category = FragmentCategory.list[val];
			else
				options[slot].fragment = ChromaResearch.researchList[val];
		}*/
		else if (id <= 3) {
			int slot = id-1;
			if (val < 0) {
				options[slot] = null;
			}
			else {
				int r = val & 2047;
				int f = (val >> 11) & 31;
				options[slot] = new Selection(FragmentCategory.list[f], ChromaResearch.researchList[r]);
			}
		}
		else if (id == 20) {
			ReikaSoundHelper.playClientSound(ChromaSounds.GUISEL, player, 0.5F, 0.5F);
		}
	}

	@Override
	public void onCraftMatrixChanged(IInventory ii) {
		super.onCraftMatrixChanged(ii);
		if (!player.worldObj.isRemote) {
			ArrayList<Selection> li = choice.pickThreeCategories();
			options[0] = li.get(0);
			options[1] = li.get(1);
			options[2] = li.get(2);
		}
	}

	public Selection getOption(int i) {
		return options[i];
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
		ItemStack is = inventory.getStackInSlot(0);
		if (is != null) {
			selectRandom(is, ep);
			ReikaPlayerAPI.addOrDropItem(is, ep);
		}
		super.onContainerClosed(ep);
	}

	public boolean selectSlot(int slot) {
		if (player.worldObj.isRemote) {
			ReikaPacketHelper.sendPacketToServer(ChromatiCraft.packetChannel, ChromaPackets.FRAGSELECT.ordinal(), slot);
			return true;
		}
		else {
			ItemStack in = inventory.getStackInSlot(0);
			if (ChromaItems.FRAGMENT.matchWith(in) && options[slot].giveToPlayer(player)) {
				ItemInfoFragment.setResearch(in, options[slot].fragment);
				return true;
			}
			return false;
		}
	}

	private static void selectRandom(ItemStack is, EntityPlayer ep) {
		if (!ep.worldObj.isRemote && ItemInfoFragment.isBlank(is))
			ItemInfoFragment.programShardAndGiveData(is, ep);
	}

	private static class SlotFragmentSelect extends Slot {

		public SlotFragmentSelect(IInventory ii, int id, int x, int y) {
			super(ii, id, x, y);
		}

		@Override
		public boolean isItemValid(ItemStack is)
		{
			return ChromaItems.FRAGMENT.matchWith(is) && ItemInfoFragment.getResearch(is) == null;
		}

		@Override
		public void onPickupFromSlot(EntityPlayer ep, ItemStack is) {
			super.onPickupFromSlot(ep, is);
			selectRandom(is, ep);
		}

	}
	/*
	private static class MutableSelection {

		private ChromaResearch fragment;
		private FragmentCategory category;

		private final Selection base;

		private MutableSelection(Selection s) {
			fragment = s.fragment;
			category = s.category;

			base = s;
		}

	}
	 */
	private static class FragmentInventory extends BasicInventory {

		private FragmentInventory() {
			super("Fragment", 1, 1);
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
