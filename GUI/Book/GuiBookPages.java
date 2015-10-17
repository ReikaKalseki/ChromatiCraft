/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI.Book;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.CustomSoundGuiButton.CustomSoundGui;
import Reika.ChromatiCraft.Auxiliary.CustomSoundGuiButton.CustomSoundImagedGuiButton;
import Reika.ChromatiCraft.Container.ContainerBookPages;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiBookPages extends GuiContainer implements CustomSoundGui {

	private final EntityPlayer player;

	private int scroll;

	private ContainerBookPages inv;

	public GuiBookPages(EntityPlayer p5ep, int sc) {
		super(new ContainerBookPages(p5ep, sc));
		player = p5ep;
		scroll = sc;
		player.openContainer = inventorySlots;
		//cacheMouse = true;
		inv = ((ContainerBookPages)inventorySlots);
		inv.populate();
	}

	public void playButtonSound(GuiButton b) {
		ReikaSoundHelper.playClientSound(ChromaSounds.GUICLICK, player, 0.33F, 1);
	}

	public void playHoverSound(GuiButton b) {
		ReikaSoundHelper.playClientSound(ChromaSounds.GUISEL, player, 0.75F, 1);
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.clear();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		String tex = "Textures/GUIs/buttons.png";
		buttonList.add(new CustomSoundImagedGuiButton(0, j+148, k+4, 10, 10, 90, 16, tex, ChromatiCraft.class, this));
		buttonList.add(new CustomSoundImagedGuiButton(1, j+158, k+4, 10, 10, 90, 26, tex, ChromatiCraft.class, this));
	}

	@Override
	protected void actionPerformed(GuiButton b) {
		super.actionPerformed(b);
		this.initGui();
		this.scroll(b.id > 0);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2)
	{
		ReikaGuiAPI.instance.drawCenteredStringNoShadow(fontRendererObj, "Pages: "+inv.getPageCount()+"/"+(inv.getSize()-1), xSize/2, 6, 0xffffff);
		fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 96 + 2, 0xffffff);
	}

	private void scroll(boolean up) {
		scroll += up ? 1 : -1;
		scroll = MathHelper.clamp_int(scroll, 0, ContainerBookPages.MAX_SCROLL);
		inv.scroll(up);
		ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.BOOKINVSCROLL.ordinal(), player.worldObj, 0, 0, 0, up ? 1 : 0);
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
