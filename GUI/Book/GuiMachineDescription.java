/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI.Book;

import net.minecraft.entity.player.EntityPlayer;
import Reika.ChromatiCraft.Base.GuiBookSection;
import Reika.ChromatiCraft.Registry.ChromaResearch;

public class GuiMachineDescription extends GuiBookSection {

	public GuiMachineDescription(EntityPlayer ep, ChromaResearch r) {
		super(ep, r, 256, 220);
	}

	@Override
	protected PageType getGuiLayout() {
		return PageType.PLAIN;
	}

	@Override
	public final void drawScreen(int x, int y, float f) {
		super.drawScreen(x, y, f);

		int posX = (width - xSize) / 2;
		int posY = (height - ySize) / 2 - 8;

		boolean disable = page.isConfigDisabled();
		int c = disable ? 0x777777 : 0xffffff;
		int px = posX+descX;
		if (subpage == 0 || page.sameTextAllSubpages()) {
			fontRendererObj.drawSplitString(String.format("%s", page.getData()), px, posY+descY, 242, c);
		}
		else {
			fontRendererObj.drawSplitString(String.format("%s", page.getNotes()), px, posY+descY, 242, c);
		}
		if (disable) {
			fontRendererObj.drawSplitString("This machine has been disabled by your server admin or modpack creator.", px, posY+descY, 242, 0xffffff);
			fontRendererObj.drawSplitString("Contact them for further information or to request that they remove this restriction.", px, posY+descY+27, 242, 0xffffff);
			fontRendererObj.drawSplitString("If you are the server admin or pack creator, use the configuration files to change this setting.", px, posY+descY+54, 242, 0xffffff);
		}

		//if (subpage == 0)
		//	this.drawMachineRender(posX, posY);
	}

	@Override
	protected int getMaxSubpage() {
		return 0;
	}

	@Override
	public String getPageTitle() {
		return page.isConfigDisabled() ? page.getTitle()+" (Disabled)" : super.getPageTitle();
	}

	@Override
	public int getTitleColor() {
		return page.isConfigDisabled() ? 0xffffff : super.getTitleColor();
	}

}
