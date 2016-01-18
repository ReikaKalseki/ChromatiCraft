/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI.Book;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaFontRenderer;
import Reika.ChromatiCraft.Auxiliary.CustomSoundGuiButton.CustomSoundImagedGuiButton;
import Reika.ChromatiCraft.Base.GuiScrollingPage;
import Reika.ChromatiCraft.Items.Tools.ItemChromaBook;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Registry.ChromaResearchManager.ResearchLevel;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Instantiable.Data.Maps.PluralMap;
import Reika.DragonAPI.Instantiable.Data.Maps.RegionMap;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;

public class GuiNavigation extends GuiScrollingPage {

	private static boolean craftMode = false;

	private final TreeMap<ChromaResearch, Section> sections = new TreeMap();
	private static final int SectionSpacing = 32;
	private static RegionMap<SectionElement> locations = new RegionMap();
	private static PluralMap<String> tooltips = new PluralMap(2);

	public GuiNavigation(EntityPlayer ep) {
		super(ChromaGuis.BOOKNAV, ep, 256, 220, 242, 206);

		Section z = null;
		for (int i = 0; i < ChromaResearch.researchList.length; i++) {
			ChromaResearch b = ChromaResearch.researchList[i];
			if (b.isParent()) {
				z = new Section(b.getTitle());
				sections.put(b, z);
			}
			else {
				if (ItemChromaBook.hasPage(player.getCurrentEquippedItem(), b)) {
					//if (b.playerCanSee(ep)) {
					if (!b.isDummiedOut())
						z.addElement(new SectionElement(b));
				}
			}
		}
		Iterator<ChromaResearch> it = sections.keySet().iterator();
		while (it.hasNext()) {
			ChromaResearch r = it.next();
			Section s = sections.get(r);
			if (s.elements.isEmpty()) {
				it.remove();
			}
			else {
				maxX = Math.max(maxX, leftX+15+s.getWidth(4));
				maxY += SectionSpacing+s.getHeight(4);
			}
		}
		maxX -= paneWidth+Section.sectionSpacing+Section.margin*2;
		maxY -= paneHeight+SectionSpacing/2;

		int sc = ReikaRenderHelper.getGUIScale();
		maxX *= 1;//sc;
		maxY *= 1;//sc;

		//craftMode = false;
	}

	@Override
	public void initGui() {
		super.initGui();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		String file = "Textures/GUIs/Handbook/buttons.png";
		if (craftMode) {
			this.addAuxButton(new CustomSoundImagedGuiButton(0, j-13, k-7, 13, 88, 15, 95, file, ChromatiCraft.class, this), "Items");
			this.addAuxButton(new CustomSoundImagedGuiButton(1, j-13, k+27, 13, 88, 15, 4, file, ChromatiCraft.class, this), "Recipes");
		}
		else {
			this.addAuxButton(new CustomSoundImagedGuiButton(1, j-13, k+27, 13, 88, 15, 95, file, ChromatiCraft.class, this), "Recipes");
			this.addAuxButton(new CustomSoundImagedGuiButton(0, j-13, k-7, 13, 88, 15, 4, file, ChromatiCraft.class, this), "Items");
		}

		this.addAuxButton(new CustomSoundImagedGuiButton(2, j+xSize, k, 22, 39, 42, 84, file, ChromatiCraft.class, this), "Progress");
		this.addAuxButton(new CustomSoundImagedGuiButton(3, j+xSize, k+40, 22, 39, 42, 168, file, ChromatiCraft.class, this), "Recovery");
		this.addAuxButton(new CustomSoundImagedGuiButton(4, j+xSize, k+80, 22, 39, 42, 168, file, ChromatiCraft.class, this), "Notebook");
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		super.actionPerformed(button);
		//Do things
		if (button.id == 0) {
			craftMode = false;
			buttonList.clear();
		}
		else if (button.id == 1) {
			craftMode = true;
			buttonList.clear();
		}
		else if (button.id == 2) {
			this.goTo(ChromaGuis.PROGRESS, null);
			this.resetOffset();
		}
		else if (button.id == 3) {
			this.goTo(ChromaGuis.REFRAGMENT, null);
			this.resetOffset();
		}
		else if (button.id == 4) {
			this.goTo(ChromaGuis.NOTES, null);
			this.resetOffset();
		}
		this.initGui();
	}

	@Override
	public String getBackgroundTexture() {
		return "Textures/GUIs/Handbook/navigation2.png";
	}

	@Override
	protected String getScrollingTexture() {
		return "Textures/GUIs/Handbook/navbcg.png";
	}

