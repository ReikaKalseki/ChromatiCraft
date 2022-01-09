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

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import Reika.ChromatiCraft.GUI.Tile.Inventory.GuiItemInserter;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityItemInserter;
import Reika.DragonAPI.Base.CoreContainer;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class ContainerItemInserter extends CoreContainer {

	public ContainerItemInserter(EntityPlayer player, TileEntityItemInserter te) {
		super(player, te);

		for (int i = 0; i < TileEntityItemInserter.TARGETS; i++) {
			int x = 8;
			int y = 8;
			this.addSlot(i, x+0, y+i*20);
		}

		for (int i = 0; i < 9; i++) {
			int dx = 8+18*(8-i);
			this.addSlot(TileEntityItemInserter.TARGETS+i, 8+dx, 128);
		}

		this.addPlayerInventoryWithOffset(player, 0, 64);
	}

	@Override
	public ItemStack slotClick(int ID, int par2, int par3, EntityPlayer ep) {
		ItemStack is = super.slotClick(ID, par2, par3, ep);
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			this.reloadGui();
		}
		return is;
	}

	@SideOnly(Side.CLIENT)
	private void reloadGui() {
		if (Minecraft.getMinecraft().currentScreen instanceof GuiItemInserter)
			Minecraft.getMinecraft().currentScreen.initGui();
	}

}
