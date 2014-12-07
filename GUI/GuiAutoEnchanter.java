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

import java.util.ArrayList;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.GuiChromaBase;
import Reika.ChromatiCraft.Container.ContainerAutoEnchanter;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.TileEntity.Processing.TileEntityAutoEnchanter;
import Reika.DragonAPI.Instantiable.GUI.ImagedGuiButton;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaLiquidRenderer;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;

public class GuiAutoEnchanter extends GuiChromaBase {

	private TileEntityAutoEnchanter ench;
	private int selectedEnchant = 0;
	private static ArrayList<Enchantment> validEnchants = new ArrayList();
	static {
		for (int i = 0; i < Enchantment.enchantmentsList.length; i++) {
			Enchantment e = Enchantment.enchantmentsList[i];
			if (e != null) {
				validEnchants.add(e);
			}
		}
	}


	public GuiAutoEnchanter(EntityPlayer ep, TileEntityAutoEnchanter tile) {
		super(new ContainerAutoEnchanter(ep, tile), ep, tile);
		player = ep;
		ench = tile;
		ySize = 181;
	}

	@Override
	public void initGui() {
		super.initGui();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		int dx = 122;
		int dy = 33;
		int w = 4;
		buttonList.add(new GuiButton(0, j+dx-w, k+dy, 20, 20, "-"));
		buttonList.add(new GuiButton(1, j+dx+20+w, k+dy, 20, 20, "+"));

		buttonList.add(new ImagedGuiButton(2, j+16, k+72, 7, 14, 200, 200, this.getFullTexturePath(), ChromatiCraft.class));
		buttonList.add(new ImagedGuiButton(3, j+154, k+72, 7, 14, 200, 200, this.getFullTexturePath(), ChromatiCraft.class));
	}

	@Override
	protected void actionPerformed(GuiButton b) {
		switch (b.id) {
		case 0:
			ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.ENCHANTER.ordinal(), ench, this.getID(), 0);
			break;
		case 1:
			ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.ENCHANTER.ordinal(), ench, this.getID(), 1);
			break;
		case 2:
			if (selectedEnchant > 0)
				selectedEnchant--;
			break;
		case 3:
			if (selectedEnchant < validEnchants.size()-1)
				selectedEnchant++;
			break;
		}
		this.initGui();
	}

	private Enchantment getHighlightedEnchantment() {
		return validEnchants.get(selectedEnchant);
	}

	private String getEnchantDisplayString() {
		Enchantment e = this.getHighlightedEnchantment();
		int level = ench.getEnchantment(e);
		return level > 0 ? e.getTranslatedName(level) : StatCollector.translateToLocal(e.getName())+" 0";
	}

	private int getID() {
		return this.getHighlightedEnchantment().effectId;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		super.drawGuiContainerForegroundLayer(par1, par2);

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		int level = ench.getLevel();
		if (level > 0) {
			Fluid f = FluidRegistry.getFluid("chroma");
			IIcon ico = f.getStillIcon();
			ReikaLiquidRenderer.bindFluidTexture(f);
			GL11.glColor3f(1, 1, 1);
			int h = 54 * level / ench.getCapacity();
			this.drawTexturedModelRectFromIcon(35, 68-h, ico, 16, h);
		}

		String display = this.getEnchantDisplayString();
		ReikaGuiAPI.instance.drawCenteredString(fontRendererObj, display, xSize/2, 75, 0xffffff);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int a, int b) {
		super.drawGuiContainerBackgroundLayer(f, a, b);
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		int h = ench.getProgressScaled(46);
		this.drawTexturedModalRect(j+66, k+21, 179, 3, h, 44);
	}

	@Override
	public String getGuiTexture() {
		return "enchanter";
	}

}
