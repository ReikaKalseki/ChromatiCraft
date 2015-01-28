/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items.ItemBlock;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;

import org.lwjgl.input.Keyboard;

import Reika.ChromatiCraft.Auxiliary.ChromaFontRenderer;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager;
import Reika.ChromatiCraft.Base.CrystalBlock;
import Reika.ChromatiCraft.Magic.CrystalPotionController;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.ReikaPotionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlockCrystal extends ItemBlock {

	public ItemBlockCrystal(Block b) {
		super(b);
		hasSubtypes = true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		for (int i = 0; i < CrystalElement.elements.length; i++) {
			ItemStack item = new ItemStack(par1, 1, i);
			par3List.add(item);
		}
	}

	@Override
	public int getMetadata(int meta) {
		return meta;
	}

	@Override
	public IIcon getIconFromDamage(int dmg) {
		return field_150939_a.getIcon(0, dmg);
	}

	@Override
	public String getItemStackDisplayName(ItemStack is) {
		return ChromaBlocks.getEntryByID(field_150939_a).getMultiValuedName(is.getItemDamage());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public FontRenderer getFontRenderer(ItemStack is) {
		CrystalElement color = CrystalElement.elements[is.getItemDamage()];
		EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
		return ProgressionManager.instance.hasPlayerDiscoveredColor(ep, color) ? null : ChromaFontRenderer.FontType.OBFUSCATED.renderer;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean par4) {
		CrystalElement color = CrystalElement.elements[is.getItemDamage()];
		if (ProgressionManager.instance.hasPlayerDiscoveredColor(ep, color)) {
			CrystalBlock block = (CrystalBlock)Block.getBlockFromItem(is.getItem());
			PotionEffect eff = CrystalPotionController.getEffectFromColor(color, 200, 0);
			PotionEffect neff = CrystalPotionController.getNetherEffectFromColor(color, 200, 0);
			boolean negative = eff != null ? ReikaPotionHelper.isBadEffect(Potion.potionTypes[eff.getPotionID()]) : false;
			boolean nnegative = neff != null ? ReikaPotionHelper.isBadEffect(Potion.potionTypes[neff.getPotionID()]) : false;
			if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
				if (block.shouldGiveEffects(color)) {
					li.add("Effects: "+CrystalPotionController.getEffectName(color));
					if (negative)
						li.add("(Mobs only)");
					String sg = nnegative || neff == null ? "(Players only)" : "(Mobs only)";
					li.add("Nether Effects: "+CrystalPotionController.getNetherEffectName(color));
					li.add(sg);
					if (CrystalPotionController.getEffectFromColor(color, 200, 0) != null) {
						li.add("");
						li.add("Effect Range: "+block.getRange());
						li.add("Effect Level: "+(block.getPotionLevel(color)+1));

					}
				}
			}
			else if (field_150939_a != ChromaBlocks.LAMP.getBlockInstance()) {
				StringBuilder sb = new StringBuilder();
				sb.append("Hold ");
				sb.append(EnumChatFormatting.GREEN.toString());
				sb.append("Shift");
				sb.append(EnumChatFormatting.GRAY.toString());
				sb.append(" for effect data");
				li.add(sb.toString());
			}
		}
		else {
			li.add(EnumChatFormatting.OBFUSCATED.toString()+"------------------");
		}
	}

}
