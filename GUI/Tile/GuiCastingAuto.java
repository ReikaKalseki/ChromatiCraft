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
import java.util.Collections;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaBookData;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.RecipeComparator;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.RecipesCastingTable;
import Reika.ChromatiCraft.Base.GuiChromaBase;
import Reika.ChromatiCraft.Container.ContainerCastingAuto;
import Reika.ChromatiCraft.Items.ItemChromaPlacer;
import Reika.ChromatiCraft.Items.Tools.ItemPendant;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Registry.ChromaResearchManager;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingAuto;
import Reika.DragonAPI.Instantiable.GUI.CustomSoundGuiButton.CustomSoundImagedGuiButton;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class GuiCastingAuto extends GuiChromaBase {

	public static CastingRecipe lexiconSelectedRecipe = null;

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
		xSize = 224;
		ySize = 227;

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
		if (lexiconSelectedRecipe != null)
			return lexiconSelectedRecipe;
		return index >= 0 && !visible.isEmpty() ? visible.get(index) : null;
	}

	@Override
	public void initGui() {
		super.initGui();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		String tex = "Textures/GUIs/buttons.png";
		buttonList.add(new CustomSoundImagedGuiButton(0, j+144, k+32, 74, 10, 100, 36, tex, ChromatiCraft.class, this));
		buttonList.add(new CustomSoundImagedGuiButton(1, j+144, k+42, 74, 10, 100, 46, tex, ChromatiCraft.class, this));

		buttonList.add(new CustomSoundImagedGuiButton(3, j+40, k+32, 10, 10, 90, 16, tex, ChromatiCraft.class, this));
		buttonList.add(new CustomSoundImagedGuiButton(2, j+40, k+42, 10, 10, 90, 26, tex, ChromatiCraft.class, this));

		buttonList.add(new CustomSoundImagedGuiButton(4, j+28, k+32, 10, 10, 90, 66, tex, ChromatiCraft.class, this));
		buttonList.add(new CustomSoundImagedGuiButton(5, j+28, k+42, 10, 10, 90, 56, tex, ChromatiCraft.class, this));
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

		Collections.sort(visible, new RecipeComparator());

		index = Math.min(index, visible.size()-1);

		if (!visible.isEmpty() && lexiconSelectedRecipe != null && !visible.contains(lexiconSelectedRecipe)) {
			ReikaSoundHelper.playClientSound(ChromaSounds.ERROR, player, 1, 1);
			lexiconSelectedRecipe = null;
		}
	}

	@Override
	protected void actionPerformed(GuiButton b) {
		super.actionPerformed(b);

		switch(b.id) {
			case 0:
				this.prevRecipe(GuiScreen.isCtrlKeyDown(), Keyboard.isKeyDown(Keyboard.KEY_LSHIFT));
				break;
			case 1:
				this.nextRecipe(GuiScreen.isCtrlKeyDown(), Keyboard.isKeyDown(Keyboard.KEY_LSHIFT));
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
					ReikaPacketHelper.sendStringIntPacket(ChromatiCraft.packetChannel, ChromaPackets.AUTORECIPE.ordinal(), tile, RecipesCastingTable.instance.getStringIDForRecipe(this.getRecipe()), number);
				break;

			case 5:
				ReikaPacketHelper.sendPacketToServer(ChromatiCraft.packetChannel, ChromaPackets.AUTOCANCEL.ordinal(), tile);
				lexiconSelectedRecipe = null;
				break;
		}
	}

	@Override
	protected void keyTyped(char c, int key) {
		super.keyTyped(c, key);

		if (key == Keyboard.KEY_END) {
			index = visible.size()-1;
			ReikaSoundHelper.playClientSound(ChromaSounds.GUICLICK, player, 0.5F, 1);
		}
		else if (key == Keyboard.KEY_HOME) {
			index = 0;
			ReikaSoundHelper.playClientSound(ChromaSounds.GUICLICK, player, 0.5F, 1);
		}
	}

	private void prevRecipe(boolean newItem, boolean newType) {
		CastingRecipe cr = this.getRecipe();
		ItemStack cur = null;
		if (cr != null) {
			cur = cr.getOutput();
		}
		if (index > 0) {
			do {
				//subindex = 0;
				index--;
				number = 1;
				cr = this.getRecipe();
			} while(index > 0 && (newItem || newType) && this.getRecipe() != null && this.matchRecipe(cur, cr, newType));
		}
	}

	private void nextRecipe(boolean newItem, boolean newType) {
		CastingRecipe cr = this.getRecipe();
		ItemStack cur = null;
		if (cr != null) {
			cur = cr.getOutput();
		}
		if (index < visible.size()-1) {
			//subindex = 0;
			do {
				index++;
				number = 1;
				cr = this.getRecipe();
			} while(index < visible.size()-1 && (newItem || newType) && this.getRecipe() != null && this.matchRecipe(cur, cr, newType));
		}
	}

	private boolean matchRecipe(ItemStack cur, CastingRecipe r, boolean newType) {
		if (newType) {
			if (cur.getItem() instanceof ItemPendant) {
				return r.getOutput().getItem() instanceof ItemPendant;
			}
			if (cur.getItem() instanceof ItemChromaPlacer) {
				return r.getOutput().getItemDamage() == cur.getItemDamage();
			}
		}
		return (newType ? cur.getItem() == this.getRecipe().getOutput().getItem() : ReikaItemHelper.matchStacks(cur, this.getRecipe().getOutput()));
	}

	private int getIncrement() {
		return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ? 64 : GuiScreen.isCtrlKeyDown() ? 16 : 1;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
	{
		super.drawGuiContainerBackgroundLayer(par1, par2, par3);
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		CastingRecipe cr = this.getRecipe();
		if (cr != null) {
			ChromaBookData.drawCompressedCastingRecipe(fontRendererObj, itemRender, cr, j, k);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2)
	{
		super.drawGuiContainerForegroundLayer(par1, par2);

		CastingRecipe cr = this.getRecipe();
		if (cr != null) {
			//r.drawTabIcon(itemRender, 21, 33);
			//fontRendererObj.drawSplitString(r.getTitle(), 40, 36, 120, 0xffffff);

			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			ItemStack out = cr.getOutput();
			BlendMode.DEFAULT.apply();
			String s = out.getDisplayName();
			if (ChromaItems.ADJACENCY.matchWith(out)) {
				s = s+" (Tier "+(out.stackTagCompound.getInteger("tier"))+")";
			}
			fontRendererObj.drawString(s, 10, 18, 0xffffff);

			fontRendererObj.drawString(String.format("x%d = %d", number, number*out.stackSize), 74, 38, 0xffffff);
			api.drawItemStack(itemRender, out, 52, 34);
			GL11.glPopAttrib();

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
		return "automator3";
	}

}
