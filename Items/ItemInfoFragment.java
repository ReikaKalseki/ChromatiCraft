/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ItemChromaBasic;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Registry.ChromaResearchManager;
import Reika.DragonAPI.Interfaces.SpriteRenderCallback;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemInfoFragment extends ItemChromaBasic implements SpriteRenderCallback {

	public ItemInfoFragment(int tex) {
		super(tex);
		this.setMaxStackSize(1);
		this.setCreativeTab(ChromatiCraft.tabChromaFragments);
	}

	@Override
	public void onUpdate(ItemStack is, World world, Entity ep, int slot, boolean held) {
		if (this.isBlank(is) && ep instanceof EntityPlayer) {
			this.programShardAndGiveData(is, (EntityPlayer)ep);
		}
	}

	@Override
	public final void getSubItems(Item i, CreativeTabs tab, List li)
	{
		li.add(ChromaItems.FRAGMENT.getStackOf());
		for (ChromaResearch r : ChromaResearch.getAllNonParents()) {
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
			String s = read || has ? pre+" "+title : pre+" "+EnumChatFormatting.OBFUSCATED.toString()+title;
			li.add(s);
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
		//return is.getItemDamage() == 0;
	}

	public static ChromaResearch getResearch(ItemStack is) {
		if (is.stackTagCompound == null || !is.stackTagCompound.hasKey("page")) {
			is.stackTagCompound = null;
			return null;
		}
		return ChromaResearch.getByName(is.stackTagCompound.getString("page"));
		//return ChromaResearch.getResearch(is.getItemDamage());
	}

	public static ItemStack getItem(ChromaResearch r) {
		ItemStack is = ChromaItems.FRAGMENT.getStackOf();
		setResearch(is, r);
		return is;
		//return ChromaItems.FRAGMENT.getStackOfMetadata(r.index());
	}

	private static void setResearch(ItemStack is, ChromaResearch r) {
		if (ChromaItems.FRAGMENT.matchWith(is)) {
			is.stackTagCompound = new NBTTagCompound();
			is.stackTagCompound.setString("page", r.name());
			//is.setItemDamage(r.index());
		}
	}

	public static void programShardAndGiveData(ItemStack is, EntityPlayer ep) {
		ChromaResearch r = ChromaResearchManager.instance.getRandomNextResearchFor(ep);
		if (r != null) {
			setResearch(is, r);
			ChromaResearchManager.instance.givePlayerFragment(ep, getResearch(is));
		}
	}

	@Override
	public boolean onRender(RenderItem ri, ItemStack is, ItemRenderType type) {
		if (type == ItemRenderType.INVENTORY) {
			ChromaResearch r = this.getResearch(is);
			if (r != null) {
				ItemStack out = r.getTabIcon();
				if (out != null) {
					boolean key = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
					double s = key ? 0.063 : 0.0315;
					GL11.glScaled(s, -s, s);
					int dx = key ? 0 : 16;
					ReikaGuiAPI.instance.drawItemStack(ri, out, dx, -16);
					return key && out.getItem() != this;
				}
			}
		}
		return false;
	}

	@Override
	public boolean doPreGLTransforms(ItemStack is, ItemRenderType type) {
		return true;
	}

}
