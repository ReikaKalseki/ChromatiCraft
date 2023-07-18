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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.CastingAutomationSystem;
import Reika.ChromatiCraft.Auxiliary.ChromaBookData;
import Reika.ChromatiCraft.Auxiliary.RecursiveCastingAutomationSystem;
import Reika.ChromatiCraft.Auxiliary.Interfaces.CastingAutomationBlock;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.RecipesCastingTable;
import Reika.ChromatiCraft.Base.GuiLetterSearchable;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Container.ContainerCastingAuto;
import Reika.ChromatiCraft.Items.ItemChromaPlacer;
import Reika.ChromatiCraft.Items.Tools.ItemPendant;
import Reika.ChromatiCraft.Magic.Progression.ChromaResearchManager;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.DragonAPI.Instantiable.GUI.CustomSoundGuiButton.CustomSoundImagedGuiButton;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class GuiCastingAuto extends GuiLetterSearchable<CastingRecipe> {

	public static CastingRecipe lexiconSelectedRecipe = null;

	private static final List<ChromaResearch> list = new ArrayList();

	static {
		for (ChromaResearch r : ChromaResearch.getAllNonParents()) {
			if (r.isCrafting() && r.getRecipeCount() > 0) {
				list.add(r);
			}
		}
	}

	private int number = 1;

	private final CastingAutomationBlock tile;

	private final ArrayList<CastingRecipe> usableRecipes = new ArrayList();

	public GuiCastingAuto(CastingAutomationBlock te, EntityPlayer ep) {
		super(new ContainerCastingAuto(te, ep), ep, (TileEntityChromaticBase)te);
		xSize = 224;
		ySize = 227;

		tile = te;
		this.buildList(ep);
		index = filteredList.contains(te.getAutomationHandler().getCurrentRecipeOutput()) ? filteredList.indexOf(te.getAutomationHandler().getCurrentRecipeOutput()) : 0;
	}

	@Override
	public void initGui() {
		super.initGui();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		String tex = "Textures/GUIs/buttons.png";

		buttonList.add(new CustomSoundImagedGuiButton(3, j+40, k+32, 10, 10, 90, 16, tex, ChromatiCraft.class, this));
		buttonList.add(new CustomSoundImagedGuiButton(2, j+40, k+42, 10, 10, 90, 26, tex, ChromatiCraft.class, this));

		buttonList.add(new CustomSoundImagedGuiButton(4, j+28, k+32, 10, 10, 90, 66, tex, ChromatiCraft.class, this));
		buttonList.add(new CustomSoundImagedGuiButton(5, j+28, k+42, 10, 10, 90, 56, tex, ChromatiCraft.class, this));

		CastingAutomationSystem sys = tile.getAutomationHandler();
		if (sys instanceof RecursiveCastingAutomationSystem) {
			RecursiveCastingAutomationSystem rec = (RecursiveCastingAutomationSystem)sys;
			buttonList.add(new CustomSoundImagedGuiButton(0, j+144, k+32, 64, 10, 150, 56, tex, ChromatiCraft.class, this));
			buttonList.add(new CustomSoundImagedGuiButton(1, j+144, k+42, 64, 10, 150, 66, tex, ChromatiCraft.class, this));

			buttonList.add(new CustomSoundImagedGuiButton(6, j+144+64, k+32, 10, 10, 80, rec.recursionEnabled ? 76 : 86, tex, ChromatiCraft.class, this).setTooltip("Recursion"));
			buttonList.add(new CustomSoundImagedGuiButton(7, j+144+64, k+42, 10, 10, 90, 86, tex, ChromatiCraft.class, this).setTooltip("Priority"));
		}
		else {
			buttonList.add(new CustomSoundImagedGuiButton(0, j+144, k+32, 74, 10, 100, 36, tex, ChromatiCraft.class, this));
			buttonList.add(new CustomSoundImagedGuiButton(1, j+144, k+42, 74, 10, 100, 46, tex, ChromatiCraft.class, this));
		}
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
	}

	@Override
	protected void actionPerformed(GuiButton b) {
		super.actionPerformed(b);

		switch(b.id) {
			case 0:
				this.decrIndex(this.getFilter(GuiScreen.isCtrlKeyDown(), GuiScreen.isShiftKeyDown()));
				break;
			case 1:
				this.incrIndex(this.getFilter(GuiScreen.isCtrlKeyDown(), GuiScreen.isShiftKeyDown()));
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
				if (this.getActive() != null) {
					ReikaPacketHelper.sendStringIntPacket(ChromatiCraft.packetChannel, ChromaPackets.AUTORECIPE.ordinal(), (TileEntity)tile, RecipesCastingTable.instance.getStringIDForRecipe(this.getActive()), number);
					if (this.getActive() == lexiconSelectedRecipe)
						this.clearLexiconSelectedRecipe();
				}
				break;
			case 5:
				ReikaPacketHelper.sendPacketToServer(ChromatiCraft.packetChannel, ChromaPackets.AUTOCANCEL.ordinal(), (TileEntity)tile);
				this.clearLexiconSelectedRecipe();
				break;
			case 6: {
				RecursiveCastingAutomationSystem sys = (RecursiveCastingAutomationSystem)tile.getAutomationHandler();
				sys.recursionEnabled = !sys.recursionEnabled;
				ReikaPacketHelper.sendPacketToServer(ChromatiCraft.packetChannel, ChromaPackets.AUTORECURSE.ordinal(), (TileEntity)tile);
				break;
			}
			case 7: {
				if (this.getActive() == null) {
					ChromaSounds.ERROR.playSound(player);
					break;
				}
				RecursiveCastingAutomationSystem sys = (RecursiveCastingAutomationSystem)tile.getAutomationHandler();
				String cr = RecipesCastingTable.instance.getStringIDForRecipe(this.getActive());
				sys.toggleRecipePriority(this.getActive());
				ReikaPacketHelper.sendStringIntPacket(ChromatiCraft.packetChannel, ChromaPackets.AUTORECIPEPRIORITY.ordinal(), (TileEntity)tile, cr);
				break;
			}
		}

		this.initGui();
	}

	private void clearLexiconSelectedRecipe() {
		lexiconSelectedRecipe = null;
		this.refresh();
	}

	public void refresh() {
		this.buildList(player);
	}

	@Override
	protected Function<CastingRecipe, Boolean> getStepByCategory() {
		ChromaResearch r = this.getActive().getFragment();
		if (r == null)
			return null;
		ChromaResearch par = r.getParent();
		return (cr) -> cr.getFragment() != null && cr.getFragment().getParent() == par;
	}

	private Function<CastingRecipe, Boolean> getFilter(boolean newItem, boolean newType) {
		if (!newItem && !newType)
			return null;
		CastingRecipe cr = this.getActive();
		ItemStack cur = cr == null ? null : cr.getOutput();
		return (r) -> !this.matchRecipe(r, cur, newType);
	}

	private boolean matchRecipe(CastingRecipe r, ItemStack cur, boolean newType) {
		if (newType) {
			if (cur.getItem() instanceof ItemPendant) {
				return r.getOutput().getItem() instanceof ItemPendant;
			}
			if (cur.getItem() instanceof ItemChromaPlacer) {
				return r.getOutput().getItemDamage() == cur.getItemDamage();
			}
		}
		return (newType ? cur.getItem() == this.getActive().getOutput().getItem() : ReikaItemHelper.matchStacks(cur, this.getActive().getOutput()));
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
		CastingRecipe cr = this.getActive();
		if (cr != null) {
			ChromaBookData.drawCompressedCastingRecipe(fontRendererObj, itemRender, cr, j, k);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2)
	{
		super.drawGuiContainerForegroundLayer(par1, par2);

		CastingRecipe cr = this.getActive();
		if (cr != null) {
			//r.drawTabIcon(itemRender, 21, 33);
			//fontRendererObj.drawSplitString(r.getTitle(), 40, 36, 120, 0xffffff);

			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			ItemStack out = cr.getOutputForDisplay();
			BlendMode.DEFAULT.apply();
			String s = out.getDisplayName();
			if (ChromaItems.ADJACENCY.matchWith(out)) {
				s = s+" (Tier "+(out.stackTagCompound.getInteger("tier"))+")";
			}
			int c = 0xffffff;
			CastingAutomationSystem sys = tile.getAutomationHandler();
			if (sys instanceof RecursiveCastingAutomationSystem) {
				RecursiveCastingAutomationSystem rec = (RecursiveCastingAutomationSystem)sys;
				if (rec.isRecipePriority(cr)) {
					c = 0xCE8EDB;
				}
			}
			fontRendererObj.drawString(s, 10, 18, c);

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

		this.drawSearch();
	}

	@Override
	public String getGuiTexture() {
		return "automator3";
	}

	@Override
	protected String getString(CastingRecipe val) {
		return val.getDisplayName();
	}

	@Override
	protected Collection<CastingRecipe> getAllEntries(EntityPlayer ep) {
		if (tile == null || ep == null)
			return new ArrayList();
		Container open = Minecraft.getMinecraft().thePlayer.openContainer;
		ArrayList<CastingRecipe> li = new ArrayList();
		Collection<CastingRecipe> recipes = tile.getAvailableRecipes();//ChromaResearchManager.instance.getRecipesPerformed(ep);
		for (ChromaResearch r : list) {
			if (ChromaResearchManager.instance.playerHasFragment(ep, r)) {
				Collection<CastingRecipe> c = r.getCraftingRecipes();
				for (CastingRecipe cr : c) {
					if (recipes.contains(cr)) {
						if (open instanceof ContainerCastingAuto) {
							ContainerCastingAuto cc = (ContainerCastingAuto)open;
							if (!cc.isRecipeValid(cr))
								continue;
						}
						li.add(cr);
					}
				}
			}
		}

		if (!li.isEmpty() && lexiconSelectedRecipe != null && !li.contains(lexiconSelectedRecipe)) {
			ReikaSoundHelper.playClientSound(ChromaSounds.ERROR, player, 1, 1);
			lexiconSelectedRecipe = null;
		}

		return lexiconSelectedRecipe != null ? Arrays.asList(lexiconSelectedRecipe) : li;
	}

	@Override
	protected void sortEntries(ArrayList<CastingRecipe> li) {
		Collections.sort(li, CastingRecipe.recipeComparator);
	}

}
