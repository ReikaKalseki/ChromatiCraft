/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface;

import net.minecraft.entity.player.EntityPlayer;
import Reika.ChromatiCraft.Base.GuiChromaBase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiPatternCache extends GuiChromaBase
{
	private TileEntityPatternCache cache;

	private int inventoryRows = 0;

	public GuiPatternCache(EntityPlayer p5ep, TileEntityPatternCache te)
	{
		super(new ContainerPatternCache(p5ep, te), p5ep, te);
		ySize = 176;
		cache = te;
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
	protected void actionPerformed(GuiButton button) {
		super.actionPerformed(button);
		if (button.id == 0)
			ReikaPacketHelper.sendUpdatePacket(RotaryCraft.packetChannel, PacketRegistry.PatternCache.getMinValue(), vac);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int a, int b)
	{
		super.drawGuiContainerForegroundLayer(a, b);

		fontRendererObj.drawString("XP: "+String.format("%d", vac.getExperience()), 150-fontRendererObj.getStringWidth(String.format("%d", vac.getExperience())), 6, 4210752);
	}
	 */

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int a, int b) {
		super.drawGuiContainerBackgroundLayer(f, a, b);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		super.drawGuiContainerForegroundLayer(par1, par2);

	}

	@Override
	public String getGuiTexture() {
		return "/Reika/ChromatiCraft/Textures/GUIs/patterncache.png";
	}
}
