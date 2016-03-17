/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI;

import java.util.ArrayList;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.AbilityHelper;
import Reika.ChromatiCraft.Auxiliary.AbilityHelper.WarpPoint;
import Reika.ChromatiCraft.Auxiliary.ChromaFontRenderer;
import Reika.ChromatiCraft.Auxiliary.CustomSoundGuiButton;
import Reika.ChromatiCraft.Auxiliary.CustomSoundGuiButton.CustomSoundGui;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Data.Maps.RectangleMap;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;

public class GuiTeleportAbility extends GuiScreen implements CustomSoundGui {

	private final int xSize = 176;
	private final int ySize = 220;

	private final EntityPlayer player;

	private final ArrayList<WarpPoint> points = new ArrayList();

	private Screen screen = Screen.SELECT;
	private GuiTextField newLabel;
	private int selection = -1;
	private RectangleMap<WarpPoint> locations = new RectangleMap();

	public GuiTeleportAbility(EntityPlayer ep) {
		player = ep;
	}

	public void playButtonSound(GuiButton b) {
		ReikaSoundHelper.playClientSound(ChromaSounds.GUICLICK, player, 0.5F, 1);
	}

	public void playHoverSound(GuiButton b) {
		ReikaSoundHelper.playClientSound(ChromaSounds.GUISEL, player, 0.8F, 1);
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.clear();
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		String tex = "Textures/GUIs/buttons.png";

		if (screen == Screen.SET) {
			buttonList.add(new CustomSoundGuiButton(0, j+28, k+ySize-28, 120, 20, ChromaFontRenderer.FontType.GUI.id+"   Add Point", this));
		}
		else if (screen == Screen.SELECT) {
			buttonList.add(new CustomSoundGuiButton(1, j+28, k+ySize-28, 60, 20, ChromaFontRenderer.FontType.GUI.id+"   Go To", this));
			buttonList.add(new CustomSoundGuiButton(2, j+88, k+ySize-28, 60, 20, ChromaFontRenderer.FontType.GUI.id+"   Remove", this));
		}

		buttonList.add(new CustomSoundGuiButton(10, j+8, k+ySize-28, 20, 20, ChromaFontRenderer.FontType.GUI.id+"   <", this));
		buttonList.add(new CustomSoundGuiButton(11, j+148, k+ySize-28, 20, 20, ChromaFontRenderer.FontType.GUI.id+"   >", this));

		if (screen == Screen.SET) {
			newLabel = new GuiTextField(ChromaFontRenderer.FontType.GUI.renderer, j+8, k+30, 160, 16);
			newLabel.setFocused(false);
			newLabel.setMaxStringLength(24);
		}
		points.clear();
		points.addAll(AbilityHelper.instance.getTeleportLocations(player));
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

		if (screen == Screen.SELECT) {
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
		if (b.id >= 10) {
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
			AbilityHelper.instance.gotoWarpPoint(this.getCurrentSelected().label, player);
			ReikaPacketHelper.sendStringPacket(ChromatiCraft.packetChannel, ChromaPackets.TELEPORT.ordinal(), this.getCurrentSelected().label);
			player.closeScreen();
		}
		else if (b.id == 2 && this.getCurrentSelected() != null) {
			AbilityHelper.instance.removeWarpPoint(this.getCurrentSelected().label, player);
			ReikaPacketHelper.sendStringPacket(ChromatiCraft.packetChannel, ChromaPackets.DELTELEPORT.ordinal(), this.getCurrentSelected().label);
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

		GL11.glColor4f(1, 1, 1, 1);
		locations.clear();

		FontRenderer fr = ChromaFontRenderer.FontType.GUI.renderer;

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/GUIs/teleports.png");
		ReikaGuiAPI.instance.drawTexturedModalRect(j, k, 0, 0, xSize, ySize);
		int tx = j+xSize/2;
		int ty = k+4;
		String s = screen.title;
		ReikaGuiAPI.instance.drawCenteredStringNoShadow(fr, s, tx, ty, 0xffffff);

		if (screen == Screen.SELECT) {
			int h = 12;
			for (int i = 0; i < points.size(); i++) {
				WarpPoint p = points.get(i);
				int dx = j+8;
				int w = 40;
				int dy = k+18+i*h;
				locations.addItem(p, dx, dy, w, h);
				fr.drawString(p.toString(), dx, dy, i == selection ? 0x00ff00 : 0xffffff);
			}
		}

		if (screen == Screen.SET) {
			newLabel.drawTextBox();

			ReikaGuiAPI.instance.drawCenteredStringNoShadow(fr, newLabel.getText(), tx, k+60, 0xffffff);
			ReikaGuiAPI.instance.drawCenteredStringNoShadow(fr, "["+new WorldLocation(player).toString()+"]", tx, k+60+fr.FONT_HEIGHT+2, 0xffffff);
		}

		GL11.glPushMatrix();
		GL11.glTranslated(0, 0, 1000);
		super.drawScreen(x, y, ptick);
		GL11.glPopMatrix();
	}

	private static enum Screen {
		SELECT("Warp Locations"),
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
