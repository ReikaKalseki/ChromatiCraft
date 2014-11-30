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

import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ChromaBookGui;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Registry.ChromaResearchManager.ResearchLevel;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Instantiable.Data.RegionMap;
import Reika.DragonAPI.Instantiable.GUI.ImagedGuiButton;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;

import com.google.common.collect.TreeMultimap;

public class GuiNavigation extends ChromaBookGui {

	private int offsetX = 0;
	private int offsetY = 0;

	private int maxX = 0;
	private int maxY = 0;

	private static final int paneWidth = 242;
	private static final int paneHeight = 206;

	private static boolean craftMode = false;

	private final TreeMap<ChromaResearch, Section> sections = new TreeMap();
	private static final int SectionSpacing = 32;
	private static RegionMap<SectionElement> locations = new RegionMap();

	public GuiNavigation(EntityPlayer ep) {
		super(ep, 256, 220);

		Section z = null;
		for (int i = 0; i < ChromaResearch.researchList.length; i++) {
			ChromaResearch b = ChromaResearch.researchList[i];
			if (b.isParent()) {
				z = new Section(b.getTitle());
				sections.put(b, z);
			}
			else {
				if (b.playerCanSee(ep)) {
					z.addElement(new SectionElement(b));
				}
			}
		}
		Iterator<ChromaResearch> it = sections.keySet().iterator();
		while (it.hasNext()) {
			ChromaResearch r = it.next();
			Section s = sections.get(r);
			if (s.elements.isEmpty())
				it.remove();
			else {
				maxX = Math.max(maxX, leftX+15+s.getWidth(4));
				maxY += SectionSpacing+s.getHeight(4);
			}
		}
		maxX -= paneWidth+Section.sectionSpacing+Section.margin*2;
		maxY -= paneHeight+SectionSpacing/2;

		craftMode = false;
	}

	@Override
	public void initGui() {
		super.initGui();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		String file = "Textures/GUIs/Handbook/buttons.png";
		if (craftMode) {
			buttonList.add(new ImagedGuiButton(0, j-13, k-7, 13, 88, 15, 95, file, ChromatiCraft.class));
			buttonList.add(new ImagedGuiButton(1, j-13, k+27, 13, 88, 15, 4, file, ChromatiCraft.class));
		}
		else {
			buttonList.add(new ImagedGuiButton(1, j-13, k+27, 13, 88, 15, 95, file, ChromatiCraft.class));
			buttonList.add(new ImagedGuiButton(0, j-13, k-7, 13, 88, 15, 4, file, ChromatiCraft.class));
		}
	}

	@Override
	public void actionPerformed(GuiButton button) {
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
		this.initGui();
	}

	@Override
	public String getBackgroundTexture() {
		return "Textures/GUIs/Handbook/navigation2.png";
	}

	@Override
	public void drawScreen(int x, int y, float f) {
		leftX = (width - xSize) / 2;
		topY = (height - ySize) / 2;

		int sp = 1;
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			sp = 2;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			offsetY -= sp;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			offsetX -= sp;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			offsetY += sp;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			offsetX += sp;
		}

		if (offsetX < 0) {
			offsetX = 0;
		}
		if (offsetY < 0) {
			offsetY = 0;
		}
		if (offsetX > maxX) {
			offsetX = maxX;
		}
		if (offsetY > maxY) {
			offsetY = maxY;
		}

		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/GUIs/Handbook/navbcg.png");
		int u = offsetX%256;
		int v = offsetY%256;
		this.drawTexturedModalRect(leftX+7, topY-1, u, v, paneWidth, paneHeight);

