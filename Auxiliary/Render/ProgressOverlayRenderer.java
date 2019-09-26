/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.Render;

import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;

import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.RecipesCastingTable;
import Reika.ChromatiCraft.Magic.Progression.ChromaResearchManager.ProgressElement;
import Reika.ChromatiCraft.Magic.Progression.ProgressStage;
import Reika.ChromatiCraft.Magic.Progression.ProgressionManager.ColorDiscovery;
import Reika.ChromatiCraft.Magic.Progression.ProgressionManager.StructureComplete;
import Reika.ChromatiCraft.Magic.Progression.ResearchLevel;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;


public class ProgressOverlayRenderer {

	static final ProgressOverlayRenderer instance = new ProgressOverlayRenderer();

	private static final RenderItem itemRender = new RenderItem();
	private static final int PROGRESS_DURATION = Math.max(100, ChromaOptions.PROGRESSDURATION.getValue());

	private final TreeMap<ProgressElement, Integer> progressFlags = new TreeMap(new ProgressComparator());

	private int soundCooldown;

	private ProgressOverlayRenderer() {

	}

	void renderProgressOverlays(EntityPlayer ep, int gsc) {
		HashMap<ProgressElement, Integer> map = new HashMap();
		int dy = 0;
		//ReikaJavaLibrary.pConsole(progressFlags.keySet());
		for (ProgressElement p : progressFlags.keySet()) {
			int tick = progressFlags.get(p);
			GL11.glColor4f(1, 1, 1, 1);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_LIGHTING);

			FontRenderer fr = ChromaFontRenderer.FontType.HUD.renderer;
			int sw = Math.max(40, fr.getStringWidth(p.getTitle()));
			int sh = 24+(fr.listFormattedStringToWidth(p.getShortDesc(), sw*2).size()-1)*4;//24;
			int w = sw+28;//144;
			int h = tick > PROGRESS_DURATION-sh ? PROGRESS_DURATION-tick : tick < sh ? tick : sh;

			int x = Minecraft.getMinecraft().displayWidth/gsc-w-1;

			ReikaGuiAPI.instance.drawRect(x, dy, x+w, dy+h, 0xff444444);
			ReikaGuiAPI.instance.drawRectFrame(x+1, dy+1, w-2, h-2, 0xcccccc);
			ReikaGuiAPI.instance.drawRectFrame(x+2, dy+2, w-4, h-4, 0xcccccc);

			GL11.glEnable(GL11.GL_TEXTURE_2D);

			if (h == sh) {

				fr.drawString(p.getTitle(), x+w-4-sw, dy+8-4, 0xffffff);
				GL11.glPushMatrix();
				double s = 0.5;
				GL11.glScaled(s, s, s);
				GL11.glTranslated(x+16+8, dy+16-1, 0);
				fr.drawSplitString(p.getShortDesc(), x+w-4-sw, dy+8+4, sw*2, 0xffffff);
				GL11.glPopMatrix();

				GL11.glEnable(GL11.GL_LIGHTING);

				p.renderIcon(itemRender, fr, x+4, dy+4);

			}

			GL11.glEnable(GL11.GL_LIGHTING);

			if (tick > 1) {
				map.put(p, tick-(DragonAPICore.debugtest ? 32 : 1));
			}
			dy += h+4;
			if (dy > Minecraft.getMinecraft().displayHeight/gsc-h) {
				//break;
				map.put(p, tick);
			}
		}
		//if (map.isEmpty())
		progressFlags.clear();
		//else
		//	progressFlags.keySet().removeAll(map.keySet());
		progressFlags.putAll(map);
		if (soundCooldown > 0)
			soundCooldown--;
	}

	void addProgressionNote(ProgressElement p) {
		progressFlags.put(p, PROGRESS_DURATION);

		if (soundCooldown == 0) {
			ReikaSoundHelper.playClientSound(ChromaSounds.GAINPROGRESS, Minecraft.getMinecraft().thePlayer, 0.5F, 1, false);
			soundCooldown = 24;
		}
		//ReikaJavaLibrary.pConsole("Adding "+p+" to map ("+progressFlags.keySet().contains(p)+"), set is "+progressFlags.keySet());
	}

	private static final class ProgressComparator implements Comparator<ProgressElement> {

		/** General order:
			ProgressStage - 0 by ordinal
			ColorDiscovery - 1 by color ordinal
			ResearchLevel - 2 by ordinal
			ChromaResearch - 3 by research level by ordinal
			CastingRecipe - 4 by ID
		 */

		@Override
		public int compare(ProgressElement o1, ProgressElement o2) {
			return this.getIndex(o1)-this.getIndex(o2);
		}

		private int getIndex(ProgressElement e) {
			if (e instanceof ColorDiscovery) {
				return ((ColorDiscovery)e).color.ordinal();
			}
			if (e instanceof StructureComplete) {
				return 500000+((StructureComplete)e).color.ordinal();
			}
			else if (e instanceof ProgressStage) {
				return 1000000+((ProgressStage)e).ordinal();
			}
			else if (e instanceof ResearchLevel) {
				return 2000000+((ResearchLevel)e).ordinal();
			}
			else if (e instanceof ChromaResearch) {
				return 3000000+1000*((ChromaResearch)e).level.ordinal()+((ChromaResearch)e).ordinal();
			}
			else if (e instanceof CastingRecipe) {
				return 3000000+RecipesCastingTable.instance.getIDForRecipe((CastingRecipe)e);
			}
			else
				return -1;
		}

	}

}
