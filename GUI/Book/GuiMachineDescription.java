/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI.Book;

import java.util.ArrayList;
import java.util.Collection;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaDescriptions;
import Reika.ChromatiCraft.Auxiliary.Interfaces.ComplexAOE;
import Reika.ChromatiCraft.Base.GuiDescription;
import Reika.ChromatiCraft.Base.TileEntity.ChargedCrystalPowered;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityAdjacencyUpgrade;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityRelayPowered;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalReceiver;
import Reika.ChromatiCraft.ModInterface.Bees.TileEntityLumenAlveary;
import Reika.ChromatiCraft.Registry.AdjacencyUpgrades;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.ISBRH.CrystalRenderer;
import Reika.ChromatiCraft.TileEntity.TileEntityLumenWire.CheckType;
import Reika.ChromatiCraft.TileEntity.Auxiliary.TileEntityFocusCrystal.CrystalTier;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Instantiable.Data.Proportionality;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockBox;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Rendering.ReikaGuiAPI;
import Reika.DragonAPI.Objects.LineType;

public class GuiMachineDescription extends GuiDescription {

	private float renderq = 22.5F;

	public static boolean runningRender = false;

	private final ArrayList<Pages> pageList = new ArrayList();

	public GuiMachineDescription(EntityPlayer ep, ChromaResearch r) {
		super(ChromaGuis.MACHINEDESC, ep, r, 256, 220);
		pageList.add(Pages.MAIN);
		if (!ChromaDescriptions.isUnfilled(page.getNotes(1)))
			pageList.add(Pages.NOTES);
		if (this.getUsedEnergy() != null)
			pageList.add(Pages.ENERGY);
		if (ComplexAOE.class.isAssignableFrom(page.getMachine().getTEClass()))
			pageList.add(Pages.AOE);
	}

	@Override
	protected PageType getGuiLayout() {
		return PageType.PLAIN;
	}

	@Override
	protected int getMaxSubpage() {
		if (page == ChromaResearch.ALVEARY && ModList.FORESTRY.isLoaded())
			return 1+TileEntityLumenAlveary.getEffectSet().size();
		/*
		int max = super.getMaxSubpage();
		if (max == 0 && this.getUsedEnergy() != null)
			max = 1;
		if (ComplexAOE.class.isAssignableFrom(page.getMachine().getTEClass()))
			max++;
		return max;
		 */
		return pageList.size()-1;
	}

	@Override
	protected String getText(int subpage) {
		if (page == ChromaResearch.ALVEARY)
			return super.getText(subpage);
		switch(pageList.get(subpage)) {
			case AOE:
				return "This construct has a complex area of effect, shown here.\n\nMore intensely colored tiles indicate stronger effect relative to weakly colored ones.";
			case ENERGY:
				return "This device requires lumen energy to function.";
			default:
				return super.getText(subpage);
		}
	}
	/*
	@Override
	protected int parseMaxSubpage() {
		int ret = super.parseMaxSubpage();
		if (ret == 0 && this.getUsedEnergy() != null)
			ret = 1;
		return ret;
	}
	 */
	private ElementTagCompound getUsedEnergy() {
		ElementTagCompound tag = new ElementTagCompound();
		ChromaTiles m = page.getMachine();
		TileEntity te = m.createTEInstanceForRender(0);
		if (m == ChromaTiles.ADJACENCY) {
			ArrayList<Integer> li = new ArrayList();
			for (int i = 0; i < CrystalElement.elements.length; i++) {
				if (AdjacencyUpgrades.upgrades[i].isImplemented())
					li.add(i);
			}
			int offset = li.get((int)((System.currentTimeMillis()/(1000*TileEntityAdjacencyUpgrade.MAX_TIER))%li.size()));
			te = m.createTEInstanceForRender(offset);
			tag.addValueToColor(CrystalElement.elements[offset], 1);
		}
		else if (m.isChargedCrystalPowered()) {
			ChargedCrystalPowered r = (ChargedCrystalPowered)te;
			tag = r.getRequiredEnergy();
		}
		else if (m.isRelayPowered()) {
			TileEntityRelayPowered r = (TileEntityRelayPowered)te;
			tag = r.getRequiredEnergy();
		}
		else if (m.isPylonPowered()) {
			CrystalReceiver r = (CrystalReceiver)te;
			for (int i = 0; i < 16; i++) {
				if (r.isConductingElement(CrystalElement.elements[i])) {
					int amt = 25+(int)(20*Math.sin(i+this.getGuiTick()/20D));
					tag.addValueToColor(CrystalElement.elements[i], amt);
				}
			}
		}
		return tag != null && !tag.isEmpty() ? tag : null;
	}

