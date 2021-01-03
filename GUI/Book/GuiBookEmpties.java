/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI.Book;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Container.ContainerBookEmpties;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.DragonAPI.Instantiable.GUI.CustomSoundGuiButton.CustomSoundGui;
import Reika.DragonAPI.Instantiable.GUI.CustomSoundGuiButton.CustomSoundImagedGuiButton;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Rendering.ReikaGuiAPI;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiBookEmpties extends GuiContainer implements CustomSoundGui {

	private final EntityPlayer player;

	private ContainerBookEmpties inv;

	public GuiBookEmpties(EntityPlayer p5ep) {
		super(new ContainerBookEmpties(p5ep));
		player = p5ep;
		player.openContainer = inventorySlots;
		//cacheMouse = true;
		inv = ((ContainerBookEmpties)inventorySlots);
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
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		ReikaGuiAPI.instance.drawCenteredStringNoShadow(fontRendererObj, "Pages: "+inv.getPageCount()+"/"+inv.getSize(), xSize/2, 6, 0xffffff);
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
