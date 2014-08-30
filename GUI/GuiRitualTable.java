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

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.TileEntity.TileEntityRitualTable;
import Reika.DragonAPI.Instantiable.GUI.ImagedGuiButton;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;

public class GuiRitualTable extends GuiChromability {

	private final TileEntityRitualTable tile;

	public GuiRitualTable(EntityPlayer ep, TileEntityRitualTable te) {
		super(ep);
		tile = te;
	}

	@Override
	public void initGui() {
		super.initGui();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		String tex = "Textures/GUIs/ability.png";
		buttonList.add(new ImagedGuiButton(2, j+8, k+8, 50, 50, 0, 193, tex, ChromatiCraft.class));
	}

	@Override
	public void actionPerformed(GuiButton b) {
		if (b.id == 2) {
			ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.ABILITYCHOOSE.ordinal(), tile, index);
			player.closeScreen();
			return;
		}
		super.actionPerformed(b);
	}

}
