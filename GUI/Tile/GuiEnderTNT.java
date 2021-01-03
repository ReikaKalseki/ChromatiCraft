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

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Render.ChromaFontRenderer;
import Reika.ChromatiCraft.Block.BlockEnderTNT.TileEntityEnderTNT;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.DragonAPI.Base.CoreContainer;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Rendering.ReikaGuiAPI;

public class GuiEnderTNT extends GuiContainer {

	private final TileEntityEnderTNT tile;

	private int dim;
	private int tx;
	private int ty;
	private int tz;

	private int dim_last;
	private int tx_last;
	private int ty_last;
	private int tz_last;

	private GuiTextField input;
	private GuiTextField input2;
	private GuiTextField input3;
	private GuiTextField input4;

	public GuiEnderTNT(EntityPlayer player, TileEntityEnderTNT te) {
		super(new CoreContainer(player, te));
		tile = te;

		ySize = 106;
		xSize = 176;

		WorldLocation loc = tile.getTarget();
		if (loc != null) {
			dim = loc.dimensionID;
			tx = loc.xCoord;
			ty = loc.yCoord;
			tz = loc.zCoord;
		}
	}

	@Override
	public void initGui() {
		super.initGui();
		int j = (width - xSize) / 2+8;
		int k = (height - ySize) / 2 - 12;
		input = new GuiTextField(fontRendererObj, j+xSize/2-46, k+33, 40, 16);
		input.setFocused(false);
		input.setMaxStringLength(5);

		input2 = new GuiTextField(fontRendererObj, j+xSize/2-46, k+53, 40, 16);
		input2.setFocused(false);
		input2.setMaxStringLength(5);

		input3 = new GuiTextField(fontRendererObj, j+xSize/2-46, k+73, 40, 16);
		input3.setFocused(false);
		input3.setMaxStringLength(5);

		input4 = new GuiTextField(fontRendererObj, j+xSize/2-46, k+93, 40, 16);
		input4.setFocused(false);
		input4.setMaxStringLength(5);
	}

	@Override
	protected void keyTyped(char c, int i) {
		super.keyTyped(c, i);
		input.textboxKeyTyped(c, i);
		input2.textboxKeyTyped(c, i);
		input3.textboxKeyTyped(c, i);
		input4.textboxKeyTyped(c, i);
	}

	@Override
	protected void mouseClicked(int i, int j, int k) {
		super.mouseClicked(i, j, k);
		input.mouseClicked(i, j, k);
		input2.mouseClicked(i, j, k);
		input3.mouseClicked(i, j, k);
		input4.mouseClicked(i, j, k);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		if (input.isFocused())
			dim = this.parseInt(input);
		if (input2.isFocused())
			tx = this.parseInt(input2);
		if (input3.isFocused())
			ty = this.parseInt(input3);
		if (input4.isFocused())
			tz = this.parseInt(input4);

		if (this.isChanged()) {
			this.sendData();
		}
	}

	private boolean isChanged() {
		return dim != dim_last || tx != tx_last || ty != ty_last || tz != tz_last;
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
		dim_last = dim;
		tx_last = tx;
		ty_last = ty;
		tz_last = tz;
		ReikaPacketHelper.sendPacketToServer(ChromatiCraft.packetChannel, ChromaPackets.TNT.ordinal(), tile, dim, tx, ty, tz);
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
		ReikaGuiAPI.instance.drawCenteredStringNoShadow(fontRendererObj, StatCollector.translateToLocal("chroma.endertnt"), xSize/2, 5, 0xffffff);

		String[] s = new String[]{"World:", "X:", "Y:", "Z:"};
		for (int i = 0; i < 4; i++)
			fontRendererObj.drawString(s[i], xSize/2-48-fontRendererObj.getStringWidth(s[i]), 25+20*i, 0xffffff);
		if (!input.isFocused()) {
			fontRendererObj.drawString(String.format("%d", dim), xSize/2-34, 25, 0xffffffff);
		}
		if (!input2.isFocused()) {
			fontRendererObj.drawString(String.format("%d", tx), xSize/2-34, 45, 0xffffffff);
		}
		if (!input3.isFocused()) {
			fontRendererObj.drawString(String.format("%d", ty), xSize/2-34, 65, 0xffffffff);
		}
		if (!input4.isFocused()) {
			fontRendererObj.drawString(String.format("%d", tz), xSize/2-34, 85, 0xffffffff);
		}
		//World world = DimensionManager.getWorld(dim); //cannot get clientside
		String sn = "Dimension "+dim;//world != null ? world.provider.getDimensionName() : "NO SUCH WORLD";
		int c = 0xffffff;//world != null ? 0xffffff : 0xff0000;
		ReikaGuiAPI.instance.drawCenteredStringNoShadow(fontRendererObj, String.format("[%s]", sn), xSize/2+46, 25, c);
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
		input2.drawTextBox();
		input3.drawTextBox();
		input4.drawTextBox();
	}

	public final String getFullTexturePath() {
		return "/Reika/ChromatiCraft/Textures/GUIs/endertnt.png";
	}

}
