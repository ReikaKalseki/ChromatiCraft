/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI.Tile;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.GuiLetterSearchable;
import Reika.ChromatiCraft.Container.ContainerFluidRelay;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.TileEntity.Transport.TileEntityFluidRelay;
import Reika.DragonAPI.Instantiable.GUI.CustomSoundGuiButton.CustomSoundImagedGuiButton;
import Reika.DragonAPI.Instantiable.GUI.CustomSoundGuiButton.CustomSoundImagedGuiButtonSneakIcon;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Rendering.ReikaLiquidRenderer;


public class GuiFluidRelay extends GuiLetterSearchable<Fluid> {

	private final TileEntityFluidRelay relay;

	private int lastSelSlot;

	public GuiFluidRelay(EntityPlayer ep, TileEntityFluidRelay te) {
		super(new ContainerFluidRelay(ep, te), ep, te);
		relay = te;

		ySize = 153;
	}

	@Override
	public void initGui() {
		super.initGui();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		String tex = "Textures/GUIs/buttons.png";
		int in = 22;
		int iny = 45;
		int dy = 5;
		buttonList.add(new CustomSoundImagedGuiButtonSneakIcon(0, j+in, k+iny-dy, 10, 10, 100, 66, tex, ChromatiCraft.class, this, 100, 86));
		buttonList.add(new CustomSoundImagedGuiButtonSneakIcon(1, j+xSize-10-in, k+iny-dy, 10, 10, 100, 56, tex, ChromatiCraft.class, this, 100, 76));

		buttonList.add(new CustomSoundImagedGuiButtonSneakIcon(2, j+in, k+iny+dy, 10, 10, 100, 66, tex, ChromatiCraft.class, this, 100, 86));
		buttonList.add(new CustomSoundImagedGuiButtonSneakIcon(3, j+xSize-10-in, k+iny+dy, 10, 10, 100, 56, tex, ChromatiCraft.class, this, 100, 76));


		int dx = 14;
		buttonList.add(new CustomSoundImagedGuiButton(4, j+in-dx, k+iny, 10, 10, 90, 56, tex, ChromatiCraft.class, this));
		buttonList.add(new CustomSoundImagedGuiButton(5, j+xSize-10-in+dx, k+iny-dy, 10, 10, 90, 76, tex, ChromatiCraft.class, this).setTooltip("Auto"));

		buttonList.add(new CustomSoundImagedGuiButton(6, j+xSize-10-in+dx, k+iny+dy, 10, 10, 90, relay.autoFilter ? 86 : 56, tex, ChromatiCraft.class, this));
	}

	@Override
	protected void actionPerformed(GuiButton b) {
		super.actionPerformed(b);
		int delta1 = 0;
		int delta2 = 0;
		int n = GuiScreen.isCtrlKeyDown() ? 1 : (GuiScreen.isShiftKeyDown() ? 100 : 10);
		switch(b.id) {
			case 0:
				delta1 = -n;
				break;
			case 1:
				delta1 = n;
				break;
			case 2:
				delta2 = -n;
				break;
			case 3:
				delta2 = n;
				break;
			case 4:
				ReikaPacketHelper.sendPacketToServer(ChromatiCraft.packetChannel, ChromaPackets.RELAYCLEAR.ordinal(), relay);
				break;
			case 5:
				ReikaPacketHelper.sendPacketToServer(ChromatiCraft.packetChannel, ChromaPackets.RELAYCOPY.ordinal(), relay);
				break;
			case 6:
				relay.autoFilter = !relay.autoFilter;
				ReikaPacketHelper.sendPacketToServer(ChromatiCraft.packetChannel, ChromaPackets.RELAYAUTO.ordinal(), relay);
				break;
		}
		if (delta1 != 0) {
			ReikaPacketHelper.sendPacketToServer(ChromatiCraft.packetChannel, ChromaPackets.RELAYPRESSUREBASE.ordinal(), relay, delta1);
		}
		if (delta2 != 0) {
			ReikaPacketHelper.sendPacketToServer(ChromatiCraft.packetChannel, ChromaPackets.RELAYPRESSUREVAR.ordinal(), relay, delta2);
		}
		this.initGui();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		super.drawGuiContainerForegroundLayer(par1, par2);

		this.drawSearch();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f0, int a, int b) {
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		super.drawGuiContainerBackgroundLayer(f0, a, b);


		Fluid[] types = relay.getFluidTypes();
		int n = types.length;
		/*
		for (int i = n+1; i < 7; i++) {
			int x = j+14+i*22;
			int y = k+17;
			api.drawTexturedModalRect(x, y, 179, 0, 16, 16);
		}*/
		ReikaTextureHelper.bindTerrainTexture();
		int mansel = ((ContainerFluidRelay)inventorySlots).getManualSelectSlot();
		if (mansel != lastSelSlot)
			this.resetFilter(true);
		for (int i = 0; i < n; i++) {
			int x = j+14+i*22;
			int y = k+17;
			Fluid f = types[i];
			if (i == mansel) {
				int c = ReikaColorAPI.mixColors(0x77d0ff, 0x22aaff, 0.5F+0.5F*MathHelper.sin((System.currentTimeMillis()%1000000)/250F));
				api.drawRectFrame(x-2, y-2, 19, 19, c);
				f = this.getActive();
			}
			if (f != null) {
				api.drawTexturedModelRectFromIcon(x, y, ReikaLiquidRenderer.getFluidIconSafe(f), 16, 16);

				if (api.isMouseInBox(x, x+16, y, y+16)) {
					api.drawTooltip(fontRendererObj, this.getString(f));
				}
			}
		}

		String s = String.format("Pressure: %d + %d/B", relay.getBasePressure(), relay.getFunctionPressure());
		api.drawCenteredStringNoShadow(fontRendererObj, s, j+xSize/2, k+45, 0xffffff);

		lastSelSlot = mansel;
	}

	@Override
	public String getGuiTexture() {
		return "fluidrelay";
	}

	@Override
	protected String getString(Fluid f) {
		return f.getLocalizedName();
	}

	@Override
	protected Collection<Fluid> getAllEntries(EntityPlayer ep) {
		return FluidRegistry.getRegisteredFluids().values();
	}

	@Override
	protected void sortEntries(ArrayList<Fluid> li) {
		li.sort((f1, f2) -> Integer.compare(f1.getID(), f2.getID()));
	}

	@Override
	protected boolean isSearchActive() {
		return super.isSearchActive() && ((ContainerFluidRelay)inventorySlots).getManualSelectSlot() >= 0;
	}

	@Override
	protected void onSelected(Fluid f) {
		((ContainerFluidRelay)inventorySlots).setFluid(f);
	}

}
