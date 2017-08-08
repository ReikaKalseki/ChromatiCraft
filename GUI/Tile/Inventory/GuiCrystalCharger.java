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

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.GuiChromaBase;
import Reika.ChromatiCraft.Container.ContainerCrystalCharger;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Auxiliary.TileEntityCrystalCharger;
import Reika.DragonAPI.Instantiable.GUI.ImagedGuiButton;
import Reika.DragonAPI.Instantiable.GUI.CustomSoundGuiButton.CustomSoundImagedGuiButton;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

public class GuiCrystalCharger extends GuiChromaBase {

	private final TileEntityCrystalCharger tile;

	public GuiCrystalCharger(EntityPlayer ep, TileEntityCrystalCharger te) {
		super(new ContainerCrystalCharger(ep, te), ep, te);

		tile = te;
		ySize = 191;
	}

	@Override
	public void initGui() {
		super.initGui();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		for (int i = 0; i < CrystalElement.elements.length; i++) {
			int dx = i%8 >= 4 ? 22 : 0;
			int x = j+6+18*(i%8)+dx-1;
			int y = k+17+40*(i/8)-1;
			ImagedGuiButton b = new CustomSoundImagedGuiButton(i, x, y, 18, 36, 0, 0, "Textures/buttons.png", ChromatiCraft.class, this);
			b.invisible = true;
			buttonList.add(b);
		}
	}

	@Override
	protected void actionPerformed(GuiButton b) {
		if (b.id >= 0 && b.id < 16)
			ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.CHARGERTOGGLE.ordinal(), tile, b.id);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		super.drawGuiContainerForegroundLayer(par1, par2);
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		int mx = api.getMouseRealX();
		int my = api.getMouseRealY();
		for (int i = 0; i < CrystalElement.elements.length; i++) {
			CrystalElement e = CrystalElement.elements[i];
			int max = tile.getMaxStorage(e);
			int dx = i%8 >= 4 ? 22 : 0;
			int x1 = j+6+18*(i%8)+dx;
			int x2 = x1+16;
			int y1 = k+17+40*(i/8);
			int y2 = y1+33;
			if (api.isMouseInBox(x1, x2, y1, y2)) {
				int level = tile.getEnergy(e);
				String s = String.format("%s: %d/%d", e.displayName, level, max);
				api.drawTooltipAt(fontRendererObj, s, mx-32, my);
			}

			int c = tile.isToggled(e) ? 0x00ff00 : 0xff0000;
			BlendMode.ADDITIVEDARK.apply();
			api.drawRectFrame(x1-j-1, y1-k-1, 18, 36, c);
			BlendMode.DEFAULT.apply();
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int a, int b) {
		super.drawGuiContainerBackgroundLayer(f, a, b);
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		GL11.glColor4f(1, 1, 1, 1);
		for (int i = 0; i < CrystalElement.elements.length; i++) {
			CrystalElement e = CrystalElement.elements[i];
			int px = tile.getEnergyScaled(e, 34);
			int dy = i < 8 ? 50 : 90;
			int dx = i%8 >= 4 ? 22 : 0;
			int x1 = 6+18*(i%8)+dx;
			//api.fillBar(j+x1, k+dy-33, 18, dy, e.color.color, px, 34, false);
			api.drawRect(j+x1, k+dy-px+1, 16, px, e.getColor(), true);
		}
	}

	@Override
	public String getGuiTexture() {
		return "charger";
	}

}
