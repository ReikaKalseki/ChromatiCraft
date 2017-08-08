/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Render.ChromaFontRenderer;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.DragonAPI.DragonOptions;
import Reika.DragonAPI.Instantiable.GUI.CustomSoundGuiButton.CustomSoundGui;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

public abstract class GuiChromaBase extends GuiContainer implements CustomSoundGui {

	private final TileEntityChromaticBase tile;
	protected EntityPlayer player;
	protected static final ReikaGuiAPI api = ReikaGuiAPI.instance;
	private int clickCooldown;

	public GuiChromaBase(Container par1Container, EntityPlayer ep, TileEntityChromaticBase te) {
		super(par1Container);
		tile = te;
		player = ep;
	}

	public final int getXSize() {
		return xSize;
	}

	public final int getYSize() {
		return ySize;
	}

	public int getGuiLeft() {
		return guiLeft;
	}

	public int getGuiTop() {
		return guiTop;
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.clear();
	}

	@Override
	protected void mouseClicked(int x, int y, int button) {
		super.mouseClicked(x, y, button);

		clickCooldown = 0;
	}

	@Override
	public void setWorldAndResolution(Minecraft mc, int x, int y) {
		super.setWorldAndResolution(mc, x, y);
		fontRendererObj = ChromaFontRenderer.FontType.GUI.renderer;
	}

	@Override
	public void drawScreen(int x, int y, float a) {
		super.drawScreen(x, y, a);

		clickCooldown++;
	}

	protected final int getClickCooldown() {
		return clickCooldown;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		ReikaTextureHelper.bindFontTexture();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		if (this.drawTitle())
			api.drawCenteredStringNoShadow(fontRendererObj, this.getGuiName(), xSize/2, this.getTitlePosition(), 0xffffff);

		if (tile instanceof IInventory && this.labelInventory()) {
			int dx = this.inventoryLabelLeft() ? 8 : xSize-58;
			fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), dx, (ySize - 96) + 3, 0xffffff);
		}
	}

	protected String getGuiName() {
		return tile.getName();
	}

	protected boolean drawTitle() {
		return true;
	}

	protected int getTitlePosition() {
		return 5;
	}

	protected boolean inventoryLabelLeft() {
		return false;
	}

	protected boolean labelInventory() {
		return true;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int a, int b) {
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		this.drawFromBackground(j, k, 0, 0, xSize, ySize);
	}

	protected final void drawFromBackground(int j, int k, int u, int v, int w, int h) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
		String i = this.getFullTexturePath();
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, i);
		GL11.glEnable(GL11.GL_BLEND);
		BlendMode.ALPHA.apply();
		this.drawTexturedModalRect(j, k, u, v, w, h);
		BlendMode.DEFAULT.apply();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.25F);
		this.drawTexturedModalRect(j, k, u, v, w, h);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		//ReikaTextureHelper.bindTexture(ChromatiCraft.class, i);
	}

	public abstract String getGuiTexture();

	public final String getFullTexturePath() {
		return "/Reika/ChromatiCraft/Textures/GUIs/"+this.getGuiTexture()+".png";
	}

	@Override
	protected final void func_146977_a(Slot slot) {
		super.func_146977_a(slot);
		if (Keyboard.isKeyDown(DragonOptions.DEBUGKEY.getValue()) && DragonOptions.TABNBT.getState()) {
			ReikaTextureHelper.bindFontTexture();
			fontRendererObj.drawString(String.format("%d", slot.getSlotIndex()), slot.xDisplayPosition+1, slot.yDisplayPosition+1, 0x888888);
		}
	}

	public void playButtonSound(GuiButton b) {
		ReikaSoundHelper.playClientSound(ChromaSounds.GUICLICK, player, 0.5F, 1);
	}

	public void playHoverSound(GuiButton b) {
		ReikaSoundHelper.playClientSound(ChromaSounds.GUISEL, player, 0.75F, 1);
	}

}
