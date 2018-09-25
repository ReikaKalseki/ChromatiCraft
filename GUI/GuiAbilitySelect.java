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

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromaClient;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.API.AbilityAPI.Ability;
import Reika.ChromatiCraft.Auxiliary.Ability.AbilityHotkeys;
import Reika.ChromatiCraft.Auxiliary.Ability.AbilityHotkeys.CachedAbilitySelection;
import Reika.ChromatiCraft.Auxiliary.Render.ChromaFontRenderer.FontType;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;

public class GuiAbilitySelect extends GuiScreen {

	protected final EntityPlayer player;

	private int dx = 0;
	private double offset = 0;
	private Ability ability;
	private int data = 0;

	private final ArrayList<Ability> abilities = new ArrayList();

	public GuiAbilitySelect(EntityPlayer ep) {
		player = ep;
		abilities.addAll(Chromabilities.getAbilitiesAvailableToPlayer(ep));
		allowUserInput = true;
	}

	@Override
	protected final void keyTyped(char c, int key)
	{
		super.keyTyped(c, key);
		int sep = 22;
		int w = 50;
		int step = w+sep;

		for (int i = 0; i < AbilityHotkeys.keys.length; i++) {
			if (AbilityHotkeys.keys[i] != null) {
				if (key == AbilityHotkeys.keys[i].getKeyCode()) {
					AbilityHotkeys.cacheAbility(player, ability, data, i);
					ReikaSoundHelper.playClientSound(ChromaSounds.GUICLICK, player, 1, 1);
					player.closeScreen();
					return;
				}
			}
		}

		if (key == Keyboard.KEY_ESCAPE || (ChromaOptions.KEYBINDABILITY.getState() && key == ChromaClient.key_ability.getKeyCode())) {
			player.closeScreen();
		}
		else if (key == Keyboard.KEY_RETURN || key == Keyboard.KEY_SPACE) {
			this.selectAbility();
		}
		else if (key == Keyboard.KEY_RIGHT || key == Minecraft.getMinecraft().gameSettings.keyBindRight.getKeyCode()) {
			this.scrollRight(step);
		}
		else if (key == Keyboard.KEY_LEFT || key == Minecraft.getMinecraft().gameSettings.keyBindLeft.getKeyCode()) {
			this.scrollLeft(step);
		}
		else if (key == Keyboard.KEY_UP || key == Minecraft.getMinecraft().gameSettings.keyBindForward.getKeyCode()) {
			if (ability != null && data < Chromabilities.maxPower(player, ability) && Chromabilities.playerHasAbility(player, ability))
				data++;
		}
		else if (key == Keyboard.KEY_DOWN || key == Minecraft.getMinecraft().gameSettings.keyBindBack.getKeyCode()) {
			if (data > 0)
				data--;
		}
		else if (key == Keyboard.KEY_END) {
			this.scrollRight(Integer.MAX_VALUE);
		}
		else if (key == Keyboard.KEY_HOME) {
			this.scrollLeft(Integer.MAX_VALUE);
		}
		else if (key == Keyboard.KEY_NEXT) {
			this.scrollRight(step*2);
		}
		else if (key == Keyboard.KEY_PRIOR) {
			this.scrollLeft(step*2);
		}
	}

	@Override
	public void initGui() {
		super.initGui();

		Keyboard.enableRepeatEvents(true);
	}

	@Override
	public void onGuiClosed()
	{
		super.onGuiClosed();

		Keyboard.enableRepeatEvents(false);
	}

