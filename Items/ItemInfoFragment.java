/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items;

import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaFX;
import Reika.ChromatiCraft.Auxiliary.Render.ChromaFontRenderer;
import Reika.ChromatiCraft.Base.ItemChromaBasic;
import Reika.ChromatiCraft.Magic.Progression.ChromaResearchManager;
import Reika.ChromatiCraft.Magic.Progression.FragmentCategorizationSystem;
import Reika.ChromatiCraft.Magic.Progression.FragmentCategorizationSystem.FragmentCategorization;
import Reika.ChromatiCraft.Magic.Progression.FragmentCategorizationSystem.FragmentCategory;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.DragonAPI.Instantiable.Event.Client.RenderItemInSlotEvent;
import Reika.DragonAPI.Interfaces.Item.SpriteRenderCallback;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Rendering.ReikaRenderHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemInfoFragment extends ItemChromaBasic implements SpriteRenderCallback {

	public ItemInfoFragment(int tex) {
		super(tex);
		this.setMaxStackSize(1);
		this.setCreativeTab(ChromatiCraft.tabChromaFragments);
	}

	@Override
	public void onUpdate(ItemStack is, World world, Entity e, int slot, boolean held) {
		if (e instanceof EntityPlayer && !world.isRemote) {
			EntityPlayer ep = (EntityPlayer)e;
			int n = 8;
			if (world.getTotalWorldTime()%n == slot%n) {
				ChromaResearch r = getResearch(is);
				if (r == null) {
					//this.programShardAndGiveData(is, ep);
				}
				else {
					if (r.canPlayerProgressTo(ep) && ChromaResearchManager.instance.getNextResearchesFor(ep).contains(r))
						ChromaResearchManager.instance.givePlayerFragment(ep, r, true);
				}
			}
		}
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer ep) {
		if (isBlank(is) && !ChromaResearchManager.instance.getNextResearchesFor(ep).isEmpty())
			ep.openGui(ChromatiCraft.instance, ChromaGuis.FRAGSELECT.ordinal(), world, 0, 0, 0);
		return is;
	}

	@Override
	public final void getSubItems(Item i, CreativeTabs tab, List li)
	{
		li.add(ChromaItems.FRAGMENT.getStackOf());
		for (ChromaResearch r : ChromaResearch.getAllObtainableFragments()) {
			li.add(this.getItem(r));
		}
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean vb) {
		if (this.isBlank(is)) {
			li.add(EnumChatFormatting.ITALIC.toString()+"Blank");
		}
		else {
			ChromaResearch r = this.getResearch(is);
			if (r == null) {
				li.add("[ERROR: NO RESEARCH FOR NAME '"+is.stackTagCompound.getString("page")+"'");
				return;
			}
			EnumChatFormatting format = null;
			boolean read = r.playerCanRead(ep);
			boolean has = ChromaResearchManager.instance.playerHasFragment(ep, r);
			if (has)
				format = EnumChatFormatting.GREEN;
			else if (read)
				format = EnumChatFormatting.LIGHT_PURPLE;
			else
				format = EnumChatFormatting.RESET;
			String pre = ""+format.toString();
			String title = r.getParent().getTitle()+": "+r.getTitle();
			String s = read || has ? pre+" "+title : pre+" "+ChromaFontRenderer.FontType.OBFUSCATED.id+title;
			li.add(s);
			if (r == ChromaResearch.FRAGMENT) {
				li.add(" Shift-Right-Click with");
				li.add(" the lexicon to add");
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack is, int pass) {
		if (is.getItemDamage() == 0)
			return false;
		ChromaResearch r = this.getResearch(is);
		return ChromaResearchManager.instance.canPlayerStepTo(Minecraft.getMinecraft().thePlayer, r);
	}

	public static boolean isBlank(ItemStack is) {
		return is.stackTagCompound == null;
	}

	public static ChromaResearch getResearch(ItemStack is) {
		if (is.stackTagCompound == null || !is.stackTagCompound.hasKey("page")) {
			is.stackTagCompound = null;
			return null;
		}
		return ChromaResearch.getByName(is.stackTagCompound.getString("page"));
	}

	public static ItemStack getItem(ChromaResearch r) {
		ItemStack is = ChromaItems.FRAGMENT.getStackOf();
		setResearch(is, r);
		return is;
	}

	public static void setResearch(ItemStack is, ChromaResearch r) {
		if (ChromaItems.FRAGMENT.matchWith(is)) {
			is.stackTagCompound = new NBTTagCompound();
			is.stackTagCompound.setString("page", r.name());
		}
	}

	public static void programShardAndGiveData(ItemStack is, EntityPlayer ep) {
		ChromaResearch r = ChromaResearchManager.instance.getRandomNextResearchFor(ep);
		if (r != null && !ep.worldObj.isRemote) {
			setResearch(is, r);
			ChromaResearchManager.instance.givePlayerFragment(ep, getResearch(is), true);
			ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.GIVERESEARCH.ordinal(), (EntityPlayerMP)ep, r.ordinal());
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean onRender(RenderItem ri, ItemStack is, ItemRenderType type) {
		if (type == ItemRenderType.INVENTORY) {
			ChromaResearch r = this.getResearch(is);
			if (r != null) {
				Tessellator v5 = Tessellator.instance;
				FragmentCategorization frag = FragmentCategorizationSystem.instance.getCategories(r);
				if (frag != null && !GuiScreen.isCtrlKeyDown() && RenderItemInSlotEvent.isRenderingStackHovered(is)) {
					int i = frag.set().size();
					GL11.glPushMatrix();
					GL11.glScaled(0.0625, -0.0625, 1);
					GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
					GL11.glDisable(GL11.GL_CULL_FACE);
					GL11.glDisable(GL11.GL_DEPTH_TEST);
					GL11.glDisable(GL11.GL_LIGHTING);
					GL11.glEnable(GL11.GL_BLEND);
					BlendMode.DEFAULT.apply();
					GL11.glColor4f(1, 1, 1, 1);
					double z = 0;
					int s = 8;
					int w = s*i;
					int h = s;
					int x = -w;
					int y = -h*3;
					//if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
					//	w = 16;
					//	x2 -= 8;
					//}
					int r2 = 1;
					GL11.glDisable(GL11.GL_TEXTURE_2D);
					v5.startDrawingQuads();
					v5.setColorRGBA(127, 0, 255, 255);
					v5.addVertex(x-r2, y-r2, z);
					v5.addVertex(x+w+r2, y-r2, z);
					v5.addVertex(x+w+r2, y+h+r2, z);
					v5.addVertex(x-r2, y+h+r2, z);
					v5.draw();
					v5.startDrawingQuads();
					v5.setColorRGBA(0, 0, 0, 255);
					v5.addVertex(x, y, z);
					v5.addVertex(x+w, y, z);
					v5.addVertex(x+w, y+h, z);
					v5.addVertex(x, y+h, z);
					v5.draw();
					GL11.glEnable(GL11.GL_TEXTURE_2D);
					int in = 0;
					ReikaTextureHelper.bindFinalTexture(ChromatiCraft.class, "Textures/fragmentcategories.png");
					int[] arr = ChromaFX.getChromaColorTiles();
					for (FragmentCategory e : frag.set()) {
						int ex = x+in*s;
						float u = (e.ordinal()%8)/8F;
						float v = (e.ordinal()/8)/8F;
						in++;/*
					if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
						GL11.glPushMatrix();
						double sc = 0.5;
						GL11.glScaled(sc, sc, sc);
						String s = Integer.toString(tag.getValue(e), 10).toUpperCase();//String.format("%d", tag.getValue(e));
						int color = e.getColor() | 0xff000000;
						FontRenderer f = Minecraft.getMinecraft().fontRenderer;
						ReikaGuiAPI.instance.drawCenteredStringNoShadow(f, s, (int)((x+w-0)/sc), (int)((y+w-6)/sc), color);
						GL11.glTranslated(1, 0, 0);
						ReikaGuiAPI.instance.drawCenteredStringNoShadow(f, s, (int)((x+w-0)/sc), (int)((y+w-6)/sc), color);
						GL11.glPopMatrix();
					}
					else {*/
						v5.startDrawingQuads();
						int idx = (int)(((System.identityHashCode(e)*11+ReikaRenderHelper.getRenderFrame()/6)%arr.length)+arr.length)%arr.length;
						v5.setColorOpaque_I(ReikaColorAPI.mixColors(arr[idx], 0xffffff, 0.6F));
						v5.addVertexWithUV(ex, y, z, u, v);
						v5.addVertexWithUV(ex+s, y, z, u+0.125, v);
						v5.addVertexWithUV(ex+s, y+s, z, u+0.125, v+0.125);
						v5.addVertexWithUV(ex, y+s, z, u, v+0.125);
						v5.draw();
						//}
					}
					GL11.glPopAttrib();
					GL11.glPopMatrix();
				}
				boolean key = GuiScreen.isShiftKeyDown();
				double s = key ? 0.063 : 0.0315;
				GL11.glScaled(s, -s, s);
				int dx = key ? 0 : 16;
				//GL11.glDepthMask(false);
				/*
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				v5.startDrawingQuads();
				v5.setColorRGBA(127, 192, 255, 255);
				v5.addVertex(16, 36, 0);
				v5.addVertex(32, 36, 0);
				v5.addVertex(32, 20, 0);
				v5.addVertex(16, 20, 0);
				v5.draw();
				v5.startDrawingQuads();
				v5.setColorRGBA(0, 0, 0, 255);
				v5.addVertex(17, 35, 0);
				v5.addVertex(31, 35, 0);
				v5.addVertex(31, 21, 0);
				v5.addVertex(17, 21, 0);
				v5.draw();
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				 */
				r.drawTabIcon(ri, dx, -16);//ReikaGuiAPI.instance.drawItemStack(ri, out, dx, -16);
				//GL11.glDepthMask(true);
				return key && r != ChromaResearch.FRAGMENT;
			}
		}
		return false;
	}

	@Override
	public boolean doPreGLTransforms(ItemStack is, ItemRenderType type) {
		return true;
	}

	@Override
	public String getTexture(ItemStack is) {
		return "/Reika/ChromatiCraft/Textures/Items/items_tool.png";
	}

}
