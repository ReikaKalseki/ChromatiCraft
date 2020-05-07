/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI.Book;

import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;

import Reika.ChromatiCraft.Base.GuiProgressStages;
import Reika.ChromatiCraft.Magic.Progression.ProgressionManager;
import Reika.ChromatiCraft.Magic.Progression.ProgressionManager.ProgressLink;
import Reika.ChromatiCraft.Registry.ChromaGuis;

public class GuiProgressByLevel extends GuiProgressStages {

	private final Map<ProgressLink, Integer> levels = ProgressionManager.instance.constructResearchLevelDepthMap(1);

	public GuiProgressByLevel(EntityPlayer ep) {
		super(ChromaGuis.PROGRESSBYTIER, ep);
		this.initMap();
	}

	@Override
	public void drawScreen(int x, int y, float f) {
		super.drawScreen(x, y, f);

		int posX = (width - xSize) / 2;
		int posY = (height - ySize) / 2 - 8;

		this.renderElements(posX, posY);

		this.renderText(posX, posY);

		//ReikaJavaLibrary.pConsole(offsetX+"/"+maxX+","+offsetY+"/"+maxY);
	}

	@Override
	public String getBackgroundTexture() {
		return "Textures/GUIs/Handbook/progress.png";
	}
	/*
	@Override
	public String getPageTitle() {
		return subpage > 0 ? this.getStage().getTitleString() : "Research Notes";
	}

	private ProgressStage getStage() {
		return subpage > 0 ? stages.get(subpage-1) : null;
	}

	@Override
	protected int getMaxSubpage() {
		return stages.size();
	}

	@Override
	protected PageType getGuiLayout() {
		return PageType.PLAIN;
	}
	 */

	@Override
	protected String getScrollingTexture() {
		return "Textures/GUIs/Handbook/navbcg.png";
	}

	@Override
	protected Map<ProgressLink, Integer> getDepthMap() {
		return levels;
	}
}
