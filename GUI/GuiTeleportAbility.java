/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI;

import java.util.ArrayList;
import java.util.Collections;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Ability.AbilityHelper;
import Reika.ChromatiCraft.Auxiliary.Ability.WarpPoint;
import Reika.ChromatiCraft.Auxiliary.Ability.WarpPointData;
import Reika.ChromatiCraft.Auxiliary.Render.ChromaFontRenderer;
import Reika.ChromatiCraft.Base.GuiChromaTool;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Data.Maps.RectangleMap;
import Reika.DragonAPI.Instantiable.GUI.CustomSoundGuiButton;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Rendering.ReikaGuiAPI;

public class GuiTeleportAbility extends GuiChromaTool {

	private final int xSize = 176;
	private final int ySize = 220;

	private final ArrayList<WarpPoint> points = new ArrayList();

	private static final int MAX_LINES = 14;
	private int listOffset = 0;

	private Screen screen = Screen.SELECT;
	private GuiTextField newLabel;
	private int selection = -1;
	private RectangleMap<WarpPoint> locations = new RectangleMap();

	private int pageAge = 0;

	public GuiTeleportAbility(EntityPlayer ep) {
		super(ep);
	}

	@Override
	public void initGui() {
		super.initGui();
		pageAge = 0;
		listOffset = 0;
		points.clear();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		String tex = "Textures/GUIs/buttons.png";

		if (screen == Screen.SET) {
			buttonList.add(new CustomSoundGuiButton(0, j+28, k+ySize-28, 120, 20, ChromaFontRenderer.FontType.GUI.id+"   Add Point", this));
			//buttonList.add(new CustomSoundGuiButton(3, j+88, k+ySize-28, 60, 20, ChromaFontRenderer.FontType.GUI.id+"   Minimap", this));
		}
		else if (screen == Screen.SELECT || screen == Screen.MINIMAP) {
			buttonList.add(new CustomSoundGuiButton(1, j+28, k+ySize-28, screen == Screen.SELECT ? 60 : 120, 20, ChromaFontRenderer.FontType.GUI.id+"   Go To", this));
			if (screen == Screen.SELECT)
				buttonList.add(new CustomSoundGuiButton(2, j+88, k+ySize-28, 60, 20, ChromaFontRenderer.FontType.GUI.id+"   Remove", this));
			buttonList.add(new CustomSoundGuiButton(4, j+4, k+4, 10, 10, ChromaFontRenderer.FontType.GUI.id+"   -", this));
			buttonList.add(new CustomSoundGuiButton(5, j+14, k+4, 10, 10, ChromaFontRenderer.FontType.GUI.id+"   +", this));
		}

		buttonList.add(new CustomSoundGuiButton(10, j+8, k+ySize-28, 20, 20, ChromaFontRenderer.FontType.GUI.id+"   <", this));
		buttonList.add(new CustomSoundGuiButton(11, j+148, k+ySize-28, 20, 20, ChromaFontRenderer.FontType.GUI.id+"   >", this));

		if (screen == Screen.SET) {
			newLabel = new GuiTextField(ChromaFontRenderer.FontType.GUI.renderer, j+8, k+30, 160, 16);
			newLabel.setFocused(false);
			newLabel.setMaxStringLength(24);
		}
		if (screen == Screen.SELECT) {
			points.addAll(AbilityHelper.instance.getTeleportLocations(player));
			Collections.sort(points);
		}
		else if (screen == Screen.MINIMAP) {
			points.addAll(WarpPointData.loadMiniMaps());
			Collections.sort(points);
		}
	}

	@Override
	protected void keyTyped(char c, int i) {
		super.keyTyped(c, i);
		if (screen == Screen.SET)
			newLabel.textboxKeyTyped(c, i);
	}

	@Override
	protected void mouseClicked(int x, int y, int b) {
		super.mouseClicked(x, y, b);
		if (screen == Screen.SET)
			newLabel.mouseClicked(x, y, b);

		if (screen == Screen.SELECT || screen == Screen.MINIMAP) {
			WarpPoint p = locations.getItemAt(x, y);
			if (p != null) {
				selection = points.indexOf(p);
			}
			else {
				selection = -1;
			}
		}
	}

	@Override
	public void updateScreen() {
		super.updateScreen();

	}

