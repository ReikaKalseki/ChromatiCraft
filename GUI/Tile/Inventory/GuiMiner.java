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
import java.util.EnumMap;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.GuiChromaBase;
import Reika.ChromatiCraft.Container.ContainerMiner;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.TileEntity.Acquisition.TileEntityMiner;
import Reika.ChromatiCraft.TileEntity.Acquisition.TileEntityMiner.ItemDisplay;
import Reika.ChromatiCraft.TileEntity.Acquisition.TileEntityMiner.MineralCategory;
import Reika.DragonAPI.Instantiable.GUI.CustomSoundGuiButton.CustomSoundImagedGuiButton;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;

public class GuiMiner extends GuiChromaBase {

	private final TileEntityMiner tile;

	private int offset = 0;
	private int cooldown = 0;

	private static final int ITEM_SIZE = 16;
	private static final int COLS = 10;
	private static final int ROWS = 6;
	private static final int CAT_SPACE = 1;
	private static final int COLS_PER_CAT = Math.max(1, COLS/((MineralCategory.values().length-1)*2-1));

	private ItemDisplay selectedItem;

	public GuiMiner(EntityPlayer ep, TileEntityMiner te) {
		super(new ContainerMiner(ep, te), ep, te);
		tile = te;
		ySize = 206;
	}

	@Override
	public void initGui() {
		super.initGui();

		String file = this.getFullTexturePath();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		int n = 4;
		int x = j+n+xSize-30;
		int y = k+n+50;
		int u = tile.getCategory() == MineralCategory.ANY ? 198 : 178;
		int v = 0;
		buttonList.add(new CustomSoundImagedGuiButton(0, x, y, 20, 20, u, v, file, "Mine All", 0xffffff, false, ChromatiCraft.class, this));
	}

	@Override
	protected void actionPerformed(GuiButton b) {
		super.actionPerformed(b);

		if (b.id == 0) {
			tile.setCategory(MineralCategory.ANY.ordinal());
			ReikaPacketHelper.sendPacketToServer(ChromatiCraft.packetChannel, ChromaPackets.MINERCATEGORY.ordinal(), tile, MineralCategory.ANY.ordinal());
			this.initGui();
		}
	}

	@Override
	public void drawScreen(int x, int y, float f) {
		super.drawScreen(x, y, f);
		selectedItem = null;
	}

	@Override
	protected void mouseClicked(int x, int y, int b) {
		super.mouseClicked(x, y, b);

		if (selectedItem != null) {
			ReikaSoundHelper.playClientSound(ChromaSounds.GUICLICK, player, 0.33F, 1);
			tile.setCategory(selectedItem.category.ordinal());
			ReikaPacketHelper.sendPacketToServer(ChromatiCraft.packetChannel, ChromaPackets.MINERCATEGORY.ordinal(), tile, selectedItem.category.ordinal());
			this.initGui();
		}
	}

