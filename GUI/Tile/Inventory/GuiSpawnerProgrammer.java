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

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.GuiLetterSearchable;
import Reika.ChromatiCraft.Container.ContainerSpawnerProgrammer;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.TileEntity.Processing.TileEntitySpawnerReprogrammer;
import Reika.DragonAPI.DragonOptions;
import Reika.DragonAPI.Instantiable.GUI.CustomSoundGuiButton.CustomSoundImagedGuiButton;
import Reika.DragonAPI.Instantiable.GUI.FractionalBar;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;

public class GuiSpawnerProgrammer extends GuiLetterSearchable<String> {

	private TileEntitySpawnerReprogrammer prog;
	private static final ArrayList<String> validMobs = new ArrayList();
	private Pages page = Pages.MOBTYPE;

	private Setting minDelay = new Setting(200);
	private Setting maxDelay = new Setting(800);
	private Setting maxNearMobs = new Setting(6);
	private Setting spawnCount = new Setting(4);
	private Setting spawnRange = new Setting(4);
	private Setting activationRange = new Setting(16);

	static {
		for (Object key : EntityList.stringToClassMapping.keySet()) {
			String name = (String)key;
			if (TileEntitySpawnerReprogrammer.isMobAllowed(name)) {
				validMobs.add(name);
			}
		}
		Collections.sort(validMobs);
	}

	public GuiSpawnerProgrammer(EntityPlayer ep, TileEntitySpawnerReprogrammer tile) {
		super(new ContainerSpawnerProgrammer(ep, tile), ep, tile);
		player = ep;
		prog = tile;
		ySize = 190;//166;
		int[] data = tile.getData();
		minDelay.currentValue = data[0];
		maxDelay.currentValue = data[1];
		maxNearMobs.currentValue = data[2];
		spawnCount.currentValue = data[3];
		spawnRange.currentValue = data[4];
		activationRange.currentValue = data[5];
		index = Math.max(list.indexOf(tile.getSelectedMob()), 0);
		ReikaPacketHelper.sendStringPacket(ChromatiCraft.packetChannel, ChromaPackets.SPAWNERPROGRAM.ordinal(), this.getActive(), prog);
	}

	@Override
	public void initGui() {
		super.initGui();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		//int dx = 122;
		//int dy = 33;
		//int w = 4;

		switch(page) {
			case MOBTYPE:
				buttonList.add(new CustomSoundImagedGuiButton(0, j+16, k+44, 7, 14, 86, 191, this.getFullTexturePath(), ChromatiCraft.class, this));
				buttonList.add(new CustomSoundImagedGuiButton(1, j+154, k+44, 7, 14, 224, 191, this.getFullTexturePath(), ChromatiCraft.class, this));
				break;
			case TIMER:
				minDelay.bar = new FractionalBar(j+xSize/2-64+34, k+55, 100, 6, TileEntitySpawnerReprogrammer.MIN_MINDELAY, 18000, minDelay.currentValue); //10s to 15 min
				maxDelay.bar = new FractionalBar(j+xSize/2-64+34, k+85, 100, 6, TileEntitySpawnerReprogrammer.MIN_MINDELAY, 18000, maxDelay.currentValue);
				break;
			case COUNTS:
				maxNearMobs.bar = new FractionalBar(j+xSize/2-64+34, k+55, 100, 6, 0, 16, maxNearMobs.currentValue);
				spawnCount.bar = new FractionalBar(j+xSize/2-64+34, k+85, 100, 6, 1, 16, spawnCount.currentValue);
				break;
			case RANGES:
				spawnRange.bar = new FractionalBar(j+xSize/2-64+34, k+55, 100, 6, 1, 32, spawnRange.currentValue);
				activationRange.bar = new FractionalBar(j+xSize/2-64+34, k+85, 100, 6, 1, 128, activationRange.currentValue);
				break;
		}
		buttonList.add(new CustomSoundImagedGuiButton(2, j+6, k+63, 20, 20, 178+(page == Pages.MOBTYPE ? 20 : 0), 40, this.getFullTexturePath(), ChromatiCraft.class, this));
		buttonList.add(new CustomSoundImagedGuiButton(3, j+6, k+83, 20, 20, 178+(page == Pages.TIMER ? 20 : 0), 60, this.getFullTexturePath(), ChromatiCraft.class, this));
		buttonList.add(new CustomSoundImagedGuiButton(4, j+26, k+63, 20, 20, 178+(page == Pages.COUNTS ? 20 : 0), 80, this.getFullTexturePath(), ChromatiCraft.class, this));
		buttonList.add(new CustomSoundImagedGuiButton(5, j+26, k+83, 20, 20, 178+(page == Pages.RANGES ? 20 : 0), 100, this.getFullTexturePath(), ChromatiCraft.class, this));
	}

