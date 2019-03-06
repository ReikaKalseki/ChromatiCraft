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
import net.minecraft.item.ItemStack;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.GuiChromaBase;
import Reika.ChromatiCraft.Container.ContainerItemCollector;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityItemCollector;
import Reika.DragonAPI.Instantiable.GUI.CustomSoundGuiButton.CustomSoundImagedGuiButtonSneakIcon;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiItemCollector extends GuiChromaBase
{
	private IInventory upperItemCollectorInventory;
	private TileEntityItemCollector vac;

	private int inventoryRows = 0;

	public GuiItemCollector(EntityPlayer p5ep, TileEntityItemCollector te)
	{
		super(new ContainerItemCollector(p5ep, te), p5ep, te);
		upperItemCollectorInventory = p5ep.inventory;
		allowUserInput = false;
		short var3 = 222;
		int var4 = var3 - 108;
		inventoryRows = 6;//te.getSizeInventory() / 9;
		ySize = var4 + inventoryRows * 18;
		vac = te;
	}

	@Override
	public void initGui() {
		super.initGui();
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		String tex = "Textures/GUIs/buttons.png";
		buttonList.add(new CustomSoundImagedGuiButtonSneakIcon(0, j+11, k+75, 10, 10, 100, 66, tex, ChromatiCraft.class, this, 100, 86));
		buttonList.add(new CustomSoundImagedGuiButtonSneakIcon(1, j+83, k+75, 10, 10, 100, 56, tex, ChromatiCraft.class, this, 100, 76));
	}

	@Override
	protected void actionPerformed(GuiButton b) {
		super.actionPerformed(b);
		if (b.id <= 1) {
			int amt = GuiScreen.isShiftKeyDown() ? 10 : 1;
			ReikaPacketHelper.sendPacketToServer(ChromatiCraft.packetChannel, ChromaPackets.COLLECTORRANGE.ordinal(), vac, b.id, amt);
			if (b.id > 0)
				vac.increaseRange(amt);
			else
				vac.decreaseRange(amt);
		}
	}
	/*
	@Override
	protected void drawGuiContainerForegroundLayer(int a, int b)
	{
		super.drawGuiContainerForegroundLayer(a, b);

		fontRendererObj.drawString("XP: "+String.format("%d", vac.getExperience()), 150-fontRendererObj.getStringWidth(String.format("%d", vac.getExperience())), 6, 4210752);
	}
	 */

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
	{
		String var4 = "/gui/container.png";
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		//mc.renderEngine.bindTexture(GuiContainer.field_110408_a);
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, this.getGuiTexture());
		int var5 = (width - xSize) / 2;
		int var6 = (height - ySize) / 2;
		this.drawTexturedModalRect(var5, var6, 0, 0, xSize, inventoryRows * 18 + 17);
		this.drawTexturedModalRect(var5, var6 + inventoryRows * 18 + 17, 0, 126, xSize, 96);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		super.drawGuiContainerForegroundLayer(par1, par2);
		fontRendererObj.drawString("Filter", xSize-32, 79, 0xffffff);

		for (int i = 0; i < 2; i++) {
			for (int k = 0; k < 9; k++) {
				ItemStack is = vac.getMapping(i*9+k);
				if (is != null) {
					int dx = 8+k*18;
					int dy = 90+i*18;
					api.drawItemStack(itemRender, fontRendererObj, is, dx, dy);
				}
			}
		}

		fontRendererObj.drawString("Range: "+vac.getRange(), 28, 76, 0xffffff);
	}

	@Override
	public String getGuiTexture() {
		return "/Reika/ChromatiCraft/Textures/GUIs/itemcollector.png";
	}
}
