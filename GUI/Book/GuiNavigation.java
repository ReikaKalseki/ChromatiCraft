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

import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ChromaBookGui;
import Reika.ChromatiCraft.Registry.ChromaBook;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;

public class GuiNavigation extends ChromaBookGui {

	private int offsetX = 0;
	private int offsetY = 0;

	//private int mouseX;
	//private int mouseY;

	private int maxX = 500;
	private int maxY = 250;

	private int paneWidth = 240;
	private int paneHeight = 180;

	private final TreeMap<ChromaBook, Zone> zones = new TreeMap();
	private static final int zoneSpacing = 32;

	public GuiNavigation(EntityPlayer ep) {
		super(ep, 256, 220);

		Zone z = null;
		for (int i = 0; i < ChromaBook.tabList.length; i++) {
			ChromaBook b = ChromaBook.tabList[i];
			if (b.isParent()) {
				z = new Zone(b.getTitle());
				zones.put(b, z);
			}
			else {
				z.addElement(new ZoneElement(b));
			}
		}
	}

	@Override
	public void initGui() {
		super.initGui();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		//add tabs or whatever
	}

	@Override
	public void actionPerformed(GuiButton button) {
		super.actionPerformed(button);
		if (buttontimer > 0)
			return;
		//Do things
	}

	@Override
	public String getBackgroundTexture() {
		return "";
	}

	@Override
	public void drawScreen(int x, int y, float f) {
		super.drawScreen(x, y, f);

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		int sp = 1;
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			sp = 2;
		}
		/*
		if (Mouse.isButtonDown(0)) {
			offsetX += mouseX-x;
			offsetY += mouseY-y;
		}
		else */if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			offsetY -= sp;
		}
		else if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			offsetX -= sp;
		}
		else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			offsetY += sp;
		}
		else if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
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

		//mouseX = x;
		//mouseY = y;

		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/GUIs/Handbook/navbcg.png");
		int u = offsetX%256;
		int v = offsetY%256;
		this.drawTexturedModalRect(j+8, k+24, u, v, paneWidth, paneHeight);

		this.drawZones(j+12-offsetX, k+32-offsetY);
	}

	private void drawZones(int x, int y) {
		float line = GL11.glGetFloat(GL11.GL_LINE_WIDTH);
		GL11.glLineWidth(2);
		int dy = y+1;
		for (Zone z : zones.values()) {
			int dx = x+4;
			int n = 7;
			int c = 0xffffff;
			int ddx = dx+z.getWidth(n);
			int ddy = dy+z.getHeight(n);
			api.drawLine(dx, dy, ddx, dy, c);
			api.drawLine(dx, dy, dx, ddy, c);
			api.drawLine(ddx, dy, ddx, ddy, c);
			api.drawLine(dx, ddy, ddx, ddy, c);
			fontRendererObj.drawString(z.title, dx+2, dy-fontRendererObj.FONT_HEIGHT, 0xffffff);
			z.drawElements(dx, dy, n);
			dy += z.getHeight(n)+zoneSpacing;
		}
		GL11.glLineWidth(line);
	}

	private static class Zone implements Comparable<Zone> {

		private final ArrayList<ZoneElement> elements = new ArrayList();
		public final String title;

		private static final int elementWidth = 24;
		private static final int margin = 8;
		private static final int spacing = 4;

		public Zone(String s) {
			title = s;
		}

		public void addElement(ZoneElement e) {
			elements.add(e);
			Collections.sort(elements);
		}

		public int getHeight(int cols) {
			int num = 1+elements.size()/cols;
			return num*elementWidth+(num-1)*spacing+margin*2;
		}

		public int getWidth(int cols) {
			int num = elements.size() >= cols ? cols : elements.size()%cols;
			return num*elementWidth+(num-1)*spacing+margin*2;
		}

		private void drawElements(int x, int y, int cols) {
			int i = 0;
			for (ZoneElement e : elements) {
				int dx = x+margin+(i%cols)*(elementWidth+spacing);
				int dy = y+margin+(i/cols)*(elementWidth+spacing);
				GL11.glPushMatrix();
				double s = elementWidth/16D;
				GL11.glScaled(s, s, 1);
				api.drawItemStack(itemRender, e.destination.getTabIcon(), (int)(dx/s), (int)(dy/s));
				int mx = dx-1;
				int mmx = mx+elementWidth;
				int my = dy;
				int mmy = my+elementWidth;
				if (api.isMouseInBox(mx, mmx, my, mmy)) {
					FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
					api.drawTooltipAt(fr, e.destination.getTitle(), (int)(api.getMouseRealX()/s), (int)(api.getMouseRealY()/s));
					int w = (int)(elementWidth/s);
					int ox = i%cols%3 > 0 ? 1 : 0;
					api.drawRectFrame((int)(mx/s)+ox, (int)(my/s), w, w, 0xffffff);
				}
				GL11.glPopMatrix();
				i++;
			}
		}

		@Override
		public int compareTo(Zone o) {
			return elements.get(0).destination.ordinal()-o.elements.get(0).destination.ordinal();
		}

	}

	private static class ZoneElement implements Comparable<ZoneElement> {

		public final ChromaBook destination;

		private ZoneElement(ChromaBook b) {
			destination = b;
		}

		@Override
		public int compareTo(ZoneElement o) {
			return (destination.getParent().ordinal()-o.destination.getParent().ordinal())*10000+destination.ordinal()-o.destination.ordinal();
		}

	}

}
