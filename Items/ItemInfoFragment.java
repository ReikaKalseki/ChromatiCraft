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

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
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
import Reika.ChromatiCraft.Auxiliary.Render.ChromaFontRenderer;
import Reika.ChromatiCraft.Base.ItemChromaBasic;
import Reika.ChromatiCraft.Magic.Progression.ChromaResearchManager;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.DragonAPI.Interfaces.Item.SpriteRenderCallback;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;

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
		if (!ChromaResearchManager.instance.getNextResearchesFor(ep).isEmpty())
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

	private static void setResearch(ItemStack is, ChromaResearch r) {
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
	public boolean onRender(RenderItem ri, ItemStack is, ItemRenderType type) {
		if (type == ItemRenderType.INVENTORY) {
			ChromaResearch r = this.getResearch(is);
			if (r != null) {
				boolean key = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
				double s = key ? 0.063 : 0.0315;
				GL11.glScaled(s, -s, s);
				int dx = key ? 0 : 16;
				r.drawTabIcon(ri, dx, -16);//ReikaGuiAPI.instance.drawItemStack(ri, out, dx, -16);
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
