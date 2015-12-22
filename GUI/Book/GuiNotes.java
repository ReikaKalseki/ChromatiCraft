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

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import org.lwjgl.input.Keyboard;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaFontRenderer;
import Reika.ChromatiCraft.Auxiliary.CustomSoundGuiButton;
import Reika.ChromatiCraft.Auxiliary.CustomSoundGuiButton.CustomSoundImagedGuiButton;
import Reika.ChromatiCraft.Base.ChromaBookGui;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;

public class GuiNotes extends ChromaBookGui {

	public static final int LINES = 10;
	public static final int TEXTCOLOR = 0x202020;

	private int scroll = 0;
	private boolean lineAppend = false;

	private final ArrayList<String> data = new ArrayList();

	private int activeIndex = -1;
	private GuiTextField[] input = new GuiTextField[LINES];

	public GuiNotes(EntityPlayer ep) {
		super(ChromaGuis.NOTES, ep, 256, 220);

		ItemStack held = ep.getHeldItem();
		//ReikaJavaLibrary.pConsole(held.stackTagCompound);
		if (held.stackTagCompound != null && held.stackTagCompound.hasKey("notes")) {
			NBTTagList tag = held.stackTagCompound.getTagList("notes", NBTTypes.STRING.ID);
			//ReikaJavaLibrary.pConsole("Loaded "+tag);
			for (Object o : tag.tagList) {
				data.add(((NBTTagString)o).func_150285_a_());
			}
		}
		/*
		data.add("This is a test line 1");
		data.add("This is a test line 2, of an extremely long and overly wordy string that will not fit in the gui.");
		data.add("Containing special characters. \n new line?");
		data.add("This is a test line 4");
		data.add("This is a test line 5");
		data.add("This is a test line 6");
		data.add("This is a test line 7");
		data.add("This is a test line 8");
		data.add("This is a test line 9");
		data.add("This is a test line 10");
		data.add("This is a test line 11");
		data.add("This is a test line 12");
		data.add("This is a test line 13");
		data.add("This is a test line 14");
		data.add("This is a test line 15");
		data.add("This is a test line 16");
		 */
	}

