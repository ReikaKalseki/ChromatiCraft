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

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.lwjgl.util.Point;

import net.minecraft.entity.player.EntityPlayer;

import Reika.ChromatiCraft.Base.GuiProgressStages;
import Reika.ChromatiCraft.Magic.Progression.ProgressionManager;
import Reika.ChromatiCraft.Magic.Progression.ProgressionManager.ProgressLink;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.DragonAPI.Instantiable.Data.Maps.SequenceMap.Topology;
import Reika.DragonAPI.Libraries.MathSci.ReikaVectorHelper;

public class GuiProgressTree extends GuiProgressStages {

	//private ArrayList<ProgressStage> stages = new ArrayList();
	private final Topology<ProgressLink> map = ProgressionManager.instance.getTopology();
	private final Map<ProgressLink, Integer> levels = map.getDepthMap();

	public GuiProgressTree(EntityPlayer ep) {
		super(ChromaGuis.PROGRESS, ep);
		this.initMap();
	}

	/*
	private static Map<ProgressLink, Integer> modifyDepths(Map<ProgressLink, Integer> map) {
		Map<ProgressLink, Integer> ret = new HashMap();
		for (Entry<ProgressLink, Integer> e : map.entrySet()) {
			ProgressLink p = e.getKey();
			int l = e.getValue();
			ResearchLevel rl = ChromaResearchManager.instance.getEarliestResearchLevelRequiring(p.parent);
			//if (rl != null)
			//l += rl.ordinal()*2;
			ret.put(p, l);
		}
		return ret;
	}*/

	@Override
	public void drawScreen(int x, int y, float f) {
		super.drawScreen(x, y, f);

		int posX = (width - xSize) / 2;
		int posY = (height - ySize) / 2 - 8;

		this.renderTree(posX, posY);
		this.renderText(posX, posY);

		//ReikaJavaLibrary.pConsole(offsetX+"/"+maxX+","+offsetY+"/"+maxY);
	}

	private void renderTree(int posX, int posY) {
		this.renderLines(posX, posY);
		this.renderElements(posX, posY);
	}

	private void renderLines(int posX, int posY) {
		for (ProgressLink p : levels.keySet()) {
			this.renderLine(posX, posY, p);
		}
	}

	private void renderLine(int posX, int posY, ProgressLink p) {
		Collection<ProgressLink> c = map.getParents(p);
		int dx = -offsetX+posX+12;
		int dy = -offsetY+posY+36;
		Point pt = this.getRenderPosition(p.parent);
		for (ProgressLink par : c) {
			Point pt2 = this.getRenderPosition(par.parent);
			int x1 = dx+pt.getX()+elementWidth/2;
			int y1 = dy+pt.getY();
			/*
			if (this.elementOnScreen(p, posX, posY, x1, y1)) {
				int x2 = dx+pt2.getX()+elementWidth/2;
				int y2 = dy+pt2.getY()+elementHeight;
				if (this.elementOnScreen(par, posX, posY, x2, y2)) {
					api.drawLine(x1, y1, x2, y2, 0xffffff);
				}
			}
			 */
			int x2 = dx+pt2.getX()+elementWidth/2;
			int y2 = dy+pt2.getY()+elementHeight;

			ImmutablePair<java.awt.Point, java.awt.Point> ps = ReikaVectorHelper.clipLine(x1, x2, y1, y2, posX+8, posY+26, posX+xSize-8, posY+ySize/2+6);
			if (ps != null) {
				int clr = p.parent == this.getActive() || par.parent == this.getActive() ? (par.parent.isPlayerAtStage(player) ? 0x00ff00 : 0xff4040) : 0xffffff;
				api.drawLine(ps.left.x, ps.left.y, ps.right.x, ps.right.y, clr, par.type);
			}
		}
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
