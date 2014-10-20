/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.GuiChromaBase;
import Reika.ChromatiCraft.Container.ContainerItemCollector;
import Reika.ChromatiCraft.TileEntity.TileEntityItemCollector;
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
	/*
	@Override
	public void initGui() {
		super.initGui();
		int var5 = (width - xSize) / 2;
		int var6 = (height - ySize) / 2;
		buttonList.add(new GuiButton(0, var5+xSize-1, var6+32, 43, 20, "Get XP"));
	}

	@Override
	public void actionPerformed(GuiButton button) {
		super.actionPerformed(button);
		if (button.id == 0)
			ReikaPacketHelper.sendUpdatePacket(RotaryCraft.packetChannel, PacketRegistry.ItemCollector.getMinValue(), vac);
	}

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
	public String getGuiTexture() {
		return "/Reika/ChromatiCraft/Textures/GUIs/itemcollector.png";
	}
}
