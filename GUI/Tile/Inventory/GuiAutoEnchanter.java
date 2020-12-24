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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.GuiLetterSearchable;
import Reika.ChromatiCraft.Container.ContainerAutoEnchanter;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.TileEntity.Processing.TileEntityAutoEnchanter;
import Reika.DragonAPI.Instantiable.GUI.CustomSoundGuiButton;
import Reika.DragonAPI.Instantiable.GUI.CustomSoundGuiButton.CustomSoundImagedGuiButton;
import Reika.DragonAPI.Libraries.ReikaEnchantmentHelper;
import Reika.DragonAPI.Libraries.IO.ReikaLiquidRenderer;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

public class GuiAutoEnchanter extends GuiLetterSearchable<Enchantment> {

	private final TileEntityAutoEnchanter ench;

	private static final ArrayList<Enchantment> validEnchants = new ArrayList();

	static {
		for (int i = 0; i < Enchantment.enchantmentsList.length; i++) {
			Enchantment e = Enchantment.enchantmentsList[i];
			if (e != null && !TileEntityAutoEnchanter.isBlacklisted(e)) {
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
				ReikaPacketHelper.sendPacketToServer(ChromatiCraft.packetChannel, ChromaPackets.ENCHANTER.ordinal(), ench, this.getID(), 0, GuiScreen.isShiftKeyDown() ? 1 : 0);
				break;
			case 1:
				ReikaPacketHelper.sendPacketToServer(ChromatiCraft.packetChannel, ChromaPackets.ENCHANTER.ordinal(), ench, this.getID(), 1, GuiScreen.isShiftKeyDown() ? 1 : 0);
				break;
			case 10:
				ReikaPacketHelper.sendPacketToServer(ChromatiCraft.packetChannel, ChromaPackets.ENCHANTERRESET.ordinal(), ench);
				index = 0;
				break;
			case 2:
				this.decrementEnchant(GuiScreen.isShiftKeyDown(), GuiScreen.isCtrlKeyDown());
				break;
			case 3:
				this.incrementEnchant(GuiScreen.isShiftKeyDown(), GuiScreen.isCtrlKeyDown());
				break;
		}
		this.initGui();
	}

	private void incrementEnchant(boolean newType, boolean skipInvalid) {
		Enchantment pre = this.getActive();
		if (index < list.size()-1) {
			do {
				index++;
			} while(index < list.size()-1 && ((newType && this.getActive().type != pre.type) || (skipInvalid && !this.isValidForItem(this.getActive()))));
		}
	}

	private boolean isValidForItem(Enchantment e) {
		ItemStack is = ench.getStackInSlot(0);
		return is == null || ench.isEnchantValid(e, is, is.getItem(), false);
	}

	private void decrementEnchant(boolean newType, boolean skipInvalid) {
		Enchantment pre = this.getActive();
		if (index > 0) {
			do {
				index--;
			} while(index > 0 && ((newType && this.getActive().type != pre.type) || (skipInvalid && !this.isValidForItem(this.getActive()))));
		}
	}

	private String getEnchantDisplayString() {
		Enchantment e = this.getActive();
		int level = ench.getEnchantment(e);
		return level > 0 ? e.getTranslatedName(level) : StatCollector.translateToLocal(e.getName())+" 0";
	}

	private int getID() {
		return this.getActive().effectId;
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
		BlendMode.DEFAULT.apply();

		int level = ench.getLevel();
		if (level > 0) {
			Fluid f = FluidRegistry.getFluid("chroma");
			IIcon ico = ReikaLiquidRenderer.getFluidIconSafe(f);
			ReikaLiquidRenderer.bindFluidTexture(f);
			GL11.glColor3f(1, 1, 1);
			int h = 54 * level / ench.getCapacity();
			this.drawTexturedModelRectFromIcon(35, 68-h, ico, 16, h);
		}

		int amt = ench.getConsumedChroma();
		String sg = String.valueOf(amt/1000F);
		fontRendererObj.drawString("Cost:", 32-fontRendererObj.getStringWidth("Cost:"), 28, 0xffffff);
		fontRendererObj.drawString(sg, 32-fontRendererObj.getStringWidth(sg), 38, amt > ench.getCapacity() ? 0xff0000 : amt > ench.getChromaLevel() ? 0xffff00 : 0x00ff00);

		String display = this.getEnchantDisplayString();
		api.drawCenteredString(fontRendererObj, display, xSize/2, 75, 0xffffff);

		float w = GL11.glGetFloat(GL11.GL_LINE_WIDTH);
		GL11.glLineWidth(5);
		if (amt > ench.getCapacity()) {
			api.drawLine(35, 68-54, 51, 68, 0xffff0000);
			api.drawLine(35, 68, 51, 68-54, 0xffff0000);
		}
		else {
			int a = 54*amt/ench.getCapacity();
			api.drawLine(35, 68-a, 51, 68-a, 0xff707070);
		}
		GL11.glLineWidth(w);
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
		return "enchanter2";
	}

	@Override
	protected String getString(Enchantment val) {
		return val.getTranslatedName(1);
	}

	@Override
	protected boolean isIndexable(Enchantment val) {
		return true;
	}

	@Override
	protected Collection<Enchantment> getAllEntries(EntityPlayer ep) {
		Collection<Enchantment> c = new ArrayList();
		for (Enchantment e : validEnchants) {
			if (TileEntityAutoEnchanter.canPlayerGetEnchantment(e, ep))
				c.add(e);
		}
		return c;
	}

	@Override
	protected void sortEntries(ArrayList<Enchantment> li) {
		Collections.sort(li, ReikaEnchantmentHelper.enchantmentNameSorter);
	}

}