	@Override
	public void drawScreen(int x, int y, float f) {
		locations.clear();
		tooltips.clear();

		super.drawScreen(x, y, f);

		this.drawSections(leftX+11-offsetX, topY+11-offsetY);

		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glPushMatrix();
		GL11.glTranslated(0, 0, 500);
		GL11.glPopMatrix();

		for (List<Object> o : tooltips.pluralKeySet()) {
			int a = (Integer)o.get(0);
			int b = (Integer)o.get(1);
			String s = tooltips.get(a, b);
			api.drawTooltipAt(fontRendererObj, s, a, b);
		}
	}

	@Override
	protected void mouseClicked(int x, int y, int b) {
		super.mouseClicked(x, y, b);

		SectionElement e = this.getSectionElementAt(x, y);
		if (e != null && e.getGuiType() != null) {
			this.goTo(e.getGuiType(), e.destination);
		}
	}

	private SectionElement getSectionElementAt(int x, int y) {
		return locations.getRegion(x, y);
	}

	private void drawSections(int x, int y) {
		float line = GL11.glGetFloat(GL11.GL_LINE_WIDTH);
		GL11.glLineWidth(2);
		int dy = y+1;
		for (Section z : sections.values()) {
			int dx = x+4;
			int n = z.allOneLevel() ? 10 : 4;
			int c = ReikaColorAPI.GStoHex(15+z.hoverTime*12);
			int ddx = dx+z.getWidth(n);
			int ddy = dy+z.getHeight(n);

			int dx_ = MathHelper.clamp_int(dx, leftX+2, leftX+paneWidth+10);
			int dy_ = MathHelper.clamp_int(dy, topY-5, topY+paneHeight+5);
			int ddx_ = MathHelper.clamp_int(ddx, leftX+2, leftX+paneWidth+10);
			int ddy_ = MathHelper.clamp_int(ddy, topY-5, topY+paneHeight+5);

			api.drawLine(dx_, dy_, ddx_, dy_, c);
			api.drawLine(dx_, dy_, dx_, ddy_, c);
			api.drawLine(ddx_, dy_, ddx_, ddy_, c);
			api.drawLine(dx_, ddy_, ddx_, ddy_, c);
			if (dx >= leftX && dx <= leftX+paneWidth-fontRendererObj.getStringWidth(z.title))
				if (dy >= topY && dy <= topY+paneHeight-fontRendererObj.FONT_HEIGHT/2)
					fontRendererObj.drawString(z.title, dx+2, dy-fontRendererObj.FONT_HEIGHT, ReikaColorAPI.mixColors(c, 0xffffff, 0.675F));
			z.drawElements(dx, dy+fontRendererObj.FONT_HEIGHT+2, n);
			dy += z.getHeight(n)+SectionSpacing;
			if (dy >= paneHeight && Minecraft.getMinecraft().gameSettings.guiScale == 0) //causes errors on other gui scales
				break;
		}
		GL11.glLineWidth(line);
	}

	private static boolean craftMode() {
		return GuiScreen.isCtrlKeyDown() ^ craftMode;
	}

	private class Section/* implements Comparable<Section>*/ {

		private final TreeMap<ResearchLevel, SectionCategory> elements = new TreeMap();
		public final String title;

		private int hoverTime;

		private static final int elementWidth = 24;
		private static final int sectionSpacing = 64;
		private static final int margin = 8;
		private static final int spacing = 4;

		public Section(String s) {
			title = s;
		}

		public int getLevelCount() {
			return elements.keySet().size();
		}

		public boolean allOneLevel() {
			return this.getLevelCount() == 1;
		}

		public void addElement(SectionElement e) {
			SectionCategory s = elements.get(e.research());
			if (s == null) {
				s = new SectionCategory(e.research());
				elements.put(e.research(), s);
			}
			s.addElement(e);
		}

		public int getHeight(int cols) {
			int max = 0;
			for (ResearchLevel rl : elements.keySet()) {
				max = Math.max(max, sectionSpacing+this.getSubSectionHeight(cols, elements.get(rl)));
			}
			max -= sectionSpacing;
			return max+margin+Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT+2;
		}

		public int getWidth(int cols) {
			int sum = 0;
			for (ResearchLevel rl : elements.keySet()) {
				sum += this.getSubsectionWidth(cols, elements.get(rl), true);
				sum += sectionSpacing;
			}
			sum -= sectionSpacing;
			return sum+margin+Minecraft.getMinecraft().fontRenderer.getStringWidth(elements.lastKey().getDisplayName())-elementWidth-margin;
		}

		public int getSubSectionHeight(int cols, SectionCategory se) {
			int num = 1+(se.size()-1)/cols;
			return num*elementWidth+(num-1)*spacing+margin-1;
		}

