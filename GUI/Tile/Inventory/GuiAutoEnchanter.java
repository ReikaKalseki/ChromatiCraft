/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI.Tile.Inventory;

import java.util.ArrayList;
import java.util.Collections;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.CustomSoundGuiButton;
import Reika.ChromatiCraft.Auxiliary.CustomSoundGuiButton.CustomSoundImagedGuiButton;
import Reika.ChromatiCraft.Base.GuiChromaBase;
import Reika.ChromatiCraft.Container.ContainerAutoEnchanter;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.TileEntity.Processing.TileEntityAutoEnchanter;
import Reika.DragonAPI.Libraries.ReikaEnchantmentHelper;
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

		Collections.sort(validEnchants, ReikaEnchantmentHelper.enchantmentNameSorter);
		//Collections.sort(validEnchants, ReikaEnchantmentHelper.enchantmentTypeSorter);
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
		buttonList.add(new CustomSoundGuiButton(0, j+dx-w, k+dy-13, 20, 20, "-", this));
		buttonList.add(new CustomSoundGuiButton(1, j+dx+20+w, k+dy-13, 20, 20, "+", this));

		buttonList.add(new CustomSoundGuiButton(10, j+dx-w, k+dy+20+5-13, 48, 20, "Reset", this));

		buttonList.add(new CustomSoundImagedGuiButton(2, j+16, k+72, 7, 14, 200, 200, this.getFullTexturePath(), ChromatiCraft.class, this));
		buttonList.add(new CustomSoundImagedGuiButton(3, j+154, k+72, 7, 14, 200, 200, this.getFullTexturePath(), ChromatiCraft.class, this));
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
			case 10:
				ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.ENCHANTERRESET.ordinal(), ench);
				selectedEnchant = 0;
				break;
			case 2:
				this.decrementEnchant(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT));
				break;
			case 3:
				this.incrementEnchant(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT));
				break;
		}
		this.initGui();
	}

	private void incrementEnchant(boolean newType) {
		Enchantment pre = this.getHighlightedEnchantment();
		if (selectedEnchant < validEnchants.size()-1) {
			do {
				selectedEnchant++;
			} while(newType && selectedEnchant < validEnchants.size()-1 && this.getHighlightedEnchantment().type != pre.type);
		}
	}

	private void decrementEnchant(boolean newType) {
		Enchantment pre = this.getHighlightedEnchantment();
		if (selectedEnchant > 0) {
			do {
				selectedEnchant--;
			} while(newType && selectedEnchant > 0 && this.getHighlightedEnchantment().type != pre.type);
		}
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
	protected int getTitlePosition() {
		return 4;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		super.drawGuiContainerForegroundLayer(par1, par2);

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		GL11.glDisable(GL11.GL_BLEND);

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

		GL11.glEnable(GL11.GL_BLEND);
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
