/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI;

import Reika.ChromatiCraft.Container.ContainerCrystalBrewer;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalBrewer;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

public class GuiCrystalBrewer extends GuiContainer {

	private static final ResourceLocation textures = new ResourceLocation("textures/gui/container/brewing_stand.png");
	private TileEntityCrystalBrewer tile;

	public GuiCrystalBrewer(EntityPlayer ep, TileEntityCrystalBrewer te) {
		super(new ContainerCrystalBrewer(ep, te));
		tile = te;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2)
	{
		String s = ChromaTiles.BREWER.getName();
		fontRendererObj.drawString(s, xSize / 2 - fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
		fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 96 + 2, 4210752);
	}

	/**
	 * Draw the background layer for the GuiContainer (everything behind the items)
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(textures);
		int k = (width - xSize) / 2;
		int l = (height - ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, xSize, ySize);
		int i1 = tile.getBrewTime();

		if (i1 > 0)
		{
			int j1 = (int)(28.0F * (1.0F - i1 / 400.0F));

			if (j1 > 0)
			{
				this.drawTexturedModalRect(k + 97, l + 16, 176, 0, 9, j1);
			}

			int k1 = i1 / 2 % 7;

			switch (k1)
			{
			case 0:
				j1 = 29;
				break;
			case 1:
				j1 = 24;
				break;
			case 2:
				j1 = 20;
				break;
			case 3:
				j1 = 16;
				break;
			case 4:
				j1 = 11;
				break;
			case 5:
				j1 = 6;
				break;
			case 6:
				j1 = 0;
			}

			if (j1 > 0)
			{
				this.drawTexturedModalRect(k + 65, l + 14 + 29 - j1, 185, 29 - j1, 12, j1);
			}
		}
	}

}
