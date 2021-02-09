/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI.Tile;

import java.util.HashMap;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.CrystalMusicManager;
import Reika.ChromatiCraft.Base.GuiChromaBase;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Decoration.TileEntityCrystalMusic;
import Reika.DragonAPI.Base.CoreContainer;
import Reika.DragonAPI.Instantiable.GUI.CustomSoundGuiButton.CustomSoundImagedGuiButton;
import Reika.DragonAPI.Instantiable.GUI.PianoWheel;
import Reika.DragonAPI.Instantiable.GUI.PianoWheel.PianoGui;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMusicHelper.MusicKey;
import Reika.DragonAPI.Libraries.MathSci.ReikaMusicHelper.Note;
import Reika.DragonAPI.Libraries.Rendering.ReikaGuiAPI;

public class GuiCrystalMusic extends GuiChromaBase implements PianoGui {

	private final TileEntityCrystalMusic music;

	private PianoWheel wheel;

	private int channel = 0;
	private boolean rest = false;
	private int activeNote;
	private int length;

	private Pages page = Pages.KEYS;

	private GuiTextField input;
	private GuiTextField ticksPer;

	private int midiTickRate;
	private int midiBPM;

	private static final HashMap<Note, Integer> colors = new HashMap();

	static {
		colors.put(Note.C, 		CrystalElement.BLACK.getColor());
		colors.put(Note.CSHARP, CrystalElement.RED.getColor());
		colors.put(Note.D, 		CrystalElement.ORANGE.getColor());
		colors.put(Note.EFLAT, 	CrystalElement.CYAN.getColor());
		colors.put(Note.E, 		CrystalElement.YELLOW.getColor());
		colors.put(Note.F, 		CrystalElement.LIME.getColor());
		colors.put(Note.FSHARP, CrystalElement.GREEN.getColor());
		colors.put(Note.G, 		CrystalElement.BLUE.getColor());
		colors.put(Note.GSHARP, CrystalElement.MAGENTA.getColor());
		colors.put(Note.A, 		CrystalElement.PURPLE.getColor());
		colors.put(Note.BFLAT, 	CrystalElement.LIGHTBLUE.getColor());
		colors.put(Note.B, 		CrystalElement.PINK.getColor());
	}

	public GuiCrystalMusic(EntityPlayer ep, TileEntityCrystalMusic te) {
		super(new CoreContainer(ep, te), ep, te);
		music = te;
		xSize = 240;
		ySize = 190;

		activeNote = 3; //qtr
		length = this.getNoteLength(activeNote);
	}

	@Override
	public void initGui() {
		super.initGui();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		int r = 72;
		wheel = new PianoWheel(this, MusicKey.C3, 4, r, j+xSize/2, k+ySize/2+4, false);

		String file = "Textures/GUIs/musicoverlay.png";

		buttonList.add(new CustomSoundImagedGuiButton(4, j+4, k+4, 19, 20, page == Pages.MIDI ? 209 : 228, 116, file, ChromatiCraft.class, this));

		if (page == Pages.KEYS) {
			buttonList.add(new CustomSoundImagedGuiButton(0, j+4, k+4+20, 19, 20, 228, 136, file, ChromatiCraft.class, this));
			buttonList.add(new CustomSoundImagedGuiButton(1, j+4, k+4+40, 19, 20, 228, 156, file, ChromatiCraft.class, this));
			buttonList.add(new CustomSoundImagedGuiButton(2, j+4, k+4+100, 28, 20, 228, 196, file, ChromatiCraft.class, this));
			buttonList.add(new CustomSoundImagedGuiButton(3, j+4, k+4+80, 21, 20, 228, 176, file, ChromatiCraft.class, this));
			buttonList.add(new CustomSoundImagedGuiButton(6, j+4, k+4+60, 19, 20, 209, 156, file, ChromatiCraft.class, this));
		}
		else {
			buttonList.add(new CustomSoundImagedGuiButton(5, j+4, k+4+20, 19, 20, 209, 136, file, ChromatiCraft.class, this));
		}

		if (page == Pages.KEYS) {
			for (int i = 0; i < 6; i++) {
				int col = i%3;
				int row = i/3;
				int u = 60+col*20;
				int v = 216+row*20;

				if (rest) {
					u += 120;
				}
				if (i == activeNote) {
					u -= 60;
				}

				buttonList.add(new CustomSoundImagedGuiButton(50+i, j+48+4+r*2+8, k+ySize/2+(i-3)*20, 20, 20, u, v, file, ChromatiCraft.class, this));
			}
		}

		if (page == Pages.KEYS) {
			int dx = (xSize-16*12)/2;
			for (int i = 0; i < 16; i++) {
				int u = 0+i*12;
				int v = i == channel ? 204 : 192;
				buttonList.add(new CustomSoundImagedGuiButton(16+i, j+dx+i*12, k+ySize-12-4, 12, 12, u, v, file, ChromatiCraft.class, this));
			}
		}

		if (page == Pages.MIDI) {
			input = new GuiTextField(fontRendererObj, j+8, k+82, xSize-16, 16);
			input.setMaxStringLength(128);
			input.setFocused(false);
			input.setText("");

			ticksPer = new GuiTextField(fontRendererObj, j+xSize/2+7, k+126, fontRendererObj.getStringWidth("60")*2, 16);
			ticksPer.setMaxStringLength(2);
			ticksPer.setFocused(false);
			ticksPer.setText("9");
		}
	}

