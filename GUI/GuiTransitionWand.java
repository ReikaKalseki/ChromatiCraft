/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaFontRenderer;
import Reika.ChromatiCraft.Auxiliary.CustomSoundGuiButton.CustomSoundGui;
import Reika.ChromatiCraft.Auxiliary.CustomSoundGuiButton.CustomSoundImagedGuiButton;
import Reika.ChromatiCraft.Items.Tools.Wands.ItemTransitionWand;
import Reika.ChromatiCraft.Items.Tools.Wands.ItemTransitionWand.TransitionMode;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;

public class GuiTransitionWand extends GuiScreen implements CustomSoundGui {

	private final EntityPlayer player;

	private final int xSize = 176;
	private final int ySize = 52;

	public GuiTransitionWand(EntityPlayer ep) {
		player = ep;
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.clear();
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		String tex = "Textures/GUIs/buttons.png";

		int l = TransitionMode.list.length;
		TransitionMode md = this.getCurrentMode();
		for (int i = 0; i < l; i++) {
			int u = md.ordinal() == i ? 18 : 0;
			buttonList.add(new CustomSoundImagedGuiButton(i, j+8+i*(xSize-10)/l, k+14, 18, 18, u, 0, tex, ChromatiCraft.class, this));
		}
	}

	public void playButtonSound(GuiButton b) {
		ReikaSoundHelper.playClientSound(ChromaSounds.GUICLICK, player, 0.5F, 1);
	}

	public void playHoverSound(GuiButton b) {
		ReikaSoundHelper.playClientSound(ChromaSounds.GUISEL, player, 0.8F, 1);
	}

	@Override
	protected void actionPerformed(GuiButton b) {
		this.getItem().setMode(player.getCurrentEquippedItem(), TransitionMode.list[b.id]);
		ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.TRANSITIONWAND.ordinal(), new PacketTarget.ServerTarget(), b.id);
		this.initGui();
	}

	@Override
	public void drawScreen(int x, int y, float ptick) {
		super.drawScreen(x, y, ptick);
		GL11.glPushMatrix();
		GL11.glTranslated(0, 0, -500);
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/GUIs/transition.png");
		ReikaGuiAPI.instance.drawTexturedModalRect(j, k, 0, 0, xSize, ySize);
		int tx = j+xSize/2;
		int ty = k+4;
		ReikaGuiAPI.instance.drawCenteredStringNoShadow(ChromaFontRenderer.FontType.GUI.renderer, "Transition Wand Mode Selection", tx, ty, 0xffffff);

		int l = TransitionMode.list.length;
		TransitionMode md = this.getCurrentMode();
		for (int i = 0; i < l; i++) {
			int color = md.ordinal() == i ? 0x00ff00 : 0xffffff;
			int w = (xSize-10)/l;
			tx = j+8+i*w;
			ty = k+33;
			ChromaFontRenderer.FontType.GUI.renderer.drawSplitString(TransitionMode.list[i].desc, tx, ty, w, color);
		}
		GL11.glPopMatrix();
	}

	private TransitionMode getCurrentMode() {
		return this.getItem().getMode(player.getCurrentEquippedItem());
	}

	private ItemTransitionWand getItem() {
		return (ItemTransitionWand)player.getCurrentEquippedItem().getItem();
	}

}