	@Override
	protected void mouseClicked(int x, int y, int b) {
		super.mouseClicked(x, y, b);
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		boolean flag = false;
		if (page == Pages.TIMER) {
			if (minDelay.bar.handleClick(x, y, b)) {
				minDelay.read();
				flag = true;
			}
			if (maxDelay.bar.handleClick(x, y, b)) {
				maxDelay.read();
				flag = true;
			}
		}
		if (page == Pages.COUNTS) {
			if (maxNearMobs.bar.handleClick(x, y, b)) {
				maxNearMobs.read();
				flag = true;
			}
			if (spawnCount.bar.handleClick(x, y, b)) {
				spawnCount.read();
				flag = true;
			}
		}
		if (page == Pages.RANGES) {
			if (spawnRange.bar.handleClick(x, y, b)) {
				spawnRange.read();
				flag = true;
			}
			if (activationRange.bar.handleClick(x, y, b)) {
				activationRange.read();
				flag = true;
			}
		}
		if (flag) {
			this.sendData();
			ReikaSoundHelper.playClientSound(ChromaSounds.GUICLICK, Minecraft.getMinecraft().thePlayer, 1, 0.8F);
		}
	}

	private void sendData() {
		ReikaPacketHelper.sendPacketToServer(ChromatiCraft.packetChannel, ChromaPackets.SPAWNERDATA.ordinal(), prog, minDelay.currentValue, maxDelay.currentValue, maxNearMobs.currentValue, spawnCount.currentValue, spawnRange.currentValue, activationRange.currentValue);
	}

	@Override
	protected void actionPerformed(GuiButton b) {
		switch (b.id) {
			case 0:
				this.decrIndex();
				ReikaPacketHelper.sendStringPacket(ChromatiCraft.packetChannel, ChromaPackets.SPAWNERPROGRAM.ordinal(), this.getActive(), prog);
				break;
			case 1:
				this.incrIndex();
				ReikaPacketHelper.sendStringPacket(ChromatiCraft.packetChannel, ChromaPackets.SPAWNERPROGRAM.ordinal(), this.getActive(), prog);
				break;
			case 2:
			case 3:
			case 4:
			case 5:
				page = Pages.list[b.id-2];
				break;
		}
		this.initGui();
	}

	private String getMobDisplayName() {
		String label = this.getActive();
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

		if (page == Pages.MOBTYPE) {
			boolean debug = Keyboard.isKeyDown(DragonOptions.DEBUGKEY.getValue());
			String display = debug ? this.getActive() : this.getMobDisplayName();
			ReikaGuiAPI.instance.drawCenteredString(fontRendererObj, display, xSize/2, 47, debug ? 0xffff00 : 0xffffff);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int a, int b) {
		super.drawGuiContainerBackgroundLayer(f, a, b);
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		int h = prog.getProgressScaled(78);
		this.drawTexturedModalRect(j+49, k+21, 2, 192, h, 16);

		switch(page) {
			case MOBTYPE:
				this.drawTexturedModalRect(j+22, k+43, 92, 190, 132, 16);
				break;
			case TIMER:
				minDelay.bar.draw(this, 92, 210, 104, 6, 94, 218, 6, 2);
				maxDelay.bar.draw(this, 92, 210, 104, 6, 94, 218, 6, 2);
				minDelay.bar.drawTitle(fontRendererObj, "Min Delay", 0xffffff);
				maxDelay.bar.drawTitle(fontRendererObj, "Max Delay", 0xffffff);
				break;
			case COUNTS:
				maxNearMobs.bar.draw(this, 92, 210, 104, 6, 94, 218, 6, 2);
				spawnCount.bar.draw(this, 92, 210, 104, 6, 94, 218, 6, 2);
				maxNearMobs.bar.drawTitle(fontRendererObj, "Max Near Mobs", 0xffffff);
				spawnCount.bar.drawTitle(fontRendererObj, "Spawn Count", 0xffffff);
				break;
			case RANGES:
				spawnRange.bar.draw(this, 92, 210, 104, 6, 94, 218, 6, 2);
				activationRange.bar.draw(this, 92, 210, 104, 6, 94, 218, 6, 2);
				spawnRange.bar.drawTitle(fontRendererObj, "Spawn Range", 0xffffff);
				activationRange.bar.drawTitle(fontRendererObj, "Activation Range", 0xffffff);
				break;
		}
	}

	@Override
	public String getGuiTexture() {
		return "spawnerprogrammer2";
	}

	@Override
	protected String getString(String val) {
		return ReikaEntityHelper.getEntityDisplayName(val);
	}

	@Override
	protected boolean isIndexable(String val) {
		return true;
	}

	@Override
	protected Collection<String> getAllEntries(EntityPlayer ep) {
		return validMobs;
	}

	@Override
	protected void sortEntries(ArrayList<String> li) {
		Collections.sort(li, ReikaEntityHelper.entityByDisplayComparator);
	}

	private static enum Pages {
		MOBTYPE(),
		TIMER(),
		COUNTS(),
		RANGES();

		private static Pages[] list = values();
	}

	private static class Setting {

		private final int defaultValue;
		private int currentValue;
		private FractionalBar bar;

		private Setting(int val) {
			defaultValue = currentValue = val;
		}

		public void read() {
			currentValue = bar.getCurrentValue();
		}

	}

}