	@Override
	protected void actionPerformed(GuiButton b) {
		if (b.id >= 10 && pageAge > 6) {
			if (b.id == 10) {
				screen = screen.prev();
			}
			else if (b.id == 11) {
				screen = screen.next();
			}
		}
		else if (b.id == 0 && this.isValidPoint()) {
			AbilityHelper.instance.addWarpPoint(newLabel.getText(), player);
			ReikaPacketHelper.sendStringPacket(ChromatiCraft.packetChannel, ChromaPackets.NEWTELEPORT.ordinal(), newLabel.getText());
		}
		else if (b.id == 1 && this.getCurrentSelected() != null) {
			/*
			if (screen == Screen.MINIMAP)
				AbilityHelper.instance.gotoWarpPoint(this.getCurrentSelected(), player);
			else
				AbilityHelper.instance.gotoWarpPoint(this.getCurrentSelected().label, player);
			 */
			if (screen == Screen.MINIMAP) {
				WarpPoint p = this.getCurrentSelected();
				ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.MAPTELEPORT.ordinal(), PacketTarget.server, p.location.dimensionID, p.location.xCoord, p.location.yCoord, p.location.zCoord);
			}
			else
				ReikaPacketHelper.sendStringPacket(ChromatiCraft.packetChannel, ChromaPackets.TELEPORT.ordinal(), this.getCurrentSelected().label);
			player.closeScreen();
		}
		else if (b.id == 2 && this.getCurrentSelected() != null) {
			AbilityHelper.instance.removeWarpPoint(this.getCurrentSelected().label, player);
			ReikaPacketHelper.sendStringPacket(ChromatiCraft.packetChannel, ChromaPackets.DELTELEPORT.ordinal(), this.getCurrentSelected().label);
		}/*
		else if (b.id == 3) {
			AbilityHelper.instance.copyVoxelMapWaypoints();
		}*/
		else if (b.id == 4) {
			listOffset = Math.max(0, listOffset-(GuiScreen.isShiftKeyDown() ? 10 : 1));
			return;
		}
		else if (b.id == 5) {
			listOffset = Math.min(this.maxListOffset(), listOffset+(GuiScreen.isShiftKeyDown() ? 10 : 1));
			return;
		}
		this.initGui();
	}

	private boolean isValidPoint() {
		return !newLabel.getText().isEmpty() && !AbilityHelper.instance.playerCanWarpTo(player, new WorldLocation(player));
	}

	private WarpPoint getCurrentSelected() {
		return points.isEmpty() || selection < 0 ? null : points.get(selection);
	}

	@Override
	public void drawScreen(int x, int y, float ptick) {
		pageAge++;

		GL11.glColor4f(1, 1, 1, 1);
		locations.clear();

		GL11.glDisable(GL11.GL_DEPTH_TEST);
		FontRenderer fr = ChromaFontRenderer.FontType.GUI.renderer;

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/GUIs/teleports.png");
		ReikaGuiAPI.instance.drawTexturedModalRect(j, k, 0, 0, xSize, ySize);
		int tx = j+xSize/2;
		int ty = k+4;
		String s = screen.title;
		ReikaGuiAPI.instance.drawCenteredStringNoShadow(fr, s, tx, ty, 0xffffff);

		if (screen == Screen.SELECT || screen == Screen.MINIMAP) {
			int h = 12;
			int sz = Math.min(MAX_LINES, points.size()-listOffset);
			for (int i = 0; i < sz; i++) {
				WarpPoint p = points.get(i+listOffset);
				int dx = j+8;
				int w = xSize-16;
				int dy = k+18+i*h;
				locations.addItem(p, dx, dy, w, h);
				fr.drawString(p.toString(), dx, dy, (i+listOffset) == selection ? 0x00ff00 : 0xffffff);
			}
		}

		if (screen == Screen.SET) {
			newLabel.drawTextBox();

			ReikaGuiAPI.instance.drawCenteredStringNoShadow(fr, newLabel.getText(), tx, k+60, 0xffffff);
			ReikaGuiAPI.instance.drawCenteredStringNoShadow(fr, "["+new WorldLocation(player).toString()+"]", tx, k+60+fr.FONT_HEIGHT+2, 0xffffff);
		}
		GL11.glEnable(GL11.GL_DEPTH_TEST);

		GL11.glPushMatrix();
		GL11.glTranslated(0, 0, 1000);
		super.drawScreen(x, y, ptick);
		GL11.glPopMatrix();
	}

	private int maxListOffset() {
		return Math.max(0, points.size()-MAX_LINES);
	}

	private static enum Screen {
		SELECT("Warp Locations"),
		MINIMAP("Minimap Waypoints"),
		SET("Add Location");

		private final String title;

		private static final Screen[] list = values();

		private Screen(String s) {
			title = s;
		}

		public Screen prev() {
			return this.ordinal() == 0 ? this : list[this.ordinal()-1];
		}

		public Screen next() {
			return this.ordinal() == list.length-1 ? this : list[this.ordinal()+1];
		}
	}

}
