/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI.Tile;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fluids.Fluid;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.GuiChromaBase;
import Reika.ChromatiCraft.Container.ContainerFluidRelay;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.TileEntity.Transport.TileEntityFluidRelay;
import Reika.DragonAPI.Instantiable.GUI.ImagedGuiButton;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;


public class GuiFluidRelay extends GuiChromaBase {

	private final TileEntityFluidRelay relay;

	public GuiFluidRelay(EntityPlayer ep, TileEntityFluidRelay te) {
		super(new ContainerFluidRelay(ep, te), ep, te);
		relay = te;

		ySize = 153;
	}

	@Override
	public void initGui() {
		super.initGui();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		String tex = "Textures/GUIs/buttons.png";
		int in = 22;
		int iny = 45;
		buttonList.add(new ImagedGuiButton(0, j+in, k+iny, 10, 10, 100, 66, tex, ChromatiCraft.class));
		buttonList.add(new ImagedGuiButton(1, j+xSize-10-in, k+iny, 10, 10, 100, 56, tex, ChromatiCraft.class));
	}

	@Override
	protected void actionPerformed(GuiButton b) {
		super.actionPerformed(b);
		int delta = 0;
		int n = GuiScreen.isCtrlKeyDown() ? 1 : (GuiScreen.isShiftKeyDown() ? 100 : 10);
		switch(b.id) {
			case 0:
				delta = -n;
				break;
			case 1:
				delta = n;
				break;
		}
		if (delta != 0) {
			ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.RELAYPRESSURE.ordinal(), relay, delta);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int a, int b) {
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		super.drawGuiContainerBackgroundLayer(f, a, b);


		Fluid[] types = relay.getFluidTypes();
		int n = types.length;
		/*
		for (int i = n+1; i < 7; i++) {
			int x = j+14+i*22;
			int y = k+17;
			api.drawTexturedModalRect(x, y, 179, 0, 16, 16);
		}*/
		ReikaTextureHelper.bindTerrainTexture();
		for (int i = 0; i < n; i++) {
			if (types[i] != null) {
				int x = j+14+i*22;
				int y = k+17;
				api.drawTexturedModelRectFromIcon(x, y, types[i].getIcon(), 16, 16);
			}
		}

		String s = String.format("Pressure: %d", relay.getPressure());
		api.drawCenteredStringNoShadow(fontRendererObj, s, j+xSize/2, k+45, 0xffffff);
	}

	@Override
	public String getGuiTexture() {
		return "fluidrelay";
	}

}