	private int getMaxOffset() {
		//return 20;//Math.max(0, tile.getFound().size()/COLS-ROWS);
		EnumMap<MineralCategory, ArrayList<ItemDisplay>> c = tile.getFoundByCategory();
		int max = 0;
		for (ArrayList li : c.values()) {
			max = Math.max(max, li.size()-ROWS);
		}
		return max;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		super.drawGuiContainerForegroundLayer(par1, par2);

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		EnumMap<MineralCategory, ArrayList<ItemDisplay>> c = tile.getFoundByCategory();

		double s = 0.75;
		int n = 0;
		for (MineralCategory mc : c.keySet()) {
			boolean sel = mc == tile.getCategory();
			int ox = (int)((1/s)*(12+mc.ordinal()*ITEM_SIZE*s*s+(mc.ordinal()*CAT_SPACE*ITEM_SIZE*s*s)));
			int oy = (int)((1/s)*(28));
			int clr = sel ? 0x7000ff00 : 0x40ffffff;
			if (api.isMouseInBox(j+ox-4, j-4+ox+10, k, k+ySize)) {
				int cn = tile.getTotalFound(mc);
				fontRendererObj.drawString(mc.displayName+" (x"+cn+")", 10, 27-fontRendererObj.FONT_HEIGHT, sel ? 0xff00ff00 : 0xffffffff);
				clr = sel ? 0xff00ff00 : 0xffffffff;
			}
			else {

			}
			api.drawRectFrame(ox-5, 27, 13, (int)(ROWS*ITEM_SIZE*s)+2, clr);
			int i = -offset;
			for (ItemDisplay id : c.get(mc)) {
				if (i >= 0) {
					int dx = ox+(int)((1/s)*((i%COLS_PER_CAT)*ITEM_SIZE*s*s));
					int dy = oy+(int)((1/s)*((i/COLS_PER_CAT)*ITEM_SIZE*s*s));
					if (api.isMouseInBox(j+dx-4, j+dx+10-4, k+dy-9, k+dy+12-9)) {
						String sg = id.getDisplayItem().getDisplayName()+": "+id.getAmount();
						api.drawTooltipAt(fontRendererObj, sg, dx+fontRendererObj.getStringWidth(sg)+36, dy);
						selectedItem = id;
						if (Mouse.isButtonDown(0) && cooldown == 0) {
							cooldown = 5;
							this.mouseClicked(0, 0, 0);
						}
					}
					//api.drawItemStack(itemRender, id.getDisplayItem(), dx, dy);
				}
				i++;
				if (i >= COLS_PER_CAT*ROWS) {
					break;
				}
			}
			n++;
		}

		/*
		int max = Math.min(c.size(), COLS*ROWS);
		for (int i = 0; i < max; i++) {
			ItemStack is = c.get(i+COLS*offset);
			double s = 0.75;
			int dx = (int)((1/s)*(j*s+10+(i%COLS)*10))-3;
			int dy = (int)((1/s)*(k*s+22+(i/COLS)*10))-6;
			if (api.isMouseInBox(dx, dx+12, dy, dy+12)) {
				String sg = is.getDisplayName()+": "+tile.getNumberFound(is).getAmount();
				api.drawTooltipAt(fontRendererObj, sg, dx-j+fontRendererObj.getStringWidth(sg)+36, dy-k);
			}
		}
		 */

		String sg = String.format("Scan: %.2f%s done", 100*tile.getDigCompletion(), "%");
		fontRendererObj.drawString(sg, 4, 111, 0xffffff);

		if (tile.isReady()) {
			int g = 160+(int)(95*Math.sin(Math.toRadians(System.currentTimeMillis()/3%360)));
			int color = 0xff000000 | (g << 8);
			this.drawRect(156, 40, 164, 48, color);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int a, int b) {
		super.drawGuiContainerBackgroundLayer(f, a, b);

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		EnumMap<MineralCategory, ArrayList<ItemDisplay>> c = tile.getFoundByCategory();

		GL11.glPushMatrix();
		double s = 0.75;
		GL11.glScaled(s, s, s);
		int n = 0;
		for (MineralCategory mc : c.keySet()) {
			int ox = (int)((1/s)*(j+12+mc.ordinal()*ITEM_SIZE*s+(mc.ordinal()*CAT_SPACE*ITEM_SIZE*s)));
			int oy = (int)((1/s)*(k+28));
			//fontRendererObj.drawString(mc.displayName, ox, oy-fontRendererObj.FONT_HEIGHT-2, 0xffffffff);
			int i = -offset;
			for (ItemDisplay id : c.get(mc)) {
				if (i >= 0) {
					int dx = ox+(int)((1/s)*((i%COLS_PER_CAT)*ITEM_SIZE*s));
					int dy = oy+(int)((1/s)*((i/COLS_PER_CAT)*ITEM_SIZE*s));
					api.drawItemStack(itemRender, id.getDisplayItem(), dx, dy);
				}
				i++;
				if (i >= COLS_PER_CAT*ROWS) {
					break;
				}
			}
			n++;
		}
		GL11.glPopMatrix();

		if (cooldown > 0) {
			cooldown--;
		}
		else {
			if (offset > 0 && (Keyboard.isKeyDown(Keyboard.KEY_W) || Keyboard.isKeyDown(Keyboard.KEY_UP))) {
				offset--;
				cooldown = 4;
			}
			else if (offset < this.getMaxOffset() && (Keyboard.isKeyDown(Keyboard.KEY_S) || Keyboard.isKeyDown(Keyboard.KEY_DOWN))) {
				offset++;
				cooldown = 4;
			}
		}
		/*
		List<ItemStack> c = tile.getFound();
		GL11.glPushMatrix();
		double s = 0.75;
		GL11.glScaled(s, s, s);
		int max = Math.min(c.size(), COLS*ROWS);
		for (int i = 0; i < max; i++) {
			ItemStack is = c.get(i+COLS*offset);
			int dx = (int)((1/s)*(j+10+(i%COLS)*18*s));
			int dy = (int)((1/s)*(k+22+(i/COLS)*18*s));
			api.drawItemStack(itemRender, is, dx, dy);
		}
		GL11.glPopMatrix();
		 */

		if (offset < this.getMaxOffset()) {
			api.drawLine(j+70, k+103, j+84, k+103, 0xffffff);
			api.drawLine(j+70, k+103, j+77, k+105, 0xffffff);
			api.drawLine(j+84, k+103, j+77, k+105, 0xffffff);
		}
		if (offset > 0) {
			api.drawLine(j+70, k+20, j+84, k+20, 0xffffff);
			api.drawLine(j+70, k+20, j+77, k+17, 0xffffff);
			api.drawLine(j+84, k+20, j+77, k+17, 0xffffff);
		}
	}

	@Override
	public String getGuiTexture() {
		return "miner2";
	}

}
