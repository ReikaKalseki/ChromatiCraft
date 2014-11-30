/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI.Book;

import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Auxiliary.ChromaBookData;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.RecipesCastingTable;
import Reika.ChromatiCraft.Base.GuiBookSection;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;

public class GuiCastingRecipe extends GuiBookSection {

	private final ArrayList<CastingRecipe> recipes;
	private int index = 0;

	public GuiCastingRecipe(EntityPlayer ep, ItemStack out) {
		super(ep, null, 256, 220);
		recipes = RecipesCastingTable.instance.getAllRecipesMaking(out);
	}

	private CastingRecipe getActiveRecipe() {
		return recipes.get(index);
	}

	@Override
	public void initGui() {
		super.initGui();
	}

	@Override
	public void actionPerformed(GuiButton button) {
		super.actionPerformed(button);
		if (buttontimer > 0)
			return;
		buttontimer = 20;
		subpage = 0; //?
		//renderq = 22.5F;
		this.initGui();
	}

	@Override
	protected PageType getGuiLayout() {
		switch(subpage) {
		case 0:
			return PageType.CAST;
		case 1:
			return PageType.RUNES;
		case 2:
			return PageType.MULTICAST;
		case 3:
			return PageType.PYLONCAST;
		}
		return PageType.PLAIN;
	}

	private final void drawRecipes() {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		int posX = (width - xSize) / 2;
		int posY = (height - ySize) / 2;
		try {
			this.drawAuxData(posX, posY);
		}
		catch (Exception e) {
			ReikaChatHelper.write(Arrays.toString(e.getStackTrace()));
			e.printStackTrace();
		}
	}

	protected void drawAuxData(int posX, int posY) {
		ChromaBookData.drawCastingRecipe(fontRendererObj, ri, this.getActiveRecipe(), subpage, posX, posY);
	}

	private final void drawGraphics() {
		int posX = (width - xSize) / 2-2;
		int posY = (height - ySize) / 2-8;

		ReikaRenderHelper.disableLighting();
		int msx = ReikaGuiAPI.instance.getMouseRealX();
		int msy = ReikaGuiAPI.instance.getMouseRealY();

		this.drawAuxGraphics(posX, posY);
	}

	@Override
	public int getMaxSubpage() {
		return this.getActiveRecipe().type.ordinal();
	}

	protected void drawAuxGraphics(int posX, int posY) {
		//HandbookAuxData.drawGraphics((ChromaBook)this.getEntry(), posX, posY, xSize, ySize, fontRendererObj, ri, subpage);
	}

	@Override
	public final void drawScreen(int x, int y, float f)
	{
		super.drawScreen(x, y, f);

		int posX = (width - xSize) / 2;
		int posY = (height - ySize) / 2 - 8;

		//if (subpage == 0 && !disable)
		this.drawRecipes();

		this.drawGraphics();

		RenderHelper.disableStandardItemLighting();
	}

	@Override
	public String getPageTitle() {
		return "Casting";
	}

}