		locations.clear();
		this.drawSections(leftX+11-offsetX, topY+11-offsetY);

		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glPushMatrix();
		GL11.glTranslated(0, 0, 500);
		super.drawScreen(x, y, f);
		GL11.glPopMatrix();
	}

	@Override
	public void mouseClicked(int x, int y, int b) {
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
			int n = 4;
			int c = 0xffffff;
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
					fontRendererObj.drawString(z.title, dx+2, dy-fontRendererObj.FONT_HEIGHT, 0xffffff);
			z.drawElements(dx, dy+fontRendererObj.FONT_HEIGHT+2, n);
			dy += z.getHeight(n)+SectionSpacing;
			if (dy >= paneHeight)
				break;
		}
		GL11.glLineWidth(line);
	}

	private static boolean craftMode() {
		return Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) ^ craftMode;
	}

	private static class Section/* implements Comparable<Section>*/ {

		private final TreeMultimap<ResearchLevel, SectionElement> elements = TreeMultimap.create();
		public final String title;

		private static final int elementWidth = 24;
		private static final int sectionSpacing = 64;
		private static final int margin = 8;
		private static final int spacing = 4;

		public Section(String s) {
			title = s;
		}

		public void addElement(SectionElement e) {
			elements.put(e.research(), e);
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
				sum += this.getSubsectionWidth(cols, elements.get(rl));
				sum += sectionSpacing;
			}
			sum -= sectionSpacing;
			return sum+margin+Minecraft.getMinecraft().fontRenderer.getStringWidth(elements.keySet().last().getDisplayName())-elementWidth-margin;
		}

		public int getSubSectionHeight(int cols, Collection<SectionElement> se) {
			int num = 1+(se.size()-1)/cols;
			return num*elementWidth+(num-1)*spacing+margin-1;
		}

		public int getSubsectionWidth(int cols, Collection<SectionElement> se) {
			int num = se.size() >= cols ? cols : se.size()%cols;
			return num*elementWidth+(num-1)*spacing+margin-1;
		}

		private void drawElements(int x, int y, int cols) {
			int dox = 0;
			for (ResearchLevel rl : elements.keySet()) {
				Collection<SectionElement> se = elements.get(rl);
				int c = 0xffffff;
				int dx2 = x+dox+4;
				int dy2 =  y+4;
				int ddx2 = dx2+this.getSubsectionWidth(cols, se);
				int ddy2 = dy2+this.getSubSectionHeight(cols, se);

				int dx2_ = MathHelper.clamp_int(dx2, leftX+2, leftX+paneWidth+10);
				int dy2_ = MathHelper.clamp_int(dy2, topY-5, topY+paneHeight+5);
				int ddx2_ = MathHelper.clamp_int(ddx2, leftX+2, leftX+paneWidth+10);
				int ddy2_ = MathHelper.clamp_int(ddy2, topY-5, topY+paneHeight+5);
				api.drawLine(dx2_, dy2_, ddx2_, dy2_, c);
				api.drawLine(dx2_, dy2_, dx2_, ddy2_, c);
				api.drawLine(ddx2_, dy2_, ddx2_, ddy2_, c);
				api.drawLine(dx2_, ddy2_, ddx2_, ddy2_, c);
				FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
				if (dx2 >= leftX && dx2 <= leftX+paneWidth-fr.getStringWidth(rl.getDisplayName())) {
					if (dy2 >= topY && dy2 <= topY+paneHeight-fr.FONT_HEIGHT/2) {
						GL11.glDisable(GL11.GL_LIGHTING);
						fr.drawString(rl.getDisplayName(), dx2, y-5, 0xffffff);
						GL11.glEnable(GL11.GL_LIGHTING);
					}
				}
				int i = 0;
				for (SectionElement e : se) {
					int dx = x+margin+(i%cols)*(elementWidth+spacing)+dox;
					int dy = y+margin+(i/cols)*(elementWidth+spacing);
					GL11.glPushMatrix();
					double s = elementWidth/16D;
					GL11.glScaled(s, s, 1);
					int ix = (int)(dx/s);
					int iy = (int)(dy/s);
					if (dx >= leftX && dx <= leftX+paneWidth-elementWidth) {
						if (dy >= topY && dy <= topY+paneHeight-elementWidth) {
							e.draw(ix, iy);
							int mx = dx;
							int mmx = mx+elementWidth;
							int my = dy;
							int mmy = my+elementWidth;
							if (api.isMouseInBox(mx, mmx, my, mmy)) {
								api.drawTooltipAt(fr, e.getName(), (int)(api.getMouseRealX()/s), (int)(api.getMouseRealY()/s));
								int w = (int)(elementWidth/s);
								int mxs = (int)(mx/s);
								int mys = (int)(my/s);
								api.drawRectFrame(mxs, mys, w, w, 0xffffff);
								locations.addRegionByWH(mx, my, elementWidth, elementWidth, e);
							}
						}
					}
					GL11.glPopMatrix();
					i++;
				}
				dox += this.getSubsectionWidth(cols, se)+sectionSpacing;
			}
		}
		/*
		@Override
		public int compareTo(Section o) {
			return elements.get(0).destination.ordinal()-o.elements.get(0).destination.ordinal();
		}*/

	}

	private static class SectionElement implements Comparable<SectionElement> {

		private final ChromaResearch destination;

		private SectionElement(ChromaResearch b) {
			destination = b;
		}

		public void draw(int x, int y) {
			api.drawItemStack(itemRender, this.getIcon(), x, y);
			if (craftMode() && (destination.isCrafting() || destination.isAbility())) {
				GL11.glPushMatrix();
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glTranslated(0, 0, 400);
				ReikaTextureHelper.bindTerrainTexture();
				IIcon ico = ChromaTiles.TABLE.getBlock().getIcon(1, ChromaTiles.TABLE.getBlockMetadata());
				if (destination.isAbility()) {
					ico = ChromaTiles.RITUAL.getBlock().getIcon(1, ChromaTiles.RITUAL.getBlockMetadata());
				}
				api.drawTexturedModelRectFromIcon(x+8, y+8, ico, 9, 9);
				if (destination.isCrafting() && !destination.isCraftable()) {
					GL11.glEnable(GL11.GL_BLEND);
					ico = ChromaIcons.NOENTER.getIcon();
					GL11.glColor4f(1, 1, 1, 0.75F);
					api.drawTexturedModelRectFromIcon(x+8, y+8, ico, 9, 9);
					GL11.glDisable(GL11.GL_BLEND);
				}
				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glPopMatrix();
			}
		}

		public ChromaGuis getGuiType() {
			switch(destination.getParent()) {
			case MACHINEDESC:
				return craftMode() && destination.isCraftable() ? ChromaGuis.RECIPE : ChromaGuis.MACHINEDESC;
			case RESOURCEDESC:
				return ChromaGuis.RESOURCEDESC;
			case TOOLDESC:
				return craftMode() && destination.isCraftable() ? ChromaGuis.RECIPE : ChromaGuis.TOOLDESC;
			case ABILITYDESC:
				return craftMode() ? ChromaGuis.RITUAL : ChromaGuis.ABILITYDESC;
			case INTRO:
				return ChromaGuis.INFO;
			default:
				return null;
			}
		}

		public int getGuiID() {
			return destination.ordinal();
		}

		private ItemStack getIcon() {
			return destination.getTabIcon();
		}

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
