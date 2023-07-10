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

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.GuiChromaBase;
import Reika.ChromatiCraft.Container.ContainerItemInserter;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityItemInserter;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityItemInserter.InsertionType;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.GUI.ImagedGuiButton;
import Reika.DragonAPI.Interfaces.Block.MachineRegistryBlock;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.World.ReikaRedstoneHelper;


public class GuiItemInserter extends GuiChromaBase {

	private final TileEntityItemInserter tile;

	public GuiItemInserter(EntityPlayer ep, TileEntityItemInserter te) {
		super(new ContainerItemInserter(ep, te), ep, te);
		tile = te;
		xSize = 180;
		ySize = 230;
	}

	@Override
	public void initGui() {
		super.initGui();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		for (int i = 0; i < TileEntityItemInserter.TARGETS; i++) {
			InsertionType type = tile.getInsertionType(i);
			if (type != null) {
				int x = j+138;
				int y = k+8;
				int u = 181+7*type.ordinal();
				buttonList.add(new ImagedGuiButton(i, x+0, y+i*20, 6, 16, u, 31, this.getFullTexturePath(), ChromatiCraft.class));
			}

			if (tile.getStackInSlot(i) != null) {
				for (int f = 0; f < TileEntityItemInserter.TARGETS; f++) {
					Coordinate c = tile.getLink(f);
					if (c != null) {
						int id = 100+i*TileEntityItemInserter.TARGETS+f;
						int dx = j+51+i*16-1;
						int dy = k+7+f*20+i*3-1;
						int u = tile.isLinkEnabled(i, f) ? 181 : 187;
						//ReikaJavaLibrary.pConsole(i+","+f+">"+tile.isLinkEnabled(i, f)+" @ "+dx+","+dy);
						buttonList.add(new ImagedGuiButton(id, dx, dy, 5+2, 3+2, u-1, 170-1, this.getFullTexturePath(), ChromatiCraft.class).setTooltip("Slot "+i+" > "+tile.getLink(f)));
					}
				}
			}
		}
	}

	@Override
	protected void actionPerformed(GuiButton b) {
		super.actionPerformed(b);

		if (b.id < TileEntityItemInserter.TARGETS) {
			ReikaPacketHelper.sendPacketToServer(ChromatiCraft.packetChannel, ChromaPackets.INSERTERMODE.ordinal(), tile, b.id);
			tile.setInsertionType(b.id, tile.getInsertionType(b.id).next());
		}
		else if (b.id >= 100 && b.id < 100+(TileEntityItemInserter.TARGETS*TileEntityItemInserter.TARGETS)) {
			int i = (b.id-100)/TileEntityItemInserter.TARGETS;
			int k = (b.id-100)%TileEntityItemInserter.TARGETS;
			ReikaPacketHelper.sendPacketToServer(ChromatiCraft.packetChannel, ChromaPackets.INSERTERCONNECTION.ordinal(), tile, i, k);
			//tile.toggleConnection(i, k);
		}
	}

	@Override
	protected void mouseClicked(int x, int y, int b) {
		super.mouseClicked(x, y, b);

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			int slot = this.getClickedSlot(-j+x, -k+y);
			if (slot >= 0) {
				tile.removeCoordinate(slot);
				ReikaPacketHelper.sendPacketToServer(ChromatiCraft.packetChannel, ChromaPackets.INSERTERCLEAR.ordinal(), tile, slot);
			}
		}

		this.initGui();
	}

	private int getClickedSlot(int x, int y) {
		if (x < 152 || y < 15)
			return -1;
		if (x > 167 || y > 130)
			return -1;
		return (y-15)/20;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int a, int b) {
		super.drawGuiContainerBackgroundLayer(f, a, b);

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		for (int i = 0; i < 6; i++) {
			if (ReikaRedstoneHelper.isPoweredOnSide(tile.worldObj, tile.xCoord, tile.yCoord, tile.zCoord, ForgeDirection.VALID_DIRECTIONS[i])) {
				int x = j+44+i*16;
				int u = 181+i*8;
				api.drawTexturedModalRect(x, k+6, u, 49, 8, 120);
			}
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		super.drawGuiContainerForegroundLayer(par1, par2);

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		for (int i = 0; i < TileEntityItemInserter.TARGETS; i++) {
			Coordinate c = tile.getLink(i);
			if (c != null) {
				Block b = c.getBlock(tile.worldObj);
				if (b != Blocks.air) {
					ItemStack is = b instanceof MachineRegistryBlock ? ((MachineRegistryBlock)b).getMachine(tile.worldObj, c.xCoord, c.yCoord, c.zCoord).getCraftedProduct() : new ItemStack(b, 1, c.getBlockMetadata(tile.worldObj));
					api.drawItemStack(itemRender, fontRendererObj, is, 152, 8+20*i);

					int x = j+137;
					int y = k+7+i*20;
					if (api.isMouseInBox(x, x+8, y, y+18)) {
						String s = tile.getInsertionType(i).displayName;
						api.drawTooltipAt(fontRendererObj, s, api.getMouseRealX()-j, api.getMouseRealY()-k);
					}

					x += 15;
					if (api.isMouseInBox(x, x+16, y, y+18)) {
						String s = is.getDisplayName()+" at "+c.toString();
						api.drawTooltipAt(fontRendererObj, s, api.getMouseRealX()-j, api.getMouseRealY()-k);
					}
				}
			}
		}

		GL11.glEnable(GL11.GL_BLEND);
		BlendMode.DEFAULT.apply();
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, this.getFullTexturePath());
		float f = (float)(0.875+0.125*Math.sin(System.currentTimeMillis()/400D));
		GL11.glColor4f(f, f, f, f);
		int u = tile.omniMode ? 227+8 : 227;
		this.drawTexturedModalRect(31, 6, u+12, 49, 8, 120);
		GL11.glColor4f(1, 1, 1, 1);
		this.drawTexturedModalRect(31, 6, 231, 49, 8, 120);
	}

	@Override
	protected boolean drawTitle() {
		return false;
	}

	@Override
	protected boolean labelInventory() {
		return false;
	}

	@Override
	public String getGuiTexture() {
		return "inserter4";
	}

}
