/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;

import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.DragonAPI.Instantiable.GUI.CustomSoundGuiButton.CustomSoundGui;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;

@Deprecated
public class GuiLore extends GuiScreen implements CustomSoundGui {

	private final EntityPlayer player;

	//private final ClickableTree web = new RadialTree(LoreData.instance.getRoot(), (Collection)LoreData.instance.getData()).getClickable();

	private final int xSize = 120;
	private final int ySize = 176;

	public GuiLore(EntityPlayer ep) {
		player = ep;
	}

	@Override
	public void initGui() {
		super.initGui();

		buttonList.clear();
	}

	public void playButtonSound(GuiButton b) {
		ReikaSoundHelper.playClientSound(ChromaSounds.GUICLICK, player, 0.5F, 1);
	}

	public void playHoverSound(GuiButton b) {
		ReikaSoundHelper.playClientSound(ChromaSounds.GUISEL, player, 0.8F, 1);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public final void drawScreen(int x, int y, float f) {
		super.drawScreen(x, y, f);

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		//web.render(j, k);
	}

	@Override
	protected void mouseClicked(int x, int y, int button) {
		super.mouseClicked(x, y, button);

		//web.onClick(x, y, button);
	}

}