	@Override
	protected void actionPerformed(GuiButton b) {
		if (b.id == 0) {
			ReikaPacketHelper.sendPacketToServer(ChromatiCraft.packetChannel, ChromaPackets.MUSICCLEAR.ordinal(), music);
		}
		else if (b.id == 1) {
			ReikaPacketHelper.sendPacketToServer(ChromatiCraft.packetChannel, ChromaPackets.MUSICCLEARCHANNEL.ordinal(), music, channel);
		}
		else if (b.id == 2) {
			rest = !rest;
		}
		else if (b.id == 3) {
			music.dispatchDemo(); //in case serverside does not have functioning MIDI environment
			ReikaPacketHelper.sendPacketToServer(ChromatiCraft.packetChannel, ChromaPackets.MUSICDEMO.ordinal(), music);
		}
		else if (b.id == 4) {
			page = page.otherPage();
		}
		else if (b.id == 5) {
			if (music.loadLocalMIDI(input.getText(), midiTickRate)) {
				ReikaSoundHelper.playClientSound(ChromaSounds.CAST, player, 1, 1);
			}
			else {
				ReikaSoundHelper.playClientSound(ChromaSounds.ERROR, player, 1, 1);
			}
			return; //prevent reinit and textbox clear
		}
		else if (b.id == 6) {
			ReikaPacketHelper.sendPacketToServer(ChromatiCraft.packetChannel, ChromaPackets.MUSICBKSP.ordinal(), music, channel);
		}
		else if (b.id >= 16 && b.id < 32) {
			channel = b.id-16;
		}
		else if (b.id >= 50 && b.id < 56) {
			activeNote = b.id-50;
			length = this.getNoteLength(activeNote);
		}
		this.initGui();
	}

	private int getNoteLength(int idx) {
		switch(idx) {
			case 0:
				return 48;
			case 1:
				return 36;
			case 2:
				return 24;
			case 3:
				return 12;
			case 4:
				return 6;
			case 5:
				return 3;
			default:
				return 0;
		}
	}

	@Override
	protected void mouseClicked(int x, int y, int b) {
		super.mouseClicked(x, y, b);

		if (page == Pages.KEYS)
			wheel.mouseClicked(b, x, y);
		else if (page == Pages.MIDI) {
			input.mouseClicked(x, y, b);
			ticksPer.mouseClicked(x, y, b);
		}
	}

	@Override
	protected void keyTyped(char c, int i){
		if (i != mc.gameSettings.keyBindInventory.getKeyCode() && (input == null || !input.isFocused()))
			super.keyTyped(c, i);

		if (page == Pages.MIDI) {
			input.textboxKeyTyped(c, i);
			ticksPer.textboxKeyTyped(c, i);
		}
	}

	@Override
	public void updateScreen() {
		super.updateScreen();

		if (page == Pages.MIDI && ticksPer != null) {
			midiTickRate = ReikaJavaLibrary.safeIntParse(ticksPer.getText());
			midiTickRate = MathHelper.clamp_int(midiTickRate, 4, 60);
			midiBPM = 120*9/midiTickRate; //120 @ 9
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		super.drawGuiContainerForegroundLayer(par1, par2);
		//fontRendererObj = Minecraft.getMinecraft().fontRenderer;
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;


	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float p, int a, int b) {
		super.drawGuiContainerBackgroundLayer(p, a, b);
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		if (page == Pages.KEYS) {
			wheel.draw(false, false, false);
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glEnable(GL11.GL_BLEND);
			BlendMode.DEFAULT.apply();
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/GUIs/musicoverlay.png");
			this.drawTexturedModalRect(wheel.originX-wheel.radius-16, wheel.originY-wheel.radius-16, 0, 0, 176, 176);
			GL11.glPopAttrib();
		}
		else if (page == Pages.MIDI) {
			input.drawTextBox();
			ticksPer.drawTextBox();

			if (!input.isFocused()) {
				int d = input.getCursorPosition();
				//fontRendererObj.drawStringWithShadow(file.substring(d, Math.min(file.length(), 37+d)), posX+10, posY+ySize-15, 0xaaaaaa);
			}
			ReikaGuiAPI.instance.drawCenteredStringNoShadow(fontRendererObj, "Select a MIDI file. Be sure to include the drive", j+xSize/2+1, k+60, 0xffffff);
			ReikaGuiAPI.instance.drawCenteredStringNoShadow(fontRendererObj, "letter and file extension and use \"/\", not \"\\\".", j+xSize/2+1, k+70, 0xffffff);

			ReikaGuiAPI.instance.drawCenteredStringNoShadow(fontRendererObj, "MC Ticks Per Beat: ", j+xSize/2+1-43, k+130, 0xffffff);
			fontRendererObj.drawString("("+midiBPM+" bpm)", j+xSize/2+1+37, k+130, 0xffffff);
		}
	}

	@Override
	public String getGuiTexture() {
		return "music";
	}

	@Override
	public void onKeyPressed(MusicKey key) {
		ReikaSoundHelper.playClientSound(ChromaSounds.DING, player, 1, (float)CrystalMusicManager.instance.getPitchFactor(key));
		for (CrystalElement e : CrystalMusicManager.instance.getColorsWithKey(key)) {
			music.playCrystal(music.worldObj, music.xCoord, music.yCoord, music.zCoord, e, length, key);
		}
		ReikaPacketHelper.sendPacketToServer(ChromatiCraft.packetChannel, ChromaPackets.MUSICNOTE.ordinal(), music, channel, key.ordinal(), length, rest ? 1 : 0);
	}

	@Override
	public int getColor(MusicKey key) {
		return colors.get(key.getNote());
	}

	private static enum Pages {
		KEYS(),
		MIDI();

		private Pages otherPage() {
			return this == KEYS ? MIDI : KEYS;
		}
	}

}
