/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface.AE;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.StatCollector;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Render.ChromaFontRenderer;
import Reika.ChromatiCraft.Base.GuiChromaBase;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.DragonAPI.Instantiable.GUI.ImagedGuiButton;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiMEDistributor extends GuiChromaBase
{
	private IInventory upperMEDistributorInventory;
	private TileEntityMEDistributor med;
	private final GuiTextField[] texts;

	public GuiMEDistributor(EntityPlayer p5ep, TileEntityMEDistributor te)
	{
		super(new ContainerMEDistributor(p5ep, te), p5ep, te);
		upperMEDistributorInventory = p5ep.inventory;
		med = te;

		xSize = 214;
		ySize = 215;

		texts = new GuiTextField[te.NSLOTS];
	}

	@Override
	public void initGui() {
		super.initGui();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		int w = 45;
		int h = 16;
		for (int i = 0; i < texts.length; i++) {
			int x = j+27+(i/5)*115;
			int y = k+16+(i%5)*20;
			texts[i] = new GuiTextField(ChromaFontRenderer.FontType.GUI.renderer, x, y, w, h);
			texts[i].setFocused(false);
			texts[i].setMaxStringLength(6);
			texts[i].setText(String.valueOf(med.getThreshold(i)));
		}

		String tex = "Textures/GUIs/buttons.png";

		for (int i = 0; i < med.NSLOTS; i++) {
			int x = j+94+16*(i*2/med.NSLOTS);
			int y = k+19+20*(i%(med.NSLOTS/2));
			int u = 70+med.getMode(i).ordinal()*10;
			buttonList.add(new ImagedGuiButton(i, x, y, 10, 10, u, 96, tex, ChromatiCraft.class).setTooltip("Match Mode"));
		}
	}

	@Override
	protected void actionPerformed(GuiButton b) {
		super.actionPerformed(b);

		if (b.id < med.NSLOTS) {
			med.toggleFuzzy(b.id);
			ReikaPacketHelper.sendPacketToServer(ChromatiCraft.packetChannel, ChromaPackets.MEDISTRIBFUZZY.ordinal(), med, b.id);
		}
		this.initGui();
	}

	@Override
	public void keyTyped(char c, int key) {
		super.keyTyped(c, key);
		for (int i = 0; i < texts.length; i++) {
			if (texts[i].isFocused())
				texts[i].textboxKeyTyped(c, key);
		}
	}

	@Override
	public void mouseClicked(int x, int y, int b) {
		super.mouseClicked(x, y, b);
		for (int i = 0; i < texts.length; i++) {
			texts[i].mouseClicked(x, y, b);

			this.parseAndSend(i);
		}
	}

	@Override
	public void updateScreen() {
		super.updateScreen();

		for (int i = 0; i < texts.length; i++) {
			if (texts[i].isFocused()) {
				this.parseAndSend(i);
			}
		}
	}

	private void parseAndSend(int slot) {
		int data = ReikaJavaLibrary.safeIntParse(texts[slot].getText());
		data = Math.max(data, 0);
		ReikaPacketHelper.sendPacketToServer(ChromatiCraft.packetChannel, ChromaPackets.MEDISTRIBTHRESH.ordinal(), med, slot, data);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
	{
		super.drawGuiContainerBackgroundLayer(par1, par2, par3);

		for (int i = 0; i < texts.length; i++) {
			texts[i].setTextColor(med.getMode(i).color);
			texts[i].drawTextBox();
		}
	}

	@Override
	public boolean labelInventory() {
		return true;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		super.drawGuiContainerForegroundLayer(par1, par2);

		fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), xSize-76, (ySize - 97) + 3, 0xffffff);
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		int w = 67;
		int w2 = 115;
		for (int i = 0; i < med.NSLOTS; i++) {
			int dx = 8;
			if (i >= 5)
				dx += w2;
			int dy = 7+(i%5)*20+9;
			api.drawItemStack(itemRender, fontRendererObj, med.getMapping(i), dx+w, dy);
			api.drawItemStack(itemRender, fontRendererObj, med.getMapping(i+med.NSLOTS), dx, dy);
			String s = null;
			if (api.isMouseInBox(j+dx-1, j+dx-1+17, k+dy-1, k+dy+18-1)) {
				s = "Query";
			}
			else if (api.isMouseInBox(j+dx+w-1, j+dx+w+16, k+dy-1, k+dy+18-1)) {
				s = "Transfer";
			}
			else if (api.isMouseInBox(j+dx-1+18, j+dx+w-2, k+dy-1, k+dy+18-1)) {
				s = med.getMode(i).desc;
			}
			if (s != null)
				api.drawTooltipAt(fontRendererObj, s, api.getMouseRealX()+fontRendererObj.getStringWidth(s)-80, api.getMouseRealY()+12);
		}
	}

	@Override
	public String getGuiTexture() {
		return "medistributor";
	}
}
