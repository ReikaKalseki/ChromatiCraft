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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.CustomSoundGuiButton.CustomSoundImagedGuiButton;
import Reika.ChromatiCraft.Base.ChromaBookGui;
import Reika.ChromatiCraft.Items.ItemInfoFragment;
import Reika.ChromatiCraft.Items.Tools.ItemChromaBook;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Registry.ChromaResearchManager;
import Reika.ChromatiCraft.Registry.ChromaResearchManager.ResearchLevel;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.Maps.RegionMap;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class GuiFragmentRecovery extends ChromaBookGui {

	private ArrayList<ChromaResearch> missing = new ArrayList();
	private ArrayList<ChromaResearch> missingVisible = new ArrayList();

	private RegionMap<ChromaResearch> locations = new RegionMap();

	public GuiFragmentRecovery(EntityPlayer ep) {
		super(ChromaGuis.REFRAGMENT, ep, 256, 220);

		Collection<ChromaResearch> c = ChromaResearchManager.instance.getFragments(ep);
		for (ChromaResearch r : c) {
			if (ItemChromaBook.hasPage(player.getCurrentEquippedItem(), r)) {

			}
			else {
				missing.add(r);
			}
		}
		Collections.sort(missing, ChromaResearchManager.instance.researchComparator);
		this.recalculateMissingVisible();
	}

	@Override
	public void initGui() {
		super.initGui();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		String file = "Textures/GUIs/Handbook/buttons.png";

		this.addAuxButton(new CustomSoundImagedGuiButton(10, j+xSize, k, 22, 39, 42, 126, file, ChromatiCraft.class, this), "Return");
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		super.actionPerformed(button);
		if (button.id == 10) {
			this.goTo(ChromaGuis.BOOKNAV, null);
		}
		this.initGui();
	}

	@Override
	protected void mouseClicked(int x, int y, int b) {
		super.mouseClicked(x, y, b);

		ChromaResearch r = locations.getRegion(x, y);
		if (r != null) {
			boolean cr = player.capabilities.isCreativeMode;
			ItemStack[] inv = player.inventory.mainInventory;
			int ink = cr ? -1 : this.checkForInk(inv);
			if (cr || ink >= 0) {
				int paper = cr ? -1 : ReikaInventoryHelper.locateInInventory(Items.paper, inv);
				if (cr || paper >= 0) {
					this.giveResearch(r);
					ReikaInventoryHelper.decrStack(paper, inv);
					ReikaInventoryHelper.decrStack(ink, inv);
					//Minecraft.getMinecraft().thePlayer.playSound("random.click", 2, 1);
					ReikaSoundHelper.playClientSound(ChromaSounds.GUICLICK, player, 0.33F, 1);
				}
			}
		}
	}

	private int checkForInk(ItemStack[] inv) {
		for (int i = 0; i < inv.length; i++) {
			ItemStack in = inv[i];
			if (in != null) {
				if (ReikaItemHelper.matchStacks(in, ReikaItemHelper.inksac))
					return i;
				else if (ReikaItemHelper.isInOreTag(in, "dyeBlack"))
					return i;
			}
		}
		return -1;
	}

	@Override
	public void drawScreen(int x, int y, float f) {
		locations.clear();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/GUIs/Handbook/navbcg.png");
		this.drawTexturedModalRect(j, k-8, -7, -7, xSize, ySize);
		super.drawScreen(x, y, f);

		int w = 7;
		for (int i = 0; i < missingVisible.size() && i < 35; i++) {
			ChromaResearch r = missingVisible.get(i);
			int dx = j/2+4+(i%w)*17;
			int dy = k/2+9+(i/w)*16;
			GL11.glPushMatrix();
			GL11.glScaled(2, 2, 2);
			api.drawItemStack(itemRender, ItemInfoFragment.getItem(r), dx, dy);
			GL11.glPopMatrix();
			locations.addRegionByWH(dx*2, dy*2, 32, 32, r);
			if (api.isMouseInBox(dx*2, dx*2+32, dy*2, dy*2+32)) {
				api.drawRectFrame(dx*2, dy*2, 32, 32, CrystalElement.getBlendedColor(this.getGuiTick(), 300));
				fontRendererObj.drawString(r.getTitle()+" ("+r.level.getDisplayName()+")", j+8, k+196, 0xffffff);
			}
		}
	}

	private void giveResearch(ChromaResearch r) {
		ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.RERESEARCH.ordinal(), Minecraft.getMinecraft().theWorld, 0, 0, 0, r.ordinal());
		missing.remove(r);
		this.recalculateMissingVisible();
	}

	private void recalculateMissingVisible() {
		missingVisible.clear();
		for (ChromaResearch r : missing) {
			if (this.isVisible(r))
				missingVisible.add(r);
		}
		Collections.sort(missingVisible, ChromaResearchManager.instance.researchComparator);
	}

	private boolean isVisible(ChromaResearch r) {
		Collection<ChromaResearch> par = ChromaResearchManager.instance.getPreReqsFor(r);
		for (ChromaResearch p : par) {
			if (missing.contains(p))
				return false;
		}
		if (r.level != ResearchLevel.ENTRY) {
			Collection<ChromaResearch> lvl = ChromaResearchManager.instance.getResearchForLevelAndBelow(r.level.pre());
			for (ChromaResearch p : lvl) {
				if (missing.contains(p))
					return false;
			}
		}
		return true;
	}

	@Override
	public String getBackgroundTexture() {
		return "/Reika/ChromatiCraft/Textures/GUIs/Handbook/fragments.png";
	}

}