	@Override
	public final void drawScreen(int x, int y, float f)
	{
		super.drawScreen(x, y, f);

		int dy = 51;

		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/GUIs/abilityselect.png");
		int tx = width/2-128;
		int ty = height/2-128+dy+15;
		GL11.glEnable(GL11.GL_BLEND);
		this.drawTexturedModalRect(tx, ty, 0, 79, 256, 177);
		GL11.glDisable(GL11.GL_BLEND);
		if (ability != null && ability.getMaxPower() > 0) {
			int sc = data*64/ability.getMaxPower();
			this.drawTexturedModalRect(width/2-12, height/2+5-sc, 116, 76-sc, 24, sc);
			ReikaGuiAPI.instance.drawCenteredStringNoShadow(FontType.GUI.renderer, String.format("%d/%d", data, ability.getMaxPower()), width/2, height/2-72, 0xffffff);
		}

		int sep = 22;
		int w = 50;
		int step = w+sep;
		int move = Mouse.getDWheel();
		if (move != 0) {
			ReikaSoundHelper.playClientSound(ChromaSounds.GUISEL, player, 1, 1);
		}
		boolean ctrl = GuiScreen.isCtrlKeyDown() || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
		if (move > 0) {
			if (ability != null && ctrl) {
				if (data < Chromabilities.maxPower(player, ability) && Chromabilities.playerHasAbility(player, ability))
					data++;
			}
			else {
				this.scrollRight(step);
			}
		}
		if (move < 0) {
			if (ctrl) {
				if (data > 0)
					data--;
			}
			else {
				this.scrollLeft(step);
			}
		}

		if (dx > 0)
			dx = 0;
		if (dx < -(abilities.size()-1)*step)
			dx = -(abilities.size()-1)*step;

		ability = abilities.get(-dx/step);

		double sp = Math.min(10, 270D/Math.max(1, ReikaRenderHelper.getFPS()))*Math.max(1, Math.abs(offset-dx)/4)/5D;
		if (offset > dx) {
			offset -= sp;
		}
		if (offset < dx) {
			offset += sp;
		}
		if (Math.abs(offset-dx) < 1) {
			offset = dx;
		}

		for (int i = 0; i < abilities.size(); i++) {
			Ability c = abilities.get(i);
			int w2 = c == ability ? Math.max(50, 75-(int)Math.abs(dx-offset)) : w;
			String s = this.getTextureName(c);
			ReikaTextureHelper.bindTexture(ChromatiCraft.class, s);
			GL11.glPushMatrix();
			double d = w2/256D;
			GL11.glScaled(d, d, d);
			int ax = (int)offset+width/2-w2/2+i*step;
			int ay = height/2-w2/2+dy;
			if (c == ability) {
				ay -= 2;
				ax--;
			}
			this.drawTexturedModalRect((int)(ax/d), (int)(ay/d), 0, 0, 256, 256);
			GL11.glPopMatrix();
		}

		//fontRendererObj.drawString(String.valueOf(data), 8, 8, 0xffffff);

		for (int i = 0; i < AbilityHotkeys.SLOTS; i++) {
			KeyBinding kb = AbilityHotkeys.keys[i];
			if (kb != null) {
				String disp = GameSettings.getKeyDisplayString(kb.getKeyCode());
				fontRendererObj.drawString("Press "+disp+" to save this ability to hotkey slot "+i, 8, 8+i*(fontRendererObj.FONT_HEIGHT+1), 0xffffff);

				CachedAbilitySelection a = AbilityHotkeys.getCachedHotkey(player, i);
				double sz = 0.1;
				GL11.glPushMatrix();
				GL11.glScaled(sz, sz, sz);
				int ix = (int)((width/2+60+i*40)/sz);
				int iy = (int)(8/sz);
				if (a != null) {
					String s = this.getTextureName(a.ability);
					ReikaTextureHelper.bindTexture(ChromatiCraft.class, s);
				}
				else {
					ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/NoAbilityHotKey.png");
				}
				this.drawTexturedModalRect(ix, iy, 0, 0, 256, 256);
				GL11.glPopMatrix();
				fontRendererObj.drawString(String.valueOf(i), (int)(ix*sz)-6, (int)(iy*sz), 0xffffff);
				fontRendererObj.drawString(disp, (int)(ix*sz)+13-fontRendererObj.getStringWidth(disp)/2, (int)(iy*sz)+27, 0xffffff);
			}
		}
	}

	private void scrollLeft(int step) {
		dx += step;
		data = 0;
	}

	private void scrollRight(int step) {
		dx -= step;
		data = 0;
	}

	@Override
	protected void mouseMovedOrUp(int x, int y, int control) { //actually a mouse release

	}

	private String getTextureName(Ability c) {
		return c.getTexturePath(!Chromabilities.playerHasAbility(player, c));
	}

	@Override
	protected void mouseClicked(int x, int y, int button) {
		super.mouseClicked(x, y, button);
		if (button == 0 && ReikaGuiAPI.instance.isMouseInBox(width/2-37, width/2+37, height/2-37, height/2+37)) {
			this.selectAbility();
		}
	}

	private void selectAbility() {
		if (selectAbility(player, ability, data)) {
			player.closeScreen();
		}
	}

	public static boolean selectAbility(EntityPlayer ep, Ability a, int data) {
		if (a != null && Chromabilities.playerHasAbility(ep, a)) {
			if (Chromabilities.canPlayerExecuteAt(ep, a)) {
				ReikaSoundHelper.playClientSound(ChromaSounds.GUICLICK, ep, 0.75F, 1);
				Chromabilities.triggerAbility(ep, a, data, true);
				return true;
			}
			else {
				ReikaSoundHelper.playClientSound(ChromaSounds.ERROR, ep, 1, 1);
				ReikaSoundHelper.playClientSound(ChromaSounds.ERROR, ep, 1, 2);
			}
		}
		return false;
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	public Ability getSelectedAbility() {
		return ability;
	}

	public int getSelectedData() {
		return data;
	}

}
