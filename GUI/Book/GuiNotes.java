/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI.Book;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.CustomSoundGuiButton.CustomSoundImagedGuiButton;
import Reika.ChromatiCraft.Base.ChromaBookGui;
import Reika.ChromatiCraft.Registry.ChromaGuis;

public class GuiNotes extends ChromaBookGui {

	public GuiNotes(EntityPlayer ep) {
		super(ep, 256, 220);
	}

	@Override
	public void initGui() {
		super.initGui();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		String file = "Textures/GUIs/Handbook/buttons.png";

		buttonList.add(new CustomSoundImagedGuiButton(10, j+xSize, k, 22, 39, 42, 126, file, ChromatiCraft.class, this));
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		super.actionPerformed(button);
		if (button.id == 10) {
			this.goTo(ChromaGuis.BOOKNAV, null);
		}
		this.initGui();
	}

	@Override
	public String getBackgroundTexture() {
		return "";
	}

}