	@Override
	public final void drawScreen(int x, int y, float f) {
		super.drawScreen(x, y, f);

		int posX = (width - xSize) / 2;
		int posY = (height - ySize) / 2 - 8;

		if (page == ChromaResearch.ACCEL && ((GuiAdjacencyDescription)this).getMachine() != null) {
			this.drawMachineRender(posX, posY);
			if (subpage > 0)
				this.drawNotesGraphics(posX, posY);
		}
		else {
			switch (pageList.get(subpage)) {
				case MAIN:
					this.drawMachineRender(posX, posY);
					break;
				case ENERGY:
					ElementTagCompound tag = this.getUsedEnergy();
					if (tag != null) {
						ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/infoicons.png");
						int idx = -1;
						if (page.getMachine().isPylonPowered()) {
							idx = 1;
						}
						else if (page.getMachine().isRelayPowered()) {
							idx = 2;
						}
						else if (page.getMachine().isChargedCrystalPowered()) {
							idx = 3;
						}
						else if (page.getMachine().isWirelessPowered()) {
							idx = 4;
						}
						double u = 0.0625*idx;
						double v = 0;
						double s = 0.0625;
						Tessellator v5 = Tessellator.instance;
						int r = 64;
						int dx = posX+xSize/2-r/2;
						int dy = posY+ySize-r-16;
						v5.startDrawingQuads();
						v5.addVertexWithUV(dx, dy+r, 0, u, v+s);
						v5.addVertexWithUV(dx+r, dy+r, 0, u+s, v+s);
						v5.addVertexWithUV(dx+r, dy, 0, u+s, v);
						v5.addVertexWithUV(dx, dy, 0, u, v);
						v5.draw();

						r = 32;
						dx = posX+xSize-r-50;
						dy = posY+r+10;
						Proportionality<CrystalElement> p = tag.getProportionality();
						p.setGeometry(dx, dy, r, System.identityHashCode(this)+this.getGuiTick()%360);
						p.render(CrystalElement.getColorMap());
						float lf = GL11.glGetFloat(GL11.GL_LINE_WIDTH);
						GL11.glLineWidth(2.5F);
						api.drawCircle(dx, dy, r+1, 0xffffffff);
						api.drawCircle(dx, dy, r, 0xff000000);
						GL11.glLineWidth(lf);
					}
					break;
				case AOE:
					ComplexAOE te = (ComplexAOE)page.getMachine().createTEInstanceForRender(0);
					Collection<Coordinate> li = te.getPossibleRelativePositions();
					BlockBox box = BlockBox.nothing();
					for (Coordinate c : li) {
						box = box.addCoordinate(c.xCoord, c.yCoord, c.zCoord);
					}
					int area = xSize/2;
					int height = 77;
					int w = Math.min(area/box.getSizeX(), height/box.getSizeZ());
					//ReikaGuiAPI.instance.drawRectFrame(posX+xSize/2-30, posY+3, 90, height, 0xff0000, LineType.SOLID);
					for (Coordinate c : li) {
						int dx = posX+xSize/2+w*c.xCoord;
						int dy = posY+height/2+5-w/2+w*c.zCoord;
						int a = (int)(te.getNormalizedWeight(c)*255);
						int c1 = ReikaColorAPI.RGBtoHex(192, 212, 255, a);
						int c2 = ReikaColorAPI.RGBtoHex(192, 128, 255, a);
						int clr = ReikaColorAPI.mixColors(c1, c2, 0.5F+0.5F*MathHelper.sin((this.getGuiTick()*0.1F+System.identityHashCode(c)%10000))*0.2F);
						ReikaGuiAPI.instance.drawRect(dx, dy, w, w, clr, true);
						ReikaGuiAPI.instance.drawRectFrame(dx, dy, w, w, clr | (a/3 << 24), LineType.SOLID);
					}
					break;
				case NOTES:
					this.drawNotesGraphics(posX, posY);
					break;
				default:
					break;
			}
		}
	}

	protected void drawNotesGraphics(int posX, int posY) {
		switch(page) {
			case LUMENWIRE:
				for (int i = 0; i < CheckType.list.length; i++) {
					ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/infoicons.png");
					double u = 0.0625*i;
					double v = 0.0625;
					double s = 0.0625;
					int r = 48;
					Tessellator v5 = Tessellator.instance;
					v5.startDrawingQuads();
					int dx = posX+(r+1)*(i%4-2)+xSize/2;
					int dy = posY+(r+1)*(i/4)+115;
					v5.setColorOpaque_I(CheckType.list[i].renderColor);
					v5.addVertexWithUV(dx+0, dy+r, 0, u, v+s);
					v5.addVertexWithUV(dx+r, dy+r, 0, u+s, v+s);
					v5.addVertexWithUV(dx+r, dy+0, 0, u+s, v);
					v5.addVertexWithUV(dx+0, dy+0, 0, u, v);

					v5.draw();
				}
				break;
			default:
				break;
		}
	}

