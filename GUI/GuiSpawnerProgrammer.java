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
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;

import org.lwjgl.input.Keyboard;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.GuiChromaBase;
import Reika.ChromatiCraft.Container.ContainerSpawnerProgrammer;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.TileEntity.TileEntitySpawnerReprogrammer;
import Reika.DragonAPI.Instantiable.GUI.ImagedGuiButton;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;

public class GuiSpawnerProgrammer extends GuiChromaBase {

	private TileEntitySpawnerReprogrammer prog;
	private int selectedMob;
	private static final ArrayList<String> validMobs = new ArrayList();
	static {
		for (Object key : EntityList.stringToClassMapping.keySet()) {
			String name = (String)key;
			if (TileEntitySpawnerReprogrammer.isMobAllowed(name)) {
				validMobs.add(name);
			}
		}
	}

	public GuiSpawnerProgrammer(EntityPlayer ep, TileEntitySpawnerReprogrammer tile) {
		super(new ContainerSpawnerProgrammer(ep, tile), tile);
		player = ep;
		prog = tile;
		ySize = 166;
		selectedMob = Math.max(validMobs.indexOf(tile.getSelectedMob()), 0);
		ReikaPacketHelper.sendStringPacket(ChromatiCraft.packetChannel, ChromaPackets.SPAWNERPROGRAM.ordinal(), this.getMobLabel(), prog);
	}

	@Override
	public void initGui() {
		super.initGui();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		int dx = 122;
		int dy = 33;
		int w = 4;

		buttonList.add(new ImagedGuiButton(0, j+16, k+52, 7, 14, 200, 200, this.getFullTexturePath(), ChromatiCraft.class));
		buttonList.add(new ImagedGuiButton(1, j+154, k+52, 7, 14, 200, 200, this.getFullTexturePath(), ChromatiCraft.class));
	}

	@Override
	public void actionPerformed(GuiButton b) {
		switch (b.id) {
		case 0:
			if (selectedMob > 0)
				selectedMob--;
			break;
		case 1:
			if (selectedMob < validMobs.size()-1)
				selectedMob++;
			break;
		}
		ReikaPacketHelper.sendStringPacket(ChromatiCraft.packetChannel, ChromaPackets.SPAWNERPROGRAM.ordinal(), this.getMobLabel(), prog);
		this.initGui();
	}

	private String getMobLabel() {
		return validMobs.get(selectedMob);
	}

	private String getMobDisplayName() {
		String label = this.getMobLabel();
		Class c = (Class)EntityList.stringToClassMapping.get(label);
		String f = EnumChatFormatting.WHITE.toString();
		if (EntityEnderman.class.isAssignableFrom(c) || EntityPigZombie.class.isAssignableFrom(c)) {
			f = EnumChatFormatting.GOLD.toString();
		}
		else if (ReikaEntityHelper.isHostile(c)) {
			f = EnumChatFormatting.RED.toString();
		}
		else if (EntityAnimal.class.isAssignableFrom(c)) {
			f = EnumChatFormatting.GREEN.toString();
		}
		return f+ReikaEntityHelper.getEntityDisplayName(label);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		super.drawGuiContainerForegroundLayer(par1, par2);

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		/*
		int level = prog.getLevel();
		if (level > 0) {
			Fluid f = FluidRegistry.getFluid("chroma");
			Icon ico = f.getStillIcon();
			ReikaLiquidRenderer.bindFluidTexture(f);
			GL11.glColor3f(1, 1, 1);
			int h = 54 * level / prog.getCapacity();
			this.drawTexturedModelRectFromIcon(35, 70-h, ico, 16, h);
		}*/

		String display = Keyboard.isKeyDown(Keyboard.KEY_TAB) ? this.getMobLabel() : this.getMobDisplayName();
		ReikaGuiAPI.instance.drawCenteredString(fontRenderer, display, xSize/2, 55, 0xffffff);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int a, int b) {
		super.drawGuiContainerBackgroundLayer(f, a, b);
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		int h = prog.getProgressScaled(78);
		this.drawTexturedModalRect(j+49, k+21, 177, 1, h, 16);
	}

	@Override
	public String getGuiTexture() {
		return "spawnerprogrammer";
	}

}
