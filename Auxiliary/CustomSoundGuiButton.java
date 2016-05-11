/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import Reika.DragonAPI.Instantiable.GUI.ImagedGuiButton;

public class CustomSoundGuiButton extends GuiButton {

	public final CustomSoundGui gui;
	private boolean lastHover;
	private int ticks = 0;

	public CustomSoundGuiButton(int id, int x, int y, int w, int h, String s, CustomSoundGui gui) {
		super(id, x, y, w, h, s);
		this.gui = gui;
	}

	@Override
	public void drawButton(Minecraft mc, int x, int y)
	{
		super.drawButton(mc, x, y);
		if (visible) {
			if (field_146123_n && !lastHover && ticks > 1) {
				gui.playHoverSound(this);
			}
			lastHover = field_146123_n;
			ticks++;
		}
	}

	@Override
	public void func_146113_a(SoundHandler sh)
	{
		gui.playButtonSound(this);
	}

	public static interface CustomSoundGui {

		void playButtonSound(GuiButton b);

		void playHoverSound(GuiButton b);

	}

	public static class CustomSoundImagedGuiButton extends ImagedGuiButton {

		public final CustomSoundGui gui;

		public CustomSoundImagedGuiButton(int par1, int par2, int par3, String par4Str, Class mod, CustomSoundGui gui)
		{
			super(par1, par2, par3, par4Str, mod);
			this.gui = gui;
		}

		/** Draw a Gui Button with an image background. Args: id, x, y, width, height, u, v, filepath, class root */
		public CustomSoundImagedGuiButton(int par1, int par2, int par3, int par4, int par5, int par7, int par8, String file, Class mod, CustomSoundGui gui)
		{
			super(par1, par2, par3, par4, par5, par7, par8, file, mod);
			this.gui = gui;
		}

		/** Draw a Gui Button with an image background and text overlay.
		 *Args: id, x, y, width, height, u, v, text overlay, text color, shadow, filepath, class root */
		public CustomSoundImagedGuiButton(int par1, int par2, int par3, int par4, int par5, int par7, int par8, String par6Str, int par9, boolean par10, String file, Class mod, CustomSoundGui gui)
		{
			super(par1, par2, par3, par4, par5, par7, par8, par6Str, par9, par10, file, mod);
			this.gui = gui;
		}

		/** Draw a Gui Button with an image background and text tooltip. Args: id, x, y, width, height, u, v, filepath, text tooltip, text color, shadow */
		public CustomSoundImagedGuiButton(int par1, int par2, int par3, int par4, int par5, int par7, int par8, String file, String par6Str, int par9, boolean par10, Class mod, CustomSoundGui gui)
		{
			super(par1, par2, par3, par4, par5, par7, par8, file, par6Str, par9, par10, mod);
			this.gui = gui;
		}

		@Override
		public void func_146113_a(SoundHandler sh)
		{
			gui.playButtonSound(this);
		}

		@Override
		protected void onHoverTo() {
			gui.playHoverSound(this);
		}

	}

}
