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
import Reika.ChromatiCraft.Auxiliary.Render.ChromaFontRenderer;
import Reika.ChromatiCraft.Block.BlockHoverBlock.HoverType;
import Reika.ChromatiCraft.Items.Tools.Wands.ItemFlightWand;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.DragonAPI.Instantiable.GUI.CustomSoundGuiButton.CustomSoundGui;
import Reika.DragonAPI.Instantiable.GUI.CustomSoundGuiButton.CustomSoundImagedGuiButton;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;

public class GuiFlightWand extends GuiScreen implements CustomSoundGui {

	private final EntityPlayer player;

	private final int xSize = 111;
	private final int ySize = 166;

	public GuiFlightWand(EntityPlayer ep) {
		player = ep;
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.clear();
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		String tex = "Textures/GUIs/buttons.png";

		int l = HoverType.list.length;
		HoverType md = this.getCurrentMode();
		for (int i = 0; i < l; i++) {
			int u = md.ordinal() == i ? 18 : 0;
			GuiButton b = new CustomSoundImagedGuiButton(i, j+8, k+14+i*40, 18, 18, u, 0, tex, ChromatiCraft.class, this);
			b.packedFGColour = md.renderColor;
			buttonList.add(b);
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
		ItemFlightWand.setMode(player.getCurrentEquippedItem(), HoverType.list[b.id]);
		ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.HOVERWAND.ordinal(), PacketTarget.server, b.id);
		this.initGui();
	}

	@Override
	public void drawScreen(int x, int y, float ptick) {
		super.drawScreen(x, y, ptick);
		GL11.glPushMatrix();
		GL11.glTranslated(0, 0, -500);
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/GUIs/hoverwand.png");
		ReikaGuiAPI.instance.drawTexturedModalRect(j, k, 0, 0, xSize, ySize);
		int tx = j+xSize/2;
		int ty = k+4;
		ReikaGuiAPI.instance.drawCenteredStringNoShadow(ChromaFontRenderer.FontType.GUI.renderer, "Hover Selection", tx, ty, 0xffffff);

		int l = HoverType.list.length;
		HoverType md = this.getCurrentMode();
		for (int i = 0; i < l; i++) {
			int color = HoverType.list[i].renderColor;//md.ordinal() == i ? 0x00ff00 : 0xffffff;
			if (md.ordinal() == i) {
				color = 0xffffff;
			}
			tx = j+30;
			ty = k+19+i*40;
			ChromaFontRenderer.FontType.GUI.renderer.drawString(HoverType.list[i].desc, tx, ty, color);
		}
		GL11.glPopMatrix();
	}

	private HoverType getCurrentMode() {
		return ItemFlightWand.getMode(player.getCurrentEquippedItem());
	}

}
