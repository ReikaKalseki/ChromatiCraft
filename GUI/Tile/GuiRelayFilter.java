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

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Render.ChromaFontRenderer;
import Reika.ChromatiCraft.Block.Relay.BlockRelayFilter.TileEntityRelayFilter;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Base.CoreContainer;
import Reika.DragonAPI.Instantiable.GUI.ImagedGuiButton;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;

public class GuiRelayFilter extends GuiContainer {

	private final TileEntityRelayFilter tile;

	private boolean[] allow = new boolean[16];

	public GuiRelayFilter(EntityPlayer player, TileEntityRelayFilter te) {
		super(new CoreContainer(player, te));
		tile = te;

		ySize = 110;
		xSize = 176;

		allow = te.getFilter();
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.clear();
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		String tex = this.getFullTexturePath();
		for (int i = 0; i < 16; i++) {
			int x = j+xSize/2+10;
			x += ((i/4)-2)*40;
			int y = k+20+(i%4)*20;
			buttonList.add(new ImagedGuiButton(i, x, y, 20, 20, 180, 0, tex, ChromatiCraft.class));
		}
	}

	@Override
	protected void actionPerformed(GuiButton b) {
		super.actionPerformed(b);

		allow[b.id] = !allow[b.id];
		this.sendData(b.id);
	}

	@Override
	protected void keyTyped(char c, int i) {
		super.keyTyped(c, i);
	}

	@Override
	protected void mouseClicked(int i, int j, int k) {
		super.mouseClicked(i, j, k);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
	}

	private void sendData(int color) {
		ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.RELAYFILTER.ordinal(), tile, color, allow[color] ? 1 : 0);
	}

	@Override
	public void setWorldAndResolution(Minecraft mc, int x, int y) {
		super.setWorldAndResolution(mc, x, y);
		fontRendererObj = ChromaFontRenderer.FontType.GUI.renderer;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		ReikaTextureHelper.bindFontTexture();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		ReikaGuiAPI.instance.drawCenteredStringNoShadow(fontRendererObj, StatCollector.translateToLocal("chroma.relayfilter"), xSize/2, 5, 0xffffff);

		ReikaTextureHelper.bindTerrainTexture();
		for (int i = 0; i < 16; i++) {
			int x = xSize/2+10;
			x += ((i/4)-2)*40;
			int y = 20+(i%4)*20;
			CrystalElement e = CrystalElement.elements[i];
			IIcon ico = e.getGlowRune();
			ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(x+2, y+2, ico, 16, 16);

			IIcon ico2 = allow[i] ? ChromaIcons.CHECK.getIcon() : ChromaIcons.NOENTER.getIcon();
			ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(x+2+8, y+2+8, ico2, 8, 8);
		}

	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int a, int b) {
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		String i = this.getFullTexturePath();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, i);
		this.drawTexturedModalRect(j, k, 0, 0, xSize, ySize);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, i);
	}

	public final String getFullTexturePath() {
		return "/Reika/ChromatiCraft/Textures/GUIs/relayfilter.png";
	}

}
