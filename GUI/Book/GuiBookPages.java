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
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Container.ContainerBookPages;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiBookPages extends GuiContainer {

	private final EntityPlayer player;

	private int scroll;

	//private static int mouseX;
	//private static int mouseY;
	//private boolean cacheMouse;
	private static int scrollCooldown = 0;

	public GuiBookPages(EntityPlayer p5ep, int sc) {
		super(new ContainerBookPages(p5ep, sc));
		player = p5ep;
		scroll = sc;
		//cacheMouse = true;
	}
	/*
	@Override
	public void initGui() {
		super.initGui();

		if (cacheMouse) {
			Mouse.setCursorPosition(mouseX, mouseY);
			cacheMouse = false;
		}
	}
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2)
	{
		ReikaGuiAPI.instance.drawCenteredStringNoShadow(fontRendererObj, "Pages", xSize/2, 6, 0xffffff);
		fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 96 + 2, 0xffffff);
	}

	//Use something like this
	//GuiContainerCreative:627 -> ((GuiContainerCreative.ContainerCreative)this.inventorySlots).scrollTo(this.currentScroll);
	private void scroll(boolean up) {
		scroll += up ? 1 : -1;
		scroll = MathHelper.clamp_int(scroll, 0, ContainerBookPages.MAX_SCROLL);
		//mouseX = Mouse.getX();
		//mouseY = Mouse.getY();
		//player.closeScreen();
		//player.openGui(ChromatiCraft.instance, ChromaGuis.BOOKPAGES.ordinal(), null, scroll, 0, 0);
		scrollCooldown = 15;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
	{
		if (scrollCooldown > 0)
			scrollCooldown--;
		else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			this.scroll(false);
		}
		else if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			this.scroll(true);
		}
		String var4 = "/Reika/ChromatiCraft/Textures/GUIs/basicstorage_small.png";
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, var4);
		int var5 = (width - xSize) / 2;
		int var6 = (height - ySize) / 2;
		this.drawTexturedModalRect(var5, var6, 0, 0, xSize, ySize);
	}
}
