/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI;

import java.util.HashSet;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import Reika.ChromatiCraft.Auxiliary.CustomSoundGuiButton.CustomSoundGui;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;


public class GuiLoreKeyAssembly extends GuiScreen implements CustomSoundGui {

	private final EntityPlayer player;

	private final HashSet<Integer> availableStars = new HashSet();

	public GuiLoreKeyAssembly(EntityPlayer ep) {
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

}
