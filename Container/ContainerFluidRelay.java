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
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.TileEntity.Transport.TileEntityFluidRelay;
import Reika.DragonAPI.Base.CoreContainer;
import Reika.DragonAPI.Instantiable.GUI.Slot.GhostSlot;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Libraries.ReikaFluidHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;


public class ContainerFluidRelay extends CoreContainer {

	private final TileEntityFluidRelay relay;

	private int manualSelectionSlot = -1;

	public ContainerFluidRelay(EntityPlayer player, TileEntityFluidRelay te) {
		super(player, te);

		relay = te;

		for (int i = 0; i < relay.getFluidTypes().length; i++) {
			int x = 14+22*i;
			int y = 17;
			this.addSlotToContainer(new GhostSlot(i, x, y));
		}
		this.addPlayerInventoryWithOffset(ep, 0, -16);
	}

	@Override
	public boolean allowShiftClicking(EntityPlayer player, int slot, ItemStack stack) {
		return false;
	}
	/*
	public void onCharTyped(char c) {
		if (manualSelectionSlot >= 0) {
			if (manualSelectionChar == c) {
				manualSelectionIndex = manualOptions.isEmpty() ? 0 : (manualSelectionIndex+1)%manualOptions.size();
			}
			else {
				manualSelectionChar = c;
				manualOptions.clear();
				manualOptions.addAll(allFluids);
				manualOptions.removeIf(f -> f.getName().toLowerCase(Locale.ENGLISH).charAt(0) != manualSelectionChar);
				manualSelectionIndex = 0;
			}
			Fluid f = manualOptions.isEmpty() ? null : manualOptions.get(manualSelectionIndex);
			if (relay.worldObj.isRemote)
				ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.RELAYFLUIDKEY.ordinal(), relay.worldObj, relay.xCoord, relay.yCoord, relay.zCoord, PacketTarget.server, manualSelectionSlot, f == null ? -1 : f.getID());
			else
				relay.setFluid(manualSelectionSlot, f);
		}
	}
	 */

	public int getManualSelectSlot() {
		return manualSelectionSlot;
	}

	public void setFluid(Fluid f) {
		if (relay.worldObj.isRemote)
			ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.RELAYFLUIDKEY.ordinal(), relay.worldObj, relay.xCoord, relay.yCoord, relay.zCoord, PacketTarget.server, manualSelectionSlot, f == null ? -1 : f.getID());
		else
			relay.setFluid(manualSelectionSlot, f);
	}

	@Override
	public ItemStack slotClick(int slot, int mouse, int action, EntityPlayer ep) {
		manualSelectionSlot = -1;
		if (slot >= 0 && slot < 7) {
			ItemStack held = ep.inventory.getItemStack();
			if (mouse == 0) {
				FluidStack fs = held != null ? ReikaFluidHelper.getFluidForItem(held) : null;
				relay.setFluid(slot, fs != null ? fs.getFluid() : null);
			}
			else if (mouse == 1) {
				manualSelectionSlot = slot;
			}
			return held;
		}

		ItemStack is = super.slotClick(slot, mouse, action, ep);
		InventoryPlayer ip = ep.inventory;
		return is;
	}

}
