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

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.entity.player.EntityPlayer;

import Reika.ChromatiCraft.Base.GuiProgressStages;
import Reika.ChromatiCraft.Magic.Progression.ChromaResearchManager;
import Reika.ChromatiCraft.Magic.Progression.ProgressionManager;
import Reika.ChromatiCraft.Magic.Progression.ProgressionManager.ProgressLink;
import Reika.ChromatiCraft.Magic.Progression.ProgressionManager.ProgressTier;
import Reika.ChromatiCraft.Magic.Progression.ResearchLevel;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Objects.LineType;

public class GuiProgressByLevel extends GuiProgressStages {

	private final Map<ProgressLink, ProgressTier> levels = ProgressionManager.instance.constructResearchLevelDepthMap();
	private final MultiMap<ProgressTier, ProgressLink> tiers = new MultiMap();

	public GuiProgressByLevel(EntityPlayer ep) {
		super(ChromaGuis.PROGRESSBYTIER, ep);
		this.initMap();
		for (Entry<ProgressLink, ProgressTier> e : levels.entrySet()) {
			tiers.addValue(e.getValue(), e.getKey());
		}
	}

	@Override
	public void drawScreen(int x, int y, float f) {
		super.drawScreen(x, y, f);

		int posX = (width - xSize) / 2;
		int posY = (height - ySize) / 2 - 8;

		this.renderElements(posX, posY);

		int o = 0;
		int h = elementHeight+8;
		int x0 = posX+12-4-1;
		int y0 = posY+36-4-6;
		int minx = x0-offsetX+1;
		for (ProgressTier pt : tiers.keySet()) {
			Collection<ProgressLink> c = tiers.get(pt);
			int w = elementWidth*c.size()+spacingX*(c.size()-1)+8;
			int miny = y0-offsetY+pt.getIndex()*(h+spacingY-8)+6;
			ResearchLevel at = ChromaResearchManager.instance.getPlayerResearchLevel(player);
			int clr = pt.level.isAtLeast(at) ? 0xff0000 : 0x00ff00;
			if (at == pt.level)
				clr = ReikaColorAPI.mixColors(0x22aaff, 0xffffff, (float)(0.5+0.5*Math.sin(System.currentTimeMillis()/120D)));
			//api.drawRectFrame(x1, y1, w, h, 0xff000000 | clr, LineType.DOTTED);
			int x1 = Math.max(minx, x0);
			int y1 = Math.max(y0, miny);
			int x2 = Math.min(minx+w, x0+paneWidth-2);
			int y2 = Math.min(miny+h, y0+paneHeight-22);
			if (x2 <= x1 || y2 <= y1)
				continue;
			api.drawLine(x1, y1, x2, y1, clr, LineType.DOTTED);
			api.drawLine(x1, y2, x2, y2, clr, LineType.DOTTED);
			api.drawLine(x1, y1, x1, y2, clr, LineType.DOTTED);
			api.drawLine(x2, y1, x2, y2, clr, LineType.DOTTED);
		}

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
	protected int getDepth(ProgressLink p) {
		return levels.get(p).getIndex();
	}

	@Override
	protected Collection<ProgressLink> getProgress() {
		return levels.keySet();
	}
}
