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

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.GuiChromaBase;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityLampController;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityLampController.Control;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.CoreContainer;
import Reika.DragonAPI.Instantiable.GUI.CustomSoundGuiButton;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import cpw.mods.fml.common.registry.GameRegistry;

public class GuiLampController extends GuiChromaBase {

	private int channel;
	private GuiTextField input;

	private TileEntityLampController lamp;
	private Control control;

	private int lockout = 0;

	public GuiLampController(EntityPlayer ep, TileEntityLampController te) {
		super(new CoreContainer(ep, te), ep, te);

		lamp = te;

		ySize = 83;
		channel = te.getChannel();
		control = te.getControlType();
	}

	@Override
	public void initGui() {
		super.initGui();
		int j = (width - xSize) / 2+8;
		int k = (height - ySize) / 2 - 12;
		input = new GuiTextField(fontRendererObj, j+xSize/2-6, k+33, 26, 16);
		input.setFocused(false);
		input.setMaxStringLength(3);

		if (control == Control.MANUAL) {
			buttonList.add(new CustomSoundGuiButton(1, j+85, k+60, 60, 20, "Toggle", this));
		}

		buttonList.add(new CustomSoundGuiButton(0, j+15, k+60, 20, 20, "", this));
	}

	@Override
	protected void actionPerformed(GuiButton b) {
		super.actionPerformed(b);

		if (b.id == 0 && lockout == 0) {
			ReikaPacketHelper.sendPacketToServer(ChromatiCraft.packetChannel, ChromaPackets.LAMPCONTROL.ordinal(), lamp, 1, 0);
			control = control.next();
		}
		else if (b.id == 1) {
			ReikaPacketHelper.sendPacketToServer(ChromatiCraft.packetChannel, ChromaPackets.LAMPCONTROL.ordinal(), lamp, 2, 0);
		}
		lockout = 20;
		this.initGui();
	}

	@Override
	protected void keyTyped(char c, int i){
		super.keyTyped(c, i);
		input.textboxKeyTyped(c, i);
	}

	@Override
	protected void mouseClicked(int i, int j, int k){
		super.mouseClicked(i, j, k);
		input.mouseClicked(i, j, k);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		if (input.getText().isEmpty()) {
			return;
		}
		if (!(input.getText().matches("^[0-9 ]+$"))) {
			channel = 0;
			input.deleteFromCursor(-1);
			ReikaPacketHelper.sendPacketToServer(ChromatiCraft.packetChannel, ChromaPackets.LAMPCONTROL.ordinal(), lamp, 0, channel);
			return;
		}
		channel = Integer.parseInt(input.getText());
		if (channel >= 0) {
			ReikaPacketHelper.sendPacketToServer(ChromatiCraft.packetChannel, ChromaPackets.LAMPCONTROL.ordinal(), lamp, 0, channel);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		super.drawGuiContainerForegroundLayer(par1, par2);

		fontRendererObj.drawString("Channel:", xSize/2-72, 25, 0xffffff);
		if (!input.isFocused()) {
			fontRendererObj.drawString(String.format("%d", lamp.getChannel()), xSize/2+6, 25, 0xffffffff);
		}

		Control c = lamp.getControlType();
		ItemStack is = null;
		switch(c) {
			case MANUAL:
				break;
			case REDSTONE:
				is = new ItemStack(Items.redstone);
				break;
			case RFSTORAGE:
				is = GameRegistry.findItemStack(ModList.THERMALEXPANSION.modLabel, "powerCoilElectrum", 1);
				break;
			case SHAFTPOWER:
				is = GameRegistry.findItemStack(ModList.ROTARYCRAFT.modLabel, "rotarycraft_item_shaftcraft", 1);
				if (is != null)
					is.setItemDamage(2);
				break;
		}
		if (is != null)
			api.drawItemStack(itemRender, is, 25, 49);
		if (lockout > 0)
			lockout--;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int a, int b) {
		super.drawGuiContainerBackgroundLayer(f, a, b);
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		input.drawTextBox();
	}

	@Override
	public String getGuiTexture() {
		return "lampcontrol";
	}

}
