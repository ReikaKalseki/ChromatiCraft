/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI.Book;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import Reika.ChromatiCraft.Auxiliary.CustomSoundGuiButton;
import Reika.ChromatiCraft.Base.GuiBookSection;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Instantiable.Data.Maps.ItemHashMap;
import Reika.DragonAPI.Instantiable.Rendering.StructureRenderer;
import Reika.DragonAPI.Instantiable.Rendering.StructureRenderer.BlockRenderHook;

public class GuiStructure extends GuiBookSection {

	private int mode = 0;
	private int tick = 0;

	private final FilledBlockArray array;
	private final StructureRenderer render;

	public GuiStructure(EntityPlayer ep, ChromaResearch r) {
		super(ep, r, 256, 220, false);

		array = page.getStructure().getStructureForDisplay();
		if (page.name().toLowerCase().contains("casting")) {
			array.setBlock(array.getMidX(), array.getMinY()+1, array.getMidZ(), ChromaTiles.TABLE.getBlock(), ChromaTiles.TABLE.getBlockMetadata());
		}
		render = new StructureRenderer(array);
		if (page.name().toLowerCase().contains("pylon")) {
			render.addOverride(array.getMidX(), array.getMinY()+9, array.getMidZ(), ChromaTiles.PYLON.getCraftedProduct());
		}
		render.addRenderHook(ChromaTiles.PYLON.getCraftedProduct(), new PylonRenderHook());
	}

	@Override
	public void initGui() {
		super.initGui();
		render.resetRotation();
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		buttonList.add(new CustomSoundGuiButton(0, j+185, k-2, 20, 20, "3D", this));
		buttonList.add(new CustomSoundGuiButton(1, j+205, k-2, 20, 20, "2D", this));
		buttonList.add(new CustomSoundGuiButton(4, mode == 1 ? j+125 : j+165, k-2, 20, 20, "N#", this));


		if (mode == 1) {
			buttonList.add(new CustomSoundGuiButton(2, j+165, k-2, 20, 20, "+", this));
			buttonList.add(new CustomSoundGuiButton(3, j+145, k-2, 20, 20, "-", this));
		}
	}

	@Override
	protected void actionPerformed(GuiButton b) {
		super.actionPerformed(b);

		if (b.id == 0) {
			mode = 0;
			render.reset();
			this.initGui();
		}
		else if (b.id == 1) {
			mode = 1;
			this.initGui();
		}
		else if (b.id == 2) {
			render.incrementStepY();
			this.initGui();
		}
		else if (b.id == 3) {
			render.decrementStepY();
			this.initGui();
		}
		else if (b.id == 4 ) {
			mode = 2;
			this.initGui();
		}
	}

	@Override
	protected int getMaxSubpage() {
		return 0;
	}

	@Override
	protected PageType getGuiLayout() {
		return PageType.STRUCT;
	}

	@Override
	public final void drawScreen(int mx, int my, float f) {
		super.drawScreen(mx, my, f);

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		tick++;

		//FilledBlockArray arr = page.getStructure().getStructureForDisplay();

		switch(mode) {
			case 0:
				this.draw3d(j, k);
				break;
			case 1:
				this.drawSlice(j, k);
				break;
			case 2:
				this.drawTally(j, k);
				break;
		}
	}

	private void drawSlice(int j, int k) {
		render.drawSlice(j, k);
	}

	private void drawTally(int j, int k) {
		ItemHashMap<Integer> map = array.tally();
		int i = 0;
		int n = 8;
		for (ItemStack is : map.keySet()) {
			int dx = j+10+(i/n)*50;
			int dy = k+30+(i%n)*22;
			ItemStack is2 = is.copy();
			if (ChromaBlocks.CHROMA.match(is)) {
				is2 = ChromaItems.BUCKET.getStackOfMetadata(0);
			}
			api.drawItemStackWithTooltip(itemRender, fontRendererObj, is2, dx, dy);
			fontRendererObj.drawString(String.valueOf(map.get(is)), dx+20, dy+5, 0xffffff);
			i++;
		}
	}

	private void draw3d(int j, int k) {
		if (Mouse.isButtonDown(0) && tick > 2) {
			render.rotate(0.25*Mouse.getDY(), 0.25*Mouse.getDX(), 0);
		}
		else if (Mouse.isButtonDown(1)) {
			render.resetRotation();
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			render.rotate(0, 0.75, 0);
		}
		else if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			render.rotate(0, -0.75, 0);
		}
		else if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			render.rotate(-0.75, 0, 0);
		}
		else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			render.rotate(0.75, 0, 0);
		}

		render.draw3D(j, k);
	}

	private static class PylonRenderHook implements BlockRenderHook {

		@Override
		public double getScale() {
			return 2;
		}

		@Override
		public int getOffsetX() {
			return -4;
		}

		@Override
		public int getOffsetY() {
			return -6;
		}

	}

}