		public int getSubsectionWidth(int cols, SectionCategory se, boolean wTitle) {
			int num = se.size() >= cols ? cols : se.size()%cols;
			int w = num*elementWidth+(num-1)*spacing+margin-1;
			if (wTitle)
				w = Math.max(w, Minecraft.getMinecraft().fontRenderer.getStringWidth(se.level.getDisplayName())-sectionSpacing+24);
			return w;
		}

		private void increaseHover() {
			if (hoverTime < 20)
				hoverTime++;
		}

		private void decreaseHover() {
			if (hoverTime > 0 && GuiNavigation.this.getGuiTick()%2 == 0)
				hoverTime--;
		}

		private void drawElements(int x, int y, int cols) {
			int dox = 0;
			boolean hovered = false;
			for (ResearchLevel rl : elements.keySet()) {
				SectionCategory se = elements.get(rl);
				int c = ReikaColorAPI.GStoHex(15+se.hoverTime*12);//ReikaColorAPI.GStoHex(15+hoverTime*12);
				//c = ReikaColorAPI.mixColors(c, 0x22aaff, 1-se.hoverTime/20F);
				int dx2 = x+dox+4;
				int dy2 =  y+4;
				int ddx2 = dx2+this.getSubsectionWidth(cols, se, false);
				int ddy2 = dy2+this.getSubSectionHeight(cols, se);

				int dx2_ = MathHelper.clamp_int(dx2, leftX+2, leftX+paneWidth+10);
				int dy2_ = MathHelper.clamp_int(dy2, topY-5, topY+paneHeight+5);
				int ddx2_ = MathHelper.clamp_int(ddx2, leftX+2, leftX+paneWidth+10);
				int ddy2_ = MathHelper.clamp_int(ddy2, topY-5, topY+paneHeight+5);
				api.drawLine(dx2_, dy2_, ddx2_, dy2_, c);
				api.drawLine(dx2_, dy2_, dx2_, ddy2_, c);
				api.drawLine(ddx2_, dy2_, ddx2_, ddy2_, c);
				api.drawLine(dx2_, ddy2_, ddx2_, ddy2_, c);
				FontRenderer fr = ChromaFontRenderer.FontType.LEXICON.renderer;
				String title = rl.getDisplayName();
				if (dx2 >= leftX && dx2 <= leftX+paneWidth-fr.getStringWidth(title)) {
					if (dy2 >= topY && dy2 <= topY+paneHeight-fr.FONT_HEIGHT/2) {
						GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
						GL11.glDisable(GL11.GL_LIGHTING);
						GL11.glColor4f(1, 1, 1, 1);
						fr.drawString(title, dx2, y-5, ReikaColorAPI.mixColors(c, 0xffffff, 0.75F));
						GL11.glPopAttrib();
					}
				}
				hovered |= se.renderElements(x, y, cols, dox);
				dox += Math.max(this.getSubsectionWidth(cols, se, true)+sectionSpacing, 0*24+0*Minecraft.getMinecraft().fontRenderer.getStringWidth(se.level.getDisplayName()));
			}
			if (hovered) {
				this.increaseHover();
			}
			else {
				this.decreaseHover();
			}
		}
		/*
		@Override
		public int compareTo(Section o) {
			return elements.get(0).destination.ordinal()-o.elements.get(0).destination.ordinal();
		}*/

	}

	private class SectionCategory {

		private final ArrayList<SectionElement> elements = new ArrayList();
		private final ResearchLevel level;

		private int hoverTime = 0;

		private SectionCategory(ResearchLevel r) {
			level = r;
		}

		private void addElement(SectionElement e) {
			elements.add(e);
			Collections.sort(elements);
		}

		private boolean renderElements(int x, int y, int cols, int dox) {
			boolean hovered = false;
			int i = 0;
			for (SectionElement e : elements) {
				int dx = x+Section.margin+(i%cols)*(Section.elementWidth+Section.spacing)+dox;
				int dy = y+Section.margin+(i/cols)*(Section.elementWidth+Section.spacing);
				GL11.glPushMatrix();
				double s = Section.elementWidth/16D;
				GL11.glScaled(s, s, 1);
				int ix = (int)Math.round(dx/s);
				int iy = (int)Math.round(dy/s);
				if (dx >= leftX && dx <= leftX+paneWidth-Section.elementWidth) {
					if (dy >= topY && dy <= topY+paneHeight-Section.elementWidth) {
						e.draw(ix, iy);
						int mx = dx;
						int mmx = mx+Section.elementWidth;
						int my = dy;
						int mmy = my+Section.elementWidth;
						if (api.isMouseInBox(mx, mmx, my, mmy)) {
							e.increaseHover();
							tooltips.put(e.getName(), api.getMouseRealX(), api.getMouseRealY());
							locations.addRegionByWH(mx, my, Section.elementWidth, Section.elementWidth, e);
							hovered = true;
						}
						else {
							e.decreaseHover();
						}
						if (e.hoverTime > 0) {
							int w = (int)Math.round(Section.elementWidth/s);
							int mxs = (int)Math.round(mx/s);
							int mys = (int)Math.round(my/s);
							api.drawRectFrame(mxs, mys, w, w, ReikaColorAPI.GStoHex(15+e.hoverTime*12));
						}
					}
				}
				GL11.glPopMatrix();
				i++;
			}
			if (hovered) {
				this.increaseHover();
			}
			else {
				this.decreaseHover();
			}
			return hovered;
		}

