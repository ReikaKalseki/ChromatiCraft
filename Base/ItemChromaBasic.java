/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base;

import java.util.Collection;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Auxiliary.Interfaces.ResearchDependentName;
import Reika.ChromatiCraft.Auxiliary.Interfaces.TieredItem;
import Reika.ChromatiCraft.Auxiliary.Render.ChromaFontRenderer;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Registry.ChromaResearchManager;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Interfaces.Item.MultisheetItem;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class ItemChromaBasic extends Item implements MultisheetItem {

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

	public final Class getTextureReferenceClass() {
		return ChromatiCraft.class;
	}

	@Override
	public String getTexture(ItemStack is) {
		return "/Reika/ChromatiCraft/Textures/Items/items_resource.png";
	}

	public final String getSpritesheet(ItemStack is) {
		return this.getTexture(is);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public FontRenderer getFontRenderer(ItemStack is) {
		return null;
	}

	@Override
	public final String getItemStackDisplayName(ItemStack is) {
		ChromaItems ir = ChromaItems.getEntry(is);
		String name = ir.hasMultiValuedName() ? ir.getMultiValuedName(is.getItemDamage()) : ir.getBasicName();
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			//name = ModList.NEI.isLoaded() && DragonAPICore.hasGameLoaded() ? ObfuscatedNameHandler.registerName(name, is) : name;
			if (this.obfuscate(is)) {
				//name = EnumChatFormatting.OBFUSCATED.toString()+name;
				name = this.getObfuscatedName(name);
			}
		}
		return name;
	}

	@SideOnly(Side.CLIENT)
	private String getObfuscatedName(String name) {
		return ChromaFontRenderer.FontType.OBFUSCATED.id+name;
	}

	@SideOnly(Side.CLIENT)
	private boolean obfuscate(ItemStack is) {
		if (!DragonAPICore.hasGameLoaded())
			return false;
		if (this instanceof TieredItem) {
			if (this.obfuscateIf(is))
				return true;
		}
		else if (this instanceof ResearchDependentName) {
			Collection<ChromaResearch> rs = ((ResearchDependentName)this).getRequiredResearch(is);
			if (rs != null) {
				for (ChromaResearch r : rs) {
					if (!ChromaResearchManager.instance.playerHasFragment(Minecraft.getMinecraft().thePlayer, r)) {
						return true;
					}
				}
			}
		}
		if (this instanceof ItemCrystalBasic) {
			CrystalElement e = CrystalElement.elements[is.getItemDamage()%16];
			if (!ProgressionManager.instance.hasPlayerDiscoveredColor(Minecraft.getMinecraft().thePlayer, e)) {
				return true;
			}
		}
		return false;
	}

	@SideOnly(Side.CLIENT)
	private boolean obfuscateIf(ItemStack is) {
		TieredItem it = (TieredItem)this;
		if (it.isTiered(is)) {
			ProgressStage p = it.getDiscoveryTier(is);
			if (!p.isPlayerAtStage(Minecraft.getMinecraft().thePlayer)) {
				return true;
			}
		}
		return false;
	}

}
