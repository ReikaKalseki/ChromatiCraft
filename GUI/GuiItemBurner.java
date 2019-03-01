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
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Render.ChromaFontRenderer;
import Reika.ChromatiCraft.Container.ContainerItemBurner;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.DragonAPI.Instantiable.GUI.ImagedGuiButton;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

public class GuiItemBurner extends GuiContainer {

	private final EntityPlayer player;

	public GuiItemBurner(EntityPlayer ep) {
		super(new ContainerItemBurner(ep));
		player = ep;
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.clear();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		buttonList.add(new ButtonItemBurner(this, j+142, k+16));
	}

	@Override
	protected void actionPerformed(GuiButton b) {
		super.actionPerformed(b);

		if (b.id == ButtonItemBurner.BUTTON_ID) {
			mc.displayGuiScreen(new GuiInventory(player));
			ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.BURNERINV.ordinal(), PacketTarget.server, 0);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		ChromaFontRenderer.FontType.GUI.renderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, 30, 0xffffff);
		String s = "Lumen Incineration";
		ChromaFontRenderer.FontType.GUI.renderer.drawString(s, xSize/2-ChromaFontRenderer.FontType.GUI.renderer.getStringWidth(s)/2, 4, 0xffffff);

		String var4 = "Textures/GUIs/itemburner.png";
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, var4);

		ContainerItemBurner c = (ContainerItemBurner)player.openContainer;
		int dv = c.getScaledBurn(16); //change v coord of texture, so slides upward
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		BlendMode.DEFAULT.apply();
		ReikaGuiAPI.instance.drawTexturedModalRect(80, 18, 178, 0+dv, 16, 16);
		GL11.glPopAttrib();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float ptick, int mx, int my) {
		GL11.glEnable(GL11.GL_BLEND);
		BlendMode.ALPHA.apply();
		String var4 = "Textures/GUIs/itemburner.png";
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, var4);
		int var5 = (width - xSize) / 2;
		int var6 = (height - ySize) / 2;
		this.drawTexturedModalRect(var5, var6, 0, 0, xSize, ySize);
		BlendMode.DEFAULT.apply();
		this.drawTexturedModalRect(var5, var6, 0, 0, xSize, ySize);
		GL11.glDisable(GL11.GL_BLEND);
	}

	public static class ButtonItemBurner extends ImagedGuiButton {

		public static final int BUTTON_ID = 240;
		private static final String TEXTURE = "Textures/GUIs/itemburner.png";

		public ButtonItemBurner(GuiContainer gui, int x, int y) {
			super(BUTTON_ID, x, y, 20, 20, gui instanceof GuiInventory ? 200 : 220, 36, TEXTURE, ChromatiCraft.class);
		}

		@Override
		protected void getHoveredTextureCoordinates() {
			v -= 20;
		}

	}

}
