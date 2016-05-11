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

import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import Reika.ChromatiCraft.Auxiliary.ChromaFontRenderer;

public class GuiRemoteTerminal extends GuiContainer {

	private final EntityPlayer player;

	private GuiTextField search;

	public GuiRemoteTerminal(EntityPlayer ep) {
		super(new ContainerRemoteTerminal(ep));

		player = ep;
	}

	@Override
	public void initGui() {
		super.initGui();

		buttonList.clear();

		search = new GuiTextField(ChromaFontRenderer.FontType.GUI.renderer, 0, 0, 120, 20);
		search.setFocused(false);
		search.setMaxStringLength(20);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float ptick, int mx, int my) {

	}
}
