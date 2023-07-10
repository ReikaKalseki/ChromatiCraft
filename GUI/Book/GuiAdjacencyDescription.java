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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;

import Reika.ChromatiCraft.Auxiliary.ChromaDescriptions;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityAdjacencyUpgrade;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityAdjacencyUpgrade.AdjacencyEffectDescription;
import Reika.ChromatiCraft.Registry.AdjacencyUpgrades;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Instantiable.GUI.GuiItemDisplay;

public class GuiAdjacencyDescription extends GuiMachineDescription {

	private final ArrayList<AdjacencyPage> pages = new ArrayList();

	public GuiAdjacencyDescription(EntityPlayer ep) {
		super(ep, ChromaResearch.ACCEL);
		pages.add(new AdjacencyPage(Pages.MAIN, null, page.getData(), false));
		pages.add(new AdjacencyPage(Pages.NOTES, null, page.getNotes(0), false));
		for (int i = 0; i < 16; i++) {
			AdjacencyUpgrades a = AdjacencyUpgrades.upgrades[i];
			if (a.isImplemented()) {
				pages.add(new AdjacencyPage(Pages.NOTES, a, ChromaDescriptions.getNotes(page, i+1), false));
				Collection<AdjacencyEffectDescription> c = TileEntityAdjacencyUpgrade.getSpecificEffects(a.getColor());
				if (!c.isEmpty()) {/*
					StringBuilder sb = new StringBuilder();
					sb.append("The following is a non-exhaustive sample list of effects:");
					sb.append("\n\n");
					for (SpecificAdjacencyEffect e : c) {
						sb.append("-");
						sb.append(e.getDescription());
						sb.append("\n");
					}
					pages.add(new AdjacencyPage(Pages.NOTES, a, sb.toString()));
				 */
					pages.add(new AdjacencyPage(Pages.NOTES, a, "The following is a sample list of effects:", true));
				}
			}
		}
	}

	@Override
	protected boolean hasScroll() {
		AdjacencyPage ap = this.getCurrentPage();
		return ap.isEffectList;// && TileEntityAdjacencyUpgrade.getSpecificEffects(ap.coreType.getColor()).size() > 5;
	}

	@Override
	protected int getMaxScroll() {
		return 50;
	}

	@Override
	protected PageType getGuiLayout() {
		return this.getCurrentPage().isEffectList ? PageType.ADJACENCY : PageType.PLAIN;
	}

	@Override
	protected int getMaxSubpage() {
		return pages.size()-1;
	}

	@Override
	protected String getText(int subpage) {
		String ret = this.getCurrentPage().text;
		if (ChromaOptions.POWEREDACCEL.getState() && subpage == 1)
			ret += "\nRequire "+this.getMachine().getColor().displayName+" lumens from an "+ChromaTiles.WIRELESS.getName();
		return ret;
	}

	public AdjacencyUpgrades getMachine() {
		AdjacencyUpgrades a = this.getCurrentPage().coreType;
		if (a == null && subpage <= 1)
			return AdjacencyUpgrades.upgrades[(int)((System.currentTimeMillis()/(500*TileEntityAdjacencyUpgrade.MAX_TIER))%AdjacencyUpgrades.upgrades.length)];
		return a;
	}

	private AdjacencyPage getCurrentPage() {
		return pages.get(subpage);
	}

	@Override
	protected void drawNotesGraphics(int posX, int posY) {
		AdjacencyPage ap = this.getCurrentPage();
		if (ap.isEffectList) {
			Collection<AdjacencyEffectDescription> li = TileEntityAdjacencyUpgrade.getSpecificEffects(ap.coreType.getColor());
			int oy = posY+107;
			int dy = 0;
			for (AdjacencyEffectDescription s : li) {
				List<GuiItemDisplay> items = s.getRelevantItems();
				if (!items.isEmpty()) {
					int ox = posX+12;
					int dx = 0;
					for (GuiItemDisplay g : items) {
						int dx2 = dx+ox;
						int dy2 = dy+oy-textOffset*17;
						if (dx2 >= 0 && dy2 >= oy && dy2 <= oy+90) {
							g.draw(fontRendererObj, dx2, dy2);
							if (api.isMouseInBox(dx2, dx2+16, dy2, dy2+16)) {
								String sg = s.description;
								api.drawTooltipAt(fontRendererObj, sg, api.getMouseRealX()+fontRendererObj.getStringWidth(sg)+22, api.getMouseRealY()+15);
							}
						}
						dx += 18;
						if (dx >= 220) {
							dx = 0;
							dy += 17;
						}
					}
					dy += 22;
				}
			}
		}
	}

	/*
	 	@Override
	protected void drawNotesGraphics(int posX, int posY) {
		AdjacencyPage ap = getCurrentPage();
		if (ap.isEffectList) {
			Collection<SpecificAdjacencyEffect> li = TileEntityAdjacencyUpgrade.getSpecificEffects(ap.coreType.getColor(), true);
			int i = 0;
			for (SpecificAdjacencyEffect s : li) {
				ArrayList<GuiItemDisplay> items = new ArrayList();
				s.getRelevantItems(items);
				items.removeIf(g -> g instanceof GuiStackDisplay && ((GuiStackDisplay)g).isEmpty());
				if (!items.isEmpty()) {
					for (GuiItemDisplay g : items) {
						int dx = posX+8+(i%15)*16;
						int dy = posY+108+(i/15)*17;
						g.draw(fontRendererObj, dx, dy);
						if (api.isMouseInBox(dx, dx+16, dy, dy+16)) {
							String sg = s.getDescription();
							api.drawTooltipAt(fontRendererObj, sg, api.getMouseRealX()+fontRendererObj.getStringWidth(sg)+22, api.getMouseRealY());
						}
						i++;
					}
					i++;
				}
			}
		}
	}
	 */

	private static class AdjacencyPage {

		private final AdjacencyUpgrades coreType;
		private final String text;
		private final Pages pageType;
		private final boolean isEffectList;

		private AdjacencyPage(Pages p, AdjacencyUpgrades a, String t, boolean e) {
			coreType = a;
			text = t;
			pageType = p;
			isEffectList = e;
		}

	}

}
