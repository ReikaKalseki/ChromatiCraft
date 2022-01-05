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
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityItemCollector;
import Reika.DragonAPI.Base.CoreContainer;
import Reika.DragonAPI.Instantiable.GUI.Slot.GhostSlot;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Instantiable.IO.PacketTarget.PlayerTarget;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ContainerItemCollector extends CoreContainer {

	private TileEntityItemCollector tile;
	private int rowOffset;

	public ContainerItemCollector(EntityPlayer player, TileEntityItemCollector te) {
		super(player, te);
		int dx = -18;
		int dy = 18;
		tile = te;

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				this.addSlotToContainer(new Slot(te, i*9+j, dx+26+j*18, dy+i*18));
			}
		}

		dy = 81;
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 9; j++) {
				//this.addSlotToContainer(new GhostSlot(te, 18+i*3+j, dx+26+j*18, dy+i*18));
				this.addSlotToContainer(new GhostSlot(te, 27+i*9+j, dx+26+j*18, dy+i*18+9));
			}
		}

		this.addPlayerInventoryWithOffset(player, 0, 55);
	}

	public void stepOffset(EntityPlayer ep, int d) {
		rowOffset = MathHelper.clamp_int(rowOffset+d, 0, this.getMaxRowOffset());
		if (tile.worldObj.isRemote) {
			ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.COLLECTORROW.ordinal(), PacketTarget.server, rowOffset);
		}
		else {
			ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.COLLECTORROW.ordinal(), new PlayerTarget((EntityPlayerMP)ep), rowOffset);
		}
	}

	public void setRowOffset(int i) {
		rowOffset = i;
	}

	private int getMaxRowOffset() {
		return 3;
	}

	public int getRowOffset() {
		return rowOffset;
	}

	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();

		for (int i = 0; i < crafters.size(); ++i)
		{
			ICrafting icrafting = (ICrafting)crafters.get(i);
			icrafting.sendProgressBarUpdate(this, 0, tile.filterLimit);
		}
	}

	@Override
	public void addCraftingToCrafters(ICrafting par1ICrafting)
	{
		super.addCraftingToCrafters(par1ICrafting);
		par1ICrafting.sendProgressBarUpdate(this, 0, tile.filterLimit);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int par1, int par2)
	{
		if (par1 == 0)
		{
			tile.filterLimit = par2;
		}
	}

	@Override
	public boolean allowShiftClicking(EntityPlayer player, int slot, ItemStack stack) {
		return false;
	}

	@Override
	public ItemStack slotClick(int slot, int button, int action, EntityPlayer ep) {
		/*
		if (slot >= 18 && slot < tile.getSizeInventory()) {
			ItemStack held = ep.inventory.getItemStack();
			tile.setMapping(slot, ReikaItemHelper.getSizedItemStack(held, 1));
			return held;
		}
		 */

		if (slot >= tile.getSizeInventory() && slot < tile.getSizeInventory()+2*9) {
			action = 0;
			int off = rowOffset*9;
			ItemStack held = ep.inventory.getItemStack();
			if (button == 1) { //right click
				tile.toggleNBT(slot+off-tile.getSizeInventory());
			}
			else {
				tile.setMapping(slot+off-tile.getSizeInventory(), ReikaItemHelper.getSizedItemStack(held, 1));
			}
			//this.detectAndSendChanges();
			return held;
		}

		ItemStack is = super.slotClick(slot, button, action, ep);
		InventoryPlayer ip = ep.inventory;
		return is;
		//ReikaJavaLibrary.pConsole(ip.getItemStack());
	}
}
