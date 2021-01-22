/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI.Tile.Inventory;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

import Reika.ChromatiCraft.Base.GuiChromaBase;
import Reika.ChromatiCraft.Container.ContainerNetworkItemTransporter;
import Reika.ChromatiCraft.TileEntity.Transport.TileEntityNetworkItemTransporter;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiNetworkItemTransporter extends GuiChromaBase
{
	private IInventory upperNetworkItemTransporterInventory;
	private TileEntityNetworkItemTransporter net;

	public GuiNetworkItemTransporter(EntityPlayer p5ep, TileEntityNetworkItemTransporter te) {
		super(new ContainerNetworkItemTransporter(p5ep, te), p5ep, te);
		upperNetworkItemTransporterInventory = p5ep.inventory;
		allowUserInput = false;
		ySize = 166;
		net = te;
	}

	@Override
	public void initGui() {
		super.initGui();
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		String tex = "Textures/GUIs/buttons.png";
	}

	@Override
	protected void actionPerformed(GuiButton b) {
		super.actionPerformed(b);
	}

	public ContainerNetworkItemTransporter getContainer() {
		return (ContainerNetworkItemTransporter)inventorySlots;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int a, int b) {
		super.drawGuiContainerBackgroundLayer(f, a, b);
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		super.drawGuiContainerForegroundLayer(par1, par2);
		GL11.glColor4f(1, 1, 1, 1);

		//ReikaRenderHelper.disableEntityLighting();
		//ReikaRenderHelper.disableLighting();
		//fontRendererObj.drawString("Receiver", xSize-32, 79, 0xffffff);

		/*
		if (GuiScreen.isCtrlKeyDown()) {
			int idx = 0;
			for (int k = 5; k < 9; k++) {
				for (int i = 0; i < 3; i++) {
					ItemStack is = net.getFilter(idx);
					int dx = 8+k*18;
					int dy = 17+i*18;
					if (is != null) {
						api.drawItemStack(itemRender, fontRendererObj, is, dx, dy);
					}
					idx++;
				}
			}
		}*/
	}

	@Override
	protected final void func_146977_a(Slot slot) {
		if (GuiScreen.isCtrlKeyDown() && slot.slotNumber >= 12 && slot.inventory == net) {
			api.drawItemStack(itemRender, fontRendererObj, net.getFilter(slot.slotNumber-12), slot.xDisplayPosition, slot.yDisplayPosition);
		}
		else {
			super.func_146977_a(slot);
		}
	}

	@Override
	public String getGuiTexture() {
		return "networktransport";
	}
}
