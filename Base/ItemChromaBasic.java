/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Auxiliary.Interfaces.TieredItem;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Interfaces.IndexedItemSprites;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class ItemChromaBasic extends Item implements IndexedItemSprites {

	protected Random par5Random = new Random();

	private int index;

	public ItemChromaBasic(int tex) {
		super();
		maxStackSize = 64;
		this.setIndex(tex);
		this.setCreativeTab(this.isAvailableInCreativeMode() ? this.getCreativePage() : null);
	}

	private boolean isAvailableInCreativeMode() {
		if (ChromatiCraft.instance.isLocked())
			return false;
		return true;
	}

	public ItemChromaBasic(int tex, int max) {
		super();
		maxStackSize = max;
		if (max == 1);
		hasSubtypes = true;
		if (this.isAvailableInCreativeMode())
			this.setCreativeTab(ChromatiCraft.tabChromaItems);
		else
			this.setCreativeTab(null);
		this.setIndex(tex);
	}

	protected CreativeTabs getCreativePage() {
		return ChromatiCraft.tabChromaItems;
	}

	public int getItemSpriteIndex(ItemStack item) {
		return index;
	}

	public void setIndex(int a) {
		index = a;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final void registerIcons(IIconRegister ico) {}

	@Override
	public void onCreated(ItemStack is, World world, EntityPlayer ep) {
		this.checkAchievements(ep, is);
	}

	private void checkAchievements(EntityPlayer player, ItemStack item) {

	}

	public Class getTextureReferenceClass() {
		return ChromatiCraft.class;
	}

	@Override
	public String getTexture(ItemStack is) {
		int i = this.getItemSpriteIndex(is)/256;
		return i > 0 ? "/Reika/ChromatiCraft/Textures/Items/items"+i+".png" : "/Reika/ChromatiCraft/Textures/Items/items.png";
	}

	@Override
	public final String getItemStackDisplayName(ItemStack is) {
		ChromaItems ir = ChromaItems.getEntry(is);
		String name = ir.hasMultiValuedName() ? ir.getMultiValuedName(is.getItemDamage()) : ir.getBasicName();
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			name = this.obfuscate(name, is);
		}
		return name;
	}

	@SideOnly(Side.CLIENT)
	private String obfuscate(String name, ItemStack is) {
		if (this instanceof TieredItem) {
			name = this.obfuscateIf(name, is);
		}
		else if (this instanceof ItemCrystalBasic) {
			CrystalElement e = CrystalElement.elements[is.getItemDamage()%16];
			if (!ProgressionManager.instance.hasPlayerDiscoveredColor(Minecraft.getMinecraft().thePlayer, e)) {
				name = EnumChatFormatting.OBFUSCATED.toString()+name+EnumChatFormatting.RESET.toString();
			}
		}
		return name;
	}

	@SideOnly(Side.CLIENT)
	private String obfuscateIf(String name, ItemStack is) {
		TieredItem it = (TieredItem)this;
		if (it.isTiered(is)) {
			ProgressStage p = it.getDiscoveryTier(is);
			if (!ProgressionManager.instance.isPlayerAtStage(Minecraft.getMinecraft().thePlayer, p)) {
				name = EnumChatFormatting.OBFUSCATED.toString()+name;
			}
		}
		return name;
	}

}