	@Override
	public void onGuiClosed() {
		ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.BOOKNOTESRESET.ordinal(), new PacketTarget.ServerTarget());
		for (String s : data) {
			ReikaPacketHelper.sendStringPacket(ChromatiCraft.packetChannel, ChromaPackets.BOOKNOTE.ordinal(), s);
			//ReikaJavaLibrary.pConsole("Sending "+s+" of "+data);
		}
	}

	@Override
	public void initGui() {
		super.initGui();

		int j = (width-xSize) / 2;
		int k = (height-ySize) / 2;

		String file = "Textures/GUIs/Handbook/buttons.png";

		this.addAuxButton(new CustomSoundImagedGuiButton(10, j+xSize, k, 22, 39, 42, 126, file, ChromatiCraft.class, this), "Return");

		buttonList.add(new CustomSoundGuiButton(1, j-20, k-5, 20, 20, "-", this));
		buttonList.add(new CustomSoundGuiButton(0, j-20, k+15, 20, 20, "+", this));
		buttonList.add(new CustomSoundGuiButton(2, j-20, k+35, 20, 20, "*", this));

		for (int i = 0; i < LINES; i++) {
			input[i] = new NoteTextField(ChromaFontRenderer.FontType.LEXICON.renderer, j+8, k+3+i*20, 240, 19);
			input[i].setFocused(false);
			input[i].setMaxStringLength(9999);
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		super.actionPerformed(button);
		if (button.id == 10) {
			this.saveCurrentLine();
			this.goTo(ChromaGuis.BOOKNAV, null);
		}
		else if (button.id == 0) {
			if (scroll < data.size()-LINES)
				this.scrollDown();
		}
		else if (button.id == 1) {
			if (scroll > 0)
				this.scrollUp();
		}
		else if (button.id == 2) {
			lineAppend = true;
			data.add("-Add Notes-");
			this.saveCurrentLine();
			activeIndex = -1;
			while (scroll < data.size()-LINES)
				this.scrollDown();
			//input[LINES-1].setFocused(true);
			//activeIndex = data.size()-1;
			return;
		}
		this.initGui();
	}

	private void scrollDown() {
		this.saveCurrentLine();
		this.unfocusText();
		scroll++;
	}

	private void scrollUp() {
		this.saveCurrentLine();
		this.unfocusText();
		scroll--;
	}

	private void unfocusText() {
		activeIndex = -1;
		for (int i = 0; i < LINES; i++) {
			input[i].setFocused(false);
		}
	}

	private void saveCurrentLine() {
		if (activeIndex != -1) {
			data.set(activeIndex, input[activeIndex-scroll].getText());
		}
	}

	@Override
	public void drawScreen(int x, int y, float f) {
		int j = (width-xSize) / 2;
		int k = (height-ySize) / 2;

		super.drawScreen(x, y, f);

		int max = Math.min(scroll+data.size(), scroll+LINES);
		for (int i = scroll; i < max; i++) {
			if (i == activeIndex) {

			}
			else {
				String s = data.get(i);
				String sg = s.length() > 45 ? s.substring(0, 45)+"..." : s;
				int dy = k+8+20*(i-scroll);
				fontRendererObj.drawString(sg, j+12, dy, TEXTCOLOR);
			}
		}

		if (activeIndex != -1)
			input[activeIndex-scroll].drawTextBox();
	}

	@Override
	protected void keyTyped(char c, int k) {
		if (k == Keyboard.KEY_ESCAPE)
			this.saveCurrentLine();
		super.keyTyped(c, k);
		for (int i = 0; i < LINES; i++)
			input[i].textboxKeyTyped(c, k);
	}

	@Override
	protected void mouseClicked(int x, int y, int b) {
		super.mouseClicked(x, y, b);

		int idx = this.getTextBoxFromClickPos(x, y);
		//ReikaJavaLibrary.pConsole(activeIndex+">"+idx);
		if (idx != activeIndex && !lineAppend) {
			this.saveCurrentLine();
		}
		if (idx != -1) {
			this.createAndEditTextBoxForLine(idx, x, y, b);
		}
		else {
			activeIndex = -1;
		}
		if (lineAppend) {
			this.createAndEditTextBoxForLine(data.size()-1-scroll, x, y, b);
			input[activeIndex-scroll].setFocused(true);
			input[activeIndex-scroll].setText(data.get(activeIndex));
		}
		lineAppend = false;
	}

	private void createAndEditTextBoxForLine(int idx, int x, int y, int b) {
		activeIndex = idx+scroll;
		input[activeIndex-scroll].mouseClicked(x, y, b);
		if (input[activeIndex-scroll].isFocused()) {
			input[activeIndex-scroll].setText(data.get(activeIndex));
		}
	}

	private int getTextBoxFromClickPos(int x, int y) {
		int j = (width-xSize) / 2;
		int k = (height-ySize) / 2;
		int dx = x-j-7;
		int dy = y-k-3;
		if (dx < 1 || dx > 240)
			return -1;
		if (dy < 0 || dy > 197)
			return -1;
		int h = dy/20;
		int v = dy-h*20;
		if (v < 1 || v > 16)
			return -1;
		if (h >= data.size())
			return -1;
		return h;
	}

	@Override
	public String getBackgroundTexture() {
		return "Textures/GUIs/Handbook/notes.png";
	}

	private static class NoteTextField extends GuiTextField {

		private final FontRenderer renderer;

		public NoteTextField(FontRenderer fr, int x, int y, int w, int h) {
			super(fr, x, y, w, h);
			//this.setEnableBackgroundDrawing(false);
			this.setTextColor(TEXTCOLOR);
			renderer = fr;
		}

		@Override
		public void drawTextBox()
		{
			int i = TEXTCOLOR;
			int j = this.getCursorPosition()-lineScrollOffset;
			int k = this.getSelectionEnd()-lineScrollOffset;
			String text = this.getText();
			String s = renderer.trimStringToWidth(text.substring(lineScrollOffset), this.getWidth());
			boolean flag = j >= 0 && j <= s.length();
			boolean flag1 = this.isFocused() && cursorCounter / 6 % 2 == 0 && flag;
			int l = xPosition+4;
			int i1 = yPosition+(height-8) / 2;
			int j1 = l;

			if (k > s.length())
				k = s.length();

			if (s.length() > 0) {
				String s1 = flag ? s.substring(0, j) : s;
				j1 = renderer.drawString(s1, l, i1, i);
			}

			boolean flag2 = this.getCursorPosition() < text.length() || text.length() >= this.getMaxStringLength();
			int k1 = j1;

			if (!flag) {
				k1 = j > 0 ? l+width : l;
			}
			else if (flag2) {
				k1 = j1-1;
				j1--;
			}

			if (s.length() > 0 && flag && j < s.length()) {
				renderer.drawString(s.substring(j), j1, i1, i);
			}

			if (flag1) {
				if (flag2)
					Gui.drawRect(k1, i1-1, k1+1, i1+1+renderer.FONT_HEIGHT, -3092272);
				else
					renderer.drawString("_", k1, i1, i);
			}

			if (k != j) {
				int l1 = l+renderer.getStringWidth(s.substring(0, k));
				this.drawCursorVertical(k1, i1-1, l1-1, i1+1+renderer.FONT_HEIGHT);
			}
		}
	}

}
