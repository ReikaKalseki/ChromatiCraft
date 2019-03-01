/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI.Tile.Inventory;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Block.Dimension.Structure.BlockStructureDataStorage.TileEntityStructurePassword;
import Reika.ChromatiCraft.Container.ContainerStructurePassword;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

public class GuiStructurePassword extends GuiContainer {

	private TileEntityStructurePassword tile;

	public GuiStructurePassword(EntityPlayer ep, TileEntityStructurePassword te) {
		super(new ContainerStructurePassword(ep, te));
		tile = te;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		String s = tile.getInventoryName();
		fontRendererObj.drawString(s, xSize / 2 - fontRendererObj.getStringWidth(s) / 2, 6, 0xffffff);
		//fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 96 + 2, 0xffffff);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int a, int b) {
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
		String i = "/Reika/ChromatiCraft/Textures/GUIs/structpass.png";
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, i);
		GL11.glEnable(GL11.GL_BLEND);
		BlendMode.ALPHA.apply();
		this.drawTexturedModalRect(j, k, 0, 0, xSize, ySize);
		BlendMode.DEFAULT.apply();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.25F);
		this.drawTexturedModalRect(j, k, 0, 0, xSize, ySize);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		//ReikaTextureHelper.bindTexture(ChromatiCraft.class, i);
	}

}
