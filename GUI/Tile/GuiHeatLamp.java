/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI.Tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaFontRenderer;
import Reika.ChromatiCraft.Block.BlockHeatLamp.TileEntityHeatLamp;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.DragonAPI.Base.CoreContainer;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class GuiHeatLamp extends GuiContainer {

	private int temperature;

	private GuiTextField input;

	private final EntityPlayer player;
	private final TileEntityHeatLamp tile;

	public GuiHeatLamp(TileEntityHeatLamp te, EntityPlayer ep) {
		super(new CoreContainer(ep, te));

		ySize = 48;
		xSize = 176;

		tile = te;
		player = ep;

		temperature = te.temperature;
	}

	@Override
	public void initGui() {
		super.initGui();
		int j = (width - xSize) / 2+8;
		int k = (height - ySize) / 2 - 12;
		input = new GuiTextField(fontRendererObj, j+xSize/2, k+33, 60, 16);
		input.setFocused(false);
		input.setMaxStringLength(3);
	}

	@Override
	protected void keyTyped(char c, int i) {
		super.keyTyped(c, i);
		input.textboxKeyTyped(c, i);
	}

	@Override
	protected void mouseClicked(int i, int j, int k) {
		super.mouseClicked(i, j, k);
		input.mouseClicked(i, j, k);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		if (input.isFocused())
			temperature = this.parseInt(input);

		this.sendData();
	}

	private int parseInt(GuiTextField g) {
		if (g.getText().isEmpty()) {
			return 0;
		}
		if (!g.getText().isEmpty() && !ReikaJavaLibrary.isValidInteger(g.getText())) {
			g.deleteFromCursor(-1);
			return 0;
		}
		return ReikaJavaLibrary.safeIntParse(g.getText());
	}

	private void sendData() {
		tile.temperature = temperature;
		ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.HEATLAMP.ordinal(), tile, temperature);
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
		ReikaGuiAPI.instance.drawCenteredStringNoShadow(fontRendererObj, StatCollector.translateToLocal("chroma.heatlamp"), xSize/2, 5, 0xffffff);

		fontRendererObj.drawString("Temperature:", xSize/2-72, 25, 0xffffff);
		if (!input.isFocused()) {
			fontRendererObj.drawString(String.format("%d", temperature), xSize/2+12, 25, 0xffffffff);
		}
		//ReikaGuiAPI.instance.drawCenteredStringNoShadow(fontRendererObj, String.format("[%d]", sn), xSize/2+46, 25, c);
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

		input.drawTextBox();
	}

	public final String getFullTexturePath() {
		return "/Reika/ChromatiCraft/Textures/GUIs/heatlamp.png";
	}

}
