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

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.GuiChromaBase;
import Reika.ChromatiCraft.Container.ContainerTelePump;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.TileEntity.Acquisition.TileEntityTeleportationPump;
import Reika.DragonAPI.Instantiable.GUI.CustomSoundGuiButton.CustomSoundImagedGuiButton;
import Reika.DragonAPI.Libraries.IO.ReikaLiquidRenderer;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaEngLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

public class GuiTelePump extends GuiChromaBase {

	private TileEntityTeleportationPump pump;
	private int dy;
	private ArrayList<Fluid> list = new ArrayList();
	private int active = 0;

	public GuiTelePump(EntityPlayer ep, TileEntityTeleportationPump tile) {
		super(new ContainerTelePump(ep, tile), ep, tile);
		player = ep;
		pump = tile;
		list.addAll(tile.getFluids());
		xSize = 194;
		ySize = 168;
	}

	@Override
	public void initGui() {
		super.initGui();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		String tex = this.getFullTexturePath();
		buttonList.add(new CustomSoundImagedGuiButton(1000, j+7, k+15, 140, 8, 7, 15, tex, ChromatiCraft.class, this));
		buttonList.add(new CustomSoundImagedGuiButton(1001, j+7, k+63, 140, 8, 7, 63, tex, ChromatiCraft.class, this));

		//for (int i = 0; i < list.size(); i++) {
		//	Fluid f = list.get(i);
		buttonList.add(new CustomSoundImagedGuiButton(0, j+9, k+31, 136, 24, 0, 168, tex, ChromatiCraft.class, this));
		//}
	}

	@Override
	protected void actionPerformed(GuiButton b) {
		super.actionPerformed(b);

		if (b.id == 1000) {
			if (active > 0)
				this.scrollUp();
		}
		else if (b.id == 1001) {
			if (active < list.size()-1)
				this.scrollDown();
		}
		else if (!list.isEmpty()) {
			Fluid sel = list.get(active);
			ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.TELEPUMP.ordinal(), pump, sel.getID());
			player.closeScreen();
		}
		this.initGui();
	}

	private void scrollDown() {
		dy = 1;
		buttonList.clear();
	}

	private void scrollUp() {
		dy = -1;
		buttonList.clear();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		String s = this.getFullTexturePath();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, s);
		if (active < list.size()-1)
			this.drawTexturedModalRect(9, 55, 0, 168, 136, 6);
		if (active > 0)
			this.drawTexturedModalRect(9, 25, 0, 186, 136, 6);

		int h = pump.getLiquidScaled(16);
		Fluid f = pump.getTankFluid();
		if (f != null) {
			IIcon ico = f.getStillIcon();
			ReikaLiquidRenderer.bindFluidTexture(f);
			GL11.glColor3f(1, 1, 1);
			this.drawTexturedModelRectFromIcon(152, 16+16-h, ico, 16, h);
			//this.drawTexturedModalRect(j+152, k+31, 179, 3, h, 44);
		}

		int min = Math.max(0, active-1);
		int max = Math.min(list.size()-1, active+1);
		for (int i = min; i <= max; i++) {
			f = list.get(i);
			//ReikaJavaLibrary.pConsole(f.getName());
			IIcon ico = f.getStillIcon();
			GL11.glColor3f(1, 1, 1);
			int dy = 24*(i-active);
			ReikaLiquidRenderer.bindFluidTexture(f);
			this.drawTexturedModelRectFromIcon(13, 35+dy, ico, 16, 16);

			if (i == active) {
				double ct = pump.getFluidCount(f)/(double)FluidContainerRegistry.BUCKET_VOLUME;
				String sg = String.format("%s (%.3f%sB)", f.getLocalizedName(), ReikaMathLibrary.getThousandBase(ct), ReikaEngLibrary.getSIPrefix(ct));
				fontRendererObj.drawStringWithShadow(sg, 36, 39+dy, 0xffffff);
			}
		}

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, s);
		this.drawTexturedModalRect(7, 10, 7, 10, 140, 15);
		this.drawTexturedModalRect(7, 61, 7, 61, 140, 15);

		super.drawGuiContainerForegroundLayer(par1, par2);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int a, int b) {
		super.drawGuiContainerBackgroundLayer(f, a, b);
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		if (dy > 0) {
			dy++;
		}
		else if (dy < 0) {
			dy--;
		}

		//ReikaJavaLibrary.pConsole(dy);

		if (Math.abs(dy) >= 24) {
			active += Math.signum(dy);
			dy = 0;
			this.initGui();
		}
	}

	@Override
	public String getGuiTexture() {
		return "telepump3";
	}

}
