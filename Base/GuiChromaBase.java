/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;

public abstract class GuiChromaBase extends GuiContainer {

	private final TileEntityChromaticBase tile;
	protected EntityPlayer player;

	public GuiChromaBase(Container par1Container, TileEntityChromaticBase te) {
		super(par1Container);
		tile = te;
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.clear();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		ReikaTextureHelper.bindFontTexture();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		ReikaGuiAPI.instance.drawCenteredStringNoShadow(fontRenderer, tile.getName(), xSize/2, 5, 4210752);

		if (tile instanceof IInventory && this.labelInventory()) {
			int dx = this.inventoryLabelLeft() ? 8 : xSize-58;
			fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), dx, (ySize - 96) + 3, 4210752);
		}
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
		String i = this.getFullTexturePath();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, i);
		this.drawTexturedModalRect(j, k, 0, 0, xSize, ySize);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, i);
	}

	public abstract String getGuiTexture();

	public final String getFullTexturePath() {
		return "/Reika/ChromatiCraft/Textures/GUIs/"+this.getGuiTexture()+".png";
	}

}
