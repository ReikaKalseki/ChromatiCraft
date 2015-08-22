/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI.Tile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

import org.lwjgl.input.Keyboard;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.CustomSoundGuiButton.CustomSoundImagedGuiButton;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.RecipesCastingTable;
import Reika.ChromatiCraft.Base.GuiChromaBase;
import Reika.ChromatiCraft.Container.ContainerCastingAuto;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Registry.ChromaResearchManager;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingAuto;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;

public class GuiCastingAuto extends GuiChromaBase {

	private static final List<ChromaResearch> list = new ArrayList();

	static {
		for (ChromaResearch r : ChromaResearch.getAllNonParents()) {
			if (r.isCrafting() && r.getRecipeCount() > 0) {
				list.add(r);
			}
		}
	}

	private int index = 0;
	//private int subindex = 0;

	private int number = 1;

	private final List<CastingRecipe> usableRecipes = new ArrayList();
	private final List<CastingRecipe> visible = new ArrayList();

	private final TileEntityCastingAuto tile;

	public GuiCastingAuto(TileEntityCastingAuto te, EntityPlayer ep) {
		super(new ContainerCastingAuto(te, ep), ep, te);
		ySize = 194;

		tile = te;

		Collection<CastingRecipe> recipes = te.getAvailableRecipes();//ChromaResearchManager.instance.getRecipesPerformed(ep);
		for (ChromaResearch r : list) {
			if (ChromaResearchManager.instance.playerHasFragment(ep, r)) {
				Collection<CastingRecipe> c = r.getCraftingRecipes();
				for (CastingRecipe cr : c) {
					if (recipes.contains(cr)) {
						usableRecipes.add(cr);
					}
				}
			}
		}

		this.filterRecipes();
		index = visible.contains(te.getCurrentRecipeOutput()) ? visible.indexOf(te.getCurrentRecipeOutput()) : 0;
	}

	private CastingRecipe getRecipe() {
		return !visible.isEmpty() ? visible.get(index) : null;
	}

	@Override
	public void initGui() {
		super.initGui();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		String tex = "Textures/GUIs/buttons.png";
		buttonList.add(new CustomSoundImagedGuiButton(0, j+xSize/2-40, k+20, 80, 10, 100, 16, tex, ChromatiCraft.class, this));
		buttonList.add(new CustomSoundImagedGuiButton(1, j+xSize/2-40, k+60, 80, 10, 100, 26, tex, ChromatiCraft.class, this));

		buttonList.add(new CustomSoundImagedGuiButton(3, j+xSize/2-80, k+35, 10, 10, 90, 16, tex, ChromatiCraft.class, this));
		buttonList.add(new CustomSoundImagedGuiButton(2, j+xSize/2-80, k+45, 10, 10, 90, 26, tex, ChromatiCraft.class, this));

		buttonList.add(new CustomSoundImagedGuiButton(4, j+xSize/2+55, k+45, 10, 10, 90, 6, tex, ChromatiCraft.class, this));
	}

	@Override
	public void updateScreen() {
		super.updateScreen();

		if (Minecraft.getMinecraft().theWorld.getTotalWorldTime()%5 == 0)
			this.filterRecipes();
	}

	private void filterRecipes() {
		visible.clear();

		Container c = Minecraft.getMinecraft().thePlayer.openContainer;
		if (c instanceof ContainerCastingAuto) {
			ContainerCastingAuto cc = (ContainerCastingAuto)c;
			for (CastingRecipe cr : usableRecipes) {
				if (cc.isRecipeValid(cr)) {
					visible.add(cr);
				}
			}
		}

		index = Math.min(index, visible.size()-1);
	}

	@Override
	protected void actionPerformed(GuiButton b) {
		super.actionPerformed(b);

		switch(b.id) {
		case 0:
			if (index > 0) {
				//subindex = 0;
				index--;
				number = 1;
			}
			break;
		case 1:
			if (index < visible.size()-1) {
				//subindex = 0;
				index++;
				number = 1;
			}
			break;

		case 2:
			if (number > 1)
				number -= this.getIncrement();
			if (number < 1)
				number = 1;
			break;
		case 3:
			number += this.getIncrement();
			break;

		case 4:
			if (this.getRecipe() != null)
				ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.AUTORECIPE.ordinal(), tile, RecipesCastingTable.instance.getIDForRecipe(this.getRecipe()), number);
			break;
		}
	}

	private int getIncrement() {
		return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ? 64 : Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) ? 16 : 1;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
	{
		super.drawGuiContainerBackgroundLayer(par1, par2, par3);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2)
	{
		super.drawGuiContainerForegroundLayer(par1, par2);

		CastingRecipe cr = this.getRecipe();
		if (cr != null) {
			//r.drawTabIcon(itemRender, 21, 33);
			//fontRendererObj.drawSplitString(r.getTitle(), 40, 36, 120, 0xffffff);

			fontRendererObj.drawSplitString(cr.getOutput().getDisplayName(), 24, 36, 130, 0xffffff);

			api.drawItemStack(itemRender, cr.getOutput(), 80, 75);
			fontRendererObj.drawString(String.format("x%d = %d", number, number*cr.getOutput().stackSize), 102, 79, 0xffffff);

			/*
			ItemHashMap<Integer> map = cr.getItemCounts();
			int dx = 6;
			int dy = 97;
			int c = 0;
			for (ItemStack is : map.keySet()) {
				int amt = map.get(is);
				api.drawItemStack(itemRender, is, dx, dy);
				fontRendererObj.drawString(String.format("x%d", amt), dx+18, dy+5, 0xffffff);
				c++;
				dy += 19;
				if (c%5 == 0) {
					dy = 97;
					dx += 42;
				}
			}
			 */
		}
	}

	@Override
	public String getGuiTexture() {
		return "automator2";
	}

}
