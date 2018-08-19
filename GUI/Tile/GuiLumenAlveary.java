/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2018
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI.Tile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.GuiChromaBase;
import Reika.ChromatiCraft.ModInterface.Bees.TileEntityLumenAlveary;
import Reika.ChromatiCraft.ModInterface.Bees.TileEntityLumenAlveary.AlvearyEffect;
import Reika.ChromatiCraft.ModInterface.Bees.TileEntityLumenAlveary.LumenAlvearyEffect;
import Reika.ChromatiCraft.ModInterface.Bees.TileEntityLumenAlveary.VisAlvearyEffect;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.CoreContainer;
import Reika.DragonAPI.Instantiable.Data.Proportionality;
import Reika.DragonAPI.Instantiable.Data.Proportionality.ColorCallback;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap.SortedDeterminator;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;


public class GuiLumenAlveary extends GuiChromaBase {

	private static final Collection<AlvearyEffect> allEffects = new ArrayList();

	private final TileEntityLumenAlveary tile;
	private final ArrayList<AlvearyEffect> activeEffects = new ArrayList();
	private Proportionality<AlvearyEffectControl> globalChart;
	private final Proportionality<AlvearyEffectControl>[] colorCharts = new Proportionality[16];

	private final MultiMap<CrystalElement, AlvearyEffectControl> controls = new MultiMap();

	private CrystalElement selectedColor;

	static {
		allEffects.addAll(TileEntityLumenAlveary.getEffectSet());
	}

	public GuiLumenAlveary(EntityPlayer ep, TileEntityLumenAlveary te) {
		super(new CoreContainer(ep, te), ep, te);
		tile = te;

		this.setData();
	}

	private void setData() {
		activeEffects.clear();
		controls.clear();
		activeEffects.addAll(tile.getSelectedEffects());
		Collections.sort(activeEffects, TileEntityLumenAlveary.effectSorter);
		globalChart = new Proportionality(new SortedDeterminator());
		int[] colorCost = new int[16];
		for (int i = 0; i < 16; i++) {
			colorCharts[i] = new Proportionality();
		}
		for (AlvearyEffect e : activeEffects) {
			if (e instanceof LumenAlvearyEffect) {
				LumenAlvearyEffect l = (LumenAlvearyEffect)e;
				AlvearyEffectControl a = new AlvearyEffectControl(l, controls.get(l.color).size());
				a.isActive = activeEffects.contains(a.effect);
				colorCharts[l.color.ordinal()].addValue(a, l.requiredEnergy);
				colorCost[l.color.ordinal()] += l.requiredEnergy;
				controls.addValue(l.color, a);
				globalChart.addValue(a, l.requiredEnergy);
			}
			else if (ModList.THAUMCRAFT.isLoaded() && e instanceof VisAlvearyEffect) {
				VisAlvearyEffect v = (VisAlvearyEffect)e;
			}
			else {

			}
		}
		for (int i = 0; i < 16; i++) {
			//globalChart.addValue(CrystalElement.elements[i], colorCost[i]);
		}
	}

	@Override
	protected void mouseClicked(int x, int y, int button) {
		super.mouseClicked(x, y, button);

		if (selectedColor != null) {
			AlvearyEffectControl e = colorCharts[selectedColor.ordinal()].getClickedSection(x, y);
			if (e != null) {
				e.isActive = !e.isActive;
				ReikaSoundHelper.playClientSound(ChromaSounds.GUICLICK, player, 1, 1);
				ReikaPacketHelper.sendPacketToServer(ChromatiCraft.packetChannel, ChromaPackets.ALVEARYEFFECT.ordinal(), tile, e.effect.ID, e.isActive ? 1 : 0);
			}
			else {//if (GuiScreen.isShiftKeyDown()) {
				selectedColor = null;
				ReikaSoundHelper.playClientSound(ChromaSounds.GUICLICK, player, 1, 1);
			}
		}
		else {
			AlvearyEffectControl e = globalChart.getClickedSection(x, y);
			if (e != null) {
				selectedColor = e.effect.color;
				ReikaSoundHelper.playClientSound(ChromaSounds.GUICLICK, player, 1, 1);
			}
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int a, int b) {
		super.drawGuiContainerBackgroundLayer(f, a, b);
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		int x = j+xSize/2;
		int y = k+ySize/2;
		int r = 64;

		if (selectedColor != null) {
			AlvearyEffectControl e = colorCharts[selectedColor.ordinal()].getClickedSection(a, b);
			if (e != null) {
				e.isHovered = true;
			}
			colorCharts[selectedColor.ordinal()].renderAsPie(x, y, r, 0);
			if (e != null) {
				api.drawTooltipAt(fontRendererObj, e.effect.getDescription(), a, b);
			}
		}
		else {
			AlvearyEffectControl ae = globalChart.getClickedSection(a, b);
			if (ae != null) {
				CrystalElement e = ae.effect.color;
				Collection<AlvearyEffectControl> c = controls.get(e);
				for (AlvearyEffectControl ae2 : c) {
					ae2.isColorHovered = true;
				}
				api.drawTooltipAt(fontRendererObj, String.valueOf(e.displayName+": "+c.size()+" Effects"), a, b);
			}
			globalChart.renderAsPie(x, y, r, 0);
			if (ae != null) {
				CrystalElement e = ae.effect.color;
				Collection<AlvearyEffectControl> c = controls.get(e);
				api.drawTooltipAt(fontRendererObj, String.valueOf(e.displayName+": "+c.size()+" Effects"), a, b);
			}
		}
		for (AlvearyEffectControl ae : controls.allValues(false)) {
			ae.isColorHovered = ae.isHovered = false;
		}

		float lf = GL11.glGetFloat(GL11.GL_LINE_WIDTH);
		GL11.glLineWidth(4);
		api.drawCircle(x, y, r, 0xff000000);
		GL11.glLineWidth(lf);
	}

	@Override
	public String getGuiTexture() {
		return "alveary";
	}

	private static class AlvearyEffectControl implements ColorCallback, Comparable<AlvearyEffectControl> {

		private final LumenAlvearyEffect effect;
		private final int index;

		private final int hue;

		private boolean isActive = true;
		private boolean isColorHovered = false;
		private boolean isHovered = false;

		private AlvearyEffectControl(LumenAlvearyEffect l, int idx) {
			effect = l;
			index = idx;

			hue = ReikaColorAPI.getHue(l.color.getColor())-45+15*index;
		}

		@Override
		public int getColor(Object key) {
			float f = 1-index/(float)(isColorHovered ? 12 : 6);
			//int c = ReikaColorAPI.getModifiedHue(effect.color.getColor(), hue);
			int c = ReikaColorAPI.getColorWithBrightnessMultiplier(effect.color.getColor(), f);
			if (isHovered && isActive) {
				return ReikaColorAPI.mixColors(c, 0xffffff, 0.75F);
			}
			else if (isColorHovered) {
				return ReikaColorAPI.mixColors(c, 0xffffff, 0.875F);
			}
			else {
				return ReikaColorAPI.getColorWithBrightnessMultiplier(c, isActive ? 1 : (isHovered ? 0.625F : 0.25F));
			}
		}

		@Override
		public int compareTo(AlvearyEffectControl o) {
			return TileEntityLumenAlveary.effectSorter.compare(effect, o.effect);
		}

	}

}
