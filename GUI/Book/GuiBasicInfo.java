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
import net.minecraft.util.IIcon;
import Reika.ChromatiCraft.Auxiliary.ChromaDescriptions;
import Reika.ChromatiCraft.Base.GuiBookSection;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;

public class GuiBasicInfo extends GuiBookSection {

	public GuiBasicInfo(EntityPlayer ep, ChromaResearch r) {
		super(ep, r, 256, 220, false);
	}

	@Override
	protected int getMaxSubpage() {
		return page == ChromaResearch.ELEMENTS ? CrystalElement.elements.length : 0;
	}

	@Override
	protected PageType getGuiLayout() {
		return this.isElementPage() ? PageType.ELEMENT : PageType.PLAIN;
	}

	@Override
	public void drawScreen(int x, int y, float f) {
		int posX = (width - xSize) / 2;
		int posY = (height - ySize) / 2 - 8;
		super.drawScreen(x, y, f);

		int c = 0xffffff;
		int px = posX+descX;
		if (subpage == 0 || page.sameTextAllSubpages()) {
			fontRendererObj.drawSplitString(String.format("%s", page.getData()), px, posY+descY, 242, c);
		}
		if (this.isElementPage()) {
			this.renderElementPage(CrystalElement.elements[subpage-1], posX, posY, px, c);
		}
		else if (page == ChromaResearch.CRYSTALS) {
			--draw crystal, fading through colors--
		}
		else if (page == ChromaResearch.PYLONS) {
			--draw pylon, fading through colors--
		}
	}

	private boolean isElementPage() {
		return page == ChromaResearch.ELEMENTS && subpage > 0;
	}

	private void renderElementPage(CrystalElement e, int posX, int posY, int px, int c) {
		String s = ChromaDescriptions.getElementDescription(e);
		fontRendererObj.drawSplitString(String.format("%s", s), px, posY+descY, 242, c);
		IIcon ico = e.getGlowRune();
		ReikaTextureHelper.bindTerrainTexture();
		this.drawTexturedModelRectFromIcon(posX+153, posY+12, ico, 64, 64);
	}

}
