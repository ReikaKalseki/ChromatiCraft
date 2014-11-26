/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.MouseEvent;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class GuiAbilitySelect extends GuiScreen {

	protected final EntityPlayer player;

	private int dx = 0;
	private double offset = 0;
	private Chromabilities ability;
	private int data = 0;

	public GuiAbilitySelect(EntityPlayer ep) {
		player = ep;
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
		if (ability != null && ability.maxPower() > 0) {
			int sc = data*64/ability.maxPower();
			this.drawTexturedModalRect(width/2-12, height/2+5-sc, 116, 76-sc, 24, sc);
		}

		int sep = 22;
		int w = 50;
		int step = w+sep;
		int move = Mouse.getDWheel();
		boolean ctrl = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
		if (move > 0) {
			if (ability != null && ctrl) {
				if (data < ability.maxPower(player) && ability.playerHasAbility(player))
					data++;
			}
			else {
				dx += step;
				data = 0;
			}
		}
		if (move < 0) {
			if (ctrl) {
				if (data > 0)
					data--;
			}
			else {
				dx -= step;
				data = 0;
			}
		}

		if (dx > 0)
			dx = 0;
		if (dx < -(Chromabilities.abilities.length-1)*step)
			dx = -(Chromabilities.abilities.length-1)*step;

		ability = Chromabilities.abilities[-dx/step];

		double sp = 90D/Math.max(1, ReikaRenderHelper.getFPS())*Math.max(1, Math.abs(offset-dx)/4)/5D;
		if (offset > dx) {
			offset -= sp;
		}
		if (offset < dx) {
			offset += sp;
		}
		if (Math.abs(offset-dx) < 1) {
			offset = dx;
		}

		for (int i = 0; i < Chromabilities.abilities.length; i++) {
			Chromabilities c = Chromabilities.abilities[i];
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
	}

	@Override
	protected void mouseMovedOrUp(int x, int y, int control) { //actually a mouse release

	}

	private String getTextureName(Chromabilities c) {
		String base = c.name().toLowerCase();
		String name = c.playerHasAbility(player) ? base : base+"_g";
		String path = "Textures/Ability/"+name+".png";
		return path;
	}

	@Override
	protected void mouseClicked(int x, int y, int button)
	{
		super.mouseClicked(x, y, button);
		if (button == 0 && ReikaGuiAPI.instance.isMouseInBox(width/2-37, width/2+37, height/2-37, height/2+37)) {
			if (ability != null && ability.playerHasAbility(player)) {
				if (ability.canPlayerExecuteAt(player)) {
					mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
					ability.trigger(player, data);
					player.closeScreen();
				}
				else {
					ReikaSoundHelper.playSound(ChromaSounds.ERROR, player, 1, 1);
					ReikaSoundHelper.playSound(ChromaSounds.ERROR, player, 1, 2);
				}
			}
		}
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}

	@Override
	public void onGuiClosed() {

	}

	@SubscribeEvent
	public void mouseEvent(MouseEvent evt) {

	}

}
