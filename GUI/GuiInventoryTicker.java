/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.CustomSoundGuiButton.CustomSoundImagedGuiButton;
import Reika.ChromatiCraft.Base.GuiChromaBase;
import Reika.ChromatiCraft.Container.ContainerInventoryTicker;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.TileEntity.Processing.TileEntityInventoryTicker;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;

public class GuiInventoryTicker extends GuiChromaBase {

	private int count;

	private TileEntityInventoryTicker tile;

	public GuiInventoryTicker(EntityPlayer ep, TileEntityInventoryTicker te) {
		super(new ContainerInventoryTicker(ep, te), ep, te);
		tile = te;
		xSize = 185;
		count = tile.ticks;
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.clear();
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		String tex = "Textures/GUIs/invtick.png";
		buttonList.add(new CustomSoundImagedGuiButton(0, j+171, k+33, 10, 10, 171, 33, tex, ChromatiCraft.class, this));
		buttonList.add(new CustomSoundImagedGuiButton(1, j+171, k+43, 10, 10, 171, 43, tex, ChromatiCraft.class, this));
	}

	@Override
	protected void actionPerformed(GuiButton b) {
		switch(b.id) {
		case 1:
			if (count > 1) {
				count--;
			}
			break;
		case 0:
			if (count < TileEntityInventoryTicker.MAX_RATE) {
				count++;
			}
			break;
		}
		ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.TICKER.ordinal(), tile, count);
		this.initGui();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		super.drawGuiContainerForegroundLayer(par1, par2);

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		api.drawCenteredStringNoShadow(fontRendererObj, String.format("%dx", count), 176, 24, 0xffffff);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int r, int v) {
		super.drawGuiContainerBackgroundLayer(f, r, v);

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;


	}

	@Override
	public String getGuiTexture() {
		return "invtick";
	}

}
