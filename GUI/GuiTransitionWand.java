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

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Render.ChromaFontRenderer;
import Reika.ChromatiCraft.Base.GuiChromaTool;
import Reika.ChromatiCraft.Items.Tools.Wands.ItemTransitionWand;
import Reika.ChromatiCraft.Items.Tools.Wands.ItemTransitionWand.TransitionMode;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.DragonAPI.Instantiable.GUI.CustomSoundGuiButton.CustomSoundImagedGuiButton;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;

public class GuiTransitionWand extends GuiChromaTool {

	private final int xSize = 228;
	private final int ySize = 52;

	public GuiTransitionWand(EntityPlayer ep) {
		super(ep);
	}

	@Override
	public void initGui() {
		super.initGui();
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

	@Override
	protected void actionPerformed(GuiButton b) {
		ItemTransitionWand item = this.getItem();
		if (item == null)
			return;
		this.getItem().setMode(player.getCurrentEquippedItem(), TransitionMode.list[b.id]);
		ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.TRANSITIONWAND.ordinal(), PacketTarget.server, b.id);
		this.initGui();
	}

	@Override
	public void drawScreen(int x, int y, float ptick) {
		ItemTransitionWand item = this.getItem();
		if (item == null)
			return;
		super.drawScreen(x, y, ptick);
		GL11.glPushMatrix();
		GL11.glTranslated(0, 0, -500);
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/GUIs/transition2.png");
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
		Item base = player.getCurrentEquippedItem().getItem();
		return base instanceof ItemTransitionWand ? (ItemTransitionWand)base : null;
	}

}
