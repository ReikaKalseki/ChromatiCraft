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

import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Container.ContainerFragmentSelect;
import Reika.ChromatiCraft.Magic.Progression.ProgressionChoiceSystem.Selection;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.DragonAPI.Instantiable.GUI.CustomSoundGuiButton.CustomSoundGui;
import Reika.DragonAPI.Instantiable.GUI.CustomSoundGuiButton.CustomSoundImagedGuiButton;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;


public class GuiFragmentSelect extends GuiContainer implements CustomSoundGui {

	private static final int DURATION = 1500;

	private final EntityPlayer player;

	private int selectionIndex = -1;
	private long selectionCountdown = -1;

	public GuiFragmentSelect(EntityPlayer ep) {
		super(new ContainerFragmentSelect(ep));
		player = ep;

		ySize = 162;
		xSize = 180;
	}

	@Override
	public void initGui() {
		super.initGui();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		buttonList.clear();
		for (int i = 0; i < 3; i++)
			buttonList.add(new SelectionButton(i, j+33+i*(9+SelectionButton.SIZE), k+37));
	}

	@Override
	protected void actionPerformed(GuiButton b) {
		super.actionPerformed(b);

		if (b.id <= 2) {
			if (this.hasFragment()) {
				this.startSelection(b.id);
			}
		}
	}

	private void startSelection(int id) {
		selectionIndex = id;
		selectionCountdown = System.currentTimeMillis()+DURATION;
	}

	public boolean hasFragment() {
		return this.getContainer().hasFragment();
	}

	private ContainerFragmentSelect getContainer() {
		return (ContainerFragmentSelect)inventorySlots;
	}

	public void playButtonSound(GuiButton b) {
		ReikaSoundHelper.playClientSound(ChromaSounds.GUICLICK, player, 0.5F, 1);
	}

	public void playHoverSound(GuiButton b) {
		ReikaSoundHelper.playClientSound(ChromaSounds.GUISEL, player, 0.8F, 1);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public final void drawScreen(int x, int y, float f) {
		super.drawScreen(x, y, f);

		boolean frag = this.hasFragment();
		for (GuiButton b : ((List<GuiButton>)buttonList)) {
			if (b.id <= 2) {
				SelectionButton sb = (SelectionButton)b;
				b.enabled = b.visible = frag;
				Selection s = frag ? this.getContainer().getOption(b.id) : null;
				int idx = s != null ? s.fragment.ordinal()*SelectionButton.SIZE : -1;
				sb.textureU = (idx%SelectionButton.ROWCOLS)*SelectionButton.SIZE;
				sb.textureV = (idx/SelectionButton.ROWCOLS)*SelectionButton.SIZE;
			}
		}

		if (selectionIndex >= 0) {
			long time = System.currentTimeMillis();
			float frac = 1F-(selectionCountdown-time)/(float)DURATION;
			draw unroll or flip
			if (time >= selectionCountdown)
				this.getContainer().selectSlot(selectionIndex);
		}
	}

	@Override
	protected void mouseClicked(int x, int y, int button) {
		super.mouseClicked(x, y, button);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float ptick, int mx, int my) {
		GL11.glEnable(GL11.GL_BLEND);
		String var4 = "Textures/GUIs/fragselect.png";
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, var4);
		int var5 = (width - xSize) / 2;
		int var6 = (height - ySize) / 2;
		BlendMode.DEFAULT.apply();
		double a = 0.75+0.04*Math.sin(System.currentTimeMillis()/437D)+0.06*Math.cos(System.currentTimeMillis()/639D);
		GL11.glColor4f(1, 1, 1, (float)a);
		this.drawTexturedModalRect(var5-32, var6-32, 0, 0, xSize+64, ySize+64);
		GL11.glDisable(GL11.GL_BLEND);
	}

	private class SelectionButton extends CustomSoundImagedGuiButton {

		private static final int SIZE = 32;
		private static final int TEX_SIZE = 256;
		private static final int ROWCOLS = TEX_SIZE/SIZE;

		private int textureU = 0;
		private int textureV = 0;

		public SelectionButton(int id, int x, int y) {
			super(id, x, y, SIZE, SIZE, 0, 0, "Textures/fragmentcategories.png", ChromatiCraft.class, GuiFragmentSelect.this);
		}

		@Override
		protected void modifyTextureUV() {
			u = textureU;
			v = textureV;
		}
		
		public void draw() {
			super.draw()
			glowing border like my steam profile
		}

	}

}