	private void drawMachineRender(int posX, int posY) {
		double x = posX+167;
		double y = posY+44;
		//float q = 12.5F + fscale*(float)Math.sin(System.nanoTime()/1000000000D); //wobble
		//ReikaJavaLibrary.pConsole(y-ReikaGuiAPI.instance.getMouseScreenY(height));
		int range = 64;
		boolean rotate = ReikaGuiAPI.instance.isMouseInBox((int)x-range/2, (int)x+range/2, (int)y-range, (int)y+range);

		if (Mouse.isButtonDown(0) && rotate) {
			int mvy = Mouse.getDY();
			if (mvy < 0 && renderq < 45) {
				renderq++;
			}
			if (mvy > 0 && renderq > -45) {
				renderq--;
			}
		}
		y -= 8*Math.sin(Math.abs(Math.toRadians(renderq)));

		ChromaTiles m = page.getMachine();

		if (m.isPlant())
			renderq = 22.5F;
		if (m.isTextureFace())
			renderq = 22.5F;

		int r = (int)(System.nanoTime()/20000000)%360;
		if (m.isPlant())
			r = -45;
		if (m.isTextureFace())
			r = -45;

		int offset = 0;
		if (m == ChromaTiles.ADJACENCY) {
			offset = ((GuiAdjacencyDescription)this).getMachine().ordinal();
		}
		else if (m == ChromaTiles.FOCUSCRYSTAL) {
			offset = (int)((System.currentTimeMillis()/4000)%CrystalTier.tierList.length);
		}

		GL11.glTranslated(0, 0, 32);
		GL11.glColor4f(1, 1, 1, 1);

		GL11.glEnable(GL11.GL_BLEND);

		RenderHelper.enableGUIStandardItemLighting();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		double sc = 48;
		GL11.glPushMatrix();

		runningRender = true;
		if (m.hasRender()) {
			double dx = x;
			double dy = y+m.getRenderOffset();
			double dz = 0;
			GL11.glPushMatrix();
			GL11.glTranslated(dx, dy, dz);
			GL11.glScaled(sc, -sc, sc);
			GL11.glRotatef(renderq, 1, 0, 0);
			GL11.glRotatef(r, 0, 1, 0);
			double a = -0.5;
			double b = -0.5;
			TileEntity te = m.createTEInstanceForRender(offset);
			if (m.needsRenderOffset()) {
				a = b = -0.875;
				GL11.glTranslated(0, -0.3, 0);
			}
			if (m == ChromaTiles.LUMENWIRE) {
				a = b = -1.25;
				GL11.glTranslated(0, -0.5, 0);
			}
			if (m == ChromaTiles.AVOLASER) {
				GL11.glTranslated(0, 0.5, 0);
			}
			if (m == ChromaTiles.AURAPOINT) {
				GL11.glTranslated(0, -0.25, 0);
			}
			if (m == ChromaTiles.TELEPORT) {
				a = b = -0.125;
			}
			if (m == ChromaTiles.FLUIDRELAY) {
				a = b = -1.5;
			}
			if (m == ChromaTiles.CHROMACRAFTER) {
				a = b = -0.03125;
				GL11.glTranslated(0, 0.3125, 0);
			}
			if (m == ChromaTiles.PERSONAL) {
				double s = 0.75;
				GL11.glScaled(s, s, s);
				GL11.glTranslated(0, 0.5, 0);
			}
			if (m == ChromaTiles.ADJACENCY) {
				if (te != null) {
					ItemStack is = ChromaItems.ADJACENCY.getStackOfMetadata(offset);
					is.stackTagCompound = new NBTTagCompound();
					is.stackTagCompound.setInteger("tier", (int)((System.currentTimeMillis()/500)%TileEntityAdjacencyUpgrade.MAX_TIER));
					((TileEntityAdjacencyUpgrade)te).setDataFromItemStackTag(is);
				}
			}
			TileEntityRendererDispatcher.instance.renderTileEntityAt(te, a, 0, b, 0);
			GL11.glPopMatrix();
		}
		if (m.hasBlockRender()) {
			double dx = x;
			double dy = y;
			double dz = 0;
			GL11.glPushMatrix();
			GL11.glTranslated(dx, dy, dz);
			GL11.glScaled(sc, -sc, sc);
			GL11.glRotatef(renderq, 1, 0, 0);
			GL11.glRotatef(r, 0, 1, 0);
			if (m == ChromaTiles.CRYSTAL) {
				GL11.glTranslated(-0.5, -0.33, -0.5);
				CrystalRenderer.renderAllArmsInInventory = true;
			}
			ReikaTextureHelper.bindTerrainTexture();
			rb.renderBlockAsItem(m.getBlock(), m.getBlockMetadata(), 1);
			CrystalRenderer.renderAllArmsInInventory = false;
			GL11.glPopMatrix();
		}
		runningRender = false;
		GL11.glPopMatrix();

		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glTranslated(0, 0, -32);
	}

	protected static enum Pages {
		MAIN,
		NOTES,
		ENERGY,
		AOE;
	}

}