		private int size() {
			return elements.size();
		}

		private void increaseHover() {
			if (hoverTime < 20)
				hoverTime++;
		}

		private void decreaseHover() {
			if (hoverTime > 0 && GuiNavigation.this.getGuiTick()%3 > 0)
				hoverTime--;
		}

	}

	private static class SectionElement implements Comparable<SectionElement> {

		private int hoverTime = 0;
		private final ChromaResearch destination;

		private SectionElement(ChromaResearch b) {
			destination = b;
		}

		private void increaseHover() {
			if (hoverTime < 20)
				hoverTime++;
		}

		private void decreaseHover() {
			if (hoverTime > 0)
				hoverTime--;
		}

		public void draw(int ex, int ey) {
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			float f = 0.2F+hoverTime/25F;
			GL11.glColor4f(1, 1, 1, 1);
			GL11.glEnable(GL11.GL_BLEND);
			destination.drawTabIcon(itemRender, ex, ey);//api.drawItemStack(itemRender, this.getIcon(), ex, ey);

			GL11.glColor4f(1, 1, 1, 1);

			GL11.glEnable(GL11.GL_BLEND);
			if (craftMode() && (destination.isCrafting() || destination.isAbility())) {
				GL11.glPushMatrix();
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glTranslated(0, 0, 400);
				ReikaTextureHelper.bindTerrainTexture();
				IIcon ico = ChromaTiles.TABLE.getBlock().getIcon(1, ChromaTiles.TABLE.getBlockMetadata());
				if (destination.isAbility()) {
					ico = ChromaTiles.RITUAL.getBlock().getIcon(1, ChromaTiles.RITUAL.getBlockMetadata());
				}
				else if (destination.isVanillaRecipe()) {
					ico = Blocks.crafting_table.getIcon(1, 0);
				}
				api.drawTexturedModelRectFromIcon(ex+8, ey+8, ico, 9, 9);
				if (destination.isCrafting() && !destination.isCraftable()) {
					ico = ChromaIcons.NOENTER.getIcon();
					GL11.glColor4f(1, 1, 1, 0.75F);
					api.drawTexturedModelRectFromIcon(ex+8, ey+8, ico, 9, 9);
				}
				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glPopMatrix();
			}
			GL11.glPopAttrib();
		}

		public ChromaGuis getGuiType() {
			switch(destination.getParent()) {
				case MACHINEDESC:
					return craftMode() && destination.isCraftable() ? destination.getCraftingType() : ChromaGuis.MACHINEDESC;
				case RESOURCEDESC:
					return craftMode() && destination.isCraftable() ? destination.getCraftingType() : ChromaGuis.BASICDESC;
				case TOOLDESC:
					return craftMode() && destination.isCraftable() ? destination.getCraftingType() : ChromaGuis.TOOLDESC;
				case BLOCKS:
					return craftMode() && destination.isCraftable() ? destination.getCraftingType() : ChromaGuis.BASICDESC;
				case ABILITYDESC:
					return craftMode() ? ChromaGuis.RITUAL : ChromaGuis.ABILITYDESC;
				case INTRO:
					return craftMode() && destination.isCraftable() ? destination.getCraftingType() : ChromaGuis.INFO;
				case STRUCTUREDESC:
					return ChromaGuis.STRUCTURE;
				default:
					return null;
			}
		}

		public int getGuiID() {
			return destination.ordinal();
		}

		//private ItemStack getIcon() {
		//	return destination.getTabIcon();
		//}

		public String getName() {
			return destination.getTitle();
		}

		public ResearchLevel research() {
			return destination.level;
		}

		@Override
		public int compareTo(SectionElement o) {
			return (destination.getParent().ordinal()-o.destination.getParent().ordinal())*10000+destination.ordinal()-o.destination.ordinal();
		}

		@Override
		public String toString() {
			return "ELEMENT{"+destination.toString()+"}";
		}

	}

}
