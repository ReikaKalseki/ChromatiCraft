/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI.Book;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Container.ContainerBookPages;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiBookPages extends GuiContainer
{
	public GuiBookPages(EntityPlayer p5ep) {
		super(new ContainerBookPages(p5ep));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2)
	{
		ReikaGuiAPI.instance.drawCenteredStringNoShadow(fontRendererObj, "Pages", xSize/2, 6, 0xffffff);
		fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 96 + 2, 0xffffff);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
	{
		String var4 = "/Reika/ChromatiCraft/Textures/GUIs/basicstorage_small.png";
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, var4);
		int var5 = (width - xSize) / 2;
		int var6 = (height - ySize) / 2;
		this.drawTexturedModalRect(var5, var6, 0, 0, xSize, ySize);
	}
}
