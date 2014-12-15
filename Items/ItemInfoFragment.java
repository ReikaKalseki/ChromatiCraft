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
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ItemChromaMulti;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Registry.ChromaResearchManager;
import Reika.DragonAPI.Interfaces.SpriteRenderCallback;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemInfoFragment extends ItemChromaMulti implements SpriteRenderCallback {

	public ItemInfoFragment(int tex) {
		super(tex);
		this.setMaxStackSize(1);
		this.setCreativeTab(ChromatiCraft.tabChromaFragments);
	}

	@Override
	public void onUpdate(ItemStack is, World world, Entity ep, int slot, boolean held) {
		if (is.getItemDamage() == 0 && ep instanceof EntityPlayer) {
			this.programShardAndGiveData(is, (EntityPlayer)ep);
		}
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean vb) {
		int dmg = is.getItemDamage();
		if (dmg == 0) {
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
	protected boolean incrementTextureIndexWithMeta() {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack is, int pass) {
		if (is.getItemDamage() == 0)
			return false;
		ChromaResearch r = this.getResearch(is);
		return ChromaResearchManager.instance.canPlayerStepTo(Minecraft.getMinecraft().thePlayer, r);
	}

	public static ChromaResearch getResearch(ItemStack is) {
		if (is.stackTagCompound == null || !is.stackTagCompound.hasKey("page")) {
			is.setItemDamage(0);
			return null;
		}
		return ChromaResearch.valueOf(is.stackTagCompound.getString("page"));
		//return ChromaResearch.researchList[is.getItemDamage()];
	}

	public static ItemStack getItem(ChromaResearch r) {
		ItemStack is = ChromaItems.FRAGMENT.getStackOf();
		setResearch(is, r);
		return is;
		//return ChromaItems.FRAGMENT.getStackOfMetadata(r.ordinal());
	}

	private static void setResearch(ItemStack is, ChromaResearch r) {
		if (ChromaItems.FRAGMENT.matchWith(is)) {
			is.stackTagCompound = new NBTTagCompound();
			is.stackTagCompound.setString("page", r.name());
			//is.setItemDamage(r.ordinal());
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
		return false;
	}

}
